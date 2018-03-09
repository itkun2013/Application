package com.konsung.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;

import com.decard.portdrivelibrary.utils.Quantity;
import com.example.mtreader.Mi32Api;
import com.konsung.R;
import com.konsung.fragment.AddPatientFragment;
import com.konsung.fragment.BaseFragment;
import com.konsung.fragment.ModifyPatientFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 *用户新增页面/用户修改页面
 */

public class AddPatientActivity extends BaseActivity {

    //用于判断新增和修改的标识
    private String flag = "";
    //用户id值
    private Long personId;
    private Bundle bundle;
    //usb监听权限
    private final String usbPermisson = "com.android.example.USB_PERMISSION";
    private BaseFragment baseFragment = null;
    //记录上次点击时间戳
    private long lastClickTime;
    //时间间隔
    private final int limitTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initTitle();
        initView();
        initListener();
    }

    /**
     * 导航栏初始化
     */
    private void initTitle() {
        getSupportActionBar().show();
        bundle = getIntent().getExtras();
        flag = bundle.getString(MainActivity.PERSONKEY);
        if (flag.equals(MainActivity.ADDPATIENTFRAGMENT)) {
            setLeftButtonText(getRecString(R.string.add_person));
            initData();
            initRegister();
        } else {
            setLeftButtonText(getRecString(R.string.modify_person));
            //获取传递过来的用户id值
            personId = getIntent().getExtras().getLong(GlobalConstant.idKey);
        }
    }

    /**
     * view初始化
     */
    private void initView() {
        if (flag.equals(MainActivity.ADDPATIENTFRAGMENT)) {
            baseFragment = new AddPatientFragment();
            UiUitls.switchToFragment(R.id.measure_content, baseFragment
                    , MainActivity.ADDPATIENTFRAGMENT, false, getFragmentManager());
        } else {
            baseFragment = new ModifyPatientFragment();
            baseFragment.setArguments(bundle);
            UiUitls.switchToFragment(R.id.measure_content, baseFragment
                    , MainActivity.MODIFYPATIENTFRAGMENT, false, getFragmentManager());
        }
    }

    /**
     * 数据初始化
     */
    private void initData() {
        //页面初始化强制打开刷卡权限
        if (GlobalConstant.MI32API == null) {
            GlobalConstant.MI32API = new Mi32Api(AddPatientActivity.this);
        }
        GlobalConstant.MI32API.openDevice(AddPatientActivity.this);
        GlobalConstant.isAddPatientActivityDestyoy = false;
    }

    /**
     * 广播注册
     */
    private void initRegister() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(Quantity.ACTION_USB_PERMISSION);
        filter.addAction(usbPermisson);
        registerReceiver(usbStateReceiver, filter);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        if (flag.equals(MainActivity.ADDPATIENTFRAGMENT)) {
            //返回按钮监听事件重写
            getIvBack().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime > limitTime) {
                        GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
                        popActivity();
                    }
                    lastClickTime = currentTime;
                }
            });
        }
    }

    /**
     * 安全退出activity方法
     * @param isNeedIntent 标识是否需要跳转至居民列表页
     */
    public void exitActivitySafe(boolean isNeedIntent) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(baseFragment);
        fragmentTransaction.commit();
        popActivity();
        if (isNeedIntent) {
            //跳转至居民列表页
            pushActivity(AddPatientActivity.this, PatientListActivity.class);
        }
    }

    /**
     * usb插入权限允许监听器
     */
    private BroadcastReceiver usbStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //usb连接
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (GlobalConstant.MI32API == null) {
                    GlobalConstant.MI32API = new Mi32Api(AddPatientActivity.this);
                }
                GlobalConstant.MI32API.openDevice(AddPatientActivity.this);
                //usb试图断开
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
            } else if (Quantity.ACTION_USB_PERMISSION.equals(action)) {
                GlobalConstant.IS_LINK_MI32API_SUCCESS = false;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (GlobalConstant.MI32API != null) {
            GlobalConstant.MI32API.unRegisterReceiver();
            GlobalConstant.MI32API.closeDevice();
            GlobalConstant.MI32API = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag.equals(MainActivity.ADDPATIENTFRAGMENT)) {
            unregisterReceiver(usbStateReceiver);
        }
       GlobalConstant.isAddPatientActivityDestyoy = true;
    }
}
