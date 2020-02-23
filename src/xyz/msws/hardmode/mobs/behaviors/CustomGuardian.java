package xyz.msws.hardmode.mobs.behaviors;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.packets.PlayerElderGuardianEffectEvent;

public class CustomGuardian extends BehaviorListener {

	public CustomGuardian(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {

			@Override
			public boolean matches(Entity ent) {
				return (ent instanceof Guardian);
			}
		};

		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			return;

		plugin.enableModule(new GuardianStateListener(plugin));
	}

	@EventHandler
	public void onGuardianTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		if (event.getTarget() == null)
			return;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getDamager()))
			return;
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Guardian guard = (Guardian) event.getEntity();
		guard.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.53);
	}

	@EventHandler
	public void onGuardian(PlayerElderGuardianEffectEvent event) {
		Player player = event.getPlayer();
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 50, 0));
	}

	@Override
	public void disable() {

	}

}
