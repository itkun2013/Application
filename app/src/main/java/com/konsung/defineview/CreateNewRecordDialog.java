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
 * 自定义开始测量dialog
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class CreateNewRecordDialog extends Dialog {

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
    @InjectView(R.id.btn_close)
    ImageView btnClose;

    private UpdataButtonState updataButtonState;
    boolean flag = false;

    /**
     * 接口
     */
    public interface UpdataButtonState {
        /**
         * 回调方法
         * @param isUpdata 是否更新标识
         */
        public void getButton(Boolean isUpdata);
    }

    /**
     *构造器
     * @param context 上下文
     * @param title 标题
     * @param updataButtonState 按钮状态
     */
    public CreateNewRecordDialog(Context context, String title
            , UpdataButtonState updataButtonState) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
        this.updataButtonState = updataButtonState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_record_dialog);

        ButterKnife.inject(this);

        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flag) {
                    return;
                }
                flag = true;
                updataButtonState.getButton(true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (flag) {
                    return;
                }
                flag = true;
                updataButtonState.getButton(false);
            }
        });
        dialogTitle.setText(title);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                flag = false;
            }
        });

    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.dialog_main_show_amination));
        rootView.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.dialog_root_show_amin));
    }

    @Override
    public void dismiss() {
        flag = false;
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
                        CreateNewRecordDialog.super.dismiss();
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
