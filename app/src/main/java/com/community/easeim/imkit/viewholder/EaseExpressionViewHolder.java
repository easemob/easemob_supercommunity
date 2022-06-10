package com.community.easeim.imkit.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowBigExpression;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

public class EaseExpressionViewHolder extends EaseChatRowViewHolder {

    public EaseExpressionViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static EaseChatRowViewHolder create(ViewGroup parent
            , boolean isSender, MessageListItemClickListener itemClickListener) {
        return new EaseExpressionViewHolder(new EaseChatRowBigExpression(parent.getContext(), isSender), itemClickListener);
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
            }
        }
    }
}
