package com.konsung.util.global;

/**
 * Created by xiangshicheng on 2017/3/23 0023.
 * 心电参考值
 */

public class Ecg {
    //心电参考上限值
    public static final float ecgHigh = 100;
    //心电参考下限值
    public static final float ecgLow = 60;

    //采样频率/秒
    public static final String SAMPLE = "500";
    //+0.5mv对应的数值
    public static final String P05 = "2150";
    //-0.5mv对应的数值
    public static final String N05 = "1946";
    //波形持续时间
    public static final String DURATION = "5";
}
