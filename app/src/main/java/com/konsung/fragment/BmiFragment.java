package com.konsung.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.cengalabs.flatui.views.FlatEditText;
import com.konsung.R;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.service.AIDLServer;
import com.konsung.util.AlarmUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.OverProofUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.BmiParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author xiangshicheng
 */
public class BmiFragment extends BaseFragment {
    @InjectView(R.id.patient_wastline_et)
    FlatEditText etPatientWastline;
    @InjectView(R.id.patient_weight_et)
    FlatEditText etPatientWeight;
    @InjectView(R.id.patient_height_et)
    FlatEditText etPatientHeight;
    @InjectView(R.id.temp_bmi_tv)
    TextView bmiTv;
    @InjectView(R.id.iv_bmi_icon)
    ImageView bmiIv;

    private String result;
    private Intent mIntent;
    // 是否已经绑定服务
    private boolean mIsBind;
    public AIDLServer aidlServer;
    //bmi值
    private String bmiValue = "";
    //计算参数
    public final double countParam = 0.01f;
    //BMi有效值的最大范围值
    private final int maxNum = 50;
    //起始索引
    private final int startPosition = 0;
    //最大长度
    private final int maxLength = 3;
    //标识切换刷新
    private boolean isSwitch = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bmi, null);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        bmiTv.addTextChangedListener(new OverProofUtil(BmiParam.MIN_VALUE, BmiParam.MAX_VALUE
                , bmiTv));
        etPatientHeight.setText(GlobalConstant.HEIGHT);
        etPatientWeight.setText(GlobalConstant.WEIGHT);
        if (!TextUtils.isEmpty(GlobalConstant.BMI)) {
            bmiTv.setText(GlobalConstant.BMI);
            AlarmUtil.executeOverrunAlarm(Float.valueOf(GlobalConstant.BMI)
                    , BmiParam.MAX_VALUE, BmiParam.MIN_VALUE, bmiIv);
        } else {
            bmiTv.setText(UiUitls.getString(R.string.default_value));
        }
        GlobalConstant.currentView = etPatientHeight;
    }

    /**
     *初始化输入监听
     */
    private void initListener() {
        etPatientWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSwitch) {
                    if (s.toString().contains(".")) {
                        int i = s.toString().indexOf(".");
                        String substring = s.toString().substring(i, s.length());
                        if (substring.length() > maxLength) {
                            int i1 = substring.length() - maxLength;
                            etPatientWeight.setText(s.toString().substring(startPosition
                                    , s.length() - i1));
                            etPatientWeight.setSelection(s.length() - i1);
                        }
                    } else {
                        if (s.length() > maxLength) {
                            etPatientWeight.setText(s.toString().substring(startPosition
                                    , maxLength));
                            etPatientWeight.setSelection(maxLength);
                        }
                    }
                    String height = etPatientHeight.getText().toString().trim();
                    String weight = etPatientWeight.getText().toString().trim();
                    if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)) {
                        if (!height.startsWith(".") && !weight.startsWith(".")
                                && !height.endsWith(".") && !weight.endsWith(".")) {
                            //计算BMI
                            //BMI不需要区分男女
                            bmiValue = countBmi(height, weight);
                            if (Double.valueOf(bmiValue) <= maxNum) {
                                bmiTv.setText(bmiValue);
                                AlarmUtil.executeOverrunAlarm(Float.valueOf(bmiValue)
                                        , BmiParam.MAX_VALUE, BmiParam.MIN_VALUE, bmiIv);
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = height;
                                GlobalConstant.WEIGHT = weight;
                                ServiceUtils.saveBmiValue(bmiValue, height, weight);
                                ServiceUtils.saveToDb2();
                                //刷新左列表数据事件发送
                                EventBus.getDefault()
                                        .post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                            } else {
                                bmiTv.setText(UiUitls.getString(R.string.bmi_invalidate));
                                bmiIv.setVisibility(View.GONE);
                                bmiValue = "";
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = "";
                                GlobalConstant.WEIGHT = "";
                                ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                        , GlobalConstant.WEIGHT);
                                ServiceUtils.saveToDb2();
                                //刷新左列表数据事件发送
                                EventBus.getDefault()
                                        .post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                            }
                        }
                    } else {
                        bmiValue = "";
                        GlobalConstant.BMI = bmiValue;
                        GlobalConstant.HEIGHT = "";
                        GlobalConstant.WEIGHT = "";
                        ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                , GlobalConstant.WEIGHT);
                        ServiceUtils.saveToDb2();
                        bmiTv.setText(getRecString(R.string.default_value));
                        bmiIv.setVisibility(View.GONE);
                        //刷新左列表数据事件发送
                        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                    }
                }
            }
        });

        etPatientHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSwitch) {
                    if (s.toString().contains(".")) {
                        int i = s.toString().indexOf(".");
                        String substring = s.toString()
                                .substring(i, s.length());
                        if (substring.length() > maxLength) {
                            int i1 = substring.length() - maxLength;
                            etPatientHeight.setText(s.toString()
                                    .substring(startPosition, s.length() - i1));
                            etPatientHeight.setSelection(s.length() - i1);
                        }
                    } else {
                        if (s.length() > maxLength) {
                            etPatientHeight.setText(s.toString().substring(startPosition
                                    , maxLength));
                            etPatientHeight.setSelection(maxLength);
                        }
                    }
                    String weight = etPatientWeight.getText().toString().trim();
                    String height = etPatientHeight.getText().toString().trim();
                    if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)) {
                        if (!height.startsWith(".") && !weight.startsWith(".")
                                && !height.endsWith(".") && !weight.endsWith(".")) {
                            //计算BMI
                            //BMI不需要区分男女
                            bmiValue = countBmi(height, weight);
                            if (Double.valueOf(bmiValue) <= maxNum) {
                                bmiTv.setText(bmiValue);
                                AlarmUtil.executeOverrunAlarm(Float.valueOf(bmiValue)
                                        , BmiParam.MAX_VALUE, BmiParam.MIN_VALUE, bmiIv);
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = height;
                                GlobalConstant.WEIGHT = weight;
                                ServiceUtils.saveBmiValue(bmiValue, height, weight);
                                ServiceUtils.saveToDb2();
                                //刷新左列表数据事件发送
                                EventBus.getDefault()
                                        .post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                            } else {
                                bmiTv.setText(UiUitls.getString(R.string.bmi_invalidate));
                                bmiIv.setVisibility(View.GONE);
                                bmiValue = "";
                                GlobalConstant.BMI = bmiValue;
                                GlobalConstant.HEIGHT = "";
                                GlobalConstant.WEIGHT = "";
                                ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                        , GlobalConstant.WEIGHT);
                                ServiceUtils.saveToDb2();
                                //刷新左列表数据事件发送
                                EventBus.getDefault()
                                        .post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                            }
                        }
                    } else {
                        bmiValue = "";
                        GlobalConstant.BMI = bmiValue;
                        GlobalConstant.HEIGHT = "";
                        GlobalConstant.WEIGHT = "";
                        ServiceUtils.saveBmiValue(bmiValue, GlobalConstant.HEIGHT
                                , GlobalConstant.WEIGHT);
                        ServiceUtils.saveToDb2();
                        bmiTv.setText(getRecString(R.string.default_value));
                        bmiIv.setVisibility(View.GONE);
                        //刷新左列表数据事件发送
                        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BMI_FLAG));
                    }
                }
            }
        });

        etPatientHeight.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etPatientWeight.getText().toString().length();
                    etPatientWeight.requestFocus(length);
                }
                return false;
            }
        });

        etPatientHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSwitch = false;
            }
        });

        etPatientWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSwitch = false;
            }
        });
    }

    /**
     * 根据体重和身高计算BMI
     * @param weight 体重
     * @param height 身高
     * @return BMI值
     */
    private String countBmi(String height, String weight) {
        String result = "";
        double a = Double.parseDouble(height) * countParam;
        double b = Double.parseDouble(weight);
        result = String.format("%.2f", (b / (a * a)));
        return result;
    }

    @Override
    public void onPause() {
        super.onPause();
        UiUitls.hideSoftInput(getActivity(), etPatientHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BMI_FLAG));
            //切换即刷新数据
            isSwitch = true;
            initView();
        }
    }
}
