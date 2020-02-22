package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

public class CustomSkeleton extends BehaviorListener {

	private Map<Skeleton, BukkitTask> tasks = new HashMap<Skeleton, BukkitTask>();

	public CustomSkeleton(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.SKELETON || ent.getType() == EntityType.STRAY);
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
			if (skeleton.getTarget().isValid() && skeleton.getLocation().distanceSquared(skeleton.getTarget()
					.getLocation()) <= CE.SKELETON_UNTARGETDISTANCESQUARED.getValue(Number.class).doubleValue()) {
				event.setTarget(skeleton.getTarget());
				if (tasks.containsKey(skeleton))
					return;
			}

		if (event.getTarget() == null)
			return;
		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Skeleton runnable started targetting " + event.getTarget() + ".");
		tasks.put(skeleton,
				shoot(skeleton, event.getTarget()).runTaskTimer((Plugin) plugin,
						CE.SKELETON_FASTARROW_STARTDELAY.getValue(Number.class).intValue(),
						CE.SKELETON_FASTARROW_PERIODDELAY.getValue(Number.class).intValue()));
	}

	public BukkitRunnable shoot(Skeleton skeleton, LivingEntity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (skeleton == null || !skeleton.isValid() || skeleton.getTarget() == null
						|| !skeleton.getTarget().isValid() || skeleton.getTarget() != target) {
					this.cancel();
					tasks.remove(skeleton);
					return;
				}

				plugin.getMobManager().getAttack(AID.FAST_ARROW).attack(skeleton, target);
			}
		};
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.52);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Skeleton skeleton = (Skeleton) event.getEntity();
		if (ThreadLocalRandom.current().nextDouble() > CE.SKELETON_RESPAWN_PROBABILITY.getValue(Number.class)
				.doubleValue())
			return;

		respawn(skeleton.getLocation(), skeleton.getTarget(),
				CE.SKELETON_RESPAWN_TIME.getValue(Number.class).longValue());
	}

	public void respawn(Location location, LivingEntity target, long time) {
		new BukkitRunnable() {
			long spawnTime = System.currentTimeMillis() + time;
			private long lastPlay = System.currentTimeMillis();

			@Override
			public void run() {
				if (System.currentTimeMillis() >= spawnTime) {
					Skeleton newSkele = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
					location.getWorld().playSound(location,
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNED_NAME.getValue(Sound.class),
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNED_VOLUME.getValue(Number.class).floatValue(),
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNED_PITCH.getValue(Number.class).floatValue());
					if (target != null && target.isValid())
						newSkele.setTarget(target);

					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - lastPlay > CE.SKELETON_RESPAWN_SOUNDS_RESPAWNING_RATE
						.getValue(Number.class).longValue()) {
					lastPlay = System.currentTimeMillis();
					location.getWorld().playSound(location,
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNING_NAME.getValue(Sound.class),
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNING_VOLUME.getValue(Number.class).floatValue(),
							CE.SKELETON_RESPAWN_SOUNDS_RESPAWNING_PITCH.getValue(Number.class).floatValue());
				}

				double radius = .8;

				for (double i = 0; i < Math.PI * 2; i += .5) {
					Location loc = location.clone().add(Math.sin(i) * radius, 2, Math.cos(i) * radius);
					location.getWorld().spawnParticle(
							CE.SKELETON_RESPAWN_RESPAWNINGPARTICLES.getValue(Particle.class), loc, 0, 0, -.075, 0);
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
