package com.konsung.util;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.upload.UploadCloudMeasureData;
import com.konsung.upload.UploadMeasureData;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by YYX on 2016/6/20 0020.
 * 上传工具类
 */
public class UploadUtil {

    /**
     * 上传数据至中心端软件
     *
     * @param patient 病人
     * @param bean    体检数据
     */
    public static boolean uploadServer(PatientBean patient,
            MeasureDataBean bean) {
        boolean uploadPhysicalMeasure = false;
        try {
            uploadPhysicalMeasure = UploadMeasureData
                    .newInstance().uploadPhysicalMeasure(patient, bean);
            if (uploadPhysicalMeasure) {
                bean.setUploadFlag(true);
                DBDataUtil.saveMeasure(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        return uploadPhysicalMeasure;
    }

    /**
     * 上传数据至康尚云平台
     *
     * @param patient 病人
     * @param bean    体检数据
     */
    public static boolean uploadCloud(PatientBean patient,
            MeasureDataBean bean) {
        return UploadCloudMeasureData.newInstance().uploadPhysicalMeasure(patient, bean);
    }

}
