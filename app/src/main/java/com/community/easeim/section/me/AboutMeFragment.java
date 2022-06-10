package com.community.easeim.section.me;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.PreferenceManager;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.me.activity.SetIndexActivity;
import com.community.easeim.section.me.activity.UserDetailActivity;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.util.EMLog;


public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    static private String TAG =  "AboutMeFragment";
    private TextView clUser;
    private ImageView avatar;
    private ImageView setting;
    private TextView nickName_view;
    private TextView userId_view;
    private ImageView genderView;
    private EMUserInfo userInfo;
    private TextView txtSign;
    private TextView txtAge;
    private ClipboardManager mClipboard;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        setting = findViewById(R.id.iv_my_setting);
        clUser = findViewById(R.id.tv_edit_data);
        nickName_view = findViewById(R.id.tv_nick_name);
        userId_view = findViewById(R.id.tv_userId);
        avatar = findViewById(R.id.img_avatar);
        genderView = findViewById(R.id.iv_sex);
        txtSign = findViewById(R.id.txt_autograph);
        txtAge = findViewById(R.id.txt_age);
    }

    @Override
    protected void initListener() {
        super.initListener();
        clUser.setOnClickListener(this);
        setting.setOnClickListener(this);
        userId_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClipboard.setPrimaryClip(ClipData.newPlainText(null, EMClient.getInstance().getCurrentUser()));
                com.hjq.toast.ToastUtils.show("已复制用户ID到剪切板");
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_data:
                if(userInfo != null){
                    UserDetailActivity.actionStart(mContext,userInfo.getNickName(),userInfo.getAvatarUrl());
                } else{
                    UserDetailActivity.actionStart(mContext,null,null);
                }
                break;
            case R.id.iv_my_setting:
                SetIndexActivity.actionStart(mContext);
                break;
        }
    }


    @Override
    public void initData(){
        super.initData();
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        CustomImageUtil.getInstance().setHeadView(avatar, PreferenceManager.getInstance().getCurrentUserAvatar());

        LeanCloudManager.getInstance().getUserInfo(DemoHelper.getInstance().getCurrentUser(), new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                nickName_view.setText(value.getNickname());
                userId_view.setText("  环信ID：" + EMClient.getInstance().getCurrentUser()+"  ");
                setGenderView(value.getGender()+"",genderView);
                if (TextUtils.isEmpty(value.getSign())){
                    txtSign.setText("这个人很懒什么也没有留下!");
                } else {
                    txtSign.setText(setSign(value.getSign()));
                }

                if (TextUtils.isEmpty(value.getAge())){
                    txtAge.setText("");
                } else {
                    txtAge.setText(" "+value.getAge()+"岁");
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.i(TAG,"onError   code  " +error + ":  message : "+errorMsg);
            }
        });
        addLiveDataObserver();
    }


    protected void addLiveDataObserver() {
        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                CustomImageUtil.getInstance().setHeadView(avatar, event.message);
                if(userInfo != null){
                    userInfo.setAvatarUrl(event.message);
                }
            }
        });

        LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                nickName_view.setText(event.message);
                userId_view.setText("  环信ID：" + EMClient.getInstance().getCurrentUser()+"  ");
                if(userInfo != null){
                    userInfo.setNickName(event.message);
                }
            }
        });

        LiveDataBus.get().with(DemoConstant.USER_INFO_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event != null) {
                LeanCloudManager.getInstance().getUserInfo(DemoHelper.getInstance().getCurrentUser(), new EMValueCallBack<EaseUser>() {
                    @Override
                    public void onSuccess(EaseUser value) {
                        nickName_view.setText(value.getNickname());
                        userId_view.setText("  环信ID：" + EMClient.getInstance().getCurrentUser()+"  ");
                        setGenderView(value.getGender()+"",genderView);
                        if (TextUtils.isEmpty(value.getSign())){
                            txtSign.setText("这个人很懒什么也没有留下!");
                        } else {
                            txtSign.setText(setSign(value.getSign()));
                        }

                        if (TextUtils.isEmpty(value.getAge())){
                            txtAge.setText("");
                        } else {
                            txtAge.setText(" "+value.getAge()+"岁");
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        EMLog.i(TAG,"onError   code  " +error + ":  message : "+errorMsg);
                    }
                });
            }
        });
    }



    private void setGenderView(String gender,ImageView imageView){
        if (TextUtils.equals(gender,"0") || TextUtils.equals(gender,"其它")){
            imageView.setImageResource(R.drawable.other);
        } else if(TextUtils.equals(gender,"1") || TextUtils.equals(gender,"男")){
            imageView.setImageResource(R.drawable.male);
        } else if(TextUtils.equals(gender,"2") || TextUtils.equals(gender,"女")){
            imageView.setImageResource(R.drawable.female);
        } else if(TextUtils.equals(gender,"3") || TextUtils.equals(gender,"保密")){
            imageView.setImageResource(R.drawable.secret);
        } else {
            imageView.setImageResource(R.drawable.other);
        }
    }

    private SpannableString setSign(String sign){
//        SpannableString sp = new SpannableString("个性签名: "+sign);
//        sp.setSpan(new AbsoluteSizeSpan(15,true), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new AbsoluteSizeSpan(13,true), 6, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.em_color_common_text_gray)), 6, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString sp = new SpannableString(sign);
        sp.setSpan(new AbsoluteSizeSpan(14,true), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.em_color_common_text_gray)), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }
}
