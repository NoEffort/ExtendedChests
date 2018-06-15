package me.noeffort.extendedchests.util;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class MessageUtil implements Listener {
	
	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}
