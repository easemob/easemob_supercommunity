package com.community.easeim.imkit.modules.chat.interfaces;

import android.view.MotionEvent;
import android.view.View;

public interface OnChatRecordTouchListener {
    /**
     * 语音按压事件
     * @param v
     * @param event
     * @return
     */
    boolean onRecordTouch(View v, MotionEvent event);
}
