package com.community.easeim.section.contact.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.R;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.provider.EaseUserProfileProvider;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.chat.EMClient;


public class ContactListAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.demo_layout_friends_empty_list;
    }

    private class MyViewHolder extends ViewHolder<EaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public MyViewHolder(@NonNull View itemView) {
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
        public void setData(EaseUser item, int position) {
            Log.e("TAG", "item = "+item.toString());
            String header = item.getInitialLetter();
            Log.e("TAG", "position = "+position + " header = "+header);
            mHeader.setVisibility(View.GONE);
            if(position == 0 || (header != null && !header.equals(getItem(position -1).getInitialLetter()))) {
                if(!TextUtils.isEmpty(header)) {
                    mHeader.setVisibility(View.VISIBLE);
                    mHeader.setText(header);
                }
            }
            //判断是否为自己账号多端登录
            String username = item.getUsername();
            String nickname = item.getNickname();
            if(username.contains("/") && username.contains(EMClient.getInstance().getCurrentUser())) {
                username = EMClient.getInstance().getCurrentUser();
            }
            EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
            if(userProvider != null) {
                EaseUser user = userProvider.getUser(username);
                if(user != null) {
                    nickname = user.getNickname();
                    CustomImageUtil.getInstance().setHeadView(mAvatar,user.getAvatar());
                }
            }
            String postfix = "";
            if(username.contains("/") && username.contains(EMClient.getInstance().getCurrentUser())) {
                postfix = "/"+username.split("/")[1];
                nickname = nickname+postfix;
            }
            mName.setText(nickname);
        }
    }
}
