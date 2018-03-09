package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 已经是最新版本的提升框
 */
public class RefreshDialog extends Dialog {
    private Context context;
    private String title = "";

    private View contentView;
    private View rootView;

    @InjectView(R.id.commit_btn)
    ImageTextButton btnCommit;
    @InjectView(R.id.irtemp_dg_title)
    TextView dialogTitle;
    @InjectView(R.id.refresh_tip)
    TextView tips;
    @InjectView(R.id.btn_close)
    ImageView btnClose;
    private OnClickListener onClickListener;

    public RefreshDialog(Context context, String title, OnClickListener
            listener) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
        this.onClickListener = listener;
    }

    /**
     * 用户点击的监听事件
     */
    public interface OnClickListener {
        void onClick(View view);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_refresh);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });
        dialogTitle.setText(title);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.dialog_main_show_amination));
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
                        RefreshDialog.super.dismiss();
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

    public void setTips(String tips) {
        this.tips.setText(tips);
    }


}


