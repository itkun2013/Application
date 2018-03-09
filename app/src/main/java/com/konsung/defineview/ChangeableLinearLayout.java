package com.konsung.defineview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.UnitConvertUtil;

import java.util.Map;

/**
 * 自定义TextView动态添加控件
 * Created by DJH on 2017/10/9 0009.
 */
public class ChangeableLinearLayout extends LinearLayout {
    private LinearLayout llContainer1;
    private LinearLayout llContainer2;
    private LinearLayout llContainer3;
    private Context context;
    private static final int MAX_WIDTH = 250; //最大宽度

    /**
     * 构造
     * @param context 上下文
     */
    public ChangeableLinearLayout(Context context) {
        this(context, null, 0);
    }

    /**
     * 构造
     * @param context 上下文
     * @param attrs 属性
     */
    public ChangeableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     * @param context 上下文
     * @param attrs 属性
     * @param defStyleAttr 默认属性
     */
    public ChangeableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.linearlayout_container, this);
        llContainer1 = (LinearLayout) findViewById(R.id.ll_container_1);
        llContainer2 = (LinearLayout) findViewById(R.id.ll_container_2);
        llContainer3 = (LinearLayout) findViewById(R.id.ll_container_3);
    }

    /**
     * 根据数据填充测量项内容
     * @param map 配置项集合
     */
    public void setContent(Map<String, Integer> map) {
        llContainer1.removeAllViews();
        llContainer2.removeAllViews();
        llContainer3.removeAllViews();
        int currentWidth = 0;
        boolean line2Start = false; //第二行是否开始
        boolean line3Start = false; //第三行是否开始
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String str = entry.getKey();
            int status = entry.getValue();
            TextView textView = new TextView(context);
            textView.setTextSize(13);
            textView.setTextColor(Color.WHITE);
            textView.setText(str);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(UnitConvertUtil.dpToPx(context, 5), 0
                    , UnitConvertUtil.dpToPx(context, 5), 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.WRAP_CONTENT, UnitConvertUtil.dpToPx(context, 28));
            params.setMargins(0, 0, UnitConvertUtil.dpToPx(context, 5), 0);
            textView.setLayoutParams(params);
            //1代表未测，2代表正常，3代表异常
            switch (status) {
                case 1:
                    textView.setBackgroundResource(R.drawable.bg_no_measure);
                    break;
                case 2:
                    textView.setBackgroundResource(R.drawable.bg_normal);
                    break;
                case 3:
                    textView.setBackgroundResource(R.drawable.bg_abnormal);
                    break;
                default:
                    textView.setBackgroundResource(R.drawable.bg_no_measure);
                    break;
            }
            //19为每个字占用宽度，14表示左右padding之和
            int nextWidth = currentWidth + str.length() * 19 + 14;
            if (nextWidth <= MAX_WIDTH) {
                llContainer1.addView(textView);
                currentWidth += str.length() * 19 + 21; //21为左右padding和加上margin值
            } else if (nextWidth > MAX_WIDTH && nextWidth <= MAX_WIDTH * 2) {
                llContainer2.addView(textView);
                if (line2Start) {
                    currentWidth += str.length() * 19 + 21;
                } else {
                    line2Start = true;
                    currentWidth = MAX_WIDTH + str.length() * 19 + 21;
                }
            } else if (nextWidth > MAX_WIDTH * 2 && nextWidth <= MAX_WIDTH * 3) {
                if (line3Start) {
                    currentWidth += str.length() * 19 + 21;
                } else {
                    line3Start = true;
                    currentWidth = MAX_WIDTH * 2 + str.length() * 19 + 21;
                }
                llContainer3.addView(textView);
            }
        }
    }
}
