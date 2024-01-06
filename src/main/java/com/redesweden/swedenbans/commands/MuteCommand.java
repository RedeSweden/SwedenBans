package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.Mute;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand extends CmdSkeleton
{
    public MuteCommand() {
        super("mute", "swedenbans.mute");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length <= 0) {
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
        name = this.plugin.getBanManager().match(name);
        if (name == null) {
            name = args[0];
        }
        final Mute mute = this.plugin.getBanManager().getMute(name);
        final String reason = Util.buildReason(args);
        if (mute != null && mute.getReason().equalsIgnoreCase(reason)) {
            player.sendMessage("§cEste jogador já está mutado pelo mesmo motivo.");
            return true;
        }
        this.plugin.getBanManager().mute(name, banner, reason);
        final String message = Msg.get("announcement.player-was-muted", new String[] { "banner", "name", "reason" }, new String[] { banner, name, reason });
        this.plugin.getBanManager().announce(message, silent, sender);
        this.plugin.getBanManager().addHistory(name, banner, message);
        return true;
    }
}
