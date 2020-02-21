package xyz.msws.hardmode.attacks;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;

public class ChargeAtAttack implements Attack {

	private HardMode plugin;

	public ChargeAtAttack(HardMode plugin) {
		this.plugin = plugin;
	}

	@Override
	public AID getID() {
		return AID.CHARGE_AT;
	}

	@Override
	public void attack(Entity attacker, Entity target) {
		Preconditions.checkArgument(attacker.getWorld().equals(target.getWorld()));

		Vector vel = target.getLocation().clone().toVector().subtract(attacker.getLocation().clone().toVector());

		vel.normalize();
		vel.multiply(target.getLocation().distance(attacker.getLocation()) / 2);
		vel.setY(vel.getY() + .1);

		attacker.setVelocity(vel);

		attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2, .1f);

		long launchTime = System.currentTimeMillis();

		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - launchTime > 3000) {
					this.cancel();
					return;
				}

				if (attacker == null || !attacker.isValid() || target == null || !target.isValid()) {
					this.cancel();
					return;
				}

				double radius = 2;
				if (attacker instanceof Slime) { // MagmaCubes covered too
					Slime slime = (Slime) attacker;
					radius = Math.max(slime.getSize() / 2.0, 1);
				}

				for (Entity ent : attacker.getLocation().getNearbyEntities(radius, radius, radius)) {
					if (ent.equals(attacker))
						continue;
					if (!ent.equals(target))
						continue;
					if (!(ent instanceof LivingEntity))
						continue;
					LivingEntity t = (LivingEntity) ent;
					t.damage(radius * 3, attacker);
					t.getWorld().playSound(t.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, .3f);
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 5, 1);
	}

}
