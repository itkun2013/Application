package com.konsung.upload;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.logic.WsHandler;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.handler.timeout.TimeoutException;

/**
 * Created by YYX on 2016/5/30 0030.
 * 上传体检数据的方法
 */
public class UploadMeasureData implements BaseUpload {
    //监听机制的回调
    private StatuCallback statuCallback = null;
    //记录上传成功的测量数据的uuid
    private List<String> upLoadSuccessUuid = new ArrayList<>();

    ;

    @Override
    public void uploadData(Object bean) {
        uploadPhysical((PatientBean) bean);
    }
    /** 根据测量记录上传病人
     * @param bean 测量记录
     * @return
     */
    @Override
    public boolean uploadPhysicalMeasure(PatientBean bean, MeasureDataBean
            data) throws IOException, XmlPullParserException {
        // 命名空间
        String nameSpace = "http://webservice/";

        // 调用的方法名称
        String methodName = "WebVillageHealth";

        // EndPoint
        String endPoint = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.SERVICE_IP,
                GlobalConstant.IP_DEFAULT)
                + ":" + SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.IP_PROT, GlobalConstant
                .PORT_DEFAULT) + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.SERVER_ADDRESS,
                GlobalConstant.SERVER_FIX_ADDRESS);
        //拼接上传字段
        String checkData = DBDataUtil.getXmlData(bean, data);
        //上传数据到服务器
        return WsHandler.UpLoadToServer(checkData, nameSpace, methodName, endPoint);
    }

    /**
     * 上传数据到云服务
     * @param bean 病人信息bean
     * @param data 测量信息bean
     * @throws IOException io异常
     * @throws XmlPullParserException xml解析异常
     */
    public boolean uploadDataToCloudServer(PatientBean bean
            , MeasureDataBean data) throws IOException, XmlPullParserException{
        // 命名空间
        String nameSpace = "http://webservice/";

        // 调用的方法名称
        String methodName = "WebVillageHealth";

        // EndPoint
        String endPoint = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CLOUD_IP,
                GlobalConstant.CIP)
                + ":" + SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.CLOUD_IP_PORT, GlobalConstant
                .CIP_PORT) + GlobalConstant.CLOUD_FIX_ADDRESS;
        //拼接上传字段
        String checkData = DBDataUtil.getXmlData(bean, data);
        //上传数据到服务器
        return WsHandler.UpLoadToServer(checkData, nameSpace, methodName, endPoint);
    }

    /**
     * 设置回调方法的类
     *
     * @param l
     */
    public void setStatuCallback(StatuCallback l) {
        this.statuCallback = l;
    }

    /**
     * 私有构造方法
     */
    private UploadMeasureData() {

    }

    /**
     * 返回当前用户的实例的方法
     *
     * @return
     */
    public static UploadMeasureData newInstance() {
        return new UploadMeasureData();
    }

    /**
     * 上传时成功或者失败的回调
     */
    public interface StatuCallback {
        /**
         * 上传成功的方法
         */
        public void onSuccess();

        /**
         * 上传失败的方法
         * @param msg 消息
         */
        public void onFail(String msg);

        /**
         * 链接访问器超时
         */
        public void onConncetServerTimeOut();

        /**
         * 链接服务器失败的原因
         */
        public void onConncetServerFail(String e);

        /**
         * 当前病人的测量值为空执行的方法
         */
        public void onMeasureIsNull();
    }

    /**
     * 上传提交数据的方法
     *
     * @param bean 病人
     */
    private void uploadPhysical(PatientBean bean) {
        //获取当前病人的测量值
        List<MeasureDataBean> measures = DBDataUtil.getMeasures(bean
                .getIdCard());
        //用于记录服务器上传失败的方法
        boolean isServerFail = false;
        //用于记录服务器上传失败的方法
        boolean isServerTimeOut = false;
        //判断当前病人是否有测量记录
        if (measures.size() <= 0) {
            //如果没有测量记录，执行回调方法
            if (statuCallback != null) {
                statuCallback.onMeasureIsNull();
            }
            return;
        }
        //清空当前存储测量的值
        upLoadSuccessUuid.clear();
        //如果用户有测量数据
        for (int i = 0; i < measures.size(); i++) {
            //上传数据到服务器
            boolean isUpLoad = false;
            try {
                isUpLoad = uploadPhysicalMeasure(bean, measures.get(i));
            } catch (Exception e) {

                e.printStackTrace();
                //判断是否连接服务器超时
                if (e instanceof TimeoutException) {
                    if (statuCallback != null) {
                        isServerTimeOut = true;
                        statuCallback.onConncetServerTimeOut();
                    }

                } else {
                    //上传失败的方法
                    if (statuCallback != null) {
                        isServerFail = true;
                        statuCallback.onConncetServerFail(e.toString());
                    }
                }
                break;
            }
            //根据判断是否上传成功
            if (isUpLoad) {
                MeasureDataBean dataBean = measures.get(i);
                dataBean.setUploadFlag(true);
                DBDataUtil.getMeasureDao().update(dataBean);
                upLoadSuccessUuid.add(measures.get(i).getUuid());
            }
        }
        //判断的是否上传成功,上传的测量条数等于上传成功的条数
        if (upLoadSuccessUuid.size() == measures.size()) {
            if (statuCallback != null) {
                statuCallback.onSuccess();
            }
        } else {
            //拼接上传失败的字符串
            String str = UiUitls.getString(R.string.totil_count) + measures
                    .size() + UiUitls.getString(R.string
                    .util) + GlobalConstant.LINE + UiUitls.getString(R.
                    string.updata_sussess)
                    + upLoadSuccessUuid.size() + UiUitls.getString(R.string.util) + GlobalConstant.LINE +
                    UiUitls.getString(R.string.updata_fail) + (measures.size() - upLoadSuccessUuid.size())
                    + UiUitls.getString(R.string.util);
            if (statuCallback != null) {
                if (!isServerFail && !isServerTimeOut) {
                    statuCallback.onFail(str);
                }
            }
        }
    }
}
