package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.section.ground.bean.GroundGroupBean;

import java.util.ArrayList;
import java.util.List;

public class ChannelManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GroundGroupBean> mList = new ArrayList<>();

    public void setData(List<GroundGroupBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_manage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ManageHolder manageHolder = (ManageHolder) holder;
        GroundGroupBean group = mList.get(position);

        manageHolder.mTvName.setText(group.getGroupName());
        manageHolder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(group.getGroupId(),group.getGroupName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private OnDeleteListener mOnDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        mOnDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(String groupId,String name);
    }

    protected class ManageHolder extends RecyclerView.ViewHolder {

        protected TextView mTvName;
        protected ImageView mIvDelete;

        public ManageHolder(@NonNull View itemView) {
            super(itemView);

            mIvDelete = itemView.findViewById(R.id.iv_delete);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
