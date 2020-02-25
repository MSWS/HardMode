package xyz.msws.hardmode.boss;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.utils.MSG;
import xyz.msws.hardmode.world.Area;

public class GolemBoss extends HBoss {

	private IronGolem golem;
	private ThreadLocalRandom random;

	public GolemBoss(HardMode plugin, Entity entity) {
		super(plugin, entity);
		Preconditions.checkArgument(entity instanceof IronGolem);

		random = ThreadLocalRandom.current();

		this.golem = (IronGolem) entity;

		this.aoe = new Area(entity.getLocation().clone().subtract(20, 0, 20),
				entity.getLocation().clone().add(20, 2, 20));

		golem.getAttribute(Attribute.GENERIC_MAX_HEALTH)
				.setBaseValue(golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 3);

		new BukkitRunnable() {

			long nextGroundPound = System.currentTimeMillis() + random.nextLong(1000, 1001);

			@Override
			public void run() {
				if (entity == null || !entity.isValid()) {
					this.cancel();
					return;
				}

				List<LivingEntity> entities = aoe.getEntities(LivingEntity.class);
				entities.remove(entity);
				if (entities.isEmpty())
					return;
				entities.sort(distance(entity.getLocation()));
				LivingEntity target = entities.get(0);
				golem.setTarget(target);

				if (System.currentTimeMillis() > nextGroundPound) {
					plugin.getMobManager().getAttack(AID.GROUND_POUND).attack(entity, target);
					nextGroundPound = System.currentTimeMillis() + random.nextLong(2000, 2001);
					MSG.announce("Attacked");
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	Comparator<Entity> distance(Location loc) {
		return new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				return (o1.getLocation().distanceSquared(loc) < o2.getLocation().distanceSquared(loc)) ? -1 : 1;
			}
		};
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!event.getEntity().equals(this.getEntity()))
			return;
	}

}
