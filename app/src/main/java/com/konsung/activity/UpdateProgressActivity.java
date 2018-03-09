package com.konsung.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.BuglyLog;
import java.io.File;

import upgrade.parameter.Parameter;
import upgrade.parameter.ParameterGlobal;

/**
 * 安装弹出框，用于限制操作，预防升级异常
 */
public class UpdateProgressActivity extends Activity {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private RelativeLayout mView;
    //宽度
    private static final float WIDTH_DP = 560f;
    //高度
    private static final float HEIGHT_DP = 320f;

    //进度条
    ProgressBar progressBar;
    //进度提示
    TextView progressTx;
    //提示
    TextView tips;

    int progress = 0;
    boolean installing = true;

    private final int maxProgress = 100;
    private final int dealyTime = 1000;
    private final int times = 180;
    //参数板文件路径
    private String filePath = "";

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update_progress);
        filePath = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , ParameterGlobal.CSB_NAME_FLAG, GlobalConstant.csbPath);
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication()
                .getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.height = UiUitls.dpToPx(HEIGHT_DP, getResources());
        wmParams.width = UiUitls.dpToPx(WIDTH_DP, getResources());
        wmParams.format = PixelFormat.RGBA_8888;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mView = (RelativeLayout) inflater.inflate(R.layout.activity_update_progress, null);
        mWindowManager.addView(mView, wmParams);

        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        tips = (TextView) mView.findViewById(R.id.tips);
        progressTx = (TextView) mView.findViewById(R.id.progress_tx);

        mThread.start();
    }

    /**
     * 安装线程
     */
    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            int times = 0;

            File file = new File(filePath);

            if (!file.exists()) {
                SpUtils.saveToSp(UpdateProgressActivity.this, GlobalConstant
                        .APP_CONFIG, ParameterGlobal.SP_KEY_CSB_UPDATE, false);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateProgressActivity.this, R.string.update_failed,
                                Toast.LENGTH_SHORT).show();
                        mWindowManager.removeView(mView);
                        finish();
                    }
                });
                return;
            }
            String[] str = {"", "/dev/ttyMT1", file.getAbsolutePath()};
            mHandler.removeCallbacks(progressRunnable);
            mHandler.post(progressRunnable);
            while (installing) {
                //如果升级成功，会返回0
                final int c = Parameter.upgrades(1, 3, "/dev/ttyMT1", file.getAbsolutePath());
                times++;
                if (times > 2 || c == 0) {
                    installing = false;
                }
                if (c == 0) {

                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            BuglyLog.v(MyApplication.class.getName(), "参数版升级成功");
                            progress = maxProgress;
                            progressBar.setProgress(progress);
                            progressTx.setText("100%");
                            tips.setText(R.string.update_success);
                            SpUtils.saveToSp(UpdateProgressActivity.this, GlobalConstant
                                    .APP_CONFIG, ParameterGlobal.SP_KEY_CSB_UPDATE, false);
                            SpUtils.saveToSp(UpdateProgressActivity.this, GlobalConstant.APP_CONFIG,
                                    ParameterGlobal.SP_KEY_CSB_CHECK_VERSION, true);
                        }
                    });
                    file.delete();
                }
                if (times > 2 && c != 0) {
                    UiUitls.post(failedRunnable);
                    installing = false;
                }
            }
        }
    });

    /**
     * 显示失败，升级失败删除文件,并启动appDevice
     */
    Runnable failedRunnable = new Runnable() {
        @Override
        public void run() {
            progress = maxProgress;
            progressBar.setProgress(progress);
            progressTx.setText("100%");
            tips.setText(R.string.update_failed);
            SpUtils.saveToSp(UpdateProgressActivity.this, GlobalConstant
                    .APP_CONFIG, ParameterGlobal.SP_KEY_CSB_UPDATE, false);
            File file = new File(filePath);
            Toast.makeText(UpdateProgressActivity.this, R.string.update_failed
                    , Toast.LENGTH_LONG).show();
            file.delete();
            BuglyLog.v(MyApplication.class.getName(), "参数版升级失败");
            //利用包名，启动AppDevice
            Intent mIntent = getPackageManager().getLaunchIntentForPackage("org" +
                    ".qtproject.qt5.android.bindings");
            if (mIntent != null) {
                startActivity(mIntent);
                Log.d("HealthOne", "Start AppDevice!");
            } else {
                Log.e("HealthOne", "AppDevice Start Failed!");
            }

            mWindowManager.removeViewImmediate(mView);
            finish();

            if (!file.exists()) {
                file.delete();
            }
        }
    };

    /**
     * 进度条控制
     */
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            progress++;
            if (progress < maxProgress) {
                progressTx.setText(progress + "%");
            }
            if (progress < maxProgress) {
                progressBar.setProgress(progress);
                mHandler.postDelayed(this, dealyTime);
            } else if (installing) {
                //大于3分钟，就显示升级失败
                if (progress > times) {
                    mHandler.post(failedRunnable);
                } else {
                    mHandler.postDelayed(this, dealyTime);
                }
            }
        }
    };
}
