/*
 * Copyright (c) 2020. Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.community.easeim.DemoApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 模拟网络数据的 OkHttp Interceptor
 *
 * @author WeiYongZheng
 * @date 2020/6/8
 */
class MockDataInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        HttpUrl url = request.url();
        String host = url.host();
        int port = url.port();
        String path = url.encodedPath();
        Map<String, String> queryNameValues = getQueryNameValues(url);
        String jsonString = mockJson(method, host, port, path, queryNameValues);
        if (jsonString != null) {
            return buildResponse(request, jsonString);
        }
        return chain.proceed(request);
    }

    private Map<String, String> getQueryNameValues(HttpUrl url) {
        Set<String> queryNames = url.queryParameterNames();
        Map<String, String> nameValueMap = new HashMap<>();
        for (String queryName : queryNames) {
            String queryValue = url.queryParameter(queryName);
            // 还有个方法 url.queryParameterValues(queryName); 返回一个列表
            // url 中, 其实一个参数名可以有多个参数值, 例如:
            // http://host/?a=apple&a=apricot
            // 服务端应该也会有对应的处理, 这个以前倒是没有听说过
            if (queryValue != null) {
                nameValueMap.put(queryName, queryValue);
            }
        }
        return nameValueMap;
    }

    private String mockJson(String method, String host, int port,
                            String requestPath, Map<String, String> queryNameValues) {
//        if (TextUtils.equals(requestPath, AppServerApi.BASE_PATH + AppServerApi.ORG_DEPT_LIST)) {
//            // /AppServer/v1/org/dept/list
//            return getMockJsonFromAssets("mock/org_dept_list.json");
//        } else if (TextUtils.equals(requestPath, AppServerApi.BASE_PATH + AppServerApi.ORG_DEPT_ADD)) {
//            // /AppServer/v1/org/dept/add
//            return getMockJsonFromAssets("mock/post_success.json");
//        } else if ("GET".equals(method) && requestPath.startsWith(AppServerApi.BASE_PATH + "org/dept/info")) {
//            // /AppServer/v1/org/dept/info/{deptId}
//            return getMockJsonFromAssets("mock/org_dept_10001.json");
//        } else if (requestPath.startsWith(AppServerApi.BASE_PATH + "org/dept/search/10088")) {
//            // /AppServer/v1/org/dept/search/{orgId}
//            return getMockJsonFromAssets("mock/post_error.json");
//        } else if (requestPath.equals(AppServerApi.BASE_PATH + AppServerApi.MEMBER_LIST)) {
//            return getMockJsonFromAssets("mock/member_list.json");
//        }
        return null;
    }

    private String getMockJsonFromAssets(String fileName) {
        AssetManager assets = DemoApplication.getInstance().getAssets();
        InputStream is = null;
        try {
            is = assets.open(fileName);
            return readStringFromInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String readStringFromInputStream(InputStream is) throws IOException {
        InputStreamReader inputStreamReader = null;
        StringBuilder builder = null;
        inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private Response buildResponse(Request request, String content) {
        if (TextUtils.isEmpty(content)) {
            return new Response.Builder()
                    .protocol(Protocol.HTTP_1_0)
                    .request(request)
                    .code(500)
                    .message("Internal Error")
                    .body(ResponseBody.create(MediaType.parse("text/html"), "Internal Error"))
                    .build();
        } else {
            return new Response.Builder()
                    .protocol(Protocol.HTTP_1_0)
                    .request(request)
                    .code(200)
                    .message("OK")
                    .addHeader("content-type", "application/json")
                    .body(ResponseBody.create(MediaType.parse("application/json"), content))
                    .build();
        }
    }
}
