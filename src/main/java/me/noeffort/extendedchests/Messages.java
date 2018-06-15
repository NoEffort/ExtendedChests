package me.noeffort.extendedchests;

//Just messages
//Begone
public interface Messages {

	String prefix = "&7[&4Extended&cChests&7] ";
	String permissions = prefix + "&cYou do not have the valid permissions to execute this command!";
	String invalid = prefix + "&cThe command you have entered is invalid!";
	String toomanyargs = prefix + "&cYou have entered too many arguments for the command!";
	String toolittleargs = prefix + "&cYou have not entered enough arguments for the command!";
	String player = prefix + "&cYou must be a player to execute this command!";
	String target = prefix + "&cThe player you have specified is not online!";
	String wip = prefix + "&fThis command or action is a work in progress, give it time.";
	String error = prefix + "&cAn error has occured!";
	String unknown = prefix + "&4An unknown error has occured! Please inform the plugin developer!";
	String reload = prefix + "&aConfig files reloaded!";
	
}

