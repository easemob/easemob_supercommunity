package com.community.easeim.section.message.delegates;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Date;

public class InviteMsgDelegate extends EaseBaseDelegate<EMMessage, InviteMsgDelegate.ViewHolder> {
    private OnInviteListener listener;

    @Override
    public boolean isForViewType(EMMessage msg, int position) {
        String statusParams = null;
        try {
            statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
        return status == InviteMessageStatus.BEINVITEED ||
                status == InviteMessageStatus.BEAPPLYED ||
                status == InviteMessageStatus.GROUPINVITATION;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_layout_item_invite_msg_invite;
    }

    @Override
    protected InviteMsgDelegate.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EMMessage> {
        private TextView name;
        private TextView message;
        private Button agree;
        private Button refuse;
        private EaseImageView avatar;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            name = findViewById(R.id.name);
            message = findViewById(R.id.message);
            agree = findViewById(R.id.agree);
            refuse = findViewById(R.id.refuse);
            time = findViewById(R.id.time);
            avatar = findViewById(R.id.avatar);
            avatar.setShapeType(DemoHelper.getInstance().getEaseAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(EMMessage msg, int position) {
            String reason = null;
            String username = null;
            try {
                username = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM);
                reason = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_REASON);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(reason)) {
                String statusParams = null;
                try {
                    statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
                    InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                    if(status == InviteMessageStatus.BEINVITEED){
                        reason = name.getContext().getString(InviteMessageStatus.BEINVITEED.getMsgContent(), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM));
                    }else if (status == InviteMessageStatus.BEAPPLYED) { //application to join group
                        reason = name.getContext().getString(InviteMessageStatus.BEAPPLYED.getMsgContent()
                                , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_FROM), msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));
                    } else if (status == InviteMessageStatus.GROUPINVITATION) {
                        reason = name.getContext().getString(InviteMessageStatus.GROUPINVITATION.getMsgContent()
                                , msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_INVITER),  msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_NAME));

                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }

            String statusParams = msg.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS,"");
            if (!TextUtils.isEmpty(statusParams)) {
                InviteMessageStatus status = InviteMessageStatus.valueOf(statusParams);
                if (status == InviteMessageStatus.GROUPINVITATION) {
                    avatar.setBackgroundResource(R.drawable.invite_noti);
                } else {
                    avatar.setBackgroundResource(R.drawable.icon_invite);
                }
            }

            if (TextUtils.equals("welcome",reason)) {
                reason = ((EMTextMessageBody) msg.getBody()).getMessage();
                avatar.setBackgroundResource(R.drawable.invite_noti);
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
                            EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    name.setText(finalUsername);
                                    message.setText(finalReason);
                                }
                            });
                        }
                    });
                }
            });

            agree.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteAgree(view, msg);
                }
            });

            refuse.setOnClickListener(view -> {
                if(listener != null) {
                    listener.onInviteRefuse(view, msg);
                }
            });
        }
    }

    public void setOnInviteListener(OnInviteListener listener) {
        this.listener = listener;
    }

    public interface OnInviteListener {
        void onInviteAgree(View view, EMMessage msg);
        void onInviteRefuse(View view, EMMessage msg);
    }
}
