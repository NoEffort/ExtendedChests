package me.noeffort.extendedchests.util;

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
import org.bukkit.util.BlockIterator;

public class PlayerClick implements Listener {
	
	//Ye old EventHandler
	@EventHandler
	public Block PlayerRightClickBlock(PlayerInteractEvent event) {
		
		//Gets the player
		Player player = (Player) event.getPlayer();
		//Pre-determines the base range that you can open the chest at (normally)
		int range = 5;
		//Multiplier used to expand the radius
		float rangeMultiplier = 1.0F;
		
		//Actually multiplying them together
		int processed = (int) rangeMultiplier;
		processed = processed * range;
		
		//This is the issue. I'm trying to find processed before I can tell it what block type to use.
		//Each block type will change the rangeMultiplier, yet it gets called before that can happen.
		BlockIterator iterator = new BlockIterator(player, processed);
		//Looking for the chest block
		Block lastBlock = iterator.next();
		
		//Looping for the chest block
		while(iterator.hasNext()) {
			lastBlock = iterator.next();
			//Fuck air
			if(lastBlock.getType() == Material.AIR) {
				continue;
			}
			//Found it
			else if(lastBlock.getType() == Material.CHEST) {
				//Getting the chest block's location
				Location blockLocation = lastBlock.getLocation();
				//Getting the player's location
				Location playerLocation = player.getPlayer().getLocation();

				//Finding the block under the chest block
				Location newBlock = blockLocation;
				newBlock.subtract(0, 1, 0).getBlock();
				
				//Re-defining the block under the chest
				Block belowBlock = newBlock.getWorld().getBlockAt(newBlock);
				
				//Switching the acceptable block types and defining multipliers
				//This part is basically useless because the range is already defined...
				switch(belowBlock.getType()) {
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
						break;
				}
				
				//Checking to see if the player is within the range of the chest
				if(blockLocation.distance(playerLocation) < processed) {
					//Checking for action
					if(event.getAction() == Action.LEFT_CLICK_AIR) {
						//Getting the chest's location again
						blockLocation.add(0, 1, 0).getBlock();
						Block block = (Block) blockLocation.getBlock();
						//Re-setting the type in case gay shit happens
						//(And you can't directly set a block to a chest anymore)
						block.setType(Material.CHEST);
						//Getting the chest's state (rotation and shit)
						Chest chest = (Chest) block.getState();
						//Opening the inventory
						Inventory inventory = chest.getInventory();
						player.openInventory(inventory);
					}
				} else {
					//Returning this shit
					return belowBlock;
				}
			}
			//Stop reading this
			break;
		}
		return lastBlock;
	}

}
//What a nerd
