package com.community.easeim.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.community.easeim.R;
import com.community.easeim.imkit.widget.EaseSearchTextView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.fragment.ChatRoomContactManageFragment;
import com.community.easeim.section.search.SearchChatRoomActivity;


public class ChatRoomContactManageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {
    private EaseTitleBar mEaseTitleBar;
    private EaseSearchTextView mSearchChatRoom;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ChatRoomContactManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_friends_chat_room_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mEaseTitleBar = findViewById(R.id.title_bar_chat_room_contact);
        mSearchChatRoom = findViewById(R.id.search_chat_room);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_fragment, new ChatRoomContactManageFragment(), "chat_room_contact")
                .commit();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEaseTitleBar.setOnBackPressListener(this);
        mSearchChatRoom.setOnClickListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_chat_room :
                SearchChatRoomActivity.actionStart(mContext);
                break;
        }
    }
}
