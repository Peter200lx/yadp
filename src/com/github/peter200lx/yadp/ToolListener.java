package com.github.peter200lx.yadp;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Cake;
import org.bukkit.material.Coal;
import org.bukkit.material.Crops;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.Diode;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.Dye;
import org.bukkit.material.Ladder;
import org.bukkit.material.Lever;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.PressurePlate;
import org.bukkit.material.Pumpkin;
import org.bukkit.material.Rails;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.material.Sign;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.Torch;
import org.bukkit.material.TrapDoor;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

public class ToolListener implements Listener {
	protected static final Logger log = Logger.getLogger("Minecraft");

	private HashMap<String, HashMap<Integer, MaterialData>> pPalette = new HashMap<String, HashMap<Integer, MaterialData>>();

	@EventHandler
	public void catchinteract(PlayerInteractEvent event){
		Player subject = event.getPlayer();
		Material tool = subject.getItemInHand().getType();
		if(YADP.tools.containsValue(tool)) {
			if((YADP.tools.get("dupe")==tool)&&(YADP.hasPerm(subject,"yadp.tool.dupe"))) {
				event.setCancelled(true);
				this.dupeTool(event);
			}else if((YADP.tools.get("scroll")==tool)&&(YADP.hasPerm(subject,"yadp.tool.scroll"))) {
				event.setCancelled(true);
				this.scrollTool(event);
			}else if((YADP.tools.get("paint")==tool)&&(YADP.hasPerm(subject, "yadp.tool.paint"))) {
				event.setCancelled(true);
				this.paintTool(event);
			}
		}
	}

	@EventHandler
	public void catchItemChange(PlayerItemHeldEvent event) {
		Player subject = event.getPlayer();
		if(pPalette.containsKey(subject.getName())				&&
				(pPalette.get(subject.getName()).size() > 1)	&&
				subject.getInventory().getItem(event.getNewSlot()).getType().equals(
						YADP.tools.get("paint"))				){
			MaterialData c = pPalette.get(subject.getName()).get(event.getNewSlot());
			paintPrint("Paint in this slot is ",subject,c);
		}
	}

	@SuppressWarnings("deprecation")	//TODO Investigate replacement .updateInventory()
	private void dupeTool(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player subject = event.getPlayer();
			Block clicked = event.getClickedBlock();
			Material type = clicked.getType();

			if(YADP.debug) log.info("[yadp][dupeTool] "+subject.getName()+
					" clicked "+clicked.getState().getData());

			Material toUse = YADP.dupeMap.get(type);
			if(toUse == null)
				toUse = type;
			if(toUse == Material.AIR) {
				subject.sendMessage(ChatColor.GREEN + "Duplicating " + ChatColor.GOLD +
						type.toString()+ ChatColor.GREEN + "is disabled");
				return;
			}

			if((clicked.getData() != 0)&&(YADP.keepData.contains(toUse))&& (
					type.equals(toUse) ||
					type.equals(Material.WOOL)&&toUse.equals(Material.INK_SACK)   ||
					type.equals(Material.STEP)&&toUse.equals(Material.DOUBLE_STEP)||
					type.equals(Material.DOUBLE_STEP)&&toUse.equals(Material.STEP)||
					type.equals(Material.LOG)&&toUse.equals(Material.LEAVES) ||
					type.equals(Material.LOG)&&toUse.equals(Material.SAPLING)||
					type.equals(Material.LEAVES)&&toUse.equals(Material.LOG) ||
					type.equals(Material.LEAVES)&&toUse.equals(Material.SAPLING)	)	) {
				subject.getInventory().addItem(new ItemStack(toUse, 64, (short) 0, clicked.getData()));
			} else {
				subject.getInventory().addItem(new ItemStack(toUse, 64));
			}
			subject.updateInventory();
			if(YADP.keepData.contains(toUse))
			{
				subject.sendMessage(ChatColor.GREEN + "Enjoy your " + ChatColor.GOLD +
						toUse.toString() + ChatColor.WHITE + ":" +
						ChatColor.BLUE + data2Str(clicked.getState().getData()));
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
				Player p = event.getPlayer();
				if(YADP.debug) log.info("[yadp][scrollTool] "+p.getName()+
						" clicked "+clicked.getState().getData());
				if(p.getGameMode().equals(GameMode.CREATIVE)		&&
						act.equals(Action.LEFT_CLICK_BLOCK)			&&
						clicked.getType().equals(Material.SIGN_POST)||
						clicked.getType().equals(Material.WALL_SIGN)){
						p.sendMessage("The sign is not erased on the server, "+
								"it is just client side");
				}

				int max = YADP.dataMap.get(clicked.getType());
				byte data = clicked.getData();

				if(max != 0) {
					data = simpScroll(event, data, max);
				} else {
					MaterialData b = clicked.getState().getData();
					Material type = clicked.getType();
					if( type.equals(Material.JUKEBOX)) {
						p.sendMessage("Data value indicates contained record, can't scroll");
						return;
					}else if(type.equals(Material.SOIL)) {
						p.sendMessage("Data value indicates dampness level, can't scroll");
						return;
					}else if(type.equals(Material.TORCH)			||
							type.equals(Material.REDSTONE_TORCH_OFF)||
							type.equals(Material.REDSTONE_TORCH_ON)	){
						data = simpScroll(event, data, 1, 6);
					} else if(type.equals(Material.POWERED_RAIL)) {
						data = simpScroll(event, (byte)(data&0x07), 6);
						if(((PoweredRail)b).isPowered())
							data |= 0x08;
					} else if(type.equals(Material.DETECTOR_RAIL)) {
						data = simpScroll(event, (byte)(data&0x07), 6);
						if(((DetectorRail)b).isPressed())
							data |= 0x08;
					} else if(type.equals(Material.LEVER)) {
						data = simpScroll(event,(byte)(data&0x07), 1,7);
						if(((Lever)b).isPowered())
							data |= 0x08;
					} else if(type.equals(Material.WOODEN_DOOR)		||
							type.equals(Material.IRON_DOOR_BLOCK)	){
						if(((Door)b).isTopHalf()) {
							p.sendMessage("Clicking the top half of a door "+
									"can't scroll the rotation corner.");
							return;
						}
						data = simpScroll(event,(byte)(data&0x07),4);
						if(((Door)b).isOpen())
							data |= 0x04;
						p.sendMessage("Top door half now looks funny, open/close door to fix");
					} else if(type.equals(Material.STONE_BUTTON)) {
						data = simpScroll(event, (byte)(data&0x07), 1, 5);
					} else if(type.equals(Material.LADDER)	||
							type.equals(Material.WALL_SIGN)	||
							type.equals(Material.FURNACE)	||
							type.equals(Material.DISPENSER)	){
						data = simpScroll(event, (byte)(data&0x07), 2, 6);
					} else if(type.equals(Material.CHEST)) {
						//It doesn't look like CHEST can be safely scrolled because of double chests.
						p.sendMessage(clicked.getType()+" is not scrollable");
						return;
					} else if(type.equals(Material.STONE_PLATE)	||
							type.equals(Material.WOOD_PLATE)	){
						p.sendMessage("There is no useful data to scroll");
						return;
					} else if(type.equals(Material.BED_BLOCK)) {
						//TODO More research into modifying foot and head of bed at once
						p.sendMessage(clicked.getType()+" is not yet scrollable");
						return;
					} else if(type.equals(Material.DIODE_BLOCK_OFF)	||
							type.equals(Material.DIODE_BLOCK_ON)	){
						byte tick = (byte)(data & (0x08 | 0x04));
						data = simpScroll(event,(byte)(data&0x03),4);
						data |= tick;
					} else if(type.equals(Material.REDSTONE_WIRE))	{
						p.sendMessage("There is no useful data to scroll");
						return;
					} else if(type.equals(Material.TRAP_DOOR))	{
						data = simpScroll(event, (byte)(data&0x03), 4);
						if(((TrapDoor)b).isOpen())
							data |= 0x04;
					} else if(type.equals(Material.PISTON_BASE)		||
							type.equals(Material.PISTON_STICKY_BASE)){
						if(((PistonBaseMaterial)b).isPowered()) {
							p.sendMessage("The piston will not be scrolled while extended");
							return;
						}
						data = simpScroll(event, (byte)(data&0x07), 6);
					} else if(type.equals(Material.PISTON_EXTENSION))	{
						p.sendMessage("The piston extension should not be scrolled");
						return;
					} else if(type.equals(Material.FENCE_GATE))	{
						data = simpScroll(event, (byte)(data&0x03), 4);
						if((b.getData()&0x04)==0x04)	//Is the gate open?
							data |= 0x04;
					} else if(type.equals(Material.BREWING_STAND))	{
						p.sendMessage("Stand data just is for visual indication of placed glass bottles");
						return;
					} else {
						p.sendMessage(clicked.getType()+" is not yet scrollable");
						return;
					}
				}

				clicked.setData(data, false);

				event.getPlayer().sendMessage(ChatColor.GREEN + "Block is now " + ChatColor.GOLD +
						clicked.getType() + ChatColor.WHITE + ":" +
						ChatColor.BLUE + data2Str(clicked.getState().getData()));
			}
		}
	}

	//Note that min is inclusive and max is exclusive.
	// So to scroll through 1,2,3,4 set min to 1 and max to 5
	private byte simpScroll(PlayerInteractEvent event, byte data, int min, int max) {
		return (byte) (simpScroll(event,(byte) (data-min),max-min) + min);
	}

	//Note that max is exclusive, to scroll through 0,1,2 set max to 3
	private byte simpScroll(PlayerInteractEvent event, byte data, int max) {
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			if ((data - 1) < 0)
				data = (byte) (max - 1);
			else
				data = (byte) ((data - 1) % max);
			if(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
				event.getPlayer().sendMessage("Data value scrolled, you might "+
					"not see the change");
		} else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			data = (byte) ((data + 1) % max);
		}
		return data;
	}

	private void paintTool(PlayerInteractEvent event) {
		Player subject = event.getPlayer();
		if(!this.pPalette.containsKey(subject.getName())) {
			this.pPalette.put(subject.getName(), new HashMap<Integer,MaterialData>());
		}
		if(event.getAction().equals(Action.LEFT_CLICK_AIR)			||
				event.getAction().equals(Action.LEFT_CLICK_BLOCK)	){
			//Acquire paint
			MaterialData target = null;
			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK))
				target = event.getClickedBlock().getState().getData();
			else
				target = subject.getTargetBlock(null, 200).getState().getData();
			if(!YADP.paintBlockLoad.contains(target.getItemType())	){
				this.pPalette.get(subject.getName()).put(subject.getInventory().getHeldItemSlot(), target );
				paintPrint("Paint is now ",subject,target);
			} else {
				subject.sendMessage(ChatColor.RED + "Was not able to grab a block to paint.");
				MaterialData old = this.pPalette.get(subject.getName()).get(subject.getInventory().getHeldItemSlot());
				paintPrint("Paint is still ",subject,old);
			}
		} else if(event.getAction().equals(Action.RIGHT_CLICK_AIR)	||
				event.getAction().equals(Action.RIGHT_CLICK_BLOCK)	){
			//Draw paint
			MaterialData set = this.pPalette.get(subject.getName()).get(subject.getInventory().getHeldItemSlot());
			if(set != null) {
				Block target = null;
				if(YADP.paintRange && event.getAction().equals(Action.RIGHT_CLICK_AIR) ){
					target = subject.getTargetBlock(null, YADP.paintDist);
				}else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					target = event.getClickedBlock();
				if(target != null && !YADP.paintBlockOverwrite.contains(target.getType())) {
					target.setTypeIdAndData(set.getItemTypeId(), set.getData(), false);
				}
			}
		}
	}

	private void paintPrint(String prefix, CommandSender subject, MaterialData m) {
		if(m == null)
			subject.sendMessage(ChatColor.RED + prefix + ChatColor.GOLD + "empty");
		else if(YADP.keepData.contains(m.getItemType())||(m.getData() != 0))
			subject.sendMessage(ChatColor.GREEN + prefix + ChatColor.GOLD +
					m.getItemType().toString() + ChatColor.WHITE + ":" +
					ChatColor.BLUE + data2Str(m));
		else
			subject.sendMessage(ChatColor.GREEN + prefix + ChatColor.GOLD +
						m.getItemType().toString());
	}

	private String data2Str(MaterialData b) {
		Material type = b.getItemType();
		byte data = b.getData();
		if(YADP.debug) log.info("[yadp][data2str] Block "+b.toString());
		if(Material.LOG == type) {
			if(((Tree)b).getSpecies() != null)
				return ((Tree)b).getSpecies().toString();
			else
				return ""+data;
		} else if((Material.LEAVES == type)||(Material.SAPLING == type)) {
			if(((Tree)b).getSpecies() != null)	//Checked because there are unnamed tree colors
				return ((Tree)b).getSpecies().toString();
			else
				return ""+data;
		} else if(Material.JUKEBOX == type) {
			if(data == 0x0)			return "Empty";
			else if(data == 0x1)	return "Record 13";
			else if(data == 0x2)	return "Record cat";
			else if(data == 0x3)	return "Record blocks";
			else if(data == 0x4)	return "Record chrip";
			else if(data == 0x5)	return "Record far";
			else if(data == 0x6)	return "Record mall";
			else if(data == 0x7)	return "Record melloci";
			else if(data == 0x8)	return "Record stal";
			else if(data == 0x9)	return "Record strad";
			else if(data == 0x10)	return "Record ward";
			else					return "Record " + data;
		} else if(Material.CROPS == type) {
			return ((Crops)b).getState().toString();
		} else if(Material.WOOL == type) {
				return ((Wool)b).getColor().toString();
			} else if(Material.INK_SACK == type) {
				return ((Dye)b).toString();
		} else if(Material.TORCH == type) {
			return ((Torch)b).getFacing().toString();
		} else if((Material.REDSTONE_TORCH_OFF == type)||(Material.REDSTONE_TORCH_ON == type)) {
			return ((RedstoneTorch)b).getFacing().toString();
		} else if(Material.RAILS==type) {
			return ((Rails)b).getDirection() +
				(	((Rails)b).isCurve() ? " on a curve" : (
					((Rails)b).isOnSlope() ? " on a slope" : ""	)	);
		} else if(Material.POWERED_RAIL==type) {
			return ((PoweredRail)b).getDirection() +
					(((PoweredRail)b).isOnSlope() ? " on a slope" : "");
		} else if(Material.DETECTOR_RAIL==type) {
			return ((DetectorRail)b).getDirection() +
					(((DetectorRail)b).isOnSlope() ? " on a slope" : "");
		} else if((Material.WOOD_STAIRS==type)||(Material.COBBLESTONE_STAIRS==type)) {
			return ((Stairs)b).getFacing().toString();
		} else if((Material.NETHER_BRICK_STAIRS==type)||
				(Material.BRICK_STAIRS==type)||(Material.SMOOTH_STAIRS==type)) {
			if((data&0x3) == 0x0) {
				return "NORTH";
			} else if((data&0x3) == 0x1) {
				return "SOUTH";
			} else if((data&0x3) == 0x2) {
				return "EAST";
			} else if((data&0x3) == 0x3) {
				return "WEST";
			}
			return "" + data;
		} else if(Material.LEVER == type) {
			return ((Lever)b).getAttachedFace().toString();
		} else if((Material.WOODEN_DOOR == type)||(Material.IRON_DOOR_BLOCK == type)) {
			return ((Door)b).getHingeCorner().toString() + " is " +
					(((Door)b).isOpen()?"OPEN":"CLOSED");
		} else if(Material.STONE_BUTTON == type) {
			return ((Button)b).getAttachedFace().toString();
		} else if(Material.SIGN_POST == type) {
			return ((Sign)b).getFacing().toString();
		} else if(Material.LADDER == type) {
			return ((Ladder)b).getAttachedFace().toString();
		} else if(Material.WALL_SIGN == type) {
			return ((Sign)b).getAttachedFace().toString();
		} else if(Material.FURNACE == type) {
			return ((Directional)b).getFacing().toString();
		} else if(Material.DISPENSER == type) {
			return ((Directional)b).getFacing().toString();
		} else if((Material.PUMPKIN==type)||(Material.JACK_O_LANTERN==type)) {
			return ((Pumpkin)b).getFacing().toString();
		} else if((Material.STONE_PLATE==type)||(Material.WOOD_PLATE==type)) {
			return ((PressurePlate)b).isPressed()?" is PRESSED":" is not PRESSED";
		} else if(Material.COAL == type) {
			return ((Coal)b).getType().toString();
		} else if((Material.STEP == type)||((Material.DOUBLE_STEP == type))) {
			return ((Step)b).getMaterial().toString();
		} else if(Material.SNOW == type) {
			if(data == 0x0)			return "1/8 HEIGHT";
			else if(data == 0x1)	return "2/8 HEIGHT";
			else if(data == 0x3)	return "3/8 HEIGHT (STEP)";
			else if(data == 0x4)	return "4/8 HEIGHT (STEP)";
			else if(data == 0x5)	return "5/8 HEIGHT (STEP)";
			else if(data == 0x6)	return "6/8 HEIGHT (STEP)";
			else if(data == 0x7)	return "7/8 HEIGHT (STEP)";
			else if(data == 0x8)	return "FULL HEIGHT (STEP)";
			else					return ""+data;
		} else if(Material.CAKE_BLOCK == type) {
			return ""+((Cake)b).getSlicesRemaining()+"/6 REMAINING";
		} else if((Material.DIODE_BLOCK_OFF==type)||(Material.DIODE_BLOCK_ON==type)) {
			return ((Diode)b).getFacing().toString()+" with DELAY of "+((Diode)b).getDelay();
		} else if(Material.LONG_GRASS == type) {
			return ((LongGrass)b).getSpecies().toString();
		} else if(Material.TRAP_DOOR == type) {
			return ((TrapDoor)b).getAttachedFace().toString() + " is " +
									(((TrapDoor)b).isOpen()?"OPEN":"CLOSED");
		} else if((Material.PISTON_BASE==type)||(Material.PISTON_STICKY_BASE==type)) {
			return ((PistonBaseMaterial)b).getFacing().toString();
		} else if(Material.SMOOTH_BRICK == type) {
			if(data == 0x0)			return "NORMAL";
			else if(data == 0x1)	return "MOSSY";
			else if(data == 0x2)	return "CRACKED";
			else					return ""+data;
		} else if((Material.HUGE_MUSHROOM_1 == type)||(Material.HUGE_MUSHROOM_2 == type)) {
			if(data == 0x0)			return "FLESHY PIECE";
			else if(data == 0x1)	return "CAP ON TOP & W & N";
			else if(data == 0x2)	return "CAP ON TOP & N";
			else if(data == 0x3)	return "CAP ON TOP & N & E";
			else if(data == 0x4)	return "CAP ON TOP & W";
			else if(data == 0x5)	return "CAP ON TOP";
			else if(data == 0x6)	return "CAP ON TOP & E";
			else if(data == 0x7)	return "CAP ON TOP & S & W";
			else if(data == 0x8)	return "CAP ON TOP & S";
			else if(data == 0x9)	return "CAP ON TOP & E & S";
			else if(data == 0x10)	return "STEM";
			else					return ""+data;
		} else if(Material.VINE == type) {
			String ret = "";
			if((data&0x1) == 0x1) {
				if(ret.length() == 0)	ret += "SOUTH";
				else					ret += " & SOUTH";	}
			if((data&0x2) == 0x2) {
				if(ret.length() == 0)	ret += "WEST";
				else					ret += " & WEST";	}
			if((data&0x4) == 0x4) {
				if(ret.length() == 0)	ret += "NORTH";
				else					ret += " & NORTH";	}
			if((data&0x8) == 0x8) {
				if(ret.length() == 0)	ret += "EAST";
				else					ret += " & EAST";	}
			if(ret.length() == 0)
				ret += "TOP";
			return ret;
		} else if(Material.FENCE_GATE == type) {
			String append = " is Closed";
			if((data&0x4) == 0x4)
				append = " is OPEN";
			if((data&0x3) == 0x0) {
				return "SOUTH"+append;
			} else if((data&0x3) == 0x1) {
				return "WEST"+append;
			} else if((data&0x3) == 0x2) {
				return "NORTH"+append;
			} else if((data&0x3) == 0x3) {
				return "EAST"+append;
			}
			return ""+data;
		} else if(Material.MONSTER_EGGS == type) {
			if(data == 0x0)			return Material.STONE.toString();
			else if(data == 0x1)	return Material.COBBLESTONE.toString();
			else if(data == 0x2)	return Material.SMOOTH_BRICK.toString();
			else					return ""+data;
		} else if(Material.BREWING_STAND == type) {
			String ret = "Bottle in ";
			if((data&0x1) == 0x1) {
			if(ret.length() == 10)	ret += "EAST Slot";
			else					ret += " & EAST Slot";	}
			if((data&0x2) == 0x2) {
			if(ret.length() == 10)	ret += "SOUTH_WEST Slot";
			else					ret += " & SOUTH_WEST Slot";	}
			if((data&0x4) == 0x4) {
				if(ret.length() == 10)	ret += "NORTH_WEST Slot";
				else					ret += " & NORTH_WEST Slot";	}
			if(ret.length() == 10)
				ret = "Empty";
			return ret;
		} else if(Material.CAULDRON == type) {
			if(data == 0x0)			return "EMPTY";
			else if(data == 0x1)	return "1/3 FILLED";
			else if(data == 0x2)	return "2/3 FILLED";
			else if(data == 0x3)	return "FULL";
			else					return ""+data;
		} else if(Material.ENDER_PORTAL_FRAME == type) {
			//TODO Add intelligence here
			return "" + data;
		} else if(Material.EGG == type) {
			//TODO Is there anywhere we can get a mapping of entity id to name?
			return "" + data;
		} else {
			return "" + data;
		}
	}
}
