package xyz.msws.hardmode.modules.mobs;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface Boss {
	BossType getBossType();

	Entity getEntity();

	Map<List<ItemStack>, Double> getLoot();
}
