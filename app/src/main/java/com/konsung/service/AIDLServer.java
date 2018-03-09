package com.konsung.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.konsung.R;
import com.konsung.fragment.AppFragment;
import com.konsung.netty.EchoServer;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;



/**
 * aidl server端
 * server端为app中AIDLServer类,client端为各个子参数的Activity
 * 在增加设本类内容时需要注意本类的变量计,具体参考注释内容
 * 本类包含AIDL数据传递,以及直接进行数据存储
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class AIDLServer extends Service {
    private static final String TAG = "AIDLServer";
    // aidl Binder
    /*private AIDLServerBinder serverBinder;*/
    Message message = null;

    // 将趋势数据存储进List,用于数据缓存
    // 保存进List集合的原因是连续数据需要过滤
    // 如果是点测数据值则不需要加入集合进行过滤,而是直接使用即可
    private HashMap<Integer, Integer> status;
    private HashMap<Integer, Integer> trends;
    private HashMap<Integer, Integer> ecgConfig;
    private HashMap<Integer, Integer> spo2Config;
    private HashMap<Integer, Integer> nibpConfig;
    private HashMap<Integer, Integer> tempConfig;
    private HashMap<Integer, Integer> irtempConfig;

    private byte[] spo2Wave = null;
    private byte[] irtempWave = null;
    private byte[] ecgIIWave = null;
    private byte[] ecgIWave = null;
    private byte[] ecgIIIWave = null;
    private byte[] ecgAVRWave = null;
    private byte[] ecgAVLWave = null;
    private byte[] ecgAVFWave = null;
    private byte[] ecgV1Wave = null;
    private byte[] ecgV2Wave = null;
    private byte[] ecgV3Wave = null;
    private byte[] ecgV4Wave = null;
    private byte[] ecgV5Wave = null;
    private byte[] ecgV6Wave = null;

    private byte[] leadDiaResult = null;

    private boolean spo2Used = false;
    private boolean irtempUsed = false;
    private boolean ecgIIUsed = false;
    private boolean ecgIUsed = false;
    private boolean ecgIIIUsed = false;
    private boolean ecgAVRUsed = false;
    private boolean ecgAVLUsed = false;
    private boolean ecgAVFUsed = false;
    private boolean ecgV1Used = false;
    private boolean ecgV2Used = false;
    private boolean ecgV3Used = false;
    private boolean ecgV4Used = false;
    private boolean ecgV5Used = false;
    private boolean ecgV6Used = false;

    private SendMSGToFragment sendMsg;
    private MsgBinder msgBinder;
    private final int flagValue = 0x05;

    /**
     * 设置接口
     * @param obj 接口对象
     */
    public void setSendMSGToFragment(SendMSGToFragment obj) {
        sendMsg = obj;
    }

    /**
     * 接口
     */
    public interface SendMSGToFragment {
        /**
         * 发送状态值
         * @param name 名称
         * @param version 版本
         */
        public void sendParaStatus(String name, String version);

        /**
         * 发送波形数据
         * @param param 类型
         * @param bytes 字节数组
         */
        public void sendWave(int param, byte[] bytes);

        /**
         * 发送趋势值
         * @param param 类型
         * @param value 数值
         */
        public void sendTrend(int param, int value);

        /**
         * 发送配置信息
         * @param param 类型
         * @param value 数值
         */
        public void sendConfig(int param, int value);

        /**
         * 发送个人信息
         * @param type 类型
         * @param address 地址
         * @param idcard 身份证号
         * @param name 姓名
         * @param pic 头像
         * @param sex 性别
         */
        public void sendPersonalDetail(String name, String idcard, int sex
                , int type, String pic , String address);

        /**
         * 发送12导联结果包方法
         * @param bytes 结果数组
         */
        public void send12LeadDiaResult(byte[] bytes);

    }

    /**
     * 构造器
     */
    public AIDLServer() {}

    /*
     * Handler 处理数据
     * 使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GlobalConstant.PARA_STATUS:
                        status.put(msg.arg1, msg.arg2);
                        Bundle paraStatusBundle = msg.getData();
                        byte[] paraBoardName = paraStatusBundle.getByteArray("paraBoardName");
                        byte[] paraBoardVersion
                                = paraStatusBundle.getByteArray("paraBoardVersion");
                        String boardName = new String(paraBoardName, "UTF-8");
                        String boardVersion = new String(paraBoardVersion, "UTF-8");
                        //                        if (sendMsg != null)
                        //                        {
                        //                            sendMsg.sendParaStatus
                        // (boardName, boardVersion);
                        //                        }

                        // 把KSM5的版本号作为多参模块版本号
                        if (boardName.equals(UiUitls.getString(R.string.ksm5))) {
                            SpUtils.saveToSp(getApplicationContext(), "app_config"
                                    , "paraBoardName", boardName);
                            SpUtils.saveToSp(getApplicationContext()
                                    , "app_config", "paraBoardVersion", boardVersion);
                            //复位
                            if (msg.arg1 == 0 && msg.arg2 == 1) {
                                UiUitls.initSysConfig();
                            }
                        }
                        break;
                    case GlobalConstant.NET_TREND:
                        if (sendMsg != null) {
                            sendMsg.sendTrend(msg.arg1, msg.arg2);
                        }
                        trends.put(msg.arg1, msg.arg2);
                        break;
                    case GlobalConstant.NET_WAVE://波形数据
                        Bundle data = msg.getData();
                        if (data.containsKey(String.valueOf(KParamType.SPO2_WAVE))) {
                            spo2Wave = data.getByteArray(String.valueOf(KParamType.SPO2_WAVE));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.SPO2_WAVE, spo2Wave);
                            }
                            spo2Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_II))) {
                            ecgIIWave = data.getByteArray(String.valueOf(KParamType.ECG_II));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_II, ecgIIWave);
                            }
                            /*sentToWave(2, ecgIIWave);*/
                            ecgIIUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_I))) {
                            ecgIWave = data.getByteArray(String.valueOf(KParamType.ECG_I));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_I, ecgIWave);
                            }
                            ecgIUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_III))) {
                            ecgIIIWave = data.getByteArray(String.valueOf(KParamType.ECG_III));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_III, ecgIIIWave);
                            }
                            ecgIIIUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_AVR))) {
                            ecgAVRWave = data.getByteArray(String.valueOf(KParamType.ECG_AVR));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_AVR, ecgAVRWave);
                            }
                            ecgAVRUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_AVL))) {
                            ecgAVLWave = data.getByteArray(String.valueOf(KParamType.ECG_AVL));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_AVL, ecgAVLWave);
                            }
                            ecgAVLUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_AVF))) {
                            ecgAVFWave = data.getByteArray(String.valueOf(KParamType.ECG_AVF));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_AVF, ecgAVFWave);
                            }
                            ecgAVFUsed = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V1))) {
                            ecgV1Wave = data.getByteArray(String.valueOf(KParamType.ECG_V1));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V1, ecgV1Wave);
                            }
                            ecgV1Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V2))) {
                            ecgV2Wave = data.getByteArray(String.valueOf(KParamType.ECG_V2));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V2, ecgV2Wave);
                            }
                            ecgV2Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V3))) {
                            ecgV3Wave = data.getByteArray(String.valueOf(KParamType.ECG_V3));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V3, ecgV3Wave);
                            }
                            ecgV3Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V4))) {
                            ecgV4Wave = data.getByteArray(String.valueOf(KParamType.ECG_V4));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V4, ecgV4Wave);
                            }
                            ecgV4Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V5))) {
                            ecgV5Wave = data.getByteArray(String.valueOf(KParamType.ECG_V5));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V5, ecgV5Wave);
                            }
                            ecgV5Used = false;
                        } else if (data.containsKey(String.valueOf(KParamType.ECG_V6))) {
                            ecgV6Wave = data.getByteArray(String.valueOf(KParamType.ECG_V6));
                            if (sendMsg != null) {
                                sendMsg.sendWave(KParamType.ECG_V6, ecgV6Wave);
                            }
                            ecgV6Used = false;
                        }
                        break;
                    case GlobalConstant.NET_NIBP_CONFIG:
                        nibpConfig.put(msg.arg1, msg.arg2);
                        if (sendMsg != null) {
                            sendMsg.sendConfig(msg.arg1, msg.arg2);
                        }
                        break;
                    case GlobalConstant.NET_SPO2_CONFIG:
                        spo2Config.put(msg.arg1, msg.arg2);
                        if (msg.arg1 == flagValue) {
                            GlobalConstant.LEFF_OFF = msg.arg2;
                        }
                        if (sendMsg != null) {
                            sendMsg.sendConfig(msg.arg1, msg.arg2);
                        }
                        break;
                    case GlobalConstant.NET_ECG_CONFIG:
                        if (msg.arg1 == KParamType.ECG_CONNECTION_STATUS) {
                            AppFragment.probeEcgStatus = msg.arg2;
                        }
                        ecgConfig.put(msg.arg1, msg.arg2);

                        if (sendMsg != null) {
                            sendMsg.sendConfig(msg.arg1, msg.arg2);
                        }

                        break;
                    case GlobalConstant.NET_TEMP_CONFIG:
                        tempConfig.put(msg.arg1, msg.arg2);
                        GlobalConstant.TEMP_STATUS = msg.arg2;
                        if (sendMsg != null) {
                            sendMsg.sendConfig(msg.arg1, msg.arg2);
                        }
                        break;
                    case GlobalConstant.NET_PATIENT_CONFIG:
                        Bundle bundle = msg.getData();
                        byte[] idcards = bundle.getByteArray("idcard");
                        byte[] name = bundle.getByteArray("name");
                        byte[] born = bundle.getByteArray("born");
                        byte type = bundle.getByte("type");
                        byte sex = bundle.getByte("sex");
                        //身份证头像
                        byte[] picture = bundle.getByteArray("picture");
                        String id = new String(idcards, "UTF-8");
                        String n = new String(name, "UTF-8");
                        //身份证头像地址信息
                        byte[] address = bundle.getByteArray("address");
                        String strAddress = new String(address, "UTF-8");
                        String pictureString = new String(picture, "ISO-8859-1");
                        sendMsg.sendPersonalDetail(n, id, sex, type, pictureString, strAddress);
                        break;
                    case GlobalConstant.NET_12LEAD_DIAG_RESULT:
                        Bundle result = msg.getData();
                        leadDiaResult = result.getByteArray("12leaddiaresult");
                        if (sendMsg != null) {
                            sendMsg.send12LeadDiaResult(leadDiaResult);
                        }
                        break;
                    default:
                        Bundle errorData = msg.getData();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        status = new HashMap<>();
        trends = new HashMap<>();
        ecgConfig = new HashMap<>();
        spo2Config = new HashMap<>();
        nibpConfig = new HashMap<>();
        tempConfig = new HashMap<>();
        msgBinder = new MsgBinder();

        // 开启线程处理网络数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EchoServer.getEchoServerInstance(GlobalConstant.PORT, mHandler).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return msgBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Binder继承类
     */
    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return aidlServr实例
         */
        public AIDLServer getService() {
            return AIDLServer.this;
        }
    }
}
