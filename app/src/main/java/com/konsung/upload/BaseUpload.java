package com.konsung.upload;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by 杨远雄 on 2016/5/30 0030.
 * 档案上传服务的接口
 */
public interface BaseUpload {
    /**
     * 上传的方法
     * @param bean  传入的bean
     */
    public void uploadData(Object bean);

    /**
     * 根据上传传入的病人和测量记录进行上传
     * @param bean  病人
     * @param data  体检数据
     * @throws IOException IO异常
     * @throws XmlPullParserException xml解析异常
     * @return 是否上传成功
     */
    public boolean uploadPhysicalMeasure(PatientBean bean
            , MeasureDataBean data) throws IOException, XmlPullParserException;

}
