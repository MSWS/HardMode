package xyz.msws.hardmode.modules.combat;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class OldPVPModule extends AbstractModule implements Listener {

	public OldPVPModule(HardMode plugin) {
		super(plugin);
	}

	private SweepAttackPreventor sap;

	@Override
	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
		for (Player player : Bukkit.getOnlinePlayers())
			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);

		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			return;

		sap = new SweepAttackPreventor(plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK)
			return;
		event.setCancelled(true);
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOWEST;
	}

	@Override
	public void disable() {
		PlayerJoinEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);

		sap.disable();

		for (Player player : Bukkit.getOnlinePlayers())
			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
	}

}
