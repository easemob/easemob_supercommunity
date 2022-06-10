package com.community.easeim.imkit.delegate;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.adapter.EaseAdapterDelegate;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;


/**
 * 本类设计的目的是做为对话消息代理类的基类，添加了对话代理类特有的方法
 * @param <T>
 * @param <VH>
 */
public abstract class EaseMessageAdapterDelegate<T, VH extends EaseChatRowViewHolder> extends EaseAdapterDelegate<T, VH> {
    private MessageListItemClickListener mItemClickListener;

    public EaseMessageAdapterDelegate() {}

    public EaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener) {
        this();
        this.mItemClickListener = itemClickListener;
    }

    public EaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener,
                                      EaseMessageListItemStyle itemStyle) {
        this(itemClickListener);
    }

    /**
     * create default item style
     * @return
     */
    public EaseMessageListItemStyle createDefaultItemStyle() {
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(true)
                .showUserNick(false);
        return builder.build();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, String tag) {
        EaseChatRow view = getEaseChatRow(parent, isSender(tag));
        return createViewHolder(view, mItemClickListener);
    }

    private boolean isSender(String tag) {
        return !TextUtils.isEmpty(tag) && TextUtils.equals(tag, EMMessage.Direct.SEND.toString());
    }

    protected abstract EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender);

    protected abstract VH createViewHolder(View view, MessageListItemClickListener itemClickListener);

    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

}
