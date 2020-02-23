package xyz.msws.hardmode.attacks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.CE;

public class FastArrowAttack implements Attack {

	@SuppressWarnings("unused")
	private HardMode plugin;

	public FastArrowAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		Vector aim = target.getLocation().toVector().subtract(attacker.getLocation().toVector());
		aim.multiply(CE.FASTARROWATTACK_MULTIPLIER.doubleValue());
		Preconditions.checkArgument(ProjectileSource.class.isAssignableFrom(attacker.getClass()),
				attacker.getType() + " is not a projectile source");
		ProjectileSource entity = (ProjectileSource) attacker;
		entity.launchProjectile(Arrow.class, aim);
		attacker.getWorld().playSound(attacker.getLocation(), CE.FASTARROWATTACK_SHOOTSOUND_NAME.getValue(Sound.class),
				CE.FASTARROWATTACK_SHOOTSOUND_VOLUME.floatValue(), CE.FASTARROWATTACK_SHOOTSOUND_PITCH.floatValue());

		Location current = attacker instanceof LivingEntity ? ((LivingEntity) entity).getEyeLocation()
				: attacker.getLocation();
		aim = target instanceof LivingEntity
				? ((LivingEntity) target).getEyeLocation().toVector().subtract(current.toVector()).normalize()
				: target.getLocation().toVector().subtract(current.toVector()).normalize();
		int particles = 0;
		double lastDist = 0, currentDist = 0;

		// If current dist > lastDist then we have gone PAST the target
		while (currentDist <= lastDist && particles < CE.FASTARROWATTACK_PARTICLES_MAXPARTICLES.intValue()) {
			lastDist = current.distanceSquared(target.getLocation());
			current.add(aim.clone().multiply(1.0 / CE.FASTARROWATTACK_PARTICLES_PATICLESPERBLOCK.doubleValue()));
			currentDist = current.distanceSquared(target.getLocation());

			current.getWorld().spawnParticle(CE.FASTARROWATTACK_PARTICLES_PARTICLETYPE.getValue(Particle.class),
					current, 0);
			if (current.getBlock().getType().isSolid()) {
				return;
			}
			particles++;
		}
	}

	@Override
	public AID getID() {
		return AID.FAST_ARROW;
	}

}
