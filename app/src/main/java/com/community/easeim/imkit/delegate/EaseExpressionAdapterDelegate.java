package com.community.easeim.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.interfaces.MessageListItemClickListener;
import com.community.easeim.imkit.model.styles.EaseMessageListItemStyle;
import com.community.easeim.imkit.viewholder.EaseChatRowViewHolder;
import com.community.easeim.imkit.viewholder.EaseExpressionViewHolder;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.community.easeim.imkit.widget.chatrow.EaseChatRowBigExpression;
import com.hyphenate.chat.EMMessage;


/**
 * 表情代理类
 */
public class EaseExpressionAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseExpressionAdapterDelegate() {
        super();
    }

    public EaseExpressionAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.TXT && item.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false);
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowBigExpression(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseExpressionViewHolder(view, itemClickListener);
    }
}
