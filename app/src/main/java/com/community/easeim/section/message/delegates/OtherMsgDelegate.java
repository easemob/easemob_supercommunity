package com.community.easeim.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.common.manager.PushAndMessageHelper;
import com.community.easeim.imkit.adapter.EaseBaseDelegate;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.utils.EaseDateUtils;
import com.community.easeim.imkit.utils.EaseUserUtils;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.voice.Callback;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Date;

public class OtherMsgDelegate extends EaseBaseDelegate<EMMessage, OtherMsgDelegate.ViewHolder> {

    @Override
    public boolean isForViewType(EMMessage msg, int position) {
        String statusParams = null;
        try {
            statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
        return status != InviteMessageStatus.BEINVITEED &&
                status != InviteMessageStatus.BEAPPLYED &&
                status != InviteMessageStatus.GROUPINVITATION &&
                status != InviteMessageStatus.BEAGREED;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_agree;
    }

    @Override
    protected OtherMsgDelegate.ViewHolder createViewHolder(View view) {
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
            String username = null;
            String str = null;
            try {
                username = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM);
                 str = PushAndMessageHelper.getSystemMessage(msg);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            name.setText(username);
            message.setText(str);

            time.setText(EaseDateUtils.getTimestampString(itemView.getContext(), new Date(msg.getMsgTime())));

            if (TextUtils.equals(username,"欢迎来到环信超级社区！")) {
                name.setText(username);
                EMTextMessageBody txtBody = (EMTextMessageBody) msg.getBody();
                message.setText(txtBody.getMessage());
                avatar.setBackgroundResource(R.drawable.ann_noti);
            } else {
                String statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS,"");
                if (!TextUtils.isEmpty(statusParams)) {
                    InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);

                    if (status == InviteMessageStatus.GROUP_USER_REMOVED) {
                        EMTextMessageBody txtBody = (EMTextMessageBody) msg.getBody();
                        message.setText(txtBody.getMessage());
                        avatar.setBackgroundResource(R.drawable.invite_noti);
                    } else if (status == InviteMessageStatus.GROUPINVITATION_ACCEPTED || status == InviteMessageStatus.GROUPINVITATION_DECLINED ) {
                        avatar.setBackgroundResource(R.drawable.invite_noti);
                    } else {
                        avatar.setBackgroundResource(R.drawable.icon_invite);
                    }
                } else {
                    avatar.setBackgroundResource(R.drawable.icon_invite);
                }

                String finalUsername = username;
                String finalReason = str;
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

                            }
                        });
                    }
                });
            }
        }
    }
}
