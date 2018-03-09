package com.konsung.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by DJH on 2016/7/25 0025.
 */
public class AddrCityBean implements Parcelable {
    private ArrayList<AddrDistrictBean> aearList;
    private String areaId = "";
    private String areaName = "";

    /**
     * 获取地址集合
     * @return 地址集合
     */
    public ArrayList<AddrDistrictBean> getAearList() {
        return aearList;
    }

    /**
     * 设置集合值
     * @param aearList 集合值
     */
    public void setAearList(ArrayList<AddrDistrictBean> aearList) {
        this.aearList = aearList;
    }

    /**
     * 获取地区id
     * @return 地区id
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * 设置地区id
     * @param areaId 地区id
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    /**
     * 获取地区名
     * @return 地区名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 设置地区名
     * @param areaName 地区名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(aearList);
        dest.writeString(this.areaId);
        dest.writeString(this.areaName);
    }

    /**
     * 默认构造器
     */
    public AddrCityBean() {
    }

    /**
     * 带参数的重载的构造器
     * @param in 对象
     */
    protected AddrCityBean(Parcel in) {
        this.aearList = in.createTypedArrayList(AddrDistrictBean.CREATOR);
        this.areaId = in.readString();
        this.areaName = in.readString();
    }

    public static final Parcelable.Creator<AddrCityBean> CREATOR = new
            Parcelable.Creator<AddrCityBean>() {
                @Override
                public AddrCityBean createFromParcel(Parcel source) {
                    return new AddrCityBean(source);
                }

                @Override
                public AddrCityBean[] newArray(int size) {
                    return new AddrCityBean[size];
                }
            };
}
