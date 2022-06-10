package com.community.easeim.section.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.community.easeim.R;
import com.community.easeim.imkit.utils.EaseCommonUtils;
import com.community.easeim.imkit.utils.KeyWordUtil;
import com.community.easeim.section.base.BaseDialogFragment;

import java.lang.reflect.Field;

public class CustomDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    public TextView mTvDialogTitle;
    public TextView mBtnDialogCancel;
    public TextView mBtnDialogConfirm;
    public OnConfirmClickListener mOnConfirmClickListener;
    public onCancelClickListener mOnCancelClickListener;

    public String title;
    public String msg;
    public String titleKey;
    private int confirmColor;
    private String confirm;
    private boolean showCancel;
    private int titleColor;
    private String cancel;
    private float titleSize;
    private TextView mTvDialogMsg;
    private String mMsgKey;

    @Override
    public int getLayoutId() {
        return R.layout.custom_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //宽度填满，高度自适应
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);

            View view = getView();
            if (view != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params instanceof FrameLayout.LayoutParams) {
                    int margin = (int) EaseCommonUtils.dip2px(mContext, 30);
                    ((FrameLayout.LayoutParams) params).setMargins(margin, 0, margin, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = CustomDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = CustomDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = CustomDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = CustomDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }

    public void initView(Bundle savedInstanceState) {
        mTvDialogTitle = findViewById(R.id.tv_dialog_title);
        mTvDialogMsg = findViewById(R.id.tv_dialog_msg);
        mBtnDialogCancel = findViewById(R.id.btn_dialog_cancel);
        mBtnDialogConfirm = findViewById(R.id.btn_dialog_confirm);

        if (!TextUtils.isEmpty(title)) {
            if (!TextUtils.isEmpty(titleKey)) {
                mTvDialogTitle.setText(KeyWordUtil.matcherSearchTitleTextColor(Color.parseColor("#27ae60"), title, titleKey));
            } else {
                mTvDialogTitle.setText(title);
            }
        }
        if (!TextUtils.isEmpty(msg)) {
            if (!TextUtils.isEmpty(mMsgKey)) {
                mTvDialogMsg.setText(KeyWordUtil.matcherSearchTitleTextColor(Color.parseColor("#27ae60"), msg, mMsgKey));
            } else {
                mTvDialogMsg.setText(msg);
            }
        } else {
            mTvDialogMsg.setVisibility(View.GONE);
        }
        if (titleColor != 0) {
            mTvDialogTitle.setTextColor(titleColor);
        }
        if (titleSize != 0) {
            mTvDialogTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        }
        if (!TextUtils.isEmpty(confirm)) {
            mBtnDialogConfirm.setText(confirm);
        }
        if (confirmColor != 0) {
            mBtnDialogConfirm.setTextColor(confirmColor);
        }
        if (!TextUtils.isEmpty(cancel)) {
            mBtnDialogCancel.setText(cancel);
        }
    }

    public void initListener() {
        mBtnDialogCancel.setOnClickListener(this);
        mBtnDialogConfirm.setOnClickListener(this);
    }

    public void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_cancel:
                onCancelClick(v);
                break;
            case R.id.btn_dialog_confirm:
                onConfirmClick(v);
                break;
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    /**
     * 设置取消事件
     * @param cancelClickListener
     */
    public void setOnCancelClickListener(onCancelClickListener cancelClickListener) {
        this.mOnCancelClickListener = cancelClickListener;
    }

    /**
     * 点击了取消按钮
     * @param v
     */
    public void onCancelClick(View v) {
        dismiss();
        if (mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v);
        }
    }

    /**
     * 点击了确认按钮
     * @param v
     */
    public void onConfirmClick(View v) {
        dismiss();
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v);
        }
    }

    /**
     * 确定事件的点击事件
     */
    public interface OnConfirmClickListener {
        void onConfirmClick(View view);
    }

    /**
     * 点击取消
     */
    public interface onCancelClickListener {
        void onCancelClick(View view);
    }

    public static class Builder {
        public AppCompatActivity context;
        private String title;
        private String titleKey;
        private String msgKey;
        private String msg;
        private int titleColor;
        private float titleSize;
        private boolean showCancel;
        private String confirmText;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private int confirmColor;
        private Bundle bundle;
        private String cancel;

        public Builder(AppCompatActivity context) {
            this.context = context;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitleKey(String titleKey) {
            this.titleKey = titleKey;
            return this;
        }

        public Builder setTitleColor(@ColorRes int color) {
            this.titleColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setTitleColorInt(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public Builder setTitleSize(float size) {
            this.titleSize = size;
            return this;
        }

        public Builder showCancelButton(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(String confirm, OnConfirmClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setMsgKey(String msgKey) {
            this.msgKey = msgKey;
            return this;
        }

        public Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder setConfirmColorInt(@ColorInt int color) {
            this.confirmColor = color;
            return this;
        }

        public Builder setOnCancelClickListener(@StringRes int cancel, onCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(String cancel, onCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public CustomDialogFragment build() {
            CustomDialogFragment fragment = getFragment();
            fragment.setTitle(title);
            fragment.setMsg(msg);
            fragment.setMsgKey(msgKey);
            fragment.setTitleKey(titleKey);
            fragment.setTitleColor(titleColor);
            fragment.setTitleSize(titleSize);
            fragment.showCancelButton(showCancel);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.setArguments(bundle);
            return fragment;
        }

        protected CustomDialogFragment getFragment() {
            return new CustomDialogFragment();
        }

        public CustomDialogFragment show() {
            CustomDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
            return fragment;
        }
    }

    private void setMsgKey(String msgKey) {
        mMsgKey = msgKey;
    }

    private void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    private void setCancelText(String cancel) {
        this.cancel = cancel;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void showCancelButton(boolean showCancel) {
        this.showCancel = showCancel;
    }

    private void setConfirmText(String confirmText) {
        this.confirm = confirmText;
    }

    private void setConfirmColor(int confirmColor) {
        this.confirmColor = confirmColor;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTitleKey(String key) {
        this.titleKey = key;
    }
}
