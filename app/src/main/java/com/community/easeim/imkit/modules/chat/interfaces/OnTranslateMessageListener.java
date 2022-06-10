package com.community.easeim.imkit.modules.chat.interfaces;

import com.hyphenate.chat.EMMessage;

public interface OnTranslateMessageListener {

    /**
     * 翻译成功
     * @param message
     */
    void translateMessageSuccess(EMMessage message);

    /**
     * 翻译失败
     * @param message
     * @param code
     * @param error
     */
    void translateMessageFail(EMMessage message, int code, String error);
}
