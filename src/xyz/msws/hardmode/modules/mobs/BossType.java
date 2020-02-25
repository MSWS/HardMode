package xyz.msws.hardmode.modules.mobs;

import org.bukkit.entity.EntityType;

public enum BossType {
	GOLEM("IRON_GOLEM");

	private EntityType type;

	BossType(String entityType) {
		this.type = EntityType.valueOf(entityType);
	}

	/**
	 * @return the type
	 */
	public EntityType getType() {
		return type;
	}

}
