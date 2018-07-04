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

public class PlayerConfig implements Listener {

	private static FileConfiguration config = null;
	private static File configFile = null;
	
	//Initiating the Main class
	Main plugin;
	
	//Constructor
	public PlayerConfig(Main instance) {
		this.plugin = instance;
	}
	
	//Used to create and reload the config file
	public void reloadPlayerConfig() {
		//Checking for file
		if(configFile == null) {
			//Making new config file
			configFile = new File(plugin.getDataFolder(), "players.yml");
			//Checking for existence
			if(!configFile.exists()) {
				//File not found
				plugin.saveResource("players.yml", false);
				Bukkit.getLogger().log(Level.INFO, "Players.yml config file generated!");
			} else {
				//File found
				savePlayerConfig();
				Bukkit.getLogger().log(Level.INFO, "Players.yml file found, no worries!");
			}
		}
		//Setting file
		config = YamlConfiguration.loadConfiguration(configFile);
		
		//Allowing inputs to file
		Reader defaultConfigStream = new InputStreamReader(plugin.getResource("players.yml"));
		if(defaultConfigStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
			defaultConfig.options().header("Logging Chest Data - Beta Phase " + plugin.getUpdate());
			defaultConfig.options().copyDefaults(true);
			defaultConfig.options().copyHeader(true);
			config.setDefaults(defaultConfig);
		}
	}
	
	//Used to get the custom config file
	public FileConfiguration getPlayerConfig() {
		//Checking for file
		if(config == null) {
			reloadPlayerConfig();
		}
		//Returning the file
		return config;
	}
	
	//Used to save the custom file
	public void savePlayerConfig() {
		//Checking for file
		if(config == null || configFile == null) {
			return;
		}
		//Saving file
		try {
			getPlayerConfig().save(configFile);
		} catch (IOException e) {
			//Error boi
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
	
	//Saving the defaults of the file, reverting to base settings
	public void saveDefaultPlayerConfig() {
		//Checking for file
		if(configFile == null) {
			configFile = new File(plugin.getDataFolder(), "players.yml");
			plugin.saveResource("players.yml", false);
			Bukkit.getLogger().log(Level.INFO, "Players.yml config file generated!");
		}
		//Saving defaults
		if(configFile.exists()) {
			plugin.saveResource("players.yml", false);
		}
	}
	
	public File getPlayerConfigFile() {
		return configFile;
	}
	
}
