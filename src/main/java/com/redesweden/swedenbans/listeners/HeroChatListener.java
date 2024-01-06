package com.redesweden.swedenbans.listeners;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.banmanager.Mute;
import com.redesweden.swedenbans.banmanager.TempMute;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import com.dthielke.herochat.Chatter;
import org.bukkit.ChatColor;
import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.event.Listener;

public class HeroChatListener implements Listener
{
    private SwedenBans plugin;
    
    public HeroChatListener(final SwedenBans plugin) {
        super();
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onHeroChat(final ChannelChatEvent e) {
        final Player p = e.getSender().getPlayer();
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
            e.setResult(Chatter.Result.FAIL);
        }
    }
}
