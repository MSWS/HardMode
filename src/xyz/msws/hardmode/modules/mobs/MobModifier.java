package xyz.msws.hardmode.modules.mobs;

import org.bukkit.entity.Entity;

import xyz.msws.hardmode.HardMobs;

public abstract class MobModifier extends BehaviorListener {

	private Entity mob;

	public MobModifier(HardMobs plugin) {
		super(plugin);
		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return mob.equals(ent);
			}
		};
	}

	public void setEntity(Entity ent) {
		this.mob = ent;
	}

	@Override
	public void disable() {
	}

}
