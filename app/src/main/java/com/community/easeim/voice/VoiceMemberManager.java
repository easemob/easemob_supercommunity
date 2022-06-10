package com.community.easeim.voice;

import android.util.Log;

import androidx.annotation.NonNull;

import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.community.easeim.common.utils.PreferenceManager;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;

public class VoiceMemberManager {

    private Map<String, VoiceMember> channelMemberMap = new HashMap<>();

    //是否关掉声音
    public static final String SPEAKER_OFF = "isSpeakerOff";
    //是否被管理员禁言，0-没，1-被禁言。
    public static final String MUTED_BY_ADMIN = "MutedByAdmin";
    //是否自己禁言，0-没，1-禁言。
    public static final String MUTED_BY_SELF = "MutedBySelf";

    public static final String KICK_OFF = "kick_off";

    private VoiceMemberManager() {
    }

    private static VoiceMemberManager instance;

    public static VoiceMemberManager getInstance() {
        if (instance == null) {
            instance = new VoiceMemberManager();
        }
        return instance;
    }

    //人员进入房间
    public void joinChannel(String channelId, int count, Callback<String> callback) {
        VoiceMember member = new VoiceMember();
        member.setChannelId(channelId);
//        member.setMutedByAdmin(false);
//        member.setMuteBySelf(false);
//        member.setSpeakerOff(false);
        member.setStreamId(count);
        member.setMemberId(EMClient.getInstance().getCurrentUser());
        member.setAvatar(PreferenceManager.getInstance().getCurrentUserAvatar());
        member.setName(PreferenceManager.getInstance().getCurrentUserNick());
//        member.setKickOff(false);
        channelMemberMap.remove(channelId);

        Log.e("tagqi", "joinChannel: " + member.toString());
        doJoinChannel(channelId, member, callback);
    }

    private void doJoinChannel(String channelId, VoiceMember member, Callback<String> callback) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("channelId", channelId)
                .put("memberId", member.getMemberId())
                .put("streamId", member.getStreamId())
                .put("avatar", member.getAvatar())
                .put("name", member.getName())
                .put("speakerOff", member.getSpeakerOff())
                .put("mutedByAdmin", member.getMutedByAdmin())
                .put("mutedBySelf", member.getMutedBySelf())
                .build();
        ApiManager.getAppServerApi().joinChannel(build).enqueue(new AppServerApi.ResultCallback<VoiceMember>() {
            @Override
            protected void onSuccess(Call<Result<VoiceMember>> call, @NonNull VoiceMember data) {
                channelMemberMap.put(channelId, data);
                VoiceChannelManager.getInstance().getJoinLeaveNoticeContacts(channelId, new EMValueCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        CmdMediaCtrl.getInstance().cmdJoinLeave2Contacts(channelId, value);
                        callback.onSuccess(data.getObjectId());
                        EMLog.e("joinRoom", "保存成功。objectId：" + data.getObjectId());
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callback.onError(errorMsg);
                    }
                });
            }

            @Override
            protected void onFail(Call<Result<VoiceMember>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void leaveChannel(VoiceMember mine) {
        if (!channelMemberMap.containsKey(mine.getChannelId())) {
            return;
        }

        VoiceChannelManager.getInstance().getJoinLeaveNoticeContacts(mine.getChannelId(), new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> value) {
                RequestBody build = ApiManager.requestBodyBuilder()
                        .put("id", mine.getObjectId())
                        .build();
                ApiManager.getAppServerApi().leaveChannel(build).enqueue(new AppServerApi.ResultCallback<Object>() {
                    @Override
                    protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                        channelMemberMap.remove(mine.getChannelId());
                        CmdMediaCtrl.getInstance().cmdJoinLeave2Contacts(mine.getChannelId(), value);
                    }

                    @Override
                    protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

    }

    public void startAudio(VoiceMember mine) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("id", mine.getObjectId())
                .put("mutedBySelf", false)
                .build();
        ApiManager.getAppServerApi().switchAudio(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("tagqi", "startAudio onSuccess " + data.toString());
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(mine.getChannelId());
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void stopAudio(VoiceMember mine) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("id", mine.getObjectId())
                .put("mutedBySelf", true)
                .build();
        ApiManager.getAppServerApi().switchAudio(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("tagqi", "stopAudio onSuccess: " + data.toString());
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(mine.getChannelId());
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void muteAllRemoteAudio(VoiceMember mine, boolean muted) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("id", mine.getObjectId())
                .put("muted", muted)
                .build();
        ApiManager.getAppServerApi().muteAllRemoteAudio(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("tagqi", "onSuccess: muteAllRemoteAudio");
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(mine.getChannelId());
            }

            @Override
            public void onResultOk() {
                Log.e("tagqi", "onResultOk: muteAllRemoteAudio");
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(mine.getChannelId());
                super.onResultOk();
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void kickOffMember(VoiceMember member) {
        if (!channelMemberMap.containsKey(member.getChannelId())) {
            return;
        }
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("id", member.getObjectId())
                .put("kickOff", true)
                .build();
        ApiManager.getAppServerApi().kickOffMember(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("tagqi", "onNext: kickOffMember");
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(member.getChannelId());
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void switchMic(boolean mutedByAdmin, VoiceMember member) {
        if (!channelMemberMap.containsKey(member.getChannelId())) {
            return;
        }
        RtcManager.getInstance().muteRemoteAudioStream(member.getStreamId(), mutedByAdmin);
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("id", member.getObjectId())
                .put("mutedByAdmin", mutedByAdmin)
                .build();
        ApiManager.getAppServerApi().switchMic(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("tagqi", "onNext: switchMic");
                CmdMediaCtrl.getInstance().cmdMediaCtrl2All(member.getChannelId());
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
