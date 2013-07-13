package me.kyle.burnett.SkyBlockWarriors.Listeners;

import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.InvManager;
import me.kyle.burnett.SkyBlockWarriors.Main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener{
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();

		if(GameManager.getInstance().isPlayerInGame(p)){

			p.teleport(Main.getInstance().getLobby());
			GameManager.getInstance().leaveGame(p);
			InvManager.restoreInv(p);

			GameManager.getInstance().getPlayerGame(p).broadCastGame(ChatColor.GOLD +"Player " + GameManager.getInstance().getPlayerGame(p).getTeamColor(p) + p.getName() + ChatColor.GOLD + " has left the game.");
		}
	}
}
