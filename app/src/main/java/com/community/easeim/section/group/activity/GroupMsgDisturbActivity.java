package com.community.easeim.section.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.group.viewmodels.GroupDetailViewModel;

import java.util.List;

public class GroupMsgDisturbActivity extends BaseInitActivity  {

    private CheckBox mCbNoMsg,mCbMsg;
    private String groupId;
    private GroupDetailViewModel viewModel;
    public static void actionStart(Context context,String groupId) {
        Intent intent = new Intent(context, GroupMsgDisturbActivity.class);
        intent.putExtra("groupId",groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_msg_disturb;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mCbMsg = findViewById(R.id.cb_all_msg);
        mCbNoMsg = findViewById(R.id.cb_no_msg);

        mCbMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    mCbNoMsg.setChecked(false);
                    mCbMsg.setChecked(true);
                    viewModel.updatePushServiceForGroup(groupId, false);
                } else {
                    mCbNoMsg.setChecked(true);
                    mCbMsg.setChecked(false);
                    viewModel.updatePushServiceForGroup(groupId, true);
                }
            }
        });


        mCbNoMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mCbNoMsg.setChecked(true);
                    mCbMsg.setChecked(false);
                    viewModel.updatePushServiceForGroup(groupId, true);
                }  else {
                    mCbNoMsg.setChecked(false);
                    mCbMsg.setChecked(true);
                    viewModel.updatePushServiceForGroup(groupId, false);
                }
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        groupId = getIntent().getStringExtra("groupId");

        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);

        viewModel.offPushObservable().observe(this, response -> {
            if(response) {
                viewModel.getGroup(groupId);
            }
        });
        List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
        if (disabledIds != null && disabledIds.contains(groupId)) {
            mCbNoMsg.setChecked(true);
            mCbMsg.setChecked(false);
        } else {
            mCbNoMsg.setChecked(false);
            mCbMsg.setChecked(true);
        }

    }
}