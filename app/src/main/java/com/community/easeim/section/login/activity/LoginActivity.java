package com.community.easeim.section.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.section.base.BaseFragment;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.login.fragment.LoginFragment;
import com.community.easeim.section.login.fragment.RegisterFragment;
import com.community.easeim.section.login.fragment.ServerSetFragment;
import com.community.easeim.R;
import com.community.easeim.section.login.viewmodels.LoginViewModel;

public class LoginActivity extends BaseInitActivity {

    public static void startAction(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fl_fragment, new LoginFragment()).
                commit();
    }

    @Override
    protected void initData() {
        super.initData();
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getPageSelect().observe(this, page -> {
            if(page == 0) {
                return;
            }
            if(page == 1) {
                replace(new RegisterFragment());
            }else if(page == 2) {
                replace(new ServerSetFragment());
            }

        });
    }

    private void replace(BaseFragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left,
                        R.anim.slide_in_from_left,
                        R.anim.slide_out_to_right
                ).
                replace(R.id.fl_fragment, fragment).
                addToBackStack(null).
                commit();
    }
}
