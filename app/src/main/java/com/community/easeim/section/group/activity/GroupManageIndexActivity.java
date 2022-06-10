package com.community.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.group.GroupHelper;
import com.community.easeim.section.group.viewmodels.GroupMemberAuthorityViewModel;

import java.util.List;
import java.util.Map;

public class GroupManageIndexActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemBlackManager;
    private ArrowItemView itemMuteManage;
    private Button btnTransfer;
    private String groupId;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupManageIndexActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_group_manage;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemBlackManager = findViewById(R.id.item_black_manager);
        itemMuteManage = findViewById(R.id.item_mute_manage);
        btnTransfer = findViewById(R.id.btn_transfer);

        btnTransfer.setVisibility(GroupHelper.isOwner(DemoHelper.getInstance().getGroupManager().getGroup(groupId)) ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemBlackManager.setOnClickListener(this);
        itemMuteManage.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_black_manager ://黑名单
                GroupMemberAuthorityActivity.actionStart(mContext, groupId, GroupMemberAuthorityActivity.TYPE_BLACK);
                break;
            case R.id.item_mute_manage ://禁言列表
                GroupMemberAuthorityActivity.actionStart(mContext, groupId, GroupMemberAuthorityActivity.TYPE_MUTE);
                break;
            case R.id.btn_transfer ://移交
                GroupTransferActivity.actionStart(mContext, groupId);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        GroupMemberAuthorityViewModel viewModel = new ViewModelProvider(this).get(GroupMemberAuthorityViewModel.class);
        viewModel.getMuteMembersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Map<String, Long>>() {
                @Override
                public void onSuccess(Map<String, Long> data) {
                    itemMuteManage.getTvContent().setText(data.size() + "个");
                }
            });
        });
        viewModel.getBlackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    itemBlackManager.getTvContent().setText(data.size() + "个");
                }

            });
        });
        viewModel.getMessageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if(TextUtils.equals(event.event, DemoConstant.GROUP_OWNER_TRANSFER)) {
                finish();
                return;
            }
            if(event.isGroupChange()) {
                viewModel.getBlackMembers(groupId);
                viewModel.getMuteMembers(groupId);
            }
        });
        viewModel.getBlackMembers(groupId);
        viewModel.getMuteMembers(groupId);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
