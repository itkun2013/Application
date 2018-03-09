package com.konsung.bean;

import android.graphics.Bitmap;

/**
 * 视频基本信息
 * Created by DJH on 2016/10/25 0025.
 */
public class VideoInfo {

    private String name; //名称
    private String size; //大小
    private String path; //路径
    private Bitmap type; //格式
    private Bitmap thumbnail; //缩略图

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取大小
     * @return 大小
     */
    public String getSize() {
        return size;
    }

    /**
     * 设置大小
     * @param size 大小
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 获取路径
     * @return 路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置路径
     * @param path 路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取图片
     * @return 图片
     */
    public Bitmap getType() {
        return type;
    }

    /**
     * 设置图片
     * @param type 图片
     */
    public void setType(Bitmap type) {
        this.type = type;
    }

    /**
     * 获取缩略图
     * @return 缩略图
     */
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    /**
     * 设置缩略图
     * @param thumbnail 速配略图
     */
    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
