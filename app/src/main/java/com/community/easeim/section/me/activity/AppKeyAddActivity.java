package com.community.easeim.section.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;


public class AppKeyAddActivity extends BaseInitActivity implements EaseTitleBar.OnRightClickListener, EaseTitleBar.OnBackPressListener {
    private EaseTitleBar titleBar;
    private EditText editCustomAppkey;

    public static void actionStartForResult(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, AppKeyAddActivity.class);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_appkey_add;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        editCustomAppkey = findViewById(R.id.edit_custom_appkey);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        titleBar.setOnRightClickListener(this);
    }

    @Override
    public void onRightClick(View view) {
        String appKey = editCustomAppkey.getText().toString().trim();
        if(!TextUtils.isEmpty(appKey)) {
            DemoHelper.getInstance().getModel().saveAppKey(appKey);
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
