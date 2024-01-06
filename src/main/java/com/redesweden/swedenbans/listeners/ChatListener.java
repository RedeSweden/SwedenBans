package com.redesweden.swedenbans.listeners;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.banmanager.Mute;
import com.redesweden.swedenbans.banmanager.TempMute;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    private SwedenBans plugin;
    
    public ChatListener(final SwedenBans mb) {
        super();
        this.plugin = mb;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player p = event.getPlayer();
        final Mute mute = this.plugin.getBanManager().getMute(p.getName());
        if (mute != null) {
            if (this.plugin.getBanManager().hasImmunity(p.getName())) {
                return;
            }
            if (mute instanceof TempMute) {
                final TempMute tMute = (TempMute)mute;
                p.sendMessage(ChatColor.RED + "Você está mutado por " + Util.getTimeUntil(tMute.getExpires()));
            }
            else {
                p.sendMessage(ChatColor.RED + "Você está mutado permanentemente!");
            }
            event.setCancelled(true);
        }
    }
}
