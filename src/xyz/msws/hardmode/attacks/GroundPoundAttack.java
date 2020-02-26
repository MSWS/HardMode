package xyz.msws.hardmode.attacks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
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
		Vector vec = target.getLocation().clone().toVector().subtract(attacker.getLocation().clone().toVector());
		vec.normalize();
		vec.setY(3);

		attacker.setVelocity(vec);
		new BukkitRunnable() {
			long jumpTime = System.currentTimeMillis();

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
				MSG.announce(hDist + "");
				if (hDist >= 1)
					return;
				attacker.setVelocity(new Vector(0, -.4, 0));

				Block below = attacker.getLocation().getBlock().getRelative(BlockFace.DOWN);
				Location loc = below.getLocation();
				if (!below.getType().isSolid())
					return;

				for (int range = 1; range < 5; range++) {
					for (int x = -range; x < range; x++) {
						for (int z = -range; z < range; z++) {
							Block b = below.getLocation().clone().add(x, 0, z).getBlock();
							Material type = b.getType();
							BlockData data = Bukkit.createBlockData(type);
							FallingBlock fall = b.getWorld().spawnFallingBlock(b.getLocation(), data);
							Vector vel = b.getLocation().toVector().subtract(loc.toVector());
							vel.setY(1);
							fall.setVelocity(vel);
						}
					}
				}

				this.cancel();
			}
		}.runTaskTimer(plugin, 0, 1);
	}

}
