package com.konsung.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Json操作工具类
 * <p/>
 * Created by 匡国华 on 2015/11/3 0003.
 */
public class JsonUtils {
    public static <T> T toEntity1(String str, Class<T> clz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(str, clz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T toEntity(String str, Class<T> clz) {
        Gson gson = new Gson();
        return gson.fromJson(str, clz);
    }


    public static <T> T toEntity(String str, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }

    public static String toJsonString(Object obj) {
        Gson g = new Gson();
        return g.toJson(obj);
    }

    public static <T> List<T> toList(String jsonString, Type type) {
        Gson g = new Gson();
        return g.fromJson(jsonString, type);
    }

    public static boolean isEmpty(String cha) {
        if (cha == null || "".equals(cha)) {
            return true;
        }

        return false;
    }

    public static boolean isNotEmpty(String cha) {
        return !isEmpty(cha);
    }

    public static boolean isValidIP(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\." +
                "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pattern = Pattern.compile(ip);

        Matcher matcher = pattern.matcher(ipAddress);

        return matcher.matches();

    }
    /**
     * 通过域名解析来判断域名是否有效（耗时操作）
     * @param host 域名
     * @return 是否有效
     */
    public static boolean analyseDomain(String host){
        boolean isValid = true;
        String ipAddress = "";
        InetAddress returnStr = null;
        try {
            returnStr = java.net.InetAddress.getByName(host);
            ipAddress = returnStr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            //未知主机，域名解析失败
            isValid = false;
        }
        //域名解析成功
        return isValid;
    }
}