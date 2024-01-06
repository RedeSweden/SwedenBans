package com.redesweden.swedenbans.commands;

import com.redesweden.swedenbans.util.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CmdSkeleton
{
    public ReloadCommand() {
        super("mbreload", "swedenbans.reload");
        this.namePos = -1;
    }
    
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        sender.sendMessage(Formatter.secondary + "Reloading MaxBans");
        Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin);
        Bukkit.getPluginManager().enablePlugin((Plugin)this.plugin);
        sender.sendMessage(ChatColor.GREEN + "Reload Complete");
        return true;
    }
}
