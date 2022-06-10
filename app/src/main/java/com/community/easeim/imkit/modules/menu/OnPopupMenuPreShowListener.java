package com.community.easeim.imkit.modules.menu;

public interface OnPopupMenuPreShowListener {
    /**
     * popupMenu展示前的监听，可以对PopupMenu进行设置
     * @param menuHelper {@link EasePopupMenuHelper}
     * @param position 条目位置
     */
    void onMenuPreShow(EasePopupMenuHelper menuHelper, int position);
}

