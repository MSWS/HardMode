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
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

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
		ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(CE.SPIDER_HEALTH.doubleValue());
		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(CE.SPIDER_SPEED.doubleValue());
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

		new BukkitRunnable() {
			ThreadLocalRandom random = ThreadLocalRandom.current();

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
				nextShot += random.nextDouble(CE.SPIDER_COBTHROW_NEXTSHOT_MINIMUM.getValue(Number.class).longValue(),
						CE.SPIDER_COBTHROW_NEXTSHOT_MAXIMUM.getValue(Number.class).longValue());
				plugin.getMobManager().getAttack(AID.BLOCK_ITEM_THROW).attack(spider, event.getTarget(),
						CE.SPIDER_COBTHROW_MATERIAL.getValue(Material.class));
			}
		}.runTaskTimer(plugin, CE.SPIDER_COBTHROW_STARTDELAY.intValue(), 1);
	}

	@Override
	public void disable() {
	}

}
