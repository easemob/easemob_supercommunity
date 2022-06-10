package com.community.easeim.section.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.viewmodels.ContactBlackViewModel;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.home.dialog.CustomDialog;

import java.util.List;

public class ContactMoreActivity extends BaseInitActivity {

    private ContactsViewModel mViewModel;
    private String mId;
    private String mName;
    private Switch mSwBlack;

    public static void actionStart(Context context, String id, String name) {
        Intent intent = new Intent(context, ContactMoreActivity.class);
        intent.putExtra(EaseConstant.CONTACT_ID, id);
        intent.putExtra(EaseConstant.CONTACT_NAME, name);
        context.startActivity(intent);
    }

    private boolean isInBlackList;

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(EaseConstant.CONTACT_ID);
        mName = getIntent().getStringExtra(EaseConstant.CONTACT_NAME);
        mViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        mViewModel.blackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    isInBlackList = false;
                    for (EaseUser easeUser : list) {
                        if (TextUtils.equals(easeUser.getUsername(), mId)) {
                            isInBlackList = true;
                            break;
                        }
                    }
                    mSwBlack.setChecked(isInBlackList);
                }
            });
        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    ToastUtils.showToast(data ? "已拉黑" : "已取消拉黑");
                }
            });
        });
        mViewModel.getBlackList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_more;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mSwBlack = findViewById(R.id.sw_black);
    }

    public void onAdd2Black(View v) {
        if (isInBlackList) {
            mViewModel.removeUserFromBlackList(mId);
            return;
        }
        new CustomDialog
                .Builder(mContext)
                .setTitle("提示")
                .setMsg("确认拉黑“" + mName + "”？")
                .setMsgKey(mName)
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        mViewModel.addUserToBlackList(mId, false);
                    }
                })
                .show();
    }

    public void onDeleteContact(View v) {
        new CustomDialog
                .Builder(mContext)
                .setTitle("提示")
                .setMsg("确认删除“" + mName + "”？")
                .setMsgKey(mName)
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        mViewModel.deleteContact(mId);
                        AppManager.getInstance().finishActivity(ContactMoreActivity.class);
                        AppManager.getInstance().finishActivity(ContactDetailActivity.class);
                    }
                })
                .show();
    }
}
