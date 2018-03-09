package com.konsung.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QueryItem;
import com.konsung.defineview.ListPopupWindow;
import com.konsung.fragment.AppFragment;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by xiangshicheng on 2017/10/10 0010.
 * 单项测量页面
 */

public class MeasureActivity extends BaseActivity {

    //fragment对象
    private AppFragment appFragment = null;
    //fragment标识
    private final String appFragmentFlag = "appFragment";
    //popWindow高度
    private final int popHeight = 460;
    //titleView
    private TextView titleView;
    //popWindow对象
    private ListPopupWindow listPopupWindow;
    //避免多次添加监听
    private boolean isHadSetListener = false;
    //用户列表集合
    private long beanListSize;
    //用户对象
    private PatientBean patientBean;
    //用户拼接信息
    private String titleAll = "";
    //病人类型数组
    private String[] patientArr;
    //性别数组
    private String[] sexArr;
    //限制popwindow高度的item数量
    private final int limitHeightNum = 4;
    //记录当前用户idcard值（UUID值）
    private String idCard = "";
    //会员卡号
    private String menberShipCard = "";
    /**
     * 血氧探头状态
     * 血氧探头状态在AIDLService对接AppDevice时发送一次,和血氧探头状态发生改变时发送
     * 因为要获取到初始状态，所以需要缓存
     */
    public static int probeSpo2Status = GlobalConstant.INVALID_DATA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initTitle();
        initView();
        initData();
        initListener();
    }

    /**
     * 导航栏初始化
     */
    private void initTitle() {
        //UUID
        idCard = SpUtils.getSp(this, GlobalConstant.APP_CONFIG, GlobalConstant.ID_CARD, "");
        //会员卡
        menberShipCard = SpUtils.getSp(this, GlobalConstant.APP_CONFIG
                , GlobalConstant.MEMBER_CARD, "");
        if (!TextUtils.isEmpty(idCard)) {
            patientBean = DBDataUtil.getPatientByIdCard(idCard).get(0);
        } else {
            patientBean = DBDataUtil.getPatientByMemberShipCard(menberShipCard).get(0);
        }
        //当前用户姓名
        String name = patientBean.getName();
        //当前用户类型
        int patientType = patientBean.getPatient_type();
        //当前用户性别
        int sexType = patientBean.getSex();
        GlobalConstant.currentSex = sexType;
        patientArr = getResources().getStringArray(R.array.patient_type_array);
        sexArr = getResources().getStringArray(R.array.detail_sex);
        //title拼接
        titleAll = name + " " + sexArr[sexType] + " " + patientArr[patientType];
        getSupportActionBar().show();
        setLeftButtonText(getRecString(R.string.single_measure));
        setTitleText(titleAll, getResources().getDrawable(R.drawable.ic_nav_drop_down));
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //获取标题视图对象
        titleView = getTitleView();
        appFragment = new AppFragment();
    }
    /**
     * 数据初始化
     */
    public void initData() {
        UiUitls.switchToFragment(R.id.measure_content, appFragment
                , appFragmentFlag, false, getFragmentManager());
        beanListSize = DBDataUtil.getPatients(new QueryItem());
        GlobalConstant.quickFlag = getIntent().getExtras().getString(GlobalConstant.QUICK_FLAG);
    }

    /**
     * 监听初始化
     */
    public void initListener() {
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向下偏移量
                int offY = (getSupportActionBar().getHeight() - getTitleView().getHeight()) / 2;
                //标题点击弹出下拉框显示本地所有用户
                if (listPopupWindow == null) {
                    if (beanListSize > limitHeightNum) {
                        listPopupWindow = new ListPopupWindow(MeasureActivity.this, popHeight
                                , titleView.getWidth());
                    } else {
                        listPopupWindow = new ListPopupWindow(MeasureActivity.this
                                , ViewGroup.LayoutParams.WRAP_CONTENT
                                , titleView.getWidth());
                    }
                }
                listPopupWindow.showAsDropDown(titleView, 0, offY);
                listPopupWindow.clearSeachBox();
                //设置半透明
                UiUitls.setWindowAlpha(MeasureActivity.this, GlobalNumber.ZERO_FIVE_FLOAT);
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
        getIvBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalConstant.ecgViewFor12 != null) {
                    GlobalConstant.ecgViewFor12.setVisibility(View.GONE);
                    GlobalConstant.ecgViewFor12 = null;
                }
                popActivity();
            }
        });
    }

    /**
     * 窗体消失监听
     */
    private PopupWindow.OnDismissListener onDismissListener =
            new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            UiUitls.setWindowAlpha(MeasureActivity.this, 1);
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
            if (!patientBean.getIdCard().equals(idCard) || !patientBean
                    .getMemberShipCard().equals(menberShipCard)) {
                //如果切换的用户为当前自己用户，则不作任何操作
                //只有切换用户不为自己本身的情况下才切换信息保存
                idCard = patientBean.getIdCard();
                menberShipCard = patientBean.getMemberShipCard();
                GlobalConstant.currentSex = patientBean.getSex();
                //保存UUID值
                SpUtils.saveToSp(MeasureActivity.this, GlobalConstant.APP_CONFIG
                        , GlobalConstant.ID_CARD, idCard);
                //保存会员卡
                SpUtils.saveToSp(MeasureActivity.this, GlobalConstant.APP_CONFIG
                        , GlobalConstant.MEMBER_CARD, menberShipCard);
                //title用户切换
                titleAll = patientBean.getName() + " " + sexArr[patientBean.getSex()] + " "
                        + patientArr[patientBean.getPatient_type()];
                getTitleView().setText(titleAll);
                ServiceUtils.resetEcgWaveData();
                //用户切换后要在新用户的基础上重新创建属于新用户的测量记录
                ServiceUtils.setMeasureDataBean(ServiceUtils.getTodayMeasureRecord());
                EventBus.getDefault()
                        .post(new EventBusUseEvent(GlobalConstant.MEASURE_USER_SWITCH));
            }
        }
    };
}
