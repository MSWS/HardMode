package xyz.msws.hardmode.mobs.behaviors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class CustomEnderman extends BehaviorListener {

	private ThreadLocalRandom random;
	private Map<Enderman, BukkitTask> tasks = new HashMap<Enderman, BukkitTask>();
	private List<Material> canPickup;

	public CustomEnderman(HardMode plugin) {
		super(plugin);

		random = ThreadLocalRandom.current();

		canPickup = Arrays.asList(Material.CLAY, Material.COARSE_DIRT, Material.DIRT, Material.SUNFLOWER,
				Material.GRASS_BLOCK, Material.GRAVEL);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.ENDERMAN);
			}
		};
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Enderman ender = (Enderman) event.getEntity();

		if (ender.getTarget() instanceof Player) {
			GameMode gm = ((Player) ender.getTarget()).getGameMode();
			if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR)
				return;
		}

		if (event.getTarget() == null && ender.getTarget() != null)
			if (ender.getTarget().isValid()
					&& ender.getLocation().distanceSquared(ender.getTarget().getLocation()) <= 2500) {
				event.setTarget(ender.getTarget());
				if (tasks.containsKey(ender))
					return;
			}

		if (event.getTarget() == null)
			return;

		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Enderman runnable started targetting " + event.getTarget() + ".");
		tasks.put(ender, chuck(ender, event.getTarget()).runTaskTimer((Plugin) plugin, 20 * 3, 40));
	}

	public BukkitRunnable chuck(Enderman ender, Entity target) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (ender == null || !ender.isValid() || ender.getTarget() == null || !ender.getTarget().isValid()
						|| ender.getTarget() != target) {
					this.cancel();
					tasks.remove(ender);
					return;
				}

				Material mat = ender.getCarriedBlock() == null ? null : ender.getCarriedBlock().getMaterial();
				if (mat == null || mat == Material.AIR) {
					if (random.nextDouble() > .7)
						return;
					Block below = ender.getLocation().getBlock().getRelative(BlockFace.DOWN);
					if (!canPickup.contains(below.getType()))
						return;
					BlockData data = Bukkit.createBlockData(below.getType());
					ender.setCarriedBlock(data);
					below.setType(Material.AIR);
					return;
				}
				plugin.getMobManager().getAttack("blockthrow").attack(ender, target, mat);
				ender.getWorld().playSound(ender.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 1);
				ender.setCarriedBlock(null);
			}
		};
	}

	@EventHandler
	public void grabAndTeleport(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getDamager()))
			return;
		if (random.nextDouble() > .3)
			return;
		Enderman ender = (Enderman) event.getDamager();
		plugin.getMobManager().getAttack("grabteleport").attack(ender, event.getDamager());
	}

	@EventHandler
	public void teleportBehind(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Entity damager = event.getDamager();

		Enderman ender = (Enderman) event.getEntity();

		if (ender.getLocation().distanceSquared(damager.getLocation()) > 25)
			return;

		Vector direction = damager.getLocation().clone().getDirection().normalize();
		direction.multiply(-1);
		direction.multiply(ender.getLocation().distance(damager.getLocation()));
		Location destination = ender.getLocation().clone().toVector().add(direction).toLocation(ender.getWorld());
		ender.teleport(destination);
	}

	@Override
	public void disable() {

	}

}
