package xyz.msws.hardmode.modules.mobs;

import org.bukkit.entity.EntityType;

public enum BossType {
	GOLEM("IRON_GOLEM", GolemBoss.class);

	private EntityType type;
	private Class<? extends Boss> clazz;

	BossType(String entityType, Class<? extends Boss> clazz) {
		this.type = EntityType.valueOf(entityType);
		this.clazz = clazz;
	}

	public EntityType getType() {
		return type;
	}

	public Class<? extends Boss> getBossClass() {
		return this.clazz;
	}
}
