package com.community.easeim.common.leancloud;

import androidx.annotation.NonNull;

import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.community.easeim.imkit.domain.EaseUser;
import com.hyphenate.EMValueCallBack;

import okhttp3.RequestBody;
import retrofit2.Call;

public class LeanCloudManager {

    private LeanCloudManager(){}
    private static LeanCloudManager instance;
    public static LeanCloudManager getInstance(){
        if (instance == null) {
            instance = new LeanCloudManager();
        }
        return instance;
    }

    //用户信息相关 开始
    public void saveUserInfo(EaseUser userBean,EMValueCallBack<Boolean> callBack){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("username", userBean.getUsername())
                .put("nickname", userBean.getNickname())
                .put("avatar", userBean.getAvatar())
                .put("gender", userBean.getGender())
                .put("sign", userBean.getSign())
                .put("birth", userBean.getBirth())
                .put("age", userBean.getAge())
                .build();
        ApiManager.getAppServerApi().saveUserInfo(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }

    public void getUserInfo(String uId, EMValueCallBack<EaseUser> callBack){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", uId).build();
        ApiManager.getAppServerApi().getUserInfo(build).enqueue(new AppServerApi.ResultCallback<EaseUser>() {
            @Override
            protected void onSuccess(Call<Result<EaseUser>> call, @NonNull EaseUser data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onResultOk() {
                callBack.onSuccess(null);
                super.onResultOk();
            }

            @Override
            protected void onFail(Call<Result<EaseUser>> call, @NonNull Throwable t) {

            }
        });
    }

    /**
     * 修改昵称
     * @param objId
     * @param nickName
     */
    public void updateUserInfo(String objId,String nickName){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", objId)
                .put("nickName", nickName).build();
        ApiManager.getAppServerApi().updateNickName(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }


    /**
     * 修改头像
     * @param objId
     * @param avatar
     */
    public void updateUserAvatar(String objId,String avatar){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", objId)
                .put("avatar", avatar).build();
        ApiManager.getAppServerApi().updateAvatar(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }

    /**
     * 修改birth
     * @param objId
     * @param birth
     */
    public void updateUserBirth(String objId,String birth,String age){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", objId)
                .put("birth", birth)
                .put("age", age).build();
        ApiManager.getAppServerApi().updateBirth(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }

    /**
     * 修改个性签名
     * @param objId
     * @param sign
     */
    public void updateUserSign(String objId,String sign){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", objId)
                .put("sign", sign)
                .build();
        ApiManager.getAppServerApi().updateSign(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }

    /**
     * 修改性别
     * @param objId
     * @param gender
     */
    public void updateUserGender(String objId,int gender){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("userId", objId)
                .put("gender", gender)
                .build();
        ApiManager.getAppServerApi().updateGender(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
//                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
//                callBack.onError(-1,t.getMessage());
            }
        });
    }
    //用户信息相关 end
}
