package com.konsung.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.BrokenLineTable;
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CholesterolFragment extends BaseFragment {

    // 参数
    @InjectView(R.id.cholesterol_trend_tv)
    TextView tvCholesterolTrend;

    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;

    @InjectView(R.id.content)
    RelativeLayout rlContent;

    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    private View view;

    public AIDLServer aidlServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = SpUtils.getSp(getActivity(), "app_config", "name", "");
        boolean check = name.equals("");

//        if (check) {
//            Toast.makeText(getActivity(), getRecString(R.string
//                    .fragment_pls_select_patient),
//                    Toast.LENGTH_SHORT).show();
//            GetAllPatientFragment allPatientFragment = new
//                    GetAllPatientFragment();
//            switchToFragment(R.id.fragment, allPatientFragment,
//                    "allPatientFragment", true);
//            isAttach = false;
//        }

    }

 /*   */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cholesterol, null);

        ButterKnife.inject(this, view);
        initView();
        initAidlService();

        initChart();
        initTable();
        return view;
    }

    private void reDraw() {
        this.rlContent.removeAllViews();
        this.rlContentTable.removeAllViews();
        initChart();
        initTable();
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
                final List<MeasureDataBean> measure = DBDataUtil.getMeasures(idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //缓存数据
                        ArrayList<String> timeList = new ArrayList<>();
                        ArrayList<Float> dataList = new ArrayList<>();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            MeasureDataBean measureDataBean = measure.get(i);
                            int ecgHr = measureDataBean.getTrendValue
                                    (KParamType.CHOLESTEROL_TREND);

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
                        bean.setUnit(UiUitls.getString(R.string.health_unit_mol));
                        //设置Y轴刻度数量
                        int Y_SIZE = 10;
                        bean.setY_SIZE(Y_SIZE);
                        //设置绘图数据
                        bean.setValues(data);
                        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
                        bean.setMax_value(30.0f);
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
                final List<MeasureDataBean> measure = DBDataUtil.getMeasures(idcard);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //参数-----Y_NAME
                        ArrayList<String> param = new ArrayList<>();
                        param.add("总胆固醇");
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
                                    .CHOLESTEROL_TREND) == -1000) ? UiUitls.getString
                                    (R.string.no_test) :
                                    String.valueOf(measure.get(i)
                                            .getTrendValue(KParamType
                                                    .CHOLESTEROL_TREND)/100.0f));
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (aidlServer != null)
            aidlServer.setSendMSGToFragment(null);
        getActivity().unbindService(serviceConnection);
    }


    /**
     * 初始化
     */
    private void initView() {
        tvCholesterolTrend.setText(UiUitls.getString(R.string.default_value));
        if (GlobalConstant.CHOLESTEROL_VALUE != GlobalConstant.INVALID_DATA) {
            tvCholesterolTrend.setText(String.valueOf(GlobalConstant
                    .CHOLESTEROL_VALUE));
        }
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
                        case KParamType.CHOLESTEROL_TREND:
                            if (value != GlobalConstant.INVALID_DATA) {
                                float temp = (float) value / 100;
                                tvCholesterolTrend.setText(String.valueOf
                                        (temp));
                                GlobalConstant.CHOLESTEROL_VALUE = temp;
                                ServiceUtils.saveTrend(KParamType.CHOLESTEROL_TREND, value);
                                ServiceUtils.saveToDb2();
                                reDraw();
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
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
