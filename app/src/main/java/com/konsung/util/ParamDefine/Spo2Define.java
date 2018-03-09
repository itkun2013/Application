package com.konsung.util.ParamDefine;

/**
 * Created by chenshuo on 2016/1/5.
 */
//血氧参数相关的定义
public class Spo2Define {
    // 脉搏调制音
    public static final int SPO2_PITCH_TONE_OFF = 0; // 关
    public static final int SPO2_PITCH_TONE_ON = 1;  // 开

    // 灵敏度
    public static final int SPO2_SENSITIVITY_HIGH = 0; // 高
    public static final int SPO2_SENSITIVITY_MIDDLE = 1; // 中
    public static final int SPO2_SENSITIVITY_LOW = 2; // 低

    // 传感器脱落状态，0：正常;1:未接;2:手指脱落
    public static final int SPO2_SENSOR_NORMAL = 0; // 正常
    public static final int SPO2_NO_SENSOR = 1; // 未接
    public static final int SPO2_SENSOR_OFF = 2; // 脱落
}
