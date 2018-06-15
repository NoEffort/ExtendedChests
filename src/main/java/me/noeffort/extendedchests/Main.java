package me.noeffort.extendedchests;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.noeffort.extendedchests.command.ExtendedChestCommand;
import me.noeffort.extendedchests.util.MessageUtil;
import me.noeffort.extendedchests.util.PlayerClick;

public class Main extends JavaPlugin {
	
	//Making instances of the Main class
	private static Main instance;
	
	//Enable this shit
	@Override
	public void onEnable() {
		getLogger().info("Plugin Enabled! " + getVersion());
		//Register commands function
		registerCommands();
		
		//Registering the listener
		getServer().getPluginManager().registerEvents(new PlayerClick(), this);
		
		//Making a file that doesn't work for some unknown reason
		File file = new File(getDataFolder() + File.separator + "range.yml");
		if(!file.exists()) {
			//Trying and failing
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Reload command that does nothing as of now
	public void load(FileConfiguration config) {
		File file = new File(getDataFolder() + File.separator + "range.yml");
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	//That same registerCommands function from earlier
	public void registerCommands() {
		//Lonely command
		this.getCommand("extendedchest").setExecutor(new ExtendedChestCommand(this));
	}
	
	//Getting the plugin's version from the plugin.yml
	public String getVersion() {
		PluginDescriptionFile pdf = this.getDescription();
		return pdf.getVersion();
	}
	
	//Reload command for nerds
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("reloadchest")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(sender.isOp()) {
					player.sendMessage(MessageUtil.translate(Messages.reload));
					
					this.reloadConfig();
					load(this.getConfig());
					
					return true;
				}
			} else {
				sender.sendMessage(MessageUtil.translate(Messages.permissions));
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	
	//Getter for the Main class' instance
	public static Main get() {
		return instance;
	}

}
