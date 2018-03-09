package com.konsung.defineview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QueryItem;
import com.konsung.util.DBDataUtil;
import com.konusng.adapter.PopupwindowListAdapter;

import java.util.List;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 * 用户列表下拉框(用于单项测量页面和体检报告列表页面)
 */

public class ListPopupWindow extends PopupWindow implements AbsListView.OnScrollListener {

    private Context context;
    private View mView;
    private int height;
    private int width;
    private ListView listView;
    private EditText etSearch;
    private PopupwindowListAdapter popupwindowListAdapter;
    private List<PatientBean> list;
    private OnPopwindowItemClickDismissListener onPopwindowItemClickDismissListener;
    //临时姓名存值
    private String nameStr = "";
    private final int pageCount = 10; //一页显示的数量
    private int totleCount = 0; //当前页数显示的总数量
    private int visibleLastIndex = 0; //最后的可视项索引
    QueryItem item = new QueryItem(); //记录查询条件
    /**
     * 构造函数
     * @param context 上下文引用
     * @param height 高度
     * @param width 宽度
     */
    public ListPopupWindow(Context context, int height, int width) {
        super(context);
        this.context = context;
        this.width = width;
        this.height = height;
        initPopwindow();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = popupwindowListAdapter.getCount() - 1;
         //数据集最后一项的索引
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex
                == itemsLastIndex) {
            List<PatientBean> patient = DBDataUtil.getPatientBeen(totleCount, pageCount, item);
            totleCount = totleCount + patient.size();
            list.addAll(patient);
            popupwindowListAdapter.setListData(list);
            popupwindowListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }

    /**
     * 窗口点击item后消失监听接口
     */
    public interface OnPopwindowItemClickDismissListener {
        /**
         * 消失回调方法
         * @param position 点击条目位置
         * @param listPatient 用户列表
         */
        public void onItemClickDismiss(int position, List<PatientBean> listPatient);
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.popwindow_layout, null);
        setContentView(mView);
        setWidth(width);
        setHeight(height);
        //注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
        setBackgroundDrawable(new BitmapDrawable());
        //设置外部点击关闭ppw窗口
        setOutsideTouchable(true);
        setFocusable(true);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0000000000);
//        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        this.setBackgroundDrawable(dw);
        initView(mView);
        initData();
        initListener();
    }

    /**
     * 初始化视图
     * @param view 视图
     */
    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.pop_list);
        etSearch = (EditText) view.findViewById(R.id.measure_search_et);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = DBDataUtil.getPatientBeen(totleCount, pageCount, item);
        totleCount += pageCount;
        popupwindowListAdapter = new PopupwindowListAdapter(context, list);
        listView.setAdapter(popupwindowListAdapter);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPopwindowItemClickDismissListener.onItemClickDismiss(position, list);
                dismiss();
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                totleCount = 0;
                nameStr = s.toString();
                list.clear();
                item.setName(nameStr);
                list = DBDataUtil.getPatientBeen(totleCount, pageCount, item);
                popupwindowListAdapter.setListData(list);
                popupwindowListAdapter.notifyDataSetChanged();
                totleCount = totleCount + list.size();
            }
        });
        listView.setOnScrollListener(this);  //添加滑动监听
    }


    /**
     * 设置监听方法
     * @param listener 接口对象
     */
    public void setListener(OnPopwindowItemClickDismissListener listener) {
        onPopwindowItemClickDismissListener = listener;
    }

    /**
     * 清除搜索框
     */
    public void clearSeachBox() {
        if (!TextUtils.isEmpty(etSearch.getText().toString().trim())) {
            etSearch.setText("");
            list.clear();
            item = new QueryItem();
            totleCount = 0;
            list = DBDataUtil.getPatientBeen(totleCount, pageCount, item);
            popupwindowListAdapter.setListData(list);
            popupwindowListAdapter.notifyDataSetChanged();
            totleCount = totleCount + list.size();
        }
    }
}
