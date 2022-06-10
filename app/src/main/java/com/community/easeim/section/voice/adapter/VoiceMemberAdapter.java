package com.community.easeim.section.voice.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.voice.VoiceMember;
import com.hyphenate.chat.EMClient;

import java.util.List;

public class VoiceMemberAdapter extends RecyclerView.Adapter<VoiceMemberAdapter.NormalHolder> {

    private List<VoiceMember> mList;
    private boolean showMore;
    private OnMoreClickListener mOnMoreClickListener;
    private String mOwner;
    private int mStreamId = -1;
    private int mVolume;

    public void setData(List<VoiceMember> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setSpeakNow(int streamId, int volume) {
        mStreamId = streamId;
        mVolume = volume;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NormalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice_member, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NormalHolder holder, int position) {
        VoiceMember member = mList.get(position);
        holder.mTvName.setText(member.getName());
        CustomImageUtil.getInstance().setHeadView(holder.mCivHead, member.getAvatar());

        holder.mIvMore.setVisibility(showMore && !TextUtils.equals(member.getMemberId(), mOwner) ? View.VISIBLE : View.GONE);

        boolean speakerOff = member.getSpeakerOff();
        holder.mIVSpeaker.setVisibility(speakerOff ? View.VISIBLE : View.GONE);

        boolean isMuted = member.getMutedByAdmin() || member.getMutedBySelf();
        holder.mIVMic.setVisibility(isMuted ? View.VISIBLE : View.GONE);

        holder.mIvTalking.setVisibility(
                (mStreamId == member.getStreamId() || (TextUtils.equals(member.getMemberId(), EMClient.getInstance().getCurrentUser()) && mStreamId == 0)) && mVolume > 10
                        ? View.VISIBLE : View.GONE);

        holder.mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnMoreClickListener != null) {
                    mOnMoreClickListener.onMoreClick(member);
                }
            }
        });
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        mOnMoreClickListener = onMoreClickListener;
    }

    public interface OnMoreClickListener {
        void onMoreClick(VoiceMember member);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void showMore(String owner) {
        mOwner = owner;
        showMore = true;
        notifyDataSetChanged();
    }

    protected class NormalHolder extends RecyclerView.ViewHolder {

        protected ImageView mIvMore;
        protected TextView mTvName;
        protected CircleImageView mCivHead;
        protected ImageView mIVSpeaker;
        protected ImageView mIVMic;
        protected ImageView mIvTalking;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);

            mCivHead = itemView.findViewById(R.id.civ_head);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIVSpeaker = itemView.findViewById(R.id.iv_speaker);
            mIVMic = itemView.findViewById(R.id.iv_mic);
            mIvMore = itemView.findViewById(R.id.iv_more);
            mIvTalking = itemView.findViewById(R.id.iv_talking);
        }
    }
}
