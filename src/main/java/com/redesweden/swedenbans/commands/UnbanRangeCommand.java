package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.banmanager.RangeBan;
import com.redesweden.swedenbans.util.Formatter;
import com.redesweden.swedenbans.util.IPAddress;
import com.redesweden.swedenbans.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnbanRangeCommand extends CmdSkeleton
{
    public UnbanRangeCommand() {
        super("unrangeban", "swedenbans.unbanrange");
        this.minArgs = 1;
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final boolean silent = Util.isSilent(args);
        final String banner = Util.getName(sender);
        if (!Util.isIP(args[0])) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid IP!");
            return true;
        }
        final IPAddress ip = new IPAddress(args[0]);
        final RangeBan rb = this.plugin.getBanManager().getBan(ip);
        if (rb == null) {
            sender.sendMessage(ChatColor.RED + ip.toString() + " is not banned!");
            return true;
        }
        this.plugin.getBanManager().unban(rb);
        this.plugin.getBanManager().announce(Formatter.secondary + banner + Formatter.primary + " unbanned the IP Range " + Formatter.secondary + rb.toString() + Formatter.primary + "!", silent, sender);
        final String msg = Formatter.secondary + banner + Formatter.primary + " unbanned the IP Range " + Formatter.secondary + rb.toString() + Formatter.primary + "!";
        this.plugin.getBanManager().addHistory(rb.toString(), banner, msg);
        return true;
    }
}
