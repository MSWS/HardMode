package xyz.msws.hardmode.inventory;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.utils.Callback;

public class InteractionModule extends AbstractModule implements Listener {

	private Set<CItem> items = new HashSet<>();

	public InteractionModule(HardMode plugin) {
		super("InteractionModule", plugin);
	}

	@Override
	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {
		items = new HashSet<>();
		PlayerInteractEvent.getHandlerList().unregister(this);
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.HIGH;
	}

	public boolean addCItem(CItem item) {
		return items.add(item);
	}

	public boolean removeCItem(CItem item) {
		return items.remove(item);
	}

	public Set<CItem> getItems() {
		return items;
	}

	public void clearItems() {
		items.clear();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item == null || item.getType() == Material.AIR)
			return;
		for (CItem i : items) {
			if (i.build().isSimilar(item))
				for (Callback<PlayerInteractEvent> e : i.getOnInteracts()) {
					e.execute(event);
				}
		}
	}

}
