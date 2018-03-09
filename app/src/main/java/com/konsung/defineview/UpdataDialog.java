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
 * 自定义开始测量dialog
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-07 13:57
 */
public class UpdataDialog extends Dialog {

    private Context context;
    private String title;

    private View contentView;
    private View rootView;

    @InjectView(R.id.commit_btn)
    ImageTextButton btnCommit;
    @InjectView(R.id.cancel_btn)
    ImageTextButton btnCancel;
    @InjectView(R.id.irtemp_dg_title)
    TextView dialogTitle;

    private UpdataButtonState updataButtonState;

    public interface UpdataButtonState {
        public void getButton(Boolean isUpdata);
    }


    /**
     * @param context
     * @param title
     * @param updataButtonState
     */
    public UpdataDialog(Context context, String title, UpdataButtonState
            updataButtonState) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
        this.updataButtonState = updataButtonState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatadialog);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updataButtonState.getButton(true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updataButtonState.getButton(false);
            }
        });
        dialogTitle.setText(title);
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
                        UpdataDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
        rootView.startAnimation(backAnim);
    }

}
