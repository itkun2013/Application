package com.konsung.util;

/**
 * Created by DengLiXiang on 2017/1/12 0012.
 */

import com.konsung.R;

import static com.konsung.util.UiUitls.getString;

/**
 *  是否超出范围
 */
public class RangeHelper {
    private float min;
    private float max;

    public boolean isHaveMin() {
        return haveMin;
    }

    public void setHaveMin(boolean haveMin) {
        this.haveMin = haveMin;
    }

    private boolean haveMin = true; //默认包含最小范围
    private String toastMsg;

    public RangeHelper(float min, float max, boolean haveMin, String field) {
        this.min = min;
        this.max = max;
        this.haveMin = haveMin;

        String sMin = min + "";
        if (sMin.endsWith(getString(R.string.t_end0))) {
            sMin = sMin.replace(getString(R.string.t_end0), "");
        }
        String sMax = max + "";
        if (sMax.endsWith(getString(R.string.t_end0))) {
            sMax = sMax.replace(getString(R.string.t_end0), "");
        }

        String tMin = "";
        if (haveMin) {
            tMin = getString(R.string.t_include) + sMin;
        } else {
            tMin = getString(R.string.t_not_include) + sMin;
        }
        toastMsg = field + getString(R.string.c_colon) +getString(R.string.t_valid_range) +
                sMin + getString(R.string.c_and) +  sMax + tMin+ getString(R.string.c_r_bracket);
    }

    public RangeHelper(float min, float max, boolean haveMin, int fieldResId) {
        this(min, max, haveMin, getString(fieldResId));
    }

    public RangeHelper(float min, float max, String field) {
        this.min = min;
        this.max = max;
        String tMin = "";

        String sMin = min + "";
        if (sMin.endsWith(getString(R.string.t_end0))) {
            sMin = sMin.replace(getString(R.string.t_end0), "");
        }
        String sMax = max + "";
        if (sMax.endsWith(getString(R.string.t_end0))) {
            sMax = sMax.replace(getString(R.string.t_end0), "");
        }

        if (haveMin) {
            tMin = getString(R.string.t_include) + sMin;
        } else {
            tMin = getString(R.string.t_not_include) + sMin;
        }

        toastMsg = field + getString(R.string.c_colon) +getString(R.string.t_valid_range) +
                sMin + getString(R.string.c_and) +  sMax + tMin+ getString(R.string.c_r_bracket);
    }

    public RangeHelper(float min, boolean haveMin, String field) {
        this.min = min;
        this.max = -1;
        this.haveMin = haveMin;

        String sMin = min + "";
        if (sMin.endsWith(getString(R.string.t_end0))) {
            sMin = sMin.replace(getString(R.string.t_end0), "");
        }

        if (haveMin) {
            toastMsg = field + getString(R.string.c_colon) +getString(R.string.t_valid_range_1) +
                    sMin + getString(R.string.c_r_bracket);
        } else {
            toastMsg = field + getString(R.string.c_colon) +getString(R.string.t_valid_range_2) +
                    min + getString(R.string.c_r_bracket);
        }


    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }
}
