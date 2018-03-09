package com.konsung.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.greendao.dao.DaoMaster;
import com.greendao.dao.DaoSession;
import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.greendao.dao.UserBeanDao;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.StandardDatabase;


/**
 * 数据库DBHelper类
 * @author ouyangfan
 * @version 0.0.1
 */
public class DBHelper extends DaoMaster.DevOpenHelper {

    private static final String DATABASE_NAME = "konsung.db";
    private static DaoSession daoSession;

    private static DBHelper helper = new DBHelper(new DatabaseContext(UiUitls.getContent()));

    /**
     * 构造函数
     * @param context 上下文
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null);
        SQLiteDatabase db = getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        //数据库移植完毕
        GlobalConstant.dataBaseUpgradeFinish = true;
    }

    /**
     * 获取helper
     * @return helper
     */
    public static DBHelper getInstance() {
        return helper;
    }

    /**
     * 获取patientbean dao对象
     * @return patientbean dao对象
     */
    public static PatientBeanDao getPatientDao() {
        return daoSession.getPatientBeanDao();
    }

    /**
     * 获取measure dao对象
     * @return measre dao对象
     */
    public static MeasureDataBeanDao getMeasureDao() {
        return daoSession.getMeasureDataBeanDao();
    }

    /**
     * 获取userbean dao对象
     * @return user dao对象
     */
    public static UserBeanDao getUserDao() {
        return daoSession.getUserBeanDao();
    }

    /**
     * 根据class对象返回dao对象
     * @param clz class对象
     * @return dao对象
     */
    public static AbstractDao getDao(Class clz) {
        return daoSession.getDao(clz);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库升级
        //修改过后的表进行表数据移植
        Log.e("tdlx", "onUpgrade: SQLiteDatabase ===" + oldVersion + "====" + newVersion);
        GlobalConstant.oldDbVersion = oldVersion;
        //数据库 版本8之前的数据都是使用的ORMLite
        DaoMaster daoMaster = new DaoMaster(db);
        daoMaster.createAllTables(new StandardDatabase(db), false);
        MigrationHelper.migrate(db);
    }
}