package xyz.msws.hardmode.modules.mobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import xyz.msws.hardmode.HardMode;

public abstract class BossSpawner implements Listener {

	protected HardMode plugin;
	private BossType type;

	public BossSpawner(HardMode plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public BossType getType() {
		return type;
	}
	
	public abstract void disable();
}
