package me.kyle.burnett.SkyBlockWarriors;

import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;
import me.kyle.burnett.SkyBlockWarriors.Utils.InventoryUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvManager {
	
	static InvManager instance = new InvManager();
	
	public InvManager getInstance(){
		return instance;
	}
	
	public void saveInv(Player p){
		
		Inventory main = InventoryUtil.getInstance().getContentInventory(p.getInventory());
		Inventory armor = InventoryUtil.getInstance().getArmorInventory(p.getInventory());
		
		Main.getInstance().Inv.set(p.getName() +  ".Main", InventoryUtil.getInstance().toBase64(main));
		Main.getInstance().Inv.set(p.getName() + ".Armor", InventoryUtil.getInstance().toBase64(armor));
		
		ConfigManager.getInstance().saveYamls();
	}
	
	public void restoreInv(Player p){
		
		Inventory inv = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Main"));
		Inventory armor = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Armor"));

		p.getInventory().clear();
		p.getInventory().setContents(inv.getContents());
		p.getInventory().setArmorContents(armor.getContents());
		
	}

}
