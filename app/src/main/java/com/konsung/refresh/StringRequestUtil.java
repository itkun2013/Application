package com.konsung.refresh;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 自定义volley的请求类
 */
public class StringRequestUtil extends StringRequest {
    public StringRequestUtil(int method,
                             String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener)
    {
        super(method, url, listener, errorListener);
    }

    public StringRequestUtil(String url,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener)
    {
        super(url, listener, errorListener);
    }
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // TODO Auto-generated method stub
        String str = null;
        try {
            str = new String(response.data,"utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
    }
}
