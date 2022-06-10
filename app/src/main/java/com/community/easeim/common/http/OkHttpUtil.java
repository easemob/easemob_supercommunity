/*
 * Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http;

import android.os.Handler;
import android.os.Looper;

import com.community.easeim.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求工具类
 *
 * @author easeMob
 * @date 2020/6/8
 */
public class OkHttpUtil {

    private OkHttpClient okHttpClient;
    private Handler handler;
    private Gson gson;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private OkHttpUtil() {
        //添加cookie
        OkHttpClient.Builder builder = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });
        builder.addInterceptor(new HeaderInterceptor());
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new MockDataInterceptor());
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        okHttpClient =  builder.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        handler = new Handler(Looper.getMainLooper());
        gson = new Gson();
    }

    /**
     * 单例模式创建OkHttpUtil
     *
     * @return mInstance
     */
    public static OkHttpUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * get 同步请求
     *
     * @return response
     */
    private Response _getDataSync(String url) {
        Request request = new Request.Builder()
                .get()//默认get,可省略
                .url(url)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * get 同步请求
     *
     * @return 字符串
     */
    private String _getDataString(String url) throws IOException {
        Response response = _getDataSync(url);
        if (response.body() != null) {
            return response.body().string();
        }
        return null;
    }

    /**
     * get 异步请求
     */
    private void _getDataAsync(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * post 同步请求
     *
     * @return response
     */
    private Response _postDataSync(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        return okHttpClient.newCall(request).execute();

    }

    /**
     * post 同步请求
     *
     * @return 字符串
     */
    private String _postDataString(String url, Param... params) throws IOException {
        Response response = _postDataSync(url, params);
        return response.body().string();
    }

    /**
     * post 异步请求
     */
    private void _postDataAsync(String url, final ResultCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);

    }


    /**
     * post 异步请求
     */
    private void _postJsonDataAsync(String url, final ResultCallback callback, String params) {
        Request request = buildPostJsonRequest(url,params);
        deliveryResult(callback, request);

    }

    /**
     * post 异步请求
     */

    private void _postDataAsync(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * post 同步文件上传
     */
    private Response _postDataFileSync(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return okHttpClient.newCall(request).execute();
    }

    private Response _postDataFileSync(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return okHttpClient.newCall(request).execute();
    }

    private Response _postDataFileSync(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的多文件上传
     */
    private void _postDataFileAsync(String url, final ResultCallback callback, File[] files, String[] fileKeys, Param... params) {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     */
    private void _postDataFileAsync(String url, ResultCallback callback, File file, String fileKey) {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url      接口地址
     * @param callback callback
     * @param file     file
     * @param fileKey  fileKey
     * @param params   params
     */
    private void _postDataFileAsync(String url, ResultCallback callback, File file, String fileKey, Param... params) {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 同步 post上传json对象
     *
     * @param url     url;
     * @param jsonStr json 字符串
     * @return 字符串
     * @throws IOException
     */
    private String _postJsonDataSync(String url, String jsonStr) throws IOException {
        String result = null;
        MediaType JSON = MediaType.parse("text/html;charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = okHttpClient.newCall(request).execute();
        result = response.body().string();
        return result;
    }

    /**
     * 异步 post上传json对象
     *
     * @param url      url
     * @param jsonStr  json数据字符串
     * @param callback callback
     */
    private void _postJsonDataAsync(String url, String jsonStr, final ResultCallback callback) {
        MediaType JSON = MediaType.parse("text/html;charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        deliveryResult(callback, request);
    }

    /**
     * post 异步上传图片
     *
     * @param url      url
     * @param callback callback
     * @param file     file
     * @param fileKey  fileKey
     * @param map      map
     */
    private void _upLoadImg(String url, final ResultCallback callback, File file, String fileKey, Map<String, String> map) {
        Param[] params = map2Params(map);
        getInstance()._postDataFileAsync(url, callback, file, fileKey, params);
    }

    /**
     * 创建 MultipartFormRequest
     *
     * @param url      接口地址
     * @param files    files
     * @param fileKeys file keys
     * @param params   params
     * @return MultipartFormRequest
     */

    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private Param[] validateParam(Param[] params) {
        if (params == null) {
            return new Param[0];
        } else {
            return params;
        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 异步下载文件
     */
    private void _downloadFileAsync(final String url, final String destFileDir,
                                    final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] b = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(b)) != -1) {
                        fos.write(b, 0, len);
                    }
                    fos.flush();
                    // 如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (Exception e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }
            }

        });
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1,
                path.length());
    }

    //***************************************************************** 公共方法***************************************************

    public static OkHttpClient getHttpClient() {
        return getInstance().okHttpClient;
    }

    public static Gson getGson() {
        return getInstance().gson;
    }

    /**
     * get 同步请求
     *
     * @param url 接口地址
     * @return response
     */
    public static Response getDataSync(String url) {
        return getInstance()._getDataSync(url);
    }

    /**
     * get 同步请求
     *
     * @param url 接口地址
     * @return 字符串
     * @throws IOException
     */
    public static String getDataString(String url) throws IOException {
        return getInstance()._getDataString(url);
    }

    /**
     * get 异步请求
     *
     * @param url 接口地址
     */
    public static void getDataAsync(String url, final ResultCallback callback) {
        getInstance()._getDataAsync(url, callback);
    }

    /**
     * post 同步请求
     *
     * @param url   url
     * @param param param
     * @return response
     * @throws IOException
     */
    public static Response postDataSync(String url, Param... param) throws IOException {
        return getInstance()._postDataSync(url, param);
    }

    /**
     * post 同步请求
     *
     * @param url    url
     * @param params param
     * @return 字符串
     * @throws IOException
     */
    public static String postDataString(String url, Param... params) throws IOException {
        return getInstance()._postDataString(url, params);
    }

    /**
     * post 异步请求
     *
     * @param url      url
     * @param callback 异步回调
     * @param params   params
     */
    public static void postDataAsync(String url, final ResultCallback callback, Param... params) {
        getInstance()._postDataAsync(url, callback, params);
    }

    /**
     * post 异步请求
     *
     * @param url      url
     * @param callback 异步回调
     * @param params   params
     */
    public static void postJsonDataAsync(String url, final ResultCallback callback, String params) {
        getInstance()._postJsonDataAsync(url,callback,params);
    }

    /**
     * post map集合 异步请求
     *
     * @param url      url
     * @param callback callback
     * @param params   params
     */
    public static void postDataAsync(String url, final ResultCallback callback, Map<String, String> params) {
        getInstance()._postDataAsync(url, callback, params);
    }

    /**
     * post 异步上传图片
     *
     * @param url      url
     * @param callback callback
     * @param file     file
     * @param fileKey  fileKey
     * @param map      map
     */
    public static void upLoadImg(String url, final ResultCallback callback, File file, String fileKey, Map<String, String> map) {
        getInstance()._upLoadImg(url, callback, file, fileKey, map);
    }

    /**
     * map 转换为 Param
     *
     * @param params map
     * @return params
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }



    /**
     * 创建 PostRequest
     *
     * @param url    url
     * @param params params
     * @return request
     */
    private Request buildPostJsonRequest(String url, String params) {
        if (params == null) {
//            params = new Param[0];
        }
        RequestBody requestBody = null;
        if (params != null) {
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params);
        }

        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 创建 PostRequest
     *
     * @param url    url
     * @param params params
     * @return request
     */
    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }

        RequestBody requestBody = builder.build();

        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    /**
     * 抽象类，用于请求成功后的回调
     *
     * @param <T>
     */
    public static abstract class ResultCallback<T> {
        //这是请求数据的返回类型，包含常见的（Bean，List等）
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        /**
         * 通过反射想要的返回类型
         *
         * @param subclass
         * @return
         */
        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        //失败回调
        public abstract void onError(Request request, Exception e);

        //成功的回调
        public abstract void onResponse(T response);
    }

    private Map<String, String> mSessions = new HashMap<>();

    /**
     * 请求回调处理方法并传递返回值
     *
     * @param callback Map类型请求参数
     * @param request  Request请求
     */
    private void deliveryResult(final ResultCallback callback, final Request request) {

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String string = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(string, callback);
                    } else {
                        Object o = gson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }
            }

        });
    }

    /**
     * 处理请求失败的回调信息方法
     *
     * @param e        错误信息
     * @param callback 回调类
     */
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }

    /**
     * 处理请求成功的回调信息方法
     *
     * @param object   服务器响应信息
     * @param callback 回调类
     */
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    private static class InstanceHolder {
        private static final OkHttpUtil INSTANCE = new OkHttpUtil();
    }
}
