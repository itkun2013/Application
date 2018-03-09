package com.konsung.util.ParamDefine;

/**
 * Created by chenshuo on 2016/1/5.
 */
//呼吸参数相关的定义
public class RespDefine {
    // 呼吸率计算类型。
    public static final int RESP_CALC_AUTO = 0;        // 自动
    public static final int RESP_CALC_MANUAL = 1;      // 手动

    // RESP导联
    public static final int RESP_LEAD_II = 0;
    public static final int RESP_LEAD_I = 1;

    // RESP增益
    public static final int RESP_GAIN_X025 = 0;     // x0.25
    public static final int RESP_GAIN_X05 = 1;      // x0.5
    public static final int RESP_GAIN_X1 = 2;       // x1
    public static final int RESP_GAIN_X2 = 3;       // x2
    public static final int RESP_GAIN_X4 = 4;       // x2

    // RESP窒息报警延迟时间
    public static final int RESP_APNEA_DELAY_10S = 0;       // 10s
    public static final int RESP_APNEA_DELAY_15S = 1;       // 15s
    public static final int RESP_APNEA_DELAY_20S = 2;       // 20s
    public static final int RESP_APNEA_DELAY_25S = 3;       // 25s
    public static final int RESP_APNEA_DELAY_30S = 4;       // 30s
    public static final int RESP_APNEA_DELAY_35S = 5;       // 35s
    public static final int RESP_APNEA_DELAY_40S = 6;       // 40s
}
