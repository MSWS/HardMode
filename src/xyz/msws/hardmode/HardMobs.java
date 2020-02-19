package xyz.msws.hardmode;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.hardmode.data.CSQL;
import xyz.msws.hardmode.data.CYAML;
import xyz.msws.hardmode.data.Saveable;
import xyz.msws.hardmode.data.YMLSQL;
import xyz.msws.hardmode.inventory.CItem;
import xyz.msws.hardmode.inventory.InteractionModule;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.modules.commands.CommandModule;
import xyz.msws.hardmode.modules.data.ConnectionManager;
import xyz.msws.hardmode.modules.data.DataManager;
import xyz.msws.hardmode.modules.debug.DebugModule;
import xyz.msws.hardmode.modules.mobs.MobManager;

public class HardMobs extends JavaPlugin implements MPlugin {
	private Set<AbstractModule> modules = new HashSet<>();

	private Saveable data;

	@Override
	public void onEnable() {
		saveResource("config.yml", false);

		if (getConfig().getBoolean("SQL.Enabled")) {
			modules.add(new ConnectionManager(this));
			modules.add(new DataManager(this, getModule(ConnectionManager.class)));

			data = new CSQL("data", YMLSQL.class, this);
		} else {
			data = new CYAML(new File(getDataFolder(), "data.yml"));
		}

		data.loadData();

		modules.add(new CommandModule(this));
		modules.add(new InteractionModule(this));
		modules.add(new MobManager(this));

		modules.add(new DebugModule(this));

		ConfigurationSerialization.registerClass(CItem.class, "CItem");

		enableModules();
	}

	public Saveable getData() {
		return this.data;
	}

	@Override
	public void onDisable() {
		data.saveData();

		disableModules();
	}

	private void enableModules() {
		for (ModulePriority priority : ModulePriority.values()) {
			enableModules(priority);
		}
	}

	private void enableModules(ModulePriority priority) {
		for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
				.collect(Collectors.toSet())) {
			mod.initialize();
			mod.setEnabled(true);
		}
	}

	private void disableModules() {
		List<ModulePriority> reverse = Arrays.asList(ModulePriority.values());
		Collections.reverse(reverse);
		for (ModulePriority priority : reverse) {
			disableModules(priority);
		}
	}

	private void disableModules(ModulePriority priority) {
		for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
				.collect(Collectors.toSet())) {
			mod.disable();
			mod.setEnabled(false);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractModule> T getModule(Class<T> mod) {
		for (AbstractModule m : getModules())
			if (mod.isAssignableFrom(m.getClass()))
				return (T) m;
		return null;
	}

	public AbstractModule getModule(String string) {
		for (AbstractModule mod : getModules())
			if (mod.getId().equals(string))
				return mod;
		return null;
	}

	public Set<AbstractModule> getModules() {
		return modules;
	}

}
