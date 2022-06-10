package com.community.easeim.section.ground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.ground.adapter.GroundAdapter;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.inter.OnItemClickListener;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.voice.VoiceTalkActivity;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceChannelManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.List;

public class SearchGroundActivity extends BaseInitActivity implements TextWatcher, TextView.OnEditorActionListener, View.OnClickListener, OnItemClickListener<GroundBean> {

    private RecyclerView mRvResult;
    private GroundAdapter mAdapter;
    private EditText mEtSearch;
    private ImageView mIvClear;
    private TextView mTvCancel;
    private MyBottomSheetDialog mDetailDialog;
    private CircleImageView mGroundCover;
    private TextView mGroundName;
    private TextView mGroundDescribe;
    private ImageView mAvatar;
    private TextView mNickName;
    private LinearLayout mLlHall;
    private LinearLayout mLlChat;
    private LinearLayout mLlVoice, mLlGroundAction;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchGroundActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_ground;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mRvResult = findViewById(R.id.rv_result);
        mEtSearch = findViewById(R.id.et_search);
        mEtSearch.requestFocus();
        mIvClear = findViewById(R.id.iv_clear);
        mTvCancel = findViewById(R.id.tv_cancel);

        initSheet();
    }

    private void initSheet() {
        mDetailDialog = new MyBottomSheetDialog(mContext);
        mDetailDialog.setContentView(R.layout.sheet_ground_item);
        mLlGroundAction = mDetailDialog.findViewById(R.id.ll_ground_action);
        mDetailDialog.findViewById(R.id.tv_join_ground).setOnClickListener(this);
        mDetailDialog.findViewById(R.id.tv_hang_out).setOnClickListener(this);
        mDetailDialog.findViewById(R.id.ll_admin).setOnClickListener(this);

        mGroundCover = mDetailDialog.findViewById(R.id.civ_head);
        mGroundName = mDetailDialog.findViewById(R.id.tv_name);
        mGroundDescribe = mDetailDialog.findViewById(R.id.tv_describe);
        mAvatar = mDetailDialog.findViewById(R.id.iv_avatar);
        mNickName = mDetailDialog.findViewById(R.id.tv_nick_name);

        mLlHall = mDetailDialog.findViewById(R.id.ll_ground_hall);
        mLlChat = mDetailDialog.findViewById(R.id.ll_casual_chat);
        mLlVoice = mDetailDialog.findViewById(R.id.ll_voice);
    }

    @Override
    protected void initListener() {
        mEtSearch.addTextChangedListener(this);
        mEtSearch.setOnEditorActionListener(this);
    }

    @Override
    protected void initData() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvResult.setLayoutManager(layoutManager);
        mAdapter = new GroundAdapter(false);
        mAdapter.setOnItemClickListener(this);
        mRvResult.setAdapter(mAdapter);
    }

    private void getGroundListByKey(String key) {
        GroundManager.getInstance().getGroundListByKey(key, new EMValueCallBack<List<GroundBean>>() {
            @Override
            public void onSuccess(List<GroundBean> value) {
                mAdapter.setData(value, key);
                hideKeyboard();
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private GroundBean mGroundBean;

    private void setSheetChannel(GroundBean groundBean) {
        mGroundBean = groundBean;
        if (DemoConstant.groundIds.contains(groundBean.getGroundId())) {
            mLlGroundAction.setVisibility(View.GONE);
        } else {
            mLlGroundAction.setVisibility(View.VISIBLE);
        }
        CustomImageUtil.getInstance().setCoverView(mGroundCover, groundBean.getGroundCover());
        mGroundName.setText(groundBean.getGroundName());
        mGroundDescribe.setText(groundBean.getGroundDes());
//        mChannelNum.setText(CommonUtil.getInstance().getShowNum(groundBean.getNum()));
        getGroundGroups(groundBean, false);
        VoiceChannelManager.getInstance().getVoiceChannelListByGroundId(groundBean.getGroundId(), new EMValueCallBack<List<VoiceChannel>>() {
            @Override
            public void onSuccess(List<VoiceChannel> value) {
                voiceJump(value.get(0));
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });

        String finalUserId = groundBean.getGroundOwner();
        mNickName.setTag(finalUserId);
        String[] userIds = new String[]{finalUserId};

        LeanCloudManager.getInstance().getUserInfo(groundBean.getGroundOwner(), new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser value) {
                mNickName.setText(value.getNickname());
                CustomImageUtil.getInstance().setHeadView(mAvatar, value.getAvatar());
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void voiceJump(VoiceChannel channel) {
        mLlVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoiceTalkActivity.actionStart(mContext, channel.getChannelId(), channel.getGroundName());
            }
        });
    }

    /**
     * 获取社区的群列表
     * @param groundBean
     */
    private void getGroundGroups(GroundBean groundBean, boolean joinGround) {
        GroundManager.getInstance().getGroundGroupBeanList(groundBean.getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
            @Override
            public void onSuccess(List<GroundGroupBean> value) {
                if (joinGround) {
                    joinGround(value);
                } else {
                    groundHallClick(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void groundHallClick(List<GroundGroupBean> groupBeans) {
        //社区大厅
        mLlHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DemoConstant.groundIds.contains(mGroundBean.getGroundId())) {
                    GroundGroupBean groundGroupBean = groupBeans.get(0);
                    joinGroundGroup(groundGroupBean);
                } else {
                    new CustomDialog
                            .Builder(mContext)
                            .setTitle("提示")
                            .setMsg("加入社区大厅等同于加入该社区！")
                            .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                @Override
                                public void onConfirmClick(View view) {
                                    GroundGroupBean groundGroupBean = groupBeans.get(0);
                                    GroundManager.getInstance().saveUserAndGroundInfo(EMClient.getInstance().getCurrentUser(), mGroundBean.getGroundId());
                                    joinGroundGroup(groundGroupBean);
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void joinGround(List<GroundGroupBean> groupBeans) {
        if (DemoConstant.groundIds.contains(mGroundBean.getGroundId())){
            GroundGroupBean groundGroupBean = groupBeans.get(0);
            joinGroundGroup(groundGroupBean);
        } else {
            new CustomDialog
                    .Builder(mContext)
                    .setTitle("提示")
                    .setMsg("确认加入该社区?")
                    .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(View view) {
                            GroundGroupBean groundGroupBean = groupBeans.get(0);
                            GroundManager.getInstance().saveUserAndGroundInfo(EMClient.getInstance().getCurrentUser(), mGroundBean.getGroundId());
                            joinGroundGroup(groundGroupBean);
                        }
                    })
                    .show();
        }
    }

    private void joinGroundGroup(GroundGroupBean groundGroupBean) {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
        if (group == null) {
            EMClient.getInstance().groupManager().asyncJoinGroup(groundGroupBean.getGroupId(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    com.hjq.toast.ToastUtils.show("加入成功!");
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                    ChatActivity.actionStart(mContext, groundGroupBean.getGroupId(), EaseConstant.CHATTYPE_GROUP);
                    mDetailDialog.dismiss();
                }

                @Override
                public void onError(int code, String error) {
                    Log.e("tagqi", "onError: error " + error);
                }

                @Override
                public void onProgress(int progress, String status) {
                }
            });
        } else {
            ChatActivity.actionStart(mContext, groundGroupBean.getGroupId(), EaseConstant.CHATTYPE_GROUP);
            mDetailDialog.dismiss();
        }
    }


    public void onClear(View v) {
        mEtSearch.setText("");
        mAdapter.clearData();
    }

    public void onCancel(View v) {
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String key = mEtSearch.getText().toString();
        if (key.length() > 0) {
            mIvClear.setVisibility(View.VISIBLE);
        } else {
            mIvClear.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            doSearch();
            return true;
        }
        return false;
    }

    private void doSearch() {
        String key = mEtSearch.getText().toString();
        if (TextUtils.isEmpty(key.trim())) {
            ToastUtils.showToast("关键字不能为空");
            return;
        }
        getGroundListByKey(key);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_search) {
            SearchGroundActivity.actionStart(mContext);
        } else if (id == R.id.ll_admin) {
            ContactDetailActivity.actionStart(mContext, (String) mNickName.getTag());
        } else if (id == R.id.tv_hang_out) {
            //FIXME 随意逛逛
            ToastUtils.showCenterToast("提示", getString(R.string.ease_contact_add_note_developing), 0, Toast.LENGTH_LONG);
        } else if (id == R.id.tv_join_ground) {
            //加入社区
            if (mGroundBean != null) {
                getGroundGroups(mGroundBean, true);
            }
        }
    }

    @Override
    public void onItemClick(GroundBean groundBean) {
        if (DemoConstant.groundIds.contains(groundBean.getGroundId())) {
            new CustomDialog
                    .Builder(mContext)
                    .setTitle("提示")
                    .setMsg("您已在“" + groundBean.getGroundName() + "”社区，是否跳转？")
                    .setMsgKey(groundBean.getGroundName())
                    .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(View view) {
                            AppManager.getInstance().finishActivity(SearchGroundActivity.this);
                            EaseEvent event = EaseEvent.create(EaseConstant.SWITCH_HOME);
                            event.message = groundBean.getGroundId();
                            LiveDataBus.get().with(EaseConstant.SWITCH_HOME).postValue(event);
                        }
                    })
                    .show();
            return;
        }
        mDetailDialog.show();
        setSheetChannel(groundBean);
    }
}
