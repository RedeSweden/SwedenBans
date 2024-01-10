package com.redesweden.swedenbans.bungee;

import org.bukkit.plugin.Plugin;
import com.redesweden.swedenbans.banmanager.RangeBan;
import com.redesweden.swedenbans.banmanager.IPBan;
import com.redesweden.swedenbans.banmanager.Temporary;
import org.bukkit.ChatColor;
import com.redesweden.swedenbans.util.Formatter;
import com.redesweden.swedenbans.util.IPAddress;
import org.bukkit.Bukkit;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.bukkit.entity.Player;
import com.redesweden.swedenbans.SwedenBans;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener
{
    private SwedenBans plugin;
    
    public BungeeListener() {
        super();
        this.plugin = SwedenBans.instance;
    }
    
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
        try {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            if (in.readUTF().equals("IP")) {
                final String ip = in.readUTF();
                SwedenBans.instance.getBanManager().logIP(player.getName(), ip);
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
                    public void run() {
                        final boolean whitelisted = SwedenBans.instance.getBanManager().isWhitelisted(player.getName());
                        if (!whitelisted) {
                            final IPBan ipban = BungeeListener.this.plugin.getBanManager().getIPBan(ip);
                            if (ipban != null) {
                                player.kickPlayer(ipban.getKickMessage());
                                return;
                            }
                            final IPAddress address = new IPAddress(ip);
                            final RangeBan rb = BungeeListener.this.plugin.getBanManager().getBan(address);
                            if (rb != null) {
                                player.kickPlayer(rb.getKickMessage());
                                return;
                            }
                            if (BungeeListener.this.plugin.getBanManager().getDNSBL() != null) {
                                BungeeListener.this.plugin.getBanManager().getDNSBL().handle(player, ip);
                            }
                        }
                    }
                }, 1L);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
