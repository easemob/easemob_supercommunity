package com.community.easeim.section.me.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.PreferenceManager;
import com.community.easeim.common.widget.ArrowItemView;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.imkit.widget.ShowBottomDialog;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.util.EMLog;

import java.util.Calendar;
import java.util.Map;

public class UserDetailActivity extends BaseInitActivity {
    static private String TAG =  "UserDetailActivity";
    private ArrowItemView itemNickname;
    private EaseImageView headImageView;
    private String headImageUrl = null;
    private String nickName;
    private ConstraintLayout itemAutograph,itemGender,itemBirthday;
    private ShowBottomDialog mShowBottomDialog;
    private TextView mTvAutograph,mTvGender,mTvBirthday;
    Calendar ca = Calendar.getInstance();
    int  mYear = ca.get(Calendar.YEAR);
    int  mMonth = ca.get(Calendar.MONTH);
    int  mDay = ca.get(Calendar.DAY_OF_MONTH);
    private String mBirthdayDate;

    public static void actionStart(Context context,String nickName,String url) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra("imageUrl",url);
        intent.putExtra("nickName",nickName);
        context.startActivity(intent);
    }

    /**
     * init intent
     * @param intent
     */
    @Override
    protected void initIntent(Intent intent){
        if(intent != null){
            headImageUrl = intent.getStringExtra("imageUrl");
            nickName = intent.getStringExtra("nickName");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_user_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        itemNickname = findViewById(R.id.item_nickname);
        headImageView = findViewById(R.id.tv_headImage_view);
        itemAutograph = findViewById(R.id.item_my_autograph);
        mTvAutograph = findViewById(R.id.tv_my_autograph);
        itemGender = findViewById(R.id.item_my_gender);
        mTvGender = findViewById(R.id.tv_sex_content);
        itemBirthday = findViewById(R.id.item_my_birthday);
        mTvBirthday = findViewById(R.id.tv_birthday_content);

        mShowBottomDialog = new ShowBottomDialog();
    }


    @Override
    protected void initListener() {
        super.initListener();
        itemNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OfflinePushNickActivity.class);
                intent.putExtra("isSign",false);
                intent.putExtra("nickName", nickName);
                startActivityForResult(intent, 2);
            }
        });
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHeadImageActivity.actionStartForResult(mContext,1, EaseConstant.TYPE_IMAGE_HEAD);
            }
        });
        itemAutograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改签名
                Intent intent = new Intent(mContext, OfflinePushNickActivity.class);
                intent.putExtra("isSign",true);
                intent.putExtra("nickName", mTvAutograph.getText().toString());
                startActivityForResult(intent, 3);
            }
        });

        itemGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改性别
                mShowBottomDialog.BottomDialog(UserDetailActivity.this);
                mShowBottomDialog.setOnItemClickListener(new ShowBottomDialog.OnItemClickListener() {
                    @Override
                    public void onSelectClick(int code, String value) {
                        mTvGender.setText(value);
                        LeanCloudManager.getInstance().updateUserGender(EMClient.getInstance().getCurrentUser(),getGender(value));
                        EaseEvent event = EaseEvent.create(DemoConstant.USER_INFO_CHANGE, EaseEvent.TYPE.CONTACT);
                        //发送联系人更新事件
                        event.message = getGender(value)+"";
                        LiveDataBus.get().with(DemoConstant.USER_INFO_CHANGE).postValue(event);
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
            }
        });

        itemBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改生日
                String string = mTvBirthday.getText().toString();
                if (!TextUtils.isEmpty(string) && string.contains("-")){
                    String[] split = string.split("-");
                    mYear = Integer.parseInt(split[0]);
                    mMonth = Integer.parseInt(split[1]) - 1;
                    mDay = Integer.parseInt(split[2]);
                }
                    DatePickerDialog datePickerDialog = new DatePickerDialog(UserDetailActivity.this, AlertDialog.THEME_HOLO_DARK,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mYear = year;
                                mMonth = month;
                                mDay = dayOfMonth;
                                mBirthdayDate = year + "-" + EaseCommonUtils.handleData(month + 1) + "-" + EaseCommonUtils.handleData(dayOfMonth);
                                mTvBirthday.setText(mBirthdayDate);


                               int age =  Integer.parseInt(EaseCommonUtils.getSysYear()) - mYear;
                                LeanCloudManager.getInstance().updateUserBirth(EMClient.getInstance().getCurrentUser(),mBirthdayDate,age+"");
                                EaseEvent event = EaseEvent.create(DemoConstant.USER_INFO_CHANGE, EaseEvent.TYPE.CONTACT);
                                //发送联系人更新事件
                                event.message = mBirthdayDate;
                                LiveDataBus.get().with(DemoConstant.USER_INFO_CHANGE).postValue(event);
                            }
                        },
                        mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        LeanCloudManager.getInstance().getUserInfo(DemoHelper.getInstance().getCurrentUser(), new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                itemNickname.getTvContent().setText(value.getNickname());
                nickName = value.getNickname();
                mTvGender.setText(getGender(value.getGender()));
                mTvAutograph.setText(value.getSign());
                mTvBirthday.setText(value.getBirth());
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.i(TAG, "onError   code  " + error + ":  message : " + errorMsg);
            }
        });


        if (headImageUrl != null && headImageUrl.length() > 0) {
            CustomImageUtil.getInstance().setHeadView(headImageView, headImageUrl);
        }
        if (headImageUrl == null || nickName == null) {
            intSelfDate();
        }
        //增加数据变化监听
        addLiveDataObserver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 && resultCode == RESULT_OK)) {
            if (data != null) {
                headImageUrl = data.getStringExtra("headImage");
                CustomImageUtil.getInstance().setHeadView(headImageView, headImageUrl);
            }
        } else if ((requestCode == 2 && resultCode == RESULT_OK)) {
            if (data != null) {
                nickName = data.getStringExtra("nickName");
                itemNickname.getTvContent().setText(nickName);
            }
        }else if ((requestCode == 3 && resultCode == RESULT_OK)) {
            if (data != null) {
                String sign = data.getStringExtra("user_sign");
                mTvAutograph.setText(sign);
            }
        }
    }

    private void intSelfDate() {
        String[] userId = new String[1];
        userId[0] = EMClient.getInstance().getCurrentUser();
        EMUserInfoType[] userInfoTypes = new EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userId, userInfoTypes, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfos) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMUserInfo userInfo = userInfos.get(EMClient.getInstance().getCurrentUser());

                        //昵称
                        if (userInfo != null && userInfo.getNickName() != null && userInfo.getNickName().length() > 0) {
                            nickName = userInfo.getNickName();
                            itemNickname.getTvContent().setText(nickName);
                            PreferenceManager.getInstance().setCurrentUserNick(nickName);
                        }
                        //头像
                        if (userInfo != null && userInfo.getAvatarUrl() != null && userInfo.getAvatarUrl().length() > 0) {
                            headImageUrl = userInfo.getAvatarUrl();
                            CustomImageUtil.getInstance().setHeadView(headImageView, headImageUrl);
                            PreferenceManager.getInstance().setCurrentUserAvatar(headImageUrl);
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG, "fetchUserInfoByIds error:" + error + " errorMsg:" + errorMsg);
            }
        });

    }

    protected void addLiveDataObserver() {
        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                CustomImageUtil.getInstance().setHeadView(headImageView, headImageUrl);
            }
        });
        LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                nickName = event.message;
            }
        });
    }


    private int getGender(String gender){
        if (TextUtils.equals("男",gender)){
            return 1;
        } else if(TextUtils.equals("女",gender)){
            return 2;
        } else if(TextUtils.equals("其它",gender)){
            return 0;
        } else if(TextUtils.equals("保密",gender)){
            return 3;
        } else {
            return 0;
        }
    }

    private String getGender(int gender){
        if (gender == 1){
            return "男";
        } else if(gender == 2){
            return "女";
        } else if(gender == 3){
            return "保密";
        } else {
            return "其它";
        }
    }
}
