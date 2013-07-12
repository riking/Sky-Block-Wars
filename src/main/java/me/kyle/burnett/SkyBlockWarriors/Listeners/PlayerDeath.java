package me.kyle.burnett.SkyBlockWarriors.Listeners;

import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Utils.InventoryUtil;
import net.minecraft.server.v1_6_R2.Packet205ClientCommand;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;

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
				
				Inventory main = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Main"));
				Inventory armor = InventoryUtil.getInstance().fromBase64(Main.getInstance().Inv.getString(p.getName() + ".Armor"));
				
				if(main != null){
					p.getInventory().setContents(main.getContents());

				}
				
				if(armor != null){
					p.getInventory().setArmorContents(armor.getContents());
				}
				
				p.sendMessage(ChatColor.RED + "You were killed by " + ChatColor.GOLD + e.getEntity().getLastDamage() + ChatColor.RED + ".");
				
				
				GameManager.getInstance().getPlayerGame(p).broadCastGame(ChatColor.GOLD +"Player " + GameManager.getInstance().getPlayerGame(p).getTeamColor(p) + p.getName() + ChatColor.GOLD + " has left the game.");

			}
		}
	}

}
