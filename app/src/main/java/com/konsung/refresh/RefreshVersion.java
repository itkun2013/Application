package com.konsung.refresh;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.AppVersionDto;
import com.konsung.defineview.CustomToast;
import com.konsung.defineview.RefreshDialog;
import com.konsung.defineview.TipsDialog;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.RequestUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 更新数据的类
 */
public class RefreshVersion {
    private static final int ID = 1;
    private TipsDialog _mTips;
    private RefreshDialog mDialog;
    private Context mContext;
    private static String _mUrl;
    private NotificationManager mNotifyManager;
    //记录是否下载成功的
    private boolean isLoadSuccess = true;
    private String mDoladFileLength = "length";
    File mFileAppDevice = null;
    private String mDoladFileLengthDevice = "lengthdevice";

    private TipsDialog wifiDialog;//提示是否在wife状态下
    File mFile = null;
    //appDevice的版本
    int adapterVersionCode = UiUitls.getAppViersion("org.qtproject.qt5" +
            ".android.bindings");
    //appDevice服务器的版本
    private int appDeviceServiceVersion = adapterVersionCode;
    //appDevice服务器的返回吗
    private String appDeviceRepsonCode = "";

    /**
     * 构造方法
     */
    public RefreshVersion(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化数据
     */
    public void initData(final boolean isShowNotice) {

        //申请的软件类型，
        String appType = "APP";
        //当前app的版本号
        String appVersion = UiUitls.getAppVersion() + "";
        //机器类别
        String deviceType = "YTJ";
        _mUrl = generateVersionInfoUrl(appType, appVersion, deviceType);
        if (GlobalConstant.REFRESH_ISDOAD >= 2) {
            if (isShowNotice) {
                if(mContext!=null) {
                    CustomToast.showMessage(mContext, mContext.getString(
                            R.string.refresh_hint));
                }
            }
            return;
        }
        Log.e("p"," GlobalConstant.REFRESH_ISDOAD = "+ GlobalConstant
                .REFRESH_ISDOAD);
        // 本段检查在线升级操作是否正在执行
        if (GlobalConstant.REFRESH_ISDOAD >= 1) {
            if(mContext!=null) {
                CustomToast.showMessage(mContext, mContext.getString(R.string
                        .refresh_auto_hint));
            }
            return;
        }
        GlobalConstant.REFRESH_ISDOAD++;


        //检测是否有网络
        boolean netWorkConnected = UiUitls.isNetworkConnected(mContext.getApplicationContext());
        if (!netWorkConnected) {
            if (isShowNotice) {
                CustomToast.showMessage(mContext, mContext.getString(R.string
                        .offline_admin));
            }
            GlobalConstant.REFRESH_ISDOAD--;
            return;
        }

        /**
         * 初始化等待对话框的
         */
        if (isShowNotice) {
            UiUitls.showProgress(mContext,UiUitls.getString(R.string.loading));
        }

        //申请的软件类型，
        String appDeviceType = "DEVICE";
        //当前app的版本号
        String appDeviceVersion = adapterVersionCode + "";
        //机器类别
        String device1Type = "YTJ";
        final String deciceQueryUrl = generateVersionInfoUrl(appDeviceType,
                appDeviceVersion, device1Type);
        //查询appDevice是否有更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                RequestUtils.clientGet(deciceQueryUrl, new
                        AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers,
                                                  byte[] bytes) {
                                AppVersionDto bean = JsonUtils.toEntity1(
                                        new String(bytes),
                                        AppVersionDto.class);
                                if (bean != null) {
                                    //记录服务器的最新版本
                                    appDeviceServiceVersion = bean
                                            .getAppVersion();
                                    appDeviceRepsonCode = bean.getAppId();
                                    mFileAppDevice = creatDoLoadFile(
                                            "org.qtproject.qt5.android" +
                                                    ".bindings",
                                            appDeviceServiceVersion);

                                }
                            }

                            @Override
                            public void onFailure(int i, Header[] headers,
                                                  byte[] bytes,
                                                  Throwable throwable) {

                            }
                        });
                RequestUtils.clientGet(_mUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, final
                    byte[] bytes) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                if(isShowNotice) {
                                    UiUitls.hideProgress();
                                }
                                final AppVersionDto bean = JsonUtils.toEntity1(
                                        new String(bytes),
                                        AppVersionDto.class);
                                //判断返回数据是否有问
                                if (bean == null) {
                                    Toast.makeText(UiUitls.getContent(),
                                            UiUitls.getString(R.string
                                                    .data_fail),
                                            Toast.LENGTH_SHORT).show();
                                    flagReSet();
                                    return;
                                }
                                mFile = creatDoLoadFile(
                                        UiUitls.getPackName(),
                                        bean.getAppVersion());
                                //判断是否有版本更新
                                if (bean.getAppVersion() == UiUitls
                                        .getAppVersion()) {
                                    if (isShowNotice) {
                                        showNewest(bean);
                                    } else {
                                        flagReSet();
                                    }
                                    return;
                                }
                                if (bean.getAppVersion() > UiUitls
                                        .getAppVersion()) {
                                    _mTips = new TipsDialog(
                                            mContext,
                                            mContext.getString(
                                                    R.string.refresh_new),
                                            getUpdataState(bean)) {
                                        @Override
                                        public void btnClose() {
                                            flagReSet();
                                        }
                                    };
                                    //有最新版本
                                    _mTips.setCancelable(false);
                                    try {
                                        _mTips.show();
                                        _mTips.setSubhead(bean.getAppName());
                                        _mTips.setTips(bean.getAppDesc());
                                        _mTips.isShowReversionText(true);
                                    } catch (WindowManager.BadTokenException e) {
                                        e.printStackTrace();
                                    }
                                    return;

                                }
                                flagReSet();
                                if (isShowNotice) {
                                    Toast.makeText(UiUitls.getContent(),
                                            UiUitls.getString(R.string
                                                    .upload_no_data),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes,
                                          final Throwable throwable) {

                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                UiUitls.hideProgress();
                                if (isShowNotice) {
                                    // 本段设置链接失败信息，默认为"访问服务器失败"，
                                    // 如果返回的错误对象为链接超时，
                                    // 则标记为"访问服务器超时"
                                    String errorMsg = mContext.getString(R
                                            .string.refresh_net_fail);
                                    if (throwable != null && throwable
                                            instanceof
                                            ConnectTimeoutException) {
                                        errorMsg = mContext.getString(R.string
                                                .refresh_net_timeout);
                                    }
                                    CustomToast.showMessage(mContext, errorMsg);
                                }

                                flagReSet();
                            }
                        });

                    }
                });
                Looper.loop();
            }
        }).start();
    }

    /**
     * 获取下载的回调方法
     *
     * @param b
     * @return
     */
    @NonNull
    private TipsDialog.UpdataButtonState getUpdataState(final AppVersionDto b) {
        return new TipsDialog.UpdataButtonState() {
            @Override
            public void getButton(Boolean p) {
                if (p) {
                    //判断是非下载过文件
                    downLoadFile(b);
                } else {
                    flagReSet();
                    _mTips.dismiss();
                }
            }
        };
    }
    /**
     * 显示已经是最新版本
     */
    private void showNewest(final AppVersionDto bean) {
        UiUitls.showTitle(new UiUitls.ClickClose() {
            @Override
            public void close() {
                GlobalConstant.REFRESH_ISDOAD = 0;
            }
        },mContext, UiUitls.getString(R.string
                .upload_is), UiUitls.getString(R.string
                .is_repeate_app), new TipsDialog.UpdataButtonState() {
            @Override
            public void getButton(Boolean pressed) {
                UiUitls.hideTitil();
                if (pressed) {
                    SpUtils.saveToSp(UiUitls.getContent(),
                            GlobalConstant.APP_CONFIG,
                            mDoladFileLength, -1f);
                    SpUtils.saveToSp(UiUitls.getContent(),
                            GlobalConstant.APP_CONFIG,
                            mDoladFileLengthDevice, -1f);
                    doLoad(bean);
                    return;
                } else {
                    flagReSet();
                    return;
                }
            }
        });
    }

    /**
     * 重置下载标记的方法
     */
    private void flagReSet() {
        GlobalConstant.REFRESH_ISDOAD = 0;
    }

    /**
     * 对文件进行处理
     *
     * @param bean
     */
    private void downLoadFile(final AppVersionDto bean) {
        //判断是否下载过app升级文件，并且已经下载完毕
        if (mFile.length() > 0 && mFile.exists() && mFile.length() >= SpUtils
                .getSpFloat(UiUitls.getContent(), GlobalConstant.APP_CONFIG,
                        mDoladFileLength, -1f)) {
            _mTips.dismiss();
            UiUitls.showTitle(new UiUitls.ClickClose() {
                @Override
                public void close() {
                    GlobalConstant.REFRESH_ISDOAD = 0;
                }
            },mContext, UiUitls.getString(R.string
                    .repeate_download), UiUitls.getString(R.string
                    .is_repeate_app), new TipsDialog.UpdataButtonState() {
                @Override
                public void getButton(Boolean pressed) {
                    UiUitls.hideTitil();
                    if(pressed){
                        SpUtils.saveToSp(UiUitls.getContent(),
                                GlobalConstant.APP_CONFIG,
                                mDoladFileLength, -1f);
                        SpUtils.saveToSp(UiUitls.getContent(),
                                GlobalConstant.APP_CONFIG,
                                mDoladFileLengthDevice, -1f);
                        doLoad(bean);
                        return;
                    } else {
                        UiUitls.install(mFile);
                        if (appDeviceServiceVersion > adapterVersionCode) {
                            if (isDownLoadFile()) {
                                UiUitls.install(mFileAppDevice);
                            }
                        }
                        flagReSet();
                        return;
                    }
                }
            });
        }else {
            //在后台执行下载操作，并且升级
            if (!UiUitls.isWifiConnected()) {
                isWife(bean);
            } else {
                doLoad(bean);
            }
            _mTips.dismiss();
        }
    }


    /**
     * 判断是非有下载个文件
     *
     * @return
     */
    private boolean isDownLoadFile() {
        return  mFileAppDevice.length() > 0 && mFileAppDevice != null &&
                mFileAppDevice.exists() && mFileAppDevice.length() >=
                SpUtils.getSpFloat(UiUitls.getContent(),
                        GlobalConstant.APP_CONFIG,
                        mDoladFileLengthDevice, 0f);
    }

    /**
     * 生成请求地址的方法
     *
     * @param appType    App类别
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
        //生成的ip
        String ip = "http://" + SpUtils.getSp(mContext,
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP,
                GlobalConstant.REFRESH_ADRESS) +
                ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT,
                GlobalConstant.REFRESH_ADRESS_PORT) +
                "/cloud/update/lastVersion?appType=" +
                appType + "&appVersion=" +
                appVersion + "&appArea="+versionName+"&deviceType=" + deviceType +
                "&appCheckCode=" + appCheckCode + "&askTime=" + askTime;

        return ip;
    }

    public void isWife(final AppVersionDto bean) {
        wifiDialog = new TipsDialog(mContext, "网络提醒", new TipsDialog
                .UpdataButtonState() {
            @Override
            public void getButton(Boolean pressed) {
                if (pressed) {
                    wifiDialog.dismiss();
                    doLoad(bean);
                } else {
                    flagReSet();
                    wifiDialog.dismiss();
                }
            }
        }

        ) {
            @Override
            public void btnClose() {
                flagReSet();
            }
        };
        wifiDialog.show();
        wifiDialog.setTips("现在是2G/3G/4G 网络 确定要下载");
    }

    float progress = 0;
    float cr = 0;

    /**
     * 执行后台下载的方法
     */
    boolean isDolaodSuccess = true;

    /**
     * 执行下载动作
     *
     * @param bean
     */
    private synchronized void doLoad(AppVersionDto bean) {
        GlobalConstant.REFRESH_ISDOAD++;
        progress = 0;
        cr = 0;

        // 构建提醒栏
        mNotifyManager = (NotificationManager) mContext.getSystemService
                (Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat
                .Builder(mContext);
        builder.setContentTitle("健康一体机软件升级下载")
                .setContentText("正在下载中......")
                .setSmallIcon(R.drawable.ic_logo);

        // 判断appdevice有没有更新
        if (appDeviceServiceVersion > adapterVersionCode) {
            //执行appDevice的更新操作
            new Thread(new AppDeviceLoad()).start();
        } else {
            flag++;
            devicefile = null;
            installApp();
        }

        //服务器返回的id
        final String appId = bean.getAppId();


        //开启子线程下载升级软件
        new Thread(new Runnable() {
            @Override
            public void run() {
                //下载app的url地址
                String appUrl = downloadURL("downloadAppFtp", appId);
                boolean frist = true;
                OkHttpClient client = new OkHttpClient();
                Request request = null;

                // 构建网络请求组件
                if (mFile != null && mFile.exists() && mFile.length()
                        < SpUtils.getSpFloat(UiUitls.getContent(),
                        GlobalConstant.APP_CONFIG, mDoladFileLength, -1f)) {
                    frist = false;
                    appUrl += "&rRange=" + mFile.length() + "-";
                    request = new Request.Builder().url(appUrl).build();
                    progress += mFile.length();
                } else {
                    frist = true;
                    appUrl += "&rRange=0-";
                    request = new Request.Builder().url(appUrl).build();
                }

                BufferedOutputStream out = null;
                InputStream is = null;
                float length = 0;
                // 断点续传
                float start = mFile.length();
                try {
                    Response response = client.newCall(request)
                            .execute();
                    //数据访问成功
                    if (response.isSuccessful()) {
                        is = response.body().byteStream();
                        //获取请求文件的长度
                        length = response.body().contentLength();
                        //获取一个下载的文件路径
                        cr += length + start;
                        //判断是否第一次下载，如果是第一次下载，就保存下载的长度
                        if (frist) {
                            SpUtils.saveToSp(UiUitls.getContent(),
                                    GlobalConstant.APP_CONFIG,
                                    mDoladFileLength,
                                    length);
                        }
                        // TODO 断点续传
                        out = new BufferedOutputStream(new FileOutputStream
                                (mFile, true));
                        byte[] buf = new byte[1024];
                        int len = -1;

                        float incr = 0;
                        while ((len = is.read(buf)) != -1) {
                            out.write(buf, 0, len);
                            out.flush();
                            progress += len;
                            incr = (progress / cr) * 100f;
                            builder.setProgress(100, (int) (incr + 0.5), false);
                            builder.setContentText("下载了" + (int) (incr + 0.5)
                                    + "%");
                            mNotifyManager.notify(ID, builder.build());
                        }

                    }
                } catch (IOException e) {
                    builder.setContentText("下载失败").setProgress(0, 0, false);
                    mNotifyManager.notify(ID, builder.build());
                    isDolaodSuccess = false;
                    e.printStackTrace();
                } finally {
                    flag++;
                    UiUitls.close(is);
                    UiUitls.close(out);
                    flagReSet();

                }
                if (isDolaodSuccess) {
                    builder.setContentText("下载完成").setProgress(0, 0, false);
                    mNotifyManager.notify(ID, builder.build());
                }

                //判断是否完整下载
                if (mFile != null && length != 0 && length + start == mFile
                        .length() && isLoadSuccess) {
                    appfile = mFile;
                    mNotifyManager.cancel(ID);
                    installApp();
                } else {
                    isLoadSuccess = false;
                    showDoladFail();
                }

            }
        }).start();
    }

    /**
     * 生成下载地址
     *
     * @param appId 查询返回的ID
     * @return ip
     */
    private String downloadURL(String interfaceName, String appId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:dd:mm");
        String askTime = simpleDateFormat.format(new Date());
        String md5 = "ksytj" + askTime + "konsung";

        //验证码
        String appCheckCode = UiUitls.stringMD5(md5);
        //生成的ip
        String ip = "http://" + SpUtils.getSp(mContext,
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP,
                GlobalConstant.REFRESH_ADRESS) +
                ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.REFRESH_IP_PORT,
                GlobalConstant.REFRESH_ADRESS_PORT) + "/cloud/update/" +
                interfaceName + "?appId=" +
                appId + "&appCheckCode=" + appCheckCode + "&askTime=" + askTime;
        return ip;
    }

    private void showDoladFail() {
        if (flag == 2) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                            mContext.getString(R.string.refresh_doload_fail),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
            isLoadSuccess = true;
            mNotifyManager.cancel(ID);
        }

    }

    //用来记录应用的调用
    private int flag = 0;
    private File appfile;
    private File devicefile;

    /**
     * 安装应用的软件
     */
    private void installApp() {
        if (flag == 2) {
            mNotifyManager.cancel(ID);
            if (appfile != null) {
                UiUitls.install(appfile);
            }
            if (devicefile != null) {
                UiUitls.install(devicefile);
            }
            flag = 0;

        }
    }

    /**
     * 计算路径的方法
     *
     * @return
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
            file = new File(mContext.getCacheDir(), "/doLoad");
            if (!file.exists()) {
                file.mkdirs();
            }
            return new File(file, pakeName + version + ".apk");
        }

    }

    /**
     * 下载AppDevice的类
     */
    private class AppDeviceLoad implements Runnable {
        private boolean mFrist = true;

        @Override
        public void run() {
            if (mFileAppDevice != null && mFileAppDevice.length() == SpUtils
                    .getSpFloat(UiUitls.getContent(),
                            GlobalConstant.APP_CONFIG,
                            mDoladFileLengthDevice, -1f)) {
                cr += mFileAppDevice.length();
                progress += mFileAppDevice.length();
                flag++;
                devicefile = mFileAppDevice;
                installApp();
                return;

            }

            // 构建请求字符串
            String appDeviceUrl = downloadURL("downloadAppFtp",
                    appDeviceRepsonCode);
            OkHttpClient client = new OkHttpClient();
            Request request = null;
            //TODO：断点续传功能未实现
            if (mFileAppDevice != null && mFileAppDevice.exists() &&
                    mFileAppDevice.length() < SpUtils.getSpFloat(
                            UiUitls.getContent(),
                            GlobalConstant.APP_CONFIG,
                            mDoladFileLengthDevice, -1f)) {
                mFrist = false;
                appDeviceUrl += "&rRange=" + mFileAppDevice.length() + "-";
                // 拼接请求字符串

                request = new Request.Builder().url(appDeviceUrl).build();
                progress += mFileAppDevice.length();
            } else {
                appDeviceUrl += "&rRange=0-"; // 拼接请求字符串

                request = new Request.Builder().url(appDeviceUrl).build();
            }
            BufferedOutputStream out = null;
            InputStream is = null;
            float length = 0;
            //TODO 断点续传
            float start = mFileAppDevice.length();
            try {
                com.squareup.okhttp.Response response = client.newCall(request)
                        .execute();
                //数据访问成功
                if (response.isSuccessful()) {
                    is = response.body().byteStream();
                    //获取请求文件的长度
                    length = response.body().contentLength();
                    cr += length + start;
                    //判断是不是第一次下载，如果是第一次就保存总的文件大小
                    if (mFrist) {
                        SpUtils.saveToSp(UiUitls.getContent(),
                                GlobalConstant.APP_CONFIG,
                                mDoladFileLengthDevice,
                                length);
                    }
                    mFileAppDevice = creatDoLoadFile("org.qtproject.qt5" +
                                    ".android.bindings",
                            appDeviceServiceVersion);
                    //TODO:断点续传
                    out = new BufferedOutputStream(new FileOutputStream
                            (mFileAppDevice, true));
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                        out.flush();
                        progress += len;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                flag++;
                UiUitls.close(is);
                UiUitls.close(out);
            }

            if (mFileAppDevice != null && length != 0 && length + start ==
                    mFileAppDevice.length() && isLoadSuccess) {
                devicefile = mFileAppDevice;
                installApp();
            } else {
                isLoadSuccess = false;
                showDoladFail();
            }

        }
    }
}
