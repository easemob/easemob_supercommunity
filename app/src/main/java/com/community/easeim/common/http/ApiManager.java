/*
 * Copyright (c) 2020. Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http;

import androidx.annotation.VisibleForTesting;

import com.community.easeim.common.http.api.AppServerApi;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API 管理类，可通过此类获取 Retrofit 风格的 API，也可以直接使用 {@link OkHttpUtil}。
 *
 * @author easeMob
 * @date 2020/6/8
 */
public class ApiManager {

    public static ApiManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private OkHttpClient mHttpClient;
    private AppServerApi mAppServerApi;
    private Gson mGson;

    private ApiManager() {
        mHttpClient = OkHttpUtil.getHttpClient();
        mGson = OkHttpUtil.getGson();
    }

    /**
     * 将 {@link Map} 中的 key，value 构造成 {@link RequestBody}
     * @param params 参数/值 Map
     * @return {@link RequestBody}
     */
    @Deprecated
    public static RequestBody buildRequestBody(Map<String, String> params) {
        String json = getInstance().mGson.toJson(params);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
    }

    /**
     * 获取 {@link AppServerApi} 实例对象。
     * @return {@link AppServerApi}
     */
    public static AppServerApi getAppServerApi() {
        return getInstance().appServerApi();
    }

    /**
     * 获取 {@link RequestBodyBuilder} 对象，用于构建 {@link RequestBody}
     * @return {@link RequestBodyBuilder}
     */
    public static RequestBodyBuilder requestBodyBuilder() {
        return new RequestBodyBuilder();
    }

    /**
     * 获取全局唯一 {@link Gson} 对象
     * @return {@link Gson}
     */
    public static Gson getGson() {
        return getInstance().mGson;
    }

    /**
     * 获取 {@link AppServerApi} 对象实例。
     * @return {@link AppServerApi}
     */
    public AppServerApi appServerApi() {
        if (mAppServerApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    // baseUrl  必须指定, 并且必须以 "/" 结尾, 否则会报错:
                    // java.lang.IllegalArgumentException: baseUrl must end in /
                    .baseUrl(AppServerApi.BASE_URL)
                    .client(mHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build();
            mAppServerApi = retrofit.create(AppServerApi.class);
        }
        return mAppServerApi;
    }

    @Deprecated
    public RequestBody getRequestBody(Map<String, String> params) {
        String json = mGson.toJson(params);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
    }

    @VisibleForTesting
    OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    private static class InstanceHolder {
        private static final ApiManager INSTANCE = new ApiManager();
    }

    public static class RequestBodyBuilder {
        private Map<String, Object> mMap = new HashMap<>();

        public RequestBodyBuilder put(String key, Object value) {
            if (value == null) {
                value = "";
            }
            mMap.put(key, value);
            return this;
        }

        public RequestBody build() {
            String json = getGson().toJson(mMap);
            return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        }
    }
}
