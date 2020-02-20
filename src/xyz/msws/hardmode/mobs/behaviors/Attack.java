package xyz.msws.hardmode.mobs.behaviors;

import org.bukkit.entity.Entity;

public interface Attack {
	void attack(Entity attacker, Entity target);
}