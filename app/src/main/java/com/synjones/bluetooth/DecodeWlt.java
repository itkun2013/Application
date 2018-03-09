package com.synjones.bluetooth;
/**
 * 身份证图片解析类
 */
public class DecodeWlt {
    /***
     * 身份证图片字符转换为bitmap图片
     * @param bmpPath bmpPath
     * @param wltPath wltPath
     * @return int
     */
    public native int Wlt2Bmp(String wltPath, String bmpPath);

    static {
        System.loadLibrary("DecodeWlt");
    }

}
