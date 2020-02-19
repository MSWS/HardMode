package xyz.msws.hardmode.modules.mobs;

import org.bukkit.entity.Entity;

public interface MobSelector {
	public boolean matches(Entity ent);
}
