package com.konsung.defineview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.util.UnitConvertUtil;

/**
 * Created by Administrator on 2015/12/30 0030.
 * 自定义Toast显示
 * 杨远雄
 */
public class CustomToast {
    public static TextView tv;

    public static WindowManager windowManager = null;

    public static void showMessage(final Context act, final String msg) {
        windowManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        showMessage(act, msg, 3000);

    }

    public static void showMessage(final Context act, final int msg) {

        showMessage(act, act.getString(msg), Toast.LENGTH_SHORT);

    }

    public static void showMessage(Context context, String msg, int Length) {

        if (tv == null) {
            tv = new TextView(context);
            tv.setTextColor(Color.WHITE);
            tv.setText(msg);
            tv.setBackgroundResource(R.drawable.toast_shape);
            tv.setTextSize(UnitConvertUtil.dpToPx(context, 15));//单位转换
            //          tv.setBackgroundResource(R.drawable.bg_layoutinput_focused);
            tv.setPadding(UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10));
        } else {
            tv.setText(msg);
            tv.setTextSize(UnitConvertUtil.dpToPx(context, 15));
            //          tv.setBackgroundResource(R.drawable.bg_layoutinput_focused);
            tv.setPadding(UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10),
                    UnitConvertUtil.dpToPx(context, 10));
        }

        if (tv.getParent() == null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.BOTTOM;
            params.verticalMargin = 0.1f;
            params.alpha = 0.65f;
            params.x = 0;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = R.style.anim_toast;
            windowManager.addView(tv, params);
            handler.sendEmptyMessageDelayed(101, Length);
        } else {
            tv.setText(msg);
            handler.removeMessages(101);
            handler.sendEmptyMessageDelayed(101, Length);
        }

    }

    static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (tv.getParent() != null) {
                windowManager.removeView(tv);
            }

        }

    };

    public static void cancelCurrentToast() {

        if (tv != null && tv.getParent() != null) {

            windowManager.removeView(tv);

        }

    }
}
