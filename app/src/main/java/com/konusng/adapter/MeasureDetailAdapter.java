package com.konusng.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CreateNewRecordDialog;
import com.konsung.util.DateUtil;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/8.
 */
public class MeasureDetailAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater = null;
    private PatientBean mPatient = null;
    private List<MeasureDataBean> mMeasure = null;
    private View lastView;
    private CreateNewRecordDialog create;

    private int editorVisibility = View.GONE;
    private int deleteVisibility = View.GONE;

    private Activity mContext;
    private int which;

    // handler
    private Handler mHandler;


    public MeasureDetailAdapter(Activity activity, PatientBean patient, int which,List<MeasureDataBean> measureDataBeans) {
        mLayoutInflater = LayoutInflater.from(activity);
        mPatient = patient;

        mContext = activity;
        this.which = which;
        this.mMeasure=measureDataBeans;
    }

    @Override
    public int getCount() {
        return mMeasure.size();
    }

    @Override
    public Object getItem(int position) {
        return mMeasure.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        MeasureDataBean _measure=mMeasure.get(position);
        if (convertView == null) {
            mViewHolder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.list_item_for_measure_detail, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.measure_time.setText(DateUtil.getTime(_measure.getMeasureTime()));
        mViewHolder.patientName.setText(mPatient.getName());
        mViewHolder.patientAge.setText(mPatient.getIdCard());
        mViewHolder.patientSex.setText(getSex(mPatient.getSex()));
        if ((!"".equals(mPatient.getBirthday())) && (null != mPatient.getBirthday())) {
            mViewHolder.patientBirthday.setText(DateFormat.getDateInstance(
                    DateFormat.LONG, Locale.CHINESE).format(mPatient.getBirthday()));
        }

        return convertView;
    }



    public final class ViewHolder {
        @InjectView(R.id.measure_time)
        TextView measure_time;
        @InjectView(R.id.tv_p_name)
        TextView patientName;
        @InjectView(R.id.p_sex)
        TextView patientSex;
        @InjectView(R.id.p_idcard)
        TextView patientAge;
        @InjectView(R.id.p_birthday)
        TextView patientBirthday;

    }

    private String getSex(int sex) {
        switch (sex) {
            case 0:
                return mContext.getString(R.string.sex_unknown);
            case 1:
                return mContext.getString(R.string.sex_man);
            case 2:
                return mContext.getString(R.string.sex_woman);
            case 3:
                return mContext.getString(R.string.sex_unsay);
            default:
                return mContext.getString(R.string.sex_unknown);
        }


    }
}
