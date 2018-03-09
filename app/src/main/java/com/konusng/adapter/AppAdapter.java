package com.konusng.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 已弃，康尚App程序适配器
 * @author ouyangfan
 * @version 0.0.1
 */
public class AppAdapter extends BaseAdapter {
    // 布局填充器
    private LayoutInflater mLayoutInflater;
    private List<ResolveInfo> mApps;
    private Context context;

    /**
     * 构造器
     * @param context 上下文
     * @param apps 集合
     */
    public AppAdapter(Context context, List<ResolveInfo> apps) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mApps = apps;
    }

    @Override
    public int getCount() {
        return mApps.size();
    }

    @Override
    public Object getItem(int position) {
        return mApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.launcher_gridview_item, null);
            // 给mViewHolder容器内容初始化
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        ResolveInfo info = mApps.get(position);
        mViewHolder.mImageView.setImageDrawable(info.activityInfo
                .loadIcon(context.getPackageManager()));
        Log.d("test", info.activityInfo.loadLabel(context.getPackageManager()).toString());
        mViewHolder.mTextView.setText(info.activityInfo.loadLabel(context.getPackageManager()));
        return convertView;
    }

    /**
     * 控件容器
     */
    public final class ViewHolder {
        // 程序图标
        @InjectView(R.id.launcher_iv) ImageView mImageView;
        // 程序文字描述
        @InjectView(R.id.launcher_tv) TextView mTextView;
    }
}
