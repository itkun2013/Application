package com.konsung.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.WaveFormEcg;
import com.konsung.floatbuttons.DonutProgress;
import com.konsung.service.AIDLServer;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Ecg Fragment
 *
 * @author yuchunhui
 * @date 2015-06-15 20:39:38
 */
public class Ecg5Fragment extends BaseFragment {
    // 1参数
    @InjectView(R.id.ecg_hr_tv)
    TextView tvHrView;
    @InjectView(R.id.ecg_notify)
    TextView tvStatusView;
    @InjectView(R.id.measure_btn)
    ImageTextButton btnMeasure;
    @InjectView(R.id.wave_i)
    WaveFormEcg waveI;
    @InjectView(R.id.wave_ii)
    WaveFormEcg waveIi;
    @InjectView(R.id.wave_iii)
    WaveFormEcg waveIii;
    @InjectView(R.id.wave_AVR)
    WaveFormEcg waveAvr;
    @InjectView(R.id.wave_AVL)
    WaveFormEcg waveAvl;
    @InjectView(R.id.wave_AVF)
    WaveFormEcg waveAvf;
    @InjectView(R.id.wave_V1)
    WaveFormEcg waveV1;
    @InjectView(R.id.donut_progress)
    DonutProgress donutProgress;

    public AIDLServer aidlServer;
    private Handler handler = new Handler();
    private int hrVaule = GlobalConstant.INVALID_DATA;
    private int measureCount = 0;
    private View view;
    /* private ArrayList<byte[]> data = new ArrayList<>();*/
    private boolean isAttach = true;
    //按钮控制，是否开始测量
    private boolean isChecking = false;
    //ecg导线是否接好
    private boolean isEcgConnect = false;
    //最多只能测量20秒
    private int max = 20;
    //进度
    private int progress = 20;
    private boolean isTimeOut = false;
    private static String lastStateText; //心电最近一次的状态字符串

    private Map<String, byte[]> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity().getApplicationContext(),
                "app_config", "name", "");
        boolean check = name.equals("");

//        if (check) {
//            Toast.makeText(getActivity(), getRecString(R.string
//                    .fragment_pls_select_patient), Toast.LENGTH_SHORT).show();
//            GetAllPatientFragment allPatientFragment = new
//                    GetAllPatientFragment();
//            switchToFragment(R.id.fragment, allPatientFragment,
//                    "allPatientFragment", true);
//            isAttach = false;
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ecg5, null);

        // 初始化视图
        data = new HashMap<>();
        ButterKnife.inject(this, view);
        initView();
        initAidlService();


        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导线连接成功，开始测量
                reinit();
                if (!isChecking && isEcgConnect) {
                    btnMeasure.setText(getRecString(R.string.nibp_btn_stop));
                    isChecking = true;
                    hrVaule = GlobalConstant.INVALID_DATA;
                    measureCount = 0;
                    resetView();
                    if (isChecking)
                        tvStatusView.setText(getRecString(R.string
                                .ecg_pls_keep_quiet_while_check));
                } else {
                    hrVaule = GlobalConstant.INVALID_DATA;
                    measureCount = 0;
                    btnMeasure.setText(getRecString(R.string.nibp_btn_start));
                    isChecking = false;
                }
            }
        });

        setEcgConnectStatus(GlobalConstant.LEFF_OFF);
        waveI.setTitle(getRecString(R.string.ecg_title_I), 1);
        waveIi.setTitle(getRecString(R.string.ecg_title_II), 2);
        waveIii.setTitle(getRecString(R.string.ecg_title_III), 3);
        waveAvr.setTitle(getRecString(R.string.ecg_title_AVR), 4);
        waveAvl.setTitle(getRecString(R.string.ecg_title_AVL), 5);
        waveAvf.setTitle(getRecString(R.string.ecg_title_AVF), 6);
        waveV1.setTitle(getRecString(R.string.ecg_title_V), 7);
        /*handler.post(updateThread);*/
        /*handler.post(ecgStatus);*/
        // Inflate the layout for this fragment
        return view;
    }

    private void reinit() {
        progress = 20;
        donutProgress.setProgress(max);
        initView();
        waveI.stop();
        waveIi.stop();
        waveIii.stop();
        waveAvr.stop();
        waveAvl.stop();
        waveAvf.stop();
        waveV1.stop();
        waveI.invalidate();
        waveIi.invalidate();
        waveIii.invalidate();
        waveAvr.invalidate();
        waveAvl.invalidate();
        waveAvf.invalidate();
        waveV1.invalidate();
    }


    /**
     * 未暂停重置
     */
    private void resetView() {
        waveI.reset();
        waveIi.reset();
        waveIii.reset();
        waveAvr.reset();
        waveAvl.reset();
        waveAvf.reset();
        waveV1.reset();

        waveI.setTitle(getRecString(R.string.ecg_title_I), 1);
        waveIi.setTitle(getRecString(R.string.ecg_title_II), 2);
        waveIii.setTitle(getRecString(R.string.ecg_title_III), 3);
        waveAvr.setTitle(getRecString(R.string.ecg_title_AVR), 4);
        waveAvl.setTitle(getRecString(R.string.ecg_title_AVL), 5);
        waveAvf.setTitle(getRecString(R.string.ecg_title_AVF), 6);
        waveV1.setTitle(getRecString(R.string.ecg_title_V1), 7);
    }


   /* public byte[] saveWave(int param, byte[] bytes) {
        String str = "";
        if (bytes != null)
            *//*Log.d("Test", param + " bytes.length = " + bytes.length);*//*
            if (bytes != null&&_aidlInterface!=null)
                try {
                    str = UnitConvertUtil.bytesToHexString(bytes);
                    _aidlInterface.savedWave(param, str);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        *//*Log.d("Test", "str = " + str);*//*
        return bytes;
    }*/

    /*
     * 初始化
     */
    private void initView() {
        tvHrView.addTextChangedListener(new OverProofUtil(50f, 120f, tvHrView));
        donutProgress.setMax(max);
        tvHrView.setText(UiUitls.getString(R.string.default_value));
    }

    /**
     * 设置心电模拟器连接状态
     * @param value
     */
    public void setEcgConnectStatus(int value) {
        int leadoff = -1;
        leadoff = value;
        if (leadoff == 496 || leadoff == 0) {
            isEcgConnect = true;
            if (!isChecking && !isTimeOut) {
                tvStatusView.setText(getRecString(R.string
                        .ecg_pls_keep_quiet_wait_check));
            }
        } else if (leadoff == GlobalConstant.INVALID_DATA) {
            isEcgConnect = false;
            isChecking = false;
            reinit();
            tvStatusView.setText(getRecString(R.string
                    .ecg_pls_checkfordevice));
        } else if (leadoff == 1) {
            if(lastStateText != null){
                tvStatusView.setText(lastStateText);
            }
        } else if (leadoff == 511){
            isEcgConnect = false;
            isChecking = false;
            reinit();
            tvStatusView.setText(getRecString(R.string
                    .ecg_pls_checkforline));
        }
    }

    /*
    * 初始化绑定AIDLService服务器
    */
    public void initAidlService() {
        // intent的action为康尚aidl服务器
        intent = new Intent("com.konsung.aidlServer");
        // 一启动程序就绑定aidl service服务
        // 保证service只运行一次，一直在后台运行
        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
        getActivity().startService(intent);
        /*Log.d("Test", "AIDLClient start");*/

        isBind = getActivity().bindService(intent, serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    /*
        * serviceConnection 与aidl接口交互
        */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*Log.i("Test", "AIDLClient.onServiceConnected()...");*/
            // 获得服务对象
            /*_aidlInterface = IKonsungAidlInterface.Stub.asInterface
            (service);*/
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    switch (param) {
                        case KParamType.ECG_I:
                            data.put("i", bytes);
                            if (isChecking) {

//                                saveWave(aidlServer, param, data.get("i"));
                                waveI.setData(data.get("i"));
                            }
                            break;
                        case KParamType.ECG_II:
                            data.put("ii", bytes);
                            if (isChecking) {
//                                saveWave(aidlServer, param, data.get("ii"));
                                waveIi.setData(data.get("ii"));
                            }

                            break;
                        case KParamType.ECG_III:
                            data.put("iii", bytes);
                            if (isChecking) {
//                                saveWave(aidlServer, param, data.get("iii"));
                                waveIii.setData(data.get("iii"));
                            }
                            break;
                        case KParamType.ECG_AVR:
                            if (isChecking) {
                                progress--;
                                donutProgress.setProgress(progress);
                                if (progress == 0) {
                                    isChecking = false;
                                    isTimeOut = true;
                                    btnMeasure.setText(getRecString(R.string
                                            .nibp_btn_start));
                                    tvStatusView.setText(getRecString(R.string
                                            .ecg_check_timeout));
                                    reinit();
                                }
                                data.put("avr", bytes);
//                                saveWave(aidlServer, param, data.get("avr"));
                                waveAvr.setData(data.get("avr"));
                            }
                            break;
                        case KParamType.ECG_AVL:
                            data.put("avl", bytes);
                            if (isChecking) {
//                                saveWave(aidlServer, param, data.get("avl"));
                                waveAvl.setData(data.get("avl"));
                            }
                            break;
                        case KParamType.ECG_AVF:
                            data.put("avf", bytes);
                            if (isChecking) {
//                                saveWave(aidlServer, param, data.get("avf"));
                                waveAvf.setData(data.get("avf"));
                            }
                            break;
                        case KParamType.ECG_V1:
                            data.put("v1", bytes);
                            if (isChecking) {
//                                saveWave(aidlServer, param, data.get("v1"));
                                waveV1.setData(data.get("v1"));
                            }

                            break;
                        default:
                            break;

                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    int hrValue;
                    switch (param) {
                        case KParamType.ECG_HR:
                            if (isChecking) {
                                hrValue = value / GlobalConstant.TREND_FACTOR;
                                //心率值波动小于4，则为有效数据
                                if ((Math.abs(hrVaule - hrValue) < 4) &&
                                        hrValue != GlobalConstant
                                                .INVALID_DATA) {
                        /*Log.d("Test", "_mesureCount" + measureCount);*/
                                    if ((measureCount++) == 10) {
                                        reinit();
                                        isChecking = false;
                                        btnMeasure.setText(getRecString(R
                                                .string.nibp_btn_start));
                                        tvStatusView.setText(getRecString(R
                                                .string.ecg_check_complited));
                                        tvHrView.setText(String.valueOf
                                                (hrVaule));
                                        //需要保存数据
                                        ServiceUtils.saveTrend(KParamType
                                                .ECG_HR, hrVaule *
                                                GlobalConstant.TREND_FACTOR);
                                        ServiceUtils.saveToDb2();
                                        return;
                                    }
                                } else {
                                    hrVaule = hrValue;
                                    measureCount = 0;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    switch (param) {
                        case 0x10:
                            setEcgConnectStatus(value);
                            break;
                    }


                }

                @Override
                public void sendPersonalDetail(String name, String idcard,
                                               int sex, int type, String pic, String address) {

                }

                @Override
                public void send12LeadDiaResult(byte[] bytes) {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /*Log.i("Test", "AIDLClient.onServiceDisconnected()...");*/
            /*_aidlInterface = null;*/
            reinit();
            aidlServer = null;
            isChecking = false;
            isEcgConnect = false;
            tvStatusView.setText(getRecString(R.string.ecg_pls_checkfordevice));
        }
    };

    public void saveWave(AIDLServer server, int param, byte[] bytes) {
        try {
            ServiceUtils.savedWave(param, Base64.encodeToString(bytes, Base64
                    .DEFAULT));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttach = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (aidlServer != null)
            aidlServer.setSendMSGToFragment(null);
        /*Log.d("Test", "5fragment is destroy");*/
        waveI.stop();
        waveIi.stop();
        waveIii.stop();
        waveAvr.stop();
        waveAvl.stop();
        waveAvf.stop();
        waveV1.stop();

        getActivity().unbindService(serviceConnection);

    }

    @Override
    public void onPause() {
        super.onPause();
        lastStateText = tvStatusView.getText().toString().trim();
    }
}
