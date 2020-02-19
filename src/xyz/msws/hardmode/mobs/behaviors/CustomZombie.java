package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.hardmode.HardMobs;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.Utils;

public class CustomZombie extends BehaviorListener {
	private final Map<PotionEffect, Double> chances;

	public CustomZombie(HardMobs plugin) {
		super(plugin);

		selector = new MobSelector() {

			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.ZOMBIE || ent.getType() == EntityType.ZOMBIE_VILLAGER
						|| ent.getType() == EntityType.DROWNED);
			}
		};

		chances = new HashMap<PotionEffect, Double>();

		chances.put(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0), .15);
		chances.put(new PotionEffect(PotionEffectType.SLOW, 10, 1), .2);
		chances.put(new PotionEffect(PotionEffectType.POISON, 15, 0), .1);
		chances.put(new PotionEffect(PotionEffectType.WITHER, 5, 1), .05);
		chances.put(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0), .2);
	}

	@EventHandler
	public void onTargetEntity(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;

		if (event.getTarget() == null)
			return;

		Zombie zombie = (Zombie) event.getEntity();

		if (zombie.hasMetadata("summonedZombie"))
			return;
		if (zombie.hasMetadata("hasSummoned"))
			return;
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (random.nextDouble() > .3)
			return;

		zombie.setMetadata("hasSummoned", new FixedMetadataValue(plugin, true));

		Location spawnLocation = zombie.getLocation().clone();
		spawnLocation.add(random.nextDouble(-5, 5), 0, random.nextDouble(-5, 5));
		spawnLocation.setY(spawnLocation.getWorld().getHighestBlockYAt(spawnLocation) + 1);

		Zombie newZombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);

		newZombie.setMetadata("summonedZombie", new FixedMetadataValue(plugin, true));

		if (random.nextDouble() > .8)
			newZombie.setBaby(true);

		newZombie.setTarget(zombie.getTarget());
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getDamager()))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
//		Zombie zombie = (Zombie) event.getDamager();
		LivingEntity entity = (LivingEntity) event.getEntity();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		double leatherProt = 0;

		if (entity instanceof HumanEntity) {
			HumanEntity player = (HumanEntity) entity;
			for (ItemStack armor : player.getInventory().getArmorContents()) {
				if (armor.getType().toString().contains("LEATHER_")) {
					leatherProt += Utils.partsInArmor(armor.getType());
				}
			}
		}

		leatherProt /= 24.0;

		for (Entry<PotionEffect, Double> entry : chances.entrySet()) {
			double min = entry.getValue();
			min *= 1 - leatherProt;
			if (random.nextDouble() > min)
				continue;
			entity.addPotionEffect(entry.getKey());
		}
		entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 2, .5f);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Zombie zombie = (Zombie) event.getEntity();
		if (zombie.hasMetadata("hasRespawned"))
			return;
		if (ThreadLocalRandom.current().nextDouble() > .2)
			return;
		respawn(zombie.getLocation(), zombie.getTarget(), 8000);
	}

	public void respawn(Location location, LivingEntity target, long time) {
		new BukkitRunnable() {
			long spawnTime = System.currentTimeMillis() + time;
			private long lastPlay = System.currentTimeMillis();

			@Override
			public void run() {
				if (System.currentTimeMillis() >= spawnTime) {
					Zombie newZombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
					newZombie.setMetadata("hasRespawned", new FixedMetadataValue(plugin, true));
					location.getWorld().playSound(location, Sound.ENTITY_SKELETON_HURT, 2, .25f);
					if (target != null && target.isValid())
						newZombie.setTarget(target);

					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - lastPlay > 200) {
					lastPlay = System.currentTimeMillis();
					location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_HURT, 2, .1f);
				}

				double radius = .8 + Math.sin(spawnTime - System.currentTimeMillis()) * 2.0;

				for (double i = 0; i < Math.PI * 2; i += .5) {
					Location loc = location.clone().add(Math.sin(i) * radius, 0, Math.cos(i) * radius);
					location.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 0, 0, .075, 0);
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}

	@Override
	public void disable() {
		EntityTargetLivingEntityEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);

	}
}
