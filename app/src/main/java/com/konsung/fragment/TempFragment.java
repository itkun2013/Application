package com.konsung.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.BrokenLineTable;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.TimeParamTable;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.service.AIDLServer;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 耳温
 * Created by JustRush on 2015/6/17.
 */

public class TempFragment extends BaseFragment {
    // 参数
    @InjectView(R.id.temp_t1_tv)
    EditText tvTempTrend;
    @InjectView(R.id.temp_notify)
    TextView tvNotify;
    @InjectView(R.id.measure_btn)
    ImageTextButton btnMeasure;
    @InjectView(R.id.save_btn)
    ImageTextButton btnSave;
    @InjectView(R.id.temp_tv)
    TextView tvTemp;

    @InjectView(R.id.temp_progress_bar)
    ProgressBar proBarTemp;

    @InjectView(R.id.content)
    RelativeLayout rlContent;
    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    private boolean attachTemp = false;
    private boolean isChecking = false;

    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;
    // 探头状态
    private boolean isAttach;
    //已经得到数据
    private boolean isGetValue;

    // 以下变量使用于模拟测量,不需要时可删除
    private List<Integer> tempTrendList;
    private boolean isMeasure;  // 模拟测量时
    /*private int mProgress = 0;*/
    private View view;
    private Handler handler = new Handler();

    public AIDLServer aidlServer;

    private final float TEMP_HIGH = 37.2f;
    private final float TEMP_LOW = 36.2f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(UiUitls.getContent(), "app_config",
                "name", "");
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

 /*   */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temp, null);

        ButterKnife.inject(this, view);
        initEvent();
        initView();
        initAidlService();

        // 设置体温类型,此处设置体温类型是为了规避参数板复位的问题
        int value = SpUtils.getSpInt(getActivity().getApplicationContext(),
                "sys_config", "temp_type",
                TempDefine.TEMP_INFRARED);
        EchoServerEncoder.setTempConfig(ProtocolDefine.NET_TEMP_TYPE, value);

        // 为了避免干扰，在心电测量界面已经关闭了体温测量，此处重新打开体温测量
        EchoServerEncoder.setTempConfig(ProtocolDefine.NET_TEMP_START_STOP,
                TempDefine.TEMP_START);
        EchoServerEncoder.setTempConfig(ProtocolDefine.NET_TEMP_START_STOP,
                TempDefine.TEMP_START);

        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aidlServer == null) {
                    return;
                }
                if (attachTemp && !isChecking) {
                    reinit();
                    isChecking = true;
                    _restartMeasure();
                    btnMeasure.setText(getRecString(R.string.nibp_btn_stop));
                    tvNotify.setText(getRecString(R.string.temp_title));
                } else {
                    if (tempTrendList.size() != 0) {
                        measureOver(aidlServer);
                    }
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempStr = tvTempTrend.getText().toString().replaceAll(" ", "");
                try {

                    if (tempStr.length() > 0 &&
                            !tempStr.equals(UiUitls.getString(R.string.default_value))) {

                        tempTrendList.add((int) (Float.valueOf(tempStr) * 100));
                        measureOver(aidlServer);
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            }
        });
        initChart();
        return view;
    }
    //初始化事件
    private void initEvent() {
        tvTempTrend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (!s.equals(UiUitls.getString(R.string.default_value)) && s.length() > 0) {
                    Float aFloat = Float.valueOf(s);
                    if (aFloat < TEMP_LOW || aFloat > TEMP_HIGH) {
                        tvTempTrend.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.red));
                    } else {
                        tvTempTrend.setTextColor(UiUitls.getContent().getResources()
                                .getColor(R.color.mesu_text));
                    }
                } else {
                    tvTempTrend.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.mesu_text));
                }
            }
        });
    }

    /**
     * 绘制折线图
     */
    private void initChart() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;" +
                "HH:mm:ss");
        //从数据库中获取数据
        final String idcard = SpUtils.getSp(UiUitls.getContent(),
                "app_config", "idcard", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询多少条数据
                int data = 11;
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data, idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int ecgHr = measureDataBean.getTrendValue
                                    (KParamType.TEMP_T1);
                            if (ecgHr != GlobalConstant.INVALID_TREND_DATA) {
                                dataList.add(Float.valueOf((ecgHr / 100.0f)));
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
                        bean.setUnit("℃");
                        //设置Y轴刻度数量
                        int Y_SIZE = 10;
                        bean.setY_SIZE(Y_SIZE);
                        //设置绘图数据
                        bean.setValues(data);
                        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
                        bean.setMax_value(43.0f);
                        bean.setMin_value(32.0f);

                        //获得数据点数
                        int length_size = bean.getX_values().length;
                        //默认最大长度为100,布局容量
                        if (bean.getX_values().length > bean.getMAX_SIZE()) {
                            length_size = bean.getMAX_SIZE();
                        } else if (bean.getX_values().length < 10) {
                            length_size = 11;

                        }
                        bean.setSize(length_size);
                        bean.setIsDecimal(true);

                        //设置布局长度和宽度
                        BrokenLineTable chart = new BrokenLineTable(UiUitls
                                .getContent(), bean);
                        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
                        int width = (int) (bean.getSCALE_x() * length_size +
                                bean.getPADDING_LEFT() + bean
                                .getPADDING_RIGHT());
                        int heigth = (int) (bean.getSCALE_Y() * Y_SIZE + bean
                                .getPADDING_LEFT() + bean.getPADDING_RIGHT());

                        RelativeLayout.LayoutParams params = new
                                RelativeLayout.LayoutParams(width, heigth);
                        params.height = heigth;

                        params.width = width;

                        rlContent.addView(chart);

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
                int data = 11;
                final List<MeasureDataBean> measure = UiUitls.getDataMesure(data, idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add("体温(℃)");
                        //时间-----X_NAME
                        ArrayList<String> times = new ArrayList<>();
                        ArrayList<ArrayList<String>> data = new ArrayList<>();
                        //行数---参数个数
                        int row_size = measure.size();
                        int line_size = param.size();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            ArrayList<String> list = new ArrayList<>();
                            times.add(sdf.format(measure.get(i)
                                    .getMeasureTime()));
                            list.add((measure.get(i).getTrendValue(KParamType
                                    .TEMP_T1) == -1000) ? UiUitls.getString(R
                                    .string
                                    .no_test) :
                                    String.valueOf((float) measure.get(i)
                                            .getTrendValue(KParamType
                                                    .TEMP_T1) / 100.0f));
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

    private void reDraw() {
        this.rlContent.removeAllViews();
        this.rlContentTable.removeAllViews();
        this.initChart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (aidlServer != null) {
            aidlServer.setSendMSGToFragment(null);
        }
        getActivity().unbindService(serviceConnection);
    }


    /**
     * 初始化
     */
    private void initView() {
        tvTempTrend.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.TEMP_VALUE != GlobalConstant.INVALID_DATA) {
            tvTempTrend.setText(String.valueOf(GlobalConstant.TEMP_VALUE));
        }
        // 模拟测量的数据
        tempTrendList = new ArrayList<>();
        proBarTemp.setDrawingCacheBackgroundColor(Color.RED);
    }

    /**
     * 初始化绑定AIDLService服务器
     */
    private void initAidlService() {
        // intent的action为康尚aidl服务器
        mIntent = new Intent("com.konsung.aidlServer");
        // 一启动程序就绑定aidl service服务
        // 保证service只运行一次，一直在后台运行
        // 如果去掉startService只是bindService话,当所有的调用者退出时，即可消除service.
        // 但是本程序中,调用者在Lanucher中，为了防止用户不断的点击参数，重复调用service,
        // 就在这也使用上了startService, 保证不会所有参数都会接收到值,并且不会重复调用service。
        getActivity().startService(mIntent);
        mIsBind = getActivity().bindService(mIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServer = ((AIDLServer.MsgBinder) service).getService();
            aidlServer.setSendMSGToFragment(new AIDLServer.SendMSGToFragment() {
                @Override
                public void sendParaStatus(String name, String version) {
                }

                @Override
                public void sendWave(int param, byte[] bytes) {

                }

                @Override
                public void sendTrend(int param, int value) {
                    switch (param) {
                        case KParamType.TEMP_T1:
                            if (value != GlobalConstant.INVALID_DATA) {
                                attachTemp = true;
                            } else {
                                attachTemp = false;
                            }
                            if (isChecking) {
                                if (value != GlobalConstant.INVALID_DATA) {
                                    tvTemp.setText(String.valueOf((float)
                                            value / 100));
                                    tempTrendList.add(value);
                                    proBarTemp.setProgress
                                            (proBarTemp.getProgress()
                                                    + 1);
                                }
                            }

                            if (tempTrendList.size() >= 180) {
                                measureOver(aidlServer);
                            }
                            break;
                        case KParamType.TEMP_T2:
                            if (value != GlobalConstant.INVALID_DATA) {
                                attachTemp = true;
                            } else {
                                attachTemp = false;
                            }
                            if (isChecking) {
                                if (value != GlobalConstant.INVALID_DATA) {
                                    tvTemp.setText(String.valueOf((float)
                                            value / 100));
                                    tempTrendList.add(value);
                                    proBarTemp.setProgress
                                            (proBarTemp.getProgress()
                                                    + 1);
                                }
                            }

                            if (tempTrendList.size() >= 180) {
                                measureOver(aidlServer);
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void sendConfig(int param, int value) {
                    switch (param) {
                        case 0x00:
                            getTempLeffStatus(value);
                            break;
                        default:
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
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * 测量结束
     */
    private void measureOver(AIDLServer server) {
        int temp = GlobalConstant.INVALID_DATA;

        for (int i = 0; i < tempTrendList.size(); i++) {
            if (tempTrendList.get(i) > temp) {
                temp = tempTrendList.get(i);
            }
        }

        if (0 != tempTrendList.size()) {
            if (GlobalConstant.INVALID_DATA == temp) {
                tvTempTrend.setText(UiUitls.getString(R.string.default_value));
            } else {
                GlobalConstant.TEMP_VALUE = (float) temp / GlobalConstant
                        .TREND_FACTOR;

                //存入数据库
                ServiceUtils.saveTrend(KParamType.TEMP_T1, temp);
                ServiceUtils.saveToDb2();
                reDraw();

                tvTempTrend.setText(String.valueOf((float) temp /
                        GlobalConstant.TREND_FACTOR));
            }
        }

        tempTrendList.clear();
        isChecking = false;
        proBarTemp.setProgress(0);
        if (attachTemp) {
            tvNotify.setText(getRecString(R.string.wait_for_check));
        } else {
            tvNotify.setText(getRecString(R.string.temp_check_probe));
        }
        tvTemp.setText("");
        btnMeasure.setText(getRecString(R.string.nibp_btn_start));
    }

    private void reinit() {
//        isChecking = false;
//        proBarTemp.setProgress(0);
//        btnMeasure.setText(getRecString(R.string.nibp_btn_start));
    }

    private void getTempLeffStatus(int value) {
        //探头正常
        if ((0 == (value & 0x01)) || (0 == (value & 0x02))) {
            attachTemp = true;
            if (!isChecking) {
                tvNotify.setText(getRecString(R.string.wait_for_check));
            }
        }
        //探头脱落
        else {
            attachTemp = false;
            isChecking = false;
            reinit();
            tvNotify.setText(getRecString(R.string.temp_check_probe));
            btnMeasure.setText(getRecString(R.string.nibp_btn_start));
            proBarTemp.setProgress(0);
            tvTemp.setText("");
            tempTrendList.clear();
        }
    }

    private void _restartMeasure() {
        tvTempTrend.setText(UiUitls.getString(R.string.default_value));
//        tvNotify.setText("-?-");
/*        handler.removeCallbacks(updateThread);
        handler.post(updateThread);*/
//        _spo2Value = GlobalConstant.INVALID_DATA;
//        _measureCount = 0;
    }
}
