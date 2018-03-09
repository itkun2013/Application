package com.konsung.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.WaitingDialog;
import com.konsung.dowmload.DownloadAppPatchManager;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.IdCardGenerator;
import com.konsung.util.SpUtils;
import com.konsung.util.TestDataGenerator;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chenshuo on 2015/12/11.
 */
public class AboutFragment extends BaseFragment implements View
        .OnClickListener {
    @InjectView(R.id.version_code)
    EditText etVersionCode;
    @InjectView(R.id.version_name)
    EditText etVersionName;
    @InjectView(R.id.adapter_version_code)
    EditText etAdapterVersionCode;
    @InjectView(R.id.et_sequence)
    EditText etSequence;
    @InjectView(R.id.detection)
    TextView tvDetection;
    @InjectView(R.id.coding_et)
    EditText etCoding;
    ImageTextButton btnChange;
    @InjectView(R.id.sqlite_version)
    EditText etSqliteVersion;
    @InjectView(R.id.para_module)
    EditText etParaModule;
    @InjectView(R.id.btn_creat_data)
    TextView create;
    private boolean mIsCoding = false;
    //提示加载大量数据的对话框
    private WaitingDialog mWaitingDialog;
    private static final String TAG = "AboutFragment";
    //上下文引用
    private Context context = null;
    //记录扫描枪输入的数据
    private StringBuffer sb;
    //记录扫描枪是否大写
    private int caps = 0;
    //标记用户输入时间
    private boolean flag = true;
    private boolean isEntering = false;
    private final int thousandValue = 1000;
    //生产用户记录条数
    private final int productNum = 300;
    //身份证无效号码数量
    private final int invalidateNum = 14;
    //截取开始位置
    private final int subStart = 6;
    //keyCode值
    private final int keyCodeValue = 59;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);
        ButterKnife.inject(this, view);
        context = getActivity();
        initView();
        //设置监听事件
        initEvent();
        return view;
    }

    /**
     * 设置监听事件的方法
     */
    private void initEvent() {
        tvDetection.setOnClickListener(this);
        //获取是否输入设备号
        mIsCoding = SpUtils.getSp(getActivity(), GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_ISCODING, false);
        try {
            //获得外接USB输入设备的信息
            Process p = Runtime.getRuntime().exec("cat /proc/bus/input/devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                sb.append(line.trim() + "\n");
            }
            if (sb.toString().contains("Bar Code Scanner")) {
                etCoding.setEnabled(true);
                etCoding.setFocusableInTouchMode(true);
                etCoding.requestFocus();
                isEntering = true;
                scannerCoding();
            } else {
                isEntering = false;
                etCoding.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        //如果不是超级用户登录，就用扫描枪输入，如果是就允许用户修改设备号
        if (!GlobalConstant.TEST_PASSWORD.equals(getString(R.string
                .test_password))) {
            getActivity().getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            if ("".equals(etCoding.getText().toString())) {
                mIsCoding = false;
            } else {
                mIsCoding = true;
            }
            create.setVisibility(View.INVISIBLE);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            etCoding.setEnabled(true);
            etCoding.setFocusableInTouchMode(true);
            etCoding.requestFocus();
            isEntering = true;
            mIsCoding = false;
            create.setVisibility(View.VISIBLE);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为当前用户添加大量数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (UiUitls.checkFileIsFull(Environment.getExternalStorageDirectory())) {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.toast(context, getRecString(R.string.storage_limits));
                                }
                            });
                            return;
                        }
                        initTimeData();
                    }
                }).start();
            }
        });
        etCoding.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SpUtils.saveToSp(getActivity(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.APP_CODING, s.toString());
            }
        });
    }

    /**
     * 模拟大量的数据
     */
    private void initTimeData() {
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                mWaitingDialog = new WaitingDialog(getActivity()
                        , getRecString(R.string.wait_for_load));
                mWaitingDialog.setCancelable(false);
                mWaitingDialog.show();
                mWaitingDialog.setText(getRecString(R.string.wait_for_load));
            }
        });
        //获取当前用户
        String idcard = SpUtils.getSp(getActivity(), GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        if (TextUtils.isEmpty(idcard)) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    if (mWaitingDialog != null) {
                        mWaitingDialog.dismiss();
                    }
                }
            });
            return;
        }
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        MeasureDataBeanDao measureDataBeanDao = DBDataUtil.getMeasureDao();
        //查询当前用户的值
        List<MeasureDataBean> measures = DBDataUtil.getMeasures(idcard);
        //生产身份证号码的类
        IdCardGenerator g = new IdCardGenerator();
        for (int i = 0; i < productNum; i++) {
            String card = g.generate();
            String patientUuid = UUIDGenerator.getUUID();
            PatientBean bean = new PatientBean();
            //身份证号
            bean.setCard(card);
            //UUID值
            bean.setIdCard(patientUuid);
            //截取身份证号码中的生日
            //当身份证号码长度小于14时作为无效号码
            if (card.length() < invalidateNum) {
                continue;
            }
            String s = card.substring(subStart, invalidateNum);
            s += "0000";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
            try {
                bean.setBirthday(sdf.parse(s));
            } catch (ParseException e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
            //给排序时间戳设置当前时间
            bean.setSortDate(new Date());
            bean.setName(TestDataGenerator.getChineseName());
            bean.setAge(UiUitls.getAge(bean.getCard()));
            patientDao.insert(bean);
            for (int j = 0; j < measures.size(); j++) {
                MeasureDataBean b = new MeasureDataBean();
                MeasureDataBean measureDataBean = null;
                try {
                    //数据赋值克隆 （直接赋值会导致内存中数据对象指向还是同一内存地址
                    // ，会造成数据重复更新）
                    measureDataBean = (MeasureDataBean) measures.get(j).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                measureDataBean.setIdcard(patientUuid);
                measureDataBean.setId(b.getId());
                measureDataBean.setPatientId(bean.getId());
                measureDataBean.setUuid(UUIDGenerator.getUUID());
                measureDataBean.setUploadFlag(false);
                measureDataBeanDao.insert(measureDataBean);
            }
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }
                UiUitls.toast(context, UiUitls.getString(R.string.create_success));
            }
        });

    }

    /**
     * 扫描条形码
     */
    private void scannerCoding() {
        sb = new StringBuffer();
        etCoding.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() {
                return 0;
            }

            @Override
            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
                //判断是不是超级管理员账户进来
                if (GlobalConstant.TEST_USER.equals(getString(R.string
                        .test_user)) && GlobalConstant.TEST_PASSWORD
                        .equals(getRecString(R.string.test_password))) {
                } else {
                    //如果不是管理账户进来，不允许输入第二次
                    if (mIsCoding) {
                        UiUitls.toastContent(context, getRecString(R.string.coding_repeat));
                        return false;
                    }
                }
                if (flag) {
                    sb = new StringBuffer();
                    flag = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(thousandValue);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                CrashReport.postCatchedException(e);
                            }
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.toast(context, getRecString(R.string.coding_sucess));
                                    if (sb.toString().length() > 0) {
                                        etCoding.setText("");
                                        etCoding.setText(sb.toString());
                                        flag = true;
                                        clickCoding();
                                        SpUtils.saveToSp(getActivity(), GlobalConstant.APP_CONFIG
                                                , GlobalConstant.APP_ISCODING, true);
                                        mIsCoding = true;
                                    }
                                }
                            });

                        }
                    }).start();
                }
                if (keyCode == keyCodeValue) {
                    caps++;
                }
                if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z || keyCode >=
                        KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    String s = event.keyCodeToString(keyCode);
                    String s1 = s.substring(s.length() - 1);
                    if (caps > 0) {
                        s1 = s1.toUpperCase();
                        caps--;
                    } else {
                        s1 = s1.toLowerCase();
                    }
                    sb.append(s1);
                }
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable content, int states) {

            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        etSequence.setText(android.os.Build.SERIAL);
        try {
            PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(getActivity()
                    .getPackageName(), PackageManager.GET_CONFIGURATIONS);
            String versionName = pinfo.versionName;
            int versionCode = pinfo.versionCode;
            int adapterVersionCode = UiUitls.getAppViersion("org.qtproject" +
                    ".qt5.android.bindings");
            etVersionName.setText(versionName);
            this.etVersionCode.setText(String.valueOf(versionCode));
            //当没有安装AppDevice软件时，adapterVersionCode为0
            if (adapterVersionCode != 0) {
                this.etAdapterVersionCode.setText(String.valueOf(adapterVersionCode));
            }
            //显示参数模块版本
            String paraBoardVersion = SpUtils.getSp(getActivity()
                    .getApplicationContext(), "app_config", "paraBoardVersion", "");
            if (!getString(R.string.default_value).equals(paraBoardVersion)) {
                etParaModule.setText(paraBoardVersion);
            }
        } catch (PackageManager.NameNotFoundException e) {
            CrashReport.postCatchedException(e);
        }
        tvDetection.setText(getString(R.string.check_update_soft));
        etCoding.setEnabled(false);
        etSqliteVersion.setText(GlobalConstant.DATABASE_VERSION + "");
        //设备号初始化
        String sp = SpUtils.getSp(getActivity(), GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_CODING, "");
        etCoding.setText(sp);
    }

    @Override
    public void onClick(View v) {
        if (v == tvDetection) {
            clickRefresh();
        } else if (v == btnChange) {
            clickChange();
        }
    }

    /**
     * 点击变化
     */
    private void clickChange() {
        btnChange.setVisibility(View.GONE);
        etCoding.setEnabled(true);
    }

    /**
     * 保存一体机编码
     */
    private void clickCoding() {
        //获取用户输入的信息编码
        String coding = etCoding.getText().toString();
        SpUtils.saveToSp(getActivity(), GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_CODING, coding);
    }

    /**
     * 点击检测软件更新
     */
    private void clickRefresh() {
        //更新操作
        GlobalConstant.mainPageUpdate = false;
        new DownloadAppPatchManager(getActivity(), new Handler()).checkUpdate();
    }

    @Override
    public void initDatas() {
        if (isEntering) {
            etCoding.setEnabled(true);
            etCoding.setFocusableInTouchMode(true);
            etCoding.requestFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clickCoding();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clickCoding();
        //离开关于界面的时候会输出一次信息
        showAboutInfo();
    }

    /**
     * 当接入扫描枪时，避免Activity被重新初始化，采用此函数进行截取该事件
     * 需要截取的事件要在AndroidManifest.xml的文件中的对应的Activity属性中设置
     */
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        // 初始化扫描枪监听事件
        initEvent();
    }

    /**
     * 离开关于界面的时候会输出一次信息,此log不可被删除
     */
    private void showAboutInfo() {
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "版本名 ："
                + etVersionName.getText().toString());
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "版本号 ："
                + etVersionCode.getText().toString());
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "适配器 ：" + etAdapterVersionCode.getText().
                toString());
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "数据库版本 ：" + etSqliteVersion.getText().
                toString());
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "多参模块 ："
                + etParaModule.getText().toString());
        Log.e(GlobalConstant.LOG_TAG, TAG + ": " + "设备号 ：" + etCoding.getText().toString());
    }
}
