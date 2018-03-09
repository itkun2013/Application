package com.konsung.defineview;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.konsung.util.GlobalConstant.CHAR_SEMICOLON;

/**
 * Created by dlx on 2017/10/25 0011.
 * 参数配置弹出框
 */

public class ParamPopupWindow extends Dialog {

    public static final int LINE_MAX_SIZE = 26; //一行最大的显示字数
    private final Activity context;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.cb_ecg)
    CheckBox cbEcg;
    @InjectView(R.id.cb_uri14)
    CheckBox cbUri14;
    @InjectView(R.id.cb_sugar_blood)
    CheckBox cbSugarBlood;
    @InjectView(R.id.cb_spo2)
    CheckBox cbSpo2;
    @InjectView(R.id.cb_uri11)
    CheckBox cbUri11;
    @InjectView(R.id.cb_bmi)
    CheckBox cbBmi;
    @InjectView(R.id.cb_blood_pressure)
    CheckBox cbBloodPressure;
    @InjectView(R.id.cb_blogin)
    CheckBox cbBlogin;
    @InjectView(R.id.cb_wbc)
    CheckBox cbWbc;
    @InjectView(R.id.cb_temperature)
    CheckBox cbTemperature;
    @InjectView(R.id.cb_blood_fat)
    CheckBox cbBloodFat;
    @InjectView(R.id.cb_more)
    CheckBox cbMore;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    private OnCallBackListener listener;
    private List<CheckBox> checkBoxes;
    private int paramValue = 0;
    private boolean is = false; //判断是否可以点击

    /**
     * 构造函数
     * @param context 上下文引用
     */
    public ParamPopupWindow(Activity context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        initPopwindow();
        initParamBox(false);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        checkBoxes = new ArrayList<>();
        checkBoxes.add(cbEcg);
        checkBoxes.add(cbSpo2);
        checkBoxes.add(cbBloodPressure);
        checkBoxes.add(cbTemperature);
        checkBoxes.add(cbMore);
        checkBoxes.add(cbUri11);
        checkBoxes.add(cbUri14);
        checkBoxes.add(cbBlogin);
        checkBoxes.add(cbBloodFat);
        checkBoxes.add(cbSugarBlood);
        checkBoxes.add(cbBmi);
        checkBoxes.add(cbWbc);
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_param, null);
        ButterKnife.inject(this, view);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        initData();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParamPopupWindow.this.hide();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParamPopupWindow.this.hide();
                if (listener != null) {
                    listener.onCommit(getCheckedParamTxt());
                    listener.onCommitParam(getCheckedParamTag());
                }
            }
        });

        cbUri11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbUri14.setChecked(false);
                    if (is) {
                        cbUri11.setEnabled(false);
                        cbUri14.setEnabled(true);
                    }
                }
            }
        });

        cbUri14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbUri11.setChecked(false);
                    if (is) {
                        cbUri14.setEnabled(false);
                        cbUri11.setEnabled(true);
                    }
                }
            }
        });
    }

    /**
     * 获取保存的参数配置值
     * @return 获取保存的参数配置值
     */
    public int getCheckedParamValue() {
        int value = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                value = value | (0x01 << i);
            }
        }
        return value;
    }

    /**
     * * 参数配置项初始化 (使用已保存的值回显)
     * @param is 判断是否可以点击
     */
    private void initParamBox(boolean is) {
        this.is = is;
        if (0 == paramValue) {
            //添加参数配置项
            paramValue = SpUtils.getSpInt(context, GlobalConstant.PARAM_CONFIGS
                    , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
        }
        //普通模式下，对于参数板自带的心电，血氧，血压三项不可修改配置
            cbEcg.setEnabled(false);
            cbSpo2.setEnabled(false);
            cbBloodPressure.setEnabled(false);

            cbEcg.setButtonDrawable(R.drawable.check_enable_selector);
            cbSpo2.setButtonDrawable(R.drawable.check_enable_selector);
            cbBloodPressure.setButtonDrawable(R.drawable.check_enable_selector);

        for (int i = 0; i < checkBoxes.size(); i++) {
            if ((paramValue & (0x01 << i)) != 0) {
                checkBoxes.get(i).setChecked(true);
                if (is) {
                    checkBoxes.get(i).setEnabled(false);
                }
            } else {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    /**
     * 参数配置项初始化 (使用传递的值回显)
     * @param paramValue 传递的值
     */
    public void initParamBox(int paramValue) {
        //添加参数配置项
        this.paramValue = paramValue;
        initParamBox(true);
    }

    /**
     * 获取选中的参数配置项的文本
     * @return 选中的参数配置项的文本
     */
    private String getCheckedParamTxt() {

        StringBuilder builder = new StringBuilder();
        boolean haveNextLine = false;
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            if (checkBox.isChecked()) {
                CharSequence text = checkBox.getText();
                builder.append(text).append(CHAR_SEMICOLON);
                if (builder.length() > LINE_MAX_SIZE && !haveNextLine) {
                    builder.append("\n");
                    haveNextLine = true;
                }
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        String txt = builder.toString();
        return txt;
    }

    /**
     * 获取选中的参数配置项的Tag
     * @return 选择控件的tag
     */
    private List<Integer> getCheckedParamTag() {
        List<Integer> tags = new ArrayList<>();

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            if (checkBox.isChecked()) {
                int tag = Integer.valueOf(String.valueOf(checkBox.getTag()));
                tags.add(tag);
            }
        }
        return tags;
    }



    /**
     * 监听回调
     * @param listener 回调
     */
    public void addCallBackListener(OnCallBackListener listener) {
        this.listener = listener;
    }

    /**
     * 回调
     */
    public interface OnCallBackListener {
        /**
         * 保存回调
         * @param checkedParamTxt 选中的参数构成的文本
         */
        public void onCommit(String checkedParamTxt);
        /**
         *返回当前选择数的标记
         * @param params  标记
         */
        public void onCommitParam(List<Integer> params);
    }
}
