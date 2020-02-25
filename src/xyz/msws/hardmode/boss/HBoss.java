package xyz.msws.hardmode.boss;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.Attack;
import xyz.msws.hardmode.world.Area;

public abstract class HBoss implements Listener {
	protected Entity entity;

	protected List<Attack> attacks;

	protected Map<List<ItemStack>, Double> loot;

	private Location spawnLocation;

	protected Area aoe;

	protected HardMode plugin;

	public HBoss(HardMode plugin, Entity entity) {
		this.entity = entity;
		this.plugin = plugin;
		this.spawnLocation = entity.getLocation();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public Entity getEntity() {
		return entity;
	}

	/**
	 * @return the spawnLocation
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * @return the attacks
	 */
	public List<Attack> getAttacks() {
		return attacks;
	}

	/**
	 * @return the Area of Effect
	 */
	public Area getAoe() {
		return aoe;
	}

	public Map<List<ItemStack>, Double> getLoot() {
		return loot;
	}

}
