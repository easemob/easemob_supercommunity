package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.community.easeim.BuildConfig;
import com.community.easeim.R;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;


public class AboutHxActivity extends BaseInitActivity implements View.OnClickListener, EaseTitleBar.OnBackPressListener {
    private TextView tv_version;

    private ArrowItemView item_privacy;
    private ArrowItemView item_disclaimers;
    private ArrowItemView item_registered_account;
    private ArrowItemView item_release_time;
    private ArrowItemView item_sdk_version;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, AboutHxActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_about_hx;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tv_version = findViewById(R.id.tv_version);
        item_privacy = findViewById(R.id.item_privacy_regulations);
        item_disclaimers = findViewById(R.id.item_disclaimers);
        item_registered_account = findViewById(R.id.item_registered_easemob_account);
        item_release_time = findViewById(R.id.item_release_time);
        item_sdk_version = findViewById(R.id.item_sdk_version);
    }

    @Override
    protected void initData() {
        super.initData();
        item_sdk_version.getTvContent().setText(getString(R.string.em_about_hx_version, BuildConfig.VERSION_NAME));
        tv_version.setText(getString(R.string.em_about_hx_version, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void initListener() {
        super.initListener();
        item_privacy.setOnClickListener(this);
        item_disclaimers.setOnClickListener(this);
        item_registered_account.setOnClickListener(this);
        item_release_time.setOnClickListener(this);
        item_sdk_version.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.item_privacy_regulations :
                Uri uri = Uri.parse("https://www.easemob.com/protocol");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.item_disclaimers :
                DisclaimerActivity.actionStart(mContext);
                break;
            case R.id.item_registered_easemob_account :
                Uri uri1 = Uri.parse("https://console.easemob.com/user/register");
                intent = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent);
                break;
            case R.id.item_release_time :
//                jumpToCompanyIntroduction();
                break;
            case R.id.item_sdk_version :
//                jumpToIMIntroduction();
                break;


        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }
}
