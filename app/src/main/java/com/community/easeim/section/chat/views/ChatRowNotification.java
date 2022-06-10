package com.community.easeim.section.chat.views;

import android.content.Context;
import android.widget.TextView;

import com.community.easeim.R;
import com.community.easeim.imkit.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMTextMessageBody;


public class ChatRowNotification extends EaseChatRow {
    private TextView contentView;

    public ChatRowNotification(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.demo_row_notification, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentView.setText(txtBody.getMessage());
    }
}

