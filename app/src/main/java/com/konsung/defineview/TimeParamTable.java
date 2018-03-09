package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.konsung.R;
import com.konsung.bean.Time_paramBean;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

/**
 * TODO:
 * <p/>
 * Created: JustRush
 * Time:    2015/11/16 09:01
 * ver:
 */
public class TimeParamTable extends View {
    private Time_paramBean bean;
    //绘制参数画笔
    private Paint paintName;
    //绘制线条画笔
    private Paint paintLine;
    //绘制数值画笔
    private Paint paintValue;


    public TimeParamTable(Context context, Time_paramBean bean) {
        super(context);
        this.bean = bean;
        paintName = new Paint();
        paintName.setColor(bean.getNAME_COLOR());
        paintName.setTextSize(Float.valueOf(bean.getTEXT_SIZE()).floatValue());
        paintName.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(bean.getLINE_COLOR());
        paintLine.setAntiAlias(true);

        paintValue = new Paint();
        paintValue.setColor(bean.getVALUE_COLOR());
        paintValue.setTextSize(Float.valueOf(bean.getTEXT_SIZE()).floatValue());
        paintValue.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawName(canvas);
        drawTable(canvas);
        drawValue(canvas);
    }

    //绘制表格
    private void drawTable(Canvas canvas) {
        //绘制列+边界线
        for (int i = 0; i <= bean.getROW_SIZE() + 1; i++) {
            float x1 = bean.getSCALE_X() * i + bean.getPADDING_LEFT();
            float y1 = bean.getPADDING_TOP();
            //纵轴间距*当前列+左边距
            float x2 = bean.getSCALE_X() * i + bean.getPADDING_LEFT();
            //横轴间距*（行数+1）+上边距
            float y2 = (bean.getLINE_SIZE() + 1) * bean.getSCALE_Y() + bean
                    .getPADDING_TOP();
            canvas.drawLine(x1, y1, x2, y2, paintLine);
        }
        //绘制行+边界线
        for (int i = 0; i <= bean.getLINE_SIZE() + 1; i++) {
            float x1 = bean.getPADDING_LEFT();
            //起点y=上边距+间距*当前列
            float y1 = bean.getPADDING_TOP() + bean.getSCALE_Y() * i;
            float x2 = bean.getPADDING_LEFT() + bean.getSCALE_X() * (bean
                    .getROW_SIZE() + 1);
            float y2 = bean.getPADDING_TOP() + bean.getSCALE_Y() * i;
            canvas.drawLine(x1, y1, x2, y2, paintLine);
        }
    }

    //绘制（1，1）的名称
    private void drawName(Canvas canvas) {
        float x = bean.getSCALE_X();
        float y = bean.getSCALE_Y();
        double z = Math.sqrt(x * x + y * y);
        int angle = Math.round((float) (Math.asin(y / z) / Math.PI * 180));
        //角度暂时无法计算
        //保存画布状态
        canvas.save();
        //旋转画布
        canvas.rotate(angle, bean.getPADDING_LEFT(), bean.getPADDING_TOP());
        //横轴名称
        canvas.drawText(bean.getX_NAME(), bean.getPADDING_LEFT() + bean
                .getDEVIAN_NAME_X1(), bean.getPADDING_TOP() + bean
                .getDEVIAN_NAME_Y1(), paintName);
        //纵轴名称
        canvas.drawText(bean.getY_NAME(), bean.getPADDING_LEFT() + bean
                .getDEVIAN_NAME_X2(), bean.getPADDING_TOP() + bean
                .getDEVIAN_NAME_Y2(), paintName);
        //复原画布
        canvas.restore();
        //绘制斜线
        canvas.drawLine(bean.getPADDING_LEFT(), bean.getPADDING_TOP(), bean
                .getSCALE_X() + bean.getPADDING_LEFT(), bean.getPADDING_TOP()
                + bean.getSCALE_Y(), paintLine);

        //————————————————————————绘制XY轴参数值————————————————————————————
        //绘制x轴
        for (int i = 0; i < bean.getX_VALUE().size(); i++) {
            String x_value = bean.getX_VALUE().get(i);
            if (x_value.contains(";")) {
                String[] value = x_value.split(";");

                canvas.drawText(value[0], bean.getPADDING_LEFT() + bean
                        .getSCALE_X() * (i + 1) + bean.getDEVIAN_XVALUE_X(),
                        bean.getPADDING_TOP() + bean.getDEVIAN_XVALUE_Y(),
                        paintName);
                canvas.drawText(value[1], bean.getPADDING_LEFT() + bean
                        .getSCALE_X() * (i + 1) + bean.getDEVIAN_XVALUE_X() +
                        10, bean.getPADDING_TOP() + bean.getDEVIAN_XVALUE_Y()
                        + 20, paintName);
            } else {
                canvas.drawText(x_value, bean.getPADDING_LEFT() + bean
                        .getSCALE_X() * (i + 1) + bean.getDEVIAN_XVALUE_X(),
                        bean.getPADDING_TOP() + bean.getDEVIAN_XVALUE_Y(),
                        paintName);
            }
        }
        //绘制Y轴
        for (int i = 0; i < bean.getY_VALUE().size(); i++) {
            String y_value = bean.getY_VALUE().get(i);
            if (y_value.contains(";")) { //当长度过长，模拟时间轴进行切割两行显示
                String[] value = y_value.split(";");
                canvas.drawText(value[0], bean.getPADDING_LEFT()
                                + bean.getDEVIAN_YVALUE_X(), bean.getSCALE_Y() * (i + 1)
                                + bean.getPADDING_TOP() + bean.getDEVIAN_YVALUE_Y() - 7,
                        paintName);
                canvas.drawText(value[1], bean.getPADDING_LEFT()
                                + bean.getDEVIAN_YVALUE_X(), bean.getSCALE_Y() * (i + 1) + 10
                                + bean.getPADDING_TOP() + bean.getDEVIAN_YVALUE_Y() + 5,
                        paintName);
            } else {
                canvas.drawText(y_value, bean.getPADDING_LEFT()
                                + bean.getDEVIAN_YVALUE_X(), bean.getSCALE_Y() * (i + 1)
                                + bean.getPADDING_TOP() + bean.getDEVIAN_YVALUE_Y(),
                        paintName);
            }
        }
    }

    /**
     * 绘制值
     * @param canvas
     */
    private void drawValue(Canvas canvas) {
        //x轴偏移值
        float deviantX = bean.getDEVIAN_DATA_X();
        //y轴偏移值
        float deviantY = bean.getDEVIAN_DATA_Y();
        //列
        for (int i = 0; i < bean.getData().size(); i++) {
            float x = bean.getPADDING_LEFT() + bean.getSCALE_X() * (i + 1) +
                    deviantX;
            //行
            for (int j = 0; j < bean.getData().get(i).size(); j++) {
                float y = bean.getPADDING_TOP() + bean.getSCALE_Y() * (j + 1)
                        + deviantY;
                //数据
                String data = bean.getData().get(i).get(j);
                //需要判断是否为默认值
                if (data.equals(String.valueOf(GlobalConstant.INVALID_DATA))) {
                    data = UiUitls.getContent().getResources().getString(R
                            .string.no_test);
                }
                canvas.drawText(data, x, y, paintValue);
            }
        }

    }

}
