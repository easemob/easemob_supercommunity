package com.community.easeim.section.chat.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.community.easeim.DemoHelper;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.utils.SoftHideKeyBoardUtil;
import com.community.easeim.common.utils.ToastUtils;
import com.community.easeim.common.widget.MyBottomSheetDialog;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.imkit.utils.EaseUserUtils;
import com.community.easeim.imkit.widget.EaseTitleBar;
import com.community.easeim.section.base.BaseInitActivity;
import com.community.easeim.section.chat.fragment.ChatFragment;
import com.community.easeim.section.chat.viewmodel.ChatViewModel;
import com.community.easeim.section.chat.viewmodel.MessageViewModel;
import com.community.easeim.section.contact.viewmodels.AddContactViewModel;
import com.community.easeim.section.contact.viewmodels.ContactsViewModel;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.friend.adapter.ContactAdapter;
import com.community.easeim.section.friend.inters.OnItemClickListener;
import com.community.easeim.section.ground.activity.GroundModifiyActivity;
import com.community.easeim.section.ground.activity.MembersListActivity;
import com.community.easeim.section.group.GroupHelper;
import com.community.easeim.section.group.activity.ChatRoomDetailActivity;
import com.community.easeim.section.group.activity.GroupDetailActivity;
import com.community.easeim.section.group.activity.GroupMsgDisturbActivity;
import com.community.easeim.section.group.viewmodels.GroupDetailViewModel;
import com.community.easeim.section.group.viewmodels.GroupPickContactsViewModel;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.voice.Callback;
import com.google.android.material.snackbar.Snackbar;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;

import java.util.List;


public class ChatActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, EaseTitleBar.OnRightClickListener, ChatFragment.OnFragmentInfoListener, View.OnClickListener {
    private String conversationId;
    private int chatType;
    private ChatFragment fragment;
    private String historyMsgId;
    private ChatViewModel viewModel;
    private TextView mTvName;
    private String mChatName;
    private MyBottomSheetDialog mInviteSheet, mChannelSheet;
    private TextView mTvChannelTitle, mTvChannelName, mTvChannelDel;
    private LinearLayout mLlInvite, mLlChannelNotification, mLLChanelNotice, mLlChannelMembers;
    private RecyclerView mRvContactMembers;
    private TextView mTvInvite;
    private ContactAdapter mContactAdapter;
    private ContactsViewModel mContactModel;
    private GroupPickContactsViewModel mPickContactsViewModel;
    private ImageView mIvInvite;
    private boolean mIsOwner;
    private ClipboardManager mClipboard;

    private GroupDetailViewModel mGroupDetailViewModel;
    private LinearLayout mLlAdd;
    private AddContactViewModel mAddContactViewModel;
    private LinearLayout mLlTop;
    private TextView mTvTop;
    private TextView mTvOwnerName;

    public static void actionStart(Context context, String conversationId, int chatType) {
        AppManager.getInstance().finishActivity(ChatActivity.class);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String conversationId, String chatName, int chatType) {
        AppManager.getInstance().finishActivity(ChatActivity.class);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        intent.putExtra(EaseConstant.EXTRA_CHAT_NAME, chatName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_chat;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        conversationId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
        chatType = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        historyMsgId = intent.getStringExtra(DemoConstant.HISTORY_MSG_ID);
        mChatName = intent.getStringExtra(EaseConstant.EXTRA_CHAT_NAME);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        SoftHideKeyBoardUtil.assistActivity(this);
        super.initView(savedInstanceState);
        mTvName = findViewById(R.id.tv_name);
        mIvInvite = findViewById(R.id.iv_invite);
        mLlAdd = findViewById(R.id.ll_add);
        mLlTop = findViewById(R.id.ll_top);
        mTvTop = findViewById(R.id.tv_top);
        mTvOwnerName = findViewById(R.id.tv_owner_name);

        initChatFragment();
        initBottomSheet();
        initChannelSheet();
    }

    private void initBottomSheet() {
        mInviteSheet = new MyBottomSheetDialog(mContext);
        mInviteSheet.setContentView(R.layout.sheet_voice_invite);
        mRvContactMembers = mInviteSheet.findViewById(R.id.rv_members);
        mInviteSheet.findViewById(R.id.ll_copy_invitation_code).setOnClickListener(this);
        mTvInvite = mInviteSheet.findViewById(R.id.tv_invite);
        initInviteMembers();
    }

    private void initChannelSheet() {
        mChannelSheet = new MyBottomSheetDialog(mContext);
        mChannelSheet.setContentView(R.layout.sheet_channel_detail);
        mLlInvite = mChannelSheet.findViewById(R.id.ll_invite);
        mLlChannelNotification = mChannelSheet.findViewById(R.id.ll_notification_setting);
        mTvChannelTitle = mChannelSheet.findViewById(R.id.tv_title);
        mTvChannelName = mChannelSheet.findViewById(R.id.tv_channel_name);
        mLLChanelNotice = mChannelSheet.findViewById(R.id.ll_channel_notice);
        mLlChannelMembers = mChannelSheet.findViewById(R.id.ll_members);
        mTvChannelDel = mChannelSheet.findViewById(R.id.tv_delete_channel);


    }

    public void onCloseTopClick(View v) {
        mLlTop.setVisibility(View.GONE);
    }

    public void onAddClick(View v) {
        mAddContactViewModel.addContact(conversationId, getResources().getString(R.string.em_add_contact_add_a_friend));
    }

    public void onCloseAddClick(View v) {
        mLlAdd.setVisibility(View.GONE);
    }

    private void initInviteMembers() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvContactMembers.setLayoutManager(layoutManager);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mContactAdapter = new ContactAdapter();
        mContactAdapter.setOnItemClickListener(new OnItemClickListener<EaseUser>() {
            @Override
            public void onItemClick(EaseUser easeUser) {
                new CustomDialog
                        .Builder(mContext)
                        .setTitle("提示")
                        .setTitleKey(easeUser.getNickname())
                        .setMsg("确认邀请“" + easeUser.getNickname() + "”加入该频道？")
                        .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(View view) {
                                mInviteSheet.dismiss();
                                mPickContactsViewModel.addGroupMembers(mIsOwner, conversationId, new String[]{easeUser.getUsername()});
                            }
                        })
                        .show();
            }
        });
        mRvContactMembers.setAdapter(mContactAdapter);

        loadAllContacts();
    }

    private void loadAllContacts() {
        mContactModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        mContactModel.getContactObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseUser>>() {
                @Override
                public void onSuccess(List<EaseUser> list) {
                    mContactAdapter.setData(list);
                }

                @Override
                public void onLoading(@Nullable List<EaseUser> data) {
                    super.onLoading(data);
                    mContactAdapter.setData(data);
                }
            });
        });

        mContactModel.resultObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    showToast(R.string.em_friends_move_into_blacklist_success);
                    mContactModel.loadContactList(false);
                }
            });
        });

        mContactModel.deleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    mContactModel.loadContactList(false);
                }
            });
        });

        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.messageChangeObservable().with(DemoConstant.REMOVE_BLACK, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(true);
            }
        });


        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_ADD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_DELETE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.messageChangeObservable().with(DemoConstant.CONTACT_UPDATE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isContactChange()) {
                mContactModel.loadContactList(false);
            }
        });

        mContactModel.loadContactList(true);

        LiveDataBus.get().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::groupChange);

    }

    private void groupChange(EaseEvent event) {
        mGroupDetailViewModel.getGroup(conversationId);
    }

    protected void onMoreClick(View v) {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
            if (group != null) {
                if (GroupHelper.isOwner(group)) {
                    mTvChannelDel.setText("删除频道");
                } else {
                    mTvChannelDel.setText("退出频道");
                }

                mTvChannelTitle.setText(group.getGroupName());
                mTvChannelName.setText(group.getGroupName());
                mLlInvite.setOnClickListener(this);
                mLlChannelNotification.setOnClickListener(this);
                mTvChannelName.setOnClickListener(this);
                mLLChanelNotice.setOnClickListener(this);
                mLlChannelMembers.setOnClickListener(this);
                mTvChannelDel.setOnClickListener(this);
            }

            mChannelSheet.show();
        } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            ChatDetailActivity.actionStart(this, conversationId);
        }
    }

    private void initChatFragment() {
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.getInstance().getModel().isMsgRoaming());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat").commit();
    }

    @Override
    protected void initListener() {
        super.initListener();
        fragment.setOnFragmentInfoListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            initIntent(intent);
            initChatFragment();
            initData();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        mIvInvite.setVisibility(chatType == EaseConstant.CHATTYPE_GROUP ? View.VISIBLE : View.GONE);
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
            EaseUserUtils.getUserInfo(group.getOwner(), new Callback<EaseUser>() {
                @Override
                public void onSuccess(EaseUser easeUser) {
                    EaseThreadManager.getInstance().runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvOwnerName.setText(easeUser.getNickname() + "：");
                        }
                    });
                }

                @Override
                public void onError(String err) {

                }
            });
            mIsOwner = GroupHelper.isOwner(group);
            mLlAdd.setVisibility(View.GONE);
        } else {
            mLlAdd.setVisibility(DemoHelper.getInstance().getModel().isContact(conversationId) ? View.GONE : View.VISIBLE);
        }

        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    EaseEvent event = EaseEvent.create(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE);
                    messageViewModel.setMessageChange(event);
                }
            });
        });
        viewModel.getChatRoomObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMChatRoom>() {
                @Override
                public void onSuccess(@Nullable EMChatRoom data) {
                    setDefaultTitle();
                }
            });
        });
        messageViewModel.getMessageChange().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isGroupLeave() && TextUtils.equals(conversationId, event.message)) {
                finish();
            }
            mGroupDetailViewModel.getGroupAnnouncement(conversationId);
        });
        messageViewModel.getMessageChange().with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isChatRoomLeave() && TextUtils.equals(conversationId, event.message)) {
                finish();
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.MESSAGE_FORWARD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                showSnackBar(event.event);
            }
        });
        messageViewModel.getMessageChange().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (conversation == null) {
                finish();
            }
        });

        setDefaultTitle();

        mPickContactsViewModel = new ViewModelProvider(this).get(GroupPickContactsViewModel.class);

        mGroupDetailViewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        mGroupDetailViewModel.getGroupAnnouncement(conversationId);
        mGroupDetailViewModel.getLeaveGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_LEAVE, EaseEvent.TYPE.GROUP, conversationId));
                }
            });
        });
        mGroupDetailViewModel.getAnnouncementObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    if (TextUtils.isEmpty(data)) {
                        mLlTop.setVisibility(View.GONE);
                        return;
                    }
                    mLlTop.setVisibility(View.VISIBLE);
                    mTvTop.setText(data);
                }
            });
        });
        mGroupDetailViewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    mTvName.setText(data.getGroupName());
                    mTvChannelName.setText(data.getGroupName());
                }
            });
        });

        mAddContactViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mAddContactViewModel.getAddContact().observe(mContext, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    if (data) {
                        showToast(getResources().getString(R.string.em_add_contact_send_successful));
                        mLlAdd.setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    private void showSnackBar(String event) {
        Snackbar.make(mTvName, event, Snackbar.LENGTH_SHORT).show();
    }

    private void setDefaultTitle() {
        if (!TextUtils.isEmpty(mChatName)) {
            mTvName.setText(mChatName);
            return;
        }
        String title;
        if (chatType == DemoConstant.CHATTYPE_GROUP) {
            title = GroupHelper.getGroupName(conversationId);
            mTvName.setText(title);
            mTvInvite.setText("Hello！欢迎来「" + title + "」和我一起嗨！打开⬇️链接：https://www.easemob.com/download/app/discord_demo");
        } else if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(conversationId);
            if (room == null) {
                viewModel.getChatRoom(conversationId);
                return;
            }
            title = TextUtils.isEmpty(room.getName()) ? conversationId : room.getName();
            mTvName.setText(title);
        } else {
            EaseUserUtils.setUserNick(conversationId, mTvName);
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        if (chatType == DemoConstant.CHATTYPE_SINGLE) {
            //跳转到单聊设置页面
            SingleChatSetActivity.actionStart(mContext, conversationId);
        } else {
            // 跳转到群组设置
            if (chatType == DemoConstant.CHATTYPE_GROUP) {
                GroupDetailActivity.actionStart(mContext, conversationId);
            } else if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
                ChatRoomDetailActivity.actionStart(mContext, conversationId);
            }
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        showToast(errorMsg);
    }

    @Override
    public void onOtherTyping(String action) {
        if (TextUtils.equals(action, "TypingBegin")) {
            mTvName.setText(getString(R.string.alert_during_typing));
        } else if (TextUtils.equals(action, "TypingEnd")) {
            setDefaultTitle();
        }
    }

    public void onInviteClick(View v) {
        mInviteSheet.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_copy_invitation_code:
            case R.id.ll_invite:
                mClipboard.setPrimaryClip(ClipData.newPlainText(null, "https://www.easemob.com/download/app/discord_demo"));
                ToastUtils.showToast("已复制频道邀请链接");
                break;
            case R.id.ll_notification_setting:
                GroupMsgDisturbActivity.actionStart(mContext, conversationId);
                break;
            case R.id.ll_members:
                MembersListActivity.actionStart(mContext, conversationId);
                break;
            case R.id.tv_channel_name:
                modifyChannelName();
                break;
            case R.id.ll_channel_notice:
                modifyChannelNotice();
                break;
            case R.id.tv_delete_channel:
                showDelChannelDialog();
                break;
        }
    }

    //修改频道名称
    private void modifyChannelName() {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
        if (GroupHelper.isOwner(group) || GroupHelper.isAdmin(group)) {
            if (TextUtils.equals(group.getGroupName(), getResources().getString(R.string.court_name))) {
                com.hjq.toast.ToastUtils.show("\"社区大厅\"为保留渠道名称不能修改!");
                return;
            }
            Intent intent = new Intent(mContext, GroundModifiyActivity.class);
            intent.putExtra("modifyType", 4);
            intent.putExtra("objId", group.getGroupId());
            intent.putExtra("nickName", group.getGroupName());
            startActivityForResult(intent, 1);

            mChannelSheet.dismiss();
        }
    }

    //修改频道公共信息
    private void modifyChannelNotice() {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
        if (GroupHelper.isOwner(group) || GroupHelper.isAdmin(group)) {
            Intent intent = new Intent(mContext, GroundModifiyActivity.class);
            intent.putExtra("modifyType", 5);
            intent.putExtra("objId", group.getGroupId());
            intent.putExtra("nickName", group.getAnnouncement());
            startActivityForResult(intent, 2);
        }
    }

    /**
     * 删除频道
     */
    private void showDelChannelDialog() {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
        if (GroupHelper.isOwner(group) && TextUtils.equals(group.getGroupName(), getResources().getString(R.string.court_name))) {
            com.hjq.toast.ToastUtils.show("社区大厅不能删除!");
            return;
        }

        String msg = "确认删除该频道?";
        if (!GroupHelper.isOwner(group)) {
            msg = "确认退出该频道?";
        }
        new CustomDialog
                .Builder(mContext)
                .setTitle("提示")
                .setMsg(msg)
                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        mChannelSheet.dismiss();
                        if (GroupHelper.isOwner(group)) {
                            //解散群  解散群时 删除一条群和社区的关联
                            mGroupDetailViewModel.destroyGroup(group.getGroupId());
                            GroundManager.getInstance().deleteGroundGroupInfo(conversationId);
                        } else {
                            //退出群
                            mGroupDetailViewModel.leaveGroup(group.getGroupId());
                        }
                    }
                })
                .show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGroupDetailViewModel.getGroup(conversationId);
        mGroupDetailViewModel.getGroupAnnouncement(conversationId);
    }
}
