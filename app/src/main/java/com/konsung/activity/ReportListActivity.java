package com.konsung.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QueryItem;
import com.konsung.defineview.ListPopupWindow;
import com.konsung.defineview.TipsDialog;
import com.konsung.upload.AsyncTaskUpload;
import com.konsung.util.ActivityUtils;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konusng.adapter.ReportListAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 测量报告列表
 * Created by DJH on 2017/10/10 0010.
 */
public class ReportListActivity extends BaseActivity implements View.OnClickListener
        , AsyncTaskUpload.OnCompeleteUploadListener {
    //popWindow高度
    private final int popHeight = 460;
    //限制popwindow高度的item数量
    private final int limitHeightNum = 4;
    @InjectView(R.id.gv_report)
    GridView gvReport;
    @InjectView(R.id.ll_bottom)
    LinearLayout llBottom;
    @InjectView(R.id.tv_select_all)
    TextView tvSelectAll;
    @InjectView(R.id.tv_delete)
    TextView tvDelete;
    @InjectView(R.id.ll_no_data)
    LinearLayout llNoData;
    @InjectView(R.id.tv_to_measure)
    TextView tvToMeasure;
    private ReportListAdapter reportListAdapter; //适配器
    private boolean manage = false; //是否处于管理状态
    private List<MeasureDataBean> dataList; //测量报告集合
    private String name; //当前用户姓名
    private int patientType; //当前用户类型
    private int sexType; //当前用户性别
    private boolean selectAll = false; //是否全选
    //titleView
    private TextView titleView;
    //popWindow对象
    private ListPopupWindow listPopupWindow;
    //避免多次添加监听
    private boolean isHadSetListener = false;
    //用户列表集合
    Long beanListSize;
    //用户对象
    private PatientBean patientBean;
    //用户拼接信息
    private String titleAll = "";
    //病人类型数组
    private String[] patientArr;
    //性别数组
    private String[] sexArr;
    //当前用户记录UUID
    public String currentUUID = "";
    //网络变化广播标识
    private final String netChangeStr = "android.net.conn.CONNECTIVITY_CHANGE";

    //当前用户会员卡
    /**
     * 窗体消失监听
     */
    private PopupWindow.OnDismissListener onDismissListener =
            new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    titleView.setBackgroundResource(R.color.title_text_normal);
                }
            };
    /**
     * 点击item选中监听
     */
    private ListPopupWindow.OnPopwindowItemClickDismissListener listener =
            new ListPopupWindow.OnPopwindowItemClickDismissListener() {
                @Override
                public void onItemClickDismiss(int position, List<PatientBean> list) {
                    //根据选中的位置的用户进行切换用户，若切换用户则创建新的测量记录
                    patientBean = list.get(position);
                    currentUUID = patientBean.getIdCard();
                    //保存UUID
                    SpUtils.saveToSp(ReportListActivity.this, GlobalConstant.APP_CONFIG
                            , GlobalConstant.ID_CARD, currentUUID);
                    //保存会员卡号
                    SpUtils.saveToSp(ReportListActivity.this, GlobalConstant.APP_CONFIG
                            , GlobalConstant.MEMBER_CARD, patientBean.getMemberShipCard());
                    //title用户切换
                    titleAll = patientBean.getName() + " " + sexArr[patientBean.getSex()] + " "
                            + patientArr[patientBean.getPatient_type()];
                    getTitleView().setText(titleAll);
                    dataList = getMeasureData(patientBean.getIdCard());
                    if (dataList.size() > 0) {
                        llNoData.setVisibility(View.GONE);
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                    }
                    reportListAdapter.refreshData(dataList);
                    reportListAdapter.notifyDataSetChanged();
                }
            };

    /**
     * 按测量时间倒序查询返回测量集合
     * @param uuid 身份证号
     * @return 测量bean集合
     */
    public static List<MeasureDataBean> getMeasureData(String uuid) {
        MeasureDataBeanDao dao = DBDataUtil.getMeasureDao();
        List<MeasureDataBean> list = dao.queryBuilder().where(MeasureDataBeanDao.Properties
                .Idcard.eq(uuid)).orderDesc(MeasureDataBeanDao.Properties.Createtime).list();
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        ButterKnife.inject(this);
        initTitle();
        initEvent();
        initListener();
        initUploadData();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GlobalConstant.isBackFromReportDetail) {
            UiUitls.showProgress(this, UiUitls.getString(R.string.data_loading));
            initData();
        }
    }

    /**
     * 初始化导航栏
     */
    private void initTitle() {
        titleView = getTitleView();
        setLeftButtonText(UiUitls.getString(R.string.main_medical_report));
        setRightButtonText(UiUitls.getString(R.string.manage));
        Intent intent = getIntent();
        //当前用户姓名
        name = intent.getStringExtra(GlobalConstant.NAME);
        //当前用户类型
        patientType = intent.getIntExtra(GlobalConstant.PATIENT_TYPE, 0);
        //当前用户性别
        sexType = intent.getIntExtra(GlobalConstant.SEX_TYPE, 0);
        currentUUID = intent.getStringExtra(GlobalConstant.UUID);
        patientArr = getResources().getStringArray(R.array.patient_type_array);
        sexArr = getResources().getStringArray(R.array.detail_sex);
        //title拼接
        titleAll = name + " " + sexArr[sexType] + " " + patientArr[patientType];
        getSupportActionBar().show();
        setTitleText(titleAll, getResources().getDrawable(R.drawable.ic_nav_drop_down));
    }

    /**
     * 初始化页面事件
     */
    private void initEvent() {
        tvSelectAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvToMeasure.setOnClickListener(this);
        gvReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (manage) {
                    reportListAdapter.sendPosition(dataList.get(position));
                } else {
                    viewReportDetail(dataList.get(position).getId());
                }
            }
        });
        getRightBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manage) {
                    manage = false;
                    llBottom.setVisibility(View.GONE);
                    setRightButtonText(UiUitls.getString(R.string.manage));
                    reportListAdapter.clearState();
                } else {
                    manage = true;
                    llBottom.setVisibility(View.VISIBLE);
                    setRightButtonText(UiUitls.getString(R.string.cancel));
                    tvSelectAll.setText(getString(R.string.select_all));
                    selectAll = false;
                }
                GlobalConstant.isDeleteState = manage;
            }
        });
    }

    /**
     * 跳转到报告详情页面
     * @param id 测量id
     */
    private void viewReportDetail(Long id) {
        Intent intent = new Intent(this, ReportDetailActivity.class);
        intent.putExtra(GlobalConstant.MEASURE_ID, id);
        intent.putExtra(GlobalConstant.PATIENT_NAME, patientBean.getName());
        intent.putExtra(GlobalConstant.ID_CARD, currentUUID);
        intent.putExtra(GlobalConstant.MEMBER_CARD, patientBean.getMemberShipCard());
        startActivity(intent);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                //根据UUid查询该所有用户记录
                beanListSize = DBDataUtil.getPatients(new QueryItem());
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(currentUUID)) {
                            //如果uuid为空
                            llNoData.setVisibility(View.GONE);
                            return;
                        }
                    }
                });
                List<PatientBean> currents = DBDataUtil.getPatientByIdCard(currentUUID);
                if (currents.size() > 0) {
                    //判断是否有该人
                    patientBean = currents.get(0);
                    dataList = getMeasureData(currentUUID);
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            reportListAdapter = new ReportListAdapter(ReportListActivity.this
                                    , dataList);
                            gvReport.setAdapter(reportListAdapter);
                        }
                    });
                } else {
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            //没有当前用户，不显示
                            setTitleVisible(false);
                        }
                    });
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != dataList && dataList.size() > 0) {
                            llNoData.setVisibility(View.GONE);
                        } else {
                            llNoData.setVisibility(View.VISIBLE);
                        }
                        UiUitls.hideProgress();
                    }
                });
            }
        });
    }

    /**
     * 自动数据上传
     */
    private void initUploadData() {
        //该字段用来标识网络是否有效
        boolean isNetAvilable = UiUitls.isNetworkConnected(this);
        if (!isNetAvilable) {
            UiUitls.toast(this, UiUitls.getString(R.string.net_unnormal_state));
            GlobalConstant.isUploadStop = true;
            return;
        } else {
            GlobalConstant.isUploadStop = false;
        }
        boolean isOpenAutoUpload = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , GlobalConstant.AUTOUPLOAD, true);
        //即时上传入口(上传当前用户当天所有未上传测量记录)
        if (isOpenAutoUpload) {
            AsyncTaskUpload asyncTaskUpload = new AsyncTaskUpload(this, this);
            asyncTaskUpload.execute(new String[]{"UploadToday"});
        }
    }

    /**
     * 初始化广播监听器 用于监听网络变化
     */
    private void initReceiver() {
        // 采用动态的方式注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(netChangeStr);
        registerReceiver(netListenerReceiver, filter);
    }

    /**
     * 监听网络变化的广播接收器
     */
    private BroadcastReceiver netListenerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(netChangeStr)) {
                initUploadData();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全选
            case R.id.tv_select_all:
                whetherSelectAll();
                break;
            //删除
            case R.id.tv_delete:
                deleteMeasure();
                break;
            //跳转到测量页面
            case R.id.tv_to_measure:
                switchToMeasure();
                break;
            default:
                break;
        }
    }

    /**
     * 全选按钮点击状态
     */
    private void whetherSelectAll() {
        if (reportListAdapter == null) {
            return;
        }
        if (selectAll) {
            selectAll = false;
            reportListAdapter.clearState();
            tvSelectAll.setText(getString(R.string.select_all));
        } else {
            selectAll = true;
            reportListAdapter.selectAll();
            tvSelectAll.setText(getString(R.string.cancel_select_all));
        }
    }

    /**
     * 手动取消全部后还原全选状态
     */
    public void restoreSelectAllState() {
        selectAll = true;
        whetherSelectAll();
    }

    /**
     * 跳转到主页
     */
    private void switchToMeasure() {
        //点击测量操作后切换用户
        SpUtils.saveToSp(this, GlobalConstant.APP_CONFIG, GlobalConstant.ID_CARD, currentUUID);
        //判断居民列表页是否存在，存在则在栈中销毁
        Activity activity = ActivityUtils.getActivityUtils()
                .getNeedActivity(PatientListActivity.class);
        if (activity != null && activity instanceof PatientListActivity) {
            ActivityUtils.getActivityUtils().popActivity(activity);
        }
        popActivity();
    }

    /**
     * 删除选中的测量报告
     */
    private void deleteMeasure() {
        if (reportListAdapter == null) {
            return;
        }
        if (reportListAdapter.getSelectList().size() <= 0) {
            UiUitls.toast(this, getRecString(R.string.select_first_to_delete));
            return;
        }
        UiUitls.showTitle(this, getString(R.string.delete),
                getString(R.string.delete_select_measure),
                new TipsDialog.UpdataButtonState() {
                    @Override
                    public void getButton(Boolean pressed) {
                        if (pressed) {
                            List<MeasureDataBean> selectList = reportListAdapter.getSelectList();
                            int length = selectList.size();
                            for (int i = length - 1; i >= 0; i--) {
                                MeasureDataBean bean = selectList.get(0);
                                DBDataUtil.deleteMeasure(bean);
                                dataList.remove(bean);
                                selectList.remove(0);
                            }
                            reportListAdapter.refreshData(dataList);
                            manage = false;
                            llBottom.setVisibility(View.GONE);
                            setRightButtonText(UiUitls.getString(R.string.manage));
                            if (dataList.size() <= 0) {
                                llNoData.setVisibility(View.VISIBLE);
                            }
                            reportListAdapter.clearState();
                            UiUitls.hideTitil();
                            //删除完成后页面状态回归到原始状态，否则，无法点击上传按钮
                            GlobalConstant.isDeleteState = false;
                        }
                    }
                });
    }

    /**
     * 监听初始化
     */
    public void initListener() {
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.setBackgroundResource(R.color.title_text_pressed);
                //向下偏移量
                int offY = (getSupportActionBar().getHeight() - getTitleView().getHeight()) / 2;
                //标题点击弹出下拉框显示本地所有用户
                if (listPopupWindow == null) {
                    if (beanListSize > limitHeightNum) {
                        listPopupWindow = new ListPopupWindow(ReportListActivity.this, popHeight
                                , titleView.getWidth());
                    } else {
                        listPopupWindow = new ListPopupWindow(ReportListActivity.this
                                , ViewGroup.LayoutParams.WRAP_CONTENT
                                , titleView.getWidth());
                    }
                }
                listPopupWindow.showAsDropDown(titleView, 0, offY);
                listPopupWindow.clearSeachBox();
                //设置消失监听、点击item的监听
                if (!isHadSetListener) {
                    if (onDismissListener != null) {
                        listPopupWindow.setOnDismissListener(onDismissListener);
                    }
                    if (listener != null) {
                        listPopupWindow.setListener(listener);
                    }
                    isHadSetListener = true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netListenerReceiver);
        GlobalConstant.isUploadStop = true;
        GlobalConstant.isBackFromReportDetail = false;
    }

    @Override
    public void onCompelete() {
        //上传完成后数据刷新
        if (patientBean == null) {
            llNoData.setVisibility(View.VISIBLE);
            return;
        }
        if (TextUtils.isEmpty(patientBean.getIdCard())) {
            llNoData.setVisibility(View.VISIBLE);
            return;
        }
        dataList = getMeasureData(patientBean.getIdCard());
        if (dataList.size() > 0) {
            llNoData.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.VISIBLE);
        }
        if (reportListAdapter != null) {
            reportListAdapter.refreshData(dataList);
            reportListAdapter.notifyDataSetChanged();
        }
    }
}
