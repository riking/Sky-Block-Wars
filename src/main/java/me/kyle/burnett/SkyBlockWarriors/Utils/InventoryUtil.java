package me.kyle.burnett.SkyBlockWarriors.Utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {


	static InventoryUtil instance = new InventoryUtil();

	public static InventoryUtil getInstance(){
		return instance;
	}

	public List<ItemStack> getArmorInventory(PlayerInventory inventory) {
		return Arrays.asList(inventory.getArmorContents());
	}

	public List<ItemStack> getContentInventory(PlayerInventory inventory) {
		return Arrays.asList(inventory.getContents());
	}
}