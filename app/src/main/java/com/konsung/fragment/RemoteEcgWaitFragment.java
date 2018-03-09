package com.konsung.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.Request.QueryEcgDiagnosesRequest;
import com.konsung.bean.Response.EcgQueryResponse;
import com.konsung.msgevent.RemoteEcgSearchEvent;
import com.konsung.upload.UploadData;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konusng.adapter.PopupWindowAdapter;
import com.konusng.adapter.RemoteRecordAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/7/17 0017.
 */

public class RemoteEcgWaitFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.left_list)
    ListView leftListView;
    @InjectView(R.id.right_list)
    ListView rightListView;

    @InjectView(R.id.remote_btn_first)
    Button btnFirst;
    @InjectView(R.id.remote_btn_previous)
    Button btnPrevious;
    @InjectView(R.id.remote_tv_number)
    TextView tvCurrentNumber;
    @InjectView(R.id.remote_btn_next)
    Button btnNext;
    @InjectView(R.id.remote_btn_last)
    Button btnLast;

    @InjectView(R.id.total_and_current)
    TextView tvAllShow;

    //左适配器数据
    private List<EcgQueryResponse.RowData> leftListData;
    //右适配器数据
    private List<EcgQueryResponse.RowData> rightListData;
    //左适配器
    private RemoteRecordAdapter leftRemoteRecordAdapter;
    //右适配器
    private RemoteRecordAdapter rightRemoteRecordAdapter;

    //当前页
    private int allCurrentPage;

    //默认从0开始查询
    private int startPosition = 0;
    //每页查询十条记录
    private final int limitNum = 10;
    //总条数
    private int totalNum;
    //总页数
    private int totalPage;
    //查询条件
    private String queryContent = "";
    //记录最后一页的起点位置
    private int lastPageStartPosition;
    //记录是否首次进入页面加载
    private boolean isFirstIn = true;
    //判断是否在查询状态
    private boolean isQuery = false;
    //每列5条记录
    public final int cloFive = 5;
    //判断是否有查询条件
    private boolean isHasCondition = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_ecg_public, null);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {}

    /**
     * 初始化数据
     */
    private void initData() {
        leftListData = new ArrayList<EcgQueryResponse.RowData>();
        rightListData = new ArrayList<EcgQueryResponse.RowData>();

        leftRemoteRecordAdapter = new RemoteRecordAdapter(getActivity(), leftListData);
        rightRemoteRecordAdapter = new RemoteRecordAdapter(getActivity(), rightListData);

        leftListView.setAdapter(leftRemoteRecordAdapter);
        rightListView.setAdapter(rightRemoteRecordAdapter);

        tvCurrentNumber.setText(allCurrentPage + "");
        tvAllShow.setText(String.format(getRecString(R.string.current_and_all)
                , allCurrentPage, totalPage));
        //数据查询
        queryRemoteEcgData(startPosition);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        btnFirst.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLast.setOnClickListener(this);
        tvCurrentNumber.setOnClickListener(this);
        leftListView.setOnItemClickListener(leftOnItemClick);
        rightListView.setOnItemClickListener(rightOnItemClick);
    }

    AdapterView.OnItemClickListener leftOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EcgQueryResponse.RowData rowData = leftListData.get(position);
            //单例类临时数据保存
            EcgRemoteInfoSaveModule.getInstance().rowData = rowData;
            //跳转至心电报告详情页面
            UiUitls.openEcgReport(getActivity());

        }
    };

    AdapterView.OnItemClickListener rightOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //点击跳转至心电报告详情页面
            EcgQueryResponse.RowData rowData = rightListData.get(position);
            //单例类临时数据保存
            EcgRemoteInfoSaveModule.getInstance().rowData = rowData;
            //跳转至心电报告详情页面
            UiUitls.openEcgReport(getActivity());

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //跳转至首页
            case R.id.remote_btn_first:
                if (totalPage > 0) {
                    if (allCurrentPage == 1) {
                        UiUitls.toast(getActivity(), getRecString(R.string.remote_first_page));
                        return;
                    }
                    allCurrentPage = 1;
                    startPosition = 0;
                    queryRemoteEcgData(startPosition);
                }
                break;
            //跳转至上一页
            case R.id.remote_btn_previous:
                if (totalPage > 0) {
                    if (startPosition >= limitNum) {
                        allCurrentPage--;
                        startPosition -= limitNum;
                        queryRemoteEcgData(startPosition);
                    } else {
                        UiUitls.toast(getActivity(), getRecString(R.string.remote_first_page));
                    }
                }
                break;
            //跳转至下一页
            case R.id.remote_btn_next:
                if (totalPage > 0) {
                    if ((lastPageStartPosition - startPosition) >= limitNum) {
                        allCurrentPage++;
                        startPosition += limitNum;
                        queryRemoteEcgData(startPosition);
                    } else {
                        UiUitls.toast(getActivity(), getRecString(R.string.remote_last_page));
                    }
                }
                break;
            //跳转至最后一页
            case R.id.remote_btn_last:
                if (totalPage > 0) {
                    if (allCurrentPage == totalPage) {
                        UiUitls.toast(getActivity(), getRecString(R.string.remote_last_page));
                        return;
                    }
                    allCurrentPage = totalPage;
                    startPosition = lastPageStartPosition;
                    queryRemoteEcgData(startPosition);
                }
                break;
            //当前页点击
            case R.id.remote_tv_number:
                if (totalNum > 0) {
                    ArrayList<String> datas = new ArrayList<>();
                    for (int i = 1; i <= totalPage; i++) {
                        if (i < limitNum) {
                            datas.add("0" + i);
                        } else {
                            datas.add(i + "");
                        }
                    }
                    initPopWindow(datas, v);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 弹窗显示页数
     * @param page 页数集合
     * @param view 视图
     */
    private void initPopWindow(ArrayList<String> page, View view) {
        View contentView = LayoutInflater.from(this.getActivity()
                .getApplicationContext()).inflate(R.layout.popupwindow_page, null);
        contentView.setBackgroundColor(Color.BLUE);
        final PopupWindow popupWindow = new PopupWindow(this.getActivity().findViewById(R.id
                .get_all_patient_fragment), 1300, 350);
        contentView.setBackgroundColor(this.getResources().getColor(R.color.white));
        popupWindow.setContentView(contentView);
        TextView textView = (TextView) contentView.findViewById(R.id.pop_title);
        textView.setText(UiUitls.getString(R.string.select_page_number));
        ListView listView = (ListView) contentView.findViewById(R.id.pop_list);
        PopupWindowAdapter adapter = new PopupWindowAdapter(this.getActivity(), page);
        listView.setAdapter(adapter);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(GlobalConstant.BACKGROUND_COLOR));
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strPageNumber = (i + 1) + "";
                allCurrentPage = Integer.parseInt(strPageNumber);
                startPosition = (allCurrentPage - 1) * limitNum;
                queryRemoteEcgData(startPosition);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 远程心电数据查询
     * @param beginPosition 查询的起始位置
     */
    private void queryRemoteEcgData(final int beginPosition) {
        QueryEcgDiagnosesRequest request = new QueryEcgDiagnosesRequest();
        //申请医生ID
        request.doctorCode = GlobalConstant.EPMID;
        request.condition = ((RemoteEcgFragment) getParentFragment()).getInputContent();
        request.state = RemoteEcgFragment.ECG_WAIT_STATE;
        request.beginPage = beginPosition;
        request.pageRecord = limitNum;
        UiUitls.showProgress(getActivity(), getRecString(R.string.querying));
        new UploadData(getActivity()).queryEcgRemote(request, new UploadData.ResponseCallBack() {
            @Override
            public void onSuccess(String s) {
                UiUitls.hideProgress();
                try {
                    EcgQueryResponse response = JsonUtils.toEntity(s, EcgQueryResponse.class);
                    totalNum = response.total;
                    if (totalNum > 0) {
                        //有数据
                        UiUitls.toast(getActivity(), getRecString(R.string.query_success));
                    } else {
                        //无数据
                        UiUitls.toast(getActivity(), getRecString(R.string.query_no_data));
                    }
                    ((RemoteEcgFragment) getParentFragment())
                            .setEcgRecordNum(RemoteEcgFragment.ECG_WAIT_STATE, totalNum);
                    showTowSideListData(response.rows);
                    if ((totalNum % limitNum) != 0) {
                        totalPage = totalNum / limitNum + 1;
                    } else {
                        totalPage = totalNum / limitNum;
                    }
                    //计算出最后一页的起始位置
                    if (totalPage > 0) {
                        lastPageStartPosition = (totalPage - 1) * limitNum;
                        if (isFirstIn) {
                            isFirstIn = false;
                            allCurrentPage = 1;
                        }
                        if (isQuery) {
                            isQuery = false;
                            allCurrentPage = 1;
                        }
                    }
                    tvCurrentNumber.setText(allCurrentPage + "");
                    tvAllShow.setText(String.format(getRecString(R.string.current_and_all)
                            , allCurrentPage, totalPage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(String message) {
                UiUitls.hideProgress();
                UiUitls.toast(getActivity(), getRecString(R.string.query_fail));
            }

            @Override
            public void onException() {
                UiUitls.hideProgress();
            }
        });
    }

    /**
     * 显示两列list列表
     * @param rowData 数据对象
     */
    private void showTowSideListData(List<EcgQueryResponse.RowData> rowData) {
        leftListData.clear();
        rightListData.clear();
        if (rowData != null && rowData.size() > 0) {
            for (int i = 0; i < rowData.size(); i++) {
                if (i < cloFive) {
                    leftListData.add(rowData.get(i));
                } else {
                    rightListData.add(rowData.get(i));
                }
            }
            //左右列表数据刷新
            leftRemoteRecordAdapter.notifyDataSetChanged();
            rightRemoteRecordAdapter.notifyDataSetChanged();
        }
    }

    /**
     * EventBus的接收事件
     * @param event 事件实体类
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchEcgData(RemoteEcgSearchEvent event) {
        if (event.getEcgState().equals(RemoteEcgFragment.ECG_WAIT_STATE)) {
            startPosition = 0;
            allCurrentPage = 0;
            isQuery = true;
            queryRemoteEcgData(startPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (!TextUtils.isEmpty(((RemoteEcgFragment) getParentFragment()).getInputContent())) {
                isHasCondition = true;
                startPosition = 0;
            } else {
                isHasCondition = false;
            }
            ((RemoteEcgFragment) getParentFragment()).isAutoClear = true;
            ((RemoteEcgFragment) getParentFragment()).clearEtContent();
            if (isHasCondition) {
                queryRemoteEcgData(startPosition);
            }
        }
    }
}
