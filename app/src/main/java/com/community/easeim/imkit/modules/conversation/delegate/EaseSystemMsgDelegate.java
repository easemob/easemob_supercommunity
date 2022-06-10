package com.community.easeim.imkit.modules.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.community.easeim.R;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseSystemMsgManager;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.modules.conversation.model.EaseConversationInfo;
import com.community.easeim.imkit.modules.conversation.model.EaseConversationSetStyle;
import com.community.easeim.imkit.provider.EaseConversationInfoProvider;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.utils.EaseDateUtils;
import com.community.easeim.imkit.utils.EaseSmileUtils;
import com.community.easeim.imkit.utils.EaseUserUtils;
import com.community.easeim.voice.Callback;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.Date;

public class EaseSystemMsgDelegate extends EaseDefaultConversationDelegate {

    public EaseSystemMsgDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof EMConversation
                && EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item.getInfo());
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
        EaseConversationInfoProvider infoProvider = EaseIM.getInstance().getConversationInfoProvider();
        holder.avatar.setImageResource(R.drawable.icon_system);
        holder.name.setText(holder.mContext.getString(R.string.ease_conversation_system_message));
        if(infoProvider != null) {
            Drawable avatar = infoProvider.getDefaultTypeAvatar(EaseConstant.DEFAULT_SYSTEM_MESSAGE_TYPE);
            if(avatar != null) {
                Glide.with(holder.mContext).load(avatar).error(R.drawable.icon_system).into(holder.avatar);
            }
        }

        if(!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if(item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            Spannable text = EaseSmileUtils.getSmiledText(context, EaseCommonUtils.getMessageDigest(lastMessage, context));
            holder.time.setText(EaseDateUtils.getTimestampString(holder.itemView.getContext(), new Date(lastMessage.getMsgTime())));

            String from = (String) lastMessage.ext().get("from");
            if (TextUtils.isEmpty(from)){
                holder.message.setText(text);
                return;
            }
            if (TextUtils.equals(from,"欢迎来到环信超级社区！")) {
                holder.message.setText(from);
                return;
            }

            String finalReason = text.toString();
            EaseThreadManager.getInstance().runOnIOThread(new Runnable() {
                @Override
                public void run() {
                    EaseUserUtils.getUserInfo(from, new Callback<EaseUser>() {
                        @Override
                        public void onSuccess(EaseUser easeUser) {
                            EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    String reason = finalReason;

                                    if (reason.contains(from)) {
                                        if (easeUser !=null) {
                                            reason = reason.replace(from, easeUser.getNickname());
                                        }
                                    }
                                    holder.message.setText(reason);
                                }
                            });
                        }

                        @Override
                        public void onError(String err) {
                            holder.message.setText(text);
                        }
                    });
                }
            });

        }
    }
}

