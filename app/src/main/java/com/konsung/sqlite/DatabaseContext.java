package com.konsung.sqlite;


import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.IOException;

/**
 * Created by xiangshicheng on 2018/1/4 0004.
 * 用于支持对存储在SD卡上的数据库的访问
 */

public class DatabaseContext extends ContextWrapper {

    /**
     * 构造器
     * @param context 上下文
     */
    public DatabaseContext(Context context) {
        super(context);
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     * @param name 数据库名
     */
    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED
                .equals(android.os.Environment.getExternalStorageState());
        //如果不存在,
        if (!sdExist) {
            return null;
        } else {
            //如果存在
            //获取sd卡路径
            String dbDir = android.os.Environment.getExternalStorageDirectory().toString();
            //数据库所在目录
            dbDir += "/db";
            //数据库所在目录
            String dbPath = dbDir + "/" + name;
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                try {
                    //创建文件
                    isFileCreateSuccess = dbFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isFileCreateSuccess = true;
            }
            //返回数据库文件对象
            if (isFileCreateSuccess) {
                return dbFile;
            } else {
                return null;
            }
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param name 数据库名称
     * @param mode 模式
     * @param factory 工厂
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode
            , SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     * @param name 数据库名
     * @param mode 模式
     * @param factory 库工厂
     * @param errorHandler handler
     * @return 数据库对象
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode
            , SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}
