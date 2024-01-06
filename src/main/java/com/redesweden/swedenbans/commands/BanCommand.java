package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.Ban;
import com.redesweden.swedenbans.banmanager.IPBan;
import com.redesweden.swedenbans.banmanager.TempBan;
import com.redesweden.swedenbans.banmanager.TempIPBan;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends CmdSkeleton
{
    public BanCommand() {
        super("ban", "swedenbans.ban");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        final boolean silent = Util.isSilent(args);
        final String banner = args[1];
        final Player player = sender.getServer().getPlayer(banner);
        String name = args[0];
        if (name.isEmpty()) {
            player.sendMessage(Msg.get("error.no-player-given"));
            return true;
        }
        final String reason = Util.buildReason(args);
        final String message = Msg.get("announcement.player-was-banned", new String[] { "banner", "name", "reason" }, new String[] { banner, name, reason });
        if (!Util.isIP(name)) {
            name = this.plugin.getBanManager().match(name);
            if (name == null) {
                name = args[0];
            }
            final Ban ban = this.plugin.getBanManager().getBan(name);
            if (ban != null && !(ban instanceof TempBan)) {
                player.sendMessage(Msg.get("error.player-already-banned"));
                return true;
            }
            this.plugin.getBanManager().ban(name, reason, banner);
        }
        else {
            final IPBan ipban = this.plugin.getBanManager().getIPBan(name);
            if (ipban != null && !(ipban instanceof TempIPBan)) {
                player.sendMessage(Msg.get("error.ip-already-banned"));
                return true;
            }
            this.plugin.getBanManager().ipban(name, reason, banner);
        }
        this.plugin.getBanManager().announce(message, silent, sender);
        this.plugin.getBanManager().addHistory(name, banner, message);
        return true;
    }
}
