package com.wolvencraft.MineReset.cmd;

import java.util.List;

import org.bukkit.Location;

import com.wolvencraft.MineReset.CommandManager;
import com.wolvencraft.MineReset.MineReset;
import com.wolvencraft.MineReset.generation.SnapshotGenerator;
import com.wolvencraft.MineReset.mine.Mine;
import com.wolvencraft.MineReset.mine.Snapshot;
import com.wolvencraft.MineReset.util.Message;
import com.wolvencraft.MineReset.util.MineUtils;
import com.wolvencraft.MineReset.util.SnapshotUtils;
import com.wolvencraft.MineReset.util.Util;

public class SnapshotCommand {
	public static void run(String[] args) {
		if(!Util.senderHasPermission("edit")) {
			Message.sendDenied(args);
			return;
		}
		
		if(args.length == 1) {
			HelpCommand.getSnapshot();
			return;
		}
		
		if(args.length > 3) {
			Message.sendInvalidArguments(args);
			return;
		}
		
		Mine curMine = CommandManager.getMine();
		if(!args[1].equalsIgnoreCase("restore") && curMine == null) {
			Message.sendMineNotSelected();
			return;
		}
		
		if(args[1].equalsIgnoreCase("save")) {
			if(args.length != 2) {
				Message.sendInvalidArguments(args);
				return;
			}
			
			if(!Util.locationsSet()) {
				Message.sendError("Make a selection first");
				return;
			}
			
			Location[] loc = CommandManager.getLocation();
			
			if(!loc[0].getWorld().equals(loc[1].getWorld())) {
				Message.sendError("Your selection points are in different worlds");
				return;
			}
			
			if(!loc[0].getWorld().equals(curMine.getWorld())) {
				Message.sendError("Mine and protection regions are in different worlds");
				return;
			}
			
			Snapshot snap = new Snapshot(curMine.getName());
			snap.setBlocks(loc[0].getWorld(), loc[0], loc[1]);
			List<Snapshot> snaps = MineReset.getSnapshots();
			snaps.add(snap);
			MineReset.setSnapshots(snaps);
			SnapshotUtils.save(snap);
			Message.sendNote(curMine.getName(), "Snapshot successfully saved!");
		}
		else if(args[1].equalsIgnoreCase("restore")) {
			if(args.length == 2 && curMine == null) {
				Message.sendMineNotSelected();
				return;
			}
			else if(args.length == 3) {
				curMine = MineUtils.getMine(args[2]);
				if(curMine == null) {
					Message.sendInvalidMineName(args[2]);
					return;
				}
			}
			
			SnapshotGenerator.reset(curMine);
			Message.sendNote(curMine.getName(), "Snapshot successfully restored!");
		}
		else if(args[1].equalsIgnoreCase("delete")) {
			if(args.length != 2) {
				Message.sendInvalidArguments(args);
				return;
			}
			
			if(SnapshotUtils.delete(SnapshotUtils.getSnapshot(curMine)))
				Message.sendNote(curMine.getName(), "Snapshot successfully deleted!");
			else
				Message.sendError("This mine does not have a snapshot saved");
		}
		else {
			Message.sendInvalid(args);
			return;
		}
		return;
	}
}
