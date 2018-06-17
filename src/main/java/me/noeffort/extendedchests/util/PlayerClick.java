package me.noeffort.extendedchests.util;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class PlayerClick implements Listener {
	
	//Ye old EventHandler
	@EventHandler
	public void onRightClickAir(PlayerInteractEvent event){
		
		//Gets the action (Left-Clicking)
		if (event.getAction() == Action.LEFT_CLICK_AIR){
			//Gets the player
			Player player = (Player) event.getPlayer();
			
			//Defines the max range
			int max = 100;
			
			//Defines the "openable" range
			int range = 5;
			
			//Mutltiplier used later for the "openable" range
			float rangeMultiplier = 1.0F;
			
			//Creates a list of blocks in the player's sight
			java.util.List<Block> blocks = player.getLineOfSight((Set<Material>) null, max);
			
			//Loops through the list (blocks) to find a chest
			for (int i = 0; i < blocks.size(); i++){
				//Found it
				if (blocks.get(i).getType() == Material.CHEST){
					//Gets the block; defines as block
					Block block = blocks.get(i);
					//Re-sets the material as a chest
					block.setType(Material.CHEST);
					
					//Gets the state of the chest
					Chest chest = (Chest) block.getState();
					
					//Defines the inventory of the chest (inv)
					Inventory inv = chest.getBlockInventory();
					
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
						rangeMultiplier = 1.5F;
						break;
					case GOLD_BLOCK:
						rangeMultiplier = 2.0F;
						break;
					case DIAMOND_BLOCK:
						rangeMultiplier = 2.5F;
						break;
					case EMERALD_BLOCK:
						rangeMultiplier = 3.0F;
						break;
					default:
						rangeMultiplier = 1.0F;
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
				}
				//Fuck air
				else if(blocks.get(i).getType() == Material.AIR){
					continue;
				}
				else{
					break;
				}	
			}
		}
	}
}
//What a nerd
