package xyz.msws.hardmode.attacks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;

public class TNTAttack implements Attack {

	private HardMode plugin;

	public TNTAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		TNTPrimed tnt = (TNTPrimed) attacker.getWorld().spawnEntity(attacker.getLocation(), EntityType.PRIMED_TNT);
		tnt.setVelocity(new Vector(0, 2, 0));
		tnt.setFuseTicks(72);
		tnt.setYield(5);

		new BukkitRunnable() {
			long start = System.currentTimeMillis();
			double radius = 1.5;

			@Override
			public void run() {
				if (tnt == null || !tnt.isValid()) {
					this.cancel();
					return;
				}
				for (double i = 0; i < Math.PI * 2.0; i += .5) {
					long time = System.currentTimeMillis() - start;

					Location partLoc = tnt.getLocation().clone();
					partLoc.add(Math.sin(time + i) * radius, Math.cos(time * 5) * radius, Math.cos(time + i) * radius);

					double velX = -Math.sin(time + i) / 5;
					double velZ = -Math.cos(time + i) / 5;

					tnt.getWorld().spawnParticle(Particle.FLAME, partLoc, 0, velX, 0, velZ);
				}
			}
		}.runTaskTimer(plugin, 1, 1);

	}
}
