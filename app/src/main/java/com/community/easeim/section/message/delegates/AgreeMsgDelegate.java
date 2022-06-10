package com.community.easeim.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.imkit.adapter.EaseBaseDelegate;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.utils.EaseDateUtils;
import com.community.easeim.imkit.utils.EaseUserUtils;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.voice.Callback;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Date;

public class AgreeMsgDelegate extends EaseBaseDelegate<EMMessage, AgreeMsgDelegate.ViewHolder> {

    @Override
    public boolean isForViewType(EMMessage msg, int position) {
        String statusParams = null;
        try {
            statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
        return status == InviteMessageStatus.BEAGREED
                || status == InviteMessageStatus.AGREED;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_agree;
    }

    @Override
    protected AgreeMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EMMessage> {
        private TextView name;
        private TextView message;
        private EaseImageView avatar;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            avatar = findViewById(R.id.avatar);
            time = findViewById(R.id.time);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(EMMessage msg, int position) {
            String reason = null;
            String username = "";
            try {
                username = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM);
                reason = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_REASON);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(reason)) {
                String statusParams = null;
                try {
                    statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                    InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                    if (status == InviteMessageStatus.AGREED) {
                        reason = name.getContext().getString(InviteMessageStatus.AGREED.getMsgContent(), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    } else if (status == InviteMessageStatus.BEAGREED) {
                        reason = name.getContext().getString(InviteMessageStatus.BEAGREED.getMsgContent());
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
            if (reason.contains("入群")){
                avatar.setBackgroundResource(R.drawable.invite_noti);
            } else {
                avatar.setBackgroundResource(R.drawable.icon_invite);
            }

            time.setText(EaseDateUtils.getTimestampString(itemView.getContext(), new Date(msg.getMsgTime())));

            String finalUsername = username;
            String finalReason = reason;
            EaseThreadManager.getInstance().runOnIOThread(new Runnable() {
                @Override
                public void run() {
                    EaseUserUtils.getUserInfo(finalUsername, new Callback<EaseUser>() {
                        @Override
                        public void onSuccess(EaseUser easeUser) {
                            EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    name.setText(easeUser.getNickname());
                                    String reason = finalReason;
                                    if (reason.contains(finalUsername)) {
                                        reason = reason.replace(finalUsername, easeUser.getNickname());
                                    }
                                    message.setText(reason);
                                }
                            });
                        }

                        @Override
                        public void onError(String err) {
                            name.setText(finalUsername);
                            message.setText(finalReason);
                        }
                    });
                }
            });
        }
    }
}
