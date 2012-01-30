package com.github.peter200lx.yadp;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.CropState;
import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class YADP extends JavaPlugin {
	private Logger log = Logger.getLogger("Minecraft");
	
	public static boolean debug = false;

	static HashMap<String, Material> tools = new HashMap<String, Material>();

	public static HashMap<Material, Material> dupeMap = new HashMap<Material, Material>();
	static {
		dupeMap.put(Material.BED_BLOCK, Material.BED);
		dupeMap.put(Material.PISTON_EXTENSION, Material.PISTON_BASE);
		dupeMap.put(Material.PISTON_MOVING_PIECE, Material.PISTON_BASE);
		dupeMap.put(Material.CROPS, Material.SEEDS);
		dupeMap.put(Material.REDSTONE_WIRE, Material.REDSTONE);
		dupeMap.put(Material.SIGN_POST, Material.SIGN);
		dupeMap.put(Material.WOODEN_DOOR, Material.WOOD_DOOR);
		dupeMap.put(Material.WALL_SIGN, Material.SIGN);
		dupeMap.put(Material.IRON_DOOR_BLOCK, Material.IRON_DOOR);
		dupeMap.put(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON);
		dupeMap.put(Material.SNOW, Material.SNOW_BALL); //TODO Is this a good conversion?
		dupeMap.put(Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE);
		dupeMap.put(Material.DIODE_BLOCK_OFF, Material.DIODE);
		dupeMap.put(Material.DIODE_BLOCK_ON, Material.DIODE);
		dupeMap.put(Material.LOCKED_CHEST, Material.CHEST);
		dupeMap.put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
		dupeMap.put(Material.MELON_STEM, Material.MELON_SEEDS);
		//TODO blocks below are a limit check, not for actual release
		dupeMap.put(Material.BEDROCK, Material.APPLE);
	}

	public static HashMap<Material, Integer> dataMap = new HashMap<Material, Integer>();
	static {
		//If the integer is 0, that means that a simple numerical shift won't work
		dataMap.put(Material.LOG, TreeSpecies.values().length);
		dataMap.put(Material.LEAVES, 4);
		dataMap.put(Material.JUKEBOX, 0);			//TODO More research into music
		dataMap.put(Material.SAPLING, TreeSpecies.values().length);	//TODO More research into age
		dataMap.put(Material.CACTUS, 16);
		dataMap.put(Material.SUGAR_CANE_BLOCK, 16);
		//TODO Add Water and Lava? Likely not	//TODO What if tool id is a bucket
		dataMap.put(Material.SOIL, 8);				//TODO More research into if should be modified
		dataMap.put(Material.CROPS, CropState.values().length);
		dataMap.put(Material.NETHER_WARTS, 4);
		dataMap.put(Material.PUMPKIN_STEM, 8);
		dataMap.put(Material.MELON_STEM, 8);
		dataMap.put(Material.WOOL, DyeColor.values().length);
		//TODO Add Dyes? Likely not
		dataMap.put(Material.TORCH, 6);
		dataMap.put(Material.REDSTONE_TORCH_OFF, 6);
		dataMap.put(Material.REDSTONE_TORCH_ON, 6);
		dataMap.put(Material.RAILS, 10);
		dataMap.put(Material.POWERED_RAIL, 0);		//TODO More research into powered state
		dataMap.put(Material.DETECTOR_RAIL, 0);		//TODO More research into powered state
		dataMap.put(Material.WOOD_STAIRS, 4);
		dataMap.put(Material.COBBLESTONE_STAIRS, 4);
		dataMap.put(Material.BRICK_STAIRS, 4);
		dataMap.put(Material.SMOOTH_STAIRS, 4);
		dataMap.put(Material.NETHER_BRICK_STAIRS, 4);
		dataMap.put(Material.LEVER, 0);				//TODO More research into powered states
		dataMap.put(Material.WOODEN_DOOR, 0);		//TODO More research into half-state
		dataMap.put(Material.IRON_DOOR_BLOCK, 0);	//TODO More research into half-state
		dataMap.put(Material.STONE_BUTTON, 0);		//TODO More research into pressed state
		dataMap.put(Material.SIGN_POST, 16);
		dataMap.put(Material.LADDER, 0);			//TODO More research into missing 0x0 & 0x1
		dataMap.put(Material.WALL_SIGN, 0);			//TODO More research into missing 0x0 & 0x1
		dataMap.put(Material.FURNACE, 0);			//TODO More research into missing 0x0 & 0x1
		dataMap.put(Material.DISPENSER, 0);			//TODO More research into missing 0x0 & 0x1
		dataMap.put(Material.CHEST, 0);				//TODO More research into missing 0x0 & 0x1
		dataMap.put(Material.PUMPKIN, 4);
		dataMap.put(Material.JACK_O_LANTERN, 4);
		dataMap.put(Material.STONE_PLATE, 0);		//TODO More research into powered states
		dataMap.put(Material.WOOD_PLATE, 0);		//TODO More research into powered states
		//TODO Add Coal? Likely not
		//TODO Tools & Armor? Likely not
		dataMap.put(Material.STEP, 7);
		dataMap.put(Material.DOUBLE_STEP, 7);
		dataMap.put(Material.SNOW, 8);
		dataMap.put(Material.CAKE_BLOCK, 6);
		dataMap.put(Material.BED_BLOCK, 0);			//TODO More research into foot/head bit
		dataMap.put(Material.DIODE_BLOCK_OFF, 0);	//TODO More research into delay/direction
		dataMap.put(Material.DIODE_BLOCK_ON, 0);	//TODO More research into delay/direction
		dataMap.put(Material.REDSTONE_WIRE, 0);		//TODO Likely not as it is power level
		dataMap.put(Material.LONG_GRASS, GrassSpecies.values().length);
		dataMap.put(Material.TRAP_DOOR, 0);			//TODO More research into open state
		dataMap.put(Material.PISTON_BASE, 0);		//TODO More research into bit 0x8
		dataMap.put(Material.PISTON_STICKY_BASE, 0);	//TODO same as above
		dataMap.put(Material.PISTON_EXTENSION, 0);	//TODO Uhh, no (ignore)
		dataMap.put(Material.SMOOTH_BRICK, 3);
		dataMap.put(Material.HUGE_MUSHROOM_1, 11);
		dataMap.put(Material.HUGE_MUSHROOM_2, 11);
		dataMap.put(Material.VINE, 16);				//TODO Look into breakdown
		dataMap.put(Material.FENCE_GATE, 0);		//TODO Look into keeping open state
		//TODO Potions? Likely not
		dataMap.put(Material.MONSTER_EGGS, 3);
		dataMap.put(Material.BREWING_STAND, 0);		//TODO Anything that can be done here?
		dataMap.put(Material.CAULDRON, 4);
		dataMap.put(Material.ENDER_PORTAL_FRAME, 4);	//TODO More research into "empty"
		dataMap.put(Material.EGG, 0);				//TODO More research into spawning
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		// Register our events
		getServer().getPluginManager().registerEvents(new ToolListener(), this);
		
		// Load and/or initialize configuration file
		this.getConfig();
		
		tools.put("dupeTool",Material.STONE_AXE);
		//tools.put("paintTool", Material.SLIME_BALL);
		//tools.put("jackhamTool", Material.DIAMOND_PICKAXE);
		//tools.put("chainsawTool", Material.GOLD_AXE);
		//tools.put("shovelTool", Material.GOLD_SPADE);
		tools.put("scrollTool",Material.BONE);
		//tools.put("sledgeTool", Material.GOLD_PICKAXE);
		//tools.put("pliersTool", Material.GLOWSTONE_DUST);
		
		//Print yadp loaded message
		if(YADP.debug) {
			PluginDescriptionFile pdfFile = this.getDescription();
			log.info( "["+pdfFile.getName() + "] version " + pdfFile.getVersion() +
					" is enabled with debug" );
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		//Safety check for determining console status
		boolean console = true;
		if (sender instanceof Player) {
			console = false;
		}
		
		if(cmd.getName().equalsIgnoreCase("yadp")&&(args.length == 1)){
			if(args[0].contentEquals("reload")) {
				if(sender.hasPermission("yadp.reload")) {
					sender.sendMessage( "In the future this will reload the configuration file");
				} else {
					sender.sendMessage( "You don't have permission to reload the config file");
				}
				return true;
			} else if (args[0].contentEquals("tools")) {
				if (console) {
					sender.sendMessage("this command can only be run by a player");
				} else {
					if((sender.hasPermission("yadp.tool.dupe"))&&
							(YADP.tools.containsKey("dupeTool"))) {
						sender.sendMessage("Right-click with the "+YADP.tools.get("dupeTool")+
								" to duplicate the item selected");
					}
					if((sender.hasPermission("yadp.tool.scroll"))&&
							(YADP.tools.containsKey("scrollTool"))) {
						sender.sendMessage("Click with the "+YADP.tools.get("scrollTool")+
						" to change a blocks data value");
					}
				}
				return true;
			}
		}
		return false; 
	}

}
