package com.community.easeim.section.friend.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.DemoApplication;
import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.db.DemoDbHelper;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

public class SearchContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EaseUser> mList = new ArrayList<>();
    private OnAddClickListener mOnAddClickListener;
    private final List<String> mAllContacts;

    public void setData(List<EaseUser> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public SearchContactAdapter() {
        mAllContacts = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao().loadContactUsers();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_search, parent, false));
    }

    private List<String> sentFriend = new ArrayList<>();

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        EaseUser easeUser = mList.get(position);

        CustomImageUtil.getInstance().setHeadView(normalHolder.mCivHead, easeUser.getAvatar());

        normalHolder.mTvName.setText(easeUser.getNickname());
        normalHolder.mTvId.setText(easeUser.getUsername());

        if (DemoHelper.getInstance().getModel().isContact(easeUser.getUsername()) || TextUtils.equals(EMClient.getInstance().getCurrentUser(), easeUser.getUsername())) {
            normalHolder.mTvAdd.setVisibility(View.GONE);
        } else {
            normalHolder.mTvAdd.setVisibility(View.VISIBLE);
        }

        normalHolder.mTvAdd.setText(sentFriend.contains(easeUser.getUsername()) ? "已发送" : "加好友");
        normalHolder.mTvAdd.setBackgroundResource(sentFriend.contains(easeUser.getUsername()) ? R.drawable.sp_radius_48_47464b : R.drawable.sp_radius_48_27ae60);
        normalHolder.mTvAdd.setEnabled(!sentFriend.contains(easeUser.getUsername()));

        normalHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAddClickListener != null) {
                    mOnAddClickListener.onItemClick(easeUser);
                }
            }
        });

        normalHolder.mTvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAddClickListener != null) {
                    sentFriend.add(easeUser.getUsername());
                    notifyItemChanged(position);
                    mOnAddClickListener.onAdd(easeUser);
                }
            }
        });
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        mOnAddClickListener = onAddClickListener;
    }

    public interface OnAddClickListener {
        void onAdd(EaseUser easeUser);

        void onItemClick(EaseUser easeUser);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class NormalHolder extends RecyclerView.ViewHolder {

        protected CircleImageView mCivHead;
        protected TextView mTvName;
        protected TextView mTvNum;
        protected TextView mTvId;
        protected TextView mTvAdd;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);

            mCivHead = itemView.findViewById(R.id.civ_head);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvId = itemView.findViewById(R.id.tv_id);
            mTvAdd = itemView.findViewById(R.id.tv_add);

        }
    }
}
