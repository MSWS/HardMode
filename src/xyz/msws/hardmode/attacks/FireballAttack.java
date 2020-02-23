package xyz.msws.hardmode.attacks;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.CE;

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
				if (balls >= CE.FIREBALLATTACK_FIREBALLS.intValue()) {
					this.cancel();
					return;
				}

				Fireball ball = entity.launchProjectile(Fireball.class);

				double speed = CE.FIREBALLATTACK_SPEED.doubleValue(),
						accuracy = CE.FIREBALLATTACK_ACCURACY.doubleValue();

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

				ball.setYield(CE.FIREBALLATTACK_POWER.floatValue());
				attacker.getWorld().playSound(attacker.getLocation(),
						CE.FIREBALLATTACK_SHOOTSOUND_NAME.getValue(Sound.class),
						CE.FIREBALLATTACK_SHOOTSOUND_VOLUME.floatValue(),
						CE.FIREBALLATTACK_SHOOTSOUND_PITCH.floatValue());
				balls++;
			}
		}.runTaskTimer(plugin, 0, 20);
	}

}
