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
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

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
			if (ender.getTarget().isValid() && ender.getLocation().distanceSquared(ender.getTarget()
					.getLocation()) <= CE.ENDERMAN_UNTARGETDISTANCESQUARED.getValue(Number.class).intValue()) {
				event.setTarget(ender.getTarget());
				if (tasks.containsKey(ender))
					return;
			}

		if (event.getTarget() == null)
			return;

		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Enderman runnable started targetting " + event.getTarget() + ".");
		tasks.put(ender,
				chuck(ender, event.getTarget()).runTaskTimer((Plugin) plugin,
						CE.ENDERMAN_CHUCK_STARTDELAY.getValue(Number.class).intValue(),
						CE.ENDERMAN_CHUCK_PERIODDELAY.getValue(Number.class).intValue()));
	}

	public BukkitRunnable chuck(Enderman ender, Entity target) {
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
					if (random.nextDouble() > CE.ENDERMAN_CHUCK_PROBABILITY.getValue(Number.class).doubleValue())
						return;
					Block below = ender.getLocation().getBlock().getRelative(BlockFace.DOWN);
					if (!canPickup.contains(below.getType()))
						return;
					BlockData data = Bukkit.createBlockData(below.getType());
					ender.setCarriedBlock(data);
					below.setType(Material.AIR);
					return;
				}
				plugin.getMobManager().getAttack(AID.BLOCK_PHYSIC_THROW).attack(ender, target, mat);
				ender.getWorld().playSound(ender.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 1);
				ender.setCarriedBlock(null);
			}
		};
	}

	@EventHandler
	public void grabAndTeleport(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getDamager()))
			return;

		double chance = CE.ENDERMAN_GRABTELEPORT_PROBABILITY_REGULAR.getValue(Number.class).doubleValue();

		if (event.getEntity() instanceof Player) {
			if (((Player) event.getEntity()).isBlocking())
				chance = CE.ENDERMAN_GRABTELEPORT_PROBABILITY_BLOCKING.getValue(Number.class).doubleValue();
		}

		if (event.getDamager().equals(event.getEntity()))
			return;

		if (random.nextDouble() > chance)
			return;

		Enderman ender = (Enderman) event.getDamager();
		plugin.getMobManager().getAttack(AID.GRAB_SLAM).attack(ender, event.getEntity());
	}

	@EventHandler
	public void teleportBehind(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Entity damager = event.getDamager();

		Enderman ender = (Enderman) event.getEntity();

		if (random.nextDouble() > CE.ENDERMAN_TELEPORTBEHIND_PROBABILITY.getValue(Number.class).doubleValue())
			return;

		if (ender.getLocation().distanceSquared(damager.getLocation()) > CE.ENDERMAN_TELEPORTBEHIND_MINIMUMDISTANCE
				.getValue(Number.class).doubleValue())
			return;

		Vector direction = damager.getLocation().clone().getDirection().normalize();
		direction.multiply(
				random.nextDouble(CE.ENDERMAN_TELEPORTBEHIND_DISTANCE_MINIMUM.getValue(Number.class).doubleValue(),
						CE.ENDERMAN_TELEPORTBEHIND_DISTANCE_MAXIMUM.getValue(Number.class).doubleValue()));
		direction.multiply(ender.getLocation().distance(damager.getLocation()));
		Location destination = ender.getLocation().clone().toVector().add(direction).toLocation(ender.getWorld());
		ender.teleport(destination);
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Enderman ender = (Enderman) event.getEntity();
		ender.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
				.setBaseValue(CE.ENDERMAN_DAMAGE.getValue(Number.class).doubleValue());
	}

	@Override
	public void disable() {
		EntityTargetLivingEntityEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
		EntitySpawnEvent.getHandlerList().unregister(this);
	}

}
