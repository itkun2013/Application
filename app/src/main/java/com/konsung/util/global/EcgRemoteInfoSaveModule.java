package com.konsung.util.global;

import com.konsung.bean.Response.EcgQueryResponse;

/**
 * Created by xiangshicheng on 2017/4/20 0020.
 */

public class EcgRemoteInfoSaveModule {
    private static EcgRemoteInfoSaveModule instance = null;
    /**
     * 获取实例
     * @return 类实例对象
     */
    public static EcgRemoteInfoSaveModule getInstance() {
        if (instance == null) {
            instance = new EcgRemoteInfoSaveModule();
        }
        return instance;
    }
    public EcgQueryResponse.RowData rowData;

    //记录是否上传成功标识
    public boolean isUploadSuccess = false;
    //判断是否退出测量界面
    public boolean isExitMeasureUi = false;
    //记录心电测量报告是否是从心电测量路径下进入
    public boolean isFromEcgMeasure = false;
}
