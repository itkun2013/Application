package com.konsung.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;

import com.konsung.R;
import com.konsung.defineview.CustomVideoView;
import com.konsung.listener.VideoClickListener;

import java.io.File;

/**
 * 视频工具类
 */
public class VideoUtil {

    /**
     * 視頻播放頁面傳值字段
     * {@link KParamType}可能值
     */
    public static final String VIDEO_PARAM = "param";

    /**
     * 获取视频状态
     * @param view Fragment布局
     * @return 是否正在播放视频
     */
    public boolean getVideoStatus(View view) {
        if (view.findViewById(R.id.video_view) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 視頻路徑獲取方法
     * @param param 參數
     * {@link KParamType}可能值
     * @param context 上下文
     * @return 路徑
     */
    public static Uri getVideoUri(int param, Context context) {
        String uri = "";
        String parentPath = Environment.getExternalStorageDirectory() + File.separator +
                "Konsung" + File.separator + "Video" + File.separator;
        switch (param) {
            case KParamType.ECG_HR:
                uri = parentPath + "ecg.mp4";
                break;
            case KParamType.SPO2_TREND:
            case KParamType.SPO2_PR:
                uri = parentPath + "spo2.mp4";
                break;
            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
                uri = parentPath + "nibp.mp4";
                break;
            case KParamType.IRTEMP_TREND:
                uri = parentPath + "irtemp.mp4";
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.BLOODGLU_AFTER_MEAL:
                uri = parentPath + "glu.mp4";
                break;
            case KParamType.URICACID_TREND:
            case KParamType.CHOLESTEROL_TREND:
                uri = parentPath + "bene.mp4";
                break;
            case KParamType.URINERT_SG:
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
            case KParamType.URINERT_ALB:
            case KParamType.URINERT_CA:
            case KParamType.URINERT_CRE:
            case KParamType.URINERT_PH:
                uri = parentPath + "urt.mp4";
                break;
            case KParamType.BLOOD_WBC:
                uri = parentPath + "wbc.mp4";
                break;
            case KParamType.BLOOD_HGB:
            case KParamType.BLOOD_HCT:
                uri = parentPath + "hemoglobin.mp4";
                break;
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
            case KParamType.GHB_EAG:
                uri = parentPath + "ghb.mp4";
                break;
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                uri = parentPath + "bloodfat.mp4";
                break;
            default:

                break;
        }
        File file = new File(uri);
        if (file.exists()) {
            return Uri.fromFile(new File(uri));
        } else {
            return null;
        }
    }

    /**
     * 播放视频方法
     * @param context 上下文
     * @param uri 视频路径
     * @param videoView 播放视频的videoView
     */
    public static void play(Context context, CustomVideoView videoView, Uri uri) {
        videoView.setMediaController(new MediaController(context));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.setBackgroundColor(0);
    }

    /**
     * 获取视频观看点击事件监听器
     * @param context 上下文
     * @param param 参数
     * @return 视频观看点击事件监听器
     */
    public static VideoClickListener getVideoListener(Context context, int param) {
        return new VideoClickListener(context, param);
    }
}
