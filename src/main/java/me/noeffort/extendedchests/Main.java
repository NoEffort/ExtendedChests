package me.noeffort.extendedchests;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.noeffort.extendedchests.command.ExtendedChestCommand;
import me.noeffort.extendedchests.util.MessageUtil;
import me.noeffort.extendedchests.util.PlayerClick;
import me.noeffort.extendedchests.util.config.PlayerConfig;
import me.noeffort.extendedchests.util.config.RangeConfig;

/*
 * Developed by Air_neko, NoEffort
 * Last release date: 23.06.2018
 * Current file download: https://mega.nz/#!Hag3nAKC!6XoGB8SsWNXwvfFNgKUzx68qwlgdp2yDb2GCPzLfUYU
 */
public class Main extends JavaPlugin {
	
	//Making instances of the Main class
	private static Main instance;
	
	/*
	 * Calling the RangeConfig and PlayerConfig classes
	 * They must be static for other sections of code to work
	 */
	private static RangeConfig rangeConfig;
	private static PlayerConfig playerConfig;
	
	//Enable this shit
	@Override
	public void onEnable() {
		instance = this;
		getLogger().info("Plugin Enabled! " + getVersion());
		//Register commands function
		registerCommands();
		
		/*
		 * Making them instances of this plugin, in accordance with their constructors
		 */
		rangeConfig = new RangeConfig(instance);
		playerConfig = new PlayerConfig(instance);
		
		//Registering the listener
		registerListeners();
		
		//Gets each config
		rangeConfig.getRangeConfigFile();
		playerConfig.getPlayerConfigFile();
		
		//Creates / Reloads each config
		rangeConfig.reloadRangeConfig();
		playerConfig.reloadPlayerConfig();
	}
	
	//Getting the current program update
	public String getUpdate() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm" + "aa");
		String currentTime = dateFormat.format(date);
		return currentTime;
	}
	
	//That same registerCommands function from earlier
	private void registerCommands() {
		//Lonely command
		this.getCommand("extendedchest").setExecutor(new ExtendedChestCommand(this));
	}
	
	//Getting and loading all listeners in the program
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerClick(this), this);
		getServer().getPluginManager().registerEvents(new RangeConfig(this), this);
		getServer().getPluginManager().registerEvents(new PlayerConfig(this), this);
	}
	
	//Getting the plugin's version from the plugin.yml
	private String getVersion() {
		PluginDescriptionFile pdf = this.getDescription();
		return pdf.getVersion();
	}
	
	//Global variables
	private static ItemStack globalItem;
	private static ItemMeta globalMeta;
	
	//Method for creating an item
	public static ItemStack createItem(Player player) {	
		//Makes an ItemStack (item)
		globalItem = new ItemStack(Material.STICK);
		//Allows for meta changing
		globalMeta = globalItem.getItemMeta();
		//Lame stuff
		globalMeta.setDisplayName(ChatColor.WHITE + "Chest Opener");
		globalMeta.setLore(Arrays.asList(ChatColor.RED + "Assigned to: " + player.getName(),
				ChatColor.RED + playerConfig.getPlayerConfig().getString("chests." + player.getName() + ".code")));
		globalItem.setItemMeta(globalMeta);
		//Returning the item
		return globalItem;
	}
	
	//Getting global variable(s)
	public static ItemStack getChestItem() {
		return globalItem;
	}
	
	//Basically making a mini uuid
	public static String generateItemCode() {
		StringBuilder code = new StringBuilder();
		String id = "ABCDEF1234567890";
		Random random = new Random();
		for(int idx = 0; idx < 5; idx++) {
			int index = (int) (random.nextFloat() * id.length());
			code.append(id.charAt(index));
		}
		return code.toString();
	}
	
	//Reload command for nerds
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("reloadchest")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(sender.isOp()) {
					player.sendMessage(MessageUtil.translate(Messages.reload));
					
					if(!playerConfig.getPlayerConfigFile().exists() || !rangeConfig.getRangeConfigFile().exists()) {
						sender.sendMessage(MessageUtil.translate(Messages.missingfile));
						saveResource("range.yml", false);
						saveResource("players.yml", false);
						sender.sendMessage(MessageUtil.translate(Messages.filefound));
						Bukkit.getLogger().log(Level.INFO, "Config files generated!");
						return true;
					} else {
						rangeConfig.getRangeConfigFile();
						playerConfig.getPlayerConfigFile();
						
						rangeConfig.reloadRangeConfig();
						playerConfig.reloadPlayerConfig();
					}
					return true;
				}
			} else {
				//Bad permissions
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
		return Main.instance;
	}

}
