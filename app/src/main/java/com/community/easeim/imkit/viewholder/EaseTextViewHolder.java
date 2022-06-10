package com.community.easeim.imkit.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.manager.EaseDingMessageHelper;
import com.community.easeim.imkit.ui.EaseDingAckUserListActivity;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowText;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

public class EaseTextViewHolder extends EaseChatRowViewHolder{

    public EaseTextViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static EaseChatRowViewHolder create(ViewGroup parent,
                                               boolean isSender, MessageListItemClickListener itemClickListener) {
        return new EaseTextViewHolder(new EaseChatRowText(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if (!EaseDingMessageHelper.get().isDingMessage(message) ||
                message.getChatType() != EMMessage.ChatType.GroupChat ||
                message.direct() != EMMessage.Direct.SEND) {
            return;
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
        i.putExtra("msg", message);
        getContext().startActivity(i);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        super.handleReceiveMessage(message);
        if(!EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
            //此处不再单独发送read_ack消息，改为进入聊天页面发送channel_ack
            //新消息在聊天页面的onReceiveMessage方法中，排除视频，语音和文件消息外，发送read_ack消息
            if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message);
    }
}
