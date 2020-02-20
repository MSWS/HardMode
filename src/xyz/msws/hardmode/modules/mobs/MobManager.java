package xyz.msws.hardmode.modules.mobs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.mobs.behaviors.Attack;
import xyz.msws.hardmode.mobs.behaviors.CustomBlaze;
import xyz.msws.hardmode.mobs.behaviors.CustomCreeper;
import xyz.msws.hardmode.mobs.behaviors.CustomSkeleton;
import xyz.msws.hardmode.mobs.behaviors.CustomSpider;
import xyz.msws.hardmode.mobs.behaviors.CustomZombie;
import xyz.msws.hardmode.mobs.behaviors.FastArrowAttack;
import xyz.msws.hardmode.mobs.behaviors.GlobalMobs;
import xyz.msws.hardmode.mobs.behaviors.TNTAttack;
import xyz.msws.hardmode.mobs.behaviors.WebAttack;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class MobManager extends AbstractModule {

	public MobManager(HardMode plugin) {
		super("mobmanager", plugin);
	}

	private List<BehaviorListener> behaviors;
	private Map<String, Attack> attacks;

	@Override
	public void initialize() {
		attacks = new HashMap<String, Attack>();

		loadAttacks();

		behaviors = Arrays.asList(new CustomCreeper(plugin), new CustomSkeleton(plugin), new CustomZombie(plugin),
				new CustomSpider(plugin), new CustomBlaze(plugin), new GlobalMobs(plugin));
	}

	public void loadAttacks() {
		attacks.put("web", new WebAttack(plugin));
		attacks.put("fastarrow", new FastArrowAttack(plugin));
		attacks.put("tnt", new TNTAttack(plugin));
	}

	public Attack getAttack(String id) {
		return attacks.get(id);
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
