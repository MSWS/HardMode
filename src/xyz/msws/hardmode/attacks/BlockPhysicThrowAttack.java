package xyz.msws.hardmode.attacks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.CE;

public class BlockPhysicThrowAttack implements Attack {

	private HardMode plugin;

	public BlockPhysicThrowAttack(HardMode plugin) {
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

		if (attacker.getLocation().distanceSquared(target.getLocation()) >= CE.BLOCKPHYSICTHROW_MINIMUMDISTANCE
				.doubleValue())
			return;

		FallingBlock fall = attacker.getWorld().spawnFallingBlock(attacker.getLocation(),
				Bukkit.createBlockData(material));

		Vector aim = target.getLocation().clone().add(0, 1, 0).toVector().subtract(attacker.getLocation().toVector());
		aim.multiply(.8);
		aim.setY(aim.getY() * 1.2);
		fall.setVelocity(aim);
		fall.setHurtEntities(true);

		new BukkitRunnable() {
			long lastHit = 0;

			@Override
			public void run() {
				if (fall == null || !fall.isValid()) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() - lastHit < 100)
					return;
				double hRange = CE.BLOCKPHYSICTHROW_DAMAGE_HORIZONTALRANGE.doubleValue();
				double yRange = CE.BLOCKPHYSICTHROW_DAMAGE_VERTICALRANGE.doubleValue();
				for (Entity ent : fall.getLocation().getNearbyEntities(hRange, yRange, hRange)) {
					if (ent.equals(attacker))
						continue;
					if (!(ent instanceof LivingEntity))
						continue;

					lastHit = System.currentTimeMillis();
					LivingEntity e = (LivingEntity) ent;
					e.damage(CE.BLOCKPHYSICTHROW_DAMAGE_DAMAGE.doubleValue(), e);
					e.getWorld().playSound(e.getLocation(), CE.BLOCKPHYSICTHROW_HITSOUND_NAME.getValue(Sound.class),
							CE.BLOCKPHYSICTHROW_HITSOUND_VOLUME.floatValue(),
							CE.BLOCKPHYSICTHROW_HITSOUND_PITCH.floatValue());
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public AID getID() {
		return AID.BLOCK_PHYSIC_THROW;
	}

}
