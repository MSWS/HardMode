package xyz.msws.hardmode;

import org.bukkit.configuration.file.FileConfiguration;

import xyz.msws.hardmode.data.Saveable;

public interface MPlugin {
	FileConfiguration getConfig();

	Saveable getData();
}
