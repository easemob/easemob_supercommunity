package com.community.easeim.section.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.PreferenceManager;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.activity.SelectUserCardActivity;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.me.headImage.HeadAdapter;
import com.community.easeim.section.me.headImage.HeadImageInfo;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/16/2021
 */

public class ChooseHeadImageActivity extends BaseInitActivity implements View.OnClickListener {
    private static final String TAG = SelectUserCardActivity.class.getSimpleName();

    private RecyclerView headImageListView;
    private TextView save_btn;
    List<HeadImageInfo> imageList = new ArrayList<>();
    private HeadAdapter mHeadAdapter;
    private String mChoosePosition = "-1";
    private TextView mTvName;
    private int mType;

    public static void actionStartForResult(Fragment fragment, int requestCode, int type) {
        Intent intent = new Intent(fragment.getActivity(), ChooseHeadImageActivity.class);
        intent.putExtra(EaseConstant.TYPE_IMAGE, type);
        fragment.startActivityForResult(intent,requestCode);
    }

    public static void actionStartForResult(Activity context, int requestCode,int type) {
        Intent intent = new Intent(context, ChooseHeadImageActivity.class);
        intent.putExtra(EaseConstant.TYPE_IMAGE, type);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_choose_headimage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvName = findViewById(R.id.tv_name);
        headImageListView = findViewById(R.id.headImage_ListView);
        save_btn = findViewById(R.id.btn_headImage_save);
        save_btn.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_headImage_save) {
            if (mType == EaseConstant.TYPE_IMAGE_HEAD){
                LeanCloudManager.getInstance().updateUserAvatar(EMClient.getInstance().getCurrentUser(),mChoosePosition);

                EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.AVATAR_URL, mChoosePosition, new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute :" + value);
                        showToast(R.string.demo_head_image_update_success);
                        PreferenceManager.getInstance().setCurrentUserAvatar(mChoosePosition);
                        DemoHelper.getInstance().getUserProfileManager().updateUserAvatar(mChoosePosition);
                        EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                        //发送联系人更新事件
                        event.message = mChoosePosition;
                        LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                        getIntent().putExtra("headImage", mChoosePosition);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        EMLog.d(TAG, "updateOwnInfoByAttribute  error:" + error + " errorMsg:" + errorMsg);
                        showToast(R.string.demo_head_image_update_failed);
                    }
                });
            }else if (mType == EaseConstant.TYPE_IMAGE_COVER){
                getIntent().putExtra("headImage", mChoosePosition);
                setResult(RESULT_OK, getIntent());
                finish();
            }

        }
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getIntent().getIntExtra(EaseConstant.TYPE_IMAGE, EaseConstant.TYPE_IMAGE_COVER);
        mTvName.setText(mType == EaseConstant.TYPE_IMAGE_HEAD?"请选择头像":"请选择封面");
        getHeadImageSrcIds(mType);
    }

    private void getHeadImageSrcIds(int type) {
        imageList.clear();
        if (type == EaseConstant.TYPE_IMAGE_HEAD) {
            for (int headId : CustomImageUtil.getInstance().getHeadIds()) {
                imageList.add(new HeadImageInfo(headId));
            }
        } else if (type == EaseConstant.TYPE_IMAGE_COVER) {
            for (int headId : CustomImageUtil.getInstance().getCoverIds()) {
                imageList.add(new HeadImageInfo(headId));
            }
        }

        initImageHeadListById();
    }

    private void initImageHeadListById() {
        if (imageList.isEmpty()) {
            headImageListView.setVisibility(View.GONE);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
            headImageListView.setLayoutManager(layoutManager);
            mHeadAdapter = new HeadAdapter();
            mHeadAdapter.setData(imageList);
            headImageListView.setAdapter(mHeadAdapter);
            mHeadAdapter.setOnHeadItemClick(new HeadAdapter.OnHeadItemClick() {
                @Override
                public void onItemClick(int position) {
                    mChoosePosition = position + "";
                    mHeadAdapter.setChooseIndex(position);
                }
            });
        }
    }
}
