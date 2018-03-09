package com.konsung.defineview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dlx on 2017/10/25 0011.
 *
 * ip设置dialog弹出框 （软件更新和服务器类型公用）
 *
 */

public class IpPopupWindow extends Dialog {

    public static final int IP_TYPE_UPDATE = 0; //软件更新类型
    public static final int IP_TYPE_SERVER = 1; //服务器类型
    public static final int IP_TYPE_CLOUD = 2; //云平台类型
    public static final String STRING_IP = "ip";
    private final Activity context;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.et_ip)
    EditText etIp;
    @InjectView(R.id.et_port)
    EditText etPort;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;

    private OnCallBackListener listener;
    private int type;
    private String title;
    private String port;
    private String ip;

    private final int portMax = 65535;
    /**
     * 构造函数
     * @param context 上下文引用
     * @param type 类型
     */
    public IpPopupWindow(Activity context, int type) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        this.type = type;
        bindViewWithType();
        initPopwindow();
    }


    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_ip, null);
        ButterKnife.inject(this, view);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        initEvent();
        initView();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etIp.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(etPort.getWindowToken(), 0);
            }
        });
    }


    /**
     * 初始化显示
     *
     */
    private void initView() {
        tvTitle.setText(title);
        etIp.setText(ip);
        etPort.setText(port);
    }

    /**
     * 初始化数据
     */
    private void initEvent() {
        etIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();

                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etIp, InputMethodManager.SHOW_FORCED);
            }
        });

        etPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusableInTouchMode(true);
                v.requestFocus();

                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPort, InputMethodManager.SHOW_FORCED);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpPopupWindow.this.hide();
                if (listener != null) {
                    listener.onCancel(etIp.getText().toString(), etPort.getText().toString());
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }
                IpPopupWindow.this.hide();
                if (listener != null) {
                    listener.onCommit(etIp.getText().toString(), etPort.getText().toString());
                }
            }
        });

    }

    /**
     * 检测是否输入合法
     * @return true 输入合法  false 输入校验不通过
     */
    private boolean checkInput() {
        ip = etIp.getText().toString().trim();
        port = etPort.getText().toString().trim();
        switch (type) {
            case IP_TYPE_CLOUD:
                if (TextUtils.isEmpty(ip)) {
                    UiUitls.toast(context, R.string.cloud_ip_address_not_empty);
                    return false;
                }
                if (!UiUitls.isValidAddress(ip, port)) {
                    UiUitls.toast(context, R.string.confirm_efficacious_doman);
                    return false;
                }
                break;
            case IP_TYPE_UPDATE:
                if (TextUtils.isEmpty(ip)) {
                    UiUitls.toast(context, R.string.software_update_address_not_empty);
                    return false;
                }
                if (!UiUitls.isValidAddress(ip, port)) {
                    UiUitls.toast(context, R.string.confirm_efficacious_doman);
                    return false;
                }
                break;
            case IP_TYPE_SERVER:
                if (TextUtils.isEmpty(ip)) {
                    UiUitls.toast(context, R.string.server_ip_address_not_empty);
                    return false;
                }
                if (!JsonUtils.isValidIP(ip)) {
                    Toast.makeText(context, UiUitls.getString(
                            R.string.confirm_efficacious_ip),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            default:
                break;
        }

        if (TextUtils.isEmpty(port) || Integer.parseInt(port) > portMax) {
            UiUitls.toast(context, R.string.confirm_efficacious_ip_port);
            return false;
        }
        return true;
    }

    /**
     * 监听查询条件确定的回调
     * @param listener 接口对象
     */
    public void addCallBackListener(OnCallBackListener listener) {
        this.listener = listener;
    }

    /**
     * 回调接口
     */
    public interface OnCallBackListener {
        /**
         * 确定回调
         * @param ip ip地址
         * @param port 端口号
         */
        void onCommit(String ip, String port);
        /**
         * 取消回调
         * @param ip ip地址
         * @param port 端口号
         */
        void onCancel(String ip, String port);
    }

    /**
     * 设置类型
     * @param type 类型
     */
    public void setType(int type) {
        this.type = type;
        bindViewWithType();
    }

    /**
     * 根据type类型绑定显示
     */
    private void bindViewWithType() {
        switch (type) {
            case IP_TYPE_UPDATE:
                title = UiUitls.getString(R.string.ip_title_default);
                ip = SpUtils.getSp(context.getApplicationContext()
                        , GlobalConstant.APP_CONFIG, GlobalConstant.REFRESH_IP
                        , GlobalConstant.REFRESH_ADRESS);
                port = SpUtils.getSp(context.getApplicationContext()
                        , GlobalConstant.APP_CONFIG, GlobalConstant.REFRESH_IP_PORT
                        , GlobalConstant.REFRESH_ADRESS_PORT);
                break;
            case IP_TYPE_SERVER:
                title = UiUitls.getString(R.string.server_title_default);
                ip = SpUtils.getSp(context.getApplicationContext()
                        , GlobalConstant.APP_CONFIG, STRING_IP, "");
                port = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
                break;
            case IP_TYPE_CLOUD:
                title = UiUitls.getString(R.string.ip_configure_cloud_title);

                ip = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.CLOUD_IP, GlobalConstant.CIP);
                port = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.CLOUD_IP_PORT, GlobalConstant.CIP_PORT);
                break;
            default:
                break;
        }
    }
}
