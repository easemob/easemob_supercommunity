package com.community.easeim.section.friend.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.utils.KeyWordUtil;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.community.easeim.section.me.headImage.CustomImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EaseUser> mList = new ArrayList<>();
    private String mKey;
    private OnItemClickListener<EaseUser> mOnItemClickListener;

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;

    public void setData(List<EaseUser> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setData(List<EaseUser> list, String key) {
        mList = list;
        mKey = key;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_empty, viewGroup, false));
        }
        return new NormalHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (mList == null || mList.isEmpty())) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    public void setOnItemClickListener(OnItemClickListener<EaseUser> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL){
            EaseUser userInfo = mList.get(position);

            NormalHolder holder = (NormalHolder) viewHolder;
            if (TextUtils.isEmpty(mKey) || TextUtils.isEmpty(mKey.trim())) {
                holder.mTvName.setText(userInfo.getNickname());
            } else {
                holder.mTvName.setText(KeyWordUtil.matcherSearchTitleTextColor(Color.parseColor("#27AE60"), userInfo.getNickname(), mKey));
            }

            CustomImageUtil.getInstance().setHeadView(holder.mCivHead, userInfo.getAvatar());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(userInfo);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class NormalHolder extends RecyclerView.ViewHolder {

        protected TextView mTvName;
        protected CircleImageView mCivHead;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);

            mCivHead = itemView.findViewById(R.id.civ_head);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }

    protected class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
