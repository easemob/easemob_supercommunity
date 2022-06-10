package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.utils.CommonUtil;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroundGroupBean> mList = new ArrayList<>();

    public void setData(List<GroundGroupBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroundGroupBean group = mList.get(position);
        ChannelHolder channelHolder = (ChannelHolder) holder;
        channelHolder.mTvName.setText(group.getGroupName());
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId());
        if (conversation != null) {
            int unreadCount = conversation.getUnreadMsgCount();
            CommonUtil.getInstance().setShowNum(channelHolder.mTvUnread,unreadCount);
        } else {
            channelHolder.mTvUnread.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(group);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(GroundGroupBean group);
    }

    protected class ChannelHolder extends RecyclerView.ViewHolder {

        protected TextView mTvName;
        protected TextView mTvUnread;

        public ChannelHolder(@NonNull View itemView) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.tv_name);
            mTvUnread = itemView.findViewById(R.id.tv_unread);
        }
    }
}
