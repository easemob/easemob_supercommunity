package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.community.easeim.R;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;


public class AccountSecurityActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {

    private ArrowItemView itemEquipments;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AccountSecurityActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_account_security;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        itemEquipments = findViewById(R.id.item_equipments);
    }

    @Override
    protected void initListener() {
        super.initListener();

        itemEquipments.setOnClickListener(this);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_equipments ://多端多设备管理
                MultiDeviceActivity.actionStart(mContext);
                break;
        }
    }
}
