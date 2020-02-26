package xyz.msws.hardmode.attacks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.MSG;

public class GroundPoundAttack implements Attack {

	private HardMode plugin;

	public GroundPoundAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public AID getID() {
		return AID.GROUND_POUND;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		if (!attacker.isOnGround())
			return;
		Vector vec = target.getLocation().clone().toVector().subtract(attacker.getLocation().clone().toVector());
		vec.multiply(.2);
		vec.setY(1);

		attacker.setVelocity(vec);

		ThreadLocalRandom random = ThreadLocalRandom.current();

		new BukkitRunnable() {
			boolean charge = false;

			long jumpTime = System.currentTimeMillis();

			double lastY = 0;

			@Override
			public void run() {
				if (attacker == null || !attacker.isValid() || target == null || !target.isValid()) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() - jumpTime > 5000) {
					this.cancel();
					return;
				}

				double hDist = attacker.getLocation().distanceSquared(target.getLocation())
						- Math.pow(Math.abs(attacker.getLocation().getY() - target.getLocation().getY()), 2);
				if (hDist <= 1 && !charge) {
					charge = true;
					attacker.setVelocity(new Vector(0, -.8, 0));
				}

				Block below = attacker.getLocation().getBlock().getRelative(BlockFace.DOWN);
				Location loc = below.getLocation();

				if (System.currentTimeMillis() - jumpTime < 500)
					return;

				if (attacker.getLocation().getY() != lastY) {
					lastY = attacker.getLocation().getY();
					return;
				}

				for (Entity ent : attacker.getLocation().getNearbyEntities(3, 3, 3)) {
					if (ent.equals(attacker))
						continue;
					if (!(ent instanceof LivingEntity))
						continue;
					LivingEntity e = (LivingEntity) ent;
					e.damage(10, attacker);
				}

				List<FallingBlock> blocks = new ArrayList<FallingBlock>();

				new BukkitRunnable() {
					int range = 0;

					Material[] types = new Material[] { Material.IRON_BLOCK, Material.IRON_ORE, Material.IRON_BARS,
							Material.IRON_TRAPDOOR };

					@Override
					public void run() {
						if (range >= 5) {
							this.cancel();
							return;
						}
						for (int x = -range; x < range; x++) {
							for (int z = -range; z < range; z++) {
								Block b = below.getLocation().clone().add(x, 0, z).getBlock();
								Material type = b.getType();
								if (type == Material.AIR || type == Material.BEDROCK || type == Material.OBSIDIAN)
									continue;
								type = random.nextDouble() > .1 ? type : types[random.nextInt(types.length)];
								BlockData data = Bukkit.createBlockData(type);
								b.setType(Material.AIR);

								FallingBlock fall = b.getWorld().spawnFallingBlock(b.getLocation(), data);

								Vector vel = b.getLocation().toVector().subtract(loc.toVector());
								vel.normalize();
								vel.multiply(range / random.nextDouble(6, 11));
								vel.setY(range / random.nextDouble(4, 6));

								if (x == 0 && z == 0)
									vel = new Vector(random.nextDouble(-.5, .5), 1, 0);

								fall.setVelocity(vel);
								b.getWorld().playSound(b.getLocation(), getBreakSound(type), 2, 1);
								blocks.add(fall);
							}
						}
						range++;
					}
				}.runTaskTimer(plugin, 0, 2);

				new BukkitRunnable() {

					@Override
					public void run() {
						List<FallingBlock> bs = blocks;
						bs = bs.stream().filter(b -> b.isValid()).collect(Collectors.toList());

						for (FallingBlock f : bs) {
							for (Entity ent : f.getLocation().getNearbyEntities(1, 1, 1)) {
								if (ent.equals(attacker))
									continue;
								if (!(ent instanceof LivingEntity))
									continue;
								LivingEntity e = (LivingEntity) ent;
								e.damage(4, attacker);
							}
						}

						if (bs.isEmpty()) {
							this.cancel();
							return;
						}
					}
				}.runTaskTimer(plugin, 5, 1);
				this.cancel();
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	private Sound getBreakSound(Material mat) {
		try {
			return Sound.valueOf("BLOCK_" + mat + "_BREAK");
		} catch (IllegalArgumentException e) {

		}

		switch (mat) {
		case GRASS_BLOCK:
		case DIRT:
			return Sound.BLOCK_GRASS_BREAK;
		case GLASS:
			return Sound.BLOCK_GLASS_BREAK;
		default:
			MSG.log("Unknown sound break type: " + mat);
		case IRON_BLOCK:
		case IRON_BARS:
		case IRON_ORE:
		case IRON_TRAPDOOR:
			return Sound.BLOCK_STONE_BREAK;
		}
	}

}
