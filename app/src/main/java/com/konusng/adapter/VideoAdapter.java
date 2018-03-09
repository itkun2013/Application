package com.konusng.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.VideoInfo;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/4/20 0020.
 */

public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<VideoInfo> list;
    private LayoutInflater inflater;
    private Handler handler;
    /**
     * 构造方法
     * @param context 上下文引用
     * @param list 集合
     * @param handler 事件传递对象
     */
    public VideoAdapter(Context context, List<VideoInfo> list, Handler handler) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.handler = handler;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.video_item_layout, null);
            ButterKnife.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoInfo videoInfo = list.get(position);
        viewHolder.ivThumb.setImageBitmap(videoInfo.getThumbnail());
        viewHolder.tvVideoName.setText(UiUitls.separatorVideoName(videoInfo.getName()));
        viewHolder.bg.getBackground().setAlpha(GlobalNumber.TWENTY_NUMBER);
        viewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.what = 4;
                message.obj = position;
                handler.sendMessage(message);
            }
        });
        return convertView;
    }

    /**
     *设置数据
     * @param list 数据集合
     */
    public void setData(List<VideoInfo> list) {
        this.list = list;
    }
    class ViewHolder{
        @InjectView(R.id.pic_iv)
        ImageView ivThumb;
        @InjectView(R.id.video_name)
        TextView tvVideoName;
        @InjectView(R.id.bg)
        View bg;
        @InjectView(R.id.btn_play)
        Button btnPlay;
    }
}
