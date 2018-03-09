package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.konsung.R;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/25.
 */
public class QuickSetDialog extends Dialog {
    @InjectView(R.id.tv_subhead)
    TextView tvSubhead;
    @InjectView(R.id.tv_reversiontext_show)
    TextView tvReversiontextShow;
    private Context context;

    private View contentView;
    private View rootView;

    @InjectView(R.id.commit_btn)
    ImageTextButton btnCommit;
    @InjectView(R.id.cancel_btn)
    ImageTextButton btnCancel;
    @InjectView(R.id.irtemp_dg_title)
    TextView dialogTitle;
    @InjectView(R.id.sp_quick)
    Spinner spQuick;
    @InjectView(R.id.btn_close)
    ImageView btnClose;
    public boolean isShow = false;

    private UpdataButtonState updataButtonState;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    int position = 0;

    public interface UpdataButtonState {
        public void getButton(int pressed);
    }


    public QuickSetDialog(Context context,  UpdataButtonState
            updataButtonState) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.updataButtonState = updataButtonState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_record_dialog);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    updataButtonState.getButton(position);
                }
                hide();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                }
                hide();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    return;
                }
                isShow = true;
                btnClose();
                hide();
            }
        });
        // 建立数据源
        String[] mItems = context.getResources().getStringArray(R.array.quickSet);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout
                .simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spQuick .setAdapter(adapter);
        spQuick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                position = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
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
    public void hide() {
        super.hide();
        isShow = false;
    }

    @Override
    public void dismiss() {
        isShow = false;
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
                        QuickSetDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
        rootView.startAnimation(backAnim);

    }


    /**
     * 设置副标题的显示
     * @param subhead
     */
    public void setSubhead(String subhead) {
        this.tvSubhead.setText(subhead);
    }

    /**
     * 当弹出框时版本更新时，显示版本描述控件
     * @param isShow
     */
    public void isShowReversionText(boolean isShow){
        if (isShow) {
            tvReversiontextShow.setVisibility(View.VISIBLE);
        } else {
            tvReversiontextShow.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 方法空实现，用户点击关闭的实现
     */
    public void btnClose() {

    }

    // ### 隐藏关闭图标，有些提示信息中不需要看到关闭的那个图标

    /**
     * 设置是否隐藏tipsdialog中的关闭按钮
     * <p/>
     * flag:
     * true  隐藏
     * false 不隐藏
     *
     * @param flag
     */
    public void setHideIvClose(boolean flag) {
        if (!flag) {
            btnClose.setVisibility(View.VISIBLE);
        } else {
            btnClose.setVisibility(View.GONE);
        }
    }

}
