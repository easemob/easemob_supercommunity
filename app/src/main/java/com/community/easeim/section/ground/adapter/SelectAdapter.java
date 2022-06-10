package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.section.ground.bean.SelectBean;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SelectBean> mList;

    public SelectAdapter(List<SelectBean> list) {
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select, parent, false));
    }

    private int mCheckedPos = 0;

    public int getCheckedPos() {
        return mCheckedPos;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SelectBean bean = mList.get(position);
        SelectHolder selectHolder = (SelectHolder) holder;
        selectHolder.mIvTitle.setImageResource(bean.getTitle());
        selectHolder.mTvName.setText(bean.getName());

        selectHolder.mCb.setChecked(position == mCheckedPos);

        selectHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckedPos = holder.getBindingAdapterPosition();
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelect(mCheckedPos);
                }
                notifyDataSetChanged();
            }
        });
    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public void setChecked(int pos) {
        mCheckedPos = pos;
        notifyDataSetChanged();
    }

    public interface OnSelectListener {
        void onSelect(int pos);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class SelectHolder extends RecyclerView.ViewHolder {

        protected ImageView mIvTitle;
        protected TextView mTvName;
        protected CheckBox mCb;

        public SelectHolder(@NonNull View itemView) {
            super(itemView);

            mIvTitle = itemView.findViewById(R.id.iv_title);
            mTvName = itemView.findViewById(R.id.tv_name);
            mCb = itemView.findViewById(R.id.cb);
        }
    }
}
