package xyz.msws.hardmode.attacks;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;

public class BlockItemThrowAttack implements Attack {

	private HardMode plugin;

	public BlockItemThrowAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
	}

	@Override
	public void attack(Entity attacker, Entity target, Object... data) {

		Preconditions.checkArgument(attacker.getWorld().equals(target.getWorld()),
				"Attacker and world not in same world");

		Preconditions.checkArgument(data[0].getClass().equals(Material.class), "Data must be Material");

		Material material = (Material) data[0];

		if (attacker.getLocation().distanceSquared(target.getLocation()) >= 100)
			return;

		Item item = attacker.getWorld().dropItem(attacker.getLocation(), new ItemStack(material));
		item.setPickupDelay(999999);
		Vector aim = target.getLocation().toVector().subtract(attacker.getLocation().toVector());
		aim.normalize();
		aim.multiply(.8);
		aim.setY(aim.getY() + .5);
		item.setVelocity(aim);

		long shootTime = System.currentTimeMillis();

		new BukkitRunnable() {
			@Override
			public void run() {
				Block block = item.getLocation().getBlock();
				if (block.getType().isSolid())
					return;
				if (!item.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
					Entity target = item.getNearbyEntities(1, 1, 1).stream().filter(ent -> !ent.equals(attacker))
							.filter(ent -> (ent instanceof LivingEntity)).findFirst().orElse(null);

					if (target == null)
						return;
					item.teleport(target.getLocation().clone().subtract(0, 1, 0));
					block = target.getLocation().getBlock();
				}
				if (System.currentTimeMillis() > shootTime + 10000) {
					item.remove();
					this.cancel();
					return;
				}
				item.remove();
				BlockData oldBlock = block.getBlockData();
				block.setType(material);
				for (Entity ent : block.getLocation().getNearbyEntities(1, 1, 1)) {
					if (ent.equals(attacker))
						continue;
					if (!(ent instanceof LivingEntity))
						continue;
					LivingEntity e = (LivingEntity) ent;
					e.damage(3, e);
					if (e instanceof Player)
						((Player) e).playSound(e.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
				}

				final Block toRemove = block;

				new BukkitRunnable() {
					@Override
					public void run() {
						toRemove.setBlockData(oldBlock);
					}
				}.runTaskLater(plugin, 20 * 20);

				this.cancel();
			}
		}.runTaskTimer(plugin, 5, 1);

	}

	@Override
	public AID getID() {
		return AID.BLOCK_ITEM_THROW;
	}

}
