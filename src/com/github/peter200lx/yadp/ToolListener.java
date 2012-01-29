package com.github.peter200lx.yadp;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ToolListener implements Listener {
	protected static final Logger log = Logger.getLogger("Minecraft");

	@EventHandler
	public void catchinteract(PlayerInteractEvent event){
		if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			//TODO Investigate how to disable left-click destruction with creative mode
			event.getPlayer().sendMessage("YADP tools do not work with left-click in creative mode");
		} else {
			Player subject = event.getPlayer();
			String call = YADP.tools.get(subject.getItemInHand().getType());
			if(call != null) {
				if((call == "dupeTool")&&(subject.hasPermission("yadp.tool.dupe")))
					this.dupeTool(event);
				else if((call == "scrollTool")&&(subject.hasPermission("yadp.tool.scroll")))
					this.scrollTool(event);
			}
		}
	}
	
	private void dupeTool(PlayerInteractEvent event){
		Player subject = event.getPlayer();
		Block clicked = event.getClickedBlock();

		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Material toUse = YADP.dupeMap.get(clicked.getType());
			if(toUse == null)
				toUse = clicked.getType();

			if(clicked.getData() == 0) {
				subject.getInventory().addItem(new ItemStack(toUse, 64));
	        	subject.sendMessage(ChatColor.GREEN + "Enjoy your " + ChatColor.GOLD + 
		        		  clicked.getType());
	        	subject.updateInventory();
			} else {
				subject.getInventory().addItem(new ItemStack(toUse, 64, (short) 0, clicked.getData()));
	        	subject.sendMessage(ChatColor.GREEN + "Enjoy your " + ChatColor.GOLD + 
		        		  clicked.getType() + ChatColor.WHITE + ":" + 
		        		  ChatColor.BLUE + clicked.getData());
	        	subject.updateInventory();
			}
		}
	}
	
	private void scrollTool(PlayerInteractEvent event) {
		if(YADP.dataMap.containsKey(event.getClickedBlock().getType())) {
			int max = YADP.dataMap.get(event.getClickedBlock().getType());
			byte data = event.getClickedBlock().getData();

			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				data = (byte) ((data - 1) % max);
				event.getPlayer().sendMessage("Data value scrolled, you just can't see it");
			} else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				data = (byte) ((data + 1) % max);
			}
			event.getClickedBlock().setData(data, false);
		} else {
			event.getPlayer().sendMessage( event.getClickedBlock().getType().toString()+
					" is not scrollable.");
		}
	}
}
