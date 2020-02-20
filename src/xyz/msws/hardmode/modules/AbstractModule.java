package xyz.msws.hardmode.modules;

import xyz.msws.hardmode.HardMode;

public abstract class AbstractModule {
	protected String id;
	protected boolean enabled;
	protected HardMode plugin;

	public AbstractModule(HardMode plugin) {
		this.plugin = plugin;
	}

	public AbstractModule(String id, HardMode plugin) {
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
