package me.kyle.burnett.SkyBlockWarriors.Listeners;

import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.InvManager;
import me.kyle.burnett.SkyBlockWarriors.Main;
import net.minecraft.server.v1_6_R2.Packet205ClientCommand;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener{

	@EventHandler
	public void onDeath(PlayerDeathEvent e){

		Entity ent = e.getEntity();

		if(ent instanceof Player){

			Player p = (Player) ent;


			if(GameManager.getInstance().isPlayerInGame(p)){

				Packet205ClientCommand packet = new Packet205ClientCommand();
				packet.a = 1;

				((CraftPlayer) e.getEntity()).getHandle().playerConnection.sendPacket(packet);

				p.teleport(Main.getInstance().getLobby());

				GameManager.getInstance().leaveGame(p);

				InvManager.restoreInv(p);

				p.sendMessage(ChatColor.RED + "You were killed by " + ChatColor.GOLD + e.getEntity().getLastDamage() + ChatColor.RED + ".");


				GameManager.getInstance().getPlayerGame(p).broadCastGame(ChatColor.GOLD +"Player " + GameManager.getInstance().getPlayerGame(p).getTeamColor(p) + p.getName() + ChatColor.GOLD + " has left the game.");

			}
		}
	}

}
