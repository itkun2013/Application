package com.konsung.msgevent;

/**
 * Created by xiangshicheng on 2017/10/12 0012.
 * EventBus数据传递对象类
 */

public class EventBusUseEvent {
    private String flag = "";
    private boolean isCanAutoUpload = true;
    /**
     * 构造函数
     * @param flag flag值
     */
    public EventBusUseEvent(String flag) {
        this.flag = flag;
    }

    /**
     * 获取flag值
     * @return flag值
     */
    public String getFlag() {
        return flag;
    }

    /**
     * 设置flag值
     * @param flag flag值
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * 自动上传开关值获取
     * @return 自动上传开关值
     */
    public boolean isCanAutoUpload() {
        return isCanAutoUpload;
    }

    /**
     * 设置自动上传开关值
     * @param canAutoUpload 自动上传开关值
     */
    public void setCanAutoUpload(boolean canAutoUpload) {
        isCanAutoUpload = canAutoUpload;
    }
}
