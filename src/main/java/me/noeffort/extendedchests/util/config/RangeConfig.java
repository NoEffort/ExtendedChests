package me.noeffort.extendedchests.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import me.noeffort.extendedchests.Main;

public class RangeConfig implements Listener {

	private static FileConfiguration config = null;
	private static File configFile = null;
	
	//Initiating the Main class
	Main plugin;
	
	//Constructor
	public RangeConfig(Main instance) {
		this.plugin = instance;
	}
	
	//Used to create and reload the config file
	public void reloadRangeConfig() {
		//Checking for file
		if(configFile == null) {
			//Making new config file
			configFile = new File(plugin.getDataFolder(), "range.yml");
			//Checking for existence
			if(!configFile.exists()) {
				//File not found
				plugin.saveResource("range.yml", false);
				Bukkit.getLogger().log(Level.INFO, "Range.yml config file generated!");
			} else {
				//File found
				saveRangeConfig();
				Bukkit.getLogger().log(Level.INFO, "Range.yml file found, no worries!");
			}
		}
		//Setting file
		config = YamlConfiguration.loadConfiguration(configFile);
		
		//Allowing inputs to file
		Reader defaultConfigStream = new InputStreamReader(plugin.getResource("range.yml"));
		if(defaultConfigStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
			defaultConfig.options().header("Update " + plugin.getUpdate() +"\nAuthors:\n  NoEffort - Project Creator\n  Air_neko - Lead Developer");
			defaultConfig.addDefault("useDefaults", true);
			defaultConfig.addDefault("baseRange", 5);
			defaultConfig.addDefault("rangeMultiplier.default", 1.0);
			defaultConfig.addDefault("rangeMultiplier.iron", 1.5);
			defaultConfig.addDefault("rangeMultiplier.gold", 2.0);
			defaultConfig.addDefault("rangeMultiplier.diamond", 2.5);
			defaultConfig.addDefault("rangeMultiplier.emerald", 3.0);
			defaultConfig.options().copyDefaults(true);
			defaultConfig.options().copyHeader(true);
			config.setDefaults(defaultConfig);
		}
	}
	
	//Used to get the custom config file
	public FileConfiguration getRangeConfig() {
		//Checking for file
		if(config == null) {
			reloadRangeConfig();
		}
		//Returning the file
		return config;
	}
	
	//Used to save the custom file
	public void saveRangeConfig() {
		//Checking for file
		if(config == null || configFile == null) {
			return;
		}
		//Saving file
		try {
			getRangeConfig().save(configFile);
		} catch (IOException e) {
			//Error boi
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
	
	//Saving the defaults of the file, reverting to base settings
	public void saveDefaultRangeConfig() {
		//Checking for file
		if(configFile == null) {
			configFile = new File(plugin.getDataFolder(), "range.yml");
			plugin.saveResource("range.yml", false);
			Bukkit.getLogger().log(Level.INFO, "Range.yml config file generated!");
		}
		//Saving defaults
		if(configFile.exists()) {
			plugin.saveResource("range.yml", false);
		}
	}
	
	public File getRangeConfigFile() {
		return configFile;
	}
	
}
