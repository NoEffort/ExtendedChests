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
import me.noeffort.extendedchests.util.config.RangeConfig;

public class ExtendedChestCommand implements CommandExecutor {
	
	//Getting the Main plugin (no uses)
	Main plugin = Main.get();
	
	//Construct your constructors
	public ExtendedChestCommand() {}
	
	public String getAuthors() {
		PluginDescriptionFile pdf = JavaPlugin.getPlugin(Main.class).getDescription();
		return pdf.getAuthors().toString();
	}
	
	//Command of super gay (does nothing cool)
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Not done yet
		if(sender instanceof Player) {
			if(args.length <= 0) {
				sender.sendMessage(MessageUtil.translate(Messages.wip));
				sender.sendMessage(MessageUtil.translate(Messages.prefix + "&fAuthors: " + getAuthors()));
				
				//Gets config
				PlayerConfig config = plugin.getPlayerConfig();
				
				//Edits config
				config.getConfig().set("chests." + sender.getName() + ".name", sender.getName());
				config.getConfig().set("chests." + sender.getName() + ".uuid", ((Player) sender).getUniqueId().toString());
				//Assigning special code
				if(config.getConfig().getString("chests." + sender.getName() + ".code") != null) {
				} else {
					config.getConfig().set("chests." + sender.getName() + ".code", plugin.generateItemCode());
				}
				//Save and reload
				config.saveConfig();
				config.reloadConfig();
				
				//Giving item
				((Player) sender).getInventory().addItem(plugin.createItem((Player) sender));
				return true;
			} else if(args.length == 1) {
				if(args[0].equals("reload")) {
					if(sender.isOp()) {
						PlayerConfig config = plugin.getPlayerConfig();
						RangeConfig range = plugin.getRangeConfig();
						config.reloadConfig();
						range.reloadConfig();
						sender.sendMessage(MessageUtil.translate(Messages.reload));
						return true;
					} else {
						sender.sendMessage(MessageUtil.translate(Messages.permissions));
						return true;
					}
				} else {
					sender.sendMessage(MessageUtil.translate(Messages.invalid));
					return true;
				}
			}
		}
		//Not done yet
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(MessageUtil.translate(Messages.wip));
			return true;
		}
		return true;
	}
}
