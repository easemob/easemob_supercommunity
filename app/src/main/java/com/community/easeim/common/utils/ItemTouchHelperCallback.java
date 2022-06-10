package com.community.easeim.common.utils;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private IMoveAndSwipeCallback iMoveAndSwipeCallback;

    public void setiMoveAndSwipeCallback(IMoveAndSwipeCallback iMoveAndSwipeCallback) {
        this.iMoveAndSwipeCallback = iMoveAndSwipeCallback;
    }

    /**
     * 设置拖拽和item滑动的可支持方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //支持上下拖拽
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //item 不支持左滑
        final int swipeFlags = 0;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 拖拽结束后（手指抬起）会回调的方法
     *
     * @param recyclerView
     * @param viewHolder 手指拖拽的item
     * @param viewHolder1 移动到的item
     * @return
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        if (iMoveAndSwipeCallback != null) {
            iMoveAndSwipeCallback.onMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        }
        return true;
    }

    /**
     * 侧滑回调
     *
     * @param viewHolder
     * @param swipeDir 方向
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
        if (iMoveAndSwipeCallback != null) {
            iMoveAndSwipeCallback.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    public interface IMoveAndSwipeCallback {
        void onMove(int prePosition, int postPosition);

        void onSwiped(int position);
    }
}
