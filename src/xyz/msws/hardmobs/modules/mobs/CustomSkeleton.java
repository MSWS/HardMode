package xyz.msws.hardmobs.modules.mobs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
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

import xyz.msws.hardmobs.MPlugin;
import xyz.msws.hardmobs.utils.MSG;

public class CustomSkeleton extends BehaviorListener {

	private Map<Skeleton, BukkitTask> runs = new HashMap<Skeleton, BukkitTask>();

	public CustomSkeleton(MPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getEntityType() != getType())
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
				if (runs.containsKey(skeleton))
					return;
			}

		if (event.getTarget() == null)
			return;

		MSG.announce("runnable started (targetting " + event.getTarget() + ")");
		runs.put(skeleton, newShooter(skeleton, event.getTarget()).runTaskTimer((Plugin) plugin, 10, 40));
	}

	public BukkitRunnable newShooter(Skeleton skeleton, LivingEntity target) {
		return new BukkitRunnable() {

			@Override
			public void run() {
				if (skeleton == null || !skeleton.isValid() || skeleton.getTarget() == null
						|| !skeleton.getTarget().isValid() || skeleton.getTarget() != target) {
					this.cancel();
					runs.remove(skeleton);
					MSG.announce("runnable cancelled (skeleton: " + skeleton + " starget: " + skeleton.getTarget()
							+ " target: " + target + ")");
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
		if (event.getEntityType() != getType())
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.52);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntityType() != getType())
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		if (ThreadLocalRandom.current().nextDouble() < .6)
			return;

		long respawnTime = 3000;

		long spawnTime = System.currentTimeMillis() + respawnTime;

		LivingEntity target = skeleton.getTarget();
		Location deathLoc = skeleton.getLocation();

		new BukkitRunnable() {
			private long lastPlay = System.currentTimeMillis();

			@Override
			public void run() {
				if (System.currentTimeMillis() >= spawnTime) {
					Skeleton newSkele = (Skeleton) skeleton.getWorld().spawnEntity(deathLoc, getType());
					skeleton.getWorld().playSound(deathLoc, Sound.ENTITY_BLAZE_AMBIENT, 2, .25f);
					if (target != null && target.isValid())
						newSkele.setTarget(target);

					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - lastPlay > 200) {
					lastPlay = System.currentTimeMillis();
					skeleton.getWorld().playSound(deathLoc, Sound.ENTITY_SKELETON_AMBIENT, 2, .1f);
				}

				double radius = .8;

				for (double i = 0; i < Math.PI * 2; i += .5) {
					Location loc = deathLoc.clone().add(Math.sin(i) * radius, 2, Math.cos(i) * radius);
					skeleton.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, -.075, 0);
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

	@Override
	public EntityType getType() {
		return EntityType.SKELETON;
	}

}
