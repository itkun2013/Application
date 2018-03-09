package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.Response.VideoResponse;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 类功能：
 */

public class OnlineVideoAdapter extends BaseAdapter {

    private List<VideoResponse.Video> data;
    private LayoutInflater layoutInflater;

    /**
     * 构造器
     * @param context 上下文
     * @param data 文本数据集
     */
    public OnlineVideoAdapter(Context context, List<VideoResponse.Video> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_online_video, null);
            ButterKnife.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoResponse.Video bean = data.get(position);
        String title = bean.getTitle();
        viewHolder.tvTitle.setText(title);
        return convertView;
    }

    /**
     * 视图容器
     */
    class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView tvTitle;
    }
}
