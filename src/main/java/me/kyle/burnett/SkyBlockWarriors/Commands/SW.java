package me.kyle.burnett.SkyBlockWarriors.Commands;

import java.io.IOException;

import me.kyle.burnett.SkyBlockWarriors.ArenaState;
import me.kyle.burnett.SkyBlockWarriors.ChestType;
import me.kyle.burnett.SkyBlockWarriors.Game;
import me.kyle.burnett.SkyBlockWarriors.GameManager;
import me.kyle.burnett.SkyBlockWarriors.Main;
import me.kyle.burnett.SkyBlockWarriors.Events.PlayerLeaveArenaEvent;
import me.kyle.burnett.SkyBlockWarriors.Utils.WorldEditUtility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class SW implements CommandExecutor{

	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("sw") || cmd.getName().equalsIgnoreCase("skyblockw")){
			
			Player p = (Player) sender;
			
			GameManager gm = GameManager.getInstance();
			
			if(args.length == 0){
				
				p.sendMessage(ChatColor.GOLD + "----------" + ChatColor.GREEN +"Sky Block War's Command's" + ChatColor.GOLD  +"----------");
				p.sendMessage(ChatColor.GOLD + "/sw" + ChatColor.GREEN +"Show's this help screen.");
				p.sendMessage(ChatColor.GOLD + "/sw join [arena number]" + ChatColor.GREEN +"Join's an arena if specified or goes to the lobby.");
				p.sendMessage(ChatColor.GOLD + "/sw leave" + ChatColor.GREEN +"Leave's the arena and teleports you to the lobby.");
				
				return true;
			}
			
			if(args.length == 1){
				
				if(args[0].equalsIgnoreCase("join")){
					
					if(Main.getInstance().teleportToLobby(p)){
						
						p.sendMessage(ChatColor.GREEN + "Arena not specifed teleporting to lobby.");
					
					} else if(!Main.getInstance().teleportToLobby(p)){
						
						p.sendMessage(ChatColor.RED + "Tryed to teleport to lobby but it was not found. Please tell server staff.");
					}
					
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("leave")){
					
					if(gm.isPlayerInGame(p)){
						
						Game game = gm.getPlayerGame(p);
						
						PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(p, gm.getPlayerGame(p));
						
						Bukkit.getServer().getPluginManager().callEvent(event);
					
						gm.leaveGame(p);
						
						p.sendMessage(ChatColor.GREEN + "You have left the arena.");
												
						game.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + ChatColor.GREEN +"Player " + p.getDisplayName() + ChatColor.GREEN + " has left the game.");
						
					}else if(!gm.isPlayerInGame(p)){
						
						p.sendMessage(ChatColor.RED + "You are not in a game.");
					}
					
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("list")){
						
					if(gm.isPlayerInGame(p)){
						
						gm.getPlayerGame(p).getPlayersAsList();
					
					}else if(!gm.isPlayerInGame(p)){
						p.sendMessage(ChatColor.RED + "You are not in a game.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("listgames")){
					
					String arenas = gm.listGames();
					
					p.sendMessage(ChatColor.GOLD + "Arena List:");
					p.sendMessage(ChatColor.GRAY + arenas);
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("create")){
					
					if(WorldEditUtility.getInstance().doesSelectionExist(p)){
						
						int arena = gm.createGame(p);
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + arena + ChatColor.GREEN + " created. Once you have edited it use '" + ChatColor.GOLD + "/sw activate arena" + ChatColor.GREEN + "' to allow people to join.");
						
					}else if(!WorldEditUtility.getInstance().doesSelectionExist(p)){
						p.sendMessage(ChatColor.RED + "Please make a selection of the arena first.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("confirm")){
					
					if(gm.getConfirming().containsKey(p.getName())){
						
						gm.overrideArena(p, gm.getConfirming().get(p.getName()));
						
					}else if(!gm.getConfirming().containsKey(p.getName())){
						
						p.sendMessage(ChatColor.RED + "You are not waiting to confirm anything.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("setlobby")){
					
					Main.getInstance().setLobby(p);
					p.sendMessage(ChatColor.GREEN + "Lobby set succesfully.");
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("finish")){
					
					if(gm.isEditing(p)){
						
						p.sendMessage(ChatColor.GREEN + "Finished editing arena " + ChatColor.GOLD + gm.getEditing().get(p.getName()) + ChatColor.GREEN + ".");
						WorldEditUtility.getInstance().resaveArena(gm.getPlayerEditing(p));
						gm.removeEditor(p);
						
					}else if(!gm.isEditing(p)){
						
						p.sendMessage(ChatColor.RED + "You are not editing an arena.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("vote")){
					
					if(gm.isPlayerInGame(p)){
						
						if(!gm.getPlayerGame(p).hasVoted(p)){
						
							if(gm.getPlayerGame(p).getState() == ArenaState.WAITING){
								
								gm.getPlayerGame(p).addVoted(p);
								p.sendMessage(ChatColor.GREEN + "Voted!");
								gm.getPlayerGame(p).broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + p.getDisplayName() +  ChatColor.GREEN + " has voted to start.");
							}
						}else if(gm.getPlayerGame(p).hasVoted(p)){
							
							p.sendMessage(ChatColor.RED + "You have already voted.");
						}
						
					}else if(!gm.isPlayerInGame(p)){
						p.sendMessage(ChatColor.RED + "You are not in an arena.");
					}
					return true;
				}
				
				p.sendMessage(ChatColor.RED + "Unknown command. Use '/sw help' for a list of commands.");
				
				return true;
			}
			
			if(args.length == 2){
				
				if(args[0].equalsIgnoreCase("setspawn")){
					
					if(gm.isEditing(p)){
					
						if(args[1].equalsIgnoreCase("red")){
							
							Game g = gm.getGameEditing(p);
							
							g.addRedSpawn(p);
							
							p.sendMessage(ChatColor.RED  + "Red " + ChatColor.GREEN + "team spawn set for arena " + ChatColor.GOLD + g.getGameID() + ChatColor.GREEN + ".");
							
						}else if(args[1].equalsIgnoreCase("blue")){
							
							Game g = gm.getGameEditing(p);
							
							g.addBlueSpawn(p);
							
							p.sendMessage(ChatColor.BLUE  + "Blue " + ChatColor.GREEN + "team spawn set for arena " + ChatColor.GOLD + g.getGameID() + ChatColor.GREEN + ".");
							
						}else if(args[1].equalsIgnoreCase("green")){
							
							Game g = gm.getGameEditing(p);
							
							g.addGreenSpawn(p);
							
							p.sendMessage(ChatColor.GREEN  + "Green team spawn set for arena " + ChatColor.GOLD + g.getGameID() + ChatColor.GREEN + ".");
							
						}else if(args[1].equalsIgnoreCase("yellow")){
							
							Game g = gm.getGameEditing(p);
							
							g.addYellowSpawn(p);
							
							p.sendMessage(ChatColor.YELLOW  + "Yellow " + ChatColor.GREEN + "team spawn set for arena " + ChatColor.GOLD + g.getGameID() + ChatColor.GREEN + ".");
						
						}
					}else if(!gm.isEditing(p)){
						
						p.sendMessage(ChatColor.RED +"You are not editing an arena.");
					}
					return true;
				}
					
				else if(args[0].equalsIgnoreCase("create")){
					
					if(gm.isInteger(args[1])){
						
						int id = Integer.parseInt(args[1]);
						
						if(!(id > gm.getArenaAmount())){
							
							p.sendMessage(ChatColor.RED + "You are away to override a previous arena. Do '/sw confirm' to confirm this action.");
							
							gm.getConfirming().put(p.getName(), id);
							
						
						}else if(id > gm.getArenaAmount()){
							
							p.sendMessage(ChatColor.RED + "That number is bigger than your amount of arenas. Use '/sw create' to add arenas.");
						}
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("join")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						if(gm.isActive(Integer.parseInt(args[1]))){
							
							Game game = gm.getGameByID(Integer.parseInt(args[1]));
							
							if(game.state.equals(ArenaState.WAITING)){
								
								game.addPlayer(p);
								gm.setPlayerGame(p, game);
																								
							}else if(game.state != ArenaState.WAITING){
								
								p.sendMessage(ChatColor.RED + "Can not join the arena because it is " + game.getState().toString() + ".");
							}
						}else if(!gm.isActive(Integer.parseInt(args[1]))){
							p.sendMessage(ChatColor.RED + "That arena is disabled.");
						}
	
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That game does not exist.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("edit")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						gm.addEditor(p, Integer.parseInt(args[1]));
						
						gm.getGameByID(Integer.parseInt(args[1])).setState(ArenaState.GETTING_EDITED);
						
						p.sendMessage(ChatColor.GREEN + "Now editing arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("addchest")){
					
					if(gm.isEditing(p)){
						
						if(WorldEditUtility.getInstance().doesSelectionExist(p)){
						
							if(WorldEditUtility.getInstance().isChest(p)){
								
								Location loc = WorldEditUtility.getInstance().getChestLocation(p);
								
								if(loc.equals(null)){
									
									p.sendMessage(ChatColor.RED + "An error occured. Please try again.");
									
									return true;
								}
							
								if(args[1].equalsIgnoreCase("side")){
									
									gm.getGameEditing(p).addChest(ChestType.SIDE, loc);
								
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "side " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}else if(args[1].equalsIgnoreCase("center")){
									
									gm.getGameEditing(p).addChest(ChestType.CENTER, loc);
									
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "center " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}else if(args[1].equalsIgnoreCase("spawn")){
								
									gm.getGameEditing(p).addChest(ChestType.SPAWN, loc);
									
									p.sendMessage(ChatColor.GREEN + "You have added a " + ChatColor.GOLD + "spawn " + ChatColor.GREEN + "chest to arena " + ChatColor.GOLD + gm.getPlayerEditing(p) + ChatColor.GREEN + ".");
									
								}
								
							}else if(!WorldEditUtility.getInstance().isChest(p)){
								
								p.sendMessage(ChatColor.RED + "Your selection is either more than one block or is not a chest.");
							}
							
						}else if(!WorldEditUtility.getInstance().doesSelectionExist(p)){
							
							p.sendMessage(ChatColor.RED + "You do not have a selection of a chest.");
						}
						
					}else if(!gm.isEditing(p)){
						p.sendMessage(ChatColor.RED  + "You are not editing an arena.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("enable")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						if(!gm.isEnabled(Integer.parseInt(args[1]))){
							
							gm.enableGame(Integer.parseInt(args[1]));
							
							p.sendMessage(ChatColor.GREEN + "You enabled arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
							
						}else if(gm.isEnabled(Integer.parseInt(args[1]))){
							
							p.sendMessage(ChatColor.RED + "Arena is already enabled.");
						}
					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED +"That is not an arena.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("disable")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						if(gm.isEnabled(Integer.parseInt(args[1]))){
							
							gm.disableGame(Integer.parseInt(args[1]));
							
							p.sendMessage(ChatColor.GREEN + "You disabled arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + ".");
							
						}else if(!gm.isEnabled(Integer.parseInt(args[1]))){
							
							p.sendMessage(ChatColor.RED + "Arena is already disabled.");
						}
					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED +"That is not an arena.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("activate")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						gm.activate(Integer.parseInt(args[1]));
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been activated.");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					return true;
				}
				
				if(args[0].equalsIgnoreCase("deactivate")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						gm.deactivate(Integer.parseInt(args[1]));
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been deactivated.");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("save")){
	
					if(gm.checkGameByID(Integer.parseInt(args[1]))){	
						
						WorldEditUtility.getInstance().resaveArena(Integer.parseInt(args[1]));
						
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been saved.");

					
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("load")){
					
					if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						try {
							
							WorldEditUtility.getInstance().loadIslandSchematic(Integer.parseInt(args[1]));
						
						} catch (NumberFormatException| MaxChangedBlocksException | DataException| IOException e) {
							
							e.printStackTrace();
						}
						
						p.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been loaded.");
						
					}else if(gm.checkGameByID(Integer.parseInt(args[1]))){
						
						p.sendMessage(ChatColor.RED + "That arena does not exist.");
					}
					return true;
				}
				
				else if(args[0].equalsIgnoreCase("team")){
					
					if(gm.isPlayerInGame(p)){
						
						Game g = gm.getPlayerGame(p);
						
						if(g.getState().equals(ArenaState.WAITING)){
							
							if(args[1].equalsIgnoreCase("red")){
								
								if(g.isRedAvailable()){
									
									if(g.isPlayerInTeam(p)){
										g.removeFromTeam(p);
									}
									
									g.setTeamRed(p);
									p.sendMessage(ChatColor.GREEN + "Joined team " + ChatColor.RED + "red" + ChatColor.GREEN + ".");
								
								}else if(!g.isRedAvailable()){
									p.sendMessage(ChatColor.RED + "You can not join red, try joining another team or waiting for the teams to even out.");
								}
							
							}else if(args[1].equalsIgnoreCase("blue")){
								
								if(g.isBlueAvailable()){
									
									if(g.isPlayerInTeam(p)){
										g.removeFromTeam(p);
									}
									
									g.setTeamRed(p);
									p.sendMessage(ChatColor.GREEN + "Joined team " + ChatColor.BLUE + "blue" + ChatColor.GREEN + ".");
									
								}else if(!g.isBlueAvailable()){
									p.sendMessage(ChatColor.RED + "You can not join" + ChatColor.BLUE +" blue" + ChatColor.RED + ", try joining another team or waiting for the teams to even out.");
								}
							
							}else if(args[1].equalsIgnoreCase("green")){
								
								if(g.isGreenAvailable()){
									
									if(g.isPlayerInTeam(p)){
										g.removeFromTeam(p);
									}
									
									g.setTeamRed(p);
									p.sendMessage(ChatColor.GREEN + "Joined team green.");
									
								}else if(!g.isGreenAvailable()){
									p.sendMessage(ChatColor.RED + "You can not join" + ChatColor.GREEN +" green" + ChatColor.RED + ", try joining another team or waiting for the teams to even out.");
								}
							
							}else if(args[1].equalsIgnoreCase("yellow")){
								
								if(g.isYellowAvailable()){
									
									if(g.isPlayerInTeam(p)){
										g.removeFromTeam(p);
									}
									
									g.setTeamRed(p);
									p.sendMessage(ChatColor.GREEN + "Joined team " + ChatColor.YELLOW + "yellow" + ChatColor.GREEN + ".");
									
								}else if(!g.isYellowAvailable()){
									p.sendMessage(ChatColor.RED + "You can not join" + ChatColor.YELLOW +" yellow" + ChatColor.RED + ", try joining another team or waiting for the teams to even out.");
								}
							}
						}else if(!g.getState().equals(ArenaState.WAITING)){
							p.sendMessage(ChatColor.RED + "You can not change team when the game is starting or has already started.");
						}
						
					}else if(!gm.isPlayerInGame(p)){
						p.sendMessage(ChatColor.RED + "You are not in a game.");
					}
					return true;
				}
				p.sendMessage(ChatColor.RED + "Unknown command. Use '/sw help' for a list of commands.");
				return true;
			}
			
			if(args.length >= 3){
				p.sendMessage(ChatColor.RED + "Unknown command. Use '/sw help' for a list of commands.");
				return true;
			}
			
			return true;
		}
		return false;
	}
	
}
