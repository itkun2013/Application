package com.konsung.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by DJH on 2016/7/25 0025.
 */
public class AddrProvinceBean implements Parcelable {
    private ArrayList<AddrCityBean> aearList;
    private String areaId = "";
    private String areaName = "";

    /**
     * 获取城市名集合
     *
     * @return 集合
     */
    public ArrayList<AddrCityBean> getAearList() {
        return aearList;
    }

    /**
     * 设置城市名集合
     *
     * @param aearList 集合
     */
    public void setAearList(ArrayList<AddrCityBean> aearList) {
        this.aearList = aearList;
    }

    /**
     * 获取区域id
     *
     * @return 区域id
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * 设置区域id
     *
     * @param areaId 区域id
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    /**
     * 获取地区名
     *
     * @return 地区名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 设置地区名称
     *
     * @param areaName 地区名称
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
     * 构造穷
     */
    public AddrProvinceBean() {
    }

    /**
     * 重载构造器
     *
     * @param in 数据对象
     */
    protected AddrProvinceBean(Parcel in) {
        this.aearList = in.createTypedArrayList(AddrCityBean.CREATOR);
        this.areaId = in.readString();
        this.areaName = in.readString();
    }

    public static final Parcelable.Creator<AddrProvinceBean> CREATOR = new
            Parcelable.Creator<AddrProvinceBean>() {
                @Override
                public AddrProvinceBean createFromParcel(Parcel source) {
                    return new AddrProvinceBean(source);
                }

                @Override
                public AddrProvinceBean[] newArray(int size) {
                    return new AddrProvinceBean[size];
                }
            };
}
