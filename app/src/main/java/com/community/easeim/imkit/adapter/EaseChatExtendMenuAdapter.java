package com.community.easeim.imkit.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.imkit.interfaces.OnItemClickListener;
import com.community.easeim.imkit.modules.chat.EaseChatExtendMenu;


public class EaseChatExtendMenuAdapter extends EaseBaseChatExtendMenuAdapter<EaseChatExtendMenuAdapter.ViewHolder, EaseChatExtendMenu.ChatMenuItemModel> {
    private OnItemClickListener itemListener;

    @Override
    protected int getItemLayoutId() {
        return R.layout.ease_chat_menu_item;
    }

    @Override
    protected EaseChatExtendMenuAdapter.ViewHolder easeCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EaseChatExtendMenuAdapter.ViewHolder holder, int position) {
        EaseChatExtendMenu.ChatMenuItemModel item = mData.get(position);
        holder.imageView.setBackgroundResource(item.image);
        holder.textView.setText(item.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(item.clickListener != null){
                    item.clickListener.onChatExtendMenuItemClick(item.id, v);
                }
                if(itemListener != null) {
                    itemListener.onItemClick(v, position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}

