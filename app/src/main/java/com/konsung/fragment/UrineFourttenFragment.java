package com.konsung.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.Urine;
import com.konsung.util.global.UrineType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 尿常规测量界面
 */
public class UrineFourttenFragment extends BaseFragment {

    // 参数
    @InjectView(R.id.urinert_leu_tv)
    TextView tvUrineRtLeu;
    @InjectView(R.id.urinert_ubg_tv)
    TextView tvUrineRtUbg;
    @InjectView(R.id.urinert_alb_tv)
    TextView tvUrineRtAlb;
    @InjectView(R.id.urinert_pro_tv)
    TextView tvUrineRtPro;
    @InjectView(R.id.urinert_bil_tv)
    TextView tvUrineRtBil;
    @InjectView(R.id.urinert_glu_tv)
    TextView tvUrineRtGlu;
    @InjectView(R.id.urinert_asc_tv)
    TextView tvUrineRtAsc;
    @InjectView(R.id.urinert_sg_tv)
    TextView tvUrineRtSg;
    @InjectView(R.id.urinert_ket_tv)
    TextView tvUrineRtKet;
    @InjectView(R.id.urinert_nit_tv)
    TextView tvUrineRtNit;
    @InjectView(R.id.urinert_cre_tv)
    TextView tvUrineRtCre;
    @InjectView(R.id.urinert_ph_tv)
    TextView tvUrineRtPh;
    @InjectView(R.id.urinert_bld_tv)
    TextView tvUrineRtBld;
    @InjectView(R.id.urinert_ca_tv)
    TextView tvUrineRtCa;
    @InjectView(R.id.leu_icon)
    ImageView ivLeuIcon;
    @InjectView(R.id.ubg_icon)
    ImageView ivUbgIcon;
    @InjectView(R.id.alb_icon)
    ImageView ivAlbIcon;
    @InjectView(R.id.pro_icon)
    ImageView ivProIcon;
    @InjectView(R.id.bil_icon)
    ImageView ivBilIcon;
    @InjectView(R.id.glu_icon)
    ImageView ivGluIcon;
    @InjectView(R.id.asc_icon)
    ImageView ivAscIcon;
    @InjectView(R.id.sg_icon)
    ImageView ivSgIcon;
    @InjectView(R.id.ket_icon)
    ImageView ivKetIcon;
    @InjectView(R.id.nit_icon)
    ImageView ivNitIcon;
    @InjectView(R.id.cre_icon)
    ImageView ivCreIcon;
    @InjectView(R.id.ph_icon)
    ImageView ivPhIcon;
    @InjectView(R.id.bld_icon)
    ImageView ivBldIcon;
    @InjectView(R.id.ca_icon)
    ImageView ivCaIcon;
    @InjectView(R.id.urine_ca_layout)
    LinearLayout caLayout;
    @InjectView(R.id.ma_layout)
    LinearLayout maLayout;
    @InjectView(R.id.cr_layout)
    LinearLayout crLayout;
    private HashMap<Integer, TextView> views;
    private View view;
    private Context context;
    //记录尿常规条数
    private int measureNum;
    //记录字段
    private int count = 0;
    private String str;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_urine_fourteen, null);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        context = getActivity();
        views = new HashMap<>();
        views.put(R.id.urinert_leu_tv, tvUrineRtLeu);
        views.put(R.id.urinert_ubg_tv, tvUrineRtUbg);
        views.put(R.id.urinert_alb_tv, tvUrineRtAlb);
        views.put(R.id.urinert_pro_tv, tvUrineRtPro);
        views.put(R.id.urinert_bil_tv, tvUrineRtBil);
        views.put(R.id.urinert_glu_tv, tvUrineRtGlu);
        views.put(R.id.urinert_asc_tv, tvUrineRtAsc);
        views.put(R.id.urinert_sg_tv, tvUrineRtSg);
        views.put(R.id.urinert_ket_tv, tvUrineRtKet);
        views.put(R.id.urinert_nit_tv, tvUrineRtNit);
        views.put(R.id.urinert_cre_tv, tvUrineRtCre);
        views.put(R.id.urinert_ph_tv, tvUrineRtPh);
        views.put(R.id.urinert_bld_tv, tvUrineRtBld);
        views.put(R.id.urinert_ca_tv, tvUrineRtCa);
        initView();
        initData();
        initMeasureListener();
        return view;
    }

    /**
     * 初始化
     */
    private void initView() {
        int urineType = SpUtils.getSpInt(getActivity(), GlobalConstant.APP_CONFIG
                , GlobalConstant.URINETYPE, UrineType.ELEVEN);
        //11项
        if (urineType == UrineType.ELEVEN) {
            caLayout.setVisibility(View.GONE);
            maLayout.setVisibility(View.GONE);
            crLayout.setVisibility(View.GONE);
            measureNum = GlobalNumber.ELEVEN_NUMBER;
        } else {
            //14项
            caLayout.setVisibility(View.VISIBLE);
            maLayout.setVisibility(View.VISIBLE);
            crLayout.setVisibility(View.VISIBLE);
            measureNum = GlobalNumber.FOURTEEN_NUMBER;
        }
        //如果超出范围 就设置字体颜色为橘黄色
        tvUrineRtPh.addTextChangedListener(new OverProofUtil(Urine.PH_LOW, Urine.PH_HIGH
                , tvUrineRtPh));
        tvUrineRtSg.addTextChangedListener(new OverProofUtil(Urine.SG_LOW, Urine.SG_HIGH
                , tvUrineRtSg));

        Iterator iterator = views.keySet().iterator();
        while (iterator.hasNext()) {
            views.get(iterator.next()).setText(UiUitls.getString(R.string.default_value));
        }
    }

    /**
     * 数据初始化
     */
    private void initData() {
        if (GlobalConstant.URINE_LEU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_LEU_VALUE);
            tvUrineRtLeu.setText(str);
            if (!"-".equals(str)) {
                ivLeuIcon.setVisibility(View.VISIBLE);
                tvUrineRtLeu.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_UBG_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_UBG_VALUE);
            tvUrineRtUbg.setText(str);
            if (!"-".equals(str)) {
                ivUbgIcon.setVisibility(View.VISIBLE);
                tvUrineRtUbg.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_ALB_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_ALB_VALUE);
            tvUrineRtAlb.setText(str);
            if (!"-".equals(str)) {
                ivAlbIcon.setVisibility(View.VISIBLE);
                tvUrineRtAlb.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_PRO_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_PRO_VALUE);
            tvUrineRtPro.setText(str);
            if (!"-".equals(str)) {
                ivProIcon.setVisibility(View.VISIBLE);
                tvUrineRtPro.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_BIL_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BIL_VALUE);
            tvUrineRtBil.setText(str);
            if (!"-".equals(str)) {
                ivBilIcon.setVisibility(View.VISIBLE);
                tvUrineRtBil.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_GLU_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_GLU_VALUE);
            tvUrineRtGlu.setText(str);
            if (!"-".equals(str)) {
                ivGluIcon.setVisibility(View.VISIBLE);
                tvUrineRtGlu.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_ASC_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_ASC_VALUE);
            tvUrineRtAsc.setText(str);
            if (!"-".equals(str)) {
                ivAscIcon.setVisibility(View.VISIBLE);
                tvUrineRtAsc.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_SG_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrineRtSg.setText(String.format("%.3f", GlobalConstant.URINE_SG_VALUE));
            if ((float) GlobalConstant.URINE_SG_VALUE > Urine.SG_HIGH) {
                ivSgIcon.setImageResource(R.drawable.ic_top);
                ivSgIcon.setVisibility(View.VISIBLE);
                tvUrineRtSg.setTextColor(context.getResources().getColor(R.color.high_color));
            } else if ((float) GlobalConstant.URINE_SG_VALUE < Urine.SG_LOW) {
                ivSgIcon.setImageResource(R.drawable.ic_low);
                ivSgIcon.setVisibility(View.VISIBLE);
                tvUrineRtSg.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_KET_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_KET_VALUE);
            tvUrineRtKet.setText(str);
            if (!"-".equals(str)) {
                ivKetIcon.setVisibility(View.VISIBLE);
                tvUrineRtKet.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_NIT_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_NIT_VALUE);
            tvUrineRtNit.setText(str);
            if (!"-".equals(str)) {
                ivNitIcon.setVisibility(View.VISIBLE);
                tvUrineRtNit.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_CRE_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_CRE_VALUE);
            tvUrineRtCre.setText(str);
            if (!"-".equals(str)) {
                ivCreIcon.setVisibility(View.VISIBLE);
                tvUrineRtCre.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_PH_VALUE != GlobalConstant.INVALID_DATA) {
            tvUrineRtPh.setText(String.valueOf(GlobalConstant.URINE_PH_VALUE));
            if (GlobalConstant.URINE_PH_VALUE > Urine.PH_HIGH) {
                ivPhIcon.setImageResource(R.drawable.ic_top);
                ivPhIcon.setVisibility(View.VISIBLE);
                tvUrineRtPh.setTextColor(context.getResources().getColor(R.color.high_color));
            } else if (GlobalConstant.URINE_PH_VALUE < Urine.PH_LOW) {
                ivPhIcon.setImageResource(R.drawable.ic_low);
                ivPhIcon.setVisibility(View.VISIBLE);
                tvUrineRtPh.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_BLD_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString(GlobalConstant.URINE_BLD_VALUE);
            tvUrineRtBld.setText(str);
            if (!"-".equals(str)) {
                ivBldIcon.setVisibility(View.VISIBLE);
                tvUrineRtBld.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
        if (GlobalConstant.URINE_CA_VALUE != GlobalConstant.INVALID_DATA) {
            str = valueToString((int) GlobalConstant.URINE_CA_VALUE);
            tvUrineRtCa.setText(str);
            if (!"-".equals(str)) {
                ivCaIcon.setVisibility(View.VISIBLE);
                tvUrineRtCa.setTextColor(context.getResources().getColor(R.color.high_color));
            }
        }
    }

    /**
     * 初始化接口
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
                int i = value / GlobalConstant.URITREND_FACTOR;
                if (value == GlobalConstant.INVALID_DATA) {
                    return;
                }
                String str = valueToString(i);
                switch (param) {
                    case KParamType.URINERT_LEU:
                        tvUrineRtLeu.setText(str);
                        GlobalConstant.URINE_LEU_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivLeuIcon.setVisibility(View.VISIBLE);
                            tvUrineRtLeu.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivLeuIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtLeu.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_UBG:
                        tvUrineRtUbg.setText(str);
                        GlobalConstant.URINE_UBG_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivUbgIcon.setVisibility(View.VISIBLE);
                            tvUrineRtUbg.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivUbgIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtUbg.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_ALB:
                        tvUrineRtAlb.setText(str);
                        GlobalConstant.URINE_ALB_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivAlbIcon.setVisibility(View.VISIBLE);
                            tvUrineRtAlb.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivAlbIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtAlb.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_PRO:
                        tvUrineRtPro.setText(str);
                        GlobalConstant.URINE_PRO_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivProIcon.setVisibility(View.VISIBLE);
                            tvUrineRtPro.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivProIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtPro.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_BIL:
                        tvUrineRtBil.setText(str);
                        GlobalConstant.URINE_BIL_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivBilIcon.setVisibility(View.VISIBLE);
                            tvUrineRtBil.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivBilIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtBil.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_GLU:
                        tvUrineRtGlu.setText(str);
                        GlobalConstant.URINE_GLU_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivGluIcon.setVisibility(View.VISIBLE);
                            tvUrineRtGlu.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivGluIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtGlu.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    //兼容恩普尿机的VC
                    case KParamType.URINERT_VC:
                        tvUrineRtAsc.setText(str);
                        GlobalConstant.URINE_ASC_VALUE = i;
                        ServiceUtils.saveTrend(KParamType.URINERT_ASC, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivAscIcon.setVisibility(View.VISIBLE);
                            tvUrineRtAsc.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivAscIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtAsc.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_ASC:
                        tvUrineRtAsc.setText(str);
                        GlobalConstant.URINE_ASC_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivAscIcon.setVisibility(View.VISIBLE);
                            tvUrineRtAsc.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivAscIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtAsc.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_SG:
                        double sg = (double) value / GlobalConstant.THOSOUND_VALUE;
                        tvUrineRtSg.setText(String.format("%.3f", sg));
                        GlobalConstant.URINE_SG_VALUE = sg;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if ((float) sg > Urine.SG_HIGH) {
                            ivSgIcon.setImageResource(R.drawable.ic_top);
                            ivSgIcon.setVisibility(View.VISIBLE);
                        } else if ((float) sg < Urine.SG_LOW) {
                            ivSgIcon.setImageResource(R.drawable.ic_low);
                            ivSgIcon.setVisibility(View.VISIBLE);
                        } else {
                            ivSgIcon.setVisibility(View.INVISIBLE);
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_KET:
                        tvUrineRtKet.setText(str);
                        GlobalConstant.URINE_KET_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivKetIcon.setVisibility(View.VISIBLE);
                            tvUrineRtKet.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivKetIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtKet.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_NIT:
                        tvUrineRtNit.setText(str);
                        GlobalConstant.URINE_NIT_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivNitIcon.setVisibility(View.VISIBLE);
                            tvUrineRtNit.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivNitIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtNit.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_CRE:
                        tvUrineRtCre.setText(str);
                        GlobalConstant.URINE_CRE_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivCreIcon.setVisibility(View.VISIBLE);
                            tvUrineRtCre.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivCreIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtCre.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_PH:
                        float ph = value / GlobalConstant.SWITCH_VALUE;
                        tvUrineRtPh.setText(String.valueOf(value / GlobalConstant.SWITCH_VALUE));
                        GlobalConstant.URINE_PH_VALUE = ph;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (ph > Urine.PH_HIGH) {
                            ivPhIcon.setImageResource(R.drawable.ic_top);
                            ivPhIcon.setVisibility(View.VISIBLE);
                        } else if (ph < Urine.PH_LOW) {
                            ivPhIcon.setImageResource(R.drawable.ic_low);
                            ivPhIcon.setVisibility(View.VISIBLE);
                        } else {
                            ivPhIcon.setVisibility(View.INVISIBLE);
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_BLD:
                        tvUrineRtBld.setText(str);
                        GlobalConstant.URINE_BLD_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivBldIcon.setVisibility(View.VISIBLE);
                            tvUrineRtBld.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivBldIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtBld.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
                        break;
                    case KParamType.URINERT_CA:
                        tvUrineRtCa.setText(str);
                        GlobalConstant.URINE_CA_VALUE = i;
                        ServiceUtils.saveTrend(param, value);
                        ServiceUtils.saveToDb2();
                        if (!"-".equals(str)) {
                            ivCaIcon.setVisibility(View.VISIBLE);
                            tvUrineRtCa.setTextColor(context.getResources()
                                    .getColor(R.color.high_color));
                        } else {
                            ivCaIcon.setVisibility(View.INVISIBLE);
                            tvUrineRtCa.setTextColor(UiUitls.getContent()
                                    .getResources().getColor(R.color.mesu_text));
                        }
                        recodeMeasureFinish();
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
     * 尿常规值转换
     *
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    private String valueToString(int value) {
        switch (value) {
            case -1:
                return "-";
            case 0:
                return "+-";
            case 1:
                return "+1";
            case 2:
                return "+2";
            case 3:
                return "+3";
            case 4:
                return "+4";
            case 5:
                return "+";
            case 6:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 记录测量完状态
     */
    private void recodeMeasureFinish() {
        count ++;
        if (count >= measureNum) {
            //数据测量结束，并且已全部显示
            //刷新左侧列表
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.URINT_FLAG));
            count = 0;
        }
    }

    /**
     * 接受顶部用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.URINT_FLAG));
            //切换即刷新数据
            initData();
        }
    }
}
