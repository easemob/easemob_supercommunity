package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.community.easeim.R;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.activity.ContactBlackListActivity;


public class PrivacyIndexActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {

    private ArrowItemView itemBlackManager;
    private ArrowItemView itemEquipmentManager;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, PrivacyIndexActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_privacy_index;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        itemBlackManager = findViewById(R.id.item_black_manager);
        itemEquipmentManager = findViewById(R.id.item_equipment_manager);
    }

    @Override
    protected void initListener() {
        super.initListener();

        itemBlackManager.setOnClickListener(this);
        itemEquipmentManager.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_black_manager :
                ContactBlackListActivity.actionStart(mContext);
                break;
            case R.id.item_equipment_manager :

                break;
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
