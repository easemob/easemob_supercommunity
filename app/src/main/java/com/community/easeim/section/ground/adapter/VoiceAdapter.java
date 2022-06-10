package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.voice.VoiceChannel;

import java.util.ArrayList;
import java.util.List;

public class VoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VoiceChannel> mList = new ArrayList<>();

    public void setData(List<VoiceChannel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VoiceChannel room = mList.get(position);
        ChannelHolder channelHolder = (ChannelHolder) holder;
        channelHolder.mTvName.setText(room.getChannelName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(room);
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
        void onItemClick(VoiceChannel room);
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
