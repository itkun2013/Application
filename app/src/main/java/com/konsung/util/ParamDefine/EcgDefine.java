package com.konsung.util.ParamDefine;

/**
 * Created by chenshuo on 2016/1/5.
 */
//ECG参数相关的定义
public class EcgDefine {
    // ECG导联系统
    public static final int ECG_3_LEAD = 0;       // 3导联
    public static final int ECG_5_LEAD = 1;       // 5导联
    public static final int ECG_12_LEAD = 2;      // 12导联

    // ECG监护模式
    public static final int ECG_MONITOR_NORMAL = 0;       // 常规界面
    //	public static final int ECG_MONITOR_HALF_SCREEN = 1;  // 半屏多导界面
    public static final int ECG_MONITOR_FULL_SCREEN = 2;  // 全屏多导界面

    // ECG导联
    public static final int ECG_LEAD_INVALID = -1;
    public static final int ECG_LEAD_I = 0;
    public static final int ECG_LEAD_II = 1;
    public static final int ECG_LEAD_III = 2;
    public static final int ECG_LEAD_AVR = 3;
    public static final int ECG_LEAD_AVL = 4;
    public static final int ECG_LEAD_AVF = 5;
    public static final int ECG_LEAD_V1 = 6;
    public static final int ECG_LEAD_V2 = 7;
    public static final int ECG_LEAD_V3 = 8;
    public static final int ECG_LEAD_V4 = 9;
    public static final int ECG_LEAD_V5 = 10;
    public static final int ECG_LEAD_V6 = 11;

    // ECG通道
    public static final int ECG_CH1 = 0;          // 通道1
    public static final int ECG_CH2 = 1;          // 通道2
    public static final int ECG_CH3 = 2;          // 通道3

    // ECG滤波方式
    public static final int ECG_FILTER_DIAGNOSIS = 0;    // 诊断方式
    public static final int ECG_FILTER_MONITOR = 1;      // 监护方式
    public static final int ECG_FILTER_SURGERY = 2;      // 手术方式

    // ECG工频滤波频率
    public static final int ECG_HUM_OFF = 0;       // 工频关
    public static final int ECG_HUM_ON = 1;        // 50Hz滤波

    // ECG增益
    public static final int ECG_GAIN_X025 = 0;     // x0.25
    public static final int ECG_GAIN_X05 = 1;      // x0.5
    public static final int ECG_GAIN_X1 = 2;       // x1
    public static final int ECG_GAIN_X2 = 3;       // x2

    // ECG起搏分析开关
    public static final int ECG_PACE_UNKNOW = 0;  // 未设置
    public static final int ECG_PACE_OFF = 1;     // 关
    public static final int ECG_PACE_ON = 2;      // 开

    // ECG心率失常类型
    public static final int ARR_NORMAL = -1;      // 无心律失常报警
    public static final int ARR_ASYSTOLE = 0;     // 心脏停搏
    public static final int ARR_VFIBVTAC = 1;     // 室颤/室速
    public static final int ARR_RONT = 2;         // R on T
    public static final int ARR_VTL2 = 3;         // 连续室性早搏
    public static final int ARR_COUPLET = 4;      // 两个连发室性早搏
    public static final int ARR_PVC = 5;          // 单个早搏
    public static final int ARR_BIGEMINY = 6;     // 二联律
    public static final int ARR_TRIGEMINY = 7;    // 三联律
    public static final int ARR_TACHY = 8;        // 室速
    public static final int ARR_BRADY = 9;        // 室缓
    public static final int ARR_PNC = 10;         // 起搏器未俘获
    public static final int ARR_PNP = 11;         // 起搏器未起搏
    public static final int ARR_MISSED_BEATS = 12;// 漏搏
    public static final int ARR_SINUS = 13;       // 窦性心律失常
    public static final int ARR_VENT = 14;        // 室性节律
    public static final int ARR_IRR = 15;         // 心律不齐

    // ECG ST分析开关
    public static final int ECG_ST_OFF = 0;     // 关
    public static final int ECG_ST_ON = 1;      // 开

    public static final String ECG_LEAD_SYSTEM = "ecg_lead_system"; // 导联设置sputil存储的key
    public static final String ECG_VELOCITY_SYSTEM = "ecg_velocity_mode"; // 波速设置sputil存储的key
    public static final String ECG_AMPLITUDE_SYSTEM = "ecg_amplitude_mode"; // 波幅设置sputil存储的key
    public static final int ECG_LEAD_DEFAULT = ECG_3_LEAD; // 默认12导联
    public static final int ECG_VELOCITY_DEFAULT = 4; // 默认25mm///s
    public static final int ECG_AMPLITUDE_DEFAULT = 2; // 默认x1
}
