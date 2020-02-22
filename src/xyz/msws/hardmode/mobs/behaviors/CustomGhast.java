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
import xyz.msws.hardmode.utils.CE;
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

		if (block.getY() < CE.GHAST_SPAWNING_BLOCKHEIGHT.getValue(Number.class).intValue())
			return;

		if (System.currentTimeMillis() - times.getOrDefault(player, (long) 0) > CE.GHAST_SPAWNING_MESSAGECOOLDOWN
				.getValue(Number.class).doubleValue() * 1000) {
			MSG.tell(player, CE.GHAST_SPAWNING_MESSAGEONPLACE.getValue(String.class));
			times.put(player, System.currentTimeMillis());
			return;
		}

		if (random.nextDouble() > CE.GHAST_SHOOT_PROBABILITY.getValue(Number.class).doubleValue())
			return;

		MSG.tell(player, CE.GHAST_SPAWNING_MESSAGEONSPAWN.getValue(String.class));
		Location spawn = block.getLocation().clone();
		double hDist = CE.GHAST_SPAWNING_HORIZONTALRANGE.getValue(Number.class).doubleValue();
		double yDist = CE.GHAST_SPAWNING_VERTICALRANGE.getValue(Number.class).doubleValue();
		spawn.add(random.nextDouble(-hDist, hDist), random.nextDouble(-yDist, yDist), random.nextDouble(-hDist, hDist));

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
			if (ghast.getTarget().isValid() && ghast.getLocation().distanceSquared(ghast.getTarget()
					.getLocation()) <= CE.GHAST_UNTARGETDISTANCESQUARED.getValue(Number.class).doubleValue()) {
				event.setTarget(ghast.getTarget());
				if (tasks.containsKey(ghast))
					return;
			}

		if (event.getTarget() == null)
			return;
		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Ghast runnable started targetting " + event.getTarget() + ".");
		tasks.put(ghast,
				shoot(ghast, event.getTarget()).runTaskTimer((Plugin) plugin,
						CE.GHAST_SHOOT_STARTDELAY.getValue(Number.class).intValue(),
						CE.GHAST_SHOOT_PERIODDELAY.getValue(Number.class).intValue()));
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
