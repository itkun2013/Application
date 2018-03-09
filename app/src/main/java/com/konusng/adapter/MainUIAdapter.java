package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.konsung.R;

import java.util.ArrayList;

/**
 * Created by U.PC on 2015/6/1.
 */
public class MainUIAdapter extends BaseAdapter{
    private ArrayList<Integer> list;
    private LayoutInflater inflater;

    public MainUIAdapter(ArrayList<Integer> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        SurfaceHolder holder;
        if(convertView==null){
            view=inflater.inflate(R.layout.select_button_view,null);
            holder=new SurfaceHolder();
            holder.image=(ImageView)view.findViewById(R.id.image);
            convertView=view;
            convertView.setTag(holder);
        }
        else {
            holder=(SurfaceHolder)convertView.getTag();
        }
            holder.image.setImageResource(list.get(position));

        return convertView;
    }

    class SurfaceHolder{
        public ImageView image;
    }
}
