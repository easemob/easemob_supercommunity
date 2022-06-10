package com.community.easeim.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.DemoApplication;
import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.manager.PushAndMessageHelper;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.interfaces.OnItemClickListener;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.widget.EaseSidebar;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.adapter.PickUserAdapter;
import com.community.easeim.section.contact.viewmodels.ContactListViewModel;
import com.community.easeim.section.dialog.DemoDialogFragment;
import com.community.easeim.section.dialog.SimpleDialogFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class ForwardMessageActivity extends BaseInitActivity implements OnRefreshListener, OnItemClickListener, EaseSidebar.OnTouchEventListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar mEaseTitleBar;
    private SmartRefreshLayout mSrlRefresh;
    private RecyclerView mRvContactList;
    private EaseSidebar mEaseSidebar;
    private TextView mFloatingHeader;
    private PickUserAdapter mAdapter;
    private ContactListViewModel mViewModel;
    private String mForwardMsgId;

    public static void actionStart(Context context, String forward_msg_id) {
        Intent starter = new Intent(context, ForwardMessageActivity.class);
        starter.putExtra("forward_msg_id", forward_msg_id);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_contact_list;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mForwardMsgId = getIntent().getStringExtra("forward_msg_id");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mSrlRefresh = findViewById(R.id.srl_refresh);
        mRvContactList = findViewById(R.id.rv_contact_list);
        mEaseSidebar = findViewById(R.id.side_bar_pick_user);
        mFloatingHeader = findViewById(R.id.floating_header);
        mEaseTitleBar = findViewById(R.id.title_bar_contact_list);

        mRvContactList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PickUserAdapter();
        mRvContactList.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlRefresh.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mEaseSidebar.setOnTouchEventListener(this);
        mEaseTitleBar.setOnBackPressListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(this).get(ContactListViewModel.class);
        mViewModel.getContactListObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> data) {
                    mAdapter.setData(data);
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    finishRefresh();
                }
            });
        });
        mViewModel.getContactList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.getContactList();
    }

    @Override
    public void onItemClick(View view, int position) {
        EaseUser user = mAdapter.getData().get(position);
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(getString(R.string.confirm_forward_to, user.getNickname()))
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        PushAndMessageHelper.sendForwardMessage(user.getUsername(), mForwardMsgId);
                        finish();
                    }
                })
                .showCancelButton(true)
                .show();
    }

    private void finishChatActivity() {
        DemoApplication.getInstance().getLifecycleCallbacks().finishTarget(ChatActivity.class);
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

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    private void moveToRecyclerItem(String pointer) {
        List<EaseUser> data = mAdapter.getData();
        if(data == null || data.isEmpty()) {
            return;
        }
        for(int i = 0; i < data.size(); i++) {
            if(TextUtils.equals(EaseCommonUtils.getLetter(data.get(i).getNickname()), pointer)) {
                LinearLayoutManager manager = (LinearLayoutManager) mRvContactList.getLayoutManager();
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

    private void finishRefresh() {
        if(mSrlRefresh != null) {
            mContext.runOnUiThread(() -> {
                mSrlRefresh.finishRefresh();
            });
        }
    }
}
