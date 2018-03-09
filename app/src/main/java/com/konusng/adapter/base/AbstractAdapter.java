package com.konusng.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xiangshicheng on 2017/7/13 0013.
 * @param <T> 泛型
 */
public abstract class AbstractAdapter<T> extends BaseAdapter {

    protected Context context;

    protected List<T> listData;

    protected LayoutInflater layoutInflater;

    /**
     * 构造器
     * @param context 上下文
     * @param listData 源数据
     */
    public AbstractAdapter(Context context, List<T> listData) {
        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData == null ? null : listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取集合数组
     * @return 集合数据
     */
    public List<T> getListData() {
        return listData;
    }

    /**
     * 设置list集合数据
     * @param listData 集合
     */
    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    /**
     * 获取视图
     * @param layoutId 布局id
     * @return 视图
     */
    public View inflate(int layoutId) {
        return layoutInflater.inflate(layoutId, null);
    }

}