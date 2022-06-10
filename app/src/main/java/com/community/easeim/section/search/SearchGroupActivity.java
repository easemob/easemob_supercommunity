package com.community.easeim.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.contact.adapter.GroupContactAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends SearchActivity {
    private List<EMGroup> mData;
    private List<EMGroup> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchGroupActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setText(getString(R.string.em_search_group));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchGroupContactAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        mData = EMClient.getInstance().groupManager().getAllGroups();
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }


    private void searchResult(String search) {
        if(mData == null || mData.isEmpty()) {
            return;
        }

        EaseThreadManager.getInstance().runOnIOThread(()-> {
            result.clear();
            for (EMGroup group : mData) {
                if(group.getGroupName().contains(search) || group.getGroupId().contains(search)) {
                    result.add(group);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        //跳转到群聊页面
        EMGroup group = ((GroupContactAdapter)adapter).getItem(position);
        ChatActivity.actionStart(mContext, group.getGroupId(), DemoConstant.CHATTYPE_GROUP);
    }

    private class SearchGroupContactAdapter extends GroupContactAdapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.demo_layout_no_data_show_nothing;
        }
    }
}
