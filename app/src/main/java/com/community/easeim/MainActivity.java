package com.community.easeim;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.enums.SearchType;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.permission.PermissionsManager;
import com.community.easeim.common.permission.PermissionsResultAction;
import com.community.easeim.common.utils.CommonUtil;
import com.community.easeim.common.utils.PreferenceManager;
import com.community.easeim.common.utils.PushUtils;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.ui.base.EaseBaseFragment;
import com.community.easeim.section.MainViewModel;
import com.community.easeim.section.av.MultipleVideoActivity;
import com.community.easeim.section.av.VideoCallActivity;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.ChatPresenter;
import com.community.easeim.section.contact.activity.AddContactActivity;
import com.community.easeim.section.contact.activity.GroupContactManageActivity;
import com.community.easeim.section.contact.fragment.ContactListFragment;
import com.community.easeim.section.conversation.ConversationListFragment;
import com.community.easeim.section.friend.fragment.ContactStartFragment;
import com.community.easeim.section.ground.frament.GroundFragment;
import com.community.easeim.section.group.activity.GroupPrePickActivity;
import com.community.easeim.section.home.fragment.HomeFragment;
import com.community.easeim.section.me.AboutMeFragment;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.chat.EMUserInfo.EMUserInfoType;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.util.EMLog;

import java.lang.reflect.Method;
import java.util.Map;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private EaseBaseFragment mConversationListFragment, mContactStartFragment, mDiscoverFragment, mAboutMeFragment;
    private EaseBaseFragment mCurrentFragment;
    private TextView mTvMainHome, mTvMainAddFriend, mTvMainGround, mTvMainMsg, mTvMainAboutMe;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_add_friends, R.layout.demo_badge_ground,
            R.layout.demo_badge_msg, R.layout.demo_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home, R.id.tv_main_add_friend,
            R.id.tv_main_ground, R.id.tv_main_msg, R.id.tv_main_about_me_msg};
    private MainViewModel viewModel;
    private boolean showMenu = true;//是否显示菜单项
    private HomeFragment mHomeFragment;

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    private void adjustNavigationIcoSize(BottomNavigationView navigation) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, displayMetrics);
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            iconView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_main;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof ContactListFragment) {
                menu.findItem(R.id.action_group).setVisible(false);
                menu.findItem(R.id.action_friend).setVisible(false);
                menu.findItem(R.id.action_search_friend).setVisible(true);
                menu.findItem(R.id.action_search_group).setVisible(true);
            } else {
                menu.findItem(R.id.action_group).setVisible(true);
                menu.findItem(R.id.action_friend).setVisible(true);
                menu.findItem(R.id.action_search_friend).setVisible(false);
                menu.findItem(R.id.action_search_group).setVisible(false);
            }
        }
        return showMenu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_video:
                break;
            case R.id.action_group:
                GroupPrePickActivity.actionStart(mContext);
                break;
            case R.id.action_friend:
            case R.id.action_search_friend:
                AddContactActivity.startAction(mContext, SearchType.CHAT);
                break;
            case R.id.action_search_group:
                GroupContactManageActivity.actionStart(mContext, true);
                break;
            case R.id.action_scan:
                showToast("扫一扫");
                break;
        }
        return true;
    }

    /**
     * 显示menu的icon，通过反射，设置menu的icon显示
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
        adjustNavigationIcoSize(navView);
        navView.setItemIconTintList(null);
        // 可以动态显示隐藏相应tab
        //navView.getMenu().findItem(R.id.em_main_nav_me).setVisible(false);
        switchToHome();
        checkIfShowSavedFragment(savedInstanceState);
        addTabBadge();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initViewModel();
        requestPermissions();
        checkUnreadMsg();
        ChatPresenter.getInstance().init();
        // 获取华为 HMS 推送 token
        HMSPushHelper.getInstance().getHMSToken(this);

        //判断是否为来电推送
        if (PushUtils.isRtcCall) {
            if (EaseCallType.getfrom(PushUtils.type) != EaseCallType.CONFERENCE_CALL) {
                Intent intent = new Intent(getApplicationContext(), VideoCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getApplication().getApplicationContext(), MultipleVideoActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            PushUtils.isRtcCall = false;
        }

        if (DemoHelper.getInstance().getModel().isUseFCM() && GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            // 启用 FCM 自动初始化
            if (!FirebaseMessaging.getInstance().isAutoInitEnabled()) {
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
            }
            // 获取FCM 推送 token 并上传
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        EMLog.d("FCM", "Fetching FCM registration token failed:" + task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    EMLog.d("FCM", token);
                    EMClient.getInstance().sendFCMTokenToServer(token);
                }
            });
        }

        //获取自己用户信息
        fetchSelfInfo();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);
        viewModel.getSwitchObservable().observe(this, response -> {
            if (response == null || response == 0) {
                return;
            }
//            if (response == R.string.em_main_title_me) {
//                mTitleBar.setVisibility(View.GONE);
//            } else {
//                mTitleBar.setVisibility(View.VISIBLE);
//                mTitleBar.setTitle(getResources().getString(response));
//            }
        });

        viewModel.homeUnReadObservable().observe(this, readCount -> {
            CommonUtil.getInstance().setShowNum(mTvMainMsg,readCount);
        });

        //加载联系人
//        ContactsViewModel contactsViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
//        contactsViewModel.loadContactList(true);

        viewModel.messageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);

        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(this, this::checkUnReadMsg);

        LiveDataBus.get().with(EaseConstant.SWITCH_GROUND, EaseEvent.class).observe(this, this::switch2Ground);
        LiveDataBus.get().with(EaseConstant.SWITCH_HOME, EaseEvent.class).observe(this, this::switch2Home);
        LiveDataBus.get().with(EaseConstant.SWITCH_HOME_STROLL, EaseEvent.class).observe(this, this::switch2Home);
    }

    private void checkUnReadMsg(EaseEvent event) {
        if (event == null) {
            return;
        }
        viewModel.checkUnreadMsg();
    }

    /**
     * 添加BottomNavigationView中每个item右上角的红点
     */
    private void addTabBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = menuView.getChildCount();
        Log.e("TAG", "bottom child count = " + childCount);
        BottomNavigationItemView itemTab;
        for (int i = 0; i < childCount; i++) {
            itemTab = (BottomNavigationItemView) menuView.getChildAt(i);
            itemTab.setOnLongClickListener(view -> true);
            View badge = LayoutInflater.from(mContext).inflate(badgeIds[i], menuView, false);
            switch (i) {
                case 0:
                    mTvMainHome = badge.findViewById(msgIds[0]);
                    break;
                case 1:
                    mTvMainAddFriend = badge.findViewById(msgIds[1]);
                    break;
                case 2:
                    mTvMainGround = badge.findViewById(msgIds[2]);
                    break;
                case 3:
                    mTvMainMsg = badge.findViewById(msgIds[3]);
                    break;
                case 4:
                    mTvMainAboutMe = badge.findViewById(msgIds[4]);
                    break;
            }
            itemTab.addView(badge);
        }
    }

    /**
     * 用于展示是否已经存在的Fragment
     * @param savedInstanceState
     */
    private void checkIfShowSavedFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("tag");
            if (!TextUtils.isEmpty(tag)) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof EaseBaseFragment) {
                    replace((EaseBaseFragment) fragment, tag);
                }
            }
        }
    }

    /**
     * 申请权限
     */
    // TODO: 2019/12/19 0019 有必要修改一下
    private void requestPermissions() {
        PermissionsManager.getInstance()
                .requestAllManifestPermissionsIfNecessary(mContext, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }

    private void switchToHome() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
        }
        replace(mHomeFragment, "conversation");
    }

    private void switchToMsg() {
        if (mConversationListFragment == null) {
            mConversationListFragment = new ConversationListFragment();
        }
        replace(mConversationListFragment, "conversation");
    }

    private void switchToFriends() {
        if (mContactStartFragment == null) {
            mContactStartFragment = new ContactStartFragment();
        }
        replace(mContactStartFragment, "contact");
    }

    private void switch2Home(EaseEvent event) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        menuView.getChildAt(0).performClick();
    }

    private void switch2Ground(EaseEvent event) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        menuView.getChildAt(2).performClick();
    }

    private void switchToGround() {
        if (mDiscoverFragment == null) {
            mDiscoverFragment = new GroundFragment();
        }
        replace(mDiscoverFragment, "discover");
    }

    private void switchToAboutMe() {
        if (mAboutMeFragment == null) {
            mAboutMeFragment = new AboutMeFragment();
        }
        replace(mAboutMeFragment, "me");
    }

    private void replace(EaseBaseFragment fragment, String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }

    private void fetchSelfInfo() {
        String[] userId = new String[1];
        userId[0] = EMClient.getInstance().getCurrentUser();
        EMUserInfoType[] userInfoTypes = new EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userId, userInfoTypes, new EMValueCallBack<Map<String, EMUserInfo>>() {
            @Override
            public void onSuccess(Map<String, EMUserInfo> userInfos) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMUserInfo userInfo = userInfos.get(EMClient.getInstance().getCurrentUser());
                        //昵称
                        if (userInfo != null && userInfo.getNickName() != null &&
                                userInfo.getNickName().length() > 0) {
                            EaseEvent event = EaseEvent.create(DemoConstant.NICK_NAME_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getNickName();
                            LiveDataBus.get().with(DemoConstant.NICK_NAME_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserNick(userInfo.getNickName());
                        }
                        //头像
                        if (userInfo != null && (TextUtils.isEmpty(userInfo.getAvatarUrl()) || userInfo.getAvatarUrl().length() > 2 || TextUtils.equals("-1", userInfo.getAvatarUrl()))) {
                            setRandomHead();
                            return;
                        }

                        if (userInfo != null && userInfo.getAvatarUrl() != null) {
                            EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                            event.message = userInfo.getAvatarUrl();
                            LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
                            PreferenceManager.getInstance().setCurrentUserAvatar(userInfo.getAvatarUrl());
                        }
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e("MainActivity", "fetchUserInfoByIds error:" + error + " errorMsg:" + errorMsg);
            }
        });
    }

    private void setRandomHead() {
        String random = CustomImageUtil.getInstance().getNum(0, 50);
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfoType.AVATAR_URL, random, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                LeanCloudManager.getInstance().updateUserAvatar(EMClient.getInstance().getCurrentUser(), random);
                PreferenceManager.getInstance().setCurrentUserAvatar(random);
                DemoHelper.getInstance().getUserProfileManager().updateUserAvatar(random);
                EaseEvent event = EaseEvent.create(DemoConstant.AVATAR_CHANGE, EaseEvent.TYPE.CONTACT);
                //发送联系人更新事件
                event.message = random;
                LiveDataBus.get().with(DemoConstant.AVATAR_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        showMenu = true;
        boolean showNavigation = false;
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_home:
                switchToHome();
//                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_home));
                showNavigation = true;
                break;
            case R.id.em_main_nav_add_friend:
                switchToFriends();
                showNavigation = true;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_ground:
                switchToGround();
                showNavigation = true;
                break;
            case R.id.em_main_nav_msg:
                switchToMsg();
                showNavigation = true;
                showMenu = false;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_me:
                switchToAboutMe();
                showMenu = false;
                showNavigation = true;
                break;
        }
        invalidateOptionsMenu();
        return showNavigation;
    }

    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().showNotificationPermissionDialog();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentFragment != null) {
            outState.putString("tag", mCurrentFragment.getTag());
        }
    }
}
