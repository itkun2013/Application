package com.konsung.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.PatientBasicInfoBean;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.UploadAllProgressDialog;
import com.konsung.util.global.GlobalNumber;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiangshicheng on 2017/4/27 0027.
 * 用户数据下载管理类
 */

public class DownloadMananger implements UploadAllProgressDialog.UpdataButtonState {

    private Context context;
    private UploadAllProgressDialog waitingDialog;
    List<PatientBean> popPatients;
    Handler handler;
    private int downloadCount = 0;
    private static final int MAXVALUE = 100;
    private static final int UPDATEVALUE = 101;

    private CompeleteRefreshListener compeleteRefreshListener;
    /**
     * 下载完成后刷新ui接口
     */
    public interface CompeleteRefreshListener {
        /**
         * 回调方法
         */
        public void refresh();
    }
    /**
     * 构造方法
     *
     * @param context  上下文引用
     * @param mHandler handler传递消息
     * @param compeleteRefreshListener 下载完成ui刷新接口
     */
    public DownloadMananger(Context context, Handler mHandler
            , CompeleteRefreshListener compeleteRefreshListener) {
        this.context = context;
        this.handler = mHandler;
        this.compeleteRefreshListener = compeleteRefreshListener;
    }

    /**
     * 请求网路数据查询
     */
    public void sendRequest() {
        boolean networkState = UiUitls.isNetworkConnected(context);
        if (!networkState) {
            Toast.makeText(context, UiUitls.getContent().getResources()
                    .getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            return;
        }
        waitingDialog = new UploadAllProgressDialog(context, this);
        waitingDialog.show();
        waitingDialog.setText(UiUitls.getString(R.string.cancel_download)
                , UiUitls.getString(R.string.data_downloading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    String ip = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                            , GlobalConstant.SERVICE_IP, GlobalConstant.IP_DEFAULT);
                    String prot = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                            , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
                    String serviceAddres = "/imms-web/data/patient/findPatientinfoByOrgId?";
                    String url = GlobalConstant.HTTP + ip + ":" + prot + serviceAddres;
                    RequestParams requestParams = new RequestParams();
                    //假机构号
                    requestParams.put("orgId", GlobalConstant.ORG_ID);
                    String sparam = requestParams.toString();
                    Log.e("sparam", "" + sparam);
                    RequestUtils.clientGet(url + sparam, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String response = new String(bytes);
                            final PatientBasicInfoBean patientBasicInfoBean = JsonUtils
                                    .toEntity(response, PatientBasicInfoBean.class);
                            if (patientBasicInfoBean != null
                                    && patientBasicInfoBean.code.equals("000")) {
                                //接口数据请求成功
                                popPatients = new ArrayList<PatientBean>();
                                List<PatientBasicInfoBean.Data> list
                                        = patientBasicInfoBean.data;
                                for (PatientBasicInfoBean.Data data : list) {
                                    popPatients.add(switchInfoToPatientBean(data));
                                }
                                if (popPatients != null && popPatients.size() > 0) {
                                    float allCount = popPatients.size();
                                    for (int j = 0; j < popPatients.size(); j++) {
                                        if (!GlobalConstant.isStopDownload) {
                                            downloadOne(j);
                                            downloadCount++;
                                            final int percentValue = (int) (downloadCount
                                                    / allCount * MAXVALUE);
                                            UiUitls.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    waitingDialog.setProgress(percentValue);
                                                    if (percentValue >= MAXVALUE) {
                                                        waitingDialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            break;
                                        }
                                    }
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            compeleteRefreshListener.refresh();
                                        }
                                    });
                                }
                            } else {
                                //接口请求数据为空
                                UiUitls.toast(context, patientBasicInfoBean.message);
                                waitingDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes
                                , final Throwable throwable) {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    //接口请求失败
                                    UiUitls.toast(context, UiUitls.getContent().getResources()
                                            .getString(R.string.load_failed));
                                    waitingDialog.dismiss();
                                }
                            });
                            Log.e("onFailure", "onFailure");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, UiUitls.getContent().getResources()
                            .getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                    Log.e("Exception", "Exception");
                }
                Looper.loop();
            }
        }).start();
    }

    /**
     * 个人信息下载
     * @param position position索引
     */
    private void downloadOne(int position) {
        PatientBean bean = popPatients.get(position);
        bean.setDownloadFlag("1");
        try {
            DBDataUtil.getPatientDao().insertOrReplace(bean);
        } catch (Exception e) {
            bean.setDownloadFlag("0");
        }
        DBDataUtil.getPatientDao().update(bean);
    }

    /**
     * 转换信息为patientBean
     * @param data 基本信息
     * @return 用户对象
     */
    private PatientBean switchInfoToPatientBean(PatientBasicInfoBean.Data data) {
        PatientBean patientBean = new PatientBean();
        patientBean.setName(data.uname);
        patientBean.setIdCard(data.cardno);
        if (!TextUtils.isEmpty(data.sex)) {
            patientBean.setSex(convertLocalSex(Integer.parseInt(data.sex)));
        }
        if (!TextUtils.isEmpty(data.birthday)) {
            patientBean.setBirthday(new Date(Long.parseLong(data.birthday)));
        }
        if (!TextUtils.isEmpty(data.patientType)) {
            patientBean.setPatient_type(Integer.parseInt(data.patientType));
        }
        //血型默认设置为不详
        patientBean.setBlood(TestDataGenerator.getBloodRawValue(GlobalNumber.FOUR_NUMBER));
        return patientBean;
    }

    @Override
    public void cancelUpload() {
        //下载取消
        GlobalConstant.isStopDownload = true;
        Log.e("isStopDownload", "" + GlobalConstant.isStopDownload);
    }

    /**
     * 转换成本地性别代码
     * @param code 下载的性别码
     * @return 性别代码
     */
    private int convertLocalSex(int code) {
        switch (code) {
            case 0: //未知
                return 9;
            case 1: //男
                return 1;
            case 2: //女
                return 0;
            case 9: //未说明
                return 9;
            default:
                return 9;
        }
    }
}
