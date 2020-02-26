package xyz.msws.hardmode.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.inventory.ItemStack;

public interface Loot {

	public Map<List<ItemStack>, Double> getLoot();

	public default List<ItemStack> getDrops() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (Entry<List<ItemStack>, Double> entry : getLoot().entrySet()) {
			if (random.nextDouble() >= random.nextDouble())
				continue;
			items.addAll(entry.getKey());
		}
		return items;
	}

}
