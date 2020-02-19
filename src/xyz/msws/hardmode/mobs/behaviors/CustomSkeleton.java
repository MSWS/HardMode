package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMobs;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.MSG;

public class CustomSkeleton extends BehaviorListener {

	private Map<Skeleton, BukkitTask> tasks = new HashMap<Skeleton, BukkitTask>();

	public CustomSkeleton(HardMobs plugin) {
		super(plugin);

		selector = new MobSelector() {

			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.SKELETON);
			}
		};
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();

		if (skeleton.getTarget() instanceof Player) {
			GameMode gm = ((Player) skeleton.getTarget()).getGameMode();
			if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR)
				return;
		}

		if (event.getTarget() == null && skeleton.getTarget() != null)
			if (skeleton.getTarget().isValid()) {
				event.setTarget(skeleton.getTarget());
				if (tasks.containsKey(skeleton))
					return;
			}

		if (event.getTarget() == null)
			return;

		MSG.announce("Runnable started (targetting " + event.getTarget() + ").");
		tasks.put(skeleton, shoot(skeleton, event.getTarget()).runTaskTimer((Plugin) plugin, 10, 40));
	}

	public BukkitRunnable shoot(Skeleton skeleton, LivingEntity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (skeleton == null || !skeleton.isValid() || skeleton.getTarget() == null
						|| !skeleton.getTarget().isValid() || skeleton.getTarget() != target) {
					this.cancel();
					tasks.remove(skeleton);
					if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
						MSG.announce("Runnable cancelled (skeleton: " + skeleton + " starget: " + skeleton.getTarget()
								+ " target: " + target + ").");
					return;
				}
				Vector aim = target.getLocation().toVector().subtract(skeleton.getLocation().toVector());
				aim.normalize().multiply(3);
				skeleton.launchProjectile(Arrow.class, aim);
				skeleton.getWorld().playSound(skeleton.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);

				Location current = skeleton.getEyeLocation();
				aim = target.getEyeLocation().toVector().subtract(current.toVector()).normalize();
				int particles = 0;
				double lastDist = Double.MAX_VALUE, currentDist = 0;

				// If current dist > lastDist then we have gone PAST the target
				while (currentDist < lastDist && particles < 200) {
					current.getWorld().spawnParticle(Particle.FLAME, current, 0);
					if (currentDist != 0)
						lastDist = currentDist;
					current.add(aim.clone().normalize().multiply(1.0 / 2.0));
					currentDist = current.distanceSquared(skeleton.getTarget().getLocation());
					particles++;
				}
			}
		};
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (selector.matches(event.getEntity()))
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.52);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (selector.matches(event.getEntity()))
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		if (ThreadLocalRandom.current().nextDouble() < .6)
			return;

		respawn(skeleton.getLocation(), skeleton.getTarget(), 3000);
	}

	public void respawn(Location location, LivingEntity target, long time) {
		new BukkitRunnable() {
			long spawnTime = System.currentTimeMillis() + time;
			private long lastPlay = System.currentTimeMillis();

			@Override
			public void run() {
				if (System.currentTimeMillis() >= spawnTime) {
					Skeleton newSkele = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
					location.getWorld().playSound(location, Sound.ENTITY_BLAZE_AMBIENT, 2, .25f);
					if (target != null && target.isValid())
						newSkele.setTarget(target);

					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - lastPlay > 200) {
					lastPlay = System.currentTimeMillis();
					location.getWorld().playSound(location, Sound.ENTITY_SKELETON_AMBIENT, 2, .1f);
				}

				double radius = .8;

				for (double i = 0; i < Math.PI * 2; i += .5) {
					Location loc = location.clone().add(Math.sin(i) * radius, 2, Math.cos(i) * radius);
					location.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, -.075, 0);
				}
			}
		}.runTaskTimer((Plugin) plugin, 1, 1);
	}

	@Override
	public void disable() {
		EntityTargetLivingEntityEvent.getHandlerList().unregister(this);
		EntitySpawnEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);
	}
}
