package me.kyle.burnett.SkyBlockWarriors.Utils;

import org.bukkit.entity.Player;

import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;

public class ArenaCreate {

	public boolean createArena(Player p){

		if(!WorldEditUtility.getInstance().doesSelectionExist(p)){
			return false;
		}

		int amount = Main.getInstance().Arena.getInt("Amount");

		Main.getInstance().Arena.createSection(Integer.toString(amount));
		Main.getInstance().Arena.set(amount + ".Enabled", true);
		Main.getInstance().Arena.set(amount + ".BlockX", p.getLocation().getBlockX());
		Main.getInstance().Arena.set(amount + ".BlockY", p.getLocation().getBlockY());
		Main.getInstance().Arena.set(amount + ".BlockZ", p.getLocation().getBlockZ());
		Main.getInstance().Arena.set(amount + ".World", p.getWorld().getName());

		WorldEditUtility.getInstance().saveArena(p, amount);

		Main.getInstance().Arena.set("Amount", amount + 1);

		ConfigManager.getInstance().saveYamls();

		return true;
	}
	
	// Add to the Game class setup.
	
}
