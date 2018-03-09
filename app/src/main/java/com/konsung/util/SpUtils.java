package com.konsung.util;

import android.content.Context;
import android.content.SharedPreferences;

import static com.konsung.fragment.ConfigureFragment.IP;

/**
 * SharedPreferences工具类
 * @author ouyangfan
 * @version 0.0.1
 */
public class SpUtils {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ADMIN = "admin";

    /**
     * 保存布尔值
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name,
            String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存数字
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name,
            String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存字符串
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name,
            String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存小数
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param value value
     */
    public static synchronized void saveToSp(Context mContext, String name,
            String key, float value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 获取指定key的int
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized int getSpInt(Context mContext, String name,
            String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * 获取指定key的float
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized float getSpFloat(Context mContext, String
            name, String key, float defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getFloat(key, defValue);
    }

    /**
     * 获取指定key的boolean
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized boolean getSp(Context mContext, String name, String key,
            boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获取指定key的String
     * @param mContext mContext
     * @param name name
     * @param key key
     * @param defValue defValue
     * @return 返回值
     */
    public static synchronized String getSp(Context mContext, String name,
            String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 删除指定的Key
     * @param mContext 上下文
     * @param name 统一name标识
     * @param key key标识
     */
    public static synchronized void removeSp(Context mContext, String name, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(name, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
    }

    /**
     * 获取sp保存的服务器ip地址
     * @return 服务器ip地址
     */
    public static String getIp() {
        String serverIp = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, IP, GlobalConstant.IP_DEFAULT);
        return serverIp;
    }

    /**
     * 获取sp保存的服务器port地址
     * @return port地址
     */
    public static String getPort() {
        String serverIp = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG,
                GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
        return serverIp;
    }
}
