package xyz.msws.hardmode.mobs.behaviors;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class CustomCreeper extends BehaviorListener {
	public CustomCreeper(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.CREEPER);
			}
		};
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if (!getSelector().matches(entity))
			return;

		Creeper creeper = (Creeper) entity;
		creeper.setExplosionRadius(getExplosionPower(creeper));
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (!getSelector().matches(entity))
			return;

		Creeper creeper = (Creeper) entity;
		creeper.setExplosionRadius(getExplosionPower(creeper));
		creeper.explode();

		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextDouble() > .05 && !creeper.isPowered())
			return;
		plugin.getMobManager().getAttack("tnt").attack(creeper, creeper);
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (!getSelector().matches(event.getEntity()))
			return;

		Creeper creeper = (Creeper) event.getEntity();
		creeper.setMaxFuseTicks(15); // 30 -> 15
	}

	public int getExplosionPower(LivingEntity creeper) {
		int radius = 5;
		float health = (float) (creeper.getHealth() / creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		if (health < .1) {
			radius *= .1;
		} else if (health < .2) {
			radius *= .3;
		} else if (health < .5) {
			radius *= .5;
		} else if (health > .75) {
			radius *= 1.5;
		}
		return radius;
	}

	@Override
	public void disable() {
		EntityExplodeEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);
		EntitySpawnEvent.getHandlerList().unregister(this);
	}
}
