package com.wolvencraft.MineReset.events;

import java.util.List;

import com.wolvencraft.MineReset.mine.Mine;
import com.wolvencraft.MineReset.mine.Protection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;

import com.wolvencraft.MineReset.MineReset;
import com.wolvencraft.MineReset.config.Configuration;
import com.wolvencraft.MineReset.util.Message;
import com.wolvencraft.MineReset.util.Util;

public class BlockPlaceListener implements Listener
{
	public BlockPlaceListener(MineReset plugin)
	{
		Message.debug("Initiating BlockPlaceListener");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
        Message.debug("BlockPlaceEvent called");
		
		Player player = event.getPlayer();
		
		if(Util.playerHasPermission(player, "protection") || !Configuration.getBoolean("lag.protection-checks-enabled"))
		{
            Message.debug("Bypass permission check passed");
			return;
		}

        Message.debug("Bypass permission check failed");
		
		List<Mine> mines = MineReset.getMines();
        Message.debug("Retrieved the region list");
		
		if(mines.size() == 0) return;
		Block b = event.getBlockPlaced();
		
		for(Mine mine : mines)
		{
            Message.debug("For mine " + mine.getName());
			
			if (mine.getProtection().contains(Protection.BLOCK_PLACE))
			{
                Message.debug(mine.getName() + " has protection enabled");
				Location blockLocation = b.getLocation();

				if (mine.isLocationInProtection(blockLocation)) {
                    Message.debug("Player placed a block in the mine region");

					if(!Util.playerHasPermission(player, "protection.place." + mine.getName()) && !Util.playerHasPermission(player, "protection.place"))
					{
                        Message.debug("Second permissions check passed");
						Message.sendPlayerError(player, "You are not allowed to place " + ChatColor.RED + b.getType().name().toLowerCase().replace("_", " ") + ChatColor.WHITE + " in this mine");
						event.setCancelled(true);
						return;
					}

                    Message.debug("Second permissions check failed");
					if(mine.getBlacklist().getEnabled())
					{
						boolean found = false;
						for(MaterialData block : mine.getBlacklist().getBlocks())
						{
							if(block.getItemType() == b.getType())
							{
								found = true;
							}
						}
						
						if((mine.getBlacklist().getWhitelist() && !found) || (!mine.getBlacklist().getWhitelist() && found))
						{
							Message.sendPlayerError(player, "You are not allowed to place " + ChatColor.RED + b.getType().name().toLowerCase().replace("_", " ") + ChatColor.WHITE + " in this mine");
							event.setCancelled(true);
							return;
						}
					}
					else
					{
						Message.sendPlayerError(player, "You are not allowed to place blocks in this mine");
						event.setCancelled(true);
					}
				}
			}
		}
		
		return;
	}
}
