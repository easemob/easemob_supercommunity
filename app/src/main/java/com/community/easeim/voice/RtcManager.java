package com.community.easeim.voice;

import android.content.Context;
import android.util.Log;

import com.community.easeim.R;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcEngineConfig;
import io.agora.rtc.models.ClientRoleOptions;

public final class RtcManager {

    private final String TAG = RtcManager.class.getSimpleName();

    private static RtcManager instance;

    private Context mContext;
    private RtcEngine mRtcEngine;

    private String mChannel;
    private int mUid;
    private boolean isJoined = false;

    private final List<IRtcEngineEventHandler> handlers = new ArrayList<>();

    private RtcManager() {

    }

    public static RtcManager getInstance() {
        if (instance == null) {
            synchronized (RtcManager.class) {
                if (instance == null)
                    instance = new RtcManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        Log.d(TAG, "init() called");
        if (mRtcEngine == null) {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = mContext;
            config.mAppId = mContext.getString(R.string.app_id);
            config.mEventHandler = mEventHandler;

//            if (Config.isLeanCloud()) {
            config.mAreaCode = RtcEngineConfig.AreaCode.AREA_CODE_CN;
//            } else {
//                config.mAreaCode = RtcEngineConfig.AreaCode.AREA_CODE_GLOB;
//            }

            try {
                mRtcEngine = RtcEngine.create(config);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "init: ", e);
            }
        }

        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        mRtcEngine.enableAudio();
        mRtcEngine.enableAudioVolumeIndication(300, 3, true);
        mRtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
    }

    public void joinChannel(String channelId, Callback<String> callback) {
        if (mRtcEngine == null) {
            return;
        }

        if (isJoined) {
            for (IRtcEngineEventHandler handler : handlers) {
                handler.onJoinChannelSuccess(mChannel, mUid, 0);
            }
            return;
        }

        doJoinChannel(channelId, (int) System.currentTimeMillis() / 1000, callback);
    }

    private void doJoinChannel(String channelId, int streamId, Callback<String> callback) {
        Log.d(TAG, "joinChannel() called with: channelId = [" + channelId + "], userId = [" + streamId + "]");
        VoiceMemberManager.getInstance().joinChannel(channelId, streamId, callback);
        mRtcEngine.joinChannel(mContext.getString(R.string.token), channelId, null, streamId);
        isJoined = true;
    }

    public void leaveChannel(VoiceMember mine) {
        Log.d(TAG, "leaveChannel() called");
        if (!isJoined) {
            Log.e("tagqi", "leaveChannel: not joined");
            return;
        }
        if (mRtcEngine == null) {
            return;
        }
        VoiceMemberManager.getInstance().leaveChannel(mine);
        mRtcEngine.leaveChannel();
    }

    public void setClientRole(int role) {
        Log.d(TAG, "setClientRole() called with: role = [" + role + "]");
        if (mRtcEngine != null)
            mRtcEngine.setClientRole(role);
    }

    public void setClientRole(int role, ClientRoleOptions options) {
        Log.d(TAG, "setClientRole() called with: role = [" + role + "], options = [" + options + "]");
        if (mRtcEngine != null)
            mRtcEngine.setClientRole(role, options);
    }

    public void startAudio() {
        Log.d(TAG, "startAudio() called");
        if (mRtcEngine == null) {
            return;
        }
        mRtcEngine.enableLocalAudio(true);
    }

    public void stopAudio() {
        Log.d(TAG, "stopAudio() called");
        if (mRtcEngine == null) {
            return;
        }
        mRtcEngine.enableLocalAudio(false);
    }

    public void muteRemoteAudioStream(int uid, boolean muted) {
        Log.d(TAG, "muteRemoteAudioStream() called with: uid = [" + uid + "], muted = [" + muted + "]");
        if (mRtcEngine == null) {
            return;
        }
        int i = mRtcEngine.muteRemoteAudioStream(uid, muted);
        Log.d(TAG, "muteRemoteAudioStream() called with: i = [" + i + "]");
    }


    public void muteAllRemoteAudioStream(boolean muted) {
        Log.d(TAG, "muteAllRemoteAudioStream() called  muted = [" + muted + "]");
        if (mRtcEngine == null) {
            return;
        }
        int i = mRtcEngine.muteAllRemoteAudioStreams(muted);
        Log.d(TAG, "muteAllRemoteAudioStreams() called with: i = [" + i + "]");
    }

    public void muteLocalAudioStream(boolean muted) {
        Log.d(TAG, "muteLocalAudioStream() called with: muted = [" + muted + "]");
        if (mRtcEngine == null) {
            return;
        }
        mRtcEngine.muteLocalAudioStream(muted);
    }

    public void addHandler(IRtcEngineEventHandler handler) {
        if (mRtcEngine == null) {
            return;
        }

        handlers.add(handler);
        mRtcEngine.addHandler(handler);
    }

    public void removeHandler(IRtcEngineEventHandler handler) {
        if (mRtcEngine == null) {
            return;
        }

        handlers.remove(handler);
        mRtcEngine.removeHandler(handler);
    }

    private final IRtcEngineEventHandler mEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onWarning(int warn) {
            super.onWarning(warn);
            Log.w(TAG, "onWarning: " + warn);
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.e(TAG, "onError: " + err + " , " + RtcEngine.getErrorDescription(err));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "onJoinChannelSuccess() called with: channel = [" + channel + "], uid = [" + uid + "], elapsed = [" + elapsed + "]");
            isJoined = true;
            mChannel = channel;
            mUid = uid;

            stopAudio();
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Log.d(TAG, "onLeaveChannel() called with: stats = [" + stats + "]");
            isJoined = false;
            mChannel = null;
            mUid = 0;

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.d(TAG, "onUserJoined() called with: uid = [" + uid + "], elapsed = [" + elapsed + "]");
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            CmdMediaCtrl.getInstance().sendVoiceMemberChange(mChannel);
            Log.d(TAG, "onUserOffline() called with: uid = [" + uid + "], reason = [" + reason + "]");
        }

        @Override
        public void onLocalAudioStateChanged(int state, int error) {
            super.onLocalAudioStateChanged(state, error);
            Log.d(TAG, "onLocalAudioStateChanged() called with: state = [" + state + "], error = [" + error + "]");
        }

        @Override
        public void onAudioPublishStateChanged(String channel, int oldState, int newState, int elapseSinceLastState) {
            super.onAudioPublishStateChanged(channel, oldState, newState, elapseSinceLastState);
            Log.e("tagqi", "onAudioPublishStateChanged: ");
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
            Log.d(TAG, "onRemoteAudioStateChanged() called with: uid = [" + uid + "], state = [" + state + "], reason = [" + reason + "], elapsed = [" + elapsed + "]");
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            super.onUserMuteAudio(uid, muted);
            Log.e("tagqi", "onUserMuteAudio: ");
        }

        @Override
        public void onAudioSubscribeStateChanged(String channel, int uid, int oldState, int newState, int elapseSinceLastState) {
            super.onAudioSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState);
            CmdMediaCtrl.getInstance().sendVoiceMemberChange(mChannel);
            Log.e("tagqi", "onAudioSubscribeStateChanged: ");
        }

        @Override
        public void onActiveSpeaker(int uid) {
            super.onActiveSpeaker(uid);
            Log.e("tagqi", "onActiveSpeaker: " + uid);
//            CmdMediaCtrl.getInstance().sendVoiceSpeaking(mChannel, uid);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            for (AudioVolumeInfo speaker : speakers) {
                Log.e("tagqi", "onAudioVolumeIndication: " + speaker.uid + " " + speaker.volume);
            }
            CmdMediaCtrl.getInstance().sendVoiceSpeaking(speakers);
        }
    };
}