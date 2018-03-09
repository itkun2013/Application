package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.NibpChart;
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.AlarmUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血脂单测类
 */
public class LipidsFragment extends BaseFragment {
    // 参数值显示控件
    @InjectView(R.id.tv_chol)
    TextView tvChol;
    @InjectView(R.id.tv_trig)
    TextView tvTrig;
    @InjectView(R.id.tv_hdl)
    TextView tvHdl;
    @InjectView(R.id.tv_ldl)
    TextView tvLdl;
    // 报警图标显示控件
    @InjectView(R.id.iv_chol_icon)
    ImageView ivCholIcon;
    @InjectView(R.id.iv_trig_icon)
    ImageView ivTrigIcon;
    @InjectView(R.id.iv_hdl_icon)
    ImageView ivHdlIcon;
    @InjectView(R.id.iv_ldl_icon)
    ImageView ivLdlIcon;
    // 参考范围显示控件
    @InjectView(R.id.tv_chol_max)
    TextView tvCholMax;
    @InjectView(R.id.tv_chol_min)
    TextView tvCholMin;
    @InjectView(R.id.tv_trig_max)
    TextView tvTrigMax;
    @InjectView(R.id.tv_trig_min)
    TextView tvTrigMin;
    @InjectView(R.id.tv_hdl_max)
    TextView tvHdlMax;
    @InjectView(R.id.tv_hdl_min)
    TextView tvHdlMin;
    @InjectView(R.id.tv_ldl_max)
    TextView tvLdlMax;
    @InjectView(R.id.tv_ldl_min)
    TextView tvLdlMin;
    @InjectView(R.id.content_sys)
    RelativeLayout rlContentSys;
    @InjectView(R.id.content_map)
    RelativeLayout rlContentMap;
    @InjectView(R.id.content_dia)
    RelativeLayout rlContentDia;
    @InjectView(R.id.content_pr)
    RelativeLayout rlContentPr;
    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;
    private View view;
    public AIDLServer aidlServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity(), "app_config", "name", "");
        boolean check = name.equals("");
//        if (check) {
//            Toast.makeText(getActivity(), getRecString(R.string.fragment_pls_select_patient),
//                    Toast.LENGTH_SHORT).show();
//            GetAllPatientFragment allPatientFragment = new GetAllPatientFragment();
//            switchToFragment(R.id.fragment, allPatientFragment, "allPatientFragment", true);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lipids, null);
        ButterKnife.inject(this, view);
        initView();
        initMeasureListener();
        initChart();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ServiceUtils.setOnMessageSendListener(null);
    }


    /**
     * 初始化界面显示
     */
    private void initView() {
        //设置超值数据字体显红
        tvChol.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_CHOL_ALARM_LOW
                , GlobalConstant.LIPIDS_CHOL_ALARM_HIGH, tvChol));
        tvTrig.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_TRIG_ALARM_LOW
                , GlobalConstant.LIPIDS_TRIG_ALARM_HIGH, tvTrig));
        tvHdl.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_HDL_ALARM_LOW
                , GlobalConstant.LIPIDS_HDL_ALARM_HIGH, tvHdl));
        tvLdl.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_LDL_ALARM_LOW
                , GlobalConstant.LIPIDS_LDL_ALARM_HIGH, tvLdl));

        tvCholMax.setText("" + GlobalConstant.LIPIDS_CHOL_ALARM_HIGH);
        tvCholMin.setText("" + GlobalConstant.LIPIDS_CHOL_ALARM_LOW);
        tvTrigMax.setText("" + GlobalConstant.LIPIDS_TRIG_ALARM_HIGH);
        tvTrigMin.setText("" + GlobalConstant.LIPIDS_TRIG_ALARM_LOW);
        tvHdlMax.setText("" + GlobalConstant.LIPIDS_HDL_ALARM_HIGH);
        tvHdlMin.setText("" + GlobalConstant.LIPIDS_HDL_ALARM_LOW);
        tvLdlMax.setText("" + GlobalConstant.LIPIDS_LDL_ALARM_HIGH);
        tvLdlMin.setText("" + GlobalConstant.LIPIDS_LDL_ALARM_LOW);
        tvChol.setText(UiUitls.getString(R.string.default_value));
        tvTrig.setText(UiUitls.getString(R.string.default_value));
        tvHdl.setText(UiUitls.getString(R.string.default_value));
        tvLdl.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.LIPIDS_CHOL_VALUE != GlobalConstant.INVALID_DATA) {
            tvChol.setText(getFormatterStr(KParamType.LIPIDS_CHOL
                    , GlobalConstant.LIPIDS_CHOL_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.LIPIDS_CHOL_VALUE,
                    GlobalConstant.LIPIDS_CHOL_ALARM_HIGH
                    , GlobalConstant.LIPIDS_CHOL_ALARM_LOW, ivCholIcon);
        }

        if (GlobalConstant.LIPIDS_TRIG_VALUE != GlobalConstant.INVALID_DATA) {
            tvTrig.setText(getFormatterStr(KParamType.LIPIDS_TRIG
                    , GlobalConstant.LIPIDS_TRIG_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.LIPIDS_TRIG_VALUE,
                    GlobalConstant.LIPIDS_TRIG_ALARM_HIGH
                    , GlobalConstant.LIPIDS_TRIG_ALARM_LOW, ivTrigIcon);
        }

        if (GlobalConstant.LIPIDS_HDL_VALUE != GlobalConstant.INVALID_DATA) {
            tvHdl.setText(getFormatterStr(KParamType.LIPIDS_HDL, GlobalConstant.LIPIDS_HDL_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.LIPIDS_HDL_VALUE,
                    GlobalConstant.LIPIDS_HDL_ALARM_HIGH, GlobalConstant.LIPIDS_HDL_ALARM_LOW,
                    ivHdlIcon);
        }

        if (GlobalConstant.LIPIDS_LDL_VALUE != GlobalConstant.INVALID_DATA) {
            tvLdl.setText(getFormatterStr(KParamType.LIPIDS_LDL, GlobalConstant.LIPIDS_LDL_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.LIPIDS_LDL_VALUE,
                    GlobalConstant.LIPIDS_LDL_ALARM_HIGH, GlobalConstant.LIPIDS_LDL_ALARM_LOW,
                    ivLdlIcon);
        }
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
//                    float temp;
//                    if (value == -10) {
//                        temp = -10f;
//                    } else if (value == -100) {
//                        temp = -100f;
//                    } else {
//                        temp = value / 100f;
//                    }
//                    switch (param) {
//                        case KParamType.LIPIDS_CHOL:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                String data = getFormatterStr(KParamType.LIPIDS_CHOL, temp);
//                                AlarmUtil.executeOverrunAlarm(temp,
//                                        GlobalConstant.LIPIDS_CHOL_ALARM_HIGH
//                                        , GlobalConstant.LIPIDS_CHOL_ALARM_LOW, ivCholIcon);
//                                tvChol.setText(data);
//                                GlobalConstant.LIPIDS_CHOL_VALUE = temp;
//                                aidlServer.saveTrend(KParamType.LIPIDS_CHOL, value);
//                                aidlServer.saveToDb2();
//                            }
//                            break;
//                        case KParamType.LIPIDS_TRIG:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                String data = getFormatterStr(KParamType.LIPIDS_TRIG, temp);
//                                AlarmUtil.executeOverrunAlarm(temp,
//                                        GlobalConstant.LIPIDS_TRIG_ALARM_HIGH
//                                        , GlobalConstant.LIPIDS_TRIG_ALARM_LOW, ivTrigIcon);
//                                tvTrig.setText(data);
//                                GlobalConstant.LIPIDS_TRIG_VALUE = temp;
//                                aidlServer.saveTrend(KParamType.LIPIDS_TRIG, value);
//                                aidlServer.saveToDb2();
//                            }
//                            break;
//                        case KParamType.LIPIDS_HDL:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                String data = getFormatterStr(KParamType.LIPIDS_HDL, temp);
//                                AlarmUtil.executeOverrunAlarm(temp,
//                                        GlobalConstant.LIPIDS_HDL_ALARM_HIGH
//                                        , GlobalConstant.LIPIDS_HDL_ALARM_LOW, ivHdlIcon);
//                                tvHdl.setText(data);
//                                GlobalConstant.LIPIDS_HDL_VALUE = temp;
//                                aidlServer.saveTrend(KParamType.LIPIDS_HDL, value);
//                                aidlServer.saveToDb2();
//                            }
//                            break;
//                        case KParamType.LIPIDS_LDL:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                String data = getFormatterStr(KParamType.LIPIDS_LDL, temp);
//                                AlarmUtil.executeOverrunAlarm(temp
//                                        , GlobalConstant.LIPIDS_LDL_ALARM_HIGH
//                                        , GlobalConstant.LIPIDS_LDL_ALARM_LOW, ivLdlIcon);
//                                GlobalConstant.LIPIDS_LDL_VALUE = temp;
//                                aidlServer.saveTrend(KParamType.LIPIDS_LDL, value);
//                                aidlServer.saveToDb2();
//                                tvLdl.setText(data);
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
//                public void sendPersonalDetail(String name, String idcard, int sex, int type) {
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
                float temp;
                if (value == -10) {
                    temp = -10f;
                } else if (value == -100) {
                    temp = -100f;
                } else {
                    temp = value / 100f;
                }
                switch (param) {
                    case KParamType.LIPIDS_CHOL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = getFormatterStr(KParamType.LIPIDS_CHOL, temp);
                            AlarmUtil.executeOverrunAlarm(temp,
                                    GlobalConstant.LIPIDS_CHOL_ALARM_HIGH
                                    , GlobalConstant.LIPIDS_CHOL_ALARM_LOW, ivCholIcon);
                            tvChol.setText(data);
                            GlobalConstant.LIPIDS_CHOL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_CHOL, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.LIPIDS_TRIG:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = getFormatterStr(KParamType.LIPIDS_TRIG, temp);
                            AlarmUtil.executeOverrunAlarm(temp,
                                    GlobalConstant.LIPIDS_TRIG_ALARM_HIGH
                                    , GlobalConstant.LIPIDS_TRIG_ALARM_LOW, ivTrigIcon);
                            tvTrig.setText(data);
                            GlobalConstant.LIPIDS_TRIG_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_TRIG, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.LIPIDS_HDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = getFormatterStr(KParamType.LIPIDS_HDL, temp);
                            AlarmUtil.executeOverrunAlarm(temp,
                                    GlobalConstant.LIPIDS_HDL_ALARM_HIGH
                                    , GlobalConstant.LIPIDS_HDL_ALARM_LOW, ivHdlIcon);
                            tvHdl.setText(data);
                            GlobalConstant.LIPIDS_HDL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_HDL, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.LIPIDS_LDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            String data = getFormatterStr(KParamType.LIPIDS_LDL, temp);
                            AlarmUtil.executeOverrunAlarm(temp
                                    , GlobalConstant.LIPIDS_LDL_ALARM_HIGH
                                    , GlobalConstant.LIPIDS_LDL_ALARM_LOW, ivLdlIcon);
                            GlobalConstant.LIPIDS_LDL_VALUE = temp;
                            ServiceUtils.saveTrend(KParamType.LIPIDS_LDL, value);
                            ServiceUtils.saveToDb2();
                            tvLdl.setText(data);
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
    /**
     * 刷新
     */
    private void redraw() {
        rlContentMap.removeAllViews();
        rlContentDia.removeAllViews();
        rlContentPr.removeAllViews();
        rlContentTable.removeAllViews();
        rlContentSys.removeAllViews();
        this.initChart();
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
                final ArrayList<float[]> trigDatas = createTrigDatas(measure);
                final ArrayList<float[]> hdlDatas = createHdlDatas(measure);
                final ArrayList<float[]> ldlDatas = createLdlDatas(measure);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int ecgHr = measureDataBean.getTrendValue(KParamType.LIPIDS_CHOL);
                            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                                if (ecgHr == -10) {
                                    dataList.add(GlobalConstant.LIPIDS_CHOL_ALARM_BELOW_VALUE);
                                } else if (ecgHr == -100) {
                                    dataList.add(GlobalConstant.LIPIDS_CHOL_ALARM_ABOVE_VALUE);
                                } else {
                                    dataList.add(ecgHr / 100f);
                                }
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
                        bean.setUnit(UiUitls.getString(R.string
                                .health_unit_mol));
                        //设置Y轴刻度数量
                        int Y_SIZE = 10;
                        bean.setY_SIZE(Y_SIZE);
                        //设置绘图数据
                        bean.setValues(data);
                        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
                        bean.setMax_value(15.0f);
                        bean.setMin_value(0.0f);
                        //获得数据点数
                        int length_size = bean.getX_values().length + 1;
                        //默认最大长度为100,布局容量
                        if (bean.getX_values().length > bean.getMAX_SIZE()) {
                            length_size = bean.getMAX_SIZE();
                        } else if (bean.getX_values().length < 10) {
                            length_size = 11;
                        }
                        bean.setSize(length_size);
                        bean.setIsDecimal(true);
                        //设置布局长度和宽度
                        NibpChart chart = new NibpChart(UiUitls.getContent(),
                                bean, trigDatas, hdlDatas, ldlDatas);
                        chart.setDrawRightUnit(false);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * length_size +
                                bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * Y_SIZE
                                + bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width, heigth);
                        params.height = heigth;
                        params.width = width;
                        rlContentSys.addView(chart);
                        chart.setLayoutParams(params);
                        initTable();
                    }
                });
            }
        }).start();
    }

    /**
     * 绘制表格
     */
    private void initTable() {
        //数据集
        final Time_paramBean bean = new Time_paramBean();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;" +
                "HH:mm:ss");
        //从数据库中获取测量数据
        final String idcard = SpUtils.getSp(UiUitls.getContent(),
                "app_config", "idcard", "");
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
                        param.add("总胆固醇;(mmol/L)");
                        param.add("甘油三酯;(mmol/L)");
                        param.add("高密度脂蛋白;(mmol/L)");
                        param.add("低密度脂蛋白;(mmol/L)");
                        //时间-----X_NAME
                        ArrayList<String> times = new ArrayList<>();
                        ArrayList<ArrayList<String>> data = new ArrayList<>();
                        //行数---参数个数
                        int row_size = measure.size();
                        int line_size = param.size();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            ArrayList<String> list = new ArrayList<>();
                            times.add(sdf.format(measure.get(i).getMeasureTime()));
                            list.add(formatterStr(KParamType.LIPIDS_CHOL
                                    , measure.get(i).getTrendValue(KParamType.LIPIDS_CHOL)));
                            list.add(formatterStr(KParamType.LIPIDS_TRIG
                                    , measure.get(i).getTrendValue(KParamType.LIPIDS_TRIG)));
                            list.add(formatterStr(KParamType.LIPIDS_HDL
                                    , measure.get(i).getTrendValue(KParamType.LIPIDS_HDL)));
                            list.add(formatterStr(KParamType.LIPIDS_LDL
                                    , measure.get(i).getTrendValue(KParamType.LIPIDS_LDL)));
//                            list.add((measure.get(i).getTrendValue(KParamType
//                                    .LIPIDS_TRIG) == -1000) ? UiUitls.getString(R.string
//                                    .no_test) :
//                                    String.valueOf(measure.get(i)
//                                            .getTrendValue(KParamType
//                                                    .LIPIDS_TRIG) / 100f));
//                            list.add((measure.get(i).getTrendValue(KParamType
//                                    .LIPIDS_HDL) == -1000) ? UiUitls.getString(R.string
//                                    .no_test) :
//                                    String.valueOf(measure.get(i)
//                                            .getTrendValue(KParamType
//                                                    .LIPIDS_HDL) / 100f));
//                            list.add((measure.get(i).getTrendValue(KParamType
//                                    .LIPIDS_LDL) == -1000) ? UiUitls.getString(R
//                                    .string
//                                    .no_test) :
//                                    String.valueOf(measure.get(i)
//                                            .getTrendValue(KParamType
//                                                    .LIPIDS_LDL) / 100f));
                            data.add(list);
                        }
                        bean.setLINE_SIZE(line_size);
                        bean.setROW_SIZE(row_size);
                        bean.setX_VALUE(times);
                        bean.setY_VALUE(param);
                        bean.setData(data);
                        //————————————————加载布局——————————————————————
                        //设置布局长度和宽度
                        TimeParamTable table = new TimeParamTable(UiUitls
                                .getContent(), bean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_X() * (row_size + 1)
                                + bean.getPADDING_LEFT() + bean
                                .getPADDING_RIGHT());
                        int height = (int) (bean.getSCALE_Y() * (line_size +
                                1) + bean.getPADDING_TOP() + bean
                                .getPADDING_BOTTOM());
                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width, ViewGroup
                                .LayoutParams.MATCH_PARENT);
                        params.height = height;
                        params.width = width;
                        rlContentTable.addView(table);
                        table.setLayoutParams(params);
                    }
                });
            }
        }).start();
    }

    /**
     *
     */
    private ArrayList<float[]> createTrigDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.LIPIDS_TRIG);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                if (ecgHr == -10) {
                    dataList.add(GlobalConstant.LIPIDS_TRIG_ALARM_BELOW_VALUE);
                } else if (ecgHr == -100) {
                    dataList.add(GlobalConstant.LIPIDS_TRIG_ALARM_ABOVE_VALUE);
                } else {
                    dataList.add(ecgHr / 100f);
                }
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
        return data;
    }

    /**
     *
     */
    private ArrayList<float[]> createHdlDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.LIPIDS_HDL);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                if (ecgHr == -10) {
                    dataList.add(GlobalConstant.LIPIDS_HDL_ALARM_BELOW_VALUE);
                } else if (ecgHr == -100) {
                    dataList.add(GlobalConstant.LIPIDS_HDL_ALARM_ABOVE_VALUE);
                } else {
                    dataList.add(ecgHr / 100f);
                }
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
        return data;
    }

    /**
     *
     */
    private ArrayList<float[]> createLdlDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.LIPIDS_LDL);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                if (ecgHr == -10) {
                    dataList.add(GlobalConstant.LIPIDS_LDL_ALARM_BELOW_VALUE);
                } else if (ecgHr == -100) {
                    dataList.add(GlobalConstant.LIPIDS_LDL_ALARM_ABOVE_VALUE);
                } else {
                    dataList.add(ecgHr / 100f);
                }
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
        return data;
    }

    /**
     *获取转换后的结果值
     * @param type 测量类型标识
     * @param value 测量结果
     * @return 转换后的结果
     */
    private String getFormatterStr(int type, float value) {
        //超低指标
        int supLow = GlobalConstant.valueMin;
        //超高指标
        int supHigh = GlobalConstant.valueMax;

        switch (type) {
            //指标数据来源于爱康血脂分析仪
            case KParamType.LIPIDS_CHOL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_TRIG:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_HDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_LDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_ABOVE;
                }
                break;
            default:
                break;
        }
        return value + "";
    }

    /**
     *测量结果值转换
     * @param type 测量类型标识
     * @param value 测量结果
     * @return 返回转换后的结果值
     */
    private String formatterStr(int type, float value) {
        //超低指标
        int supLow = GlobalConstant.valueMin;
        //超高指标
        int supHigh = GlobalConstant.valueMax;
        if (value == GlobalConstant.INVALID_DATA) {
            return UiUitls.getString(R.string.no_test);
        }
        switch (type) {
            //指标数据来源于爱康血脂分析仪
            case KParamType.LIPIDS_CHOL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_TRIG:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_HDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_LDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_ABOVE;
                }
                break;
            default:
                break;
        }
        return value / GlobalConstant.FACTOR + "";
    }
}
