package xyz.msws.hardmode.modules.mobs;

import org.bukkit.entity.EntityType;

import xyz.msws.hardmode.boss.behaviors.GolemBoss;

public enum BossType {
	GOLEM(EntityType.IRON_GOLEM, "&e&lLOGEM", GolemBoss.class);

	private EntityType type;
	private Class<? extends Boss> clazz;
	private String name;

	BossType(EntityType entityType, String name, Class<? extends Boss> clazz) {
		this.type = entityType;
		this.name = name;
		this.clazz = clazz;
	}

	public EntityType getType() {
		return type;
	}

	public Class<? extends Boss> getBossClass() {
		return this.clazz;
	}

	public String getName() {
		return name;
	}
}
