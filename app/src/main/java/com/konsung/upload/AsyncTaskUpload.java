package com.konsung.upload;

import android.content.Context;
import android.os.AsyncTask;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;
import com.konsung.util.global.EcgRemoteInfoSaveModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiangshicheng on 2017/10/23 0023.
 * 数据全部上传
 */

public class AsyncTaskUpload extends AsyncTask<String, Void, Void> {

    //上下文
    private Context context;
    //测量记录集合
    private List<MeasureDataBean> measureDataBeanList;
    //记录当天日期
    private String currentDate;
    //日期格式规范
    private SimpleDateFormat dateFormat;
    //上传当天所有测量记录标识
    private final String uploadToday = "UploadToday";
    //上传之前所有测量记录标识
    private final String uploadBefore = "UploadBefore";
    private OnCompeleteUploadListener onCompeleteUploadListener;

    /**
     * 构造函数
     * @param context 上下文引用
     * @param onCompeleteUploadListener 上传完成回调接口
     */
    public AsyncTaskUpload(Context context, OnCompeleteUploadListener onCompeleteUploadListener) {
        this.context = context;
        this.onCompeleteUploadListener = onCompeleteUploadListener;
    }

    /**
     * 执行前操作
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //时间格式
        dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        //当前日期
        currentDate = dateFormat.format(new Date());
        measureDataBeanList = new ArrayList<>();
    }

    /**
     * 执行中操作
     */
    @Override
    protected Void doInBackground(String... params) {
        switch (params[0]) {
            case uploadBefore:
                //上传除今天外的所有数据
                uploadBeforeOrToday(false);
                break;
            case uploadToday:
                //上传当天所有数据
                uploadBeforeOrToday(true);
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * 执行完成后操作
     */
    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
        if (onCompeleteUploadListener != null) {
            //后台执行完后的回调(UI线程)
            onCompeleteUploadListener.onCompelete();
        }
    }

    /**
     * 取消操作
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    /**
     * 上传之前数据(除当前记录外的以前数据)
     * @param isToday 判断是否上传今天还是之前的数据标识
     */
    private void uploadBeforeOrToday(boolean isToday) {
        if (!GlobalConstant.isUploadStop) {
            List<PatientBean> patientBeen = DBDataUtil.getPatientDao().loadAll();
            for (int j = 0; j < patientBeen.size(); j++) {
                //内层嵌套一次判断用于页面销毁后刚好程序走到这里引发的异常崩溃
                if (!GlobalConstant.isUploadStop) {
                    measureDataBeanList.clear();
                    measureDataBeanList.addAll(DBDataUtil.getMeasures(patientBeen.get(j)
                            .getIdCard()));
                    int measureSize = measureDataBeanList.size();
                    if (measureSize > 0) {
                        for (int i = 0; i < measureSize; i++) {
                            MeasureDataBean measureDataBean = measureDataBeanList.get(i);
                            if (isToday) {
                                if (currentDate.equals(dateFormat.format(measureDataBean
                                        .getCreatetime())) && !measureDataBean.getUploadFlag()) {
                                    uploadMeasureData(measureDataBean, patientBeen, j);
                                }
                            } else {
                                if (!currentDate.equals(dateFormat.format(measureDataBean
                                        .getCreatetime())) && !measureDataBean.getUploadFlag()) {
                                    uploadMeasureData(measureDataBean, patientBeen, j);
                                }
                            }
                        }
                    }
                } else {
                    //直接跳出循环
                    break;
                }
            }
        }
    }

    /**
     * 上传数据
     * @param measureDataBean 测量记录
     * @param patientBeen 用户列表
     * @param position 位置
     */
    private void uploadMeasureData(MeasureDataBean measureDataBean, List<PatientBean> patientBeen
            , int position) {
        //上传至多应用管理平台
        new UploadData(context).uploadMeasureDataMore(measureDataBean
                , patientBeen.get(position));
        boolean isUpload = EcgRemoteInfoSaveModule.getInstance()
                .isUploadSuccess;
        measureDataBean.setUploadFlag(isUpload);
        DBDataUtil.getMeasureDao().update(measureDataBean);
    }

    /**
     * 上传完成回调接口
     */
    public interface OnCompeleteUploadListener {
        /**
         * 上传完成回调方法
         */
        public void onCompelete();
    }
}
