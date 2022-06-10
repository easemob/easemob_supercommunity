package com.community.easeim;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.community.easeim.common.http.ApiManager;
import com.community.easeim.common.http.api.AppServerApi;
import com.community.easeim.common.http.api.Result;
import com.hyphenate.EMValueCallBack;

import okhttp3.RequestBody;
import retrofit2.Call;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo(new EMValueCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });
    }

    //用户信息相关 开始
    public void saveUserInfo(EMValueCallBack<Boolean> callBack){
        RequestBody build = ApiManager.requestBodyBuilder()
                .put("chatId", "mua_0gr1sso1z86q3nnltwfhpvbhy9qx")
                .put("nickname", "1234")
                .put("avatar", "userBean.getAvatar()")
                .put("gender", "userBean.getGender()")
                .put("sign", "userBean.getSign()")
                .put("birth", "userBean.getBirth()")
                .build();
        ApiManager.getAppServerApi().testInfo(build).enqueue(new AppServerApi.ResultCallback<Object>() {
            @Override
            protected void onSuccess(Call<Result<Object>> call, @NonNull Object data) {
                callBack.onSuccess(true);
            }

            @Override
            protected void onFail(Call<Result<Object>> call, @NonNull Throwable t) {
                callBack.onError(-1,t.getMessage());
            }
        });
    }
}