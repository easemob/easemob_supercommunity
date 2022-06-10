package com.community.easeim.section.ground.frament;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.DisplayUtil;
import com.community.easeim.common.widget.CircleImageView;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.activity.ContactDetailActivity;
import com.community.easeim.section.ground.activity.SearchGroundActivity;
import com.community.easeim.section.ground.adapter.GroundAdapter;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.inter.OnItemClickListener;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.community.easeim.section.voice.VoiceTalkActivity;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceChannelManager;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.List;

public class GroundFragment extends BaseInitFragment implements View.OnClickListener, OnItemClickListener<GroundBean>, GroundAdapter.OnSearchClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRvGround;
    private GroundAdapter mAdapter;
    private SwipeRefreshLayout mSfl;
    private TextView mTvSearch;
    private MyBottomSheetDialog mDetailDialog;

    private CircleImageView mGroundCover, mAvatar;
    private TextView mGroundName, mGroundDescribe, mNickName;
    private LinearLayout mLlChat, mLlHall, mLlGroundAction;
    private LinearLayout mLlVoice;
    private LinearLayout mLlSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_ground;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mRvGround = findViewById(R.id.rv_ground);
        mSfl = findViewById(R.id.srl_refresh);
        mTvSearch = findViewById(R.id.tv_search);
        mLlSearch = findViewById(R.id.ll_search);

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
    protected void initData() {
        initRv();
    }

    @Override
    public void onResume() {
        super.onResume();
        getGroundList();
    }

    private void initRv() {
        mAdapter = new GroundAdapter(true);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnSearchClickListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvGround.setLayoutManager(layoutManager);
        mRvGround.setAdapter(mAdapter);
        mRvGround.setOnScrollListener(new RvScrollListener());
    }

    int scrollY = 0;

    @Override
    public void onSearchClick() {
        SearchGroundActivity.actionStart(mContext);
    }

    @Override
    public void onRefresh() {
        getGroundList();
    }

    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            scrollY += dy;
            if (scrollY >= DisplayUtil.dip2px(mContext, 90)) {
                mLlSearch.setVisibility(View.VISIBLE);
            } else {
                mLlSearch.setVisibility(View.GONE);
            }
        }
    }


    private void getGroundList() {
        GroundManager.getInstance().getGroundList(DemoConstant.groundIds, new EMValueCallBack<List<GroundBean>>() {
            @Override
            public void onSuccess(List<GroundBean> value) {
                mSfl.setRefreshing(false);
                mAdapter.setData(value);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    @Override
    protected void initListener() {
        mSfl.setOnRefreshListener(this);
        mTvSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_search) {
            SearchGroundActivity.actionStart(mContext);
        } else if (id == R.id.ll_admin) {
            ContactDetailActivity.actionStart(mContext, (String) mNickName.getTag());
        } else if (id == R.id.tv_hang_out) {
            if (!DemoConstant.groundStrollIds.contains(mGroundBean.getGroundId())) {
                DemoConstant.groundStrollIds.add(mGroundBean.getGroundId());
                EaseEvent event = EaseEvent.create(EaseConstant.SWITCH_HOME_STROLL);
                event.message = new Gson().toJson(mGroundBean);
                event.arg0 = 1;
                LiveDataBus.get().with(EaseConstant.SWITCH_HOME_STROLL).postValue(event);
                mDetailDialog.dismiss();
            } else {
                DemoConstant.groundStrollIds.add(mGroundBean.getGroundId());
                EaseEvent event = EaseEvent.create(EaseConstant.SWITCH_HOME_STROLL);
                event.message = new Gson().toJson(mGroundBean);
                event.arg0 = 0;
                LiveDataBus.get().with(EaseConstant.SWITCH_HOME_STROLL).postValue(event);
                mDetailDialog.dismiss();
            }
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
                if (value.size() > 0) {
                    voiceJump(value.get(0));
                }
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

//        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userIds, new EMValueCallBack<Map<String, EMUserInfo>>() {
//            @Override
//            public void onSuccess(Map<String, EMUserInfo> value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EMUserInfo userInfo = value.get(finalUserId);
//                        String nickname = userInfo.getNickname();
//                        mNickName.setText(nickname);
//                        String avatarUrl = userInfo.getAvatarUrl();
//                        CustomImageUtil.getInstance().setHeadView(mAvatar, avatarUrl);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//                Log.e("tagqi", "onError: errorMsg " + errorMsg);
//            }
//        });
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
}
