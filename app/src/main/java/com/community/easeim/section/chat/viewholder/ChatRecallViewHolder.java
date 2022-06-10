package com.community.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.section.chat.views.ChatRowRecall;


public class ChatRecallViewHolder extends EaseChatRowViewHolder {

    public ChatRecallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatRecallViewHolder create(ViewGroup parent, boolean isSender,
                                              MessageListItemClickListener listener) {
        return new ChatRecallViewHolder(new ChatRowRecall(parent.getContext(), isSender), listener);
    }


}
