package com.konsung.util.global;

import com.konsung.bean.VideoInfo;
import java.util.List;

/**
 * Created by xiangshicheng on 2017/4/20 0020.
 */

public class VideoInfoSaveModule {
    private static VideoInfoSaveModule instance = null;
    /**
     * 获取实例
     * @return 类实例对象
     */
    public static VideoInfoSaveModule getInstance() {
        if (instance == null) {
            instance = new VideoInfoSaveModule();
        }
        return instance;
    }
    public List<VideoInfo> videoInfos;
}
