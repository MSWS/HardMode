package xyz.msws.hardmobs.modules.mobs;

import java.util.Arrays;
import java.util.List;

import xyz.msws.hardmobs.HardMobs;
import xyz.msws.hardmobs.behaviors.CustomCreeper;
import xyz.msws.hardmobs.behaviors.CustomSkeleton;
import xyz.msws.hardmobs.behaviors.CustomZombie;
import xyz.msws.hardmobs.modules.AbstractModule;
import xyz.msws.hardmobs.modules.ModulePriority;

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
