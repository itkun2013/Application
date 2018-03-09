package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.BrokenLineTable;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.NibpChart;
import com.konsung.defineview.TimeParamTable;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.AlarmUtil;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.Nibp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * This file is built by GBK
 * please reload in GBK
 * <p/>
 * 血压
 * Created by JustRush on 2015/6/17.
 */
public class NibpFragment extends BaseFragment {
    // 参数
    @InjectView(R.id.nibp_dia_tv)
    TextView tvDiaView;
    @InjectView(R.id.nibp_sys_tv)
    TextView tvSysView;
    @InjectView(R.id.nibp_map_tv)
    TextView tvMapView;
    @InjectView(R.id.nibp_pr_tv)
    TextView tvPrView;
    @InjectView(R.id.nibp_cuff)
    TextView tvCuffView;
    @InjectView(R.id.measure_btn)
    ImageTextButton btnMeasure;

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

    @InjectView(R.id.scrollView)
    ScrollView sclViScrollView;
    //脉率
    @InjectView(R.id.iv_nibp_pr)
    ImageView ivPr;
    //舒张压
    @InjectView(R.id.iv_nibp_dia)
    ImageView ivDia;
    //收缩压
    @InjectView(R.id.iv_nibp_sys)
    ImageView ivSys;
/*    @InjectView(R.id.back_btn) ButtonRectangle btnBack;*/

    // aidl接口
   /* private IKonsungAidlInterface _aidl = null; // AIDL接口*/
    private Intent intent = null;              // 用于绑定AIDL
    private boolean isBind = false;            // 是否已经绑定服务
    private int measureState = 0;              // 血压测量状态
    private Handler handler = new Handler();
    private int cuffStatic;
    private View view;
    //正在测量中
    private boolean isChecking = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(UiUitls.getContent(), "app_config", "name", "");
        boolean check = name.equals("");
//        if (check) {
//            Toast.makeText(UiUitls.getContent()
//                    , getRecString(R.string.fragment_pls_select_patient), Toast.LENGTH_SHORT).show();
//            GetAllPatientFragment allPatientFragment = new GetAllPatientFragment();
//            switchToFragment(R.id.fragment, allPatientFragment, "allPatientFragment", true);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nibp, null);
        isAttach = true;
        cuffStatic = 0;
        //初始化控件
        ButterKnife.inject(this, view);
        initEvent();
        initView();
        initMeasureListener();
        // 初始化底部按钮
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.aidlServer == null) {
                    return;
                }
                if (measureState == 0) {
                    initView();
                   /* handler.post(updateThread);*/            // 更新数据
                        /*_aidlInterface.sendNibpConfig(0x05, 0);         //
                        发送启动测量指令*/
//                    EchoServerEncoder.setEcgConfig((short)0x05,3);
                    // 暂时通过发送两条命令的方式，解决连续多次快速点击启动测量
                    // 按钮时，偶发下发命令不成功的问题
                    EchoServerEncoder.setNibpConfig((short) 0x05, 0);
                    EchoServerEncoder.setNibpConfig((short) 0x05, 0);
                    cuffStatic = 0;
                    //EchoServerEncoder.setNibpConfig((short) 0x07,1);
                    btnMeasure.setText(getRecString(R.string.nibp_btn_stop));
                    measureState = 1;
                } else {
                    initView();
                    // _aidlInterface.sendNibpConfig(0x06, 0);         //
                    // 发送停止测量指令
                    // 暂时通过发送两条命令的方式，解决连续多次快速点击启动测量
                    // 按钮时，偶发下发命令不成功的问题
                    EchoServerEncoder.setNibpConfig((short) 0x06, 0);
                    EchoServerEncoder.setNibpConfig((short) 0x06, 0);
                    btnMeasure.setText(getRecString(R.string
                            .nibp_btn_start));
                    /*handler.removeCallbacks(updateThread);*/
                    measureState = 0;
                }
            }
        });
//        initChart_SYS();
//        initChart_DIA();
//        initChart_MAP();
//        initChart_PR();
        initChart();
        return view;
    }
    /**
     * 初始化事件
     */
    private void initEvent() {
        tvSysView.addTextChangedListener(new OverProofUtil(Nibp.SYS_LOW, Nibp.SYS_HIGH, tvSysView));
        tvDiaView.addTextChangedListener(new OverProofUtil(Nibp.DIA_LOW, Nibp.DIA_HIGH, tvDiaView));
        tvMapView.addTextChangedListener(new OverProofUtil(Nibp.MAP_LOW, Nibp.MAP_HIGH, tvMapView));
        tvPrView.addTextChangedListener(new OverProofUtil(Nibp.PR_LOW, Nibp.PR_HIGH, tvPrView));
    }

    /**
     * 重绘折现图
     */
    private void redrawChart() {
        rlContentMap.removeAllViews();
        rlContentDia.removeAllViews();
        rlContentPr.removeAllViews();
        rlContentTable.removeAllViews();
        rlContentSys.removeAllViews();
        this.initChart();
    }

    /**
     *绘制折线图表方法
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
                final List<MeasureDataBean> measure = DBDataUtil.getMeasures(idcard);
                final ArrayList<float[]> diaDatas = createDiaDatas(measure);
//                final ArrayList<float[]> avgDatas = createAvgDatas(measure);
                final ArrayList<float[]> prDatas = createPrDatas(measure);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int ecgHr = measureDataBean.getTrendValue(KParamType.NIBP_SYS);
                            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                                dataList.add(Float.valueOf((ecgHr / GlobalConstant.TREND_FACTOR)));
                            } else {
                                dataList.add(Float.valueOf(ecgHr));
                            }
                            timeList.add(sdf.format(measureDataBean
                                    .getMeasureTime()));
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
                                .health_unit_mmhg));
                        //设置Y轴刻度数量
                        int Y_SIZE = 10;
                        bean.setY_SIZE(Y_SIZE);
                        //设置绘图数据
                        bean.setValues(data);
                        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
                        bean.setMax_value(300.0f);
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
                        bean.setIsDecimal(false);
                        //设置布局长度和宽度
                        NibpChart chart = new NibpChart(UiUitls.getContent()
                                , bean, diaDatas, prDatas);

                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * length_size +
                                bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * Y_SIZE + bean
                                .getPADDING_LEFT() + bean.getPADDING_RIGHT());

                        RelativeLayout.LayoutParams params
                                = new RelativeLayout.LayoutParams(width, heigth);
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


    private ArrayList<float[]> createAvgDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.NIBP_MAP);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                dataList.add(Float.valueOf((ecgHr / GlobalConstant.TREND_FACTOR)));
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

    private ArrayList<float[]> createDiaDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.NIBP_DIA);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                dataList.add(Float.valueOf((ecgHr / GlobalConstant
                        .TREND_FACTOR)));
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

    private ArrayList<float[]> createPrDatas(List<MeasureDataBean> measure) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.NIBP_PR);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                dataList.add(Float.valueOf((ecgHr / GlobalConstant.TREND_FACTOR)));
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
     * 绘制折线图
     */
    private void initChart_SYS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        //从数据库中获取数据
        String idcard = SpUtils.getSp(getActivity(), "app_config", "idcard", "");
        List<MeasureDataBean> measure = DBDataUtil.getMeasures(idcard);
        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int ecgHr = measureDataBean.getTrendValue(KParamType.NIBP_SYS);
            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                dataList.add(Float.valueOf((ecgHr / GlobalConstant.TREND_FACTOR)));
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
        bean.setUnit(UiUitls.getString(R.string.health_unit_mmhg));
        //设置Y轴刻度数量
        int Y_SIZE = 10;
        bean.setY_SIZE(Y_SIZE);
        //设置绘图数据
        bean.setValues(data);
        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
        bean.setMax_value(300.0f);
        bean.setMin_value(0.0f);

        //获得数据点数
        int length_size = bean.getX_values().length;
        //默认最大长度为100,布局容量
        if (bean.getX_values().length > bean.getMAX_SIZE()) {
            length_size = bean.getMAX_SIZE();
        } else if (bean.getX_values().length < 10) {
            length_size = 11;
        }
        bean.setSize(length_size);

        //设置布局长度和宽度
        BrokenLineTable chart = new BrokenLineTable(UiUitls.getContent(), bean);
        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
        int width = (int) (bean.getSCALE_x() * length_size + bean
                .getPADDING_LEFT() + bean.getPADDING_RIGHT());
        int heigth = (int) (bean.getSCALE_Y() * Y_SIZE + bean.getPADDING_LEFT()
                + bean.getPADDING_RIGHT());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, heigth);
        params.height = heigth;
        params.width = width;
        rlContentSys.addView(chart);
        chart.setLayoutParams(params);
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
                final List<MeasureDataBean> measure = DBDataUtil.getMeasures(idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add("收缩压(mmHg)");
                        param.add("舒张压(mmHg)");
//                        param.add("平均压(mmHg)");
                        param.add("脉率(bpm)");
                        //时间-----X_NAME
                        ArrayList<String> times = new ArrayList<>();
                        ArrayList<ArrayList<String>> data = new ArrayList<>();
                        //行数---参数个数
                        int row_size = measure.size();
                        int line_size = param.size();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            ArrayList<String> list = new ArrayList<>();
                            times.add(sdf.format(measure.get(i).getMeasureTime()));
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .NIBP_SYS) == -1000) ? UiUitls.getString(R.string
                                    .no_test) :
                                    String.valueOf(measure.get(i)
                                            .getTrendValue(KParamType.NIBP_SYS) /
                                            GlobalConstant.TREND_FACTOR));
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .NIBP_DIA) == -1000) ? UiUitls.getString(R.string
                                    .no_test) :
                                    String.valueOf(measure.get(i)
                                            .getTrendValue(KParamType.NIBP_DIA) /
                                            GlobalConstant.TREND_FACTOR));
//                            list.add((measure.get(i).getTrendValue(KParamType
//                                    .NIBP_MAP) == -1000) ? UiUitls.getString(R.string
//                                    .no_test) :
//                                    String.valueOf(measure.get(i)
//                                            .getTrendValue(KParamType
//                                                    .NIBP_MAP) /
//                                            GlobalConstant.TREND_FACTOR));
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .NIBP_PR) == -1000) ? UiUitls.getString(R
                                    .string
                                    .no_test) :
                                    String.valueOf(measure.get(i)
                                            .getTrendValue(KParamType.NIBP_PR) /
                                            GlobalConstant.TREND_FACTOR));
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
                                + bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int height = (int) (bean.getSCALE_Y() * (line_size +
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

    /**
     * 初始化
     */
    private void initView() {
        tvSysView.setText(UiUitls.getString(R.string.default_value));
        tvDiaView.setText(UiUitls.getString(R.string.default_value));
        tvMapView.setText(UiUitls.getString(R.string.default_value));
        tvPrView.setText(UiUitls.getString(R.string.default_value));
        tvCuffView.setText("");
        //收缩压
        if (GlobalConstant.NIBP_SYS_VALUE != GlobalConstant.INVALID_DATA) {
            tvSysView.setText(String.valueOf(GlobalConstant.NIBP_SYS_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.NIBP_SYS_VALUE
                    , Nibp.SYS_HIGH, Nibp.SYS_LOW, ivSys);
        }
        //舒张压
        if (GlobalConstant.NIBP_DIA_VALUE != GlobalConstant.INVALID_DATA) {
            tvDiaView.setText(String.valueOf(GlobalConstant.NIBP_DIA_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.NIBP_DIA_VALUE
                    , Nibp.DIA_HIGH, Nibp.DIA_LOW, ivDia);
        }
        //平均压(去掉了)
        if (GlobalConstant.NIBP_MAP_VALUE != GlobalConstant.INVALID_DATA) {
            tvMapView.setText(String.valueOf(GlobalConstant.NIBP_MAP_VALUE));
        }
        //脉率
        if (GlobalConstant.NIBP_PR_VALUE != GlobalConstant.INVALID_DATA) {
            tvPrView.setText(String.valueOf(GlobalConstant.NIBP_PR_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.NIBP_PR_VALUE
                    , Nibp.PR_HIGH, Nibp.PR_LOW, ivPr);
        }
    }

    /**
     * 初始化绑定AIDLService服务器
     */
//    public void initAidlService() {
//        // intent的action为康尚aidl服务器
//        intent = new Intent("com.konsung.aidlServer");
//        // 一启动程序就绑定aidl service服务
//        // 保证service只运行一次，一直在后台运行
//        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
//        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
//        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
//        getActivity().startService(intent);
//        isBind = getActivity().bindService(intent, serviceConnection
//                , Context.BIND_AUTO_CREATE);
//    }
//
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
//                        case KParamType.NIBP_SYS:
//                            if (value > 0) {
//                                aidlServer.saveTrend(KParamType.NIBP_SYS, value);
//                                tvSysView.setText(String.valueOf(value /
//                                        GlobalConstant.TREND_FACTOR));
//                                AlarmUtil.executeOverrunAlarm(Float.valueOf(tvSysView.getText()
//                                        .toString()), Nibp.SYS_HIGH, Nibp.SYS_LOW, ivSys);
//                                GlobalConstant.NIBP_SYS_VALUE = value /
//                                        GlobalConstant.TREND_FACTOR;
//                                aidlServer.saveToDb2();
//                            }
//                            break;
//                        case KParamType.NIBP_DIA:
//                            if (measureState == 2) {
//                                if (value > 0) {
//                                    tvDiaView.setText(String.valueOf(value /
//                                            GlobalConstant.TREND_FACTOR));
//                                    AlarmUtil.executeOverrunAlarm(Float.valueOf(tvDiaView.getText()
//                                            .toString()), Nibp.DIA_HIGH, Nibp.DIA_LOW, ivDia);
//                                    GlobalConstant.NIBP_DIA_VALUE = value /
//                                            GlobalConstant.TREND_FACTOR;
//                                    aidlServer.saveTrend(KParamType.NIBP_DIA, value);
//                                    btnMeasure.setText(getRecString(R
//                                            .string.nibp_btn_start));
//                                    showMeasureResult(0);
//                                    measureState = 0;
//                                    aidlServer.saveToDb2();
//                                }
//                            }
//                            break;
//                        case KParamType.NIBP_MAP:
//                            if (value > 0) {
//                                aidlServer.saveTrend(KParamType.NIBP_MAP, value);
//                                tvMapView.setText(String.valueOf(value /
//                                        GlobalConstant.TREND_FACTOR));
//                                GlobalConstant.NIBP_MAP_VALUE = value /
//                                        GlobalConstant.TREND_FACTOR;
//                                aidlServer.saveToDb2();
//                            }
//                            break;
//                        case KParamType.NIBP_PR:
//                            if (value > 0) {
//                                aidlServer.saveTrend(KParamType.NIBP_PR, value);
//                                tvPrView.setText(String.valueOf(value /
//                                        GlobalConstant.TREND_FACTOR));
//                                AlarmUtil.executeOverrunAlarm(Float.valueOf(tvPrView
//                                        .getText().toString()), Nibp.PR_HIGH, Nibp.PR_LOW, ivPr);
//                                GlobalConstant.NIBP_PR_VALUE = value /
//                                        GlobalConstant.TREND_FACTOR;
//                                aidlServer.saveToDb2();
//                                redrawChart();
//                            }
//                            break;
//                    }
//                }
//
//                @Override
//                public void sendConfig(int param, int value) {
//                   /* Log.d("nibp_config", "param = " + param + ",value = " +
//                    value);*/
//                    switch (param) {
//                        case 0x07:
//                            if (measureState == 1) {
//                                if (value == 1) {
//                                    measureState = 2;
//                                }
//                            }
//                            break;
//                        case 0x02:
//                            if (value > 0) {
//                                measureState = 0;
//                                btnMeasure.setText(getRecString(R.string
//                                        .nibp_btn_start));
//                                /*Log.d("Test","0x02 = "+value);*/
//                                showMeasureResult(value);
//                            }
//                            break;
//                        case 0x04:
//                            if (measureState == 2) {
//                                tvCuffView.setText(getRecString(R.string
//                                        .nibp_cuff) + ":" + (value ==
//                                        GlobalConstant.INVALID_DATA ?
//                                        cuffStatic : String.valueOf(value)));
//                            }
//                            break;
//                    }
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
                    case KParamType.NIBP_SYS:
                        if (value > 0) {
                            ServiceUtils.saveTrend(KParamType.NIBP_SYS, value);
                            tvSysView.setText(String.valueOf(value /
                                    GlobalConstant.TREND_FACTOR));
                            AlarmUtil.executeOverrunAlarm(Float.valueOf(tvSysView.getText()
                                    .toString()), Nibp.SYS_HIGH, Nibp.SYS_LOW, ivSys);
                            GlobalConstant.NIBP_SYS_VALUE = value /
                                    GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.NIBP_DIA:
                        if (measureState == 2) {
                            if (value > 0) {
                                tvDiaView.setText(String.valueOf(value /
                                        GlobalConstant.TREND_FACTOR));
                                AlarmUtil.executeOverrunAlarm(Float.valueOf(tvDiaView.getText()
                                        .toString()), Nibp.DIA_HIGH, Nibp.DIA_LOW, ivDia);
                                GlobalConstant.NIBP_DIA_VALUE = value /
                                        GlobalConstant.TREND_FACTOR;
                                ServiceUtils.saveTrend(KParamType.NIBP_DIA, value);
                                btnMeasure.setText(getRecString(R
                                        .string.nibp_btn_start));
                                showMeasureResult(0);
                                measureState = 0;
                                ServiceUtils.saveToDb2();
                            }
                        }
                        break;
                    case KParamType.NIBP_MAP:
                        if (value > 0) {
                            ServiceUtils.saveTrend(KParamType.NIBP_MAP, value);
                            tvMapView.setText(String.valueOf(value /
                                    GlobalConstant.TREND_FACTOR));
                            GlobalConstant.NIBP_MAP_VALUE = value /
                                    GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.NIBP_PR:
                        if (value > 0) {
                            ServiceUtils.saveTrend(KParamType.NIBP_PR, value);
                            tvPrView.setText(String.valueOf(value /
                                    GlobalConstant.TREND_FACTOR));
                            AlarmUtil.executeOverrunAlarm(Float.valueOf(tvPrView
                                    .getText().toString()), Nibp.PR_HIGH, Nibp.PR_LOW, ivPr);
                            GlobalConstant.NIBP_PR_VALUE = value /
                                    GlobalConstant.TREND_FACTOR;
                            ServiceUtils.saveToDb2();
                            redrawChart();
                        }
                        break;
                }
            }

            @Override
            public void sendConfig(int param, int value) {
                switch (param) {
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
                            btnMeasure.setText(getRecString(R.string
                                    .nibp_btn_start));
                                /*Log.d("Test","0x02 = "+value);*/
                            showMeasureResult(value);
                        }
                        break;
                    case 0x04:
                        if (measureState == 2) {
                            tvCuffView.setText(getRecString(R.string
                                    .nibp_cuff) + ":" + (value ==
                                    GlobalConstant.INVALID_DATA ?
                                    cuffStatic : String.valueOf(value)));
                        }
                        break;
                }
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
     * 显示测量结果
     * @param code 结果值
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
        tvCuffView.setText(result);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EchoServerEncoder.setNibpConfig((short) 0x06, 0);
//        if (aidlServer != null) {
//            aidlServer.setSendMSGToFragment(null);
//        }
        isAttach = false;
//        getActivity().unbindService(serviceConnection);
        ServiceUtils.setOnMessageSendListener(null);
    }
}
