package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class CustomSpider extends BehaviorListener {

	private Map<Entity, BukkitTask> tasks;

	public CustomSpider(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.SPIDER || ent.getType() == EntityType.CAVE_SPIDER);
			}
		};

		tasks = new HashMap<Entity, BukkitTask>();
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		LivingEntity ent = (LivingEntity) event.getEntity();
		ent.getAttribute(Attribute.GENERIC_MAX_HEALTH)
				.setBaseValue(ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * .7);
		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				.setBaseValue(ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1.25);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;

		Spider spider = (Spider) event.getEntity();

		if (spider.getTarget() instanceof Player) {
			GameMode gm = ((Player) spider.getTarget()).getGameMode();
			if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR)
				return;
		}

		if (event.getTarget() == null && spider.getTarget() != null)
			if (spider.getTarget().isValid()
					&& spider.getLocation().distanceSquared(spider.getTarget().getLocation()) <= 400) {
				event.setTarget(spider.getTarget());
				if (tasks.containsKey(spider))
					return;
			}

		if (event.getTarget() == null)
			return;
		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Spider runnable started targetting " + event.getTarget() + ".");

		ThreadLocalRandom random = ThreadLocalRandom.current();

		new BukkitRunnable() {
			long nextShot = System.currentTimeMillis();

			@Override
			public void run() {
				if (spider == null || !spider.isValid() || spider.getTarget() == null || !spider.getTarget().isValid()
						|| !spider.getTarget().equals(event.getTarget())) {
					plugin.log("Spider runnable ended.");
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() < nextShot)
					return;
				nextShot += random.nextDouble(500, 6000);
				plugin.getMobManager().getAttack("blockthrow").attack(spider, event.getTarget(), Material.COBWEB);
			}
		}.runTaskTimer(plugin, 20, 1);
	}

	@Override
	public void disable() {
	}

}
