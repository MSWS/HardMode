package xyz.msws.hardmode.modules.mobs;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.inventory.ItemStack;

public class GolemBoss implements Boss {

	private IronGolem golem;

	public GolemBoss(IronGolem golem) {
		this.golem = (IronGolem) golem;
	}

	@Override
	public BossType getBossType() {
		return BossType.GOLEM;
	}

	@Override
	public Entity getEntity() {
		return golem;
	}

	@Override
	public Map<List<ItemStack>, Double> getLoot() {
		return null;
	}

}
