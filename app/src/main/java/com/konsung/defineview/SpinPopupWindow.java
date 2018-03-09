package com.konsung.defineview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.konsung.R;
import com.konusng.adapter.PopupwindowListAdapter;
import com.konusng.adapter.PopuwindowSpinAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 */

public class SpinPopupWindow extends PopupWindow {

    //上下文引用
    private Context context;
    //PopWindow布局视图
    private View mView;
    //Popwindow高度
    private int height;
    //PopWindow宽度
    private int width;
    private ListView listView;
    //适配器
    private PopuwindowSpinAdapter popuwindowSpinAdapter;
    //列表数据集合
    private List<String> list;
    //回调
    private OnPopwindowSelectListener onPopwindowSelectListener;
    //列表点击选中位置
    private int position;
    //类型标识
    private int typeFlag = 0;
    /**
     * 构造方法
     * @param context 上下文
     * @param height 高度
     * @param width 宽度
     * @param objArr 列表数组
     * @param position 默认选中位置标识
     * @param typeFlag 判断类型
     */
    public SpinPopupWindow(Context context, int height, int width, String[] objArr
            , int position, int typeFlag) {
        super(context);
        this.context = context;
        this.width = width;
        this.height = height;
        list = arrToList(objArr);
        this.typeFlag = typeFlag;
        this.position = position;
        initPopwindow();
    }

    /**
     * 点击item回调
     */
    public interface OnPopwindowSelectListener {
        /**
         * Popwindow消失并传递选中位置
         * @param position 选中位置
         * @param flag 类型区分窗口
         */
        public void onSelect(int position, int flag);
    }

    /**
     * 初始化PopWindow
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.spinner_layout, null);
        setContentView(mView);
        setWidth(width);
        setHeight(height);
        //注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
        setBackgroundDrawable(new BitmapDrawable());
        //设置外部点击关闭ppw窗口
        setOutsideTouchable(true);
        setFocusable(true);
        initView(mView);
        initData();
        initListener();
    }

    /**
     * 初始化视图
     * @param view 视图
     */
    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.spinner_list);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        popuwindowSpinAdapter = new PopuwindowSpinAdapter(context, list, position);
        listView.setAdapter(popuwindowSpinAdapter);
        popuwindowSpinAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPopwindowSelectListener.onSelect(position, typeFlag);
                dismiss();
            }
        });
    }

    /**
     * 添加接口对象
     * @param onPopwindowSelectListener 回调接口
     */
    public void setOnPopwindowSelectListener(OnPopwindowSelectListener onPopwindowSelectListener) {
        this.onPopwindowSelectListener = onPopwindowSelectListener;
    }

    /**
     * 数据转换为集合
     * @param objArr 数组
     *@return 集合
     */
    private List<String> arrToList(String[] objArr) {
        if (objArr.length <= 0) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0;i < objArr.length;i ++) {
            list.add(objArr[i]);
        }
        return  list;
    }
}
