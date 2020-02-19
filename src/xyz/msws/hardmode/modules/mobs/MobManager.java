package xyz.msws.hardmode.modules.mobs;

import java.util.Arrays;
import java.util.List;

import xyz.msws.hardmode.HardMobs;
import xyz.msws.hardmode.mobs.behaviors.CustomCreeper;
import xyz.msws.hardmode.mobs.behaviors.CustomSkeleton;
import xyz.msws.hardmode.mobs.behaviors.CustomZombie;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class MobManager extends AbstractModule {

	public MobManager(HardMobs plugin) {
		super("mobmanager", plugin);
	}

	private List<BehaviorListener> behaviors;

	@Override
	public void initialize() {
		behaviors = Arrays.asList(new CustomCreeper(plugin), new CustomSkeleton(plugin), new CustomZombie(plugin));
	}

	@Override
	public void disable() {
		behaviors.forEach(b -> b.disable());
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOW;
	}

}
