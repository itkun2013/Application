package com.konsung.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.konsung.R;
import com.konsung.activity.MeasureActivity;
import com.konsung.util.VideoUtil;

/**
 * 观看视频点击监听事件封装
 */
public class VideoClickListener implements View.OnClickListener {

    Context context;
    int param;

    /**
     * @param context 上下文
     * @param param 需要观看的视频对应的测量项参数
     */
    public VideoClickListener(Context context, int param) {
        this.context = context;
        this.param = param;
    }

    @Override
    public void onClick(View v) {
        //观看视频
        Intent it = new Intent(context, MeasureActivity.class);
        it.putExtra(VideoUtil.VIDEO_PARAM, param);
        context.startActivity(it);
        ((Activity) context).overridePendingTransition(R.anim.anim_video_open, 0);
    }
}
