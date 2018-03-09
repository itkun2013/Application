package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/25.
 */
public class ProgressDialog extends Dialog {
    private Context context;
    private String title = "";

    private View contentView;


    @InjectView(R.id.msg)
    TextView tips;
    @InjectView(R.id.progressBar5)
    ProgressBar progressBar;
    @InjectView(R.id.tv_title)
    TextView tvTitle;

    private UpdataButtonState updataButtonState;

    public interface UpdataButtonState {
        public void getButton(Boolean pressed);
    }


    public ProgressDialog(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_data);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);

    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R
                .anim.dialog_main_show_amination));
    }
    /**
     * 方法空实现，用户点击关闭的实现
     */
    public void btnClose() {

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
                        ProgressDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setText(String tips) {
        this.tips.setText(tips);
    }

    /**
     * 设置进度条最大值
     * @param max
     */
    public void setProgressMax(int max){
        if(null != progressBar){
            progressBar.setMax(max);
        }
    }

    /**
     * 设置当前的进度
     * @param progress
     */
    public void setProgress(int progress){
        if (null != progressBar) {
            progressBar.setProgress(progress);
        }
    }
}
