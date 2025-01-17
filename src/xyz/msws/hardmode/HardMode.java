package xyz.msws.hardmode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import xyz.msws.hardmode.modules.combat.OldPVPModule;
import xyz.msws.hardmode.modules.commands.CommandModule;
import xyz.msws.hardmode.modules.data.ConnectionManager;
import xyz.msws.hardmode.modules.data.DataManager;
import xyz.msws.hardmode.modules.debug.DebugModule;
import xyz.msws.hardmode.modules.mobs.BossManager;
import xyz.msws.hardmode.modules.mobs.MobManager;
import xyz.msws.hardmode.modules.movement.PlayerMovementModule;
import xyz.msws.hardmode.utils.CE;
import xyz.msws.hardmode.utils.MSG;

public class HardMode extends JavaPlugin {
	private Set<AbstractModule> modules;

	private Saveable data;

	private MobManager mobManager;

	private File logFile, configFile;

	private FileConfiguration config;

	private static HardMode plugin;

	@Override
	public void onEnable() {
		plugin = this;
		modules = new HashSet<>();
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists())
			saveResource("config.yml", false);

		config = YamlConfiguration.loadConfiguration(configFile);

		boolean save = false;

		for (CE ce : CE.values()) {
			if (!config.contains(ce.getPath())) {
				MSG.log("Config is missing path " + MSG.FORMAT_INFO + ce.getPath().trim() + MSG.DEFAULT
						+ ", setting to default.");
				config.set(ce.getPath(), ce.getValue());
				save = true;
			}
		}

		if (save) {
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		CE.updateValues(config);

		new File(getDataFolder(), "logs").mkdirs();
		logFile = new File(getDataFolder(), "logs/" + System.currentTimeMillis() + ".log");

		try {
			logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		mobManager = new MobManager(this);
		modules.add(mobManager);

		modules.add(new BossManager(this));

		modules.add(new OldPVPModule(this));
		modules.add(new PlayerMovementModule(this));
		modules.add(new DebugModule(this));

		ConfigurationSerialization.registerClass(CItem.class, "CItem");

		enableModules();
	}

	public Saveable getData() {
		return this.data;
	}

	public File getLogFile() {
		return logFile;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public void onDisable() {
		data.saveData();

		disableModules();
	}

	public boolean reload() {
		try {
			this.onDisable();
			this.onEnable();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
		CE.updateValues(config);
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

	public void enableModule(AbstractModule module) {
		module.initialize();
		module.setEnabled(true);
		modules.add(module);
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

	public MobManager getMobManager() {
		return this.mobManager;
	}

	public void log(String line) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(line + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HardMode getPlugin() {
		return plugin;
	}
}
