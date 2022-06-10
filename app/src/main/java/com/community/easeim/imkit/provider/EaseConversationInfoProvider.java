package com.community.easeim.imkit.provider;

import android.graphics.drawable.Drawable;

public interface EaseConversationInfoProvider {
    /**
     * 获取默认类型头像
     * @param type
     * @return
     */
    Drawable getDefaultTypeAvatar(String type);
}
