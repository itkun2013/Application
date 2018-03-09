package com.konsung.dowmload;

import android.content.Context;

import com.konsung.updatelib.install.Installer;
import com.konsung.updatelib.message.UpdateMessage;
import com.konsung.updatelib.update.UpdateUtils;
import com.konsung.util.UiUitls;

import java.io.File;

/**
 * 提醒Runnable
 */
public class NotifyRunnable implements Runnable {
    //更新信息
    private UpdateMessage updateMessage;
    //上下文
    private Context context;

    /**
     * 提醒升级Runnable
     * @param context 上下文
     * @param updateMessage 更新信息
     */
    public NotifyRunnable(Context context, UpdateMessage updateMessage) {
        this.updateMessage = updateMessage;
        this.context = context;
    }

    @Override
    public void run() {
        File apk = new File(UpdateUtils.getApkFilePath(
                UpdateUtils.getParentPath(context), updateMessage));
        Installer.install(context, apk);
    }
}