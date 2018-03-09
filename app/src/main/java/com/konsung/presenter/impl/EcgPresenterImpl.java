package com.konsung.presenter.impl;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.bean.EcgCacheBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureEcgBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.fragment.AppFragment;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.EcgPresenter;
import com.konsung.util.DiagCodeToText;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;
import com.konsung.util.global.GlobalNumber;
import com.konsung.view.holder.EcgTutorialHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 心电逻辑处理类
 */

public class EcgPresenterImpl extends BasePresenter<EcgPresenter.View> implements EcgPresenter
        .Presenter {
    private static final int ONE_THOUSAND = 1000; //秒转毫秒
    private static final int ECG_OPERATION = 0xFF;
    private static final int ECG_OPERATION1 = 0x0F;
    private static final int DEFAULT_TIME = 20;

    //心电连接正常
    private static final int ECG_CONNECT_NORMAL = 0;
    EcgPresenter.View view;
    private int countDown = DEFAULT_TIME; //默认倒计时时间
    private boolean checking = false;

    private int currenHr = 0; //记录从参数版传入的心率
    private String paraBoardVersion; //记录参数版的版本号
    private MeasureDataBean measureDataBean = null;


    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            countDown--;
            AppFragment.ecgCheckTimes = countDown;
            timeHandler.postDelayed(this, ONE_THOUSAND);
        }
    };

    EcgCacheBean waveCacheBean = new EcgCacheBean();

    /**
     * 构造器
     * @param view 对应的view
     * @param paraBoardVersion 参数版版本号
     */
    public EcgPresenterImpl(EcgPresenter.View view, String paraBoardVersion) {
        this.view = view;
        this.paraBoardVersion = paraBoardVersion;
    }

    @Override
    public void startMeasure() {
        waveCacheBean.initEcgWave();
        checking = true;
        EchoServerEncoder.setEcgConfig(KParamType.START_ECG_DIAGNOSE, 1);
        startCountDown();
    }

    @Override
    public void stopMeasure() {
        checking = false;
        stopCountDown();
    }

    @Override
    public void bindAidlService() {
        //设置数据监听
        initMeasureListener();
    }

    /**
     * 初始化服务的监听
     */
    public void initMeasureListener() {
        measureDataBean = ServiceUtils.getMeausreDataBean();
        view.setMeasureDataBean(measureDataBean);
        ServiceUtils.setOnMessageSendListener(new ServiceUtils.OnMessageSendListener() {
            @Override
            public void sendParaStatus(String name, String version) {

            }

            @Override
            public void sendWave(int param, byte[] bytes) {
                //12导联心电波形参数 1-12
                if (1 <= param && param <= GlobalNumber.TWELVE_NUMBER) {
                    //对数据进行处理后返回给fragment
                    for (int i = 0; i < bytes.length / 2; i++) {
                        int data = (bytes[i * 2] & 0xFF)
                                + ((bytes[i * 2 + 1] & 0x0F) << GlobalNumber.EIGHT_NUMBER);
                        view.addEcgWaveData(param, data);
                    }
                    if (checking) {
                        view.saveEcgWave(param, bytes);
                    }
                    //测量超时
                    if (countDown <= 0) {
                        stopCountDown();
                        view.measureError();
                        UiUitls.toast(UiUitls.getContent(), UiUitls.getString(R.string
                                .ecg_check_timeout));
                    }
                }
            }

            @Override
            public void sendTrend(int param, int value) {
                switch (param) {
                    case KParamType.ECG_HR:
                        currenHr = value / GlobalConstant.TREND_FACTOR;
                        if (currenHr < 0) {
                            currenHr = 0;
                        }
                        // HR已改为从12导诊断结果获取，不再从此趋势值获取
                        //TODO 3-5导联下这里获取心率
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void sendConfig(int param, int value) {
                switch (param) {
                    case KParamType.ECG_CONNECTION_STATUS: //心电导联线连接状态码
                        view.setEcgConnectStatus(value);
                        if (value != ECG_CONNECT_NORMAL) {
                            checking = false;
                        }
                        break;

                    case KParamType.ECG_ABNORMAL: //心率失常状态监听
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
                String diaResult = " "; // 12导诊断结果
                float version = 0;
                try {
                    version = Float.valueOf(paraBoardVersion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //根据AppDevice中的协议，诊断结果最多有26个数据（不包括时间戳）
                if (bytes.length != GlobalNumber.FIFTY_TWO_NUMBER) {
                    Log.v("HealthOne", "12 Lead Dia Result len is not right!");
                }
                int[] result = new int[GlobalNumber.TWENTY_SIX_NUMBER];

                for (int i = 0; i < GlobalNumber.TWENTY_SIX_NUMBER; i++) {
                    result[i] = (short) (((bytes[i * 2 + 1] & 0x00FF)
                            << GlobalNumber.EIGHT_NUMBER) | (0x00FF & bytes[i * 2]));
                }
                int hrValue = result[0]; // HR值
                int prInterval = result[1]; // PR间期 单位ms
                int qrsDuration = result[2]; // QRS间期, 单位ms
                int qt = result[3]; // QT间期 单位ms
                int qtc = result[4]; // QTC间期 单位ms
                int pAxis = result[5]; // P 波轴 单位°
                int qrsAxis = result[6]; // QRS波心电轴 单位°
                int tAxis = result[7]; // T波心电轴 单位°
                int rv5 = result[8]; // RV5, 单位mV
                int sv1 = result[9]; // SV1, 单位mV
                int rv5Sv1 = rv5 + sv1; // RV5+SV1, 单位mV
                String divide = UiUitls.getString(R.string.unit_divide);
                //心率值为0时，代表无效值
                if (hrValue != 0) {
                    measureDataBean.setPr(prInterval);
                    measureDataBean.setQrs(qrsDuration);
                    measureDataBean.setQt(qt);
                    measureDataBean.setQtc(qtc);
                    measureDataBean.setpAxis(pAxis);
                    measureDataBean.setQrsAxis(qrsAxis);
                    measureDataBean.settAxis(tAxis);
                    measureDataBean.setRv5(String.format("%.2f", (float) rv5 / GlobalConstant
                            .FACTOR));
                    measureDataBean.setSv1(String.format("%.2f", (float) sv1 / GlobalConstant
                            .FACTOR));
                    measureDataBean.setRv5PlusSv1(String.format("%.2f", ((float) rv5 /
                            GlobalConstant.FACTOR + (float) sv1 / GlobalConstant.FACTOR)));
                } else {
                    measureDataBean.setPr(GlobalConstant.INVALID_DATA);
                    measureDataBean.setQrs(GlobalConstant.INVALID_DATA);
                    measureDataBean.setQt(GlobalConstant.INVALID_DATA);
                    measureDataBean.setQtc(GlobalConstant.INVALID_DATA);
                    measureDataBean.setpAxis(GlobalConstant.INVALID_DATA);
                    measureDataBean.setQrsAxis(GlobalConstant.INVALID_DATA);
                    measureDataBean.settAxis(GlobalConstant.INVALID_DATA);
                    measureDataBean.setRv5("");
                    measureDataBean.setSv1("");
                    measureDataBean.setRv5PlusSv1("");
                }

                MeasureEcgBean ecgBean = new MeasureEcgBean();
                if (hrValue != 0) {
                    ecgBean.setP(pAxis + "");
                    ecgBean.setPr(prInterval + "");
                    ecgBean.setQrs(qrsDuration + "");
                    ecgBean.setQtQtc(qt + divide + qtc);
                    ecgBean.setpQrsT(pAxis + divide + qrsAxis + divide + tAxis);
                    ecgBean.setRv5Sv1(String.format("%.2f", (float) rv5 / GlobalConstant.FACTOR)
                            + divide + String.format("%.2f", (float) sv1 / GlobalConstant
                            .FACTOR));
                    ecgBean.setRv5PlusSv1(String.format("%.2f", ((float) rv5 / GlobalConstant
                            .FACTOR + (float) sv1 / GlobalConstant.FACTOR)));
                }
                if (prInterval < 0) {
                    prInterval = (short) -prInterval;
                }
                diaResult = (version >= GlobalNumber.ONE_POINT_THREE_FLOAT
                        ? String.valueOf(currenHr) : String.valueOf(hrValue)) + "," + String
                        .valueOf(prInterval) + ","
                        + String.valueOf(qrsDuration) + "," + String.valueOf(qt) + ","
                        + String.valueOf(qtc) + "," + String.valueOf(pAxis) + ","
                        + String.valueOf(qrsAxis) + "," + String.valueOf(tAxis) + ","
                        + String.format("%.2f", (float) rv5 / GlobalNumber.HUNDRED_NUMBER) + ","
                        + String.format("%.2f", (float) sv1 / GlobalNumber.HUNDRED_NUMBER) + ","
                        + String.format("%.2f", ((float) rv5 / GlobalNumber.HUNDRED_NUMBER
                        + (float) sv1 / GlobalNumber.HUNDRED_NUMBER)) + ",";

                //根据AppDevice协议，诊断码有16个，但不是所有都有效
                DiagCodeToText diagCodeToText = new DiagCodeToText();
                for (int i = 0; i < GlobalNumber.SIXTEEN_NUMBER; i++) {
                    for (int j = 0; j < diagCodeToText.ECG_12_LEAD_DIAG_TEXT.length; j++) {
                        String[] str = diagCodeToText.ECG_12_LEAD_DIAG_TEXT[j].split(":");
                        if (result[GlobalNumber.TEN_NUMBER + i] == Integer.parseInt(str[0])) {
                            if (hrValue != 0) {
                                diaResult += str[1];
                                if ((str[1] != null) && (!"".equals(str[1]))) {
                                    diaResult += ";";
                                }
                            }
                        }
                    }
                }
                String[] split = diaResult.split(",");
                String resultStr = split[split.length - 1];
                measureDataBean.setEcgDiagnoseResult(resultStr);
                ecgBean.setResult(resultStr.equals("0.00") ? "" : resultStr);
                // 心电测量完成
                if (checking) {
                    stopCountDown();
                    measureDataBean.setTrendValue(KParamType.ECG_HR
                            , currenHr * GlobalNumber.HUNDRED_NUMBER);
                    //保存心电数据
                    for (int i = 1; i <= GlobalNumber.TWELVE_NUMBER; i++) {
                        measureDataBean.set_ecgWave(i, waveCacheBean.getEcgWave(i));
                    }
                    int sureHr;
                    if (version >= GlobalNumber.ONE_POINT_THREE_FLOAT) {
                        view.measureSuccess(currenHr, ecgBean);
                        sureHr = currenHr;
                    } else {
                        // 心电测量完成
                        view.measureSuccess(hrValue, ecgBean);
                        sureHr = hrValue;
                    }
                    GlobalConstant.ECG_PR_VALUE = sureHr;
                    measureDataBean.setTrendValue(KParamType.ECG_HR
                            , sureHr * GlobalConstant.TREND_FACTOR);
                    ServiceUtils.setMeasureDataBean(measureDataBean);
                    ServiceUtils.saveToDb2();
                    view.setMeasureDataBean(measureDataBean);
                    measureDataBean = null;
                }
            }

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }

    @Override
    public String getEcgConnectStatus(int leadoff) {
        String off = UiUitls.getString(R.string.please_access);
        if (leadoff == 1) {
            off += UiUitls.getString(R.string.avf) + ",";
        }
        if ((leadoff & 0x2) == 0x2) {
            off += UiUitls.getString(R.string.avl) + ",";
        }
        if ((leadoff & 0x4) == 0x4) {
            off += UiUitls.getString(R.string.avr) + ",";
        }
        if ((leadoff & 0x8) == 0x8) {
            off += UiUitls.getString(R.string.V1) + ",";
        }
        if ((leadoff & 0x10) == 0x10) {
            off += UiUitls.getString(R.string.V2) + ",";
        }
        if ((leadoff & 0x20) == 0x20) {
            off += UiUitls.getString(R.string.V3) + ",";
        }
        if ((leadoff & 0x40) == 0x40) {
            off += UiUitls.getString(R.string.V4) + ",";
        }
        if ((leadoff & 0x80) == 0x80) {
            off += UiUitls.getString(R.string.V5) + ",";
        }
        if ((leadoff & 0x100) == 0x100) {
            off += UiUitls.getString(R.string.V6) + ",";
        }
        return off.substring(0, off.length() - 1) +
                UiUitls.getString(R.string.pole_off);
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        //加载当前用户的历史测量记录
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.ECG_HR); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic() {
        String currentIdCard = ProviderReader.readPatient().getIdCard();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(GlobalNumber.THREE_HUNDRED_FIFTY_NUMBER); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean.setParameter(KParamType.ECG_HR);
        picBean.setUnit(UiUitls.getString(R.string.health_unit_bpm));
        StatisticalPicBean[] beans = {picBean};
        return beans;
    }

    /**
     * 获取设备安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @return 显示布局
     */
    public EcgTutorialHolder getInstallGuideView(Context context, ViewGroup root) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_ecg_tutorial, root, false);
        EcgTutorialHolder holder = new EcgTutorialHolder(view);

        return holder;
    }

    @Override
    public void saveWave(int param, byte[] data) {
        waveCacheBean.setEcgWave(param, UnitConvertUtil.bytesToHexString(data));
    }

    /**
     * 倒计时实时改变界面
     */
    private Handler timeHandler = new Handler();

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        timeHandler.post(timeRunnable);
    }

    /**
     * 停止倒计时
     */
    private void stopCountDown() {
        checking = false;
        timeHandler.removeCallbacks(timeRunnable);
        countDown = DEFAULT_TIME;
        AppFragment.isCheckingEcg = false;
    }

    /**
     * 重置测量表数据
     */
    public void resetMeasureDataBeanData() {
        ServiceUtils.resetEcgWaveData();
        measureDataBean = ServiceUtils.getMeausreDataBean();
    }
}
