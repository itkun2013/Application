package com.decard.healthcard;

/**
 * Created by QF on 2015/12/1.
 */
public class WSRead {

    public native void usbPermission(Object obj);
    public native int portOpen(String path, int baudrate);
    public native String readHealthCard();
    public native int portClose();

    static {
        System.loadLibrary("dc_health_card");
    }

}
