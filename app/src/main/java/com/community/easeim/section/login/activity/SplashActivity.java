package com.community.easeim.section.login.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.MainActivity;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.PushAndMessageHelper;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.imkit.EaseIM;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseSystemMsgManager;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.login.viewmodels.LoginFragmentViewModel;
import com.community.easeim.section.login.viewmodels.LoginViewModel;
import com.community.easeim.section.login.viewmodels.SplashViewModel;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.util.EMLog;

import java.util.Locale;
import java.util.Map;

public class SplashActivity extends BaseInitActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private ImageView ivSplash;
    private TextView tvProduct;
    private SplashViewModel model;
    private LoginViewModel mLoginViewModel;
    private LoginFragmentViewModel mLoginFragmentViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_splash_activity;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ivSplash = findViewById(R.id.iv_splash);
        tvProduct = findViewById(R.id.tv_product);
    }

    @Override
    protected void initData() {
        super.initData();
        model = new ViewModelProvider(this).get(SplashViewModel.class);
        mLoginFragmentViewModel = new ViewModelProvider(this).get(LoginFragmentViewModel.class);
        mLoginViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        ivSplash.animate()
                .alpha(1)
                .setDuration(1500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loginSDK();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        tvProduct.animate()
                .alpha(1)
                .setDuration(1500)
                .start();

    }

    private void loginSDK() {
        DemoHelper.getInstance().setAutoLogin(true);
        model.getLoginData().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>(true) {
                @Override
                public void onSuccess(Boolean data) {
                    MainActivity.startAction(mContext);
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    EMLog.i("TAG", "error message = "+response.getMessage());
                    createImUser();
                }
            });

        });
    }

    private void createImUser() {
        boolean loggedIn = DemoHelper.getInstance().isLoggedIn();
        if (loggedIn) {
            MainActivity.startAction(mContext);
            finish();
            return;
        }

        String name = EaseCommonUtils.generateShortUuid().toLowerCase(Locale.ROOT);
        mLoginViewModel.register(name , EaseCommonUtils.getDefaultPsw());
        mLoginViewModel.getRegisterObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>(true) {
                @Override
                public void onSuccess(String data) {
                  mLoginFragmentViewModel.login(name , EaseCommonUtils.getDefaultPsw(),false);
                }

                @Override
                public void onError(int code, String message) {
                    if(code == EMError.USER_ALREADY_EXIST) {
                        ToastUtils.showToast(R.string.demo_error_user_already_exist);
                    }else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(String data) {
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


        mLoginFragmentViewModel.getLoginObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>(true) {
                @Override
                public void onSuccess(EaseUser data) {
                    Log.e("login", "login success");
                    EaseUser easeUser = new EaseUser(name);
                    String randomName = EaseCommonUtils.randomName(true, 3);
                    saveUserNickName(randomName);
                    easeUser.setNickname(randomName);
                    LeanCloudManager.getInstance().saveUserInfo(easeUser,null);

                    //第一次登录成功发一条系统消息：
                    // save invitation as message
                    Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
                    ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, "欢迎来到环信超级社区！");
                    ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.SYSTEM_MSG.name());
                    EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);
                    EaseIM.getInstance().getNotifier().vibrateAndPlayTone(null);
                    //跳转到主页
                    MainActivity.startAction(mContext);
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    if(code == EMError.USER_AUTHENTICATION_FAILED) {
                        ToastUtils.showToast(R.string.demo_error_user_authentication_failed);
                    }else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(EaseUser data) {
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


    private void saveUserNickName(String nickName){
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.NICKNAME, nickName, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                //发送联系人更新事件
                event.message = nickName;
                LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }
}
