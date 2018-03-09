package com.konsung.defineview;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konsung.R;
import com.konsung.bean.AddrCityBean;
import com.konsung.bean.AddrDistrictBean;
import com.konsung.bean.AddrProvinceBean;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konusng.adapter.AddrCityAdapter;
import com.konusng.adapter.AddrDistrictAdapter;
import com.konusng.adapter.AddrProvinceAdapter;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.konsung.util.GlobalConstant.ADDR_JSON;

/**
 * Created by dlx on 2017/10/25 0011.
 * 上传地区设置dialog弹出框
 */

public class DistrictPopupWindow extends Dialog {


    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.spn_province)
    Spinner spnProvince;
    @InjectView(R.id.spn_city)
    Spinner spnCity;
    @InjectView(R.id.spn_district)
    Spinner spnDistrict;
    @InjectView(R.id.btn_cancel)
    Button btnCancel;
    @InjectView(R.id.btn_ok)
    Button btnOk;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    private ArrayList<AddrProvinceBean> addressData;
    private Activity context;
    private OnCallBackListener listener;
    private String title;
    private AddrProvinceBean provinceBean;
    private AddrCityBean cityBean;
    private AddrDistrictBean districtBean;
    private String provinceName = "";
    private String cityName = "";
    private String districtName = "";

    /**
     * 构造函数
     * @param context 上下文引用
     */
    public DistrictPopupWindow(Activity context) {
        super(context, android.R.style.Theme_Translucent);
        this.context = context;
        initPopwindow();

        loadAddressData();
        spnProvince.setAdapter(new AddrProvinceAdapter(context, addressData));

        initView();
        initEvent();
    }

    /**
     * 加载assets中的三级地区数据（通过输入输出流）
     */
    private void loadAddressData() {
        // ### 加载三级地区数据 ###
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(context.getAssets().open(ADDR_JSON)
                    , "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();

            String json = stringBuilder.toString();
            Gson gson = new Gson();
            addressData = gson.fromJson(json, new
                    TypeToken<List<AddrProvinceBean>>() {
                    }.getType());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    /**
     * 初始化窗口
     */
    private void initPopwindow() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pop_district, null);
        ButterKnife.inject(this, view);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        initView();
        initEvent();
    }

    /**
     * 初始化显示
     */
    private void initView() {
//        tvTitle.setText(title);
        int provinceSelected = SpUtils.getSpInt(context.getApplicationContext()
                , "sys_config", "province", 0);
        spnProvince.setSelection(provinceSelected);

        int citySelected = SpUtils.getSpInt(context.getApplicationContext()
                , "sys_config", "city", 0);
        spnCity.setSelection(citySelected);

        int districtSelected = SpUtils.getSpInt(context.getApplicationContext()
                , "sys_config", "district", 0);
        spnDistrict.setSelection(districtSelected);
    }

    /**
     * 初始化数据
     */
    private void initEvent() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistrictPopupWindow.this.hide();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistrictPopupWindow.this.hide();
                SpUtils.saveToSp(context.getApplicationContext(),
                        "sys_config", "province", spnProvince.getSelectedItemPosition());
                SpUtils.saveToSp(context.getApplicationContext(), GlobalConstant.APP_CONFIG
                        , "provinceId", provinceBean.getAreaId());
                SpUtils.saveToSp(context.getApplicationContext(), "sys_config", "city"
                        , spnCity.getSelectedItemPosition());
                SpUtils.saveToSp(context.getApplicationContext(), GlobalConstant.APP_CONFIG
                        , "cityId", cityBean.getAreaId());
                SpUtils.saveToSp(context.getApplicationContext()
                        , "sys_config", "district", spnDistrict.getSelectedItemPosition());
                SpUtils.saveToSp(context.getApplicationContext(), GlobalConstant.APP_CONFIG
                        , "districtId", districtBean.getAreaId());
                if (listener != null) {
                    listener.onCommit(provinceName + cityName + districtName);
                }
            }
        });

        //上传地址省份的选择监听
        spnProvince.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int
                    position, long id) {
                provinceBean = addressData.get(position);
                provinceName = provinceBean.getAreaName();
                spnCity.setAdapter(new AddrCityAdapter(context, provinceBean.getAearList()));
                if (position == SpUtils.getSpInt(context.getApplicationContext()
                        , "sys_config", "province", 0)) {
                    int citySelected = SpUtils.getSpInt(context.getApplicationContext()
                            , "sys_config", "city", 0);
                    spnCity.setSelection(citySelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //上传地址城市的选择监听
        spnCity.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int
                    position, long id) {
                cityBean = provinceBean.getAearList().get(position);
                cityName = cityBean.getAreaName();
                spnDistrict.setAdapter(new AddrDistrictAdapter(context, cityBean.getAearList()));
                if (position == SpUtils.getSpInt(context
                        .getApplicationContext(), "sys_config", "city", 0)) {
                    int districtSelected = SpUtils.getSpInt(context
                            .getApplicationContext(), "sys_config", "district", 0);
                    spnDistrict.setSelection(districtSelected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //上传地址区/县的选择监听
        spnDistrict.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int
                    position, long id) {
                districtBean = cityBean.getAearList().get(position);
                districtName = districtBean.getAreaName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 监听查询条件确定的回调
     * @param listener 接口
     */
    public void addCallBackListener(OnCallBackListener listener) {
        this.listener = listener;
    }

    /**
     * 接口
     */
    public interface OnCallBackListener {
        /**
         * 回调方法
         * @param districtStr 字符
         */
        void onCommit(String districtStr);
    }
}
