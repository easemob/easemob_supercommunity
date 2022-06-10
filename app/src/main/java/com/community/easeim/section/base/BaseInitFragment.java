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
     * 获取布局id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 获取传递参数
     */
    protected void initArgument() {}

    /**
     * 初始化布局相关
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {
        Log.e("TAG", "fragment = "+this.getClass().getSimpleName());
    }


    /**
     * 初始化ViewModel相关
     */
    protected void initViewModel() {}

    /**
     * 初始化监听等
     */
    protected void initListener() {}

    /**
     * 初始化数据相关
     */
    protected void initData() {}

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
