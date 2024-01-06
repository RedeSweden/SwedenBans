package com.redesweden.swedenbans.banmanager;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.util.Util;

public class TempIPBan extends IPBan implements Temporary
{
    private long expires;
    
    public TempIPBan(final String ip, final String reason, final String banner, final long created, final long expires) {
        super(ip, reason, banner, created);
        this.expires = expires;
    }
    
    public long getExpires() {
        return this.expires;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() > this.expires;
    }
    
    public String getKickMessage() {
        return Msg.get("disconnection.you-are-temp-ipbanned", new String[] { "reason", "banner", "time", "appeal-message" }, new String[] { this.getReason(), this.getBanner(), Util.getTimeUntil(this.expires), SwedenBans.instance.getBanManager().getAppealMessage() });
    }
}
