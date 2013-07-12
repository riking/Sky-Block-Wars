package me.kyle.burnett.SkyBlockWarriors.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kyle.burnett.SkyBlockWarriors.Main;

public class ChestFiller {
	
	public static Main plugin;
	
	public ChestFiller(Main instance){
		plugin = instance;
	}
	
	static World w = null;
	
	public static void loadChests(Integer arena){
		
		w = Bukkit.getWorld(Main.getInstance().Arena.getString("Arena." + arena + ".World"));
		
		loadSpawn(arena);
		loadSide(arena);
		loadCenter(arena);
	}
	
	private static void loadSpawn(Integer arena){
		
		List<String> chestLocSpawn = Main.getInstance().Chest.getStringList("Chest." + arena + ".Spawn");
		List<String> chestSpawnItems = Main.getInstance().Config.getStringList("Chests.Spawn-Chests.ItemID/Amount");

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ArrayList<Block> chests = new ArrayList<Block>();
		
		for(int x = 0; x < chestSpawnItems.size(); x++){
			
			items.add(getItem(chestSpawnItems.get(x)));
			
		}
		
		for(int x = 0; x < chestLocSpawn.size(); x++){
			
			Block b = w.getBlockAt(getX(chestLocSpawn.get(x)), getY(chestLocSpawn.get(x)), getZ(chestLocSpawn.get(x)));

			if(b.getType().equals(Material.CHEST)){
				chests.add(b);
				
			}
		}

		for(int x = 0; x < chests.size(); x++){
			
			Chest c = (Chest)chests.get(x).getState();
			
			Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.CHEST);
			
			c.getInventory().clear();
			
			for(int y = 0; y < items.size(); y++){
				
				inv.addItem(items.get(y));
			}
			
			c.getInventory().setContents(inv.getContents());
			
		}
	}
	
	private static void loadSide(Integer arena){
		
		List<String> chestLocSide = Main.getInstance().Chest.getStringList("Chest." + arena + ".Side");
		List<String> chestSideItems = Main.getInstance().Config.getStringList("Chests.Side-Chests.ItemID/Amount");

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ArrayList<Block> chests = new ArrayList<Block>();
		
		for(int x = 0; x < chestSideItems.size(); x++){
			
			items.add(getItem(chestSideItems.get(x)));
		}
		
		for(int x = 0; x < chestLocSide.size(); x++){
			Block b = w.getBlockAt(getX(chestLocSide.get(x)), getY(chestLocSide.get(x)), getZ(chestLocSide.get(x)));

			if(b.getType().equals(Material.CHEST)){
				chests.add(b);
			}
		}
		
		for(int x = 0; x < chests.size(); x++){
			Chest c = (Chest)chests.get(x).getState();
			
			Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.CHEST);
			
			c.getInventory().clear();
			
			for(int y = 0; y < items.size(); y++){
				
				inv.addItem(items.get(y));
			}
			
			c.getInventory().setContents(inv.getContents());
		}
		
	}
	
	private static void loadCenter(Integer arena){
		
		List<String> chestLocCenter = Main.getInstance().Chest.getStringList("Chest." + arena + ".Center");
		List<String> chestCenterItems = Main.getInstance().Config.getStringList("Chests.Middle-Chest.ItemID/Amount");
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ArrayList<Block> chests = new ArrayList<Block>();
		
		for(int x = 0; x < chestCenterItems.size(); x++){
			
			items.add(getItem(chestCenterItems.get(x)));
		}
		
		for(int x = 0; x < chestLocCenter.size(); x++){
			Block b = w.getBlockAt(getX(chestLocCenter.get(x)), getY(chestLocCenter.get(x)), getZ(chestLocCenter.get(x)));

			if(b.getType().equals(Material.CHEST)){
				chests.add(b);
			}
		}
		
		for(int x = 0; x < chests.size(); x++){
			Chest c = (Chest)chests.get(x).getState();
			
			Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.CHEST);
			
			c.getInventory().clear();
			
			for(int y = 0; y < items.size(); y++){
				
				inv.addItem(items.get(y));
			}
			
			c.getInventory().setContents(inv.getContents());
		}
		
		
	}
	
	
	private static ItemStack getItem(String string) {

		String[] split = string.split(",");

		for (int x = 0; x < split.length; x++) {
			split[x] = split[x].toLowerCase().trim();
		}
		if (split.length < 1)
			return null;
		if (split.length == 1)
			return new ItemStack(Integer.parseInt(split[0]));
		if (split.length == 2)
			return new ItemStack(Integer.parseInt(split[0]),
					Integer.parseInt(split[1]));
		if (split.length == 3) {
			return new ItemStack(Integer.parseInt(split[0]),
					Integer.parseInt(split[1]), Short.parseShort(split[2]));
		}

		return null;
	}
	
	private static int getX(String s){
		
		String[] split = s.split(",");
		
		int x = Integer.parseInt(split[0]);
		
		return x;
		
	}
	
	private static int getY(String s){
		
		String[] split = s.split(",");
		
		int x = Integer.parseInt(split[1]);
		
		return x;
		
	}
	
	private static int getZ(String s){
		
		String[] split = s.split(",");
		
		int x = Integer.parseInt(split[2]);
		
		return x;
		
	}
    
}
