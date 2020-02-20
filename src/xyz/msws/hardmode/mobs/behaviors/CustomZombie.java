package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.inventory.CItem;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.Utils;

public class CustomZombie extends BehaviorListener {
	private final Map<PotionEffect, Double> chances;
	private final Map<ItemStack, Double> equipment;

	public CustomZombie(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {

			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.ZOMBIE || ent.getType() == EntityType.ZOMBIE_VILLAGER
						|| ent.getType() == EntityType.DROWNED);
			}
		};

		chances = new HashMap<PotionEffect, Double>();
		equipment = new HashMap<ItemStack, Double>();

		chances.put(new PotionEffect(PotionEffectType.WEAKNESS, 30 * 20, 0), 1.0 / 52);
		chances.put(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 1), 1.0 / 28);
		chances.put(new PotionEffect(PotionEffectType.POISON, 15 * 20, 0), 1.0 / 47);
		chances.put(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 1), 1.0 / 32);
		chances.put(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 0), 1.0 / 25);

		equipment.put(new CItem(Material.DIAMOND_SWORD).build(), 1.0 / 100);
		equipment.put(new CItem(Material.WOODEN_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build(), 1.0 / 42);
		equipment.put(new CItem(Material.WOODEN_AXE).build(), 1.0 / 55);
		equipment.put(new CItem(Material.FISHING_ROD).enchantment(Enchantment.VANISHING_CURSE, 1).build(), 1.0 / 50);
		equipment.put(new CItem(Material.GOLD_NUGGET).build(), 1.0 / 35);
		equipment.put(new CItem(Material.STONE).build(), 1.0 / 46);
		equipment.put(new CItem(Material.IRON_SWORD).build(), 1.0 / 75);
		equipment.put(new CItem(Material.STONE_SWORD).enchantment(Enchantment.DURABILITY, 2).build(), 1.0 / 52);
		equipment.put(new CItem(Material.GOLDEN_SHOVEL).enchantment(Enchantment.DIG_SPEED, 1).build(), 1.0 / 72);
		equipment.put(new CItem(Material.STICK).build(), 1.0 / 36);
		equipment.put(new CItem(Material.REDSTONE).build(), 1.0 / 82);
		equipment.put(new CItem(Material.DIAMOND_AXE).enchantment(Enchantment.DAMAGE_UNDEAD, 4).build(), 1.0 / 85);
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

		if (zombie.getType() == EntityType.DROWNED)
			for (int y = spawnLocation.getBlockY(); y >= 0 && spawnLocation.getBlock().isLiquid(); y--) {
				spawnLocation.setY(y);
			}

		Zombie newZombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, zombie.getType());

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
		LivingEntity entity = (LivingEntity) event.getEntity();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		double reduction = 0;

		if (entity instanceof HumanEntity) {
			HumanEntity player = (HumanEntity) entity;
			for (ItemStack armor : player.getInventory().getArmorContents()) {
				if (armor == null)
					continue;
				if (armor.getType().toString().contains("LEATHER_"))
					reduction += Utils.partsInArmor(armor.getType());
			}
		}

		reduction /= 25.0;

		if (entity instanceof Player)
			if (((Player) entity).isBlocking())
				reduction += .4;

		boolean infected = false;

		for (Entry<PotionEffect, Double> entry : chances.entrySet()) {
			double min = entry.getValue();
			min *= 1 - reduction;
			if (random.nextDouble() > min)
				continue;
			entity.addPotionEffect(entry.getKey());
			infected = true;
		}
		if (infected)
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
		respawn(zombie.getLocation(), zombie.getTarget(), 2000);
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Zombie zombie = (Zombie) event.getEntity();

		if (zombie.getEquipment().getItemInMainHand() != null
				&& zombie.getEquipment().getItemInMainHand().getType() != Material.AIR)
			return;

		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < random.nextInt(5); i++) {
			for (Entry<ItemStack, Double> entry : equipment.entrySet()) {
				if (random.nextDouble() > entry.getValue())
					continue;
				zombie.getEquipment().setItemInMainHand(entry.getKey());
				return;
			}
		}
	}

	public void respawn(Location location, LivingEntity target, long time) {
		new BukkitRunnable() {
			long spawnTime = System.currentTimeMillis() + time;
			private long lastPlay = System.currentTimeMillis();

			@Override
			public void run() {
				if (System.currentTimeMillis() >= spawnTime) {
					Zombie newZombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
					newZombie.setMetadata("summonedZombie", new FixedMetadataValue(plugin, true));
					location.getWorld().playSound(location, Sound.ENTITY_SKELETON_HURT, 2, .1f);
					if (target != null && target.isValid())
						newZombie.setTarget(target);
					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - lastPlay > 800) {
					lastPlay = System.currentTimeMillis();
					location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_HURT, 2, .1f);
				}

				double radius = .8 + Math.sin(spawnTime - System.currentTimeMillis()) * 1.2;

				for (double i = 0; i < Math.PI * 2; i += .5) {
					Location loc = location.clone().add(Math.sin(i) * radius, 0, Math.cos(i) * radius);
					location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 0, 0, .075, 0);
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
