package com.community.easeim.section.ground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;

import java.util.ArrayList;
import java.util.List;

public class MemberFilterAdapter extends RecyclerView.Adapter<MemberFilterAdapter.FilterHolder> {
    private List<String> mList = new ArrayList<>();

    private int mCheckPos = 0;

    public void setData(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberFilterAdapter.FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemberFilterAdapter.FilterHolder holder, int position) {
        String content = mList.get(position);
        holder.mTvName.setText(content);
        holder.mTvName.setBackgroundResource(position == mCheckPos ? R.drawable.sp_radius_48_27ae60 : R.drawable.sp_radius_1f1f1f);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckPos = holder.getBindingAdapterPosition();
                if (mOnFilterListener != null) {
                    mOnFilterListener.onFilter(mCheckPos);
                }
                notifyDataSetChanged();
            }
        });
    }

    private OnFilterListener mOnFilterListener;

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        mOnFilterListener = onFilterListener;
    }

    public interface OnFilterListener {
        void onFilter(int pos);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class FilterHolder extends RecyclerView.ViewHolder {

        protected TextView mTvName;

        public FilterHolder(@NonNull View itemView) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
