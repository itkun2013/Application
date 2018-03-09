package com.konsung.util;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.konsung.anotation.ViewMapping;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class HandleUtils {

    /**
     * 设置单选按钮选中
     *
     * @param radioGroup
     * @param value
     */
    public static void checkRadioButton(RadioGroup radioGroup, String value) {
        RadioButton radioButton = (RadioButton) radioGroup.findViewWithTag
                (value);
        if (radioButton != null) {
            radioButton.setChecked(true);
        }
    }

    /**
     * 取RadioGroup tag值
     *
     * @param rg
     * @return
     */
    public static String getTagValue(RadioGroup rg) {
        if (rg.getCheckedRadioButtonId() == -1) return "0";
        return rg.findViewById(rg.getCheckedRadioButtonId()).getTag()
                .toString();
    }


    //初始化checkBox选中
    public static void checkBoxIsChecked(String field, String val, View
            pview, Class clz) {
        //解析字符串
        String[] vals = null;
        if (val != null && !"".equals(val)) {
            vals = val.split(",");
        }
        Field[] fields = clz.getDeclaredFields();
        for (Field f : fields) {
            if (field.equals(f.getName())) {
                int[] componentIds = f.getAnnotation(ViewMapping.class)
                        .viewIds();
                for (int componentId : componentIds) {
                    View view = pview.findViewById(componentId);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (vals != null && vals.length > 0) {
                            for (String tag : vals) {
                                if (tag.equals(checkBox.getTag())) {
                                    checkBox.setChecked(true);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 得到checkbox组的值
     *
     * @param field
     * @return
     */
    public static String getCheckBoxGroupValue(String field, View pview,
                                               Class clz) {
        Field[] fields = clz.getDeclaredFields();
        String vals = "";
        for (Field f : fields) {
            if (field.equals(f.getName())) {
                int[] componentIds = f.getAnnotation(ViewMapping.class)
                        .viewIds();
                for (int componentId : componentIds) {
                    View view = pview.findViewById(componentId);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        boolean isChecked = checkBox.isChecked();
                        if (isChecked) {
                            vals += checkBox.getTag() + ",";
                        }
                    }
                }
            }
        }

        if (vals.length() > 0) {
            vals = vals.substring(0, vals.length() - 1);
        }
        return vals;
    }


}
