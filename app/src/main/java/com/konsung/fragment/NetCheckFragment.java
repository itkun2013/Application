package com.konsung.fragment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.util.UiUitls;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/10/25 0025.
 * 网络检测页面
 */

public class NetCheckFragment extends BaseFragment {

    @InjectView(R.id.start_net_check)
    TextView tvStartCheck;
    @InjectView(R.id.net_work)
    TextView tvNetWork;
    @InjectView(R.id.wifi_work)
    TextView tvWifiWork;
    //wifi检测结果标识
    private boolean isWifiAvilable;
    //有线网络检测结果标识
    private boolean isNetAvilable;
    private WifiManager wifiManager;
    private Context context;
    //默认访问的主机地址
    private final String hostIp = "www.baidu.com";
    //ping外网次数
    private final int pingNum = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_check_layout, null);
        ButterKnife.inject(this, view);
        context = getActivity();
        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        initListener();
        return view;
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        tvStartCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UiUitls.isNetworkConnected(context)) {
                    UiUitls.showProgress(context, context.getString(R.string.net_checking));
                    //启动网络检测
                    UiUitls.postShortThread(new Runnable() {
                        @Override
                        public void run() {
                            //wifi检测
                            isWifiAvilable = UiUitls.isNetAvilable(hostIp, pingNum);
                            //关闭wifi，检测有线网络
                            wifiManager.setWifiEnabled(false);
                            //有线检测
                            isNetAvilable = UiUitls.isNetAvilable(hostIp, pingNum);
                            UiUitls.post(runnable);
                        }
                    });
                } else {
                    UiUitls.toast(context, context.getString(R.string.net_unvaliable));
                }
            }
        });
    }

    /**
     * 检测完成后的结果显示
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            UiUitls.hideProgress();
            //重新打开wifi
            wifiManager.setWifiEnabled(true);
            if (isNetAvilable) {
                //有线网络正常
                tvNetWork.setText(context.getString(R.string.net_normal));
            } else {
                //有线网络异常
                tvNetWork.setText(context.getString(R.string.net_unnormal));
            }

            if (isWifiAvilable) {
                //wifi网络正常
                tvWifiWork.setText(context.getString(R.string.wifi_normal));
            } else {
                //wifi网络异常
                tvWifiWork.setText(context.getString(R.string.wifi_unnormal));
            }
        }
    };
}
