package com.community.easeim.section.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.easeim.DemoHelper;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.section.chat.views.chatRowUserCard;
import com.community.easeim.section.contact.activity.ContactDetailActivity;
import com.community.easeim.section.me.activity.UserDetailActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;

import java.util.Map;

public class ChatUserCardViewHolder extends EaseChatRowViewHolder {

    public ChatUserCardViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatUserCardViewHolder create(ViewGroup parent, boolean isSender,
                                                 MessageListItemClickListener itemClickListener) {
        return new ChatUserCardViewHolder(new chatRowUserCard(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
            String event = messageBody.event();
            if(event.equals(DemoConstant.USER_CARD_EVENT)){
                Map<String,String> params = messageBody.getParams();
                String uId = params.get(DemoConstant.USER_CARD_ID);
                String avatar = params.get(DemoConstant.USER_CARD_AVATAR);
                String nickname = params.get(DemoConstant.USER_CARD_NICK);
                if(uId != null && uId.length() > 0){
                    if(uId.equals(EMClient.getInstance().getCurrentUser())){
                        UserDetailActivity.actionStart(getContext(),nickname,avatar);
                    }else{
                        EaseUser user = DemoHelper.getInstance().getUserInfo(uId);
                        if(user == null){
                            user = new EaseUser(uId);
                            user.setAvatar(avatar);
                            user.setNickname(nickname);
                        }
                        boolean isFriend =  DemoHelper.getInstance().getModel().isContact(uId);
                        if(isFriend){
                            user.setContact(0);
                        }else{
                            user.setContact(3);
                        }
                        ContactDetailActivity.actionStart(getContext(),user);
                    }
                }else{
                    EMLog.e("ChatUserCardViewHolder","onBubbleClick uId is empty");
                }
            }
        }
    }
}
