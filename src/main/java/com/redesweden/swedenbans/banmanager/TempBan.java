package com.redesweden.swedenbans.banmanager;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.util.Util;

public class TempBan extends Ban implements Temporary
{
    private long expires;
    
    public TempBan(final String user, final String reason, final String banner, final long created, final long expires) {
        super(user, reason, banner, created);
        this.expires = expires;
    }
    
    public long getExpires() {
        return this.expires;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() > this.expires;
    }
    
    public String getKickMessage() {
        return Msg.get("disconnection.you-are-temp-banned", new String[] { "reason", "banner", "time", "appeal-message" }, new String[] { this.getReason(), this.getBanner(), Util.getTimeUntil(this.expires), SwedenBans.instance.getBanManager().getAppealMessage() });
    }
}
