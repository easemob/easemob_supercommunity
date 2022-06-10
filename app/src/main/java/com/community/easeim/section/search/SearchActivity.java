package com.community.easeim.section.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.easeim.R;
import com.community.easeim.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.easeim.imkit.interfaces.OnItemClickListener;
import com.community.easeim.imkit.widget.EaseRecyclerView;
import com.community.easeim.section.base.BaseInitActivity;


public abstract class SearchActivity extends BaseInitActivity {
    protected TextView titleBar;
    protected EditText query;
    protected TextView tvCancel;
    private ImageButton searchClear;
    protected EaseRecyclerView rvList;
    protected EaseBaseRecyclerViewAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.tv_name);
        query = findViewById(R.id.query);
        searchClear = findViewById(R.id.search_clear);
        rvList = findViewById(R.id.rv_list);
        tvCancel = findViewById(R.id.tv_cancel);

        //让EditText获取焦点并弹出软键盘
        query.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void initListener() {
        super.initListener();

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                } else {
                    searchClear.setVisibility(View.INVISIBLE);
                    adapter.clearData();
                }
            }
        });

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String search = query.getText().toString().trim();
                    if(!TextUtils.isEmpty(search)) {
                        searchMessages(search);
                    }
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                adapter.clearData();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        adapter = getAdapter();
        rvList.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onChildItemClick(view, position);
            }
        });
    }

    protected abstract EaseBaseRecyclerViewAdapter getAdapter();

    public abstract void searchMessages(String search);

    protected abstract void onChildItemClick(View view, int position);
}
