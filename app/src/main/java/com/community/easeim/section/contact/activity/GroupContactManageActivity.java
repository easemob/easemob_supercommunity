package com.community.easeim.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.community.easeim.R;
import com.community.easeim.imkit.widget.EaseSearchTextView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.fragment.GroupContactManageFragment;
import com.community.easeim.section.contact.fragment.GroupPublicContactManageFragment;
import com.community.easeim.section.contact.viewmodels.GroupContactViewModel;
import com.community.easeim.section.group.activity.GroupPrePickActivity;
import com.community.easeim.section.search.SearchGroupActivity;
import com.community.easeim.section.search.SearchPublicGroupActivity;
import com.google.android.material.tabs.TabLayout;


public class GroupContactManageActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, View.OnClickListener {
    private EaseTitleBar mTitleBarGroupContact;
    private EaseSearchTextView mSearchGroup;
    private TabLayout mTlGroupContact;
    private ViewPager mVpGroupContact;
    private FrameLayout flFragment;
    private Group groupJoin;
    private Fragment joinGroupFragment;
    private Fragment publicGroupFragment;
    private boolean showPublic;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupContactManageActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, boolean showPublic) {
        Intent intent = new Intent(context, GroupContactManageActivity.class);
        intent.putExtra("showPublic", showPublic);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_friends_group_contact_manage;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        showPublic = intent.getBooleanExtra("showPublic", false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBarGroupContact = findViewById(R.id.title_bar_group_contact);
        mSearchGroup = findViewById(R.id.search_group);
        mTlGroupContact = findViewById(R.id.tl_group_contact);
        mVpGroupContact = findViewById(R.id.vp_group_contact);
        flFragment = findViewById(R.id.fl_fragment);
        groupJoin = findViewById(R.id.group_join);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBarGroupContact.setOnBackPressListener(this);
        mTitleBarGroupContact.setOnRightClickListener(this);
        mSearchGroup.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GroupContactViewModel viewModel = new ViewModelProvider(mContext).get(GroupContactViewModel.class);
        if(showPublic) {
            viewModel.loadAllGroups();
            showPublicGroup();
        }else {
            showJoinGroup();
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        createNewGroup();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_group :
                if(isShowPublic()) {
                    //搜索公开群
                    SearchPublicGroupActivity.actionStart(mContext);
                }else {
                    //搜索已加入的群
                    SearchGroupActivity.actionStart(mContext);
                }
                break;
        }
    }

    private void createNewGroup() {
        GroupPrePickActivity.actionStart(mContext);
    }

    private void switchTab() {
        if(isShowPublic()) {
            //公开群
            showPublicGroup();
        }else {
            //已加入的群
            showJoinGroup();
        }
    }

    /**
     * 是否是公开群
     * @return
     */
    private boolean isShowPublic() {
        return showPublic;
    }

    private void showJoinGroup() {
        //mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_public);
        mTitleBarGroupContact.setTitle(getString(R.string.em_friends_group_join));
        joinGroupFragment = getSupportFragmentManager().findFragmentByTag("join-group");
        if(joinGroupFragment != null && joinGroupFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(joinGroupFragment);
        }else {
            joinGroupFragment = new GroupContactManageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, joinGroupFragment, "join-group").commit();
        }
    }

    private void showPublicGroup() {
        //mTitleBarGroupContact.getRightText().setText(R.string.em_friends_group_join);
        mTitleBarGroupContact.setRightLayoutVisibility(View.GONE);
        mTitleBarGroupContact.setTitle(getString(R.string.em_friends_group_public));
        //设置公开群fragment
        publicGroupFragment = getSupportFragmentManager().findFragmentByTag("public-group");
        if(publicGroupFragment != null && publicGroupFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(publicGroupFragment);
        }else {
            publicGroupFragment = new GroupPublicContactManageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, publicGroupFragment, "public-group").commit();
        }
    }
}
