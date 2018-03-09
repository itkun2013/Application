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
 * 显示导入开机log的对话框
 */
public class StartLogDialog extends Dialog {
    private Context context;

    private View contentView;

    @InjectView(R.id.btn_start_logo)
    ImageTextButton btnStartLogo;
    @InjectView(R.id.btn_start_music)
    ImageTextButton btnStartMusic;
    @InjectView(R.id.btn_close)
    ImageView btnClose;
    public boolean isShow = false;
    private GainButtonState gainButtonState; //监听方法
    public interface GainButtonState {
        /**
         * 是获取开机动画还是获取开机音乐 true为开机动画  false未开机音乐
         * @param pressed  获取标志
         */
        public void getButton(Boolean pressed);
    }

    /**
     * 构造方法
     * @param context 上下文
     */
    public StartLogDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
    }

    /**
     * 设置监听方法
     * @param gainButtonState 监听
     */
    public void setGainButtonState(GainButtonState gainButtonState){
        this.gainButtonState = gainButtonState;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_start_log);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    return;
                }
                isShow = true;
                hide();
            }
        });
        btnStartLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != gainButtonState) {
                    gainButtonState.getButton(true);
                }
            }
        });
        btnStartMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != gainButtonState) {
                    gainButtonState.getButton(false);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R
                .anim.dialog_main_show_amination));
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
                        StartLogDialog.super.dismiss();
                    }
                });
            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
    }
    @Override
    public void hide() {
        super.hide();

    }


}
