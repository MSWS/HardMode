package xyz.msws.hardmode.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.hardmode.data.CData;
import xyz.msws.hardmode.utils.Callback;
import xyz.msws.hardmode.utils.MSG;

@SerializableAs("CItem")
public class CItem implements ConfigurationSerializable, CData {
	private ItemStack item;

	private ItemMeta meta;

	private List<Callback<InventoryClickEvent>> onClicks = new ArrayList<>();
	private List<Callback<PlayerInteractEvent>> onInteracts = new ArrayList<>();

	private Map<String, Object> data = new HashMap<>();

	public CItem(ItemStack item) {
		this.item = item;
		this.meta = item.getItemMeta();
	}

	public CItem(Material mat) {
		this(new ItemStack(mat));
		this.meta = item.getItemMeta();
	}

	@SuppressWarnings("unchecked")
	public CItem(Map<String, Object> data) {
		this(Material.valueOf((String) data.get("Icon")));

		if (data.containsKey("Amount"))
			amount((int) data.get("Amount"));
		if (data.containsKey("Damage"))
			damage((int) data.get("Damage"));
		if (data.containsKey("Name"))
			name((String) data.get("Name"));
		if (data.containsKey("Lore"))
			lore((List<String>) data.get("Lore"));
		if (data.containsKey("ItemFlags")) {
			for (String flag : (List<String>) data.get("ItemFlags"))
				itemFlag(ItemFlag.valueOf(flag.toUpperCase()));
		}
		if (data.containsKey("Unbreakable"))
			unbreakable((boolean) data.get("Unbreakable"));
		if (data.containsKey("Enchantments")) {
			for (String e : (List<String>) data.get("Enchantments")) {
				Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(e.split(":")[0].toUpperCase()));
				enchantment(ench, Integer.parseInt(e.split(":")[1]));
			}
		}

		this.data = data;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = this.data;
		data.put("Icon", item.getType().toString());
		data.put("Damage", (meta instanceof Damageable) ? ((Damageable) meta).getDamage() : 0);
		data.put("Amount", item.getAmount());
		if (meta.hasDisplayName())
			data.put("Name", meta.getDisplayName());
		if (meta.hasLore())
			data.put("Lore", meta.getLore());
		data.put("ItemFlags",
				meta.getItemFlags().parallelStream().map(flag -> flag.toString()).collect(Collectors.toSet()));
		data.put("Unbreakable", meta.isUnbreakable());

		List<String> enchantments = new ArrayList<>();
		for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
			enchantments.add(entry.getKey().getName() + ":" + entry.getValue());

		data.put("Enchantments", enchantments);
		return data;
	}

	public CItem name(String name) {
		meta.setDisplayName(MSG.color("&r" + name));
		return this;
	}

	public CItem lore(List<String> lore) {
		meta.setLore(lore.stream().map(s -> MSG.color("&r" + s)).collect(Collectors.toList()));
		return this;
	}

	public CItem lore(String... lore) {
		lore(Arrays.asList(lore));
		return this;
	}

	public ItemStack build() {
		item.setItemMeta(meta);
		return item;
	}

	public CItem amount(int amo) {
		item.setAmount(amo);
		return this;
	}

	public CItem damage(int damage) {
		if (damage == 0)
			return this;
		if (!(meta instanceof Damageable))
			throw new UnsupportedOperationException(item.getType() + " is not Damageable");
		((Damageable) meta).setDamage(damage);
		return this;
	}

	public CItem itemFlag(ItemFlag... flag) {
		item.addItemFlags(flag);
		return this;
	}

	public CItem unbreakable(boolean unbreakable) {
		meta.setUnbreakable(unbreakable);
		return this;
	}

	public CItem enchantment(Enchantment ench, int level) {
		item.addUnsafeEnchantment(ench, level);
		return this;
	}

	public CItem addOnClick(Callback<InventoryClickEvent> click) {
		onClicks.add(click);
		return this;
	}

	public CItem setOnClick(Callback<InventoryClickEvent> click) {
		onClicks.clear();
		addOnClick(click);
		return this;
	}

	public List<Callback<InventoryClickEvent>> getOnClicks() {
		return onClicks;
	}

	public CItem addOnInteract(Callback<PlayerInteractEvent> event) {
		onInteracts.add(event);
		return this;
	}

	public CItem setOnInteract(Callback<PlayerInteractEvent> event) {
		addOnInteract(event);
		return this;
	}

	public List<Callback<PlayerInteractEvent>> getOnInteracts() {
		return onInteracts;
	}

	@Override
	public boolean hasData(String key) {
		return data.containsKey(key);
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}

	@Override
	public Object getData(String key) {
		return data.get(key);
	}

	@Override
	public boolean setData(String key, Object value) {
		data.put(key, value);
		return true;
	}

	@Override
	public boolean clearData() {
		return false;
	}

}
