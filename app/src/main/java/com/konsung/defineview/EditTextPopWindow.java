package com.konsung.defineview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dlx on 2017/10/25 0011.
 * 参数配置弹出框
 */

public class EditTextPopWindow extends Dialog {

    private final Activity context;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_subtitle)
    TextView tvSubtitle;
    @InjectView(R.id.et_content)
    EditText etContent;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;

    private OnCallBackListener listener;
    private String contentTxt = "";
    private String title;
    private String subtitle;

    /**
     * 构造函数
     * @param context 上下文引用
     */
    public EditTextPopWindow(Activity context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        initPopwindow();
    }

    /**
     * 构造函数
     * @param context 上下文引用
     * @param content 内容显示
     */
    public EditTextPopWindow(Activity context, String content) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.contentTxt = content;
        initPopwindow();
        initEvent();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextPopWindow.this.hide();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextPopWindow.this.hide();
                String content = etContent.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    if (listener != null) {
                        listener.onCommit(content);
                    }
                }
            }
        });

        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();

                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etContent, InputMethodManager.SHOW_FORCED);
            }
        });
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etContent.getWindowToken(), 0);
            }
        });
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_edit_text, null);
        ButterKnife.inject(this, view);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        if (!TextUtils.isEmpty(contentTxt)) {
            etContent.setText(contentTxt);
        }

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(subtitle)) {
            tvSubtitle.setText(subtitle);
        }
    }

    /**
     * 监听回调
     * @param listener 回调
     */
    public void addCallBackListener(OnCallBackListener listener) {
        this.listener = listener;
    }

    /**
     * 设置title值
     * @param title title字段
     */
    public void setTitleName(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    /**
     * 设置subtitle值
     * @param subtitle subtitle字段
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        tvSubtitle.setText(subtitle);
    }

    /**
     * 回调
     */
    public interface OnCallBackListener {
        /**
         * 保存回调
         * @param content 内容文本
         */
        public void onCommit(String content);
    }
}
