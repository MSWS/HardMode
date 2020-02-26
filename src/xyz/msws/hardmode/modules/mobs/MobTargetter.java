package xyz.msws.hardmode.modules.mobs;

import java.util.List;

import org.bukkit.entity.Entity;

public interface MobTargetter {
	Entity getTarget(List<Entity> entities);
}
