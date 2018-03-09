package com.konsung.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.io.File;

/**
 * Created by xiangshicheng on 2017/11/17 0017.
 * 接收app安装、替换和卸载的广播
 */

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getSchemeSpecificPart();
        //安装成功
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            deleteSourceFile(context, packageName);
        }
        //卸载成功
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
        }
        //替换成功
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            deleteSourceFile(context, packageName);
        }
    }

    /**
     * 删除安装完成后的源文件
     * @param context 上下文引用
     * @param packageName 应用包名
     */
    private void deleteSourceFile(Context context, String packageName) {
        String allPath = "";
        if (packageName.equals(GlobalConstant.DEVICE_PACKAGE_NAME)) {
            //appDevice
            allPath = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.DEVICE_PATH, "");
        } else if (packageName.equals(UiUitls.getPackName())) {
            //app
            allPath = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.APP_PATH, "");
        }
        if (!TextUtils.isEmpty(allPath)) {
            File file = new File(allPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
