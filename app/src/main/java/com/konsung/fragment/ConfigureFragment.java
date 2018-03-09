package com.konsung.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konsung.R;
import com.konsung.activity.BaseActivity;
import com.konsung.activity.LoginActivity;
import com.konsung.bean.AddrCityBean;
import com.konsung.bean.AddrDistrictBean;
import com.konsung.bean.AddrProvinceBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.UserBean;
import com.konsung.defineview.DevicesPopupWindow;
import com.konsung.defineview.DistrictPopupWindow;
import com.konsung.defineview.EditTextPopWindow;
import com.konsung.defineview.IpPopupWindow;
import com.konsung.defineview.ParamPopupWindow;
import com.konsung.defineview.ProgressDialog;
import com.konsung.defineview.ToggleView;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.konsung.util.GlobalConstant.ADDR_JSON;
import static com.konsung.util.GlobalConstant.CHAR_SEMICOLON;

/**
 * 配置世轩网络的ip地址
 * Created by JustRush on 2015/9/12.
 */
public class ConfigureFragment extends BaseFragment {
    public static final String IP = "ip";
    //每行最大显示文本
    public static final int LINE_MAX_SIZE = 26;
    private final int hundredValue = 100;
    @InjectView(R.id.iv_head)
    ImageView ivHead;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.btn_exit)
    Button btnExit;
    @InjectView(R.id.toggle_upload)
    ToggleView toggleUpload;
    @InjectView(R.id.toggle_update)
    ToggleView toggleUpdate;
    @InjectView(R.id.tv_update_addr)
    TextView tvUpdateAddr;
    @InjectView(R.id.rl_update)
    RelativeLayout rlUpdate;
    @InjectView(R.id.tv_server_ip)
    TextView tvServerIp;
    @InjectView(R.id.rl_server)
    RelativeLayout rlServer;
    @InjectView(R.id.tv_server_net_addr)
    TextView tvServerNetAddr;
    @InjectView(R.id.rl_server_net_addr)
    RelativeLayout rlServerNetAddr;
    @InjectView(R.id.tv_cloud_ip)
    TextView tvCloudIp;
    @InjectView(R.id.rl_cloud_ip)
    RelativeLayout rlCloudIp;
    @InjectView(R.id.tv_cloud_net_addr)
    TextView tvCloudNetAddr;
    @InjectView(R.id.rl_cloud_net_addr)
    RelativeLayout rlCloudNetAddr;
    @InjectView(R.id.tv_upload_district)
    TextView tvUploadDistrict;
    @InjectView(R.id.rl_upload_district)
    RelativeLayout rlUploadDistrict;
    @InjectView(R.id.tv_upload_detail)
    TextView tvUploadDetail;
    @InjectView(R.id.rl_upload_detail)
    RelativeLayout rlUploadDetail;
    @InjectView(R.id.tv_param)
    TextView tvParam;
    @InjectView(R.id.rl_param_config)
    RelativeLayout rlParamConfig;
    @InjectView(R.id.tv_devices)
    TextView tvDevices;
    @InjectView(R.id.rl_devices_config)
    RelativeLayout rlDevicesConfig;
    @InjectView(R.id.ll_bottom_btn)
    LinearLayout llBottomBtn;
    @InjectView(R.id.btn_clear_data)
    Button btnClearData;
    @InjectView(R.id.btn_start_logo)
    Button btnStartLogo;
    @InjectView(R.id.rl_card_input)
    RelativeLayout rlCardInput;
    @InjectView(R.id.toggle_card_input)
    ToggleView toggleCardInput;
    @InjectView(R.id.rl_network)
    RelativeLayout rlNetwork;
    private IpPopupWindow popupWindow;
    private String cloudNetAddress;
    private EditTextPopWindow editTextPopWindow;
    private String serverNetAddr;
    private String uploadDetail;
    private List<AddrProvinceBean> addressData;
    private Context context = null;
    private Bitmap bmp = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        context = getActivity();
        View view = View.inflate(context, R.layout.fragment_configure2, null);
        ButterKnife.inject(this, view);
        initView();
        initEvent();
        return view;
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //厂家维护模式内退出登录后清空厂家维护的标识信息
                if (GlobalConstant.isAdministraor) {
                    GlobalConstant.isAdministraor = false;
                    GlobalConstant.TEST_PASSWORD = "";
                }
                BaseActivity activity = (BaseActivity) context;
                activity.pushActivity(context, LoginActivity.class);
            }
        });

        rlUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow = new IpPopupWindow(getActivity(), IpPopupWindow.IP_TYPE_UPDATE);
                popupWindow.show();
                popupWindow.addCallBackListener(new IpPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String ip, String port) {
                        SpUtils.saveToSp(context
                                , GlobalConstant.APP_CONFIG, GlobalConstant.REFRESH_IP, ip);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.REFRESH_IP_PORT, port);
                        tvUpdateAddr.setText(ip + UiUitls.getString(R.string.colon) + port);
                    }

                    @Override
                    public void onCancel(String ip, String port) {

                    }
                });
            }
        });

        rlServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow = new IpPopupWindow(getActivity(), IpPopupWindow.IP_TYPE_SERVER);
                popupWindow.show();
                popupWindow.addCallBackListener(new IpPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String ip, String port) {
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, IP, ip);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.IP_PROT, port);
                        tvServerIp.setText(ip + UiUitls.getString(R.string.colon) + port);
                    }

                    @Override
                    public void onCancel(String ip, String port) {

                    }
                });
            }
        });

        toggleUpdate.setOnToggleListener(new ToggleView.OnToggleListener() {
            @Override
            public void onclickToggle(boolean isOpen) {
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.APP_ISREFRESH, isOpen);
            }
        });
        toggleUpload.setOnToggleListener(new ToggleView.OnToggleListener() {
            @Override
            public void onclickToggle(boolean isOpen) {
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.AUTOUPLOAD
                        , isOpen);
                GlobalConstant.isUploadStop = !isOpen;
                EventBusUseEvent eventBusUseEvent
                        = new EventBusUseEvent(getRecString(R.string.auto_upload_close));
                eventBusUseEvent.setCanAutoUpload(isOpen);
                EventBus.getDefault().post(eventBusUseEvent);
            }
        });

        rlParamConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalConstant.isParamOperate = true;
                final ParamPopupWindow paramPopupWindow = new ParamPopupWindow(getActivity());
                paramPopupWindow.show();
                paramPopupWindow.addCallBackListener(new ParamPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String checkedParamTxt) {
                        tvParam.setText(checkedParamTxt);
                        SpUtils.saveToSp(context, GlobalConstant.PARAM_CONFIGS
                                , GlobalConstant.PARAM_KEY
                                , paramPopupWindow.getCheckedParamValue());
                    }

                    @Override
                    public void onCommitParam(List<Integer> params) {

                    }
                });
            }
        });

        rlDevicesConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DevicesPopupWindow paramPopupWindow = new DevicesPopupWindow(getActivity());
                paramPopupWindow.show();
                paramPopupWindow.addCallBackListener(new DevicesPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String checkedParamTxt) {
                        tvDevices.setText(checkedParamTxt);
                        int value = paramPopupWindow.getCheckedParamValue();
                        SpUtils.saveToSp(context, GlobalConstant.SYS_CONFIG
                                , GlobalConstant.DEVICE_CONFIG_TAG, value);
                        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
                    }
                });
            }
        });

        rlServerNetAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextPopWindow pop = new EditTextPopWindow(getActivity(), serverNetAddr);
                pop.setTitleName(UiUitls.getString(R.string.server_title_default));
                pop.setSubtitle(UiUitls.getString(R.string.server_address));
                pop.show();
                pop.addCallBackListener(new EditTextPopWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String content) {
                        serverNetAddr = content;
                        tvServerNetAddr.setText(content);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.SERVER_ADDRESS, content);
                    }
                });
            }
        });

        rlCloudIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpPopupWindow pop = new IpPopupWindow(getActivity(), IpPopupWindow.IP_TYPE_CLOUD);
                pop.show();
                pop.addCallBackListener(new IpPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String ip, String port) {
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.CLOUD_IP, ip);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.CLOUD_IP_PORT, port);
                        tvCloudIp.setText(ip + UiUitls.getString(R.string.colon) + port);
                    }

                    @Override
                    public void onCancel(String ip, String port) {

                    }
                });
            }
        });

        rlCloudNetAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPopWindow = new EditTextPopWindow(getActivity(), cloudNetAddress);
                editTextPopWindow.setTitleName(UiUitls.getString(R.string.cloud_address_title));
                editTextPopWindow.setSubtitle(UiUitls.getString(R.string.cloud_address));
                editTextPopWindow.show();
                editTextPopWindow.addCallBackListener(new EditTextPopWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String content) {
                        cloudNetAddress = content;
                        tvCloudNetAddr.setText(content);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.CLOUD_ADDRESS, content);
                    }
                });
            }
        });

        rlUploadDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPopWindow = new EditTextPopWindow(getActivity(), uploadDetail);
                editTextPopWindow.setTitleName(UiUitls.getString(R.string.upload_detail_title));
                editTextPopWindow.setSubtitle(UiUitls.getString(R.string.detail_address));
                editTextPopWindow.show();
                editTextPopWindow.addCallBackListener(new EditTextPopWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String content) {
                        uploadDetail = content;
                        tvUploadDetail.setText(content);
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.DETAIL_ADDRESS, content);
                    }
                });
            }
        });

        rlUploadDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistrictPopupWindow pop = new DistrictPopupWindow(getActivity());
                pop.show();
                pop.addCallBackListener(new DistrictPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String districtStr) {
                        tvUploadDistrict.setText(districtStr);
                    }
                });
            }
        });

        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllData();
            }
        });

        btnStartLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.showProgress(context, UiUitls.getString(R.string.loading));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        importLogo();
                    }
                }).start();
            }
        });
        toggleCardInput.setOnToggleListener(new ToggleView.OnToggleListener() {
            @Override
            public void onclickToggle(boolean isOpen) {
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CARD_INPUT, isOpen);
            }
        });
        rlNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化组件 回显
     */
    private void initView() {
        //头像
//        String idcard = SpUtils.getSp(getActivity(), GlobalConstant.APP_CONFIG
//                , GlobalConstant.ID_CARD, "");
//        List<PatientBean> patients = DBDataUtil.getPatientByIdCard(idcard);
//        if (patients.size() > 0) {
//            PatientBean patient = patients.get(0);
//            bmp = BmpUtil.getHeadBitmap(patient.getIdCard());
//            if (bmp != null) {
//                ivHead.setImageBitmap(bmp);
//            }
//            tvName.setText(GlobalConstant.USERNAME);
//        }
        tvName.setText(GlobalConstant.EMP_NAME);

        //toggle控件  自动上传/自动更新
        boolean isOpen = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.APP_ISREFRESH, true);
        toggleUpdate.setToggle(isOpen);
        boolean isOpenAutoUpload = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.AUTOUPLOAD, true);
        toggleUpload.setToggle(isOpenAutoUpload);

        //软件更新地址
        String updateIp = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.REFRESH_IP
                , GlobalConstant.REFRESH_ADRESS);
        String updatePort = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.REFRESH_IP_PORT, GlobalConstant.REFRESH_ADRESS_PORT);
        tvUpdateAddr.setText(updateIp + UiUitls.getString(R.string.colon) + updatePort);

        //服务器IP地址
        String serverIp = SpUtils.getSp(context, GlobalConstant.APP_CONFIG, IP, "");
        if (serverIp.equals("")) {
            SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG, IP, GlobalConstant.IP_DEFAULT);
            serverIp = GlobalConstant.IP_DEFAULT;
        }
        String serverPort = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
        tvServerIp.setText(serverIp + UiUitls.getString(R.string.colon) + serverPort);
        initParamConfig();
        boolean cardInput = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG, GlobalConstant.CARD_INPUT, true);
        toggleCardInput.setToggle(cardInput);
        // 管理员账户
        if (GlobalConstant.TEST_PASSWORD.equals(UiUitls.getString(R.string.test_password))) {

            rlServerNetAddr.setVisibility(View.VISIBLE);
            rlCloudIp.setVisibility(View.VISIBLE);
            rlCloudNetAddr.setVisibility(View.VISIBLE);
            rlUploadDistrict.setVisibility(View.VISIBLE);
            rlUploadDetail.setVisibility(View.VISIBLE);
            rlDevicesConfig.setVisibility(View.VISIBLE);
            llBottomBtn.setVisibility(View.VISIBLE);
            rlCardInput.setVisibility(View.VISIBLE);
            //服务器地址
            serverNetAddr = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.SERVER_ADDRESS, GlobalConstant.SERVER_FIX_ADDRESS);
            tvServerNetAddr.setText(serverNetAddr);

            //云平台ip地址 及地址
            final String cloudip = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.CLOUD_IP, GlobalConstant.CIP);
            String port = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.CLOUD_IP_PORT, GlobalConstant.CIP_PORT);
            tvCloudIp.setText(cloudip + UiUitls.getString(R.string.colon) + port);

            cloudNetAddress = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.CLOUD_ADDRESS, GlobalConstant.CLOUD_FIX_ADDRESS);
            tvCloudNetAddr.setText(cloudNetAddress);

            //上传详细地址
            uploadDetail = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                    , GlobalConstant.DETAIL_ADDRESS, "");
            tvUploadDetail.setText(uploadDetail);
            initDeviceConfig();
            initDistrictView();
        }
    }

    /**
     * 回显参数配置项文本
     */
    private void initParamConfig() {
        int paramValue = SpUtils.getSpInt(context, GlobalConstant.PARAM_CONFIGS
                , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);

        String[] paramArr = context.getResources().getStringArray(R.array.param_config);
        StringBuilder builder = new StringBuilder();
        boolean haveNextLine = false;
        for (int i = 0; i < paramArr.length; i++) {
            if ((paramValue & (0x01 << i)) != 0) {
                builder.append(paramArr[i]).append(CHAR_SEMICOLON);
            }
            if (builder.length() > LINE_MAX_SIZE && !haveNextLine) {
                builder.append("\n");
                haveNextLine = true;
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        tvParam.setText(builder.toString());
    }

    /**
     * 回显参数配置项文本
     */
    private void initDeviceConfig() {
        int paramValue = SpUtils.getSpInt(context, GlobalConstant.SYS_CONFIG
                , GlobalConstant.DEVICE_CONFIG_TAG, GlobalConstant.DEVICE_CONFIG);
        String[] paramArr = context.getResources().getStringArray(R.array.device_config);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < paramArr.length; i++) {
            if ((paramValue & (0x01 << i)) != 0) {
                builder.append(paramArr[i]).append(CHAR_SEMICOLON);
            }
            if (i == GlobalNumber.SEVENTEEN_NUMBER) {
                //指定串口
                if ((paramValue & (0x01 << GlobalNumber.TWENTY_NUMBER)) != 0) {
                    builder.append(paramArr[i]).append(CHAR_SEMICOLON);
                }

            }
            if (i == GlobalNumber.EIGHTTEEN_NUMBER) {
                if ((paramValue & (0x01 << GlobalNumber.TWENTY_ONE_NUMBER)) != 0) {
                    builder.append(paramArr[i]).append(CHAR_SEMICOLON);
                }
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        tvDevices.setText(builder.toString());
    }

    /**
     * 初始化数据
     */
    private void initDistrictView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadAddressData();
                final String districtTxt = getDistrictTxt();
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tvUploadDistrict != null) {
                            tvUploadDistrict.setText(districtTxt);
                        }
                    }
                });
            }
        }).start();
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
     * 获取地区回显文本
     * @return 地区回显文本
     */

    private String getDistrictTxt() {
        int provinceSelected = SpUtils.getSpInt(context, GlobalConstant.SYS_CONFIG
                , GlobalConstant.province, 0);
        int citySelected = SpUtils.getSpInt(context, GlobalConstant.SYS_CONFIG
                , GlobalConstant.city, 0);
        int districtSelected = SpUtils.getSpInt(context, GlobalConstant.SYS_CONFIG
                , GlobalConstant.district, 0);
        AddrProvinceBean provinceBean = addressData.get(provinceSelected);
        AddrCityBean cityBean = provinceBean.getAearList().get(citySelected);
        AddrDistrictBean districtBean = cityBean.getAearList().get(districtSelected);

        String province = provinceBean.getAreaName();
        String city = cityBean.getAreaName();
        String district = districtBean.getAreaName();
        return province + city + district;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (bmp != null) {
            bmp.recycle();
        }
    }

    /**
     * 清除所有数据
     */
    private void clearAllData() {
        final ProgressDialog progress = new ProgressDialog(context, "");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        //清除登录信息
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , SpUtils.USERNAME, "");
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , SpUtils.PASSWORD, "");
                        //清除当前用户
                        SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                                , GlobalConstant.ID_CARD, "");
                    }
                });
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.APP_ISREFRESH, true);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.AUTOUPLOAD, true);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.REFRESH_IP, GlobalConstant.REFRESH_ADRESS);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.REFRESH_IP_PORT, GlobalConstant.REFRESH_ADRESS_PORT);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.SERVICE_IP, GlobalConstant.IP_DEFAULT);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CLOUD_IP, GlobalConstant.CIP);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CLOUD_IP_PORT, GlobalConstant.CIP_PORT);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CLOUD_ADDRESS, GlobalConstant.CLOUD_FIX_ADDRESS);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.SERVER_ADDRESS, GlobalConstant.SERVER_FIX_ADDRESS);
                SpUtils.saveToSp(context, GlobalConstant.SYS_CONFIG, GlobalConstant.province, 0);
                SpUtils.saveToSp(context, GlobalConstant.SYS_CONFIG, GlobalConstant.city, 0);
                SpUtils.saveToSp(context, GlobalConstant.SYS_CONFIG, GlobalConstant.district, 0);
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.DETAIL_ADDRESS, "");
                SpUtils.saveToSp(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.ORGANIZATION_NAME, "");
                SpUtils.saveToSp(context, GlobalConstant.PARAM_CONFIGS
                        , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
                SpUtils.saveToSp(context, GlobalConstant.SYS_CONFIG
                        , GlobalConstant.DEVICE_CONFIG_TAG, GlobalConstant.DEVICE_CONFIG);
                //清除用户登录数据
                List<UserBean> userList = DBDataUtil.getUserDao().loadAll();
                DBDataUtil.getUserDao().deleteInTx(userList);
                //清除测量数据
                List<PatientBean> patientList = DBDataUtil.getPatientDao().loadAll();
                int total = patientList.size();
                int deleted = 0;
                if (patientList.size() > 0) {
                    for (PatientBean patient : patientList) {
                        List<MeasureDataBean> measures
                                = DBDataUtil.getMeasures(patient.getIdCard());
                        DBDataUtil.getPatientDao().delete(patient);
                        DBDataUtil.getMeasureDao().deleteInTx(measures);
                        deleted++;
                        final float finalTotal = total;
                        final float finalDeleted = deleted;
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                int i = (int) (finalDeleted / finalTotal * hundredValue);
                                progress.setProgress(i);
                                progress.setText((int) finalDeleted + "/" + (int) finalTotal);
                            }
                        });
                    }
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        initView();
                        UiUitls.toast(context, getRecString(R.string.data_has_cleared));
                    }
                });
            }
        }).start();
    }

    /**
     * 导入开机动画
     */
    private void importLogo() {
        File logPath = null;
        File musicPath = null;
        File logfile = new File("/storage/sdcard0/Konsung/bootanimation.zip");
        if (logfile.exists()) {
            logPath = logfile;
            File musicfile = new File("/storage/sdcard0/Konsung/bootaudio.mp3");
            musicPath = musicfile;
        }

        File logSDfile = new File("/storage/sdcard1/Konsung/bootanimation.zip");
        if (logSDfile.exists()) {
            if (null != logPath) {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, getRecString(R.string.more_found_log));
                        UiUitls.hideProgress();
                    }
                });
                return;
            }
            logPath = logSDfile;
            File musicSDfile = new File("/storage/sdcard1/Konsung/bootaudio.mp3");
            musicPath = musicSDfile;
        }
        File file = new File("/storage/usbotg");
        File[] files = file.listFiles();
        if (null != files) {
            for (File e : files) {
                File log = new File(e, "Konsung/bootanimation.zip");
                if (log.exists()) {
                    if (null != logPath) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                UiUitls.toast(context, getRecString(R.string.more_found_log));
                                UiUitls.hideProgress();
                            }
                        });

                        return;
                    }
                    logPath = log;
                    File musicSDfile = new File(e, "Konsung/bootaudio.mp3");
                    musicPath = musicSDfile;
                }
            }
        }

        if (logPath == null) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    UiUitls.toast(context, getRecString(R.string.no_found_log));
                    UiUitls.hideProgress();
                }
            });
            return;
        } else {
            UiUitls.copyFile(logPath.getPath(), GlobalConstant
                    .BOOTANIMATIONFILE, null);
            int i = accessPermissions(GlobalConstant.BOOTANIMATIONFILE);
            if (i != 0) {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, getRecString(R.string.copy_fail));
                    }
                });
                return;
            }
            if (musicPath != null && musicPath.exists()) {
                UiUitls.copyFile(musicPath.getPath(), GlobalConstant
                        .BOOTAUDIOFILE, null);
                accessPermissions(GlobalConstant.BOOTAUDIOFILE);
            } else {
                File file1 = new File(GlobalConstant.BOOTAUDIOFILE);
                if (file1.exists()) {
                    boolean delete = file1.delete();
                }
            }
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.hideProgress();
                UiUitls.toast(context, getRecString(R.string.chmod_succeed));
            }
        });
    }

    /**
     * 使系统权限获取该文件的方法
     * @param path 文件路径
     * @return 权限值
     */
    private int accessPermissions(String path) {
        Process p;
        int status = -1;
        try {
            p = Runtime.getRuntime().exec("chmod 666 " + path);
            status = p.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (status == 0) {

        } else {

        }
        return 0;
    }
}
