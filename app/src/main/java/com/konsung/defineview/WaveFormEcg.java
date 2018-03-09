package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.konsung.util.GlobalConstant;

import java.util.ArrayList;


/**
 * Created by XuJunwei on 2015-04-23.
 */
public class WaveFormEcg extends View {
    //一个约值，appdevices上传的基线值
    private final float AXIS = 0.0f;
    //越界界限
    private final float MAX_HEIGHT = 32.0f;
    //最大越界点数
    private final int maxErr = 2;

    private Paint paint;
    private Paint paintBrokenLine;
    private float[] points1;
    private float[] points2;
    private float[] points3;

    private int index = 0;
    float x = 0;
    private int sampleRate = 500;
    private Handler handler = new Handler();
    static private int updateRate = 25;  // 界面更新频率
    static private int height = 180;
    private int waveParam;
    private ArrayList<Byte> wave;
    private ArrayList<byte[]> waveList;
    private float factor;
    private String title = "";
    private boolean isdrawing = false;
    private boolean drawTiTle = true;
    private String EcgXx = "";
    private String EcgAuto = "x1";

    //本地增益程度，用于自动增益，默认为x1
    private float ecgAutoX = 1.0f;
    //累计越界点
    private int OUTNUM = 0;
    //累计低位点
    private int LOWNUM = 0;
    //累计重点位
    private int MIDNUM;


    public WaveFormEcg(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintBrokenLine = new Paint();
        paintBrokenLine.setStrokeWidth(2);
        paintBrokenLine.setColor(Color.parseColor("#7CFC00"));
        paintBrokenLine.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(Color.parseColor("#7CFC00"));
//		paint.setAntiAlias(true);

        //设置字体大小
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);  //设置画出的线的 粗细程度

        wave = new ArrayList<>();
        waveList = new ArrayList<>();
        handler.post(convertToList);
        factor = (float) 0.044; //180 / 4096\

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画心电测量背景的网格
        drawEcgGrid(canvas);

        //画标尺
        if (GlobalConstant.ECG_XX != -1000.f) {
            // 设置为非自动增益时，根据GlobalConstant.ECG_XX来判断增益
            drawEcgRuler(canvas, GlobalConstant.ECG_XX);
        } else {
            // 设置为自动增益时，根据ecgAutoX来判断增益
            drawEcgRuler(canvas, ecgAutoX);
        }

        if (GlobalConstant.ECG_XX != -1000f) {
            EcgXx = title + " " + getECGxx();
        } else {
            EcgXx = title + EcgAuto;
        }

        canvas.drawText(EcgXx, 10, 30, paint);
        if (isdrawing) {
            if (GlobalConstant.ECG_XX != -1000.f) {
                canvas.drawLines(getDrawPoint(GlobalConstant.ECG_XX),
                        paintBrokenLine);
            } else {
                canvas.drawLines(getDrawPoint(ecgAutoX), paintBrokenLine);
            }
        }


    }

    public void setTitle(String str, int waveParam) {

        title = str;
        this.waveParam = waveParam;
    }

    public void stop() {
        drawTiTle = false;
        isdrawing = false;
        x = 0;
        index = 0;
        handler.removeCallbacks(update);
    }

    public void reset() {
        stop();
        EcgXx = "";
        isdrawing = true;
        waveList.clear();
        wave.clear();
        points1 = new float[10000];
        points2 = new float[10000];
        points3 = new float[10000];

        x = 0;
        index = 0;
        invalidate();
        handler.post(update);
    }

    public void setData(byte[] data) {
        /*Log.d("Test"," 1 = " + UnitConvertUtil.bytesToHexString(data));*/
        if (data == null && isdrawing) {
            return;
        }
        waveList.add(data);
        if (waveList.size() > 10) {
            waveList.clear();
        }
    }

    public void setSampleRate(int sampleRate) {
        points1 = new float[10000];
        points2 = new float[10000];
        points3 = new float[10000];

        isdrawing = true;
        index = 0;
        this.sampleRate = sampleRate;
        handler.post(update);
    }

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if (sampleRate == 0) {
                /*Log.d("Test", waveParam + " = 1 - 等");*/
                handler.postDelayed(this, 1000);
                return;
            }
            // 心电是500Hz，这样一次需要更新20个点
            for (int i = 0; i < 5; i++) {
                if (wave.size() < 16) {
//                    Log.d("Test", waveParam + " = 2 - 等");
                    handler.postDelayed(this, 1000);
                    return;
                }
                points1[index] = x;
                points2[index] = x;
                points3[index] = x;
                index++;
                float h1 = (factor * ((wave.get(0) & 0xFF) + ((wave.get(1) &
                        0x0F) << 8)
                        - 2048) * 6);
                float h2 = (factor * ((wave.get(8) & 0xFF) + ((wave.get(9) &
                        0x0F) << 8)
                        - 2048) * 6);
                setAutoXX(h1);
                setAutoXX(h2);
                //三种增益的缓存，需要切换增益时，直接切换缓存
                points1[index] = getH(h1, 0.5f);
                points2[index] = getH(h1, 1.0f);
                points3[index] = getH(h1, 2.0f);

                index++;
                x = x + GlobalConstant.ECG_MM;
                points1[index] = x;
                points2[index] = x;
                points3[index] = x;

                index++;
                points1[index] = getH(h2, 0.5f);
                points2[index] = getH(h2, 1.0f);
                points3[index] = getH(h2, 2.0f);
                index++;

//                    Log.d("Test", "v1 = " + h1 + "   v2 = " + h2 + "
// height/2 = " + getHeight() / 2);

                // 每4个点（8个字节）中选取一个点，其余的删除。
                for (int j = 0; j < 8; j++) {
                    wave.remove(0);
                }

                if (x >= getWidth()) {
                    index = 0;
                    x = 0;
                }
            }

            for (int i = 0; i < 32; i += 2) {
                points1[index + i] = x + i;
                points1[index + i + 1] = -10;
                points2[index + i] = x + i;
                points2[index + i + 1] = -10;
                points3[index + i] = x + i;
                points3[index + i + 1] = -10;
            }

            postInvalidate();
            // 更新频率是25Hz，每隔40ms更新一次界面。
            handler.postDelayed(this, 40);
        }
    };


    /**
     * 数据转换runnbale
     */
    private Runnable convertToList = new Runnable() {
        @Override
        public void run() {
            if (isdrawing && waveList.size() > 0) {
                for (int i = 0; i < waveList.get(0).length; i++) {
                    wave.add(waveList.get(0)[i]);
                }
                waveList.remove(0);
            }
            postDelayed(this, 200);
        }
    };


    /**
     * 获的作为标题的增益幅度
     *
     * @return
     */
    private String getECGxx() {
        float xx = GlobalConstant.ECG_XX;
        if (xx == 0.5f) {
            return "x0.5";
        } else if (xx == 1.0f) {
            return "x1";
        } else if (xx == 2.0f) {
            return "x2";
        } else if (xx == -1000f) {
            return "x1";
        } else {
            return "x1";
        }
    }

    /**
     * 获取标题出纸速度，暂时没用
     *
     * @return
     */
    private String getECGmm() {
        float mm = GlobalConstant.ECG_MM;
        if (mm == 0.2f) {
            return "5 mm/s";
        } else if (mm == 0.25f) {
            return "6.25 mm/s";
        } else if (mm == 0.4f) {
            return "10 mm/s";
        } else if (mm == 0.5f) {
            return "12.5 mm/s";
        } else if (mm == 1.0f) {
            return "25 mm/s";
        } else if (mm == 2.0f) {
            return "50 mm/s";
        } else {
            return "5 mm/s";
        }
    }

    /**
     * @param value 获取到的点值
     * @param type  增益类型
     * @return
     */
    private float getH(float value, float type) {
        float back = 0.0f;
        float mid = (float) getHeight() / 2;
        float v = value + AXIS;
        //当值在(1,-1)时，不做增益处理，因为基线有可能在这个范围
//        if (v > 1)
//        {
//            back = type * v + mid;
//        }
//        else if (v < -1)
//        {
//            back = mid - type * v;
//        }
//        else
//        {
//            back = mid - v;
//        }
        back = mid - type * v;
        return back;
    }

    /**
     * 设置到自动增益的倍数
     *
     * @param value 当前点值，非纵坐标，传来数据使用算法转换的值
     * @return 0.5 , 1.0 , 2.0
     */
    private void setAutoXX(float value) {
        value = (float) (getHeight() / 2) - value * ecgAutoX - AXIS;
//        Log.d("JustRush", "auto value = " + value);
        //计算越界点

        if (value >= getHeight() - 10 && value < 10) {
            OUTNUM++;
//            Log.d("JustRush", "OUTNUM = " + OUTNUM);

        }
        //
        if (value >= (float) (getHeight() * 1 / 4) && value < (float)
                (getHeight() * 3 / 4)) {
            LOWNUM++;
//            Log.d("JustRush", "LOWNUM = " + LOWNUM);

        }
        if ((value >= 10 && value < (float) getHeight() * 1 / 4) || (value >=
                (float) (getHeight() * 3 / 4) && value <= (float) getHeight()
                - 10)) {
            MIDNUM++;
//            Log.d("JustRush", "MIDNUM = " + MIDNUM);
        }

        //越界两点，马上减幅
        if (OUTNUM > maxErr) {
            ecgAutoX = 0.5f;
            EcgAuto = "x0.5";
        } else if (LOWNUM > 100 && MIDNUM > 1) {
            ecgAutoX = 1.0f;
            EcgAuto = "x1";
        } else if (LOWNUM > 100 && MIDNUM <= 1) {
            ecgAutoX = 2.0f;
            EcgAuto = "x2";
        } else {
            ecgAutoX = 1.0f;
            EcgAuto = "x1";
        }
    }

    /**
     * 获取到需要绘制的点集
     *
     * @param xx 增益类型
     * @return
     */
    private float[] getDrawPoint(float xx) {
        if (xx == 0.5f) {
            return points1;
        } else if (xx == 1.0f) {
            return points2;
        } else {
            return points3;
        }
    }

    /**
     * 画心电测量界面的网格
     * @param canvas 画布
     */
    private void drawEcgGrid(Canvas canvas) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xFF4c4c4c);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setAntiAlias(true);

        // 单位:像素/mm,1280为屏幕横向像素数，216.96为屏幕横向长度（单位mm）
        double pixelPerMm = 1280 / 216.96;
        int width = getWidth();
        int height = getHeight();
        int widthLen = (int) (width / pixelPerMm);//转换为mm长度
        int heightLen = (int) (height / pixelPerMm);//转换为mm长度

        float startX;
        float startY;
        float stopX;
        float stopY;

        // 画X轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startX = 0;
        stopX = width;
        for (int i = 0; i < heightLen; i++) {
            startY = (float)(i * pixelPerMm * 5);
            stopY = (float)(i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startY = 0;
        stopY = height;
        for (int i = 0; i < widthLen; i++) {
            startX = (float)(i * pixelPerMm * 5);
            stopX = (float)(i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    /**
     * 画心电测量界面的标尺
     * @param canvas 画布
     */
    private void drawEcgRuler(Canvas canvas, float gain) {
        float startX;
        float startY;
        float stopX;
        float stopY;

        float canvasWidth = (float)getWidth();
        float canvasHeight = (float)getHeight();

        // 单位:像素/mm,1280为屏幕横向像素数，216.96为屏幕横向长度（单位mm）
        double pixelPerMm = 1280 / 216.96;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.0f);

        // 标尺长度为1cm，横向上放在六分之五处，纵向上位于画布中央
        startX = canvasWidth * 5 / 6;
        startY = canvasHeight / 2 - (float) pixelPerMm * 5 * gain;
        stopX = canvasWidth * 5 / 6;
        stopY = canvasHeight / 2 + (float) pixelPerMm * 5 * gain;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}
