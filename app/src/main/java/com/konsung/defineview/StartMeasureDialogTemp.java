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
 * @date 2015-03-23 09:53
 */
public class StartMeasureDialogTemp extends Dialog {

    private Context context;
    private String title;

    private View contentView;
    private View rootView;

    @InjectView(R.id.temp_dg_t1)
    TextView tempDgT1;
    @InjectView(R.id.temp_dg_t2)
    TextView tempDgT2;
    @InjectView(R.id.temp_dg_td)
    TextView tempDgTd;
    @InjectView(R.id.temp_dg_title)
    TextView dialogTitle;
    @InjectView(R.id.temp_dg_pb)
    ProgressBarDeterminate dialogPb;

    /*
     * dialog 只有设置title
     * @param context 上下文对象
     * @param title dialog标题
     */
    public StartMeasureDialogTemp(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_start_measure_temp);

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
                        StartMeasureDialogTemp.super.dismiss();
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
     * T1,T2,TD
     * 这些方法由外部activity调用
     */
    public void setTempT1(String t1) {
        tempDgT1.setText(t1);
    }

    public void setTempT2(String t2) {
        tempDgT2.setText(t2);
    }

    public void setTempTd(String td) {
        tempDgTd.setText(td);
    }

    public void setProgressBarMax(int length) {
        dialogPb.setMax(length);
    }

    public void setProgress(int num) {
        dialogPb.setProgress(num);
    }


}
