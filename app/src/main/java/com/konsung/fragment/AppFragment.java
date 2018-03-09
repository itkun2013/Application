package com.konsung.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.R;
import com.konsung.bean.AppMeasureBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.MeasureValueCompareUtil;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.UrineType;
import com.konusng.adapter.HealthCheckSelectedAdapter;
import com.tencent.bugly.crashreport.CrashReport;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * AppFragment 类
 * 加载康尚应用的页面
 */
public class AppFragment extends BaseFragment {

    @InjectView(R.id.app_list)
    ListView lvAppList;
    @InjectView(R.id.app_content)
    FrameLayout flAppContent;
    //左边栏应用列表
    private List<AppMeasureBean> list;
    private HealthCheckSelectedAdapter mAppAdapter;
    private int selectPosition = -1;
    private FragmentTransaction mFm;
    private FragmentManager mFm1;
    //参数配置值
    private int paramValue = 0;
    //显示测量项名称集合
    private List<String> listStr = new ArrayList<String>();
    private Fragment fragment;
    /**
     * 心电探头状态
     * 心电探头状态状态每隔一秒才发一次，ECG页面可能会出现页面闪动情况
     * 因为要获取到初始状态，所以需要缓存
     */
    public static int probeEcgStatus = GlobalConstant.INVALID_DATA;
    public static boolean isCheckingEcg = false; //在心电测量中禁止其他操作
    public static int ecgCheckTimes = 0; //心电测量剩余时间
    protected LocalActivityManager mLocalActivityManager;
    //接受测量数据标识
    public static String refreshFlag = "measure_data_refresh";
    //以下标识均为测量数据接收标识
    public static final String ECG_FLAG = "心电";
    public static final String SPO_FLAG = "血氧";
    public static final String BF_FLAG = "血压";
    public static final String TEMP_FLAG = "体温";
    public static final String BT_FLAG = "血液三项";
    public static final String URINT_FLAG = "尿常规";
    public static final String HGB_FLAG = "血红蛋白";
    public static final String LIPD_FLAG = "血脂";
    public static final String BHD_FLAG = "糖化血红蛋白";
    public static final String BMI_FLAG = "BMI";
    public static final String WC_FLAG = "白细胞";
    //快检点击模块类型值
    private int quickIndex;
    //模块位置
    private int modelPosition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        GlobalConstant.CURRENT_SELETE_BTN = GlobalConstant.BtnFlag.middle;
        GlobalConstant.CLICK_MEUES = false;
        //参数配置项值
        paramValue = SpUtils.getSpInt(getActivity(), GlobalConstant.PARAM_CONFIGS
                , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
        list = getAppList();
        mAppAdapter = new HealthCheckSelectedAdapter(getActivity(), list);
        lvAppList.setAdapter(mAppAdapter);
        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int
                    position, long id) {
                if (GlobalConstant.CLICK_MEUES) {
                    return;
                }
                if (isCheckingEcg) {
                    UiUitls.toast(UiUitls.getContent(), UiUitls.getString(R.string
                            .ecg_checking) + ecgCheckTimes + "s");
                } else {
                    if (mFm1 == null) {
                        mFm1 = getFragmentManager();
                    }
                    mFm = mFm1.beginTransaction();
                    reinit(position);
                    selectPosition = position;
                    mAppAdapter.notifyDataSetChanged();
                    onClickItemPositionJudge(position);
                    GlobalConstant.quickFlag
                            = String.valueOf(mAppAdapter.recordCurrentMeasurePosition(position));
                }
            }
        });
        //Activity管理器，加载view
        mLocalActivityManager = new LocalActivityManager(getActivity(), true);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        //加载时默认选择第一条
        selectorFrist();
        return view;
    }

    /**
     * 选择
     * @param posintion 标记
     */
    private void reinit(int posintion) {
        //初始化选项颜色
        for (int i = 0; i < list.size(); i++) {
            AppMeasureBean appMeasureBean = list.get(i);
            if (i == posintion) {
                appMeasureBean.setClick(true);
                list.set(i, appMeasureBean);
            } else {
                appMeasureBean.setClick(false);
                list.set(i, appMeasureBean);
            }
        }
    }

    /**
     * 获取应用列表数据，每添加一项体检，请在此处添加
     * @return List<String>
     */
    private List<AppMeasureBean> getAppList() {
        List<AppMeasureBean> list = new ArrayList<>();
        if ((paramValue & (0x01 << GlobalNumber.ZERO_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.ecg));
            app.setIv(R.drawable.ic_measure_ecg);
            app.setIvClick(R.drawable.ic_measure_ecg_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.ECG_HR
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.ECG_HR
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.ecg));
        }
        if ((paramValue & (0x01 << GlobalNumber.ONE_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.spo2));
            app.setIv(R.drawable.ic_measure_spo);
            app.setIvClick(R.drawable.ic_measure_spo_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.SPO2_TREND
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.SPO2_TREND
                    , getActivity()));
            app.setDoubleMeasureValue(true);
            list.add(app);
            listStr.add(UiUitls.getString(R.string.spo2));
        }
        if ((paramValue & (0x01 << GlobalNumber.TWO_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_bloothglu));
            app.setIv(R.drawable.ic_measure_bp);
            app.setIvClick(R.drawable.ic_measure_bp_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.NIBP_SYS
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.NIBP_SYS
                    , getActivity()));
            app.setDoubleMeasureValue(true);
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_bloothglu));
        }
        if ((paramValue & (0x01 << GlobalNumber.THREE_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_temp));
            app.setIv(R.drawable.ic_measure_temp);
            app.setIvClick(R.drawable.ic_measure_temp_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.IRTEMP_TREND
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.IRTEMP_TREND
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_temp));
        }
        if ((paramValue & (0x01 << GlobalNumber.FOUR_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.blood_three));
            app.setIv(R.drawable.ic_measure_glu);
            app.setIvClick(R.drawable.ic_measure_glu_sel);
            app.setMeasureValue(MeasureValueCompareUtil
                    .getFinalValue(KParamType.BLOODGLU_BEFORE_MEAL, getActivity()));
            app.setNormal(MeasureValueCompareUtil
                    .isMeasureItemNormal(KParamType.BLOODGLU_BEFORE_MEAL, getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.blood_three));
        }
        if ((paramValue & (0x01 << GlobalNumber.FIVE_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            //显示11项
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_urine));
            app.setIv(R.drawable.ic_measure_urt);
            app.setIvClick(R.drawable.ic_measure_urt_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.URINERT_LEU
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.URINERT_LEU
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_urine));
            SpUtils.saveToSp(getActivity(), GlobalConstant.APP_CONFIG
                    , GlobalConstant.URINETYPE, UrineType.ELEVEN);
        }
        if ((paramValue & (0x01 << GlobalNumber.SIX_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            //14项
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_urine));
            app.setIv(R.drawable.ic_measure_urt);
            app.setIvClick(R.drawable.ic_measure_urt_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.URINERT_LEU
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.URINERT_LEU
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_urine));
            SpUtils.saveToSp(getActivity(), GlobalConstant.APP_CONFIG
                    , GlobalConstant.URINETYPE, UrineType.FOURTEEN);
        }
        if ((paramValue & (0x01 << GlobalNumber.SEVEN_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_hemoglobin));
            app.setIv(R.drawable.ic_measure_hgb);
            app.setIvClick(R.drawable.ic_measure_hgb_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.BLOOD_HGB
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.BLOOD_HGB
                    , getActivity()));
            app.setDoubleMeasureValue(true);
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_hemoglobin));
        }
        if ((paramValue & (0x01 << GlobalNumber.EIGHT_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.lipids_name));
            app.setIv(R.drawable.ic_measure_bf);
            app.setIvClick(R.drawable.ic_measure_bf_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.LIPIDS_CHOL
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.LIPIDS_CHOL
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.lipids_name));
        }
        if ((paramValue & (0x01 << GlobalNumber.NINE_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.health_gh));
            app.setIv(R.drawable.ic_measure_hba1c);
            app.setIvClick(R.drawable.ic_measure_hba1c_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.HBA1C_NGSP
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.HBA1C_NGSP
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.health_gh));
        }
        if ((paramValue & (0x01 << GlobalNumber.TEN_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.param_bmi));
            app.setIv(R.drawable.ic_measure_bmi);
            app.setIvClick(R.drawable.ic_measure_bmi_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(GlobalConstant.BMI_FLAG
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(GlobalConstant.BMI_FLAG
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.param_bmi));
        }

        if ((paramValue & (0x01 << GlobalNumber.ELEVEN_NUMBER)) != GlobalNumber.ZERO_NUMBER) {
            AppMeasureBean app = new AppMeasureBean();
            app.setMeasureName(UiUitls.getString(R.string.param_white));
            app.setIv(R.drawable.ic_measure_wbc);
            app.setIvClick(R.drawable.ic_measure_wbc_sel);
            app.setMeasureValue(MeasureValueCompareUtil.getFinalValue(KParamType.BLOOD_WBC
                    , getActivity()));
            app.setNormal(MeasureValueCompareUtil.isMeasureItemNormal(KParamType.BLOOD_WBC
                    , getActivity()));
            list.add(app);
            listStr.add(UiUitls.getString(R.string.param_white));
        }
        return list;
    }

    /**
     *item的点击处理
     * @param position 位置
     */
    private void onClickItemPositionJudge(int position) {
        if (listStr.get(position).equals(UiUitls.getString(R.string.ecg))) {
            //跳转心电
            //两种型号的ECG
            switch (GlobalConstant.ECG_NUM) {
                case GlobalNumber.FIVE_NUMBER:
                    Ecg5Fragment ecgFragment5 = new Ecg5Fragment();
                    mFm.replace(R.id.app_content, ecgFragment5);
                    mFm.commit();
                    break;
                case GlobalNumber.TWELVE_NUMBER:
                    MeasureEcgFragment ecgFragment12 = new MeasureEcgFragment();
                    mFm.replace(R.id.app_content, ecgFragment12);
                    mFm.commit();
                    break;
                default:
                    break;
            }
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.spo2))) {
            //跳转血氧
            MeasureSpo2Fragment spo2Fragment = new MeasureSpo2Fragment();
            mFm.replace(R.id.app_content, spo2Fragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.health_bloothglu))) {
            //跳转血压
            MeasureNibpFragment nibpFragment = new MeasureNibpFragment();
            mFm.replace(R.id.app_content, nibpFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.health_temp))) {
            //跳转体温
            int tempType = SpUtils.getSpInt(UiUitls.getContent()
                    , "sys_config", "temp_type", TempDefine.TEMP_INFRARED);
            if (tempType == TempDefine.TEMP_CONTACT) {
                //接触式体温
                TempFragment tempFragment = new TempFragment();
                mFm.replace(R.id.app_content, tempFragment);
                mFm.commit();
            } else if (tempType == TempDefine.TEMP_INFRARED) {
                //额温
                MeasureTempFragment tempFragment = new MeasureTempFragment();
                mFm.replace(R.id.app_content, tempFragment);
                mFm.commit();
            }
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.blood_three))) {
            //跳转血液三项
            MeasureBloodThreeFragment bloodThreeFragment = new MeasureBloodThreeFragment();
            mFm.replace(R.id.app_content, bloodThreeFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.health_urine))) {
            //跳转尿常规
            UrineFourttenFragment urinertFourteenFragment = new UrineFourttenFragment();
            mFm.replace(R.id.app_content, urinertFourteenFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.health_hemoglobin))) {
            //跳转血红蛋白
            MeasureHgbFragment hgbFragment = new MeasureHgbFragment();
            mFm.replace(R.id.app_content, hgbFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.lipids_name))) {
            //跳转血脂
            MeasureBloodFatFragment bloodFatFragment = new MeasureBloodFatFragment();
            mFm.replace(R.id.app_content, bloodFatFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.health_gh))) {
            //糖化血红蛋白
            MeasureGHbFragment gHbFragment = new MeasureGHbFragment();
            mFm.replace(R.id.app_content, gHbFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.param_bmi))) {
            //bmi
            BmiFragment bmiFragment = new BmiFragment();
            mFm.replace(R.id.app_content, bmiFragment);
            mFm.commit();
        } else if (listStr.get(position).equals(UiUitls.getString(R.string.param_white))) {
            //白细胞
            MeasureWbcFragment wbcFragment = new MeasureWbcFragment();
            mFm.replace(R.id.app_content, wbcFragment);
            mFm.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocalActivityManager.dispatchStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        lvAppList.setVisibility(View.VISIBLE);
        flAppContent.setVisibility(View.VISIBLE);
        GlobalConstant.CLICK_MEUES = false;
        mLocalActivityManager.dispatchResume();
    }

    /**
     * 默认选中快检页面点击项
     */
    private void selectorFrist() {
        quickIndex = Integer.parseInt(GlobalConstant.quickFlag);
        modelPosition = getModelPosition(quickIndex);
        lvAppList.setSelection(modelPosition);
        reinit(modelPosition);
        if (mFm1 == null) {
            mFm1 = getFragmentManager();
        }
        mFm = mFm1.beginTransaction();
        mAppAdapter.notifyDataSetChanged();
        mFm.replace(R.id.app_content, fragment);
        mFm.commit();
    }

    /**
     * 获取模块名称
     * @param index 模块索引值
     * @return 模块位置
     */
    private int getModelPosition(int index) {
        int position = 0;
        switch (index) {
            case GlobalNumber.ZERO_NUMBER:
                //血氧
                position = listStr.indexOf(SPO_FLAG);
                fragment = new MeasureSpo2Fragment();
                break;
            case GlobalNumber.ONE_NUMBER:
                //血压
                position = listStr.indexOf(BF_FLAG);
                fragment = new MeasureNibpFragment();
                break;
            case GlobalNumber.TWO_NUMBER:
                //体温
                position = listStr.indexOf(TEMP_FLAG);
                fragment = new MeasureTempFragment();
                break;
            case GlobalNumber.THREE_NUMBER:
                //血液三项
                position = listStr.indexOf(BT_FLAG);
                fragment = new MeasureBloodThreeFragment();
                break;
            case GlobalNumber.FOUR_NUMBER:
                //尿常规11项
                position = listStr.indexOf(URINT_FLAG);
                fragment = new UrineFourttenFragment();
                break;
            case GlobalNumber.FIVE_NUMBER:
                //尿常规14项
                position = listStr.indexOf(URINT_FLAG);
                fragment = new UrineFourttenFragment();
                break;
            case GlobalNumber.SIX_NUMBER:
                //血红蛋白
                position = listStr.indexOf(HGB_FLAG);
                fragment = new MeasureHgbFragment();
                break;
            case GlobalNumber.SEVEN_NUMBER:
                //血脂
                position = listStr.indexOf(LIPD_FLAG);
                fragment = new MeasureBloodFatFragment();
                break;
            case GlobalNumber.EIGHT_NUMBER:
                //糖化血红蛋白
                position = listStr.indexOf(BHD_FLAG);
                fragment = new MeasureGHbFragment();
                break;
            case GlobalNumber.NINE_NUMBER:
                //bmi
                position = listStr.indexOf(BMI_FLAG);
                fragment = new BmiFragment();
                break;
            case GlobalNumber.TEN_NUMBER:
                //白细胞
                position = listStr.indexOf(WC_FLAG);
                fragment = new MeasureWbcFragment();
                break;
            case GlobalNumber.ELEVEN_NUMBER:
                position = listStr.indexOf(ECG_FLAG);
                fragment = new MeasureEcgFragment();
                break;
            default:
                break;
        }
        return position;
    }

    @Override
    public void onPause() {
        super.onPause();
        lvAppList.setVisibility(View.INVISIBLE);
        flAppContent.setVisibility(View.INVISIBLE);
        if (GlobalConstant.CREATE_MEASURE == GlobalConstant.valueMin) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        judgeTestMeasure();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                }
            }).start();
        }
        mLocalActivityManager.dispatchPause(true);
    }

    @Override
    public void onDestroyView() {
        if (GlobalConstant.CREATE_MEASURE == GlobalConstant.valueMin
                && !GlobalConstant.IS_RUNING_ACTIVITY) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        judgeTestMeasure();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                }
            }).start();
        }
        // 退出测量界面后，停止NIBP测量
        EchoServerEncoder.setNibpConfig(ProtocolDefine.NET_NIBP_STOP_MEASURE, 0);
        EcgRemoteInfoSaveModule.getInstance().isExitMeasureUi = true;
        super.onDestroyView();
    }

    /**
     * 判断当前表单是否有测试数据，如果没有就删除
     */
    private void judgeTestMeasure() {
        String idcard = SpUtils.getSp(getActivity(), "app_config", "idcard", "");
        MeasureDataBeanDao dao = DBDataUtil.getMeasureDao();
        List<MeasureDataBean> measureDataBeans = UiUitls.getDataMesure(1, idcard);
        if (measureDataBeans.size() > 0) {
            dao.delete(measureDataBeans.get(measureDataBeans.size() - 1));
        }
        GlobalConstant.CREATE_MEASURE = 0;
    }

    /**
     * 接受fragment测量数据后刷新左列表的值
     * @param event 传递对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reveiverMeasureDataFresh(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //用户切换后刷新左侧所有列表项数值显示
            // 该事件来于MeasureActivity发送
            list.clear();
            list = null;
            list = getAppList();
            //记录右箭头的位置
            quickIndex = Integer.parseInt(GlobalConstant.quickFlag);
            modelPosition = getModelPosition(quickIndex);
            lvAppList.setSelection(modelPosition);
            reinit(modelPosition);
            mAppAdapter.notifyDataSetChanged();
        } else {
            //当前页面测量完后即时刷新当前页面所在左侧列表处的数值
            //该事件来源于各测量页面的事件发送
            updateListItemValue(event.getFlag());
        }
    }

    /**
     * 更新list数据
     * @param flag 标识
     */
    private void updateListItemValue(String flag) {
        for (int i = 0; i < list.size(); i++) {
            AppMeasureBean appMeasureBean = list.get(i);
            if (appMeasureBean.getMeasureName().equals(flag)) {
                appMeasureBean.setMeasureValue(MeasureValueCompareUtil
                        .getFinalValue(getKParamType(flag), getActivity()));
                appMeasureBean.setNormal(MeasureValueCompareUtil
                        .isMeasureItemNormal(getKParamType(flag), getActivity()));
                list.set(i, appMeasureBean);
                break;
            }
        }
        mAppAdapter.setData(list);
    }

    /**
     * 根据flag匹配参数类型
     * @param flag 标识值
     * @return 参数类型标识
     */
    private int getKParamType(String flag) {
        int paramType = 0;
        switch (flag) {
            case ECG_FLAG:
                paramType = KParamType.ECG_HR;
                break;
            case SPO_FLAG:
                paramType = KParamType.SPO2_TREND;
                break;
            case BF_FLAG:
                paramType = KParamType.NIBP_SYS;
                break;
            case TEMP_FLAG:
                paramType = KParamType.IRTEMP_TREND;
                break;
            case BT_FLAG:
                paramType = KParamType.BLOODGLU_BEFORE_MEAL;
                break;
            case URINT_FLAG:
                paramType = KParamType.URINERT_LEU;
                break;
            case HGB_FLAG:
                paramType = KParamType.BLOOD_HGB;
                break;
            case LIPD_FLAG:
                paramType = KParamType.LIPIDS_CHOL;
                break;
            case BHD_FLAG:
                paramType = KParamType.HBA1C_NGSP;
                break;
            case BMI_FLAG:
                paramType = GlobalConstant.BMI_FLAG;
                break;
            case WC_FLAG:
                paramType = KParamType.BLOOD_WBC;
                break;
            default:
                break;
        }
        return paramType;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
