package com.community.easeim.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.viewholder.EaseImageViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowImage;
import com.hyphenate.chat.EMMessage;


/**
 * 图片代理类
 */
public class EaseImageAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseImageAdapterDelegate() {
    }

    public EaseImageAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.IMAGE;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowImage(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseImageViewHolder(view, itemClickListener);
    }
}
