package xyz.msws.hardmode.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;

public class GrabTeleportAttack implements Attack {

	private HardMode plugin;

	public GrabTeleportAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		if (attacker.equals(target))
			return;
		if (!attacker.addPassenger(target))
			return;

		new BukkitRunnable() {
			long grabTime = System.currentTimeMillis();
			boolean teleported = false;
			double lastY = 0;

			@Override
			public void run() {
				if (attacker == null || !attacker.isValid() || target == null || !target.isValid()) {
					this.cancel();
					return;
				}

				if (System.currentTimeMillis() - grabTime > 400 && !teleported) {
					teleported = true;
					Vector up = attacker.getVelocity().clone();
					up.setY(2.5);
					attacker.setVelocity(up);
				}

				if (attacker.getLocation().getY() > lastY || System.currentTimeMillis() - grabTime < 500) {
					// We are still climbing up
					lastY = attacker.getLocation().getY();
					return;
				}
				attacker.removePassenger(target);
				target.setVelocity(new Vector(0, -1.8, 0));
				attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2, 1);

				Location tp = attacker.getLocation().clone();
				tp.setY(tp.getWorld().getHighestBlockYAt(tp));

				attacker.teleport(tp);

				this.cancel();
				return;
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public AID getID() {
		return AID.GRAB_TELEPORT;
	}

}
