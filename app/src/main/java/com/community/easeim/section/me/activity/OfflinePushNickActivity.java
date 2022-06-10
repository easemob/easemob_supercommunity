package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseEditTextUtils;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.me.viewmodels.OfflinePushSetViewModel;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.util.EMLog;

public class OfflinePushNickActivity extends BaseInitActivity implements  TextWatcher {
	static private String TAG =  "OfflinePushNickActivity";
	private TextView mTextViewTitle;
	private Drawable clear;
	private EditText inputNickName,inputSign;
	private TextView limitNickName,limitSign;
	private Button saveNickName;
	private String nickName;
	private OfflinePushSetViewModel viewModel;
	private LinearLayout mLinearLayoutNickName,mLinearLayoutSign;
	private boolean isSign;


	public static void actionStart(Context context) {
	    Intent intent = new Intent(context, OfflinePushNickActivity.class);
	    context.startActivity(intent);
	}

	@Override
	protected void initIntent(Intent intent) {
		super.initIntent(intent);
		if(intent != null){
			isSign = intent.getBooleanExtra("isSign",false);
			nickName = intent.getStringExtra("nickName");
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.demo_activity_offline_push;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		mTextViewTitle = findViewById(R.id.tv_name);
		limitNickName = findViewById(R.id.tv_nick_name_limit);
		inputNickName = (EditText) findViewById(R.id.et_input_nickname);


		saveNickName = (Button) findViewById(R.id.btn_save);
		mLinearLayoutNickName = findViewById(R.id.ll_nick_name);
		mLinearLayoutSign = findViewById(R.id.ll_user_sign);
		inputSign = findViewById(R.id.et_input_sign);

		limitSign = findViewById(R.id.tv_sign_limit);
		if (isSign) {
			inputSign.requestFocus();
			mTextViewTitle.setText("修改个性签名");
			mLinearLayoutSign.setVisibility(View.VISIBLE);
			EaseEditTextUtils.clearEditTextListener(inputSign);
			mLinearLayoutNickName.setVisibility(View.GONE);
		} else {
			mTextViewTitle.setText("修改昵称");
			mLinearLayoutSign.setVisibility(View.GONE);
			inputNickName.requestFocus();
			EaseEditTextUtils.clearEditTextListener(inputNickName);
			mLinearLayoutNickName.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void initListener() {
		super.initListener();
		findViewById(R.id.tv_save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSign){
					updateSign();
				} else {
					updateNickName();
				}
			}
		});

		if (isSign) {
			inputSign.addTextChangedListener(this);
		} else {
			inputNickName.addTextChangedListener(this);
		}
	}


	private void updateNickName(){
		String nick = inputNickName.getText().toString();
		if (nick != null && nick.length() > 0) {

			EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.NICKNAME, nick, new EMValueCallBack<String>() {
				@Override
				public void onSuccess(String value) {
					EMLog.d(TAG, "fetchUserInfoById :" + value);
//					showToast(R.string.demo_offline_nickname_update_success);
					nickName = nick;
					DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nick);
					LeanCloudManager.getInstance().updateUserInfo(EMClient.getInstance().getCurrentUser(),nick);

					EaseEvent event = EaseEvent.create(DemoConstant.NICK_NAME_CHANGE, EaseEvent.TYPE.CONTACT);
					//发送联系人更新事件
					event.message = nick;
					LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE).postValue(event);
					runOnUiThread(new Runnable() {
						public void run() {
							//同时更新推送昵称
							viewModel.updatePushNickname(nick);
						}
					});
				}

				@Override
				public void onError(int error, String errorMsg) {
					EMLog.d(TAG, "fetchUserInfoById  error:" + error + " errorMsg:" + errorMsg);
					showToast(R.string.demo_offline_nickname_update_failed);
				}
			});
		}else{
			showToast(R.string.demo_offline_nickname_is_empty);
		}
	}


	private void updateSign(){
		String sign = inputSign.getText().toString();
		if (sign != null && sign.length() > 0) {

			EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.SIGN, sign, new EMValueCallBack<String>() {
				@Override
				public void onSuccess(String value) {
					EMLog.d(TAG, "fetchUserInfoById :" + value);
//					showToast(R.string.demo_offline_nickname_update_success);
//					nickName = nick;
//					PreferenceManager.getInstance().setCurrentUserNick(nick);
					LeanCloudManager.getInstance().updateUserSign(EMClient.getInstance().getCurrentUser(),sign);
					EaseEvent event = EaseEvent.create(DemoConstant.USER_INFO_CHANGE, EaseEvent.TYPE.CONTACT);
					//发送联系人更新事件
					event.message = sign;
					LiveDataBus.get().with(DemoConstant.USER_INFO_CHANGE).postValue(event);
					getIntent().putExtra("user_sign", sign);
					setResult(RESULT_OK, getIntent());
					finish();
				}

				@Override
				public void onError(int error, String errorMsg) {
					EMLog.d(TAG, "fetchUserInfoById  error:" + error + " errorMsg:" + errorMsg);
					showToast("修改失败!请检查网络并重试!");
				}
			});
		} else {
			showToast("个性签名为空,请重新输入");
		}
	}
	@Override
	protected void initData() {
		super.initData();
		if(!TextUtils.isEmpty(nickName)){
			inputSign.setText(nickName);
			inputNickName.setText(nickName);
		}

		clear = getResources().getDrawable(R.drawable.mine_delete);
		if (isSign) {

			EaseEditTextUtils.showRightDrawable(inputSign, clear);
		} else {
			EaseEditTextUtils.showRightDrawable(inputNickName, clear);
		}

		viewModel = new ViewModelProvider(this).get(OfflinePushSetViewModel.class);
		viewModel.getUpdatePushNicknameObservable().observe(this, response -> {
			parseResource(response, new OnResourceParseCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean data) {
					getIntent().putExtra("nickName", nickName);
					setResult(RESULT_OK, getIntent());
					finish();
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
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable editable) {
		String s = editable.toString();
		setLimit(s);
	}

	private void setLimit(String s) {
		int length = s.length();
		if (isSign){
			limitSign.setText(length + "/24");
		} else {
			limitNickName.setText(length + "/16");
		}
	}
}
