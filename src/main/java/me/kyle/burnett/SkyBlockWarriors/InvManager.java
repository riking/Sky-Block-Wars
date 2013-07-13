package me.kyle.burnett.SkyBlockWarriors;

import java.util.List;

import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;
import me.kyle.burnett.SkyBlockWarriors.Utils.InventoryUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InvManager {
	static InvManager instance = new InvManager();

	public InvManager getInstance(){
		return instance;
	}

	public static void saveInv(Player p){
		List<ItemStack> main = InventoryUtil.getInstance().getContentInventory(p.getInventory());
		List<ItemStack> armor = InventoryUtil.getInstance().getArmorInventory(p.getInventory());

		Main.getInstance().Inv.set(p.getName() +  ".Main", main);
		Main.getInstance().Inv.set(p.getName() + ".Armor", armor);

		ConfigManager.getInstance().saveYamls();
	}

	public static void restoreInv(Player p){
		List<?> main = Main.getInstance().Inv.getList(p.getName() + ".Main");
		List<?> armor = Main.getInstance().Inv.getList(p.getName() + ".Armor");

		p.getInventory().clear();
		if (main != null) {
			p.getInventory().setContents(main.toArray(new ItemStack[36]));
		}
		if (armor != null) {
			p.getInventory().setArmorContents(armor.toArray(new ItemStack[4]));
		}
	}
}
