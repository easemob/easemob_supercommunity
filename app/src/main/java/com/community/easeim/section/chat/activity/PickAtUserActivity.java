package com.community.easeim.section.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.interfaces.OnItemClickListener;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.widget.EaseRecyclerView;
import com.community.easeim.imkit.widget.EaseSidebar;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.adapter.PickAllUserAdapter;
import com.community.easeim.section.chat.adapter.PickUserAdapter;
import com.community.easeim.section.contact.viewmodels.GroupContactViewModel;
import com.hyphenate.chat.EMGroup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PickAtUserActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, EaseSidebar.OnTouchEventListener, EaseTitleBar.OnBackPressListener {
//    private EaseTitleBar mTitleBarPick;
    private SmartRefreshLayout mSrlRefresh;
    private EaseRecyclerView mRvPickUserList;
    private EaseSidebar mSideBarPickUser;
    private TextView mFloatingHeader;
    private String mGroupId;
    private GroupContactViewModel mViewModel;
    protected PickUserAdapter mAdapter;
    private ConcatAdapter baseAdapter;
    private PickAllUserAdapter headerAdapter;

    public static void actionStartForResult(Fragment fragment, String groupId, int requestCode) {
        Intent starter = new Intent(fragment.getContext(), PickAtUserActivity.class);
        starter.putExtra("groupId", groupId);
        fragment.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat_pick_at_user;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mGroupId = getIntent().getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        mTitleBarPick = findViewById(R.id.title_bar_pick);
        mSrlRefresh = findViewById(R.id.srl_refresh);
        mRvPickUserList = findViewById(R.id.rv_pick_user_list);
        mSideBarPickUser = findViewById(R.id.side_bar_pick_user);
        mFloatingHeader = findViewById(R.id.floating_header);

        mRvPickUserList.setLayoutManager(new LinearLayoutManager(mContext));
        baseAdapter = new ConcatAdapter();
        mAdapter = new PickUserAdapter();
        baseAdapter.addAdapter(mAdapter);
        mRvPickUserList.setAdapter(baseAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlRefresh.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mSideBarPickUser.setOnTouchEventListener(this);
//        mTitleBarPick.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(GroupContactViewModel.class);
        mViewModel.getGroupMember().observe(this, response -> {
            if(response != null) {
                checkIfAddHeader();
            }
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    removeSelf(data);
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });

        mViewModel.getGroupMembers(mGroupId);
    }

    private void removeSelf(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Iterator<EaseUser> iterator = data.iterator();
        while (iterator.hasNext()) {
            EaseUser user = iterator.next();
            if(TextUtils.equals(user.getUsername(), DemoHelper.getInstance().getCurrentUser())) {
                iterator.remove();
            }
        }
    }

    private void checkIfAddHeader() {
        EMGroup group = DemoHelper.getInstance().getGroupManager().getGroup(mGroupId);
        if(group != null) {
            String owner = group.getOwner();
            if(TextUtils.equals(owner, DemoHelper.getInstance().getCurrentUser())) {
                AddHeader();
            }
        }

    }

    private void AddHeader() {
        if( headerAdapter == null) {
            headerAdapter = new PickAllUserAdapter();
            EaseUser user = new EaseUser(getString(R.string.all_members));
            user.setAvatar(R.drawable.ease_groups_icon+"");
            List<EaseUser> users = new ArrayList<>();
            users.add(user);
            headerAdapter.setData(users);
        }
        if(!baseAdapter.getAdapters().contains(headerAdapter)) {
            baseAdapter.addAdapter(0, headerAdapter);

            headerAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    setResult(RESULT_OK, new Intent().putExtra("username", headerAdapter.getItem(position).getUsername()));
                    finish();
                }
            });
        }

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.getGroupMembers(mGroupId);
    }

    private void finishRefresh() {
        if(mSrlRefresh != null) {
            EaseThreadManager.getInstance().runOnMainThread(() -> {
                mSrlRefresh.finishRefresh();
            });

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser user = mAdapter.getData().get(position);
        if(TextUtils.equals(user.getUsername(), DemoHelper.getInstance().getCurrentUser())) {
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("username", user.getUsername());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActionDown(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionMove(MotionEvent event, String pointer) {
        showFloatingHeader(pointer);
        moveToRecyclerItem(pointer);
    }

    @Override
    public void onActionUp(MotionEvent event) {
        hideFloatingHeader();
    }

    private void moveToRecyclerItem(String pointer) {
        List<EaseUser> data = mAdapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(EaseCommonUtils.getLetter(data.get(i).getNickname()), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) mRvPickUserList.getLayoutManager();
                if(manager != null) {
                    manager.scrollToPositionWithOffset(i, 0);
                }
            }
        }
    }

    /**
     * 展示滑动的字符
     * @param pointer
     */
    private void showFloatingHeader(String pointer) {
        if(TextUtils.isEmpty(pointer)) {
            hideFloatingHeader();
            return;
        }
        mFloatingHeader.setText(pointer);
        mFloatingHeader.setVisibility(View.VISIBLE);
    }

    private void hideFloatingHeader() {
        mFloatingHeader.setVisibility(View.GONE);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
