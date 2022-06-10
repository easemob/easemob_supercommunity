package com.community.easeim.section.chat.delegates;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.imkit.delegate.EaseMessageAdapterDelegate;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.section.chat.viewholder.ChatUserCardViewHolder;
import com.community.easeim.section.chat.views.chatRowUserCard;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;



public class ChatUserCardAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if(item.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) item.getBody();
            String event = messageBody.event();
            return event.equals(DemoConstant.USER_CARD_EVENT)?true:false;
        }
        return false;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new chatRowUserCard(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(view, itemClickListener);
    }
}

