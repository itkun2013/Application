package com.konsung.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.UserBean;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangshicheng on 2017/10/27 0027.
 * 数据库升级辅助类
 * greenDao数据库升级原理：创建临时表->删除旧表->创建新表->复制临时表数据到新表
 */

public class MigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION
            = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
    private static MigrationHelper instance;
    //每次表复制100条数据限制
    private static final int LIMIT = 100;
    //sd卡数据库文件的父目录地址
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/db";

    /**
     * 获取实例对象
     *
     * @return 类实例
     */
    public static MigrationHelper getInstance() {
        if (instance == null) {
            instance = new MigrationHelper();
        }
        return instance;
    }

    /**
     * 调用升级方法
     *
     * @param db 数据库类
     */
    public static void migrate(SQLiteDatabase db) {
        //表数据移植
        Map<String, Class> map = new HashMap<>();
        map.put("t_measuredata", MeasureDataBean.class);
        map.put("t_patient", PatientBean.class);
        map.put("t_user", UserBean.class);
        for (Map.Entry<String, Class> entry : map.entrySet()) {
            String tableName = entry.getKey();
            Class clz = entry.getValue();
            //获取新表名称
            String newTable = DBDataUtil.getDBName(clz.getSimpleName());
            //获取每张表单总数据 分页加载 防止内存溢出
            long formTotal = DBDataUtil.getAllRecord(db, tableName);
            if (formTotal <= 0) {
                continue;
            }
            //分页加载几次
            long zPart = formTotal / LIMIT;
            //整除后最后剩下的记录条数
            long xPart = formTotal % LIMIT;
            //复制次数计数器
            int count = 0;
            //数据查询起始位置
            int offSet = 0;
            if (zPart < 1) {
                //数据处理
                dataReslove(db, tableName, newTable, clz, offSet);
            } else {
                while (count < zPart) {
                    count++;
                    //数据处理
                    dataReslove(db, tableName, newTable, clz, offSet);
                    offSet += LIMIT;
                }
                //数据处理
                dataReslove(db, tableName, newTable, clz, offSet);
            }
        }
    }

    /**
     * 分页后的数据处理
     *
     * @param offSet    查询起始位置
     * @param tableName 表名
     * @param newTable  新表名
     * @param db        数据库对象
     * @param clz       类对象
     */
    private static void dataReslove(SQLiteDatabase db, String tableName, String newTable
            , Class clz, int offSet) {
        List allData;
        //低版本数据库表中数据查询
        allData = DBDataUtil.loadAllBySelect(db, tableName, clz, offSet, LIMIT);
        //依次往新表中插入数据
        for (Object obj : allData) {
            if (GlobalConstant.oldDbVersion < 7 && obj instanceof PatientBean) {
                PatientBean patientBean = new PatientBean();
                String idCard = patientBean.getIdCard();
                String card = patientBean.getCard();
                if (TextUtils.isEmpty(idCard) && (idCard.length() == 15 || idCard.length() == 18)) {
                    patientBean.setCard(idCard);
                    patientBean.setIdCard(UUIDGenerator.getUUID());
                }

            }
            DBDataUtil.insert(obj, db, newTable, true);
        }
        //数据清空
        if (allData != null) {
            allData.clear();
            allData = null;
        }
    }

    /**
     * 生成临时列表
     *
     * @param db         数据库
     * @param daoClasses dao类
     */
    private void generateTempTables(Database db
            , Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String divider = "";
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList<>();
            StringBuilder createTableStringBuilder = new StringBuilder();
            createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");
            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                if (getColumns(db, tableName).contains(columnName)) {
                    properties.add(columnName);
                    String type = null;
                    try {
                        type = getTypeByClass(daoConfig.properties[j].type);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    createTableStringBuilder.append(divider).append(columnName).append(" ")
                            .append(type);
                    if (daoConfig.properties[j].primaryKey) {
                        createTableStringBuilder.append(" PRIMARY KEY");
                    }
                    divider = ",";
                }
            }
            createTableStringBuilder.append(");");
            db.execSQL(createTableStringBuilder.toString());
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(" FROM ").append(tableName).append(";");
            db.execSQL(insertTableStringBuilder.toString());
        }
    }

    /**
     * 存储新的数据库表 以及数据
     *
     * @param db         数据库
     * @param daoClasses dao类
     */
    private void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            ArrayList<String> properties = new ArrayList();
            ArrayList<String> propertiesQuery = new ArrayList();
            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                if (getColumns(db, tempTableName).contains(columnName)) {
                    properties.add(columnName);
                    propertiesQuery.add(columnName);
                } else {
                    try {
                        if (getTypeByClass(daoConfig.properties[j].type).equals("INTEGER")) {
                            propertiesQuery.add("0 as " + columnName);
                            properties.add(columnName);
                        }
                        if (getTypeByClass(daoConfig.properties[j].type).equals("Long")) {
                            propertiesQuery.add("0L as " + columnName);
                            properties.add(columnName);
                        }
                        if (getTypeByClass(daoConfig.properties[j].type).equals("String")) {
                            propertiesQuery.add("'' as " + columnName);
                            properties.add(columnName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(TextUtils.join(",", properties));
            insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
            db.execSQL(insertTableStringBuilder.toString());
            db.execSQL(dropTableStringBuilder.toString());
        }
    }

    /**
     * 获取类型
     *
     * @param type 参数基本类型
     * @return 类型字符
     * @throws Exception 异常抛出
     */
    private String getTypeByClass(Class<?> type) throws Exception {
        if (type.equals(String.class)) {
            return "TEXT";
        }
        if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
            return "INTEGER";
        }
        if (type.equals(Boolean.class)) {
            return "BOOLEAN";
        }
        Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION
                .concat(" - Class: ").concat(type.toString()));
        exception.printStackTrace();
        throw exception;
    }

    /**
     * 获取列集合
     *
     * @param db        数据库
     * @param tableName 表名
     * @return 列集合
     */
    private List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    /**
     * 导出内存中数据库文件到sd卡指定文件夹位置
     */
    public static void saveDataToSdFile() {
        //软件内部存储数据库文件绝对路径
        String dbPath = "/data/data/" + UiUitls.getPackName() + "/databases/konsung.db";
        String dbConfigPath = "/data/data/" + UiUitls.getPackName() + "/databases/konsung" +
                ".db-journal";
        //存放数据库到sd卡的路径
        String sdCardPath = FILE_PATH + "/konsung.db";
        //数据库移植
        //返回false则标识本地数据库已经存在
        //返回true则标识本地数据库原先不存在
        GlobalConstant.isExitsDataBaseInUsbMem = copyFile(dbPath, sdCardPath);
        if (GlobalConstant.isExitsDataBaseInUsbMem) {
            //删除掉内存中的数据库文件和数据库记录配置文件
            deleteFile(new File(dbPath));
            deleteFile(new File(dbConfigPath));
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                // 设置属性:让文件可执行，可读，可写
                file.setExecutable(true, false);
                file.setReadable(true, false);
                file.setWritable(true, false);
                file.delete(); // delete()方法
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.delete();
            Log.i("deleteFile", file.getName() + "成功删除！！");
        } else {
            Log.i("deleteFile", file.getName() + "不存在！！！");
        }
    }

    /**
     * 文件复制
     *
     * @param source 源文件
     * @param dest   目标文件
     * @return 是否成功复制
     */
    private static boolean copyFile(String source, String dest) {
        File f1 = new File(source);
        //系统数据库第一次复制后会被删除，删除完后则不需要再重新复制
        if (!f1.exists()) {
            return false;
        }
        File dirFile = new File(FILE_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f2 = new File(dest);
        boolean isMustCopy = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.UPDATE_DATA, true);
        if (!isMustCopy) {
            if (f2.exists()) {
                //数据库文件本身存在则不需要复制
                return false;
            }
        }
        try {
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return false;
        }
        return true;
    }
}
