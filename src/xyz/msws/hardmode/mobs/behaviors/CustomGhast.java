package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.MSG;

public class CustomGhast extends BehaviorListener {

	private Map<Player, Long> times;
	private Map<Ghast, BukkitTask> tasks = new HashMap<Ghast, BukkitTask>();

	private ThreadLocalRandom random;

	public CustomGhast(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.GHAST);
			}
		};

		times = new HashMap<Player, Long>();

		random = ThreadLocalRandom.current();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (block.getY() < block.getWorld().getMaxHeight() - 30)
			return;

		if (System.currentTimeMillis() - times.getOrDefault(player, (long) 0) > 60000 * 10) {
			MSG.tell(player, "&eAn odd presence says you shouldn't place blocks this high...");
			times.put(player, System.currentTimeMillis());
			return;
		}

		if (random.nextDouble() > .05)
			return;
		MSG.tell(player, "&6You should have listened...");
		Location spawn = block.getLocation().clone();
		spawn.add(random.nextDouble(-30, 30), random.nextDouble(-5, 5), random.nextDouble(-30, 30));

		spawn.getWorld().spawnEntity(spawn, EntityType.GHAST);
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Ghast ghast = (Ghast) event.getEntity();

		if (ghast.getTarget() instanceof Player) {
			GameMode gm = ((Player) ghast.getTarget()).getGameMode();
			if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR)
				return;
		}

		if (event.getTarget() == null && ghast.getTarget() != null)
			if (ghast.getTarget().isValid()
					&& ghast.getLocation().distanceSquared(ghast.getTarget().getLocation()) <= 2500) {
				event.setTarget(ghast.getTarget());
				if (tasks.containsKey(ghast))
					return;
			}

		if (event.getTarget() == null)
			return;
		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Ghast runnable started targetting " + event.getTarget() + ".");
		tasks.put(ghast, shoot(ghast, event.getTarget()).runTaskTimer((Plugin) plugin, 10, 120));
	}

	public BukkitRunnable shoot(Ghast ghast, LivingEntity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (ghast == null || !ghast.isValid() || ghast.getTarget() == null || !ghast.getTarget().isValid()
						|| ghast.getTarget() != target) {
					this.cancel();
					tasks.remove(ghast);
					return;
				}
				plugin.getMobManager().getAttack(AID.FIREBALL).attack(ghast, target);
			}
		};
	}

	@Override
	public void disable() {
		BlockPlaceEvent.getHandlerList().unregister(this);
	}

}
