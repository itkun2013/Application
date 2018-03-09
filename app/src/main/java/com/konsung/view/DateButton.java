package com.konsung.view;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.android.datetimepicker.date.DatePickerDialog;
import com.konsung.R;
import com.konsung.util.UiUitls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.konsung.util.UiUitls.getString;

/**
 * Created by dlx on 2017/6/8 0008.
 * <p>
 * 点击即可弹出日期选择dialog的控件
 */

public class DateButton extends Button implements DatePickerDialog.OnDateSetListener {
    public static final String DATE_PICKER = "datePicker"; //日期dialogFragment的TAG
    private final Calendar calendar; //日历对象
    private FragmentManager manager;
    private Context context;
    private DatePickerDialog datePickerDialog; //内置日期弹出框的dialog对象

    /**
     * 构造
     * @param context context
     */
    public DateButton(Context context) {
        this(context, null);
    }

    /**
     * 构造
     * @param context context
     * @param attrs attrs
     */
    public DateButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造
     * @param context context
     * @param attrs attrs
     * @param defStyleAttr defStyleAttr
     */
    public DateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }

    /**
     * 显示日期弹出框
     */
    public void show() {
        if (manager.findFragmentByTag(DATE_PICKER) == null && datePickerDialog != null) {
            datePickerDialog.show(manager, DATE_PICKER);
        }
    }

    /**
     * 设置FragmentManager
     * @param manager manager
     */
    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        this.setText(UiUitls.getDateFormat(UiUitls.DateState.SHORT).format(calendar.getTime()));
    }

    /**
     * 获取日期控件中对应的日历对象
     * @return 日历对象
     */
    public Calendar getCalendar() {
        String text = getText().toString();
        if (!TextUtils.isEmpty(text)) {
            SimpleDateFormat sdf = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(text));
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取日期控件中对应的date对象
     * @return 日期
     */
    public Date getDate() {
        Calendar calendar = getCalendar();
        if (calendar == null) {
            return null;
        }
        return calendar.getTime();
    }

    /**
     * 当前控件显示是否为时间格式字符串
     * @return 标识
     */
    public boolean haveTimeText() {
        String text = getText().toString();
        if (text.equals(getString(R.string.patient_birthday_btn))) {
            return false;
        } else {
            return true;
        }
    }
}
