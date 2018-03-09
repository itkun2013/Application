package com.konsung.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/**
 * 网络请求工具类
 */
public class RequestUtils {
    public static final int TIMEOUT_30S = 30000;
    public static final int TIMEOUT_5S = 5000;
    public static final int TIMEOUT_10S = 30000;
    public static final int TIMEOUT_10M = 600000;
    public static final String SUCCESS_CODE = "0";
    private static String contentType = "application/json";
    private static SyncHttpClient client = new SyncHttpClient();

    /**
     * get请求
     * @param u url字符串
     * @param callback 回调
     */
    public static synchronized void clientGet(String u, AsyncHttpResponseHandler callback) {
        client.setTimeout(TIMEOUT_10S);
        client.setMaxRetriesAndTimeout(0, TIMEOUT_10S);
        client.get(u, callback);
    }

    /**
     * post
     * @param url 字符串
     * @param params 请求参数
     * @param callback 回调函数
     */
    public static synchronized void clientPost(String url, RequestParams params
            , AsyncHttpResponseHandler callback) {
        client.setTimeout(TIMEOUT_5S);
        client.setMaxRetriesAndTimeout(0, TIMEOUT_5S);
        client.post(url, params, callback);
    }

    /**
     * post
     * @param context 上下文
     * @param stringEntity 参数请求实例
     * @param callback AsyncHttpResponseHandler 回调
     * @param url 请求地址
     */
    public static synchronized void clientPost(Context context, String url
            , StringEntity stringEntity, AsyncHttpResponseHandler callback) {
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
        client.setTimeout(TIMEOUT_10S);
        client.setMaxRetriesAndTimeout(0, TIMEOUT_10S);
        client.post(context, url, stringEntity, contentType, callback);
    }

    /**
     * post
     * @param context 上下文
     * @param params 参数请求实例
     * @param callback AsyncHttpResponseHandler 回调
     * @param url 请求地址
     */
    public static synchronized void clientPost(Context context, String url
            , RequestParams params, FileAsyncHttpResponseHandler callback) {
        client.setTimeout(TIMEOUT_10S);
        client.setMaxRetriesAndTimeout(0, TIMEOUT_10S);
        client.post(context, url, params,  callback);
    }
}