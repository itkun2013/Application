package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.konsung.R;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 重要升级弹出框
 * @author yuchunhui
 **/
public class ImportantUpdateDialog extends Dialog {

    private Context context;
    private String title = "";
    private String desStr = "";

    private View contentView;

    @InjectView(R.id.title)
    TextView titleTx;
    @InjectView(R.id.des_tx)
    TextView desTx;
    @InjectView(R.id.commit_btn)
    TextView commitBtn;
    @InjectView(R.id.cancel_btn)
    TextView cancelBtn;

    public boolean isShow = false;

    private UpdataButtonState updataButtonState;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public interface UpdataButtonState {
        public void getButton(Boolean pressed);
    }

    public ImportantUpdateDialog(Context context, String title, UpdataButtonState
            updataButtonState) {
        super(context, R.style.transcutestyle);
        this.context = context;
        this.title = title;
        this.updataButtonState = updataButtonState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_important_update);
        ButterKnife.inject(this);

        contentView = findViewById(R.id.content_view);
        commitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    updataButtonState.getButton(true);
                }
                hide();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    updataButtonState.getButton(false);
                }
                hide();
            }
        });
        if (!title.equals("")) {
            titleTx.setText(title);
        }
        if (!desStr.equals("")) {
            desTx.setText(desStr);
        }
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R
                .anim.dialog_main_show_amination));
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
                        ImportantUpdateDialog.super.dismiss();
                    }
                });
            }
        });

        contentView.startAnimation(anim);
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置提示内容
     * @param des 提示内容
     */
    public void setDesStr(String des) {
        this.desStr = des;
        if (!"".equals(des)) {
            desTx.setText(des);
        }
    }

    /**
     * 方法空实现，用户点击关闭的实现
     */
    public void btnClose() {

    }
}
