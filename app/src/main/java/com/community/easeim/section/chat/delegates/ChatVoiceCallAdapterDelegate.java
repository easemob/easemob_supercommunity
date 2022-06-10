package com.community.easeim.section.chat.delegates;

import static com.hyphenate.chat.EMMessage.Type.TXT;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.delegate.EaseMessageAdapterDelegate;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.section.chat.viewholder.ChatVoiceCallViewHolder;
import com.community.easeim.section.chat.views.ChatRowVoiceCall;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseMsgUtils;

public class ChatVoiceCallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        boolean isRtcCall =item.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE,"").equals(EaseMsgUtils.CALL_MSG_INFO)?true:false;
        boolean isVoiceCall = item.getIntAttribute(EaseMsgUtils.CALL_TYPE,0) == EaseCallType.SINGLE_VOICE_CALL.code?true:false;
        return item.getType() == TXT && isRtcCall && isVoiceCall;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowVoiceCall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatVoiceCallViewHolder(view, itemClickListener);
    }
}
