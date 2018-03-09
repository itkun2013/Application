package com.konsung.util;

import com.konsung.R;

import static com.konsung.util.UiUitls.getString;

/**
 * Created by DengLiXiang on 2017/1/12 0012.
 */

public class PointHelper {
    public int pointBefore;
    public int pointAfter;
    public String toastMsg;

    public PointHelper(int pointBefore, int pointAfter) {
        this.pointBefore = pointBefore;
        this.pointAfter = pointAfter;
        toastMsg = getString(R.string.t_point_len1) + pointBefore
                + getString(R.string.t_point_len3) + getString(R.string.t_point_len2)
                + pointAfter + getString(R.string.t_point_len3);
    }
}
