package com.konsung.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QueryItem;
import com.konsung.defineview.PatientQueryPopupWindow;
import com.konsung.defineview.TipsDialog;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.StringUtil;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.PatientStyle;
import com.konusng.adapter.PatientItemAdapter;
import com.synjones.bluetooth.BmpUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
import static com.konsung.activity.MainActivity.ADDPATIENTFRAGMENT;
import static com.konsung.activity.MainActivity.MODIFYPATIENTFRAGMENT;
import static com.konsung.activity.MainActivity.PERSONKEY;

/**
 * 创建者     dlx
 * 创建时间   2017/10/11 0011 下午 2:02
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class PatientListActivity extends BaseActivity implements AbsListView.OnScrollListener {
    private static final int MAN = 1;
    private static final int WOMAN = 0;
    @InjectView(R.id.iv_search)
    ImageView ivSearch;
    @InjectView(R.id.et_search)
    EditText etSearch;
    @InjectView(R.id.lv_patient)
    ListView lvPatient;
    @InjectView(R.id.ll_root)
    LinearLayout llRoot;
    @InjectView(R.id.ll_data_container)
    LinearLayout llDataContainer;
    @InjectView(R.id.btn_add)
    Button btnAdd;
    @InjectView(R.id.btn_download)
    Button btnDownload;
    @InjectView(R.id.ll_empty)
    LinearLayout llEmpty;
    @InjectView(R.id.iv_pic)
    ImageView ivPic;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.iv_sex)
    ImageView ivSex;
    @InjectView(R.id.tv_card)
    TextView tvCard;
    @InjectView(R.id.btn_modify)
    Button btnModify;
    @InjectView(R.id.btn_measure)
    Button btnMeasure;
    @InjectView(R.id.btn_report)
    Button btnReport;
    @InjectView(R.id.tv_age)
    TextView tvAge;
    @InjectView(R.id.tv_type)
    TextView tvType;
    @InjectView(R.id.tv_phone)
    TextView tvPhone;
    @InjectView(R.id.tv_blood)
    TextView tvBlood;
    @InjectView(R.id.tv_addr)
    TextView tvAddr;
    @InjectView(R.id.tv_memo)
    TextView tvMemo;
    @InjectView(R.id.tv_all)
    TextView tvAll;
    @InjectView(R.id.tv_delete)
    TextView tvDelete;
    @InjectView(R.id.rl_delete_container)
    RelativeLayout rlDeleteContainer;
    @InjectView(R.id.tv_sum)
    TextView tvSum;
    @InjectView(R.id.btn_list_clear)
    Button btnClear;
    private List<PatientBean> patientList;
    private PatientItemAdapter adapter;
    private PatientBean currentPatient;
    private boolean isAllSelect; //是否全部选择
    private Map<Integer, Boolean> deletMap = new HashMap<>(); //需要删除的数据
    private TextView tvMenuDelete;
    private PatientQueryPopupWindow pop;
    boolean isManageClick = false; //管理按钮是否被点击
    private String card;
    private String ship;
    //临时存储
    private String cardTemp = "";
    private final int pageCount = 10; //一页显示的数量
    private int totleCount = 0; //当前页数显示的总数量
    private int visibleLastIndex = 0; //最后的可视项索引
    QueryItem item = new QueryItem(); //记录查询条件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_patient_list);
        //防止弹出软键盘遮挡编辑框
        getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING | SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //新增的时候，返回重新刷新数据
        if (GlobalConstant.isAddUser) {
            GlobalConstant.isAddUser = false;
            UiUitls.showProgress(this, UiUitls.getString(R.string.data_loading));
            //延迟1s是为了防止大数据量时数据库操作影响主视图的生成
            UiUitls.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, GlobalNumber.THOUSAND_NUMBER);
        }
    }

    /**
     * 初始化列表数据
     */
    private void initData() {
        setLeftButtonText(getString(R.string.main_residents_list));
        etSearch.setText("");
        //显示所有病人数据
        totleCount = 0;
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                patientList = getPatient();
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new PatientItemAdapter(PatientListActivity.this, patientList);
                        lvPatient.setAdapter(adapter);
                        lvPatient.setOnScrollListener(PatientListActivity.this);  //添加滑动监听
                        //获得传过来的当前居民身份证和会员卡
                        Bundle bundle = getIntent().getExtras();
                        if (null != bundle) {
                            //身份证
                            card = bundle.getString(GlobalConstant.CARD);
                            //会员卡
                            ship = bundle.getString(GlobalConstant.SHIP);
                        }
                        getCurrentPaitent(card, ship);
                        //显示菜单栏
                        initMenu();
                        //根据数据检查显示不用的view
                        checkNumber();
                        showCurrentPatient(patientList);
                        //默认不选择
                        initDeletMap(patientList, false, false);
                        //默认选择第一条
                        if (patientList.size() > 0) {
                            lvPatient.setSelection(0);
                        }
                        UiUitls.hideProgress();
                    }
                });
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PatientBean> listDlete = new ArrayList();
                //全选
                if (isAllSelect) {
                    if (deletMap.size() > 0) {
                        for (Map.Entry<Integer, Boolean> entry : deletMap.entrySet()) {
                            if (!entry.getValue()) {
                                Integer position = entry.getKey();
                                PatientBean bean = patientList.get(position);
                                listDlete.add(bean);
                            }
                        }
                        //删除所有数据数据
                        showDeleteAllDialog(listDlete);
                        isAllSelect = false;
                    }
                    return;
                }
                if (deletMap.size() > 0) {
                    for (Map.Entry<Integer, Boolean> entry : deletMap.entrySet()) {
                        if (entry.getValue()) {
                            Integer position = entry.getKey();
                            PatientBean bean = patientList.get(position);
                            listDlete.add(bean);
                        }
                    }
                    if (listDlete.size() > 0) {
                        //删除数据
                        showDeleteDialog(listDlete);
                    } else {
                        UiUitls.toast(PatientListActivity.this
                                , getRecString(R.string.delete_select_please));
                    }
                }
            }
        });

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllSelect = !isAllSelect;
                initDeletMap(patientList, isAllSelect, false);
                adapter.setDeleteMapu(deletMap);
            }
        });

        lvPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isManageClick) {
                    if (deletMap == null) {
                        return;
                    }
                    //如果是删除，点击选中item效果
                    if (deletMap.get(position)) {
                        //如果当前已被选择，在长按，就取消
                        deletMap.put(position, false);
                    } else {
                        deletMap.put(position, true);
                    }
                    adapter.setDeleteMapu(deletMap);
                } else {
                    //普通点击显示当前的view
                    currentPatient = patientList.get(position);
                    selectPatient(patientList.get(position));
                }
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                totleCount = 0;
                if (TextUtils.isEmpty(s)) {
//                    item = new QueryItem();
                    patientList = getPatient();
                    initDeletMap(patientList, false, false);
                    adapter.setDeleteMap(deletMap);
                    adapter.setDatas(patientList);
                    tvSum.setText(getString(R.string.sum_1) + DBDataUtil.getPatients(
                            item) + getString(R.string.sum_2));
                    //如果没有搜索，不显示清空按钮
                    btnClear.setVisibility(View.GONE);
                    item.setName("");
                    return;
                }
                item.setName(s);
                //搜索，显示清空按钮，点击复位
                UiUitls.postShortThread(new Runnable() {
                    @Override
                    public void run() {
                        totleCount = 0;
                        final List<PatientBean> lists = DBDataUtil.getPatientBeen(totleCount
                                , pageCount, item);
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                totleCount = lists.size() + totleCount;
                                patientList = lists;
                                initDeletMap(patientList, false, false);
                                adapter.setDeleteMap(deletMap);
                                adapter.setDatas(patientList);
                                btnClear.setVisibility(View.VISIBLE);
                                tvSum.setText(getString(R.string.sum_1) + DBDataUtil.getPatients(
                                        item) + getString(R.string.sum_2));
                            }
                        });
                    }
                });
            }
        });
        //修改键盘的enter键，点击消费
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL) {
                    //消费
                    return true;
                }
                return false;
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置为空，会走etSerach的监听
                item = new QueryItem();
                etSearch.setText("");
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pushActivityWithMessage(PatientListActivity.this, AddPatientActivity.class
                        , PERSONKEY, ADDPATIENTFRAGMENT);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushActivity(PatientListActivity.this, PatientDownloadActivity.class);
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDefult();
                Bundle bundle = new Bundle();
                bundle.putString(PERSONKEY, MODIFYPATIENTFRAGMENT);
                bundle.putLong(GlobalConstant.idKey, currentPatient.getId());
                pushActivityWithMessage(PatientListActivity.this, AddPatientActivity.class, bundle);
            }
        });

        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UUID
                SpUtils.saveToSp(PatientListActivity.this, GlobalConstant.APP_CONFIG
                        , GlobalConstant.ID_CARD, currentPatient.getIdCard());
                //会员卡号
                SpUtils.saveToSp(PatientListActivity.this, GlobalConstant.APP_CONFIG
                        , GlobalConstant.MEMBER_CARD, currentPatient.getMemberShipCard());
                popActivity();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDefult();
                //跳转到体检报告
                Bundle bundle = new Bundle();
                bundle.putString(GlobalConstant.NAME, currentPatient.getName());
                bundle.putInt(GlobalConstant.PATIENT_TYPE, currentPatient.getPatient_type());
                bundle.putInt(GlobalConstant.SEX_TYPE, currentPatient.getSex());
                //idCard字段保存的是UUID值
                bundle.putString(GlobalConstant.UUID, currentPatient.getIdCard());
                pushActivityWithMessage(PatientListActivity.this
                        , ReportListActivity.class, bundle);
            }
        });
    }

    /**
     * 删除所有的数据
     * @param listDlete  保留的数据
     */
    private void showDeleteAllDialog(final List<PatientBean> listDlete) {
        UiUitls.showTitle(this, UiUitls.getString(R.string.confirm), UiUitls.
                getString(R.string.confirm_delete_selete), new TipsDialog.UpdataButtonState() {
                    @Override
                    public void getButton(Boolean pressed) {
                            if (pressed) {
                                    showProgressDelete();
                                    UiUitls.postShortThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<List<MeasureDataBean>> lists = new ArrayList();
                                            for (int i = 0; i < listDlete.size(); i++) {
                                                PatientBean bean = listDlete.get(i);
                                                List<MeasureDataBean> measures
                                                        = DBDataUtil.getMeasures(bean.getIdCard());
                                                lists.add(measures);
                                            }
                                            DBDataUtil.deleteMeasureDataBeen(item);
                                            //删除所有头像
                                            BmpUtil.deleteHeadBitmapAll();
                                            DBDataUtil.getPatientDao().insertInTx(listDlete);
//                                            DBDataUtil.getMeasureDao().deleteAll();
//                                            for (int i = 0; i < lists.size(); i++) {
//                                                List<MeasureDataBean> list = lists.get(i);
//                                                DBDataUtil.getMeasureDao().insertInTx(list);
//                                            }
                                            UiUitls.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    initData();
                                                    UiUitls.hideProgress();
                                                    //删除完所有数据后清空当前用户的存储值
                                                    SpUtils.saveToSp(PatientListActivity.this
                                                            , GlobalConstant.APP_CONFIG
                                                            , GlobalConstant.ID_CARD, "");
                                                    SpUtils.saveToSp(PatientListActivity.this
                                                            , GlobalConstant.APP_CONFIG
                                                            , GlobalConstant.MEMBER_CARD, "");
                                                }
                                            });
                                        }
                                    });
                            }
                        }
                });
    }

    /**
     * 获取居民信息
     * @return 居民信息
     */
    private List<PatientBean> getPatient() {
        item.setName(etSearch.getText().toString());
        List<PatientBean> patientBeens = DBDataUtil.getPatientBeen(totleCount, pageCount, item);
        totleCount = patientBeens.size() + totleCount;
        return patientBeens;
    }

    /**
     * 得到当前的病人，级别：先身份证>和会员卡
     * 得到当前的病人，级别：先身份证>和会员卡
     * @param card 身份证标志
     * @param ship 会员卡标志
     */
    private void getCurrentPaitent(String card, String ship) {

        if (null == currentPatient) {
            //只有当前用户为空，才去判断传过来的数据
            if (null != card && !TextUtils.isEmpty(card)) {
                List<PatientBean> list = DBDataUtil.getPatientByCard(card);
                if (list.size() > 0) {
                    //说明有该病人
                    currentPatient = list.get(0);
                }
            } else {
                //如果新建用户没填写icard和会员卡，会默认给ks开头的会员卡，参见AddPatientFragment的
//                addPatientToDB方法
                if (null != ship && !TextUtils.isEmpty(ship)) {
                    List<PatientBean> list = DBDataUtil.getPatientByMemberShipCard(ship);
                    if (list.size() > 0) {
                        //说明有该病人
                        currentPatient = list.get(0);
                    }
                }
            }
        }
    }

    /**
     * 显示删除数据的弹框提示
     * @param listDlete 需要删除的数据
     */
    private void showDeleteDialog(final List<PatientBean> listDlete) {
        UiUitls.showTitle(this, UiUitls.getString(R.string.confirm), UiUitls.
                getString(R.string.confirm_delete_selete), new TipsDialog.UpdataButtonState() {
                    @Override
                    public void getButton(Boolean pressed) {
                                if (pressed) {
                                    showProgressDelete();
                                    UiUitls.postShortThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //获取当前用户的UUID
                                            String uuid = SpUtils.getSp(PatientListActivity.this
                                                    , GlobalConstant.APP_CONFIG
                                                    , GlobalConstant.ID_CARD, "");
                                            for (int i = 0; i < listDlete.size(); i++) {
                                                PatientBean bean = listDlete.get(i);
                                                if (bean.getIdCard().equals(uuid)) {
                                                    //如果当前用户被删除了则清空UUID和会员卡号存储值
                                                    SpUtils.saveToSp(PatientListActivity.this
                                                            , GlobalConstant.APP_CONFIG
                                                            , GlobalConstant.ID_CARD, "");
                                                    SpUtils.saveToSp(PatientListActivity.this
                                                            , GlobalConstant.APP_CONFIG
                                                            , GlobalConstant.MEMBER_CARD, "");
                                                }
                                                List<MeasureDataBean> measures
                                                        = DBDataUtil.getMeasures(bean.getIdCard());
                                                DBDataUtil.getMeasureDao().deleteInTx(measures);
                                            }
                                            patientList.removeAll(listDlete);
                                            DBDataUtil.getPatientDao().deleteInTx(listDlete);
                                            //删除头像
                                            for (PatientBean patientBean : listDlete) {
                                                BmpUtil.deleteHeadBitmap(patientBean.getCard());
                                            }
                                            //删除完后，重新不让选择
                                            initDeletMap(patientList, false, false);
                                            UiUitls.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.setDeleteMapu(deletMap);
                                                    checkNumber();
                                                    //默认选择第一条数据
                                                    showCurrentPatient(patientList);
                                                    UiUitls.hideProgress();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                });
    }

    /**
     * 删除数据显示进度条
     */
    private void showProgressDelete() {
        UiUitls.showProgress(this, UiUitls.getString(R.string.wait_for_delete));
    }

    /**
     * 显示当前用于
     * @param patientList 用户数据
     */
    private void showCurrentPatient(List<PatientBean> patientList) {
        //如果数据只有一条，显示该条数据view
        if (patientList.size() == 1) {
            currentPatient = patientList.get(0);
            selectPatient(currentPatient);
            return;
        }
        //如果多条，且当前用户不为空，显示
        if (currentPatient != null) {
            //如果删除了当前用户，还是有缓存，查询数据库，是否还有当前用户
            List<PatientBean> beanList = DBDataUtil.getPatientByIdCard(currentPatient.getIdCard());
            if (beanList.size() > 0) {
                scrollToCurrent();
                selectPatient(currentPatient);
            } else {
                //说明删除了
                //默认显示最上面的一条
                if (patientList.size() > 0) {
                    //设置默认当前病人
                    currentPatient = patientList.get(0);
                    scrollToCurrent();
                    selectPatient(currentPatient);
                }
            }
        } else {
            //默认显示最上面的一条
            if (patientList.size() > 0) {
                //设置默认当前病人
                currentPatient = patientList.get(0);
                scrollToCurrent();
                selectPatient(currentPatient);
            }
        }
    }

    /**
     * 选择当前用户的时候，相应滚动到对应位置
     */
    private void scrollToCurrent() {
        for (int i = 0; i < patientList.size(); i++) {
            if (currentPatient.getIdCard().equals(patientList.get(i).getIdCard())) {
                //定位
                lvPatient.setSelection(i);
            }
        }
    }

    /**
     * 根据居民列表数量加载显示
     */
    private void checkNumber() {
        if (DBDataUtil.queryAllPatientSize() == 0) {
            currentPatient = null;
            llEmpty.setVisibility(View.VISIBLE);
            llDataContainer.setVisibility(View.GONE);
        } else {
            if (patientList.size() == 0) {
                //如果删除到最后一条数据后就清空查询和筛选条件还原到数据最开始状态
                etSearch.setText("");
                totleCount = 0;
                item = new QueryItem();
                patientList = getPatient();
                if (patientList != null && patientList.size() > 0) {
                    selectPatient(patientList.get(0));
                }
            }
            llEmpty.setVisibility(View.GONE);
            llDataContainer.setVisibility(View.VISIBLE);
            tvSum.setText(getString(R.string.sum_1) + DBDataUtil.getPatients(item)
                    + getString(R.string.sum_2));
            adapter.setDatas(patientList);
        }
    }

    /**
     * 初始化菜单选项
     */
    private void initMenu() {
        if (getRightContainer().getChildCount() > 0) {
            return;
        }
        View menu = View.inflate(this, R.layout.patient_list_menu, null);
        View btnMenuAdd = ButterKnife.findById(menu, R.id.btn_menu_add);
        View btnMenuDownload = ButterKnife.findById(menu, R.id.btn_menu_download);
        View btnMenuQuery = ButterKnife.findById(menu, R.id.btn_menu_query);
        tvMenuDelete = ButterKnife.findById(menu, R.id.btn_menu_delete);
        getRightContainer().addView(menu);

        btnMenuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDefult();
                pushActivityWithMessage(PatientListActivity.this, AddPatientActivity.class
                        , PERSONKEY, ADDPATIENTFRAGMENT);
            }
        });

        btnMenuDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDefult();
                pushActivity(PatientListActivity.this, PatientDownloadActivity.class);
            }
        });

        final TextView tvTitle = getTitleView();
        //向下偏移量
        final int offY = (getSupportActionBar().getHeight() - tvTitle.getHeight()) / 2;
        btnMenuQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDefult();
                if (pop == null) {
                    pop = new PatientQueryPopupWindow(PatientListActivity.this
                            , ViewGroup.LayoutParams.MATCH_PARENT
                            , ViewGroup.LayoutParams.MATCH_PARENT);
                    pop.addOnQueryListener(new PatientQueryPopupWindow.OnQueryListener() {
                        @Override
                        public void onQuery(QueryItem queryItem) {
                            UiUitls.showProgress(PatientListActivity.this
                                    , getString(R.string.query_wait));
                            item = queryItem;
                            queryPatient(item);
                            pop.dismiss();
                        }

                        @Override
                        public void clearSelection() {
                            //条件清空后数据列表恢复筛选前数据
                            item = new QueryItem();
                            queryPatient(item);
                            pop.dismiss();
                        }
                    });
                }
                if (pop.isShowing()) {
                    pop.dismiss();
                } else {
                    pop.showAsDropDown(tvTitle, 0, offY);
                }
            }
        });

        tvMenuDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isManageClick = !isManageClick;
                if (isManageClick) {
                    //如果点击管理，显示取消
                    tvSum.setVisibility(View.GONE);
                    rlDeleteContainer.setVisibility(View.VISIBLE);
                    tvMenuDelete.setText(R.string.patient_list_cancel);
                } else {
                    //如果点击取消，显示管理
                    rlDeleteContainer.setVisibility(View.GONE);
                    tvSum.setVisibility(View.VISIBLE);
                    tvMenuDelete.setText(R.string.patient_list_manage);
                    initDeletMap(patientList, false, false);
                    adapter.setDeleteMapu(deletMap);
                    isAllSelect = false;
                }
            }
        });
    }

    /**
     * 显示管理按钮默认
     */
    public void showMenuDefult() {
        //如果点击取消，显示管理
        isManageClick = false;
        rlDeleteContainer.setVisibility(View.GONE);
        tvSum.setVisibility(View.VISIBLE);
        tvMenuDelete.setText(R.string.patient_list_manage);
    }

    /**
     * 根据选择的居民显示数据
     * @param patient 用户
     */
    private void selectPatient(PatientBean patient) {
        if (patient == null) {
            return;
        }
        adapter.setSelectedPatient(patient.getCard());
        tvName.setText(patient.getName());
        if (!TextUtils.isEmpty(patient.getCard())) {
            cardTemp = patient.getCard();
        } else if (!TextUtils.isEmpty(patient.getMemberShipCard())
                && !patient.getMemberShipCard().startsWith(GlobalConstant.preStr)) {
            cardTemp = patient.getMemberShipCard();
        } else {
            cardTemp = "";
        }
        tvCard.setText(cardTemp);
        tvAge.setText(patient.getAge() < 0 ? "" : (patient.getAge() + UiUitls
                .getString(R.string.unit_age)));
        tvPhone.setText(patient.getSelfmobile());
        tvBlood.setText(StringUtil.getBlood(patient.getBlood()));
        tvAddr.setText(patient.getAddress());
        tvMemo.setText(patient.getRemark());
        try {
            Bitmap bmp = BmpUtil.getHeadBitmap(patient.getCard());
            if (bmp != null) {
                ivPic.setImageBitmap(bmp);
            } else {
                String tempStr = patient.getHeadBmpStr();
                byte[] temp = Base64.decode(tempStr, Base64.DEFAULT);
                String picStr = new String(temp, "ISO-8859-1");
                //导入1.3.0数据身份证头像
                if (!TextUtils.isEmpty(picStr)) {
                    //头像名以时间戳唯一命名
                    String bmpStr = patient.getCard();
                    //保存头像至本地
                    UiUitls.savePhoto(picStr, bmpStr);
                    //从本地获取头像
                    Bitmap bitmap = BmpUtil.getBitmapByFileName(bmpStr);
                    if (bitmap != null) {
                        ivPic.setImageBitmap(bitmap);
                    } else {
                        ivPic.setImageResource(R.drawable.pic_default_avatar);
                    }
                } else {
                    ivPic.setImageResource(R.drawable.pic_default_avatar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (patient.getPatient_type()) {
            case PatientStyle.ADULT:
                tvType.setText(R.string.type_adult);
                break;
            case PatientStyle.YOUTH:
                tvType.setText(R.string.type_youth);
                break;
            case PatientStyle.BABY:
                tvType.setText(R.string.type_baby);
                break;
            default:
                break;
        }
        switch (patient.getSex()) {
            case MAN:
                ivSex.setImageResource(R.drawable.ic_male);
                break;
            case WOMAN:
                ivSex.setImageResource(R.drawable.ic_female);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化删除条目 map
     * @param list 用户记录列表
     * @param flag 标识
     * @param add  是否在原本的基础上增加数据
     */
    private void initDeletMap(List<PatientBean> list, boolean flag, boolean add) {
        int c = deletMap.size();
        if (!add) {
            deletMap = new HashMap<>();
            c = 0;
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                deletMap.put(c + i, flag);
            }
        }
    }

    /**
     * 根据查询条件，查询居民
     * @param item 查询条件
     */
    private void queryPatient(final QueryItem item) {
        if (item == null) {
            return;
        }
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                //复合条件查询
                totleCount = 0;
                item.setName(etSearch.getText().toString().trim());
                final List<PatientBean> lists = DBDataUtil.getPatientBeen(totleCount, pageCount
                        , item);
                totleCount = lists.size() + totleCount;
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                        tvSum.setText(getString(R.string.sum_1)
                                + DBDataUtil.getPatients(item) + getString(R.string.sum_2));
                        patientList = lists;
                        initDeletMap(patientList, false, false);
                        adapter.setDatas(patientList);
                    }
                });
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁dialog
        UiUitls.hideTitil();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = adapter.getCount() - 1;
        //数据集最后一项的索引
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex
                == itemsLastIndex) {
            UiUitls.showProgress(PatientListActivity.this
                    , getString(R.string.query_wait));
            UiUitls.postShortThread(new Runnable() {
                @Override
                public void run() {
                    final List<PatientBean> patient = DBDataUtil.getPatientBeen(totleCount
                            , pageCount, item);
                    totleCount = totleCount + patient.size();
                    if (isAllSelect) {
                        initDeletMap(patient, true, true);
                    } else {
                        initDeletMap(patient, false, true);
                    }
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.hideProgress();
                            adapter.setDeleteMap(deletMap);
                            tvSum.setText(getString(R.string.sum_1) + DBDataUtil.getPatients(
                                    item) + getString(R.string.sum_2));
                            patientList.addAll(patient);
                            adapter.setDatas(patientList);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }
}
