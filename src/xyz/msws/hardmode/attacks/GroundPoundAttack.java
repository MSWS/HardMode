package xyz.msws.hardmode.attacks;

import org.bukkit.entity.Entity;
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
		vec.multiply(3);
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
				this.cancel();

			}
		}.runTaskTimer(plugin, 0, 1);
	}

}
