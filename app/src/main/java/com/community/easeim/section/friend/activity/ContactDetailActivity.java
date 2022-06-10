package com.community.easeim.section.friend.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.contact.viewmodels.AddContactViewModel;
import com.community.easeim.section.contact.viewmodels.ContactDetailViewModel;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

public class ContactDetailActivity extends BaseInitActivity {

    private ContactDetailViewModel viewModel;
    private EaseUser mUser;
    private TextView mTvName;
    private CircleImageView mCivHead;
    private TextView mTvAdd;
    private TextView mTvId;
    private TextView mTvSign, mTvDes;
    private TextView mTvAge;
    private ImageView mImageViewGender;
    private String mId;
    private AddContactViewModel mAddContactViewModel;
    private ClipboardManager mClipboard;
    private EaseUser mContact;
    private LinearLayout mLlFollow;
    private TextView mTvFollow;
    private ImageView mIvFollow;
    private ImageView mIvMore;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTvName = findViewById(R.id.tv_name);
        mTvId = findViewById(R.id.tv_id);
        mCivHead = findViewById(R.id.civ_head);
        mTvAdd = findViewById(R.id.tv_add);
        mImageViewGender = findViewById(R.id.iv_gender);
        mTvSign = findViewById(R.id.tv_sign);
        mTvDes = findViewById(R.id.tv_des);
        mTvAge = findViewById(R.id.tv_age);
        mLlFollow = findViewById(R.id.ll_follow);
        mTvFollow = findViewById(R.id.tv_follow);
        mIvFollow = findViewById(R.id.iv_follow);
        mIvMore = findViewById(R.id.iv_more);
    }

    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra(DemoConstant.CONTACT_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        viewModel = new ViewModelProvider(mContext).get(ContactDetailViewModel.class);
        mId = getIntent().getStringExtra(DemoConstant.CONTACT_ID);
        boolean isFriend = DemoHelper.getInstance().getModel().isContactOrBlack(mId);

        mIvMore.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        mTvId.setText("环信ID：" + mId);

        LeanCloudManager.getInstance().getUserInfo(mId, new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                mContact = value;
                mTvName.setText(value.getNickname());
                mTvId.setText("环信ID：" + mId);
                setGenderView(value.getGender() + "", mImageViewGender);
                if (TextUtils.isEmpty(value.getSign())) {
                    mTvSign.setVisibility(View.GONE);
                    mTvDes.setText("这个人很懒什么也没有留下!");
                } else {
                    mTvSign.setVisibility(View.GONE);
                    mTvDes.setText(value.getSign());
                }
                if (TextUtils.isEmpty(value.getAge())) {
                    mTvAge.setText("");
                } else {
                    mTvAge.setText(" " + value.getAge() + "岁");
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.i("ContactDetailActivity", "onError   code  " + error + ":  message : " + errorMsg);
            }
        });


        mTvAdd.setVisibility(isFriend || TextUtils.equals(EMClient.getInstance().getCurrentUser(), mId) ? View.GONE : View.VISIBLE);

        viewModel.userInfoObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>() {
                @Override
                public void onSuccess(EaseUser data) {
                    mUser = data;
                    updateLayout();
                }
            });
        });
        viewModel.getUserInfoById(mId, isFriend);

        mAddContactViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mAddContactViewModel.getAddContact().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if (data) {
                        showToast(getResources().getString(R.string.em_add_contact_send_successful));
                    }
                }
            });
        });
    }

    @Override
    protected void initListener() {
        mTvId.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClipboard.setPrimaryClip(ClipData.newPlainText(null, mId));
                com.hjq.toast.ToastUtils.show("已复制用户ID到剪切板");
                return true;
            }
        });
    }

    public void onAddClick(View v) {
        mAddContactViewModel.addContact(mId, getResources().getString(R.string.em_add_contact_add_a_friend));
    }

    public void onChatClick(View v) {
        ChatActivity.actionStart(mContext, mId, mContact.getNickname(), EaseConstant.CHATTYPE_SINGLE);
    }

    private boolean isFollowed;
    public void onFollowClick(View v) {
        isFollowed = ! isFollowed;
        ToastUtils.showToast(isFollowed?"已喜欢":"已取消喜欢");
        mLlFollow.setBackgroundResource(isFollowed?R.drawable.sp_radius_48_47464b:R.drawable.sp_radius_48_27ae60);
        mTvFollow.setText(isFollowed?"已喜欢":"喜欢Ta");
        mIvFollow.setImageResource(isFollowed?R.drawable.icon_like_red:R.drawable.icon_like_detail);
    }

    private void updateLayout() {
        mTvName.setText(mUser.getNickname());
        CustomImageUtil.getInstance().setHeadView(mCivHead, mUser.getAvatar());
    }


    @Override
    protected void onMoreClick(View v) {
        ContactMoreActivity.actionStart(mContext, mId, mUser.getNickname());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_detail;
    }


    private void setGenderView(String gender, ImageView imageView) {
        if (TextUtils.equals(gender, "0") || TextUtils.equals(gender, "其它")) {
            imageView.setImageResource(R.drawable.other);
        } else if (TextUtils.equals(gender, "1") || TextUtils.equals(gender, "男")) {
            imageView.setImageResource(R.drawable.male);
        } else if (TextUtils.equals(gender, "2") || TextUtils.equals(gender, "女")) {
            imageView.setImageResource(R.drawable.female);
        } else if (TextUtils.equals(gender, "3") || TextUtils.equals(gender, "保密")) {
            imageView.setImageResource(R.drawable.secret);
        } else {
            imageView.setImageResource(R.drawable.other);
        }
    }
}
