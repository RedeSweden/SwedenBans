package com.redesweden.swedenbans.banmanager;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;
import com.redesweden.swedenbans.util.IPAddress;
import com.redesweden.swedenbans.util.Util;

public class TempRangeBan extends RangeBan implements Temporary
{
    private long expires;
    
    public TempRangeBan(final String banner, final String reason, final long created, final long expires, final IPAddress start, final IPAddress end) {
        super(banner, reason, created, start, end);
        this.expires = expires;
    }
    
    public long getExpires() {
        return this.expires;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() > this.expires;
    }
    
    public String getKickMessage() {
        return Msg.get("disconnection.you-are-temp-rangebanned", new String[] { "reason", "banner", "appeal-message", "range", "time" }, new String[] { this.getReason(), this.getBanner(), SwedenBans.instance.getBanManager().getAppealMessage(this.getBanner(), this.getId()), this.toString(), Util.getTimeUntil(this.expires) });
    }
}
