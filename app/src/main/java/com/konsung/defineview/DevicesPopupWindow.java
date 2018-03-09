package com.konsung.defineview;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.konsung.util.GlobalConstant.CHAR_SEMICOLON;

/**
 * Created by dlx on 2017/10/25 0011.
 * 设备配置弹出框
 */

public class DevicesPopupWindow extends Dialog {

    private final Activity context;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.cb_ksm5)
    CheckBox cbKsm5;
    @InjectView(R.id.cb_empui)
    CheckBox cbEmpui;
    @InjectView(R.id.cb_ga7)
    CheckBox cbGa7;
    @InjectView(R.id.cb_ida007)
    CheckBox cbIda007;
    @InjectView(R.id.cb_htd8819)
    CheckBox cbHtd8819;
    @InjectView(R.id.cb_th809)
    CheckBox cbTh809;
    @InjectView(R.id.cb_bene_check_glu)
    CheckBox cbBeneCheckGlu;
    @InjectView(R.id.cb_bene_check)
    CheckBox cbBeneCheck;
    @InjectView(R.id.cb_ogm111)
    CheckBox cbOgm111;
    @InjectView(R.id.cb_urit31)
    CheckBox cbUrit31;
    @InjectView(R.id.cb_mission_u120)
    CheckBox cbMissionU120;
    @InjectView(R.id.cb_hemo_cue_wbc)
    CheckBox cbHemoCueWbc;
    @InjectView(R.id.cb_hemo_cue_hb)
    CheckBox cbHemoCueHb;
    @InjectView(R.id.cb_urit12)
    CheckBox cbUrit12;
    @InjectView(R.id.cb_mission_hb)
    CheckBox cbMissionHb;
    @InjectView(R.id.cb_ccm111)
    CheckBox cbCcm111;
    @InjectView(R.id.cb_beiwen_one)
    CheckBox cbBeiwenOne;  //usb串口绑定，需要通过握手协议的倍稳血糖自动识别串口
    @InjectView(R.id.cb_beiwen_two)
    CheckBox cbBeiwenTwo;  //指定串口绑定，不通过握手协议的倍稳血糖
    @InjectView(R.id.cbx_ea12)
    CheckBox cbxEa12; //三诺二合一
    @InjectView(R.id.chk_unt5000)
    CheckBox chkUnt5000;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;

    private OnCallBackListener listener;
    private List<CheckBox> checkBoxes;
    private int paramValue;

    /**
     * 构造函数
     * @param context 上下文引用
     */
    public DevicesPopupWindow(Activity context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        initPopwindow();
        initParamBox();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        checkBoxes = new ArrayList<>();
        cbKsm5.setChecked(false);
        checkBoxes.add(cbKsm5);
        checkBoxes.add(cbEmpui);
        checkBoxes.add(cbGa7);
        checkBoxes.add(cbIda007);
        checkBoxes.add(cbHtd8819);
        checkBoxes.add(cbTh809);
        checkBoxes.add(cbBeneCheckGlu);
        checkBoxes.add(cbBeneCheck);
        checkBoxes.add(cbOgm111);
        checkBoxes.add(cbUrit31);
        checkBoxes.add(cbMissionU120);
        checkBoxes.add(cbHemoCueWbc);
        checkBoxes.add(cbHemoCueHb);
        checkBoxes.add(cbUrit12);
        checkBoxes.add(cbMissionHb);
        checkBoxes.add(cbCcm111);
        checkBoxes.add(cbBeiwenOne); // 16
        checkBoxes.add(chkUnt5000); //17
        checkBoxes.add(cbBeiwenTwo); // 20 看文档
        checkBoxes.add(cbxEa12); //21
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_devices, null);
        ButterKnife.inject(this, view);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        initData();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicesPopupWindow.this.hide();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicesPopupWindow.this.hide();
                if (listener != null) {
                    listener.onCommit(getCheckedParamTxt());
                }
            }
        });
    }

    /**
     *
     * 获取保存的参数配置值
     * @return 参数配置值
     */
    public int getCheckedParamValue() {
        int value = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                if (checkBoxes.get(i) == cbBeiwenTwo) {
                    value = value | (0x01 << GlobalNumber.TWENTY_NUMBER);
                } else if (checkBoxes.get(i) == cbxEa12) {
                    value = value | (0x01 << GlobalNumber.TWENTY_ONE_NUMBER);
                } else {
                    value = value | (0x01 << i);
                }
            }
        }
        return value;
    }

    /**
     * 参数配置项初始化 (使用已保存的值回显)
     */
    private void initParamBox() {
        //普通模式下，对于参数板自带的心电，血氧，血压三项不可修改配置
        if (GlobalConstant.TEST_PASSWORD.equals(UiUitls.getString(R.string.test_password))) {
            cbKsm5.setEnabled(false);
            cbKsm5.setButtonDrawable(R.drawable.check_enable_selector);
        }
        //添加参数配置项
        paramValue = SpUtils.getSpInt(context.getApplicationContext()
                , GlobalConstant.SYS_CONFIG, GlobalConstant.DEVICE_CONFIG_TAG
                , GlobalConstant.DEVICE_CONFIG);
        for (int i = 0; i < checkBoxes.size(); i++) {
            if ((paramValue & (0x01 << i)) != 0) {
                checkBoxes.get(i).setChecked(true);
            } else if (checkBoxes.get(i) == cbBeiwenTwo) {
                //指定串口，看协议
                if ((paramValue & (0x01 << GlobalNumber.TWENTY_NUMBER)) != 0) {
                    checkBoxes.get(i).setChecked(true);
                }
            } else if (checkBoxes.get(i) == cbxEa12) {
                if ((paramValue & (0x01 << GlobalNumber.TWENTY_ONE_NUMBER)) != 0) {
                    checkBoxes.get(i).setChecked(true);
                }
            } else {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    /**
     * 参数配置项初始化 (使用传递的值回显)
     * @param paramValue  配置值
     */
    private void initParamBox(int paramValue) {
        //添加参数配置项
        this.paramValue = paramValue;
        initParamBox();
    }

    /**
     * 获取选中的参数配置项的文本
     * @return 选中的参数值文本
     */
    private String getCheckedParamTxt() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            if (checkBox.isChecked()) {
                CharSequence text = checkBox.getText();
                builder.append(text).append(CHAR_SEMICOLON);
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        String txt = builder.toString();
        return txt;
    }

    /**
     * 回调接口
     */
    public interface OnCallBackListener {
        /**
         * 确定回调函数
         * @param checkedParamTxt 值文本
         */
        void onCommit(String checkedParamTxt);
    }

    /**
     * 监听查询条件确定的回调
     * @param listener 接口
     */
    public void addCallBackListener(OnCallBackListener listener) {
        this.listener = listener;
    }
}
