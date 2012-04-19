package com.wolvencraft.MineReset.events;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.wolvencraft.MineReset.cmd.Util;

public class BlockBreakListener implements Listener
{
	public BlockBreakListener()
	{
		// does nothing
	}
	
	@SuppressWarnings("unused")
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(!Util.getConfigBoolean("use-protection")) return;
		
		Player player = event.getPlayer();
		
		if(player.isOp() || Util.playerHasPermission(player, "protection") || Util.playerHasPermission(player, "protection.break"))
			return;
		
		int padding;
		int paddingTop;
		
		List<String> regionList = Util.getRegionList("data.list-of-mines");
		
		for(int i = 0; i < regionList.size(); i++)
		{
				if(!Util.playerHasPermission(player, "protection.break." + regionList.get(i)))
				{
					Block b = event.getBlock();
					Location blockLocation = b.getLocation();
					padding = Util.getRegionInt("mines." + regionList.get(i) + ".protection.padding");
					paddingTop = Util.getRegionInt("mines." + regionList.get(i) + ".protection.padding-top");
					int[] x = {Util.getConfigInt("regions." + regionList.get(i) + ".coords.p1.x"), Util.getConfigInt("regions." + regionList.get(i) + ".coords.p2.x")};
					int[] y = {Util.getConfigInt("regions." + regionList.get(i) + ".coords.p1.y"), Util.getConfigInt("regions." + regionList.get(i) + ".coords.p2.y")};
					int[] z = {Util.getConfigInt("regions." + regionList.get(i) + ".coords.p1.z"), Util.getConfigInt("regions." + regionList.get(i) + ".coords.p2.z")};
			
					if((blockLocation.getX() > (x[0] - padding) && blockLocation.getX() < (x[1] + padding))
							&& (blockLocation.getY() > (y[0] - padding) && blockLocation.getY() < (y[1] + paddingTop))
							&& (blockLocation.getZ() > (z[0] - padding) && blockLocation.getZ() < (z[1] + padding)))
					{
						if(Util.getConfigBoolean("mines." + regionList.get(i) + ".protection.breaking.blacklist.enabled"))
						{
							List<String> blacklist = Util.getConfigList("mines." + regionList.get(i) + ".protection.breaking.blacklist.blocks");
							if(Util.getConfigBoolean("mines." + regionList.get(i) + ".protection.breaking.blacklist.whitelist"))
							{
								for(int j = 0; j < blacklist.size(); j++)
								{
									if(blacklist.get(j).indexOf(b.getTypeId() + "") == -1)
									{
										Util.sendPlayerError(player, "You are not allowed to break " + ChatColor.RED + b.getType().name().toLowerCase().replace("_", " ") + ChatColor.WHITE + " in this mine");
										event.setCancelled(true);
										return;
									}
									else return;
								}
							}
							else
							{
								for(int j = 0; j < blacklist.size(); j++)
								{
									if(blacklist.get(j).indexOf(b.getTypeId() + "") == -1)
										return;
									else
									{
										Util.sendPlayerError(player, "You are not allowed to break " + ChatColor.RED + b.getType().name().toLowerCase().replace("_", " ") + ChatColor.WHITE + " in this mine");
										event.setCancelled(true);
										return;
									}
								}
							}
						}
					
					Util.sendPlayerError(player, "You are not allowed to break blocks in this mine");
					event.setCancelled(true);
					}
					else return;
				}
		}
		
		return;
	}
}
