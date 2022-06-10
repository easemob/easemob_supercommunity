package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.platform.comapi.map.A;
import com.community.easeim.R;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.section.ground.bean.MemberBean;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.voice.VoiceMember;

import java.util.ArrayList;
import java.util.List;

public class VoiceHeadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_MORE = 1;

    private List<VoiceMember> mList = new ArrayList<>();

    public void setData(List<VoiceMember> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MORE) {
            return new MoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_round_head_more, parent, false));
        }
        return new MemberHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_round_head, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 11) {
            return TYPE_MORE;
        }
        return TYPE_MEMBER;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MEMBER) {
            VoiceMember member = mList.get(position);
            MemberHolder memberHolder = (MemberHolder) holder;
            CustomImageUtil.getInstance().setHeadView(memberHolder.mCivHead, member.getAvatar());
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(mList.size(), 12);
    }

    protected class MemberHolder extends RecyclerView.ViewHolder {

        protected CircleImageView mCivHead;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);

            mCivHead = itemView.findViewById(R.id.civ_head);
        }
    }

    protected class MoreHolder extends RecyclerView.ViewHolder {

        public MoreHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
