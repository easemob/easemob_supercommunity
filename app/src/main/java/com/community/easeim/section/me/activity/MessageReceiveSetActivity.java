package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.model.DemoModel;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.common.widget.SwitchItemView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.imkit.widget.ShowMsgSettingBottomDialog;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.me.viewmodels.OfflinePushSetViewModel;
import com.community.easeim.section.me.viewmodels.PushStyleViewModel;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chat.EMPushManager;


public class MessageReceiveSetActivity extends BaseInitActivity implements SwitchItemView.OnCheckedChangeListener, EaseTitleBar.OnBackPressListener, View.OnClickListener {

    private SwitchItemView rlSwitchNotification;
//    private SwitchItemView rlSwitchSound;
//    private SwitchItemView rlSwitchVibrate;
    private ArrowItemView itemPushMessageStyle;
    private DemoModel model;
    private EMPushManager.DisplayStyle displayStyle;
    private OfflinePushSetViewModel viewModel;


    private ShowMsgSettingBottomDialog mShowBottomDialog;
    private PushStyleViewModel mPushStyleViewModel;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MessageReceiveSetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_message_receive_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        rlSwitchNotification = findViewById(R.id.rl_switch_notification);
//        rlSwitchSound = findViewById(R.id.rl_switch_sound);
//        rlSwitchVibrate = findViewById(R.id.rl_switch_vibrate);
        itemPushMessageStyle = findViewById(R.id.item_push_message_style);
        mShowBottomDialog = new ShowMsgSettingBottomDialog();
    }

    @Override
    protected void initListener() {
        super.initListener();
        rlSwitchNotification.setOnCheckedChangeListener(this);
//        rlSwitchSound.setOnCheckedChangeListener(this);
//        rlSwitchVibrate.setOnCheckedChangeListener(this);
        itemPushMessageStyle.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        model = DemoHelper.getInstance().getModel();
        rlSwitchNotification.getSwitch().setChecked(model.getSettingMsgNotification());
        setSwitchVisible(rlSwitchNotification.getSwitch().isChecked());

//        rlSwitchSound.getSwitch().setChecked(model.getSettingMsgSound());
//        rlSwitchVibrate.getSwitch().setChecked(model.getSettingMsgVibrate());

        viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);

        viewModel.getConfigsObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMPushConfigs>() {
                @Override
                public void onSuccess(EMPushConfigs data) {
                    if(data != null) {
                        displayStyle = data.getDisplayStyle();
                        if(displayStyle == EMPushManager.DisplayStyle.SimpleBanner) {
                            itemPushMessageStyle.getTvContent().setText(R.string.push_message_style_simple);
                        }else {
                            itemPushMessageStyle.getTvContent().setText(R.string.push_message_style_summary);
                        }
                    }
                }
            });
        });
        viewModel.getPushConfigs();

        mPushStyleViewModel = new ViewModelProvider(this).get(PushStyleViewModel.class);
        mPushStyleViewModel.getPushStyleObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    viewModel.getPushConfigs();
                }

                @Override
                public void onLoading(Boolean data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    dismissLoading();
                }
            });
        });
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl_switch_notification ://接收新消息通知
                setSwitchVisible(isChecked);
                model.setSettingMsgNotification(isChecked);
                model.setSettingMsgSound(isChecked);
                model.setSettingMsgVibrate(isChecked);
                break;
//            case R.id.rl_switch_sound ://声音
//                model.setSettingMsgSound(isChecked);
//                break;
//            case R.id.rl_switch_vibrate ://震动
//                model.setSettingMsgVibrate(isChecked);
//                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    /**
     * 设置声音和震动的布局是否可见
     * @param isChecked
     */
    private void setSwitchVisible(boolean isChecked) {
//        if(isChecked) {
//            rlSwitchSound.setVisibility(View.VISIBLE);
//            rlSwitchVibrate.setVisibility(View.VISIBLE);
//        }else {
//            rlSwitchSound.setVisibility(View.GONE);
//            rlSwitchVibrate.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_push_message_style :
//                MessagePushStyleActivity.actionStartForResult(mContext, displayStyle.ordinal(), 100);

                //修改性别
                final EMPushManager.DisplayStyle[] values = EMPushManager.DisplayStyle.values();
                mShowBottomDialog.BottomDialog(MessageReceiveSetActivity.this);
                mShowBottomDialog.setOnItemClickListener(new ShowMsgSettingBottomDialog.OnItemClickListener() {
                    @Override
                    public void onSelectClick(int code, String value) {
                        mPushStyleViewModel.updateStyle(values[code]);
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && requestCode == 100) {
//            viewModel.getPushConfigs();
//        }
//    }
}
