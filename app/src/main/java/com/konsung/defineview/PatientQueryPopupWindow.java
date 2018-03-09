package com.konsung.defineview;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.konsung.R;
import com.konsung.bean.QueryItem;
import com.konsung.util.UiUitls;
import com.konsung.view.DateButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dlx on 2017/10/25 0011.
 */

public class PatientQueryPopupWindow extends PopupWindow {

    private final Activity context;
    private final int width;
    private final int height;
    @InjectView(R.id.rb_all)
    RadioButton rbAll;
    @InjectView(R.id.rb_man)
    RadioButton rbMan;
    @InjectView(R.id.rb_woman)
    RadioButton rbWoman;
    @InjectView(R.id.rg_sex)
    RadioGroup rgSex;
    @InjectView(R.id.db_start)
    DateButton dbStart;
    @InjectView(R.id.db_end)
    DateButton dbEnd;
    @InjectView(R.id.btn_today)
    Button btnToday;
    @InjectView(R.id.btn_week)
    Button btnWeek;
    @InjectView(R.id.btn_month)
    Button btnMonth;
    @InjectView(R.id.view_shadow)
    View viewShadow;
    @InjectView(R.id.btn_clear)
    Button btnClear;
    @InjectView(R.id.btn_query)
    Button btnQuery;
    private SimpleDateFormat sdf;
    private int sex = -1; // -1全部  1男 0女   默认选择全部
    private OnQueryListener listener;
    private Date date = null;
    private Calendar calendar = null;

    /**
     * 构造函数
     * @param context 上下文引用
     * @param height 高度
     * @param width 宽度
     */
    public PatientQueryPopupWindow(Activity context, int height, int width) {
        super(context);
        this.context = context;
        this.width = width;
        this.height = height;
        initPopwindow();
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_patient_query, null);
        ButterKnife.inject(this, view);

        setContentView(view);
        setWidth(width);
        setHeight(height);

        //注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
        setBackgroundDrawable(new BitmapDrawable());
        //设置外部点击关闭ppw窗口
        setOutsideTouchable(true);
        setFocusable(true);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        sex = -1;
        dbStart.setManager(context.getFragmentManager());
        dbEnd.setManager(context.getFragmentManager());
        viewShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatientQueryPopupWindow.this.dismiss();
            }
        });
        initDataTime();
        dbEnd.setText(sdf.format(date));
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDataTime();
                calendar.setTime(date);
                dbStart.setText(sdf.format(date));
                dbEnd.setText(sdf.format(date));
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDataTime();
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, -1);
                dbStart.setText(sdf.format(calendar.getTime()));
                dbEnd.setText(sdf.format(date));
            }
        });

        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDataTime();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_WEEK, -7);
                dbStart.setText(sdf.format(calendar.getTime()));
                dbEnd.setText(sdf.format(date));
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbStart.setText("");
                dbEnd.setText(sdf.format(date));
                rgSex.check(R.id.rb_all);
                if (listener != null) {
                    listener.clearSelection();
                }
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryItem item = new QueryItem();
                item.setSex(sex);
                item.setMeasureStart(dbStart.getDate());
                item.setMeasureEnd(dbEnd.getDate());
                if (listener != null) {
                    listener.onQuery(item);
                }
            }
        });

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        sex = -1;
                        break;
                    case R.id.rb_man:
                        sex = 1;
                        break;
                    case R.id.rb_woman:
                        sex = 0;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始化时间
     */
    private void initDataTime() {
        sdf = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        date = new Date();
        calendar = Calendar.getInstance();
    }
    /**
     * 监听查询条件确定的回调
     * @param listener 监听接口
     */
    public void addOnQueryListener(OnQueryListener listener) {
        this.listener = listener;
    }

    /**
     * 筛选回调接口
     */
    public interface OnQueryListener {
        /**
         * 筛选回调
         * @param item 筛选条件
         */
        void onQuery(QueryItem item);
        /**
         * 清空条件
         */
        void clearSelection();
    }

}
