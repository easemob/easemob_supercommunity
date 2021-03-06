package com.community.easeim.section.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.community.easeim.DemoApplication;
import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.enums.Status;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.net.Resource;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.EaseProgressDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.StatusBarCompat;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.login.activity.LoginActivity;
import com.community.easeim.section.voice.VoiceTalkActivity;
import com.community.easeim.section.voice.bean.ChannelCmd;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.util.EMLog;

import java.util.List;

/**
 * ????????????activity,???????????????????????????
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mContext;
    private EaseProgressDialog dialog;
    private AlertDialog logoutDialog;
    private long dialogCreateTime;//dialog???????????????????????????dialog???????????????
    private Handler handler = new Handler();//??????dialog????????????

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        clearFragmentsBeforeCreate();
        registerAccountObservable();
        AppManager.getInstance().addActivity(this);
    }

    /**
     * ????????????????????????
     */
    protected void registerAccountObservable() {
        LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (!event.isAccountChange()) {
                return;
            }
            String accountEvent = event.event;
            if (TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED) ||
                    TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD) ||
                    TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE)) {
                DemoHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        finishOtherActivities();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.e("logout", "logout error: error code = " + code + " error message = " + error);
                        showToast("logout error: error code = " + code + " error message = " + error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            } else if (TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_CONFLICT)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_FORBIDDEN)) {
                DemoHelper.getInstance().logout(false, null);
                showExceptionDialog(accountEvent);
            }
        });
    }

    private void showExceptionDialog(String accountEvent) {
        if (logoutDialog != null && logoutDialog.isShowing() && !mContext.isFinishing()) {
            logoutDialog.dismiss();
        }
        logoutDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.em_account_logoff_notification)
                .setMessage(getExceptionMessageId(accountEvent))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishOtherActivities();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
        logoutDialog.show();
    }

    private int getExceptionMessageId(String exceptionType) {
        if (exceptionType.equals(DemoConstant.ACCOUNT_CONFLICT)) {
            return R.string.em_account_connect_conflict;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_REMOVED)) {
            return R.string.em_account_user_remove;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_FORBIDDEN)) {
            return R.string.em_account_user_forbidden;
        }
        return R.string.Network_error;
    }

    /**
     * ??????????????????Activity????????????Activity
     */
    protected void finishOtherActivities() {
        UserActivityLifecycleCallbacks lifecycleCallbacks = DemoApplication.getInstance().getLifecycleCallbacks();
        if (lifecycleCallbacks == null) {
            finish();
            return;
        }
        List<Activity> activities = lifecycleCallbacks.getActivityList();
        if (activities == null || activities.isEmpty()) {
            finish();
            return;
        }
        for (Activity activity : activities) {
            if (activity != lifecycleCallbacks.current()) {
                activity.finish();
            }
        }
    }


    /**
     * ?????????toolbar
     * @param toolbar
     */
    public void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//?????????
            getSupportActionBar().setDisplayShowTitleEnabled(false);//?????????title
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * ???????????????????????????
     * @param mContext
     * @param colorId
     */
    public static void setToolbarCustomColor(AppCompatActivity mContext, int colorId) {
        Drawable leftArrow = ContextCompat.getDrawable(mContext, R.drawable.abc_ic_ab_back_material);
        if (leftArrow != null) {
            leftArrow.setColorFilter(ContextCompat.getColor(mContext, colorId), PorterDuff.Mode.SRC_ATOP);
            if (mContext.getSupportActionBar() != null) {
                mContext.getSupportActionBar().setHomeAsUpIndicator(leftArrow);
            }
        }
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
        dismissLoading();
    }

    /**
     * hide keyboard
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * toast by string
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    /**
     * toast by string res
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (null != this.getCurrentFocus()) {
            /**
             * ?????????????????? ???????????????
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        return super.onTouchEvent(event);
    }


    /**
     * ??????????????????
     */
    public void setFitSystemForTheme() {
        setFitSystemForTheme(false, R.color.black);
        setStatusBarTextColor(false);
    }

    /**
     * ????????????????????????????????????
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, R.color.black);
        setStatusBarTextColor(false);
    }

    /**
     * ????????????????????????????????????
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme2(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, "#ffffffff");
        setStatusBarTextColor(false);
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param fitSystemForTheme
     * @param colorId ??????????????????
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, @ColorRes int colorId) {
        setFitSystem(fitSystemForTheme);
        //????????????
        StatusBarCompat.compat(this, ContextCompat.getColor(mContext, colorId));
    }

    /**
     * ???????????????????????????
     * @param isLight ?????????????????????
     */
    public void setStatusBarTextColor(boolean isLight) {
        StatusBarCompat.setLightStatusBar(mContext, true);
    }


    /**
     * ??????????????????????????????????????????????????????
     * @param fitSystemForTheme true ???????????????
     * @param color ???????????????
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, String color) {
        setFitSystem(fitSystemForTheme);
        //????????????
        StatusBarCompat.compat(mContext, Color.parseColor(color));
    }

    /**
     * ????????????????????????
     * @param fitSystemForTheme
     */
    public void setFitSystem(boolean fitSystemForTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (fitSystemForTheme) {
            ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * ??????Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (response == null) {
            return;
        }
        if (response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        } else if (response.status == Status.ERROR) {
            callback.hideLoading();
            if (!callback.hideErrorMsg) {
                showToast(response.getMessage());
            }
            callback.onError(response.errorCode, response.getMessage());
        } else if (response.status == Status.LOADING) {
            callback.onLoading(response.data);
        }
    }

    public boolean isMessageChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("message")) {
            return true;
        }
        return false;
    }

    public boolean isContactChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("contact")) {
            return true;
        }
        return false;
    }

    public boolean isGroupInviteChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("invite")) {
            return true;
        }
        return false;
    }

    public boolean isNotify(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("invite")) {
            return true;
        }
        return false;
    }

    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

    public void showLoading(String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mContext.isFinishing()) {
            return;
        }
        dialogCreateTime = System.currentTimeMillis();
        dialog = new EaseProgressDialog.Builder(mContext)
                .setLoadingMessage(message)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            //??????dialog?????????????????????????????????1s?????????
            if (System.currentTimeMillis() - dialogCreateTime < 500 && !isFinishing()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                }, 1000);
            } else {
                dialog.dismiss();
                dialog = null;
            }

        }
    }

    /**
     * ????????????Activity???????????????fragment????????????
     */
    public void clearFragmentsBeforeCreate() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commitNow();
    }
}
