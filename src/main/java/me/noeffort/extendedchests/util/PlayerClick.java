package me.noeffort.extendedchests.util;

import java.util.HashMap;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import me.noeffort.extendedchests.Main;
import me.noeffort.extendedchests.Messages;
import me.noeffort.extendedchests.util.config.PlayerConfig;
import me.noeffort.extendedchests.util.config.RangeConfig;

public class PlayerClick implements Listener {
	
	private RangeConfig range;
	private PlayerConfig config;
	private Sign sign;
	
	//Initiating the Main class
	Main plugin = Main.get();
	
	private HashMap<Location, String> map = new HashMap<Location, String>();
	
	//Constructor
	public PlayerClick() {}
	
	//Ye old EventHandler
	@EventHandler
	public void onRightClickAir(PlayerInteractEvent event){
		
		range = plugin.getRangeConfig();
		config = plugin.getPlayerConfig();
		
		//Gets all possible Block Faces
		final BlockFace[] BLOCKFACE = { BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST };
		
		//Gets the player
		Player player = (Player) event.getPlayer();
		
		//Gets the action (Left-Clicking)
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			
			//Makes sure the player is using a certain item.
			if(!event.getItem().equals(plugin.createItem(player))) {
				return;
			}
			
			config.saveConfig();
			range.saveConfig();
			
			//Defines the max range
			int max = 100;
			
			//Defines the "openable" range
			int range = this.range.getConfig().getInt("baseRange");
			
			if(this.range.getConfig().getBoolean("useDefaults")) {
				if(true) {
					range = 5;
				}
			}
			
			//Mutltiplier used later for the "openable" range
			float rangeMultiplier = 1.0F;
			
			//Creates a list of blocks in the player's sight
			java.util.List<Block> blocks = player.getLineOfSight((Set<Material>) null, max);
			
			//Loops through the list (blocks) to find a chest
			for (int i = 0; i < blocks.size(); i++){
				//Found it
				if (blocks.get(i).getType() == Material.CHEST) {
					//Gets the block; defines as block
					Block block = blocks.get(i);
					//Gets the state of the chest
					Chest chest = (Chest) block.getState();
					//Defines the inventory of the chest (inv)
					Inventory inv = chest.getBlockInventory();
					
					//Checking for sign
					for(BlockFace face : BLOCKFACE) {
						Block ifSign = block.getRelative(face);
						if(ifSign == null || ifSign.getType() != Material.WALL_SIGN) {
							continue;
						}
						
						//Getting state and data of block
						BlockState state = ifSign.getState();
						MaterialData data = state.getData();
						if(!(data instanceof org.bukkit.material.Sign)) {
							continue;
						}
						//Getting sign and attatched face
						Sign sign = (Sign) state;
						setSign(sign);
						BlockFace attatched = ((org.bukkit.material.Sign) data).getAttachedFace();
						//Debug
						if(!attatched.equals(face.getOppositeFace())) {
							return;
						} else {
							if(sign.getLines().length > 0 && sign.getLines()[1].contains("[Extended]")) {
								//Proper sign
								if(!sign.getLine(3).isEmpty()) {
									sign.setLine(0, ChatColor.GREEN + "Generated");
									sign.setLine(2, ChatColor.RED + player.getName());
									sign.update();
								} else {
									//Invalid sign
									player.sendMessage(MessageUtil.translate(Messages.invalidChest));
									sign.setLine(0, "");
									sign.setLine(1, "");
									sign.setLine(2, "");
									sign.setLine(3, "");
									sign.update();
									return;
								}
								
								ItemStack mainhand = player.getInventory().getItemInMainHand();
								net.minecraft.server.v1_12_R1.ItemStack nbtMainhand = CraftItemStack.asNMSCopy(mainhand);
								
								if(nbtMainhand.hasTag() && nbtMainhand.getTag().hasKey("Code")) {
									config.getConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "." + sign.getLine(3) + ".x", sign.getLocation().getBlockX());
									config.getConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "." + sign.getLine(3) + ".y", sign.getLocation().getBlockY());
									config.getConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "."  + sign.getLine(3) + ".z", sign.getLocation().getBlockZ());
									config.saveConfig();
									config.reloadConfig();
									if(!isSignOwner(sign, player)) {
										System.out.println("Running...");
										event.setCancelled(true);
										return;
									}
								}
								
								//Gets the block's location
								Location blockLocation = blocks.get(i).getLocation();
								//Gets the player's location
								Location playerLocation = player.getLocation();
								
								//New location on the block below the chest
								Location newBlock = blockLocation.clone();
								newBlock.subtract(0, 1, 0);
								
								//Defining the block below the chest
								Block belowBlock = newBlock.getWorld().getBlockAt(newBlock);
								
								//Switches through the block types of belowBlock
								switch(belowBlock.getType()){
								case IRON_BLOCK:
									rangeMultiplier = (float) this.range.getConfig().getInt("rangeMultiplier.iron");
									//Default
									if(this.range.getConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 1.5F;
										}
									}
									break;
								case GOLD_BLOCK:
									rangeMultiplier = (float) this.range.getConfig().getInt("rangeMultiplier.gold");
									//Default
									if(this.range.getConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 2.0F;
										}
									}
									break;
								case DIAMOND_BLOCK:
									rangeMultiplier = (float) this.range.getConfig().getInt("rangeMultiplier.diamond");
									//Default
									if(this.range.getConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 2.5F;
										}
									}
									break;
								case EMERALD_BLOCK:
									rangeMultiplier = (float) this.range.getConfig().getInt("rangeMultiplier.emerald");
									//Default
									if(this.range.getConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 3.0F;
										}
									}
									break;
								default:
									rangeMultiplier = (float) this.range.getConfig().getInt("rangeMultiplier.default");
									//Default
									if(this.range.getConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 1.0F;
										}
									}
									break;
								}
								
								//Does many maths
								int processed = (int) ((float)range * rangeMultiplier);
								
								//Determines if the player is within the block's range
								if(blockLocation.distance(playerLocation) < processed){
									//Opens the inventory
									player.openInventory(inv);
								}
								break;
							} else {
								return;
							}
						}
					}
				}
				//Fuck air
				else if(blocks.get(i).getType() == Material.AIR) {
					continue;
				} else {
					break;
				}
			}
		} else {
			return;
		}
	}
	
	public void checkSigns() {
		
		config = plugin.getPlayerConfig();
		
		for(String key : config.getConfig().getConfigurationSection("chests").getKeys(false)) {
			for(String world : config.getConfig().getConfigurationSection("chests." + key).getKeys(false)) {
				if(world.equals("name")) {
					continue;
				}
				if(world.equals("uuid")) {
					continue;
				}
				if(world.equals("code")) {
					continue;
				}
				for(String name : config.getConfig().getConfigurationSection("chests." + key + "." + world).getKeys(false)) {
					String path = "chests." + key + "." + world + "." + name;
					int x = config.getConfig().getInt(path + ".x");
					int y = config.getConfig().getInt(path + ".y");
					int z = config.getConfig().getInt(path + ".z");
					Location location = new Location(Bukkit.getWorld(world), x, y, z);
					BlockState state = location.getBlock().getState();
					MaterialData data = state.getData();
					if(!(data instanceof org.bukkit.material.Sign)) {
						config.getConfig().set(path + ".x", null);
						config.getConfig().set(path + ".y", null);
						config.getConfig().set(path + ".z", null);
						config.getConfig().set(path, null);
						map.remove(location);
					}
				}
			}
		}
		config.saveConfig();
		config.reloadConfig();
	}
	
	public Sign getSign() {
		return sign;
	}
	
	public void setSign(Sign sign) {
		this.sign = sign;
	}
	
	public void addMapValues(Location location, String key) {
		if(!map.containsKey(location)) {
			map.put(location, key);
		}
	}
	
	public boolean isSignOwner(Sign sign, Player player) {
		config = plugin.getPlayerConfig();
		String path = "chests." + player.getName() + "." + player.getWorld().getName() + "." + sign.getLine(3);
		Location location = sign.getLocation();
		if(!config.getConfig().isSet(path)) {
			return false;
		} else {
			double x = config.getConfig().getDouble(path + ".x");
			double y = config.getConfig().getDouble(path + ".y");
			double z = config.getConfig().getDouble(path + ".z");
			Location configLocation = new Location(player.getWorld(), x, y, z);
			if(location.equals(configLocation) && ChatColor.stripColor(sign.getLine(2)).equals(player.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public HashMap<Location, String> getMap() {
		return map;
	}
}
//What a nerd
