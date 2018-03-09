package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Handler;
import android.util.AttributeSet;

import android.view.View;


import com.konsung.R;
import com.konsung.util.UiUitls;

import java.util.ArrayList;

/**
 * Created by XuJunwei on 2015-04-20.
 */
public class WaveFormSpo2 extends View {

    private Paint paint;
    private Paint paintBrokenLine;
    private float[] points;
    private int index = 0;
    int x = 0;
    private int sampleRate;
    private Handler handler = new Handler();
    private ArrayList<Byte> wave;

    private int POST_TIME = 40;

    private ArrayList<byte[]> waveList;
    private float factor = (float) 0.781;  // 200 / 256
    private boolean isDrawing = false;

    private boolean isQcheck = false;

    public WaveFormSpo2(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintBrokenLine = new Paint();
        paintBrokenLine.setStrokeWidth(2);
        paintBrokenLine.setColor(Color.parseColor("#00CED1"));
        paintBrokenLine.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(Color.parseColor("#00CED1"));
        paint.setAntiAlias(true);


        //设置字体大小
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);  //设置画出的线的 粗细程度

        wave = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(UiUitls.getContent().getResources().getString(R
                .string.spo2_ware), 10, 30, paint);
        if (isDrawing)
            canvas.drawLines(points, paintBrokenLine);
        super.onDraw(canvas);
    }

    public void stop() {
        x = 0;
        index = 0;
        isDrawing = false;
        wave.clear();
        handler.removeCallbacks(update);
    }

    public void reset() {
        stop();
        isDrawing = true;
        points = new float[sampleRate * 40];
        handler.post(update);
    }

    /*
     * 往波形控件中添加数据
     */
    public void setData(byte[] data) {
        if (data == null) {
            return;
        }

        if (isDrawing)
            for (int i = 0; i < data.length; i++) {
                wave.add(data[i]);
            }
        if (wave.size() > 6000) {
            wave.clear();
        }
    }

    public void setSampleRate(int sampleRate) {
        points = new float[sampleRate * 40];
        index = 0;
        this.sampleRate = sampleRate;
        handler.post(update);

    }

    /*
     * 更新界面函数
     */
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if (sampleRate == 0) {
                handler.postDelayed(this, POST_TIME);
                return;
            }

            for (int i = 0; i < 5; i++) {
                if (wave.size() < 4) {
                    handler.postDelayed(this, POST_TIME);
                    return;
                }

                // 如果是在快速检测页面刷新波形图，图形向下移动一些
                if (isQcheck) {
                    points[index++] = x;
                    points[index++] = getHeight() - (factor * (wave.get(0) &
                            0xFF)) + 20;
                    points[index++] = ++x;
                    points[index++] = getHeight() - (factor * (wave.get(2) &
                            0xFF)) + 20;
                    wave.remove(0);        // SPO2波形占用两个字节，将最前面的两个字节移除
                    wave.remove(0);
                } else {
                    //
                    points[index++] = x;
                    points[index++] = getHeight() - (factor * (wave.get(0) &
                            0xFF)) - 40;
                    points[index++] = ++x;
                    points[index++] = getHeight() - (factor * (wave.get(2) &
                            0xFF)) - 40;
                    wave.remove(0);        // SPO2波形占用两个字节，将最前面的两个字节移除
                    wave.remove(0);

                }
                // Log.d("Test", "2 = " + points[index - 3] + " ;4 = " +
                // points[index-1]);
/*                Log.d("Test", "y1 = " + (getHeight() / 2 - (factor * (wave
.get(0) & 0xFF))));
                Log.d("Test", "y2 = " + (getHeight() / 2 - (factor * (wave
                .get(2) & 0xFF))));*/
                /*if (index >= getWidth() * 5) {
                    index = 0;
                    x = 0;
                    break;
                }*/
                if (index >= getWidth() * 4.5f) {
                    index = 0;
                    x = 0;
                    break;
                }
            }
            for (int i = 0; i < 32; i += 2) {
                points[index + i] = x + i;
                points[index + i + 1] = -10;
            }

            postInvalidate();
            handler.postDelayed(this, POST_TIME);
        }
    };

    /**
     * 是否是在快速检测页面绘制波形
     *
     * @param flag
     */
    public void setQcheck(boolean flag) {
        isQcheck = flag;
    }
}
