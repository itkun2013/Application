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
public class StartMeasureDialogSpo2 extends Dialog {

    private Context context;
    private String title;

    private View contentView;
    private View rootView;

    @InjectView(R.id.spo2_dg_trend)
    TextView spo2TrendTv;
    @InjectView(R.id.spo2_dg_pr)
    TextView spo2PrTv;
    @InjectView(R.id.spo2_dg_title)
    TextView dialogTitle;
    @InjectView(R.id.spo2_dg_pb)
    ProgressBarDeterminate dialogPb;

    /*
     * dialog 只有设置title
     * @param context 上下文对象
     * @param title dialog标题
     */

    public StartMeasureDialogSpo2(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_start_measure_spo2);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        // 去掉外部点击消失功能
//        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getX() < contentView.getLeft()
//                        || event.getX() > contentView.getRight()
//                        || event.getY() > contentView.getBottom()
//                        || event.getY() < contentView.getTop()) {
//                    dismiss();
//                }
//                return false;
//            }
//        });

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
                        StartMeasureDialogSpo2.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
        rootView.startAnimation(backAnim);
    }

    /*
     * 设置数据
     * spo2Trend,spo2Pr
     * 这些方法由外部activity调用
     */
    public void setSpo2Trend(String spo2Trend) {
        spo2TrendTv.setText(spo2Trend);
    }

    public void setspo2Pr(String pr) {
        spo2PrTv.setText(pr);
    }

    public void setProgressBarMax(int length) {
        dialogPb.setMax(length);
    }

    public void setProgress(int num) {
        dialogPb.setProgress(num);
    }
}
