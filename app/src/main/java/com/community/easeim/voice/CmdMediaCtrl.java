package com.community.easeim.voice;

import com.community.easeim.common.constant.DemoConstant;
import com.community.easeim.common.livedatas.LiveDataBus;
import com.community.easeim.common.utils.PreferenceManager;
import com.community.easeim.imkit.constants.EaseConstant;
import com.community.easeim.imkit.model.EaseEvent;
import com.community.easeim.section.voice.bean.ChannelCmd;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;

public class CmdMediaCtrl {
    private static final CmdMediaCtrl ourInstance = new CmdMediaCtrl();

    public static CmdMediaCtrl getInstance() {
        return ourInstance;
    }

    private Gson mGson;

    private CmdMediaCtrl() {
        if (mGson == null) mGson = new Gson();
    }

    public void cmdInviteJoinVoice(String userId, VoiceChannel channel) {
        sendChannelMsg(userId, channel, EaseConstant.CMD_INVITE);
    }

    public void cmdJoinLeave2All(String channelId) {
        sendVoiceMemberChange(channelId);
        VoiceChannelManager.getInstance().getGroundMembersByChannelId(channelId, new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> value) {
                List<String> allMembers = new ArrayList<>(value);
                VoiceChannelManager.getInstance().getChannelMembers(channelId, new EMValueCallBack<List<VoiceMember>>() {
                    @Override
                    public void onSuccess(List<VoiceMember> value) {
                        for (VoiceMember member : value) {
                            if (!allMembers.contains(member.getMemberId())) {
                                allMembers.add(member.getMemberId());
                            }
                        }

                        for (String member : allMembers) {
                            sendCmdMsg(member, channelId);
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {

                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    public void cmdJoinLeave2Contacts(String channelId, List<String> contacts) {
        sendVoiceMemberChange(channelId);
        for (String contact : contacts) {
            sendCmdMsg(contact, channelId);
        }
    }

    public void cmdMediaCtrl2All(String channelId) {
        sendVoiceMemberChange(channelId);
        VoiceChannelManager.getInstance().getChannelMembers(channelId, new EMValueCallBack<List<VoiceMember>>() {
            @Override
            public void onSuccess(List<VoiceMember> value) {
                for (VoiceMember member : value) {
                    sendCmdMsg(member.getMemberId(), channelId);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    public void sendVoiceMemberChange(String channelId) {
        EaseEvent event = EaseEvent.create(EaseConstant.VOICE_MEMBER_CHANGE, EaseEvent.TYPE.CONTACT);
        event.message = channelId;
        LiveDataBus.get().with(DemoConstant.VOICE_MEMBER_CHANGE).postValue(event);
    }

    public void sendVoiceSpeaking(String channelId, int stream) {
        EaseEvent event = EaseEvent.create(EaseConstant.VOICE_MEMBER_TALKING, EaseEvent.TYPE.CONTACT);
        event.message = channelId;
        event.arg0 = stream;
        LiveDataBus.get().with(DemoConstant.VOICE_MEMBER_TALKING).postValue(event);
    }

    public void sendVoiceSpeaking(IRtcEngineEventHandler.AudioVolumeInfo[] infos) {
        for (IRtcEngineEventHandler.AudioVolumeInfo info : infos) {
            EaseEvent event = EaseEvent.create(EaseConstant.VOICE_MEMBER_TALKING, EaseEvent.TYPE.CONTACT);
            event.message = info.channelId;
            event.arg0 = info.uid;
            event.arg1 = info.volume;
            LiveDataBus.get().with(DemoConstant.VOICE_MEMBER_TALKING).postValue(event);
        }
    }

    public void sendChannelCmd(String channelCmd) {
        EaseEvent event = EaseEvent.create(EaseConstant.CHANNEL_CMD, EaseEvent.TYPE.CONTACT);
        event.message = channelCmd;
        LiveDataBus.get().with(DemoConstant.CHANNEL_CMD).postValue(event);
    }

    private void sendCmdMsg(String id, String channelId) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);

        message.setAttribute(EaseConstant.CHANNEL_ID, channelId);
        message.setAttribute(EaseConstant.MEDIA_CTRL, "cmdMediaCtrl");

        EMCmdMessageBody body = new EMCmdMessageBody(EaseConstant.MEDIA_CTRL_ACTION);
        message.addBody(body);
        message.setTo(id);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void sendChannelMsg(String id, VoiceChannel channel, String cmd) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);

        ChannelCmd channelCmd = new ChannelCmd();
        channelCmd.setChannelId(channel.getChannelId());
        channelCmd.setChannelName(channel.getChannelName());
        channelCmd.setInviter(PreferenceManager.getInstance().getCurrentUserNick());
        channelCmd.setCmd(cmd);
        channelCmd.setGroundName(channel.getGroundName());

        message.setAttribute(EaseConstant.CHANNEL_CMD, mGson.toJson(channelCmd));

        EMCmdMessageBody body = new EMCmdMessageBody(EaseConstant.CHANNEL_ACTION);
        message.addBody(body);
        message.setTo(id);
        EMClient.getInstance().chatManager().sendMessage(message);
    }
}
