package xyz.msws.hardmode.modules.mobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import xyz.msws.hardmode.HardMode;

public abstract class BehaviorListener implements Listener {
	protected HardMode plugin;
	protected MobSelector selector;

	public BehaviorListener(HardMode plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) this.plugin);
	}

	public MobSelector getSelector() {
		return selector;
	}

	public abstract void disable();

}
