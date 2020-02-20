package xyz.msws.hardmode.attacks;

import org.bukkit.entity.Entity;

public interface Attack {
	void attack(Entity attacker, Entity target);
}