package me.kyle.burnett.SkyBlockWarriors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;
import me.kyle.burnett.SkyBlockWarriors.Events.PlayerJoinArenaEvent;
import me.kyle.burnett.SkyBlockWarriors.Events.PlayerLeaveArenaEvent;
import me.kyle.burnett.SkyBlockWarriors.Utils.ChestFiller;
import me.kyle.burnett.SkyBlockWarriors.Utils.WorldEditUtility;
import net.minecraft.server.v1_6_R2.Packet54PlayNoteBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class Game {
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();
	Team BLUE = board.registerNewTeam("Blue Team");
	Team RED = board.registerNewTeam("Red Team");
	Team YELLOW = board.registerNewTeam("Yellow Team");
	Team GREEN = board.registerNewTeam("Green Team");
	public ArenaState state = ArenaState.LOADING;
	private List<String> unteamed = new ArrayList<String>();
	private List<String> players = new ArrayList<String>();
	private List<String> voted = new ArrayList<String>();
	private HashMap<String, Team> team = new HashMap<String, Team>();
	private List<String> editors = new ArrayList<String>();
	private int gameID;
	private int count;
	private int task;
	
	public Game(int gameID){
		
		this.gameID = gameID;
		this.task = gameID;
		prepareArena(false);
		
	}
	
	public Game(int gameID, boolean just){
		
		this.gameID = gameID;
		this.task = gameID;
		prepareArena(just);
		
	}
	
	public void prepareArena(boolean just){
		
		this.state = ArenaState.LOADING;
		this.voted.clear();
		this.players.clear();
		this.team.clear();
		
		if(!just){
		
			try {
				
				WorldEditUtility.getInstance().loadIslandSchematic(this.gameID);
				
			} catch (MaxChangedBlocksException e) {
				
				e.printStackTrace();
			} catch (DataException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
			ChestFiller.loadChests(this.gameID);
			
			this.state = ArenaState.WAITING;
			return;
		}
		
		this.state = ArenaState.IN_SETUP;
		
	}
	
	public List<String> getPlayers(){
		return players;
	}
	
	public int getGameID(){
		return this.gameID;
	}
	
	public Team getYellowTeam(){
		
		return this.YELLOW;
	}
	
	public Team getGreenTeam(){
		
		return this.GREEN;
	}
	
	public Team getBlueTeam(){
		
		return this.BLUE;
	}
	
	public Team getRedTeam(){
		
		return this.RED;
	}
	
	public List<String> getVoted(){
		
		return voted;
	}
	
	public void addVoted(Player p){
		this.voted.add(p.getName());
		checkStart();
	}
	
	public boolean hasVoted(Player p){
		if(this.voted.contains(p.getName())){
			return true;
		}
		
		return false;
	}
	
	public Team getPlayerTeam(Player p){
		
		Team team = this.team.get(p.getName());
		
		if(team == null){
			return null;
		}
		
		if(team.equals(this.RED)){
			return this.RED;
		
		}else if(team.equals(this.GREEN)){
			
			return this.GREEN;
			
		}else if(team.equals(this.BLUE)){
			
			return this.BLUE;
		
		}else if(team.equals(this.YELLOW)){
			
			return this.YELLOW;
		
		}else{
			return null;
		}
		
	}
	
	public void setTeamRed(Player p){
		
		this.removeFromTeam(p);
		this.team.put(p.getName(), this.RED);
		this.RED.addPlayer(p);
	}
	
	public void setTeamBlue(Player p){
		
		this.removeFromTeam(p);
		this.team.put(p.getName(), this.BLUE);
		this.RED.addPlayer(p);
	}
	
	public void setTeamGreen(Player p){
		
		this.removeFromTeam(p);
		this.team.put(p.getName(), this.GREEN);
		this.RED.addPlayer(p);
	}
	
	public void setTeamYellow(Player p){
		
		this.removeFromTeam(p);
		this.team.put(p.getName(), this.YELLOW);
		this.RED.addPlayer(p);
	}
	
	public void removeFromGame(Player p){
		
		PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(p, Game.this);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		this.players.remove(p.getName());
		
		this.removeFromTeam(p);
		
		this.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + ChatColor.GOLD + p.getName() + ChatColor.GREEN + "has left the arena.");
		
	}
	
	public void removeFromTeam(Player p){
		if(this.team.containsKey(p.getName())){
			this.team.get(p.getName()).removePlayer(p);
			this.team.remove(p.getName());
		}
	}
	
	public boolean isPlayerInTeam(Player p){
		if(this.team.containsKey(p.getName())){
			if(this.team.get(p.getName()) != null){
				return true;
			}
		}
		
		return false;
	}
	
	public ArenaState addPlayer(Player p){
		
		if(this.players.size() > Main.getInstance().Config.getInt("Max-People-In-A-Team") * 4){
			
			return ArenaState.FULL;
		
		}else if(this.state != ArenaState.WAITING){
			
			return this.state;
		
		}else if(this.state == ArenaState.WAITING){
			
			if(!((Main.getInstance().Config.getInt("Max-People-In-A-Team") * 4) == this.players.size())){
				
				this.players.add(p.getName());
				this.unteamed.add(p.getName());
				
				PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(p, Game.this);
				Bukkit.getServer().getPluginManager().callEvent(event);
				
				this.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + p.getDisplayName() + ChatColor.GREEN + " has joined the arena.");
				
				int startPlayers = Main.getInstance().Config.getInt("Auto-Start-Players");
				int max = Main.getInstance().Config.getInt("Max-People-In-A-Team") * 4;
				
				p.sendMessage(ChatColor.GREEN + "The game will automatically start when there are " + startPlayers + " players.");
				p.sendMessage(ChatColor.GREEN + "There are " + ChatColor.GOLD + this.players.size() + "/" + max + ChatColor.GREEN + " players in the game.");
				 
				this.checkStart();
			
			}else if((Main.getInstance().Config.getInt("Max-People-In-A-Team") * 4) == this.players.size()){
				
				p.sendMessage(ChatColor.RED + "That arena is full.");
			}
		}
		
		return ArenaState.OTHER_REASON;
	}
	
	public ArenaState getState(){
		return this.state;
	}
	
	public void setState(ArenaState state){
		this.state = state;
	}
	
	
	
	public void broadCastGame(String s){
		
		for(int x = 0; x < players.size(); x++){
			
			Player p = Bukkit.getServer().getPlayer(players.get(x));
			
			p.sendMessage(s);
			
		}
		
	}
	
	
	public void broadCastServer(String s){
		
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			
			p.sendMessage(s);
			
		}
		
	}
	
	public ChatColor getTeamColor(Player p){
		
		Team team = this.getPlayerTeam(p);
		
		if(team == null){
			return null;
		}
		
		if(team.equals(this.RED)){
			
			return ChatColor.RED;
		
		}else if(team.equals(this.GREEN)){
			
			return ChatColor.GREEN;
		
		}else if(team.equals(this.BLUE)){
			
			return ChatColor.BLUE;
		
		}if(team.equals(this.YELLOW)){
			
			return ChatColor.YELLOW;
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	public String getPlayersAsList(){
		
		ArrayList<String> playersColor = new ArrayList<String>();
		
		for(int x = 0; x < this.getPlayers().size(); x++){
			
			Player p = Bukkit.getServer().getPlayer(this.getPlayers().get(x));
			
			//Get the players add them to a list with there colors to send to the player.
		}
		
		return this.getPlayers().toString().replace("[", " ").replace("]", " ");
	}
	
	public void addChest(ChestType chest, Location loc){
		
		if(chest.equals(ChestType.SPAWN)){
			
			ArrayList<String> spawnChests = (ArrayList<String>) Main.getInstance().Chest.getStringList("Chest." + this.getGameID() + ".Spawn");
			
			int x, y, z;
			
			x = loc.getBlockX();
			
			y = loc.getBlockY();
			
			z = loc.getBlockZ();
			
			spawnChests.add(Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z));
			
			Main.getInstance().Chest.set("Chest." + this.getGameID() + ".Spawn", spawnChests);
			
			ConfigManager.getInstance().saveYamls();
			
		}else if(chest.equals(ChestType.SIDE)){
			
			ArrayList<String> spawnChests = (ArrayList<String>) Main.getInstance().Chest.getStringList("Chest." + this.getGameID() + ".Side");
			
			int x, y, z;
			
			x = loc.getBlockX();
			
			y = loc.getBlockY();
			
			z = loc.getBlockZ();
			
			spawnChests.add(Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z));
			
			Main.getInstance().Chest.set("Chest." + this.getGameID() + ".Side", spawnChests);
			
			ConfigManager.getInstance().saveYamls();
			
			
		}else if(chest.equals(ChestType.CENTER)){
			
			ArrayList<String> spawnChests = (ArrayList<String>) Main.getInstance().Chest.getStringList("Chest." + this.getGameID() + ".Center");
			
			int x, y, z;
			
			x = loc.getBlockX();
			
			y = loc.getBlockY();
			
			z = loc.getBlockZ();
			
			spawnChests.add(Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z));
			
			Main.getInstance().Chest.set("Chest." + this.getGameID() + ".Center", spawnChests);
			
			ConfigManager.getInstance().saveYamls();
			
		}
		
	}
	
	public List<String> getEditors(){
		
		return editors;
	}
	
	public int getEditorsSize(){
		
		return getEditors().size();
	}
	
	public void addEditor(Player p){
		editors.add(p.getName());
	}
	
	public void removeEditor(Player p){
		if(editors.contains(p.getName())){
			editors.remove(p.getName());
		}
	}
	
	public void addRedSpawn(Player p){
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Red.X", p.getLocation().getBlockX());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Red.Y", p.getLocation().getBlockY());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Red.Z", p.getLocation().getBlockZ());
		ConfigManager.getInstance().saveYamls();
	}
	
	public void addBlueSpawn(Player p){
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Blue.X", p.getLocation().getBlockX());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Blue.Y", p.getLocation().getBlockY());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Blue.Z", p.getLocation().getBlockZ());
		ConfigManager.getInstance().saveYamls();
	}
	
	public void addYellowSpawn(Player p){
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Yellow.X", p.getLocation().getBlockX());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Yellow.Y", p.getLocation().getBlockY());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Yellow.Z", p.getLocation().getBlockZ());
		ConfigManager.getInstance().saveYamls();
	}
	
	public void addGreenSpawn(Player p){
		
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Green.X", p.getLocation().getBlockX());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Green.Y", p.getLocation().getBlockY());
		Main.getInstance().Spawns.set("Spawn." + this.gameID + ".Green.Z", p.getLocation().getBlockZ());
		ConfigManager.getInstance().saveYamls();
	}
	
	public Location getSpawnRed(){
		
		World world = Bukkit.getServer().getWorld(Main.getInstance().Arena.getString("Arena." + this.gameID + ".World"));
		
		int x = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Red.X");
		int y = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Red.Y");
		int z = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Red.Z");
		
		return new Location(world,x,y,z);
	}
	
	public Location getSpawnBlue(){
		
		World world = Bukkit.getServer().getWorld(Main.getInstance().Arena.getString("Arena." + this.gameID + ".World"));
		
		int x = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Blue.X");
		int y = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Blue.Y");
		int z = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Blue.Z");
		
		return new Location(world,x,y,z);
	}
	
	public Location getSpawnYellow(){
		
		World world = Bukkit.getServer().getWorld(Main.getInstance().Arena.getString("Arena." + this.gameID + ".World"));
		
		int x = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Yellow.X");
		int y = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Yellow.Y");
		int z = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Yellow.Z");
		
		return new Location(world,x,y,z);
	}
	
	public Location getSpawnGreen(){
		
		World world = Bukkit.getServer().getWorld(Main.getInstance().Arena.getString("Arena." + this.gameID + ".World"));
		
		int x = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Green.X");
		int y = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Green.Y");
		int z = Main.getInstance().Spawns.getInt("Spawn." + this.gameID + ".Green.Z");
		
		return new Location(world,x,y,z);
	}
	
	public List<String> getUnteamed(){
		return this.unteamed;
	}
	
	public void playerSoundGame(){
		Packet54PlayNoteBlock packet = new Packet54PlayNoteBlock(1, 1, 1, 1, 1, 1);
		
		for(int x = 0; x < this.players.size(); x++){
			
			Player p = Bukkit.getServer().getPlayer(this.players.get(x));
		
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	public void checkStart(){
		
		if(getPlayers().size() >= Main.getInstance().Config.getInt("Auto-Start-Players")){
			this.setState(ArenaState.STARTING);
			this.countdown();
			
		}else if(this.voted.size() * 100 / this.players.size() > 60){
			
			this.setState(ArenaState.STARTING);
			this.countdown();
		}
		
	}
	
	public void start(){
		
		 Bukkit.getServer().getScheduler().cancelTask(Game.this.task);
		
		this.setState(ArenaState.INGAME);
		
		this.addPlayersToTeams();
		
		this.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + ChatColor.GREEN + "GO!");
		
		this.teleportPlayers();
		
	}
	
	public void addPlayersToTeams(){
		
		for(int y = 0; y < players.size(); y++){

			Team team = null;
			
			int red = this.RED.getPlayers().size();
			int blue = this.BLUE.getPlayers().size();
			int yellow = this.YELLOW.getPlayers().size();
			int green = this.GREEN.getPlayers().size();

			if (red < blue && red < yellow && red < green) {
				team = this.RED;
			}

			else if (blue < red && blue < yellow && blue < green) {
				team = this.BLUE;
			}

			else if (yellow < blue && yellow < red && yellow < green) {
				team = this.YELLOW;
			}

			else if (green < red && green < yellow && green < blue) {
				team = this.GREEN;

			}

			else if (red == blue && red == green && red == yellow) {
				team = chooseTeam();
			}

			else if (red == blue && red < green && red < yellow) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.BLUE;
				}
				team = this.RED;
			}

			else if (red == green && red < blue && red < yellow) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.GREEN;
				}
				team = this.RED;
			}

			else if (red == yellow && red < blue && red < green) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.YELLOW;
				}
				team = this.RED;
			}

			else if (blue == green && blue < red && blue < yellow) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.GREEN;
				}
				team = this.RED;
			}

			else if (blue == yellow && blue < red && blue < green) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.GREEN;
				}
				team = this.RED;
			}

			else if (yellow == green && yellow < red && yellow < blue) {

				Random r = new Random();
				int x = r.nextInt(1);

				if (x == 1) {
					team = this.GREEN;
				}
				team = this.RED;
			}

			else if (blue == green && green == red && red < yellow) {

				Random r = new Random();
				int x = r.nextInt(2);

				if (x == 0) {
					team = this.GREEN;
				} else if (x == 1) {
					team = this.RED;
				} else if (x == 2) {
					team = this.BLUE;
				}
			}

			else if (blue == green && green == yellow && yellow < red) {

				Random r = new Random();
				int x = r.nextInt(2);

				if (x == 0) {
					team = this.YELLOW;
				} else if (x == 1) {
					team = this.GREEN;
				} else if (x == 2) {
					team = this.BLUE;
				}
			}

			else if (blue == red && red == yellow && yellow < green) {

				Random r = new Random();
				int x = r.nextInt(2);

				if (x == 0) {
					team = this.YELLOW;
				} else if (x == 1) {
					team = this.RED;
				} else if (x == 2) {
					team = this.BLUE;
				}
			}

			else if (green == red && red == yellow && yellow < blue) {

				Random r = new Random();
				int x = r.nextInt(2);

				if (x == 0) {
					team = this.YELLOW;
				} else if (x == 1) {
					team = this.RED;
				} else if (x == 2) {
					team = this.GREEN;
				}
			}	
			
			team.addPlayer(Bukkit.getServer().getPlayer(players.get(y)));
			this.unteamed.remove(players.get(y));
		}
	}
	
	public Team chooseTeam(){
		
		Random r = new Random();
		
		int x = r.nextInt(4) + 1;
		
		Team team = null;
		
		switch (x){
		
		case 1:
			team = this.RED;
			break;
		case 2:
			team = this.GREEN;
			break;
		case 3:
			team = this.YELLOW;
			break;
		case 4:
			team = this.BLUE;
			break;
		default: team = this.RED;
			break;
		}
		return team;
	}
	
	public void endGame(){
		//Announce winner or reason of end.
		//Remove players.
		//Register stats.
		//Unregister teams or clear them.
	}
	
	public boolean isRedAvailable(){
		
		int red = this.RED.getPlayers().size();
		int blue = this.BLUE.getPlayers().size();
		int yellow = this.YELLOW.getPlayers().size();
		int green = this.GREEN.getPlayers().size();

		if(red >= Main.getInstance().Config.getInt("Max-People-In-A-Team")){
			return false;
		}
		
		if (red < blue && red < yellow && red < green) {
			return true;
		}

		else if (red == blue && red == green && red == yellow) {
			return true;
		}

		else if (red == blue && red < green && red < yellow) {
			return true;
		}

		else if (red == green && red < blue && red < yellow) {
			return true;
		}

		else if (red == yellow && red < blue && red < green) {
			return true;
		}

		else if (blue == green && green == red && red < yellow) {
			return true;
		}

		else if (blue == red && red == yellow && yellow < green) {
			return true;
		}

		else if (green == red && red == yellow && yellow < blue) {
			return true;
		}
		else{
			return false;
		}	
	}
	
	public boolean isGreenAvailable(){
		
		int red = this.RED.getPlayers().size();
		int blue = this.BLUE.getPlayers().size();
		int yellow = this.YELLOW.getPlayers().size();
		int green = this.GREEN.getPlayers().size();

		if(green >= Main.getInstance().Config.getInt("Max-People-In-A-Team")){
			return false;
		}
		
		if (green < blue && green < yellow && green < red) {
			return true;
		}

		else if (green == blue && green == red && green == yellow) {
			return true;
		}

		else if (green == blue && green < red && green < yellow) {
			return true;
		}

		else if (green == red && green < blue && green < yellow) {
			return true;
		}

		else if (green == yellow && green < blue && green < red) {
			return true;
		}

		else if (blue == green && green == red && red < yellow) {
			return true;
		}

		else if (blue == green && green == yellow && yellow < red) {
			return true;
		}

		else if (green == red && red == yellow && yellow < blue) {
			return true;
		}
		else{
			return false;
		}	
	}
	
	public boolean isBlueAvailable(){
		
		int red = this.RED.getPlayers().size();
		int blue = this.BLUE.getPlayers().size();
		int yellow = this.YELLOW.getPlayers().size();
		int green = this.GREEN.getPlayers().size();

		if(blue >= Main.getInstance().Config.getInt("Max-People-In-A-Team")){
			return false;
		}
		
		if (blue < green && blue < yellow && blue < red) {
			return true;
		}

		else if (blue == green && blue == red && blue == yellow) {
			return true;
		}

		else if (blue == green && blue < red && blue < yellow) {
			return true;
		}

		else if (blue == red && blue < green && blue < yellow) {
			return true;
		}

		else if (blue == yellow && blue < green && blue < red) {
			return true;
		}

		else if (blue == green && green == red && red < yellow) {
			return true;
		}

		else if (blue == green && green == yellow && yellow < red) {
			return true;
		}

		else if (blue == red && red == yellow && yellow < green) {
			return true;
		}
		else{
			return false;
		}	
	}
	
	public boolean isYellowAvailable(){
		
		int red = this.RED.getPlayers().size();
		int blue = this.BLUE.getPlayers().size();
		int yellow = this.YELLOW.getPlayers().size();
		int green = this.GREEN.getPlayers().size();

		if(yellow >= Main.getInstance().Config.getInt("Max-People-In-A-Team")){
			return false;
		}
		
		if (yellow < green && yellow < blue && yellow < red) {
			return true;
		}

		else if (yellow == green && yellow == red && yellow == blue) {
			return true;
		}

		else if (yellow == green && yellow < red && yellow < blue) {
			return true;
		}

		else if (yellow == red && yellow < green && yellow < blue) {
			return true;
		}

		else if (yellow == blue && yellow < green && yellow < red) {
			return true;
		}

		else if (yellow == green && green == red && red < yellow) {
			return true;
		}

		else if (yellow == red && red == blue && yellow < green) {
			return true;
		}

		else if (yellow == blue && blue == green && yellow < red) {
			return true;
		}
		else{
			return false;
		}	
	}
	
	public void teleportPlayers(){
		
		FileConfiguration a = Main.getInstance().Arena;
		FileConfiguration s = Main.getInstance().Spawns;
		
		World world = Bukkit.getServer().getWorld(a.getString("Arena." + this.gameID + ".World"));
		Location red = new Location(world, s.getDouble("Spawn." + this.gameID + ".Red.X"),s.getDouble("Spawn." + this.gameID + ".Red.Y"),s.getDouble("Spawn." + this.gameID + ".Red.Z") );
		Location green = new Location(world, s.getDouble("Spawn." + this.gameID + ".Green.X"),s.getDouble("Spawn." + this.gameID + ".Green.Y"),s.getDouble("Spawn." + this.gameID + ".Green.Z") );
		Location blue = new Location(world, s.getDouble("Spawn." + this.gameID + ".Blue.X"),s.getDouble("Spawn." + this.gameID + ".Blue.Y"),s.getDouble("Spawn." + this.gameID + ".Blue.Z") );
		Location yellow = new Location(world, s.getDouble("Spawn." + this.gameID + ".Yellow.X"),s.getDouble("Spawn." + this.gameID + ".Yellow.Y"),s.getDouble("Spawn." + this.gameID + ".Yellow.Z") );

		for(OfflinePlayer p : this.RED.getPlayers()){
			p.getPlayer().teleport(red);
		}
		
		for(OfflinePlayer p : this.GREEN.getPlayers()){
			p.getPlayer().teleport(green);
		}
		
		for(OfflinePlayer p : this.BLUE.getPlayers()){
			p.getPlayer().teleport(blue);
		}
		
		for(OfflinePlayer p : this.YELLOW.getPlayers()){
			p.getPlayer().teleport(yellow);
		}
	}
	

	  public void countdown(){
		  
	    this.count = Main.getInstance().Config.getInt("Auto-Start-Time");

	    if((this.state == ArenaState.WAITING) || (this.state == ArenaState.STARTING)) {
	    	
	      this.state = ArenaState.STARTING;
	      
	      this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
	        public void run() {
	          if (Game.this.count > 0) {
	        	  
	            if (Game.this.count % 10 == 0) {
	              Game.this.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + ChatColor.GREEN + "Starting in " + ChatColor.GOLD + count + ChatColor.GREEN + ".");
	            }
	            if (Game.this.count < 6) {
		              Game.this.broadCastGame(ChatColor.GOLD + "[" + ChatColor.BLUE + "SB" + ChatColor.GOLD + "]" + ChatColor.GREEN + "Starting in " + ChatColor.GOLD + count + ChatColor.GREEN + ".");
		              Game.this.playerSoundGame();
	            }

	            Game.this.count -= 1;
	          } else {
	            Game.this.start();
	          }
	        }
	      }
	      , 0L, 20L);
	    }
	  }
}