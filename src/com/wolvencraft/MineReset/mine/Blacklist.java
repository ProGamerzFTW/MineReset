package com.wolvencraft.MineReset.mine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

@SerializableAs("Blacklist")
public class Blacklist implements ConfigurationSerializable, Listener {

	private Mine parent;
	private List<MaterialData> blocks;
	private boolean whitelist;
	
	public Blacklist(Mine parent) {
		this.parent = parent;
		blocks = new ArrayList<MaterialData>();
		whitelist = false;
	}
	
	public Blacklist(Map<String, Object> me) {
		parent = (Mine) me.get("parent");
		whitelist = (Boolean) me.get("whitelist");
        blocks = (List<MaterialData>) me.get("blocks");
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> me = new HashMap<String, Object>();
		me.put("parent", parent);
		me.put("whitelist", whitelist);
		me.put("blocks", blocks);
		return me;
	}
	
	public Mine getParent() {
		return parent;
	}
	
	public List<MaterialData> getBlocks() {
		return blocks;
	}
	
	public boolean getWhitelist() {
		return whitelist;
	}
	
	public void setBlocks(List<MaterialData> blocks) {
		this.blocks = blocks;
	}
	
	public void setWhitelist(boolean whitelist) {
		this.whitelist = whitelist;
	}
}
