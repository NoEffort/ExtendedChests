package me.noeffort.extendedchests;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import me.noeffort.extendedchests.command.ExtendedChestCommand;
import me.noeffort.extendedchests.util.PlayerClick;
import me.noeffort.extendedchests.util.config.PlayerConfig;
import me.noeffort.extendedchests.util.config.RangeConfig;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;

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
	private RangeConfig range;
	private PlayerConfig player;
	private PlayerClick click;
	
	//Enable this shit
	@Override
	public void onEnable() {
		
		instance = this;
		
		getLogger().info("Plugin Enabled! " + getVersion());
		
		registerConfig();
		registerCommands();
		registerListeners();
		
		runChecker();
	}
	
	@Override
	public void onDisable() {
		stopTask(taskID);
		range.saveConfig();
		player.saveConfig();
	}
	
	public void registerConfig() {
		range = new RangeConfig();
		range.createFile();
		
		player = new PlayerConfig();
		player.createFile();
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
		this.getCommand("extendedchest").setExecutor(new ExtendedChestCommand());
	}
	
	//Getting and loading all listeners in the program
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerClick(), this);
		click = new PlayerClick();
	}
	
	//Getting the plugin's version from the plugin.yml
	private String getVersion() {
		PluginDescriptionFile pdf = this.getDescription();
		return pdf.getVersion();
	}
	
	//Global variables
	private ItemStack globalItem;
	private ItemMeta globalMeta;
	
	//Method for creating an item
	public ItemStack createItem(Player player) {	
		//Makes an ItemStack (item)
		globalItem = new ItemStack(Material.STICK);
		//Allows for meta changing
		globalMeta = globalItem.getItemMeta();
		//Lame stuff
		globalMeta.setDisplayName(ChatColor.WHITE + "Chest Opener");
		globalMeta.setLore(Arrays.asList(ChatColor.RED + "Assigned to: " + player.getName()));
		globalItem.setItemMeta(globalMeta);
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(globalItem);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		compound.set("Code", new NBTTagString(this.player.getConfig().getString("chests." + player.getName() + ".code")));
		nmsStack.setTag(compound);
		globalItem = CraftItemStack.asBukkitCopy(nmsStack);
		//Returning the item
		return globalItem;
	}
	
	//Getting global variable(s)
	public ItemStack getChestItem() {
		return globalItem;
	}
	
	//Basically making a mini uuid
	public String generateItemCode() {
		StringBuilder code = new StringBuilder();
		String id = "ABCDEF1234567890";
		Random random = new Random();
		for(int idx = 0; idx < 5; idx++) {
			int index = (int) (random.nextFloat() * id.length());
			code.append(id.charAt(index));
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(String key : player.getConfig().getConfigurationSection("chests").getKeys(false)) {
			map.put(key, player.getConfig().getString("chests." + key + ".code"));
			for(String str : map.values()) {
				if(str.equals(code.toString())) {
					getLogger().warning("Duplicate Code Found! Editing...");
					for(int idx = 0; idx < 5; idx++) {
						int index = (int) (random.nextFloat() * id.length());
						code.append(id.charAt(index));
					}
				} else {
					return code.toString();
				}
			}
		}
		
		return code.toString();
	}
	
	private int taskID;
	
	private void runChecker() {
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				click.checkSigns();
			}
		}, 0L, 6000L);
	}
	
	private void stopTask(int id) {
		Bukkit.getScheduler().cancelTask(id);
	}
	
	public PlayerConfig getPlayerConfig() {
		return player;
	}
	
	public RangeConfig getRangeConfig() {
		return range;
	}
	
	public static Main get() {
		return Main.instance;
	}

}
