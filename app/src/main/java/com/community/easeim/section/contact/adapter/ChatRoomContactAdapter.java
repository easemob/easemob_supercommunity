package com.community.easeim.section.contact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.R;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.widget.EaseImageView;
import com.hyphenate.chat.EMChatRoom;


public class ChatRoomContactAdapter extends EaseBaseRecyclerViewAdapter<EMChatRoom> {
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    private class GroupViewHolder extends ViewHolder<EMChatRoom> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            mHeader = findViewById(R.id.header);
            mAvatar = findViewById(R.id.avatar);
            mName = findViewById(R.id.name);
            mSignature = findViewById(R.id.signature);
            mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
        }

        @Override
        public void setData(EMChatRoom item, int position) {
            mName.setText(item.getName());
            mAvatar.setImageResource(R.drawable.ease_chat_room_icon);
            mSignature.setVisibility(View.VISIBLE);
            mSignature.setText(item.getId()+"");
        }
    }
}
