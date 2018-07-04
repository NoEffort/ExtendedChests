package me.noeffort.extendedchests.util;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;

import me.noeffort.extendedchests.Main;
import me.noeffort.extendedchests.Messages;
import me.noeffort.extendedchests.util.config.PlayerConfig;
import me.noeffort.extendedchests.util.config.RangeConfig;

public class PlayerClick implements Listener {
	
	private static RangeConfig rangeConfig;
	private static PlayerConfig playerConfig;
	
	//Initiating the Main class
	Main plugin;
	
	//Constructor
	public PlayerClick(Main instance) {
		this.plugin = instance;
	}
	
	//Ye old EventHandler
	@EventHandler
	public void onRightClickAir(PlayerInteractEvent event){
		
		rangeConfig = new RangeConfig(plugin) ;
		playerConfig = new PlayerConfig(plugin);
		
		//Gets all possible Block Faces
		final BlockFace[] BLOCKFACE = { BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST };
		
		//Gets the player
		Player player = (Player) event.getPlayer();
		
		//Gets the action (Left-Clicking)
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			
			//Makes sure the player is using a certain item.
			if(!event.getItem().isSimilar(Main.getChestItem())) {
				return;
			}
			
			rangeConfig.saveRangeConfig();
			rangeConfig.reloadRangeConfig();
			
			//Defines the max range
			int max = 100;
			
			//Defines the "openable" range
			int range = rangeConfig.getRangeConfig().getInt("baseRange");
			
			if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
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
						BlockFace attatched = ((org.bukkit.material.Sign) data).getAttachedFace();
						//Debug
						if(!attatched.equals(face.getOppositeFace())) {
							System.out.println("Opposite Face");
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
								
								//Getting the length of the player's name
								int length = player.getName().length();
								
								//Getting the player's name on the sign (breaks once then works properly)
								if(!sign.getLine(2).replaceAll("(?i)\u00A7[0-F]", "").equals(player.getName().substring(0, length))) {
									event.setCancelled(true);
									break;
								} else {
									//Editing the config (save and reload)
									playerConfig.getPlayerConfigFile();
									playerConfig.getPlayerConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "." + sign.getLine(3) + ".x", block.getLocation().getBlockX());
									playerConfig.getPlayerConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "." + sign.getLine(3) + ".y", block.getLocation().getBlockY());
									playerConfig.getPlayerConfig().set("chests." + player.getName() + "." + player.getWorld().getName() + "."  + sign.getLine(3) + ".z", block.getLocation().getBlockZ());
									playerConfig.savePlayerConfig();
									playerConfig.reloadPlayerConfig();
									
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
									rangeMultiplier = (float) rangeConfig.getRangeConfig().getInt("rangeMultiplier.iron");
									//Default
									if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 1.5F;
										}
									}
									break;
								case GOLD_BLOCK:
									rangeMultiplier = (float) rangeConfig.getRangeConfig().getInt("rangeMultiplier.gold");
									//Default
									if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 2.0F;
										}
									}
									break;
								case DIAMOND_BLOCK:
									rangeMultiplier = (float) rangeConfig.getRangeConfig().getInt("rangeMultiplier.diamond");
									//Default
									if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 2.5F;
										}
									}
									break;
								case EMERALD_BLOCK:
									rangeMultiplier = (float) rangeConfig.getRangeConfig().getInt("rangeMultiplier.emerald");
									//Default
									if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
										if(true) {
											rangeMultiplier = 3.0F;
										}
									}
									break;
								default:
									rangeMultiplier = (float) rangeConfig.getRangeConfig().getInt("rangeMultiplier.default");
									//Default
									if(rangeConfig.getRangeConfig().getBoolean("useDefaults")) {
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
}
//What a nerd
