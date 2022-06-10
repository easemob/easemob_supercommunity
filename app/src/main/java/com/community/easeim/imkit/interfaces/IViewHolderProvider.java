package com.community.easeim.imkit.interfaces;

import android.view.ViewGroup;

import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.hyphenate.chat.EMMessage;


/**
 * 开发者可以通过实现下面的两个接口，提供相应的ViewHolder和ViewType
 */
public interface IViewHolderProvider {
    /**
     * 提供对应的ViewHolder
     * @return key指的的对应的viewType, value为对应的ViewHolder
     * @param parent
     * @param itemClickListener
     * @param itemStyle
     */
    EaseChatRowViewHolder provideViewHolder(ViewGroup parent, int viewType, MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle);

    /**
     * 根据消息类型提供相对应的view type
     * @param message
     * @return 返回的为viewType
     */
    int provideViewType(EMMessage message);

}
