package xyz.msws.hardmobs.modules.mobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import xyz.msws.hardmobs.HardMobs;

public abstract class BehaviorListener implements Listener {
	protected HardMobs plugin;

	public BehaviorListener(HardMobs plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) this.plugin);
	}

	public abstract void disable();

	public abstract EntityType getType();

}
