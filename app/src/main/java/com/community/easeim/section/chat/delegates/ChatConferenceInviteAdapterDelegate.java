package com.community.easeim.section.chat.delegates;

import static com.hyphenate.chat.EMMessage.Type.TXT;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.imkit.delegate.EaseMessageAdapterDelegate;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.section.chat.viewholder.ChatConferenceInviteViewHolder;
import com.community.easeim.section.chat.views.ChatRowConferenceInvite;
import com.hyphenate.chat.EMMessage;

public class ChatConferenceInviteAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == TXT && !item.getStringAttribute(DemoConstant.MSG_ATTR_CONF_ID, "").equals("");
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowConferenceInvite(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatConferenceInviteViewHolder(view, itemClickListener);
    }
}
