package com.redesweden.swedenbans.banmanager;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;

public class IPBan extends Ban
{
    public IPBan(final String ip, final String reason, final String banner, final long created) {
        super(ip, reason, banner, created);
    }
    
    public String getKickMessage() {
        return Msg.get("disconnection.you-are-ipbanned", new String[] { "reason", "banner", "appeal-message" }, new String[] { this.getReason(), this.getBanner(), SwedenBans.instance.getBanManager().getAppealMessage() });
    }
}
