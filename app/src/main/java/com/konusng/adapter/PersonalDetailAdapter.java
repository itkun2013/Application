package com.konusng.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CreateNewRecordDialog;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/4.
 */


public class PersonalDetailAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater = null;
    private List<PatientBean> mPatient = null;
    private View lastView;
    private CreateNewRecordDialog create;

    private int editorVisibility = View.GONE;
    private int deleteVisibility = View.GONE;

    private Activity mContext;
    private int which;

    // handler
    private Handler mHandler;


    public PersonalDetailAdapter(Activity activity, List<PatientBean> patientLists, int which) {
        mLayoutInflater = LayoutInflater.from(activity);
        mPatient = patientLists;

        mContext = activity;
        this.which = which;
    }

    @Override
    public int getCount() {
        return mPatient.size();
    }

    @Override
    public Object getItem(int position) {
        return mPatient.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        PatientBean _patient = mPatient.get(position);
        if (convertView == null) {
            mViewHolder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.list_item_for_all, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.patientName.setText(_patient.getName());
        mViewHolder.patientAge.setText(_patient.getIdCard());
        mViewHolder.patientSex.setText(getSex(_patient.getSex()));
        if ((!"".equals(_patient.getBirthday())) && (null != _patient.getBirthday())) {
            mViewHolder.patientBirthday.setText(DateFormat.getDateInstance(
                    DateFormat.LONG, Locale.CHINESE).format(_patient.getBirthday()));
        }

        return convertView;
    }



    public final class ViewHolder {
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
