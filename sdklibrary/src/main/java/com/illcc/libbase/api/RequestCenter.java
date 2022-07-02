package com.illcc.libbase.api;

import android.text.TextUtils;

import com.illcc.libnet.okhttp.CommonOkHttpClient;
import com.illcc.libnet.okhttp.listener.DisposeDataHandle;
import com.illcc.libnet.okhttp.listener.DisposeDataListener;
import com.illcc.libnet.okhttp.request.CommonRequest;
import com.illcc.libnet.okhttp.request.RequestParams;

import java.io.File;

public class RequestCenter {

    public static class HttpConstants {
        public static String ROOT_URL;
    }

    //根据参数发送所有post请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        String token = "";
        String version = "";
        if (params != null) {
            version = params.urlParams.get("version");
            token = params.urlParams.get("token");
            if (!TextUtils.isEmpty(version)) {
                params.put("version", version);
            }
        }
        if (!TextUtils.isEmpty(token)) {
            RequestParams head = new RequestParams();
            head.put("token", token);
            CommonOkHttpClient.get(CommonRequest.
                            createGetRequest(HttpConstants.ROOT_URL + url, params, head),
                    new DisposeDataHandle(listener, clazz));
        } else {
            CommonOkHttpClient.get(CommonRequest.
                            createGetRequest(HttpConstants.ROOT_URL + url, params),
                    new DisposeDataHandle(listener, clazz));
        }
    }

    public static void postRequest(String url, RequestParams params, DisposeDataListener listener,
                                   Class<?> clazz) {
        String token = "";
        String version = "";
        if (params != null) {
            version = params.urlParams.get("version");
            token = params.urlParams.get("token");
            if (!TextUtils.isEmpty(version)) {
                params.put("version", version);
            }
        }
        if (!TextUtils.isEmpty(token)) {
            RequestParams head = new RequestParams();
            head.put("token", token);
            CommonOkHttpClient.get(CommonRequest.
                    createPostRequest(HttpConstants.ROOT_URL + url, params, head), new DisposeDataHandle(listener, clazz));
        } else {
            CommonOkHttpClient.get(CommonRequest.
                    createPostRequest(HttpConstants.ROOT_URL + url, params), new DisposeDataHandle(listener, clazz));
        }
    }


    private static void creatFileRequest(File file, String url, RequestParams params,
                                         DisposeDataListener listener, Class<?> clazz) {
        String token = "";
        String version = "";
        if (params != null) {
            version = String.valueOf(params.fileParams.get("version"));
            token = String.valueOf(params.fileParams.get("token"));
            if (!TextUtils.isEmpty(version)) {
                params.put("version", version);
            }
        }
        if (!TextUtils.isEmpty(token)) {
            RequestParams head = new RequestParams();
            head.put("token", token);
            CommonOkHttpClient.get(CommonRequest.
                    creatFileRequest(file, HttpConstants.ROOT_URL + url, params, head), new DisposeDataHandle(listener, clazz));
        } else {
            CommonOkHttpClient.get(CommonRequest.
                    creatFileRequest(file, HttpConstants.ROOT_URL + url, params, null), new DisposeDataHandle(listener, clazz));
        }
    }

}
