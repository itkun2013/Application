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
import com.konsung.util.GlobalConstant;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/25.
 */
public class UploadAllProgressDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private String title = "";

    private View contentView;
    @InjectView(R.id.progressBar5)
    MyProgress progressBar;
    @InjectView(R.id.cancel)
    TextView tvCancalUpload;
    @InjectView(R.id.tv_title)
    TextView tvTitle;

    public interface UpdataButtonState {
        /**
         * 取消上传
         */
        public void cancelUpload();
    }

    private UpdataButtonState updataButtonState;

    /**
     * 构造方法
     * @param context 上下文引用
     * @param updataButtonState 接口
     */
    public UploadAllProgressDialog(Context context, UpdataButtonState updataButtonState){
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.updataButtonState = updataButtonState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upload_all_progress_data);
        ButterKnife.inject(this);
        contentView = findViewById(R.id.dg_content);
        initListener();
    }

    /**
     * 添加监听事件
     */
    private void initListener() {
        tvCancalUpload.setOnClickListener(this);
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
                        UploadAllProgressDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
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
     * 设置进度条最大值
     * @param max 进度条最大值
     */
    public void setProgressMax(int max){
        if (null != progressBar){
            progressBar.setMax(max);
        }
    }

    /**
     * 设置当前的进度
     * @param progress 当前进度
     */
    public void setProgress(int progress){
        if (null != progressBar) {
            progressBar.setProgress(progress);
            progressBar.setText();
        }
    }
    @Override
    public void onClick(View v) {
        //上传取消
        if (v == tvCancalUpload) {
            dismiss();
            updataButtonState.cancelUpload();
        }
    }
    /**
     * 设置按钮值
     * @param text 显示值
     * @param title 标题
     */
    public void setText(String text, String title) {
        tvCancalUpload.setText(text);
        tvTitle.setText(title);
    }
}
