package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.ground.bean.MemberBean;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean mShowRole = true;
    private List<MemberBean> mList = new ArrayList<>();
    private boolean mIsOwner;
    private boolean mIsAdmin;
    private List<MemberBean> mCheckList = new ArrayList<>();

    public void hideRole() {
        mShowRole = false;
    }

    public void setData(List<MemberBean> list) {
        mList = list;
        filter(MemberBean.ONLINE_ALL);
        notifyDataSetChanged();
    }

    public void setData(List<MemberBean> list, boolean isOwner, boolean isAdmin) {
        mList = list;
        mIsOwner = isOwner;
        mIsAdmin = isAdmin;
        filter(MemberBean.ONLINE_ALL);
        notifyDataSetChanged();
    }

    private void getMemberDetail() {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        for (int i = 0; i < mCheckList.size(); i++) {
            MemberBean memberBean = mCheckList.get(i);
            int finalI = i;
            LeanCloudManager.getInstance().getUserInfo(memberBean.getId(), new EMValueCallBack<EaseUser>() {
                @Override
                public void onSuccess(EaseUser easeUser) {
                    memberBean.setId(easeUser.getUsername());
                    memberBean.setAvatar(easeUser.getAvatar());
                    memberBean.setName(easeUser.getNickname());
                    notifyItemChanged(finalI);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }

    }

    public void filter(int onLine) {
        mCheckList.clear();
        if (onLine == MemberBean.ONLINE_ALL) {
            mCheckList.addAll(mList);
        } else {
            for (MemberBean bean : mList) {
                if (onLine == bean.getOnLineState()) {
                    mCheckList.add(bean);
                }
            }
        }
        Collections.sort(mCheckList, new Comparator<MemberBean>() {
            @Override
            public int compare(MemberBean memberBean, MemberBean t1) {
                if (memberBean.getRole() > t1.getRole()) {
                    return 1;
                } else if (memberBean.getRole() < t1.getRole()) {
                    return -1;
                }
                return 0;
            }
        });
        getMemberDetail();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MemberHolder memberHolder = (MemberHolder) holder;
        MemberBean bean = mCheckList.get(position);
        memberHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean);
                }
            }
        });

        memberHolder.mTvName.setText(bean.getName());

        int role = bean.getRole();

        if (mIsOwner) {
            memberHolder.mIvMore.setVisibility(role == MemberBean.ROLE_OWNER ? View.GONE : View.VISIBLE);
        } else if (mIsAdmin) {
            memberHolder.mIvMore.setVisibility(role == MemberBean.ROLE_MEMBER ? View.VISIBLE : View.GONE);
        } else {
            memberHolder.mIvMore.setVisibility(View.GONE);
        }

        memberHolder.mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onMoreClick(bean);
                }
            }
        });

        if (mShowRole) {
            if (role == MemberBean.ROLE_MEMBER) {
                memberHolder.mTvRole.setVisibility(View.GONE);
            } else {
                memberHolder.mTvRole.setVisibility(View.VISIBLE);
                if (role == MemberBean.ROLE_OWNER) {
                    memberHolder.mTvRole.setText("区长");
                    memberHolder.mTvRole.setBackgroundResource(R.drawable.sp_radius_48_27ae60);
                } else {
                    memberHolder.mTvRole.setText("区干部");
                    memberHolder.mTvRole.setBackgroundResource(R.drawable.sp_radius_48_47464b);
                }
            }
        } else {
            memberHolder.mTvRole.setVisibility(View.GONE);
        }

        CustomImageUtil.getInstance().setHeadView(memberHolder.mCivHead, bean.getAvatar());

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(MemberBean memberBean);

        void onMoreClick(MemberBean memberBean);
    }

    @Override
    public int getItemCount() {
        return mCheckList.size();
    }

    protected class MemberHolder extends RecyclerView.ViewHolder {

        protected TextView mTvRole;
        protected TextView mTvName;
        protected ImageView mCivHead;
        protected ImageView mIvMore;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvRole = itemView.findViewById(R.id.tv_role);
            mCivHead = itemView.findViewById(R.id.civ_head);
            mIvMore = itemView.findViewById(R.id.iv_more);
        }
    }
}
