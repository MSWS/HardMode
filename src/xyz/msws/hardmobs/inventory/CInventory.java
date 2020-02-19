package xyz.msws.hardmobs.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.hardmobs.utils.Callback;
import xyz.msws.hardmobs.utils.MSG;

/**
 * CInventory (Custom Inventory) Is a custom inventory object to hold
 * {@link CItem} and run their events, if no documentation is provided, the
 * method is (or reflects) the default implementation that bukkit provides
 * 
 * @author imodm
 *
 */
public class CInventory implements Listener {
	private Map<Integer, CItem> items = new HashMap<Integer, CItem>();

	private Callback<InventoryCloseEvent> onClose;

	private List<Callback<InventoryClickEvent>> onClick = new ArrayList<Callback<InventoryClickEvent>>();

	private Inventory inv;

	/**
	 * Creates a new inventory and registers the object as a listener
	 * 
	 * @param size
	 * @param title
	 */
	public CInventory(int size, String title, JavaPlugin plugin) {
		this.inv = Bukkit.createInventory(null, size, MSG.color(title));

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Add a Callback to when the inventory is closed
	 * 
	 * @param event
	 * @return
	 */
	public CInventory addClose(Callback<InventoryCloseEvent> event) {
		this.onClose = event;
		return this;
	}

	public Inventory getInventory() {
		return inv;
	}

	public void setItem(int slot, CItem item) {
		items.put(slot, item);
		refresh();
	}

	public boolean add(CItem item) {
		int slot = firstEmpty();
		if (slot == -1)
			return false;
		setItem(slot, item);
		return true;
	}

	public int firstEmpty() {
		for (int i = 0; i < inv.getSize(); i++) {
			if (items.get(i) == null || items.get(i).build().getType() == Material.AIR)
				return i;
		}
		return -1;
	}

	public boolean contains(Material type) {
		for (CItem item : items.values()) {
			if (item.build().getType() == type)
				return true;
		}
		return false;
	}

	public boolean contains(ItemStack item) {
		for (CItem i : items.values())
			if (i.build().equals(item))
				return true;
		return false;
	}

	public boolean contains(Material type, int amo) {
		int a = 0;
		for (CItem item : items.values()) {
			if (item.build().getType() == type)
				amo += item.build().getAmount();
		}

		return a >= amo;
	}

	public boolean contains(ItemStack item, int amo) {
		return contains(item.getType(), amo * item.getAmount());
	}

	public void addClick(Callback<InventoryClickEvent> event) {
		onClick.add(event);
	}

	public List<Callback<InventoryClickEvent>> getClicks() {
		return onClick;
	}

	/**
	 * Fills all non-filled slots with the CItem
	 * 
	 * @param item
	 */
	public void fill(CItem item) {
		for (int i = 0; i < inv.getSize(); i++) {
			if (items.get(i) == null || items.get(i).build().getType() == Material.AIR)
				continue;
			setItem(i, item);
		}
	}

	public void clearInventory() {
		items.clear();
		refresh();
	}

	public CItem getItem(int slot) {
		return items.get(slot);
	}

	/**
	 * Set all CItems into their proper slots
	 */
	public void refresh() {
		for (Entry<Integer, CItem> entry : items.entrySet()) {
			inv.setItem(entry.getKey(), entry.getValue().build());
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!event.getInventory().equals(getInventory()))
			return;
		onClick.forEach(c -> c.execute(event));
		if (!items.containsKey(event.getRawSlot()))
			return;
		for (Callback<InventoryClickEvent> c : items.get(event.getRawSlot()).getOnClicks())
			c.execute(event);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!event.getInventory().equals(getInventory()))
			return;
		if (onClose != null)
			onClose.execute(event);
	}

}
