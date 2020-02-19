package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmode.HardMobs;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class GlobalMobs extends BehaviorListener {
	private final Map<ItemStack, Double> loot;

	public GlobalMobs(HardMobs plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent instanceof Monster);
			}
		};

		loot = new HashMap<ItemStack, Double>();
		loot.put(new ItemStack(Material.GOLD_INGOT, 2), .05);
		loot.put(new ItemStack(Material.GOLD_NUGGET, 3), .2);
		loot.put(new ItemStack(Material.GOLD_INGOT, 1), .1);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
//		Monster entity = (Monster) event.getEntity();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextBoolean())
			return;
		for (Entry<ItemStack, Double> entry : loot.entrySet()) {
			if (random.nextDouble() > entry.getValue())
				continue;
			event.getDrops().add(entry.getKey());
		}
	}

	@Override
	public void disable() {

	}
}
