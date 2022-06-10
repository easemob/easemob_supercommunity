package com.community.easeim.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.db.DemoDbHelper;
import com.community.easeim.common.enums.SearchType;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.contact.adapter.AddContactAdapter;
import com.community.easeim.section.contact.viewmodels.AddContactViewModel;
import com.community.easeim.section.search.SearchActivity;

import java.util.List;

public class AddContactActivity extends SearchActivity implements EaseTitleBar.OnBackPressListener, AddContactAdapter.OnItemAddClickListener {
    private AddContactViewModel mViewModel;
    private SearchType mType;

    public static void startAction(Context context, SearchType type) {
        Intent intent = new Intent(context, AddContactActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mType = (SearchType) getIntent().getSerializableExtra("type");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setText(getString(R.string.em_search_add_contact));
        query.setHint(getString(R.string.em_search_add_contact_hint));
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mViewModel.getAddContact().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(getResources().getString(R.string.em_add_contact_send_successful));
                }
            });

        });
        //获取本地的好友列表
        List<String> localUsers = null;
        if(DemoDbHelper.getInstance(mContext).getUserDao() != null) {
            localUsers = DemoDbHelper.getInstance(mContext).getUserDao().loadContactUsers();
        }
        ((AddContactAdapter)adapter).addLocalContacts(localUsers);

        ((AddContactAdapter)adapter).setOnItemAddClickListener(this);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new AddContactAdapter();
    }

    @Override
    public void searchMessages(String query) {
        // you can search the user from your app server here.
        if(!TextUtils.isEmpty(query)) {
            if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                adapter.clearData();
            }
            adapter.addData(query);
        }
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        // 跳转到好友页面
        String item = (String) adapter.getItem(position);
        EaseUser user = new EaseUser(item);
        ContactDetailActivity.actionStart(mContext, user, false);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onItemAddClick(View view, int position) {
        // 添加好友
        mViewModel.addContact((String) adapter.getItem(position), getResources().getString(R.string.em_add_contact_add_a_friend));
    }
}
