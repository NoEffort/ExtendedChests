package me.noeffort.extendedchests.util.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.noeffort.extendedchests.Main;

public class RangeConfig {

	Main plugin = Main.get();
	
	ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
	
	private File file = null;
	private FileConfiguration config = null;
	
	public FileConfiguration getConfig() {
		if(config == null) {
			reloadConfig();
		}
		return config;
	}
	
	public File getFile() {
		return file;
	}
	
	public void createFile() {
		if(file == null) {
			if(!plugin.getDataFolder().exists()) {
				plugin.getDataFolder().mkdir();
				sender.sendMessage(ChatColor.GREEN + "Plugin folder created!");
			}
			
			file = new File(plugin.getDataFolder(), "range.yml");
			
			if(!file.exists()) {
				try {
					file.createNewFile();
					sender.sendMessage(ChatColor.GREEN + "Range file created!");
				} catch (IOException e) {
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "Error creating Range file!");
				}
			}
		}
		
		config = YamlConfiguration.loadConfiguration(file);
		
		loadDefaultConfiguration();
	}
	
	private void loadDefaultConfiguration() {
		config.options().copyDefaults(true);
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void saveConfig() {
		if(config == null || file == null) {
			return;
		}
		
		try {
			config.save(file);
		} catch(IOException ignored) {}
	}
	
	public void saveDefaultConfig() {
		if(file == null) {
			file = new File(plugin.getDataFolder(), "range.yml");
			plugin.saveResource("range.yml", false);
		}
		
		if(file.exists()) {
			plugin.saveResource("range.yml", false);
		}
	}
}
