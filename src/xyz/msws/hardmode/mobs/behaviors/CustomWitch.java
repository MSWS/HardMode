package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

public class CustomWitch extends BehaviorListener {

	private Map<PotionMeta, Double> potions;

	public CustomWitch(HardMode plugin) {
		super(plugin);

		potions = new HashMap<PotionMeta, Double>();

		selector = new MobSelector() {

			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.WITCH);
			}
		};

		PotionMeta def = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);

		for (String potionType : plugin.getConfig().getConfigurationSection("Mobs.Witch.Potions").getKeys(false)) {
			String path = "Mobs.Witch.Potions." + potionType;
			def.addCustomEffect(
					new PotionEffect(PotionEffectType.getByName(potionType),
							plugin.getConfig().getInt(path + ".Duration"), plugin.getConfig().getInt(path + ".Level")),
					true);
			potions.put(def, plugin.getConfig().getDouble(path + ".Probability"));
		}
	}

	@EventHandler
	public void onSpawn(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Witch witch = (Witch) event.getEntity();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		ItemStack potion = new ItemStack(Material.POTION);

		boolean hasPotion = false;

		for (Entry<PotionMeta, Double> entry : potions.entrySet()) {
			if (random.nextDouble() > entry.getValue())
				continue;
			potion.setItemMeta(entry.getKey());
			hasPotion = true;
			break;
		}

		if (!hasPotion)
			return;

		witch.setDrinkingPotion(potion);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Witch witch = (Witch) event.getEntity();
		if (witch.isInsideVehicle())
			return;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextDouble() > CE.WITCH_BATSPAWN_PROBABILITY.doubleValue())
			return;
		Bat bat = (Bat) witch.getWorld().spawnEntity(witch.getLocation(), EntityType.BAT);
		bat.addPassenger(witch);
	}

	@Override
	public void disable() {
		EntityTargetLivingEntityEvent.getHandlerList().unregister(this);
	}

}
