package com.community.easeim.section.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.community.easeim.DemoApplication;
import com.community.easeim.DemoHelper;
import com.community.easeim.MainActivity;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.db.DemoDbHelper;
import com.community.easeim.common.db.dao.EmUserDao;
import com.community.easeim.common.db.entity.EmUserEntity;
import com.community.easeim.common.db.entity.InviteMessageStatus;
import com.community.easeim.common.interfaceOrImplement.ResultCallBack;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.leancloud.LeanCloudManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.manager.AppManager;
import com.community.easeim.common.manager.PushAndMessageHelper;
import com.community.easeim.common.repositories.EMContactManagerRepository;
import com.community.easeim.common.repositories.EMGroupManagerRepository;
import com.community.easeim.common.repositories.EMPushManagerRepository;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.imkit.interfaces.EaseGroupListener;
import com.community.easeim.imkit.manager.EaseAtMessageHelper;
import com.community.easeim.imkit.manager.EaseChatPresenter;
import com.community.easeim.imkit.manager.EaseSystemMsgManager;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.chat.activity.ChatActivity;
import com.community.easeim.section.dialog.CustomDialogFragment;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.group.GroupHelper;
import com.community.easeim.section.home.dialog.CustomDialog;
import com.community.easeim.section.voice.VoiceTalkActivity;
import com.community.easeim.section.voice.bean.ChannelCmd;
import com.community.easeim.voice.CmdMediaCtrl;
import com.google.gson.Gson;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 主要用于chat过程中的全局监听，并对相应的事件进行处理
 * {@link #init()}方法建议在登录成功以后进行调用
 */
public class ChatPresenter extends EaseChatPresenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();
    private static final int HANDLER_SHOW_TOAST = 0;
    private static ChatPresenter instance;
    private LiveDataBus messageChangeLiveData;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    private boolean isPushConfigsWithServer = false;
    private Context appContext;
    protected Handler handler;

    Queue<String> msgQueue = new ConcurrentLinkedQueue<>();

    private ChatPresenter() {
        appContext = DemoApplication.getInstance();
        initHandler(appContext.getMainLooper());
        messageChangeLiveData = LiveDataBus.get();
        //添加网络连接状态监听
        DemoHelper.getInstance().getEMClient().addConnectionListener(new ChatConnectionListener());
        //添加多端登录监听
        DemoHelper.getInstance().getEMClient().addMultiDeviceListener(new ChatMultiDeviceListener());
        //添加群组监听
        DemoHelper.getInstance().getGroupManager().addGroupChangeListener(new ChatGroupListener());
        //添加联系人监听
        DemoHelper.getInstance().getContactManager().setContactListener(new ChatContactListener());
        //添加聊天室监听
        DemoHelper.getInstance().getChatroomManager().addChatRoomChangeListener(new ChatRoomListener());
        //添加对会话的监听（监听已读回执）
        DemoHelper.getInstance().getChatManager().addConversationListener(new ChatConversationListener());
    }

    public static ChatPresenter getInstance() {
        if (instance == null) {
            synchronized (ChatPresenter.class) {
                if (instance == null) {
                    instance = new ChatPresenter();
                }
            }
        }
        return instance;
    }

    /**
     * 将需要登录成功进入MainActivity中初始化的逻辑，放到此处进行处理
     */
    public void init() {

    }

    public void initHandler(Looper looper) {
        handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                switch (msg.what) {
                    case HANDLER_SHOW_TOAST:
                        if (obj instanceof String) {
                            String str = (String) obj;
                            //ToastUtils.showToast(str);
                            Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        while (!msgQueue.isEmpty()) {
            showToast(msgQueue.remove());
        }
    }

    void showToast(@StringRes int mesId) {
        showToast(context.getString(mesId));
    }

    void showToast(final String message) {
        Log.d(TAG, "receive invitation to join the group：" + message);
        if (handler != null) {
            Message msg = Message.obtain(handler, HANDLER_SHOW_TOAST, message);
            handler.sendMessage(msg);
        } else {
            msgQueue.add(message);
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
        for (EMMessage message : messages) {
            EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
            EMLog.d(TAG, "onMessageReceived: " + message.getType());
            // 如果设置群组离线消息免打扰，则不进行消息通知
            List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
            if (disabledIds != null && disabledIds.contains(message.conversationId())) {
                return;
            }
            // in background, do not refresh UI, notify it in notification bar
            if (!DemoApplication.getInstance().getLifecycleCallbacks().isFront()) {
                getNotifier().notify(message);
            }
            //notify new message
            getNotifier().vibrateAndPlayTone(message);
        }
    }


    /**
     * 判断是否已经启动了MainActivity
     * @return
     */
    private synchronized boolean isAppLaunchMain() {
        List<Activity> activities = DemoApplication.getInstance().getLifecycleCallbacks().getActivityList();
        if (activities != null && !activities.isEmpty()) {
            for (int i = activities.size() - 1; i >= 0; i--) {
                if (activities.get(i) instanceof MainActivity) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        Gson gson = new Gson();
        super.onCmdMessageReceived(messages);
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_CMD_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);


        for (EMMessage message : messages) {
            String action = ((EMCmdMessageBody) message.getBody()).action();
            if (TextUtils.equals(action, EaseConstant.MEDIA_CTRL_ACTION)) {
                Map<String, Object> ext = message.ext();
                if (ext.containsKey(EaseConstant.CHANNEL_ID) && ext.containsKey(EaseConstant.MEDIA_CTRL)) {
                    String channelId = (String) ext.get(EaseConstant.CHANNEL_ID);
                    Log.e("tagqi", "onCmdMessageReceived: media ctrl");
                    CmdMediaCtrl.getInstance().sendVoiceMemberChange(channelId);
                }
            } else if (TextUtils.equals(action, EaseConstant.CHANNEL_ACTION)) {
                Map<String, Object> ext = message.ext();
                if (ext.containsKey(EaseConstant.CHANNEL_CMD)) {
                    String cmd = (String) ext.get(EaseConstant.CHANNEL_CMD);
                    Log.e("tagqi", "onCmdMessageReceived: channel ctrl " + cmd);

                    ChannelCmd channelCmd = gson.fromJson(cmd, ChannelCmd.class);
                    if (TextUtils.equals(channelCmd.getCmd(),EaseConstant.CMD_INVITE)){
                        new CustomDialog
                                .Builder((AppCompatActivity) AppManager.getInstance().getTopActivity())
                                .setTitle("来自“" + channelCmd.getInviter() + "”的语音频道邀请")
                                .setTitleKey(channelCmd.getInviter())
                                .setMsg("确认加入“" + channelCmd.getChannelName() + "”？")
                                .setMsgKey(channelCmd.getChannelName())
                                .setOnConfirmClickListener(new CustomDialogFragment.OnConfirmClickListener() {
                                    @Override
                                    public void onConfirmClick(View view) {
                                        AppManager.getInstance().finishActivity(VoiceTalkActivity.class);
                                        VoiceTalkActivity.actionStart((AppCompatActivity) AppManager.getInstance().getTopActivity(), channelCmd.getChannelId(),channelCmd.getGroundName());
                                    }
                                })
                                .show();
                    }

                }
            }
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        super.onMessageRead(messages);
        if (!(DemoApplication.getInstance().getLifecycleCallbacks().current() instanceof ChatActivity)) {
            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
        for (EMMessage msg : messages) {
            if (msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)) {
                EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
            }
            EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            EMTextMessageBody txtBody = new EMTextMessageBody(String.format(context.getString(R.string.msg_recall_by_user), msg.getFrom()));
            msgNotification.addBody(txtBody);
            msgNotification.setFrom(msg.getFrom());
            msgNotification.setTo(msg.getTo());
            msgNotification.setUnread(false);
            msgNotification.setMsgTime(msg.getMsgTime());
            msgNotification.setLocalTime(msg.getMsgTime());
            msgNotification.setChatType(msg.getChatType());
            msgNotification.setAttribute(DemoConstant.MESSAGE_TYPE_RECALL, true);
            msgNotification.setStatus(EMMessage.Status.SUCCESS);
            EMClient.getInstance().chatManager().saveMessage(msgNotification);
        }
    }

    private class ChatConversationListener implements EMConversationListener {

        @Override
        public void onCoversationUpdate() {

        }

        @Override
        public void onConversationRead(String from, String to) {
            EaseEvent event = EaseEvent.create(DemoConstant.CONVERSATION_READ, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.CONVERSATION_READ).postValue(event);
        }
    }

    private class ChatConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            EMLog.i(TAG, "onConnected");
            if (!DemoHelper.getInstance().isLoggedIn()) {
                return;
            }
            if (!isGroupsSyncedWithServer) {
                EMLog.i(TAG, "isGroupsSyncedWithServer");
                new EMGroupManagerRepository().getAllGroups(new ResultCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> value) {
                        //加载完群组信息后，刷新会话列表页面，保证展示群组名称
                        EMLog.i(TAG, "isGroupsSyncedWithServer success");
                        EaseEvent event = EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
                        messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(event);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {

                    }
                });
                isGroupsSyncedWithServer = true;
            }
            if (!isContactsSyncedWithServer) {
                EMLog.i(TAG, "isContactsSyncedWithServer");
                new EMContactManagerRepository().getContactList(new ResultCallBack<List<EaseUser>>() {
                    @Override
                    public void onSuccess(List<EaseUser> value) {
                        EmUserDao userDao = DemoDbHelper.getInstance(DemoApplication.getInstance()).getUserDao();
                        if (userDao != null) {
                            userDao.clearUsers();
                            userDao.insert(EmUserEntity.parseList(value));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {

                    }
                });
                isContactsSyncedWithServer = true;
            }
            if (!isBlackListSyncedWithServer) {
                EMLog.i(TAG, "isBlackListSyncedWithServer");
                new EMContactManagerRepository().getBlackContactList(null);
                isBlackListSyncedWithServer = true;
            }
            if (!isPushConfigsWithServer) {
                EMLog.i(TAG, "isPushConfigsWithServer");
                //首先获取push配置，否则获取push配置项会为空
                new EMPushManagerRepository().fetchPushConfigsFromServer();
                isPushConfigsWithServer = true;
            }
        }

        /**
         * 用来监听账号异常
         * @param error
         */
        @Override
        public void onDisconnected(int error) {
            EMLog.i(TAG, "onDisconnected =" + error);
            String event = null;
            if (error == EMError.USER_REMOVED) {
                event = DemoConstant.ACCOUNT_REMOVED;
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_CONFLICT;
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                event = DemoConstant.ACCOUNT_FORBIDDEN;
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                event = DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD;
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                event = DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE;
            }
            if (!TextUtils.isEmpty(event)) {
                LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE).postValue(new EaseEvent(event, EaseEvent.TYPE.ACCOUNT));
                EMLog.i(TAG, event);
            }
        }
    }

    private class ChatGroupListener extends EaseGroupListener {

        //接收到群组加入邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            super.onInvitationReceived(groupId, groupName, inviter, reason);
            //移除相同的请求
            List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
            if (allMessages != null && !allMessages.isEmpty()) {
                for (EMMessage message : allMessages) {
                    Map<String, Object> ext = message.ext();
                    if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID) && TextUtils.equals(groupId, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)))
                            && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_INVITER) && TextUtils.equals(inviter, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_INVITER)))) {
                        EaseSystemMsgManager.getInstance().removeMessage(message);
                    }
                }
            }
            GroundManager.getInstance().getGroundGroupByGroupId(groupId, new EMValueCallBack<GroundGroupBean>() {
                @Override
                public void onSuccess(GroundGroupBean value) {
                     String groupName = TextUtils.isEmpty(value.getGroupName()) ? groupId : value.getGroupName();
                    Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
                    ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, inviter);
                    ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
                    ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
                    ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
                    ext.put(DemoConstant.SYSTEM_MESSAGE_INVITER, inviter);
                    ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.GROUPINVITATION.name());
                    EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

                    notifyNewInviteMessage(message);
                    EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_RECEIVE, EaseEvent.TYPE.NOTIFY);
                    messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

//            showToast(context.getString(InviteMessageStatus.GROUPINVITATION.getMsgContent(), inviter, groupName));
                    EMLog.i(TAG, context.getString(InviteMessageStatus.GROUPINVITATION.getMsgContent(), inviter, groupName));
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            super.onInvitationAccepted(groupId, invitee, reason);
            //user accept your invitation
            String groupName = GroupHelper.getGroupName(groupId);

            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, invitee);
            ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
            ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_INVITER, invitee);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.GROUPINVITATION_ACCEPTED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_ACCEPTED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(InviteMessageStatus.GROUPINVITATION_ACCEPTED.getMsgContent(), invitee));
            EMLog.i(TAG, context.getString(InviteMessageStatus.GROUPINVITATION_ACCEPTED.getMsgContent(), invitee));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            super.onInvitationDeclined(groupId, invitee, reason);
            //user declined your invitation
            String groupName = GroupHelper.getGroupName(groupId);

            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, invitee);
            ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
            ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_INVITER, invitee);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.GROUPINVITATION_DECLINED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_INVITE_DECLINED, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(InviteMessageStatus.GROUPINVITATION_DECLINED.getMsgContent(), invitee));
            EMLog.i(TAG, context.getString(InviteMessageStatus.GROUPINVITATION_DECLINED.getMsgContent(), invitee));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_INVITER, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.GROUP_USER_REMOVED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);


            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(R.string.demo_group_listener_onUserRemoved, groupName));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onUserRemoved, groupName));
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(R.string.demo_group_listener_onGroupDestroyed, groupName));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onGroupDestroyed, groupName));
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            super.onRequestToJoinReceived(groupId, groupName, applicant, reason);
            //移除相同的请求
            List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
            if (allMessages != null && !allMessages.isEmpty()) {
                for (EMMessage message : allMessages) {
                    Map<String, Object> ext = message.ext();
                    if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID) && TextUtils.equals(groupId, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)))
                            && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM) && TextUtils.equals(applicant, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_FROM)))) {
                        EaseSystemMsgManager.getInstance().removeMessage(message);
                    }
                }
            }
            // user apply to join group
            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, applicant);
            ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
            ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEAPPLYED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);
            EaseEvent event = EaseEvent.create(DemoConstant.NOTIFY_GROUP_JOIN_RECEIVE, EaseEvent.TYPE.NOTIFY);
            messageChangeLiveData.with(DemoConstant.NOTIFY_CHANGE).postValue(event);

            showToast(context.getString(InviteMessageStatus.BEAPPLYED.getMsgContent(), applicant, groupName));
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEAPPLYED.getMsgContent(), applicant, groupName));
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            super.onRequestToJoinAccepted(groupId, groupName, accepter);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true);
            msg.addBody(new EMTextMessageBody(context.getString(R.string.demo_group_listener_onRequestToJoinAccepted, accepter, groupName)));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_JOIN_ACCEPTED, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onRequestToJoinAccepted, accepter, groupName));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onRequestToJoinAccepted, accepter, groupName));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            super.onRequestToJoinDeclined(groupId, groupName, decliner, reason);
            showToast(context.getString(R.string.demo_group_listener_onRequestToJoinDeclined, decliner, groupName));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onRequestToJoinDeclined, decliner, groupName));
        }

        //接收邀请时自动加入到群组的通知
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            super.onAutoAcceptInvitationFromGroup(groupId, inviter, inviteMessage);
            String groupName = GroupHelper.getGroupName(groupId);

            GroundManager.getInstance().getGroundGroupByGroupId(groupId, new EMValueCallBack<GroundGroupBean>() {
                @Override
                public void onSuccess(GroundGroupBean groundGroupBean) {
                    GroundManager.getInstance().saveUserAndGroundInfo(EMClient.getInstance().getCurrentUser(),groundGroupBean.getGroundId());
                    EaseEvent event = EaseEvent.create(EaseConstant.GROUND_CHANGE);
                    LiveDataBus.get().with(EaseConstant.GROUND_CHANGE).postValue(event);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });

            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true);
            msg.addBody(new EMTextMessageBody(context.getString(R.string.demo_group_listener_onAutoAcceptInvitationFromGroup, groupName)));
            msg.setStatus(EMMessage.Status.SUCCESS);
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify invitation message
            getNotifier().vibrateAndPlayTone(msg);
            EaseEvent event = EaseEvent.create(DemoConstant.MESSAGE_GROUP_AUTO_ACCEPT, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(event);

            showToast(context.getString(R.string.demo_group_listener_onAutoAcceptInvitationFromGroup, groupName));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onAutoAcceptInvitationFromGroup, groupName));
        }

        @Override
        public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
            super.onMuteListAdded(groupId, mutes, muteExpire);
            String content = getContentFromList(mutes);
            showToast(context.getString(R.string.demo_group_listener_onMuteListAdded, content));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMuteListAdded, content));
        }

        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes) {
            super.onMuteListRemoved(groupId, mutes);
            String content = getContentFromList(mutes);
            showToast(context.getString(R.string.demo_group_listener_onMuteListRemoved, content));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMuteListRemoved, content));
        }

        @Override
        public void onWhiteListAdded(String groupId, List<String> whitelist) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            String content = getContentFromList(whitelist);
            showToast(context.getString(R.string.demo_group_listener_onWhiteListAdded, content));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onWhiteListAdded, content));
        }

        @Override
        public void onWhiteListRemoved(String groupId, List<String> whitelist) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            String content = getContentFromList(whitelist);
            showToast(context.getString(R.string.demo_group_listener_onWhiteListRemoved, content));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onWhiteListRemoved, content));
        }

        @Override
        public void onAllMemberMuteStateChanged(String groupId, boolean isMuted) {
            EaseEvent easeEvent = new EaseEvent(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
            easeEvent.message = groupId;
            messageChangeLiveData.with(DemoConstant.GROUP_CHANGE).postValue(easeEvent);

            showToast(context.getString(isMuted ? R.string.demo_group_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_group_listener_onAllMemberMuteStateChanged_not_mute));

            EMLog.i(TAG, context.getString(isMuted ? R.string.demo_group_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_group_listener_onAllMemberMuteStateChanged_not_mute));
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            super.onAdminAdded(groupId, administrator);
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onAdminAdded, administrator));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onAdminAdded, administrator));
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onAdminRemoved, administrator));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onAdminRemoved, administrator));
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_OWNER_TRANSFER, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onOwnerChanged, oldOwner, newOwner));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onOwnerChanged, oldOwner, newOwner));
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onMemberJoined, member));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMemberJoined, member));
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onMemberExited, member));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onMemberExited, member));
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onAnnouncementChanged));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onAnnouncementChanged));
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_SHARE_FILE_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onSharedFileAdded, sharedFile.getFileName()));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onSharedFileAdded, sharedFile.getFileName()));
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            LiveDataBus.get().with(DemoConstant.GROUP_SHARE_FILE_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_SHARE_FILE_CHANGE, EaseEvent.TYPE.GROUP));
            showToast(context.getString(R.string.demo_group_listener_onSharedFileDeleted, fileId));
            EMLog.i(TAG, context.getString(R.string.demo_group_listener_onSharedFileDeleted, fileId));
        }
    }

    private class ChatContactListener implements EMContactListener {

        //增加了联系人时回调此方法
        @Override
        public void onContactAdded(String username) {
            EMLog.i("ChatContactListener", "onContactAdded");
            LeanCloudManager.getInstance().getUserInfo(username, new EMValueCallBack<EaseUser>() {
                @Override
                public void onSuccess(EaseUser userInfo) {
                    EmUserEntity entity = new EmUserEntity();
                    entity.setUsername(username);
                    if (userInfo != null){
                        entity.setNickname(userInfo.getNickname());
                        entity.setEmail(userInfo.getEmail());
                        entity.setAvatar(userInfo.getAvatar());
                        entity.setBirth(userInfo.getBirth());
                        entity.setGender(userInfo.getGender());
                        entity.setExt(userInfo.getExt());
                        entity.setContact(0);
                        entity.setSign(userInfo.getSign());
                    }
                    DemoHelper.getInstance().getModel().insert(entity);
                    DemoHelper.getInstance().updateContactList();
                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_ADD, EaseEvent.TYPE.CONTACT);
                    event.message = username;
                    messageChangeLiveData.with(DemoConstant.CONTACT_ADD).postValue(event);
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });

//            String[] userId = new String[1];
//            userId[0] = username;
//            EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userId, new EMValueCallBack<Map<String, EMUserInfo>>() {
//                @Override
//                public void onSuccess(Map<String, EMUserInfo> value) {
//                    EMUserInfo userInfo = value.get(username);
//                    EmUserEntity entity = new EmUserEntity();
//                    entity.setUsername(username);
//                    if (userInfo != null) {
//                        entity.setNickname(userInfo.getNickName());
//                        entity.setEmail(userInfo.getEmail());
//                        entity.setAvatar(userInfo.getAvatarUrl());
//                        entity.setBirth(userInfo.getBirth());
//                        entity.setGender(userInfo.getGender());
//                        entity.setExt(userInfo.getExt());
//                        entity.setContact(0);
//                        entity.setSign(userInfo.getSignature());
//                    }
//                    DemoHelper.getInstance().getModel().insert(entity);
//                    DemoHelper.getInstance().updateContactList();
//                    EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_ADD, EaseEvent.TYPE.CONTACT);
//                    event.message = username;
//                    messageChangeLiveData.with(DemoConstant.CONTACT_ADD).postValue(event);
//
////                    showToast(context.getString(R.string.demo_contact_listener_onContactAdded, username));
//                    EMLog.i(TAG, context.getString(R.string.demo_contact_listener_onContactAdded, username));
//                }
//
//                @Override
//                public void onError(int error, String errorMsg) {
//                    EMLog.i(TAG, context.getString(R.string.demo_contact_get_userInfo_failed) + username + "error:" + error + " errorMsg:" + errorMsg);
//                }
//            });
        }

        @Override
        public void onContactDeleted(String username) {
            EMLog.i("ChatContactListener", "onContactDeleted");
            boolean deleteUsername = DemoHelper.getInstance().getModel().isDeleteUsername(username);
            int num = DemoHelper.getInstance().deleteContact(username);
            DemoHelper.getInstance().updateContactList();
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_DELETE, EaseEvent.TYPE.CONTACT);
            event.message = username;
            messageChangeLiveData.with(DemoConstant.CONTACT_DELETE).postValue(event);

            if (deleteUsername || num == 0) {
//                showToast(context.getString(R.string.demo_contact_listener_onContactDeleted, username));
                EMLog.i(TAG, context.getString(R.string.demo_contact_listener_onContactDeleted, username));
            } else {
                //showToast(context.getString(R.string.demo_contact_listener_onContactDeleted_by_other, username));
                EMLog.i(TAG, context.getString(R.string.demo_contact_listener_onContactDeleted_by_other, username));
            }
        }


        //收到好友邀请
        @Override
        public void onContactInvited(String username, String reason) {
            EMLog.i("ChatContactListener", "onContactInvited");
            List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
            if (allMessages != null && !allMessages.isEmpty()) {
                for (EMMessage message : allMessages) {
                    Map<String, Object> ext = message.ext();
                    if (ext != null && !ext.containsKey(DemoConstant.SYSTEM_MESSAGE_GROUP_ID)
                            && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM) && TextUtils.equals(username, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_FROM)))) {
                        EaseSystemMsgManager.getInstance().removeMessage(message);
                    }
                }
            }

            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, username);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEINVITEED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

//            showToast(context.getString(InviteMessageStatus.BEINVITEED.getMsgContent(), username));
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEINVITEED.getMsgContent(), username));
        }

        //好友请求被同意
        @Override
        public void onFriendRequestAccepted(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestAccepted");
            List<EMMessage> allMessages = EaseSystemMsgManager.getInstance().getAllMessages();
            if (allMessages != null && !allMessages.isEmpty()) {
                for (EMMessage message : allMessages) {
                    Map<String, Object> ext = message.ext();
                    if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM)
                            && TextUtils.equals(username, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_FROM)))) {
                        updateMessage(message);
                        return;
                    }
                }
            }
            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, username);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEAGREED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);
            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);

//            showToast(context.getString(InviteMessageStatus.BEAGREED.getMsgContent()));
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEAGREED.getMsgContent()));
        }

        //好友请求被拒绝
        @Override
        public void onFriendRequestDeclined(String username) {
            EMLog.i("ChatContactListener", "onFriendRequestDeclined");
            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, username);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEREFUSED.name());
            EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

            notifyNewInviteMessage(message);

            EaseEvent event = EaseEvent.create(DemoConstant.CONTACT_CHANGE, EaseEvent.TYPE.CONTACT);
            messageChangeLiveData.with(DemoConstant.CONTACT_CHANGE).postValue(event);
//            showToast(context.getString(InviteMessageStatus.BEREFUSED.getMsgContent(), username));
            EMLog.i(TAG, context.getString(InviteMessageStatus.BEREFUSED.getMsgContent(), username));
        }
    }


    private void updateMessage(EMMessage message) {
        message.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.BEAGREED.name());
        EMTextMessageBody body = new EMTextMessageBody(PushAndMessageHelper.getSystemMessage(message.ext()));
        message.addBody(body);
        EaseSystemMsgManager.getInstance().updateMessage(message);
    }

    private class ChatMultiDeviceListener implements EMMultiDeviceListener {


        @Override
        public void onContactEvent(int event, String target, String ext) {
            EMLog.i(TAG, "onContactEvent event" + event);
            DemoDbHelper dbHelper = DemoDbHelper.getInstance(DemoApplication.getInstance());
            String message = null;
            switch (event) {
                case CONTACT_REMOVE: //好友已经在其他机子上被移除
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_REMOVE");
                    message = DemoConstant.CONTACT_REMOVE;
                    if (dbHelper.getUserDao() != null) {
                        dbHelper.getUserDao().deleteUser(target);
                    }
                    removeTargetSystemMessage(target, DemoConstant.SYSTEM_MESSAGE_FROM);
                    // TODO: 2020/1/16 0016 确认此处逻辑，是否是删除当前的target
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);

                    showToast("CONTACT_REMOVE");
                    break;
                case CONTACT_ACCEPT: //好友请求已经在其他机子上被同意
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ACCEPT");
                    message = DemoConstant.CONTACT_ACCEPT;
                    EmUserEntity entity = new EmUserEntity();
                    entity.setUsername(target);
                    if (dbHelper.getUserDao() != null) {
                        dbHelper.getUserDao().insert(entity);
                    }
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_ACCEPT);

                    showToast("CONTACT_ACCEPT");
                    break;
                case CONTACT_DECLINE: //好友请求已经在其他机子上被拒绝
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_DECLINE");
                    message = DemoConstant.CONTACT_DECLINE;
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_DECLINE);

                    showToast("CONTACT_DECLINE");
                    break;
                case CONTACT_BAN: //当前用户在其他设备加某人进入黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_BAN");
                    message = DemoConstant.CONTACT_BAN;
                    if (dbHelper.getUserDao() != null) {
                        dbHelper.getUserDao().deleteUser(target);
                    }
                    removeTargetSystemMessage(target, DemoConstant.SYSTEM_MESSAGE_FROM);
                    DemoHelper.getInstance().getChatManager().deleteConversation(target, false);
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_BAN);

                    showToast("CONTACT_BAN");
                    break;
                case CONTACT_ALLOW: // 好友在其他设备被移出黑名单
                    EMLog.i("ChatMultiDeviceListener", "CONTACT_ALLOW");
                    message = DemoConstant.CONTACT_ALLOW;
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_ALLOW);

                    showToast("CONTACT_ALLOW");
                    break;
            }
            if (!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.CONTACT);
                messageChangeLiveData.with(message).postValue(easeEvent);
            }
        }

        @Override
        public void onGroupEvent(int event, String groupId, List<String> usernames) {
            EMLog.i(TAG, "onGroupEvent event" + event);
            String message = null;
            switch (event) {
                case GROUP_CREATE:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE);

                    showToast("GROUP_CREATE");
                    break;
                case GROUP_DESTROY:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_DESTROY");
                    break;
                case GROUP_JOIN:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_JOIN");
                    break;
                case GROUP_LEAVE:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_LEAVE");
                    break;
                case GROUP_APPLY:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID);
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY);

                    showToast("GROUP_APPLY");
                    break;
                case GROUP_APPLY_ACCEPT:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID, usernames.get(0), DemoConstant.SYSTEM_MESSAGE_FROM);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT);

                    showToast("GROUP_APPLY_ACCEPT");
                    break;
                case GROUP_APPLY_DECLINE:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID, usernames.get(0), DemoConstant.SYSTEM_MESSAGE_FROM);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE);

                    showToast("GROUP_APPLY_DECLINE");
                    break;
                case GROUP_INVITE:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE);

                    showToast("GROUP_INVITE");
                    break;
                case GROUP_INVITE_ACCEPT:
                    String st3 = context.getString(R.string.Invite_you_to_join_a_group_chat);
                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.GroupChat);
                    // TODO: person, reason from ext
                    String from = "";
                    if (usernames != null && usernames.size() > 0) {
                        msg.setFrom(usernames.get(0));
                    }
                    msg.setTo(groupId);
                    msg.setMsgId(UUID.randomUUID().toString());
                    msg.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true);
                    msg.addBody(new EMTextMessageBody(msg.getFrom() + " " + st3));
                    msg.setStatus(EMMessage.Status.SUCCESS);
                    // save invitation as messages
                    EMClient.getInstance().chatManager().saveMessage(msg);

                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_ACCEPT);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_INVITE_ACCEPT");
                    break;
                case GROUP_INVITE_DECLINE:
                    removeTargetSystemMessage(groupId, DemoConstant.SYSTEM_MESSAGE_GROUP_ID);
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);

                    showToast("GROUP_INVITE_DECLINE");
                    break;
                case GROUP_KICK:
                    // TODO: person, reason from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_KICK);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_KICK");
                    break;
                case GROUP_BAN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_BAN");
                    break;
                case GROUP_ALLOW:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW);

                    showToast("GROUP_ALLOW");
                    break;
                case GROUP_BLOCK:
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK);

                    showToast("GROUP_BLOCK");
                    break;
                case GROUP_UNBLOCK:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK);

                    showToast("GROUP_UNBLOCK");
                    break;
                case GROUP_ASSIGN_OWNER:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ASSIGN_OWNER);

                    showToast("GROUP_ASSIGN_OWNER");
                    break;
                case GROUP_ADD_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_ADMIN);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_ADD_ADMIN");
                    break;
                case GROUP_REMOVE_ADMIN:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_ADMIN);
                    message = DemoConstant.GROUP_CHANGE;

                    showToast("GROUP_REMOVE_ADMIN");
                    break;
                case GROUP_ADD_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_MUTE);

                    showToast("GROUP_ADD_MUTE");
                    break;
                case GROUP_REMOVE_MUTE:
                    // TODO: person from ext
                    saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_MUTE);

                    showToast("GROUP_REMOVE_MUTE");
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(message)) {
                EaseEvent easeEvent = EaseEvent.create(message, EaseEvent.TYPE.GROUP);
                messageChangeLiveData.with(message).postValue(easeEvent);
            }
        }
    }

    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target
     */
    private void removeTargetSystemMessage(String target, String params) {
        EMConversation conversation = EaseSystemMsgManager.getInstance().getConversation();
        List<EMMessage> messages = conversation.getAllMessages();
        if (messages != null && !messages.isEmpty()) {
            for (EMMessage message : messages) {
                String from = null;
                try {
                    from = message.getStringAttribute(params);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals(from, target)) {
                    conversation.removeMessage(message.getMsgId());
                }
            }
        }
    }

    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target1
     */
    private void removeTargetSystemMessage(String target1, String params1, String target2, String params2) {
        EMConversation conversation = EaseSystemMsgManager.getInstance().getConversation();
        List<EMMessage> messages = conversation.getAllMessages();
        if (messages != null && !messages.isEmpty()) {
            for (EMMessage message : messages) {
                String targetParams1 = null;
                String targetParams2 = null;
                try {
                    targetParams1 = message.getStringAttribute(params1);
                    targetParams2 = message.getStringAttribute(params2);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals(targetParams1, target1) && TextUtils.equals(targetParams2, target2)) {
                    conversation.removeMessage(message.getMsgId());
                }
            }
        }
    }


    private void notifyNewInviteMessage(EMMessage msg) {
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    private void updateContactNotificationStatus(String from, String reason, InviteMessageStatus status) {
        EMMessage msg = null;
        EMConversation conversation = EaseSystemMsgManager.getInstance().getConversation();
        List<EMMessage> allMessages = conversation.getAllMessages();
        if (allMessages != null && !allMessages.isEmpty()) {
            for (EMMessage message : allMessages) {
                Map<String, Object> ext = message.ext();
                if (ext != null && (ext.containsKey(DemoConstant.SYSTEM_MESSAGE_FROM)
                        && TextUtils.equals(from, (String) ext.get(DemoConstant.SYSTEM_MESSAGE_FROM)))) {
                    msg = message;
                }
            }
        }

        if (msg != null) {
            msg.setAttribute(DemoConstant.SYSTEM_MESSAGE_STATUS, status.name());
            EaseSystemMsgManager.getInstance().updateMessage(msg);
        } else {
            // save invitation as message
            Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
            ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, from);
            ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
            ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, status.name());
            msg = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);
            notifyNewInviteMessage(msg);
        }
    }

    private void saveGroupNotification(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
        Map<String, Object> ext = EaseSystemMsgManager.getInstance().createMsgExt();
        ext.put(DemoConstant.SYSTEM_MESSAGE_FROM, groupId);
        ext.put(DemoConstant.SYSTEM_MESSAGE_GROUP_ID, groupId);
        ext.put(DemoConstant.SYSTEM_MESSAGE_REASON, reason);
        ext.put(DemoConstant.SYSTEM_MESSAGE_NAME, groupName);
        ext.put(DemoConstant.SYSTEM_MESSAGE_INVITER, inviter);
        ext.put(DemoConstant.SYSTEM_MESSAGE_STATUS, status.name());
        EMMessage message = EaseSystemMsgManager.getInstance().createMessage(PushAndMessageHelper.getSystemMessage(ext), ext);

        notifyNewInviteMessage(message);
    }

    private class ChatRoomListener implements EMChatRoomChangeListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM_LEAVE);
            showToast(context.getString(R.string.demo_chat_room_listener_onChatRoomDestroyed, roomName));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onChatRoomDestroyed, roomName));
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onMemberJoined, participant));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onMemberJoined, participant));
        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {
            setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onMemberExited, participant));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onMemberExited, participant));
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            if (TextUtils.equals(DemoHelper.getInstance().getCurrentUser(), participant)) {
                setChatRoomEvent(roomId, EaseEvent.TYPE.CHAT_ROOM);
                if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                    showToast(R.string.quiting_the_chat_room);
                    showToast(R.string.quiting_the_chat_room);
                } else {
                    showToast(context.getString(R.string.demo_chat_room_listener_onRemovedFromChatRoom, participant));
                    EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onRemovedFromChatRoom, participant));
                }

            }
        }

        @Override
        public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            String content = getContentFromList(mutes);
            showToast(context.getString(R.string.demo_chat_room_listener_onMuteListAdded, content));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onMuteListAdded, content));
        }

        @Override
        public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
            String content = getContentFromList(mutes);
            showToast(context.getString(R.string.demo_chat_room_listener_onMuteListRemoved, content));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onMuteListRemoved, content));
        }

        @Override
        public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {
            String content = getContentFromList(whitelist);
            showToast(context.getString(R.string.demo_chat_room_listener_onWhiteListAdded, content));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onWhiteListAdded, content));
        }

        @Override
        public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {
            String content = getContentFromList(whitelist);
            showToast(context.getString(R.string.demo_chat_room_listener_onWhiteListRemoved, content));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onWhiteListRemoved, content));
        }

        @Override
        public void onAllMemberMuteStateChanged(String chatRoomId, boolean isMuted) {
            showToast(context.getString(isMuted ? R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_note_mute));
            EMLog.i(TAG, context.getString(isMuted ? R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_mute
                    : R.string.demo_chat_room_listener_onAllMemberMuteStateChanged_note_mute));
        }

        @Override
        public void onAdminAdded(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onAdminAdded, admin));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAdminAdded, admin));
        }

        @Override
        public void onAdminRemoved(String chatRoomId, String admin) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onAdminRemoved, admin));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAdminRemoved, admin));
        }

        @Override
        public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);

            showToast(context.getString(R.string.demo_chat_room_listener_onOwnerChanged, oldOwner, newOwner));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onOwnerChanged, oldOwner, newOwner));
        }

        @Override
        public void onAnnouncementChanged(String chatRoomId, String announcement) {
            setChatRoomEvent(chatRoomId, EaseEvent.TYPE.CHAT_ROOM);
            showToast(context.getString(R.string.demo_chat_room_listener_onAnnouncementChanged));
            EMLog.i(TAG, context.getString(R.string.demo_chat_room_listener_onAnnouncementChanged));
        }
    }

    private void setChatRoomEvent(String roomId, EaseEvent.TYPE type) {
        EaseEvent easeEvent = new EaseEvent(DemoConstant.CHAT_ROOM_CHANGE, type);
        easeEvent.message = roomId;
        messageChangeLiveData.with(DemoConstant.CHAT_ROOM_CHANGE).postValue(easeEvent);
    }

    private String getContentFromList(List<String> members) {
        StringBuilder sb = new StringBuilder();
        for (String member : members) {
            if (!TextUtils.isEmpty(sb.toString().trim())) {
                sb.append(",");
            }
            sb.append(member);
        }
        String content = sb.toString();
        if (content.contains(EMClient.getInstance().getCurrentUser())) {
            content = "您";
        }
        return content;
    }
}
