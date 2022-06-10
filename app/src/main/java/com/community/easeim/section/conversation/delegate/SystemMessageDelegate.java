package com.community.easeim.section.conversation.delegate;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.community.easeim.R;
import com.community.easeim.common.db.entity.InviteMessage;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.common.db.entity.MsgTypeManageEntity;
import com.community.easeim.common.manager.PushAndMessageHelper;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.adapter.EaseBaseDelegate;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.utils.EaseDateUtils;
import com.community.easeim.imkit.widget.EaseImageView;

import java.util.Date;

public class SystemMessageDelegate extends EaseBaseDelegate<MsgTypeManageEntity, SystemMessageDelegate.ViewHolder> {
    @Override
    public boolean isForViewType(MsgTypeManageEntity item, int position) {
        return item != null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ease_item_row_chat_history;
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<MsgTypeManageEntity> {
        private ConstraintLayout listIteaseLayout;
        private EaseImageView avatar;
        private TextView mUnreadMsgNumber;
        private TextView name;
        private TextView time;
        private ImageView mMsgState;
        private TextView mentioned;
        private TextView message;
        private Context mContext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mContext = itemView.getContext();
            listIteaseLayout = findViewById(R.id.list_itease_layout);
            avatar = findViewById(R.id.avatar);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
            name = findViewById(R.id.name);
            time = findViewById(R.id.time);
            mMsgState = findViewById(R.id.msg_state);
            mentioned = findViewById(R.id.mentioned);
            message = findViewById(R.id.message);
            avatar.setShapeType(EaseIM.getInstance().getAvatarOptions().getAvatarShape());
        }

        @Override
        public void setData(MsgTypeManageEntity object, int position) {
            String type = ((MsgTypeManageEntity) object).getType();
            Object lastMsg = ((MsgTypeManageEntity) object).getLastMsg();
            if(lastMsg == null || TextUtils.isEmpty(type)) {
                return;
            }
            listIteaseLayout.setBackground(!TextUtils.isEmpty(((MsgTypeManageEntity) object).getExtField())
                    ? ContextCompat.getDrawable(mContext, R.drawable.ease_conversation_top_bg)
                    : null);
            if(TextUtils.equals(type, MsgTypeManageEntity.msgType.NOTIFICATION.name())) {
                avatar.setImageResource(R.drawable.icon_system);
                name.setText(mContext.getString(R.string.em_conversation_system_notification));
            }
            int unReadCount = ((MsgTypeManageEntity) object).getUnReadCount();
            if(unReadCount > 0) {
                mUnreadMsgNumber.setText(String.valueOf(unReadCount));
                mUnreadMsgNumber.setVisibility(View.VISIBLE);
            }else {
                mUnreadMsgNumber.setVisibility(View.GONE);
            }
            if(lastMsg instanceof InviteMessage) {
                time.setText(EaseDateUtils.getTimestampString(mContext, new Date(((InviteMessage) lastMsg).getTime())));
                InviteMessageStatus status = ((InviteMessage) lastMsg).getStatusEnum();
                if(status == null) {
                    return;
                }
                String reason = ((InviteMessage) lastMsg).getReason();
                if(status == InviteMessageStatus.BEINVITEED ||
                        status == InviteMessageStatus.BEAPPLYED ||
                        status == InviteMessageStatus.GROUPINVITATION ||
                        status == InviteMessageStatus.AGREED) {
                    message.setText(TextUtils.isEmpty(reason) ? PushAndMessageHelper.getSystemMessage((InviteMessage) lastMsg) : reason);
                }else {
                    message.setText(PushAndMessageHelper.getSystemMessage((InviteMessage) lastMsg));
                }
            }
        }
    }
}
