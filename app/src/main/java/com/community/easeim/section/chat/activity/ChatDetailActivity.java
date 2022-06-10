package com.community.easeim.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.viewmodel.ChatViewModel;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.search.SearchSingleChatActivity;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.util.EMLog;

import java.util.List;

public class ChatDetailActivity extends BaseInitActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String mUserId;
    private CircleImageView mCircleImageView;
    private TextView mName;
    private LinearLayout mLlSearchHistory,mLlUserInfo;
    private Switch mTop,mDisturb;
    private TextView mClearHistory;

    private ChatViewModel viewModel;
    private EMConversation conversation;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_detail;
    }

    public static void actionStart(Context context,String userId) {
        Intent intent = new Intent(context, ChatDetailActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mCircleImageView =findViewById(R.id.civ_head);
        mName = findViewById(R.id.tv_nick_name);
        mLlSearchHistory = findViewById(R.id.ll_search_history);
        mLlUserInfo =findViewById(R.id.ll_user_info);
        mTop = findViewById(R.id.sw_conversion_top);
        mDisturb = findViewById(R.id.sw_not_disturb);
        mClearHistory = findViewById(R.id.tv_clear_history);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLlUserInfo.setOnClickListener(this);
        mLlSearchHistory.setOnClickListener(this);
        mClearHistory.setOnClickListener(this);
        mTop.setOnCheckedChangeListener(this);
        mDisturb.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mUserId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
    }


    @Override
    protected void initData() {
        super.initData();
        conversation = EMClient.getInstance()
                .chatManager()
                .getConversation(mUserId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);

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
        mTop.setChecked(!TextUtils.isEmpty(conversation.getExtField()));

        viewModel.getNoPushUsersObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> noPushUsers) {
                    if (noPushUsers.contains(mUserId)) {
                        mDisturb.setChecked(true);
                    }else{
                        mDisturb.setChecked(false);
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

        LeanCloudManager.getInstance().getUserInfo(mUserId, new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                mName.setText(value.getNickname());
                CustomImageUtil.getInstance().setHeadView(mCircleImageView, value.getAvatar());
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.i("ContactDetailActivity","onError   code  " +error + ":  message : "+errorMsg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_info:
                ContactDetailActivity.actionStart(mContext, mUserId);
                break;
            case R.id.ll_search_history:
                SearchSingleChatActivity.actionStart(mContext, mUserId);
                break;
            case R.id.tv_clear_history:
                clearHistory();
                break;
        }
    }


    private void clearHistory() {
        new CustomDialog
                .Builder(mContext)
                .setTitle("确认删除")
//                .setKey(name)
                .setMsg(getString(R.string.em_chat_delete_conversation))
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        viewModel.deleteConversationById(conversation.conversationId());
                    }
                })
                .show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_conversion_top:
                conversation.setExtField(isChecked ? (System.currentTimeMillis() + "") : "");
                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                break;
            case R.id.sw_not_disturb:
                viewModel.setUserNotDisturb(mUserId, isChecked);
                break;
        }
    }
}
