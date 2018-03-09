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
public class UpLoadingDialog extends Dialog {

    private Context context;
    private String title;

    private View contentView;
    private View rootView;

    @InjectView(R.id.irtemp_dg_trend)
    TextView irtempTrendTv;
    @InjectView(R.id.irtemp_dg_title)
    TextView dialogTitle;
    @InjectView(R.id.irtemp_dg_pb)
    ProgressBarDeterminate dialogPb;
    @InjectView(R.id.file_count)
    TextView count;


    /**
     * @param context
     * @param title
     */
    public UpLoadingDialog(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploading_dialog);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
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
                        UpLoadingDialog.super.dismiss();
                        //getContext().StartMeasureDialog.super.dismiss();
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
     * @param irtempTrend
     */
    public void setIrtempTrend(String irtempTrend) {

        irtempTrendTv.setText(irtempTrend);
    }

    public void setProgressBarMax(int length) {

        dialogPb.setMax(length);
    }

    public void setProgress(int num) {

        dialogPb.setProgress(num);
    }


    /**
     * @param count
     */
    public void setCount(int count) {
        this.count.setText(String.valueOf(count));
    }
}
