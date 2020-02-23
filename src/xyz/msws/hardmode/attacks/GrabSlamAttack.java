package xyz.msws.hardmode.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.CE;

public class GrabSlamAttack implements Attack {

	private HardMode plugin;

	public GrabSlamAttack(HardMode plugin) {
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
					if (!attacker.getPassengers().contains(target)) {
						this.cancel();
						return;
					}
					Vector up = attacker.getVelocity().clone();
					up.setY(CE.GRABSLAMATTACK_UPVELOCITY.doubleValue());
					attacker.setVelocity(up);
				}

				if (target.getLocation().getY() > lastY
						|| System.currentTimeMillis() - grabTime < CE.GRABSLAMATTACK_GRABTIME.longValue()) {
					// We are still climbing up
					lastY = target.getLocation().getY();
					return;
				}
				attacker.removePassenger(target);
				target.setVelocity(new Vector(0, CE.GRABSLAMATTACK_THROWVELOCITY.doubleValue(), 0));
				attacker.getWorld().playSound(attacker.getLocation(),
						CE.GRABSLAMATTACK_THROWSOUND_NAME.getValue(Sound.class),
						CE.GRABSLAMATTACK_THROWSOUND_VOLUME.floatValue(),
						CE.GRABSLAMATTACK_THROWSOUND_PITCH.floatValue());

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
		return AID.GRAB_SLAM;
	}

}
