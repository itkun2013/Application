package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;

import java.util.ArrayList;

/**
 * Created by lipengjie on 2016/9/27 0027.
 */
public class PopupWindowAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mData;

    public PopupWindowAdapter(Context context, ArrayList<String> data){
        mContext = context;
        mData = data;

    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_item_text,null);
            viewHolder = new ViewHolder();
            viewHolder.index = (TextView) view.findViewById(R.id.pop_item_text);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.index.setText(mData.get(position));

        return view;
    }

    private class ViewHolder{
        TextView index;
    }

}
