package com.konusng.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.konsung.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 图片列表适配器
 * Created by DJH on 2017/9/1 0001.
 */
public class PictureListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Bitmap> bitmapList;
    private int selectedPosition = -1;

    /**
     * 构造方法
     * @param context 上下文
     * @param bitmaps 图片集合
     */
    public PictureListAdapter(Context context, List<Bitmap> bitmaps) {
        this.layoutInflater = LayoutInflater.from(context);
        this.bitmapList = bitmaps;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_list_of_picture, null);
            // 给mViewHolder容器内容初始化
            ButterKnife.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivImage.setImageBitmap(bitmapList.get(position));
        if (position == selectedPosition) {
            viewHolder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 适配器
     */
    class ViewHolder {
        @InjectView(R.id.iv_image)
        ImageView ivImage;
        @InjectView(R.id.iv_selected)
        ImageView ivSelected;
    }

    /**
     * 设置选中的图片position
     * @param position item位置
     */
    public void setPosition(int position) {
        this.selectedPosition = position;
    }
}
