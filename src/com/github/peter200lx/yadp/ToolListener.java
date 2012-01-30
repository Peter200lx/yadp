package com.github.peter200lx.yadp;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.Ladder;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.Step;
import org.bukkit.material.TrapDoor;
import org.bukkit.material.Wool;

public class ToolListener implements Listener {
	protected static final Logger log = Logger.getLogger("Minecraft");

	@EventHandler
	public void catchinteract(PlayerInteractEvent event){
		Player subject = event.getPlayer();
		if ((event.getAction().equals(Action.LEFT_CLICK_AIR)) ||
				event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			//TODO Investigate how to disable left-click destruction with creative mode
			if(YADP.debug) log.info("[catchinteract]"+subject.getName()+" "+
					event.getAction().toString() + " on air (likely in creative mode)");
		} else {
			Material tool = subject.getItemInHand().getType();
			if(YADP.tools.containsValue(tool)) {
				if((YADP.tools.get("dupeTool")==tool)&&(subject.hasPermission("yadp.tool.dupe")))
					this.dupeTool(event);
				else if((YADP.tools.get("scrollTool")==tool)&&(subject.hasPermission("yadp.tool.scroll")))
					this.scrollTool(event);
			}
		}
	}

	@SuppressWarnings("deprecation")	//TODO Investigate replacement .updateInventory()
	private void dupeTool(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player subject = event.getPlayer();

			Block clicked = event.getClickedBlock();
			if(YADP.debug) log.info("[dupeTool] "+subject.getName()+" clicked "+clicked.getType());

			Material toUse = YADP.dupeMap.get(clicked.getType());
			if(toUse == null)
				toUse = clicked.getType();

			if((YADP.dataMap.containsKey(clicked.getType()))||(clicked.getData() != 0)) {
				subject.getInventory().addItem(new ItemStack(toUse, 64, (short) 0, clicked.getData()));
				subject.updateInventory();
			} else {
				subject.getInventory().addItem(new ItemStack(toUse, 64));
				subject.updateInventory();
			}
			if(YADP.dataMap.containsKey(toUse))
			{
				subject.sendMessage(ChatColor.GREEN + "Enjoy your " + ChatColor.GOLD +
						toUse.toString() + ChatColor.WHITE + ":" +
						ChatColor.BLUE + data2Str(clicked));
			} else {
				subject.sendMessage(ChatColor.GREEN + "Enjoy your " + ChatColor.GOLD +
							toUse.toString());
			}
		}
	}
	
	private void scrollTool(PlayerInteractEvent event) {
		Action act = event.getAction();
		if(act.equals(Action.LEFT_CLICK_BLOCK)||(act.equals(Action.RIGHT_CLICK_BLOCK))) {
			if(YADP.dataMap.containsKey(event.getClickedBlock().getType())) {
				Block clicked = event.getClickedBlock();
				if(YADP.debug) log.info("[scrollTool] "+event.getPlayer().getName()+" clicked "+clicked.getType());
				int max = YADP.dataMap.get(clicked.getType());
				byte data = clicked.getData();

				if(max != 0) {
					if(act.equals(Action.LEFT_CLICK_BLOCK)){
						data = (byte) ((data - 1) % max);
						event.getPlayer().sendMessage("Data value scrolled, you just can't see it");
					} else if(act.equals(Action.RIGHT_CLICK_BLOCK)){
						data = (byte) ((data + 1) % max);
					}
				} else {
					//TODO Add special case if statements here for complex scrolls
					//if(clicked.getType()==Material.NOTE_BLOCK) { }
					event.getPlayer().sendMessage(clicked.getType()+" is not yet scrollable");
				}

				clicked.setData(data, false);

				event.getPlayer().sendMessage(ChatColor.GREEN + "Block is now " + ChatColor.GOLD +
						clicked.getType() + ChatColor.WHITE + ":" +
						ChatColor.BLUE + data2Str(clicked));
			}
		}
	}

	//TODO Fill out these print statements
	//		Look at YAML.dupeMap for more types to investigate
	private String data2Str(Block b) {
		Material type = b.getType();
		byte data = b.getData();
		int max = YADP.dataMap.get(type);
		if(YADP.debug) log.info("[data2str] Block "+type.toString()+":"+data);
		if(Material.WOOL == type) {
			Wool tmp = new Wool(type,data);
			return tmp.getColor().toString();
		} else if(Material.CROPS == type) {
			Crops tmp = new Crops(type,data);
			return tmp.getState().toString();
		} else if(Material.LONG_GRASS == type) {
			LongGrass tmp = new LongGrass(type,data);
			return tmp.getSpecies().toString();
		} else if(Material.SAPLING == type) {
			return TreeSpecies.getByData(
					(byte)(data%((max==0)?3:max))	).toString();
		} else if(Material.LOG == type) {
			return TreeSpecies.getByData(
					(byte)(data%((max==0)?3:max))	).toString();
		} else if(Material.LEAVES == type) {
			Leaves tmp = new Leaves(type,data);
			if(tmp.getSpecies() == null)
				return ""+data; //TODO Find a proper way to cast a byte to a string
			else
				return tmp.getSpecies().toString();
		} else if(Material.LADDER == type) {
			Ladder tmp = new Ladder(type,data);
			return tmp.getAttachedFace().toString();
		} else if(Material.TRAP_DOOR == type) {
			TrapDoor tmp = new TrapDoor(type,data);
			return tmp.getAttachedFace().toString() + " is " +
									(tmp.isOpen()?"Open":"Closed");
		} else if(Material.STEP == type) {
			Step tmp = new Step(type,data);
			return tmp.getMaterial().toString();
		} else if(Material.DOUBLE_STEP == type) {
			Step tmp = new Step(type,data);
			return tmp.getMaterial().toString();
		} else {
			return "" + data; //TODO Find a proper way to cast a byte to a string
		}
	}
}
