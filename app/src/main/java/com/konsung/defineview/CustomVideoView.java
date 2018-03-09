package com.konsung.defineview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 自定义video view
 * Created by DJH on 2016/10/25 0025.
 */
public class CustomVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;

    /**
     * 构造器
     * @param context 上下文
     */
    public CustomVideoView(Context context) {
        super(context);
    }

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造器
     * @param attrs 属性值
     * @param context 上下文
     * @param defStyle 风格
     */
    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //下面的代码是让视频的播放的长宽是根据你设置的参数来决定
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
