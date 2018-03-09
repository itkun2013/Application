package com.konsung.upload;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.logic.WsHandler;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * Created by YYX on 2016/6/7 0007.
 * 上传到云平台的类
 */
public class UploadCloudMeasureData implements BaseUpload {
    private static UploadCloudMeasureData instance = null;
    @Override
    public void uploadData(Object bean) {
        uploadPhysical((PatientBean) bean);
    }

    /**
     * 返回当前用户的实例的方法
     *
     * @return
     */
    public static UploadCloudMeasureData newInstance() {
        if (instance == null) {
            instance = new UploadCloudMeasureData();
        }
        return instance;
    }

    /** 根据测量记录上传病人
     * @param bean 测量记录
     * @param data 测量信息
     * @return
     */
    public boolean uploadPhysicalMeasure(PatientBean bean, MeasureDataBean data) {
        // 命名空间
        String nameSpace = "http://webservice/";

        // 调用的方法名称
        String methodName = "WebVillageHealth";

        // EndPoint
        String endPoint = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CLOUD_IP,
                GlobalConstant.CIP) + ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CLOUD_IP_PORT,
                GlobalConstant.CIP_PORT) + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CLOUD_ADDRESS,
                GlobalConstant.CLOUD_FIX_ADDRESS);
        //拼接上传字段
        String checkData = DBDataUtil.getXmlData(bean, data);
        try {
            return WsHandler.UpLoadToServer(checkData, nameSpace, methodName, endPoint);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        return false;
    }
    /**
     * /**
     * 上传提交数据的方法
     *
     * @param bean 病人
     */
    private void uploadPhysical(PatientBean bean) {
        //获取当前病人的测量值
        List<MeasureDataBean> measures = DBDataUtil.getMeasures(bean
                .getIdCard());
        //判断当前病人是否有测量记录
        if (measures.size() <= 0) {
            return;
        }
        //循环上传到康尚云服务器
        for (int i = 0; i < measures.size(); i++) {
            uploadPhysicalMeasure(bean, measures.get(i));
        }
    }
}
