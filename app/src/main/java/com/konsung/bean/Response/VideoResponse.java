package com.konsung.bean.Response;

import java.util.List;

/**
 * 类功能：视屏数据返回的bean
 */

public class VideoResponse {

    /**
     * code : SUCESS
     * chronicData : []
     * message :
     * count :
     */
    private String code = "";
    private List<Video> videoData;
    private String message = "";
    private int count; //集合长度

    /**
     * 获取code的值
     * @return code code值
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置code的值
     * @param code code值
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取videoData的值
     * @return videoData videoData值
     */
    public List<Video> getVideoData() {
        return videoData;
    }

    /**
     * 设置videoData的值
     * @param videoData videoData值
     */
    public void setVideoData(List<Video> videoData) {
        this.videoData = videoData;
    }

    /**
     * 获取message的值
     * @return message message值
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置message的值
     * @param message message值
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取count的值
     * @return count count值
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置count的值
     * @param count count值
     */
    public void setCount(int count) {
        this.count = count;
    }

    public static class Video {
        /**
         * 构造
         * @param title 视频名称
         * @param videoPath 路径
         */
        public Video(String title, String videoPath) {
            this.title = title;
            this.videoPath = videoPath;
        }

        private String title; //标题
        private String videoPath; //视屏完整路径

        /**
         * 获取title的值
         * @return title title值
         */
        public String getTitle() {
            return title;
        }

        /**
         * 设置title的值
         * @param title title值
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * 获取videoPath的值
         * @return videoPath videoPath值
         */
        public String getVideoPath() {
            return videoPath;
        }

        /**
         * 设置videoPath的值
         * @param videoPath videoPath值
         */
        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }
    }
}
