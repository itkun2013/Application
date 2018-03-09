package com.example.mtreader;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.konsung.util.GlobalConstant;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 明泰控制类
 */
public class Mi32Api {

    private MainActivity loadMt332 = new MainActivity();
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private int dev = 0;
    private int fd = -1;
    private PendingIntent pendingIntent;
    private Context mContext = null;

    /**
     * 构造方法
     * @param context 上下文
     */
    public Mi32Api(Context context) {
        mContext = context;
        initDevice(mContext);
    }

    /**
     * 打开设备
     * @param context 上下文
     * @return 是否打开设备
     */
    public boolean openDevice(Context context) {
//        initDevice(context);
        connectDevice(context);
        // 打开设备
        dev = loadMt332.opendevicefd(fd);
        if (dev > 0) {
            GlobalConstant.IS_LINK_MI32API_SUCCESS = true;
            return true;
        } else {
            Log.d("HealthOne", "MI32 Device Open Failed!");
            GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
            return false;
        }
    }

    /**
     * 关闭设备
     * @return 是否关闭设备
     */
    public boolean closeDevice() {
        int status = loadMt332.closedevice();
        if (status == 0) {
            dev = 0;
            return true;
        } else {
            Log.d("HealthOne", "MI32 Device Close Failed!");
            return false;
        }
    }

    /**
     * 广播注销
     */
    public void unRegisterReceiver() {
        mContext.unregisterReceiver(mUsbReceiver);
    }
    /**
     * 获取M1卡序列号
     * @return 序列号字符串
     */
    public String getM1CardSn() {
        String cardSn = "";
        byte[] snAscArray = new byte[40];
        byte[] snArray = new byte[20];
        if (GlobalConstant.IS_LINK_MI32API_SUCCESS) {
            int status = loadMt332.rfcard(snArray);
            if (status == 0) {
                loadMt332.hex2asc(snArray, snAscArray, 4);
                // 序列号为8位asc码
                cardSn = new String(snAscArray, 0, 8);
            } else {
                cardSn = "";
            }
        }
        return cardSn;
    }

    /**
     * 创建一个USB相关的广播接收者
     */
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                // 监听用户授权设备连接事件
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            GlobalConstant.IS_LINK_MI32API_SUCCESS = openDevice(context);
                        } else {
                            Log.d("HealthOne", "permission denied for device " +
                                    "" + device);
                            GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
                        }
                    }
                }
                // 监听USB设备断开事件
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 初始化设备
     * @param context 上下文
     */
    public void initDevice(Context context) {
        // 注册一个广播接收者，用于接收USB设备广播
        pendingIntent = PendingIntent.getBroadcast(context, 0
                , new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbReceiver, filter);
    }

    /**
     * 连接设备
     * @param context 上下文
     */
    public void connectDevice(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return;
        }
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceList.size() == 0) {
            return;
        }
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            if (usbDevice.getVendorId() == 0x23a4 && (usbDevice.getProductId()
                    == 0x0218 || usbDevice.getProductId() == 0x020c)) {
                //程序是否有操作设备的权限
                if (usbManager.hasPermission(usbDevice)) {
                    UsbDeviceConnection connection = usbManager
                            .openDevice(usbDevice);
                    if (connection != null) {
                        fd = connection.getFileDescriptor();
                    }
                } else {
                    // 询问用户是否授予程序操作USB设备的权限
                    usbManager.requestPermission(usbDevice, pendingIntent);
                }
                break;
            }
        }
    }
}