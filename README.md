SwedenBans
=======

O SwedenBans é um Fork do plugin MaxBans com funções designadas especialmente para a Rede Sweden.

Databases
=======

MySQL
SQLite (Flatfile)


Best Features
=======

Here are the top ten features of MaxBans over other banning plugins:

Full server lockdown - Prevent anyone from joining with a custom message (Such as bot attacks)
Offline player name auto completion
Warnings system
Duplicate IP detection
DNSBL lookups to stop proxys!
Multiline kick messages! No more running off the screen!
Notifications when a banned player tries to join!
All times are relative! (Eg. "You're banned for 4 minutes 6 seconds", not "You're banned til 5:43pm CST")
Customize every colour!
Block commands like /me when muted!

Commands
=======

- /unban &lt;name or IP>
- /ban &lt;name or IP> &lt;reason>
- /ipban &lt;name or IP> &lt;reason>
- /tempban &lt;name or IP> &lt;number> &lt;minutes|hours|days|weeks|etc> &lt;reason>
- /tempipban &lt;name or IP> &lt;number> &lt;minutes|hours|days|weeks|etc> &lt;reason>
- /mute &lt;name>
- /tempmute &lt;name> &lt;number> &lt;minutes|hours|days|weeks|etc>
- /kick &lt;name or * for everyone>
- /checkip &lt;name>
- /dupeip &lt;name or IP>
- /checkban &lt;name or IP>
- /warn &lt;name> &lt;reason>
- /clearwarnings &lt;name> &lt;reason>
- /unwarn &lt;name> - Removes a players most recent warning
- /unmute &lt;name>
- /history [name] [number of records] - Displays a history of bans, kicks, mutes & more dealt
- /mbreload - Reloads the plugin
- /mbdebug - Outputs debug information for me if you're having issues!
- /mbwhitelist &lt;name> - Allows the given user to bypass IP bans (Not regular bans! Eg, use for players with siblings who need to be IP banned)
- /ipreport - Basically, a mass /dupeip, on everyone who is online
- /lockdown [reason]
- /forcespawn - Teleports someone to the spawn (Twice, so /back won't work)
- /mbreload - Reloads maxbans
- /mbimport - Imports vanilla minecraft (And others) bans.
- /mbexport - Export bans to vanilla, MySQL or SQLite databases. (Allows swapping SQLite <-> MySQL), and others ban plugins.
- /rangeban &lt;ip1-ip2> [reason] - Bans the IP range from ip1 to ip2 for the supplied reason.
- /temprangeban &lt;ip1-ip2> &lt;time> &lt;hours/min/sec> [reason] - Temporary variant of above
- /unrangeban &lt;ip> - Removes any RangeBan which overlaps with the given IP. Eg, if 127.0.0.1-127.0.0.5 is banned, unbanning 127.0.0.3 will lift the whole ban on 127.0.0.1-127.0.0.5.
Almost any command may have -s added in it to prevent announcing it, for example: 
- /tempban NewGuy101 -s 1 hour MaxBans is Awesome!

- Nobody will see the announcement that NewGuy101 was temp banned, just the fact he "has left the game."

If you want an in-depth analysis of each command, try here:
http://dev.bukkit.org/server-mods/maxbans/pages/command-tutorial/

Permissions
=======

All permissions are maxbans.<commandName>, with the following exceptions:
Lockdown on/off: maxbans.lockdown.use
Lockdown bypass: maxbans.lockdown.bypass
Kick all (/kick *): maxbans.kick.*
Notification: maxbans.notify. This is for when the DNSBL discovers a proxy, or a banned player tries to join.
Check your own warnings/mutes: maxbans.checkban.self
See silent commands (Eg commands with -s in them): maxbans.seesilent
See broadcasts, such as kick messages of others: maxbans.seebroadcast
Configuration Guide

http://dev.bukkit.org/server-mods/maxbans/pages/config-tutorial/

This is an in-depth guide on how to configure MaxBans :) If I've missed anything, ask in the comments at http://dev.bukkit.org/bukkit-plugins/maxbans/

Common Issues
=======

http://dev.bukkit.org/server-mods/maxbans/pages/common-issues/

This is a list of common issues people have with MaxBans, such as plugin conflicts.

Check out this guy's work for an amazing webpage setup to view MaxBans while using MySQL. Link: http://yive.me/maxbans/. I haven't personally set it up, but the page looks quite sleek and well done!

GeoIP Lookup
=======

MaxBans will download a GeoIP.csv file, which allows it to look up the country of origin for IP addresses. The file is approx. 1.7MB and is downloaded from http://maxgamer.org/plugins/maxbans/geoip.csv automatically on the first run. The file is only downloaded once (Unless it is renamed/removed).
