package xyz.msws.hardmode.attacks;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;

public class FireballAttack implements Attack {

	private HardMode plugin;

	public FireballAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public AID getID() {
		return AID.FIREBALL;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		Preconditions.checkArgument(ProjectileSource.class.isAssignableFrom(attacker.getClass()),
				attacker.getType() + " is not a projectile source");
		ProjectileSource entity = (ProjectileSource) attacker;
		new BukkitRunnable() {
			int balls = 0;

			@Override
			public void run() {
				if (balls >= 5) {
					this.cancel();
					return;
				}

				Fireball ball = entity.launchProjectile(Fireball.class);

				double speed = 7, accuracy = .15;

				new BukkitRunnable() {
					@Override
					public void run() {
						if (target == null || !target.isValid() || !target.getWorld().equals(ball.getWorld())) {
							this.cancel();
							return;
						}

						Vector a = ball.getVelocity().add(target.getLocation().clone().toVector()
								.subtract(ball.getLocation().toVector()).normalize());

						ball.setVelocity(
								ball.getVelocity().clone().add(a.multiply(accuracy)).normalize().multiply(speed / 10));

					}
				}.runTaskTimer(plugin, 0, 1);

				ball.setYield(2);
				attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2, 1);
				balls++;
			}
		}.runTaskTimer(plugin, 0, 20);
	}

}
