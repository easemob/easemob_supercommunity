package com.community.easeim.imkit.utils;

import android.text.TextUtils;

import com.community.easeim.DemoApplication;
import com.community.easeim.R;
import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.leancloud.GroundManager;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.imkit.manager.EaseThreadManager;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceChannelManager;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImManager {
    private static final ImManager ourInstance = new ImManager();

    public static ImManager getInstance() {
        return ourInstance;
    }

    private ImManager() {
    }

    public interface CallBack<T> {
        void onSuccess(T t);

        void onFailed(String err);
    }

    public void getAllGround(CallBack<Map<String, List<EMGroup>>> callBack) {
        Map<String, List<EMGroup>> map = new HashMap<>();
        EaseThreadManager.getInstance().runOnIOThread(new Runnable() {
            @Override
            public void run() {
                List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();
                Gson gson = new Gson();
                for (EMGroup group : allGroups) {
                    String des = group.getDescription();
                    if (!TextUtils.isEmpty(des)) {
                        if (map.containsKey(des)) {
                            map.get(des).add(group);
                        } else {
                            List<EMGroup> list = new ArrayList<>();
                            list.add(group);
                            map.put(des, list);
                        }
                    }
                }


                callBack.onSuccess(map);
            }
        });
    }

    public void sendCreateGroupMsg(String msg, String groupId) {
        EMMessage message = EMMessage.createTxtSendMessage(msg, groupId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        message.setAttribute(DemoConstant.EM_NOTIFICATION_TYPE, true);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 创建私有社区
     * @param groundName
     * @param groundDes
     * @param memberCanInvite
     * @param cover
     * @param callBack
     */
    public void createPrivateGround(String groundName, String groundDes, boolean memberCanInvite, String cover, CallBack<List<EMGroup>> callBack) {
//        String id = EaseCommonUtils.generateShortUuid();
        String id = System.currentTimeMillis()+"";
//        EMGroupManager.EMGroupStyle style = memberCanInvite ? EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
        EMGroupManager.EMGroupStyle style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
        //创建channel之后创建社区
        GroundBean bean = new GroundBean();
        bean.setGround_name(groundName);
        bean.setGround_describe(groundDes);
        bean.setGround_id(id);
        bean.setGround_cover(cover);
        bean.setGround_owner(EMClient.getInstance().getCurrentUser());
        bean.setMemberCanInvite(memberCanInvite);
        bean.setGround_type(false);

        GroundManager.getInstance().createGround(bean);

        Gson gson = new Gson();
        String des = gson.toJson(bean);
        List<EMGroup> groups = new ArrayList<>();
        createChannel(DemoApplication.getInstance().getResources().getString(R.string.court_name), des,  false, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup group) {
                groups.add(group);
                GroundManager.getInstance().saveGroundGroupInfo(bean.getGroundId(),group,"",2,"");
                sendCreateGroupMsg(DemoApplication.getInstance().getResources().getString(R.string.court_name)+"创建成功", group.getGroupId());
                createChannel(DemoApplication.getInstance().getResources().getString(R.string.default_channel_name), des, false, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        GroundManager.getInstance().saveGroundGroupInfo(bean.getGroundId(),value,"",1,"");

                        createVoiceChannel(bean);

                        groups.add(value);
                        sendCreateGroupMsg(DemoApplication.getInstance().getString(R.string.default_channel_name)+"创建成功", value.getGroupId());
                        callBack.onSuccess(groups);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onFailed(errorMsg);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                callBack.onFailed(errorMsg);
            }
        });
    }

    /**
     * 创建公共社区
     * @param groundName
     * @param groundDes
     * @param memberCanInvite
     * @param cover
     * @param callBack
     */
    public void createPublicGround(String groundName, String groundDes, boolean memberCanInvite, String cover, CallBack<List<EMGroup>> callBack) {
//        String id = EaseCommonUtils.generateShortUuid();
        String id = System.currentTimeMillis()+"";
//        final EMGroupManager.EMGroupStyle style = memberCanInvite ? EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin : EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;

        final EMGroupManager.EMGroupStyle style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        //创建channel之后创建社区
        GroundBean bean = new GroundBean();
        bean.setGround_name(groundName);
        bean.setGround_describe(groundDes);
        bean.setMemberCanInvite(memberCanInvite);
        bean.setGround_id(id);
        bean.setGround_cover(cover);
        bean.setGround_owner(EMClient.getInstance().getCurrentUser());
        bean.setGround_type(true);

        GroundManager.getInstance().createGround(bean);

        Gson gson = new Gson();
        String des = gson.toJson(bean);
        List<EMGroup> groups = new ArrayList<>();
        createChannel(DemoApplication.getInstance().getResources().getString(R.string.court_name), des, true, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup group) {
                groups.add(group);
                GroundManager.getInstance().saveGroundGroupInfo(bean.getGroundId(),group,"",2,"");
                sendCreateGroupMsg(DemoApplication.getInstance().getResources().getString(R.string.court_name)+"创建成功", group.getGroupId());
                createChannel(DemoApplication.getInstance().getString(R.string.default_channel_name), des, true, new EMValueCallBack<EMGroup>() {
                    @Override
                    public void onSuccess(EMGroup value) {
                        GroundManager.getInstance().saveGroundGroupInfo(bean.getGroundId(),value,"",1,"");

                        createVoiceChannel(bean);

                        groups.add(value);
                        sendCreateGroupMsg(DemoApplication.getInstance().getString(R.string.default_channel_name)+"创建成功", value.getGroupId());
                        callBack.onSuccess(groups);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onFailed(errorMsg);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                callBack.onFailed(errorMsg);
            }
        });
    }

    public void createVoiceChannel(GroundBean bean){
        VoiceChannel channel = new VoiceChannel();
        channel.setOwner(EMClient.getInstance().getCurrentUser());
        channel.setChannelName("语音交流");
        channel.setChannelId(System.currentTimeMillis()+"");
        channel.setRoomIndex(1);
        channel.setGroundId(bean.getGroundId());
        channel.setGroundName(bean.getGroundName());
        VoiceChannelManager.getInstance().createChannel(channel);
    }

    public void createVoiceChannel(GroundBean bean,String channelName){
        VoiceChannel channel = new VoiceChannel();
        channel.setOwner(EMClient.getInstance().getCurrentUser());
        channel.setChannelName(channelName);
        channel.setChannelId(System.currentTimeMillis()+"");
        channel.setRoomIndex(0);
        channel.setGroundId(bean.getGroundId());
        channel.setGroundName(bean.getGroundName());
        VoiceChannelManager.getInstance().createChannel(channel);
    }

    public void createChannel(String name, GroundBean bean, EMValueCallBack<EMGroup> callback) {
        if (TextUtils.equals(name, (DemoApplication.getInstance().getResources().getString(R.string.court_name)))
                || TextUtils.equals(name, (DemoApplication.getInstance().getResources().getString(R.string.default_channel_name)))) {
            callback.onError(-1, "不允许使用此社区名称");
        }
        Gson gson = new Gson();
        String des = gson.toJson(bean);
        createChannel(name, des, bean.isGround_type(), callback);
    }

    private void createChannel(String name, String des, boolean style, EMValueCallBack<EMGroup> callback) {
        EMGroupOptions options = new EMGroupOptions();
        if (style){
            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        } else {
            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
        }
        options.inviteNeedConfirm = true;
        options.maxUsers = 10000;

        EMClient.getInstance().groupManager().asyncCreateGroup(name, des, new String[]{}, "1", options, new EMValueCallBack<EMGroup>() {
            @Override
            public void onSuccess(EMGroup value) {
                callback.onSuccess(value);
            }

            @Override
            public void onError(int error, String errorMsg) {
                callback.onError(error, errorMsg);
            }
        });
    }

    public void deleteChannel(String groupId, CallBack<Object> callBack) {
        GroundManager.getInstance().deleteGroundGroupInfo(groupId);

        EaseThreadManager.getInstance().runOnIOThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(groupId);
                    LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP));
                    callBack.onSuccess(null);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    callBack.onFailed(e.getDescription());
                }
            }
        });

    }
}
