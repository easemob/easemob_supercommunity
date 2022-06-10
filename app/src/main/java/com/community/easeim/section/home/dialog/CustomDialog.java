package com.community.easeim.section.home.dialog;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.community.easeim.section.base.BaseActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;

public class CustomDialog extends CustomDialogFragment {
    public static final String DELETE_KEY = "delete";

    @Override
    public void initArgument() {
        if(getArguments() != null) {
            title = getArguments().getString(DELETE_KEY);
        }
    }

    public static class Builder extends CustomDialogFragment.Builder {

        public Builder(AppCompatActivity context) {
            super(context);
        }

        @Override
        protected CustomDialog getFragment() {
            return new CustomDialog();
        }

    }
}
