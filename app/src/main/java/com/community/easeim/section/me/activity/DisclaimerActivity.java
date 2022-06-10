package com.community.easeim.section.me.activity;

import android.content.Context;
import android.content.Intent;

import com.community.easeim.R;
import com.community.easeim.section.base.BaseInitActivity;

public class DisclaimerActivity extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DisclaimerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disclaimer;
    }
}