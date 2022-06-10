package com.community.easeim.imkit.modules.chat.interfaces;

import com.hyphenate.chat.EMMessage;

/**
 * 发送消息前，添加属性事件
 */
public interface OnAddMsgAttrsBeforeSendEvent {
    /**
     * 发送消息前设置消息属性，比如设置ext
     * @param message
     * @return
     */
    void addMsgAttrsBeforeSend(EMMessage message);
}
