package com.konsung.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJH on 2016/7/25 0025.
 */
public class AddrDistrictBean implements Parcelable {
    private String areaId = "";
    private String areaName = "";

    /**
     * 获取区域名
     * @return 区域名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 设置区域名
     * @param areaName 区域名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 获取区域id
     * @return 区域id
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * 设置区域id
     * @param areaId 区域id
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.areaId);
        dest.writeString(this.areaName);
    }

    /**
     * 默认构造器
     */
    public AddrDistrictBean() {}

    /**
     * 构造器重载
     * @param in 数据对象
     */
    protected AddrDistrictBean(Parcel in) {
        this.areaId = in.readString();
        this.areaName = in.readString();
    }

    public static final Parcelable.Creator<AddrDistrictBean> CREATOR = new
            Parcelable.Creator<AddrDistrictBean>() {
                @Override
                public AddrDistrictBean createFromParcel(Parcel source) {
                    return new AddrDistrictBean(source);
                }

                @Override
                public AddrDistrictBean[] newArray(int size) {
                    return new AddrDistrictBean[size];
                }
            };
}
