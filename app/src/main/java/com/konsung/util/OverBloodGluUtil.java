package com.konsung.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.konsung.R;

/**
 * Created by lipengjie on 2016/11/30 0030.
 */
public class OverBloodGluUtil implements TextWatcher {
    private float min;
    private float max;
    private TextView view;
    public OverBloodGluUtil(float min, float max, TextView view) {
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
       if (!s.equals(UiUitls.getString(R.string.default_value)) && s.length() > 0) {
            try {
                Float aFloat;
                aFloat = Float.valueOf(s);
                if (aFloat < min || aFloat > max) {
                    view.setTextColor(UiUitls.getContent().getResources().getColor(R.color.red));
                } else {
                    view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                            .grass_konsung_1));
                }
            } catch (Exception e) {
                view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                        .grass_konsung_1));
            }
        } else {
            view.setTextColor(UiUitls.getContent().getResources().getColor(R.color
                    .grass_konsung_1));
        }
    }
}
