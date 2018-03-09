package com.konsung.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.decard.healthcard.WSRead;
import com.huada.healthcard.HuaDaDeviceLib;
import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konsung.dowmload.DownloadAppPatchManager;
import com.konsung.fragment.QuickCheckFragmentNew;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.upload.AsyncTaskUpload;
import com.konsung.util.ActivityUtils;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.MeasureValueCompareUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.synjones.bluetooth.BmpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/10/9 0009.
 * 首页
 */

public class MainActivity extends BaseActivity implements View.OnClickListener
        , AsyncTaskUpload.OnCompeleteUploadListener {

    @InjectView(R.id.main_personal)
    LinearLayout personView;
    @InjectView(R.id.main_residents_list)
    LinearLayout residentsView;
    @InjectView(R.id.main_medical_report)
    LinearLayout reportView;
    @InjectView(R.id.main_tele_ecg)
    LinearLayout telEcgView;
    @InjectView(R.id.main_system_settings)
    LinearLayout systemSettingsView;
    @InjectView(R.id.main_head_pic)
    CircleImageView circleImageView;
    @InjectView(R.id.main_name)
    TextView tvUserName;
    @InjectView(R.id.main_type)
    TextView tvUserType;
    @InjectView(R.id.main_sex)
    ImageView ivSexType;
    @InjectView(R.id.blank_ll)
    LinearLayout llBlank;
    @InjectView(R.id.main_user_add_new)
    TextView tvAddUserNew;
    @InjectView(R.id.main_user_download)
    TextView tvUserDownload;
    @InjectView(R.id.main_change_layout)
    LinearLayout llMainChangeLayout;
    @InjectView(R.id.main_user_add_tv)
    TextView tvUserAdd;
    private QuickCheckFragmentNew quickCheckFragmentNew = null;
    //快检标识
    private final String quickCheckFragmentNewFlag = "quickCheckFragmentNew";
    //用户新增标识
    public static final String ADDPATIENTFRAGMENT = "addPatientFragment";
    //用户修改标识
    public static final String MODIFYPATIENTFRAGMENT = "modifyPatientFragment";
    //公用key值
    public static final String PERSONKEY = "isAddPerson";
    //网络监听字符
    private final String netChangeStr = "android.net.conn.CONNECTIVITY_CHANGE";
    //每天结束时间标识
    private final int endHour = 23;
    private final int endMinute = 60;
    //s/ms转换倍数
    private final int switchTime = 1000;
    //记录当天日期
    private String currentDate;
    //日期格式规范类
    private SimpleDateFormat dateFormat;
    //天转ms数
    private final long dayTime = 24 * 60 * 60 * 1000;
    //UUID值
    private String idCard;
    //会员卡号
    private String memberShipCard;
    private Handler handler = new Handler();
    private Bitmap bitmap = null;
    //姓名
    private String name = "";
    //病人类型
    private int patientType;
    //头像名称
    private String headPic = "";
    //性别
    private int sexType;
    //当前用户
    private PatientBean patientBean = null;
    //标识零点上传
    private boolean isZeroUpload = false;
    //记录上次点击时间(防暴点击)
    private long lastClickTime;
    //限制点击时间间隔为2s以上
    private final long limitTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initTitle();
        initData();
        initListener();
    }

    /**
     * 初始化导航栏
     */
    private void initTitle() {
        //首页隐藏导航栏
        getSupportActionBar().hide();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        GlobalConstant.isUploadStop = false;
        //检测当前的数据是否存储过多
        if (UiUitls.checkFileIsFull(Environment.getExternalStorageDirectory())) {
            UiUitls.toast(this, getRecString(R.string.storage_limits));
        }
        //初始化居民健康卡
        if (GlobalConstant.HEALTH_CARD_WSREAD == null) {
            GlobalConstant.HEALTH_CARD_WSREAD = new WSRead();
        }
        GlobalConstant.HEALTH_CARD_WSREAD.usbPermission(MainActivity.this);
        //初始化华大一体机健康读卡器
        if (GlobalConstant.ID_HEALTH_CARD == null) {
            GlobalConstant.ID_HEALTH_CARD = new HuaDaDeviceLib(UiUitls.getContent());
        }
        GlobalConstant.ID_HEALTH_CARD.openDevice();
        // 采用动态的方式注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(netChangeStr);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(updatePatientReceiver, filter);
        //登录进来触发上传所有数据(除今天外的所有数据)
        boolean isOpenAutoUpload = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , GlobalConstant.AUTOUPLOAD, true);
        if (isOpenAutoUpload) {
            //开启零点自动上传
            startZeroUpload();
        }
    }

    /**
     * 初始化监听
     */
    public void initListener() {
        personView.setOnClickListener(this);
        residentsView.setOnClickListener(this);
        reportView.setOnClickListener(this);
        telEcgView.setOnClickListener(this);
        systemSettingsView.setOnClickListener(this);
        tvAddUserNew.setOnClickListener(this);
        tvUserDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == personView) {
            //居民新增
            if (GlobalConstant.isAddPatientActivityDestyoy) {
                //跳转用户新增页面
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > limitTime) {
                    pushActivityWithMessage(this, AddPatientActivity.class
                            , PERSONKEY, ADDPATIENTFRAGMENT);
                    stopBloodMeasure();
                    stopSpo2Measure();
                }
                lastClickTime = currentTime;
            }
        } else if (v == residentsView) {
            //居民列表
            //停止血压测量
            stopBloodMeasure();
            //停止血氧测量
            stopSpo2Measure();
            //跳转居民列表页面
            if (patientBean != null) {
                //如果设置了当前病人信息，点击跳转到居民列表，显示当前的病人
                Bundle bundle = new Bundle();
                bundle.putString(GlobalConstant.CARD, patientBean.getCard());
                //会员卡
                bundle.putString(GlobalConstant.SHIP, patientBean.getMemberShipCard());
                pushActivityWithMessage(this, PatientListActivity.class, bundle);
                return;
            }
            pushActivity(this, PatientListActivity.class);
        } else if (v == reportView) {
            //体检报告
            //停止血压测量
            stopBloodMeasure();
            //停止血氧测量
            stopSpo2Measure();
            if (TextUtils.isEmpty(idCard)) {
                UiUitls.toast(this, R.string.set_current_user);
                return;
            }
            //跳转体检报告页面
            Bundle bundle = new Bundle();
            bundle.putString(GlobalConstant.NAME, name);
            bundle.putInt(GlobalConstant.PATIENT_TYPE, patientType);
            bundle.putInt(GlobalConstant.SEX_TYPE, sexType);
            bundle.putString(GlobalConstant.UUID, idCard);
            pushActivityWithMessage(MainActivity.this, ReportListActivity.class, bundle);
        } else if (v == telEcgView) {
            //跳转远程心电页面
            //停止血压测量
            stopBloodMeasure();
            //停止血压测量
            stopSpo2Measure();
            pushActivity(this, TeleEcgActivity.class);
        } else if (v == systemSettingsView) {
            //跳转系统设置页面
            //停止血压测量
            stopBloodMeasure();
            //停止血氧测量
            stopSpo2Measure();
            pushActivity(this, SystemActivity.class);
        } else if (v == tvAddUserNew) {
            //空白页下，点击用户新增按钮跳到用户新增页
            pushActivityWithMessage(this, AddPatientActivity.class
                    , PERSONKEY, ADDPATIENTFRAGMENT);
        } else if (v == tvUserDownload) {
            //空白页下，点击用户下载跳转到用户下载页
            pushActivity(this, PatientDownloadActivity.class);
        }
    }

    /**
     * 血氧测量的时候，切换页面，停止测量，ui还原到默认
     */
    private void stopSpo2Measure() {
        if (null != patientBean) {
            //当前用户不为空
            if (null != quickCheckFragmentNew) {
                //快检页面不为空
                quickCheckFragmentNew.refresgSpo2();
            }
        }
    }

    /**
     * 血压测量的时候，切换页面，停止测量，ui还原到默认
     */
    private void stopBloodMeasure() {
        if (null != patientBean) {
            //当前用户不为空
            if (null != quickCheckFragmentNew) {
                //快检页面不为空
                quickCheckFragmentNew.refresgBlood();
            }
        }
    }

    //广播接收器
    BroadcastReceiver updatePatientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case netChangeStr:
                    //该字段用来标识网络是否有效
                    boolean isNetAvilable = UiUitls.isNetworkConnected(context);
                    if (!isNetAvilable) {
                        UiUitls.toast(MainActivity.this
                                , UiUitls.getString(R.string.net_unnormal_state));
                        GlobalConstant.isUploadStop = true;
                        return;
                    } else {
                        GlobalConstant.isUploadStop = false;
                    }
                    //连接网络触发数据全部上传(上传除今天外的所有数据)
                    //先判断开关状态
                    boolean isOpenAutoUpload = SpUtils.getSp(MainActivity.this
                            , GlobalConstant.APP_CONFIG, GlobalConstant.AUTOUPLOAD, true);
                    if (isOpenAutoUpload) {
                        //全部上传(除当天数据)
                        uploadAll();
                    }
                    //网络变化情况下触发软件更新检测
                    boolean isAutoUpdate = SpUtils.getSp(MainActivity.this
                            , GlobalConstant.APP_CONFIG, GlobalConstant.APP_ISREFRESH, true);
                    if (isAutoUpdate) {
                        GlobalConstant.mainPageUpdate = true;
                        new DownloadAppPatchManager(MainActivity.this, handler).checkUpdate();
                    }
                    break;
                //息屏接受到的广播
                case Intent.ACTION_SCREEN_OFF:
                    //息屏后停止血压测量
                    if (quickCheckFragmentNew != null) {
                        //重新刷新血压测量页面，避免页面显示重叠
                        quickCheckFragmentNew.beNormalNibpModel();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        GlobalConstant.isAddUser = true;
        lastClickTime = 0;
        //该idcrad实际上为UUid,非身份证id
        idCard = SpUtils.getSp(this, GlobalConstant.APP_CONFIG, GlobalConstant.ID_CARD, "");
        memberShipCard = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , GlobalConstant.MEMBER_CARD, "");
        if (!TextUtils.isEmpty(idCard) || !TextUtils.isEmpty(memberShipCard)) {
            //新建记录
            ServiceUtils.setMeasureDataBean(ServiceUtils.getTodayMeasureRecord());
        }
        //初始化个人信息
        initPersonData();
        GlobalConstant.IS_MEUSE = true;
        //该log日志暂时不要删除，用于打印操作完后返回首页栈中activity对象是否正常销毁
        Log.e("stack ==>", "" + ActivityUtils.activityStack.size());
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
        //屏蔽返回，该代码可不删除
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(updatePatientReceiver);
        GlobalConstant.IS_MEUSE = false;
        UiUitls.removeCallbacks(r);
        GlobalConstant.isUploadStop = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalConstant.IS_MEUSE = false;
        ServiceUtils.setOnMessageSendListenerQuick(null);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * 接受厂家维护发送过来的信息以及自动上传开关发送的bus事件
     * @param event 传递事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void switchFactoryProtectModel(EventBusUseEvent event) {
        if (event.getFlag().equals(getString(R.string.factory_model_flag))) {
            //重新加载厂家维护模式下的页面
            pushActivity(this, SystemActivity.class);
        } else if (event.getFlag().equals(getString(R.string.auto_upload_close))) {
            //关闭自动上传
            if (!event.isCanAutoUpload()) {
                //解除0点上传机制
                UiUitls.removeCallbacks(r);
            } else {
                //开启0点上传机制
                startZeroUpload();
            }
        }
    }

    /**
     * 上传除今天外的所有数据
     */
    private void uploadAll() {
        AsyncTaskUpload asyncTaskUpload = new AsyncTaskUpload(this, this);
        asyncTaskUpload.execute(new String[]{"UploadBefore"});
    }

    /**
     * 开启零点上传模式
     */
    public void startZeroUpload() {
        Calendar calendar = Calendar.getInstance();
        //记录当前时间几点
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int hourPadding = endHour - hour;
        int minutePadding = endMinute - minute;
        //每次登陆进去后计算到凌晨的时间
        long delayTime = (hourPadding * endMinute + minutePadding) * endMinute * switchTime;
        //转换成ms
        Log.e("delayTime", "" + hourPadding + "-" + minutePadding + "-" + delayTime);
        UiUitls.postDelayed(r, delayTime);
    }

    //执行数据上传任务
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
            currentDate = dateFormat.format(new Date());
            isZeroUpload = true;
            //数据上传
            uploadAll();
            //设置间隔24h后数据再次上传，以此循环
            //转换成ms
            UiUitls.postDelayed(r, dayTime);
        }
    };

    /**
     * 初始化个人信息部分
     */
    private void initPersonData() {
        //每次初始化均把该变量置空
        patientBean = null;
        if (!TextUtils.isEmpty(idCard)) {
            //用idCrad查询
            PatientBean unique = DBDataUtil.getPatientByUnique(idCard);
            if (unique != null) {
                patientBean = unique;
            }
        }
        if (patientBean == null) {
            if (quickCheckFragmentNew != null) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.remove(quickCheckFragmentNew);
                fragmentTransaction.commit();
                quickCheckFragmentNew = null;
            }
            //没有设置当前用户
            llBlank.setVisibility(View.VISIBLE);
            tvUserName.setText(getRecString(R.string.main_value_str));
            llMainChangeLayout.setVisibility(View.GONE);
            tvUserAdd.setVisibility(View.VISIBLE);
            circleImageView.setImageResource(R.drawable.pic_default_avatar);
        } else {
            name = patientBean.getName();
            headPic = patientBean.getBmpStr();
            patientType = patientBean.getPatient_type();
            sexType = patientBean.getSex();
            //有当前用户
            //默认加载快检fragment
            llBlank.setVisibility(View.GONE);
            llMainChangeLayout.setVisibility(View.VISIBLE);
            tvUserAdd.setVisibility(View.GONE);
            //如果进行了参数配置
            if (GlobalConstant.isParamOperate) {
                GlobalConstant.isParamOperate = false;
                //如果进行了参数配置则重新加载页面
                quickCheckFragmentNew = null;
                quickCheckFragmentNew = new QuickCheckFragmentNew();
                UiUitls.switchToFragment(R.id.app_content, quickCheckFragmentNew
                        , quickCheckFragmentNewFlag, false, getFragmentManager());
            } else {
                if (quickCheckFragmentNew != null) {
                    //重新初始化服务监听
                    quickCheckFragmentNew.initMeasureListener();
                    //重新刷新数据
                    quickCheckFragmentNew.refreshMeasureData();
                }
                if (quickCheckFragmentNew == null) {
                    quickCheckFragmentNew = new QuickCheckFragmentNew();
                    UiUitls.switchToFragment(R.id.app_content, quickCheckFragmentNew
                            , quickCheckFragmentNewFlag, false, getFragmentManager());
                }
            }
            //病人类型数组
            String[] patientArr = getResources().getStringArray(R.array.patient_type_array);
            tvUserName.setText(name);
            tvUserType.setText(patientArr[patientType]);
            if (sexType == 0) {
                //女
                ivSexType.setImageResource(R.drawable.ic_female_mine);
            } else {
                //男
                ivSexType.setImageResource(R.drawable.ic_male_mine);
            }
            try {
                //当前用户头像
                Bitmap bmp = BmpUtil.getHeadBitmap(patientBean.getCard());
                if (bmp != null) {
                    circleImageView.setImageBitmap(bmp);
                } else {
                    circleImageView.setImageResource(R.drawable.pic_default_avatar);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompelete() {
        //上传完成后异步类置空
        //如果是零点自动上传上传完毕后即时刷新页面
        if (isZeroUpload) {
            isZeroUpload = false;
            if (quickCheckFragmentNew != null) {
                GlobalConstant.isAllUploadDataRefresh = true;
                //全局值初始化置空
                MeasureValueCompareUtil.setGlobalValueNull();
                //刷新页面
                quickCheckFragmentNew.refreshMeasureData();
                //建立新记录
                ServiceUtils.setMeasureDataBean(ServiceUtils.getTodayMeasureRecord());
                GlobalConstant.isAllUploadDataRefresh = false;
            }
        }
    }
}
