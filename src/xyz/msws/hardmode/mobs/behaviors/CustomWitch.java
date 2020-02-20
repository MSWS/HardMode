package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

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
		def.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 2), true);
		potions.put(def, 1.0 / 50);
		def.clearCustomEffects();
		def.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 7 * 20, 0), true);
		potions.put(def, 1.0 / 30);
		def.clearCustomEffects();
		def.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 1), true);
		potions.put(def, 1.0 / 40);
		def.clearCustomEffects();
		def.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 2), true);
		potions.put(def, 1.0 / 35);
	}

	@EventHandler
	public void onSpawn(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		if (event.getEntity() == null || !event.getEntity().isValid())
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

		if (!hasPotion) {
			return;
		}

		witch.setDrinkingPotion(potion);
	}

	@Override
	public void disable() {

	}

}
