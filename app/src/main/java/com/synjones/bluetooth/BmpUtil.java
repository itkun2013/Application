package com.synjones.bluetooth;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Bitmap处理工具类
 */
public class BmpUtil {

    //rootPath
    public static final String ROOT_PATH = "/sdcard/photo/";
    //压缩比例
    private static final int COMPRESS_NUM = 100;
    //字节数组长度
    private static final int LENGTH = 1024;
    //倍数
    private static final int NUM = 4;

    /**
     * bitmap位图转换为字节数组
     * @param bitmap 位图
     * @return 字节数组
     */
    public static byte[] bmp2bytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int size = bitmap.getWidth() * bitmap.getHeight() * NUM;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_NUM, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 字节数组转换为位图
     * @param photo 字节数组
     * @param fileName 文件名
     * @return 位图
     */
    public static Bitmap getBmpFromByteArray(byte[] photo, String fileName) {
        try {
            File dirFile = new File(ROOT_PATH);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File wltFile = new File(ROOT_PATH + fileName + ".wlt");
            FileOutputStream fos = new FileOutputStream(wltFile);
            fos.write(photo, 0, LENGTH);
            fos.close();
            DecodeWlt dw = new DecodeWlt();
            int result = dw.Wlt2Bmp(ROOT_PATH + fileName + ".wlt", ROOT_PATH + fileName + ".bmp");
            if (result == 1) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 1;
                opt.inJustDecodeBounds = false;
                //BMP图片数据
                Bitmap bmp = BitmapFactory.decodeFile(ROOT_PATH + fileName + ".bmp", opt);
                if (bmp != null) {
                    return bmp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存头像图片至sd卡
     * @param photo 数组
     * @param fileName 文件名
     */
    public static void saveHeadPic(byte[] photo, String fileName) {
        File dirFile = new File(ROOT_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        try {
            File wltFile = new File(ROOT_PATH + fileName + ".wlt");
            FileOutputStream fos = null;
            fos = new FileOutputStream(wltFile);
            fos.write(photo, 0, LENGTH);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存头像图片至sd卡
     * @param file 头像文件
     * @param fileName 文件名
     */
    public static void saveHeadPic(File file, String fileName) {
        File dirFile = new File(ROOT_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        try {
            File wltFile = new File(ROOT_PATH + fileName + ".wlt");
            copyFile(file, wltFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图片名称获取本地图片
     * @param fileName 文件名
     * @return 位图
     */
    public static Bitmap getBitmapByFileName(String fileName) {
        Bitmap bitmap = null;
        DecodeWlt dw = new DecodeWlt();
        int result = dw.Wlt2Bmp(ROOT_PATH + fileName + ".wlt", ROOT_PATH + fileName + ".bmp");
        if (result == 1) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 1;
            opt.inJustDecodeBounds = false;
            //BMP图片数据
            Bitmap bmp = BitmapFactory.decodeFile(ROOT_PATH + fileName + ".bmp", opt);
            if (bmp != null) {
                return bmp;
            }
        }
        return bitmap;
    }

    /**
     * 根据图片名称获取头像bitmap
     * @param fileName 文件名
     * @return 位图
     */
    public static Bitmap getHeadBitmap(String fileName) {
        File rootFile = getHeadPicRootFile();
        File picFile = new File(rootFile, fileName + ".bmp");

        if (!picFile.exists()) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 1;
        opt.inJustDecodeBounds = false;
        //BMP图片数据
        Bitmap bmp = BitmapFactory.decodeFile(ROOT_PATH + fileName + ".bmp", opt);
        if (bmp != null) {
            return bmp;
        }
        return null;
    }

    /**
     * 根据图片名称获取头像bitmap
     * @param fileName 文件名
     */
    public static void deleteHeadBitmap(String fileName) {
        File rootFile = getHeadPicRootFile();
        File picFile = new File(rootFile, fileName + ".bmp");
        if (picFile != null) {
            picFile.delete();
        }
    }

    /**
     * 文件复制
     * @param fin 输入的文件
     * @param fout 输出的文件
     */
    public static void copyFile(File fin, File fout) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(fin);
            fo = new FileOutputStream(fout);
            in = fi.getChannel(); //得到对应的文件通道
            out = fo.getChannel(); //得到对应的文件通道
            in.transferTo(0, in.size(), out); //连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 获取头像文件保存路径文件夹 对象
     * @return 头像文件保存路径文件夹 对象  如果不存在会创建该文件夹
     */
    public static File getHeadPicRootFile() {
        File root = Environment.getExternalStorageDirectory();
        File picDir = new File(root.getAbsolutePath() + File.separator + "photo" + File.separator);
        if (!picDir.exists()) {
            picDir.mkdirs();
        }
        return picDir;
    }

    /**
     * 使用文件通道的方式复制文件
     * @param srcFile 源文件
     * @param targetFile 复制到的新文件
     */

    public static void fileChannelCopy(File srcFile, File targetFile) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(srcFile);
            fo = new FileOutputStream(targetFile);
            in = fi.getChannel(); //得到对应的文件通道
            out = fo.getChannel(); //得到对应的文件通道
            in.transferTo(0, in.size(), out); //连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字节数组转换为位图
     * @param photo 字节数组
     * @return 位图
     */
    public Bitmap getBmpFromCursor(byte[] photo) {
        try {
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除所有的身份证照片
     */
    public static void deleteHeadBitmapAll() {
        File headPicRootFile = getHeadPicRootFile();
        if (headPicRootFile.exists()) {
            headPicRootFile.delete();
        }
    }
}
