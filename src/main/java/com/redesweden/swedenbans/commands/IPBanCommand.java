package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.IPBan;
import com.redesweden.swedenbans.banmanager.TempIPBan;
import com.redesweden.swedenbans.util.Formatter;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IPBanCommand extends CmdSkeleton
{
    public IPBanCommand() {
        super("ipban", "swedenbans.ipban");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final boolean silent = Util.isSilent(args);
        final String reason = Util.buildReason(args);
        if (args.length <= 0) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        final String banner = args[1];
        final Player player = sender.getServer().getPlayer(banner);
        String name = args[0];
        if (name.isEmpty()) {
            player.sendMessage(Msg.get("error.no-player-given"));
            return true;
        }
        String ip;
        if (!Util.isIP(name)) {
            name = this.plugin.getBanManager().match(name);
            if (name == null) {
                name = args[0];
            }
            ip = this.plugin.getBanManager().getIP(name);
            if (ip == null) {
                player.sendMessage(Formatter.secondary + "No IP recorded for " + name + " - Try ban them normally instead?");
                return true;
            }
            this.plugin.getBanManager().ban(name, reason, banner);
        }
        else {
            ip = name;
        }
        final IPBan ban = this.plugin.getBanManager().getIPBan(ip);
        if (ban != null && !(ban instanceof TempIPBan)) {
            player.sendMessage(Msg.get("error.ip-already-banned"));
            return true;
        }
        this.plugin.getBanManager().ipban(ip, reason, banner);
        final String message = Msg.get("announcement.player-was-ip-banned", new String[] { "banner", "name", "reason", "ip" }, new String[] { banner, name, reason, ip });
        this.plugin.getBanManager().announce(message, silent, sender);
        this.plugin.getBanManager().addHistory(name, banner, message);
        return true;
    }
}
