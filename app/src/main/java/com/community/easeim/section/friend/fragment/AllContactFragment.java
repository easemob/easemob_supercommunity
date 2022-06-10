package com.community.easeim.section.friend.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.friend.activity.SearchContactActivity;
import com.community.easeim.section.friend.adapter.ContactAdapter;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class AllContactFragment extends BaseInitFragment implements View.OnClickListener, OnItemClickListener<EaseUser>, OnRefreshListener {

    private ContactAdapter mAdapter;
    private RecyclerView mRvContact;
    private ContactsViewModel mViewModel;
    private SmartRefreshLayout mSrlRefresh;
    private EditText mEtSearch;
    private ImageView mIvClear;
    private String mKey;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_all_contact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mSrlRefresh = findViewById(R.id.srl_refresh);
        mRvContact = findViewById(R.id.rv_contact);
        mEtSearch = findViewById(R.id.et_search);
        mIvClear = findViewById(R.id.iv_clear);
    }

    @Override
    protected void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRvContact.setLayoutManager(linearLayoutManager);
        mAdapter = new ContactAdapter();
        mRvContact.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        observer();
    }

    private void observer() {
        mViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);

        mViewModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    List<EaseUser> showList = new ArrayList<>();
                    if (mSrlRefresh.isRefreshing()) mSrlRefresh.finishRefresh();
                    if (!TextUtils.isEmpty(mKey)) {
                        for (EaseUser easeUser : list) {
                            if (easeUser.getNickname().contains(mKey)) {
                                showList.add(easeUser);
                            }
                        }
                    } else {
                        showList.addAll(list);
                    }

                    setAdapterData(showList);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                }
            });
        });

        mViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mViewModel.loadContactList(false);
                }
            });
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(true);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });


        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mViewModel.loadContactList(false);
            }
        });

        mViewModel.loadContactList(true);
    }

    private void doSearch(){
        mKey = mEtSearch.getText().toString();
        mViewModel.loadContactList(false);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.iv_add).setOnClickListener(this);
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String key = mEtSearch.getText().toString();
                if (TextUtils.isEmpty(key.trim())) {
                    mIvClear.setVisibility(View.GONE);
                    doSearch();
                } else {
                    mIvClear.setVisibility(View.VISIBLE);
                }
            }
        });

        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtSearch.setText("");
                doSearch();
            }
        });
        mSrlRefresh.setOnRefreshListener(this);
    }

    private void setAdapterData(List<EaseUser> list) {
        if (list != null) mAdapter.setData(list,mEtSearch.getText().toString());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_add) {
            SearchContactActivity.actionStart(mContext);
        }
    }

    @Override
    public void onItemClick(EaseUser easeUser) {
        ContactDetailActivity.actionStart(mContext, easeUser.getUsername());
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mViewModel.loadContactList(true);
    }
}
