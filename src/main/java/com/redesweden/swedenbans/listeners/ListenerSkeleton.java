package com.redesweden.swedenbans.listeners;

import com.redesweden.swedenbans.SwedenBans;
import org.bukkit.event.Listener;

public class ListenerSkeleton implements Listener
{
    protected SwedenBans getPlugin() {
        return SwedenBans.instance;
    }
}
