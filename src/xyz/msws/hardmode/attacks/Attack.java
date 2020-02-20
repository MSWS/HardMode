package xyz.msws.hardmode.attacks;

import org.bukkit.entity.Entity;

public interface Attack {
	AID getID();

	void attack(Entity attacker, Entity target);

	default void attack(Entity attacker, Entity target, Object... data) {
		attack(attacker, target);
	}
}