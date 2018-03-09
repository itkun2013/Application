package com.konsung.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.global.GlobalNumber;

/**
 * Created by xiangshicheng on 2017/12/22 0022.
 * 快检辅助类
 */

public class QuickCheckHelpUtils {

    private Context context = null;
    //糖化-大于标识符
    public final int maxFlag = -100;
    //糖化-小于标识符
    public final int minFlag = -10;
    //固定高度
    private final int heightNum = 225;

    /**
     * 构造器
     * @param context 上下文引用
     */
    public QuickCheckHelpUtils(Context context) {
        this.context = context;
    }

    /**
     * 赋值并判断其是否异常
     * @param tv TextView控件
     * @param min 最小参考值
     * @param max 最大参考值
     * @param type 测量项标识
     * @param value 当前测量值大小
     */
    public void fillValue(TextView tv, float min, float max, int type, float value) {
        if (tv == null) {
            return;
        }
        if (value == GlobalConstant.INVALID_DATA || value == 0) {
            tv.setText(context.getString(R.string.default_value));
            tv.setTextColor(context.getResources().getColor(R.color.mesu_text));
            return;
        }
        if (value == minFlag) {
            //超低值
            if (type == KParamType.HBA1C_NGSP) {
                tv.setText(GlobalConstant.NGSP_BELOW);
            } else if (type == KParamType.HBA1C_IFCC) {
                tv.setText(GlobalConstant.IFCC_BELOW);
            } else {
                tv.setText(GlobalConstant.EAG_BELOW);
            }
            tv.setTextColor(context.getResources().getColor(R.color.high_color));
        } else if (value == maxFlag) {
            //超高值
            if (type == KParamType.HBA1C_NGSP) {
                tv.setText(GlobalConstant.NGSP_ABOVE);
            } else if (type == KParamType.HBA1C_IFCC) {
                tv.setText(GlobalConstant.IFCC_ABOVE);
            } else {
                tv.setText(GlobalConstant.EAG_ABOVE);
            }
            tv.setTextColor(context.getResources().getColor(R.color.high_color));
        } else {
            //正常值
            tv.setText(value + "");
            if (value > max) {
                tv.setTextColor(context.getResources().getColor(R.color.high_color));
            } else if (value < min) {
                tv.setTextColor(context.getResources().getColor(R.color.high_color));
            } else {
                tv.setTextColor(context.getResources().getColor(R.color.mesu_text));
            }
        }
    }

    /**
     * 获得布局参数
     * @param isBig 不同高度模块获取的参数不同
     * @return 返回当前控件的高度
     */
    public LinearLayout.LayoutParams getLayoutParams(int isBig) {
        if (isBig == 2) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , heightNum * 2);
        } else if (isBig == 1) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightNum);
        } else {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , heightNum * GlobalNumber.THREE_NUMBER);
        }
    }

    /**
     * 血脂数据转换比较
     * @param type 类型
     * @param value 数值
     * @return 当前测量的血糖值
     */
    public String getFormatterStr(int type, float value) {
        //超低指标
        int supLow = GlobalConstant.valueMin;
        //超高指标
        int supHigh = GlobalConstant.valueMax;
        //指标数据来源于爱康血脂分析仪
        switch (type) {
            case KParamType.LIPIDS_CHOL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_CHOL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_TRIG:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_TRIG_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_HDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_HDL_ALARM_ABOVE;
                }
                break;
            case KParamType.LIPIDS_LDL:
                if (value == supLow) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_BELOW;
                } else if (value == supHigh) {
                    return GlobalConstant.LIPIDS_LDL_ALARM_ABOVE;
                }
                break;
            default:
                break;
        }
        return value + "";
    }
}
