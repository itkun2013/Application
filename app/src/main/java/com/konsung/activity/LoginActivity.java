package com.konsung.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.greendao.dao.UserBeanDao;
import com.konsung.R;
import com.konsung.bean.CheckData;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.UserBean;
import com.konsung.fragment.SkinSettingFragment;
import com.konsung.refresh.UploadRefreshVersion;
import com.konsung.sqlite.DBHelper;
import com.konsung.sqlite.DatabaseContext;
import com.konsung.sqlite.MigrationHelper;
import com.konsung.util.ActivityUtils;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.ParamDefine.LogGlobalConstant;
import com.konsung.util.RequestUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;
import com.konsung.util.global.Constant;
import com.konsung.util.global.GlobalNumber;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import upgrade.parameter.ParameterGlobal;

/**
 * Created by JustRush on 2015/8/25.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private static final int TEXTSIZE = 18;
    //勾选了所有测量配置项
    private final int allSelect = 4063;
    //appDevice状态值，3为未启动
    private final int status = 3;
    @InjectView(R.id.rl_background)
    RelativeLayout rlBackground;
    @InjectView(R.id.username_ed)
    EditText usernameEd;
    @InjectView(R.id.password_ed)
    EditText passwordEd;
    @InjectView(R.id.login_btn)
    Button loginBt;
    @InjectView(R.id.register_btn)
    Button registerBt;
    @InjectView(R.id.username_iv)
    ImageView ivUsernameClear;
    @InjectView(R.id.password_iv)
    ImageView ivPasswordClear;
    //几个 100条数据
    private long countNum;
    //不足100条剩余的数据
    private long restData;
    //计数器
    private int count;
    //查询限制条数
    private final int limit = 100;
    //数据查询起始位置
    private int offPostion;
    //登录成功标识
    private final String successFlag = "SUCESS";
    private long patientSizeTotal;
    //限制onResume方法只升级一次标识
    //因为多应用1.3.0非常规操作下升级版本打开软件该页面会出现onResume方法三次执行，所以，增加强制限制
    private boolean isOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_login);
        //防止弹出软键盘遮挡编辑框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);
        GlobalConstant.ACTIVITY_IS_RUNNING = false;
        initView();
        initBackground();
        initListener();
    }

    /**
     * 清除栈中所有activity对象
     */
    private void clearStack() {
        if (ActivityUtils.activityStack != null && ActivityUtils.activityStack.size() > 0) {
            ActivityUtils.getActivityUtils().popAllActivity();
        }
    }

    /**
     * 创建皮肤默认文件夹
     */
    private void createSkinFolder() {
        //创建主页背景存放文件夹
        File backgroundFolder = new File(SkinSettingFragment.BACKGROUND_PATH);
        if (!backgroundFolder.exists()) {
            backgroundFolder.mkdirs();
        }
        //创建Logo存放文件夹
        File logoFolder = new File(SkinSettingFragment.LOGO_PATH);
        if (!logoFolder.exists()) {
            logoFolder.mkdirs();
        }
    }

    /**
     * 处理升级兼容数据
     */
    private void initUpdateData() {
        if (SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG, GlobalConstant
                .UPDATE_DATA, true)) {
            SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                    , GlobalConstant.SERVER_ADDRESS, GlobalConstant.SERVER_FIX_ADDRESS);
            UiUitls.showProgress(this, UiUitls.getString(R.string.loading_local_data));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MigrationHelper.saveDataToSdFile();
                    //false标识数据库已经存在 true则表示数据库是被复制过来
                    if (GlobalConstant.isExitsDataBaseInUsbMem) {
                        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
                        while (!GlobalConstant.dataBaseUpgradeFinish) {
                            //等待数据库更新完成后的数据转换操作 该循环用作等待
                        }
                        patientDao = DBDataUtil.getPatientDao();
                        MeasureDataBeanDao measureDao = DBDataUtil.getMeasureDao();
                        //用户总记录条数
                        patientSizeTotal = patientDao.count();
                        if (patientSizeTotal > 0) {
                            restData = patientSizeTotal % limit;
                            if (restData == 0) {
                                countNum = patientSizeTotal / limit;
                            } else {
                                countNum = patientSizeTotal / limit + 1;
                            }
                            for (int i = 0; i < countNum; i++) {
                                switchDataByPage(i);
                            }
                            //回显上一版本的当前用户
                            String idCard = SpUtils.getSp(LoginActivity.this
                                    , GlobalConstant.APP_CONFIG, GlobalConstant.ID_CARD, "");
                            if (!TextUtils.isEmpty(idCard)) {
                                PatientBean unique = DBDataUtil.getPatientByUnique(idCard);
                                if (unique != null) {
                                    //保存当前用户idcard值
                                    SpUtils.saveToSp(LoginActivity.this, GlobalConstant.APP_CONFIG
                                            , GlobalConstant.ID_CARD, unique.getIdCard());
                                }
                            }
                        }
                    } else {
                        UiUitls.hideProgress();
                        //创建在sd卡上访问的数据库
                        new DBHelper(new DatabaseContext(UiUitls.getContent()));
                    }
                    SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                            , GlobalConstant.UPDATE_DATA, false);
                }
            }).start();
        } else {
            //创建在sd卡上访问的数据库
            new DBHelper(new DatabaseContext(UiUitls.getContent()));
        }
    }

    /**
     * 分页数据转换
     */
    private void switchDataByPage(final int page) {
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        MeasureDataBeanDao measureDao = DBDataUtil.getMeasureDao();
        int offset = page * 100;
        List<PatientBean> listTemp = patientDao.queryBuilder().offset(offset).limit(limit).list();
        //转换操作
        if (listTemp != null && listTemp.size() > 0) {
            for (PatientBean patientBean : listTemp) {
                //备注：从多应用1.2.2(数据库版本为7)开始更换了身份证存储字段 idcard存储UUid作为唯一标识，card储存身份证号
                //所以，从老版本数据库版本低于7的都需要做字段值转换，7以后的版本字段为相同，不需要做转换处理
                //数据库版本8：idcard通过uuid自动生成，card现在作为原来的身份证字段
                if (GlobalConstant.oldDbVersion < GlobalNumber.SEVEN_NUMBER) {
                    //旧数据版本大于等于7之后的版本都不需要再进行数据字段值转换
                    patientBean.setCard(UiUitls.getValidCard(patientBean));
                    String uuid = UUIDGenerator.getUUID();
                    patientBean.setIdCard(uuid);
                }
                //该转换是因为之前版本该字段未初始化导致数据库更新过来为null
                if (patientBean.getAddress() == null) {
                    patientBean.setAddress("");
                }
                //兼容之前版本的数据转换
                int bloodType = patientBean.getBlood();
                if (bloodType >= GlobalNumber.FIVE_NUMBER) {
                    bloodType = GlobalNumber.FOUR_NUMBER;
                    patientBean.setBlood(bloodType);
                }
                String idCard = patientBean.getCard();
                int age = patientBean.getAge();
                if (!TextUtils.isEmpty(idCard) && age < 0) {
                    //计算年龄
                    //兼容低版本升级过来数据兼容的问题
                    age = UiUitls.getAge(patientBean);
                    patientBean.setAge(age);
                }
                //建立居民数据与测量数据的关联
                //多应用1.2.3 1.2.4Idcard字段为UUID，非身份证字段。
                List<MeasureDataBean> measures = DBDataUtil.getMeasuresByUuidOrIdCard(patientBean
                        .getIdCard(), patientBean.getCard());
                List<MeasureDataBean> measureDataBeenTemp = new ArrayList<>();
                if (measures.size() > 0) {
                    for (MeasureDataBean measure : measures) {
                        measure.setPatientId(patientBean.getId());
                        measure.setIdcard(patientBean.getIdCard());
                        //4063表示勾选了所有测量配置项
                        if (0 == measure.getParamValue()) {
                            measure.setParamValue(allSelect);
                        }
                        measure.setMeasureStrTime(UiUitls.getDateFormat(UiUitls
                                .DateState.SHORT).format(measure.getMeasureTime()));
                        measureDataBeenTemp.add(measure);
                    }
                    measureDao.updateInTx(measureDataBeenTemp);
                }
                patientDao.update(patientBean);
            }
        }
        if (offset + limit >= patientSizeTotal) {
            UiUitls.hideProgress();
        }
    }

    /**
     * 初始化背景图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initBackground() {
        String path = SkinSettingFragment.CURRENT_PICTURE_PATH + "/" + SkinSettingFragment
                .BACKGROUND_NAME;
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            rlBackground.setBackground(new BitmapDrawable(bitmap));
        } else {
            rlBackground.setBackgroundResource(R.drawable.bg_login);
        }
    }

    /**
     * 检测参数板是否需要升级
     */
    private void checkCSBUpdate() {
        GlobalConstant.UPLAOD_CSBU = false;
        boolean update = SpUtils.getSp(this, GlobalConstant.APP_CONFIG,
                ParameterGlobal.SP_KEY_CSB_UPDATE, false);
        int appDeviceStatus = UiUitls.getAppStatus(this, UploadRefreshVersion.DEVICEPACKAGE);
        String filePath = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , ParameterGlobal.CSB_NAME_FLAG, GlobalConstant.csbPath);
        File file = new File(filePath);
        //记录日志
        BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.CSBUPDATA + update);
        BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.CSBUPDATAFILEEXITST + file
                .exists());
        if (update && appDeviceStatus == status && file.exists()) {
            Intent it = new Intent(this, UpdateProgressActivity.class);
            startActivity(it);
        }
    }

    /**
     * 视图初始化
     */
    private void initView() {
        //初始化用户名
        usernameEd.setText(SpUtils.getSp(UiUitls.getContent()
                , GlobalConstant.APP_CONFIG, SpUtils.USERNAME, SpUtils.ADMIN));
        passwordEd.setText(SpUtils.getSp(UiUitls.getContent()
                , GlobalConstant.APP_CONFIG, SpUtils.PASSWORD, ""));
        loginBt.setOnClickListener(this);
        registerBt.setOnClickListener(this);
        loginBt.setFocusable(true);
        loginBt.setFocusableInTouchMode(true);
        loginBt.requestFocus();
        loginBt.requestFocusFromTouch();
        loginBt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onClick(v);
                }
            }
        });
        registerBt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onClick(v);
                }
            }
        });
        if (usernameEd.getText().toString().equals("")) {
            usernameEd.setHint(UiUitls.getString(R.string.please_input_user));
            usernameEd.setHintTextColor(getResources().getColor(R.color.username));
            usernameEd.setTextSize(TEXTSIZE);
            ivUsernameClear.setVisibility(View.GONE);
        } else {
            ivUsernameClear.setVisibility(View.VISIBLE);
        }
        if (passwordEd.getText().toString().equals("")) {
            passwordEd.setHint(UiUitls.getString(R.string.input_password));
            passwordEd.setHintTextColor(getResources().getColor(R.color.username));
            passwordEd.setTextSize(TEXTSIZE);
            ivPasswordClear.setVisibility(View.GONE);
        } else {
            ivPasswordClear.setVisibility(View.VISIBLE);
        }
        usernameEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    usernameEd.setBackgroundResource(R.drawable.ic_username_sel);
                    usernameEd.setTextColor(Color.BLACK);
                } else {
                    usernameEd.setBackgroundResource(R.drawable.ic_username_nor);
                    if (usernameEd.getText().toString().equals("")) {
                        usernameEd.setHint(UiUitls.getString(R.string
                                .please_input_user));
                        usernameEd.setHintTextColor(getResources().getColor(R.color.username));
                        usernameEd.setTextSize(TEXTSIZE);
                    }
                }
            }
        });
        usernameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    ivUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    ivUsernameClear.setVisibility(View.GONE);
                }
            }
        });
        passwordEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    ivPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    ivPasswordClear.setVisibility(View.GONE);
                }
            }
        });
        passwordEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordEd.setBackgroundResource(R.drawable.ic_password_sel);
                } else {
                    passwordEd.setBackgroundResource(R.drawable.ic_password_nor);
                    if (passwordEd.getText().toString().equals("")) {
                        passwordEd.setHint(UiUitls.getString(R.string.input_password));
                        passwordEd.setHintTextColor(getResources().getColor(R.color.username));
                        passwordEd.setTextSize(TEXTSIZE);
                    }
                }
            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initListener() {
        ivUsernameClear.setOnClickListener(this);
        ivPasswordClear.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                final String username = usernameEd.getText().toString();
                final String password = passwordEd.getText().toString();
                //排除空格
                if (username.contains(" ")) {
                    UiUitls.toast(this, getString(R.string.username_isexist_nbsp));
                    return;
                }
                if (password.contains(" ")) {
                    UiUitls.toast(this, getString(R.string.password_isexist_nbsp));
                    return;
                }
                if (UiUitls.isConSpeCharacters(username) || UiUitls
                        .isConSpeCharacters(password)) {
                    UiUitls.toast(this, getString(R.string.password_isexist_special));
                    return;
                }
                if (!"".equalsIgnoreCase(username) && !"".equalsIgnoreCase(password)) {
                    if (username.equals(getString(R.string.test_user)) &&
                            password.equals(getString(R.string.test_common_password))) {
                        UiUitls.showProgress(this, getString(R.string.loading_wait));
                        //普通测试账户登录，方便产线进行普通功能测试时，不需要注册即可登录
                        successsLogin(null, username, password);
                        UiUitls.hideProgress();
                        return;
                    }
                    //登录每次均走联网登录，再走本地登录
                    if (UiUitls.isNetworkConnected(this)) {
                        UiUitls.showProgress(this, getString(R.string.loading_wait));
                        //有网状态下联网登录
                        UiUitls.postShortThread(new Runnable() {
                            @Override
                            public void run() {
                                requestLogin(username, password);
                            }
                        });
                    } else {
                        UserBean user = isExistUser(username, password);
                        if (user != null) {
                            //记录到log日志里，不要删除
                            successsLogin(null, username, password);
                        } else {
                            UiUitls.toast(this, getString(R.string.no_network));
                        }
                    }
                } else {
                    UiUitls.toast(this, getString(R.string.pls_enter));
                }
                break;
            case R.id.register_btn:
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it);
                break;
            case R.id.username_iv:
                usernameEd.setText("");
                usernameEd.setHint(UiUitls.getString(R.string.please_input_user));
                usernameEd.setHintTextColor(getResources().getColor(R.color.username));
                usernameEd.setTextSize(TEXTSIZE);
                ivUsernameClear.setVisibility(View.GONE);
                break;
            case R.id.password_iv:
                passwordEd.setText("");
                passwordEd.setHint(UiUitls.getString(R.string.input_password));
                passwordEd.setHintTextColor(getResources().getColor(R.color.username));
                passwordEd.setTextSize(TEXTSIZE);
                ivPasswordClear.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 请求服务器登录
     *
     * @param username 用户名
     * @param password 密码
     */
    private void requestLogin(final String username, final String password) {
        String ip = SpUtils.getSp(this, GlobalConstant.APP_CONFIG, GlobalConstant.SERVICE_IP
                , GlobalConstant.IP_DEFAULT);
        String prot = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
        String serviceAddres = "/imms-web/publicUser/loginV2?";
        String deviceCode = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_CODING, Constant.DEVICE_CODE);
        Date date = new Date();
        String longTime = UiUitls.getDateFormat(UiUitls.DateState.LONG).format(date);
        final String url = GlobalConstant.HTTP + ip + ":" + prot + serviceAddres;
        String key = UiUitls.stringMD5(UiUitls.stringMD5(password + GlobalConstant.KUNSONGYITIJI)
                .toLowerCase() + longTime);
        String softVersion = UiUitls.getAppVersionName();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userName", username);
        requestParams.put("key", key);
        requestParams.put("time", longTime);
        requestParams.put("deviceCode", android.os.Build.SERIAL);
        requestParams.put("oldDeviceCode", deviceCode);
        requestParams.put("softwareVersion", softVersion);
        final String s = requestParams.toString();
        RequestUtils.clientGet(url + s, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String responseStr = new String(bytes);
                final CheckData checkData = JsonUtils.toEntity(responseStr, CheckData.class);

                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        if (checkData != null && successFlag.equals(checkData.getCode())) {
                            UserBean userBean = new UserBean();
                            userBean.setPassword(password);
                            userBean.setUsername(username);
                            UiUitls.hideProgress();
                            userBean.setOrgId(checkData.getCheckData().getOrgId());
                            userBean.setOrgName(checkData.getCheckData().getOrgName());
                            userBean.setEmpId(checkData.getCheckData().getEmpId());
                            userBean.setEmpName(checkData.getCheckData().getEmpName());
                            successsLogin(userBean, username, password);
                        } else {
                            //记录日志
                            BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.USERNAME
                                    + username);
                            BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.PASSWORD
                                    + password);
                            BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.URL + url
                                    + s);
                            CrashReport.postCatchedException(new Throwable(LogGlobalConstant
                                    .USER_ERROR));
                            fallLogin(checkData.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes
                    , final Throwable throwable) {
                // bugly会将这个throwable上报
                CrashReport.postCatchedException(throwable);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        fallLogin(getString(R.string.upload_server_fail));
                    }
                });
            }
        });
    }

    /**
     * 登录失败的方法
     *
     * @param errorMsg 错误信息
     */
    private void fallLogin(String errorMsg) {
        UiUitls.hideProgress();
        UiUitls.toast(this, errorMsg);
    }

    /**
     * 登录成功改变的方法
     *
     * @param checkData 登录接口返回的bean对象
     * @param userName  用户名
     * @param password  密码
     */
    private void successsLogin(UserBean checkData, String userName, String password) {
        //记录日志
        //记录日志
        BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.USERNAME
                + userName);
        BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.PASSWORD
                + password);
        if (null != checkData) {
            BuglyLog.v(LoginActivity.class.getName(), LogGlobalConstant.INFORMATION + JsonUtils
                    .toJsonString(checkData));
        }
        //持久化保存用户名
        SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , SpUtils.USERNAME, userName);
        SpUtils.saveToSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , SpUtils.PASSWORD, password);
        if (checkData != null) {
            boolean isExitUser = true;
            UserBean userBean = isExistUser(userName, password);
            if (userBean == null) {
                userBean = new UserBean();
                isExitUser = false;
            }
            userBean.setPassword(password);
            userBean.setUsername(userName);
            UiUitls.hideProgress();
            userBean.setOrgId(checkData.getOrgId());
            userBean.setOrgName(checkData.getOrgName());
            userBean.setEmpId(checkData.getEmpId());
            userBean.setEmpName(checkData.getEmpName());
            if (isExitUser) {
                DBHelper.getInstance().getUserDao().update(userBean);
            } else {
                DBHelper.getInstance().getUserDao().insert(userBean);
            }
        }
        UserBean userBean = isExistUser(userName, password);
        if (userBean != null) {
            GlobalConstant.ORG_ID = userBean.getOrgId();
            GlobalConstant.ORG_NAME = userBean.getOrgName();
            GlobalConstant.USERNAME = userBean.getUsername();
            GlobalConstant.EPMID = userBean.getEmpId();
            GlobalConstant.EMP_NAME = userBean.getEmpName();
            GlobalConstant.PASSWORD = userBean.getPassword();
        } else {
            GlobalConstant.ORG_ID = "";
            GlobalConstant.ORG_NAME = "";
            GlobalConstant.USERNAME = "";
            GlobalConstant.EPMID = "";
            GlobalConstant.EMP_NAME = "";
            GlobalConstant.PASSWORD = "";
        }
        UiUitls.toast(this, getString(R.string.login_success));
        Intent it = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnResume) {
            //数据迁移
            initUpdateData();
            createSkinFolder();
            if (GlobalConstant.UPLAOD_CSBU) {
                checkCSBUpdate();
            }
            clearStack();
            isOnResume = true;
        }
    }

    /**
     * 判断是否存在用户，如果存在就返回存在的用户，如果没有存在就返回null
     *
     * @param username 用户来查询的用户姓名
     * @param password 用户密码
     * @return UserBean
     */
    private UserBean isExistUser(String username, String password) {
        List<UserBean> userBeen = DBHelper.getInstance().getUserDao().queryBuilder()
                .where(UserBeanDao.Properties.Username.eq(username)).list();
        if (userBeen != null && userBeen.size() > 0) {
            if (userBeen.get(0).getPassword().equals(password)) {
                return userBeen.get(0);
            }
        }
        return null;
    }
}
