package com.community.easeim.section.friend.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.friend.activity.SearchContactActivity;
import com.community.easeim.section.friend.adapter.SuggestContactAdapter;
import com.community.easeim.section.me.headImage.CustomImageUtil;

import java.util.ArrayList;
import java.util.List;

public class AddContactFragment extends BaseInitFragment implements View.OnClickListener {

    private MyBottomSheetDialog mSuggestDialog;
    private RecyclerView mRvSuggest;
    private SuggestContactAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_contact;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView(Bundle savedInstanceState) {
        mSuggestDialog = new MyBottomSheetDialog(mContext);
        mSuggestDialog.setContentView(R.layout.sheet_friend_suggest);
        mRvSuggest = mSuggestDialog.findViewById(R.id.rv_suggest);
        mSuggestDialog.findViewById(R.id.btn_watch_all).setOnClickListener(this);
        mSuggestDialog.findViewById(R.id.btn_watch).setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        mRvSuggest.setLayoutManager(gridLayoutManager);

        mAdapter = new SuggestContactAdapter();
        mRvSuggest.setAdapter(mAdapter);
        List<EaseUser> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            EaseUser user = new EaseUser();
            user.setAvatar(CustomImageUtil.getInstance().getNum(0, 50));
            user.setGender(Math.floorMod(i, 4));
            user.setNickname("达人style");
            list.add(user);
        }
        mAdapter.setData(list);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_suggest).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_search) {
            SearchContactActivity.actionStart(mContext);
        } else if (id == R.id.tv_suggest) {
            mSuggestDialog.show();
        } else if (id == R.id.btn_watch_all) {
            mAdapter.setAllChecked(true);
            ToastUtils.showToast("已关注");
            mSuggestDialog.dismiss();
        }else if (id == R.id.btn_watch){
            ToastUtils.showToast("已关注");
            mSuggestDialog.dismiss();
        }
    }
}
