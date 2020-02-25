package xyz.msws.hardmode.modules.mobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.boss.GolemSpawner;
import xyz.msws.hardmode.boss.HBoss;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.utils.MSG;

public class BossManager extends AbstractModule {

	public BossManager(HardMode plugin) {
		super("BossManager", plugin);
	}

	private List<HBoss> bosses;
	private List<BossSpawner> spawners;

	@Override
	public void initialize() {
		bosses = new ArrayList<HBoss>();
		spawners = new ArrayList<>();

		spawners.add(new GolemSpawner(plugin));
	}

	@Override
	public void disable() {
		for (HBoss boss : bosses) {
			boss.getEntity().remove();
		}
		bosses.clear();
		spawners.forEach(s -> s.disable());
	}

	public void addBoss(HBoss boss) {
		Location loc = boss.getEntity().getLocation();
		MSG.announce("A NEW BOSS HAS SPAWNED AT " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
		bosses.add(boss);
	}

	public List<HBoss> getBosses() {
		return new ArrayList<>(bosses);
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.MEDIUM;
	}

}
