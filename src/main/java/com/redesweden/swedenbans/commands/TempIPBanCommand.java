package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.IPBan;
import com.redesweden.swedenbans.banmanager.TempIPBan;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempIPBanCommand extends CmdSkeleton
{
    public TempIPBanCommand() {
        super("tempipban", "swedenbans.tempipban");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length <= 2) {
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
        long time = Util.getTime(args);
        if (time <= 0L) {
            player.sendMessage(this.getUsage());
            return true;
        }
        time += System.currentTimeMillis();
        final String reason = Util.buildReason(args);
        String ip;
        if (!Util.isIP(name)) {
            name = this.plugin.getBanManager().match(name);
            if (name == null) {
                name = args[0];
            }
            ip = this.plugin.getBanManager().getIP(name);
            if (ip == null) {
                final String msg = Msg.get("error.no-ip-known");
                player.sendMessage(msg);
                return true;
            }
            this.plugin.getBanManager().tempban(name, reason, banner, time);
        }
        else {
            ip = name;
        }
        final IPBan ban = this.plugin.getBanManager().getIPBan(ip);
        if (ban != null) {
            if (!(ban instanceof TempIPBan)) {
                final String msg2 = Msg.get("error.tempipban-shorter-than-last");
                player.sendMessage(msg2);
                return true;
            }
            final TempIPBan tBan = (TempIPBan)ban;
            if (tBan.getExpires() > time) {
                final String msg3 = Msg.get("error.tempipban-shorter-than-last");
                player.sendMessage(msg3);
                return true;
            }
            this.plugin.getBanManager().unbanip(ip);
        }
        this.plugin.getBanManager().tempipban(ip, reason, banner, time);
        final String message = Msg.get("announcement.player-was-tempipbanned", new String[] { "banner", "name", "reason", "ip", "time" }, new String[] { banner, name, reason, ip, Util.getTimeUntil(time) });
        this.plugin.getBanManager().announce(message, silent, sender);
        return true;
    }
}
