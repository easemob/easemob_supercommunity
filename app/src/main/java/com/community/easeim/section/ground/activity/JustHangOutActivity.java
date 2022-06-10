package com.community.easeim.section.ground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.ground.adapter.MsgRecordAdapter;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.ground.bean.MessageBean;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;

public class JustHangOutActivity extends BaseInitActivity {

    private RecyclerView mRvMsgRecord;
    private MsgRecordAdapter mMsgRecordAdapter;
    private TextView mTvJoin,mTvTitle;
    private GroundBean mGroundBean;
    private GroundGroupBean mGroundGroupBean;
    public static void actionStart(Context context, String groundGroupInfo,String userGroundInfo) {
        Intent intent = new Intent(context, JustHangOutActivity.class);
        intent.putExtra("groundGroupInfo",groundGroupInfo);
        intent.putExtra("userGroundInfo",userGroundInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_just_hang_out;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        String groundGroupInfo = intent.getStringExtra("groundGroupInfo");
        mGroundGroupBean = new Gson().fromJson(groundGroupInfo,GroundGroupBean.class);
        String userGroundInfo = intent.getStringExtra("userGroundInfo");
        mGroundBean = new Gson().fromJson(userGroundInfo,GroundBean.class);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTvTitle = findViewById(R.id.tv_name);
        mRvMsgRecord = findViewById(R.id.rv_message_record);
        mTvJoin = findViewById(R.id.tv_join_ground);
        mTvJoin.setClickable(false);
        mTvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("提示")
                        .setMsg("确认加入该社区?")
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                GroundManager.getInstance().saveUserAndGroundInfo(EMClient.getInstance().getCurrentUser(),mGroundBean.getGroundId());
                                //如果不是社区大厅群，同时加入社区大厅群和当前群
                                if (!TextUtils.equals(mGroundGroupBean.getGroupName(),getResources().getString(R.string.court_name))){
                                        getGroundHall();
                                    }
                                joinGroundGroup(mGroundGroupBean);
                            }
                        })
                        .show();
            }
        });

        mRvMsgRecord.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMsgRecord.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mMsgRecordAdapter = new MsgRecordAdapter(this);

        mRvMsgRecord.setAdapter(mMsgRecordAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mTvTitle.setText("#"+mGroundGroupBean.getGroupName());
        AppServerApi appServerApi = ApiManager.getInstance().appServerApi();
        RequestBody requestBody = ApiManager.requestBodyBuilder().put("group_id", mGroundGroupBean.getGroupId()).build();
        appServerApi.getMessageList(requestBody).enqueue(new AppServerApi.ResultCallback<List<MessageBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<MessageBean>>> call, @NonNull List<MessageBean> data) {
                if (data == null || data.size() < 1) {
                    mTvJoin.setVisibility(View.GONE);
                    ToastUtils.show("暂无聊天记录!");
                } else {
                    mTvJoin.setClickable(true);
                    mMsgRecordAdapter.setData(data);
                }

            }

            @Override
            protected void onFail(Call<Result<List<MessageBean>>> call, @NonNull Throwable t) {
                mTvJoin.setVisibility(View.GONE);
                ToastUtils.show("暂无聊天记录!");
                Log.e("TAG", "onFail: "+call.toString()+ "   "+t.toString());
            }
        });
    }


    private void getGroundHall(){
        GroundManager.getInstance().getGroundGroupBeanList(mGroundGroupBean.getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
            @Override
            public void onSuccess(List<GroundGroupBean> value) {
                GroundGroupBean groundGroupBean = value.get(0);
                joinGroundGroup(groundGroupBean);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }


    private void joinGroundGroup(GroundGroupBean groundGroupBean) {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
        if (group == null) {
            EMClient.getInstance().groupManager().asyncJoinGroup(groundGroupBean.getGroupId(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    DemoConstant.groundStrollIds.remove(groundGroupBean.getGroundId());
                    com.hjq.toast.ToastUtils.show("加入成功!");
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                    finish();
                }

                @Override
                public void onError(int code, String error) {
                    Log.e("tagqi", "onError: error " + error);
                }

                @Override
                public void onProgress(int progress, String status) {
                }
            });
        } else {
            ChatActivity.actionStart(mContext, groundGroupBean.getGroupId(), EaseConstant.CHATTYPE_GROUP);
            finish();
        }

    }
}