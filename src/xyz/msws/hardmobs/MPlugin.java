package xyz.msws.hardmobs;

import org.bukkit.configuration.file.FileConfiguration;

import xyz.msws.hardmobs.data.Saveable;

public interface MPlugin {
	FileConfiguration getConfig();

	Saveable getData();
}
