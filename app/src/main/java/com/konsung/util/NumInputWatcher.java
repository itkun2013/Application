package com.konsung.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.konsung.R;

/**
 * Created by lipengjie on 2016/10/9 0009.
 * 判断EditText里的数字是否合法
 */
public class NumInputWatcher implements TextWatcher, View.OnFocusChangeListener {

    private int maxLength = 5;//默认值为5个长度
    private int mDecimal;//合法的小数位长度
    private PointHelper pointHelper;// 标识输入限制为小数点前后位数限制
    private String mDecimalFormat;//不合法的小数提示语
    private String mOverLength;//不合法的小数提示语
    private onTextChangeListener listener;
    private RangeHelper rangeHelper;
    private int maxSize; //该编辑框允许复制的最多字节数

    /**
     * 传该编辑框需要保留几位小数，和最大输入长度（包含小数点）
     *
     * @param decimal   保留几位小数
     * @param maxLength 编辑框最大长度
     */
    public NumInputWatcher(int decimal, int maxLength) {
        this.maxLength = maxLength;
        mDecimal = decimal;
    }

    /**
     * 传该编辑框需要保留几位小数，和最大输入长度（包含小数点）
     *
     * @param decimal   保留几位小数
     * @param maxLength 编辑框最大长度
     * @param listener  在输入限制校验之后所做的具体内容监听器（需要复写onTextChange,onTextEmpty方法）
     */
    public NumInputWatcher(int decimal, int maxLength, onTextChangeListener listener) {
        this.maxLength = maxLength;
        mDecimal = decimal;
        markedWords(maxLength, mDecimal);
        this.listener = listener;
    }


    /**
     * 传该编辑框需要保留几位小数，和最大输入长度（包含小数点）
     *
     * @param pointHelper 小数限制的帮助类
     * @param rangeHelper 在输入限制校验之后所做的范围监听器
     */
    public NumInputWatcher(PointHelper pointHelper, RangeHelper rangeHelper) {
        this.pointHelper = pointHelper;
        this.rangeHelper = rangeHelper;

        maxSize = pointHelper.pointBefore + pointHelper.pointAfter + 1;
    }


    /**
     * 传该编辑框需要保留几位小数，和最大输入长度（包含小数点）
     *
     * @param pointHelper 小数限制的帮助类
     * @param rangeHelper 在输入限制校验之后所做的范围监听器
     */
    public NumInputWatcher(PointHelper pointHelper, RangeHelper rangeHelper
            , onTextChangeListener listener) {
        this.pointHelper = pointHelper;
        this.rangeHelper = rangeHelper;
        this.listener = listener;

        maxSize = pointHelper.pointBefore + pointHelper.pointAfter + 1;
    }

    /**
     * 传该编辑框需要保留几位小数，和最大输入长度（包含小数点）
     *
     * @param pointHelper 小数限制的帮助类
     */
    public NumInputWatcher(PointHelper pointHelper) {
        this.pointHelper = pointHelper;
        maxSize = pointHelper.pointBefore + pointHelper.pointAfter + 1;
    }

    /**
     * 传该编辑框的大小范围
     *
     * @param rangeHelper 大小限制的帮助类
     */
    public NumInputWatcher(RangeHelper rangeHelper) {
        this.rangeHelper = rangeHelper;
    }


    /**
     * 传该小数需要保留几位小数是合法的。整数可以传0
     *
     * @param decimal
     */
    public NumInputWatcher(int decimal) {
        this.mDecimal = decimal;
    }

    /**
     * 设置可输入的最大字符长度
     *
     * @param length 设置字符长度
     */
    public void setMaxLength(int length) {
        this.maxLength = length;
    }

    /**
     * 初始化提示语
     *
     * @param length
     * @param decimal
     */
    private void markedWords(int length, int decimal) {
        mOverLength = UiUitls.getContent().getString(R.string.number_overlength) +
                (length - mDecimal - 1) + UiUitls.getContent().getString(R.string.digit);
        mDecimalFormat = UiUitls.getContent().getString(R.string.retain) +
                decimal + UiUitls.getContent().getString(R.string.decimal);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        changText();
        String text = editable.toString();
        if (text.length() > 0) {

            if (text.equals("0.")) {
                return;
            }

//            if (pointHelper != null && maxSize > 0 && text.length() > maxSize) {
//                Toast.makeText(UiUitls.getContent(),
//                        UiUitls.getString(R.string.widget_input_too_long),
//                        Toast.LENGTH_SHORT).show();
//                if (editable.length() > 0) {
//                    editable.delete(editable.length() - 1, editable.length());
//                }
//                return;
//            }

            //对录入的数字进行正则匹配
//
//            String regEx = "\\^[0-9]+([\\.]{1}[0-9]+){0,1}\\$";
//            boolean result = Pattern.compile(regEx).matcher(text).matches();

            if (mDecimal != 0 && text.contains(".") && text.substring(text.indexOf(".")
                    + 1).length() > mDecimal) {
                //如果字符时合法的判断小数位数是否超长
                Toast.makeText(UiUitls.getContent(),
                        UiUitls.getString(R.string.widget_input_unreasonable),
                        Toast.LENGTH_SHORT).show();
                editable.delete(text.length() - 1, text.length());
            } else if (mDecimal != 0) {
                //是小数类型的，但是整数长度超过长度了
                if (text.length() > maxLength - mDecimal - 1 && !text.contains(".")) {
                    Toast.makeText(UiUitls.getContent(),
                            UiUitls.getString(R.string.widget_input_unreasonable),
                            Toast.LENGTH_SHORT).show();
                    editable.delete(text.length() - 1, text.length());
                } else if (text.contains(".") && text.substring(0, text.indexOf("."))
                        .length() > maxLength - mDecimal - 1) {
                    Toast.makeText(UiUitls.getContent(),
                            UiUitls.getString(R.string.widget_input_unreasonable),
                            Toast.LENGTH_SHORT).show();
                    editable.delete(text.indexOf(".") - 1, text.length());
                }
            } else if (mDecimal == 0 && pointHelper != null) {
                if (text.contains(".")) {
                    int before = text.substring(0, text.indexOf(".")).length();
                    int after = text.substring(text.indexOf(".")).length() - 1;
                    if (before > pointHelper.pointBefore) {
                        UiUitls.toast(UiUitls.getContent(), pointHelper.toastMsg);
                        return;
                    }
                    if (after > pointHelper.pointAfter) {
                        UiUitls.toast(UiUitls.getContent(), pointHelper.toastMsg);
                        editable.delete(text.length() - 1, text.length());
                        return;
                    }
                } else {
                    // ## 如果是初始化的默认值，不做处理 ##
                    if (text.equals(UiUitls.getString(R.string.default_value))) {
                        return;
                    }
                    int length = text.length();
                    if (length > pointHelper.pointBefore) {
                        UiUitls.toast(UiUitls.getContent(), pointHelper.toastMsg);
                        editable.delete(text.length() - 1, text.length());
                        return;
                    }
                }
            }

            if (listener != null) {
                listener.onTextChange(text);
            }

        } else {
            if (listener != null) {
                listener.onTextEmpty();
            }
        }
    }


    /**
     * 监听edittext焦点变化后，对edittext里的字符串进行处理
     *
     * @param view
     * @param b
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        if (view instanceof EditText) {
            Editable editable = ((EditText) view).getText();
            if (editable != null) {
                String text = editable.toString();
                if (text.equals("0.")) {
                    ((EditText) view).setText("0");
                } else {
                    ((EditText) view).setText(deleteEnd0(text));
                }

                if (rangeHelper != null) {
                    float min = rangeHelper.getMin();
                    float max = rangeHelper.getMax();
                    try {
                        float value = Float.valueOf(text);
                        if (max > 0) {
                            if (rangeHelper.isHaveMin()) {
                                if (value > max || value < min) {
                                    UiUitls.toast(UiUitls.getContent(), rangeHelper.getToastMsg());
                                    ((EditText) view).setText("");
                                    return;
                                }
                            } else {
                                if (value > max || value <= min) {
                                    UiUitls.toast(UiUitls.getContent(), rangeHelper.getToastMsg());
                                    ((EditText) view).setText("");
                                    return;
                                }
                            }

                        } else {
                            if (value < min) {
                                UiUitls.toast(UiUitls.getContent(), rangeHelper.getToastMsg());
                                ((EditText) view).setText("");
                                return;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 删除字符串最后的0和处理数据
     *
     * @param str
     * @return
     */
    public static String deleteEnd0(String str) {
        if (str.contains(".")) {
            if (str.endsWith("0")) {
                return deleteEnd0(str.substring(0, str.length() - 1));
            } else if (str.endsWith(".")) {
                return str.substring(0, str.length() - 1);
            } else {
                return str;
            }
        } else {
            return str;
        }
    }

    public interface onTextChangeListener {
        public void onTextChange(String text);

        public void onTextEmpty();
    }

    public void setMaxCopySize(int size) {
        maxSize = size;
    }


    /**
     * 改变字的输入方法
     */
    public void changText() {
    }

}
