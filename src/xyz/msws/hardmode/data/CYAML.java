package xyz.msws.hardmode.data;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class CYAML implements Saveable {

	private File file;

	private YamlConfiguration config;

	public CYAML(File file) {
		this.file = file;

		loadData();
	}

	@Override
	public boolean loadData() {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		this.config = YamlConfiguration.loadConfiguration(file);
		return this.config != null;
	}

	@Override
	public boolean saveData() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean hasData(String key) {
		return config.contains(key);
	}

	@Override
	public Object getData(String key) {
		return config.get(key);
	}

	@Override
	public boolean setData(String key, Object value) {
		config.set(key, value);
		return true;
	}

	@Override
	public boolean clearData() {
		config = new YamlConfiguration();
		return true;
	}

	@Override
	public Map<String, Object> getData() {
		return config.getValues(true);
	}
}
