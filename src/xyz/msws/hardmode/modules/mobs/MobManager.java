package xyz.msws.hardmode.modules.mobs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.attacks.Attack;
import xyz.msws.hardmode.attacks.BlockItemThrowAttack;
import xyz.msws.hardmode.attacks.BlockPhysicThrowAttack;
import xyz.msws.hardmode.attacks.ChargeAtAttack;
import xyz.msws.hardmode.attacks.FastArrowAttack;
import xyz.msws.hardmode.attacks.FireballAttack;
import xyz.msws.hardmode.attacks.GrabSlamAttack;
import xyz.msws.hardmode.attacks.GroundPoundAttack;
import xyz.msws.hardmode.attacks.TNTAttack;
import xyz.msws.hardmode.mobs.behaviors.CustomBlaze;
import xyz.msws.hardmode.mobs.behaviors.CustomCreeper;
import xyz.msws.hardmode.mobs.behaviors.CustomEnderman;
import xyz.msws.hardmode.mobs.behaviors.CustomGhast;
import xyz.msws.hardmode.mobs.behaviors.CustomGuardian;
import xyz.msws.hardmode.mobs.behaviors.CustomSkeleton;
import xyz.msws.hardmode.mobs.behaviors.CustomSlime;
import xyz.msws.hardmode.mobs.behaviors.CustomSpider;
import xyz.msws.hardmode.mobs.behaviors.CustomWitch;
import xyz.msws.hardmode.mobs.behaviors.CustomZombie;
import xyz.msws.hardmode.mobs.behaviors.GlobalMobs;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class MobManager extends AbstractModule {

	public MobManager(HardMode plugin) {
		super("MobManager", plugin);
	}

	private List<BehaviorListener> behaviors;
	private Map<AID, Attack> attacks;

	@Override
	public void initialize() {
		attacks = new HashMap<AID, Attack>();

		loadAttacks();

		behaviors = Arrays.asList(new CustomCreeper(plugin), new CustomSkeleton(plugin), new CustomZombie(plugin),
				new CustomSpider(plugin), new CustomBlaze(plugin), new CustomWitch(plugin), new CustomEnderman(plugin),
				new CustomGhast(plugin), new CustomSlime(plugin), new CustomGuardian(plugin), new GlobalMobs(plugin));
	}

	public void loadAttacks() {
		attacks.put(AID.FAST_ARROW, new FastArrowAttack(plugin));
		attacks.put(AID.TNT, new TNTAttack(plugin));
		attacks.put(AID.GRAB_SLAM, new GrabSlamAttack(plugin));
		attacks.put(AID.BLOCK_ITEM_THROW, new BlockItemThrowAttack(plugin));
		attacks.put(AID.BLOCK_PHYSIC_THROW, new BlockPhysicThrowAttack(plugin));
		attacks.put(AID.FIREBALL, new FireballAttack(plugin));
		attacks.put(AID.CHARGE_AT, new ChargeAtAttack(plugin));
		attacks.put(AID.GROUND_POUND, new GroundPoundAttack(plugin));
	}

	public Attack getAttack(AID attack) {
		return attacks.get(attack);
	}

	@Override
	public void disable() {
		behaviors.forEach(b -> b.disable());
		attacks.clear();
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.HIGH;
	}

}
