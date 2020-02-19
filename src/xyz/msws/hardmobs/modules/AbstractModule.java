package xyz.msws.hardmobs.modules;

import xyz.msws.hardmobs.HardMobs;

public abstract class AbstractModule {
	protected String id;
	protected boolean enabled;
	protected HardMobs plugin;

	public AbstractModule(HardMobs plugin) {
		this.plugin = plugin;
	}

	public AbstractModule(String id, HardMobs plugin) {
		this.id = id;
		this.plugin = plugin;
	}

	public abstract void initialize();

	public abstract void disable();

	public abstract ModulePriority getPriority();

	public String getId() {
		return id;
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
