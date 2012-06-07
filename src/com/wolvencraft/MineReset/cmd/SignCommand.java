package com.wolvencraft.MineReset.cmd;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;

import com.wolvencraft.MineReset.CommandManager;
import com.wolvencraft.MineReset.MineReset;
import com.wolvencraft.MineReset.config.Language;
import com.wolvencraft.MineReset.config.Signs;
import com.wolvencraft.MineReset.mine.Mine;
import com.wolvencraft.MineReset.mine.SignClass;
import com.wolvencraft.MineReset.util.Message;
import com.wolvencraft.MineReset.util.SignUtils;
import com.wolvencraft.MineReset.util.Util;

public class SignCommand
{
	public static void run(String args[])
	{
		Player player;
		if(CommandManager.getSender() instanceof Player) {
			player = (Player) CommandManager.getSender();
		}
		else {
			Message.sendError("This command cannot be executed via console");
			return;
		}
		if(!Util.senderHasPermission("edit")) {
			Message.sendDenied(args);
			return;
		}
		
		if(args.length == 1) {
			HelpCommand.getSign();
			return;
		}
		else if(args.length != 2) {
			Message.sendInvalid(args);
			return;
		}
		
		Block b = player.getTargetBlock(null, 100);
		if(b.getType() != Material.WALL_SIGN && b.getType() != Material.SIGN_POST) {
			Message.sendError("The targeted block is not a sign");
			return;
		}
		
		Mine curMine = CommandManager.getMine();
		if(curMine == null) {
			Message.sendError(Language.getString("general.mine-not-selected"));
			return;
		}
		
		
		if(args[1].equalsIgnoreCase("create")) {	
			if(SignUtils.exists(b.getLocation())) {
				Message.sendError("This sign has been defined already.");
				return;
			}
			
			SignClass sign = new SignClass(curMine, b.getLocation(), (Sign) b.getState());
			
			List<SignClass> signs = MineReset.getSigns();
			signs.add(sign);
			MineReset.setSigns(signs);
			
			Message.sendSuccess("A new sign was defined successfully");
			return;
		}
		else if(args[1].equalsIgnoreCase("reset")) {
			if(!Signs.signExists(b)) {
				Message.sendError("This sign has not been defined for this mine yet");
				return;
			}
			SignClass sign = SignUtils.getSignAt(b.getLocation());
			
			if(sign.getReset()) {
				sign.setReset(false);
				Message.sendSuccess("Right-clicking on this sign will no longer result in a reset of " + sign.getParent().getDisplayName());
			}
			else {
				sign.setReset(true);
				Message.sendSuccess("Right-clicking on this sign will now result in a reset of " + sign.getParent().getDisplayName());
			}
			
			return;
		}
		else if(args[1].equalsIgnoreCase("remove")) {
			if(!Signs.signExists(b)) {
				Message.sendError("This sign has not been defined yet");
				return;
			}
			
			SignClass sign = SignUtils.getSignAt(b.getLocation());
			
			if(b.getState() instanceof Sign) {
				Sign signBlock = (Sign) b.getState();

				for(int i = 0; i < 4; i++) {
					String line = sign.getLines().get(i);
					if(!line.equals("")) {
						signBlock.setLine(i, line);
					}
				}
				signBlock.update(true);
			}
			

			List<SignClass> signs = MineReset.getSigns();
			signs.remove(sign);
			MineReset.setSigns(signs);
			
			Message.sendSuccess("The sign is no longer defined. You can destroy it now.");
			return;
		}
		else {
			Message.sendInvalid(args);
			return;
		}
	}
}
