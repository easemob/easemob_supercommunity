package com.community.easeim.section.friend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.me.headImage.CustomImageUtil;

import java.util.ArrayList;
import java.util.List;

public class SuggestContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EaseUser> mList = new ArrayList<>();
    private List<EaseUser> mCheckedList = new ArrayList<>();
    private boolean mAllChecked = false;

    public void setData(List<EaseUser> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean allChecked) {
        mAllChecked = allChecked;
        notifyDataSetChanged();
    }

    public List<EaseUser> getCheckedList() {
        return mCheckedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        EaseUser easeUser = mList.get(position);


        if (mAllChecked) {
            normalHolder.mCb.setChecked(true);
        }

        int gender = easeUser.getGender();
        int genderResId = R.drawable.icon_gender_unknown;
        if (gender == DemoConstant.GENDER_OTHER) {
            genderResId = R.drawable.icon_flower;
        } else if (gender == DemoConstant.GENDER_MALE) {
            genderResId = R.drawable.icon_male_white;
        } else if (gender == DemoConstant.GENDER_FEMALE) {
            genderResId = R.drawable.icon_female_white;
        }

        normalHolder.mIvGender.setImageResource(genderResId);
        normalHolder.mTvName.setText(easeUser.getNickname());
        CustomImageUtil.getInstance().setHeadView(normalHolder.mCivHead, easeUser.getAvatar());

        normalHolder.mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && !mCheckedList.contains(easeUser)) {
                    mCheckedList.add(easeUser);
                } else if (!b) {
                    mCheckedList.remove(easeUser);
                    mAllChecked = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class NormalHolder extends RecyclerView.ViewHolder {

        protected CircleImageView mCivHead;
        protected CheckBox mCb;
        protected TextView mTvName;
        protected ImageView mIvGender;
        protected TextView mTvNum;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);

            mCivHead = itemView.findViewById(R.id.civ_head);
            mCb = itemView.findViewById(R.id.cb);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIvGender = itemView.findViewById(R.id.iv_gender);
            mTvNum = itemView.findViewById(R.id.tv_num);


        }
    }
}
