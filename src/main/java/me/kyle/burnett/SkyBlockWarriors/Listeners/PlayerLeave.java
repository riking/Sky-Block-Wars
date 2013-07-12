package me.kyle.burnett.SkyBlockWarriors.Listeners;

import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Utils.InventoryUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class PlayerLeave implements Listener{
	
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		
		Player p = e.getPlayer();
		
		if(GameManager.getInstance().isPlayerInGame(p)){
			
			p.teleport(Main.getInstance().getLobby());
						
			GameManager.getInstance().leaveGame(p);
			
			Inventory main = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Main"));
			Inventory armor = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Armor"));
			
			p.getInventory().clear();
			
			if(main != null){
				p.getInventory().setContents(main.getContents());

			}
			
			if(armor != null){
				p.getInventory().setArmorContents(armor.getContents());
			}
		
			GameManager.getInstance().getPlayerGame(p).broadCastGame(ChatColor.GOLD +"Player " + GameManager.getInstance().getPlayerGame(p).getTeamColor(p) + p.getName() + ChatColor.GOLD + " has left the game.");
		}
		
	}
}
