package xyz.msws.hardmode.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class LootGenerator implements Loot {

	private Map<List<ItemStack>, Double> loot;

	public LootGenerator() {
		this.loot = new HashMap<List<ItemStack>, Double>();
	}

	public LootGenerator(Map<List<ItemStack>, Double> loot) {
		this.loot = loot;
	}

	public LootGenerator addLoot(List<ItemStack> items, Double chance) {
		loot.put(items, chance);
		return this;
	}

	public LootGenerator addItem(ItemStack item, Double chance) {
		loot.put(Arrays.asList(item), chance);
		return this;
	}

	@Override
	public Map<List<ItemStack>, Double> getLoot() {
		return loot;
	}

}
