package com.redesweden.swedenbans;

import com.redesweden.swedenbans.bungee.BungeeListener;
import com.redesweden.swedenbans.geoip.GeoIPDatabase;
import com.redesweden.swedenbans.sync.SyncServer;
import com.redesweden.swedenbans.sync.Syncer;
import com.redesweden.swedenbans.util.Formatter;
import com.redesweden.swedenbans.util.Metrics;
import com.redesweden.swedenbans.commands.UnbanRangeCommand;
import com.redesweden.swedenbans.commands.TempRangeBanCommand;
import com.redesweden.swedenbans.commands.RangeBanCommand;
import com.redesweden.swedenbans.commands.ImmuneCommand;
import com.redesweden.swedenbans.commands.WhitelistCommand;
import com.redesweden.swedenbans.commands.ReloadCommand;
import com.redesweden.swedenbans.commands.MBDebugCommand;
import com.redesweden.swedenbans.commands.MBExportCommand;
import com.redesweden.swedenbans.commands.MBImportCommand;
import com.redesweden.swedenbans.commands.HistoryCommand;
import com.redesweden.swedenbans.commands.MBCommand;
import com.redesweden.swedenbans.commands.ForceSpawnCommand;
import com.redesweden.swedenbans.commands.KickCommand;
import com.redesweden.swedenbans.commands.LockdownCommand;
import com.redesweden.swedenbans.commands.ClearWarningsCommand;
import com.redesweden.swedenbans.commands.UnWarnCommand;
import com.redesweden.swedenbans.commands.WarnCommand;
import com.redesweden.swedenbans.commands.DupeIPCommand;
import com.redesweden.swedenbans.commands.CheckBanCommand;
import com.redesweden.swedenbans.commands.CheckIPCommand;
import com.redesweden.swedenbans.commands.UUID;
import com.redesweden.swedenbans.commands.UnMuteCommand;
import com.redesweden.swedenbans.commands.UnbanCommand;
import com.redesweden.swedenbans.commands.TempMuteCommand;
import com.redesweden.swedenbans.commands.TempIPBanCommand;
import com.redesweden.swedenbans.commands.TempBanCommand;
import com.redesweden.swedenbans.commands.MuteCommand;
import com.redesweden.swedenbans.commands.IPBanCommand;
import com.redesweden.swedenbans.commands.BanCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.event.Listener;
import com.redesweden.swedenbans.commands.ToggleChat;
import com.redesweden.swedenbans.banmanager.SyncBanManager;
import java.io.IOException;
import com.redesweden.swedenbans.database.DatabaseCore;
import com.redesweden.swedenbans.database.SQLiteCore;
import com.redesweden.swedenbans.database.MySQLCore;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.File;

import com.redesweden.swedenbans.database.Database;
import com.redesweden.swedenbans.listeners.ChatCommandListener;
import com.redesweden.swedenbans.listeners.ChatListener;
import com.redesweden.swedenbans.listeners.HeroChatListener;
import com.redesweden.swedenbans.listeners.JoinListener;
import com.redesweden.swedenbans.banmanager.BanManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SwedenBans extends JavaPlugin
{
    public static final String BUNGEE_CHANNEL = "BungeeCord";
    private BanManager banManager;
    private Syncer syncer;
    private SyncServer syncServer;
    private GeoIPDatabase geoIPDB;
    private JoinListener joinListener;
    private HeroChatListener herochatListener;
    private ChatListener chatListener;
    private ChatCommandListener chatCommandListener;
    private Database db;
    private Metrics metrics;
    public boolean filter_names;
    public static SwedenBans instance;
    
    public GeoIPDatabase getGeoDB() {
        return this.geoIPDB;
    }
    
    public void onEnable() {
        SwedenBans.instance = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        final File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveResource("config.yml", false);
        }
        this.reloadConfig();
        Msg.reload();
        this.getConfig().options().copyDefaults();
        final File geoCSV = new File(this.getDataFolder(), "geoip.csv");
        if (!geoCSV.exists()) {
            final Runnable download = new Runnable() {
                public void run() {
                    final String url = "http://maxgamer.org/plugins/maxbans/geoip.csv";
                    SwedenBans.this.getLogger().info("Downloading geoIPDatabase...");
                    try {
                        final FileOutputStream out = new FileOutputStream(geoCSV);
                        final BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                        final byte[] data = new byte[1024];
                        int count;
                        while ((count = in.read(data, 0, 1024)) != -1) {
                            out.write(data, 0, count);
                        }
                        SwedenBans.this.getLogger().info("Download complete.");
                        out.close();
                        in.close();
                        SwedenBans.access$0(SwedenBans.this, new GeoIPDatabase(geoCSV));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Failed to download MaxBans GeoIPDatabase");
                    }
                }
            };
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this, download);
        }
        else {
            this.geoIPDB = new GeoIPDatabase(geoCSV);
        }
        this.filter_names = this.getConfig().getBoolean("filter-names");
        Formatter.load((Plugin)this);
        final ConfigurationSection dbConfig = this.getConfig().getConfigurationSection("database");
        DatabaseCore dbCore;
        if (this.getConfig().getBoolean("database.mysql", false)) {
            this.getLogger().info("Using MySQL");
            final String user = dbConfig.getString("user");
            final String pass = dbConfig.getString("pass");
            final String host = dbConfig.getString("host");
            final String name = dbConfig.getString("name");
            final String port = dbConfig.getString("port");
            dbCore = new MySQLCore(host, user, pass, name, port);
        }
        else {
            this.getLogger().info("Using SQLite");
            dbCore = new SQLiteCore(new File(this.getDataFolder(), "bans.db"));
        }
        final boolean readOnly = dbConfig.getBoolean("read-only", false);
        try {
            this.db = new Database(dbCore) {
                public void execute(final String query, final Object... objs) {
                    if (readOnly) {
                        return;
                    }
                    super.execute(query, objs);
                }
            };
        }
        catch (Database.ConnectionException e1) {
            e1.printStackTrace();
            System.out.println("Failed to create connection to database. Disabling MaxBans :(");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        final ConfigurationSection syncConfig = this.getConfig().getConfigurationSection("sync");
        if (syncConfig.getBoolean("use", false)) {
            this.getLogger().info("Using Sync.");
            final String host = syncConfig.getString("host");
            final int port2 = syncConfig.getInt("port");
            final String pass2 = syncConfig.getString("pass");
            if (syncConfig.getBoolean("server", false)) {
                try {
                    (this.syncServer = new SyncServer(port2, pass2)).start();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                    this.getLogger().info("Could not start sync server!");
                }
            }
            (this.syncer = new Syncer(host, port2, pass2)).start();
            this.banManager = new SyncBanManager(this);
        }
        else {
            this.banManager = new BanManager(this);
        }
        this.registerCommands();
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ToggleChat(), (Plugin)this);
        if (Bukkit.getPluginManager().getPlugin("Herochat") != null) {
            this.getLogger().info("Found Herochat... Hooking!");
            this.herochatListener = new HeroChatListener(this);
            Bukkit.getServer().getPluginManager().registerEvents((Listener)this.herochatListener, (Plugin)this);
        }
        else {
            this.chatListener = new ChatListener(this);
            Bukkit.getServer().getPluginManager().registerEvents((Listener)this.chatListener, (Plugin)this);
        }
        this.joinListener = new JoinListener();
        this.chatCommandListener = new ChatCommandListener();
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this.joinListener, (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this.chatCommandListener, (Plugin)this);
        this.startMetrics();
        if (this.isBungee()) {
            Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (PluginMessageListener)new BungeeListener());
            Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        }
    }
    
    public boolean isBungee() {
        return SwedenBans.instance.getConfig().getBoolean("bungee");
    }
    
    public void onDisable() {
        this.getLogger().info("Disabling Maxbans...");
        if (this.syncer != null) {
            this.syncer.stop();
            this.syncer = null;
        }
        if (this.syncServer != null) {
            this.syncServer.stop();
            this.syncServer = null;
        }
        this.getLogger().info("Clearing buffer...");
        this.db.close();
        this.getLogger().info("Cleared buffer...");
        SwedenBans.instance = null;
    }
    
    public BanManager getBanManager() {
        return this.banManager;
    }
    
    public Database getDB() {
        return this.db;
    }
    
    public void registerCommands() {
        new BanCommand();
        new IPBanCommand();
        new MuteCommand();
        new TempBanCommand();
        new TempIPBanCommand();
        new TempMuteCommand();
        new UnbanCommand();
        new UnMuteCommand();
        new UUID();
        new CheckIPCommand();
        new CheckBanCommand();
        new DupeIPCommand();
        new WarnCommand();
        new UnWarnCommand();
        new ClearWarningsCommand();
        new LockdownCommand();
        new KickCommand();
        new ForceSpawnCommand();
        new MBCommand();
        new HistoryCommand();
        new MBImportCommand();
        new MBExportCommand();
        new MBDebugCommand();
        new ReloadCommand();
        new WhitelistCommand();
        new ImmuneCommand();
        new RangeBanCommand();
        new TempRangeBanCommand();
        new UnbanRangeCommand();
    }
    
    public void startMetrics() {
        try {
            if (this.metrics != null) {
                return;
            }
            this.metrics = new Metrics((Plugin)this);
            if (!this.metrics.start()) {
                return;
            }
            final Metrics.Graph bans = this.metrics.createGraph("Bans");
            final Metrics.Graph ipbans = this.metrics.createGraph("IP Bans");
            final Metrics.Graph mutes = this.metrics.createGraph("Mutes");
            bans.addPlotter(new Metrics.Plotter() {
                public int getValue() {
                    return SwedenBans.this.getBanManager().getBans().size();
                }
            });
            ipbans.addPlotter(new Metrics.Plotter() {
                public int getValue() {
                    return SwedenBans.this.getBanManager().getIPBans().size();
                }
            });
            mutes.addPlotter(new Metrics.Plotter() {
                public int getValue() {
                    return SwedenBans.this.getBanManager().getMutes().size();
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Metrics start failed");
        }
    }
    
    public Metrics getMetrics() {
        return this.metrics;
    }
    
    public Syncer getSyncer() {
        return this.syncer;
    }
    
    static /* synthetic */ void access$0(final SwedenBans swedenBans, final GeoIPDatabase geoIPDB) {
        swedenBans.geoIPDB = geoIPDB;
    }
}
