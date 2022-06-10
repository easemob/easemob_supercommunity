package com.community.easeim.section.ground.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.imkit.utils.EaseEditTextUtils;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.group.viewmodels.GroupDetailViewModel;
import com.hjq.toast.ToastUtils;
import com.hyphenate.EMValueCallBack;


public class GroundModifiyActivity extends BaseInitActivity implements TextWatcher {

    private String nickName;
    private String objId;
    private int modifyType;// 1 社区名称，2 社区简介，3 用户社区昵称 4 频道名称（群名） ，5 群公告
    private TextView mTextViewTitle;
    private Drawable clear;
    private EditText inputNickName;
    private TextView limitNickName, nickNameTips;
    private LinearLayout mLinearLayoutNickName;
    private GroupDetailViewModel mGroupDetailViewModel;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroundModifiyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        if (intent != null) {
            modifyType = intent.getIntExtra("modifyType", 0);
            nickName = intent.getStringExtra("nickName");
            objId = intent.getStringExtra("objId");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ground_modifiy;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTextViewTitle = findViewById(R.id.tv_name);
        limitNickName = findViewById(R.id.tv_nick_name_limit);

        inputNickName = (EditText) findViewById(R.id.et_input_nickname);
        inputNickName.requestFocus();
        inputNickName.addTextChangedListener(this);
        EaseEditTextUtils.clearEditTextListener(inputNickName);
        nickNameTips = findViewById(R.id.tv_hint_nickname);
        mLinearLayoutNickName = findViewById(R.id.ll_nick_name);
        InputFilter[] inputFilter = new InputFilter[1];

        if (modifyType == 1) {
            mTextViewTitle.setText("修改社区名称");
            inputNickName.setHint("请输入社区名称");
            inputFilter[0] = new InputFilter.LengthFilter(16);
            inputNickName.setFilters(inputFilter);
            inputNickName.setMinLines(2);
            inputNickName.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else if (modifyType == 2) {
            mTextViewTitle.setText("修改社区简介");
            inputNickName.setHint("请输入社区简介");
            inputFilter[0] = new InputFilter.LengthFilter(120);
            inputNickName.setFilters(inputFilter);
            inputNickName.setMinLines(10);
            inputNickName.setGravity(Gravity.LEFT | Gravity.TOP);
            nickNameTips.setVisibility(View.GONE);
        } else if (modifyType == 3) {
            mTextViewTitle.setText("我在本社区名称");
            inputNickName.setHint("请输入新昵称");
            inputFilter[0] = new InputFilter.LengthFilter(16);
            inputNickName.setFilters(inputFilter);
            inputNickName.setMinLines(2);
            inputNickName.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            nickNameTips.setText("人如其名，其叶蓁蓁。");
        } else if (modifyType == 4) {
            mTextViewTitle.setText("频道名称");
            inputNickName.setHint("请输入新的频道名称");
            inputFilter[0] = new InputFilter.LengthFilter(16);
            inputNickName.setFilters(inputFilter);
            inputNickName.setMinLines(2);
            inputNickName.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            nickNameTips.setVisibility(View.GONE);
        } else if (modifyType == 5) {
            mTextViewTitle.setText("频道公告");
            inputNickName.setHint("请输入频道公告");
            inputFilter[0] = new InputFilter.LengthFilter(120);
            inputNickName.setFilters(inputFilter);
            inputNickName.setMinLines(10);
            limitNickName.setText("0/120");
            inputNickName.setGravity(Gravity.LEFT | Gravity.TOP);
            nickNameTips.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (modifyType == 1) {
                    updateGroundName();
                } else if (modifyType == 2) {
                    updateDescribe();
                } else if (modifyType == 3) {
                    updateNickName();
                } else if (modifyType == 4) {
                    updateGroupName();
                } else if (modifyType == 5) {
                    updateGroupAnnouncement();
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (nickName != null && nickName.length() > 0) {
            inputNickName.setText(nickName);
        }

        if (modifyType == 1 || modifyType == 3) {
            clear = getResources().getDrawable(R.drawable.mine_delete);
            EaseEditTextUtils.showRightDrawable(inputNickName, clear);
        }
        mGroupDetailViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);

        mGroupDetailViewModel.getRefreshObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    Intent intent = new Intent();
                    intent.putExtra(getResultKey(), data);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        });
    }

    private String getResultKey() {
        String key = "";
        //1 社区名称，2 社区简介，3 用户社区昵称 4 频道名称（群名） ，5 群公告
        switch (modifyType) {
            case 1:
                key = "groundName";
                break;
            case 2:
                key = "groundDescribe";
                break;
            case 3:
                key = "groundNickName";
                break;
            case 4:
                key = "groupName";
                break;
            case 5:
                key = "announcement";
                break;
        }
        return key;
    }

    private void updateGroundName() {
        String groundName = inputNickName.getText().toString();
        if (!TextUtils.isEmpty(groundName)) {
            GroundManager.getInstance().updateGroundName(objId, groundName, new EMValueCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    getIntent().putExtra(getResultKey(), groundName);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }

                @Override
                public void onError(int error, String errorMsg) {
                    ToastUtils.show("修改失败!请检查网络并重试!");
                }
            });
        } else {
            ToastUtils.show("社区名称不能为空!");
        }
    }

    private void updateDescribe() {
        String groundDescribe = inputNickName.getText().toString();
        if (!TextUtils.isEmpty(groundDescribe)) {
            GroundManager.getInstance().updateGroundDescribe(objId, groundDescribe, new EMValueCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean value) {
                    getIntent().putExtra(getResultKey(), groundDescribe);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }

                @Override
                public void onError(int error, String errorMsg) {
                    ToastUtils.show("修改失败!请检查网络并重试!");
                }
            });
        }
    }

    private void updateNickName() {
        String nick = inputNickName.getText().toString();
        if (!TextUtils.isEmpty(nick)) {
//            GroundManager.getInstance().updateGroundNickName(objId, nick, new EMValueCallBack<Boolean>() {
//                @Override
//                public void onSuccess(Boolean value) {
//                    getIntent().putExtra(getResultKey(), nick);
//                    setResult(RESULT_OK, getIntent());
//                    finish();
//                }
//
//                @Override
//                public void onError(int error, String errorMsg) {
//                    ToastUtils.show("修改失败!请检查网络并重试!");
//                }
//            });
        } else {
            showToast(R.string.demo_offline_nickname_is_empty);
        }
    }


    private void updateGroupName() {
        String groupName = inputNickName.getText().toString();
        if (TextUtils.equals(getResources().getString(R.string.court_name), groupName)) {
            ToastUtils.show("\"社区大厅\"为保留渠道名称不能修改!");
            return;
        }

        if (!TextUtils.isEmpty(groupName)) {
            mGroupDetailViewModel.setGroupName(objId, groupName);
            GroundManager.getInstance().updateGroupName(objId, groupName);
        } else {
            ToastUtils.show("频道名称不能为空!");
        }
    }

    private void updateGroupAnnouncement() {
        String announcement = inputNickName.getText().toString();
        if (!TextUtils.isEmpty(announcement)) {
            mGroupDetailViewModel.setGroupAnnouncement(objId, announcement);
        } else {
            ToastUtils.show("频道公告不能为空!");
        }
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
        if (modifyType == 1) {
            limitNickName.setText(length + "/16");
        } else if (modifyType == 2 || modifyType == 5) {
            limitNickName.setText(length + "/120");
        } else if (modifyType == 3 || modifyType == 4) {
            limitNickName.setText(length + "/16");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}