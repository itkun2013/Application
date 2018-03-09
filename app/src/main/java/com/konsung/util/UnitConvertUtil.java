package com.konsung.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 单位转换工具类
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-04 10:19
 */
public class UnitConvertUtil {

    /*
     * 私有构造
     */
    private UnitConvertUtil() {

    }

    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    // Dp转换Px的方法
    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float dpToPx(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float spToPx(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
/*
    */
/**
 * byte[] 转16进制工具
 * @param src
 * @return
 *//*

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
*/

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString
     * (int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte
                    (hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] getByteformHexString(String hexString) {
        //去最高位
        byte[] bytes = hexStringToBytes(hexString);
        return changeByte(bytes);
    }

    public static byte[] changeByte(byte[] a) {
        List<Integer> iList = new ArrayList<Integer>();
        int len = 0;
        int a_length = 0;
        if (a != null) {
            a_length = a.length;
        } else {
            a_length = 0;
        }
        if (a_length % 2 == 1) {
            len = 1;
        }
        for (int i = 4; i < a_length - len; i++) {
            byte[] f = new byte[2];
            f[0] = a[i + 0];
            f[1] = a[i + 1];
            int v = toInt(f);
            if (v > 4096) {
                v = v % 4096;
            }
            iList.add(v);
            i++;
        }

        byte[] d = new byte[4 + iList.size() * 2];
        byte[] fb = toByteArray(iList.size(), 4);
        d[0] = fb[3];
        d[1] = fb[2];
        d[2] = fb[1];
        d[3] = fb[0];
        for (int i = 0; i < iList.size(); i++) {
            fb = toByteArray(iList.get(i), 2);
            d[4 + i * 2] = fb[1];
            d[4 + i * 2 + 1] = fb[0];
        }
        return d;
    }

    /**
     * 转换波形数据
     * @param value 波形数据
     * @return 转换后的数据
     * @throws Exception 转换异常
     */
    public static int[] intValue(String value) throws Exception {
        byte[] out = Base64.decode(value.getBytes(), Base64.DEFAULT);
        byte[] f = new byte[4];
        f[3] = out[0];
        f[2] = out[1];
        f[1] = out[2];
        f[0] = out[3];
        List<Integer> vList = new ArrayList<Integer>();
        for (int i = 4; i < out.length; i++) {
            f = new byte[2];
            f[1] = out[i + 0];
            f[0] = out[i + 1];
            vList.add(toInt(f));
            i++;
        }
        int[] b = new int[vList.size()];
        for (int i = 0; i < vList.size(); i++) {
            b[i] = vList.get(i);
        }
        return b;
    }

    /**
     * 字节转int
     * @param bRefArr 字节数据
     * @return int
     */
    private static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 转javabean为json
     *
     * @param bean
     * @return
     */
    public static String objectToJSON(Object bean) {
        //返回的json
        String json = "{";
        //几个属性
        int count;
        //当前位置
        int position = 0;
        Class cl = bean.getClass();
        Field[] fields = cl.getDeclaredFields();
        count = fields.length;
        try {
            for (Field field : fields) {
                position++;//从第一个开始
                field.setAccessible(true);
                json += "\"" + field.getName() + "\":\"" + field.get(bean) +
                        "\"";
                if (position != count) {
                    json += ",";
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        json += "}";
        return json;

    }

    /**
     * Convert Map to JSON
     *
     * @param map
     * @return
     */

    public static String mapToJSON(Map<String, Object> map) {
        String json = "{";
        int position = 0;
        for (String str : map.keySet()) {
            if (str.equals("recs")) {
                json += "\"" + str + "\":" + map.get(str);
            } else {
                json += "\"" + str + "\":" + "\"" + map.get(str) + "\"";
            }
            position++;
            if (position != map.keySet().size()) {
                json += ",";
            }
        }
        json += "}";

        return json;
    }

    public static String ecgwaveToString(String sixteenbytes) {
        //转化的float[]字符串
        String s = "";
        float _factor = 0.044f;

        //float[]的size
        int size = 0;
        byte[] bytes = hexStringToBytes(sixteenbytes);
        if (bytes != null) {
            size = bytes.length / 4;
        } else {
            return "";
        }
        ArrayList<Byte> data = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            data.add(bytes[i]);
        }
        float[] _point = new float[size];
        for (int i = 0; ; ) {
            if (data.size() > 16) {
                _point[i++] = (data.get(0) & 0xFF) + ((data.get(1) & 0x0F) <<
                        8);
                _point[i++] = (data.get(8) & 0xFF) + ((data.get(9) & 0x0F) <<
                        8);
                for (int j = 0; j < 8; j++) {
                    data.remove(0);
                }
            } else {
                break;
            }
        }
        s = Arrays.toString(_point);
        return s;
    }

}
