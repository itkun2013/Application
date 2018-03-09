package com.konsung.defineview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.konsung.R;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.view.holder.BaseRefreshBean;
import com.konsung.view.holder.EcgRefreshParamBean;

import java.util.Observable;
import java.util.Observer;

/**
 * 心电电极脱落引导图,不支持N导
 * @author JustRush
 * @since 1
 */

@SuppressLint("AppCompatCustomView")
public class EcgProbeGuideView extends ImageView implements Observer {

    private static final int[] DEFAULT_COLORS = {R.color.ecg_line_red, R.color.ecg_line_yellow,
            R.color.ecg_line_greed, R.color.ecg_line_brown, R.color.ecg_line_black,
            R.color.ecg_line_purple, R.color.ecg_line_red, R.color.ecg_line_yellow,
            R.color.ecg_line_black, R.color.ecg_line_greed}; //默认颜色
    private static final float[] DEFAULT_POSITIONS = {96.5f, 171.5f, //1
            152.5f, 179, //2
            182, 192.5f, //3
            209, 207, //4
            242, 210.5f, //5
            272, 205, //6
            439, 152.5f, //R
            535.5f, 152.5f, //L
            475.5f, 275.5f, //N
            506, 275.5f  //F
    }; //默认位置

    private static final float LITTLE_CIRCLE_OFFSET = 2.5f; //小圆圈偏移量

    private static final String[] DEFAULT_TEXT = {"1", "2", "3", "4", "5", "6", "R", "L",
            "N", "F"}; //默认文本

    private static final int DEFAULT_REFRESH_TIMES = 1000; //默认刷新时间 毫秒
    private static final float DEFAULT_CIRCLE_SIZE = 15f; //默认闪烁圆圈大小
    private static final float DEFAULT_LITTLE_CIRCLE_SIZE = 10f; //默认小圆圆圈大小

    private int[] colors = DEFAULT_COLORS; //设置的颜色
    private float[] positions = DEFAULT_POSITIONS; //设置的位置

    private boolean drawing = false; //是否正在绘制

    private int signal = 0; //导联信号

    private int refreshTime = DEFAULT_REFRESH_TIMES; //刷新时间

    private Paint circlePaint; //画圆圈的画笔

    private Paint textPaint;  //写字画笔

    private Paint tipPaint; //写提示画笔

    private Paint tipBackPaint; //写提示背景画笔

    Rect textRect = new Rect(); //文本显示区域

    Bitmap tipBackground; //提示背景图片
    NinePatch ninePatch; //提示背景点九绘制路径

    Rect rectF = new Rect(); //提示背景区域

    private boolean hide = false; //脱落导联是否隐藏了

    /**
     * 相关参数配置Bean
     */
    private BaseRefreshBean refreshBean;

    Handler handler = new Handler();

    /**
     * 构造函数
     * @param context 山下文
     */
    public EcgProbeGuideView(Context context) {
        super(context);
        initPaint();
        start();
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 样式
     */
    public EcgProbeGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        start();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 初始化画笔
        textPaint = new Paint();
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(GlobalNumber.EIGHTTEEN_NUMBER);
        textPaint.setColor(getResources().getColor(R.color.ecg_guide_text));
        textPaint.setAntiAlias(true); // 设置抗锯齿

        tipPaint = new Paint();
        tipPaint.setStrokeWidth(2);
        tipPaint.setTextSize(GlobalNumber.TWENTY_FOUR_NUMBER);
        tipPaint.setColor(getResources().getColor(R.color.ecg_line_red));
        tipPaint.setAntiAlias(true); // 设置抗锯齿

        tipBackPaint = new Paint();
        tipBackPaint.setStrokeWidth(2);
        tipBackPaint.setColor(getResources().getColor(R.color.ecg_guide_text_background));
        tipBackPaint.setAntiAlias(true); // 设置抗锯齿

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true); // 设置抗锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //预防点需要描的点数量不合理
        if (positions.length / 2 > colors.length) {
            CustomToast.showMessage(UiUitls.getContent(), R.string.wrong_ecg_position);
        } else {
            for (int i = 0; i < positions.length - 1; i += 2) {
                drawPoints(i, canvas);
            }
            hide = !hide;
        }
        if (signal != 0) {
            drawTips(canvas);
        }
    }

    /**
     * 绘制图上的点
     * @param xPosition x坐标的索引
     * @param canvas 画布
     */
    private void drawPoints(int xPosition, Canvas canvas) {
//设置圆圈颜色
        circlePaint.setColor(getResources().getColor(colors[xPosition / 2]));
        //V2和L 的显示文本特殊处理，显示白色不明显

        if (xPosition == 2 || xPosition == GlobalNumber.FOURTEEN_NUMBER) {
            textPaint.setColor(getResources().getColor(R.color.ecg_guide_text_black));
        } else {
            textPaint.setColor(getResources().getColor(R.color.ecg_guide_text));
        }
        //绘制小圆
        canvas.drawCircle(positions[xPosition] + LITTLE_CIRCLE_OFFSET,
                positions[xPosition + 1] + LITTLE_CIRCLE_OFFSET,
                DEFAULT_LITTLE_CIRCLE_SIZE, circlePaint);

        //绘制大圆
        if (!getProbeStatusFormSignal(xPosition / 2, signal)) {
            canvas.drawCircle(positions[xPosition], positions[xPosition + 1], DEFAULT_CIRCLE_SIZE,
                    circlePaint);

            canvas.drawText(DEFAULT_TEXT[xPosition / 2], positions[xPosition]
                    - GlobalNumber.FIVE_NUMBER, positions[xPosition + 1]
                    + GlobalNumber.SEVEN_NUMBER, textPaint);
        } else {
            if (!hide) {
                circlePaint.setColor(getResources().getColor(colors[xPosition / 2]));
                canvas.drawCircle(positions[xPosition], positions[xPosition + 1],
                        DEFAULT_CIRCLE_SIZE, circlePaint);
                canvas.drawText(DEFAULT_TEXT[xPosition / 2], positions[xPosition]
                        - GlobalNumber.FIVE_NUMBER, positions[xPosition + 1]
                        + GlobalNumber.SEVEN_NUMBER, textPaint);
            }
        }
    }

    /**
     * 绘制提示
     * @param canvas 画布
     */
    private void drawTips(Canvas canvas) {
        //初始化背景
        initTips();
        //绘制点九背景
        ninePatch.draw(canvas, rectF);
        String text = getProbeOffString(signal);
        //获取字符串占用空间大小
        tipPaint.getTextBounds(text, 0, text.length(), textRect);

        canvas.drawText(text, getWidth() / 2 - textRect.width() / 2
                , GlobalNumber.THREE_HUNDRED_FOURTY_SEVEN_NUMBER, tipPaint);
    }

    /**
     * 初始化提示背景绘制
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initTips() {
        if (tipBackground == null) {
            rectF.left = GlobalNumber.FOUR_NUMBER;
            rectF.top = getBottom() - GlobalNumber.FIFTY_SEVEN;
            rectF.right = getWidth() - GlobalNumber.FOUR_NUMBER;
            rectF.bottom = getBottom() - GlobalNumber.THREETEEN_NUMBER;
            //绘制提示背景
            tipBackground = BitmapFactory.decodeResource(getResources()
                    , R.drawable.pic_ecg2_float_tips);
            ninePatch = new NinePatch(tipBackground, tipBackground.getNinePatchChunk());
            ninePatch.setPaint(new Paint());
        }
    }

    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (drawing) {
                invalidate();
                handler.postDelayed(this, refreshTime);
            }
        }
    };

    /**
     * 设置需要描点的位置 从R L N F - V1~6
     * @param point 描点位置
     */
    public void setPointList(float[] point) {
        this.positions = point;
    }

    /**
     * 脱落信号
     * @param signal 信号
     */
    public void sentLeadOffSignal(int signal) {
        this.signal = signal;
    }

    /**
     * 设置闪烁频率
     * @param rate 频率-每秒闪烁次数
     */
    public void setFlickerRate(int rate) {
        refreshTime = DEFAULT_REFRESH_TIMES / rate;
    }

    /**
     * 设置闪烁的颜色，从R L N F - V1~6
     * @param color 颜色值
     */
    public void setLightColors(@ColorRes int... color) {
        colors = color;
    }

    /**
     * 开始闪烁
     */
    public void start() {
        //启动绘制
        drawing = true;
        handler.post(refreshRunnable);
    }

    /**
     * 停止
     */
    public void stop() {
        drawing = false;
        handler.removeCallbacks(refreshRunnable);
    }

    /**
     * 获取View的刷新配置
     * @return 刷新配置Bean
     */
    public BaseRefreshBean getRefreshBean() {
        return refreshBean;
    }

    /**
     * 设置View的刷新配置项
     * @param refreshBean 刷新配置bean
     */
    public void setRefreshBean(BaseRefreshBean refreshBean) {
        this.refreshBean = refreshBean;
    }

    @Override
    public void update(Observable observable, Object o) {
        refreshBean = (EcgRefreshParamBean) observable;
    }

    /**
     * 从信号中获取对应位置的脱落信息
     * @param position 位置
     * @param signal 信号
     * @return 是否脱落 true - 脱落 false - 连接
     */
    private boolean getProbeStatusFormSignal(int position, int signal) {
        boolean result = false;
        switch (position) {
            case 9:
                //LL,F
                if ((signal & 0x01) == 0x01) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 7:
                //LA ,L
                if ((signal & 0x02) == 0x02) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 6:
                //RA,R
                if ((signal & 0x04) == 0x04) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 0:
                //V1
                if ((signal & 0x08) == 0x08) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 1:
                //v2
                if ((signal & 0x10) == 0x10) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 2:
                //v3
                if ((signal & 0x20) == 0x20) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 3:
                //v4
                if ((signal & 0x40) == 0x40) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 4:
                //v5
                if ((signal & 0x80) == 0x80) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            case 5:
                //v6
                if ((signal & 0x100) == 0x100) {
                    result = true;
                } else {
                    result = false;
                }
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 电极脱落显示文字
     * @param leadOff 信号
     * @return 需要显示的文字
     */
    public String getProbeOffString(int leadOff) {
        String off = "";
        if ((leadOff & 0x1) == 0x1) {
            off += UiUitls.getString(R.string.avf) + ",";
        }
        if ((leadOff & 0x2) == 0x2) {
            off += UiUitls.getString(R.string.avl) + ",";
        }
        if ((leadOff & 0x4) == 0x4) {
            off += UiUitls.getString(R.string.avr) + ",";
        }
        if ((leadOff & 0x8) == 0x8) {
            off += UiUitls.getString(R.string.V1) + ",";
        }
        if ((leadOff & 0x10) == 0x10) {
            off += UiUitls.getString(R.string.V2) + ",";
        }
        if ((leadOff & 0x20) == 0x20) {
            off += UiUitls.getString(R.string.V3) + ",";
        }
        if ((leadOff & 0x40) == 0x40) {
            off += UiUitls.getString(R.string.V4) + ",";
        }
        if ((leadOff & 0x80) == 0x80) {
            off += UiUitls.getString(R.string.V5) + ",";
        }
        if ((leadOff & 0x100) == 0x100) {
            off += UiUitls.getString(R.string.V6) + ",";
        }
        return off.substring(0, off.length() - 1) +
                UiUitls.getString(R.string.pole_off);
    }
}
