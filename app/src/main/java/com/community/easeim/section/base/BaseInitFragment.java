package com.community.easeim.section.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.community.easeim.R;
import com.community.easeim.imkit.ui.base.EaseBaseFragment;

public abstract class BaseInitFragment extends BaseFragment {

    private EaseBaseFragment mCurrentFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initArgument();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initViewModel();
        initListener();
    }

    protected void replace(EaseBaseFragment fragment, String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(R.id.fcv_fragment, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }

    protected void replace(EaseBaseFragment fragment, int id,String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(id, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * ????????????id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * ??????????????????
     */
    protected void initArgument() {}

    /**
     * ?????????????????????
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {
        Log.e("TAG", "fragment = "+this.getClass().getSimpleName());
    }


    /**
     * ?????????ViewModel??????
     */
    protected void initViewModel() {}

    /**
     * ??????????????????
     */
    protected void initListener() {}

    /**
     * ?????????????????????
     */
    protected void initData() {}

    /**
     * ??????id????????????view??????????????????onViewCreated()???????????????????????????
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
