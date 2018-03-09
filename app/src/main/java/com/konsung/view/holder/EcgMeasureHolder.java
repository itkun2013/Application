package com.konsung.view.holder;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.defineview.EcgViewFor12;
import com.konsung.util.UiUitls;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 正在测量中的心电持有者
 */
public class EcgMeasureHolder extends ViewHolder {

    @InjectView(R.id.view_ecg)
    public EcgViewFor12 ecgView;

    private boolean checking = false;

    /**
     * 持有者构造器，固定布局
     * @param context 上下文
     */
    public EcgMeasureHolder(Context context) {
        super(context, R.layout.layout_measure_ecg, null);
        ButterKnife.inject(this, view);
        ecgView.setLayoutZoom(0.6f);
        initEvent();
    }

    /**
     * 事件监听
     */
    private void initEvent() {
        ecgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(UiUitls.getContent(), UiUitls.getString(R.string
                                .ecg_measuring_not_action), Toast.LENGTH_SHORT).show();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
    
    /**
     * 从Window中移除
     * @param windowManager 窗口管理器
     */
    public void remove(WindowManager windowManager) {
        if (checking) {
            windowManager.removeViewImmediate(view);
            checking = false;
        }
    }

    /**
     * 添加进悬浮窗
     * @param windowManager 窗口管理器
     */
    public void addToWindow(WindowManager windowManager) {
        if (!checking) {
            final WindowManager.LayoutParams params = new WindowManager.
                    LayoutParams(WindowManager.LayoutParams.TYPE_PHONE);
            params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; //可以获取back事件
            windowManager.addView(view, params);
            checking = true;
        }
    }

    /**
     * 写入心电数据
     * @param param 参数
     * @param data 心电数据
     */
    public void addEcgData(int param, int data) {
        ecgView.addEcgData(param, data);
    }
}
