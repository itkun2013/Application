package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.SpUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/25.
 */
public class EcgSettingDialog extends Dialog {
    private Context context;
    private String title = "";

    private View contentView;
    private View rootView;

    @InjectView(R.id.btn_confirm)
    Button btnCommit;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.spn_speed)
    Spinner spnSpeed;
    @InjectView(R.id.spn_amplification)
    Spinner spnAmplification;
    @InjectView(R.id.btn_close)
    Button btnClose;


    private UpdataButtonState mLinster;

    public interface UpdataButtonState {
        public void getButton(Boolean pressed);
    }


    public EcgSettingDialog(Context context, String title, UpdataButtonState
            mLinster) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
        this.mLinster = mLinster;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ecg_setting);

        ButterKnife.inject(this);

        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(
                context, R.array.mm_list, R.layout.ecg_spinner_button);
        spAdapter.setDropDownViewResource(R.layout.flat_list_item);
        spnSpeed.setAdapter(spAdapter);

        ArrayAdapter<CharSequence> xxAdapter = ArrayAdapter.createFromResource(
                context, R.array.xx_list, R.layout.ecg_spinner_button);
        // Specify the layout to use when the list of choices appears
        xxAdapter.setDropDownViewResource(R.layout.flat_list_item);
        spnAmplification.setAdapter(xxAdapter);

        init();
        contentView = findViewById(R.id.ll_content);
        rootView = findViewById(R.id.rl_root_view);
//        init();

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setECGmm(spnSpeed.getSelectedItemPosition());
                setECGxx(spnAmplification.getSelectedItemPosition());
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, EcgDefine
                        .ECG_VELOCITY_SYSTEM, spnSpeed.getSelectedItemPosition());
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, EcgDefine
                        .ECG_AMPLITUDE_SYSTEM, spnAmplification.getSelectedItemPosition());
                mLinster.getButton(true);
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinster.getButton(false);
                dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinster.getButton(false);
                dismiss();
            }
        });
        tvTitle.setText(title);
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R
                .anim.dialog_main_show_amination));
        rootView.startAnimation(AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_show_amin));
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_main_hide_amination);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contentView.post(new Runnable() {
                    @Override
                    public void run() {
                        EcgSettingDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
        rootView.startAnimation(backAnim);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * 设置ECG出纸速度
     * @param position
     * @return
     */
    private float setECGmm(int position) {
        switch (position) {
            case 0:
                GlobalConstant.ECG_MM = 0.2f;

                SpUtils.saveToSp(context, "sys_config", "mm", 0.2f);

                return 0.2f;
            case 1:
                GlobalConstant.ECG_MM = 0.25f;
                SpUtils.saveToSp(context, "sys_config", "mm", 0.25f);
                return 0.25f;

            case 2:
                GlobalConstant.ECG_MM = 0.4f;
                SpUtils.saveToSp(context, "sys_config", "mm", 0.4f);
                return 0.4f;

            case 3:
                GlobalConstant.ECG_MM = 0.5f;
                SpUtils.saveToSp(context, "sys_config", "mm", 0.5f);
                return 0.5f;

            case 4:
                GlobalConstant.ECG_MM = 1.0f;
                SpUtils.saveToSp(context, "sys_config", "mm", 1.0f);
                return 1.0f;

            case 5:
                GlobalConstant.ECG_MM = 2.0f;
                SpUtils.saveToSp(context, "sys_config", "mm", 2.0f);
                return 2.0f;

            default:
                GlobalConstant.ECG_MM = 1.0f;
                SpUtils.saveToSp(context, "sys_config", "mm", 1.0f);
                return 1.0f;
        }

    }


    /**
     * 获取当前出纸速度
     * @param mm
     * @return
     */
    private int getECGmm(float mm) {
        if (mm == 0.2f) {
            return 1;
        } else if (mm == 0.25f) {
            return 2;
        } else if (mm == 0.4f) {
            return 3;
        } else if (mm == 0.5f) {
            return 4;
        } else if (mm == 1.0f) {
            return 5;
        } else if (mm == 2.0f) {
            return 6;
        } else {
            return 5;
        }
    }


    /**
     * 设置ECG出纸增幅
     * @param position
     * @return
     */
    private float setECGxx(int position) {
        switch (position) {
            case 0:
                GlobalConstant.ECG_XX = 0.25f;
                SpUtils.saveToSp(context, "sys_config", "xx", 0.25f);
                return 0.25f;

            case 1:
                GlobalConstant.ECG_XX = 0.5f;
                SpUtils.saveToSp(context, "sys_config", "xx", 0.5f);
                return 0.5f;

            case 2:
                GlobalConstant.ECG_XX = 1.0f;
                SpUtils.saveToSp(context, "sys_config", "xx", 1.0f);

                return 1.0f;
            case 3:
                GlobalConstant.ECG_XX = 2.0f;
                SpUtils.saveToSp(context, "sys_config", "xx", 2.0f);

                return 2.0f;
            case 4:
                GlobalConstant.ECG_XX = -1000f;
                SpUtils.saveToSp(context, "sys_config", "xx", -1000f);

                return -1000f;
            default:
                GlobalConstant.ECG_XX = 1.0f;
                SpUtils.saveToSp(context, "sys_config", "xx", 1.0f);
                return 1.0f;
        }
    }


    /**
     * 获取当前出纸速度
     * @param xx
     * @return
     */
    private int getECGxx(float xx) {
        if (xx == 0.25f) {
            return 1;
        } else if (xx == 0.5f) {
            return 2;
        } else if (xx == 1.0f) {
            return 3;
        } else if (xx == 2.0f) {
            return 4;
        } else if (xx == -1000f) {
            return 5;
        } else {
            return 1;
        }
    }


    /**
     * 初始化
     */
    private void init() {
        spnSpeed.setSelection(getECGmm(SpUtils.getSpFloat(context
                        .getApplicationContext(),
                "sys_config", "mm", 1.0f)) - 1);
        spnAmplification.setSelection(getECGxx(SpUtils.getSpFloat(context
                        .getApplicationContext(),
                "sys_config", "xx", 1.0f)) - 1);
    }


}
