package me.noeffort.extendedchests.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.noeffort.extendedchests.Main;
import me.noeffort.extendedchests.Messages;
import me.noeffort.extendedchests.util.MessageUtil;

public class ExtendedChestCommand implements CommandExecutor {
	
	//Getting the Main plugin (no uses)
	private final Main plugin;
	
	//Construct your constructors
	public ExtendedChestCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	//Command of super gay (does nothing cool)
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			sender.sendMessage(MessageUtil.translate(Messages.wip));
			return true;
		}
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(MessageUtil.translate(Messages.wip));
			return true;
		}
		return true;
	}
}
