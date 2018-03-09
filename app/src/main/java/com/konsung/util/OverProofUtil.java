package com.konsung.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.konsung.R;

/**
 * Created by lipengjie on 2016/11/30 0030.
 */
public class OverProofUtil implements TextWatcher {
    private float min;
    private float max;
    private TextView view;

    /**
     *构造方法
     * @param min 参考值最小值
     * @param max 参考值最大值
     * @param view TextView控件
     */
    public OverProofUtil(float min, float max, TextView view) {
        this.min = min;
        this.max = max;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString();
        if (s.contains(">") || s.contains("<")) {
            view.setTextColor(UiUitls.getContent().getResources().getColor(R.color.high_color));
        } else if (!s.equals(UiUitls.getString(R.string.default_value)) && s.length() > 0) {
            try {
                Float aFloat;
                aFloat = Float.valueOf(s);
                if (aFloat < min || aFloat > max) {
                    view.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    if (GlobalConstant.isInHealthReportFragment) {
                        view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                                .health_report_text_color));
                    } else {
                        view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                                .mesu_text));
                    }
                }
            } catch (Exception e) {
                if (GlobalConstant.isInHealthReportFragment) {
                    view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                            .health_report_text_color));
                } else {
                    view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                            .mesu_text));
                }
            }
        } else {
            if (GlobalConstant.isInHealthReportFragment) {
                view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                        .health_report_text_color));
            } else {
                view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                        .mesu_text));
            }
        }
    }
}
