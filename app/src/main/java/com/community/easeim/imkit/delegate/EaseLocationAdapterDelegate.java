package com.community.easeim.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.viewholder.EaseLocationViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowLocation;
import com.hyphenate.chat.EMMessage;


/**
 * 定位代理类
 */
public class EaseLocationAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseLocationAdapterDelegate() {
    }

    public EaseLocationAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.LOCATION;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowLocation(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseLocationViewHolder(view, itemClickListener);
    }
}
