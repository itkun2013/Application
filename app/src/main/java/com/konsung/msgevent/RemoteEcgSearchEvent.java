package com.konsung.msgevent;

/**
 * Created by xiangshicheng on 2017/7/17 0017.
 * 用于EventBus事件传递类
 * 远程心电搜索处用
 */

public class RemoteEcgSearchEvent {
    //查询的心电状态
    private String ecgState = "";
    /**
     * 构造方法
     * @param ecgState 心电状态
     */
    public RemoteEcgSearchEvent (String ecgState) {
        this.ecgState = ecgState;
    }

    public String getEcgState() {
        return ecgState;
    }

    public void setEcgState(String ecgState) {
        this.ecgState = ecgState;
    }
}
