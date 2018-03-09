package com.konsung.util;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore.Video.Thumbnails;

import com.konsung.bean.VideoInfo;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
/**
 * 视频信息获取处理类
 */
public class Utility {
    private static ArrayList<VideoInfo> videoTmpeList;
    /*
     * 文件类型 视频3GP
     */
    public static String FILETYPE_VIDEO_3GPP = ".3gp";
    /*
     * 文件类型 视频MP4
     */
    public static String FILETYPE_VIDEO_MP4 = ".mp4";
    public static final long NUM = 1024;
    /*
     * 视频文件过滤器[3gp|mp4]
     */
    private static final FileFilter VIDEOS_FILTER = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().matches("^.*?\\.(3gp|mp4)$");
        }
    };

    /*
     * 格式化显示输出文件大小
     * @param fileSize
     * @return
     */
    private static String formatFileSize(float fileSize) {
        long kb = NUM;
        long mb = (kb * NUM);
        long gb = (mb * NUM);

        if (fileSize < kb) {
            return String.format("%d B", (int) fileSize);
        } else if (fileSize < mb) {
            return String.format("%.2f KB", fileSize / kb);
        } else if (fileSize < gb) {
            return String.format("%.2f MB", fileSize / mb);
        } else {
            return String.format("%.2f GB", fileSize / gb);
        }
    }

    /*
     * 取得视频文件相关信息
     * 填充视频实体类
     * @param ctx
     * @param videoinfo
     * @param file
     */
    private static void fillVideoInfo(Context ctx, VideoInfo videoinfo, File file) {
        boolean is3gp = false;
        boolean ismp4 = false;
        is3gp = file.getName().toLowerCase().endsWith(FILETYPE_VIDEO_3GPP);
        ismp4 = file.getName().toLowerCase().endsWith(FILETYPE_VIDEO_MP4);
        //默认类型
        Bitmap typeIcon = null;
        //默认截图
        Bitmap thumbnail = null;

        //使用AndroidSDK2.2提供的缩略图创建类 可以直接从视频文件中抽取第一帧图片作为缩略图显示
        Bitmap tempThumb = ThumbnailUtils.createVideoThumbnail(file
                .getAbsolutePath(), Thumbnails.MINI_KIND);
        if (is3gp) {
            //R.id.icon_3gp);
            typeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.id
                    .icon1);
            //thumb_3gp
            thumbnail = (null != tempThumb) ? tempThumb :
                    BitmapFactory.decodeResource(ctx.getResources(), R.id
                    .icon);
        } else if (ismp4) {
            //R.id.icon_mp4);
            typeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.id
                    .icon2);
            //thumb_mp4
            thumbnail = (null != tempThumb) ? tempThumb :
                    BitmapFactory.decodeResource(ctx.getResources(), R.id
                    .icon);
        } else {
            //R.id.icon_mp4);
            typeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.id
                    .icon2);
            //thumb_mp4
            thumbnail = (null != tempThumb) ? tempThumb :
                    BitmapFactory.decodeResource(ctx.getResources(), R.id
                            .icon);
        }

        videoinfo.setName(file.getName());
        videoinfo.setSize(formatFileSize(file.length()));
        videoinfo.setPath(file.getAbsolutePath());
        videoinfo.setType(typeIcon);
        videoinfo.setThumbnail(thumbnail);
    }

    /**
     * 遍历SD卡 取得播放视频列表
     * @param ctx 上下文引用
     */
    public static ArrayList<VideoInfo> getVideosFromSD(Context ctx) {
        ArrayList<VideoInfo> videolist = new ArrayList<VideoInfo>();
        //videoTmpeList = null;
        videoTmpeList = new ArrayList<VideoInfo>();
        // 本地文件搜索根路径
        File root = new File("/storage/sdcard0");
        if (!root.exists()) {
            return videolist;
        }
        //使用文件过滤器[视频格式限定为3gp mp4]
        //遍历sd卡取得所有符合过滤原则的视频文件
        //添加到视频播放列表
        ArrayList<VideoInfo> allFiles = getAllFiles(ctx, root, videoTmpeList);
        //删除重复的视频文件
        for (int i = 0; i < allFiles.size() - 1; i++) {
            for (int j = allFiles.size() - 1; j > i; j--) {
                if (allFiles.get(j).getName().equals(allFiles.get(i).getName()) && allFiles.get(j)
                        .getSize().equals(allFiles.get(i).getSize())) {
                    allFiles.remove(j);
                }
            }
        }
        videolist.addAll(allFiles);
        return videolist;
    }

    /**
     * 遍历SD卡中所有的视频
     * @param ctx 上下文引用
     * @param root 访问的文件
     * @param videoTmpeList 视频信息集合
     */
    public static ArrayList<VideoInfo> getAllFiles(Context ctx, File root
            , ArrayList<VideoInfo> videoTmpeList) {
        //videoTmpeList = new ArrayList<VideoInfo>();
        File files[] = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllFiles(ctx, file, videoTmpeList);
                }
                if (file.isFile()) {
                    if (file.getName().matches("^.*?\\.(3gp|mp4|avi)$")) {
                        VideoInfo video = new VideoInfo();
                        fillVideoInfo(ctx, video, file);
                        videoTmpeList.add(video);
                    }
                }
            }
        }
        return videoTmpeList;
    }

    /**
     * 检查sd卡是否存在 如果sd卡存在，则返回true
     */
    public static boolean checkSDCard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取外置SD卡路径
     * @param mContext 上下文
     * @return 外置SD卡路径，如果未挂载外置SD卡，返回空字符串
     */
    public static String getExternalSdCardPath(Context mContext) {
        String extSdCard = "";
        StorageManager sm = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new Class[0])
                    .invoke(sm, new Object[]{});
            String esd = Environment.getExternalStorageDirectory().getPath();
            for (int i = 0; i < paths.length; i++) {
                if (paths[i].equals(esd)) {
                    continue;
                }
                File sdFile = new File(paths[i]);
                if (sdFile.canWrite()) {
                    extSdCard += paths[i];
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return extSdCard;
    }
}
