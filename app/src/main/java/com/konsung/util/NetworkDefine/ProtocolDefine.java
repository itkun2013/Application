package com.konsung.util.NetworkDefine;

/**
 * Created by chenshuo on 2016/1/6.
 * 网络协议相关的常量定义
 */
public class ProtocolDefine {
    // ECG配置指令,配置项的内容的定义参见EcgDefine.h
    public static final short NET_ECG_WAVE_SPEED = 0x00; // 波形速度
    public static final short NET_ECG_WAVE_GAIN = 0x01;    // 波形增益
    public static final short NET_ECG_LEAD_SYSTEM = 0x02;
    // 导联系统，0：3导，1：5导，2：12导
    public static final short NET_ECG_CALC_LEAD = 0x03;    // 计算导联
    public static final short NET_ECG_DISPLAY_MODE = 0x05;
    // 显示模式，0：正常;1:半屏多导联;2:全屏
    public static final short NET_ECG_HUMFILTER_MODE = 0x06;
    // 工频干扰抑制开关,参见枚举类型EcgHumMode
    public static final short NET_ECG_FILTER_MODE = 0x07;
    // 滤波模式 0：诊断；1：监护；2：手术
    public static final short NET_ECG_PACE_SWITCH = 0x08;
    // PACE开关，0：未设置;1:关;2：开
    public static final short NET_ECG_ST_ANALYSIS = 0x09; // ST开关, 0:关; 1:开
    public static final short NET_ECG_ARR_ANALYSIS = 0x0A;
    // ARR分析开关, 0:关; 1:开
    public static final short NET_ECG_P05mV_VALUE = 0X0B; // +0.5mV对应的波形值
    public static final short NET_ECG_N05mV_VALUE = 0X0C; // -0.5mV对应的波形值
    public static final short NET_ECG_CALIBRATION_SWITCH = 0X0D; // 心电校准开关
    public static final short NET_ECG_ISO_VALUE = 0x0E; // ISO值
    public static final short NET_ECG_ST_VALUE = 0x0F; // ST值
    public static final short NET_ECG_LEADOFF_STATUS = 0x10; // 导联脱落状态
    public static final short NET_ECG_ARR_TYPE = 0x11; // ARR类型
    public static final short NET_ECG_ST_SUPPORT = 0x12; // 是否支持ST
    public static final short NET_ECG_PVCS_SUPPORT = 0x13; // 是否支持PVCs
    public static final short NET_ECG_LEAD_SUPPORT = 0x14;
    // 0只支持3导，1支持35导,2支持3，5,12导
    public static final short NET_ECG_12LEAD_DIAG = 0x15; // 开始12导诊断分析命令
    public static final short NET_ECG_SIGNAL_SATURATED = 0x16; // 信号越界标志

    // RESP配置指令
    public static final short NET_RESP_WAVE_SPEED = 0x00; // 波形速度
    public static final short NET_RESP_WAVE_GAIN = 0x01; // 波形增益
    public static final short NET_RESP_CALC_TYPE = 0x02;
    // 计算类型，0：自动，1：手动
    public static final short NET_RESP_LEAD_TYPE = 0x03;
    // 呼吸导联，0：II导联，1：I导联
    public static final short NET_RESP_APNEA_TIME = 0x04; // 窒息报警延迟时间
    public static final short NET_RESP_APNEA_STATUS = 0x05;
    // 窒息状态，0：窒息结束，1：窒息开始

    // SPO2配置指令
    public static final short NET_SPO2_WAVE_SPEED = 0x00; // 波速
    public static final short NET_SPO2_SENSITIVE = 0x01; // 灵敏度,0高，1中，2低
    public static final short NET_SPO2_PITCH_SWITCH = 0x02;
    // 调制音开关，0为关，1为开
    public static final short NET_SPO2_SEARCHING_PULSE = 0x03; // 搜索脉搏标志
    public static final short NET_SPO2_LOW_PERFUSION = 0x04; // 弱灌注标志
    public static final short NET_SPO2_SENSOR_STATUS = 0x05;
    // 传感器脱落状态，0：正常;1:未接;2:手指脱落
    public static final short NET_SPO2_NO_PULSE = 0x06; // 无脉搏标志

    // NIBP配置指令，参见NibpDefine.h
    public static final short NET_NIBP_MODE = 0x00;    // 测量模式，0手动，1自动
    public static final short NET_NIBP_INTERVAL = 0x01;    // 间隔值，单位分钟
    public static final short NET_NIBP_RESULT = 0x02; // 测量结果
    public static final short NET_NIBP_DISPLAY_MODE = 0x03;
    public static final short NET_NIBP_CUFF = 0x04;    // 袖带压
    public static final short NET_NIBP_START_MEASURE = 0x05;
    // 0：测量，1：漏气检测，2：校准，3：复位，4：连续测量
    public static final int NIBP_MEASURE = 0;
    public static final int NIBP_AIR_LEAKAGE = 1;
    public static final int NIBP_CORRECT = 2;
    public static final int NIBP_RESTORATION = 3;
    public static final int NIBP_CONTINUOUS_MEASUREMENT = 4;
    public static final short NET_NIBP_STOP_MEASURE = 0x06; // 结束测量
    public static final short NET_NIBP_STATUS = 0x07; // 测量状态

    // TEMP配置指令
    // 探头脱落状态，最低位表示探头1,次低位表示探头2
    public static final short NET_TEMP_SENEOR = 0x00;
    // 体温类型，0：接触式；1：红外
    public static final short NET_TEMP_TYPE = 0x01;
    // 体温启动停止命令，0：启动；1：停止
    public static final short NET_TEMP_START_STOP = 0x02;

    // 设备配置指令
    public static final short NET_DEVICE_CONFIG = 0x00;
}
