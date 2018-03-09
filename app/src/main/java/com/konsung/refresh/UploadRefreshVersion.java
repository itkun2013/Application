package com.konsung.refresh;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.AppVersionDto;
import com.konsung.bean.ServerBaseDto;
import com.konsung.defineview.ImportantUpdateDialog;
import com.konsung.defineview.TipsDialog;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.ParamDefine.LogGlobalConstant;
import com.konsung.util.RequestUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import upgrade.parameter.ParameterGlobal;

/**
 * Created by YYX on 2016/10/22 0022.
 * 软件更新的类
 */
public class UploadRefreshVersion {

    /**
     * 记录正在下载软件的容器
     */
    private static Map<String, Boolean> refreshing = new HashMap<>();
    //记录下载数据的方法
    private static Map<String, Integer> refreshConut = new HashMap<>();
    //device的文件名
    public static final String DEVICEPACKAGE = "org.qtproject.qt5" +
            ".android.bindings";
    private static final String DEVICENAME = UiUitls.getString(R.string
            .app_devce_name);
    private static List<String> id = new ArrayList<>(); //记录显示id

    private static List<String> files = new ArrayList<>(); //记录安装的文件

    private static Activity activity = null;

    private static Map<File, AppVersionDto> apk = new ConcurrentHashMap<>(); //记录本次更新下载完成的apk安装包

    private boolean isShowing = false; //标记升级提示框是否正在显示

    /**
     * 下载软件的方法
     * @param isShow 是否显示弹出框
     * @param activity 上下文
     */
    public void doloadVersion(boolean isShow, Activity activity) {
        try {
            String appUrl = getAppUrl();
            //获取升级ip地址
            String url = SpUtils.getSp(UiUitls.getContent(),
                    GlobalConstant.APP_CONFIG,
                    GlobalConstant.REFRESH_IP,
                    GlobalConstant.REFRESH_ADRESS);
            boolean isUrl = checkUrl(url);
            if (!isUrl) {
                if (isShow) {
                    Toast.makeText(UiUitls.getContent(), UiUitls.getString(R
                            .string.adress_no), Toast.LENGTH_SHORT).show();
                }
                BuglyLog.v(UploadRefreshVersion.class.getName(), LogGlobalConstant
                        .UPLOAD_ADRESS_ILLEGITMACY + url);
                return;
            }
            this.activity = activity;
            if (isShow) {
                UiUitls.showProgress(activity, UiUitls.getString(R.string
                        .refresh_loading));
            }
            boolean queryVersion = queryVersion(getDeviceUrl(),
                    UiUitls.getAppViersion(DEVICEPACKAGE), DEVICENAME, isShow);
            boolean isLoading = queryVersion(appUrl, UiUitls.getAppVersion(),
                    UiUitls.getAppName(), isShow);
            //参数板
            int csbVersion = UiUitls.getAppCSBVersion(UiUitls.getContent());
            boolean csbDownload = QueryCSBVersion(getAppCSBUrl(), csbVersion, ParameterGlobal
                    .CSB_NAME, isShow);

            if (isShow && !isLoading && !queryVersion && !csbDownload) {
                Toast.makeText(UiUitls.getContent(), UiUitls.getString(R.string
                        .refresh_hint), Toast.LENGTH_SHORT).show();
                UiUitls.hideProgress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            UiUitls.hideProgress();
        }
    }

    /**
     * 检查url
     * @param url url
     * @return 正确为true 不是url为false
     */

    private boolean checkUrl(String url) {
        int i = url.indexOf(".");
        try {
            Integer.valueOf(url.substring(0, i));
            boolean ipv4 = UiUitls.isIpv4(url);
            return ipv4;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 重新设置activity的方法
     * @param a 上下文
     */
    public static void setActivity(Activity a) {
        activity = a;
    }

    /**
     * 查询升级版本的方法
     * @param url 查询的地址
     * @param version 查询的版本
     * @param name 包名
     * @param isShow 是否显示对话框
     * @return 返回请假的结果
     */
    public boolean queryVersion(final String url, final int version, final
    String name, final boolean isShow) {
        if (refreshing.get(name) != null && refreshing.get(name)) {
            return false;
        }
        refreshing.put(name, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestUtils.clientGet(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, final byte[]
                            bytes) {
                        final AppVersionDto bean = JsonUtils.toEntity1(
                                new String(bytes), AppVersionDto.class);
                        if (bean == null) {
                            refreshing.put(name, false);
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShow) {
                                        BuglyLog.v(UploadRefreshVersion.class.getName(),
                                                UiUitls.getString(R.string.data_fail) + new
                                                String(bytes));
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(),
                                                UiUitls.getString(R.string.data_fail),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }
                        //当前版本与服务器版本一致
                        if (bean.getAppVersion() == version) {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    refreshing.put(name, false);
                                    if (isShow) {
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(), name + UiUitls
                                                .getString(R.string.current_is_new_version)
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }
                        //有版本升级
                        if (bean.getAppVersion() > version) {
                            final File file = creatDoLoadFile(name, bean
                                    .getAppVersion());
                            refreshConut.put(name, 0);
                            deleteOldApk(name, file);
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.hideProgress();
                                    Toast.makeText(UiUitls.getContent(),
                                            UiUitls.getString(R.string
                                            .loading_refresh) + name,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            doloadSoft(file, bean, name);
                                        }
                                    }).start();
                                }
                            });
                        } else {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShow) {
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(), name + UiUitls
                                                        .getString(R.string.upload_no_data),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            refreshConut.put(name, 0);
                            refreshing.put(name, false);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers,
                            byte[] bytes, final Throwable throwable) {
                        BuglyLog.v(UploadRefreshVersion.class.getName(), LogGlobalConstant.URL +
                                url);
                        BuglyLog.v(UploadRefreshVersion.class.getName(), LogGlobalConstant.NAME +
                                name);
                        CrashReport.postCatchedException(throwable);
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                String errorMsg = UiUitls.getString(R
                                        .string.refresh_net_fail);
                                if (throwable != null && throwable
                                        instanceof ConnectTimeoutException) {
                                    errorMsg = UiUitls.getString(R.string
                                            .refresh_net_timeout);
                                }
                                final String finalErrorMsg = errorMsg;
                                refreshing.put(name, false);
                                UiUitls.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isShow) {
                                            UiUitls.hideProgress();
                                            Toast.makeText(UiUitls.getContent(),
                                                    finalErrorMsg,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).start();
        return true;
    }

    /**
     * 查询升级版本的方法
     * @param url 查询的地址
     * @param version 查询的版本
     */
    public boolean QueryCSBVersion(final String url, final int version, final
    String name, final boolean isShow) {
        if (refreshing.get(name) != null && refreshing.get(name)) {
            return false;
        }
        refreshing.put(name, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                RequestUtils.clientGet(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[]
                            bytes) {
                        final AppVersionDto bean = JsonUtils.toEntity1(
                                new String(bytes),
                                AppVersionDto.class);
                        if (bean == null) {
                            refreshing.put(name, false);
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShow) {
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(),
                                                UiUitls.getString(R.string
                                                        .data_fail),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }
                        //当前版本与服务器版本一致
                        if (bean.getAppVersion() == version) {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    refreshing.put(name, false);
                                    if (isShow) {
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(),
                                                UiUitls.getString(R.string
                                                        .current_is_new_version)
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }
                        //有版本升级
                        if (bean.getAppVersion() > version) {
                            final File file = createCSBDoLoadFile(name, bean
                                    .getAppVersion());
                            refreshConut.put(name, 0);
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.hideProgress();
                                    Toast.makeText(UiUitls.getContent(),
                                            UiUitls.getString(R.string
                                                    .loading_refresh) + name,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            doloadSoft(file, bean, name);
                                        }
                                    }).start();
                                }
                            });
                        } else {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShow) {
                                        UiUitls.hideProgress();
                                        Toast.makeText(UiUitls.getContent(),
                                                name + UiUitls.getString(R.string
                                                        .upload_no_data),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            refreshConut.put(name, 0);
                            refreshing.put(name, false);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers,
                            byte[] bytes, final Throwable throwable) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                String errorMsg = UiUitls.getString(R
                                        .string.refresh_net_fail);
                                if (throwable != null && throwable
                                        instanceof ConnectTimeoutException) {
                                    errorMsg = UiUitls.getString(R.string
                                            .refresh_net_timeout);
                                }
                                final String finalErrorMsg = errorMsg;
                                refreshing.put(name, false);
                                UiUitls.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isShow) {
                                            UiUitls.hideProgress();
                                            Toast.makeText(UiUitls.getContent(),
                                                    finalErrorMsg,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
                Looper.loop();
            }
        }).start();
        return true;
    }

    /**
     * 下载软件的方法
     * @param file 下载的文件路径
     * @param bean 下载的数据
     * @param name 下载安装包的名字
     * @return
     */
    private void doloadSoft(File file, AppVersionDto bean, String name) {
        if (bean.getServerList().size() <= 0) {
            install(null, bean);
            refreshing.put(name, false);
            BuglyLog.v(UploadRefreshVersion.class.getName(), LogGlobalConstant.NAME + name);
            BuglyLog.v(UploadRefreshVersion.class.getName(), JsonUtils.toJsonString(bean));
            CrashReport.postCatchedException(new Throwable(LogGlobalConstant.SERVER_NO_ADDRESS));
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UiUitls.getContent(), UiUitls.getString(R.string
                            .server_no), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        //下载的地址
        String appUrl = getAppDoloadUrl(bean, refreshConut.get(name));
        //下载的进度
        float progress = 0;
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        final NotificationCompat.Builder builder = new NotificationCompat
                .Builder(UiUitls.getContent());
        builder.setContentTitle(name + UiUitls.getString(R.string.refresh_title))
                .setContentText(UiUitls.getString(R.string.loading))
                .setSmallIcon(R.drawable.ic_logo);
        // 构建提醒栏
        NotificationManager mNotifyManager = (NotificationManager) UiUitls
                .getContent().getSystemService(Context.NOTIFICATION_SERVICE);
        int id = 1;
        if (!UploadRefreshVersion.id.contains(name)) {
            UploadRefreshVersion.id.add(name);
        }
        for (int i = 0; i < UploadRefreshVersion.id.size(); i++) {
            if (UploadRefreshVersion.id.get(i).equals(name)) {
                id = i;
            }
        }
        BufferedOutputStream out = null;
        InputStream is = null;
        // 构建网络请求组件
        try {
            if (file != null && file.exists()) {
                appUrl += "&rRange=" + file.length() + "-";
                request = new Request.Builder().url(appUrl).build();
                progress += file.length();
            } else {
                appUrl += "&rRange=0-";
                request = new Request.Builder().url(appUrl).build();
            }
            //请求服务器
            Response response = client.newCall(request).execute();
            //判断是否请求成功
            if (response.isSuccessful()) {
                is = response.body().byteStream();
                //获取请求文件的长度
                long length = response.body().contentLength();
                //判断是否自动安装软件
                if (file != null && 0 == length) {
                    String md5 = UiUitls.getMd5(file);
                    if (md5.equalsIgnoreCase(bean.getFileMd5())) {
//                        Log.e("BroadCast", "文件下载完成" + bean.getFileMd5());

                        builder.setContentText(UiUitls.getString(R.string
                                .doload_fished)).setProgress(0, 0, false);
                        mNotifyManager.notify(id, builder.build());
                        mNotifyManager.cancel(id);
                        refreshing.put(name, false);
                        install(file, bean);
                    } else {
                        builder.setContentText(UiUitls.getString(R.string
                                .refresh_doload_fail))
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, builder.build());
                        mNotifyManager.cancel(id);
                        file.delete();
                        //判断是否还有备用的服务器
                        if (refreshConut.get(name) + 1 < bean.getServerList()
                                .size()) {
                            Integer integer = refreshConut.get(name);
                            refreshConut.put(name, integer + 1);
                            doloadSoft(file, bean, name);
                        } else {
                            refreshing.put(name, false);
                            install(null, bean);
                        }
                    }
                } else {
                    //下载文件的总长度
                    float cr = file != null ? file.length() + length : length;
                    // TODO 断点续传
                    out = new BufferedOutputStream(new FileOutputStream(file, true));
                    byte[] buf = new byte[1024];
                    int len = -1;

                    float incr = 0;
                    int i = 0;
                    while ((len = is.read(buf)) != -1) {
                        i++;
                        out.write(buf, 0, len);
                        out.flush();
                        progress += len;
                        incr = (progress / cr) * 100f;
                        if (i % 20 == 0) {
                            i = 0;
                            builder.setProgress(100, (int) (incr + 0.5), false);
                            builder.setContentText(UiUitls.getString(R.string
                                    .doload_dataing) + (int)
                                    (incr + 0.5) + UiUitls.getString(R.string
                                    .x_measure_unit_percent));
                            mNotifyManager.notify(id, builder.build());
                        }
                    }
                    String md5 = UiUitls.getMd5(file);
                    //判断的文件与下载的是否一致
                    if (md5.equalsIgnoreCase(bean.getFileMd5())) {
                        builder.setContentText(UiUitls.getString(R.string
                                .doload_fished))
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, builder.build());
                        mNotifyManager.cancel(id);
                        refreshing.put(name, false);
                        install(file, bean);
                    } else {
                        builder.setContentText(UiUitls.getString(R.string
                                .refresh_doload_fail))
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, builder.build());
                        mNotifyManager.cancel(id);
                        file.delete();
                        //判断是否还有备用的服务器
                        if (refreshConut.get(name) + 1 < bean.getServerList()
                                .size()) {
                            Integer integer = refreshConut.get(name);
                            refreshConut.put(name, integer + 1);
                            doloadSoft(file, bean, name);
                        } else {
                            refreshing.put(name, false);
                            install(null, bean);
                        }
                    }
                }
            } else {
                install(null, bean);
                refreshing.put(name, false);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UiUitls.getContent(), UiUitls.getString(R.string
                                .server_dolown), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            BuglyLog.v(UploadRefreshVersion.class.getName(), LogGlobalConstant.NAME + name);
            CrashReport.postCatchedException(e);
            builder.setContentText(UiUitls.getString(R.string
                    .refresh_doload_fail))
                    .setProgress(0, 0, false);
            mNotifyManager.notify(id, builder.build());
            mNotifyManager.cancel(id);
            //判断是否还有备用的服务器
            if (refreshConut.get(name) + 1 < bean.getServerList().size()) {
                Integer integer = refreshConut.get(name);
                refreshConut.put(name, integer + 1);
                doloadSoft(file, bean, name);
            } else {
                String md5 = null;
                try {
                    md5 = UiUitls.getMd5(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (!TextUtils.isEmpty(md5) && md5.equalsIgnoreCase(bean.getFileMd5())) {
                    builder.setContentText(UiUitls.getString(R.string
                            .doload_fished))
                            .setProgress(0, 0, false);
                    mNotifyManager.notify(id, builder.build());
                    mNotifyManager.cancel(id);
                    refreshing.put(name, false);
                    install(file, bean);
                } else {
                    install(null, bean);
                    refreshing.put(name, false);
                }
            }
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    /**
     * 安装镜像弹出框
     * @param mContext 上下文
     * @param file 文件
     * @param title 标题
     * @param bean 显示数据
     */
    private void showInstallImageDialog(final Context mContext, final File file, String title,
            AppVersionDto bean) {
        SpUtils.saveToSp(mContext, GlobalConstant.APP_CONFIG, ParameterGlobal.SP_KEY_CSB_UPDATE,
                true);
        ImportantUpdateDialog updateDialog = new ImportantUpdateDialog(mContext, title,
                new ImportantUpdateDialog.UpdataButtonState() {
                    @Override
                    public void getButton(Boolean pressed) {

                    }
                });
        updateDialog.show();

        updateDialog.setDesStr(bean.getAppDesc());
    }

    /**
     * 获取参数板升级URL
     * appVersion 当前版本号 版本号规则，第一位*10000，第二位*100，第三位*1
     * 例：1.2 对应版本号  10200
     * @return 参数板升级地址
     */
    public String getAppCSBUrl() {
        //申请的软件类型，
        String appDeviceType = "APP-CSB";
        //当前app的版本号
        int version = UiUitls.getAppCSBVersion(UiUitls.getContent());
        String appDeviceVersion = String.valueOf(version);
        //机器类别
        String device1Type = "YTJ";
        return getCSBVersionInfoUrl(appDeviceType,
                appDeviceVersion, device1Type);
    }

    /**
     * 获取参数版URL
     * @param appType 类型
     * @param appVersion 当前版本号 版本号规则，第一位*10000，第二位*100，第三位*1
     * 例：1.2 对应版本号  10200
     * @param deviceType app类型
     * @return URL
     */
    private String getCSBVersionInfoUrl(String appType, String
            appVersion, String deviceType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:dd:mm");
        String askTime = simpleDateFormat.format(new Date());
        String md5 = "ksytj" + askTime + "konsung";
        //验证码
        String appCheckCode = UiUitls.stringMD5(md5);
        String versionName = UiUitls.getAreaName();
        String DeviceID = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.APP_CODING, "M0000");//一体机编号
        //生成的ip
        String ip = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP,
                GlobalConstant.REFRESH_ADRESS) +
                ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT,
                GlobalConstant.REFRESH_ADRESS_PORT) +
                "/cloud/update/lastVersionV2?appType=" +
                appType + "&deviceCode=" + DeviceID + "&appVersion=" +
                appVersion + "&appArea=COMMON" + "&deviceType=" + deviceType
                + "&appCheckCode=" + appCheckCode + "&askTime=" + askTime;
        return ip;
    }

    /**
     * 计算文件路径的方法
     * @param name 文件名称
     * @param version 文件版本
     * @return
     */
    protected synchronized File createCSBDoLoadFile(String name, int version) {
        File file = null;
        //判断内存卡是否存在
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(),
                    "/Download/");
            if (!file.exists()) {
                file.mkdirs();
            }
            File updateFile = new File(file, ParameterGlobal.CSB_NAME);
            return updateFile;
        } else {
            file = new File(UiUitls.getContent().getCacheDir(), "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
            return new File(file, ParameterGlobal.CSB_NAME);
        }
    }

    /**
     * 安装软件的方法
     * @param file 软件安装的地址
     * @param bean 软件安装的信息
     */
    private void install(final File file, final AppVersionDto bean) {

        if (null == file || files.contains(file.getName())) {
            return;
        }
        if (!GlobalConstant.ACTIVITY_IS_RUNNING) {
            return;
        }
        files.add(file.getName());
        apk.put(file, bean);
        try {
            String md5 = UiUitls.getMd5(file);
            //判断的文件与下载的是否一致
            if (!md5.equalsIgnoreCase(bean.getFileMd5())) {
                files.remove(file.getName());
                apk.remove(file);
                file.delete();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            files.remove(file.getName());
            apk.remove(file);
        }

        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                if (null != file && null != activity) {
                    if (file.getName().contains("image")) {
                        showInstallImageDialog(activity, file,
                                UiUitls.getString(R.string.important_update), bean);
                    } else if (file.getName().contains("apk")) {
                        if (!isShowing) {
                            TipsDialog tips = new TipsDialog(activity, UiUitls
                                    .getString(R.string.refresh_title),
                                    new TipsDialog.UpdataButtonState() {
                                        @Override
                                        public void getButton(Boolean pressed) {
                                            if (pressed) {
                                                if (file != null) {
                                                    UiUitls.install(file);
                                                    files.remove(file.getName());
                                                    apk.remove(file);
                                                }
                                                isShowing = false;
                                                Iterator it = apk.entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry entry = (Map.Entry) it.next();
                                                    File key = (File) entry.getKey();
                                                    AppVersionDto val = (AppVersionDto)
                                                            entry.getValue();
                                                    install(key, val);
                                                }
                                            } else {
                                                files.remove(file.getName());
                                                apk.remove(file);
                                                isShowing = false;
                                                Iterator it = apk.entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry entry = (Map.Entry) it.next();
                                                    File key = (File) entry.getKey();
                                                    AppVersionDto val = (AppVersionDto)
                                                            entry.getValue();
                                                    install(key, val);
                                                }
                                            }
                                        }
                                    });
                            isShowing = true;
                            tips.show();
                            tips.setTips(bean.getAppDesc());
                            tips.isShowReversionText(true);
                            tips.setHideIvClose(true);
                            tips.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    files.remove(file.getName());
                                }
                            });
                        } else {
                            files.remove(file.getName());
                        }
                    }
                } else {
                    files.remove(file.getName());
                }
            }
        });
    }
    public void installCSBImage(Context mContext, final File file) {
        Intent it = new Intent();
    }



    /**
     * 重启
     */
    private void reboot() {

    }

    /**
     * 生成请求地址的方法
     * @param appType App类别
     * @param appVersion app的当前版本
     * @param deviceType 机器类型
     * @return 生产请求的ip地址
     */
    private String generateVersionInfoUrl(String appType, String appVersion,
            String deviceType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:dd:mm");
        String askTime = simpleDateFormat.format(new Date());
        String md5 = "ksytj" + askTime + "konsung";
        //验证码
        String appCheckCode = UiUitls.stringMD5(md5);
        String versionName = UiUitls.getAreaName();
        String deviceID = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.APP_CODING, "M0000"); //一体机编号
        //生成的ip
        String ip = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP,
                GlobalConstant.REFRESH_ADRESS) +
                ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT,
                GlobalConstant.REFRESH_ADRESS_PORT) +
                "/cloud/update/lastVersionV2?appType=" +
                appType + "&deviceCode=" + deviceID + "&appVersion=" +
                appVersion + "&appArea=" + versionName + "&deviceType=" + deviceType +
                "&appCheckCode=" + appCheckCode + "&askTime=" + askTime;

        return ip;
    }

    /**
     * 获取健康一体机软件查询的地址
     * @return 请求地址
     */
    public String getAppUrl() {
        //申请的软件类型，
        String appType = "APP";
        //当前app的版本号
        String appVersion = UiUitls.getAppVersion() + "";
        //机器类别
        String deviceType = "YTJ";
        return generateVersionInfoUrl(appType, appVersion, deviceType);
    }

    /**
     * 获取Device版本查询地址
     * @return 请求地址
     */
    public String getDeviceUrl() {
        //申请的软件类型，
        String appDeviceType = "DEVICE";
        //当前app的版本号
        String appDeviceVersion = UiUitls.getAppViersion("org.qtproject.qt5" +
                ".android.bindings") + "";
        //机器类别
        String device1Type = "YTJ";
        return getDeviceVersionInfoUrl(appDeviceType,
                appDeviceVersion, device1Type);
    }



    private String getDeviceVersionInfoUrl(String appType, String
            appVersion, String deviceType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:dd:mm");
        String askTime = simpleDateFormat.format(new Date());
        String md5 = "ksytj" + askTime + "konsung";
        //验证码
        String appCheckCode = UiUitls.stringMD5(md5);
        String versionName = UiUitls.getAreaName();
        String deviceID = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.APP_CODING, "M0000"); //一体机编号
        //生成的ip
        String ip = "http://" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP,
                GlobalConstant.REFRESH_ADRESS) +
                ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT,
                GlobalConstant.REFRESH_ADRESS_PORT) +
                "/cloud/update/lastVersionV2?appType=" +
                appType + "&deviceCode=" + deviceID + "&appVersion=" +
                appVersion + "&appArea=" + versionName + "&deviceType=" + deviceType
                + "&appCheckCode=" + appCheckCode + "&askTime=" + askTime;

        return ip;
    }

    /**
     * 生产下载地址
     * @param appId 查询返回的ID
     * @param value 剩余调用服务器的次数
     * @return 请求的地址
     */
    private String downloadURL(AppVersionDto appId, int value) {
        if (appId.getServerList().size() <= value) {
            return "";
        }
        ServerBaseDto dto = appId.getServerList().get(value);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:dd:mm");
        String askTime = simpleDateFormat.format(new Date());
        String md5 = "ksytj" + askTime + "konsung";

        //验证码
        String appCheckCode = UiUitls.stringMD5(md5);
        //生成的ip
        String ip = "http://" + dto.getServerIp() +
                ":" + dto.getServerPort() + dto.getServerUrl()
                + "?appId=" + appId.getAppId() + "&appCheckCode="
                + appCheckCode +
                "&askTime=" + askTime;
        return ip;
    }

    /**
     * 获取健康一体机软件升级下载地址
     * @param appId 返回的id
     * @param value 请求的次数
     * @return 返回下载的安装包
     */
    public String getAppDoloadUrl(AppVersionDto appId, int value) {
        return downloadURL(appId, value);
    }

    /**
     * 计算文件路径的方法
     * @param pakeName 下载的包名
     * @param version 下载版本
     * @return 文件
     */
    protected synchronized File creatDoLoadFile(String pakeName, int version) {
        File file = null;
        //判断内存卡是否存在
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(),
                    "/Android/data/" + UiUitls.getPackName() + "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
            return new File(file, pakeName + version + ".apk");
        } else {
            file = new File(UiUitls.getContent().getCacheDir(), "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
            return new File(file, pakeName + version + ".apk");
        }
    }


    /**
     * 删除旧的安装包
     * @param name 包名
     * @param srcFile 文件
     */
    public void deleteOldApk(String name, File srcFile) {
        File file = null;
        //判断内存卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(),
                    "/Android/data/" + UiUitls.getPackName() + "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            file = new File(UiUitls.getContent().getCacheDir(), "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        if (null != file && file.exists()) {
            File[] files = file.listFiles();
            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].getName().contains(name) && !files[i].getName().equals(srcFile
                        .getName())) {
                    files[i].delete();
                }
            }
        }
    }
    /**
     * 清楚缓存的方法
     */
    public static void clearFiles() {
        files.clear();
        apk.clear();
    }
}
