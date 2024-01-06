package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.Mute;
import com.redesweden.swedenbans.banmanager.TempMute;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempMuteCommand extends CmdSkeleton
{
    public TempMuteCommand() {
        super("tempmute", "swedenbans.tempmute");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length <= 2) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        String name = args[0];
        String banner = args[1];
        final Player player = sender.getServer().getPlayer(banner);
        name = this.plugin.getBanManager().match(name);
        if (name == null) {
            name = args[0];
        }
        final boolean silent = Util.isSilent(args);
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
        final Mute mute = this.plugin.getBanManager().getMute(name);
        if (mute != null) {
            if (!(mute instanceof TempMute)) {
                final String msg = "§cEsse jogador já possui um mute atual recorrente.";
                player.sendMessage(msg);
                return true;
            }
            final TempMute tMute = (TempMute)mute;
            if (tMute.getExpires() > time) {
                final String msg2 = "§cEsse jogador já possui um mute atual recorrente.";
                player.sendMessage(msg2);
                return true;
            }
        }
        final String reason = Util.buildReason(args);
        this.plugin.getBanManager().tempmute(name, banner, reason, time);
        final String until = Util.getTimeUntil(time);
        final String message = Msg.get("announcement.player-was-temp-muted", new String[] { "banner", "name", "time", "reason" }, new String[] { banner, name, until, reason });
        this.plugin.getBanManager().addHistory(name, banner, message);
        this.plugin.getBanManager().announce(message, silent, sender);
        return true;
    }
}
