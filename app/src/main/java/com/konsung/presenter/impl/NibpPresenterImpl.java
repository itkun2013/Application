package com.konsung.presenter.impl;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.fragment.MeasureNibpFragment;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.NibpPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 血氧逻辑实现
 * @author yuchunhui
 **/

public class NibpPresenterImpl extends BasePresenter<NibpPresenter.View>
        implements NibpPresenter.Presenter {

    public static final String TAG_CUFF_VALUE_TEXT_VIEW = "cuff_value_text_view";
    public static final String TAG_CUFF_NAME_TEXT_VIEW = "cuff_name_text_view";
    //不在测量状态
    public static final int STATUS_NOT_IN_CHECKED = 0;
    //正在测量中
    public static final int STATUS_CHECKING = 1;
    //漏气检测通过
    public static final int STATUS_CHECKING_PASS = 14;
    int measureState = STATUS_NOT_IN_CHECKED;
    private int sysValue = GlobalConstant.INVALID_DATA;
    private int diaValue = GlobalConstant.INVALID_DATA;
    NibpPresenter.View view;
    //过滤config中下发异常时，还会下发value = 0
    private boolean showErr = false;
    private final int textSize = 30;
    /**
     * 构造函数
     * @param view 布局操作
     */
    public NibpPresenterImpl(NibpPresenter.View view) {
        this.view = view;
    }

    @Override
    public void startMeasure() {
        measureState = STATUS_CHECKING;
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE
                , ProtocolDefine.NIBP_MEASURE);
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE
                , ProtocolDefine.NIBP_MEASURE);
        view.refresh(MeasureNibpFragment.MEASURE_LAYOUT);
    }

    @Override
    public void stopMeasure() {
        measureState = STATUS_NOT_IN_CHECKED;
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
    }

    @Override
    public void bindAidlService() {
       initServiceListener();
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.NIBP_SYS); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.NIBP_DIA);
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(300); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.NIBP_SYS);
        picBean.setUnit(UiUitls.getString(R.string.unit_mmhg));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(10); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(300); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(10); //y轴10个刻度
        picBean2.setParameter(KParamType.NIBP_DIA);
        picBean2.setUnit(UiUitls.getString(R.string.unit_mmhg));
        StatisticalPicBean[] beans = {picBean, picBean2};
        return beans;
    }

    @Override
    public void startCalibrate() {
        // 发送校准命令（压力计模式）
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE, 2);
    }

    @Override
    public void stopCalibrate() {
        // 发送停止测量命令
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
    }

    @Override
    public void startLeakTest() {
        // 发送漏气检测命令
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE, 1);
    }

    @Override
    public void stopLeakTest() {
        // 发送停止测量命令
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
    }

    @Override
    public void resetNibp() {
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
        // 发送血压复位命令
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_START_MEASURE, 3);

//        view.refresh(MeasureNibpFragment.GUIDE_LAYOUT);
    }

    @Override
    public void unbindService(Context context) {}

    /**
     * 获取设备安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @param onClickListener 观看视频按钮时间
     * @return 显示布局
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_nibp_tutorial, root, false);
        TextView tvIntroduction = (TextView) view.findViewById(R.id.tv_introduction);
        CharSequence text = tvIntroduction.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        String rexgString = context.getString(R.string.flag_down_pic);
        Pattern pattern = Pattern.compile(rexgString);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            builder.setSpan(
                    new ImageSpan(context, R.drawable.pic_bp_tag), matcher.start(), matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvIntroduction.setText(builder);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }

    /**
     * 获取测量界面
     * @param context 上下文
     * @param root 父布局
     * @return 测量界面的View
     */
    public View getMeasureLayout(Context context, ViewGroup root) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        //袖带压
        TextView cuffName = new TextView(context);
        cuffName.setText(R.string.nibp_cuff);
        cuffName.setTag(TAG_CUFF_NAME_TEXT_VIEW);
        cuffName.setTextSize(textSize);
        linearLayout.addView(cuffName);

        //袖带压值
        TextView cuffValue = new TextView(context);
        cuffValue.setText("0");
        cuffValue.setTextSize(textSize);
        cuffValue.setTag(TAG_CUFF_VALUE_TEXT_VIEW);
        linearLayout.addView(cuffValue);

        return linearLayout;
    }

    /**
     * 获取测量成功界面
     * @param context 上下文
     * @param root 父布局
     * @return 测量界面的View
     */
    public View getSuccessLayout(Context context, ViewGroup root) {
        return LayoutInflater.from(context).inflate(R.layout.layout_spo2_mesure_success, root
                , false);
    }

    /**
     * 初始化服务的监听
     */
    private void initServiceListener() {
        view.setDateBean(ServiceUtils.getMeausreDataBean());
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
                            sysValue = value;
                            GlobalConstant.NIBP_SYS_VALUE = value / GlobalConstant.TREND_FACTOR;
                        }
                        break;
                    case KParamType.NIBP_DIA:
                        if (value > 0) {
                            diaValue = value;
                            ServiceUtils.saveTrend(KParamType.NIBP_DIA, value);
                            GlobalConstant.NIBP_DIA_VALUE = value / GlobalConstant.TREND_FACTOR;
                        }
                        break;
                    case KParamType.NIBP_PR:
                        if (value > 0) {
                            view.measureSuccess(sysValue, diaValue);
                            ServiceUtils.saveToDb2();
                            view.refresh(MeasureNibpFragment.OVER_LAYOUT);
                            measureState = STATUS_NOT_IN_CHECKED;
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void sendConfig(int param, int value) {
                switch (param) {
                    case ProtocolDefine.NET_NIBP_STATUS:
                        if (measureState == STATUS_CHECKING) {
                            if (value == 1) {
                                measureState = STATUS_CHECKING_PASS;
                            }
                        }
                        break;
                    case ProtocolDefine.NET_NIBP_RESULT:
                        if (value > 0) {
                            showErr = true;
                            measureState = STATUS_NOT_IN_CHECKED;
                            view.setNibpStatus(value);
                        }
                        // 已经停止测量,通知UI，如果是漏气检测过程，则是漏气检测通过了
                        if (value == 0) {
                            //当上一次下发的是异常数据时，就不下发测量通过
                            if (!showErr) {
                                view.setNibpStatus(STATUS_CHECKING_PASS);
                            } else {
                                showErr = false;
                            }
                        }
                        break;
                    case ProtocolDefine.NET_NIBP_CUFF:
                        if (value != GlobalConstant.INVALID_DATA) {
                            view.refreshCuff(value);
                        }
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

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }
}
