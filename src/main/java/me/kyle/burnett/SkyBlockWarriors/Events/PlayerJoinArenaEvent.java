package me.kyle.burnett.SkyBlockWarriors.Events;

import me.kyle.burnett.SkyBlockWarriors.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event {
	
  private static final HandlerList handlers = new HandlerList();
  private Player player;
  private Game game;

  public PlayerJoinArenaEvent(Player p, Game g)
  {
    this.player = p;
    this.game = g;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Game getGame() {
    return this.game;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
