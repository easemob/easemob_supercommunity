package com.community.easeim.section.ground.adapter;

import android.content.Context;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.domain.EaseAvatarOptions;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.modules.chat.model.EaseChatItemStyleHelper;
import com.community.easeim.imkit.modules.chat.model.EaseChatSetStyle;
import com.community.easeim.imkit.utils.EaseSmileUtils;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.section.ground.bean.MessageBean;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;

import java.util.ArrayList;
import java.util.List;

public class MsgRecordAdapter extends RecyclerView.Adapter<MsgRecordAdapter.MsgRecordViewHolder>{

    private List<MessageBean> mList = new ArrayList<>();
    private Context mContext;

    public MsgRecordAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<MessageBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MsgRecordAdapter.MsgRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MsgRecordViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_row_received_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MsgRecordViewHolder holder, int position) {
        MessageBean messageBean = mList.get(position);
        LeanCloudManager.getInstance().getUserInfo(messageBean.getMsg_from(), new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                Log.e("TAG", "onSuccess: value.getNickname()   "+ value.getNickname() );
                holder.mTvTitle.setText(value.getNickname());
                CustomImageUtil.getInstance().setHeadView(holder.userAvatarView, value.getAvatar());
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

        Spannable span = EaseSmileUtils.getSmiledText(mContext, messageBean.getPayload().toString());
        // 设置内容
        holder.mTvContent.setText(span, TextView.BufferType.SPANNABLE);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MsgRecordViewHolder extends RecyclerView.ViewHolder{
        public TextView mTvContent;
        public TextView mTvTitle;
        public ImageView userAvatarView;
        private View translationContainer,bubbleLayout;

        public MsgRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarView = itemView.findViewById(R.id.iv_userhead);
            mTvTitle = itemView.findViewById(R.id.tv_userid);
            mTvContent = itemView.findViewById(R.id.tv_chatcontent);
            bubbleLayout = itemView.findViewById(R.id.bubble);
            bubbleLayout.setPadding(0, 0, 0, 0);
            bubbleLayout.setBackgroundResource(R.drawable.ease_chat_bubble_transparent);
            translationContainer = (View) itemView.findViewById(R.id.subBubble);
            translationContainer.setVisibility(View.GONE);

            userAvatarView.setVisibility(View.VISIBLE);
            if (userAvatarView instanceof EaseImageView) {
                EaseChatItemStyleHelper helper = EaseChatItemStyleHelper.getInstance();
                EaseChatSetStyle itemStyle = helper.getStyle();
                EaseImageView avatarView = (EaseImageView) userAvatarView;
                if (itemStyle.getAvatarDefaultSrc() != null) {
                    avatarView.setImageDrawable(itemStyle.getAvatarDefaultSrc());
                }
                avatarView.setShapeType(itemStyle.getShapeType());
                if (itemStyle.getAvatarSize() != 0) {
                    ViewGroup.LayoutParams params = avatarView.getLayoutParams();
                    params.width = (int) itemStyle.getAvatarSize();
                    params.height = (int) itemStyle.getAvatarSize();
                }
                if (itemStyle.getBorderWidth() != 0) {
                    avatarView.setBorderWidth((int) itemStyle.getBorderWidth());
                }
                if (itemStyle.getBorderColor() != 0) {
                    avatarView.setBorderColor(itemStyle.getBorderColor());
                }
                if (itemStyle.getAvatarRadius() != 0) {
                    avatarView.setRadius((int) itemStyle.getAvatarRadius());
                }
            }
            EaseAvatarOptions avatarOptions = EaseIM.getInstance().getAvatarOptions();
            if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
                EaseImageView avatarView = ((EaseImageView) userAvatarView);
                if (avatarOptions.getAvatarShape() != 0)
                    avatarView.setShapeType(avatarOptions.getAvatarShape());
                if (avatarOptions.getAvatarBorderWidth() != 0)
                    avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
                if (avatarOptions.getAvatarBorderColor() != 0)
                    avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
                if (avatarOptions.getAvatarRadius() != 0)
                    avatarView.setRadius(avatarOptions.getAvatarRadius());
            }
        }
    }
}
