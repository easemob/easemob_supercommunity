package com.community.easeim.section.home.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.ItemTouchHelperCallback;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.base.BaseInitFragment;
import com.community.easeim.section.contact.viewmodels.GroupContactViewModel;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.ground.frament.GroundDetailFragment;
import com.community.easeim.section.home.adapter.HomeMenuAdapter;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseInitFragment implements HomeMenuAdapter.OnMenuClickListener, ItemTouchHelperCallback.IMoveAndSwipeCallback {

    private FragmentContainerView mFcv;
    private RecyclerView mRvMenu;
    private HomeMenuAdapter mAdapter;
    private GroundDetailFragment mGroundDetailFragment;
    private HomeStartFragment mHomeStartFragment;
    private MyBottomSheetDialog mCreateDialog;
    private int mCheckPos;
    private GroupContactViewModel mGroupContactViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mRvMenu = findViewById(R.id.rv_menu);
        mFcv = findViewById(R.id.fcv_fragment);

        initSheet();

        mHomeStartFragment = new HomeStartFragment();
        mHomeStartFragment.setCreateSheet(mCreateDialog);
        replace(mHomeStartFragment, "start");
    }

    protected void initSheet() {
        mCreateDialog = new MyBottomSheetDialog(mContext);
        mCreateDialog.setContentView(R.layout.sheet_home_ground_create_start);
    }

    protected void showCreateDialog() {
        if (!mCreateDialog.isShowing()) mCreateDialog.show();
    }

    @Override
    protected void initData() {

        mAdapter = new HomeMenuAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRvMenu.setLayoutManager(layoutManager);
        mRvMenu.setAdapter(mAdapter);

        //给RecyclerView设置ItemTouchHelper
        ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback();
        itemTouchHelperCallback.setiMoveAndSwipeCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRvMenu);

        mAdapter.setOnMenuClickListener(this);

        mGroundDetailFragment = new GroundDetailFragment();
        getGroundData();
        observer();
    }

    @Override
    public void onStartClick() {
        replace(mHomeStartFragment, "start");
    }

    @Override
    public void onItemClick(int pos, GroundBean bean) {
        mCheckPos = pos;
        GroundManager.getInstance().getGroundGroupBeanList(bean.getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
            @Override
            public void onSuccess(List<GroundGroupBean> groundGroupBeanList) {
                GroundGroupBean groundGroupBean = groundGroupBeanList.get(0);
                EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                mGroundDetailFragment.setFragmentData(bean, group, groundGroupBeanList);
                replace(mGroundDetailFragment, "detail");
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.e("HomeFragment", "onError: " + errorMsg);
            }
        });
    }

    @Override
    public void onAddClick() {
        showCreateDialog();
    }

    private List<GroundBean> mGroundBeans = new ArrayList<>();

    private synchronized void getGroundData() {
        DemoConstant.groundStrollIds.clear();
        mGroundBeans.clear();
        GroundManager.getInstance().getUserGroundList(EMClient.getInstance().getCurrentUser(), new EMValueCallBack<List<GroundBean>>() {
            @Override
            public void onSuccess(List<GroundBean> groundBeans) {
                mGroundBeans = groundBeans;
                if (groundBeans.size() == 0 || groundBeans.isEmpty()) {
                    mCheckPos = 0;
                    mAdapter.setCheckedPos(mCheckPos);
                    replace(mHomeStartFragment, "start");
                    return;
                }
                if (mCheckPos == 0) mCheckPos++;
                if (mCheckPos == -1) mCheckPos = groundBeans.size();
                mAdapter.setData(groundBeans);
                mRvMenu.scrollToPosition(mCheckPos);
                if (groundBeans.size() == 0 ) {
                    return;
                }
                Log.e("TAG", "onSuccess: groundBeans.size()  : "+groundBeans.size()  );
                for (GroundBean userGroundBean : groundBeans) {
                    if (!TextUtils.isEmpty(userGroundBean.getGroundId())) {
                        DemoConstant.groundIds.add(userGroundBean.getGroundId());
                    }
                }

                GroundManager.getInstance().getGroundGroupBeanList(groundBeans.get(mCheckPos - 1).getGroundId(), new EMValueCallBack<List<GroundGroupBean>>() {
                    @Override
                    public void onSuccess(List<GroundGroupBean> groundGroupBeanList) {
                        if (groundBeans.size() > 0 && groundGroupBeanList.size() > 0) {
                            mAdapter.setCheckedPos(mCheckPos);

                            GroundGroupBean groundGroupBean = groundGroupBeanList.get(0);
                            EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                            if (mCheckPos != 0) {
                                mGroundDetailFragment.setFragmentData(groundBeans.get(mCheckPos - 1), group, groundGroupBeanList);
                            }
                            replace(mGroundDetailFragment, "detail");
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        Log.e("HomeFragment", "onError: " + errorMsg);
                    }
                });

                getAllUnreadCount(null);

            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

    }

    public static Map<String, Integer> mUnreadMap = new HashMap<>();

    private void getAllUnreadCount(EaseEvent event) {
        for (GroundBean bean : mGroundBeans) {
            String groundId = bean.getGroundId();
            GroundManager.getInstance().getGroundGroupBeanList(groundId, new EMValueCallBack<List<GroundGroupBean>>() {
                @Override
                public void onSuccess(List<GroundGroupBean> groundGroupBeanList) {
                    int count = 0;
                    for (GroundGroupBean groundGroupBean : groundGroupBeanList) {
                        String groupId = groundGroupBean.getGroupId();
                        count += EMClient.getInstance().chatManager().getConversation(groupId, EMConversation.EMConversationType.GroupChat,true).getUnreadMsgCount();
                    }
                    mUnreadMap.put(bean.getGroundId(), count);
                    int finalCount = count;
                    EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setUnreadMap(bean.getGroundId(), finalCount, mGroundBeans.indexOf(bean) + 1);
//                            mAdapter.notifyItemChanged(mGroundBeans.indexOf(bean)+1);
                        }
                    });
                }

                @Override
                public void onError(int error, String errorMsg) {
                    Log.e("getAllUnreadCount", errorMsg);
                }
            });
        }
    }

    private void observer() {
        mGroupContactViewModel = new ViewModelProvider(this).get(GroupContactViewModel.class);
        mGroupContactViewModel.getMessageObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::groupChange);

        mGroupContactViewModel.getMessageObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (TextUtils.equals(event.event, DemoConstant.MESSAGE_GROUP_AUTO_ACCEPT)) {
                loadAllGround();
            }
        });

        loadAllGround();

        LiveDataBus.get().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::groupChange);
        LiveDataBus.get().with(EaseConstant.SWITCH_HOME, EaseEvent.class).observe(this, this::setDetail);
        LiveDataBus.get().with(EaseConstant.SWITCH_HOME_STROLL, EaseEvent.class).observe(this, this::setStrollDetail);
        LiveDataBus.get().with(EaseConstant.GROUND_CHANGE, EaseEvent.class).observe(this, this::groundUpdate);
        LiveDataBus.get().with(EaseConstant.CONVERSATION_READ, EaseEvent.class).observe(this, this::getAllUnreadCount);
        LiveDataBus.get().with(EaseConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::getAllUnreadCount);
    }

    private void groundUpdate(EaseEvent event) {
        if (event == null || TextUtils.isEmpty(event.message)) {
            getGroundData();
            return;
        }
//        UserGroundBean  mUserGroundBean = new Gson().fromJson(event.message,UserGroundBean.class);
//
//        if (mGroundBeans != null && !mGroundBeans.isEmpty()) {
//            for (int i = 0; i < mGroundBeans.size(); i++) {
//                UserGroundBean userGroundBean = mGroundBeans.get(i);
//                if (TextUtils.equals(userGroundBean.getGroundId(),mUserGroundBean.getGroundId())){
//                    onItemClick(i+1, mUserGroundBean);
//                    mAdapter.setCheckedPos(i+1);
//                }
//            }
//        }
    }

    private void groupChange(EaseEvent event) {
        if (event == null) {
            return;
        }
        if (event.isGroupChange() || event.isGroupLeave() || event.isGroundCreate() || event.isChannelCreate()) {
            loadAllGround();
            if (event.isGroundCreate()) {
                mCheckPos = -1;
            }
            getGroundData();
        }
    }

    private void setDetail(EaseEvent event) {
        if (mGroundBeans != null && !mGroundBeans.isEmpty()) {
            String groundId = event.message;
            for (int i = 0; i < mGroundBeans.size(); i++) {
                GroundBean userGroundBean = mGroundBeans.get(i);
                if (TextUtils.equals(userGroundBean.getGroundId(), groundId)) {
                    onItemClick(i + 1, userGroundBean);
                    mAdapter.setCheckedPos(i + 1);
                }
            }
        }
    }

    private void setStrollDetail(EaseEvent event) {
        GroundBean mUserGroundBean = new Gson().fromJson(event.message, GroundBean.class);
        if (event.arg0 == 1) {
            mAdapter.addData(mUserGroundBean);
        }
        onItemClick(mGroundBeans.size(), mUserGroundBean);
        mAdapter.setCheckedPos(mGroundBeans.size());
    }

    private void loadAllGround() {
        mGroupContactViewModel.loadGroupListFromServer(0, 1000);
    }

    @Override
    public void onMove(int prePosition, int postPosition) {
        if (prePosition == 0 || prePosition == mGroundBeans.size() + 1) {
            return;
        }
        if (postPosition == 0) {
            postPosition = 1;
        }
        if (postPosition >= mGroundBeans.size()) {
            postPosition = mGroundBeans.size();
        }

        Log.e("tagqi", "onMove: " + prePosition + "-->" + postPosition);
        Collections.swap(mGroundBeans, prePosition - 1, postPosition - 1);
        if (mAdapter != null) {
            mAdapter.notifyItemMoved(prePosition, postPosition);
        }
    }

    @Override
    public void onSwiped(int position) {

    }
}
