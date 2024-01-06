package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.Ban;
import com.redesweden.swedenbans.banmanager.IPBan;
import com.redesweden.swedenbans.banmanager.TempBan;
import com.redesweden.swedenbans.banmanager.TempIPBan;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TempBanCommand extends CmdSkeleton
{
    public TempBanCommand() {
        super("tempban", "swedenbans.tempban");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        final boolean silent = Util.isSilent(args);
        String name = args[0];
        String banner = args[1];
        final Player player = sender.getServer().getPlayer(banner);
        if (name.isEmpty()) {
            player.sendMessage(Msg.get("error.no-player-given"));
            return true;
        }
        long expires = Util.getTime(args);
        if (expires <= 0L) {
            player.sendMessage(this.getUsage());
            return true;
        }
        expires += System.currentTimeMillis();
        final FileConfiguration conf = SwedenBans.instance.getConfig();
        long tempbanTime;
        try {
            tempbanTime = conf.getLong("MaxTempbanTime");
        }
        catch (Exception e) {
            tempbanTime = 604800L;
        }
        long compare = tempbanTime;
        tempbanTime = tempbanTime * 1000;
        
        tempbanTime += System.currentTimeMillis();
        if (compare != 0 && expires > tempbanTime) {
            player.sendMessage("Ban time is too long! Reducing to ban limit! (" + Util.getTimeUntil(tempbanTime) + ")");
            expires = tempbanTime;
        }
        final String reason = Util.buildReason(args);
        if (!Util.isIP(name)) {
            name = this.plugin.getBanManager().match(name);
            if (name == null) {
                name = args[0];
            }
            final Ban ban = this.plugin.getBanManager().getBan(name);
            if (ban != null) {
                if (!(ban instanceof TempBan)) {
                    final String msg = Msg.get("error.tempban-shorter-than-last");
                    player.sendMessage(msg);
                    return true;
                }
                final TempBan tBan = (TempBan)ban;
                if (tBan.getExpires() > expires) {
                    final String msg2 = Msg.get("error.tempban-shorter-than-last");
                    player.sendMessage(msg2);
                    return true;
                }
                this.plugin.getBanManager().unban(name);
            }
            this.plugin.getBanManager().tempban(name, reason, banner, expires);
        }
        else {
            final String ip = name;
            final IPBan ipban = this.plugin.getBanManager().getIPBan(ip);
            if (ipban != null && ipban instanceof TempIPBan) {
                final TempIPBan tiBan = (TempIPBan)ipban;
                if (tiBan.getExpires() > expires) {
                    final String msg3 = Msg.get("error.tempipban-shorter-than-last");
                    player.sendMessage(msg3);
                    return true;
                }
                this.plugin.getBanManager().unbanip(ip);
            }
            this.plugin.getBanManager().tempipban(ip, reason, banner, expires);
        }
        final String message = Msg.get("announcement.player-was-tempbanned", new String[] { "banner", "name", "reason", "time" }, new String[] { banner, name, reason, Util.getTimeUntil(expires) });
        this.plugin.getBanManager().announce(message, silent, sender);
        this.plugin.getBanManager().addHistory(name, banner, message);
        return true;
    }
}
