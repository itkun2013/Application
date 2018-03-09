package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.Time_paramBean;
import com.konsung.defineview.TimeParamTable;
import com.konsung.service.AIDLServer;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.SugarBloodParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author xiangshicheng
 * 糖化血红蛋白
 */
public class BloodRedFragment extends BaseFragment {

    @InjectView(R.id.tv_hba1c_ngsp)
    TextView tvHba1cNgsp;
    @InjectView(R.id.tv_hba1c_ifcc)
    TextView tvHba1cIfcc;
    @InjectView(R.id.tv_hba1c_eag)
    TextView tvHba1cEag;

    @InjectView(R.id.iv_hba1c_ngsp)
    ImageView ivHba1cNgsp;
    @InjectView(R.id.iv_hba1c_ifcc)
    ImageView ivHba1cIfcc;
    @InjectView(R.id.iv_hba1c_eag)
    ImageView ivHba1cEag;

    @InjectView(R.id.second_unit)
    TextView secondUnitTv;
    @InjectView(R.id.third_unit)
    TextView thirdUnitTv;

    @InjectView(R.id.content_table)
    RelativeLayout rlContentTable;

    private View view;
    private Intent mIntent;
    private boolean mIsBind;
    public AIDLServer aidlServer;
    //大于标识符
    private static final float MAXFLAG = -100f;
    //小于标识符
    private static final float MINFLAG = -10f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blood_red, container, false);
        ButterKnife.inject(this, view);
        initView();
        initTable();
        initMeasureListener();
        return view;
    }

    /**
     * 初始化view显示
     */
    private void initView() {
        //NGSP赋值
        fillValue(tvHba1cNgsp, ivHba1cNgsp, SugarBloodParam.NGSP_MIN, SugarBloodParam.NGSP_MAX
                , KParamType.HBA1C_NGSP, GlobalConstant.HBA1C_NGSP);
        //IFCC赋值
        fillValue(tvHba1cIfcc, ivHba1cIfcc, SugarBloodParam.IFCC_MIN, SugarBloodParam.IFCC_MAX
                , KParamType.HBA1C_IFCC, GlobalConstant.HBA1C_IFCC);
        //EAG赋值
        fillValue(tvHba1cEag, ivHba1cEag, SugarBloodParam.EAG_MIN, SugarBloodParam.EAG_MAX
                , KParamType.HBA1C_EAG, GlobalConstant.HBA1C_EAG);
    }

    /**
     * 赋值并判断其是否异常
     * @param tv TextView控件
     * @param iv ImageView控件
     * @param min 最小参考值
     * @param max 最大参考值
     * @param type 测量项标识
     * @param value 当前测量值大小
     */
    private void fillValue(TextView tv, ImageView iv, float min, float max, int type
            , float value) {
        if (value == GlobalConstant.INVALID_DATA || value == 0) {
            return;
        }
        if (value == MINFLAG) {
            //超低值
            if (type == KParamType.HBA1C_NGSP) {
                tv.setText(GlobalConstant.NGSP_BELOW);
            } else if (type == KParamType.HBA1C_IFCC) {
                tv.setText(GlobalConstant.IFCC_BELOW);
            } else {
                tv.setText(GlobalConstant.EAG_BELOW);
            }
            tv.setTextColor(getActivity().getResources().getColor(R.color.red));
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.alarm_low);
        } else if (value == MAXFLAG) {
            //超高值
            if (type == KParamType.HBA1C_NGSP) {
                tv.setText(GlobalConstant.NGSP_ABOVE);
            } else if (type == KParamType.HBA1C_IFCC) {
                tv.setText(GlobalConstant.IFCC_ABOVE);
            } else {
                tv.setText(GlobalConstant.EAG_ABOVE);
            }
            tv.setTextColor(getActivity().getResources().getColor(R.color.red));
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.alarm_high);
        } else {
            //正常值
            tv.setText(value + "");
            if (value > max) {
                tv.setTextColor(getActivity().getResources().getColor(R.color.red));
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.alarm_high);
            } else if (value < min) {
                tv.setTextColor(getActivity().getResources().getColor(R.color.red));
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.alarm_low);
            } else {
                tv.setTextColor(getActivity().getResources().getColor(R.color.mesu_text));
                iv.setVisibility(View.GONE);
            }
        }
    }
    /**
     * 服务初始化
     */
//    private void initService() {
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
//                    if (value == GlobalConstant.INVALID_DATA) {
//                        return;
//                    }
//                    if (value == MINFLAG) {
//                        temp = MINFLAG;
//                    } else if (value == MAXFLAG) {
//                        temp = MAXFLAG;
//                    } else {
//                        temp = (float) value / GlobalConstant.TREND_FACTOR;
//                    }
//                    switch (param) {
//                        case KParamType.HBA1C_NGSP:
//                            fillValue(tvHba1cNgsp, ivHba1cNgsp, SugarBloodParam.NGSP_MIN
//                                , SugarBloodParam.NGSP_MAX, KParamType.HBA1C_NGSP, temp);
//                            GlobalConstant.HBA1C_NGSP = temp;
//                            aidlServer.saveTrend(KParamType.HBA1C_NGSP, value);
//                            aidlServer.saveToDb2();
//                            break;
//                        case KParamType.HBA1C_IFCC:
//                            fillValue(tvHba1cIfcc, ivHba1cIfcc, SugarBloodParam.IFCC_MIN
//                                    , SugarBloodParam.IFCC_MAX, KParamType.HBA1C_IFCC, temp);
//                            GlobalConstant.HBA1C_IFCC = temp;
//                            aidlServer.saveTrend(KParamType.HBA1C_IFCC, value);
//                            aidlServer.saveToDb2();
//                            break;
//                        case KParamType.HBA1C_EAG:
//                            fillValue(tvHba1cEag, ivHba1cEag, SugarBloodParam.EAG_MIN
//                                    , SugarBloodParam.EAG_MAX, KParamType.HBA1C_EAG, temp);
//                            GlobalConstant.HBA1C_EAG = temp;
//                            aidlServer.saveTrend(KParamType.HBA1C_EAG, value);
//                            aidlServer.saveToDb2();
//                            //获取数据后立即刷新表格
//                            reDraw();
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
                if (value == GlobalConstant.INVALID_DATA) {
                    return;
                }
                if (value == MINFLAG) {
                    temp = MINFLAG;
                } else if (value == MAXFLAG) {
                    temp = MAXFLAG;
                } else {
                    temp = (float) value / GlobalConstant.TREND_FACTOR;
                }
                switch (param) {
                    case KParamType.HBA1C_NGSP:
                        fillValue(tvHba1cNgsp, ivHba1cNgsp, SugarBloodParam.NGSP_MIN
                                , SugarBloodParam.NGSP_MAX, KParamType.HBA1C_NGSP, temp);
                        GlobalConstant.HBA1C_NGSP = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_NGSP, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.HBA1C_IFCC:
                        fillValue(tvHba1cIfcc, ivHba1cIfcc, SugarBloodParam.IFCC_MIN
                                , SugarBloodParam.IFCC_MAX, KParamType.HBA1C_IFCC, temp);
                        GlobalConstant.HBA1C_IFCC = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_IFCC, value);
                        ServiceUtils.saveToDb2();
                        break;
                    case KParamType.HBA1C_EAG:
                        fillValue(tvHba1cEag, ivHba1cEag, SugarBloodParam.EAG_MIN
                                , SugarBloodParam.EAG_MAX, KParamType.HBA1C_EAG, temp);
                        GlobalConstant.HBA1C_EAG = temp;
                        ServiceUtils.saveTrend(KParamType.HBA1C_EAG, value);
                        ServiceUtils.saveToDb2();
                        //获取数据后立即刷新表格
                        reDraw();
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
     * 重绘表格
     */
    private void reDraw() {
        rlContentTable.removeAllViews();
        initTable();
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
                        param.add(UiUitls.getString(R.string.ngsp_table));
                        param.add(UiUitls.getString(R.string.ifcc_table));
//                        param.add("总胆固醇;(mmol/L)");
                        param.add(UiUitls.getString(R.string.eag_table));
                        //时间-----X_NAME
                        ArrayList<String> times = new ArrayList<>();
                        ArrayList<ArrayList<String>> data = new ArrayList<>();
                        //行数---参数个数
                        int rowSize = measure.size();
                        int lineSize = param.size();
                        for (int i = measure.size() - 1; i >= 0; i--) {
                            ArrayList<String> list = new ArrayList<>();
                            times.add(sdf.format(measure.get(i).getMeasureTime()));
                            //ngsp
                            list.add(formatterStr(KParamType.HBA1C_NGSP
                                    , measure.get(i).getTrendValue(KParamType.HBA1C_NGSP)));
                            //ifcc
                            list.add(formatterStr(KParamType.HBA1C_IFCC
                                    , measure.get(i).getTrendValue(KParamType.HBA1C_IFCC)));
                            //eag
                            list.add(formatterStr(KParamType.HBA1C_EAG
                                    , measure.get(i).getTrendValue(KParamType.HBA1C_EAG)));
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

    /**
     * 值转换
     * @param type 类型
     * @param value 值
     * @return 转换后的数值
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
            case KParamType.HBA1C_NGSP:
                if (value == supLow) {
                    return GlobalConstant.NGSP_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.NGSP_ABOVE;
                }
                break;
            case KParamType.HBA1C_IFCC:
                if (value == supLow) {
                    return GlobalConstant.IFCC_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.IFCC_ABOVE;
                }
                break;
            case KParamType.HBA1C_EAG:
                if (value == supLow) {
                    return GlobalConstant.EAG_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.EAG_ABOVE;
                }
                break;
            default:
                break;
        }
        return value / GlobalConstant.FACTOR + "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ServiceUtils.setOnMessageSendListener(null);
    }
}
