package com.wolvencraft.MineReset.cmd;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wolvencraft.MineReset.CommandManager;

public class Util
{
	/**
	 * Checks if the command sender has a permission.
	 * @param node The permission node
	 * @return true is the sender has a permission, false if he does not
	 */
	public static boolean senderHasPermission(String node, boolean allowConsole)
	{
		CommandSender sender = CommandManager.getSender();
		boolean usePermissions = getConfigBoolean("configuration.use-permissions");
		
		// If a command is sent from the console, it is
		// automatically allowed
		Player player;
		if (isPlayer()) player = (Player) sender;
		else
		{
			if(allowConsole) return true;
			else return false;
		}
		
		if(!usePermissions)
		{
			if(player.isOp()) return true;
			else return false;
		}
		
		if(player.hasPermission("minereset." + node))
			return true;
		
		return false;
	}
	

	/**
	 * Checks if the command sender has a permission.
	 * @param node The permission node
	 * @return true is the sender has a permission, false if he does not
	 */
	public static boolean playerHasPermission(Player player, String node)
	{
		boolean usePermissions = getConfigBoolean("configuration.use-permissions");
		
		if(!usePermissions)
		{
			if(player.isOp()) return true;
			else return false;
		}
		
		if(player.hasPermission("minereset." + node))
			return true;
		
		return false;
	}
	
	/**
	 * Checks if the command sender is a player or a console
	 * @return true if sender is a player, false if not
	 */
	public static boolean isPlayer()
	{
		CommandSender sender = CommandManager.getSender();
		
		if(sender instanceof Player) return true;
		else return false;
	}
	
	/**
	 * Checks if debug mode is enabled in the config
	 * @return true if debug is enabled, false if it's not
	 */
	public static boolean debugEnabled()
	{
		if(getConfigBoolean("configuration.debug-mode")) return true;
		else return false;
	}
	
	/**
	 * Sends a message back to the command sender
	 * @param message A message to be sent
	 */
	public static void sendMessage(String message)
	{
		CommandSender sender = CommandManager.getSender();
		sender.sendMessage(message);
	}

	/**
	 * Sends a green-titled message to the command sender
	 * @param message A message to be sent
	 */
	public static void sendSuccess(String message)
	{
		CommandSender sender = CommandManager.getSender();
		String title = getConfigString("messages.title");
		sender.sendMessage(ChatColor.GREEN + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Sends a green-titled message to a player
	 * @param message A message to be sent
	 */
	public static void sendPlayerSuccess(Player player, String message)
	{
		String title = getConfigString("messages.title");
		player.sendMessage(ChatColor.GREEN + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Sends a red-titled message to the command sender
	 * @param message A message to be sent
	 */
	public static void sendError(String message)
	{
		CommandSender sender = CommandManager.getSender();
		String title = getConfigString("messages.title");
		sender.sendMessage(ChatColor.RED + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Sends a red-titled message to a player
	 * @param message A message to be sent
	 */
	public static void sendPlayerError(Player player, String message)
	{
		String title = getConfigString("messages.title");
		player.sendMessage(ChatColor.RED + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Message thrown when the command used by a player is invalid
	 * Also sends a command into the log
	 * @param command Command used
	 */
	public static void sendInvalid(String[] args)
	{
		CommandSender sender = CommandManager.getSender();
		String title = getConfigString("messages.title");
		String message = getConfigString("messages.invalid-command");
		String command = "";
		for(int i = 0; i < args.length; i++)
			command = command + " " + args[i];
		log(sender.getName() + " sent an invalid command (" + command + ")");
		sender.sendMessage(ChatColor.RED + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Message thrown when the user is denied of an action
	 * @param command Command used
	 */
	public static void sendDenied(String[] args)
	{
		CommandSender sender = CommandManager.getSender();
		String title = getConfigString("messages.title");
		String message = getConfigString("messages.access-denied");
		String command = "";
		for(int i = 0; i < args.length; i++)
			command = command + " " + args[i];
		log(sender.getName() + " was denied to use a command (" + command + ")");
		sender.sendMessage(ChatColor.RED + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Sends a message into the server log
	 * @param message A message to be sent
	 */
	public static void log(String message)
	{
		Logger log = Bukkit.getServer().getLogger();
		log.info(message);
	}
	
	/**
	 * Sends a warning message into the server log
	 * @param message A message to be sent
	 */
	public static void logWarning(String message)
	{
		Logger log = Bukkit.getServer().getLogger();
		log.warning(message);
	}
	
	/**
	 * Broadcasts a green-titled message to all players
	 * @param message A message to be sent
	 */
	public static void broadcastMessage(String message)
	{
		Bukkit.getServer().broadcastMessage(message);
		return;
	}
	
	/**
	 * Broadcasts a green-titled message to all players
	 * This should be normally used just for the mine reset warnings
	 * @param message A message to be sent
	 */
	public static void broadcastSuccess(String message)
	{
		String title = CommandManager.getPlugin().getConfig().getString("messages.title");
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Broadcasts a red-titled message to all players
	 * This should not be used normally
	 * @param message A message to be sent
	 */
	public static void broadcastError(String message)
	{
		String title = CommandManager.getPlugin().getConfig().getString("messages.title");
		Bukkit.getServer().broadcastMessage(ChatColor.RED + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Returns configuration data from the node
	 * @param node Configuration node
	 * @return String to be returned
	 */
	public static String getConfigString(String node)
	{
		String stringToReturn = CommandManager.getPlugin().getConfig().getString(node);
		if(stringToReturn != null) return stringToReturn;
		logWarning("Node does not exist: " + node);
		return "";
	}
	
	/**
	 * Returns configuration data from the node
	 * @param node Configuration node
	 * @return boolean to be returned
	 */
	public static boolean getConfigBoolean(String node)
	{
		boolean booleanToReturn = CommandManager.getPlugin().getConfig().getBoolean(node);
		return booleanToReturn;
	}
	
	/**
	 * Returns configuration data from the node
	 * @param node Configuration node
	 * @return int to be returned
	 */
	public static int getConfigInt(String node)
	{
		int intToReturn = CommandManager.getPlugin().getConfig().getInt(node);
		return intToReturn;
	}
	
	/**
	 * Returns configuration data from the node
	 * @param node Configuration node
	 * @return double to be returned
	 */
	public static double getConfigDouble(String node)
	{
		double intToReturn = CommandManager.getPlugin().getConfig().getDouble(node);
		return intToReturn;
	}
	
	/**
	 * Returns configuration data from the node
	 * @param node Configuration node
	 * @return List<String> to be returned
	 */
	public static List<String> getConfigList(String node)
	{
		List<String> listToReturn = CommandManager.getPlugin().getConfig().getStringList(node);
		if(listToReturn != null) return listToReturn;
		logWarning("Node does not exist: " + node);
		return null;
	}
	
	/**
	 * Returns region data from the node
	 * @param node Configuration node
	 * @return String to be returned
	 */
	public static String getRegionString(String node)
	{
		String stringToReturn = CommandManager.getPlugin().getRegionData().getString(node);
		if(stringToReturn != null) return stringToReturn;
		logWarning("Node does not exist: " + node);
		return "";
	}
	
	/**
	 * Returns region data from the node
	 * @param node Configuration node
	 * @return boolean to be returned
	 */
	public static boolean getRegionBoolean(String node)
	{
		boolean booleanToReturn = CommandManager.getPlugin().getRegionData().getBoolean(node);
		return booleanToReturn;
	}
	
	/**
	 * Returns region data from the node
	 * @param node Configuration node
	 * @return int to be returned
	 */
	public static int getRegionInt(String node)
	{
		int intToReturn = CommandManager.getPlugin().getRegionData().getInt(node);
		return intToReturn;
	}
	
	/**
	 * Returns region data from the node
	 * @param node Configuration node
	 * @return int to be returned
	 */
	public static double getRegionDouble(String node)
	{
		double intToReturn = CommandManager.getPlugin().getRegionData().getDouble(node);
		return intToReturn;
	}
	
	/**
	 * Returns region data from the node
	 * @param node Configuration node
	 * @return List<String> to be returned
	 */
	public static List<String> getRegionList(String node)
	{
		List<String> listToReturn = CommandManager.getPlugin().getRegionData().getStringList(node);
		if(listToReturn != null) return listToReturn;
		logWarning("Node does not exist: " + node);
		return null;
	}
	
	/**
	 * Sets region data to a designated node
	 * @param node Configuration node
	 * @param data Data to be set
	 */
	public static void setRegionString(String node, String data)
	{
		CommandManager.getPlugin().getRegionData().set(node, data);
	}
	
	/**
	 * Sets region data to a designated node
	 * @param node Configuration node
	 * @param data Data to be set
	 */
	public static void setRegionBoolean(String node, boolean data)
	{
		CommandManager.getPlugin().getRegionData().set(node, data);
	}
	
	/**
	 * Sets region data to a designated node
	 * @param node Configuration node
	 * @param data Data to be set
	 */
	public static void setRegionInt(String node, int data)
	{
		CommandManager.getPlugin().getRegionData().set(node, data);
	}
	
	/**
	 * Sets region data to a designated node
	 * @param node Configuration node
	 * @param data Data to be set
	 */
	public static void setRegionDouble(String node, double data)
	{
		CommandManager.getPlugin().getRegionData().set(node, data);
	}
	
	/**
	 * Sets region data to a designated node
	 * @param node Configuration node
	 * @param data Data to be set
	 */
	public static void setRegionList(String node, List<String> data)
	{
		CommandManager.getPlugin().getRegionData().set(node, data);
	}
	
	/**
	 * Saves region data to a file.
	 * This should not be used too often due to possible server lag
	 */
	public static void saveRegionData()
	{
		CommandManager.getPlugin().saveRegionData();
	}
	
	/**
	 * Parses a block specified for a material
	 * @param blockName Name of a block
	 * @return Block material if it exists, null if it does not.
	 */
	public static int getBlockId(String blockName)
	{
		try
		{
			if(isNumeric(blockName)) return Integer.parseInt(blockName);
			else
			{
				Material material = Material.matchMaterial(blockName);
				if(material == null) return -1;
				return material.getId();
			}
		}
		catch(NumberFormatException nfe) { return -1; }
	}
	
	/**
	 * Checks if a string is numeric
	 * @param str String String to be checked
	 * @return boolean True if a string is numeric
	 */
	@SuppressWarnings("unused")
	public static boolean isNumeric(String str)  
	{  
	  try
	  { double d = Double.parseDouble(str); }
	  catch(NumberFormatException nfe)  
	  { return false; }  
	  return true;  
	}
	

	/**
	 * Checks if the mine exists
	 * @param name Name of the mine being checked
	 * @return True if the mine exists, False if it does not
	 */
	public static boolean mineExists(String name)
	{
		List<String> mineList = getRegionList("data.list-of-mines");
		if(mineList.indexOf(name) == -1) return false;
		else return true;
	}
	
	/**
	 * Replaces the specified value in the string provided with the new value
	 * @param str String to parse
	 * @param replaceFrom Value to be replaced
	 * @param replaceTo Value to be substituted
	 * @return A new String with necessary values substituted
	 */
	public static String parseString(String str, String replaceFrom, String replaceTo)
	{
		str = str.replaceAll(replaceFrom, replaceTo);
		return str;
	}
}
