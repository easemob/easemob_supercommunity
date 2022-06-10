package com.community.easeim.section.friend.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.contact.viewmodels.AddContactViewModel;
import com.community.easeim.section.friend.adapter.SearchContactAdapter;
import com.hyphenate.EMValueCallBack;

import java.util.ArrayList;
import java.util.List;

public class SearchContactActivity extends BaseInitActivity implements TextView.OnEditorActionListener, SearchContactAdapter.OnAddClickListener {

    private RecyclerView mRvResult;
    private SearchContactAdapter mAdapter;
    private EditText mEtSearch;
    private AddContactViewModel mAddContactViewModel;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchContactActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mEtSearch = findViewById(R.id.et_search);

        mRvResult = findViewById(R.id.rv_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRvResult.setLayoutManager(layoutManager);

        mAdapter = new SearchContactAdapter();
        mAdapter.setOnAddClickListener(this);
        mRvResult.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mAddContactViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mAddContactViewModel.getAddContact().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if (data) {
                        ToastUtils.showCenterToast("提示",getString(R.string.em_add_contact_send_successful),0, Toast.LENGTH_LONG);
                    }
                }
            });
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_contact;
    }

    @Override
    protected void initListener() {
        mEtSearch.setOnEditorActionListener(this);
    }

    public void onCancel(View v) {
        finish();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String id = mEtSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(id)) {
                doSearch(id);
            }
            hideKeyboard();
            return true;
        }
        return false;
    }

    private void doSearch(String id) {
        LeanCloudManager.getInstance().getUserInfo(id, new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser info) {
                if (info != null){
                    List<EaseUser> list = new ArrayList<>();
                    EaseUser user = new EaseUser();
                    user.setUsername(id);
                    user.setAvatar(info.getAvatar());
                    user.setNickname(info.getNickname());
                    list.add(user);
                    mAdapter.setData(list);
                } else {
                    mAdapter.setData(new ArrayList<>());
                    com.hjq.toast.ToastUtils.show("查询的用户不存在!");
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                mAdapter.setData(new ArrayList<>());
                com.hjq.toast.ToastUtils.show("查询的用户不存在!");
            }
        });
    }

    @Override
    public void onAdd(EaseUser easeUser) {
        mAddContactViewModel.addContact(easeUser.getUsername(), getResources().getString(R.string.em_add_contact_add_a_friend));
    }

    @Override
    public void onItemClick(EaseUser easeUser) {
        ContactDetailActivity.actionStart(mContext,easeUser.getUsername());
    }
}
