package com.community.easeim.section.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.widget.EaseRecyclerView;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.message.delegates.AgreeMsgDelegate;
import com.community.easeim.section.message.delegates.InviteMsgDelegate;
import com.community.easeim.section.message.delegates.OtherMsgDelegate;
import com.community.easeim.section.message.viewmodels.NewFriendsViewModel;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

public class SystemMsgsActivity extends BaseInitActivity implements OnRefreshLoadMoreListener, InviteMsgDelegate.OnInviteListener {
    private SmartRefreshLayout srlRefresh;
    private EaseRecyclerView rvList;
    private static final int limit = 10;
    private int offset;
    private NewFriendsMsgAdapter adapter;
    private NewFriendsViewModel viewModel;
    private InviteMsgDelegate msgDelegate;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, SystemMsgsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_system_msgs;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        srlRefresh = findViewById(R.id.srl_refresh);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new NewFriendsMsgAdapter();
        msgDelegate = new InviteMsgDelegate();
        adapter.addDelegate(new AgreeMsgDelegate())
                .addDelegate(msgDelegate)
                .addDelegate(new OtherMsgDelegate());
        rvList.setAdapter(adapter);

        registerForContextMenu(rvList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.demo_invite_list_menu, menu);
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) menuInfo).position;
        EMMessage item = adapter.getItem(position);
        String statusParams = null;
        try {
            statusParams = item.getStringAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        if(statusParams == null) {
            return;
        }
        InviteMessageStatus statusEnum = InviteMessageStatus.valueOf(statusParams);
        if(statusEnum == InviteMessageStatus.BEINVITEED ||
                statusEnum == InviteMessageStatus.BEAPPLYED ||
                statusEnum == InviteMessageStatus.GROUPINVITATION) {
            menu.findItem(R.id.action_invite_agree).setVisible(true);
            menu.findItem(R.id.action_invite_refuse).setVisible(true);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = ((EaseRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo()).position;
        EMMessage message = adapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.action_invite_agree :
                viewModel.agreeInvite(message);
                break;
            case R.id.action_invite_refuse :
                viewModel.refuseInvite(message);
                break;
            case R.id.action_invite_delete :
                viewModel.deleteMsg(message);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void initListener() {
        super.initListener();
        srlRefresh.setOnRefreshLoadMoreListener(this);
        msgDelegate.setOnInviteListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(NewFriendsViewModel.class);
        viewModel.inviteMsgObservable().observe(this, response -> {
            finishRefresh();
            if(response == null) {
                return;
            }
            adapter.setData(response);
        });
        viewModel.moreInviteMsgObservable().observe(this, response -> {
            finishLoadMore();
            if(response == null) {
                return;
            }
            adapter.addData(response);
        });

        viewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        viewModel.agreeObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String message) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        viewModel.refuseObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String message) {
                    viewModel.loadMessages(limit);
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(event);
                }
            });
        });
        LiveDataBus bus = LiveDataBus.get();
        bus.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, this::loadData);
        bus.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::loadData);
        viewModel.makeAllMsgRead();
        viewModel.loadMessages(limit);
    }

    private void loadData(EaseEvent easeEvent) {
        viewModel.loadMessages(limit);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        offset += limit;
        EMMessage message = adapter.getData().get(adapter.getData().size() - 1);
        viewModel.loadMoreMessages(message.getMsgId(), limit);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        viewModel.loadMessages(limit);
    }

    private void finishRefresh() {
        if(srlRefresh != null) {
            srlRefresh.finishRefresh();
        }
    }

    private void finishLoadMore() {
        if(srlRefresh != null) {
            srlRefresh.finishLoadMore();
        }
    }

    @Override
    public void onInviteAgree(View view, EMMessage msg) {
        viewModel.agreeInvite(msg);
    }

    @Override
    public void onInviteRefuse(View view, EMMessage msg) {
        viewModel.refuseInvite(msg);
    }
}
