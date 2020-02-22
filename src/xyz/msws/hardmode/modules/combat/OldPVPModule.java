package xyz.msws.hardmode.modules.combat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;

public class OldPVPModule extends AbstractModule implements Listener {

	public OldPVPModule(HardMode plugin) {
		super("OldPVP", plugin);
	}

	private SweepAttackPreventor sap;

	private double instantSpeed = 100;

	private final double defaultSpeed = 4;

	@Override
	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
		for (Player player : Bukkit.getOnlinePlayers())
			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(instantSpeed);

		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			return;

		sap = new SweepAttackPreventor(plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(instantSpeed);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		stack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		event.getItem().setItemStack(stack);
	}

	@EventHandler
	public void onItemCraft(CraftItemEvent event) {
		event.setCurrentItem(removeAttributes(event.getCurrentItem()));
	}

	@EventHandler
	public void onCreative(InventoryCreativeEvent event) {
		event.setCurrentItem(removeAttributes(event.getCurrentItem()));
		event.setCursor(removeAttributes(event.getCursor()));
	}

	public ItemStack removeAttributes(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return item;
		item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return item;
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOWEST;
	}

	@Override
	public void disable() {
		PlayerJoinEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);

		if (sap != null)
			sap.disable();

		for (Player player : Bukkit.getOnlinePlayers())
			player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(defaultSpeed);
	}

}
