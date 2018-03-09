package com.konsung.logic;

import android.content.Context;

import com.konsung.util.GlobalConstant;
import com.konsung.util.ParamDefine.LogGlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * webservice服务调用工具类
 * <p/>
 * Created by 匡国华 on 2015/11/3 0003.
 */
public class WsHandler {

    //请求时间限制
    private static final int timeOut = 30000;

    /**
     * Webservice调用通用类
     * @param context 上下文
     * @param params 键值对
     * @param methodName 调用方接口的方法名
     * @return 若出现异常则由调用这个方法的activity处理，若调用成功，
     * 则返回服务端传递过来的字符串，需要调用者自行解析成对象。
     * @throws Exception 异常
     */
    public static String invoke(Context context, Map<String, String> params
            , String methodName) throws Exception {
        // 命名空间
        String nameSpace = "http://webservice/";

        // EndPoint
        String endPoint = "http://" + SpUtils.getSp(context, "app_config",
                "ip", "") + ":" + SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.IP_PROT,
                GlobalConstant.PORT_DEFAULT) +
                SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.SERVER_ADDRESS,
                GlobalConstant.SERVER_FIX_ADDRESS);
        // SOAP Action
        String soapAction = "";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        Set<String> keys = params.keySet();
        for (String key : keys) {
            rpc.addProperty(key, params.get(key));
        }

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = false;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.encodingStyle = "UTF-8";


        HttpTransportSE transport = new HttpTransportSE(endPoint);
        transport.debug = true;

        // 调用WebService
        transport.call(soapAction, envelope);

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;

        // 获取返回的结果
        if (object != null) {
            String result = object.getProperty(0).toString();
            return result;
        }

        return "";
    }

    /**
     * 上传到康尚服务器的方法
     * @param checkData  上传的数据
     * @param nameSpace   域名空间
     * @param methodName  方法名
     * @param endPoint    请求的url
     * @return
     * @throws IOException io异常
     * @throws XmlPullParserException xml解析异常
     */
    public static boolean UpLoadToServer(String checkData, String nameSpace
            , String methodName, String endPoint) throws IOException, XmlPullParserException {
        String soapAction = "";
        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);
        rpc.addProperty("arg0", "");
        try {
            rpc.addProperty("arg1", new String(checkData.getBytes(), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = false;
        envelope.setOutputSoapObject(rpc);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE transport = new HttpTransportSE(endPoint, timeOut);
        transport.debug = true;
        // 调用WebService
        transport.call(soapAction, envelope);
        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        if (object != null) {
            //这里可能也需要修改
            String result = object.getProperty(0).toString();
            if (result.contains("10000")) {
                return true;
            } else {
                //记录日志
                BuglyLog.v(WsHandler.class.getName(), LogGlobalConstant.UPLOAD_FAIL + result);
                BuglyLog.v(WsHandler.class.getName(), checkData);
                BuglyLog.v(WsHandler.class.getName(), LogGlobalConstant.URL + endPoint);
                CrashReport.postCatchedException(new Throwable(LogGlobalConstant.UPLOAD_FAIL));
                return  false;
            }
        } else {
            return  false;
        }

    }
}
