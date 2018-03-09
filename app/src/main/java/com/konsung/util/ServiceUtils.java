package com.konsung.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.service.AIDLServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiangshicheng on 2017/9/6 0006.
 * 用于管理公共服务
 */

public class ServiceUtils {

    /**
     * 数据发送接口
     */
    public interface OnMessageSendListener {

        /**
         * 发送参数状态
         * @param name name
         * @param version version
         */
        public void sendParaStatus(String name, String version);

        /**
         * 发送波形数据
         * @param param 参数
         * @param bytes 心电值
         */
        public void sendWave(int param, byte[] bytes);

        /**
         * 发送趋势数据
         * @param param 参数
         * @param value 趋势值
         */
        public void sendTrend(int param, int value);

        /**
         * 发送配置数据
         * @param param 参数
         * @param value 值
         */
        public void sendConfig(int param, int value);

        /**
         * @param idcard 身份证
         * @param name 姓名
         * @param sex 性别
         * @param type 类型
         * @param pic 图片
         * @param address 地址
         */
        public void sendPersonalDetail(String name, String idcard, int sex, int type
                , String pic, String address);

        /**
         * 发送12导联结果包方法
         * @param bytes 字节数组
         */
        public void send12LeadDiaResult(byte[] bytes);
        /**
         * 断开连接后的回调
         */
        public void sendUnConnectMessageSend();
    }

    //数据发送接口
    private static OnMessageSendListener onMessageSendListener;
    //快检页listener
    private static OnMessageSendListener onMessageSendListenerQuick;

    //服务
    public static AIDLServer aidlServer;
    //测量bean
    public static MeasureDataBean dataBean = null;
    //导联数
    private static final int ECG_NUM = 12;
    /**
     * 设置数据发送监听
     * @param listener 接口
     */
    public static void setOnMessageSendListener(OnMessageSendListener listener) {
        onMessageSendListener = listener;
        if (listener != null) {
            dataBean = getTodayMeasureRecord();
        }
    }

    /**
     * 设置数据发送监听
     * @param listener 接口
     */
    public static void setOnMessageSendListenerQuick(OnMessageSendListener listener) {
        onMessageSendListenerQuick = listener;
        if (listener != null) {
            dataBean = getTodayMeasureRecord();
        }
    }

    /**
     * 绑定服务
     * @param context 上下文引用
     */
    public static void bindService(Context context) {
        // intent的action为康尚aidl服务器
        Intent mIntent = new Intent("com.konsung.aidlServer");
        // 一启动程序就绑定aidl service服务
        // 保证service只运行一次，一直在后台运行
        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
        if (mIntent != null) {
            context.startService(mIntent);
            context.bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解除service的绑定
     * @param context 上下文引用
     * 调用绑定服务方法后在不用的时候必须要解绑服务，不然，服务会一直存在于后台线程，会耗cpu资源
     */
    public static void unBindService(Context context) {
        context.unbindService(serviceConnection);
    }

    //服务连接接口
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendParaStatus(String name, String version) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.sendParaStatus(name, version);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.sendParaStatus(name, version);
                    }
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.sendWave(param, bytes);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.sendWave(param, bytes);
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.sendTrend(param, value);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.sendTrend(param, value);
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.sendConfig(param, value);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.sendConfig(param, value);
                    }
                }

                @Override
                public void sendPersonalDetail(String name, String idcard, int sex, int type
                        , String pic, String address) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.sendPersonalDetail(name, idcard, sex, type, pic
                                , address);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.sendPersonalDetail(name, idcard, sex, type, pic
                                , address);
                    }
                }

                @Override
                public void send12LeadDiaResult(byte[] bytes) {
                    if (onMessageSendListener != null) {
                        onMessageSendListener.send12LeadDiaResult(bytes);
                    }
                    if (onMessageSendListenerQuick != null) {
                        onMessageSendListenerQuick.send12LeadDiaResult(bytes);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (onMessageSendListener != null) {
                onMessageSendListener.sendUnConnectMessageSend();
            }
            if (onMessageSendListenerQuick != null) {
                onMessageSendListenerQuick.sendUnConnectMessageSend();
            }
        }
    };

    /**
     * 获取今天的测量记录
     * @return 今天的测量记录
     */
    public static MeasureDataBean getTodayMeasureRecord() {
        Date date = new Date();
        //UUID
        String idcard = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        List<PatientBean> patientByIdCard = DBDataUtil.getPatientByIdCard(idcard);
        //会员卡
        String memberShipCard = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.MEMBER_CARD, "");
        SimpleDateFormat dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        String shortDate = dateFormat.format(date);
        List<MeasureDataBean> dataBeanList = null;
        if (!TextUtils.isEmpty(idcard)) {
            dataBeanList = DBDataUtil.getMeasures(idcard);
        } else if (!TextUtils.isEmpty(memberShipCard)) {
            dataBeanList = DBDataUtil.getMeasuresByMembership(memberShipCard);
        }
        int paramValue = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.PARAM_CONFIGS
                , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
        //查询今天是否已经有测量记录
        if (dataBeanList != null && dataBeanList.size() > 0) {
            MeasureDataBean bean = dataBeanList.get(dataBeanList.size() - 1);
            if (shortDate.equals(dateFormat.format(bean.getCreatetime()))
                    && !bean.getUploadFlag()) {
                bean.setParamValue(paramValue);
                MeasureValueCompareUtil.setGlobalValueInit(bean);
                GlobalConstant.todayMeasureRecord = bean;
                return GlobalConstant.todayMeasureRecord;
            }
        }
        //没有则新建
        GlobalConstant.todayMeasureRecord = new MeasureDataBean();
        //关联病人表
        if (patientByIdCard.size() > 0) {
            GlobalConstant.todayMeasureRecord.setPatientId(patientByIdCard.get(0).getId());
        }
        //UUID保存
        GlobalConstant.todayMeasureRecord.setIdcard(idcard);
        //会员卡号保存
        GlobalConstant.todayMeasureRecord.setMemberShipCard(memberShipCard);
        GlobalConstant.todayMeasureRecord.setParamValue(paramValue);
        GlobalConstant.todayMeasureRecord.setDoctor(GlobalConstant.EMP_NAME);
        //全局值初始化置空
        MeasureValueCompareUtil.setGlobalValueNull();
        return GlobalConstant.todayMeasureRecord;
    }

    /**
     * 保存到数据库
     */
    public static void saveToDb2() {
        if (UiUitls.checkFileIsFull(Environment.getExternalStorageDirectory())) {
            UiUitls.toast(UiUitls.getContent(), R.string.storage_limits);
            return;
        }
        // 如果数据库中该表含有该ID，就更新数据，如果没有就创建一行数据
        MeasureDataBeanDao dao = DBDataUtil.getMeasureDao();
        //如果重新测量了数据，就更新设置未上传
        dataBean.setUploadFlag(false);
        dataBean.setDoctor(GlobalConstant.EMP_NAME);
        dao.insertOrReplace(dataBean);
        GlobalConstant.CREATE_MEASURE ++;
    }

    /**
     *设置血糖方式
     * @param btnFlag 血糖方式
     */
    public static void setGluStyle(String btnFlag) {
        dataBean.setGluStyle(btnFlag);
    }

    /**
     * 保存趋势数据
     * @param param 参数标识
     * @param value 参数值
     */
    public static void saveTrend(int param, int value) {
        dataBean.setTrendValue(param, value);
    }

    /**
     * 保存波形数据
     * @param param 参数
     * @param value 值
     * @throws RemoteException 异常
     */
    public static void savedWave(int param, String value) throws RemoteException {
        dataBean.set_ecgWave(param, value);
    }

    /**
     * 保存12诊断结果
     * @param str 诊断结果值
     */
    public static void saveEcgDiagnoseResult(String str) {
        dataBean.setEcgDiagnoseResult(str);
    }

    /**
     * 保存BMI
     * @param bmi bmi值
     * @param weight 体重值
     * @param height 身高值
     */
    public static void saveBmiValue(String bmi, String height, String weight) {
        dataBean.setBmi(bmi);
        dataBean.setHeight(height);
        dataBean.setWeight(weight);
    }

    /**
     * 获取当前病人的测量数据
     * @return 测量bean
     */
    public static MeasureDataBean getMeausreDataBean() {
        return dataBean;
    }

    /**
     * 设置ecg波形已经更新
     */
    public static void setECGUpdate() {
        for (int i = 1; i <= ECG_NUM; i++) {
            dataBean.setWaveStatues(i, true);
        }
    }

    /**
     * 数据库保存血糖测量状态信息
     * @param state 测量时状态
     */
    public static void saveBloodGluState(String state) {
        dataBean.setGluStyle(state);
    }

    /**
     * 设置测量bean
     * @param measureDataBean 测量bean
     */
    public static void setMeasureDataBean(MeasureDataBean measureDataBean) {
        dataBean = measureDataBean;
    }

    /**
     * 置空心电波形数据，数据过大，导致卡顿
     */
    public static void resetEcgWaveData() {
        if (dataBean != null) {
            dataBean.reset();
        }
    }
}
