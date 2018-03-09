package com.konsung.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatEditText;
import com.konsung.R;
import com.konsung.activity.BaseActivity;
import com.konsung.activity.MeasureActivity;
import com.konsung.activity.QuickCheckModifyActivity;
import com.konsung.bean.BloodReferenceBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QuickBean;
import com.konsung.defineview.WaveFormEcg;
import com.konsung.floatbuttons.DonutProgress;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.DBDataUtil;
import com.konsung.util.DiagCodeToText;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.NumUtil;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.QuickCheckHelpUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;
import com.konsung.util.global.BeneParamValue;
import com.konsung.util.global.BloodFour;
import com.konsung.util.global.BloodMem;
import com.konsung.util.global.BmiParam;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.IrTemp;
import com.konsung.util.global.Nibp;
import com.konsung.util.global.Spo;
import com.konsung.util.global.SugarBloodParam;
import com.konsung.util.global.Urine;
import com.konsung.util.global.WbcParamValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by DLX on 2016/9/27 0027.
 */
public class QuickCheckFragmentNew extends BaseFragment {
    //记录配置的标记
    public static final String SET = "set";
    public static final int RESULT_CODE = 1;
    TextView tvTp;
    TextView tvBs;
    TextView tvShrink;
    TextView tvSpread;
    TextView btnMeasureBp;
    TextView btnMeasureBo;
    TextView tvLeu;
    TextView tvNit;
    TextView tvUbg;
    TextView tvPro;
    TextView tvPh;
    TextView tvSg;
    TextView tvBld;
    TextView tvKet;
    TextView tvBil;
    TextView tvGlu;
    //尿钙
    TextView tvCa;
    TextView tvAsc;
    TextView tvAlb;
    TextView tvCr;
    TextView tvBo;
    TextView tvPr;
    TextView tvCuff;
    LinearLayout llCuff;
    TextView tvHgb;
    TextView tvHgbHigh;
    TextView tvHgbLow;
    TextView tvHtc;
    TextView tvHtcHigh;
    TextView tvHtcLow;
    TextView tvChol;
    TextView tvTrig;
    TextView tvHdl;
    TextView tvLdl;
    DonutProgress donutProgress;
    TextView tvTd;
    TextView tvNs;
    TextView tvUrineMax;
    TextView tvUrineMin;
    TextView tvBloodMax;
    TextView tvBloodMin;
    LinearLayout caLayout;
    LinearLayout maLayout;
    LinearLayout crLayout;
    TextView tvDinnerBefore;
    TextView tvDinnerAfter;
    //1.1.0新增id
    //血压相关视图
    LinearLayout llBloodPressureUnmeausre;
    RelativeLayout rlBloodPressureResult;
    TextView bpState;
    RelativeLayout rlBloodPressureBg;
    //血氧相关视图
    RelativeLayout rlBoBg;
    LinearLayout llBoResult;
    TextView boStateValue;
    LinearLayout llBoMeasuing;
    TextView tvBoUnmeasure;
    //体温相关视图
    TextView tvTempUnmeasure;
    RelativeLayout rlTempResult;
    TextView tempState;
    RelativeLayout rlTempBg;
    //血糖相关
    TextView tvXtUnit;
    LinearLayout llJiantou1;
    LinearLayout llJiantou2;
    //尿酸相关
    TextView nsUnit;
    //总胆固醇相关
    TextView tdUnit;
    //血红蛋白相关
    TextView tvBloodRedUnmeasure;
    LinearLayout llXhdbResult;
    TextView tvXhdbState;
    RelativeLayout rlXhdbBg;
    //BMI视图
    RelativeLayout rlBmiBg;
    TextView bmiUnmeasure;
    RelativeLayout rlBmiResult;
    TextView tvBmi;
    FlatEditText etHeight;
    FlatEditText etWeight;
    //糖化视图
    TextView tvNgsp;
    TextView tvIfcc;
    TextView tvEag;
    //白细胞视图
    RelativeLayout rlWbcBg;
    TextView tvWbcUnmeasure;
    RelativeLayout rlWbcResult;
    TextView tvWbcState;
    TextView tvWbcValue;
    LinearLayout llRoot;
    //血脂相关
    //参数配置值
    private int paramValue = 0;
    //对应配置项视图集合
    private List<QuickBean> listView = new ArrayList<QuickBean>();
    //中间存值集合
    private List<QuickBean> lisTemp = new ArrayList<QuickBean>();
    //所有视图集合
    private List<Integer> listAllView = new ArrayList<Integer>();
    LinearLayout ll1;
    LinearLayout ll2;
    LinearLayout ll3;
    LinearLayout ll4;
    LinearLayout ll5;
    LinearLayout ll6;
    List<LinearLayout> ls = new ArrayList<>();
    private int spo2Value = GlobalConstant.INVALID_DATA;
    private int spo2Pr = GlobalConstant.INVALID_DATA;
    //测试次数
    private int measureCountSpo2 = 0;
    //血氧探头指示
    private boolean attachSpo2 = false;
    //测量状态
    private boolean isCheckingSpo2 = false;
    //手指
    private boolean isFingerInsert = false;
    private int maxSpo2 = GlobalNumber.FIVETEEN_NUMBER;
    private int progressSpo2 = GlobalNumber.FIVETEEN_NUMBER;
    private HashMap<Integer, TextView> viewUrinert;
    //体温参数声明begin
    private boolean attachTemp = false;
    //已经得到数据
    private boolean isGetValue;
    // 以下变量使用于模拟测量,不需要时可删除
    private List<Integer> tempTrendList;
    //血红蛋白参考值
    private String hgbMax = "";
    private String hgbMin = "";
    //红细胞参考值
    private String hctMax = "";
    private String hctMin = "";
    //bmi值
    private String bmiValue;
    private int brVaule = GlobalConstant.INVALID_DATA;
    //是否已经开始检查
    private boolean isCheckingEcg = false;
    //ecg是否有连接
    private boolean isEcgConnect = false;
    private boolean isTimeOut = false;
    private HashMap<Integer, WaveFormEcg> waves;
    private String rhAlarm = " ";    //心律报警
    private String diaResult = " "; // 12导诊断结果
    private int measureState = 0;              // 血压测量状态
    private int cuffStatic;   //白细胞参数设置
    private BloodReferenceBean bloodReferenceBean;
    private View mView;
    private String idCard;
    private int sexType;
    //默认餐前
    private boolean isBeforeMeal = true;
    //餐前餐后的值记录
    private String beforeMeal = "-?-";
    private String afterMeal = "-?-";
    //血糖餐前餐后点击字体大小变化
    private final float textSizeMin = 10f;
    private final float textSizeMax = 14f;
    LayoutInflater inflater; //布局加载器
    //体温类型
    private int tempType;
    private Context context = null;
    //字符截取长度
    private final int strLength = 3;
    private QuickCheckHelpUtils quickCheckHelpUtils = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_quick_check_new_other, null);
        context = getActivity();
        quickCheckHelpUtils = new QuickCheckHelpUtils(context);
        GlobalConstant.isInQuickPage = true;
        this.inflater = inflater;
        getAllLayout();
        initViewLayout();
        getSex();
        initViewId();
        initMeasureListener();
        initSpo2();
        initViewNibp();
        initViewHgb();
        //初始化测量数据的监听
        waves = new HashMap<>();
        // 设置心电导联类型，旧版本的硬件，拔交流电时会导致参数板断电重启，为规避此问题，在进入心电界面时再发送一次导
        // 联设置命令
        int value = SpUtils.getSpInt(getActivity().getApplicationContext(),
                "sys_config", "ecg_lead_system", EcgDefine.ECG_12_LEAD);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM, value);
        tvBo.setText(UiUitls.getString(R.string.default_value));
        tvPr.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.SPO2_VALUE != GlobalConstant.INVALID_DATA) {
            tvBo.setText(String.valueOf(GlobalConstant.SPO2_VALUE));
        }
        if (GlobalConstant.SPO2_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvPr.setText(String.valueOf(GlobalConstant.SPO2_PR_VALUE));
        }
        spo2Value = GlobalConstant.INVALID_DATA;
        measureCountSpo2 = 0;
        setSpo2LeffStatus(GlobalConstant.LEFF_OFF);
        //初始化体温view
        tempType = SpUtils.getSpInt(getActivity().getApplicationContext(),
                "sys_config", "temp_type", TempDefine.TEMP_CONTACT);
        if (tempType == TempDefine.TEMP_CONTACT) {
            //接触式
            initViewTemp();
        } else if (tempType == TempDefine.TEMP_INFRARED) {
            //红外式体温
            initViewIrtemp();
        }
        //血糖初始化view
        initViewBlood();
        //初始化尿常规view
        initViewUrinert();
        initLipidsEvent();
        initLipids();
        initSuageBhd();
        initWbc();
        initEvent();
        return mView;
    }

    /**
     * 尿常规map存view
     */
    private void saveViewByMap() {
        viewUrinert = new HashMap<>();
        viewUrinert.put(R.id.tv_leu, tvLeu);
        viewUrinert.put(R.id.tv_ubg, tvUbg);
        viewUrinert.put(R.id.tv_pro, tvPro);
        viewUrinert.put(R.id.tv_bil, tvBil);
        viewUrinert.put(R.id.tv_glu, tvGlu);
        viewUrinert.put(R.id.tv_ca, tvCa);
        viewUrinert.put(R.id.tv_asc, tvAsc);
        viewUrinert.put(R.id.tv_alb, tvAlb);
        viewUrinert.put(R.id.tv_cr, tvCr);
        viewUrinert.put(R.id.tv_ket, tvKet);
        viewUrinert.put(R.id.tv_nit, tvNit);
        viewUrinert.put(R.id.tv_ph, tvPh);
        viewUrinert.put(R.id.tv_bld, tvBld);
        viewUrinert.put(R.id.tv_sg, tvSg);
    }

    /**
     * 获取性别
     */
    private void getSex() {
        if (context == null) {
            context = getActivity();
        }
        idCard = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        List<PatientBean> patientBeanList = DBDataUtil.getPatientByIdCard(idCard);
        if (null != patientBeanList && patientBeanList.size() > 0) {
            sexType = patientBeanList.get(0).getSex();
        } else {
            sexType = 1;
        }
        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.CURRENT_SEX
                , sexType);
    }

    /**
     * 监听控件点击事件
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //当点击快检页面，跳转到单项页面时，测量停止
            refresgBlood();
            refresgSpo2();
            QuickBean tagBean = (QuickBean) v.getTag();
            //对于的type在bean内有说明
            String type = tagBean.getType() + "";
            ((BaseActivity) context).pushActivityWithMessage(context, MeasureActivity.class
                    , GlobalConstant.QUICK_FLAG, type);
        }
    };
    /**
     * 长按监听事件
     */
    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //停止血压测量
            refresgBlood();
            modifyQuick();
            return false;
        }
    };
    /**
     * 刷新快检页面数据
     */
    public void refreshMeasureData() {
        getSex();
        //血氧刷新
        initSpo2();
        //血压刷新
        initViewNibp();
        //体温刷新
        if (tempType == TempDefine.TEMP_CONTACT) {
            //接触式
            initViewTemp();
        } else if (tempType == TempDefine.TEMP_INFRARED) {
            //红外式体温
            initViewIrtemp();
        }
        //血液三项刷新
        initViewBlood();
        //尿常规刷新
        initViewUrinert();
        //血红蛋白刷新
        initViewHgb();
        //血脂四项刷新
        initLipids();
        //糖化血红蛋白刷新
        initSuageBhd();
        //bmi刷新
        initBmi();
        //白细胞刷新
        initWbc();
    }

    /**
     *控件id绑定
     */
    private void initViewId() {
        tvTp = (TextView) mView.findViewById(R.id.tv_tp);
        tvBs = (TextView) mView.findViewById(R.id.tv_bs);
        tvShrink = (TextView) mView.findViewById(R.id.tv_shrink);
        tvSpread = (TextView) mView.findViewById(R.id.tv_spread);
        btnMeasureBp = (TextView) mView.findViewById(R.id.btn_measure_bp);
        btnMeasureBo = (TextView) mView.findViewById(R.id.btn_measure_bo);
        tvLeu = (TextView) mView.findViewById(R.id.tv_leu);
        tvNit = (TextView) mView.findViewById(R.id.tv_nit);
        tvUbg = (TextView) mView.findViewById(R.id.tv_ubg);
        tvPro = (TextView) mView.findViewById(R.id.tv_pro);
        tvPh = (TextView) mView.findViewById(R.id.tv_ph);
        tvSg = (TextView) mView.findViewById(R.id.tv_sg);
        tvBld = (TextView) mView.findViewById(R.id.tv_bld);
        tvKet = (TextView) mView.findViewById(R.id.tv_ket);
        tvBil = (TextView) mView.findViewById(R.id.tv_bil);
        tvGlu = (TextView) mView.findViewById(R.id.tv_glu);
        tvCa = (TextView) mView.findViewById(R.id.tv_ca);
        tvAsc = (TextView) mView.findViewById(R.id.tv_asc);
        tvAlb = (TextView) mView.findViewById(R.id.tv_alb);
        tvCr = (TextView) mView.findViewById(R.id.tv_cr);
        tvBo = (TextView) mView.findViewById(R.id.tv_bo);
        tvPr = (TextView) mView.findViewById(R.id.tv_pr);
        tvCuff = (TextView) mView.findViewById(R.id.tv_cuff);
        llCuff = (LinearLayout) mView.findViewById(R.id.ll_cuff);
        tvHgb = (TextView) mView.findViewById(R.id.tv_hgb_value);
        tvHgbHigh = (TextView) mView.findViewById(R.id.hgb_high);
        tvHgbLow = (TextView) mView.findViewById(R.id.hgb_low);
        tvHtc = (TextView) mView.findViewById(R.id.tv_htc_value);
        tvHtcHigh = (TextView) mView.findViewById(R.id.htc_high);
        tvHtcLow = (TextView) mView.findViewById(R.id.htc_low);
        tvChol = (TextView) mView.findViewById(R.id.tv_chol_value);
        tvTrig = (TextView) mView.findViewById(R.id.tv_trig_value);
        tvHdl = (TextView) mView.findViewById(R.id.tv_hdl_value);
        tvLdl = (TextView) mView.findViewById(R.id.tv_ldl_value);
        donutProgress = (DonutProgress) mView.findViewById(R.id.donut_progress);
        tvTd = (TextView) mView.findViewById(R.id.tv_td);
        tvNs = (TextView) mView.findViewById(R.id.tv_ns);
        tvUrineMax = (TextView) mView.findViewById(R.id.urine_max);
        tvUrineMin = (TextView) mView.findViewById(R.id.urine_min);
        tvBloodMax = (TextView) mView.findViewById(R.id.blood_max);
        tvBloodMin = (TextView) mView.findViewById(R.id.blood_min);
        caLayout = (LinearLayout) mView.findViewById(R.id.ca_layout);
        maLayout = (LinearLayout) mView.findViewById(R.id.ma_layout);
        crLayout = (LinearLayout) mView.findViewById(R.id.cr_layout);
        tvDinnerBefore = (TextView) mView.findViewById(R.id.dinner_before);
        tvDinnerAfter = (TextView) mView.findViewById(R.id.dinner_after);
        //1.1.0新增id
        //血压相关视图
        llBloodPressureUnmeausre = (LinearLayout) mView.findViewById(R.id
                .ll_blood_pressure_unmeasure);
        rlBloodPressureResult = (RelativeLayout) mView.findViewById(R.id
                .rl_blood_pressure_result);
        bpState = (TextView) mView.findViewById(R.id.bp_state_value);
        rlBloodPressureBg = (RelativeLayout) mView.findViewById(R.id.rl_blood_pressure_bg);
        //血氧相关视图
        rlBoBg = (RelativeLayout) mView.findViewById(R.id.rl_bo_bg);
        llBoResult = (LinearLayout) mView.findViewById(R.id.ll_bo_first_ui);
        boStateValue = (TextView) mView.findViewById(R.id.bo_state_value);
        llBoMeasuing = (LinearLayout) mView.findViewById(R.id.ll_bo_second_ui);
        tvBoUnmeasure = (TextView) mView.findViewById(R.id.bo_unmeasure);
        //体温相关视图
        tvTempUnmeasure = (TextView) mView.findViewById(R.id.temp_unmeasure);
        rlTempResult = (RelativeLayout) mView.findViewById(R.id.rl_temp_result_ui);
        tempState = (TextView) mView.findViewById(R.id.temp_state);
        rlTempBg = (RelativeLayout) mView.findViewById(R.id.rl_temp_bg);
        //血糖相关
        tvXtUnit = (TextView) mView.findViewById(R.id.xt_value_unit);
        llJiantou1 = (LinearLayout) mView.findViewById(R.id.ll_jiantou_1);
        llJiantou2 = (LinearLayout) mView.findViewById(R.id.ll_jiantou_2);
        //尿酸相关
        nsUnit = (TextView) mView.findViewById(R.id.ns_unit);
        //总胆固醇相关
        tdUnit = (TextView) mView.findViewById(R.id.td_unit);
        //血红蛋白相关
        tvBloodRedUnmeasure = (TextView) mView.findViewById(R.id.blood_red_unmeasure);
        llXhdbResult = (LinearLayout) mView.findViewById(R.id.ll_xhdb_first_ui);
        tvXhdbState = (TextView) mView.findViewById(R.id.xhdb_state_value);
        rlXhdbBg = (RelativeLayout) mView.findViewById(R.id.rl_xhdb_bg);
        //BMI视图
        rlBmiBg = (RelativeLayout) mView.findViewById(R.id.rl_bmi_bg);
        bmiUnmeasure = (TextView) mView.findViewById(R.id.bmi_unmeasure);
        rlBmiResult = (RelativeLayout) mView.findViewById(R.id.rl_bmi_result_ui);
        tvBmi = (TextView) mView.findViewById(R.id.tv_bmi);
        etHeight = (FlatEditText) mView.findViewById(R.id.bmi_height_et);
        etWeight = (FlatEditText) mView.findViewById(R.id.bmi_weight_et);
        //糖化视图
        tvNgsp = (TextView) mView.findViewById(R.id.tv_ngsp_value);
        tvIfcc = (TextView) mView.findViewById(R.id.tv_ifcc_value);
        tvEag = (TextView) mView.findViewById(R.id.tv_eag_value);
        //白细胞视图
        rlWbcBg = (RelativeLayout) mView.findViewById(R.id.rl_wbc_bg);
        tvWbcUnmeasure = (TextView) mView.findViewById(R.id.wbc_unmeasure);
        rlWbcResult = (RelativeLayout) mView.findViewById(R.id.rl_wbc_result_ui);
        tvWbcState = (TextView) mView.findViewById(R.id.wbc_state);
        tvWbcValue = (TextView) mView.findViewById(R.id.tv_wbc);
        llRoot = (LinearLayout) mView.findViewById(R.id.ll_root);
    }

    /**
     * 监听事件
     */
    private void initEvent() {
        llRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                modifyQuick();
                return false;
            }
        });
    }

    /**
     * 跳转到修改界面的方法
     */
    private void modifyQuick() {
        Intent intent = new Intent(getActivity(), QuickCheckModifyActivity.class);
        Bundle b = new Bundle();
        QuickBean bean = new QuickBean();
        bean.setQuickBeen(listView);
        b.putSerializable(SET, bean);
        intent.putExtras(b);
        startActivityForResult(intent, RESULT_CODE);
    }

    @Override
    public void onStop() {
        super.onStop();
        GlobalConstant.isInQuickPage = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        initBmi();
        initBmiEvent();
        GlobalConstant.isInQuickPage = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_CODE == resultCode) {
            QuickBean serializableExtra = (QuickBean) data.getSerializableExtra(SET);
            listView = serializableExtra.getQuickBeen();
            int value = 0;
            value = value | (0x01 << 0);
            for (int j = 0; j < listView.size(); j++) {
                QuickBean bean = listView.get(j);
                for (int i = 0; i < listAllView.size(); i++) {
                    //血氧
                    if (bean.getType() == i) {
                        value = value | (0x01 << (i + 1));
                    }
                }
            }
            paramValue = value;
            SpUtils.saveToSp(getActivity(), GlobalConstant.PARAM_CONFIGS
                    , GlobalConstant.PARAM_KEY, value);
            loadView();
            initViewId();
            initSpo2();
            initViewNibp();
            initViewHgb();
            //初始化测量数据的监听
            initMeasureListener();
            initViewUrinert();
            initLipidsEvent();
            initLipids();
            initSuageBhd();
            initWbc();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != etHeight) {
            UiUitls.hideSoftInput(getActivity(), etHeight);
        }
    }

    /**
     * 初始化糖化血红蛋白值
     */
    private void initSuageBhd() {
        if ((paramValue & (0x01 << GlobalNumber.NINE_NUMBER)) == 0) {
            return;
        }
        quickCheckHelpUtils.fillValue(tvNgsp, SugarBloodParam.NGSP_MIN
                , SugarBloodParam.NGSP_MAX, KParamType.HBA1C_NGSP, GlobalConstant.HBA1C_NGSP);
        quickCheckHelpUtils.fillValue(tvIfcc, SugarBloodParam.IFCC_MIN
                , SugarBloodParam.IFCC_MAX, KParamType.HBA1C_IFCC, GlobalConstant.HBA1C_IFCC);
        quickCheckHelpUtils.fillValue(tvEag, SugarBloodParam.EAG_MIN
                , SugarBloodParam.EAG_MAX, KParamType.HBA1C_EAG, GlobalConstant.HBA1C_EAG);
    }

    /**
     * 初始化BMI
     */
    private void initBmi() {
        if ((paramValue & (0x01 << GlobalNumber.TEN_NUMBER)) == 0) {
            return;
        }
        GlobalConstant.currentView = etHeight;
        //身高赋值
        etHeight.setText(GlobalConstant.HEIGHT);
        //体重赋值
        etWeight.setText(GlobalConstant.WEIGHT);
        //BMI赋值
        if (!TextUtils.isEmpty(GlobalConstant.BMI)) {
            float bmi = Float.valueOf(GlobalConstant.BMI);
            bmiUnmeasure.setVisibility(View.GONE);
            rlBmiResult.setVisibility(View.VISIBLE);
            if (bmi > BmiParam.MAX_VALUE || bmi < BmiParam.MIN_VALUE) {
                rlBmiBg.setBackgroundResource(R.drawable.bg_above_high);
            } else {
                rlBmiBg.setBackgroundResource(R.drawable.bg_above);
            }
            tvBmi.setText(GlobalConstant.BMI);
        } else {
            bmiUnmeasure.setVisibility(View.VISIBLE);
            bmiUnmeasure.setText(UiUitls.getString(R.string.bmi_unmeasure));
            rlBmiResult.setVisibility(View.GONE);
            rlBmiBg.setBackgroundResource(R.drawable.bg_above);
        }
    }

    /**
     * 初始化白细胞
     */
    private void initWbc() {
        if ((paramValue & (0x01 << GlobalNumber.ELEVEN_NUMBER)) == 0) {
            return;
        }
        if (GlobalConstant.BLOOD_WBC_VALUE != GlobalConstant.INVALID_DATA) {
            float wbcValue = GlobalConstant.BLOOD_WBC_VALUE;
            tvWbcUnmeasure.setVisibility(View.GONE);
            rlWbcResult.setVisibility(View.VISIBLE);
            tvWbcValue.setText(wbcValue + "");
            if (wbcValue > WbcParamValue.MAX_VALUE || wbcValue < WbcParamValue.MIN_VALUE) {
                rlWbcBg.setBackgroundResource(R.drawable.bg_above_high);
                tvWbcState.setText(getRecString(R.string.wbc_unnormal));
            } else {
                tvWbcState.setText(getRecString(R.string.param_white));
                rlWbcBg.setBackgroundResource(R.drawable.bg_above);
            }
        } else {
            tvWbcUnmeasure.setVisibility(View.VISIBLE);
            rlWbcResult.setVisibility(View.GONE);
            rlWbcBg.setBackgroundResource(R.drawable.bg_above);
        }
    }

    /**
     * 初始化BMI模块EditText输入监听
     */
    private void initBmiEvent() {
        if ((paramValue & (0x01 << GlobalNumber.TEN_NUMBER)) == 0) {
            return;
        }
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (GlobalConstant.isInQuickPage) {
                    if (s.toString().contains(".")) {
                        int i = s.toString().indexOf(".");
                        String substring = s.toString().substring(i, s.length());
                        if (substring.length() > strLength) {
                            int i1 = substring.length() - strLength;
                            etWeight.setText(s.toString().substring(0, s.length() - i1));
                            etWeight.setSelection(s.length() - i1);
                        }
                    } else {
                        if (s.length() > strLength) {
                            etWeight.setText(s.toString().substring(0, strLength));
                            etWeight.setSelection(strLength);
                        }
                    }
                    String height = etHeight.getText().toString().trim();
                    String weight = etWeight.getText().toString().trim();
                    if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)) {
                        if (!height.startsWith(".") && !weight.startsWith(".")
                                && !height.endsWith(".") && !weight.endsWith(".")) {
                            //计算BMI
                            //BMI不需要区分男女
                            bmiValue = UiUitls.countBmi(height, weight);
                            if (Double.valueOf(bmiValue) <= GlobalNumber.FIVTY_NUMBER) {
                                //BMI赋值
                                if (!TextUtils.isEmpty(bmiValue)) {
                                    float bmi = Float.valueOf(bmiValue);
                                    bmiUnmeasure.setVisibility(View.GONE);
                                    rlBmiResult.setVisibility(View.VISIBLE);
                                    if (bmi > BmiParam.MAX_VALUE || bmi < BmiParam.MIN_VALUE) {
                                        rlBmiBg.setBackgroundResource(R.drawable.bg_above_high);
                                    } else {
                                        rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                    }
                                    tvBmi.setText(bmiValue);
                                } else {
                                    bmiUnmeasure.setText(UiUitls
                                            .getString(R.string.bmi_unmeasure));
                                    bmiUnmeasure.setVisibility(View.VISIBLE);
                                    rlBmiResult.setVisibility(View.GONE);
                                    rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                }
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = height;
                                GlobalConstant.WEIGHT = weight;
                                ServiceUtils.saveBmiValue(bmiValue, height, weight);
                                ServiceUtils.saveToDb2();
                            } else {
                                bmiUnmeasure.setText(UiUitls.getString(R.string.bmi_invalidate));
                                bmiUnmeasure.setVisibility(View.VISIBLE);
                                rlBmiResult.setVisibility(View.GONE);
                                rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                bmiValue = "";
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = "";
                                GlobalConstant.WEIGHT = "";
                                ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                        , GlobalConstant.WEIGHT);
                                ServiceUtils.saveToDb2();
                            }
                        }
                    } else {
                        bmiValue = "";
                        GlobalConstant.BMI = bmiValue;
                        GlobalConstant.HEIGHT = "";
                        GlobalConstant.WEIGHT = "";
                        if (!GlobalConstant.isAllUploadDataRefresh) {
                            ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                    , GlobalConstant.WEIGHT);
                            ServiceUtils.saveToDb2();
                        }
                        bmiUnmeasure.setVisibility(View.VISIBLE);
                        rlBmiResult.setVisibility(View.GONE);
                        rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                    }
                }
            }
        });

        etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (GlobalConstant.isInQuickPage) {
                    if (s.toString().contains(".")) {
                        int i = s.toString().indexOf(".");
                        String substring = s.toString().substring(i, s.length());
                        if (substring.length() > strLength) {
                            int i1 = substring.length() - strLength;
                            etHeight.setText(s.toString().substring(0, s.length() - i1));
                            etHeight.setSelection(s.length() - i1);
                        }
                    } else {
                        if (s.length() > strLength) {
                            etHeight.setText(s.toString().substring(0, strLength));
                            etHeight.setSelection(strLength);
                        }
                    }
                    String weight = etWeight.getText().toString().trim();
                    String height = etHeight.getText().toString().trim();
                    if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)) {
                        if (!height.startsWith(".") && !weight.startsWith(".")
                                && !height.endsWith(".") && !weight.endsWith(".")) {
                            //计算BMI
                            //BMI不需要区分男女
                            bmiValue = UiUitls.countBmi(height, weight);
                            if (Double.valueOf(bmiValue) <= GlobalNumber.FIVTY_NUMBER) {
                                //BMI赋值
                                if (!TextUtils.isEmpty(bmiValue)) {
                                    float bmi = Float.valueOf(bmiValue);
                                    bmiUnmeasure.setVisibility(View.GONE);
                                    rlBmiResult.setVisibility(View.VISIBLE);
                                    if (bmi > BmiParam.MAX_VALUE || bmi < BmiParam.MIN_VALUE) {
                                        rlBmiBg.setBackgroundResource(R.drawable.bg_above_high);
                                    } else {
                                        rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                    }
                                    tvBmi.setText(bmiValue);
                                } else {
                                    bmiUnmeasure.setText(UiUitls
                                            .getString(R.string.bmi_unmeasure));
                                    bmiUnmeasure.setVisibility(View.VISIBLE);
                                    rlBmiResult.setVisibility(View.GONE);
                                    rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                }
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = height;
                                GlobalConstant.WEIGHT = weight;
                                ServiceUtils.saveBmiValue(bmiValue, height, weight);
                                ServiceUtils.saveToDb2();
                            } else {
                                bmiUnmeasure.setText(UiUitls.getString(R.string.bmi_invalidate));
                                bmiUnmeasure.setVisibility(View.VISIBLE);
                                rlBmiResult.setVisibility(View.GONE);
                                rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                                bmiValue = "";
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = "";
                                GlobalConstant.WEIGHT = "";
                                ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                        , GlobalConstant.WEIGHT);
                                ServiceUtils.saveToDb2();
                            }
                        }
                    } else {
                        bmiValue = "";
                        GlobalConstant.BMI = bmiValue;
                        GlobalConstant.HEIGHT = "";
                        GlobalConstant.WEIGHT = "";
                        if (!GlobalConstant.isAllUploadDataRefresh) {
                            ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                    , GlobalConstant.WEIGHT);
                            ServiceUtils.saveToDb2();
                        }
                        bmiUnmeasure.setVisibility(View.VISIBLE);
                        rlBmiResult.setVisibility(View.GONE);
                        rlBmiBg.setBackgroundResource(R.drawable.bg_above);
                    }
                }
            }
        });

        etHeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etWeight.getText().toString().length();
                    etWeight.requestFocus(length);
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceUtils.setOnMessageSendListener(null);
        measureState = 0;
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        isCheckingSpo2 = false;
        progressSpo2 = GlobalNumber.FIVETEEN_NUMBER;
        maxSpo2 = GlobalNumber.FIVETEEN_NUMBER;
        //每个父layout与其里面的所有子视图解绑，否则，子视图不能被重复动态加载
        if (ll1 != null) {
            ll1.removeAllViews();
        }
        if (ll2 != null) {
            ll2.removeAllViews();
        }
        if (ll3 != null) {
            ll3.removeAllViews();
        }
        if (ll4 != null) {
            ll4.removeAllViews();
        }
        if (ll6 != null) {
            ll6.removeAllViews();
        }
    }

    /**
     * 初始化血液三项
     */
    private void initViewBlood() {
        if ((paramValue & (0x01 << GlobalNumber.FOUR_NUMBER)) == 0) {
            return;
        }
        if (GlobalConstant.BLOODGLUSTATE.equals("0")) {
            //餐前
            isBeforeMeal = true;
        } else {
            //餐后
            isBeforeMeal = false;
            if (GlobalConstant.BLOOD_GLU_VALUE == GlobalConstant.INVALID_DATA) {
                GlobalConstant.BLOODGLUSTATE = "0";
                isBeforeMeal = true;
            }
        }
        initSelectBtn();
        tvBs.setText(UiUitls.getString(R.string.default_value));
        tvTd.setText(UiUitls.getString(R.string.default_value));
        tvNs.setText(UiUitls.getString(R.string.default_value));
        if (isBeforeMeal) {
            tvBloodMin.setText(BeneParamValue.XT_VALUE_MIN + "");
            tvBloodMax.setText(BeneParamValue.XT_VALUE_MAX + "");
        } else {
            tvBloodMin.setText(BeneParamValue.XT_AFTER_VALUE_MIN + "");
            tvBloodMax.setText(BeneParamValue.XT_AFTER_VALUE_MAX + "");
        }
        //有测量值的情况下去判断是餐前还是餐后的值
        if (GlobalConstant.BLOOD_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            if (isBeforeMeal) {
                tvDinnerBefore.setTextSize(UiUitls.dpToPx(textSizeMax, getResources()));
                tvDinnerAfter.setTextSize(UiUitls.dpToPx(textSizeMin, getResources()));
                llJiantou1.setVisibility(View.VISIBLE);
                llJiantou2.setVisibility(View.INVISIBLE);
            } else {
                tvDinnerBefore.setTextSize(UiUitls.dpToPx(textSizeMin, getResources()));
                tvDinnerAfter.setTextSize(UiUitls.dpToPx(textSizeMax, getResources()));
                llJiantou1.setVisibility(View.INVISIBLE);
                llJiantou2.setVisibility(View.VISIBLE);
            }
            tvBs.setText(String.valueOf(GlobalConstant.BLOOD_GLU_VALUE));
            tvXtUnit.setVisibility(View.VISIBLE);
            UiUitls.compareRangeForSugar(Float.valueOf(tvBloodMin.getText().toString())
                    , Float.valueOf(tvBloodMax.getText().toString()), tvBs);
        } else {
            tvXtUnit.setVisibility(View.VISIBLE);
            //没有血糖值默认为餐前默认值显示
            tvDinnerBefore.setTextSize(UiUitls.dpToPx(textSizeMax, context.getResources()));
            tvDinnerAfter.setTextSize(UiUitls.dpToPx(textSizeMin, context.getResources()));
            llJiantou1.setVisibility(View.VISIBLE);
            llJiantou2.setVisibility(View.INVISIBLE);
            tvBs.setText(R.string.default_value);
            tvBs.setTextColor(context.getResources().getColor(R.color.normal_color));
        }
        //总胆固醇
        if (GlobalConstant.CHOLESTEROL_VALUE != GlobalConstant.INVALID_DATA) {
            tvTd.setText(String.valueOf(GlobalConstant.CHOLESTEROL_VALUE));
        } else {
            tvTd.setText(R.string.default_value);
        }
        UiUitls.compareRangeForSugar(BeneParamValue.CHOL_VALUE_MIN
                , BeneParamValue.CHOL_VALUE_MAX, tvTd);
        //尿酸
        if (sexType == 1) {
            tvUrineMax.setText(BeneParamValue.NS_VALUE_MAX + "");
            tvUrineMin.setText(BeneParamValue.NS_VALUE_MIN + "");
        } else if (sexType == 0) {
            tvUrineMax.setText(BeneParamValue.NS_VALUE_MAXG + "");
            tvUrineMin.setText(BeneParamValue.NS_VALUE_MING + "");
        }
        if (GlobalConstant.URIC_ACID_VALUE != GlobalConstant.INVALID_DATA) {
            tvNs.setText(String.valueOf(GlobalConstant.URIC_ACID_VALUE));
        } else {
            tvNs.setText(R.string.default_value);
        }
        UiUitls.compareRangeForSugar(Float.valueOf(tvUrineMin.getText().toString())
                , Float.valueOf(tvUrineMax.getText().toString()), tvNs);
    }

    /**
     * 初始化餐前餐后的切换事件
     */
    private void initSelectBtn() {
        tvDinnerBefore.setOnClickListener(onClickListener);
        tvDinnerAfter.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvDinnerBefore) {
                if (!isBeforeMeal) {
                    isBeforeMeal = true;
                    tvDinnerBefore.setTextSize(UiUitls.dpToPx(textSizeMax, getResources()));
                    tvDinnerAfter.setTextSize(UiUitls.dpToPx(textSizeMin, getResources()));
                    llJiantou1.setVisibility(View.VISIBLE);
                    llJiantou2.setVisibility(View.INVISIBLE);
                    tvBloodMin.setText(BeneParamValue.XT_VALUE_MIN + "");
                    tvBloodMax.setText(BeneParamValue.XT_VALUE_MAX + "");
                    if (!tvBs.getText().toString().equals("-?-")
                            && !TextUtils.isEmpty(tvBs.getText().toString())) {
                        //表示测量了餐后数据
                        //保存餐后数据
                        GlobalConstant.BLOODGLUSTATE = "1";
                        afterMeal = tvBs.getText().toString();
                        tvBs.setText(getString(R.string.unknown_value));
                        tvBs.setTextColor(context.getResources().getColor(R.color.normal_color));
                    } else {
                        GlobalConstant.BLOODGLUSTATE = "0";
                        tvBs.setText(beforeMeal);
                    }
                }
            } else if (v == tvDinnerAfter) {
                if (isBeforeMeal) {
                    isBeforeMeal = false;
                    tvDinnerBefore.setTextSize(UiUitls.dpToPx(textSizeMin, getResources()));
                    tvDinnerAfter.setTextSize(UiUitls.dpToPx(textSizeMax, getResources()));
                    llJiantou1.setVisibility(View.INVISIBLE);
                    llJiantou2.setVisibility(View.VISIBLE);
                    tvBloodMin.setText(BeneParamValue.XT_AFTER_VALUE_MIN + "");
                    tvBloodMax.setText(BeneParamValue.XT_AFTER_VALUE_MAX + "");
                    if (!tvBs.getText().toString().equals("-?-")
                            && !TextUtils.isEmpty(tvBs.getText().toString())) {
                        //表示测量了餐前数据
                        //保存餐前数据
                        GlobalConstant.BLOODGLUSTATE = "0";
                        beforeMeal = tvBs.getText().toString();
                        tvBs.setText(getString(R.string.unknown_value));
                        tvBs.setTextColor(context.getResources().getColor(R.color.normal_color));
                    } else {
                        GlobalConstant.BLOODGLUSTATE = "1";
                        tvBs.setText(afterMeal);
                    }
                }
            }
            UiUitls.compareRangeForSugar(Float.valueOf(tvBloodMin.getText().toString())
                    , Float.valueOf(tvBloodMax.getText().toString()), tvBs);
        }
    };

    /**
     * 初始化血氧值
     */
    private void initSpo2() {
        if (GlobalConstant.SPO2_VALUE != GlobalConstant.INVALID_DATA) {
            tvBoUnmeasure.setVisibility(View.GONE);
            llBoResult.setVisibility(View.VISIBLE);
            tvBo.setText(String.valueOf(GlobalConstant.SPO2_VALUE));
            if (GlobalConstant.SPO2_VALUE > Spo.SPO2_HIGH
                    || GlobalConstant.SPO2_VALUE < Spo.SPO2_LOW) {
                boStateValue.setText(getString(R.string.bo_unnormal));
                rlBoBg.setBackgroundResource(R.drawable.bg_above_high);
            } else {
                boStateValue.setText(getString(R.string.bo_normal));
                rlBoBg.setBackgroundResource(R.drawable.bg_above);
            }
        } else {
            tvBoUnmeasure.setVisibility(View.VISIBLE);
            llBoResult.setVisibility(View.GONE);
            rlBoBg.setBackgroundResource(R.drawable.bg_above);
        }
        llBoMeasuing.setVisibility(View.GONE);
        if (GlobalConstant.SPO2_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvPr.setText(String.valueOf(GlobalConstant.SPO2_PR_VALUE));
        }
        btnMeasureBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachSpo2 && !isCheckingSpo2 && isFingerInsert) {
                    llBoResult.setVisibility(View.GONE);
                    tvBoUnmeasure.setVisibility(View.GONE);
                    llBoMeasuing.setVisibility(View.VISIBLE);
                    rlBoBg.setBackgroundResource(R.drawable.bg_above);
                    //开始测量
                    reinitSpo2();
                    isCheckingSpo2 = true;
                    //开始测量
                    restartMeasureSpo2();
                    btnMeasureBo.setText(getRecString(R.string.nibp_btn_stop));
                    UiUitls.toast(context, getRecString(R.string.spo2_isChecking));
                } else {
                    //等待测量
                    btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
                    reinitSpo2();
                    isCheckingSpo2 = false;
                    if (isFingerInsert) {
                        UiUitls.toast(context, getRecString(R.string.spo2_waiting));
                    } else if (attachSpo2 && !isFingerInsert) {
                        UiUitls.toast(context, getRecString(R.string.spo2_pls_put_finger));
                    } else if (!attachSpo2) {
                        //请插入血氧探头
                        UiUitls.toast(context, getRecString(R.string.spo2_pls_put_probe));
                    }
                }
            }
        });
    }

    /**
     * 初始化血脂警报事件
     */
    private void initLipidsEvent() {
        if ((paramValue & (0x01 << GlobalNumber.EIGHT_NUMBER)) == 0) {
            return;
        }
        tvChol.setText(UiUitls.getString(R.string.default_value));
        tvTrig.setText(UiUitls.getString(R.string.default_value));
        tvHdl.setText(UiUitls.getString(R.string.default_value));
        tvLdl.setText(UiUitls.getString(R.string.default_value));
        //设置超值数据字体显红
        tvChol.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_CHOL_ALARM_LOW
                , GlobalConstant.LIPIDS_CHOL_ALARM_HIGH, tvChol));
        tvTrig.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_TRIG_ALARM_LOW
                , GlobalConstant.LIPIDS_TRIG_ALARM_HIGH, tvTrig));
        tvHdl.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_HDL_ALARM_LOW
                , GlobalConstant.LIPIDS_HDL_ALARM_HIGH, tvHdl));
        tvLdl.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_LDL_ALARM_LOW
                , GlobalConstant.LIPIDS_LDL_ALARM_HIGH, tvLdl));
    }

    /**
     * 初始化血脂
     */
    private void initLipids() {
        if ((paramValue & (0x01 << GlobalNumber.EIGHT_NUMBER)) == 0) {
            return;
        }
        if (GlobalConstant.LIPIDS_CHOL_VALUE != GlobalConstant.INVALID_DATA) {
            tvChol.setText(quickCheckHelpUtils.getFormatterStr(KParamType.LIPIDS_CHOL
                    , GlobalConstant.LIPIDS_CHOL_VALUE));
        } else {
            tvChol.setText(R.string.default_value);
            tvChol.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.LIPIDS_TRIG_VALUE != GlobalConstant.INVALID_DATA) {
            tvTrig.setText(quickCheckHelpUtils.getFormatterStr(KParamType.LIPIDS_TRIG
                    , GlobalConstant.LIPIDS_TRIG_VALUE));
        } else {
            tvTrig.setText(R.string.default_value);
            tvTrig.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.LIPIDS_HDL_VALUE != GlobalConstant.INVALID_DATA) {
            tvHdl.setText(quickCheckHelpUtils.getFormatterStr(KParamType.LIPIDS_HDL
                    , GlobalConstant.LIPIDS_HDL_VALUE));
        } else {
            tvHdl.setText(R.string.default_value);
            tvHdl.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.LIPIDS_LDL_VALUE != GlobalConstant.INVALID_DATA) {
            tvLdl.setText(quickCheckHelpUtils.getFormatterStr(KParamType.LIPIDS_LDL
                    , GlobalConstant.LIPIDS_LDL_VALUE));
        } else {
            tvLdl.setText(R.string.default_value);
            tvLdl.setTextColor(getResources().getColor(R.color.mesu_text));
        }
    }

    /**
     * 初始化血红蛋白显示
     */
    private void initViewHgb() {
        if ((paramValue & (0x01 << GlobalNumber.SEVEN_NUMBER)) == 0) {
            return;
        }
        if (sexType == 1) {
            tvHgbHigh.setText(BloodMem.MAN_BLOOD_MAX + "");
            tvHgbLow.setText(BloodMem.MAN_BLOOD_MIN + "");
            tvHtcHigh.setText(BloodMem.MAN_HOGIN_MAX + "");
            tvHtcLow.setText(BloodMem.MAN_HOGIN_MIN + "");
        } else if (sexType == 0) {
            tvHgbHigh.setText(BloodMem.WOMAN_BLOOD_MAX + "");
            tvHgbLow.setText(BloodMem.WOMAN_BLOOD_MIN + "");
            tvHtcHigh.setText(BloodMem.WOMAN_HOGIN_MAX + "");
            tvHtcLow.setText(BloodMem.WOMAN_HOGIN_MIN + "");
        }
        hgbMax = tvHgbHigh.getText().toString();
        hgbMin = tvHgbLow.getText().toString();
        hctMax = tvHtcHigh.getText().toString();
        hctMin = tvHtcLow.getText().toString();
        tvHgb.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HGB_VALUE != GlobalConstant.INVALID_DATA) {
            tvHgb.setText(String.valueOf(GlobalConstant.BLOOD_HGB_VALUE));
            tvBloodRedUnmeasure.setVisibility(View.GONE);
            llXhdbResult.setVisibility(View.VISIBLE);
            UiUitls.changePicByValue(Float.valueOf(hgbMin), Float.valueOf(hgbMax)
                    , GlobalConstant.BLOOD_HGB_VALUE, rlXhdbBg, tvXhdbState
                    , getString(R.string.blood_red_unnormal));
        } else {
            tvBloodRedUnmeasure.setVisibility(View.VISIBLE);
            llXhdbResult.setVisibility(View.GONE);
            rlXhdbBg.setBackgroundResource(R.drawable.bg_above);
        }
        tvHtc.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HCT_VALUE != GlobalConstant.INVALID_DATA) {
            tvHtc.setText(String.valueOf(GlobalConstant.BLOOD_HCT_VALUE));
        }
    }

    /**
     * 初始化服务监听
     */
    public void initMeasureListener() {
        ServiceUtils.setOnMessageSendListener(null);
        ServiceUtils.setOnMessageSendListenerQuick(new ServiceUtils.OnMessageSendListener() {
            @Override
            public void sendParaStatus(String name, String version) {

            }

            @Override
            public void sendWave(int param, byte[] bytes) {
                switch (param) {
                    //血氧
                    case KParamType.SPO2_WAVE://正在检测状态
                        if (isCheckingSpo2) {
                            try {
                                ServiceUtils.savedWave(param
                                        , UnitConvertUtil.bytesToHexString(bytes));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            progressSpo2--;
                            if (progressSpo2 == 0) {
                                isCheckingSpo2 = false;
                                reinitSpo2();
                                UiUitls.toast(context, getRecString(R.string.ecg_check_timeout));
                                btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
                            }
                            donutProgress.setProgress(progressSpo2);
                        } else {
                            reinitSpo2();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void sendTrend(int param, int value) {
                if (value == GlobalConstant.INVALID_DATA) {
                    return;
                }
                int i = value / GlobalConstant.URITREND_FACTOR;
                String str = valueToString(i);
                float vGlu = (float) value / GlobalConstant.TREND_FACTOR;
                float temp;
                if (value == GlobalConstant.valueMin) {
                    temp = GlobalConstant.TEN_VALUE_MIN;
                } else if (value == GlobalConstant.valueMax) {
                    temp = GlobalConstant.HUNDRED_VALUE;
                } else {
                    temp = value / GlobalConstant.SWITCH_VALUE;
                }
                switch (param) {
                    case KParamType.SPO2_TREND:
                        //趋势参数202
                        if (isCheckingSpo2) {
                            if ((Math.abs(spo2Value - value /
                                    GlobalConstant.TREND_FACTOR) < GlobalNumber.FOUR_NUMBER)
                                    && value != GlobalConstant.INVALID_DATA) {
                                if ((measureCountSpo2++) == GlobalNumber.SIX_NUMBER) {
                                    tvBo.setText(String.valueOf(spo2Value));
                                    tvPr.setText(String.valueOf(spo2Pr));
                                    measureCountSpo2 = 0;
                                    reinitSpo2();
                                    //当血氧值小于94时，进行低血氧报警
                                    if (spo2Value < Spo.SPO2_LOW) {
                                        UiUitls.toast(context
                                                , getRecString(R.string.spo2_low_alarm));
                                    } else {
                                        UiUitls.toast(context
                                                , getRecString(R.string.spo2_check_complited));
                                    }
                                    llBoMeasuing.setVisibility(View.INVISIBLE);
                                    llBoResult.setVisibility(View.VISIBLE);
                                    UiUitls.changePicByValue(Spo.SPO2_LOW, Spo.SPO2_HIGH
                                            , spo2Value, rlBoBg, boStateValue
                                            , getString(R.string.bo_unnormal));
                                    GlobalConstant.SPO2_VALUE = spo2Value;
                                    GlobalConstant.SPO2_PR_VALUE = spo2Pr;
                                    ServiceUtils.saveTrend(KParamType.SPO2_TREND
                                            , spo2Value * GlobalConstant.TREND_FACTOR);
                                    ServiceUtils.saveTrend(KParamType.SPO2_PR
                                            , spo2Pr * GlobalConstant.TREND_FACTOR);
                                    ServiceUtils.saveToDb2();
                                    //设定探头脱落状态，停止测量
                                    setSpo2LeffStatus(GlobalConstant.LEFF_OFF);
                                    isCheckingSpo2 = false;
                                    btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
                                    return;
                                }
                            } else {
                                spo2Value = value / GlobalConstant.TREND_FACTOR;
                                measureCountSpo2 = 0;
                            }
                        }
                        break;
                    case KParamType.SPO2_PR://趋势参数203
                        spo2Pr = value / GlobalConstant.TREND_FACTOR;
                        break;
                    case KParamType.RESP_RR:
                        brVaule = value / GlobalConstant.TREND_FACTOR;
                        break;
                    //体温测量
                    case KParamType.TEMP_T1:
                        tvTp.setText(String.valueOf((float) value / GlobalConstant.TREND_FACTOR));
                        UiUitls.compareRange(IrTemp.LOW, IrTemp.HIGH, tvTp);
                        break;
                    case KParamType.TEMP_T2:
                        tvTp.setText(String.valueOf((float) value / GlobalConstant.TREND_FACTOR));
                        UiUitls.compareRange(IrTemp.LOW, IrTemp.HIGH, tvTp);
                        break;
                    //红外式体温测量
                    case KParamType.IRTEMP_TREND:
                        tvTp.setText(String.valueOf((float) value / GlobalConstant.TREND_FACTOR));
                        GlobalConstant.IR_TEMP_VALUE = (float) value / GlobalConstant.TREND_FACTOR;
                        tvTempUnmeasure.setVisibility(View.GONE);
                        rlTempResult.setVisibility(View.VISIBLE);
                        UiUitls.changePicByValue(IrTemp.LOW, IrTemp.HIGH
                                , GlobalConstant.IR_TEMP_VALUE, rlTempBg, tempState
                                , getString(R.string.temp_unnormal));
                        ServiceUtils.saveTrend(KParamType.IRTEMP_TREND, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //血压测量
                    //收缩压
                    case KParamType.NIBP_SYS:
                        if (value > 0) {
                            llCuff.setVisibility(View.GONE);
                            rlBloodPressureResult.setVisibility(View.VISIBLE);
                            ServiceUtils.saveTrend(KParamType.NIBP_SYS, value);
                            tvShrink.setText(String.valueOf(value / GlobalConstant.TREND_FACTOR));
                            GlobalConstant.NIBP_SYS_VALUE = value / GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    //舒张压
                    case KParamType.NIBP_DIA:
                        if (measureState == 2) {
                            if (value > 0) {
                                tvSpread.setText(String
                                        .valueOf(value / GlobalConstant.TREND_FACTOR));
                                GlobalConstant.NIBP_DIA_VALUE
                                        = value / GlobalConstant.TREND_FACTOR;
                                ServiceUtils.saveTrend(KParamType.NIBP_DIA, value);
                                btnMeasureBp.setText(getRecString(R.string.nibp_btn_start));
                                showMeasureResult(0);
                                measureState = 0;
                                ServiceUtils.saveToDb2();
                            }
                        }
                        break;
                    //平均压
                    case KParamType.NIBP_MAP:
                        if (value > 0) {
                            ServiceUtils.saveTrend(KParamType.NIBP_MAP, value);
                            GlobalConstant.NIBP_MAP_VALUE = value / GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    //脉率
                    case KParamType.NIBP_PR:
                        if (value > 0) {
                            ServiceUtils.saveTrend(KParamType.NIBP_PR, value);
                            GlobalConstant.NIBP_PR_VALUE = value / GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    //尿常规趋势参数
                    case KParamType.URINERT_LEU:
                        tvLeu.setText(str);
                        GlobalConstant.URINE_LEU_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_UBG:
                        tvUbg.setText(str);
                        GlobalConstant.URINE_UBG_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_PRO:
                        tvPro.setText(str);
                        GlobalConstant.URINE_PRO_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_BIL:
                        tvBil.setText(str);
                        GlobalConstant.URINE_BIL_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_GLU:
                        tvGlu.setText(str);
                        GlobalConstant.URINE_GLU_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //兼容恩普尿机的VC
//                        case KParamType.URINERT_VC:
//                            tvVc.setText(str);
//                            GlobalConstant.URINE_ASC_VALUE = i;
//                            aidlServer.saveTrend(KParamType.URINERT_ASC, value);
//                            if (!"-".equals(str)) {
//                                tvAlarmVc.setText("↑");
//                            }
//                            break;
                    case KParamType.URINERT_ASC:
                        tvAsc.setText(str);
                        GlobalConstant.URINE_ASC_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        if (!str.equals("-")) {
                            tvAsc.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            tvAsc.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.mesu_text));
                        }
                        ServiceUtils.saveToDb2();
                        break;
                    //兼容恩普尿机的VC
                    case KParamType.URINERT_VC:
                        tvAsc.setText(str);
                        GlobalConstant.URINE_ASC_VALUE = i;
                        if (!str.equals("-")) {
                            tvAsc.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            tvAsc.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.mesu_text));
                        }
                        ServiceUtils.saveTrend(KParamType.URINERT_ASC, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_CA:
                        tvCa.setText(str);
                        GlobalConstant.URINE_CA_VALUE = i;
                        if (!"-".equals(str)) {
                            tvCa.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.high_color));
                        }
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_ALB:
                        tvAlb.setText(str);
                        GlobalConstant.URINE_ALB_VALUE = i;
                        if (!"-".equals(str)) {
                            tvAlb.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.high_color));
                        }
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_CRE:
                        tvCr.setText(str);
                        GlobalConstant.URINE_CRE_VALUE = i;
                        if (!"-".equals(str)) {
                            tvCr.setTextColor(UiUitls.getContent().getResources()
                                    .getColor(R.color.high_color));
                        }
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_SG:
                        double sg = (double) value / GlobalConstant.THOSOUND_VALUE;
                        tvSg.setText(String.format("%.3f", sg));
                        GlobalConstant.URINE_SG_VALUE = sg;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //尿常规趋势参数设置
                    case KParamType.URINERT_KET:
                        tvKet.setText(str);
                        GlobalConstant.URINE_KET_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_NIT:
                        tvNit.setText(str);
                        GlobalConstant.URINE_NIT_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_PH:
                        float ph = value / GlobalConstant.SWITCH_VALUE;
                        tvPh.setText(String.valueOf(value / GlobalConstant.SWITCH_VALUE));
                        GlobalConstant.URINE_PH_VALUE = ph;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.URINERT_BLD:
                        tvBld.setText(str);
                        GlobalConstant.URINE_BLD_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //血糖参数
                    case KParamType.BLOODGLU_AFTER_MEAL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            isGetValue = true;
                            tvBs.setText(String.valueOf(vGlu));
                            ServiceUtils.saveTrend(KParamType.BLOODGLU_AFTER_MEAL, value);
                            ServiceUtils.saveToDb2();
                            float v1 = (float) value / GlobalConstant.TREND_FACTOR;
                            GlobalConstant.BLOOD_GLU_VALUE = v1;
                            UiUitls.compareRangeForSugar(Float.valueOf(tvBloodMin.getText()
                                    .toString()), Float.valueOf(tvBloodMax.getText().toString())
                                    , tvBs);
                        }
                        break;
                    case KParamType.BLOODGLU_BEFORE_MEAL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            tvXtUnit.setVisibility(View.VISIBLE);
                            isGetValue = true;
                            tvBs.setText(String.valueOf(vGlu));
                            float v1 = (float) value / GlobalConstant.TREND_FACTOR;
                            GlobalConstant.BLOOD_GLU_VALUE = v1;
                            if (isBeforeMeal) {
                                //餐前测量数据
                                GlobalConstant.BLOODGLUSTATE = "0";
                                afterMeal = getString(R.string.default_value);
                            } else {
                                //餐后测量数据
                                GlobalConstant.BLOODGLUSTATE = "1";
                                beforeMeal = getString(R.string.default_value);
                            }
                            ServiceUtils.saveBloodGluState(GlobalConstant.BLOODGLUSTATE);
                            ServiceUtils.saveTrend(KParamType.BLOODGLU_BEFORE_MEAL, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRangeForSugar(Float.valueOf(tvBloodMin.getText()
                                    .toString()), Float.valueOf(tvBloodMax.getText().toString())
                                    , tvBs);
                        }
                        break;
                    //尿酸测试数据效果并显示
                    case KParamType.URICACID_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            nsUnit.setVisibility(View.VISIBLE);
                            float tempNs = (float) value / GlobalConstant.TREND_FACTOR;
                            tvNs.setText(String.valueOf(tempNs));
                            GlobalConstant.URIC_ACID_VALUE = tempNs;
                            ServiceUtils.saveTrend(KParamType.URICACID_TREND, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRangeForSugar(Float.valueOf(tvUrineMin.getText()
                                    .toString()), Float.valueOf(tvUrineMax.getText().toString())
                                    , tvNs);
                        }
                        break;
                    //总胆固醇
                    case KParamType.CHOLESTEROL_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            tdUnit.setVisibility(View.VISIBLE);
                            float tempTd = (float) value / GlobalConstant.TREND_FACTOR;
                            tvTd.setText(String.valueOf(tempTd));
                            GlobalConstant.CHOLESTEROL_VALUE = tempTd;
                            ServiceUtils.saveTrend(KParamType.CHOLESTEROL_TREND, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRangeForSugar(BeneParamValue.CHOL_VALUE_MIN
                                    , BeneParamValue.CHOL_VALUE_MAX, tvTd);
                        }
                        break;
                    case KParamType.BLOOD_HGB:
                        if (value != GlobalConstant.INVALID_DATA) {
                            //由于血红蛋白里是 g/dL 再除以10.保存到数据库也默认除以10
                            float v1 = (float) value / GlobalConstant.TREND_FACTOR
                                    / GlobalConstant.TEN_VALUE * GlobalConstant.TREND_HGB;
                            float v2 = NumUtil.trans2FloatValue(v1, 1);
                            isGetValue = true;
                            tvHgb.setText(String.valueOf(v2));
                            GlobalConstant.BLOOD_HGB_VALUE = v2;
                            int hgbValue = (int) (v2 * GlobalConstant.TREND_FACTOR);
                            ServiceUtils.saveTrend(KParamType.BLOOD_HGB, hgbValue);
                            ServiceUtils.saveToDb2();
                            tvBloodRedUnmeasure.setVisibility(View.GONE);
                            llXhdbResult.setVisibility(View.VISIBLE);
                            UiUitls.changePicByValue(Float.valueOf(hgbMin)
                                    , Float.valueOf(hgbMax), GlobalConstant.BLOOD_HGB_VALUE
                                    , rlXhdbBg, tvXhdbState
                                    , getRecString(R.string.blood_red_unnormal));
                        }
                        break;
                    case KParamType.BLOOD_HCT:
                        if (value != GlobalConstant.INVALID_DATA) {
                            int v1 = value / GlobalConstant.TREND_FACTOR;
                            isGetValue = true;
                            tvHtc.setText(String.valueOf(v1));
                            GlobalConstant.BLOOD_HCT_VALUE = (int) v1;
                            ServiceUtils.saveTrend(KParamType.BLOOD_HCT, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.LIPIDS_CHOL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = quickCheckHelpUtils
                                    .getFormatterStr(KParamType.LIPIDS_CHOL, temp);
                            tvChol.setText(data);
                            GlobalConstant.LIPIDS_CHOL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_CHOL, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRange(BloodFour.TCMIN, BloodFour.TCMAX, tvChol);
                        }
                        break;
                    case KParamType.LIPIDS_TRIG:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = quickCheckHelpUtils
                                    .getFormatterStr(KParamType.LIPIDS_TRIG, temp);
                            tvTrig.setText(data);
                            GlobalConstant.LIPIDS_TRIG_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_TRIG, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRange(BloodFour.TGMIN, BloodFour.TGMAX, tvTrig);
                        }
                        break;
                    case KParamType.LIPIDS_HDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = quickCheckHelpUtils
                                    .getFormatterStr(KParamType.LIPIDS_HDL, temp);
                            tvHdl.setText(data);
                            GlobalConstant.LIPIDS_HDL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_HDL, value);
                            ServiceUtils.saveToDb2();
                            UiUitls.compareRange(BloodFour.HDLMIN, BloodFour.HDLMAX, tvHdl);
                        }
                        break;
                    case KParamType.LIPIDS_LDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = quickCheckHelpUtils
                                    .getFormatterStr(KParamType.LIPIDS_LDL, temp);
                            GlobalConstant.LIPIDS_LDL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_LDL, value);
                            ServiceUtils.saveToDb2();
                            tvLdl.setText(data);
                            UiUitls.compareRange(BloodFour.LDLMIN, BloodFour.LDLMAX, tvLdl);
                        }
                        break;
                    //糖化-ngsp
                    case KParamType.HBA1C_NGSP:
                        quickCheckHelpUtils.fillValue(tvNgsp, SugarBloodParam.NGSP_MIN
                                , SugarBloodParam.NGSP_MAX, KParamType.HBA1C_NGSP, temp);
                        GlobalConstant.HBA1C_NGSP = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_NGSP, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //糖化-ifcc
                    case KParamType.HBA1C_IFCC:
                        quickCheckHelpUtils.fillValue(tvIfcc, SugarBloodParam.IFCC_MIN
                                , SugarBloodParam.IFCC_MAX, KParamType.HBA1C_IFCC, temp);
                        GlobalConstant.HBA1C_IFCC = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_IFCC, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //糖化-eag
                    case KParamType.HBA1C_EAG:
                        quickCheckHelpUtils.fillValue(tvEag, SugarBloodParam.EAG_MIN
                                , SugarBloodParam.EAG_MAX, KParamType.HBA1C_EAG, temp);
                        GlobalConstant.HBA1C_EAG = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_EAG, value);
                        ServiceUtils.saveToDb2();
                        break;
                    //白细胞
                    case KParamType.BLOOD_WBC:
                        float v = (float) value / GlobalConstant.WBC_FACTOR;
                        tvWbcUnmeasure.setVisibility(View.GONE);
                        rlWbcResult.setVisibility(View.VISIBLE);
                        tvWbcValue.setText(String.valueOf(v));
                        if (v > WbcParamValue.MAX_VALUE || v < WbcParamValue.MIN_VALUE) {
                            rlWbcBg.setBackgroundResource(R.drawable.bg_above_high);
                            tvWbcState.setText(getRecString(R.string.wbc_unnormal));
                        } else {
                            rlWbcBg.setBackgroundResource(R.drawable.bg_above);
                            tvWbcState.setText(getRecString(R.string.param_white));
                        }
                        GlobalConstant.BLOOD_WBC_VALUE = v;
                        ServiceUtils.saveTrend(KParamType.BLOOD_WBC, value);
                        ServiceUtils.saveToDb2();
                        break;
                    default:
                        break;
                }
                setWarm(param);
            }

            @Override
            public void sendConfig(int param, int value) {
                setEcgConnectStatus(param, value);
            }

            @Override
            public void sendPersonalDetail(String name, String idcard, int sex, int type
                    , String pic, String address) {

            }

            @Override
            public void send12LeadDiaResult(byte[] bytes) {
                //根据AppDevice中的协议，诊断结果最多有26个数据（不包括时间戳）
                if (bytes.length != 52) {

                }
                int[] result = new int[26];
                for (int i = 0; i < 26; i++) {
                    result[i] = (int) (((bytes[i * 2 + 1] & 0x00FF) << 8)
                            | (0x00FF & bytes[i * 2]));
                }
                // HR值
                int hrValue = result[0];
                // PR间期
                int prInterval = result[1];
                // QRS间期, 单位ms
                int qrsDuration = result[2];
                // QT间期
                int qt = result[3];
                // QTC间期
                int qtc = result[4];
                // P 波轴
                int pAxis = result[5];
                // QRS波心电轴
                int qrsAxis = result[6];
                // T波心电轴
                int tAxis = result[7];
                // RV5, 单位0.01ms
                int rv5 = result[8];
                // SV1, 单位0.01ms
                int sv1 = result[9];
                if (prInterval < 0) {
                    prInterval = (short) -prInterval;
                }
                diaResult = String.valueOf(hrValue) + "," + String
                        .valueOf(prInterval) + ","
                        + String.valueOf(qrsDuration) + "," + String
                        .valueOf(qt) + ","
                        + String.valueOf(qtc) + "," + String.valueOf(pAxis) + ","
                        + String.valueOf(qrsAxis) + "," + String.valueOf(tAxis) + ","
                        + String.format("%.2f", (float) rv5 / 100) + ","
                        + String.format("%.2f", (float) sv1 / 100) + ","
                        + String.format("%.2f", ((float) rv5 / 100 + (float) sv1 / 100)) + ",";
                //根据AppDevice协议，诊断码有16个，但不是所有都有效
                DiagCodeToText diagCodeToText = new DiagCodeToText();
                String text;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < diagCodeToText.ECG_12_LEAD_DIAG_TEXT.length; j++) {
                        String[] str = diagCodeToText.ECG_12_LEAD_DIAG_TEXT[j].split(":");
                        if (result[10 + i] == Integer.parseInt(str[0])) {
                            diaResult += str[1];
                            if ((str[1] != null) && (!"".equals(str[1]))) {
                                diaResult += ";";
                            }
                        }
                    }
                }
                ServiceUtils.saveEcgDiagnoseResult(diaResult);
            }

            @Override
            public void sendUnConnectMessageSend() {
                isCheckingEcg = false;
                isEcgConnect = false;
                /*血氧测试*/
                isCheckingSpo2 = false;
                attachSpo2 = false;
                UiUitls.toast(context, getRecString(R.string.ecg_pls_checkfordevice));
            }
        });
    }

    /**
     * 设置连接状态
     * @param param  类型
     * @param value   数值
     */
    private void setEcgConnectStatus(int param, int value) {
        int leadoff = -1;
        switch (param) {
            //血氧
            case 0x05:
                //设定探头状态
                setSpo2LeffStatus(value);
                break;
            case 0x10:
                leadoff = value;
                if (leadoff == 0) {
                    isEcgConnect = true;
                    if (!isCheckingEcg && !isTimeOut) {
                        if (GlobalConstant.ECG_PR_VALUE == GlobalConstant.INVALID_DATA) {
                        } else {
                        }
                    } else {
                    }
                } else if (leadoff == GlobalConstant.INVALID_DATA) {
                    isEcgConnect = false;
                    isCheckingEcg = false;
                } else {
                    isEcgConnect = false;
                    isCheckingEcg = false;
                }
                break;
            case 0x11:
                if (isCheckingEcg) {
                    switch (value) {
                        case 0:
                            UiUitls.toast(context, getRecString(R.string.heart_case0));
                            rhAlarm = UiUitls.getString(R.string.heart_case0);
                            break;
                        case 1:
                            UiUitls.toast(context, getRecString(R.string.heart_case1));
                            rhAlarm = UiUitls.getString(R.string.heart_case1);
                            break;
                        case 2:
                            UiUitls.toast(context, getRecString(R.string.heart_case2));
                            rhAlarm = UiUitls.getString(R.string.heart_case2);
                            break;
                        case 3:
                            UiUitls.toast(context, getRecString(R.string.heart_case3));
                            rhAlarm = UiUitls.getString(R.string.heart_case3);
                            break;
                        case 4:
                            UiUitls.toast(context, getRecString(R.string.heart_case4));
                            rhAlarm = UiUitls.getString(R.string.heart_case4);
                            break;
                        case 5:
                            UiUitls.toast(context, getRecString(R.string.heart_case5));
                            rhAlarm = UiUitls.getString(R.string.heart_case5);
                            break;
                        case 6:
                            UiUitls.toast(context, getRecString(R.string.heart_case6));
                            rhAlarm = UiUitls.getString(R.string.heart_case6);
                            break;
                        case 7:
                            UiUitls.toast(context, getRecString(R.string.heart_case7));
                            rhAlarm = UiUitls.getString(R.string.heart_case7);
                            break;
                        case 8:
                            UiUitls.toast(context, getRecString(R.string.heart_case8));
                            rhAlarm = UiUitls.getString(R.string.heart_case8);
                            break;
                        case 9:
                            UiUitls.toast(context, getRecString(R.string.heart_case9));
                            rhAlarm = UiUitls.getString(R.string.heart_case9);
                            break;
                        case 10:
                            UiUitls.toast(context, getRecString(R.string.heart_case10));
                            rhAlarm = UiUitls.getString(R.string.heart_case10);
                            break;
                        case 11:
                            UiUitls.toast(context, getRecString(R.string.heart_case11));
                            rhAlarm = UiUitls.getString(R.string.heart_case11);
                            break;
                        case 12:
                            UiUitls.toast(context, getRecString(R.string.heart_case12));
                            rhAlarm = UiUitls.getString(R.string.heart_case12);
                            break;
                        case 13:
                            UiUitls.toast(context, getRecString(R.string.heart_case13));
                            rhAlarm = UiUitls.getString(R.string.heart_case13);
                            break;
                        case 14:
                            UiUitls.toast(context, getRecString(R.string.heart_case14));
                            rhAlarm = UiUitls.getString(R.string.heart_case14);
                            break;
                        case 15:
                            UiUitls.toast(context, getRecString(R.string.heart_case15));
                            rhAlarm = UiUitls.getString(R.string.heart_case15);
                            break;
                        default:
                            break;
                    }
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
                    btnMeasureBp.setText(getRecString(R
                            .string.nibp_btn_start));
                    showMeasureResult(value);
                }
                break;
            case 0x04:
                if (measureState == 2) {
                    //袖带压
                    tvCuff.setText((value == GlobalConstant.INVALID_DATA) ?
                            "" : String.valueOf(value));
                }
                break;
            case 0x00://体温测量
                getTempLeffStatus(value);
                break;
            default:
                break;
        }
    }

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
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

    /**
     * 通过服务提供的值显示探头状态
     * @param value 血氧设备的连接状态
     */
    public void setSpo2LeffStatus(int value) {
        if (value == 0) {
            attachSpo2 = true;
            isFingerInsert = true;
            if (!isCheckingSpo2 && GlobalConstant.IS_MEUSE) {
                UiUitls.toast(context, getRecString(R.string.spo2_waiting));
            }
            GlobalConstant.spoState = MeasureSpo2Fragment.SPO2_LAYOUT;
        } else if (value == 1) {
            //探头脱落状态
            attachSpo2 = false;
            isCheckingSpo2 = false;
            reinitSpo2();
            btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
            GlobalConstant.spoState = MeasureSpo2Fragment.GUIDE_LAYOUT;
        } else if (value == 2) {
            //手指未插入状态
            attachSpo2 = true;
            isCheckingSpo2 = false;
            isFingerInsert = false;
            reinitSpo2();
            btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
            GlobalConstant.spoState = MeasureSpo2Fragment.FINGER_INSERT_LAYOUT;
        }
    }

    /**
     * 重启血氧测量
     */
    private void restartMeasureSpo2() {
        spo2Value = GlobalConstant.INVALID_DATA;
        measureCountSpo2 = 0;
    }

    /**
     * 重置血氧
     */
    private void reinitSpo2() {
        progressSpo2 = 15;
        donutProgress.setMax(15);
        donutProgress.setProgress(progressSpo2);
    }

    /**
     * 体温测试调用方法
     */
    private void initViewTemp() {
        if ((paramValue & (0x01 << 3)) == 0) {
            return;
        }
        tvTp.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.IR_TEMP_VALUE != GlobalConstant.INVALID_DATA) {
            tvTp.setText(String.valueOf(GlobalConstant.IR_TEMP_VALUE));
        }
        // 模拟测量的数据
        tempTrendList = new ArrayList<>();
    }

    /**
     * 获取体温设备连接状态
     * @param value 连接状态值
     */
    private void getTempLeffStatus(int value) {
        //探头正常
        if ((0 == (value & 0x01)) || (0 == (value & 0x02))) {
            attachTemp = true;
            //探头脱落
        } else {
            attachTemp = false;
            tvTp.setText("");
            tempTrendList.clear();
        }
    }

    /**
     * 尿常规初始化view
     */
    private void initViewUrinert() {
        if ((paramValue & (0x01 << 6)) == 0 && (paramValue & (0x01 << 5)) == 0) {
            return;
        }
        saveViewByMap();
        Iterator iterator = viewUrinert.keySet().iterator();
        while (iterator.hasNext()) {
            viewUrinert.get(iterator.next()).setText(UiUitls.getString(R.string.default_value));
        }
        String str;
        if (GlobalConstant.URINE_LEU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_LEU_VALUE);
            tvLeu.setText(str);
            if (!"-".equals(str)) {
                tvLeu.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvLeu.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvLeu.setText(R.string.default_value);
            tvLeu.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_UBG_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_UBG_VALUE);
            tvUbg.setText(str);
            if (!"-".equals(str)) {
                tvUbg.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvUbg.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvUbg.setText(R.string.default_value);
            tvUbg.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_PRO_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_PRO_VALUE);
            tvPro.setText(str);
            if (!"-".equals(str)) {
                tvPro.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvPro.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvPro.setText(R.string.default_value);
            tvPro.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_BIL_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BIL_VALUE);
            tvBil.setText(str);
            if (!"-".equals(str)) {
                tvBil.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvBil.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvBil.setText(R.string.default_value);
            tvBil.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_GLU_VALUE);
            tvGlu.setText(str);
            if (!"-".equals(str)) {
                tvGlu.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvGlu.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvGlu.setText(R.string.default_value);
            tvGlu.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_ASC_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_ASC_VALUE);
            tvAsc.setText(str);
            if (!"-".equals(str)) {
                tvAsc.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvAsc.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvAsc.setText(R.string.default_value);
            tvAsc.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_CA_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_CA_VALUE);
            tvCa.setText(str);
            if (!"-".equals(str)) {
                tvCa.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvCa.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvCa.setText(R.string.default_value);
            tvCa.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_ALB_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_ALB_VALUE);
            tvAlb.setText(str);
            if (!"-".equals(str)) {
                tvAlb.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvAlb.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvAlb.setText(R.string.default_value);
            tvAlb.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_CRE_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_CRE_VALUE);
            tvCr.setText(str);
            if (!"-".equals(str)) {
                tvCr.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvCr.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvCr.setText(R.string.default_value);
            tvCr.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_SG_VALUE != GlobalConstant.INVALID_DATA) {
            tvSg.setText(String.format("%.3f", GlobalConstant.URINE_SG_VALUE));
            if (GlobalConstant.URINE_SG_VALUE > 1.025) {
                tvSg.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else if (GlobalConstant.URINE_SG_VALUE < 1.015) {
                tvSg.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvSg.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvSg.setText(R.string.default_value);
            tvSg.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_KET_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_KET_VALUE);
            tvKet.setText(str);
            if (!"-".equals(str)) {
                tvKet.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvKet.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvKet.setText(R.string.default_value);
            tvKet.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_NIT_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_NIT_VALUE);
            tvNit.setText(str);
            if (!"-".equals(str)) {
                tvNit.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvNit.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvNit.setText(R.string.default_value);
            tvNit.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_PH_VALUE != GlobalConstant.INVALID_DATA) {
            tvPh.setText(String.valueOf(GlobalConstant
                    .URINE_PH_VALUE));
            if (GlobalConstant.URINE_PH_VALUE > Urine.PH_HIGH) {
                tvPh.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else if (GlobalConstant.URINE_PH_VALUE < Urine.PH_LOW) {
                tvPh.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvPh.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvPh.setText(R.string.default_value);
            tvPh.setTextColor(getResources().getColor(R.color.mesu_text));
        }
        if (GlobalConstant.URINE_BLD_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BLD_VALUE);
            tvBld.setText(str);
            if (!"-".equals(str)) {
                tvBld.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                tvBld.setTextColor(getResources().getColor(R.color.mesu_text));
            }
        } else {
            tvBld.setText(R.string.default_value);
            tvBld.setTextColor(getResources().getColor(R.color.mesu_text));
        }
    }

    /**
     * 初始化
     */
    private void initViewIrtemp() {
        if ((paramValue & (0x01 << 3)) == 0) {
            return;
        }
        tvTp.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.IR_TEMP_VALUE != GlobalConstant.INVALID_DATA
                && GlobalConstant.IR_TEMP_VALUE > 0) {
            tvTempUnmeasure.setVisibility(View.GONE);
            rlTempResult.setVisibility(View.VISIBLE);
            tvTp.setText(String.valueOf(GlobalConstant.IR_TEMP_VALUE));
            if (GlobalConstant.IR_TEMP_VALUE > IrTemp.HIGH ||
                    GlobalConstant.IR_TEMP_VALUE < IrTemp.LOW) {
                rlTempBg.setBackgroundResource(R.drawable.bg_above_high);
                tempState.setText(UiUitls.getString(R.string.temp_unnormal));
            } else {
                rlTempBg.setBackgroundResource(R.drawable.bg_above);
                tempState.setText(UiUitls.getString(R.string.temp_normal));
            }
        } else {
            tvTempUnmeasure.setVisibility(View.VISIBLE);
            rlTempBg.setBackgroundResource(R.drawable.bg_above);
            rlTempResult.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化血压显示值
     */
    private void initViewNibp() {
        tvShrink.setText(UiUitls.getString(R.string.default_value));
        tvSpread.setText(UiUitls.getString(R.string.default_value));
        tvCuff.setText("");
        if (GlobalConstant.NIBP_SYS_VALUE != GlobalConstant.INVALID_DATA) {
            llBloodPressureUnmeausre.setVisibility(View.GONE);
            rlBloodPressureResult.setVisibility(View.VISIBLE);
            tvShrink.setText(String.valueOf(GlobalConstant.NIBP_SYS_VALUE));
        } else {
            llBloodPressureUnmeausre.setVisibility(View.VISIBLE);
            rlBloodPressureResult.setVisibility(View.GONE);
            rlBloodPressureBg.setBackgroundResource(R.drawable.bg_above);
        }
        if (GlobalConstant.NIBP_DIA_VALUE != GlobalConstant.INVALID_DATA) {
            tvSpread.setText(String.valueOf(GlobalConstant.NIBP_DIA_VALUE));
            //血压异常判断标准：收缩压和舒张压有一个或者两个异常都视为异常
            if (GlobalConstant.NIBP_SYS_VALUE > Nibp.SYS_HIGH ||
                    GlobalConstant.NIBP_SYS_VALUE < Nibp.SYS_LOW ||
                    GlobalConstant.NIBP_DIA_VALUE > Nibp.DIA_HIGH ||
                    GlobalConstant.NIBP_DIA_VALUE < Nibp.DIA_LOW) {
                bpState.setText(getString(R.string.bp_unnormal));
                rlBloodPressureBg.setBackgroundResource(R.drawable.bg_above_high);
            } else {
                bpState.setText(getString(R.string.bp_normal));
                rlBloodPressureBg.setBackgroundResource(R.drawable.bg_above);
            }
        }
        btnMeasureBp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (measureState == 0) {
                    initViewNibp();
                    llBloodPressureUnmeausre.setVisibility(View.GONE);
                    rlBloodPressureResult.setVisibility(View.GONE);
                    bpState.setText(getString(R.string.health_bloothglu));
                    rlBloodPressureBg.setBackgroundResource(R.drawable.bg_above);
                    llCuff.setVisibility(View.VISIBLE);
                    EchoServerEncoder.setNibpConfig((short) 0x05, 0);
                    EchoServerEncoder.setNibpConfig((short) 0x05, 0);
                    cuffStatic = 0;
                    btnMeasureBp.setText(getRecString(R.string.nibp_btn_stop));
                    measureState = 1;
                } else {
                    initViewNibp();
                    llCuff.setVisibility(View.GONE);
                    EchoServerEncoder.setNibpConfig((short) 0x06, 0);
                    EchoServerEncoder.setNibpConfig((short) 0x06, 0);
                    btnMeasureBp.setText(getRecString(R.string.nibp_btn_start));
                    measureState = 0;
                }
            }
        });
    }

    /**
     * 该方法用于解决息屏后点亮屏幕血压模块恢复正常
     */
    public void beNormalNibpModel() {
        //血压初始化
        initViewNibp();
        llCuff.setVisibility(View.GONE);
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        btnMeasureBp.setText(getRecString(R.string.nibp_btn_start));
        measureState = 0;

        //血氧初始化
        initSpo2();
        //等待测量
        btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
        reinitSpo2();
        isCheckingSpo2 = false;
    }



    /**
     * 根据编码显示血压测量结果
     * @param code 编码
     */
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
        tvCuff.setText("");
        llCuff.setVisibility(View.GONE);
        initViewNibp();
        UiUitls.toast(getActivity(), result);
    }

    /**
     * 设置提示
     * @param param 血压类型
     */
    private void setWarm(int param) {
        String str = "";
        switch (param) {
            case KParamType.NIBP_SYS:
                if (GlobalConstant.NIBP_SYS_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.NIBP_SYS_VALUE > Nibp.SYS_HIGH ||
                            GlobalConstant.NIBP_SYS_VALUE < Nibp.SYS_LOW) {
                        tvShrink.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                        bpState.setText(getString(R.string.bp_unnormal));
                    } else {
                        tvShrink.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvShrink.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            case KParamType.NIBP_DIA:
                if (GlobalConstant.NIBP_DIA_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.NIBP_DIA_VALUE > Nibp.DIA_HIGH ||
                            GlobalConstant.NIBP_DIA_VALUE < Nibp.DIA_LOW) {
                        tvSpread.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvSpread.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvSpread.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }

                break;
            case KParamType.IRTEMP_TREND:

                if (GlobalConstant.IR_TEMP_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.IR_TEMP_VALUE > IrTemp.HIGH ||
                            GlobalConstant.IR_TEMP_VALUE < IrTemp.LOW) {
                        tvTp.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvTp.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvTp.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            case KParamType.BLOODGLU_AFTER_MEAL:
                if (GlobalConstant.BLOOD_GLU_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.BLOOD_GLU_VALUE > Float.valueOf(tvBloodMax
                            .getText().toString()) || GlobalConstant.BLOOD_GLU_VALUE < Float
                            .valueOf(tvBloodMin.getText().toString())) {
                        tvBs.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvBs.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.quick_measure_text_color_1));
                    }
                } else {
                    tvBs.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.quick_measure_text_color_1));
                }
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                if (GlobalConstant.BLOOD_GLU_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.BLOOD_GLU_VALUE > Float.valueOf(tvBloodMax
                            .getText().toString()) || GlobalConstant.BLOOD_GLU_VALUE < Float
                            .valueOf(tvBloodMin.getText().toString())) {
                        tvBs.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvBs.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.quick_measure_text_color_1));
                    }
                } else {
                    tvBs.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.quick_measure_text_color_1));
                }
                break;
            case KParamType.BLOOD_HGB:
                if (GlobalConstant.BLOOD_HGB_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.BLOOD_HGB_VALUE > Float.valueOf(hgbMax) ||
                            GlobalConstant.BLOOD_HGB_VALUE < Float.valueOf(hgbMin)) {
                        tvHgb.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvHgb.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvHgb.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            case KParamType.BLOOD_HCT:
                if (GlobalConstant.BLOOD_HCT_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.BLOOD_HCT_VALUE > Float.valueOf(hctMax) ||
                            GlobalConstant.BLOOD_HCT_VALUE < Float.valueOf(hctMin)) {
                        tvHtc.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvHtc.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvHtc.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            case KParamType.URINERT_LEU:
                str = valueToString(GlobalConstant.URINE_LEU_VALUE);
                if (!str.equals("-")) {
                    tvLeu.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvLeu.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_NIT:
                str = valueToString(GlobalConstant.URINE_NIT_VALUE);
                if (!str.equals("-")) {
                    tvNit.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvNit.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_UBG:
                str = valueToString(GlobalConstant.URINE_UBG_VALUE);
                if (!str.equals("-")) {
                    tvUbg.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvUbg.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_PRO:
                str = valueToString(GlobalConstant.URINE_PRO_VALUE);
                if (!str.equals("-")) {
                    tvPro.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvPro.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_PH:
                if (GlobalConstant.URINE_PH_VALUE != GlobalConstant.INVALID_DATA) {
                    tvPh.setText(String.valueOf(GlobalConstant
                            .URINE_PH_VALUE));
                    if (GlobalConstant.URINE_PH_VALUE > Urine.PH_HIGH) {
                        tvPh.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else if (GlobalConstant.URINE_PH_VALUE < Urine.PH_LOW) {
                        tvPh.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvPh.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.mesu_text));
                    }
                }
                break;
            case KParamType.URINERT_SG:
                if (GlobalConstant.URINE_SG_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.URINE_SG_VALUE > Urine.SG_HIGH ||
                            GlobalConstant.URINE_SG_VALUE < Urine.SG_LOW) {
                        tvSg.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvSg.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.mesu_text));
                    }
                } else {
                    tvSg.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_CA:
                if (GlobalConstant.URINE_CA_VALUE != GlobalConstant.INVALID_DATA) {
                    str = valueToString((int) GlobalConstant.URINE_CA_VALUE);
                    if (!str.equals("-")) {
                        tvCa.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvCa.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.mesu_text));
                    }
                } else {
                    tvCa.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_BLD:
                str = valueToString(GlobalConstant.URINE_BLD_VALUE);
                if (!str.equals("-")) {
                    tvBld.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvBld.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_KET:
                str = valueToString(GlobalConstant.URINE_KET_VALUE);
                if (!str.equals("-")) {
                    tvKet.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvKet.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_BIL:
                str = valueToString(GlobalConstant.URINE_BIL_VALUE);
                if (!str.equals("-")) {
                    tvBil.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvBil.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_GLU:
                str = valueToString(GlobalConstant.URINE_GLU_VALUE);
                if (!str.equals("-")) {
                    tvGlu.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvGlu.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_ASC:
                str = valueToString(GlobalConstant.URINE_ASC_VALUE);
                if (!str.equals("-")) {
                    tvAsc.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    tvAsc.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_ALB:
                if (GlobalConstant.URINE_ALB_VALUE != GlobalConstant.INVALID_DATA) {
                    str = valueToString((int) GlobalConstant.URINE_ALB_VALUE);
                    if (!str.equals("-")) {
                        tvAlb.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.high_color));
                    } else {
                        tvAlb.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.mesu_text));
                    }
                } else {
                    tvAlb.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
                break;
            case KParamType.URINERT_CRE:
                break;
            case KParamType.ECG_HR:
                if (GlobalConstant.ECG_PR_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.ECG_PR_VALUE > GlobalConstant.HR_ALARM_HIGH ||
                            GlobalConstant.ECG_PR_VALUE < GlobalConstant.HR_ALARM_LOW) {
                    }
                }
                break;
            case KParamType.SPO2_TREND:
                if (GlobalConstant.SPO2_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.SPO2_VALUE > Spo.SPO2_HIGH ||
                            GlobalConstant.SPO2_VALUE < Spo.SPO2_LOW) {
                        tvBo.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvBo.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvBo.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            case KParamType.SPO2_PR:
                if (GlobalConstant.SPO2_PR_VALUE != GlobalConstant.INVALID_DATA) {
                    if (GlobalConstant.SPO2_PR_VALUE > GlobalConstant.HR_ALARM_HIGH ||
                            GlobalConstant.SPO2_PR_VALUE < GlobalConstant.HR_ALARM_LOW) {
                        tvPr.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    } else {
                        tvPr.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.white));
                    }
                } else {
                    tvPr.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.white));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        } else {
            initView();
        }
    }

    /**
     * 重置所有数值
     */
    private void initView() {
        initViewBlood();
        initViewIrtemp();
        initViewNibp();
        initViewUrinert();
        initViewHgb();
        initSpo2();
        initLipids();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * 每次加载动态加载布局
     */
    public void initViewLayout() {
        //对应的四列布局
        ll1 = (LinearLayout) mView.findViewById(R.id.ll_1);
        ll2 = (LinearLayout) mView.findViewById(R.id.ll_2);
        ll3 = (LinearLayout) mView.findViewById(R.id.ll_3);
        ll4 = (LinearLayout) mView.findViewById(R.id.ll_4);
        ll5 = (LinearLayout) mView.findViewById(R.id.ll_5);
        ll6 = (LinearLayout) mView.findViewById(R.id.ll_6);
        ls.add(ll1);
        ls.add(ll2);
        ls.add(ll3);
        ls.add(ll4);
        ls.add(ll5);
        ls.add(ll6);
        //对应加载所需布局
        //参数配置项值
        paramValue = SpUtils.getSpInt(getActivity(), GlobalConstant.PARAM_CONFIGS
                , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
        listView.clear();
        if ((paramValue & (0x01 << 1)) != 0) {
            //血氧
            QuickBean beanSp = new QuickBean();
            beanSp.setWeight(1);
            beanSp.setType(0);
            lisTemp.add(beanSp);
        }
        if ((paramValue & (0x01 << 2)) != 0) {
            //血压
            QuickBean beanBp = new QuickBean();
            beanBp.setWeight(1);
            beanBp.setType(1);
            lisTemp.add(beanBp);
        }
        if ((paramValue & (0x01 << 3)) != 0) {
            //体温
            QuickBean beanTemp = new QuickBean();
            beanTemp.setWeight(1);
            beanTemp.setType(2);
            lisTemp.add(beanTemp);
        }
        if ((paramValue & (0x01 << 4)) != 0) {
            //血液三项
            QuickBean beanBt = new QuickBean();
            beanBt.setWeight(2);
            beanBt.setType(3);
            lisTemp.add(beanBt);
        }
        if ((paramValue & (0x01 << 5)) != 0) {
            //显示11项
            QuickBean beanUrine11 = new QuickBean();
            beanUrine11.setWeight(2);
            beanUrine11.setType(4);
            lisTemp.add(beanUrine11);
        }
        if ((paramValue & (0x01 << 6)) != 0) {
            //14项
            QuickBean beanUrine14 = new QuickBean();
            beanUrine14.setWeight(3);
            beanUrine14.setType(5);
            lisTemp.add(beanUrine14);
        }

        if ((paramValue & (0x01 << 7)) != 0) {
            //血红蛋白
            QuickBean beanUrineBd = new QuickBean();
            beanUrineBd.setWeight(1);
            beanUrineBd.setType(6);
            lisTemp.add(beanUrineBd);
        }
        if ((paramValue & (0x01 << 8)) != 0) {
            //血脂
            QuickBean beanUrineLids = new QuickBean();
            beanUrineLids.setWeight(1);
            beanUrineLids.setType(7);
            lisTemp.add(beanUrineLids);
        }
        if ((paramValue & (0x01 << 9)) != 0) {
            //糖化
            QuickBean beanSugar = new QuickBean();
            beanSugar.setWeight(1);
            beanSugar.setType(8);
            lisTemp.add(beanSugar);
        }
        if ((paramValue & (0x01 << 10)) != 0) {
            //bmi
            QuickBean beanBmi = new QuickBean();
            beanBmi.setWeight(1);
            beanBmi.setType(9);
            lisTemp.add(beanBmi);
        }
        if ((paramValue & (0x01 << 11)) != 0) {
            //白细胞
            QuickBean beanWbc = new QuickBean();
            beanWbc.setWeight(1);
            beanWbc.setType(10);
            lisTemp.add(beanWbc);
        }
        List<QuickBean> quickBeen1 = new ArrayList<>();
        List<QuickBean> quickBeen2 = new ArrayList<>();
        List<QuickBean> quickBeen3 = new ArrayList<>();
        for (int i = 0; i < lisTemp.size(); i++) {
            QuickBean bean = lisTemp.get(i);
            if (bean.getWeight() == 1) {
                quickBeen1.add(bean);
            } else if (bean.getWeight() == 2) {
                quickBeen2.add(bean);
            } else if (bean.getWeight() == 3) {
                quickBeen3.add(bean);
            }
        }
        int j = 0;
        for (int i = 0; i < quickBeen2.size(); i++) {
            QuickBean bean = quickBeen2.get(i);
            bean.setIndex(i);
            bean.setChildIndex(1);
            listView.add(bean);
            QuickBean bean1 = quickBeen1.get(0);
            bean1.setIndex(i);
            bean1.setChildIndex(0);
            listView.add(bean1);
            quickBeen1.remove(0);
            j = i + 1;
        }
        for (int i = 0; i < quickBeen3.size(); i++) {
            QuickBean bean = quickBeen3.get(i);
            bean.setIndex(j);
            bean.setChildIndex(0);
            listView.add(bean);
            j = j + 1;
        }
        int max = 0;
        for (int i = 0; i < quickBeen1.size(); i++) {
            QuickBean bean = quickBeen1.get(i);
            bean.setIndex(j);
            bean.setChildIndex(max);
            listView.add(bean);
            max += 1;
            if (max > 2) {
                max = 0;
                j += 1;
            }
        }
        loadView();
    }

    /**
     * 加载界面的方法
     */
    private void loadView() {
        for (int i = 0; i < ls.size(); i++) {
            LinearLayout linearLayout = ls.get(i);
            if (linearLayout.getChildCount() > 0) {
                linearLayout.removeAllViews();
            }
            View v1 = null;
            View v2 = null;
            View v3 = null;
            int max = 0;
            for (int j = 0; j < listView.size(); j++) {
                if (max >= GlobalNumber.THREE_NUMBER) {
                    break;
                } else {
                    QuickBean bean = listView.get(j);
                    if (bean.getIndex() == i) {
                        if (bean.getChildIndex() == 0) {
                            v1 = inflater.inflate(listAllView.get(bean.getType()), null);
                            //给每一组控件加上标记，为了移动操作计算
                            v1.setTag(bean);
                            max += bean.getWeight();
                        } else if (bean.getChildIndex() == 1) {
                            v2 = inflater.inflate(listAllView.get(bean.getType()), null);
                            v2.setTag(bean);
                            max += bean.getWeight();
                        } else if (bean.getChildIndex() == 2) {
                            v3 = inflater.inflate(listAllView.get(bean.getType()), null);
                            v3.setTag(bean);
                            max += bean.getWeight();
                        }
                    }
                }
            }
            if (null != v1) {
                linearLayout.addView(v1, quickCheckHelpUtils
                        .getLayoutParams(((QuickBean) v1.getTag()).getWeight()));
                v1.setOnClickListener(clickListener);
                v1.setOnLongClickListener(longClickListener);
            }
            if (null != v2) {
                linearLayout.addView(v2, quickCheckHelpUtils
                        .getLayoutParams(((QuickBean) v2.getTag()).getWeight()));
                v2.setOnClickListener(clickListener);
                v2.setOnLongClickListener(longClickListener);
            }
            if (null != v3) {
                linearLayout.addView(v3, quickCheckHelpUtils
                        .getLayoutParams(((QuickBean) v3.getTag()).getWeight()));
                v3.setOnClickListener(clickListener);
                v3.setOnLongClickListener(longClickListener);
            }
            if (null == v3 && null == v2 && null == v1) {
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 得到所有子布局
     */
    private void getAllLayout() {
        listAllView.clear();
        //血氧
        listAllView.add(R.layout.quick_check_spo_layout);
        //血压
        listAllView.add(R.layout.quick_check_bp_layout);
        //体温
        listAllView.add(R.layout.quick_check_temp_layout);
        //血液三项
        listAllView.add(R.layout.quick_check_blood_sugar_layout);
        //显示11项
        listAllView.add(R.layout.quick_check_urine_11_layout);
        //14项
        listAllView.add(R.layout.quick_check_urine_14_layout);
        //血红蛋白
        listAllView.add(R.layout.quick_check_blood_red_layout);
        //血脂
        listAllView.add(R.layout.quick_check_xuezhi_layout);
        //糖化
        listAllView.add(R.layout.quick_check_sugar_bhd_layout);
        //bmi
        listAllView.add(R.layout.quick_check_bmi_layout);
        //白细胞
        listAllView.add(R.layout.quick_check_wbc_layout);
    }

    /**
     * 切换其他页面，停止血压测量，更新UI
     */
    public void refresgBlood() {
        initViewNibp();
        llCuff.setVisibility(View.GONE);
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
        btnMeasureBp.setText(getRecString(R.string.nibp_btn_start));
        measureState = 0;
    }  /**
     * 切换其他页面，停止血氧测量，更新UI
     */
    public void refresgSpo2() {
        if (isCheckingSpo2) {
            //如果正在测量血氧，跳转其他页面，停止测量
            isCheckingSpo2 = false;
            reinitSpo2();
            llBoMeasuing.setVisibility(View.GONE);
            btnMeasureBo.setText(getRecString(R.string.nibp_btn_start));
            tvBoUnmeasure.setVisibility(View.VISIBLE);
        }
    }
}