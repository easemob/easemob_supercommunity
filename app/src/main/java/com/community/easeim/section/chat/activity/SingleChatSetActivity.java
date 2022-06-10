package com.community.easeim.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.common.widget.SwitchItemView;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.viewmodel.ChatViewModel;
import com.community.easeim.section.contact.activity.ContactDetailActivity;
import com.community.easeim.section.dialog.DemoDialogFragment;
import com.community.easeim.section.dialog.SimpleDialogFragment;
import com.community.easeim.section.search.SearchSingleChatActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.List;

public class SingleChatSetActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener, SwitchItemView.OnCheckedChangeListener {
    private EaseTitleBar titleBar;
    private ArrowItemView itemUserInfo;
    private ArrowItemView itemSearchHistory;
    private ArrowItemView itemClearHistory;
    private SwitchItemView itemSwitchTop;
    private SwitchItemView itemUserNotDisturb;

    private ChatViewModel viewModel;
    private String toChatUsername;
    private EMConversation conversation;

    public static void actionStart(Context context, String toChatUsername) {
        Intent intent = new Intent(context, SingleChatSetActivity.class);
        intent.putExtra("toChatUsername", toChatUsername);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_single_chat_set;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = getIntent().getStringExtra("toChatUsername");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        itemUserInfo = findViewById(R.id.item_user_info);
        itemSearchHistory = findViewById(R.id.item_search_history);
        itemClearHistory = findViewById(R.id.item_clear_history);
        itemSwitchTop = findViewById(R.id.item_switch_top);
        itemUserNotDisturb = findViewById(R.id.item_user_not_disturb);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        itemUserInfo.setOnClickListener(this);
        itemSearchHistory.setOnClickListener(this);
        itemClearHistory.setOnClickListener(this);
        itemSwitchTop.setOnCheckedChangeListener(this);
        itemUserNotDisturb.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        conversation = EMClient.getInstance()
                                                .chatManager()
                                                .getConversation(toChatUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        itemUserInfo.getAvatar().setShapeType(1);
        itemUserInfo.getTvTitle().setText(toChatUsername);
        itemSwitchTop.getSwitch().setChecked(!TextUtils.isEmpty(conversation.getExtField()));

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONTACT_DECLINE, EaseEvent.TYPE.MESSAGE));
                    finish();
                }
            });
        });
        viewModel.getNoPushUsersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> noPushUsers) {
                    if (noPushUsers.contains(toChatUsername)) {
                        itemUserNotDisturb.getSwitch().setChecked(true);
                    }else{
                        itemUserNotDisturb.getSwitch().setChecked(false);
                    }
                }
            });
        });
        viewModel.setNoPushUsersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean bool) {
                    //设置免打扰成功
                }
                @Override
                public void onError(int code, String message){
                    //可根据需求做出提示
                    //ToastUtils.showFailToast("设置用户免打扰失败");

                }
            });
        });
        viewModel.getNoPushUsers();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_user_info:
                EaseUser user = new EaseUser();
                user.setUsername(toChatUsername);
                ContactDetailActivity.actionStart(mContext, user);
                break;
            case R.id.item_search_history:
                SearchSingleChatActivity.actionStart(mContext, toChatUsername);
                break;
            case R.id.item_clear_history:
                clearHistory();
                break;
        }
    }

    private void clearHistory() {
        // 是否删除会话
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_chat_delete_conversation)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        viewModel.deleteConversationById(conversation.conversationId());
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_switch_top:
                conversation.setExtField(isChecked ? (System.currentTimeMillis() + "") : "");
                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                break;
            case R.id.item_user_not_disturb:
                viewModel.setUserNotDisturb(toChatUsername, isChecked);
                break;
        }
    }
}
