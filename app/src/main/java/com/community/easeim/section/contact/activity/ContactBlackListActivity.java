package com.community.easeim.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.widget.EaseRecyclerView;
import com.community.easeim.imkit.widget.EaseSearchEditText;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.viewmodels.ContactBlackViewModel;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.friend.adapter.ContactAdapter;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.community.easeim.section.search.SearchBlackActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class ContactBlackListActivity extends BaseInitActivity implements OnRefreshListener, EaseTitleBar.OnBackPressListener, View.OnClickListener, OnItemClickListener<EaseUser> {
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private EaseSearchEditText searchBlack;
    private ContactAdapter adapter;
    private ContactBlackViewModel viewModel;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, ContactBlackListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_contact_black_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);
        searchBlack = findViewById(R.id.search_black);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ContactAdapter();
        rvList.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshListener(this);
        searchBlack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(ContactBlackViewModel.class);
        viewModel.blackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    adapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        viewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_out_blacklist_success);
                    LiveDataBus.get().with(DemoConstant.REMOVE_BLACK).postValue(EaseEvent.create(DemoConstant.REMOVE_BLACK, EaseEvent.TYPE.CONTACT));
                }
            });
        });

        LiveDataBus.get().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                viewModel.getBlackList();
            }
        });

        LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                viewModel.getBlackList();
            }
        });

        viewModel.getBlackList();

        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.getBlackList();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private void finishRefresh() {
        if (srlRefresh != null) {
            srlRefresh.finishRefresh();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_black:
                SearchBlackActivity.actionStart(mContext);
                break;
        }
    }

    @Override
    public void onItemClick(EaseUser easeUser) {
        ContactDetailActivity.actionStart(mContext, easeUser.getUsername());
    }
}
