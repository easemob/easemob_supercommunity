package com.community.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.section.av.VideoCallActivity;
import com.community.easeim.section.chat.views.ChatRowVideoCall;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;


public class ChatVideoCallViewHolder extends EaseChatRowViewHolder {

    public ChatVideoCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatVideoCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatVideoCallViewHolder(new ChatRowVideoCall(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.direct() == EMMessage.Direct.SEND) {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL,message.getTo(),null, VideoCallActivity.class);
        }else {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL,message.getFrom(),null, VideoCallActivity.class);
        }
    }
}
