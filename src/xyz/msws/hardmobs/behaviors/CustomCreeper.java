package xyz.msws.hardmobs.behaviors;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmobs.HardMobs;
import xyz.msws.hardmobs.modules.mobs.BehaviorListener;

public class CustomCreeper extends BehaviorListener {

	public CustomCreeper(HardMobs plugin) {
		super(plugin);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != this.getType())
			return;

		Creeper creeper = (Creeper) entity;
		creeper.setExplosionRadius(getExplosionPower(creeper));
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != this.getType())
			return;

		Creeper creeper = (Creeper) entity;
		creeper.setExplosionRadius(getExplosionPower(creeper));
		creeper.explode();

		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (int i = 0; i < 3; i++) {
			if (random.nextDouble() < .4)
				continue;
			event.getDrops().add(new ItemStack(Material.GOLD_NUGGET));
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntityType() != this.getType())
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
	public EntityType getType() {
		return EntityType.CREEPER;
	}

	@Override
	public void disable() {
		EntityExplodeEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);
		EntitySpawnEvent.getHandlerList().unregister(this);
	}

}
