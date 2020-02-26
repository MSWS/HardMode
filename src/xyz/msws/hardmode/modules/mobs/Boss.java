package xyz.msws.hardmode.modules.mobs;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmode.world.Area;

public interface Boss {
	BossType getBossType();

	Entity getEntity();

	Map<List<ItemStack>, Double> getLoot();

	Area getArea();

	MobSelector getSelector();
	
	PeriodManager getAttackManager();

	default boolean canTarget(Entity ent) {
		return getSelector().matches(ent);
	}
}
