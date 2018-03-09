package com.example.mtreader;

/**
 * 加载libmt332.so库
 * 特殊说明：包名和类名必须为"com.example.mtreader"和MainActivity，否则mt332库可
 * 能加在不成功。明泰不愿意修改此包名和类名，因此我们只能这样沿用。
 */
public class MainActivity {

    static {
        try {
            System.loadLibrary("mt332");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设备操作函数
    public static native int hex2asc(byte hex[],byte asc[],int len);
    public static native int asc2hex(byte asc[],byte hex[],int len);
    public static native int opendevice();
    public static native int opendevicefd(int fd);
    public native int closedevice();
    public native String getversion();
    public native int devbeep(int Msec,int Msec_end,int times);
    public native int readeeprom(int addr,int len,byte hex[]);
    public native int writeeeprom(int add,int len,String data);
    public native int getcardstate();
    //CPU 卡操作函数
    public static native int ICCPowerOn(char cardset,byte atr[],int atrlen[]);
    public static native int ICCCommand(char cardset,byte cmd[],int cmdlen,byte resp[],int resplen[]);
    public static native int ICCPowerOff(char cardset);
    public static native int ICCResetBaud(int baud, char cardset, byte atr[], int atrlen[]);
    //非接CPU卡操作函数
    public static native int OpenCard(byte snr[],byte cardinfo[],int infolen[]);
    public static native int ExchangePro(byte cmd[],int cmdlen,byte resp[],int resplen[]);
    public static native int CloseCard();
    //M1卡操作函数
    public static native int rfcard(byte snr[]);
    public static native int rfreset();
    public static native int rfauthkey(char mode,char blockaddr,byte authkey[]);
    public static native int rfread(char blockaddr,byte rdata[]);
    public static native int rfwrite(char blockaddr,byte wdata[]);
    public static native int rfinitval(char blockaddr,int value);
    public static native int rfreadval(char blockaddr,int value[]);
    public static native int ReadNAN(int type,byte cardno[],byte holdername[],byte err[]);
    public static native int ReadSBInfo(byte info[],byte err[]);
    //磁条卡操作
    public static native int SetMagicMode(char mode);
    public static native int ReadMagic(byte track1[],byte track2[],byte track3[]);
    //SLE4442
    public static native int contactverify(byte cardtype[]);
    public static native int sle4442is42(byte CardState[]);
    public static native int sle4442read(byte nAddr, short nDLen,byte RecData[]);
    public static native int sle4442write(byte nAddr, short nWLen,byte sWriteData[]);
    public static native int sle4442pwdcheck(byte sKey[]);
    public static native int sle4442pwdmodify(byte sKey[]);
    public static native int sle4442probitread(byte nLen[],byte sProBitData[]);
    public static native int sle4442probitwrite(byte nAddr, short nWLen, byte sProBitData[]);
    public static native int sle4442errcountread(byte nErrCount[]);
    //******************************* SLE4428 ********************************************************//
    //检测是否为4428卡
    public static native int sle4428is28(byte CardState[]);
    public static native int sle4428read(short nAddr,short nDLen,byte sRecData[]);
    public static native int sle4428write(short nAddr, short nWLen,byte sWriteData[]);
    public static native int sle4428pwdcheck(byte sKey[]);
    public static native int sle4428pwdmodify(byte sKey[]);
    public static native int sle4428probitreaddata(short nAddr, short nDLen,byte sRecData[]);
    public static native int sle4428probitwritedata(short nAddr,short nWLen,byte sWriteData[]);
}
