package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.cengalabs.flatui.views.FlatEditText;
import com.konsung.R;
import com.konsung.defineview.ButtonFloatSmall;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JustRush on 2015/8/7.
 */
public class SpinnerAndFlatEdAdapter extends BaseAdapter {
    private Integer id;
    private ArrayList data;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<Integer> nums;


    public SpinnerAndFlatEdAdapter(Context mContext, Integer id
            , ArrayList data,ArrayList<Integer> nums) {
        this.id = id;
        this.data = data;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext=mContext;
        this.nums=nums;
    }

    @Override
    public int getCount() {
        return data.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.list_item_for_symptom, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext, id, R.layout.spinner_button);
        adapter.setDropDownViewResource(R.layout.flat_list_item);
        mViewHolder.other_sp.setAdapter(adapter);
        final ViewHolder finalMViewHolder = mViewHolder;
        mViewHolder.other_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0;i<nums.size();i++){
                    if(position==nums.get(i))
                    {
                        finalMViewHolder.other_ed.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return convertView;
    }

    public final class ViewHolder {
        @InjectView(R.id.other_sp)
        Spinner other_sp;
        @InjectView(R.id.other_ed)
        FlatEditText other_ed;
        @InjectView(R.id.other_add)
        ButtonFloatSmall other_add;
    }

}
