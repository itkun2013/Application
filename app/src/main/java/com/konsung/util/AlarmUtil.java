package com.konsung.util;

import android.view.View;
import android.widget.ImageView;

import com.konsung.R;

/**
 * 描述 报警工具类
 */
public class AlarmUtil {

    private static final float MIN_VALUE = -10f;
    private static final float MAX_VALUE = -100f;
    /**
     * 执行超限报警
     * @param value 测量值
     * @param high 报警高限
     * @param low 报警低限
     * @param imageView 超限报警标志控件
     */
    public static void executeOverrunAlarm(float value, float high, float low,
            ImageView imageView) {
        if (imageView == null) {
            return;
        }
        //超过最高值
        if (value == MAX_VALUE) {
            imageView.setImageResource(R.drawable.ic_top);
            imageView.setVisibility(View.VISIBLE);
        } else if (value == MIN_VALUE) {
            //超过最低值
            imageView.setImageResource(R.drawable.ic_low);
            imageView.setVisibility(View.VISIBLE);
        } else if (value > high) {
            imageView.setImageResource(R.drawable.ic_top);
            imageView.setVisibility(View.VISIBLE);
        } else if (value < low) {
            imageView.setImageResource(R.drawable.ic_low);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 执行超限报警
     * @param value 测量值
     * @param high 报警高限
     * @param low 报警低限
     * @param imageView 超限报警标志控件
     */
    public static void executeOverrunAlarm(int value, int high, int low,
            ImageView imageView) {
        if (imageView == null) {
            return;
        }
        if (value > high) {
            imageView.setImageResource(R.drawable.ic_top);
            imageView.setVisibility(View.VISIBLE);
        } else if (value < low) {
            imageView.setImageResource(R.drawable.ic_low);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }
}
