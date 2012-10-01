//Push test by Darek
package org.maxgamer.maxbans;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.maxbans.banmanager.BanManager;
import org.maxgamer.maxbans.commands.BanCommand;
import org.maxgamer.maxbans.database.Database;
import org.maxgamer.maxbans.listeners.*;

public class MaxBans extends JavaPlugin{
        private BanManager banManager;
        private ChatListener chatListener;
        private BanCommand banCommand;
        
        //private CommandListener commandExecutor;
        /*TODO: We're going to need command listeners for:
         *		IPBan
         *		TempBan
         *		TempIPBan
         *		Mute
         *		Unmute(Maybe, toggle mute?)
         *		Kick
         *		Unban (This should handle IP Bans too)
        */	
        private JoinListener joinListener; 
        private Database db;
        
	public void onEnable(){
		/* Generates files for the first run */
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		File configFile = new File(this.getDataFolder(), "config.yml");
		
		if(!configFile.exists()){
			//Saves config.yml from inside the plugin
			//into the plugins directory folder
			this.saveResource("config.yml", false);
		}
		
		/*
		 * Reloads the config from disk.
		 * Normally this is done before onEnable()
		 * anyway, but, if we do /plugman reload MaxBans,
		 * it doesnt.  This makes it friendlier.
		 */
		this.reloadConfig();
		
		//The database for bans
		db = new Database(this, new File(this.getDataFolder(), "bans.db"));
		
		//BanManager
		banManager = new BanManager(this);
		
		//Commands
		this.banCommand = new BanCommand(this);
		
		//Register commands
		this.getCommand("ban").setExecutor(banCommand);
		
		//Listeners for chat (mute) and join (Ban)
		this.chatListener = new ChatListener(this);
		this.joinListener = new JoinListener(this);
	
		//Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(this.chatListener, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.joinListener, this);
    }
	public void onDisable(){
		this.getLogger().info("Disabling Maxbans...");
		this.db.getDatabaseWatcher().stop(); //Pauses
		this.db.getDatabaseWatcher().run(); //Empties buffer
		this.getLogger().info("Cleared buffer...");
	}
	/**
	 * Returns the ban manager for banning and checking bans and mutes.
	 * @return the ban manager for banning and checking bans and mutes.
	 */
    public BanManager getBanManager() {
        return banManager;
    }
        
    /**
     * Returns the raw database for storing data and loading into the cache.
     * @return the raw database for storing data and loading into the cache.
     */
    public Database getDB(){
    	return db;
    }
}
