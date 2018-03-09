package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.AlarmUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.WbcParamValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * 白细胞测量类
 */
public class BloodWbcFragment extends BaseFragment {


    // 参数
    @InjectView(R.id.blood_wbc_trend_tv)
    TextView tvBloodWbcTrend;

    @InjectView(R.id.content)
    RelativeLayout rlContent;

    @InjectView(R.id.scrollView)
    ScrollView sclViScrollView;

    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    @InjectView(R.id.iv_wbc_trend)
    ImageView ivWbc;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity(), "app_config", "name", "");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bloodwbc, null);
        ButterKnife.inject(this, view);
        initEvent();
        initView();
        initMeasureListener();
        initChart();
        initTable();
        return view;
    }

    /**
     * 绘制折线图
     */
    private void initChart() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;" + "HH:mm:ss");
        //从数据库中获取数据
        final String idcard = SpUtils.getSp(getActivity(), "app_config", "idcard", "");
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
                            int ecgHr = measureDataBean.getTrendValue(KParamType.BLOOD_WBC);
                            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                                dataList.add(ecgHr / GlobalConstant.WBC_FACTOR);
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
                        bean.setUnit("10^9/L");
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
                        bean.setIsDecimal(true);

                        //设置布局长度和宽度
                        BrokenLineTable chart = new BrokenLineTable(UiUitls.getContent(), bean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * lengthSize +
                                bean.getPADDING_LEFT() + bean.getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * ySize + bean
                                .getPADDING_LEFT() + bean.getPADDING_RIGHT());

                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width, heigth);
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
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data,  idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add("白细胞(10^9/L)");
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
                                    .BLOOD_WBC) == GlobalConstant.INVALID_DATA)
                                    ? UiUitls.getString(R.string.no_test)
                                    : String.valueOf(measure.get(i).getTrendValue(KParamType
                                    .BLOOD_WBC) / GlobalConstant.WBC_FACTOR));
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
     * 初始化事件
     */
    private void initEvent() {
        tvBloodWbcTrend.addTextChangedListener(new OverProofUtil(WbcParamValue.MIN_VALUE
                , WbcParamValue.MAX_VALUE, tvBloodWbcTrend));
    }

    /**
     * 初始化
     */
    private void initView() {
        tvBloodWbcTrend.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.BLOOD_WBC_VALUE != GlobalConstant.INVALID_DATA) {
            tvBloodWbcTrend.setText(String.valueOf(GlobalConstant.BLOOD_WBC_VALUE));
            AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_WBC_VALUE, WbcParamValue.MAX_VALUE
                    , WbcParamValue.MIN_VALUE, ivWbc);
        }
        // 模拟测量的数据
        irtempTrendList = new ArrayList<>();
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
//        mIsBind = getActivity().bindService(mIntent, serviceConnection
//                , Context.BIND_AUTO_CREATE);
//    }

//    public ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            aidlServer = ((AIDLServer.MsgBinder) service).getService();
//            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
//                @Override
//                public void sendParaStatus(String name, String version) {
//                }
//
//                @Override
//                public void sendWave(int param, byte[] bytes) {
//
//                }
//
//                @Override
//                public void sendTrend(int param, int value) {
//                    //除以100为还原数据，除以1000为单位转换
//                    float v1 = (float) value / GlobalConstant.WBC_FACTOR;
//                    switch (param) {
//                        case KParamType.BLOOD_WBC:
//                            if (value != GlobalConstant.INVALID_DATA) {
//                                isGetValue = true;
//                                tvBloodWbcTrend.setText(String.valueOf(v1));
//                                GlobalConstant.BLOOD_WBC_VALUE = v1;
//                                AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_WBC_VALUE,
//                                        WbcParamValue.MAX_VALUE, WbcParamValue.MIN_VALUE, ivWbc);
//                                aidlServer.saveTrend(KParamType.BLOOD_WBC, value);
//                                aidlServer.saveToDb2();
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
                //除以100为还原数据，除以1000为单位转换
                float v1 = (float) value / GlobalConstant.WBC_FACTOR;
                switch (param) {
                    case KParamType.BLOOD_WBC:
                        if (value != GlobalConstant.INVALID_DATA) {
                            isGetValue = true;
                            tvBloodWbcTrend.setText(String.valueOf(v1));
                            GlobalConstant.BLOOD_WBC_VALUE = v1;
                            AlarmUtil.executeOverrunAlarm(GlobalConstant.BLOOD_WBC_VALUE,
                                    WbcParamValue.MAX_VALUE, WbcParamValue.MIN_VALUE, ivWbc);
                            ServiceUtils.saveTrend(KParamType.BLOOD_WBC, value);
                            ServiceUtils.saveToDb2();
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
     * @param server 服务
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
                tvBloodWbcTrend.setText(UiUitls.getString(R.string.no_test));
            } else {
                tvBloodWbcTrend.setText(String.valueOf((float) (irtempTrends /
                        irtempTrendList.size()) / GlobalConstant.TREND_FACTOR));

            }
        }
        irtempTrendList.clear();
    }
}
