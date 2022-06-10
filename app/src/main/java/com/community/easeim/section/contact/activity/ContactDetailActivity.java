package com.community.easeim.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.DemoDbHelper;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.widget.EaseImageView;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.av.VideoCallActivity;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.contact.viewmodels.AddContactViewModel;
import com.community.easeim.section.contact.viewmodels.ContactBlackViewModel;
import com.community.easeim.section.contact.viewmodels.ContactDetailViewModel;
import com.community.easeim.section.dialog.DemoDialogFragment;
import com.community.easeim.section.dialog.SimpleDialogFragment;
import com.community.easeim.section.me.headImage.CustomImageUtil;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;

import java.util.List;

public class ContactDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener {

    private EaseImageView mAvatarUser;
    private TextView mTvName;
    private TextView mTvNote;
    private TextView mBtnChat;
    private TextView mBtnVoice;
    private TextView mBtnVideo;
    private TextView mBtnAddContact;
    private TextView mBtnRemoveBlack;
    private Group mGroupFriend;

    private EaseUser mUser;
    private boolean mIsFriend;
    private boolean mIsBlack;
    private ContactDetailViewModel viewModel;
    private AddContactViewModel addContactViewModel;
    private ContactBlackViewModel blackViewModel;
    private LiveDataBus contactChangeLiveData;

    public static void actionStart(Context context, EaseUser user) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("user", (EaseUser)user);
        if(user.getContact() == 0){
            intent.putExtra("isFriend", true);
        }else{
            intent.putExtra("isFriend", false);
        }

        context.startActivity(intent);
    }

    public static void actionStart(Context context, EaseUser user, boolean isFriend) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isFriend", isFriend);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_friends_contact_detail;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mIsFriend && !mIsBlack;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_friends_contact_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_delete:
                showDeleteDialog(mUser);
                break;
            case R.id.action_add_black :
                viewModel.addUserToBlackList(mUser.getUsername(), false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mUser = (EaseUser)getIntent().getSerializableExtra("user");
        mIsFriend = getIntent().getBooleanExtra("isFriend", true);
        if(!mIsFriend) {
            List<String> users = null;
            if(DemoDbHelper.getInstance(mContext).getUserDao() != null) {
                users = DemoDbHelper.getInstance(mContext).getUserDao().loadContactUsers();
            }
            mIsFriend = users != null && users.contains(mUser.getUsername());
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mAvatarUser = findViewById(R.id.avatar_user);
        mTvName = findViewById(R.id.tv_name);
        mTvNote = findViewById(R.id.tv_note);
        mBtnChat = findViewById(R.id.btn_chat);
        mBtnVoice = findViewById(R.id.btn_voice);
        mBtnVideo = findViewById(R.id.btn_video);
        mBtnAddContact = findViewById(R.id.btn_add_contact);
        mGroupFriend = findViewById(R.id.group_friend);
        mBtnRemoveBlack = findViewById(R.id.btn_remove_black);

        if(mIsFriend) {
            mGroupFriend.setVisibility(View.VISIBLE);
            mBtnAddContact.setVisibility(View.GONE);
            EaseUser user = DemoHelper.getInstance().getModel().getContactList().get(mUser.getUsername());
            if(user != null && user.getContact() == 1) {
                mIsBlack = true;
                //如果在黑名单中
                mGroupFriend.setVisibility(View.GONE);
                mBtnRemoveBlack.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }
        }else {
            mGroupFriend.setVisibility(View.GONE);
            mBtnAddContact.setVisibility(View.VISIBLE);
        }
        mBtnVideo.setVisibility(View.GONE);
        mBtnVoice.setVisibility(View.GONE);
        updateLayout();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvNote.setOnClickListener(this);
        mBtnChat.setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mBtnAddContact.setOnClickListener(this);
        mBtnRemoveBlack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        contactChangeLiveData = LiveDataBus.get();
        viewModel = new ViewModelProvider(this).get(ContactDetailViewModel.class);
        viewModel.blackObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT));
                    finish();
                }
            });
        });
        viewModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT));
                    finish();
                }
            });
        });
        viewModel.userInfoObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>() {
                @Override
                public void onSuccess(EaseUser data) {
                    mUser = data;
                    updateLayout();
                    sendEvent();
                }
            });
        });

        addContactViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        addContactViewModel.getAddContact().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if(data) {
                        showToast(getResources().getString(R.string.em_add_contact_send_successful));
                        mBtnAddContact.setEnabled(false);
                    }
                }
            });
        });

        blackViewModel = new ViewModelProvider(this).get(ContactBlackViewModel.class);

        blackViewModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.CONTACT_CHANGE).postValue(EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT));
                    finish();
                }
            });
        });

        viewModel.getUserInfoById(mUser.getUsername(),mIsFriend);
    }

    private void sendEvent() {
        //更新本地联系人列表
        DemoHelper.getInstance().updateContactList();
        EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_UPDATE, EaseEvent.TYPE.CONTACT);
        event.message = mUser.getUsername();
        //发送联系人更新事件
        contactChangeLiveData.with(DemoConstant.CONTACT_UPDATE).postValue(event);
    }

    private void updateLayout() {
        mTvName.setText(mUser.getNickname());
        CustomImageUtil.getInstance().setHeadView(mAvatarUser,mUser.getAvatar());
    }

    private void showDeleteDialog(EaseUser user) {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.ease_friends_delete_contact_hint)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        viewModel.deleteContact(user.getUsername());
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_note :
                showToast("跳转到备注设置");
                break;
            case R.id.btn_chat :
                ChatActivity.actionStart(mContext, mUser.getUsername(), EaseConstant.CHATTYPE_SINGLE);
                break;
            case R.id.btn_voice :
                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,mUser.getUsername(),null, VideoCallActivity.class);
                break;
            case R.id.btn_video :
                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL,mUser.getUsername(),null, VideoCallActivity.class);
                break;
            case R.id.btn_add_contact :
                addContactViewModel.addContact(mUser.getUsername(), getResources().getString(R.string.em_add_contact_add_a_friend));
                break;
            case R.id.btn_remove_black://从黑名单中移除
                removeBlack();
                break;
        }
    }

    private void removeBlack() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_friends_move_out_the_blacklist_hint)
                .setOnConfirmClickListener(new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        blackViewModel.removeUserFromBlackList(mUser.getUsername());
                    }
                })
                .showCancelButton(true)
                .show();
    }
    
}
