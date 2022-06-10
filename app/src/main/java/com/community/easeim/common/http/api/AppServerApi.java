/*
 * Copyright (c) 2020. Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.community.easeim.imkit.domain.EaseUser;
import com.community.easeim.section.ground.bean.GroundBean;
import com.community.easeim.section.ground.bean.GroundGroupBean;
import com.community.easeim.section.ground.bean.GroundMemberBean;
import com.community.easeim.section.ground.bean.MessageBean;
import com.community.easeim.voice.VoiceChannel;
import com.community.easeim.voice.VoiceMember;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * App Server API 定义，使用 Retrofit 实现 Restful 风格。
 *
 * @author WeiYongZheng
 * @date 2020/6/8
 */
public interface AppServerApi {
//http://39.97.166.221: 阿里云正式地址
    // 本机 192.168.137.1  192.168.1.108
//    String HOST = "http://"+ EaseConstant.PREFIX_APP_SERVER+":12503";
    String HOST = "http://121.199.27.63" + ":8888";
    String BASE_PATH = "/";
    String BASE_URL = HOST + BASE_PATH;

    String RESULT_OK = "0000";

    String GET_MESSAGE_LIST = "message/getMessageList";

    /**
     * 获取消息列表
     *
     */
    @POST(GET_MESSAGE_LIST)
    Call<Result<List<MessageBean>>> getMessageList(@Body RequestBody body);


    //用户信息相关开始
    String SAVE_USER_INFO = "user/save";
    String GET_USER_INFO = "user/getUserInfo";
    String UPDATE_USER_NAME = "user/updateNickName";
    String UPDATE_USER_AVATAR = "user/updateAvatar";
    String UPDATE_USER_SIGN = "user/updateSign";
    String UPDATE_USER_GENDER = "user/updateGender";
    String UPDATE_USER_BIRTH = "user/updateBirth";

    @POST(SAVE_USER_INFO)
    Call<Result<Object>> saveUserInfo(@Body RequestBody body);

    @POST(GET_USER_INFO)
    Call<Result<EaseUser>> getUserInfo(@Body RequestBody body);

    @POST(UPDATE_USER_NAME)
    Call<Result<Object>> updateNickName(@Body RequestBody body);

    @POST(UPDATE_USER_AVATAR)
    Call<Result<Object>> updateAvatar(@Body RequestBody body);

    @POST(UPDATE_USER_SIGN)
    Call<Result<Object>> updateSign(@Body RequestBody body);

    @POST(UPDATE_USER_GENDER)
    Call<Result<Object>> updateGender(@Body RequestBody body);

    @POST(UPDATE_USER_BIRTH)
    Call<Result<Object>> updateBirth(@Body RequestBody body);
    //用户信息相关结束

    //社区相关
    String CREATE_GROUND = "ground/create";
    String GET_GROUND_LIST = "ground/getList";
    String GET_GROUND_LIST_KEY = "ground/getListByKey";
    String UPDATE_GROUND_COVER = "ground/updateGroundCover";
    String UPDATE_GROUND_NAME = "ground/updateGroundName";
    String UPDATE_GROUND_DES = "ground/updateGroundDescribe";
    String GET_GROUND_LIST_UID = "ground/getGroundListByUserId";
    String GET_GROUND_TOTAL = "ground/getGroundMemberTotal";
    String UPDATE_GROUND_TOTAL = "ground/updateGroundTotal";
    String UPDATE_GROUP_NAME = "ground/updateGroupName";
    String GROUND_DESTROY = "ground/destroyGround";
    String GROUND_LEAVE = "ground/leaveGround";
    String GET_GROUND_BY_ID = "ground/getGroundByGroundId";
    String GET_GROUND_BY_GROUP_ID = "ground/getGroundByGroupId";
    String DELETE_GROUND_GROUP_BY_GROUP_ID = "ground/deleteGroundGroupByGroupId";
    String GET_GROUND_GROUPS = "ground/getGroundGroups";
    String SAVE_GROUND_GROUP = "ground/saveGroundAndGroup";
    String SAVE_GROUND_USER = "ground/saveUserGroundInfo";

    @POST(CREATE_GROUND)
    Call<Result<Object>> createGround(@Body RequestBody body);

    @POST(GET_GROUND_LIST)
    Call<Result<List<GroundBean>>> getGroundList();

    @POST(GET_GROUND_LIST_KEY)
    Call<Result<List<GroundBean>>> getGroundListKey(@Body RequestBody body);

    @POST(UPDATE_GROUND_COVER)
    Call<Result<Object>> updateGroundCover(@Body RequestBody body);

    @POST(UPDATE_GROUND_NAME)
    Call<Result<Object>> updateGroundName(@Body RequestBody body);

    @POST(UPDATE_GROUND_DES)
    Call<Result<Object>> updateGroundDescribe(@Body RequestBody body);

    @POST(GET_GROUND_LIST_UID)
    Call<Result<List<GroundBean>>> getGroundListByUserId(@Body RequestBody body);

    @POST(GET_GROUND_TOTAL)
    Call<Result<GroundMemberBean>> getGroundMemberTotal(@Body RequestBody body);

    @POST(UPDATE_GROUND_TOTAL)
    Call<Result<Object>> updateGroundTotal(@Body RequestBody body);

    @POST(UPDATE_GROUP_NAME)
    Call<Result<Object>> updateGroupName(@Body RequestBody body);

    @POST(GROUND_DESTROY)
    Call<Result<Object>> destroyGround(@Body RequestBody body);

    @POST(GROUND_LEAVE)
    Call<Result<Object>> leaveGround(@Body RequestBody body);

    @POST(GET_GROUND_BY_ID)
    Call<Result<GroundBean>> getGroundByGroundId(@Body RequestBody body);

    @POST(GET_GROUND_BY_GROUP_ID)
    Call<Result<GroundGroupBean>> getGroundGroupByGroupId(@Body RequestBody body);

    @POST(DELETE_GROUND_GROUP_BY_GROUP_ID)
    Call<Result<Object>> deleteGroundGroupByGroupId(@Body RequestBody body);

    @POST(GET_GROUND_GROUPS)
    Call<Result<List<GroundGroupBean>>> getGroundGroups(@Body RequestBody body);

    @POST(SAVE_GROUND_GROUP)
    Call<Result<Object>> saveGroundAndGroup(@Body RequestBody body);

    @POST(SAVE_GROUND_USER)
    Call<Result<Object>> saveUserGroundInfo(@Body RequestBody body);

    // 社区和文字频道结束
    // 语音频道相关
    String CREATE_CHANNEL = "voiceChannel/createChannel";
    String GET_CHANNEL_BY_ID = "voiceChannel/getVoiceChannelById";
    String GET_CHANNEL_BY_GROUND_ID = "voiceChannel/getVoiceChannelsByGroundId";
    String GET_GROUPS_BY_CHANNEL_ID = "voiceChannel/getGroundGroupByChannelId";
    String DELETE_CHANNEL_BY_ID = "voiceChannel/deleteVoiceChannelById";

    String GET_MEMBER_BY_ID = "voiceMember/getVoiceMemberByChannelId";
    String JOIN_CHANNEL = "voiceMember/joinChannel";
    String LEAVE_CHANNEL = "voiceMember/leaveChannel";
    String MEMBER_SWITCH_AUDIO = "voiceMember/switchAudio";
    String MEMBER_MUTE_ALL_AUDIO = "voiceMember/muteAllRemoteAudio";
    String MEMBER_KICK_OFF_MEMBER = "voiceMember/kickOffMember";
    String MEMBER_SWITCH_MIC = "voiceMember/switchMic";

    @POST(CREATE_CHANNEL)
    Call<Result<Object>> createChannel(@Body RequestBody body);
    @POST(GET_CHANNEL_BY_ID)
    Call<Result<VoiceChannel>> getVoiceChannelById(@Body RequestBody body);
    @POST(GET_GROUPS_BY_CHANNEL_ID)
    Call<Result<List<GroundGroupBean>>> getGroundGroupByChannelId(@Body RequestBody body);
    @POST(GET_CHANNEL_BY_GROUND_ID)
    Call<Result<List<VoiceChannel>>> getVoiceChannelsByGroundId(@Body RequestBody body);
    @POST(DELETE_CHANNEL_BY_ID)
    Call<Result<Object>> deleteVoiceChannelById(@Body RequestBody body);
    @POST(GET_MEMBER_BY_ID)
    Call<Result<List<VoiceMember>>> getVoiceMemberByChannelId(@Body RequestBody body);
    @POST(JOIN_CHANNEL)
    Call<Result<VoiceMember>> joinChannel(@Body RequestBody body);
    @POST(LEAVE_CHANNEL)
    Call<Result<Object>> leaveChannel(@Body RequestBody body);
    @POST(MEMBER_SWITCH_AUDIO)
    Call<Result<Object>> switchAudio(@Body RequestBody body);
    @POST(MEMBER_MUTE_ALL_AUDIO)
    Call<Result<Object>> muteAllRemoteAudio(@Body RequestBody body);
    @POST(MEMBER_KICK_OFF_MEMBER)
    Call<Result<Object>> kickOffMember(@Body RequestBody body);
    @POST(MEMBER_SWITCH_MIC)
    Call<Result<Object>> switchMic(@Body RequestBody body);

    /**
     * {@link Callback} 的封装类
     * @param <T>
     */
    abstract class ResultCallback<T> implements Callback<Result<T>> {
        @Override
        public void onResponse(@Nullable Call<Result<T>> call,
                               @Nullable Response<Result<T>> response) {
            if (response != null) {
                Result<T> body = response.body();
                if (body != null) {
                    if (AppServerApi.RESULT_OK.equals(body.code)) {
                        if (body.data != null){
                            onSuccess(call, body.data);
                        } else {
                            onResultOk();
                        }
                    } else {
                        onFail(call, new Exception(body.msg));
                    }
                } else {
                    onFail(call, new Exception("系统繁忙，请稍后重试"));
                }
            } else {
                onFail(call, new Exception("系统繁忙，请稍后重试"));
            }
        }



        /**
         * 响应报文为公有内容 data 为空
         */
        public void onResultOk() {

        }
        @Override
        public void onFailure(@Nullable Call<Result<T>> call, @NonNull Throwable t) {
            onFail(call, new Exception("连接失败，请检查网络后重试"));
        }

        /**
         * 请求成功
         * @param call 请求对象
         * @param data 响应数据解析后的 Java 对象，不会为 null
         */
        protected abstract void onSuccess(Call<Result<T>> call, @NonNull T data);

        /**
         * 请求失败
         * @param call 请求对象
         * @param t 异常，不会为 null
         */
        protected abstract void onFail(Call<Result<T>> call, @NonNull Throwable t);
    }
}
