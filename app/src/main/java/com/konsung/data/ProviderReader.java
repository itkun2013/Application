package com.konsung.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.activity.MyApplication;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * 内容提供读取者
 * 主要用于读取健康档案中的数据
 */
public class ProviderReader {

    /**
     * 读取病人信息
     * @return 病人bean
     */
    public static PatientBean readPatient() {

        String currentIdCard = SpUtils.getSp(MyApplication.getGlobalContext(), GlobalConstant
                .APP_CONFIG, "idcard", "");
        List<PatientBean> patients = DBDataUtil.getPatientByIdCard(currentIdCard);
        if (patients.size() > 0) {
            return patients.get(0);
        }
        return new PatientBean();
    }

    /**
     * 读取病人最近十次测量数据
     * @param idcard 身份证
     * @return 测量数据
     * @throws SQLException SQL异常
     */
    public static List<MeasureDataBean> readMeasureData(String idcard) throws SQLException {
        //暂时先从数据库读取
        MeasureDataBeanDao dataBeanDao = DBDataUtil.getMeasureDao();
        List<MeasureDataBean> measureDataBeanList = dataBeanDao.queryBuilder()
                .where(MeasureDataBeanDao.Properties.Idcard.eq(idcard))
                .limit(10)
                .orderDesc(MeasureDataBeanDao.Properties.MeasureTime)
                .list();
        return measureDataBeanList;
    }

    /**
     * 获取数据配置项-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 32位
     */
    public static int getDeviceConfig(Activity context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_DEVICE_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 获取界面配置项-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getFragmentDisplayConfig(Activity context) {
        //获取配置项
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_MEASURE_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }
    /**
     * 获取尿常规测量项配置参数-从厂家维护中获取
     * @param context 上下文
     * @return 配置项 4位
     */
    public static int getUrtConfig(Activity context) {
        //获取配置项
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.AUTHORITY_URT_CONFIG);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int config = cursor.getInt(cursor.getColumnIndex(GlobalConstant.CONFIG));
            return config;
        }
        return GlobalConstant.DEVICE_CONFIG;
    }

    /**
     * 读取病人信息
     * @param context 上下文
     * @return 病人bean
     */
    public static PatientBean readCurrentPatient(Context context) {
        //获取配置项
        Uri uri = Uri.parse(GlobalConstant.QUICK_CURRENT_PATIENT);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            PatientBean bean;
            if (cursor.moveToNext()) {
                String patientDetail = cursor.getString(cursor.getColumnIndex(GlobalConstant
                        .CONFIG));
                if (patientDetail.equals("")) {
                    bean = new PatientBean();
                } else {
                    bean = new Gson().fromJson(patientDetail, PatientBean.class);
                }
            } else {
                Log.e("JustRush", "111");

                bean = new PatientBean();
            }
            cursor.close();
            return bean;
        } else {
            Log.e("JustRush", "222");

            return new PatientBean();
        }
    }
}
