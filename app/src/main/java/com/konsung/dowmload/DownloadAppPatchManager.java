package com.konsung.dowmload;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.konsung.R;
import com.konsung.activity.BaseActivity;
import com.konsung.defineview.ImportantUpdateDialog;
import com.konsung.defineview.NoticeDialog;
import com.konsung.updatelib.install.InstallListener;
import com.konsung.updatelib.install.InstallManager;
import com.konsung.updatelib.message.App;
import com.konsung.updatelib.message.AppUpdate;
import com.konsung.updatelib.message.RequestMessage;
import com.konsung.updatelib.message.ResponseMessage;
import com.konsung.updatelib.message.UpdateMessage;
import com.konsung.updatelib.message.value.Progress;
import com.konsung.updatelib.update.MD5;
import com.konsung.updatelib.update.OnDownloadListener;
import com.konsung.updatelib.update.OnRequestListener;
import com.konsung.updatelib.update.RequestManager;
import com.konsung.updatelib.update.UpdateUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.Constant;
import com.squareup.okhttp.HttpUrl;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import upgrade.parameter.ParameterGlobal;

/**
 * Created by xiangshicheng on 2017/11/1 0001.
 * 软件差分包下载管理类
 */

public class DownloadAppPatchManager implements OnDownloadListener, InstallListener {

    private Context context;
    private RequestManager requestManager;
    private InstallManager installManager;
    private Handler handler;
    private Map<String, UpdateMessage> appMap;
    //通知栏
    private NotificationCompat.Builder builderApp;
    private NotificationCompat.Builder builderDevice;
    private NotificationCompat.Builder builderCsb;
    private NotificationManager mNotifyManagerApp;
    private NotificationManager mNotifyManagerDevice;
    private NotificationManager mNotifyManagerCsb;
    //通知id
    private int idApp;
    private int idDevice;
    private int idCsb;
    //下载完成后的map存储信息
    private Map<String, UpdateMessage> downloadMapInfo;
    //type常量
    private final String appType = "APP"; //app标识
    private final String deviceType = "DEVICE"; //device标识
    private final String csbType = "APP-CSB"; //参数版标识
    //下载进度条最大值
    private final int maxProgress = 100;
    //id地址
    private String ipAddress = "";
    //端口号
    private String port = "";
    //http前缀
    private final String httpText = "http://";
    //存储服务器返回结果信息应用类型值
    private List<String> listAppType;
    //下载条目数
    private int downloadCount = 0;
    //下载完成计数器
    private int count = 0;
    //记录device安装包信息的临时变量
    private UpdateMessage updateMessageDevice = null;
    /**
     * 构造函数
     *
     * @param context 上下文
     * @param handler handler
     */
    public DownloadAppPatchManager(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        appMap = new HashMap<>();
        downloadMapInfo = new HashMap<>();
        ipAddress = SpUtils.getSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.REFRESH_IP
                , GlobalConstant.REFRESH_ADRESS);
        port = SpUtils.getSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.REFRESH_IP_PORT
                , GlobalConstant.REFRESH_ADRESS_PORT);
    }

    /**
     * 检测更新入口
     *
     */
    public void checkUpdate() {
        //判断是否存在有效网络
        if (UiUitls.isNetworkConnected(context)) {
            UiUitls.showProgress(context, UiUitls.getString(R.string.refresh_loading));
            requestManager = new RequestManager(context, this);
            installManager = new InstallManager(context, false, this);
            requestUpdate();
        } else {
            UiUitls.toast(context, UiUitls.getString(R.string.net_unnormal_state));
        }
    }

    /**
     * 更新请求
     */
    private void requestUpdate() {
        //软件更新地址，根据设置页面动态获取
        String serverUrl = httpText + ipAddress + ":" + port;
        if (TextUtils.isEmpty(serverUrl)) {
            if (!GlobalConstant.mainPageUpdate) {
                UiUitls.toast(context, R.string.update_url_did_not_set);
            }
            UiUitls.hideProgress();
            return;
        }
        HttpUrl parsed = HttpUrl.parse(serverUrl);
        if (parsed == null) {
            if (!GlobalConstant.mainPageUpdate) {
                UiUitls.toast(context, R.string.unexpected_url);
            }
            UiUitls.hideProgress();
            return;
        }
        serverUrl += Constant.SUFFIX_UPDATE_PATH;
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.deviceCode = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_CODING, "");
        requestMessage.appArea = UiUitls.getAreaName();
        @SuppressLint("SimpleDateFormat")
        DateFormat sdm = new SimpleDateFormat(UpdateUtils.CHECK_TIME_PATTERN);
        requestMessage.deviceType = Constant.DEVICE_TYPE;
        requestMessage.askTime = sdm.format(new Date());
        requestMessage.appCheckCode = MD5.getKonsungMd5(requestMessage.askTime);
        requestMessage.app = getApps();
        Log.e("request", "request == " + JsonUtils.toJsonString(requestMessage));
        requestManager.requestUpdate(serverUrl, requestMessage, new OnRequestListener() {
            @Override
            public void onFailure(IOException e) {
                //请求失败
                e.printStackTrace();
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                        UiUitls.toast(context, UiUitls.getString(R.string.query_failure));
                    }
                });
            }

            @Override
            public void onSuccess(String jsonString) {
                Log.e("response", "response == " + jsonString);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                    }
                });
                ResponseMessage message = ResponseMessage.toBean(jsonString);
                //服务器返回空数组 均为最新或者无升级信息
                List<AppUpdate> listInfo = message.app;
                downloadCount = listInfo.size();
                if (downloadCount <= 0) {
                    //已经更新到最新
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.toast(context, UiUitls.getString(R.string.upload_is));
                        }
                    });
                    return;
                }
                //返回不为空的情况下，至少存在一项升级项
                listAppType = new ArrayList<String>();
                for (AppUpdate appUpdate : listInfo) {
                    listAppType.add(appUpdate.appType);
                }
                if (!listAppType.contains(appType)) {
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.toast(context, UiUitls.getString(R.string.app_new_version));
                        }
                    });
                }
                if (!listAppType.contains(deviceType)) {
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.toast(context
                                    , UiUitls.getString(R.string.device_new_version));
                        }
                    });
                }
                if (!listAppType.contains(csbType)) {
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.toast(context, UiUitls.getString(R.string.csb_new_version));
                        }
                    });
                }
                //过滤系统升级
                int size = message.app.size();
                for (int i = 0; i < size; i++) {
                    if (message.app.get(i).appType.equals(Constant.APP_TYPE_SYSTEM)) {
                        message.app.remove(i);
                        break;
                    }
                }
                //比较历史更新信息，和当前更新信息，如果更新信息不一致，则删除历史安装包
                saveToLocal(message);
                //END
                //判断是否有足够的空间
                boolean canDownload = UpdateUtils.getDownloadAvailable(
                        UpdateUtils.MIN_DOWNLOAD_SPACE_FOR_APP);
                if (canDownload) {
                    //加入下载
                    queryForAddToDownload(appMap);
                } else {
                    //提示空间不足
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.toast(context, UiUitls.getString(R.string.memory_not_full));
                        }
                    });
                }
            }
        }, true);
    }

    /**
     * 获取系统中需要更新的应用
     *
     * @return 需要更新的应用
     */
    public List<App> getApps() {
        List<App> apps = new ArrayList<>();
        //应用
        App app = new App();
        app.appVersionCode = UiUitls.getAppVersion() + ""; //应用版本号
        app.appType = appType; //应用类型
        apps.add(app);
        //appDevice
        App app1 = new App();
        app1.appVersionCode = UiUitls.getAppViersion(GlobalConstant.DEVICE_PACKAGE_NAME) + "";
        app1.appType = deviceType;
        apps.add(app1);
        //参数板
        App app2 = new App();
        app2.appVersionCode = calculateVersionCode(SpUtils.getSp(context
                , GlobalConstant.APP_CONFIG, GlobalConstant.PARA_KEY, ""));
        //测试code值
//        app2.appVersionCode = "1002";
        app2.appType = csbType;
        apps.add(app2);
        return apps;
    }

    /**
     * 按照后台规则计算版本号
     * @param versionCode 版本号
     * @return 计算后的code值
     */
    private String calculateVersionCode(String versionCode) {
        String resultCode = "";
        if (TextUtils.isEmpty(versionCode)) {
            return resultCode;
        }
        if (!versionCode.contains(".")) {
            return String.valueOf(Integer.parseInt(versionCode) * GlobalConstant.TREND_FACTOR_SG);
        }
        String[] strArr = versionCode.split("\\.");
        int varInteger = Integer.parseInt(strArr[0]);
        int varFloat = Integer.parseInt(strArr[1]);
        return String.valueOf(varInteger * GlobalConstant.TREND_FACTOR_SG + varFloat);
    }
    /**
     * 保存更新信息到本地
     *
     * @param resp 服务器返回信息
     */
    private void saveToLocal(ResponseMessage resp) {
        String history = SpUtils.getSp(context
                , Constant.HISTORY_RESPONSE_MSG_NAME, Constant.HISTORY_RESPONSE_MSG_KEY, "");
        //存在历史信息，与本地信息比较
        if (!"".equals(history)) {
            ResponseMessage responseMessage = ResponseMessage.toBean(history);
            if (responseMessage.app.size() > 0) {
                List<UpdateMessage> histories = new ArrayList<>();
                for (AppUpdate update : responseMessage.app) {
                    UpdateMessage updateMessage = toUpdateMessage(context, null, update);
                    histories.add(updateMessage);
                }
                //与历史记录比较，删除历史安装包
                compareToHistory(histories, resp);
            }
        }
        appMap.clear();
        for (AppUpdate app : resp.app) {
            UpdateMessage msg = toUpdateMessage(context, null, app);
            appMap.put(msg.getAppType(), msg);
        }
        SpUtils.saveToSp(context, Constant.HISTORY_RESPONSE_MSG_NAME
                , Constant.HISTORY_RESPONSE_MSG_KEY, new Gson().toJson(resp));
    }

    /**
     * 对比当前更新信息与数据库信息，如果查询版本有更新，则查找以前的更新文件是否存在，如果存在就删除
     *
     * @param histories 历史记录
     * @param message   服务器更新信息
     */
    private void compareToHistory(List<UpdateMessage> histories, ResponseMessage message) {
        if (message.app == null || message.app.size() == 0) {
            //如果没有任何更新信息，直接清空所有安装包
            for (UpdateMessage msg : histories) {
                File oldFile = UpdateUtils.createDownloadFile(context, msg);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            return;
        }
        //遍历历史记录，如果版本有更新就删除，如果服务器删除了该条记录，也要删除文件
        for (UpdateMessage msg : histories) {
            boolean getUpdate = false; //是否获取到该应用更新信息
            for (AppUpdate update : message.app) {
                //获取到版本
                if (update.appType.equals(msg.getAppType())) {
                    //版本变更
                    if (!update.appVersionCode.equals(msg.getNewVersionCode())) {
                        File oldFile = UpdateUtils.createDownloadFile(context, msg);
                        if (oldFile.exists()) {
                            //新版本升级则删除存在的旧文件
                            oldFile.delete();
                        }
                    }
                    getUpdate = true;
                }
            }
            if (!getUpdate) {
                File oldFile = UpdateUtils.createDownloadFile(context, msg);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
        }
    }

    /**
     * 查询队列中是否有需要下载的任务，加入下载队列
     *
     * @param messages 应用队列
     */
    private void queryForAddToDownload(Map<String, UpdateMessage> messages) {

        for (String key : messages.keySet()) {
            final UpdateMessage msg = messages.get(key);
            if (UpdateUtils.canDownload(msg)) {
                //加入任务队列，等待开始下载
                requestManager.addTask(msg);
                builderNotification(msg.getAppName(), msg.getAppType());
            } else {
                switch (msg.getTaskType()) {
                    case Progress.WAITING_INSTALL:
                    case Progress.NEW_APP_WAITING_INSTALL:
                        //表示本地已下载，直接弹窗提示是否安装
                        //提醒安装,弹窗提示是否选择安装
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                openDialogNoticeInstall(msg);
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        }
        requestManager.start();
    }

    /**
     * 根据服务器返回信息，添加或者修改一个应用信息
     *
     * @param context       上下文
     * @param updateMessage 应用信息
     * @param update        服务器返回信息
     * @return 应用信息
     */
    private UpdateMessage toUpdateMessage(Context context
            , @Nullable UpdateMessage updateMessage, AppUpdate update) {
        if (updateMessage == null) {
            updateMessage = new UpdateMessage();
            updateMessage.setTaskType(Progress.NEW_APP);
            updateMessage.setIcon(ContextCompat.getDrawable(context
                    , R.drawable.ic_launcher_round));
        } else {
            //如果是新应用，重置为新应用等待下载
            if (updateMessage.getTaskType() >= Progress.NEW_APP) {
                updateMessage.setTaskType(Progress.NEW_APP);
                updateMessage.setIcon(ContextCompat.getDrawable(context
                        , R.drawable.ic_launcher_round));
            } else {
                updateMessage.setTaskType(Progress.WAITING_DOWNLOAD);
            }
        }
        updateMessage.setAppType(update.appType);
        if (updateMessage.getAppType().equals(appType)) {
            updateMessage.setPackageName(UiUitls.getPackName());
        } else if (updateMessage.getAppType().equals(deviceType)) {
            updateMessage.setPackageName(GlobalConstant.DEVICE_PACKAGE_NAME);
        }
        if (updateMessage.getAppType().equals(csbType)) {
            //参数板判断.image文件
            if (isPatchExits(null, updateMessage, false)) {
                updateMessage.setTaskType(Progress.NEW_APP_WAITING_INSTALL);
            }
        } else {
            //app和device判断patch文件
            if (isPatchExits(update, null, true)) {
                updateMessage.setTaskType(Progress.NEW_APP_WAITING_INSTALL);
            }
        }
        updateMessage.setAppName(update.appName);
        updateMessage.setUpdateLevel(update.updateLevel);
        updateMessage.setAppId(update.appId);
        if (!update.appVersionCode.equals(updateMessage.getNewVersionCode())) {
            updateMessage.setNotified(false);
        }
        updateMessage.setNewVersionCode(update.appVersionCode);
        updateMessage.setNewVersionName(update.appVersionName);
        updateMessage.setUpdateType(update.updateType);
        updateMessage.setFileMd5(update.fileMd5);
        updateMessage.setFileSize(update.fileSize);
        if (update.fullFileSize == null) {
            updateMessage.setFullSize(update.fileSize);
        } else {
            updateMessage.setFullSize(update.fullFileSize);
        }
        if (update.fullFileMd5.isEmpty()) {
            updateMessage.setFullFileMd5(update.fileMd5);
        } else {
            updateMessage.setFullFileMd5(update.fullFileMd5);
        }
        updateMessage.setDescription(update.appDesc);
        updateMessage.setTime(update.uploadTime);
        updateMessage.setServerAddresses(update.serverList);
        updateMessage.setReboot(update.reboot);
        return updateMessage;
    }

    /**
     * 构建通知栏提示下载进度
     *
     * @param name 软件名称
     * @param type 软件类型
     */
    private void builderNotification(String name, String type) {
        //构建通知栏
        if (type.equals(appType)) {
            //app下载
            builderApp = new NotificationCompat.Builder(context);
            builderApp.setContentTitle(name + UiUitls.getString(R.string.refresh_title))
                    .setContentText(UiUitls.getString(R.string.loading))
                    .setSmallIcon(R.drawable.ic_logo);
            // 构建提醒栏
            mNotifyManagerApp = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            idApp = 0;
        } else if (type.equals(deviceType)) {
            //device下载
            builderDevice = new NotificationCompat.Builder(context);
            builderDevice.setContentTitle(name + UiUitls.getString(R.string.refresh_title))
                    .setContentText(UiUitls.getString(R.string.loading))
                    .setSmallIcon(R.drawable.ic_logo);
            // 构建提醒栏
            mNotifyManagerDevice = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            idDevice = 1;
        } else if (type.equals(csbType)) {
            //参数板下载
            builderCsb = new NotificationCompat.Builder(context);
            builderCsb.setContentTitle(name + UiUitls.getString(R.string.refresh_title))
                    .setContentText(UiUitls.getString(R.string.loading))
                    .setSmallIcon(R.drawable.ic_logo);
            // 构建提醒栏
            mNotifyManagerCsb = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            idCsb = 2;
        }
    }

    /**
     * downloadlistener
     * 差分包下载完成后的回调
     */
    @Override
    public void onDownloaded(final UpdateMessage updateMessage) {
        //单个应用下载完成
        //这个是自动启动安装的，如果提示，则自动提示窗口提示
        //这里可以弹窗提示是否选择进行安装
        downloadMapInfo.put(updateMessage.getAppType(), updateMessage);
        Log.d("UpdateDemo", "下载完成,应用 = " + updateMessage.getAppType() +
                ",升级类型 = " + updateMessage.getUpdateType());
        if (updateMessage.getAppType().equals(appType)) {
            builderApp.setContentText(UiUitls.getString(R.string.doload_fished)).setProgress(0
                    , 0, false);
            mNotifyManagerApp.notify(idApp, builderApp.build());
            mNotifyManagerApp.cancel(idApp);
            //保存内存存储路径
            String appPath = UpdateUtils.APP_PARENT_PATH + "/" + updateMessage.getAppType()
                    + updateMessage.getNewVersionName()
                    + "_" + updateMessage.getNewVersionCode() + ".apk";
            SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.APP_PATH
                    , appPath);
            count ++;
        } else if (updateMessage.getAppType().equals(deviceType)) {
            updateMessageDevice = updateMessage;
            builderDevice.setContentText(UiUitls.getString(R.string.doload_fished)).setProgress(0, 0
                    , false);
            mNotifyManagerDevice.notify(idDevice, builderDevice.build());
            mNotifyManagerDevice.cancel(idDevice);
            //保存内存存储路径
            String devicePath = UpdateUtils.APP_PARENT_PATH + "/" + updateMessage.getAppType()
                    + updateMessage.getNewVersionName()
                    + "_" + updateMessage.getNewVersionCode() + ".apk";
            SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.DEVICE_PATH
                    , devicePath);
            count ++;
        } else if (updateMessage.getAppType().equals(csbType)) {
            builderCsb.setContentText(UiUitls.getString(R.string.doload_fished)).setProgress(0
                    , 0, false);
            mNotifyManagerCsb.notify(idCsb, builderCsb.build());
            mNotifyManagerCsb.cancel(idCsb);
            count ++;
        }
        //添加任务
        installManager.addTask(downloadMapInfo.get(updateMessage.getAppType()));
        //所有任务下载完毕的判断
        // TODO: 2018/1/19 0019 后面优化 可以直接在taskfinished方法中作为结束下载后的逻辑操作 
        if (count >= downloadCount) {
            //计数完成后清空
            count = 0;
            downloadCount = 0;
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    //防止页面意外销毁带来的context内存溢出
                    if (!((BaseActivity)context).isFinishing()) {
                        UiUitls.showProgress(context, context.getString(R.string.updating));
                    }
                }
            });
            //差分包合成apk执行任务
            installManager.startInstall();
        }
    }

    /**
     * 弹窗逻辑
     *
     * @param updateMessage 更新信息
     */
    private void openDialogNoticeInstall(final UpdateMessage updateMessage) {
        //如果页面已经销毁，则不允许弹窗
        if (((BaseActivity)context).isFinishing()) {
            return;
        }
        //下载完成后弹窗显示是否更新
        if (updateMessage.getAppType().equals(csbType)) {
            //参数版
            showInstallImageDialog(context, UiUitls.getString(R.string.important_update)
                    , updateMessage);
        } else {
            NoticeDialog noticeDialog = new NoticeDialog(context
                    , UiUitls.getString(R.string.refresh_title)
                    , new NoticeDialog.UpdataButtonState() {
                        @Override
                        public void getButton(Boolean pressed, UpdateMessage updateMessage1) {
                            if (pressed) {
                                //提醒安装的应用
                                handler.post(new NotifyRunnable(context, updateMessage));
                            }
                        }
                    }, updateMessage);
            noticeDialog.show();
            noticeDialog.setTips(updateMessage.getDescription());
            noticeDialog.isShowReversionText(true);
            noticeDialog.setHideIvClose(true);
        }
    }

    /**
     * 安装镜像弹出框
     *
     * @param mContext 上下文
     * @param title    标题
     * @param bean     显示数据
     */
    private void showInstallImageDialog(final Context mContext, String title
            , UpdateMessage bean) {
        SpUtils.saveToSp(mContext, GlobalConstant.APP_CONFIG, ParameterGlobal.SP_KEY_CSB_UPDATE
                , true);
        try {
            String filePath = UpdateUtils.getCSBUpdateFilePath(bean);
            SpUtils.saveToSp(mContext, GlobalConstant.APP_CONFIG, ParameterGlobal.CSB_NAME_FLAG
                    , filePath);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ImportantUpdateDialog updateDialog = new ImportantUpdateDialog(mContext, title
                , new ImportantUpdateDialog.UpdataButtonState() {
                    @Override
                    public void getButton(Boolean pressed) {

                    }
                });
        updateDialog.show();
        updateDialog.setDesStr(bean.getDescription());
    }

    /**
     * downloadlistener
     */
    @Override
    public void onDownloading(UpdateMessage updateMessage) {
        //正在下载的应用
        if (updateMessage.getAppType().equals("APP")) {
            int progress = updateMessage.getDownloadProgress();
            Log.d("UpdateDemo", updateMessage.getAppType() + "下载进度 = " + progress);
            builderApp.setProgress(maxProgress, progress, false);
            builderApp.setContentText(UiUitls.getString(R.string.doload_dataing) + progress
                    + UiUitls.getString(R.string.x_measure_unit_percent));
            mNotifyManagerApp.notify(idApp, builderApp.build());
        } else if (updateMessage.getAppType().equals("DEVICE")) {
            int progress = updateMessage.getDownloadProgress();
            Log.d("UpdateDemo", updateMessage.getAppType() + "下载进度 = " + progress);
            builderDevice.setProgress(maxProgress, progress, false);
            builderDevice.setContentText(UiUitls.getString(R.string.doload_dataing) + progress
                    + UiUitls.getString(R.string.x_measure_unit_percent));
            mNotifyManagerDevice.notify(idDevice, builderDevice.build());
        } else if (updateMessage.getAppType().equals("APP-CSB")) {
            int progress = updateMessage.getDownloadProgress();
            Log.d("UpdateDemo", updateMessage.getAppType() + "下载进度 = " + progress);
            builderCsb.setProgress(maxProgress, progress, false);
            builderCsb.setContentText(UiUitls.getString(R.string.doload_dataing) + progress
                    + UiUitls.getString(R.string.x_measure_unit_percent));
            mNotifyManagerCsb.notify(idCsb, builderCsb.build());
        }
    }

    /**
     * downloadlistener
     */
    @Override
    public void onDownError(UpdateMessage updateMessage, int i) {
        //下载出错
        if (updateMessage.getAppType().equals(appType)) {
            builderApp.setContentText(UiUitls
                    .getString(R.string.refresh_doload_fail)).setProgress(0, 0, false);
            mNotifyManagerApp.notify(idApp, builderApp.build());
            mNotifyManagerApp.cancel(idApp);
        } else if (updateMessage.getAppType().equals(deviceType)) {
            builderDevice.setContentText(UiUitls
                    .getString(R.string.refresh_doload_fail)).setProgress(0, 0, false);
            mNotifyManagerDevice.notify(idDevice, builderDevice.build());
            mNotifyManagerDevice.cancel(idDevice);
        } else if (updateMessage.getAppType().equals(csbType)) {
            builderCsb.setContentText(UiUitls
                    .getString(R.string.refresh_doload_fail)).setProgress(0, 0, false);
            mNotifyManagerCsb.notify(idCsb, builderCsb.build());
            mNotifyManagerCsb.cancel(idCsb);
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.toast(context, UiUitls.getString(R.string.refresh_doload_fail));
            }
        });
    }

    /**
     * downloadlistener
     */
    @Override
    public void onTaskFinished() {
        //下载完成
    }

    /**
     * InstallListener
     */
    @Override
    public void onProgressUpdated(UpdateMessage updateMessage, int i, int i1) {

    }

    /**
     * InstallListener
     */
    @Override
    public void onInstalling(UpdateMessage updateMessage, int i, int i1) {

    }

    /**
     * InstallListener
     */
    @Override
    public void notifyInstall(final UpdateMessage updateMessage) {
        //如果是device先不弹框，放在最后面弹框，保证先升级device
        if (!updateMessage.getAppType().equals(deviceType)) {
            //弹框提示升级
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    openDialogNoticeInstall(updateMessage);
                }
            });
        }
    }

    /**
     * InstallListener
     */
    @Override
    public void onAllInstallFinished() {
        //所有合成任务执行完成后的逻辑
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.hideProgress();
            }
        });
        //最后弹appdevice升级框
        if (updateMessageDevice != null
                && updateMessageDevice.getAppType().equals(deviceType)) {
            //弹框提示升级
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    openDialogNoticeInstall(updateMessageDevice);
                }
            });
        }
    }

    /**
     * InstallListener
     */
    @Override
    public void onInstallFinished(UpdateMessage updateMessage) {
    }

    /**
     * InstallListener
     */
    @Override
    public void onInstallFailed(int i) {
    }

    /**
     * 判读patch包是否存在
     * @param appUpdate 数据信息
     * @param updateMessage 数据信息
     * @param isPatch 是否判断patch包类型
     * @return 是否存在patch包
     */
    private boolean isPatchExits(AppUpdate appUpdate, UpdateMessage updateMessage
            , boolean isPatch) {
        //文件夹路径
        String parentPath = "";
        //文件完整路径
        String allPath = "";
        if (isPatch) {
            if (appUpdate == null) {
                return false;
            }
            parentPath = UpdateUtils.APP_PARENT_PATH;
            allPath = parentPath + "/" + appUpdate.appType + appUpdate.appVersionName
                    + "_" + appUpdate.appVersionCode + ".apk";
        } else {
            try {
                if (updateMessage == null) {
                    return false;
                }
                allPath = UpdateUtils.getCSBUpdateFilePath(updateMessage);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        File file = new File(allPath);
        return file.exists();
    }
}
