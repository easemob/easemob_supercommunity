package com.community.easeim.section.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.ImManager;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.me.activity.ChooseHeadImageActivity;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeStartFragment extends BaseInitFragment implements View.OnClickListener {

    private MyBottomSheetDialog mCreateStartDialog;
    private List<MyBottomSheetDialog> mDialogList = new ArrayList<>();
    private MyBottomSheetDialog mCreateGroundDialog;
    private TextView mTvCreate;
    private EditText mEtCreateName;
    private EditText mEtCreateDes;
    private ImageView mIvCover;
    private ImageView mIvTran;
    private CheckBox mCbInvite;
    private TextView mTvInvite;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_start;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initSheet();

    }

    private String mCoverPos;

    private void initSheet() {
        mCreateGroundDialog = new MyBottomSheetDialog(mContext);
        mCreateGroundDialog.setContentView(R.layout.sheet_home_ground_create);
        mIvCover = mCreateGroundDialog.findViewById(R.id.iv_cover);
        mIvTran = mCreateGroundDialog.findViewById(R.id.iv_tran);
        mIvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseHeadImageActivity.actionStartForResult(HomeStartFragment.this, 1, EaseConstant.TYPE_IMAGE_COVER);
            }
        });
        mDialogList.add(mCreateGroundDialog);

        mTvCreate = mCreateGroundDialog.findViewById(R.id.tv_create);
        mTvCreate.setClickable(false);
        mEtCreateName = mCreateGroundDialog.findViewById(R.id.et_name_create);
        mEtCreateDes = mCreateGroundDialog.findViewById(R.id.et_public_des);
        mCbInvite = mCreateGroundDialog.findViewById(R.id.cb_invite);
        mCbInvite.setChecked(true);
        mTvInvite = mCreateGroundDialog.findViewById(R.id.tv_invite);
        TextView tvDesNum = mCreateGroundDialog.findViewById(R.id.tv_des_num);
        TextView tvNameNum = mCreateGroundDialog.findViewById(R.id.tv_name_num);
        mEtCreateDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvDesNum.setText(mEtCreateDes.getText().toString().length() + "");
            }
        });
        mEtCreateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = mEtCreateName.getText().toString();
                tvNameNum.setText(name.length() + "");
                setCreateEnable(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mCoverPos));
            }
        });
    }

    private void setCreateEnable(boolean b) {
        if (b) {
            mTvCreate.setClickable(true);
            mTvCreate.setTextColor(Color.parseColor("#27ae60"));
            mTvCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCreatePublic) {
                        onCreatePublicGround();
                    } else {
                        onCreatePrivateGround();
                    }
                }
            });
        } else {
            mTvCreate.setClickable(false);
            mTvCreate.setTextColor(Color.parseColor("#929497"));
            mTvCreate.setOnClickListener(null);
        }
    }

    private void onCreatePrivateGround() {
        ToastUtils.showToast("创建中");
        String name = mEtCreateName.getText().toString();
        String des = mEtCreateDes.getText().toString();

        mEtCreateName.setText("");
        mEtCreateDes.setText("");
        ImManager.getInstance().createPrivateGround(name, des, mCbInvite.isChecked(), mCoverPos, new ImManager.CallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(List<EMGroup> groups) {
                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUND_CREATE));
                ToastUtils.showToast("社区创建成功");
                mCreateGroundDialog.dismiss();
            }

            @Override
            public void onFailed(String err) {
                ToastUtils.showToast("社区创建失败:" + err);
                mCreateGroundDialog.dismiss();
            }
        });
    }

    @Override
    protected void initListener() {
        findViewById(R.id.ll_create).setOnClickListener(this);
        findViewById(R.id.ll_find).setOnClickListener(this);


        mCreateStartDialog.findViewById(R.id.tv_public).setOnClickListener(this);
        mCreateStartDialog.findViewById(R.id.tv_private).setOnClickListener(this);
    }

    private void onCreateGround() {
        closeAllSheet();
        mCreateStartDialog.show();
    }

    private void onFindGround() {
        closeAllSheet();
        EaseEvent event = EaseEvent.create(EaseConstant.SWITCH_GROUND);
        LiveDataBus.get().with(EaseConstant.SWITCH_GROUND).postValue(event);
    }

    boolean isCreatePublic = true;

    private void onPublic() {
        closeAllSheet();
        isCreatePublic = true;
        mCreateGroundDialog.show();
        mTvInvite.setText("允许成员邀请");
    }

    private void onPrivate() {
        closeAllSheet();
        isCreatePublic = false;
        mCreateGroundDialog.show();
        mTvInvite.setText("允许成员邀请");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_create:
                onCreateGround();
                break;
            case R.id.ll_find:
                onFindGround();
                break;
            case R.id.tv_private:
                onPrivate();
                break;
            case R.id.tv_public:
                onPublic();
                break;
            default:
                break;
        }
    }

    private void onCreatePublicGround() {
        ToastUtils.showToast("创建中");
        String name = mEtCreateName.getText().toString();
        String des = mEtCreateDes.getText().toString();

        mEtCreateName.setText("");
        mEtCreateDes.setText("");
        ImManager.getInstance().createPublicGround(name, des, mCbInvite.isChecked(), mCoverPos, new ImManager.CallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(List<EMGroup> groups) {
                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUND_CREATE));
                ToastUtils.showToast("社区创建成功");
                mCreateGroundDialog.dismiss();
            }

            @Override
            public void onFailed(String err) {
                ToastUtils.showToast("社区创建失败:" + err);
                mCreateGroundDialog.dismiss();
            }
        });
    }

    private void closeAllSheet() {
        for (MyBottomSheetDialog dialog : mDialogList) {
            if (dialog.isShowing()) dialog.dismiss();
        }
    }

    public void setCreateSheet(MyBottomSheetDialog createDialog) {
        mCreateStartDialog = createDialog;
        mDialogList.add(mCreateStartDialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                mCoverPos = data.getStringExtra("headImage");
                if (!TextUtils.isEmpty(mCoverPos)) {
                    mIvTran.setImageResource(R.drawable.icon_change_cover);
                    setCreateEnable(!TextUtils.isEmpty(mEtCreateName.getText().toString().trim()));
                    CustomImageUtil.getInstance().setCoverView(mIvCover, mCoverPos);
                } else {
                    mIvTran.setImageResource(R.drawable.em_close);
                }
            }
        }
    }
}
