package com.konusng.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.defineview.CustomToast;
import com.konsung.defineview.TipsDialog;
import com.konsung.upload.UploadData;
import com.konsung.util.DBDataUtil;
import com.konsung.util.UiUitls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by YYX on 2016/6/14 0014.
 * 显示病人的测量数据的adpter
 */
public class MeasureDataAdapter extends BaseAdapter {
    //上下文
    private Context context;
    //用户测量数据
    private List<MeasureDataBean> dataBeen;
    //记录序列号
    private int count = 0;
    //当前日期
    String shortDate;
    SimpleDateFormat dateFormat;

    //是否上传成功
    boolean isUpload = false;
    //延时时间
    private static final int TIME = 300;
    /**
     * 构造器
     * @param context 上下文引用
     * @param dataBeanList 数据集合
     */
    public MeasureDataAdapter(Context context, List<MeasureDataBean> dataBeanList) {
        this.context = context;
        this.dataBeen = dataBeanList;
        dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        shortDate = dateFormat.format(new Date());
    }

    @Override
    public int getCount() {
        if (null != dataBeen) {
            return dataBeen.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != dataBeen) {
            return dataBeen.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.adpter_measuredata, null);
            holder = new ViewHolder();
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MeasureDataBean bean = dataBeen.get(position);
        //设置测量医生
        holder.tvDoctor.setText(TextUtils.isEmpty(bean.getDoctor()) ?
                context.getString(R.string.sex_unknown) : bean.getDoctor());
        //设置测量时间
        holder.tvMeasureTime.setText(UiUitls.getDateFormat(UiUitls.DateState
                .LONG).format(bean.getCheck_day()));
        //判断用户是否上传
        if (bean.getUploadFlag()) {
            holder.tvState.setTextColor(context.getResources()
                    .getColor(R.color.grass_konsung_2));
            holder.tvState.setText(UiUitls.getString(R.string.uploaded));
            //已经上传的和今天测量的都隐藏上传按钮
            holder.upload.setVisibility(View.GONE);
        } else {
            holder.tvState.setTextColor(Color.RED);
            holder.tvState.setText(UiUitls.getString(R.string.unUpload));
            holder.upload.setVisibility(View.VISIBLE);
//            if (shortDate.equals(dateFormat.format(bean.getCreteTime()))) {
//                holder.upload.setVisibility(View.GONE);
//            } else {
//                holder.upload.setVisibility(View.VISIBLE);
//            }
        }
        //设置序列号
        holder.tvSerial.setText(count + position + 1 + "");
        //给删除设置点击事件
        holder.ivOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.showTitle(context, UiUitls.getString(R.string
                        .confirm), UiUitls.getString(R.string.confirm_delete_measure),
                        new TipsDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                if (pressed) {
                                    DBDataUtil.deleteMeasure(bean);
                                    dataBeen.remove(bean);
                                    deleteOne();
                                    notifyDataSetChanged();
                                }
                                UiUitls.hideTitil();
                            }
                        });
            }
        });
        //点击上传按钮
        holder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUitls.showProgress(context, UiUitls.getString(R.string.isuploading));
                isUpload = false;
                new UploadData(context).uploadMeasureData(bean, new UploadData.ResponseCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        UiUitls.hideProgress();
                        if (s.equals("10000")) {
                            CustomToast.showMessage(context, UiUitls
                                    .getString(R.string.updata_sussess));
                            isUpload = true;
                            bean.setUploadFlag(isUpload);
                            DBDataUtil.getMeasureDao().update(bean);
                            UiUitls.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, TIME);
                        } else {
                            CustomToast.showMessage(context, UiUitls
                                    .getString(R.string.data_upload_fail));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        UiUitls.hideProgress();
                        CustomToast.showMessage(context, UiUitls
                                .getString(R.string.data_upload_fail));
                    }

                    @Override
                    public void onException() {
                        UiUitls.hideProgress();
                        CustomToast.showMessage(context, UiUitls
                                .getString(R.string.data_upload_fail));
                    }
                });
            }
        });
        return convertView;
    }

    /**
     * holder类
     */
    class ViewHolder {
        //序号
        @InjectView(R.id.tv_serial)
        TextView tvSerial;
        //测量医生
        @InjectView(R.id.tv_doctor)
        TextView tvDoctor;
        //测量时间
        @InjectView(R.id.tv_measure_time)
        TextView tvMeasureTime;
        //上传状态
        @InjectView(R.id.tv_state)
        TextView tvState;
        //删除操作
        @InjectView(R.id.rela_operator)
        RelativeLayout ivOperator;
        //上传操作
        @InjectView(R.id.upload)
        ImageView upload;
    }

    /**
     * 调用删除方法
     */
    public void deleteOne() {

    }

    /**
     * 设置显示条数
     * @param count 条数
     */
    public void setCount(int count) {
        this.count = count;
    }
}
