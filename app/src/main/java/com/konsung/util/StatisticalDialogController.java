package com.konsung.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.LineChatBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.defineview.StatisticalDataDialog;
import com.konsung.defineview.StatisticalPic;
import com.konsung.view.holder.StatisticalTableHolder;
import com.konusng.adapter.StatisticalDataAdapter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 趋势图表工具
 **/

public class StatisticalDialogController {

    StatisticalDataDialog dialog;

    /**
     * 显示趋势图趋势表控制者
     * 注：血糖趋势图表奇葩，无法通过UiUitls.getParamString(int param) 获取对应字符串显示
     * @param context 上下文
     * @param tableParam 趋势表参数数组
     * @param picBeans 趋势图显示参数Bean数组
     * @param isDrawRight 趋势图是否显示右边列
     */
    public StatisticalDialogController(Context context, List<Integer> tableParam,
            StatisticalPicBean[] picBeans, boolean isDrawRight) {
        dialog = getTrendDialog(context, tableParam, picBeans, isDrawRight);
    }

    /**
     * 仅有趋势表的控制者
     * @param context 上下文
     * @param tableParam 趋势表参数
     */
    public StatisticalDialogController(Context context, List<Integer> tableParam) {
        dialog = getTrendDialog(context, tableParam);
    }

    /**
     * 显示窗口
     */
    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 隐藏窗口
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 显示趋势图趋势表方法
     * 注：血糖趋势图表奇葩，无法通过UiUitls.getParamString(int param) 获取对应字符串显示
     * @param context 上下文
     * @param tableParam 趋势表参数数组
     * @param picBeans 趋势图显示参数Bean数组
     * @param isDrawRight 趋势图是否显示右边列
     * @return 趋势图趋势表弹出框
     */
    private StatisticalDataDialog getTrendDialog(Context context, List<Integer> tableParam,
            StatisticalPicBean[] picBeans, boolean isDrawRight) {
        List<View> list = new ArrayList<>();

        list.add(getTableView(context, tableParam)); //添加趋势表控件
        //检测是否需要加入趋势图
        if (picBeans.length > 0) {
            list.add(getPicView(context, picBeans, isDrawRight)); //添加趋势图控件
        }
        StatisticalDataDialog dialog = new StatisticalDataDialog(context, list);

        dialog.show();
        return dialog;
    }

    /**
     * 仅有趋势表的Dialog
     * @param context 上下文
     * @param tableParam 趋势表参数
     * @return 趋势表弹出框
     */
    private StatisticalDataDialog getTrendDialog(Context context, List<Integer> tableParam) {
        List<View> list = new ArrayList<>();
        list.add(getTableView(context, tableParam)); //添加趋势表控件
        StatisticalDataDialog dataDialog = new StatisticalDataDialog(context, list);
        dataDialog.show();
        return dataDialog;
    }

    /**
     * 获取趋势图
     * @param context 上下文
     * @param picBeans 趋势表参数数组
     * @param isDrawRight 趋势图是否显示右边列
     * @return 趋势图View
     */
    private View getPicView(Context context, StatisticalPicBean[] picBeans, boolean
            isDrawRight) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout pic = (LinearLayout) inflater.inflate(R.layout.linear_layout2, null);
        //标识
        LinearLayout noticeLL = (LinearLayout) pic.findViewById(R.id.ll_notice);
        noticeLL.setVisibility(View.VISIBLE);

        for (int j = 0; j < picBeans.length; j++) {

            TextView text = (TextView) noticeLL.getChildAt(j);
            text.setVisibility(View.VISIBLE);
            text.setText(UiUitls.getParamString(picBeans[j].getParameter()));
        }
        LinearLayout contentLL = (LinearLayout) pic.findViewById(R.id.ll_content);
        contentLL.removeAllViews();
        contentLL.addView(getStatisticalPic(isDrawRight, picBeans));
        return pic;
    }

    /**
     * 获取趋势表
     * @param context 上下文
     * @param tableParam 需要加入趋势表的参数
     * @return 趋势表View
     */
    private View getTableView(Context context, List<Integer> tableParam) {
        LayoutInflater inflater = LayoutInflater.from(context);
        StatisticalTableHolder holder = new StatisticalTableHolder(
                inflater.inflate(R.layout.linear_layout, null));

        //在标题中显示参数名称
        int i = 0;
        for (Integer param : tableParam) {
            holder.titles.get(i).setVisibility(View.VISIBLE);
            if (param == KParamType.BLOODGLU_BEFORE_MEAL) {
                holder.titles.get(i).setText(R.string.glu);
            } else {
                holder.titles.get(i).setText(UiUitls.getParamString(param));
            }
            i++;
        }
        String idcard = ProviderReader.readPatient().getIdCard();
        StatisticalDataAdapter statisticalTableAdapter = getStatisticalTableAdapter(idcard,
                tableParam);
        holder.lvStatistical.setAdapter(statisticalTableAdapter);
        return holder.view;
    }

    /**
     * 获取趋势图
     * @param picBean 绘制趋势图需要的数据集
     * @param isDrawRight 是否绘制右刻度尺
     * @return 趋势图
     */
    private View getStatisticalPic(boolean isDrawRight, StatisticalPicBean[] picBean) {
        //构建绘制趋势图数据
        LineChatBean[] beanArray = new LineChatBean[picBean.length];
        for (int i = 0; i < picBean.length; i++) {
            beanArray[i] = getLineChatBean(picBean[i]);
        }
        //构建趋势图
        StatisticalPic chart = new StatisticalPic(UiUitls.getContent(), beanArray, isDrawRight);
        //布局宽度 = 单位长度 * x轴容量 + 左边距 + 右边距
        int width = (int) (beanArray[0].getScaleX() * beanArray[0].getXValues().length +
                beanArray[0].getPaddingLeft() + beanArray[0]
                .getPaddingRight());
        int height = (int) (beanArray[0].getScaleY() * beanArray[0].getYSize() + beanArray[0]
                .getPaddingLeft() + beanArray[0].getPaddingRight());
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(width,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = height;
        params.width = width;
        return chart;
    }

    /**
     * 获取趋势图的数据集
     * @param picBean 定义趋势图的数据源
     * @return LineChatBean
     */
    private LineChatBean getLineChatBean(StatisticalPicBean picBean) {

        SimpleDateFormat sdf = UiUitls.getDateFormat(UiUitls.DateState.STATISTICAL);
        List<MeasureDataBean> measures = null;
        try {
            measures = ProviderReader.readMeasureData(picBean.getIdCard());
        } catch (SQLException e) {
            e.printStackTrace();
            measures = new ArrayList<>();
        }
        Collections.reverse(measures);

        final List<MeasureDataBean> measure = measures;

        //缓存数据
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<Float> dataList = new ArrayList<>();
        for (int i = measure.size() - 1; i >= 0; i--) {
            MeasureDataBean measureDataBean = measure.get(i);
            int value = measureDataBean.getTrendValue(picBean.getParameter());
            dataList.add(Float.valueOf(value));
            timeList.add(sdf.format(measureDataBean.getMeasureTime()));
        }
        //把List转float[]
        float[] values = new float[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            values[i] = dataList.get(i).floatValue();
        }
        //把timeList转String[]
        String[] times = timeList.toArray(new String[0]);

        //绘线描点数据集
        ArrayList<float[]> data = new ArrayList<>();
        data.add(values);

        LineChatBean bean = new LineChatBean();

        //设置X轴
        bean.setXValues(times);
        //设置Y轴刻度数量
        bean.setYSize(picBean.getySize());
        bean.setUnit(picBean.getUnit());
        //设置绘图数据
        bean.setValues(data);
        //设置最大最小值，绘图根据最大最小值和刻度数量设置Y轴单位刻度
        bean.setMaxValue(picBean.getMaxValue());
        bean.setMinValue(picBean.getMinValue());

        //获得数据点数
        int lengthSize = bean.getXValues().length;
        //默认最大长度为100,布局容量
        if (bean.getXValues().length > bean.getMaxSize()) {
            lengthSize = bean.getMaxSize();
        } else if (bean.getXValues().length < 10) {
            lengthSize = 10;
        }
        bean.setParam(picBean.getParameter());
        bean.setSize(lengthSize);
        return bean;
    }

    /**
     * 获取趋势表的适配器
     * @param idcard 身份证
     * @param paramList 测量数据对应的参数key值
     * @return 适配器
     */
    private StatisticalDataAdapter getStatisticalTableAdapter(String idcard, List<Integer>
            paramList) {
        List<MeasureDataBean> measures = null;
        try {
            measures = ProviderReader.readMeasureData(idcard);
        } catch (SQLException e) {
            e.printStackTrace();
            measures = new ArrayList<>();
        }
        return new StatisticalDataAdapter(UiUitls.getContent(), measures, paramList);
    }
}
