package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.AddrDistrictBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 上传地址区/县下拉框适配器
 * Created by DJH on 2016/7/25 0025.
 */
public class AddrDistrictAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context context;
    private List<AddrDistrictBean> mDatas = new ArrayList<>();

    /**
     * 构造器
     * @param context 上下文引用
     * @param datas 集合数组
     */
    public AddrDistrictAdapter(Context context, List<AddrDistrictBean> datas) {
        mDatas = datas;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }


    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AddrDistrictBean bean = mDatas.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_listview_area, null);
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String countyName = bean.getAreaName();
        holder.tvDistrict.setText(countyName);
        return convertView;
    }

    /**
     * ViewHolder类
     */
    static class ViewHolder {
        @InjectView(R.id.tv_area_name)
        TextView tvDistrict;
    }
}
