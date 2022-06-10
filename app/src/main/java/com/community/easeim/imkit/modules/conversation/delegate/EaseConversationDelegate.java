package com.community.easeim.imkit.modules.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.community.easeim.DemoApplication;
import com.community.easeim.R;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseAtMessageHelper;
import com.community.easeim.imkit.manager.EasePreferenceManager;
import com.community.easeim.imkit.modules.conversation.model.EaseConversationInfo;
import com.community.easeim.imkit.modules.conversation.model.EaseConversationSetStyle;
import com.community.easeim.imkit.provider.EaseConversationInfoProvider;
import com.community.easeim.imkit.provider.EaseUserProfileProvider;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.utils.EaseDateUtils;
import com.community.easeim.imkit.utils.EaseSmileUtils;
import com.community.easeim.imkit.utils.EaseUserUtils;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;

import java.util.Date;

public class EaseConversationDelegate extends EaseDefaultConversationDelegate {

    public EaseConversationDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof EMConversation;
    }

    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        EMConversation item = (EMConversation) bean.getInfo();
        Context context = holder.itemView.getContext();
        String username = item.conversationId();
        holder.listIteaseLayout.setBackground(!TextUtils.isEmpty(item.getExtField())
                ? ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg)
                : null);
        holder.mentioned.setVisibility(View.GONE);
        int defaultAvatar = 0;
        String showName = null;
        if (item.getType() == EMConversation.EMConversationType.GroupChat) {
            if (EaseAtMessageHelper.get().hasAtMeMsg(username)) {
                holder.mentioned.setText(R.string.were_mentioned);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
            defaultAvatar = R.drawable.ease_group_icon;
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            showName = group != null ? group.getGroupName() : username;
        } else if (item.getType() == EMConversation.EMConversationType.ChatRoom) {
            defaultAvatar = R.drawable.ease_chat_room_icon;
            EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().getChatRoom(username);
            showName = chatRoom != null && !TextUtils.isEmpty(chatRoom.getName()) ? chatRoom.getName() : username;
        } else {
            defaultAvatar = R.drawable.ease_default_avatar;
            showName = username;
        }
//        holder.avatar.setImageResource(defaultAvatar);
//        holder.name.setText(showName);
        EaseConversationInfoProvider infoProvider = EaseIM.getInstance().getConversationInfoProvider();
        if (infoProvider != null) {
            Drawable avatarResource = infoProvider.getDefaultTypeAvatar(item.getType().name());
            if (avatarResource != null) {
                Glide.with(holder.mContext).load(avatarResource).error(defaultAvatar).into(holder.avatar);
            }
        }
        // add judgement for conversation type
        if (item.getType() == EMConversation.EMConversationType.Chat) {
//            EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
//            if (userProvider != null) {
//                EaseUser user = userProvider.getUser(username);
//                if (user != null) {
//                    if (!TextUtils.isEmpty(user.getNickname())) {
//                        holder.name.setText(user.getNickname());
//                    }
            EaseUserUtils.setUserNick(username, holder.name);
            EaseUserUtils.setUserAvatar(username, holder.avatar);
//                }
//            }
        }

        if (!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if (item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseCommonUtils.getMessageDigest(lastMessage, context)));
            holder.time.setText(EaseDateUtils.getTimestampString(context, new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.mMsgState.setVisibility(View.VISIBLE);
            } else {
                holder.mMsgState.setVisibility(View.GONE);
            }
        }

        if (holder.mentioned.getVisibility() != View.VISIBLE) {
            String unSendMsg = EasePreferenceManager.getInstance().getUnSendMsgInfo(username);
            if (!TextUtils.isEmpty(unSendMsg)) {
                holder.mentioned.setText(R.string.were_not_send_msg);
                holder.message.setText(unSendMsg);
                holder.mentioned.setVisibility(View.VISIBLE);
            }
        }
    }
}

