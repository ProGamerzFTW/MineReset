package com.wolvencraft.MineReset.cmd;

import java.util.List;

import com.wolvencraft.MineReset.CommandManager;
import com.wolvencraft.MineReset.cmd.Help;
import com.wolvencraft.MineReset.config.Language;
import com.wolvencraft.MineReset.config.Regions;
import com.wolvencraft.MineReset.generation.EmptyGenerator;
import com.wolvencraft.MineReset.generation.RandomGenerator;
import com.wolvencraft.MineReset.util.Broadcast;
import com.wolvencraft.MineReset.util.Message;
import com.wolvencraft.MineReset.util.Mine;
import com.wolvencraft.MineReset.util.Util;

public class Reset
{	
	public static void run(String[] args, boolean automatic, String forcedGenerator)
	{	
		String mineName = null;
		if(args.length == 1)
		{
			mineName = CommandManager.getMine();

			if(mineName == null)
			{
				Help.getReset();
				return;
			}
		}
		else if(args.length != 2)
		{
			Message.sendInvalid(args);
			return;
		}
		else
			mineName = args[1];
		
		if(!Mine.exists(mineName))
		{
			String error = Util.parseVars(Language.getString("general.mine-name-invalid"), mineName);
			Message.sendError(error);
			return;
		}
		
		if(!automatic)
		{
			if(!Util.senderHasPermission("reset.manual") && !Util.senderHasPermission("reset.manual." + mineName))
			{
				Message.sendDenied(args);
				return;
			}
		}
		
		String generator;
		if(forcedGenerator == null)
			generator = Regions.getString("mines." + mineName + ".reset.generator");
		else generator = forcedGenerator;
		
		if(generator.equalsIgnoreCase("random"))
		{
			RandomGenerator.run(mineName);
		}
		else if(generator.equalsIgnoreCase("empty") || generator.equalsIgnoreCase("clear"))
		{
			EmptyGenerator.run(mineName);
		}
		else
		{
			Message.log("Invalid generator!");
			return;
		}
		
		boolean silent = Regions.getBoolean("mines." + mineName + ".silent");
		boolean parent = Regions.getString("mines." + mineName + ".parent") == null;
		int nextReset = Mine.getResetTime(mineName);
		Regions.setInt("mines." + mineName + ".reset.auto.data.next", nextReset);
		Regions.saveData();
		
		String broadcastMessage;
		if(automatic)
		{
			List<String> mineList = Regions.getList("data.list-of-mines");
			for(String childMineName : mineList)
			{
				if(Regions.getString("mines." + childMineName + ".parent") != null && Regions.getString("mines." + childMineName + ".parent").equalsIgnoreCase(mineName))
				{
					String[] childArgs = {"", childMineName};
					run(childArgs, true, null);
				}
			}
			
			broadcastMessage = Language.getString("reset.automatic-reset-successful");
		}
		else
		{
			broadcastMessage = Language.getString("reset.manual-reset-successful");
		}
		
		broadcastMessage = Util.parseVars(broadcastMessage, mineName);
		
		if(parent)
		{
			if(!silent)
			{
				Broadcast.sendSuccess(broadcastMessage);
			}
			else if(!automatic)
			{
				Message.sendSuccess(broadcastMessage);
			}
		}
		return;
	}
}
