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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/25.
 */
public class WaitingDialog extends Dialog {
    private Context context;
    private String title = "";
    private View contentView;
    @InjectView(R.id.msg)
    TextView tips;
    private final int alphaNum = 90;
    private UpdataButtonState updataButtonState;

    /**
     * 更新按钮状态
     */
    public interface UpdataButtonState {
        /**
         * 按钮更新状态
         * @param pressed 是否按压
         */
        public void getButton(Boolean pressed);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param title 标题文字
     */
    public WaitingDialog(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_waiting);
        findViewById(R.id.dg_content).getBackground().setAlpha(alphaNum);
        ButterKnife.inject(this);
        contentView = findViewById(R.id.dg_content);
    }

    @Override
    public void show() {
        super.show();
        contentView.startAnimation(AnimationUtils.loadAnimation(context
                , R.anim.dialog_main_show_amination));
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(context
                , R.anim.dialog_main_hide_amination);
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
                        WaitingDialog.super.dismiss();
                    }
                });
            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
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
     * 设置内容
     * @param tips 内容
     */
    public void setText(String tips) {
        this.tips.setText(tips);
    }

}
