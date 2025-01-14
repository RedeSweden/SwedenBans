package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.banmanager.Mute;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnMuteCommand extends CmdSkeleton
{
    public UnMuteCommand() {
        super("unmute", "swedenbans.unmute");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        final boolean silent = Util.isSilent(args);
        String name = args[0];
        if (name.isEmpty()) {
            sender.sendMessage(Msg.get("error.no-player-given"));
            return true;
        }
        name = this.plugin.getBanManager().match(name);
        if (name == null) {
            name = args[0];
        }
        final Mute mute = this.plugin.getBanManager().getMute(name);
        if (mute != null) {
            this.plugin.getBanManager().unmute(name);
            final String banner = args[1];
            final String message = Msg.get("announcement.player-was-unmuted", new String[] { "banner", "name" }, new String[] { banner, name });
            this.plugin.getBanManager().announce(message, silent, sender);
        }
        else {
            sender.sendMessage(Msg.get("error.no-mute-found", "name", name));
        }
        return true;
    }
}
