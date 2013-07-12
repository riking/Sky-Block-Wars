package me.kyle.burnett.SkyBlockWarriors;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.kyle.burnett.SkyBlockWarriors.Commands.SW;
import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;
import me.kyle.burnett.SkyBlockWarriors.Listeners.PlayerDeath;
import me.kyle.burnett.SkyBlockWarriors.Listeners.PlayerLeave;
import me.kyle.burnett.SkyBlockWarriors.Utils.WorldEditUtility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin{
	
	private static Main instance;

	public File configFile;
	public FileConfiguration Config;

	public File arenaFile;
	public FileConfiguration Arena;

	public File invFile;
	public FileConfiguration Inv;

	public File chestFile;
	public FileConfiguration Chest;
	
	public File spawnFile;
	public FileConfiguration Spawns;

	private PluginManager pm = Bukkit.getServer().getPluginManager();

	private Logger log = Bukkit.getLogger();
	
	public static Main getInstance(){
		return instance;
	}

	@Override
	public void onEnable(){
		
		instance = this;

		configFile = new File(getDataFolder(), "config.yml");
		arenaFile = new File(getDataFolder(), "arena.yml");
		invFile = new File(getDataFolder(), "inventorys.yml");
		chestFile = new File(getDataFolder(), "chests.yml");
		spawnFile = new File(getDataFolder(), "spawns.yml");
		
		try {
			
			ConfigManager.getInstance().firstRun();

		} catch (Exception e) {

			e.printStackTrace();
		}
		
		this.Config = new YamlConfiguration();
		this.Arena = new YamlConfiguration();
		this.Inv = new YamlConfiguration();
		this.Chest = new YamlConfiguration();
		this.Spawns = new YamlConfiguration();
		ConfigManager.getInstance().loadYamls();
		ConfigManager.getInstance().saveYamls();
		
		pm.registerEvents(new PlayerDeath(), this);
		pm.registerEvents(new PlayerLeave(), this);

		getCommand("skyblockw").setExecutor(new SW());
		
		setUp();
       
	}
	
	@Override
	public void onDisable(){
		
	}    
	
	public Location getLobby(){
		
		if(this.Config.contains("Lobby")){
		
			World world = Bukkit.getServer().getWorld(this.Config.getString("Lobby.World"));
			
			int X = this.Config.getInt("Lobby.X");
			int Y = this.Config.getInt("Lobby.Y");
			int Z = this.Config.getInt("Lobby.Z");
			float Yaw = this.Config.getLong("Lobby.Yaw");
			float Pitch = this.Config.getLong("Lobby.Pitch");
			
			Location lobby = new Location(world, X, Y, Z, Yaw, Pitch);
			
			return lobby;
		}
		log.log(Level.SEVERE, "Skyblock Wars lobby not found.");
		return null;
	}
	
	public void setUp(){
		
		new BukkitRunnable(){
			
			  @Override
			  public void run(){
				  
				  GameManager.getInstance().setUp();
				  WorldEditUtility.getInstance().regenAllIslands(); 
			  }
			}.run();
	}
	
	public void setLobby(Player p){
		
		this.Config.set("Lobby.X", p.getLocation().getBlockX());
		this.Config.set("Lobby.Y", p.getLocation().getBlockY());
		this.Config.set("Lobby.Z", p.getLocation().getBlockZ());
		this.Config.set("Lobby.YAW", p.getLocation().getPitch());
		this.Config.set("Lobby.PITCH", p.getLocation().getYaw());
		this.Config.set("Lobby.WORLD", p.getLocation().getWorld().getName());
		ConfigManager.getInstance().saveYamls();
	}
	
	public boolean teleportToLobby(Player p){
		
		if(!this.Config.contains("Lobby")){
			return false;
		}
		
		World world = Bukkit.getServer().getWorld(this.Config.getString("Lobby.WORLD"));
		double x = Main.getInstance().Config.getDouble("Lobby.X");
		double y = Main.getInstance().Config.getDouble("Lobby.Y");
		double z = Main.getInstance().Config.getDouble("Lobby.Z");
		long yaw = Main.getInstance().Config.getLong("Lobby.YAW");
		long pitch = Main.getInstance().Config.getLong("Lobby.PITCH");
		
		Location location = new Location(world, x, y, z, yaw, pitch);
		
		p.teleport(location);
		
		return true;
	}

}
