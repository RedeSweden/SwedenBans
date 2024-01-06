package com.redesweden.swedenbans.commands;

import java.text.ParseException;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ImmuneCommand extends CmdSkeleton
{
    public ImmuneCommand() {
        super("immune", "swedenbans.immune");
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /immune <name> <true|false>");
            return true;
        }
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (!p.hasPermission("MaxBans.immune") && !p.isOp() && !p.hasPermission("MaxBans.*")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
                return true;
            }
        }
        try {
            final boolean immune = Util.parseBoolean(args[1]);
            final String user = SwedenBans.instance.getBanManager().match(args[0]);
            if (this.plugin.getBanManager().setImmunity(user, immune)) {
                sender.sendMessage(ChatColor.GREEN + "Success!");
            }
            else {
                sender.sendMessage(ChatColor.RED + "Failure - Nothing has changed.");
            }
        }
        catch (ParseException e) {
            sender.sendMessage(ChatColor.RED + "Unrecognised state " + args[1]);
        }
        return true;
    }
}
