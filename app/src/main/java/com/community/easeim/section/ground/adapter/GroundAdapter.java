package com.community.easeim.section.ground.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.community.easeim.R;
import com.community.easeim.imkit.utils.KeyWordUtil;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.views.BlurringView;
import com.community.easeim.section.inter.OnItemClickListener;
import com.community.easeim.section.me.headImage.CustomImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GroundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroundBean> mList = new ArrayList<>();
    private OnItemClickListener<GroundBean> mOnItemClickListener;
    private String mKey;
    private static final int TYPE_TOP = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_EMPTY = 2;
    private OnSearchClickListener mOnSearchClickListener;
    private boolean mShowTop;

    public GroundAdapter(boolean showTop) {
        mShowTop = showTop;
    }

    public void setData(List<GroundBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setData(List<GroundBean> list, String key) {
        mList = list;
        mKey = key;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!mShowTop) {
            if (viewType == TYPE_EMPTY) {
                return new EmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false));
            }
            return new GroundViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ground, parent, false));
        }

        if (viewType == TYPE_TOP) {
            return new TopHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_ground_top, parent, false));
        }

        if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false));
        }

        return new GroundViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ground, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (!mShowTop) {
            if (mList.isEmpty()) {
                return TYPE_EMPTY;
            }
            return TYPE_NORMAL;
        }

        if (position == 0) {
            return TYPE_TOP;
        }

        if (mList.isEmpty() && position == 1) {
            return TYPE_EMPTY;
        }

        return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_TOP) {
            TopHolder holder = (TopHolder) viewHolder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // set title full span
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
            holder.mTvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnSearchClickListener != null) {
                        mOnSearchClickListener.onSearchClick();
                    }
                }
            });
        } else if (viewType == TYPE_NORMAL) {
            GroundViewHolder holder = (GroundViewHolder) viewHolder;
            GroundBean bean = mList.get(mShowTop ? position - 1 : position);

            setText(holder.mTvTitle, bean.getGroundName());
            holder.mTvNum.setText(bean.getGround_total()+"");
            setText(holder.mTvDescribe, bean.getGroundDes());

            CustomImageUtil.getInstance().setCoverView(holder.mIvContent, bean.getGroundCover());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
        } else if (viewType == TYPE_EMPTY) {
            EmptyHolder holder = (EmptyHolder) viewHolder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // set title full span
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }

    }

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener) {
        mOnSearchClickListener = onSearchClickListener;
    }

    public interface OnSearchClickListener {
        void onSearchClick();
    }

    @Override
    public int getItemCount() {
        int size;
        if (mList.isEmpty()) {
            if (mShowTop) {
                size = 2;
            } else {
                size = 1;
            }
        } else {
            if (mShowTop) {
                size = mList.size() + 1;
            } else {
                size = mList.size();
            }
        }

        return size;
    }

    public void clearData() {
        mList = new ArrayList<>();
        mKey = "";
        notifyDataSetChanged();
    }

    protected class TopHolder extends RecyclerView.ViewHolder {

        protected TextView mTvSearch;

        public TopHolder(@NonNull View itemView) {
            super(itemView);

            mTvSearch = itemView.findViewById(R.id.tv_search);
        }
    }

    protected class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected class GroundViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvDescribe;
        public TextView mTvNum;
        public TextView mTvTitle;
        public ImageView mIvContent;

        public GroundViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvContent = itemView.findViewById(R.id.iv_content);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvNum = itemView.findViewById(R.id.tv_num);
            mTvDescribe = itemView.findViewById(R.id.tv_describe);

            BlurringView bv = itemView.findViewById(R.id.bv);
            bv.setBlurredView(mIvContent);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<GroundBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void setText(TextView textView, String text) {
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(mKey) && text.contains(mKey)) {
            textView.setText(KeyWordUtil.matcherSearchTitleBackColor(Color.parseColor("#27ae60"), text, mKey));
        } else {
            textView.setText(text);
        }
    }
}
