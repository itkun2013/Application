package com.konsung.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.konsung.listener.SdcardListener;

/**
 * Created by xiangshicheng on 2018/1/16 0016.
 * 该服务用于对FileObserver对象的长期引用，保证监听持续存在
 */

public class FileObserverService extends Service {

    private SdcardListener sdcardListener = null;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化sd卡内文件的监听
        sdcardListener
                = new SdcardListener(Environment.getExternalStorageDirectory() + "");
        sdcardListener.startWatching();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sdcardListener.stopWatching();
    }
}
