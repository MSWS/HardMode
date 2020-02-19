package xyz.msws.hardmode.modules.mobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import xyz.msws.hardmode.HardMobs;

public abstract class BehaviorListener implements Listener {
	protected HardMobs plugin;
	protected MobSelector selector;

	public BehaviorListener(HardMobs plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) this.plugin);
	}

	public MobSelector getSelector() {
		return selector;
	}

	public abstract void disable();

}
