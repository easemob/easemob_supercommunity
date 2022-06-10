package com.community.easeim.section.me.headImage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;

import java.util.ArrayList;
import java.util.List;

public class HeadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int chooseIndex = -1;

    private List<HeadImageInfo> mList = new ArrayList<>();
    private OnHeadItemClick mOnHeadItemClick;

    public void setData(List<HeadImageInfo> list) {

        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_head_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeadImageInfo imageInfo = mList.get(position);
        HeadHolder headHolder = (HeadHolder) holder;

        headHolder.mIvHeadImage.setImageResource(imageInfo.getResId());

        if (chooseIndex == position) {
            headHolder.mIvShowSelect.setVisibility(View.VISIBLE);
            headHolder.mIvShowSelect.setBackgroundResource(R.drawable.headimage_checked);
        } else {
            headHolder.mIvShowSelect.setVisibility(View.GONE);
        }

        headHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnHeadItemClick != null) {
                    mOnHeadItemClick.onItemClick(headHolder.getBindingAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setChooseIndex(int position) {
        chooseIndex = position;
        notifyDataSetChanged();
    }

    public interface OnHeadItemClick {
        void onItemClick(int pos);
    }

    public void setOnHeadItemClick(OnHeadItemClick onHeadItemClick) {
        mOnHeadItemClick = onHeadItemClick;
    }

    protected class HeadHolder extends RecyclerView.ViewHolder {

        protected ImageView mIvShowSelect;
        protected ImageView mIvHeadImage;

        public HeadHolder(@NonNull View itemView) {
            super(itemView);

            mIvHeadImage = itemView.findViewById(R.id.iv_headImage);
            mIvShowSelect = itemView.findViewById(R.id.iv_showSelect);
        }
    }
}
