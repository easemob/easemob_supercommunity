package com.community.easeim.voice;

import android.util.Log;

import androidx.annotation.NonNull;

import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import okhttp3.RequestBody;
import retrofit2.Call;

public class VoiceChannelManager {

    private volatile static VoiceChannelManager instance;

    private VoiceChannelManager() {
    }

    private final IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {
    };

    public synchronized static VoiceChannelManager getInstance() {
        if (instance == null) {
            synchronized (VoiceChannelManager.class) {
                if (instance == null)
                    instance = new VoiceChannelManager();
            }
        }
        return instance;
    }

    /**
     * 社区和语音频道的关系表：
     * 关系id（自动生成）  社区id  channel id   房间名称（渠道名称）  分类名称（默认为空）  index (1 随便交流，0 社区创建的其它语音渠道)
     */
    public void createChannel(VoiceChannel channel) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("owner_id", channel.getOwner())
                .put("ground_id", channel.getGroundId())
                .put("ground_name", channel.getGroundName())
                .put("channel_id", channel.getChannelId())
                .put("channel_name", channel.getChannelName())
                .put("channel_index", channel.getRoomIndex())
                .build();
        ApiManager.getAppServerApi().createChannel(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("VoiceChannelManager", "onSuccess: createChannel");
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                    t.printStackTrace();
            }
        });
    }

    public void getChannelById(String id, Callback<VoiceChannel> callback) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("channelId", id)
                .build();
        ApiManager.getAppServerApi().getVoiceChannelById(build).enqueue(new AppServerApi.ResultCallback<VoiceChannel>() {
            @Override
            protected void onSuccess(Call<Result<VoiceChannel>> call, @NonNull VoiceChannel data) {
                callback.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<VoiceChannel>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getChannelMembers(String channelId, EMValueCallBack<List<VoiceMember>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("channelId", channelId)
                .build();
        ApiManager.getAppServerApi().getVoiceMemberByChannelId(build).enqueue(new AppServerApi.ResultCallback<List<VoiceMember>>() {
            @Override
            protected void onSuccess(Call<Result<List<VoiceMember>>> call, @NonNull List<VoiceMember> data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<List<VoiceMember>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //加入频道或离开频道时需要通知的对象列表
    public void getJoinLeaveNoticeContacts(String channelId,EMValueCallBack<List<String>> callBack) {
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
                        callBack.onSuccess(allMembers);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error,errorMsg);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                callBack.onError(error,errorMsg);
            }
        });
    }

    //根据voice channelId查询社区下所有成员
    public void getGroundMembersByChannelId(String channelId, EMValueCallBack<List<String>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("channelId", channelId)
                .build();
        ApiManager.getAppServerApi().getGroundGroupByChannelId(build).enqueue(new AppServerApi.ResultCallback<List<GroundGroupBean>>() {
            @Override
            protected void onSuccess(Call<Result<List<GroundGroupBean>>> call, @NonNull List<GroundGroupBean> data) {
                List<String> allList = new ArrayList<>();
                for (GroundGroupBean groundGroupBean : data) {
                    try {
                        EMGroup group = EMClient.getInstance().groupManager().getGroup(groundGroupBean.getGroupId());
                        allList.add(group.getOwner());
                        allList.addAll(group.getAdminList());
                        allList.addAll(group.getMembers());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                callBack.onSuccess(allList);
            }

            @Override
            protected void onFail(Call<Result<List<GroundGroupBean>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 根据社区ID查询语音频道
     */
    public void getVoiceChannelListByGroundId(String groundId, EMValueCallBack<List<VoiceChannel>> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("groundId", groundId)
                .build();
        ApiManager.getAppServerApi().getVoiceChannelsByGroundId(build).enqueue(new AppServerApi.ResultCallback<List<VoiceChannel>>() {
            @Override
            protected void onSuccess(Call<Result<List<VoiceChannel>>> call, @NonNull List<VoiceChannel> data) {
                callBack.onSuccess(data);
            }

            @Override
            protected void onFail(Call<Result<List<VoiceChannel>>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    /**
     * 删除一条社区和群的关联记录
     * @param channelId
     */
    public void deleteVoiceChannel(String channelId,EMValueCallBack<Boolean> callBack) {
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("channelId", channelId)
                .build();
        ApiManager.getAppServerApi().deleteVoiceChannelById(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                Log.e("VoiceChannelManager", "onSuccess: deleteVoiceChannel");
                callBack.onSuccess(true);
            }

            @Override
            public void onResultOk() {
                super.onResultOk();
                callBack.onSuccess(true);
                Log.e("VoiceChannelManager", "onResultOk: deleteVoiceChannel");
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                t.printStackTrace();
                callBack.onSuccess(false);
            }


        });
    }

}
