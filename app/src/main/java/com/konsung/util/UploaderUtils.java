package com.konsung.util;

/**
 * Created by 匡国华 on 2016/1/18 0018.
 */
public class UploaderUtils {
    public static final String UPLOAD_READY = "";
    public static final String UPLOAD_DONE = "1";
    public static final String DOWNLOAD_READY = "";
    public static final String DOWNLOAD_DONE = "1";

    /**
     * 将null转换成空字符串
     *
     * @param v
     * @return
     */
    public static String parseNull(String v) {
        if (v == null) {
            return "";
        }

        return v;
    }

    /**
     * 将字典值还原成
     *
     * @param value
     * @param scheme
     * @return
     */
    public static String decodeConcept(String value, String scheme, String
            defaultValue) {
        if (JsonUtils.isEmpty(value)) {
            return defaultValue;
        }

        return value.replace(scheme, "");
    }

}
