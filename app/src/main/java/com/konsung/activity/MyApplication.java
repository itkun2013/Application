package com.konsung.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.konsung.service.FileObserverService;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.ParamDefine.LogGlobalConstant;
import com.konsung.util.ParamDefine.NibpDefine;
import com.konsung.util.ParamDefine.RespDefine;
import com.konsung.util.ParamDefine.Spo2Define;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.File;
import java.util.ArrayList;
import upgrade.parameter.ParameterGlobal;
import static com.konsung.util.SpUtils.getSp;

/**
 * Created by JustRush on 2015/7/15.
 */
public class MyApplication extends Application {

    private static Context context;
    private ArrayList<String> infos = new ArrayList<>();
    String versionName;
    String versionCode;
    //转换倍数
    private final int multiple = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        context = getApplicationContext();
        UiUitls.initData(this);
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = UiUitls.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        if (UiUitls.isApkInDebug(this)) {
            CrashReport.initCrashReport(context, LogGlobalConstant.APPIDDEBUG, false, strategy);
        } else {
            CrashReport.initCrashReport(context, LogGlobalConstant.APPID, false, strategy);
        }
        CrashReport.setUserId(android.os.Build.SERIAL);
        createInfoLog();
        isFristStartOrNot();

        //检测参数板是否要升级，如果需要升级，则不启动app device
        boolean csbUpdate = SpUtils.getSp(this, GlobalConstant.APP_CONFIG,
                ParameterGlobal.SP_KEY_CSB_UPDATE, false);
        String filePath = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , ParameterGlobal.CSB_NAME_FLAG, GlobalConstant.csbPath);
        File file = new File(filePath);
        //判断如果file存在的情况下。升级标志置为true
        if (file.exists()) {
            csbUpdate = true;
            SpUtils.saveToSp(this, GlobalConstant.APP_CONFIG
                    , ParameterGlobal.SP_KEY_CSB_UPDATE, true);
        }
        if (!csbUpdate && !file.exists()) {
            //利用包名，启动AppDevice
            Intent mIntent = getPackageManager().getLaunchIntentForPackage("org" +
                    ".qtproject.qt5.android.bindings");
            if (mIntent != null) {
                startActivity(mIntent);
                BuglyLog.v(MyApplication.class.getName(), "Start AppDevice!");
            } else {
                BuglyLog.v(MyApplication.class.getName(), "AppDevice Start Failed!");
            }
        }
        // 初始化service
        initServer();
        //启动文件监听服务
        initFileService();
    }

    /**
     * 启动文件服务
     */
    private void initFileService() {
        Intent intent = new Intent(context, FileObserverService.class);
        startService(intent);
    }
    /**
     * 创建log文件，并在应用开启的时候记录一次配置信息
     */
    private void createInfoLog() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                versionName = pi.versionName == null ? "null" : pi.versionName;
                versionCode = pi.versionCode + "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        infos.add("版本名 ：" + versionName);
        infos.add("版本号 ：" + versionCode);
        //适配器
        int adapterVersionCode = UiUitls.getAppViersion("org.qtproject" +
                ".qt5.android.bindings");
        //当没有安装AppDevice软件时，adapterVersionCode为0
        if (adapterVersionCode != 0) {
            infos.add("适配器 ：" + String.valueOf(adapterVersionCode));
        } else {
            infos.add("适配器 ：" + "");
        }
        //数据库版本
        infos.add("数据库版本 ：" + GlobalConstant.DATABASE_VERSION + "");
        //多参模块
        String paraBoardVersion = getSp(context,
                "app_config",
                "paraBoardVersion",
                "");
        infos.add("多参模块 ：" + paraBoardVersion);
        //设备号
        infos.add("设备号 ：" + getSp(context,
                GlobalConstant.APP_CONFIG, GlobalConstant.APP_CODING, ""));
        //获取CPU占用率
        float processCpuRate = UiUitls.getProcessCpuRate();
        infos.add("本应用CPU占用率：" + processCpuRate + "%");
        //获取内存使用率
        float pecentMem = ((float) UiUitls.getmemCurrent() / (float) UiUitls
                .getmemTOTAL()) * multiple;
        infos.add("本应用内存占用率：" + pecentMem + "%");
        // 获得sd卡的内存状态
        File sdcardFileDir = Environment.getExternalStorageDirectory();
        String sdcardSize = UiUitls.getSizeInfo(sdcardFileDir);
        // 获得手机内部存储控件的状态
        File dataFileDir = Environment.getDataDirectory();
        String dataSize = UiUitls.getSizeInfo(dataFileDir);
        //磁盘使用情况
        String sizeDisk = "USB存储器: " + sdcardSize + "\n手机内部存储空间: " +
                dataSize;
        infos.add(sizeDisk);

        //读取软件升级服务器地址和端口
        String refreshIp = getSp(context, GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP, GlobalConstant.REFRESH_ADRESS);
        String refreshIpPort = getSp(context, GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT, GlobalConstant.REFRESH_ADRESS_PORT);
        infos.add("升级服务器地址 ：" + refreshIp + ":" + refreshIpPort);
        //读取服务器地址和端口
        String serverIp = GlobalConstant.IP_DEFAULT;
        String serverIpPort = getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.IP_PROT, GlobalConstant
                .PORT_DEFAULT);
        infos.add("服务器地址 ：" + serverIp + ":" + serverIpPort);
        //读取云平台服务器地址和端口
        String cloudIp = getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CLOUD_IP,
                GlobalConstant.CIP);
        String cloudIpPort = getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.CLOUD_IP_PORT, GlobalConstant
                .CIP_PORT);
        infos.add("云平台地址 ：" + cloudIp + ":" + cloudIpPort);
        saveInfoLogFile();
    }

    /**
     * 保存日志信息
     */
    private void saveInfoLogFile() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < infos.size(); i++) {
            sb.append(infos.get(i) + "\n");
        }
        BuglyLog.v(MyApplication.class.getName(), sb.toString());
    }

    /**
     * 初始化service,在Launcher中直接开启service
     */
    private void initServer() {
        ServiceUtils.bindService(this);
    }

    /**
     * 是否为第一次启动app
     */
    private void isFristStartOrNot() {
        Boolean userFirst = getSp(getApplicationContext(), "app_config", "FIRST", true);
        //第一次
        if (userFirst) {
            createDefaultSysConfig();  //创建默认的系统配置文件
            SpUtils.saveToSp(getApplicationContext(), "app_config", "FIRST", false);
            SpUtils.saveToSp(getApplicationContext(), "app_config", "ip"
                    , GlobalConstant.IP_DEFAULT);
            SpUtils.saveToSp(getApplicationContext(), "app_config", "printer_ip"
                    , GlobalConstant.PRINTER_IP);
        } else {
            GlobalConstant.ECG_XX = SpUtils.getSpFloat(getApplicationContext(), "sys_config"
                    , "xx", 1.0f);
            GlobalConstant.ECG_MM = SpUtils.getSpFloat(getApplicationContext(), "sys_config"
                    , "mm", 1.0f);
        }
    }

    /**
     * 创建默认的系统配置
     */
    private void createDefaultSysConfig() {
        //心电参数配置初始化
        // 波形速度
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "ecg_wave_speed", value);
        // 增益
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "ecg_gain", value);
        // 导联系统
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_lead_system"
                , EcgDefine.ECG_12_LEAD);
        // 计算导联
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_calc_lead"
                , EcgDefine.ECG_LEAD_II);
        // 工频干扰抑制开关
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_hum_filter_mode"
                , EcgDefine.ECG_HUM_ON);
        // 滤波模式
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_filter_mode"
                , EcgDefine.ECG_FILTER_DIAGNOSIS);
        // PACE开关
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_pace_mode"
                , EcgDefine.ECG_PACE_UNKNOW);
        // ST分析开关
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "ecg_st_analysis"
                , EcgDefine.ECG_ST_OFF);

        //血氧参数配置初始化
        // 调制音
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "spo2_pitch_tone", value);
        // 灵敏度
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "spo2_sensitivity"
                , Spo2Define.SPO2_SENSITIVITY_MIDDLE);
        // 波形速度
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "spo2_wave_speed", value);

        //血压参数配置初始化
        // 测量模式
        SpUtils.saveToSp(getApplicationContext(), "sys_config"
                , "nibp_measure_mode", NibpDefine.MANUAL_MODE);
        // 测量间隔
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "nibp_interval", value);

        //呼吸参数配置初始化
        // 呼吸导联
        SpUtils.saveToSp(getApplicationContext(), "sys_config",
                "resp_lead_type",
                RespDefine.RESP_LEAD_II);
        // 窒息报警延迟时间
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "resp_apnea_time"
                , RespDefine.RESP_APNEA_DELAY_20S);
        // 波形速度
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "resp_wave_speed", value);
        // 波形增益
//            SpUtils.saveToSp(getApplicationContext(), "sys_config",
// "resp_wave_gain", value);

        //体温参数配置初始化
        // 体温类型
        SpUtils.saveToSp(getApplicationContext(), "sys_config", "temp_type"
                , TempDefine.TEMP_INFRARED);
    }

    /**
     * 获取全局context
     * @return 上下文引用
     */
    public static Context getGlobalContext() {
        return context;
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     * @param context 上下文
     * @return true, 程序为debug版本；false,程序为release版本
     */
    private static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            Log.e("HealthOne", "Apk debugable check failed!");
        }
        return false;
    }
}
