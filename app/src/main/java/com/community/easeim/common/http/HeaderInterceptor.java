/*
 * Copyright (c) 2020. Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 公共请求头 Header
 *
 * @author WeiYongZheng
 * @date 2020/6/8
 */
public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
//        Map<String, String> headerMap = NetUtils.getHeaders();
//        // 添加公共请求头
//        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
//            builder.addHeader(entry.getKey(), entry.getValue());
//        }
        // 将新的 Request 向下传递
        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
