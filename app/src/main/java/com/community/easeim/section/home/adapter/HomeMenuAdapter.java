package com.community.easeim.section.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.me.headImage.CustomImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TOP = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_BOTTOM = 2;
    private List<GroundBean> mList = new ArrayList<>();
    private Context mContext;

    public void addData(GroundBean userGroundBean) {
        mList.add(userGroundBean);
        notifyDataSetChanged();
    }

    public void setData(List<GroundBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public HomeMenuAdapter(Context context) {
        mContext = context;
    }

    public void setCheckedPos(int pos){
        this.mCheckedPos = pos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TOP) {
            return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_menu_top, parent, false));
        }
        if (viewType == TYPE_BOTTOM) {
            return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_menu_bottom, parent, false));
        }
        return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_menu_normal, parent, false));
    }

    private int mCheckedPos = 0;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TOP || viewType == TYPE_NORMAL) {
            GroundBean bean = new GroundBean();
            if (viewType == TYPE_NORMAL) {
                bean = mList.get(position - 1);

                NormalHolder normalHolder = (NormalHolder) holder;
                CustomImageUtil.getInstance().setCoverView(normalHolder.mIvIcon,bean.getGroundCover());

            }

            GroundBean finalBean = bean;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    if (mOnMenuClickListener != null) {
                        if (viewType == TYPE_TOP) {
                            mOnMenuClickListener.onStartClick();
                        } else {
                            mOnMenuClickListener.onItemClick(holder.getBindingAdapterPosition(), finalBean);
                        }
                    }

                    mCheckedPos = holder.getBindingAdapterPosition();
                    notifyDataSetChanged();
                }
            });

            CanCheckHolder canCheckHolder = (CanCheckHolder) holder;
            canCheckHolder.mIvBg.setBackgroundResource(position == mCheckedPos ? R.drawable.sp_home_menu_selected : R.drawable.sp_home_menu_unselected);
            if (viewType == TYPE_TOP) {
                canCheckHolder.mIvIcon.setImageResource(mCheckedPos == 0 ? R.drawable.smile_focus : R.drawable.smile_normal);
            }else {
                if (mCheckedPos == position){
                    canCheckHolder.mIvUnread.setVisibility(View.GONE);
                }else {
                    canCheckHolder.mIvUnread.setVisibility(mUnreadMap.containsKey(bean.getGroundId()) && mUnreadMap.get(bean.getGroundId())>0?
                    View.VISIBLE:View.GONE);
                }
            }

        } else if (viewType == TYPE_BOTTOM) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMenuClickListener != null) {
                        mOnMenuClickListener.onAddClick();
                    }
                }
            });

        }


    }

    private OnMenuClickListener mOnMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    private Map<String,Integer> mUnreadMap = new HashMap<>();
    public void setUnreadMap(String groundId, int finalCount,int pos) {
        mUnreadMap.put(groundId,finalCount);
        notifyItemChanged(pos);
    }

    public interface OnMenuClickListener {
        void onStartClick();

        void onItemClick(int pos, GroundBean bean);

        void onAddClick();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        if (position == mList.size() + 1) {
            return TYPE_BOTTOM;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mList.isEmpty() ? 2 : mList.size() + 2;
    }

    private class NormalHolder extends CanCheckHolder {

        protected CircleImageView mIvIcon;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.iv_icon);
        }
    }

    private class TopHolder extends CanCheckHolder {

        public TopHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class CanCheckHolder extends RecyclerView.ViewHolder {
        protected ImageView mIvBg;
        protected ImageView mIvIcon;
        protected ImageView mIvUnread;

        public CanCheckHolder(@NonNull View itemView) {
            super(itemView);
            mIvBg = itemView.findViewById(R.id.iv_bg);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mIvUnread = itemView.findViewById(R.id.iv_unread);

        }
    }

    private class BottomHolder extends RecyclerView.ViewHolder {

        public BottomHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
