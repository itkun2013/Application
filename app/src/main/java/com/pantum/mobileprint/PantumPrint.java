package com.pantum.mobileprint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.pantum.mobileprint.LoadJNI;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.http.util.EncodingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by allenchan on 2015/12/17.
 */
public class PantumPrint {
    /**
     * 奔图打印机的权限获取回调
     * 当权限获取成功后，会发送该Action广播
     */
    public static final String ACTION_USB_PERMISSION = "com.konsung.pantum.permission";

    private LoadJNI loadjni = new LoadJNI();
    private int printJobDataTotalLen = 0;
    private int printJobDataSendTotalLen = 0;
    private UsbManager usbManager; // USB管理器
    private UsbDevice usbDevice; // 找到的USB设备
    private UsbInterface usbInterface;
    private ArrayList<String> usbDeviceList = new ArrayList<String>();
    private UsbDeviceConnection usbDeviceConn;
    private UsbEndpoint usbEndPointOut;
    private PendingIntent mPermissionIntent;

    public void print(Context context, Bitmap bitmap) {
        initPrint(context);
        connectUsb(context);
        printPicture(bitmap);
    }

    /**
     * 添加打印路径的构造方法
     * @param context 上下文
     * @param path 路径
     */
    public void print(Context context, String path) {
        initPrint(context);
        connectUsb(context);
        printPicture(path);
    }

    public void initPrint(Context context) {
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        String json_Str = "{\"AndroidSDKVersion\":\"" + "20" + "\", " +
                "\"PlatformType\":\"MPlatformPrint\"" + ", " +
                "\"PrintModelType\":\"USB\"}";
        if (1 != loadjni.initLLD(json_Str, 3)) {
            Log.e("HealthOne", "Dll init failed!");
        }
    }

    public int connectUsb(Context context) {
        try {
            usbManager = (UsbManager) context.getSystemService(Context
                    .USB_SERVICE);

            if (usbManager == null) {
                return 1;//未得到USB管理者
            }
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            if (deviceList.size() == 0) {
                return 2;//不存在USB设备
            }

            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 9003) {
                    //找到了打印设备
                    usbDevice = device;
                    usbDeviceList.add(String.valueOf(device.getVendorId()));
                    usbDeviceList.add(String.valueOf(device.getProductId()));
                }
            }
            if (usbDevice == null) {
                return 3;//未找到打印机设备
            }
            for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
                UsbInterface intf = usbDevice.getInterface(i);
                usbInterface = intf;
                break;
            }

            if (usbInterface != null) {
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint usbEndPoint = usbInterface.getEndpoint(i);
                }

                UsbDeviceConnection usbDConn = null;
                if (usbManager.hasPermission(usbDevice)) {
                    usbDConn = usbManager.openDevice(usbDevice);
                    if (usbDConn == null) {
                        return 4;//打开数据发送端口失败
                    }
                    if (usbDConn.claimInterface(usbInterface, true)) {
                        usbDeviceConn = usbDConn;
                        getEndpoint(usbDeviceConn, usbInterface);
                    } else {
                        usbDConn.close();
                        return 5;//获取USB传出节点口失败
                    }
                } else {
                    return 6;//没有被授予连接权限
                }
            } else {
                return 7;//Usb interface is not exist!
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        return 0;//连接成功
    }

    public void printPicture(String path) {
        try {
            printJobDataTotalLen = 0;
            printJobDataSendTotalLen = 0;

//            String srcImgPath = Environment.getExternalStorageDirectory()
// .toString() + "/1.jpg";
            String srcImgPath = path;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 1;//缩放倍数
            opt.inJustDecodeBounds = false;
            //BMP图片数据
            Bitmap bmp = BitmapFactory.decodeFile(srcImgPath, opt);
            String printData = EncodingUtils.getString(getBmpToByte(bmp),
                    "ISO-8859-1");
            int combinationPackageResult = unPack(printData);

            // 打印机IP地址
            String serverIp = "127.0.0.1";
            // 打印机Port
            int serverPort = 9100;
            // 纸张尺寸宽度（像素）
            int printPageWPixel = 4736;
            // 纸张尺寸高度（像素）
            int printPageHPixel = 6784;
            // 打印份数
            int printNumCopies = 1;
            // 打印页数
            int printPages = 1;
            //BMP图片数据大小
            int srcImgDataSize = printData.length();
            //BMP图片宽度（像素），该值需要根据实际图片像素填写
            int srcImgWPixel = bmp.getWidth();
            //BMP图片高度（像素），该值需要根据实际图片像素填写
            int srcImgHPixel = bmp.getHeight();
            //BMP图片位深度（界面层须提供RGB 24位的BMP图片数据）
            int srcImgBitCount = 24;
            // 是否云打印（默认0）
            int isCloudPrint = 0;
            // 云打印类型（默认0）
            int cloudPrintType = 0;

            loadjni.sendDataToServer(serverIp, serverPort, printPageWPixel,
                    printPageHPixel, printNumCopies, printPages, srcImgDataSize,
                    srcImgWPixel, srcImgHPixel, srcImgBitCount, isCloudPrint,
                    cloudPrintType);
            byte[] printJob = loadjni.getPrintJobData();
            printJobDataTotalLen = printJob.length;

            //DisplayToast("打印作业数据大小: " + String.valueOf(printJobDataTotalLen));

            int printJobIndex = 0;
            int printJobPackagesCount = printJob.length / 10000;
            int count2 = printJob.length % 10000;
            if (count2 != 0) {
                printJobPackagesCount += 2;
            }

            for (int j = 1; j < printJobPackagesCount; j++) {

                if (j == printJobPackagesCount - 1) {
                    byte[] fileblock = new byte[count2];

                    for (int i = 0; i < count2; i++) {
                        fileblock[i] = printJob[printJobIndex];
                        printJobIndex++;
                    }

                    printJobDataSendTotalLen += usbDeviceConn.bulkTransfer
                            (usbEndPointOut, fileblock, fileblock.length, 3000);
                } else {
                    byte[] fileblock = new byte[10000];

                    for (int i = 0; i < 10000; i++) {
                        fileblock[i] = printJob[printJobIndex];
                        printJobIndex++;
                    }
                    printJobDataSendTotalLen += usbDeviceConn.bulkTransfer
                            (usbEndPointOut, fileblock, fileblock.length, 3000);
                }
            }
//            DisplayToast("打印作业数据大小: " + String.valueOf
// (printJobDataTotalLen) + "\n" + "打印作业数据已打印大小" + ": " + String.valueOf
// (printJobDataSendTotalLen));
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    // 获取USB传出节点口
    private void getEndpoint(UsbDeviceConnection connection, UsbInterface
            intf) {
        if (intf.getEndpoint(0) != null) {
            UsbEndpoint endPoint = intf.getEndpoint(0);

            if (endPoint.getDirection() == UsbConstants.USB_DIR_OUT)
                usbEndPointOut = endPoint;
        } else {
            UsbEndpoint endPoint = intf.getEndpoint(1);

            if (endPoint.getDirection() == UsbConstants.USB_DIR_OUT)
                usbEndPointOut = endPoint;
        }
    }

    private byte[] getBmpToByte(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        byte[] rgb = addBMP_RGB_888(pixels, w, h);
        byte[] header = addBMPImageHeader(rgb.length);
        byte[] infos = addBMPImageInfosHeader(rgb.length, w, h);

        byte[] buffer = new byte[54 + rgb.length];
        System.arraycopy(header, 0, buffer, 0, header.length);
        System.arraycopy(infos, 0, buffer, 14, infos.length);
        System.arraycopy(rgb, 0, buffer, 54, rgb.length);

        return buffer;
    }

    private byte[] addBMP_RGB_888(int[] b, int w, int h) {
        int len = b.length;
        System.out.println(b.length);
        byte[] buffer = new byte[w * h * 3];
        int offset = 0;

        for (int i = len - 1; i >= w - 1; i -= w) {
            int end = i, start = i - w + 1;
            for (int j = start; j <= end; j++) {
                buffer[offset] = (byte) (b[j]);
                buffer[offset + 1] = (byte) (b[j] >> 8);
                buffer[offset + 2] = (byte) (b[j] >> 16);
                offset += 3;
            }
        }

        return buffer;

    }

    private byte[] addBMPImageHeader(int size) {
        byte[] buffer = new byte[14];
        buffer[0] = 0x42;
        buffer[1] = 0x4D;
        buffer[2] = (byte) ((size + 54) >> 0);
        buffer[3] = (byte) ((size + 54) >> 8);
        buffer[4] = (byte) ((size + 54) >> 16);
        buffer[5] = (byte) ((size + 54) >> 24);
        buffer[6] = 0x00;
        buffer[7] = 0x00;
        buffer[8] = 0x00;
        buffer[9] = 0x00;
        buffer[10] = 0x36;
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        return buffer;
    }

    private byte[] addBMPImageInfosHeader(int size, int w, int h) {
        byte[] buffer = new byte[40];

        // 这个是固定的 BMP 信息头要40个字节
        buffer[0] = 0x28;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = 0x00;

        // 宽度 地位放在序号前的位置 高位放在序号后的位置
        buffer[4] = (byte) (w >> 0);
        buffer[5] = (byte) (w >> 8);
        buffer[6] = (byte) (w >> 16);
        buffer[7] = (byte) (w >> 24);

        // 长度 同上
        buffer[8] = (byte) (h >> 0);
        buffer[9] = (byte) (h >> 8);
        buffer[10] = (byte) (h >> 16);
        buffer[11] = (byte) (h >> 24);

        // 总是被设置为1
        buffer[12] = 0x01;
        buffer[13] = 0x00;

        // 比特数 像素 32位保存一个比特 这个不同的方式(ARGB 32位 RGB24位不同的!!!!
        buffer[14] = 0x18;
        buffer[15] = 0x00;

        // 0-不压缩 1-8bit位图
        // 2-4bit位图 3-16/32位图
        // 4 jpeg 5 png
        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = 0x00;
        buffer[19] = 0x00;

        // 说明图像大小
        buffer[20] = (byte) size;
        buffer[21] = (byte) (size >> 8);
        buffer[22] = (byte) (size >> 16);
        buffer[23] = (byte) (size >> 24);

        // 水平分辨率
        buffer[24] = 0x00;
        buffer[25] = 0x00;
        buffer[26] = 0x00;
        buffer[27] = 0x00;

        // 垂直分辨率
        buffer[28] = 0x00;
        buffer[29] = 0x00;
        buffer[30] = 0x00;
        buffer[31] = 0x00;

        // 0 使用所有的调色板项
        buffer[32] = 0x00;
        buffer[33] = 0x00;
        buffer[34] = 0x00;
        buffer[35] = 0x00;

        // 不开颜色索引
        buffer[36] = 0x00;
        buffer[37] = 0x00;
        buffer[38] = 0x00;
        buffer[39] = 0x00;
        return buffer;
    }

    private int unPack(String printDataIn) {
        int PackageResult = 0;

        if (printDataIn.length() <= 10240) {
            char[] printDataArray = printDataIn.toCharArray();

            int printDataSize = printDataArray.length;
            Runtime.getRuntime().gc();
            PackageResult = loadjni.combinationPackage(printDataArray,
                    printDataSize, printDataSize, printDataSize);
        } else {
            int countsend = 0;
            for (countsend = 0; printDataIn.length() / 10240 > countsend;
                 countsend++) {

                char[] printDataArray = printDataIn.substring(10240 *
                        (countsend), 10240 * (countsend + 1)).toCharArray();
                int printDataSize = printDataArray.length;
                PackageResult += loadjni.combinationPackage(printDataArray,
                        printDataSize, printDataSize, printDataIn.length());
            }
            if (printDataIn.length() % 10240 != 0) {
                char[] printDataArray = printDataIn.substring((printDataIn
                        .length() / 10240) * 10240, printDataIn.length())
                        .toCharArray();
                int printDataSize = printDataArray.length;
                PackageResult += loadjni.combinationPackage(printDataArray,
                        printDataSize, printDataSize, printDataIn.length());
            }
        }
        return PackageResult;
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice) intent
                                .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager
                                .EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                            }
                        } else {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
        }
    };
    public void printPicture(Bitmap bmp) {
        try {
            printJobDataTotalLen = 0;
            printJobDataSendTotalLen = 0;
            String printData = EncodingUtils.getString(getBmpToByte(bmp),
                    "ISO-8859-1");
            int combinationPackageResult = unPack(printData);

            // 打印机IP地址
            String serverIp = "127.0.0.1";
            // 打印机Port
            int serverPort = 9100;
            // 纸张尺寸宽度（像素）
            int printPageWPixel = 4736;
            // 纸张尺寸高度（像素）
            int printPageHPixel = 6784;
            // 打印份数
            int printNumCopies = 1;
            // 打印页数
            int printPages = 1;
            //BMP图片数据大小
            int srcImgDataSize = printData.length();
            //BMP图片宽度（像素），该值需要根据实际图片像素填写
            int srcImgWPixel = bmp.getWidth();
            //BMP图片高度（像素），该值需要根据实际图片像素填写
            int srcImgHPixel = bmp.getHeight();
            //BMP图片位深度（界面层须提供RGB 24位的BMP图片数据）
            int srcImgBitCount = 24;
            // 是否云打印（默认0）
            int isCloudPrint = 0;
            // 云打印类型（默认0）
            int cloudPrintType = 0;

            loadjni.sendDataToServer(serverIp, serverPort, printPageWPixel,
                    printPageHPixel, printNumCopies, printPages, srcImgDataSize,
                    srcImgWPixel, srcImgHPixel, srcImgBitCount, isCloudPrint,
                    cloudPrintType);
            byte[] printJob = loadjni.getPrintJobData();
            printJobDataTotalLen = printJob.length;

            //DisplayToast("打印作业数据大小: " + String.valueOf(printJobDataTotalLen));

            int printJobIndex = 0;
            int printJobPackagesCount = printJob.length / 10000;
            int count2 = printJob.length % 10000;
            if (count2 != 0) {
                printJobPackagesCount += 2;
            }

            for (int j = 1; j < printJobPackagesCount; j++) {

                if (j == printJobPackagesCount - 1) {
                    byte[] fileblock = new byte[count2];

                    for (int i = 0; i < count2; i++) {
                        fileblock[i] = printJob[printJobIndex];
                        printJobIndex++;
                    }
                    try {
                        printJobDataSendTotalLen += usbDeviceConn.bulkTransfer
                                (usbEndPointOut, fileblock, fileblock.length,
                                3000);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                } else {
                    byte[] fileblock = new byte[10000];

                    for (int i = 0; i < 10000; i++) {
                        fileblock[i] = printJob[printJobIndex];
                        printJobIndex++;
                    }
                    try {
                        printJobDataSendTotalLen += usbDeviceConn.bulkTransfer
                                (usbEndPointOut, fileblock, fileblock.length,
                                3000);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }
}
