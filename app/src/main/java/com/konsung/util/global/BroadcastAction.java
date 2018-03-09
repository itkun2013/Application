package com.konsung.util.global;

import com.pantum.mobileprint.PantumPrint;

/**
 * 广播Action
 * @author yuchunhui
 **/
public class BroadcastAction {
    //更换病人
    public static final String ACTION_PATIENT_CHANGE = "com.konsung.patientChange.receiver";
    //病人修改
    public static final String ACTION_PATIENT_MODIFY = "com.konsung.modifyPatient.receiver";
    //病人退出
    public static final String ACTION_PATIENT_DELETE = "com.konsung.deletePatient.receiver";

    //标题修改
    public static final String ACTION_FRAGMENT_CHANGE = "com.konsung.fragmentChange.receiver";

    //标题修改，Intent 数据标记
    public static final String EXTRA_FRAGMENT_CHANGE = "fragmentChange";

    /**
     * 奔图打印机权限Action
     * 获取到权限以后，会发送该广播
     */
    public static final String ACTION_PANTUM_PERMISSION = PantumPrint.ACTION_USB_PERMISSION;

    /**
     * 热敏快捷打印机权限Action
     * 获取到权限以后，会发送该广播
     */
    public static final String ACTION_QUICK_PRINTER_PERMISSION = "com.konsung.quickprinter" +
            ".permission";
}
