package xyz.msws.hardmode.modules.mobs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class BossManager extends AbstractModule {

	private Map<UUID, Boss> bosses;

	public BossManager(HardMode plugin) {
		super("BossManager", plugin);

	}

	@Override
	public void initialize() {
		bosses = new HashMap<UUID, Boss>();

		Bukkit.getBossBars().forEachRemaining(bar -> bar.removeAll());
	}

	@Override
	public void disable() {
		for (Boss boss : bosses.values())
			boss.getEntity().remove();

	}

	@SuppressWarnings("unchecked")
	public Boss spawnBoss(BossType type, Location loc) {
		Entity boss = loc.getWorld().spawnEntity(loc, type.getType());
		Boss b = null;
		try {
			Class<? extends Entity> clazz = null;
			Constructor<Boss> constructor = null;

			for (Constructor<?> c : type.getBossClass().getConstructors()) {
				for (Class<?> t : c.getParameterTypes()) {
					if (Entity.class.isAssignableFrom(t)) {
						clazz = (Class<? extends Entity>) t;
					}
				}
				constructor = (Constructor<Boss>) c;
			}

			b = constructor.newInstance(clazz.cast(boss));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			e.printStackTrace();
		}
		if (b == null)
			return null;
		UUID uuid = boss.getUniqueId();
		bosses.put(uuid, b);
		return b;
	}

	public boolean isBoss(UUID uuid) {
		return bosses.containsKey(uuid);
	}

	public Boss getBoss(UUID uuid) {
		return bosses.get(uuid);
	}

	public Collection<Boss> getBosses() {
		return bosses.values();
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.MEDIUM;
	}

}
