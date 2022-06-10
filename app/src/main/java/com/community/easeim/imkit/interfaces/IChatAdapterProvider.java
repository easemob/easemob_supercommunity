package com.community.easeim.imkit.interfaces;

import com.community.easeim.imkit.adapter.EaseBaseMessageAdapter;
import com.hyphenate.chat.EMMessage;


public interface IChatAdapterProvider {
    /**
     * provide chat message's adapter
     * if is null , will use default {@link com.hyphenate.easeui.adapter.EaseMessageAdapter}
     * @return
     */
    EaseBaseMessageAdapter<EMMessage> provideMessageAdaper();
}
