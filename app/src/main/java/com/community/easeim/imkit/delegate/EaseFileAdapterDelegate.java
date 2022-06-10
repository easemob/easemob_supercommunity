package com.community.easeim.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.viewholder.EaseFileViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowFile;
import com.hyphenate.chat.EMMessage;


/**
 * 文件代理类
 */
public class EaseFileAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseFileAdapterDelegate() {
    }

    public EaseFileAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.FILE;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowFile(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseFileViewHolder(view, itemClickListener);
    }
}
