package com.konsung.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.BrokenLineTable;
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.AlarmUtil;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.BeneParamValue;
import com.konsung.util.global.GlobalNumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/7/23.
 */
public class BloodGluFragment extends BaseFragment implements View
        .OnClickListener {
    // 参数
    @InjectView(R.id.blood_glu_trend_tv)
    TextView tvBloodGluTrend;
    @InjectView(R.id.blood_ns_trend_tv)
    TextView tvNsTrend;
    @InjectView(R.id.blood_td_trend_tv)
    TextView tvTdTrend;

    @InjectView(R.id.content)
    RelativeLayout rlContent;

    @InjectView(R.id.scrollView)
    ScrollView sclViScrollView;

    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;
    @InjectView(R.id.btn_before_eat)
    Button btnBeforeEat;
    @InjectView(R.id.btn_random)
    Button btnRandom;
    @InjectView(R.id.btn_late_eat)
    Button btnLateEat;

    //血糖正常最高值
    @InjectView(R.id.blood_glu_high)
    TextView tvBloodGluHigh;
    //血糖正常最低值
    @InjectView(R.id.blood_glu_low)
    TextView tvBloodGluLow;
    @InjectView(R.id.blood_ns_low)
    TextView tvUrineMin;
    @InjectView(R.id.blood_ns_high)
    TextView tvUrineMax;
    @InjectView(R.id.iv_blood_glu_trend)
    ImageView ivBloodGlu;
    @InjectView(R.id.iv_blood_ns_trend)
    ImageView ivBloodNs;
    @InjectView(R.id.iv_blood_td_trend)
    ImageView ivBloodTd;
    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;
    // 开始测量按钮按下状态
    private boolean isStartMeasure;
    //已经得到数据
    private boolean isGetValue;
/*    // 点击开始测量按钮弹出的对话框
    private StartMeasureDialogIrTemp mStartDialog;*/

    // 以下变量使用于模拟测量,不需要时可删除
    private List<Integer> irtempTrendList;
    private boolean isRunning;  // 模拟测量时,线程还在运行
    /*private int mProgress = 0;*/
    private View view;
    /* private AppFragment appFragment;*/
//    private Handler handler = new Handler();

    public AIDLServer aidlServer;

    //数据接收完后的一次刷新
    public int updateFlag = 0;
    //身份证号
    private String idCard;
    //性别
    private String sexType;
    private float xtMax;
    private float xtMin;
    private float nsMax;
    private float nsMin;
    private float cholMax;
    private float cholMin;
    //餐前，餐后值
    private String beforeMeal = "-?-";
    private String afterMeal = "-?-";
    //判断餐前餐后标识
    private boolean isBeforeMeal = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity(), "app_config", "name", "");
        boolean check = name.equals("");

//        if (check) {
//            Toast.makeText(getActivity(), getRecString(R.string
//                            .fragment_pls_select_patient)
//                    , Toast.LENGTH_SHORT).show();
//            GetAllPatientFragment allPatientFragment = new GetAllPatientFragment();
//            switchToFragment(R.id.fragment, allPatientFragment, "allPatientFragment", true);
//            isAttach = false;
//        }

    }

 /*   */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bloodglu, null);
        ButterKnife.inject(this, view);
        initView();
        initMeasureListener();
        initTable(false);
        return view;
    }

    /**
     * 绘制折线图
     */
    private void initChart() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;" + "HH:mm:ss");
        //从数据库中获取数据
        final String idcard = SpUtils.getSp(UiUitls.getContent(), "app_config", "idcard", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询多少条数据
                int data = GlobalConstant.MEASURE_NUM;
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data,  idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int ecgHr = measureDataBean
                                    .getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL);
                            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                                dataList.add(Float.valueOf((ecgHr / GlobalConstant.SWITCH_VALUE)));
                            } else {
                                dataList.add(Float.valueOf(ecgHr));
                            }
                            timeList.add(sdf.format(measureDataBean.getMeasureTime()));
                        }
                        //把List转float[]
                        float[] values = new float[dataList.size()];
                        for (int i = 0; i < dataList.size(); i++) {
                            values[i] = dataList.get(i).floatValue();
                        }
                        //把timeList转String[]
                        String[] times = timeList.toArray(new String[0]);

                        //绘线描点数据集
                        ArrayList<float[]> data = new ArrayList<>();
                        data.add(values);

                        InitChartBean bean = new InitChartBean();

                        //设置X轴
                        bean.setX_values(times);
                        //设置单位
                        bean.setUnit(UiUitls.getString(R.string.health_unit_mol));
                        //设置Y轴刻度数量
                        int ySize = GlobalNumber.TEN_NUMBER;
                        bean.setY_SIZE(ySize);
                        //设置绘图数据
                        bean.setValues(data);
                        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
                        bean.setMax_value(GlobalNumber.FIFTY_NUMBER_FLOAT);
                        bean.setMin_value(0.0f);

                        //获得数据点数
                        int lengthSize = bean.getX_values().length;
                        //默认最大长度为100,布局容量
                        if (bean.getX_values().length > bean.getMAX_SIZE()) {
                            lengthSize = bean.getMAX_SIZE();
                        } else if (bean.getX_values().length < GlobalNumber.TEN_NUMBER) {
                            lengthSize = GlobalNumber.ELEVEN_NUMBER;
                        }
                        bean.setSize(lengthSize);
                        //设置布局长度和宽度
                        BrokenLineTable chart = new BrokenLineTable(UiUitls.getContent(), bean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * lengthSize +
                                bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * ySize + bean
                                .getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(width, heigth);
                        params.height = heigth;
                        params.width = width;
                        rlContent.addView(chart);
                        chart.setLayoutParams(params);
                    }
                });
            }
        }).start();
    }

    /**
     * 绘制表格
     * @param reDraw 是否是重新绘制
     */
    private void initTable(final boolean reDraw) {
        //数据集
        final Time_paramBean bean = new Time_paramBean();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;" + "HH:mm:ss");
        //从数据库中获取测量数据
        final String idcard = SpUtils.getSp(UiUitls.getContent(), "app_config", "idcard", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询多少条数据
                int data = GlobalConstant.MEASURE_NUM;
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data,  idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add(UiUitls.getString(R.string.blood_sugar));
                        param.add(UiUitls.getString(R.string.purine_trione_mmol));
                        param.add("总胆固醇;(mmol/L)");
                        //时间-----X_NAME
                        ArrayList<String> times = new ArrayList<>();
                        ArrayList<ArrayList<String>> data = new ArrayList<>();
                        //行数---参数个数
                        int rowSize = measure.size();
                        int lineSize = param.size();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            ArrayList<String> list = new ArrayList<>();
                            times.add(sdf.format(measure.get(i).getMeasureTime()));
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .BLOODGLU_BEFORE_MEAL) == GlobalConstant.INVALID_TREND_DATA)
                                    ? UiUitls.getString(R.string.no_test)
                                    : String.valueOf(measure.get(i).getTrendValue(KParamType
                                    .BLOODGLU_BEFORE_MEAL) / GlobalNumber.HUNDRED_NUMBER_FLOAT));
                            //尿酸
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .URICACID_TREND) == GlobalNumber.UN_THOUSAND_NUMBER)
                                    ? UiUitls.getString(R.string.no_test)
                                    : String.valueOf(measure.get(i).getTrendValue(KParamType
                                    .URICACID_TREND) / GlobalNumber.HUNDRED_NUMBER_FLOAT));
                            //总胆固醇
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .CHOLESTEROL_TREND) == GlobalNumber.UN_THOUSAND_NUMBER)
                                    ? UiUitls.getString(R.string.no_test)
                                    : String.valueOf(measure.get(i).getTrendValue(KParamType
                                    .CHOLESTEROL_TREND) / GlobalNumber.HUNDRED_NUMBER_FLOAT));
                            data.add(list);
                        }
                        bean.setLINE_SIZE(lineSize);
                        bean.setROW_SIZE(rowSize);
                        bean.setX_VALUE(times);
                        bean.setY_VALUE(param);
                        bean.setData(data);

                        //加载布局
                        //设置布局长度和宽度
                        TimeParamTable table = new TimeParamTable(UiUitls.getContent(), bean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_X() * (rowSize + 1)
                                + bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int height = (int) (bean.getSCALE_Y() * (lineSize +
                                1) + bean.getPADDING_TOP() + bean.getPADDING_BOTTOM());
                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width
                                , ViewGroup.LayoutParams.MATCH_PARENT);
                        params.height = height;
                        params.width = width;
                        if (reDraw) {
                            rlContentTable.removeAllViews();
                            rlContentTable.addView(table);
                            table.setLayoutParams(params);
                        } else {
                            rlContentTable.addView(table);
                            table.setLayoutParams(params);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStartMeasure = false;
        ServiceUtils.setOnMessageSendListener(null);
    }

    /**
     * 初始化
     */
    private void initView() {
        if (GlobalConstant.BLOODGLUSTATE.equals("0")) {
            //餐前
            isBeforeMeal = true;
            GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.lift;
        } else {
            //餐后
            isBeforeMeal = false;
            if (GlobalConstant.BLOOD_GLU_VALUE == GlobalConstant.INVALID_DATA) {
                GlobalConstant.BLOODGLUSTATE = "0";
                isBeforeMeal = true;
                GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.lift;
            } else {
                GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.middle;
            }
        }
        if (GlobalConstant.CURRENT_SELETE_BTN == GlobalConstant.BtnFlag.middle) {
            tvBloodGluTrend.addTextChangedListener(new OverProofUtil(BeneParamValue
                    .XT_AFTER_VALUE_MIN, BeneParamValue.XT_AFTER_VALUE_MAX, tvBloodGluTrend));
            xtMax = BeneParamValue.XT_AFTER_VALUE_MAX;
            xtMin = BeneParamValue.XT_AFTER_VALUE_MIN;
        } else if (GlobalConstant.CURRENT_SELETE_BTN == GlobalConstant.BtnFlag.lift) {
            tvBloodGluTrend.addTextChangedListener(new OverProofUtil(BeneParamValue
                    .XT_VALUE_MIN, BeneParamValue.XT_VALUE_MAX, tvBloodGluTrend));
            xtMax = BeneParamValue.XT_VALUE_MAX;
            xtMin = BeneParamValue.XT_VALUE_MIN;
        } else {
            tvBloodGluTrend.addTextChangedListener(new OverProofUtil(BeneParamValue
                    .XT_VALUE_MIN, BeneParamValue.XT_VALUE_MAX, tvBloodGluTrend));
            xtMax = BeneParamValue.XT_VALUE_MAX;
            xtMin = BeneParamValue.XT_VALUE_MIN;
        }
        idCard = SpUtils.getSp(UiUitls.getContent(), "app_config", "idcard", "");
        List<PatientBean> patientBeanList = DBDataUtil.getPatientByIdCard(idCard);
        if (null != patientBeanList && patientBeanList.size() > 0) {
            if (SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG,
                    GlobalConstant.CARD_INPUT, true)) {
                sexType = UiUitls.judgeSexByIdCard(patientBeanList.get(0).getCard());
            } else {
                int sex = patientBeanList.get(0).getSex();
                sexType = sex == 1 ? UiUitls.getString(R.string.boy) : UiUitls.getString(
                        R.string.girl);
            }
        }
        //尿酸
        if (sexType.equals(UiUitls.getString(R.string.boy))) {
            tvUrineMax.setText(BeneParamValue.NS_VALUE_MAX + "");
            tvUrineMin.setText(BeneParamValue.NS_VALUE_MIN + "");
            tvNsTrend.addTextChangedListener(new OverProofUtil(BeneParamValue.NS_VALUE_MIN
                    , BeneParamValue.NS_VALUE_MAX, tvNsTrend));
            nsMax = BeneParamValue.NS_VALUE_MAX;
            nsMin = BeneParamValue.NS_VALUE_MIN;
        } else if (sexType.equals(UiUitls.getString(R.string.girl))) {
            tvUrineMax.setText(BeneParamValue.NS_VALUE_MAXG + "");
            tvUrineMin.setText(BeneParamValue.NS_VALUE_MING + "");
            tvNsTrend.addTextChangedListener(new OverProofUtil(BeneParamValue.NS_VALUE_MING
                    , BeneParamValue.NS_VALUE_MAXG, tvNsTrend));
            nsMax = BeneParamValue.NS_VALUE_MAXG;
            nsMin = BeneParamValue.NS_VALUE_MING;
        }
        tvTdTrend.addTextChangedListener(new OverProofUtil(BeneParamValue.CHOL_VALUE_MIN
                , BeneParamValue.CHOL_VALUE_MAX, tvTdTrend));
        cholMax = BeneParamValue.CHOL_VALUE_MAX;
        cholMin = BeneParamValue.CHOL_VALUE_MIN;
        tvBloodGluHigh.setText(BeneParamValue.XT_VALUE_MAX + "");
        tvBloodGluLow.setText(BeneParamValue.XT_VALUE_MIN + "");
        tvBloodGluTrend.setText(UiUitls.getString(R.string.default_value));
        tvNsTrend.setText(UiUitls.getString(R.string.default_value));
        tvTdTrend.setText(UiUitls.getString(R.string.default_value));
        //有值的情况下进行餐前餐后判断
        if (GlobalConstant.BLOOD_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            seteleBtn(GlobalConstant.CURRENT_SELETE_BTN);
            tvBloodGluTrend.setText(String.valueOf(GlobalConstant.BLOOD_GLU_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_GLU_VALUE, xtMax, xtMin
                    , ivBloodGlu);
        } else {
            seteleBtn(GlobalConstant.BtnFlag.lift);
        }
        if (GlobalConstant.CHOLESTEROL_VALUE != GlobalConstant.INVALID_DATA) {
            tvTdTrend.setText(String.valueOf(GlobalConstant.CHOLESTEROL_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.CHOLESTEROL_VALUE, cholMax, cholMin
                    , ivBloodTd);
        }

        if (GlobalConstant.URIC_ACID_VALUE != GlobalConstant.INVALID_DATA) {
            tvNsTrend.setText(String.valueOf(GlobalConstant.URIC_ACID_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.URIC_ACID_VALUE, nsMax, nsMin
                    , ivBloodNs);
        }
        // 模拟测量的数据
        irtempTrendList = new ArrayList<>();
        btnBeforeEat.setOnClickListener(this);
        btnRandom.setOnClickListener(this);
        btnLateEat.setOnClickListener(this);
    }

    /**
     * 初始化服务监听
     */
    private void initMeasureListener() {
        ServiceUtils.setOnMessageSendListener(new ServiceUtils.OnMessageSendListener() {
            @Override
            public void sendParaStatus(String name, String version) {

            }

            @Override
            public void sendWave(int param, byte[] bytes) {

            }

            @Override
            public void sendTrend(int param, int value) {
                float v1 = (float) value / GlobalConstant.TREND_FACTOR;
                ServiceUtils.setGluStyle(UiUitls.getBtnFlag(GlobalConstant.CURRENT_SELETE_BTN));
                switch (param) {
                    case KParamType.BLOODGLU_AFTER_MEAL:
                        float vGlu = (float) value / GlobalConstant.TREND_FACTOR;
                        if (value != GlobalConstant.INVALID_DATA) {
                            isGetValue = true;
                            tvBloodGluTrend.setText(String.valueOf(vGlu));
                            ServiceUtils.saveTrend(KParamType.BLOODGLU_AFTER_MEAL, value);
                            ServiceUtils.saveToDb2();
                            GlobalConstant.BLOOD_GLU_VALUE = v1;
                            AlarmUtil.executeOverrunAlarm(vGlu, BeneParamValue
                                    .XT_AFTER_VALUE_MAX, BeneParamValue.XT_AFTER_VALUE_MIN,
                                    ivBloodGlu);
                            initTable(true);
                        }
                        break;
                    case KParamType.BLOODGLU_BEFORE_MEAL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            isGetValue = true;
                            tvBloodGluTrend.setText(String.valueOf(v1));
                            GlobalConstant.BLOOD_GLU_VALUE = v1;
                            //保存测量血糖时候病人的测量状态
                            if (isBeforeMeal) {
                                //餐前测量数据
                                GlobalConstant.BLOODGLUSTATE = "0";
                                afterMeal = "-?-";
                            } else {
                                //餐后测量数据
                                GlobalConstant.BLOODGLUSTATE = "1";
                                beforeMeal = "-?-";
                            }
                            ServiceUtils.saveBloodGluState(GlobalConstant.BLOODGLUSTATE);
                            ServiceUtils.saveTrend(KParamType.BLOODGLU_BEFORE_MEAL, value);
                            ServiceUtils.saveToDb2();
                            AlarmUtil.executeOverrunAlarm(v1, xtMax, xtMin, ivBloodGlu);
                            initTable(true);
                        }
                        break;
                    //尿酸
                    case KParamType.URICACID_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            float tempNs = (float) value / GlobalConstant.TREND_FACTOR;
                            tvNsTrend.setText(String.valueOf(tempNs));
                            GlobalConstant.URIC_ACID_VALUE = tempNs;
                            ServiceUtils.saveTrend(KParamType.URICACID_TREND, value);
                            ServiceUtils.saveToDb2();
                            AlarmUtil.executeOverrunAlarm(tempNs, nsMax, nsMin, ivBloodNs);
                            initTable(true);
                        }
                        break;
                    //总胆固醇
                    case KParamType.CHOLESTEROL_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            float temp = (float) value / GlobalConstant.TREND_FACTOR;
                            tvTdTrend.setText(String.valueOf(temp));
                            GlobalConstant.CHOLESTEROL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.CHOLESTEROL_TREND, value);
                            ServiceUtils.saveToDb2();
                            AlarmUtil.executeOverrunAlarm(temp, cholMax, cholMin, ivBloodTd);
                            initTable(true);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void sendConfig(int param, int value) {

            }

            @Override
            public void sendPersonalDetail(String name, String idcard, int sex, int type
                    , String pic, String address) {

            }

            @Override
            public void send12LeadDiaResult(byte[] bytes) {

            }

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }

    /**
     * 测量结束
     * @param server 服务对象
     */
    private void measureOver(AIDLServer server) {
        int irtempTrends = 0;
        for (int i = 0; i < irtempTrendList.size(); i++) {
            irtempTrends += irtempTrendList.get(i);
        }
        //计算平均值
        if (0 != irtempTrendList.size()) {
            if (GlobalConstant.INVALID_DATA == irtempTrends / irtempTrendList
                    .size()) {
                tvBloodGluTrend.setText(UiUitls.getString(R.string
                        .default_value));
            } else {
                tvBloodGluTrend.setText(String.valueOf((float) (irtempTrends /
                        irtempTrendList.size()) / GlobalConstant.TREND_FACTOR));
            }
        }
        irtempTrendList.clear();
      /*  mProgress = 0;*/
    }

    @Override
    public void onClick(View view) {
        //用户餐前按钮
        if (view == btnBeforeEat) {
            if (!isBeforeMeal) {
                tvBloodGluTrend.addTextChangedListener(new OverProofUtil(BeneParamValue
                        .XT_VALUE_MIN, BeneParamValue.XT_VALUE_MAX, tvBloodGluTrend));
                isBeforeMeal = true;
                seteleBtn(GlobalConstant.BtnFlag.lift);
                GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.lift;
                GlobalConstant.BLOODGLUSTATE = "0";
                if (!tvBloodGluTrend.getText().toString().equals("-?-") &&
                        !TextUtils.isEmpty(tvBloodGluTrend.getText().toString())) {
                    //表示测量了餐后数据
                    //保存餐后数据
                    GlobalConstant.BLOODGLUSTATE = "1";
                    afterMeal = tvBloodGluTrend.getText().toString();
                    tvBloodGluTrend.setText("-?-");
                    ivBloodGlu.setVisibility(View.INVISIBLE);
                } else {
                    GlobalConstant.BLOODGLUSTATE = "0";
                    tvBloodGluTrend.setText(beforeMeal);
                    if (!beforeMeal.equals("-?-")) {
                        AlarmUtil.executeOverrunAlarm(Float.valueOf(beforeMeal)
                                , xtMax, xtMin, ivBloodGlu);
                    }
                }
            }
        } else if (view == btnRandom) {
            if (isBeforeMeal) {
                tvBloodGluTrend.addTextChangedListener(new OverProofUtil(BeneParamValue
                        .XT_AFTER_VALUE_MIN, BeneParamValue.XT_AFTER_VALUE_MAX
                        , tvBloodGluTrend));
                isBeforeMeal = false;
                //用户点击餐后按钮
                seteleBtn(GlobalConstant.BtnFlag.middle);
                GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.middle;
                if (!tvBloodGluTrend.getText().toString().equals("-?-") &&
                        !TextUtils.isEmpty(tvBloodGluTrend.getText().toString())) {
                    //表示测量了餐前数据
                    //保存餐前数据
                    GlobalConstant.BLOODGLUSTATE = "0";
                    beforeMeal = tvBloodGluTrend.getText().toString();
                    tvBloodGluTrend.setText("-?-");
                    ivBloodGlu.setVisibility(View.INVISIBLE);
                } else {
                    GlobalConstant.BLOODGLUSTATE = "1";
                    tvBloodGluTrend.setText(afterMeal);
                    if (!afterMeal.equals("-?-")) {
                        AlarmUtil.executeOverrunAlarm(Float.valueOf(afterMeal), xtMax, xtMin
                                , ivBloodGlu);
                    }
                }
            }
        } else if (view == btnLateEat) {
            //废弃按钮
            seteleBtn(GlobalConstant.BtnFlag.right);
        }
    }
    /**
     * 选中按钮的方法
     * @param flag 按钮标识
     */
    public void seteleBtn(GlobalConstant.BtnFlag flag) {
        GlobalConstant.CURRENT_SELETE_BTN = flag;
        switch (flag) {
            case lift://餐前 0
                isBeforeMeal = true;
                clickBtn(btnBeforeEat, R.drawable.select_before_dinner);
                break;
            case middle://餐后 1
                isBeforeMeal = false;
                clickBtn(btnRandom, R.drawable.select_after_dinner);
                break;
//            case right://餐后 2
//                clickBtn(btnLateEat,R.drawable.right_sel);
//                break;
            default:
                break;
        }
    }
    /**
     * 点击按钮切换背景
     * @param btn 点击的按钮
     * @param ba  点击的背景图片
     */
    @SuppressLint("NewApi")
    public void clickBtn(Button btn, int ba) {
        btnBeforeEat.setTextColor(UiUitls.getContent().getResources()
                .getColor(R.color.grass_konsung_2));
//        btnLateEat.setTextColor(UiUitls.getContent().getResources()
//                .getColor(R.color.grass_konsung_2));
        btnRandom.setTextColor(UiUitls.getContent().getResources()
                .getColor(R.color.grass_konsung_2));
        btnBeforeEat.setBackground(UiUitls.getContent().getResources()
                .getDrawable(R.drawable.unselect_before_dinner));
//        btnLateEat.setBackground(UiUitls.getContent().getResources()
//                .getDrawable(R.drawable.right_nor));
        btnRandom.setBackground(UiUitls.getContent().getResources()
                .getDrawable(R.drawable.unselect_after_dinner));
        btn.setTextColor(Color.WHITE);
        btn.setBackground(UiUitls.getContent().getResources().getDrawable(ba));
        switch (btn.getId()) {
            case R.id.btn_before_eat:
                tvBloodGluHigh.setText(BeneParamValue.XT_VALUE_MAX + "");
                tvBloodGluLow.setText(BeneParamValue.XT_VALUE_MIN + "");
                xtMin = BeneParamValue.XT_VALUE_MIN;
                xtMax = BeneParamValue.XT_VALUE_MAX;
                break;
            case R.id.btn_random:
                tvBloodGluHigh.setText(BeneParamValue.XT_AFTER_VALUE_MAX + "");
                tvBloodGluLow.setText(BeneParamValue.XT_AFTER_VALUE_MIN + "");
                xtMin = BeneParamValue.XT_AFTER_VALUE_MIN;
                xtMax = BeneParamValue.XT_AFTER_VALUE_MAX;
                break;
//            case R.id.btn_late_eat:
//                tvBloodGluHigh.setText("11.1");
//                tvBloodGluLow.setText("2.77");
//                break;
            default:
                break;
        }
    }

}
