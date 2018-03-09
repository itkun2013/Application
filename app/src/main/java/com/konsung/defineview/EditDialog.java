package com.konsung.defineview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatEditText;
import com.konsung.R;
import com.konsung.util.UiUitls;

/**
 * Created by Administrator on 2016/1/20 0020.
 * 血细胞分析设置ip的对话框
 */
public class EditDialog extends Dialog {
    private Context context;
    private String title = "";

    private View contentView;
    private View rootView;


    ImageTextButton btnCommit;

    ImageTextButton btnCancel;

    FlatEditText etQuery;

    TextView tips;

    boolean flag = false;

    private UpdataButtonState mLinster;

    public interface UpdataButtonState {
        public void getButton(Boolean pressed);
    }


    public EditDialog(Context context, String title, UpdataButtonState
            mLinster) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.title = title;
        this.mLinster = mLinster;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.dialog_edit, null);
        setContentView(view);
        btnCommit = (ImageTextButton) view.findViewById(R.id.btn_commit);
        btnCancel = (ImageTextButton) view.findViewById(R.id.btn_cancel);
        etQuery = (FlatEditText) view.findViewById(R.id.dialog_edit_query);
        tips = (TextView) view.findViewById(R.id.tv_dg_title);
        contentView = findViewById(R.id.dg_content);
        rootView = findViewById(R.id.dg_rootview);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    return;
                }
                mLinster.getButton(true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    return;
                }
                mLinster.getButton(false);
                dismiss();
            }
        });
        tips.setText(title);
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        contentView.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.dialog_main_show_amination));
        rootView.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.dialog_root_show_amin));
        flag = false;
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

                        EditDialog.super.dismiss();
                    }
                });

            }
        });

        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim
                .dialog_root_hide_amin);
        contentView.startAnimation(anim);
        rootView.startAnimation(backAnim);
        flag = true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTips(String tips) {
        this.tips.setText(tips);
    }

    /**
     * 获取用户输入ip的方法
     *
     * @return
     */
    public String getQuery() {
        return etQuery.getText().toString();
    }


    /**
     * 查询功能
     */
    public void initSelectView() {
        etQuery.setText("");
        btnCancel.setText(UiUitls.getString(R.string.cancel));
        btnCommit.setText(UiUitls.getString(R.string.query));
    }

    public EditText getEtSelectView() {
        return etQuery;
    }
}
