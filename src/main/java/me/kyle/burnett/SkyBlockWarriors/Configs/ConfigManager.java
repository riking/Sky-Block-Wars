package me.kyle.burnett.SkyBlockWarriors.Configs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.kyle.burnett.SkyBlockWarriors.Main;

public class ConfigManager {

	static ConfigManager instance = new ConfigManager();
	
	public static ConfigManager getInstance(){
		return instance;
	}
	
	public void firstRun() throws Exception {

        if(!Main.getInstance().configFile.exists()){
           Main.getInstance().configFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("config.yml"),Main.getInstance().configFile);
        }
        
        if(!Main.getInstance().arenaFile.exists()){
           Main.getInstance().arenaFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("arenas.yml"),Main.getInstance().arenaFile);
        }
        
        if(!Main.getInstance().invFile.exists()){
           Main.getInstance().invFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("inventorys.yml"),Main.getInstance().invFile);
        }
        
        if(!Main.getInstance().chestFile.exists()){
           Main.getInstance().chestFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("chests.yml"),Main.getInstance().chestFile);
        }
        
        if(!Main.getInstance().spawnFile.exists()){
            Main.getInstance().spawnFile.getParentFile().mkdirs();
             copy(Main.getInstance().getResource("spawns.yml"),Main.getInstance().spawnFile);
         }
   
	}
 
	public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
 
	public void loadYamls() {
        try {
           Main.getInstance().Config.load(Main.getInstance().configFile);
           Main.getInstance().Arena.load(Main.getInstance().arenaFile);
           Main.getInstance().Inv.load(Main.getInstance().invFile);
           Main.getInstance().Chest.load(Main.getInstance().chestFile);
           Main.getInstance().Spawns.load(Main.getInstance().spawnFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
	}
 
	public void saveYamls() {
        try {
	         Main.getInstance().Config.save(Main.getInstance().configFile);  
	         Main.getInstance().Arena.save(Main.getInstance().arenaFile);
	         Main.getInstance().Inv.save(Main.getInstance().invFile);
	         Main.getInstance().Chest.save(Main.getInstance().chestFile);
	         Main.getInstance().Spawns.save(Main.getInstance().spawnFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
    	}
	}
