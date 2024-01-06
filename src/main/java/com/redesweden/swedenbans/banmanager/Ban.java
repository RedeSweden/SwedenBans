package com.redesweden.swedenbans.banmanager;

import com.redesweden.swedenbans.SwedenBans;
import com.redesweden.swedenbans.Msg;

public class Ban extends Punishment
{
    public Ban(final String user, final String reason, final String banner, final long created) {
        super(user, reason, banner, created);
    }
    
    public String getKickMessage() {
        return Msg.get("disconnection.you-are-banned", new String[] { "reason", "banner", "appeal-message" }, new String[] { this.getReason(), this.getBanner(), SwedenBans.instance.getBanManager().getAppealMessage() });
    }
}
