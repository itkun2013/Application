package com.konsung.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;
import java.util.List;

/** 快捷页面的bean
 * Created by YYX on 2017/10/24 0024.
 */

public class QuickBean implements Serializable , Parcelable {
    private int weight; //控件占的比例
    private View view; //控件
    private int  posi; //控件索引
    // 0表示血氧 1表示血压，2表示体温，3表示血液三项 ，4 表示尿常规11项， 5.表示尿常规14项   6表示血红蛋白
    // 7表示血脂 8表示糖化血红蛋白   9表示bmi  10表示 白细胞
    private  int type;
    //控件显示在第几个容器内
    private  int index;
    //控件显示容器内子控件索引
    private  int childIndex;
    //用来存储数据
    private List<QuickBean> quickBeen;

    /**
     * 构造方法
     * @param in 序列化数
     */
    protected QuickBean(Parcel in) {
        weight = in.readInt();
        posi = in.readInt();
        type = in.readInt();
        index = in.readInt();
        childIndex = in.readInt();
        quickBeen = in.createTypedArrayList(QuickBean.CREATOR);
    }

    /**
     * 构造方法
     */
    public QuickBean() {

    }

    public static final Creator<QuickBean> CREATOR = new Creator<QuickBean>() {
        @Override
        public QuickBean createFromParcel(Parcel in) {
            return new QuickBean(in);
        }

        @Override
        public QuickBean[] newArray(int size) {
            return new QuickBean[size];
        }
    };

    /**
     * 获取weight的值
     * @return weight weight值
     */
    public int getWeight() {
        return weight;
    }

    /**
     * 设置weight的值
     *
     * @param weight weight值
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * 获取view的值
     *
     * @return view view值
     */
    public View getView() {
        return view;
    }

    /**
     * 设置view的值
     *
     * @param view view值
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * 获取posi的值
     *
     * @return posi posi值
     */
    public int getPosi() {
        return posi;
    }

    /**
     * 设置posi的值
     *
     * @param posi posi值
     */
    public void setPosi(int posi) {
        this.posi = posi;
    }

    /**
     * 获取type的值
     *
     * @return type type值
     */
    public int getType() {
        return type;
    }

    /**
     * 设置type的值
     *
     * @param type type值
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取index的值
     *
     * @return index index值
     */
    public int getIndex() {
        return index;
    }

    /**
     * 设置index的值
     *
     * @param index index值
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 获取childIndex的值
     *
     * @return childIndex childIndex值
     */
    public int getChildIndex() {
        return childIndex;
    }

    /**
     * 设置childIndex的值
     *
     * @param childIndex childIndex值
     */
    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    /**
     * 获取quickBeen的值
     *
     * @return quickBeen quickBeen值
     */
    public List<QuickBean> getQuickBeen() {
        return quickBeen;
    }

    /**
     * 设置quickBeen的值
     *
     * @param quickBeen quickBeen值
     */
    public void setQuickBeen(List<QuickBean> quickBeen) {
        this.quickBeen = quickBeen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(weight);
        dest.writeInt(posi);
        dest.writeInt(type);
        dest.writeInt(index);
        dest.writeInt(childIndex);
        dest.writeTypedList(quickBeen);
    }
}
