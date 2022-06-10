package com.community.easeim.section.chat.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.community.easeim.R;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.section.me.headImage.CustomImageUtil;


public class PickAllUserAdapter extends EaseBaseRecyclerViewAdapter<EaseUser> {
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new PickUserViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_widget_contact_item, parent, false));
    }

    private class PickUserViewHolder extends ViewHolder<EaseUser> {
        private TextView mHeader;
        private EaseImageView mAvatar;
        private TextView mName;
        private TextView mSignature;
        private TextView mUnreadMsgNumber;

        public PickUserViewHolder(@NonNull View itemView) {
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
            String header = EaseCommonUtils.getLetter(item.getNickname());
            Log.e("TAG", "GroupContactAdapter header = "+header);
            mHeader.setVisibility(View.GONE);
            // 是否显示字母
//            if(position == 0 || (header != null && !header.equals(EaseCommonUtils.getLetter(getItem(position - 1).getNickname())))) {
//                if(!TextUtils.isEmpty(header)) {
//                    mHeader.setVisibility(View.VISIBLE);
//                    mHeader.setText(header);
//                }
//            }
            mName.setText(item.getNickname());
            CustomImageUtil.getInstance().setHeadView(mAvatar,item.getAvatar());
        }
    }
}
