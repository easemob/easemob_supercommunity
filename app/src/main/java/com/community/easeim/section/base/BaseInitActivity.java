package com.community.easeim.section.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.community.easeim.R;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.section.av.MultipleVideoActivity;
import com.community.easeim.section.av.VideoCallActivity;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallFloatWindow;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseCallState;

import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public abstract class BaseInitActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initSystemFit();
        initIntent(getIntent());
        initView(savedInstanceState);
        initListener();
        initData();

        initCommonButton();
    }

    private void initCommonButton() {
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppManager.getInstance().finishActivity(BaseInitActivity.this);
                }
            });
        }
        ImageView ivMore = findViewById(R.id.iv_more);
        if (ivMore != null) {
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMoreClick(view);
                }
            });
        }
    }

    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
        getWindow().setNavigationBarColor(Color.BLACK);
    }

    protected void onMoreClick(View v) {

    }

    /**
     * get layout id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * init intent
     * @param intent
     */
    protected void initIntent(Intent intent) {
    }

    /**
     * init view
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {

    }

    /**
     * init listener
     */
    protected void initListener() {
    }

    /**
     * init data
     */
    protected void initData() {
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE && !EaseCallFloatWindow.getInstance(mContext).isShowing()) {
            if (EaseCallKit.getInstance().getCallType() == EaseCallType.CONFERENCE_CALL) {
                Intent intent = new Intent(mContext, MultipleVideoActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, VideoCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }
}
