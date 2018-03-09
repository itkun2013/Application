package com.konsung.fragment;

import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatEditText;
import com.decard.healthcard.WSRead;
import com.decard.portdrivelibrary.utils.DigitalTrans;
import com.huada.healthcard.HuaDaDeviceLib;
import com.konsung.R;
import com.konsung.defineview.EcgSettingDialog;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.WaveFormEcg;
import com.konsung.defineview.WaveFormSpo2;
import com.konsung.floatbuttons.DonutProgress;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.service.AIDLServer;
import com.konsung.util.DiagCodeToText;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 Administrator
 * @创建时间 16/03/03 上午 9:21
 * @描述 ${TODO}
 * @更新者 ${Author}
 * @更新时间 ${Date}
 * @描述 ${TODO}
 */
public class TestFragment extends BaseFragment {

    @InjectView(R.id.tv_check_idCard)
    TextView tvCheckIdCard;
    @InjectView(R.id.ll_detail)
    LinearLayout llDetail;
    @InjectView(R.id.et_patient_name)
    FlatEditText etPatientName;
    @InjectView(R.id.et_patient_idCard)
    FlatEditText etPatientIdCard;
    @InjectView(R.id.spn_patient_sex)
    Spinner spnPatientSex;
    @InjectView(R.id.spn_patient_blood)
    Spinner spnPatientBlood;
    @InjectView(R.id.spn_patient_type)
    Spinner spnPatientType;
    @InjectView(R.id.btn_clear_data)
    ImageTextButton btnClearData;
    protected LocalActivityManager mLocalActivityManager;
    @InjectView(R.id.hr)
    TextView tvHr;
    @InjectView(R.id.ecg_hr_tv)
    TextView tvEcgHr;
    @InjectView(R.id.ecg_br_tv)
    TextView tvEcgBr;
    @InjectView(R.id.tv_ecg_alarm)
    TextView tvEcgAlarm;
    @InjectView(R.id.ecg_notify)
    TextView tvEcgNotify;
    @InjectView(R.id.donut_progress_ecg)
    DonutProgress mDonutProgressEcg;
    @InjectView(R.id.measure_btn_ecg)
    TextView tvMeasureBtnEcg;
    @InjectView(R.id.ecg_setting_ecg)
    TextView tvEcgSettingEcg;
    @InjectView(R.id.wave_i)
    WaveFormEcg waveI;
    @InjectView(R.id.wave_ii)
    WaveFormEcg waveIi;
    @InjectView(R.id.wave_iii)
    WaveFormEcg waveIii;
    @InjectView(R.id.wave_AVR)
    WaveFormEcg waveAVR;
    @InjectView(R.id.wave_AVL)
    WaveFormEcg waveAVL;
    @InjectView(R.id.wave_AVF)
    WaveFormEcg waveAVF;
    @InjectView(R.id.wave_V1)
    WaveFormEcg waveV1;
    @InjectView(R.id.wave_V2)
    WaveFormEcg waveV2;
    @InjectView(R.id.wave_V3)
    WaveFormEcg waveV3;
    @InjectView(R.id.wave_V4)
    WaveFormEcg waveV4;
    @InjectView(R.id.wave_V5)
    WaveFormEcg waveV5;
    @InjectView(R.id.wave_V6)
    WaveFormEcg waveV6;
    @InjectView(R.id.ecg_rl)
    LinearLayout rlEcg;
    @InjectView(R.id.spo2_trend)
    TextView tvSpo2Trend;
    @InjectView(R.id.spo2)
    LinearLayout llSpo2;
    @InjectView(R.id.spo2_pr)
    TextView tvSpo2Pr;
    @InjectView(R.id.spo2_pr_tv)
    TextView tvSpo2PrTv;
    @InjectView(R.id.pr)
    LinearLayout llPr;
    @InjectView(R.id.spo2_notify)
    TextView tvSpo2Notify;
    @InjectView(R.id.donut_progress_spo2)
    DonutProgress mDonutProgressSpo2;
    @InjectView(R.id.measure_btn_spo2)
    ImageTextButton btnMeasureBtnSpo2;
    @InjectView(R.id.wave_form)
    WaveFormSpo2 mWaveForm;
    @InjectView(R.id.wave_form1)
    FrameLayout flWaveForm1;
    @InjectView(R.id.nibp_sys)
    TextView tvNibpSys;
    @InjectView(R.id.nibp_sys_tv)
    TextView tvNibpSysTv;
    @InjectView(R.id.nibp_dia)
    TextView tvNibpDia;
    @InjectView(R.id.nibp_dia_tv)
    TextView tvNibpDiaTv;
    @InjectView(R.id.nibp_map)
    TextView tvNibpMap;
    @InjectView(R.id.nibp_map_tv)
    TextView tvNibpMapTv;
    @InjectView(R.id.nibp_pr)
    TextView tvNibpPr;
    @InjectView(R.id.nibp_pr_tv)
    TextView tvNibpPrTv;
    @InjectView(R.id.nibp_cuff)
    TextView tvNibpCuff;
    @InjectView(R.id.measure_btn_nibp)
    TextView tvMeasureBtnNibp;
    @InjectView(R.id.temp_trend)
    TextView tvTempTrend;
    @InjectView(R.id.temp_t1_tv)
    TextView tvTempT1Tv;
    @InjectView(R.id.temp_notify)
    TextView tvTempNotify;
    @InjectView(R.id.temp_progress_bar)
    ProgressBar proBarTempProgressBar;
    @InjectView(R.id.measure_btn_temp)
    ImageTextButton btnMeasureBtnTemp;
    @InjectView(R.id.temp_test)
    FrameLayout flTempTest;
    @InjectView(R.id.irtemp_trend)
    TextView tvIrtempTrend;
    @InjectView(R.id.irtemp_trend_tv)
    TextView tvIrtempTrendTv;
    @InjectView(R.id.irtemp_test)
    FrameLayout flIrtempTest;
    @InjectView(R.id.blood_wbc_trend)
    TextView tvBloodWbcTrend;
    @InjectView(R.id.blood_wbc_trend_tv)
    TextView tvBloodWbcTrendTv;
    @InjectView(R.id.scrollView_blood_glu)
    LinearLayout llScrollViewBloodGlu;
    @InjectView(R.id.urinert_leu)
    TextView tvmUrinertLeu;
    @InjectView(R.id.urinert_leu_tv)
    TextView tvUrinertLeuTv;
    @InjectView(R.id.leu_icon)
    ImageView ivLeuIcon;
    @InjectView(R.id.urinert_alb)
    TextView tvUrinertAlb;
    @InjectView(R.id.urinert_alb_tv)
    TextView tvUrinertAlbTv;
    @InjectView(R.id.alb_icon)
    ImageView ivAlbIcon;
    @InjectView(R.id.urinert_bil)
    TextView tvUrinertBil;
    @InjectView(R.id.urinert_bil_tv)
    TextView tvUrinertBilTv;
    @InjectView(R.id.bil_icon)
    ImageView ivBilIcon;
    @InjectView(R.id.urinert_asc)
    TextView tvUrinertAsc;
    @InjectView(R.id.urinert_asc_tv)
    TextView tvUrinertAscTv;
    @InjectView(R.id.asc_icon)
    ImageView ivAscIcon;
    @InjectView(R.id.urinert_ket)
    TextView tvUrinertKet;
    @InjectView(R.id.urinert_ket_tv)
    TextView tvUrinertKetTv;
    @InjectView(R.id.ket_icon)
    ImageView ivKetIcon;
    @InjectView(R.id.urinert_ubg)
    TextView tvUrinertUbg;
    @InjectView(R.id.urinert_ubg_tv)
    TextView tvUrinertUbgTv;
    @InjectView(R.id.ubg_icon)
    ImageView ivUbgIcon;
    @InjectView(R.id.urinert_pro)
    TextView tvUrinertPro;
    @InjectView(R.id.urinert_pro_tv)
    TextView tvUrinertProTv;
    @InjectView(R.id.pro_icon)
    ImageView ivProIcon;
    @InjectView(R.id.urinert_glu)
    TextView tvUrinertGlu;
    @InjectView(R.id.urinert_glu_tv)
    TextView tvUrinertGluTv;
    @InjectView(R.id.glu_icon)
    ImageView ivGluIcon;
    @InjectView(R.id.urinert_sg)
    TextView tvUrinertSg;
    @InjectView(R.id.urinert_sg_tv)
    TextView tvUrinertSgTv;
    @InjectView(R.id.sg_icon)
    ImageView ivSgIcon;
    @InjectView(R.id.urinert_nit)
    TextView tvUrinertNit;
    @InjectView(R.id.urinert_nit_tv)
    TextView tvUrinertNitTv;
    @InjectView(R.id.nit_icon)
    ImageView ivNitIcon;
    @InjectView(R.id.urinert_cre)
    TextView tvUrinertCre;
    @InjectView(R.id.urinert_cre_tv)
    TextView tvUrinertCreTv;
    @InjectView(R.id.cre_icon)
    ImageView ivCreIcon;
    @InjectView(R.id.urinert_bld)
    TextView tvUrinertBld;
    @InjectView(R.id.urinert_bld_tv)
    TextView tvUrinertBldTv;
    @InjectView(R.id.bld_icon)
    ImageView ivBldIcon;
    @InjectView(R.id.urinert_ph)
    TextView tvUrinertPh;
    @InjectView(R.id.urinert_ph_tv)
    TextView tvUrinertPhTv;
    @InjectView(R.id.ph_icon)
    ImageView ivPhIcon;
    @InjectView(R.id.urinert_ca)
    TextView tvUrinertCa;
    @InjectView(R.id.urinert_ca_tv)
    TextView tvUrinertCaTv;
    @InjectView(R.id.ca_icon)
    ImageView ivCaIcon;
    @InjectView(R.id.blood_glu_trend)
    TextView tvBloodGluTrend;
    @InjectView(R.id.blood_glu_trend_tv)
    TextView tvBloodGluTrendTv;
    @InjectView(R.id.uric_acid_trend)
    TextView tvUricAcidTrend;
    @InjectView(R.id.uric_acid_trend_tv)
    TextView tvUricAcidTrendTv;
    @InjectView(R.id.cholesterol_trend)
    TextView tvCholesterolTrend;
    @InjectView(R.id.cholesterol_trend_tv)
    TextView tvCholesterolTrendTv;
    @InjectView(R.id.scrollView)
    LinearLayout llScrollView;
    @InjectView(R.id.blood_hgb_trend)
    TextView tvBloodHgbTrend;
    @InjectView(R.id.blood_hgb_trend_tv)
    TextView tvBloodHgbTrendTv;
    @InjectView(R.id.blood_hct_trend)
    TextView tvBloodHctTrend;
    @InjectView(R.id.blood_hct_trend_tv)
    TextView tvBloodHctTrendTv;


    //    private MeasureDataBean _measureBean;

    private Intent intentSpo2;

    private int spo2Value = GlobalConstant.INVALID_DATA;
    private int spo2Pr = GlobalConstant.INVALID_DATA;
    private int measureCountSpo2 = 0;  //测试次数

    private Handler handlerSpo2 = new Handler();

    //spo2 attach status
    private boolean attachSpo2 = false; //血氧探头指示

    private boolean isCheckingSpo2 = false;  //测量状态

    private boolean isFingerInsert = false;//手指
    private int maxSpo2 = 20;
    private int progressSpo2 = 20;
    private boolean isStartService = false;

    private HashMap<Integer, TextView> viewUrinert;

    //************************ 体温参数声明begin**********/
    private boolean attachTemp = false;
    // 探头状态
    private boolean isAttach;
    //已经得到数据
    private boolean isGetValue;

    // 以下变量使用于模拟测量,不需要时可删除
    private List<Integer> tempTrendList;
    private boolean isMeasure;  // 模拟测量时

    // 支持健康卡读卡器
    private WSRead mWSRead;// 用来读取健康卡的类
    private HuaDaDeviceLib hdosUsbDevice;//华大一体机读卡器类
    private boolean isConnected = true;// 用来标记是否连接USB设备

    //心率参数
    private Intent intentEcg12 = null;              // 用于绑定AIDL
    //    private boolean isBind_ecg12       = false;            // 是否已经绑定服务
    public AIDLServer aidlServer;
    private Handler handlerEcg12 = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final byte[] data = (byte[]) msg.obj;
            saveWave(aidlServer, msg.arg1, data);
            //            _measureDataBean.set_ecgWave(msg.arg1,
            // UnitConvertUtil.bytesToHexString(data));
        }
    };

    private int brVaule = GlobalConstant.INVALID_DATA;

    //是否已经开始检查
    private boolean isCheckingEcg = false;
    //ecg是否有连接
    private boolean isEcgConnect = false;
    //进度条最大值
    private int maxEcg = 30;
    //进度条默认值
    private int progressEcg = 30;
    private boolean isTimeOut = false;

    private Map<String, byte[]> data;
    private HashMap<Integer, WaveFormEcg> waves;
    private Iterator iterator;

    private final int hrAlarmHigh = GlobalConstant.HR_ALARM_HIGH;// 心率报警高限
    private final int hrAlarmLow = GlobalConstant.HR_ALARM_LOW;// 心率报警低限

    private int measureState = 0;              // 血压测量状态
    private int cuffStatic;   //白细胞参数设置

    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        changeTiTle(UiUitls.getString(R.string.health_measure_test));
        ButterKnife.inject(this, view);
        calendar = Calendar.getInstance();
        //设置性别的spn选项
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter
                .createFromResource(getActivity().getBaseContext(),
                R.array.detail_sex, R.layout.spinner_button);
        sexAdapter.setDropDownViewResource(R.layout.flat_list_item);
        spnPatientSex.setAdapter(sexAdapter);
        //设置血型的spn选项
        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter
                .createFromResource(getActivity().getBaseContext(),
                R.array.detail_blood_type_array, R.layout.spinner_button);
        bloodAdapter.setDropDownViewResource(R.layout.flat_list_item);
        spnPatientBlood.setAdapter(bloodAdapter);

        //设置病人类型的spn选项
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter
                .createFromResource(getActivity().getBaseContext(),
                R.array.patient_type_array, R.layout.spinner_button);
        typeAdapter.setDropDownViewResource(R.layout.flat_list_item);
        spnPatientType.setAdapter(typeAdapter);

        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMeasureData();
            }
        });

        data = new HashMap<>();
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter
                .createFromResource(getActivity().getBaseContext(),
                R.array.mm_list, R.layout.mm_spinner);
        // Specify the layout to use when the list of choices appears
        spAdapter.setDropDownViewResource(R.layout.mm_list_item);
        initView_ecg();
        initView_nibp();
        initAidlService_ecg();
        tvEcgSettingEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EcgSettingDialog dialog = new EcgSettingDialog(getActivity(),
                        UiUitls.getString(R.string.ecg_setting),
                        new EcgSettingDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                if (pressed && !isCheckingEcg) {
                                    resetView_ecg();
                                }
                            }
                        });
                dialog.show();
            }
        });
        waves = new HashMap<>();
        waves.put(R.id.wave_i, waveI);
        waves.put(R.id.wave_ii, waveIi);
        waves.put(R.id.wave_iii, waveIii);
        waves.put(R.id.wave_AVR, waveAVR);
        waves.put(R.id.wave_AVF, waveAVF);
        waves.put(R.id.wave_AVL, waveAVL);
        waves.put(R.id.wave_V1, waveV1);
        waves.put(R.id.wave_V2, waveV2);
        waves.put(R.id.wave_V3, waveV3);
        waves.put(R.id.wave_V4, waveV4);
        waves.put(R.id.wave_V5, waveV5);
        waves.put(R.id.wave_V6, waveV6);
        // 设置心电导联类型，旧版本的硬件，拔交流电时会导致参数板断电重启，为规避此问题，在进入心电界面时再发送一次导
        // 联设置命令
        int value = SpUtils.getSpInt(getActivity().getApplicationContext(),
                "sys_config", "ecg_lead_system", EcgDefine.ECG_12_LEAD);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM,
                value);
        tvMeasureBtnEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reinit_ecg();
                //&&isEcgConnect
                if (!isCheckingEcg && isEcgConnect) {
                    //启动12导诊断
                    EchoServerEncoder.setEcgConfig((short) 0x15, 1);
                    //                    mm_sp.setClickable(false);
                    tvMeasureBtnEcg.setText(getRecString(R.string
                            .nibp_btn_stop));
                    isCheckingEcg = true;
                    brVaule = GlobalConstant.INVALID_DATA;
                    resetView_ecg();
                    if (isCheckingEcg) {
                        tvEcgNotify.setText(getRecString(R.string
                                .ecg_pls_keep_quiet_while_check));
                    }
                } else {
                    tvMeasureBtnEcg.setText(getRecString(R.string
                            .nibp_btn_start));
                    isCheckingEcg = false;
                }
            }
        });
        waveI.setTitle(getRecString(R.string.ecg_title_I), 1);
        waveIi.setTitle(getRecString(R.string.ecg_title_II), 2);
        waveIii.setTitle(getRecString(R.string.ecg_title_III), 3);
        waveAVR.setTitle(getRecString(R.string.ecg_title_AVR), 4);
        waveAVL.setTitle(getRecString(R.string.ecg_title_AVL), 5);
        waveAVF.setTitle(getRecString(R.string.ecg_title_AVF), 6);
        waveV1.setTitle(getRecString(R.string.ecg_title_V1), 7);
        waveV2.setTitle(getRecString(R.string.ecg_title_V2), 8);
        waveV3.setTitle(getRecString(R.string.ecg_title_V3), 9);
        waveV4.setTitle(getRecString(R.string.ecg_title_V4), 10);
        waveV5.setTitle(getRecString(R.string.ecg_title_V5), 11);
        waveV6.setTitle(getRecString(R.string.ecg_title_V6), 12);

        mDonutProgressSpo2.setMax(maxSpo2);
        mWaveForm.setSampleRate(200);
        tvSpo2Trend.setText(UiUitls.getString(R.string.default_value));
        tvSpo2PrTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.SPO2_VALUE != GlobalConstant.INVALID_DATA) {
            tvSpo2Trend.setText(String.valueOf(GlobalConstant.SPO2_VALUE));
        }
        if (GlobalConstant.SPO2_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvSpo2PrTv.setText(String.valueOf(GlobalConstant.SPO2_PR_VALUE));
        }
        spo2Value = GlobalConstant.INVALID_DATA;
        measureCountSpo2 = 0;
        setSpo2LeffStatus(GlobalConstant.LEFF_OFF);
        btnMeasureBtnSpo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (attachSpo2 && !isCheckingSpo2 && isFingerInsert) {//开始测量
                    reinit_spo2();
                    isCheckingSpo2 = true;
                    _restartMeasure_spo2();//开始测量
                    btnMeasureBtnSpo2.setText(getRecString(R.string
                            .nibp_btn_stop));
                    tvSpo2Notify.setText(getRecString(R.string
                            .spo2_isChecking));
                } else {//等待测量
                    btnMeasureBtnSpo2.setText(getRecString(R.string
                            .nibp_btn_start));
                    mWaveForm.reset();
                    reinit_spo2();
                    isCheckingSpo2 = false;
                    if (isFingerInsert) {
                        tvSpo2Notify.setText(getRecString(R.string
                                .spo2_waiting));
                    } else if (attachSpo2 && !isFingerInsert) {
                        tvSpo2Notify.setText(getRecString(R.string
                                .spo2_pls_put_finger));
                    } else if (!attachSpo2) {//请插入血氧探头
                        tvSpo2Notify.setText(getRecString(R.string
                                .spo2_pls_put_probe));
                    }

                }
            }
        });


        //初始化体温view
        int temp_type = SpUtils.getSpInt(getActivity().getApplicationContext(),
                "sys_config", "temp_type", TempDefine.TEMP_CONTACT);
        if (temp_type == TempDefine.TEMP_CONTACT) {//接触式
            flTempTest.setVisibility(View.VISIBLE);
            flIrtempTest.setVisibility(View.GONE);
            initView_temp();
        } else if (temp_type == TempDefine.TEMP_INFRARED) {//红外式体温
            flTempTest.setVisibility(View.GONE);
            flIrtempTest.setVisibility(View.VISIBLE);
            initView_irtemp();
        }
        tvMeasureBtnNibp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (measureState == 0) {
                    initView_nibp();
                    EchoServerEncoder.setNibpConfig((short) 0x05, 0);
                    cuffStatic = 0;
                    tvMeasureBtnNibp.setText(getRecString(R.string
                            .nibp_btn_stop));
                    measureState = 1;
                } else {
                    initView_nibp();
                    EchoServerEncoder.setNibpConfig((short) 0x06, 0);
                    tvMeasureBtnNibp.setText(getRecString(R.string
                            .nibp_btn_start));
                    measureState = 0;
                }
            }
        });

        //血糖初始化view
        initView_blood();


        //初始化尿常规view
        viewUrinert = new HashMap<>();
        viewUrinert.put(R.id.urinert_leu_tv, tvUrinertLeuTv);
        viewUrinert.put(R.id.urinert_ubg_tv, tvUrinertUbgTv);
        viewUrinert.put(R.id.urinert_alb_tv, tvUrinertAlbTv);
        viewUrinert.put(R.id.urinert_pro_tv, tvUrinertProTv);
        viewUrinert.put(R.id.urinert_bil_tv, tvUrinertBilTv);
        viewUrinert.put(R.id.urinert_glu_tv, tvUrinertGluTv);
        viewUrinert.put(R.id.urinert_asc_tv, tvUrinertAscTv);
        viewUrinert.put(R.id.urinert_ket_tv, tvUrinertKetTv);
        viewUrinert.put(R.id.urinert_nit_tv, tvUrinertNitTv);
        viewUrinert.put(R.id.urinert_cre_tv, tvUrinertCreTv);
        viewUrinert.put(R.id.urinert_ph_tv, tvUrinertPhTv);
        viewUrinert.put(R.id.urinert_bld_tv, tvUrinertBldTv);
        viewUrinert.put(R.id.urinert_ca_tv, tvUrinertCaTv);
        initView_urinert();


        initView_bloodwbs();//白细胞View初始化

        initView_bloodwhgb();//血红蛋白初始化


        initView_uricAcid();//尿酸初始化


        initView_cholesterol();//总胆固醇初始化
        //初始化清除数据的按钮
        btnClearData.setText(UiUitls.getString(R.string.data_clean_success));
        return view;
    }

    //清除所有测量数据
    private void clearMeasureData() {
        GlobalConstant.SPO2_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.SPO2_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.ECG_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_SYS_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_DIA_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_MAP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.TEMP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.IR_TEMP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_GLU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_LEU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_NIT_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_UBG_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_PRO_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_PH_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_SG_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_BLD_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_KET_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_BIL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_GLU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_ASC_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_ALB_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_CRE_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_CA_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_WBC_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_HGB_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_HCT_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URIC_ACID_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.CHOLESTEROL_VALUE = GlobalConstant.INVALID_DATA;
        tvSpo2Trend.setText(UiUitls.getString(R.string.default_value));
        tvSpo2PrTv.setText(UiUitls.getString(R.string.default_value));
        tvCholesterolTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvBloodWbcTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvUricAcidTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvBloodHgbTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvBloodHctTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvBloodGluTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvEcgHr.setText(UiUitls.getString(R.string.default_value));
        tvEcgBr.setText(UiUitls.getString(R.string.default_value));
        tvTempT1Tv.setText(UiUitls.getString(R.string.default_value));
        tvIrtempTrendTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpSysTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpDiaTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpMapTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpPrTv.setText(UiUitls.getString(R.string.default_value));
        Iterator iterator = viewUrinert.keySet().iterator();
        while (iterator.hasNext()) {
            viewUrinert.get(iterator.next()).setText(
                    UiUitls.getString(R.string.default_value));
        }
        tvUrinertSgTv.setText(UiUitls.getString(R.string.default_value));
        ivLeuIcon.setVisibility(View.GONE);
        ivUbgIcon.setVisibility(View.GONE);
        ivAlbIcon.setVisibility(View.GONE);
        ivProIcon.setVisibility(View.GONE);
        ivBilIcon.setVisibility(View.GONE);
        ivGluIcon.setVisibility(View.GONE);
        ivAscIcon.setVisibility(View.GONE);
        ivSgIcon.setVisibility(View.GONE);
        ivKetIcon.setVisibility(View.GONE);
        ivNitIcon.setVisibility(View.GONE);
        ivCreIcon.setVisibility(View.GONE);
        ivPhIcon.setVisibility(View.GONE);
        ivBldIcon.setVisibility(View.GONE);
        ivCaIcon.setVisibility(View.GONE);
        initView_nibp();

        // 清空身份证或健康卡数据
        llDetail.setVisibility(View.GONE);
        tvCheckIdCard.setVisibility(View.VISIBLE);

        // 清空心率报警信息
        tvEcgAlarm.setText("");

        // 清空血氧报警信息
        tvSpo2Notify.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalConstant.CLICK_MEUES = false;

        isConnected = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!isConnected) {
                        break;
                    }
                    initCard();//获取健康卡信息的类
                    brushIdCard();
                    brushHealthCard();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        isConnected = false;
    }

    /**
     * 初始化
     */
    private void initView_cholesterol() {
        tvCholesterolTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.CHOLESTEROL_VALUE != GlobalConstant.INVALID_DATA) {
            tvCholesterolTrendTv.setText(String.valueOf(GlobalConstant
                    .CHOLESTEROL_VALUE));
        }
    }

    /**
     * 白细胞初始化
     */
    private void initView_bloodwbs() {
        tvBloodWbcTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_WBC_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodWbcTrendTv.setText(String.valueOf(GlobalConstant
                    .BLOOD_WBC_VALUE));
        }

    }

    /**
     * 尿酸初始化
     */
    private void initView_uricAcid() {
        tvUricAcidTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.URIC_ACID_VALUE != GlobalConstant.INVALID_DATA) {
            tvUricAcidTrendTv.setText(String.valueOf(GlobalConstant
                    .URIC_ACID_VALUE));
        }
    }


    /**
     * 血红蛋白初始化
     */
    private void initView_bloodwhgb() {
        tvBloodHgbTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HGB_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodHgbTrendTv.setText(String.valueOf(GlobalConstant
                    .BLOOD_HGB_VALUE));
        }
        tvBloodHctTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HCT_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodHctTrendTv.setText(String.valueOf(GlobalConstant
                    .BLOOD_HCT_VALUE));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (aidlServer != null) {
            aidlServer.setSendMSGToFragment(null);
        }
        getActivity().unbindService(serviceConnection_ecg12);
    }

    private void initView_blood() {
        tvBloodGluTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodGluTrendTv.setText(String.valueOf(GlobalConstant
                    .BLOOD_GLU_VALUE));
        }
    }


    private void initView_ecg() {
        progressEcg = 30;
        mDonutProgressEcg.setProgress(progressEcg);
        mDonutProgressEcg.setMax(maxEcg);
        tvEcgHr.setText(UiUitls.getString(R.string.default_value));
        tvEcgBr.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.ECG_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvEcgHr.setText(String.valueOf(GlobalConstant.ECG_PR_VALUE));
            executeHrAlarm(GlobalConstant.ECG_PR_VALUE);
        }
        if (GlobalConstant.ECG_BR_VALUE != GlobalConstant.INVALID_DATA) {
            tvEcgBr.setText(String.valueOf(GlobalConstant.ECG_BR_VALUE));
        }
    }

    private void reinit_ecg() {
        initView_ecg();
        iterator = waves.keySet().iterator();
        while (iterator.hasNext()) {
            waves.get(iterator.next()).stop();
        }
        iterator = waves.keySet().iterator();
        while (iterator.hasNext()) {
            waves.get(iterator.next()).invalidate();
        }

    }

    /**
     * 执行心率报警
     *
     * @param hrValue 心率值
     */
    private void executeHrAlarm(int hrValue) {
        if (hrValue > hrAlarmHigh) {
            tvEcgAlarm.setText(UiUitls.getString(R.string.heart_rate_high));
        } else if (hrValue < hrAlarmLow) {
            tvEcgAlarm.setText(UiUitls.getString(R.string.heart_rate_low));
        } else {
            tvEcgAlarm.setText("");
        }
    }

    private void resetView_ecg() {
        iterator = waves.keySet().iterator();
        while (iterator.hasNext()) {
            waves.get(iterator.next()).reset();
        }
        waveI.setTitle(getRecString(R.string.ecg_title_I), 1);
        waveIi.setTitle(getRecString(R.string.ecg_title_II), 2);
        waveIii.setTitle(getRecString(R.string.ecg_title_III), 3);
        waveAVR.setTitle(getRecString(R.string.ecg_title_AVR), 4);
        waveAVL.setTitle(getRecString(R.string.ecg_title_AVL), 5);
        waveAVF.setTitle(getRecString(R.string.ecg_title_AVF), 6);
        waveV1.setTitle(getRecString(R.string.ecg_title_V1), 7);
        waveV2.setTitle(getRecString(R.string.ecg_title_V2), 8);
        waveV3.setTitle(getRecString(R.string.ecg_title_V3), 9);
        waveV4.setTitle(getRecString(R.string.ecg_title_V4), 10);
        waveV5.setTitle(getRecString(R.string.ecg_title_V5), 11);
        waveV6.setTitle(getRecString(R.string.ecg_title_V6), 12);

        // 清空报警信息
        tvEcgAlarm.setText("");
    }

    /*
   * AIDLService
   */
    public void initAidlService_ecg() {
        //绑定服务
        intentEcg12 = new Intent("com.konsung.aidlServer");
        getActivity().startService(intentEcg12);
        getActivity().bindService(intentEcg12, serviceConnection_ecg12,
                Context.BIND_AUTO_CREATE);
    }

    /*
    * serviceConnection
    */
    public ServiceConnection serviceConnection_ecg12 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            aidlServer = ((AIDLServer.MsgBinder) service).getService();

            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {

                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {
                    switch (param) {
                        //血氧
                        case KParamType.SPO2_WAVE://正在检测状态
                            if (isCheckingSpo2) {
                                mWaveForm.setData(bytes);
                                try {
                                    ServiceUtils.savedWave(param,UnitConvertUtil
                                            .bytesToHexString(bytes));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                progressSpo2--;
                                if (progressSpo2 == 0) {
                                    isCheckingSpo2 = false;
                                    reinit_spo2();
                                    tvSpo2Notify.setText(getRecString(R
                                            .string.ecg_check_timeout));
                                    btnMeasureBtnSpo2.setText(getRecString(R
                                            .string.nibp_btn_start));
                                }
                                mDonutProgressSpo2.setProgress(progressSpo2);
                            }
                            break;

                        case KParamType.ECG_I:
                            data.put("i", bytes);
                            if (isCheckingEcg) {
                                //绘图
                                waveI.setData(data.get("i"));
                                Message msg = Message.obtain();
                                msg.arg1 = 1;
                                msg.obj = bytes;
                                //发送数据到Handler保存
                                handlerEcg12.sendMessage(msg);
                                //                                list.append
                                // (1,bytes);
                            }
                            break;
                        case KParamType.ECG_II:
                            data.put("ii", bytes);
                            if (isCheckingEcg) {
                                waveIi.setData(data.get("ii"));
                                Message msg = Message.obtain();
                                msg.arg1 = 2;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_III:
                            data.put("iii", bytes);
                            if (isCheckingEcg) {
                                waveIii.setData(data.get("iii"));
                                Message msg = Message.obtain();
                                msg.arg1 = 3;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_AVR:
                            if (isCheckingEcg) {
                                waveAVR.setData(data.get("avr"));
                                Message msg = Message.obtain();
                                msg.arg1 = 4;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                                progressEcg--;
                                mDonutProgressEcg.setProgress(progressEcg);
                                if (progressEcg == 0) {
                                    isCheckingEcg = false;
                                    isTimeOut = true;
                                    tvMeasureBtnEcg.setText(getRecString(R
                                            .string.nibp_btn_start));
                                    tvEcgNotify.setText(getRecString(R.string
                                            .ecg_check_timeout));
                                    reinit_ecg();
                                }
                                data.put("avr", bytes);
                            }
                            break;
                        case KParamType.ECG_AVL:
                            data.put("avl", bytes);
                            if (isCheckingEcg) {
                                waveAVL.setData(data.get("avl"));
                                Message msg = Message.obtain();
                                msg.arg1 = 5;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_AVF:
                            data.put("avf", bytes);
                            if (isCheckingEcg) {
                                waveAVF.setData(data.get("avf"));
                                Message msg = Message.obtain();
                                msg.arg1 = 6;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V1:
                            data.put("v1", bytes);
                            if (isCheckingEcg) {
                                waveV1.setData(data.get("v1"));
                                Message msg = Message.obtain();
                                msg.arg1 = 7;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V2:
                            data.put("v2", bytes);
                            if (isCheckingEcg) {
                                waveV2.setData(data.get("v2"));
                                Message msg = Message.obtain();
                                msg.arg1 = 8;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V3:
                            data.put("v3", bytes);
                            if (isCheckingEcg) {
                                waveV3.setData(data.get("v3"));
                                Message msg = Message.obtain();
                                msg.arg1 = 9;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V4:
                            data.put("v4", bytes);
                            if (isCheckingEcg) {
                                waveV4.setData(data.get("v4"));
                                Message msg = Message.obtain();
                                msg.arg1 = 10;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V5:
                            data.put("v5", bytes);
                            if (isCheckingEcg) {
                                waveV5.setData(data.get("v5"));
                                Message msg = Message.obtain();
                                msg.arg1 = 11;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        case KParamType.ECG_V6:
                            data.put("v6", bytes);
                            if (isCheckingEcg) {
                                waveV6.setData(data.get("v6"));
                                Message msg = Message.obtain();
                                msg.arg1 = 12;
                                msg.obj = bytes;
                                handlerEcg12.sendMessage(msg);
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendTrend(int param, int value) {
                    int i = value / GlobalConstant.URITREND_FACTOR;
                    String str = valueToString(i);
                    float v_glu = (float) value / GlobalConstant.TREND_FACTOR;

                    float v_wbc = (float) value / 100 / 1000;
                    int hrValue;
                    switch (param) {
                        case KParamType.SPO2_TREND://趋势参数202
                            if (isCheckingSpo2) {
                                if ((Math.abs(spo2Value - value /
                                        GlobalConstant.TREND_FACTOR) < 4)
                                        && value != GlobalConstant
                                        .INVALID_DATA) {
                                    if ((measureCountSpo2++) == 6) {
                                        tvSpo2Trend.setText(String.valueOf
                                                (spo2Value));
                                        tvSpo2PrTv.setText(String.valueOf
                                                (spo2Pr));
                                        measureCountSpo2 = 0;
                                        mWaveForm.reset();
                                        reinit_spo2();
                                        //当血氧值小于90时，进行低血氧报警
                                        if (spo2Value < 90) {
                                            tvSpo2Notify.setText(getRecString
                                                    (R.string.spo2_low_alarm));
                                        } else {
                                            tvSpo2Notify.setText(getRecString
                                                    (R.string
                                                            .spo2_check_complited));
                                        }
                                        GlobalConstant.SPO2_VALUE = spo2Value;
                                        GlobalConstant.SPO2_PR_VALUE = spo2Pr;
                                        ServiceUtils.saveTrend(KParamType
                                                .SPO2_TREND, spo2Value *
                                                GlobalConstant.TREND_FACTOR);
                                        ServiceUtils.saveTrend(KParamType
                                                .SPO2_PR, spo2Pr *
                                                GlobalConstant.TREND_FACTOR);

                                        setSpo2LeffStatus(GlobalConstant
                                                .LEFF_OFF);//设定探头脱落状态，停止测量
                                        isCheckingSpo2 = false;
                                        btnMeasureBtnSpo2.setText
                                                (getRecString(R.string
                                                        .nibp_btn_start));

                                        return;
                                    }
                                } else {
                                    spo2Value = value / GlobalConstant
                                            .TREND_FACTOR;
                                    measureCountSpo2 = 0;
                                }
                            }
                            break;
                        case KParamType.SPO2_PR://趋势参数203
                            spo2Pr = value / GlobalConstant.TREND_FACTOR;
                            break;

                        //心率----------------------
                        case KParamType.ECG_HR:
                            break;
                        case KParamType.RESP_RR:
                            brVaule = value / GlobalConstant.TREND_FACTOR;
                            break;


                         /*体温测量*/
                        case KParamType.TEMP_T1:
                            if((float) value != (float) GlobalConstant.
                                    INVALID_DATA){
                                tvTempT1Tv.setText(String.valueOf((float)
                                        value / 100));
                            }
                            break;
                        case KParamType.TEMP_T2:
                            if((float) value != (float) GlobalConstant.
                                    INVALID_DATA) {
                                tvTempT1Tv.setText(String.valueOf(
                                        (float) value / 100));
                            }
                            break;
                        //红外式体温测量
                        case KParamType.IRTEMP_TREND:

                            tvIrtempTrendTv.setText(String.valueOf((float)
                                    value / GlobalConstant.TREND_FACTOR));
                            GlobalConstant.IR_TEMP_VALUE = (float) value /
                                    GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveTrend(KParamType.IRTEMP_TREND,
                                    value);
                            break;
                        //血压测量
                        case 501:
                            if (value > 0) {
                                ServiceUtils.saveTrend(501, value);
                                tvNibpSysTv.setText(String.valueOf(value /
                                        GlobalConstant.TREND_FACTOR));
                                GlobalConstant.NIBP_SYS_VALUE = value /
                                        GlobalConstant.TREND_FACTOR;
                            }
                            break;
                        case 502:
                            if (measureState == 2) {
                                if (value > 0) {
                                    tvNibpDiaTv.setText(String.valueOf(value /
                                            GlobalConstant.TREND_FACTOR));
                                    GlobalConstant.NIBP_DIA_VALUE = value /
                                            GlobalConstant.TREND_FACTOR;
                                    ServiceUtils.saveTrend(502, value);
                                    tvMeasureBtnNibp.setText(getRecString(R
                                            .string.nibp_btn_start));
                                    showMeasureResult(0);
                                    measureState = 0;
                                }
                            }
                            break;
                        case 503:
                            if (value > 0) {
                                ServiceUtils.saveTrend(503, value);
                                tvNibpMapTv.setText(String.valueOf(value /
                                        GlobalConstant.TREND_FACTOR));
                                GlobalConstant.NIBP_MAP_VALUE = value /
                                        GlobalConstant.TREND_FACTOR;

                            }
                            break;
                        case 504:
                            if (value > 0) {
                                ServiceUtils.saveTrend(504, value);
                                tvNibpPrTv.setText(String.valueOf(value /
                                        GlobalConstant.TREND_FACTOR));
                                GlobalConstant.NIBP_PR_VALUE = value /
                                        GlobalConstant.TREND_FACTOR;
                            }
                            break;

                           /*尿常规趋势参数*/
                        case KParamType.URINERT_LEU:
                            tvUrinertLeuTv.setText(str);
                            GlobalConstant.URINE_LEU_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivLeuIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_UBG:
                            tvUrinertUbgTv.setText(str);
                            GlobalConstant.URINE_UBG_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivUbgIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_ALB:
                            tvUrinertAlbTv.setText(String.valueOf(i));
                            GlobalConstant.URINE_ALB_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (i > 150) {
                                ivAlbIcon.setImageResource(R.drawable
                                        .alarm_high);
                                ivAlbIcon.setVisibility(View.VISIBLE);
                            } else if (i < 10) {
                                ivAlbIcon.setImageResource(R.drawable
                                        .alarm_low);
                                ivAlbIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_PRO:
                            tvUrinertProTv.setText(str);
                            GlobalConstant.URINE_PRO_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivProIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_BIL:
                            tvUrinertBilTv.setText(str);
                            GlobalConstant.URINE_BIL_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivBilIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_GLU:
                            tvUrinertGluTv.setText(str);
                            GlobalConstant.URINE_GLU_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivGluIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        //兼容恩普尿机的VC
                        case KParamType.URINERT_VC:
                            tvUrinertAscTv.setText(str);
                            GlobalConstant.URINE_ASC_VALUE = i;
                            ServiceUtils.saveTrend(KParamType.URINERT_ASC, value);
                            if (!"-".equals(str)) {
                                ivAscIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_ASC:
                            tvUrinertAscTv.setText(str);
                            GlobalConstant.URINE_ASC_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivAscIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_SG:
                            double sg = (double) value / 1000.0f;
                            tvUrinertSgTv.setText(String.format("%.3f", sg));
                            GlobalConstant.URINE_SG_VALUE = sg;
                            ServiceUtils.saveTrend(param, value);
                            if (sg > 1.025) {
                                ivSgIcon.setImageResource(R.drawable
                                        .alarm_high);
                                ivSgIcon.setVisibility(View.VISIBLE);
                            } else if (sg < 1.015) {
                                ivSgIcon.setImageResource(R.drawable.alarm_low);
                                ivSgIcon.setVisibility(View.VISIBLE);
                            }
                            break;

                        //尿常规趋势参数设置
                        case KParamType.URINERT_KET:
                            tvUrinertKetTv.setText(str);
                            GlobalConstant.URINE_KET_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivKetIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_NIT:
                            tvUrinertNitTv.setText(str);
                            GlobalConstant.URINE_NIT_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (!"-".equals(str)) {
                                ivNitIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_CRE:
                            tvUrinertCreTv.setText(String.valueOf(value /
                                    100.0f));
                            GlobalConstant.URINE_CRE_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (i > 26.5) {
                                ivCreIcon.setImageResource(R.drawable
                                        .alarm_high);
                                ivCreIcon.setVisibility(View.VISIBLE);
                            } else if (i < 0.9) {
                                ivCreIcon.setImageResource(R.drawable
                                        .alarm_low);
                                ivCreIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_PH:
                            tvUrinertPhTv.setText(String.valueOf(value /
                                    100.0f));
                            GlobalConstant.URINE_PH_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (i > 8.0) {
                                ivPhIcon.setImageResource(R.drawable
                                        .alarm_high);
                                ivPhIcon.setVisibility(View.VISIBLE);
                            } else if (i < 4.6) {
                                ivPhIcon.setImageResource(R.drawable.alarm_low);
                                ivPhIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_BLD:
                            tvUrinertBldTv.setText(str);
                            GlobalConstant.URINE_BLD_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            //                            aidlServer
                            // .saveToDb2();

                            if (!"-".equals(str)) {
                                ivBldIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        case KParamType.URINERT_CA:
                            tvUrinertCaTv.setText(String.valueOf(i));
                            GlobalConstant.URINE_CA_VALUE = i;
                            ServiceUtils.saveTrend(param, value);
                            if (i > 10) {
                                ivCaIcon.setImageResource(R.drawable
                                        .alarm_high);
                                ivCaIcon.setVisibility(View.VISIBLE);
                            } else if (i < 1.0) {
                                ivCaIcon.setImageResource(R.drawable.alarm_low);
                                ivCaIcon.setVisibility(View.VISIBLE);
                            }
                            break;

                        //血糖参数---------------------------------
                        case KParamType.BLOODGLU_AFTER_MEAL:
                            if (value != GlobalConstant.INVALID_DATA) {
                                isGetValue = true;
                                tvBloodGluTrendTv.setText(String.valueOf
                                        (v_glu));
                                ServiceUtils.saveTrend(KParamType
                                        .BLOODGLU_AFTER_MEAL, value);
                                ServiceUtils.saveToDb2();
                            }
                            break;
                        case KParamType.BLOODGLU_BEFORE_MEAL:
                            if (value != GlobalConstant.INVALID_DATA) {
                                isGetValue = true;
                                tvBloodGluTrendTv.setText(String.valueOf
                                        (v_glu));
                                GlobalConstant.BLOOD_GLU_VALUE = v_glu;
                                ServiceUtils.saveTrend(KParamType
                                        .BLOODGLU_BEFORE_MEAL, value);
                            }
                            break;


                        //白细胞的测量效果

                        case KParamType.BLOOD_WBC:
                            if (value != GlobalConstant.INVALID_DATA) {
                                isGetValue = true;
                                tvBloodWbcTrendTv.setText(String.valueOf
                                        (v_wbc));
                                GlobalConstant.BLOOD_WBC_VALUE = v_wbc;
                                ServiceUtils.saveTrend(KParamType.BLOOD_WBC,
                                        value);
                            }
                            break;

                        //血红蛋白测量数据效果
                        case KParamType.BLOOD_HGB:
                            if (value != GlobalConstant.INVALID_DATA) {
                                float v1 = (float) value / GlobalConstant
                                        .TREND_FACTOR;
                                isGetValue = true;
                                tvBloodHgbTrendTv.setText(String.valueOf(v1));
                                GlobalConstant.BLOOD_HGB_VALUE = v1;
                                ServiceUtils.saveTrend(KParamType.BLOOD_HGB,
                                        value);
                            }
                            break;
                        case KParamType.BLOOD_HCT:
                            if (value != GlobalConstant.INVALID_DATA) {
                                int v1 = (int) value / GlobalConstant
                                        .TREND_FACTOR;
                                isGetValue = true;
                                tvBloodHctTrendTv.setText(String.valueOf(v1));
                                GlobalConstant.BLOOD_HCT_VALUE = v1;
                                ServiceUtils.saveTrend(KParamType.BLOOD_HCT,
                                        value);
                            }
                            break;

                        //尿酸测试数据效果并显示
                        case KParamType.URICACID_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                float temp = (float) value / GlobalConstant
                                        .TREND_FACTOR;
                                tvUricAcidTrendTv.setText(String.valueOf(temp));
                                GlobalConstant.URIC_ACID_VALUE = temp;
                                ServiceUtils.saveTrend(KParamType
                                        .URICACID_TREND, value);
                            }
                            break;


                        case KParamType.CHOLESTEROL_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                float temp = (float) value / 100;
                                tvCholesterolTrendTv.setText(String.valueOf
                                        (temp));
                                GlobalConstant.CHOLESTEROL_VALUE = temp;
                                ServiceUtils.saveTrend(KParamType
                                        .CHOLESTEROL_TREND, value);
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {

                    int leadoff = -1;
                    switch (param) {
                        //血氧
                        case 0x05:
                            setSpo2LeffStatus(value);//设定探头状态
                            break;
                        case 0x10:
                            leadoff = value;
                            if (leadoff == 496 || leadoff == 0) {
                                isEcgConnect = true;
                                if (!isCheckingEcg && !isTimeOut) {
                                    tvEcgNotify.setText(getRecString(R.string
                                            .ecg_pls_keep_quiet_wait_check));
                                }
                            } else if (leadoff == GlobalConstant.INVALID_DATA) {
                                isEcgConnect = false;
                                isCheckingEcg = false;
                                reinit_ecg();
                                tvEcgNotify.setText(getRecString(R.string
                                        .ecg_pls_checkfordevice));
                            } else {
                                isEcgConnect = false;
                                isCheckingEcg = false;
                                reinit_ecg();
                                tvEcgNotify.setText(getRecString(R.string
                                        .ecg_pls_checkforline));
                                tvMeasureBtnEcg.setText(getRecString(R.string
                                        .nibp_btn_start));
                            }
                            break;

                        //血压
                        case 0x07:
                            if (measureState == 1) {
                                if (value == 1) {
                                    measureState = 2;
                                }
                            }
                            break;
                        case 0x02:
                            if (value > 0) {
                                measureState = 0;
                                tvMeasureBtnNibp.setText(getRecString(R
                                        .string.nibp_btn_start));
                                showMeasureResult(value);

                            }
                            break;
                        case 0x04:
                            if (measureState == 2) {
                                tvNibpCuff.setText(getRecString(R.string
                                        .nibp_cuff) + ":"
                                        + (value == GlobalConstant.INVALID_DATA
                                        ? cuffStatic : String.valueOf(value)));
                            }
                            break;
                        case 0x00://体温测量
                            getTempLeffStatus(value);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendPersonalDetail(String name, String idcard, int sex, int type
                        , String pic, String address) {
                    getInfoFromCard(name, idcard, sex, type);
                }


                @Override
                public void send12LeadDiaResult(byte[] bytes) {
                    String diaResult = " "; // 12导诊断结果

                    // 只有在测量过程中才对12导诊断结果进行处理，用户停止测量、
                    // 重新刷新界面等情况下不对12导诊断结果进行处理。
                    if (!isCheckingEcg) {
                        return;
                    }

                    //根据AppDevice中的协议，诊断结果最多有26个数据（不包括时间戳）
                    if (bytes.length != 52) {
                        Log.v("HealthOne", "12 Lead Dia Result len is not " +
                                "right!");
                    }
                    int[] result = new int[26];

                    for (int i = 0; i < 26; i++) {
                        result[i] = (int) (((bytes[i * 2 + 1] & 0x00FF) << 8)
                                | (0x00FF & bytes[i * 2]));
                    }
                    int hrValue = result[0];// HR值
                    int prInterval = result[1];// PR间期
                    int qrsDuration = result[2];// QRS间期, 单位ms
                    int qt = result[3];// QT间期
                    int qtc = result[4];// QTC间期
                    int pAxis = result[5];// P 波轴
                    int qrsAxis = result[6];// QRS波心电轴
                    int tAxis = result[7];// T波心电轴
                    int rv5 = result[8];// RV5, 单位0.01ms
                    int sv1 = result[9];// SV1, 单位0.01ms

                    if (prInterval < 0) {
                        prInterval = (short) -prInterval;
                    }
                    diaResult = String.valueOf(hrValue) + "," + String
                            .valueOf(prInterval) + ","
                            + String.valueOf(qrsDuration) + "," + String
                            .valueOf(qt) + ","
                            + String.valueOf(qtc) + "," + String.valueOf
                            (pAxis) + ","
                            + String.valueOf(qrsAxis) + "," + String.valueOf
                            (tAxis) + ","
                            + String.format("%.2f", (float) rv5 / 100) + ","
                            + String.format("%.2f", (float) sv1 / 100) + ","
                            + String.format("%.2f", ((float) rv5 / 100 +
                            (float) sv1 / 100)) + ",";

                    //根据AppDevice协议，诊断码有16个，但不是所有都有效
                    DiagCodeToText diagCodeToText = new DiagCodeToText();
                    String text;
                    for (int i = 0; i < 16; i++) {
                        for (int j = 0; j < diagCodeToText.ECG_12_LEAD_DIAG_TEXT
                                .length; j++) {
                            String[] str = diagCodeToText
                                    .ECG_12_LEAD_DIAG_TEXT[j].split(":");
                            if (result[10 + i] == Integer.parseInt(str[0])) {
                                diaResult += str[1];
                                if ((str[1] != null) && (!"".equals(str[1]))) {
                                    diaResult += ";";
                                }
                            }
                        }
                    }
                    // 心电测量完成
                    ecgMeasureFinish(hrValue, diaResult);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            reinit_ecg();
            aidlServer = null;
            isCheckingEcg = false;
            isEcgConnect = false;
            tvEcgNotify.setText(getRecString(R.string.ecg_pls_checkfordevice));

                /*血氧测试*/
            mWaveForm.reset();
            mWaveForm.stop();
            mWaveForm.invalidate();
            isCheckingSpo2 = false;
            attachSpo2 = false;
            tvSpo2Notify.setText(getRecString(R.string.ecg_pls_checkfordevice));
        }
    };

    public void saveWave(AIDLServer server, int param, byte[] bytes) {
        try {
            ServiceUtils.savedWave(param, UnitConvertUtil.bytesToHexString(bytes));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*尿常规值转换
    @param
    value 模块传递过来的值
    *@return 显示测量值*/


    private String valueToString(int value) {

        switch (value) {
            case -1:
                return "-";
            case 0:
                return "+-";
            case 1:
                return "+1";
            case 2:
                return "+2";
            case 3:
                return "+3";
            case 4:
                return "+4";
            case 5:
                return "+";
            case 6:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }

    //通过服务提供的值显示探头状态

    public void setSpo2LeffStatus(int value) {
        if (value == 0) {
            attachSpo2 = true;
            isFingerInsert = true;
            if (!isCheckingSpo2) {
                tvSpo2Notify.setText(getRecString(R.string.spo2_waiting));
            }

        } else if (value == 1) {//探头脱落状态
            attachSpo2 = false;
            isCheckingSpo2 = false;
            mWaveForm.reset();
            reinit_spo2();
            tvSpo2Notify.setText(getRecString(R.string.spo2_pls_put_probe));
            btnMeasureBtnSpo2.setText(getRecString(R.string.nibp_btn_start));
        } else if (value == 2) {//手指未插入状态
            attachSpo2 = true;
            isCheckingSpo2 = false;
            isFingerInsert = false;
            mWaveForm.reset();
            reinit_spo2();
            tvSpo2Notify.setText(getRecString(R.string.spo2_pls_put_finger));
            btnMeasureBtnSpo2.setText(getRecString(R.string.nibp_btn_start));
        }
    }


    private void _restartMeasure_spo2() {
        tvSpo2Trend.setText("-?-");
        tvSpo2PrTv.setText("-?-");
        tvSpo2Notify.setText("-?-");
        spo2Value = GlobalConstant.INVALID_DATA;
        measureCountSpo2 = 0;
        mWaveForm.reset();
    }


    private void reinit_spo2() {
        progressSpo2 = 20;
        mDonutProgressSpo2.setProgress(progressSpo2);
        mWaveForm.stop();
        mWaveForm.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    //*************** 体温测试调用方法************************
    private void initView_temp() {
        tvTempT1Tv.setText(UiUitls.getString(R.string.default_value));
        proBarTempProgressBar.setMax(maxEcg);
        if (GlobalConstant.TEMP_VALUE != GlobalConstant.INVALID_DATA) {
            tvTempT1Tv.setText(String.valueOf(GlobalConstant.TEMP_VALUE));
        }
    }


    private void getTempLeffStatus(int value) {
        //探头正常
        if ((0 == (value & 0x01)) || (0 == (value & 0x02))) {
            attachTemp = true;
        }
        //探头脱落
        else {
            attachTemp = false;
            tvTempT1Tv.setText(UiUitls.getString(R.string.default_value));
            tempTrendList.clear();
        }
    }

    private void _restartMeasure() {
        tvTempT1Tv.setText("-?-");
    }


    //**************** 尿常规初始化view**********************
    private void initView_urinert() {
        Iterator iterator = viewUrinert.keySet().iterator();
        while (iterator.hasNext()) {
            viewUrinert.get(iterator.next()).setText(
                    UiUitls.getString(R.string.default_value));
        }
        String str;
        if (GlobalConstant.URINE_LEU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_LEU_VALUE);
            tvUrinertLeuTv.setText(str);
            if (!"-".equals(str)) {
                ivLeuIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_UBG_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_UBG_VALUE);
            tvUrinertUbgTv.setText(str);
            if (!"-".equals(str)) {
                ivUbgIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_ALB_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrinertAlbTv.setText(String.valueOf(GlobalConstant
                    .URINE_ALB_VALUE));
            if (GlobalConstant.URINE_ALB_VALUE > 150) {
                ivAlbIcon.setImageResource(R.drawable.alarm_high);
                ivAlbIcon.setVisibility(View.VISIBLE);
            } else if (GlobalConstant.URINE_ALB_VALUE < 10) {
                ivAlbIcon.setImageResource(R.drawable.alarm_low);
                ivAlbIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_PRO_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_PRO_VALUE);
            tvUrinertProTv.setText(str);
            if (!"-".equals(str)) {
                ivProIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_BIL_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BIL_VALUE);
            tvUrinertBilTv.setText(str);
            if (!"-".equals(str)) {
                ivBilIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_GLU_VALUE);
            tvUrinertGluTv.setText(str);
            if (!"-".equals(str)) {
                ivGluIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_ASC_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_ASC_VALUE);
            tvUrinertAscTv.setText(str);
            if (!"-".equals(str)) {
                ivAscIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_SG_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrinertSgTv.setText(String.format("%.3f", GlobalConstant
                    .URINE_SG_VALUE));

            if (GlobalConstant.URINE_SG_VALUE > 1.025) {
                ivSgIcon.setImageResource(R.drawable.alarm_high);
                ivSgIcon.setVisibility(View.VISIBLE);
            } else if (GlobalConstant.URINE_SG_VALUE < 1.015) {
                ivSgIcon.setImageResource(R.drawable.alarm_low);
                ivSgIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_KET_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_KET_VALUE);
            tvUrinertKetTv.setText(str);
            if (!"-".equals(str)) {
                ivKetIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_NIT_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_NIT_VALUE);
            tvUrinertNitTv.setText(str);
            if (!"-".equals(str)) {
                ivNitIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_CRE_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrinertCreTv.setText(String.format("%.1f", GlobalConstant
                    .URINE_CRE_VALUE));
            if (GlobalConstant.URINE_CRE_VALUE > 26.5) {
                ivCreIcon.setImageResource(R.drawable.alarm_high);
                ivCreIcon.setVisibility(View.VISIBLE);
            } else if (GlobalConstant.URINE_CRE_VALUE < 0.9) {
                ivCreIcon.setImageResource(R.drawable.alarm_low);
                ivCreIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_PH_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrinertPhTv.setText(String.valueOf(GlobalConstant
                    .URINE_PH_VALUE));
            if (GlobalConstant.URINE_PH_VALUE > 8.0) {
                ivPhIcon.setImageResource(R.drawable.alarm_high);
                ivPhIcon.setVisibility(View.VISIBLE);
            } else if (GlobalConstant.URINE_PH_VALUE < 4.6) {
                ivPhIcon.setImageResource(R.drawable.alarm_low);
                ivPhIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_BLD_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BLD_VALUE);
            tvUrinertBldTv.setText(str);
            if (!"-".equals(str)) {
                ivBldIcon.setVisibility(View.VISIBLE);
            }
        }
        if (GlobalConstant.URINE_CA_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrinertCaTv.setText(String.valueOf(GlobalConstant
                    .URINE_CA_VALUE));
            if (GlobalConstant.URINE_CA_VALUE > 10) {
                ivCaIcon.setImageResource(R.drawable.alarm_high);
                ivCaIcon.setVisibility(View.VISIBLE);
            } else if (GlobalConstant.URINE_CA_VALUE < 1.0) {
                ivCaIcon.setImageResource(R.drawable.alarm_low);
                ivCaIcon.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 初始化
     */
    private void initView_irtemp() {
        tvIrtempTrendTv.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.IR_TEMP_VALUE != GlobalConstant.INVALID_DATA) {
            tvIrtempTrendTv.setText(String.valueOf(GlobalConstant
                    .IR_TEMP_VALUE));
        }
    }


    private void initView_nibp() {
        tvNibpSysTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpDiaTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpMapTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpPrTv.setText(UiUitls.getString(R.string.default_value));
        tvNibpCuff.setText("");
        if (GlobalConstant.NIBP_SYS_VALUE != GlobalConstant.INVALID_DATA) {
            tvNibpSysTv.setText(String.valueOf(GlobalConstant.NIBP_SYS_VALUE));
        }
        if (GlobalConstant.NIBP_DIA_VALUE != GlobalConstant.INVALID_DATA) {
            tvNibpDiaTv.setText(String.valueOf(GlobalConstant.NIBP_DIA_VALUE));
        }
        if (GlobalConstant.NIBP_MAP_VALUE != GlobalConstant.INVALID_DATA) {
            tvNibpMapTv.setText(String.valueOf(GlobalConstant.NIBP_MAP_VALUE));
        }
        if (GlobalConstant.NIBP_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvNibpPrTv.setText(String.valueOf(GlobalConstant.NIBP_PR_VALUE));
        }
    }

    private void showMeasureResult(int code) {
        String result = new String();
        switch (code) {
            case 0:
                result = getRecString(R.string.nibbp_result_0);
                break;
            case 1:
                result = getRecString(R.string.nibbp_result_1);
                break;
            case 2:
                result = getRecString(R.string.nibbp_result_2);
                break;
            case 3:
                result = getRecString(R.string.nibbp_result_3);
                break;
            case 4:
                result = getRecString(R.string.nibbp_result_4);
                break;
            case 5:
                result = getRecString(R.string.nibbp_result_5);
                break;
            case 6:
                result = getRecString(R.string.nibbp_result_6);
                break;
            case 7:
            case 8:
                result = getRecString(R.string.nibbp_result_7);
                break;
            case 9:
                result = getRecString(R.string.nibbp_result_8);
                break;
            case 10:
                result = getRecString(R.string.nibbp_result_9);
                break;
            case 11:
                result = getRecString(R.string.nibbp_result_10);
                break;
            case 12:
                result = getRecString(R.string.nibbp_result_11);
                break;
            case 13:
                result = getRecString(R.string.nibbp_result_12);
                break;
            default:
                break;
        }
        tvNibpCuff.setText(result);
    }

    private void initCard() {
        if (GlobalConstant.HEALTH_CARD_WSREAD == null) {
            return;
        }
        mWSRead = GlobalConstant.HEALTH_CARD_WSREAD;
        final int portState = mWSRead.portOpen("USB", 0);//打开usb权限

        if (portState == 0) {//打开端口成功
            try {
                String data = mWSRead.readHealthCard();//读取健康卡
                String[] datas = data.split("\\|");
                String id = datas[0];//卡号
                final String idCard = datas[1];//身份证号；
                final String name = new String(DigitalTrans
                        .hexStringToByteReal(datas[2]), "GBK");//姓名
                final int pSex = 1;//性别
                final int type = 0;//病人类型
                String nation = datas[4];//名族
                String birthday = datas[5];//生日
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //检测是否是新用户，并作出对应的操作;
                        getInfoFromCard(name, idCard, pSex, type);
                    }
                });
            } catch (Exception e) {
            }

        } else {//打开端口失败

        }
    }

    //通过华大一体机刷身份证获取基本信息
    public void brushIdCard() {
        if (GlobalConstant.ID_HEALTH_CARD == null) {
            return;
        }
        hdosUsbDevice = GlobalConstant.ID_HEALTH_CARD;
        int re = 0;
        // For IDcard
        byte[] name = new byte[32];
        byte[] sex = new byte[6];
        byte[] birth = new byte[18];
        byte[] nation = new byte[12];
        byte[] address = new byte[72];
        byte[] Department = new byte[32];
        byte[] IDNo = new byte[38];
        byte[] EffectDate = new byte[18];
        byte[] ExpireDate = new byte[18];
        byte[] ErrMsg = new byte[20];
        byte[] BmpFile = new byte[38556];


        final int pSex = 1;
        final int type = 0;
        String pkName;
        pkName = UiUitls.getContent().getPackageName();
        pkName = "/data/data/" + pkName + "/lib/libwlt2bmp.so";

        try {
            re = hdosUsbDevice.PICC_Reader_ReadIDMsg(BmpFile, name,
                    sex, nation, birth, address, IDNo, Department,
                    EffectDate, ExpireDate, ErrMsg, pkName);
            if (re < 0) {
                return;
            } else {
                hdosUsbDevice.demoBeepOn();
                final String pName = (new String(name, "Unicode"))
                        .replace(" ", "");
                final String idcard = new String(IDNo, "Unicode");
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        getInfoFromCard(pName, idcard, pSex, type);
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception eint) {
            eint.printStackTrace();
        }
    }

    //通过华大一体机刷健康卡获取信息
    public void brushHealthCard() {
        if (GlobalConstant.ID_HEALTH_CARD == null) {
            return;
        }
        hdosUsbDevice = GlobalConstant.ID_HEALTH_CARD;
        try {
            //PSAM上电成功标识
            int flag1 = hdosUsbDevice.powernOnPSAM();
            String password = "123456";
            //PSAM校验标识
            int flag2 = hdosUsbDevice.verificationPSAM(password);
            //健康卡上电标识
            int flag3 = hdosUsbDevice.powernOnHealthCard();
            byte[] KLB = new byte[4];    //卡的类别
            byte[] GFBB = new byte[16];   //规范版本
            byte[] FKJGDM = new byte[64]; //初始化机构编号
            byte[] FKJGZS = new byte[360]; //发卡机构证书
            byte[] FKJGMC = new byte[64]; //初始化机构编号
            byte[] FKRQ = new byte[8];   //发卡日期
            byte[] KYXQ = new byte[8];    //卡有效期
            byte[] KH = new byte[18];      //卡号
            byte[] AQM = new byte[3];      //安全码
            byte[] XPXLH = new byte[10];      //芯片序列号
            byte[] YYCSDM = new byte[8];      //应用城市代码
            byte[] ERR = new byte[64];       //错误信息
            int flag4 = 0;
            //获取健康卡发卡机构信息是否成功标识
            flag4 = hdosUsbDevice.readIssuerAgency(KLB, GFBB, FKJGDM, FKJGZS,
                    FKJGMC, FKRQ, KH, AQM, XPXLH, YYCSDM, ERR);

            if (flag1 > 0 && flag2 == 1 && flag3 > 0 && flag4 >= 0) {
                int nRet = 0;
                byte[] chsXM = new byte[30];            //姓名
                byte[] chsXB = new byte[2 + 1];         //性别代码
                byte[] chsMZ = new byte[2 + 1];         //民族代码
                byte[] chsCSRQ = new byte[8 + 1];       //出生日期，YYYYMMDD格式
                byte[] chsSFZH = new byte[18];          //居民身份证号

                final int pSex = 0;
                final int type = 0;

                nRet = hdosUsbDevice.readPersonalInformation(chsXM, chsXB,
                        chsMZ, chsCSRQ, chsSFZH);
                if (nRet < 0) {
                    return;
                } else {
                    final String pName = new String(chsXM, "GBK");
                    final String idcard = new String(chsSFZH);
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            getInfoFromCard(pName, idcard, pSex, type);
                        }
                    });
                }
            } else {
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception eint) {
            eint.printStackTrace();
        }
    }

    /**
     * 通过刷卡获取信息更新view
     * @param name
     * @param idcard
     * @param sex
     * @param type
     */
    private void getInfoFromCard(String name, String idcard, int sex, int
            type) {
        if(!isConnected) {
            return;
        }
        llDetail.setVisibility(View.VISIBLE);
        tvCheckIdCard.setVisibility(View.GONE);
        //                    性别
        char s = idcard.charAt(16);
        sex = Integer.valueOf(s);
        //                    生日
        String born = idcard.substring(6, 14);
        born += "0000";
        Date date;
        Date date1;
        SimpleDateFormat sdf = new SimpleDateFormat
                ("yyyyMMddhhmm");
        //                    判断是否成人
        try {
            date = sdf.parse(born);
            calendar.setTime(date);
            calendar.set(Calendar.YEAR, 18);
            date1 = new Date();
            if (date1.before(calendar.getTime())) {
                type = 1;//非成年人
            } else {
                type = 0;//成年人
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        etPatientIdCard.setText(idcard);
        etPatientName.setText(name);
        //血型默认设置为不详
        spnPatientBlood.setSelection(4);
        if (sex % 2 == 0) {
            spnPatientSex.setSelection(2);
        } else if (sex % 2 == 1) {
            spnPatientSex.setSelection(1);
        }
        spnPatientType.setSelection(type);
    }

    /**
     * 心电测量完成。收到12导诊断结果代表心电测量完成。
     *
     * @param hrValue 心率值
     * @param diaResult 12导诊断结果
     */
    private void ecgMeasureFinish(int hrValue, String diaResult) {
        isCheckingEcg = false;

        reinit_ecg();
        tvMeasureBtnEcg.setText(getRecString(R.string.nibp_btn_start));
        tvEcgNotify.setText(getRecString(R.string.ecg_check_complited));

        // 心率报警
        executeHrAlarm(hrValue);

        // 设置显示值到UI
        tvEcgHr.setText(String.valueOf(hrValue));
        GlobalConstant.ECG_PR_VALUE = hrValue;

        // 填充值到存储对象
        ServiceUtils.setECGUpdate();
        ServiceUtils.saveTrend(KParamType.ECG_HR, hrValue * GlobalConstant
                .TREND_FACTOR);
        ServiceUtils.saveTrend(KParamType.RESP_RR, brVaule * GlobalConstant
                .TREND_FACTOR);
        ServiceUtils.saveEcgDiagnoseResult(diaResult);
//        aidlServer.saveToDb2();
    }
}
