package com.community.easeim.section.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.community.easeim.R;
import com.community.easeim.common.db.DemoDbHelper;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.section.contact.activity.ContactDetailActivity;
import com.community.easeim.section.contact.adapter.ContactListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends SearchActivity {
    public List<EaseUser> mData;
    public List<EaseUser> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchFriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setText(getString(R.string.em_search_contact));
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchFriendAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        if(DemoDbHelper.getInstance(mContext).getUserDao() != null) {
            mData = DemoDbHelper.getInstance(mContext).getUserDao().loadAllContactUsers();
        }
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }

    private void searchResult(String search) {
        if(mData == null || mData.isEmpty() || TextUtils.isEmpty(search)) {
            return;
        }

        EaseThreadManager.getInstance().runOnIOThread(()-> {
            result.clear();
            for (EaseUser user : mData) {
                if(user.getUsername().contains(search) || user.getNickname().contains(search)) {
                    result.add(user);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        EaseUser item = ((SearchFriendAdapter)adapter).getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }

    public class SearchFriendAdapter extends ContactListAdapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.demo_layout_no_data_show_nothing;
        }
    }
}
