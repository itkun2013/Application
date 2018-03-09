package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血压设置Dialog
 */
public class NibpSettingDialog extends Dialog implements View.OnClickListener {

    @InjectView(R.id.btn_close)
    Button btnClose;
    @InjectView(R.id.btn_calibrate)
    Button btnCalibrate;
    @InjectView(R.id.btn_leak)
    Button btnLeak;
    @InjectView(R.id.btn_reset)
    Button btnReset;
    @InjectView(R.id.tv_cuff)
    TextView tvCuff;
    @InjectView(R.id.tv_result)
    TextView tvResult;
    @InjectView(R.id.tv_stop_status)
    TextView tvStopStatus;

    private Context context;

    private boolean checking = false;

    private OnButtonClickListener listener;

    //是否已经显示文本，防止错误信息被测量通过刷走
    private boolean showing = false;

    /**
     * * 构造器
     * @param context 上下文
     */
    public NibpSettingDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nibp_setting);
        ButterKnife.inject(this);
        btnCalibrate.setOnClickListener(this);
        btnLeak.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        setCuffValue(0);
        tvResult.setVisibility(View.INVISIBLE);
        tvCuff.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                if (listener != null) {
                    tvResult.setVisibility(View.INVISIBLE);
                    tvCuff.setVisibility(View.GONE);
                    resetUI();
                    //如果关闭了窗口，就停止检测
                    listener.onLeakTestClick(false);
                }
                dismiss();
                break;
            case R.id.btn_calibrate:
                if (checking) {
                    checking = false;
                    btnCalibrate.setText(R.string.nibp_calibrate);
                    btnCalibrate.setActivated(false);
                    btnLeak.setEnabled(true);
                    tvResult.setVisibility(View.GONE);
                    tvStopStatus.setVisibility(View.VISIBLE);
                    tvStopStatus.setText(R.string.nibp_stop_calibrate);
                } else {
                    showing = false;
                    checking = true;
                    tvStopStatus.setVisibility(View.GONE);
                    tvCuff.setVisibility(View.VISIBLE);
                    btnCalibrate.setText(R.string.nibp_stop_calibrate);
                    btnCalibrate.setActivated(true);
                    btnLeak.setEnabled(false);
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(R.string.calibrating);
                }

                if (null != listener) {
                    listener.onCalibrateClick(checking);
                }

                break;
            case R.id.btn_leak:
                if (checking) {
                    checking = false;
                    btnLeak.setText(R.string.nibp_leak_test);
                    btnLeak.setActivated(false);
                    btnCalibrate.setEnabled(true);
                    tvResult.setVisibility(View.GONE);
                    tvStopStatus.setVisibility(View.VISIBLE);
                    tvStopStatus.setText(R.string.nibp_stop_leak_test);
                } else {
                    showing = false;
                    checking = true;
                    tvStopStatus.setVisibility(View.GONE);
                    tvCuff.setVisibility(View.VISIBLE);
                    btnLeak.setText(R.string.nibp_stop_leak_test);
                    btnLeak.setActivated(true);
                    btnCalibrate.setEnabled(false);
                    tvResult.setVisibility(View.VISIBLE);
                    tvResult.setText(R.string.leak_testing);
                }

                if (null != listener) {
                    listener.onLeakTestClick(checking);
                }
                break;
            case R.id.btn_reset:
                tvResult.setVisibility(View.INVISIBLE);
                tvCuff.setVisibility(View.GONE);
                resetUI();
                if (null != listener) {
                    listener.onResetClick();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        //设置显示dialog时窗体背景变暗
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (metrics.widthPixels * 0.9);
        lp.height = (int) (metrics.heightPixels * 0.9);
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 添加按钮事件监听者
     * @param listener 按钮监听者
     */
    public void addOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    /**
     * 按钮点击监听器
     */
    public interface OnButtonClickListener {
        /**
         * 校准被点击
         * @param check 校准或停止校准
         */
        void onCalibrateClick(boolean check);

        /**
         * 漏气检测被点击
         * @param check 检测或停止检测
         */
        void onLeakTestClick(boolean check);

        /**
         * 复位被点击
         */
        void onResetClick();
    }

    /**
     * 测量成功或失败时，重置界面
     */
    public void resetUI() {
        if (btnCalibrate != null) {
            checking = false;
            btnCalibrate.setEnabled(true);
            btnCalibrate.setText(R.string.nibp_calibrate);
            btnCalibrate.setActivated(false);
            btnLeak.setEnabled(true);
            btnLeak.setText(R.string.nibp_leak_test);
            btnLeak.setActivated(false);
        }
    }

    /**
     * 设置袖带压
     * @param cuff 袖带压
     */
    public void setCuffValue(int cuff) {
        if (tvCuff != null) {
            tvCuff.setText(getContext().getString(R.string.nibp_cuff) + String.valueOf(cuff));
        }
    }

    /**
     * 设置测量状态
     * @param result 状态
     */
    public void setCheckStatus(String result) {
        if (!showing) {
            tvResult.setText(result);
            tvCuff.setVisibility(View.GONE);
            showing = true;
        }
    }

    /**
     * 获取测量状态
     * @return 获取测量状态
     */
    public boolean getCheckState() {
        return checking;
    }
}
