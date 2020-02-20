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

public class FastArrowAttack implements Attack {

	private HardMode plugin;

	public FastArrowAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		Vector aim = target.getLocation().toVector().subtract(attacker.getLocation().toVector());
		aim.normalize().multiply(3);
		Preconditions.checkArgument(ProjectileSource.class.isAssignableFrom(attacker.getClass()),
				attacker.getType() + " is not a projectile source");
		ProjectileSource entity = (ProjectileSource) attacker;
		entity.launchProjectile(Arrow.class, aim);
		attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);

		Location current = attacker instanceof LivingEntity ? ((LivingEntity) entity).getEyeLocation()
				: attacker.getLocation();
		aim = target instanceof LivingEntity ? ((LivingEntity) target).getEyeLocation().toVector()
				: target.getLocation().toVector().subtract(current.toVector()).normalize();
		int particles = 0;
		double lastDist = Double.MAX_VALUE, currentDist = 0;

		// If current dist > lastDist then we have gone PAST the target
		while (currentDist < lastDist && particles < 200) {
			current.getWorld().spawnParticle(Particle.FLAME, current, 0);
			if (current.getBlock().getType().isSolid())
				return;
			if (currentDist != 0)
				lastDist = currentDist;
			current.add(aim.clone().multiply(1.0 / 2.0));
			currentDist = current.distanceSquared(target.getLocation());
			particles++;
		}
		if (particles >= 200)
			plugin.log("FastArrowAttack reached max particle amount (" + particles + ")");
	}

}
