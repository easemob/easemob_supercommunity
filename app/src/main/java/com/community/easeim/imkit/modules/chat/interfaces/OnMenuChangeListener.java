package com.community.easeim.imkit.modules.chat.interfaces;

import android.view.View;
import android.widget.PopupWindow;

import com.community.easeim.imkit.modules.menu.EasePopupWindowHelper;
import com.community.easeim.imkit.modules.menu.MenuItemBean;
import com.hyphenate.chat.EMMessage;


/**
 * {@link EasePopupWindowHelper}中的条目点击事件
 */
public interface OnMenuChangeListener {
    /**
     * 展示Menu之前
     * @param helper
     * @param message
     */
    void onPreMenu(EasePopupWindowHelper helper, EMMessage message, View v);

    /**
     * 点击条目
     * @param item
     * @param message
     */
    boolean onMenuItemClick(MenuItemBean item, EMMessage message);

    /**
     * 消失
     * @param menu
     */
    default void onDismiss(PopupWindow menu) {}
}