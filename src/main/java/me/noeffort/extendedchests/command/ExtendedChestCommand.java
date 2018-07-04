package me.noeffort.extendedchests.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.noeffort.extendedchests.Main;
import me.noeffort.extendedchests.Messages;
import me.noeffort.extendedchests.util.MessageUtil;
import me.noeffort.extendedchests.util.config.PlayerConfig;

public class ExtendedChestCommand implements CommandExecutor {
	
	//Getting the Main plugin (no uses)
	Main plugin;
	
	//Construct your constructors
	public ExtendedChestCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	public String getAuthors() {
		PluginDescriptionFile pdf = JavaPlugin.getPlugin(Main.class).getDescription();
		return pdf.getAuthors().toString();
	}
	
	//Command of super gay (does nothing cool)
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Not done yet
		if(sender instanceof Player) {
			sender.sendMessage(MessageUtil.translate(Messages.wip));
			sender.sendMessage(MessageUtil.translate(Messages.prefix + "&fAuthors: " + getAuthors()));
			
			//Gets config
			PlayerConfig playerConfig = new PlayerConfig(plugin);
			
			//Edits config
			playerConfig.getPlayerConfigFile();
			playerConfig.getPlayerConfig().set("chests." + sender.getName() + ".name", sender.getName());
			playerConfig.getPlayerConfig().set("chests." + sender.getName() + ".uuid", ((Player) sender).getUniqueId().toString());
			//Assigning special code
			if(playerConfig.getPlayerConfig().getString("chests." + sender.getName() + ".code") != null) {
			} else {
				playerConfig.getPlayerConfig().set("chests." + sender.getName() + ".code", Main.generateItemCode());
			}
			//Save and reload
			playerConfig.savePlayerConfig();
			playerConfig.reloadPlayerConfig();
			
			//Giving item
			((Player) sender).getInventory().addItem(Main.createItem((Player) sender));
			
			return true;
		}
		//Not done yet
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(MessageUtil.translate(Messages.wip));
			return true;
		}
		return true;
	}
}
