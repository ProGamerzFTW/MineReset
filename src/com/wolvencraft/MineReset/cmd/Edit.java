package com.wolvencraft.MineReset.cmd;

import java.util.List;

import com.wolvencraft.MineReset.CommandManager;
import com.wolvencraft.MineReset.config.Language;
import com.wolvencraft.MineReset.config.Regions;

public class Edit
{
	private static String curMine;
	
	public static void run(String[] args)
	{
		if(Util.isPlayer())
			if(!Util.senderHasPermission("edit"))
			{
				Util.sendDenied(args);
				return;
			}
		
		if(args.length == 1)
		{
			Help.getEdit();
			return;
		}
		if(args.length > 3)
		{
			Util.sendInvalid(args);
		}
		
		curMine = CommandManager.getMine();
		
		if(args[0].equalsIgnoreCase("edit"))
		{
			if(args.length != 2)
			{
				Util.sendInvalid(args);
				return;
			}
			String mineName = args[1];
			if(!Util.mineExists(mineName))
			{
				String error = Language.getString("general.mine-name-invalid");
				error = Util.parseString(error, "%MINE%", mineName);
				Util.sendError(error);
				return;
			}
			CommandManager.setMine(mineName);
			String message = Language.getString("general.mine-selected-successfully");
			String displayName = Regions.getString("mines." + mineName + ",display-name");
			message = Util.parseString(message, "%MINE%", mineName);
			message = Util.parseString(message, "%MINENAME%", displayName);
			Util.sendSuccess(message);
		}
		else if(args[0].equalsIgnoreCase("none"))
		{
			if(args.length != 2)
			{
				Util.sendInvalid(args);
				return;
			}
			String mineName = args[1];
			if(!Util.mineExists(mineName))
			{
				String error = Language.getString("general.mine-name-invalid");
				error = Util.parseString(error, "%MINE%", mineName);
				Util.sendError(error);
				return;
			}
			CommandManager.setMine(null);
			String message = Language.getString("general.mine-deselected-successfully");
			String displayName = Regions.getString("mines." + mineName + ",display-name");
			message = Util.parseString(message, "%MINE%", mineName);
			message = Util.parseString(message, "%MINENAME%", displayName);
			Util.sendSuccess(message);
		}
		else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("+"))
		{
			if(curMine == null)
			{
				String error = Language.getString("general.mine-not-selected");
				Util.sendError(error);
				return;
			}
			
			if(args.length != 3)
			{
				Util.sendError("I find your lack of parameters disturbing");
				return;
			}
			String blockName = args[1];
			int blockId = Util.getBlockId(blockName);
			
			if(blockId == -1)
			{
				Util.sendError("Block '"+ args[1] + "' does not exist");
				return;
			}
			
			
			List<String> itemList = Regions.getList("mines." + curMine + ".materials.blocks");
			List<String> weightList = Regions.getList("mines." + curMine + ".materials.weights");
			
			if(itemList.size() != weightList.size())
			{
				Util.sendError("Your regions.yml file has been corrupted");
				return;
			}
			
			if(itemList.size() == 0)
			{
				itemList.add("0");
				weightList.add("100");
			}
			
			if(blockId == Integer.parseInt(itemList.get(0)))
			{
				Util.sendError("You do not need to do this. The weight of the default block is calculated automatically.");
				return;
			}
			
			double percent;
			if(Util.isNumeric(args[2]))
			{
				percent = Double.parseDouble(args[2]);
				if(percent <= 0)
				{
					Util.sendInvalid(args);
					return;
				}
				percent = (double)(Math.round(percent * 1000)) / 1000;
			}
			else {
				if(Util.debugEnabled()) Util.log("Argument is not numeric, attempting to parse");
				String awkwardValue = args[2];
				String[] awkArray = awkwardValue.split("%");
				try
				{
				percent = Double.parseDouble(awkArray[0]);
				}
				catch(NumberFormatException nfe)
				{
					Util.sendInvalid(args);
					return;
				}
			}
			if(Util.debugEnabled()) Util.log("Percent value is " + percent);
			
			double percentAvailable = Double.parseDouble(weightList.get(0));
			double newStonePercent;
			if((percentAvailable - percent) < 0)
			{
				Util.sendError("Invalid percentage. Use /mine info " + curMine + " to review the percentages");
				return;
			}
			else newStonePercent = percentAvailable - percent;
			
			newStonePercent = (double)(Math.round(newStonePercent * 1000)) / 1000;
			int index = itemList.indexOf("" + blockId);
			if(Util.debugEnabled()) Util.log(blockId + " ? " + index);
			if(index == -1)
			{
				itemList.add(blockId + "");
				weightList.add(""+percent);
			}
			else
			{
				String weight = Double.parseDouble(weightList.get(index)) + percent + "";
				weightList.set(index, weight);
			}
			// Writing everything down
			
			weightList.set(0, ""+newStonePercent);
			Regions.setList("mines." + curMine + ".materials.blocks", itemList);
			Regions.setList("mines."+ curMine + ".materials.weights", weightList);
			
			Regions.saveData();
			
			Util.sendSuccess(percent + "% of " + blockName + " added to " + curMine);
			Util.sendSuccess("Reset the mine for the changes to take effect");
			return;
		}
		else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-"))
		{
			if(curMine == null)
			{
				String error = Language.getString("general.mine-not-selected");
				Util.sendError(error);
				return;
			}
			
			int blockId = Util.getBlockId(args[1]);
			
			if(blockId == -1)
			{
				Util.sendError("Block '"+ args[1] + "' does not exist");
				return;
			}
			
			List<String> itemList = Regions.getList("mines." + curMine + ".materials.blocks");
			List<String> weightList = Regions.getList("mines." + curMine + ".materials.weights");

			if(blockId == Integer.parseInt(itemList.get(0)))
			{
				Util.sendError("You cannot remove the default block from the mine");
				return;
			}
			
			int index = itemList.indexOf("" + blockId);
			if(Util.debugEnabled()) Util.log(blockId + " ? " + index);
			if(index == -1)
			{
				Util.sendError("There is no '" + args[2] + "' in mine '" + curMine + "'");
				return;
			}
			double oldStoneWeight = Double.parseDouble(weightList.get(0));
			double newStoneWeight = oldStoneWeight + Double.parseDouble("" + weightList.get(index));
			weightList.set(0, "" + newStoneWeight);
			itemList.remove(index);
			weightList.remove(index);
			

			Regions.setList("mines." + curMine + ".materials.blocks", itemList);
			Regions.setList("mines." + curMine + ".materials.weights", weightList);
			
			Regions.saveData();
			Util.sendSuccess(args[1] + " was successfully removed from mine '" + curMine + "'");
			return;
		}
		else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))
		{
			CommandManager.getPlugin().getRegionData().set("mines." + curMine, null);
			List<String> regionList = Regions.getList("data.list-of-mines");
			regionList.remove(regionList.indexOf(args[1]));
			Regions.setList("data.list-of-mines", regionList);
			Regions.saveData();
			CommandManager.setMine(null);
			Util.sendSuccess("Mine '" + args[1] + "' was successfully deleted.");
			return;
		}
		else if(args[0].equalsIgnoreCase("name"))
		{
			if(curMine == null)
			{
				String error = Language.getString("general.mine-not-selected");
				Util.sendError(error);
				return;
			}
			
			String name = args[1];
			for(int i = 2; i < args.length; i++)
			{
				name = name + " " + args[i];
			}
			Regions.setString("mines." + curMine + ".display-name", name);
			Util.sendSuccess("Mine '" + curMine + "' now has a display name '" + name + "'");
			Regions.saveData();
			return;
		}
		else if(args[0].equalsIgnoreCase("silent"))
		{
			if(curMine == null)
			{
				String error = Language.getString("general.mine-not-selected");
				Util.sendError(error);
				return;
			}
			
			if(Regions.getBoolean("mines." + curMine + ".silent"))
			{
				Util.sendSuccess("Silent mode has been turned off for mine '" + curMine + "'");
				Regions.setBoolean("mines." + curMine + ".silent", false);
			}
			else
			{
				Util.sendSuccess("'" + curMine + "' will no longer broadcast any notifications");
				Regions.setBoolean("mines." + curMine + ".silent", true);
			}
		}
		else
		{
			Util.sendInvalid(args);
			return;
		}
	}
}
