package com.konsung.listener;


import android.os.FileObserver;
import com.konsung.sqlite.MigrationHelper;
import com.konsung.util.ActivityUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.io.File;

/**
 * Created by xiangshicheng on 2018/1/15 0015.
 * 用于对文件的操作后的监听(创建、删除等)
 * 要在服务中一直保存FileObserver的引用，该监听才会持续生效
 */

public class SdcardListener extends FileObserver {

    /**
     * 默认构造方法 默认监听所有事件
     * @param path sd卡路径
     */
    public SdcardListener(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
        int eventTemp = event & FileObserver.ALL_EVENTS;
        switch (eventTemp) {
            //文件被删除
            case FileObserver.DELETE:
                //判断是否为数据文件被删除，若数据文件被删除则重新建立在sd卡内部的数据库
                String filePath = MigrationHelper.FILE_PATH;
                File file = new File(filePath);
                if (!file.exists()) {
                    SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                            , GlobalConstant.UPDATE_DATA, true);
                    //数据库被删除后当前用户记录清空
                    SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                            , GlobalConstant.ID_CARD, "");
                    SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                            , GlobalConstant.MEMBER_CARD, "");
                    ActivityUtils.getActivityUtils().exitApp();
                }
                break;
            case FileObserver.CREATE:
                break;
            default:
                break;
        }
    }
}
