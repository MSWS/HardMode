package xyz.msws.hardmode.attacks;

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
		if (!attacker.addPassenger(target))
			return;

		new BukkitRunnable() {
			long grabTime = System.currentTimeMillis();
			boolean teleported = false;

			@Override
			public void run() {
				if (System.currentTimeMillis() - grabTime > 1000 && !teleported) {
					teleported = true;
					attacker.teleport(attacker.getLocation().clone().add(0, 10, 0));
				}
				if (System.currentTimeMillis() - grabTime < 2000)
					return;
				attacker.removePassenger(target);
				target.setVelocity(new Vector(0, .8, 0));
				attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2, 1);
			}
		}.runTaskTimer(plugin, 0, 1);
	}

}
