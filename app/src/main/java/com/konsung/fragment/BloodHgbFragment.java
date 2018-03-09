package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.BloodReferenceBean;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.BloodHbgChart;
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.AlarmUtil;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NumUtil;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.BloodMem;
import com.konsung.util.global.BloodReference;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.PatientStyle;
import com.konsung.util.global.Sex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author xiangshicheng
 */
public class BloodHgbFragment extends BaseFragment {

    // 参数
    @InjectView(R.id.blood_hgb_trend_tv)
    TextView tvBloodHgbTrend;
    @InjectView(R.id.blood_hct_trend_tv)
    TextView tvBloodHctTrend;

    @InjectView(R.id.content)
    RelativeLayout rlContent;

    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    @InjectView(R.id.hgb_high)
    TextView tvHgbHigh;
    @InjectView(R.id.hgb_low)
    TextView tvHgbLow;
    @InjectView(R.id.htc_high)
    TextView tvHtcHigh;
    @InjectView(R.id.htc_low)
    TextView tvHtcLow;

    @InjectView(R.id.iv_blood_hgb_trend)
    ImageView ivBloodHgb;
    @InjectView(R.id.iv_blood_hct_trend)
    ImageView ivBloodHct;
    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;
    // 开始测量按钮按下状态
    private boolean isStartMeasure;
    //已经得到数据
    private boolean isGetValue;
    // 以下变量使用于模拟测量,不需要时可删除
    private List<Integer> irtempTrendList;
    private boolean isRunning;  // 模拟测量时,线程还在运行
    private View view;
    private Handler handler = new Handler();
    public AIDLServer aidlServer;
    //身份证号
    private String idCard;
    //性别
    private String sexType;
    //血红蛋白上下限，红细胞上下限
    private String hgbMax;
    private String hgbMin;
    private String htcMax;
    private String htcMin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity(), "app_config", "name", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bloodhgb, null);
        ButterKnife.inject(this, view);
        initView();
        //initReferenceValue();
        initMeasureListener();
        initChart();
        return view;
    }

    /**
     * 初始化参考值
     */
    private void initReferenceValue() {
        String idcard = SpUtils.getSp(UiUitls.getContent()
                , "app_config", "idcard", "");
        List<PatientBean> patients = DBDataUtil.getPatientByIdCard(idcard);
        if (patients.size() > 0) {
            PatientBean patient = patients.get(0);
            int sex = patient.getSex();
            switch (patient.getPatient_type()) {
                case PatientStyle.ADULT:
                    if (sex == Sex.FEMALE) {
                        tvHgbHigh.setText(String.valueOf(BloodReference.HGB_FEMALE_HIGH));
                        tvHgbLow.setText(String.valueOf(BloodReference.HGB_FEMALE_LOW));
                        tvHtcHigh.setText(String.valueOf(BloodReference.HTC_FEMALE_HIGH));
                        tvHtcLow.setText(String.valueOf(BloodReference.HTC_FEMALE_LOW));
                    } else {
                        tvHgbHigh.setText(String.valueOf(BloodReference.HGB_MALE_HIGH));
                        tvHgbLow.setText(String.valueOf(BloodReference.HGB_MALE_LOW));
                        tvHtcHigh.setText(String.valueOf(BloodReference.HTC_MALE_HIGH));
                        tvHtcLow.setText(String.valueOf(BloodReference.HTC_MALE_LOW));
                    }
                    break;
                case PatientStyle.YOUTH:
                    tvHgbHigh.setText(String.valueOf(BloodReference.HGB_YOUTH_HIGH));
                    tvHgbLow.setText(String.valueOf(BloodReference.HGB_YOUTH_LOW));
                    tvHtcHigh.setText(String.valueOf(BloodReference.HTC_NORMAL_HIGH));
                    tvHtcLow.setText(String.valueOf(BloodReference.HTC_NORMAL_LOW));
                    break;
                case PatientStyle.BABY:
                    tvHgbHigh.setText(String.valueOf(BloodReference.HGB_BABY_HIGH));
                    tvHgbLow.setText(String.valueOf(BloodReference.HGB_BABY_LOW));
                    tvHtcHigh.setText(String.valueOf(BloodReference.HTC_NORMAL_HIGH));
                    tvHtcLow.setText(String.valueOf(BloodReference.HTC_NORMAL_LOW));
                    break;
                default:
                    tvHgbHigh.setText(String.valueOf(BloodReference.HGB_MALE_HIGH));
                    tvHgbLow.setText(String.valueOf(BloodReference.HGB_MALE_LOW));
                    tvHtcHigh.setText(String.valueOf(BloodReference.HTC_MALE_HIGH));
                    tvHtcLow.setText(String.valueOf(BloodReference.HTC_MALE_LOW));
                    break;
            }
        }
    }

    /**
     * 折线图绘制方法
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
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data, idcard);
                // 构建红细胞压积值数据绘图BEAN
                final InitChartBean hrBean = createHrBean(measure);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int hgb = measureDataBean.getTrendValue(KParamType.BLOOD_HGB);
                            if (hgb != GlobalConstant.INVALID_TREND_DATA) {
                                dataList.add(NumUtil.trans2FloatValue(Float
                                        .valueOf((hgb / GlobalNumber.HUNDRED_NUMBER_FLOAT)), 1));
                            } else {
                                dataList.add(Float.valueOf(hgb));
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
                        bean.setMax_value(GlobalNumber.THIRTY_NUMBER_FLOAT);
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
                        bean.setIsDecimal(true);
                        //设置布局长度和宽度
                        BloodHbgChart spo2Chart = new BloodHbgChart(UiUitls
                                .getContent(), bean, hrBean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * lengthSize +
                                bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * ySize + bean
                                .getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width, heigth);
                        params.height = heigth;
                        params.width = width;
                        spo2Chart.setLayoutParams(params);
                        rlContent.addView(spo2Chart);
                        initTable();
                    }
                });
            }
        }).start();
    }

    /**
     * 构建红细胞积压值数据绘图BEAN
     * @param measure 测量值集合
     * @return 图表数据
     */
    private InitChartBean createHrBean(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        InitChartBean hrBean = new InitChartBean();
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int hgb = measureDataBean.getTrendValue(KParamType.BLOOD_HCT);
            if (hgb != GlobalConstant.INVALID_TREND_DATA) {
                dataList.add(Float.valueOf((hgb / GlobalNumber.HUNDRED_NUMBER_FLOAT)));
            } else {
                dataList.add(Float.valueOf(hgb));
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

        //设置X轴
        hrBean.setX_values(times);
        //设置单位
        hrBean.setUnit(" ");
        //设置Y轴刻度数量
        int ySize = GlobalNumber.TEN_NUMBER;
        hrBean.setY_SIZE(ySize);
        //设置绘图数据
        hrBean.setValues(data);
        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
        hrBean.setMax_value(GlobalNumber.SIXTY_NUMBER_FLOAT);
        hrBean.setMin_value(0.0f);

        //获得数据点数
        int lengthSize = hrBean.getX_values().length;
        //默认最大长度为100,布局容量
        if (hrBean.getX_values().length > hrBean.getMAX_SIZE()) {
            lengthSize = hrBean.getMAX_SIZE();
        } else if (hrBean.getX_values().length < GlobalNumber.TEN_NUMBER) {
            lengthSize = GlobalNumber.ELEVEN_NUMBER;
        }
        hrBean.setSize(lengthSize);
        return hrBean;
    }

    /**
     * 绘制表格
     */
    private void initTable() {
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
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data, idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add(UiUitls.getString(R.string.health_hemoglobin));
                        param.add(UiUitls.getString(R.string.red_blood));
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
                                    .BLOOD_HGB) == GlobalNumber.UN_THOUSAND_NUMBER)
                                    ? UiUitls.getString(R.string.no_test)
                                    : String.valueOf(NumUtil.trans2FloatValue((measure.get(i)
                                    .getTrendValue(KParamType.BLOOD_HGB)
                                    / GlobalNumber.HUNDRED_NUMBER_FLOAT), 1)));
                            list.add((measure.get(i).getTrendValue(KParamType.BLOOD_HCT)
                                    == GlobalNumber.UN_THOUSAND_NUMBER)
                                    ? UiUitls.getString(R.string.no_test) : (measure.get(i)
                                    .getTrendValue(KParamType.BLOOD_HCT))
                                    / GlobalNumber.HUNDRED_NUMBER_FLOAT + "");
                            data.add(list);
                        }
                        bean.setLINE_SIZE(lineSize);
                        bean.setROW_SIZE(rowSize);
                        bean.setX_VALUE(times);
                        bean.setY_VALUE(param);
                        bean.setData(data);
                        //————————————————加载布局——————————————————————
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
                        rlContentTable.addView(table);
                        table.setLayoutParams(params);
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
        idCard = SpUtils.getSp(UiUitls.getContent(), "app_config", "idcard", "");
        sexType = UiUitls.judgeSexByIdCard(idCard);
        if (sexType.equals(UiUitls.getString(R.string.boy))) {
            tvHgbHigh.setText(BloodMem.MAN_BLOOD_MAX + "");
            tvHgbLow.setText(BloodMem.MAN_BLOOD_MIN + "");
            tvHtcHigh.setText(BloodMem.MAN_HOGIN_MAX + "");
            tvHtcLow.setText(BloodMem.MAN_HOGIN_MIN + "");
        } else if (sexType.equals(UiUitls.getString(R.string.girl))) {
            tvHgbHigh.setText(BloodMem.WOMAN_BLOOD_MAX + "");
            tvHgbLow.setText(BloodMem.WOMAN_BLOOD_MIN + "");
            tvHtcHigh.setText(BloodMem.WOMAN_HOGIN_MAX + "");
            tvHtcLow.setText(BloodMem.WOMAN_HOGIN_MIN + "");
        }
        hgbMax = tvHgbHigh.getText().toString();
        hgbMin = tvHgbLow.getText().toString();
        htcMax = tvHtcHigh.getText().toString();
        htcMin = tvHtcLow.getText().toString();
        tvBloodHgbTrend.addTextChangedListener(new OverProofUtil(
                Float.valueOf(hgbMin), Float.valueOf(hgbMax), tvBloodHgbTrend));
        tvBloodHctTrend.addTextChangedListener(new OverProofUtil(
                Float.valueOf(htcMin), Float.valueOf(htcMax), tvBloodHctTrend));
        tvBloodHgbTrend.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HGB_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodHgbTrend.setText(String.valueOf(GlobalConstant
                    .BLOOD_HGB_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_HGB_VALUE, Float.valueOf(hgbMax)
                    , Float.valueOf(hgbMin), ivBloodHgb);
        }
        tvBloodHctTrend.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_HCT_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodHctTrend.setText(String.valueOf(GlobalConstant.BLOOD_HCT_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_HCT_VALUE, Float.valueOf(htcMax)
                    , Float.valueOf(htcMin), ivBloodHct);
        }
        // 模拟测量的数据
        irtempTrendList = new ArrayList<>();

    }

    /**
     * 初始化血红蛋白和红细胞积压值事件
     */
    private void initEventForBloodHgb() {
        PatientBean patient = null;
        final String idcard = SpUtils.getSp(UiUitls.getContent(), "app_config", "idcard", "");
        List<PatientBean> patients = DBDataUtil.getPatientByIdCard(idcard);
        if (patients.size() > 0) {
            patient = patients.get(0);
        }
        if (patient == null) {
            return;
        }
        int sex = patient.getSex();
        BloodReferenceBean bloodReference = new BloodReferenceBean();
        switch (patient.getPatient_type()) {
            case PatientStyle.ADULT:
                if (sex == Sex.FEMALE) {
                    bloodReference.setHgbHigh(BloodReference.HGB_FEMALE_HIGH);
                    bloodReference.setHgbLow(BloodReference.HGB_FEMALE_LOW);
                    bloodReference.setHtcHigh(BloodReference.HTC_FEMALE_HIGH);
                    bloodReference.setHtcLow(BloodReference.HTC_FEMALE_LOW);

                } else {
                    bloodReference.setHgbHigh(BloodReference.HGB_MALE_HIGH);
                    bloodReference.setHgbLow(BloodReference.HGB_MALE_LOW);
                    bloodReference.setHtcHigh(BloodReference.HTC_MALE_HIGH);
                    bloodReference.setHtcLow(BloodReference.HTC_MALE_LOW);
                }
                break;
            case PatientStyle.YOUTH:
                bloodReference.setHgbHigh(BloodReference.HGB_YOUTH_HIGH);
                bloodReference.setHgbLow(BloodReference.HGB_YOUTH_LOW);
                bloodReference.setHtcHigh(BloodReference.HTC_NORMAL_HIGH);
                bloodReference.setHtcLow(BloodReference.HTC_NORMAL_LOW);
                break;
            case PatientStyle.BABY:
                bloodReference.setHgbHigh(BloodReference.HGB_BABY_HIGH);
                bloodReference.setHgbLow(BloodReference.HGB_BABY_LOW);
                bloodReference.setHtcHigh(BloodReference.HTC_NORMAL_HIGH);
                bloodReference.setHtcLow(BloodReference.HTC_NORMAL_LOW);
                break;
            default:
                bloodReference.setHgbHigh(BloodReference.HGB_MALE_HIGH);
                bloodReference.setHgbLow(BloodReference.HGB_MALE_LOW);
                bloodReference.setHtcHigh(BloodReference.HTC_MALE_HIGH);
                bloodReference.setHtcLow(BloodReference.HTC_MALE_LOW);
                break;
        }
        tvBloodHgbTrend.addTextChangedListener(new OverProofUtil(
                bloodReference.getHgbLow(), bloodReference.getHgbHigh(),
                tvBloodHgbTrend));
        tvBloodHctTrend.addTextChangedListener(new OverProofUtil(
                bloodReference.getHtcLow(), bloodReference.getHtcHigh(),
                tvBloodHctTrend));
    }

    /**
     * 重绘表格
     */
    private void redraw() {
        this.rlContent.removeAllViews();
        this.rlContentTable.removeAllViews();
        this.initChart();
    }

    /**
     * 初始化绑定AIDLService服务器
     */
//    private void initAidlService() {
//        // intent的action为康尚aidl服务器
//        mIntent = new Intent("com.konsung.aidlServer");
//        // 一启动程序就绑定aidl service服务
//        // 保证service只运行一次，一直在后台运行
//        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
//        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
//        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
//        getActivity().startService(mIntent);
//        mIsBind = getActivity().bindService(mIntent, serviceConnection,
//                Context.BIND_AUTO_CREATE);
//    }

//    public ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            aidlServer = ((AIDLServer.MsgBinder) service).getService();
//            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
//                @Override
//                public void sendParaStatus(String name, String version) {
//
//                }
//
//                @Override
//                public void sendWave(int param, byte[] bytes) {
//
//                }
//
//                @Override
//                public void sendTrend(int param, int value) {
//                    switch (param) {
//                        case KParamType.BLOOD_HGB:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                //由于血红蛋白里是 g/dL 再除以10.保存到数据库也默认除以10
//                                float v1 = (float) value / GlobalConstant.TREND_FACTOR
//                                        / 10f * GlobalConstant.TREND_HGB;
//                                // 获取转换后的值
//                                float v2 = NumUtil.trans2FloatValue(v1, 1);
//                                isGetValue = true;
//                                tvBloodHgbTrend.setText(String.valueOf(v2));
//                                GlobalConstant.BLOOD_HGB_VALUE = v2;
//                                int hgbValue = (int) (v2 * GlobalConstant.TREND_FACTOR);
//                                aidlServer.saveTrend(KParamType.BLOOD_HGB, hgbValue);
//                                aidlServer.saveToDb2();
//                                AlarmUtil.executeOverrunAlarm(v1, Float.valueOf(hgbMax)
//                                        , Float.valueOf(hgbMin), ivBloodHgb);
//                            }
//                            break;
//                        case KParamType.BLOOD_HCT:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                int v1 = value / GlobalConstant.TREND_FACTOR;
//                                isGetValue = true;
//                                tvBloodHctTrend.setText(String.valueOf(v1));
//                                GlobalConstant.BLOOD_HCT_VALUE = v1;
//                                aidlServer.saveTrend(KParamType.BLOOD_HCT, value);
//                                aidlServer.saveToDb2();
//                                AlarmUtil.executeOverrunAlarm(v1, Float.valueOf(htcMax)
//                                        , Float.valueOf(htcMin), ivBloodHct);
//                                redraw();
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//                @Override
//                public void sendConfig(int param, int value) {
//
//                }
//
//                @Override
//                public void sendPersonalDetail(String name, String idcard,
//                                               int sex, int type) {
//
//                }
//
//                @Override
//                public void send12LeadDiaResult(byte[] bytes) {
//
//                }
//            });
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };
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
                switch (param) {
                    case KParamType.BLOOD_HGB:
                        if (value != GlobalConstant.INVALID_DATA) {
                            //由于血红蛋白里是 g/dL 再除以10.保存到数据库也默认除以10
                            float v1 = (float) value / GlobalConstant.TREND_FACTOR
                                    / GlobalNumber.TEN_NUMBER_FLOAT * GlobalConstant.TREND_HGB;
                            // 获取转换后的值
                            float v2 = NumUtil.trans2FloatValue(v1, 1);
                            isGetValue = true;
                            tvBloodHgbTrend.setText(String.valueOf(v2));
                            GlobalConstant.BLOOD_HGB_VALUE = v2;
                            int hgbValue = (int) (v2 * GlobalConstant.TREND_FACTOR);
                            ServiceUtils.saveTrend(KParamType.BLOOD_HGB, hgbValue);
                            ServiceUtils.saveToDb2();
                            AlarmUtil.executeOverrunAlarm(v1, Float.valueOf(hgbMax)
                                    , Float.valueOf(hgbMin), ivBloodHgb);
                        }
                        break;
                    case KParamType.BLOOD_HCT:
                        if (value != GlobalConstant.INVALID_DATA) {
                            int v1 = value / GlobalConstant.TREND_FACTOR;
                            isGetValue = true;
                            tvBloodHctTrend.setText(String.valueOf(v1));
                            GlobalConstant.BLOOD_HCT_VALUE = v1;
                            ServiceUtils.saveTrend(KParamType.BLOOD_HCT, value);
                            ServiceUtils.saveToDb2();
                            AlarmUtil.executeOverrunAlarm(v1, Float.valueOf(htcMax)
                                    , Float.valueOf(htcMin), ivBloodHct);
                            redraw();
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
}
