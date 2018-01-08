package com.example.xxxloli.zshmerchant.http;


import com.interfaceconfig.Config;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by xxxloli on 2017/11/21.
 */

public class OkHttp {
    protected static OkHttpClient httpClient;
    protected static OkHttpCallback okHttpCallback;

    public static void Call(String url, Map<String, ? extends Object> params, OkHttpCallback.Impl impl){
        httpClient = new OkHttpClient();
        okHttpCallback=new OkHttpCallback(impl);
        MultipartBody.Builder requestBuilder = null;
        if (params != null) {
            requestBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            Set<String> keys = params.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                final String key = (String) iterator.next();
                requestBuilder.addFormDataPart(key, String.valueOf(params.get(key)));
            }
        }
        String tag = url;
        if (url.startsWith(Config.LOCAL_HOST)) {
            tag = url.substring(Config.LOCAL_HOST.length());
        } else if (url.startsWith(Config.HOST)) {
            tag = url.substring(Config.HOST.length());
        }
        Request.Builder request = new Request.Builder()
                .tag(tag).url(url);
        if (requestBuilder == null)
            request.post(RequestBody.create(MediaType.parse("application/json"), "1"));
        else request.post(requestBuilder.build());
        newCall(request.build());
    }

    protected static void newCall(Request request) {
        httpClient.newCall(request).enqueue(okHttpCallback);
    }
}
