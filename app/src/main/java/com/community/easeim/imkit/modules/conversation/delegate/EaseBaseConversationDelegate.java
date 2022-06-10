package com.community.easeim.imkit.modules.conversation.delegate;


import com.community.easeim.imkit.adapter.EaseAdapterDelegate;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.modules.conversation.model.EaseConversationSetStyle;

public abstract class EaseBaseConversationDelegate<T, VH extends EaseBaseRecyclerViewAdapter.ViewHolder<T>> extends EaseAdapterDelegate<T, VH> {
    public EaseConversationSetStyle setModel;

    public void setSetModel(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }

    public EaseBaseConversationDelegate(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }
}

