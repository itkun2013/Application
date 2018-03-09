package com.konsung.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsung.R;
import com.konsung.bean.EcgDiagnoseBean;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Request.QueryEcgDiagnosesRequest;
import com.konsung.bean.Response.EcgQueryResponse;
import com.konsung.defineview.EcgReportPopupWindow;
import com.konsung.defineview.ProgressDialog;
import com.konsung.upload.UploadData;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.HeartImage;
import com.konsung.util.JsonUtils;
import com.konsung.util.KParamType;
import com.konsung.util.NumUtil;
import com.konsung.util.OverProofUtil;
import com.konsung.util.StringUtil;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;
import com.konsung.util.global.BeneParamValue;
import com.konsung.util.global.BloodMem;
import com.konsung.util.global.BmiParam;
import com.konsung.util.global.BroadcastAction;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.IrTemp;
import com.konsung.util.global.Nibp;
import com.konsung.util.global.Sex;
import com.konsung.util.global.Spo;
import com.konsung.util.global.SugarBloodParam;
import com.konsung.util.global.Urine;
import com.konsung.util.global.WbcParamValue;
import com.konusng.adapter.MeasureDataByPatientListAdapter;
import com.loopj.android.http.RequestParams;
import com.lvrenyang.io.Page;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;
import com.lvrenyang.io.base.IOCallBack;
import com.pantum.mobileprint.LoadJNI;
import com.pantum.mobileprint.PantumPrint;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 根据病人姓名获取测量数据,波形数据现在只有5导联的
 * @author ouyangfan
 * @version 0.0.1
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class GetMeasureDataByPatientFragment extends BaseInsertFragment
        implements AdapterView.OnItemClickListener, IOCallBack {

    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_sex)
    TextView tvSex;
    @InjectView(R.id.tv_measure_time)
    TextView tvMeasureTime;
    @InjectView(R.id.tv_age)
    TextView tvAge;
    @InjectView(R.id.tv_doctor)
    TextView tvDoctor;
    @InjectView(R.id.tv_hr)
    TextView tvHr;
    @InjectView(R.id.iv_hr_hint)
    ImageView ivHrHint;
    @InjectView(R.id.tv_ur_leu)
    TextView tvUrLeu;
    @InjectView(R.id.iv_leu_hint)
    ImageView ivLeuHint;
    @InjectView(R.id.tv_ur_alb)
    TextView tvUrAlb;
    @InjectView(R.id.iv_alb_hint)
    ImageView ivAlbHint;
    @InjectView(R.id.tv_ur_bil)
    TextView tvUrBil;
    @InjectView(R.id.iv_bil_hint)
    ImageView ivBilHint;
    @InjectView(R.id.tv_ur_asc)
    TextView tvUrAsc;
    @InjectView(R.id.iv_asc_hint)
    ImageView ivAscHint;
    @InjectView(R.id.tv_ur_ket)
    TextView tvUrKet;
    @InjectView(R.id.iv_ket_hint)
    ImageView ivKetHint;
    @InjectView(R.id.tv_ur_cre)
    TextView tvUrCre;
    @InjectView(R.id.iv_cre_hint)
    ImageView ivCreHint;
    @InjectView(R.id.tv_ur_bld)
    TextView tvUrBld;
    @InjectView(R.id.iv_bld_hint)
    ImageView ivBldHint;
    @InjectView(R.id.tv_ur_ubg)
    TextView tvUrUbg;
    @InjectView(R.id.iv_ubg_hint)
    ImageView ivUbgHint;
    @InjectView(R.id.tv_ur_pro)
    TextView tvUrPro;
    @InjectView(R.id.iv_pro_hint)
    ImageView ivProHint;
    @InjectView(R.id.tv_ur_glu)
    TextView tvUrGlu;
    @InjectView(R.id.iv_glu_hint)
    ImageView ivGluHint;
    @InjectView(R.id.tv_ur_sg)
    TextView tvUrSg;
    @InjectView(R.id.iv_sg_hint)
    ImageView ivSgHint;
    @InjectView(R.id.tv_ur_nit)
    TextView tvUrNit;
    @InjectView(R.id.iv_nit_hint)
    ImageView ivNitHint;
    @InjectView(R.id.tv_ur_ph)
    TextView tvUrPh;
    @InjectView(R.id.iv_ph_hint)
    ImageView ivPhHint;
    @InjectView(R.id.tv_ur_ca)
    TextView tvUrCa;
    @InjectView(R.id.iv_ca_hint)
    ImageView ivCaHint;

    @InjectView(R.id.tv_bopr)
    TextView tvBopr;

    @InjectView(R.id.tv_shrink_pressure)
    TextView tvShrinkPressure;

    @InjectView(R.id.tv_spread)
    TextView tvSpreadPressure;
    @InjectView(R.id.iv_spread_hint)
    ImageView ivSpreadHint;
    @InjectView(R.id.tv_average_pressure)
    TextView tvAveragePressure;
    @InjectView(R.id.iv_average_hint)
    ImageView ivAverageHint;
    @InjectView(R.id.tv_bo)
    TextView tvBo;
    @InjectView(R.id.iv_bo_hint)
    ImageView ivBoHint;
    @InjectView(R.id.iv_temperature_hint)
    ImageView ivTemperatureHint;
    @InjectView(R.id.tv_bs)
    TextView tvBs;
    @InjectView(R.id.iv_bs_hint)
    ImageView ivBsHint;
    @InjectView(R.id.iv_bopr_hint)
    ImageView ivBoprHint;
    @InjectView(R.id.iv_shrink_hint)
    ImageView ivShrinkHint;
    @InjectView(R.id.tv_temperature)
    TextView tvTemperature;
    @InjectView(R.id.tv_district)
    TextView tvDistrict;
    @InjectView(R.id.bt_check_ecg_image)
    Button btCheckEcgImage;
    @InjectView(R.id.bt_print)
    Button btPrint;
    @InjectView(R.id.tv_pr)
    TextView tvPr;
    @InjectView(R.id.iv_pr_hint)
    ImageView ivPrHint;
    @InjectView(R.id.tv_glu_style)
    TextView tvGluStyle;
    //血红蛋白两项
    @InjectView(R.id.tv_hemoglobin)
    TextView tvBloodHGBTrend;
    @InjectView(R.id.tv_red_blood)
    TextView tvBloodHCTTrend;
    @InjectView(R.id.iv_hgb_hint)
    ImageView ivHgbHint;
    @InjectView(R.id.iv_htc_hint)
    ImageView ivHtcHint;
    //血脂四项
    @InjectView(R.id.tv_ch_value)
    TextView tvChValue;
    @InjectView(R.id.tv_trig_value)
    TextView tvTrigValuee;
    @InjectView(R.id.tv_hdl_value)
    TextView tvHdlValue;
    @InjectView(R.id.tv_ldl_value)
    TextView tvLdlValue;
    @InjectView(R.id.iv_ch_notice)
    ImageView ivChValue;
    @InjectView(R.id.iv_trig_notice)
    ImageView ivTrigValuee;
    @InjectView(R.id.iv_hdl_notice)
    ImageView ivHdlValue;
    @InjectView(R.id.iv_ldl_notice)
    ImageView ivLdlValue;
    @InjectView(R.id.tv_glu_refenrence)
    TextView tvGluReference;

    @InjectView(R.id.hgb_reference)
    TextView tvHgbReference;
    @InjectView(R.id.htc_reference)
    TextView tvHtcReference;

    @InjectView(R.id.tv_td_style)
    TextView tvChoStyle;
    @InjectView(R.id.td_reference)
    TextView tvChoRef;
    @InjectView(R.id.tv_td)
    TextView tvChol;
    @InjectView(R.id.iv_td_hint)
    ImageView ivChol;
    @InjectView(R.id.tv_ns_style)
    TextView tvNsStyle;
    @InjectView(R.id.tv_ns)
    TextView tvNs;
    @InjectView(R.id.iv_ns_hint)
    ImageView ivNs;

    @InjectView(R.id.cr_layout)
    LinearLayout crLayout;
    @InjectView(R.id.ca_layout)
    LinearLayout caLayout;
    @InjectView(R.id.ma_layout)
    LinearLayout maLayout;

    @InjectView(R.id.ns_reference)
    TextView nsTvReference;

    //糖化血红蛋白
    @InjectView(R.id.tv_ngsp_value)
    TextView tvNgsp;
    @InjectView(R.id.iv_ngsp_notice)
    ImageView ivNgsp;
    @InjectView(R.id.tv_ifcc_value)
    TextView tvIfcc;
    @InjectView(R.id.iv_ifcc_notice)
    ImageView ivIfcc;
    @InjectView(R.id.tv_eag_value)
    TextView tvEag;
    @InjectView(R.id.iv_eag_notice)
    ImageView ivEag;

    //BMI
    @InjectView(R.id.tv_bmi_value)
    TextView tvBmi;
    @InjectView(R.id.iv_bmi_notice)
    ImageView ivBmi;
    @InjectView(R.id.tv_bmi_height_value)
    TextView tvHeight;
    @InjectView(R.id.tv_bmi_weight_value)
    TextView tvWeight;

    //白细胞
    @InjectView(R.id.tv_wbc_value)
    TextView tvWbcValue;
    @InjectView(R.id.iv_wbc_notice)
    ImageView ivWbc;

    //根据参数配置动态显示各测量项
    @InjectView(R.id.ll_hr)
    LinearLayout llHr;
    @InjectView(R.id.ll_urt)
    LinearLayout llUrt;
    @InjectView(R.id.ll_spo2)
    LinearLayout llSpo2;
    @InjectView(R.id.ll_nibp)
    LinearLayout llNibp;
    @InjectView(R.id.ll_temp)
    LinearLayout llTemp;
    @InjectView(R.id.ll_glu)
    LinearLayout llGlu;
    @InjectView(R.id.ll_hgb)
    LinearLayout llHgb;
    @InjectView(R.id.ll_lipids)
    LinearLayout llLipids;
    @InjectView(R.id.ll_sugar_hgb)
    LinearLayout llSugarHgb;
    @InjectView(R.id.ll_bmi)
    LinearLayout llBmi;
    @InjectView(R.id.ll_wbc)
    LinearLayout llWbc;

    private MeasureDataBean dataMeasure;

    // 存储测量数据值
    private List<MeasureDataBean> mMeasureDataBeanList;

    // 测量数据适配器
    private MeasureDataByPatientListAdapter mDataAdapter;

    //病人信息
    private PatientBean patient;
    //病人身份证
    private String idcard;
    //会员卡号
    private String memberShipCard;
    //当前测量对象id
    private Long id;
    private MeasureDataBean dataBean;
    //是否打印心电图
    private boolean isPrintHeart = false;
    LoadJNI loadjni;
    //每次加载多少行数据
    private long viewAmount = 5;
    int containerId;
    //记录当前有多少条目录显示
    private int astItem;
    //定义2个位图对象，在fragment创建的时候加载
    Bitmap bitmapPort;
    Bitmap bitmapPort2;
    Bitmap bitmapEcg;
    //性别 默认1 男
    private int sex = 1;
    private String measureTime;
    //血红蛋白和红细胞积压值的上下限
    private float hgbMax;
    private float hgbMin;
    private float htcMax;
    private float htcMin;
    //尿酸的上下限
    private float nsMax;
    private float nsMin;
    private PatientBean patientBean;
    private String patientName;
    private View report;
    private String advice;
    private Bitmap personPic;
    private String ecghr;
    private String ecgbr;
    private String ecgDiagnoseResult;
    private String spo2Trend;
    private String spo2Pr;
    private String nibpSys; //收缩压
    private String nibpDia; //舒张压
    private String nibpMap; //平均压
    private String nibpPr;
    private String uri_sg;
    private String uri_leu;
    private String uri_nit;
    private String uri_ubg;
    private String uri_ph;
    private String uri_bld;
    private String uri_glu;
    private String uri_asc;
    private String uri_ket;
    private String uri_bil;
    private String uri_pro;
    private String uri_alb;
    private String uri_cre;
    private String uri_ca;
    private String blood_glu;
    private String nsValue;
    private String cholValue;
    private String blood_wbc;
    private String blood_hgb;
    private String blood_hct;
    private String uric_acid;
    private String cholesterol;
    private String temp;
    private String ngsp; //糖化血红蛋白HbA1c(NGSP)
    private String ifcc; //糖化血红蛋白HbA1c(IFCC)
    private String eag; //糖化血红蛋白HbA1c(eAG)
    private String height; //居民身高
    private String weight; //居民体重
    private String bmi; //BMI
    private boolean isBloodOx; //是否打印血氧
    private boolean isBloodPress; //是否打印血压
    private boolean isTemp; //是否打印体温
    private boolean isBloodThree; //是否打印血液三项
    private boolean isUr11; //是否打印尿常规11项
    private boolean isUr14; //是否打印尿常规14项
    private boolean isHgb; //是否打印血红蛋白
    private boolean isBloodFat; //是否打印血脂
    private boolean isHbal; //是否打印糖化血红蛋白
    private boolean isBmi; //是否打印bmi
    private boolean isWbac; //是否打印白细胞
    private boolean isEcg; //心电
    private int bitmapWidthwidth;
    private int measure_result_x;
    private int measure_result_y;
    private int column_interval = 450;
    private int row_interval = 60;
    public static final String TITLE_COLOR = "#c5ffcc";
    //测量方式：0:为空腹血糖，1为随机血糖，2为餐后血糖
    private final String GLU_BEFORE_EAT = "0";
    private final String GLU_AFTER_EAT = "1";

    //0 餐前 1 餐后
    private String bloodGluFlag = "0";
    private GlobalConstant.BtnFlag gluStyle;
    private float bloodGluMax;
    private float bloodGluMin;
    private EcgReportPopupWindow ecgReport;
    private ProgressDialog progressDialog;

    private static final int END_Y = 3288; //内容区底部横线Y坐标
    private static final int HR_HEIGHT = 220; //心率打印占用高度
    private static final int SPO2_HEIGHT = 300; //血氧打印占用高度
    private static final int NIBP_HEIGHT = 300; //血压打印占用高度
    private static final int TEMP_HEIGHT = 240; //体温打印占用高度
    private static final int GLU_HEIGHT = 360; //血液三项打印占用高度
    private static final int URT_11_HEIGHT = 840; //尿常规11项打印占用高度
    private static final int URT_14_HEIGHT = 1020; //尿常规14项打印占用高度
    private static final int HGB_HEIGHT = 300; //血红蛋白打印占用高度
    private static final int LIPIDS_HEIGHT = 420; //血脂打印占用高度
    private static final int SUGAR_HGB_HEIGHT = 360; //糖化血红蛋白打印占用高度
    private static final int BMI_HEIGHT = 360; //体质BMI打印占用高度
    private static final int WBC_HEIGHT = 240; //白细胞打印占用高度
    private static final int RESULT_ADVICE_HEIGHT = 350; //结论及建议打印占用高度
    private static final int MID_OF_RESULT = 730; //检查结果中点X坐标
    private static final int MID_OF_UNIT = 1132; //单位中点X坐标
    private static final int MID_OF_REFERENCE = 1606; //参考范围中点X坐标
    private static final int ALARM_X = 2020; //提示是否不在正常范围起点X坐标
    private int pageOneY = 520; //第一页内容区起始Y坐标
    private int pageTwoY = 520; //第二页内容区起始Y坐标
    private Map<Integer, Boolean> printList = new HashMap<>(); //记录需要打印的测量项，true代表打印在第一页，false代表第二页
    private String nsReference; // 尿酸参考范围
    private String glu = ""; //血糖测量方式：0:为空腹血糖，1为餐后血糖
    private String patientAge; //居民年龄
    private boolean overOnePage = false; //是否超过一页
    private String reportTime; //报告打印时间
    private boolean remoteEcgResolved = false; //远程心电是否返回了处理结果
    private static final String REMOTE_ECG_RESOLVED = "1"; //远程心电已处理状态
    private UsbManager usbManager;
    private UsbDevice usbDevice; //打印机设备
    ExecutorService es = Executors.newScheduledThreadPool(30); //执行线程
    private Page mPage = new Page(); //纸张
    USBPrinting usb = new USBPrinting();
    private Pos pos = new Pos();
    private boolean ecgPrint = false;
    private Bitmap printBmp;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1) {
                case 1://查看测量信息
                    //                    beanMsg.setMeasureDataBean(
                    // (MeasureDataBean) msg.obj);
                    //                    BaseInsertFragment fragment = new
                    // HealthCheckFragment_1();
                    //                    switchToFragment(R.id.fragment,
                    // fragment, "mHealthCheckFragment_1", true);
                    getUrl("check", msg.obj);
                    break;
                case 2://查看波形
                    beanMsg.setMeasureDataBean((MeasureDataBean) msg.obj);
                    BaseFragment fragment1 = new GetEcgWaveRecordFor12();
                    switchToFragment(R.id.fragment, fragment1,
                            "mGetEcgWaveRecordfor12", true);
                    break;
                case 7:
                    Bundle picData = msg.getData();
                    Bitmap mBitmap = (Bitmap) picData.getParcelable(GlobalConstant.PARCE1);
                    int nWidth = picData.getInt(GlobalConstant.INTPARA1);
                    int nMode = picData.getInt(GlobalConstant.INTPARA2);

                    byte picBuf[] = new byte[100];
                    boolean picResult = false;
                    if (USBPrinting.class.getName().equals(
                            pos.GetIO().getClass().getName())) {
                        picResult = usb.IsOpened();
                    } else {
                        picResult = pos.POS_QueryStatus(picBuf, 1000, 1);
                    }
                    if (picResult) {
                        pos.POS_PrintPicture(mBitmap, nWidth, nMode, 1);
                    } else {
                    }
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.hideProgress();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };
    private String bloodHgbTrend;
    private String bloodHctTrend;
    private String lipoidemiatc;
    private String lipoidemiatg;
    private String lipoidemialdl;
    private String lipoidemiahdl;
    private String wbcValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMeasureDataBeanList = new ArrayList<>();
        loadjni = new LoadJNI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_health_report, container, false);
        ButterKnife.inject(this, view);
        GlobalConstant.isInHealthReportFragment = true;
        containerId = container.getId();
        Bundle bundle = getArguments();
        String patientName = bundle.getString(GlobalConstant.PATIENT_NAME);
        tvName.setText(patientName);
        idcard = bundle.getString(GlobalConstant.ID_CARD);
        memberShipCard = bundle.getString(GlobalConstant.MEMBER_CARD);
        btCheckEcgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((dataMeasure != null) && (dataMeasure.getTrendValue(KParamType.ECG_HR) !=
                        GlobalConstant.INVALID_DATA)) {
                    View view = getActivity().getWindow().getDecorView();
                    int width = view.getWidth();
                    Rect outRect = new Rect();
                    view.getWindowVisibleDisplayFrame(outRect);
                    int height = outRect.height();
                    ecgReport = new EcgReportPopupWindow(getActivity(), patientBean, dataMeasure,
                            width, height, true, false);
                    ecgReport.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                } else {
                    UiUitls.toast(getActivity()
                            , UiUitls.getString(R.string.x_ecg_no_measure));
                }
            }
        });

        btPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPantumPrinter()) {
                    //奔图打印机
                    requestPantumPermission();
                } else if (checkQuickPrintConnected()) {
                    //便携式打印机，初始化默认会有厂家得名字，需要与厂家维护的jar包有关
                    requestQuickPrinterPermission();
                } else {
                    //打印机不存在
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UiUitls.getContent(),
                                    UiUitls.getString(R.string.dispose_print_usb),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        mPage.Set(usb);
        //usb打印回调
        usb.SetCallBack(this);
        getPatientDetail();
        initData();
        initEvent();
        initView();
        initReferenceValue();
        setPrintText();
        initReceiver(getActivity());
        return view;
    }

    /**
     * 检测是否连接奔图打印机
     * @return 是否连接
     */
    private boolean checkPanTumConnection() {
        //获取检查打印机端口连接的返回码
        int checkUSBCode = printByPantum1();
        if (!checkUSB(checkUSBCode)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 使用奔图打印机打印
     */
    private void printWithPantum() {
        if (GlobalConstant.IS_PRINTING) {//判断用户是否点击了打印
            Toast.makeText(getActivity(), UiUitls.getString(R.string.dispose_print_data),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //判断是否需要打印心电图
        isPrintHeart = dataMeasure.getTrendValue(KParamType.ECG_HR) !=
                GlobalConstant.INVALID_DATA;
        if (isPrintHeart) {
            requestRemoteEcgStatus();
        }
        progressDialog = new ProgressDialog(getActivity(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                        progressDialog.setTitle(UiUitls.getString(R.string.dispose_print)
                                + UiUitls.getString(R.string.medical_examination_report));
                    }
                });
                GlobalConstant.IS_PRINTING = true;
                if (GlobalConstant.IS_PRINTING_INTO) {
                    //录入位图
                    drawOnBitmap();
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setProgress(10);//打印进度数据
                        }
                    });
                    File file1 = new File(GlobalConstant.HEALTH_REPORT_PATH,
                            GlobalConstant.HEALTH_REPORT_NAME);

                    if (file1.exists()) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress(20);//打印进度数据
                            }
                        });
                        printPantum(file1.getAbsolutePath());
                        file1.delete();
                    }
                    File file2 = new File(GlobalConstant.HEALTH_REPORT_PATH,
                            GlobalConstant.HEALTH_REPORT_TWO);

                    if (file2.exists()) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress(40);//打印进度数据
                            }
                        });
                        printPantum(file2.getAbsolutePath());
                        file2.delete();
                    }
                }
                if (GlobalConstant.IS_PRINTING_INTO) {
                    if (isPrintHeart) {
                        //录入一个位图
                        InputStream resourceAsStream2 = UiUitls.getContent().getClass()
                                .getClassLoader().getResourceAsStream("assets/ecg.bmp");
                        if (bitmapEcg == null) {
                            bitmapEcg = BitmapFactory.decodeStream(resourceAsStream2).
                                    copy(Bitmap.Config.RGB_565, true);
                            drawEcgImage();
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setTitle(UiUitls.getString(R.string
                                            .dispose_print) + UiUitls.getString(
                                            R.string.health_ecg));
                                    progressDialog.setProgress(60);//打印进度数据
                                }
                            });
                        }
                        File file3 = new File(GlobalConstant.HEALTH_REPORT_PATH,
                                GlobalConstant.HEALTH_ECG_NAME);
                        if (file3.exists()) {
                            printPantum(file3.getAbsolutePath());
                            file3.delete();
                        }
                    }
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setProgress(100);//打印进度数据
                        progressDialog.dismiss();
                    }
                });
                printPantumFinsh();
            }
        }).start();
    }

    /**
     * 打印线程完成的方法
     */
    public void printPantumFinsh() {
        GlobalConstant.IS_PRINTING = false;
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UiUitls.getContent(),
                        UiUitls.getString(
                                R.string.dispose_print_finish),
                        Toast.LENGTH_SHORT).show();
                //回收bitmap
                bitmapEcg = null;
                bitmapPort = null;
                bitmapPort2 = null;
            }
        });
    }

    /**
     * 奔图打印方法
     * @param path 图片路径
     */
    public void printPantum(String path) {
        PantumPrint pantumPrint = new PantumPrint();
        pantumPrint.print(UiUitls.getContent(), path);
    }

    /**
     * 检测奔图打印机是否连接上
     * @param code 状态码
     */
    private boolean checkUSB(int code) {
        GlobalConstant.IS_PRINTING_INTO = false;
        if (code == 0) {
            //连接打印机成功
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UiUitls.getContent(),
                            UiUitls.getString(R.string.dispose_print_waiting),
                            Toast.LENGTH_LONG).show();
                }
            });
            GlobalConstant.IS_PRINTING_INTO = true;
            return true;
        } else if (code == 3) {
            return false;
        } else if (code == 6) {
            //未获取打印机的权限
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UiUitls.getContent(),
                            UiUitls.getString(R.string.dispose_print_again),
                            Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UiUitls.getContent(),
                        UiUitls.getString(R.string.dispose_print_port),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    /**
     * 设置需要打印的项目
     */
    private void setPrintText() {
        //参数配置项值
        int paramValue = dataMeasure.getParamValue();
        if ((paramValue & (0x01 << 0)) != 0) {
            //心电
            isEcg = true;
        }
        if ((paramValue & (0x01 << 1)) != 0) {
            //血氧
            isBloodOx = true;
        }
        if ((paramValue & (0x01 << 2)) != 0) {
            //血压
            isBloodPress = true;
        }
        if ((paramValue & (0x01 << 3)) != 0) {
            //体温
            isTemp = true;
        }
        if ((paramValue & (0x01 << 4)) != 0) {
            //血液三项
            isBloodThree = true;
        }
        if ((paramValue & (0x01 << 5)) != 0) {
            //显示11项
            isUr11 = true;
        }
        if ((paramValue & (0x01 << 6)) != 0) {
            //14项
            isUr14 = true;
        }

        if ((paramValue & (0x01 << 7)) != 0) {
            //血红蛋白
            isHgb = true;
        }
        if ((paramValue & (0x01 << 8)) != 0) {
            //血脂
            isBloodFat = true;
        }
        if ((paramValue & (0x01 << 9)) != 0) {
            //糖化
            isHbal = true;
        }
        if ((paramValue & (0x01 << 10)) != 0) {
            //bmi
            isBmi = true;
        }
        if ((paramValue & (0x01 << 11)) != 0) {
            //白细胞
            isWbac = true;
        }
    }

    /**
     * 张家港招标热敏打印机
     */
    private void requestQuickPrinterPermission() {
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new
                Intent(BroadcastAction.ACTION_QUICK_PRINTER_PERMISSION), 0);
        if (!usbManager.hasPermission(usbDevice)) {
            usbManager.requestPermission(usbDevice, mPermissionIntent);
            Toast.makeText(getActivity(),
                    R.string.dispose_print_again, Toast.LENGTH_LONG).show();
        } else {
            printWithQuickPrinter();
        }
    }

    /**
     * 连接打印机打开端口
     */
    private void printWithQuickPrinter() {
        if (usb.IsOpened()) {
            //开启打印任务
            es.submit(new TaskPrint());
            return;
        }
        //执行后在回调里
        es.submit(new TaskOpen(usb, usbManager, usbDevice));
    }

    @Override
    public void OnOpen() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //连接成功 做操作
                Toast.makeText(getActivity(), "已经连接", Toast.LENGTH_SHORT).show();
                //开启打印任务
                es.submit(new TaskPrint());
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        //连接失败
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {

    }

    /**
     * 执行连接打印机任务
     */
    public class TaskOpen implements Runnable {
        USBPrinting usb = null;
        UsbManager usbManager = null;
        UsbDevice usbDevice = null;

        /**
         * 构造
         * @param usb 类
         * @param usbManager 管理
         * @param usbDevice 设备
         */
        public TaskOpen(USBPrinting usb, UsbManager usbManager, UsbDevice usbDevice) {
            this.usb = usb;
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
        }

        @Override
        public void run() {
            //开启打印在回调里做操作
            usb.Open(usbManager, usbDevice, getActivity());
        }
    }

    /**
     * 打印机打印任务
     */
    public class TaskPrint implements Runnable {

        @Override
        public void run() {
            sendPrintData(mPage, 384, 800);
        }
    }

    /**
     * 发送数据
     * @param page 纸
     * @param nPrintWidth 宽度
     * @param nPrintHeight 高度
     */
    private void sendPrintData(Page page, int nPrintWidth, int nPrintHeight) {

        initReportData();
        if ((dataMeasure != null) && (!"".equals(dataMeasure.getEcgDiagnoseResult()))) {
            ecgPrint = true;
        } else {
            ecgPrint = false;
        }
        pos.Set(page.GetIO());
        byte[] status = new byte[1];
        if (pos.POS_RTQueryStatus(status, 4, 3000, 2)) {

            // 缺纸
            if ((status[0] & 0x60) != 0) {
                // 参考开发文档上的资料可知，type=4的时候，查询的是纸张状态。
                // 当纸尽的时候，会置第5,6位为0x60。
                // 所以，这里查纸张状态之后，对结果与上0x60，如果非零，表明纸尽。
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "纸张用尽，请放打印纸", Toast.LENGTH_SHORT).
                                show();
                    }
                });

                return;
            }
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.showProgress(getActivity(), "正在打印");
            }
        });
        int w = nPrintWidth;
        int h = nPrintHeight; // 75mm

        page.PageEnter();

        page.SetPrintArea(0, 0, w, h, Page.DIRECTION_LEFT_TO_RIGHT);
        StringBuilder buffer = new StringBuilder();
        //添加间隔号
        buffer.append(getString(R.string.usb_print_gap) + "\r\n");
        //添加姓名
        buffer.append("姓名:" + "    " + patientName + "\r\n");

        if (!StringUtil.isEmpty(patientBean.getCard())) {
            buffer.append("身份证:" + "  " + patientBean.getCard() + "\r\n");
        } else if (!StringUtil.isEmpty(patientBean.getMemberShipCard())
                && !patientBean.getMemberShipCard().contains(GlobalConstant.preStr)) {
            buffer.append("会员卡:" + "  " + patientBean.getMemberShipCard() + "\r\n");
        } else {
            buffer.append("身份证:" + "  " + patientBean.getCard() + "\r\n");
        }
        buffer.append("年龄:" + "    " + patientAge + "岁    " + "性别:" + "    " +
                UiUitls.getSex(sex) + "\r\n");
        //一般检查
        buffer.append(getString(R.string.usb_print_gap) + "\r\n");
        buffer.append(getString(R.string.print_normal_check) + "\r\n");
        //项目名称
        buffer.append(getString(R.string.print_project_name) + "  " +
                getString(R.string.print_project_result) + "  " +
                getString(R.string.print_project_unit) + "  " +
                getString(R.string.print_project_ref) + "\r\n");
        //判断
        if (isEcg) {
            buffer.append("心率" + "      " + ecghr + "   " + "bpm" + "    " + getString(R.string
                    .hr_reference_range) + "\r\n");
        }
        if (isBloodOx) {
            buffer.append("血氧" + "      " + spo2Trend + "    " + "%" + "      " + getString(R
                    .string.x_measure_bo_reference) + "\r\n");
            buffer.append("脉率" + "      " + spo2Pr + "    " + "bpm" + "    " + getString(R.string
                    .hr_reference_range) + "\r\n");
        }
        if (isBloodPress) {
            buffer.append("收缩压" + "    " + nibpSys + "   " + "mmHg" + "   " + getString(R.string
                    .x_measure_shrink_reference) + "\r\n");
            buffer.append("舒张压" + "    " + nibpDia + "   " + "mmHg" + "   " + getString(R.string
                    .x_measure_spread_reference) + "\r\n");
        }
        if (isTemp) {
            buffer.append("体温" + "      " + temp + "   " + "℃" + "     " + getString(R.string
                    .x_measure_temperature_reference) + "\r\n");
        }
        if (isBloodThree) {
            buffer.append(tvGluStyle.getText().toString() + blood_glu + "   " +
                    "mmol/L" + " " + tvGluReference
                    .getText().toString() + "\r\n");

            buffer.append(tvNsStyle.getText().toString() + "      " + nsValue + "   " + UiUitls
                    .getValueUnit(KParamType.URICACID_TREND) + " " + nsTvReference.getText()
                    .toString() + "\r\n");
            buffer.append(tvChoStyle.getText().toString() + "  " + cholValue + "   " + UiUitls
                    .getValueUnit(KParamType.CHOLESTEROL_TREND) + " " + tvChoRef.getText()
                    .toString() + "\r\n");
        }
        if (isBmi) {
            buffer.append("身高" + "      " + height + "   " + "cm" + "\r\n");
            buffer.append("体重" + "      " + weight + "   " + "kg" + "\r\n");
            buffer.append("BMI" + "       " + bmi + "       " + "\r\n");
        }

        if (isUr11) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //尿常规
            buffer.append(getString(R.string.print_urine_routine) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "  " +
                    getString(R.string.print_project_result) + "    " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("白细胞" + "    " + uri_leu + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("亚硝酸盐" + "  " + uri_nit + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("尿胆原" + "    " + uri_ubg + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("蛋白质" + "    " + uri_pro + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("ph值" + "      " + uri_ph + "      " + getString(R.string
                    .x_measure_ph_reference) + "\r\n");
            buffer.append("比重" + "      " + uri_sg + "      " + getString(R.string
                    .x_measure_sg_reference) + "\r\n");
            buffer.append("潜血" + "      " + uri_bld + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("酮体" + "      " + uri_ket + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("胆红素" + "    " + uri_bil + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("葡萄糖" + "    " + uri_glu + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("维生素" + "    " + uri_asc + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
        }
        if (isUr14) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //尿常规
            buffer.append(getString(R.string.print_urine_routine) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "   " +
                    getString(R.string.print_project_result) + "   " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("白细胞" + "     " + uri_leu + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("亚硝酸盐" + "   " + uri_nit + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("尿胆原" + "     " + uri_ubg + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("蛋白质" + "     " + uri_pro + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("ph值" + "       " + uri_ph + "      " + getString(R.string
                    .x_measure_ph_reference) + "\r\n");
            buffer.append("比重" + "      " + uri_sg + "      " + getString(R.string
                    .x_measure_sg_reference) + "\r\n");
            buffer.append("尿钙" + "       " + uri_ca + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("潜血" + "       " + uri_bld + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("酮体" + "       " + uri_ket + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("胆红素" + "     " + uri_bil + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("葡萄糖" + "     " + uri_glu + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("维生素" + "     " + uri_asc + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("微量白蛋白" + " " + uri_alb + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
            buffer.append("肌酐" + "       " + uri_cre + "      " + getString(R.string
                    .x_measure_unit_normal) + "\r\n");
        }
        if (isHgb) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //血红蛋白
            buffer.append(getString(R.string.print_blood_red) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "   " +
                    getString(R.string.print_project_result) + "  " +
                    getString(R.string.print_project_unit) + "   " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("血红蛋白" + "   " + bloodHgbTrend + "  " + "mmol/L" + "  " +
                    combine2Str(hgbMin, hgbMax) + "\r\n");
            buffer.append("红细胞压积" + " " + bloodHctTrend + "   " + "%" + "      "
                    + combine2Str(htcMin, htcMax) + "\r\n");
        }
        if (isBloodFat) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //血脂四项
            buffer.append(getString(R.string.print_blood_fat) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "   " +
                    getString(R.string.print_project_result) + "  " +
                    getString(R.string.print_project_unit) + "  " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("总胆固醇" + "   " + lipoidemiatc + "  " + "mmol/L" + " " + getString(R
                    .string.lipids_chol_range) + "\r\n");
            buffer.append("甘油三酯" + "   " + lipoidemiatg + "  " + "mmol/L" + " " + getString(R
                    .string.lipids_trig_range) + "\r\n");
            buffer.append("高密度脂蛋白" + " " + lipoidemiahdl + "  " + "mmol/L" + " " + getString(R
                    .string.lipids_hdl_range) + "\r\n");
            buffer.append("低密度脂蛋白" + " " + lipoidemialdl + "  " + "mmol/L" + " " + getString(R
                    .string.lipids_ldl_range) + "\r\n");
        }
        if (isHbal) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //糖化血红蛋白
            buffer.append(getString(R.string.print_blood_surge) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "  " +
                    getString(R.string.print_project_result) + "  " +
                    getString(R.string.print_project_unit) + "   " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("NGSP" + "      " + ngsp + "    " + "%" + "     " + getString(R.string
                    .ngsp_range) + "\r\n");
            buffer.append("IFCC" + "      " + ifcc + " " + "mmol/mol" + " " + getString(R.string
                    .ifcc_range) + "\r\n");
            buffer.append("eAG" + "       " + eag + " " + "mg/dL" + "    " + getString(R.string
                    .eag_range) + "\r\n");
        }
        if (isWbac) {
            buffer.append(getString(R.string.usb_print_gap) + "\r\n");
            //白细胞
            buffer.append(getString(R.string.print_wbc) + "\r\n");
            //项目名称
            buffer.append(getString(R.string.print_project_name) + "  " +
                    getString(R.string.print_project_result) + "  " +
                    getString(R.string.print_project_unit) + "  " +
                    getString(R.string.print_project_ref) + "\r\n");
            buffer.append("白细胞" + "    " + wbcValue + "  " + "10^9/L" + "  " + getString(R.string
                    .wbc_range) + "\r\n");
        }
        buffer.append(getString(R.string.usb_print_gap) + "\r\n");
        buffer.append("测量时间:" + " " + measureTime + "\r\n");
        buffer.append("打印时间:" + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date
                ()) + "\r\n");
        buffer.append("\r\n\r\n");
        buffer.append("医生签名：" + "\r\n\r\n");
        page.DrawText(buffer.toString(), 0, 750, 0, 0, Page.FONTTYPE_STANDARD, 0);
        page.DrawText("\r\n\r\n", 0, 750, 0, 0, Page.FONTTYPE_STANDARD, 0);
        //打印文本
        page.PagePrint();

        // 使用POS里面的切刀指令
//        pos.POS_CutPaper();

//        暂时屏蔽二维码
//        printCode();
        if (ecgPrint) {
            //打印心电图片
            printEcg();
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.hideProgress();
            }
        });
        page.PageExit();
    }

    private String formatPrintString(String name, String value, String unit, String reference) {
        String str = "";

        return str;
    }

    /**
     * 打印心电
     */
    private void printEcg() {
        //录入一个位图
        InputStream resourceAsStream2 = UiUitls.getContent().getClass()
                .getClassLoader().getResourceAsStream("assets/print.bmp");
        if (printBmp == null) {
            printBmp = BitmapFactory.decodeStream(resourceAsStream2).
                    copy(Bitmap.Config.RGB_565, true);
        }
        Canvas canvas = new Canvas(printBmp);
        //设置画笔
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, 3300, 600, paint);
        HeartImage.drawEcg(dataMeasure, canvas, 3300, 600);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(printBmp, 0, 0, printBmp.getWidth(), printBmp
                .getHeight(), matrix, true);

        Bundle data = new Bundle();
        data.putParcelable(GlobalConstant.PARCE1, bmp);
        data.putInt(GlobalConstant.INTPARA1, 350);
        data.putInt(GlobalConstant.INTPARA2, 0);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 7;
        msg.setData(data);
        mHandler.sendMessage(msg);
        printBmp = null;
    }

    /**
     * 打印一维码
     */
    private void printCode() {
        InputStream codeStream = UiUitls.getContent().getClass().getClassLoader()
                .getResourceAsStream("assets/codeTest.png");
        Bitmap codeBmp = BitmapFactory.decodeStream(codeStream).copy(Bitmap.Config.RGB_565, true);
        Bundle data = new Bundle();
        data.putParcelable(GlobalConstant.PARCE1, codeBmp);
        data.putInt(GlobalConstant.INTPARA1, 350);
        data.putInt(GlobalConstant.INTPARA2, 0);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 7;
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    /**
     * 检查USB是否链接
     * @return true代表已连接热敏打印机
     */
    private boolean checkQuickPrintConnected() {
        usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceList.size() > 0) {
            // 初始化选择对话框布局，并添加按钮和事件
            while (deviceIterator.hasNext()) { // 这里是if不是while，说明我只想支持一种device
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 19267 || device.getProductId() == 13624) {
                    usbDevice = device;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 得到病人详情
     */
    private void getPatientDetail() {
        List<PatientBean> patients = DBDataUtil.getPatientByIdCard(idcard);
        if (patients.size() > 0) {
            patient = patients.get(0);
        }
    }

    /**
     * 初始化参考值
     * 暂时只有血红蛋白
     */
    private void initReferenceValue() {
        int sex = patient.getSex();
        String hgbReference = "";
        String htcReference = "";
        if (sex == Sex.FEMALE) {
            hgbReference = combine2Str(BloodMem.WOMAN_BLOOD_MIN, BloodMem.WOMAN_BLOOD_MAX);
            htcReference = combine2Str(BloodMem.WOMAN_HOGIN_MIN, BloodMem.WOMAN_HOGIN_MAX);
        } else {
            hgbReference = combine2Str(BloodMem.MAN_BLOOD_MIN, BloodMem.MAN_BLOOD_MAX);
            htcReference = combine2Str(BloodMem.MAN_HOGIN_MIN, BloodMem.MAN_HOGIN_MAX);
        }
        tvHgbReference.setText(hgbReference);
        tvHtcReference.setText(htcReference);
    }

    /**
     * 用 "-" 合并两个数值
     * @param low 最低值
     * @param high 最高值
     * @return reference value 参考值
     */
    private String combine2Str(float low, float high) {
        return low + "-" + high;
    }

    /**
     * 用 "-" 合并两个数值
     * @param low 最低值
     * @param high 最高值
     * @return reference value 参考值
     */
    private String combine2Str(int low, int high) {
        return low + "-" + high;
    }

    /**
     * 初始化血红蛋白和红细胞积压值事件
     */
    private void compareBloodHgb() {
        tvBloodHGBTrend.addTextChangedListener(new OverProofUtil(hgbMin
                , hgbMax, tvBloodHGBTrend));
        tvBloodHCTTrend.addTextChangedListener(new OverProofUtil(htcMin
                , htcMax, tvBloodHCTTrend));
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //心率
        tvHr.addTextChangedListener(new OverProofUtil(GlobalConstant.HR_ALARM_LOW
                , GlobalConstant.HR_ALARM_HIGH, tvHr));
        //血氧
        tvBo.addTextChangedListener(new OverProofUtil(Spo.SPO2_LOW, Spo.SPO2_HIGH, tvBo));
        tvBopr.addTextChangedListener(new OverProofUtil(GlobalConstant.HR_ALARM_LOW
                , GlobalConstant.HR_ALARM_HIGH, tvBopr));
        //血压tv_shrink_pressure
        tvShrinkPressure.addTextChangedListener(new OverProofUtil(Nibp.SYS_LOW
                , Nibp.SYS_HIGH, tvShrinkPressure));
        tvSpreadPressure.addTextChangedListener(new OverProofUtil(Nibp.DIA_LOW
                , Nibp.DIA_HIGH, tvSpreadPressure));
        tvAveragePressure.addTextChangedListener(new OverProofUtil(Nibp.MAP_LOW
                , Nibp.MAP_HIGH, tvAveragePressure));
        tvPr.addTextChangedListener(new OverProofUtil(Nibp.PR_LOW, Nibp.PR_HIGH, tvPr));
        switch (gluStyle) {
            case lift:
                tvGluReference.setText(UiUitls.getString(R.string.p_2_7));
                tvBs.addTextChangedListener(new OverProofUtil(
                        BeneParamValue.XT_VALUE_MIN, BeneParamValue.XT_VALUE_MAX, tvBs));
                bloodGluMax = BeneParamValue.XT_VALUE_MAX;
                bloodGluMin = BeneParamValue.XT_VALUE_MIN;
                break;
            case middle:
                tvGluReference.setText(UiUitls.getString(R.string.p_2_11));
                tvBs.addTextChangedListener(new OverProofUtil(
                        BeneParamValue.XT_AFTER_VALUE_MIN,
                        BeneParamValue.XT_AFTER_VALUE_MAX, tvBs));
                bloodGluMax = BeneParamValue.XT_AFTER_VALUE_MAX;
                bloodGluMin = BeneParamValue.XT_AFTER_VALUE_MIN;
                break;
            default:
                break;
        }
        //1 男 2 女
        int sex = patientBean.getSex();
        if (sex == Sex.FEMALE) {
            hgbMax = BloodMem.WOMAN_BLOOD_MAX;
            hgbMin = BloodMem.WOMAN_BLOOD_MIN;
            htcMax = BloodMem.WOMAN_HOGIN_MAX;
            htcMin = BloodMem.WOMAN_HOGIN_MIN;
            nsMax = BeneParamValue.NS_VALUE_MAXG;
            nsMin = BeneParamValue.NS_VALUE_MING;
        } else {
            hgbMax = BloodMem.MAN_BLOOD_MAX;
            hgbMin = BloodMem.MAN_BLOOD_MIN;
            htcMax = BloodMem.MAN_HOGIN_MAX;
            htcMin = BloodMem.MAN_HOGIN_MIN;
            nsMax = BeneParamValue.NS_VALUE_MAX;
            nsMin = BeneParamValue.NS_VALUE_MIN;
        }
        if (sex == 0) {
            nsReference = UiUitls.getString(R.string.ns_g);
            nsTvReference.setText(nsReference);
            //尿酸提示
            compareReference(tvNs, BeneParamValue.NS_VALUE_MING
                    , BeneParamValue.NS_VALUE_MAXG, ivNs);
            tvNs.addTextChangedListener(new OverProofUtil(BeneParamValue.NS_VALUE_MING
                    , BeneParamValue.NS_VALUE_MAXG, tvNs));
        } else if (sex == 1) {
            nsReference = UiUitls.getString(R.string.ns_m);
            nsTvReference.setText(nsReference);
            //尿酸提示
            compareReference(tvNs, BeneParamValue.NS_VALUE_MIN
                    , BeneParamValue.NS_VALUE_MAX, ivNs);
            tvNs.addTextChangedListener(new OverProofUtil(BeneParamValue.NS_VALUE_MIN
                    , BeneParamValue.NS_VALUE_MAX, tvNs));
        } else {
            nsReference = UiUitls.getString(R.string.ns_m);
            nsTvReference.setText(nsReference);
            //尿酸提示
            compareReference(tvNs, BeneParamValue.NS_VALUE_MIN
                    , BeneParamValue.NS_VALUE_MAX, ivNs);
            tvNs.addTextChangedListener(new OverProofUtil(BeneParamValue.NS_VALUE_MIN
                    , BeneParamValue.NS_VALUE_MAX, tvNs));
        }
        //总胆固醇提示
        compareReference(tvChol, BeneParamValue.CHOL_VALUE_MIN, BeneParamValue.CHOL_VALUE_MAX
                , ivChol);
        tvChol.addTextChangedListener(new OverProofUtil(BeneParamValue.CHOL_VALUE_MIN
                , BeneParamValue.CHOL_VALUE_MAX, tvChol));
        //尿常规
        tvUrPh.addTextChangedListener(new OverProofUtil(Urine.PH_LOW, Urine.PH_HIGH, tvUrPh));
        tvUrSg.addTextChangedListener(new OverProofUtil(Urine.SG_LOW, Urine.SG_HIGH, tvUrSg));
//        tvUrCa.addTextChangedListener(new OverProofUtil(Urine.CA_LOW, Urine.CA_HIGH, tvUrCa));
//        tvUrAlb.addTextChangedListener(new OverProofUtil(Urine.MA_LOW, Urine.MA_HIGH, tvUrAlb));
        //肌酐不做异常区分
//        tvUrCre.addTextChangedListener(new OverProofUtil(0.9f, 26.5f, tvUrCre));
        //血红蛋白
        compareBloodHgb();
        //血脂四项
        tvChValue.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_CHOL_ALARM_LOW
                , GlobalConstant.LIPIDS_CHOL_ALARM_HIGH, tvChValue));
        tvTrigValuee.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_TRIG_ALARM_LOW
                , GlobalConstant.LIPIDS_TRIG_ALARM_HIGH, tvTrigValuee));
        tvHdlValue.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_HDL_ALARM_LOW
                , GlobalConstant.LIPIDS_HDL_ALARM_HIGH, tvHdlValue));
        tvLdlValue.addTextChangedListener(new OverProofUtil(GlobalConstant.LIPIDS_LDL_ALARM_LOW
                , GlobalConstant.LIPIDS_LDL_ALARM_HIGH, tvLdlValue));
        //体温
        tvTemperature.addTextChangedListener(new OverProofUtil(IrTemp.LOW
                , IrTemp.HIGH, tvTemperature));

        //糖化血红蛋白三项
        tvNgsp.addTextChangedListener(new OverProofUtil(SugarBloodParam.NGSP_MIN
                , SugarBloodParam.NGSP_MAX, tvNgsp));
        tvIfcc.addTextChangedListener(new OverProofUtil(SugarBloodParam.IFCC_MIN
                , SugarBloodParam.IFCC_MAX, tvIfcc));
        tvEag.addTextChangedListener(new OverProofUtil(SugarBloodParam.EAG_MIN
                , SugarBloodParam.EAG_MAX, tvEag));

        //BMI
        tvBmi.addTextChangedListener(new OverProofUtil(BmiParam.MIN_VALUE
                , BmiParam.MAX_VALUE, tvBmi));

        tvWbcValue.addTextChangedListener(new OverProofUtil(WbcParamValue.MIN_VALUE
                , WbcParamValue.MAX_VALUE, tvWbcValue));
    }

    /**
     * 获取病人idcard,姓名 初始化病人体检数据 测量数据 measuredatabean patientbean
     */
    private void initData() {
        Bundle bundle = getArguments();
        idcard = bundle.getString(GlobalConstant.ID_CARD);
        id = bundle.getLong(GlobalConstant.MEASURE_ID);
        dataMeasure = DBDataUtil.getMeasureDao().load(id);
        if (!TextUtils.isEmpty(idcard)) {
            patientBean = DBDataUtil.getPatientByIdCard(idcard).get(0);
        } else {
            patientBean = DBDataUtil.getPatientByMemberShipCard(memberShipCard).get(0);
        }
        patientName = patientBean.getName();
        if (null != dataMeasure) {
            bloodGluFlag = dataMeasure.getGluStyle();
            if (bloodGluFlag.equals("0")) {
                gluStyle = GlobalConstant.BtnFlag.lift;
            } else {
                gluStyle = GlobalConstant.BtnFlag.middle;
            }
        }

        if (patientBean != null && patientBean.getAge() > -1) {
            patientAge = String.valueOf(patientBean.getAge());
        } else {
            patientAge = "";
        }
    }

    /**
     * 根据参数配置设置测量项是否可见
     * @param param 参数配置值
     * @param position 参数位置
     * @param view 控件
     */
    private void initViewVisibility(int param, int position, View view) {
        if ((param & (0x01 << position)) != 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化病人的体检显示 各类测量数据显示
     */
    private void initView() {
        int paramValue = dataMeasure.getParamValue();
        initViewVisibility(paramValue, 0, llHr);
        initViewVisibility(paramValue, 1, llSpo2);
        initViewVisibility(paramValue, 2, llNibp);
        initViewVisibility(paramValue, 3, llTemp);
        initViewVisibility(paramValue, 4, llGlu);
        initViewVisibility(paramValue, 5, llUrt);
        initViewVisibility(paramValue, 6, llUrt);
        initViewVisibility(paramValue, 7, llHgb);
        initViewVisibility(paramValue, 8, llLipids);
        initViewVisibility(paramValue, 9, llSugarHgb);
        initViewVisibility(paramValue, 10, llBmi);
        initViewVisibility(paramValue, 11, llWbc);
        if ((paramValue & (0x01 << 5)) != 0) {
            caLayout.setVisibility(View.GONE);
            maLayout.setVisibility(View.GONE);
            crLayout.setVisibility(View.GONE);
            llUrt.setVisibility(View.VISIBLE);
        } else if ((paramValue & (0x01 << 6)) != 0) {
            caLayout.setVisibility(View.VISIBLE);
            maLayout.setVisibility(View.VISIBLE);
            crLayout.setVisibility(View.VISIBLE);
            llUrt.setVisibility(View.VISIBLE);
        } else {
            llUrt.setVisibility(View.GONE);
        }

        if (patientBean != null) {
            tvName.setText(patientName);
            tvAge.setText(patientBean.getAge() < 0 ? "" : (patientBean.getAge() + UiUitls
                    .getString(R.string.unit_age)));
            sex = patientBean.getSex();
            tvSex.setText(UiUitls.getSex(sex));
        }

        if (dataMeasure == null) {
            return;
        }
        ecghr = (dataMeasure.getTrendValue(KParamType.ECG_HR) == -1000) ?
                "-?-" : String.valueOf(dataMeasure.getTrendValue(KParamType.
                ECG_HR) / GlobalConstant.TREND_FACTOR);
        spo2Trend = (dataMeasure.getTrendValue(KParamType.SPO2_TREND) ==
                -1000) ? "-?-" : String.valueOf(dataMeasure.getTrendValue
                (KParamType.SPO2_TREND) / GlobalConstant.TREND_FACTOR);
        spo2Pr = (dataMeasure.getTrendValue(KParamType.SPO2_PR) == -1000)
                ? "-?-" : String.valueOf(dataMeasure.getTrendValue(KParamType.
                SPO2_PR) / GlobalConstant.TREND_FACTOR);
        nibpSys = (dataMeasure.getTrendValue(KParamType.NIBP_SYS) ==
                -1000) ? "-?-" : String.valueOf(dataMeasure.getTrendValue
                (KParamType.NIBP_SYS) / GlobalConstant.TREND_FACTOR);
        nibpDia = (dataMeasure.getTrendValue(KParamType.NIBP_DIA)
                == -1000) ? "-?-" : String.valueOf(dataMeasure.getTrendValue
                (KParamType.NIBP_DIA) / GlobalConstant.TREND_FACTOR);
        nibpMap = (dataMeasure.getTrendValue(KParamType.NIBP_MAP) ==
                -1000) ? "-?-" : String.valueOf(dataMeasure.getTrendValue
                (KParamType.NIBP_MAP) / GlobalConstant.TREND_FACTOR);
        nibpPr = (dataMeasure.getTrendValue(KParamType.NIBP_PR) ==
                -1000) ? "-?-" : String.valueOf(dataMeasure.getTrendValue
                (KParamType.NIBP_PR) / GlobalConstant.TREND_FACTOR);
        uri_sg = (dataMeasure.getTrendValue(KParamType.URINERT_SG)
                == -1000) ? "-?-" : String.format("%.3f", (double) dataMeasure
                .getTrendValue(KParamType.URINERT_SG) / 1000.0);
        uri_leu = (dataMeasure.getTrendValue(KParamType.URINERT_LEU)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_LEU) / GlobalConstant.URITREND_FACTOR);
        uri_nit = (dataMeasure.getTrendValue(KParamType.URINERT_NIT)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_NIT) / GlobalConstant.URITREND_FACTOR);
        uri_ubg = (dataMeasure.getTrendValue(KParamType.URINERT_UBG)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_UBG) / GlobalConstant.URITREND_FACTOR);
        uri_ph = (dataMeasure.getTrendValue(KParamType.URINERT_PH)
                == -1000) ? "-?-" : String.valueOf((float) dataMeasure.
                getTrendValue(KParamType.URINERT_PH) / 100.0f);
        uri_bld = (dataMeasure.getTrendValue(KParamType.URINERT_BLD) ==
                -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_BLD) / GlobalConstant.URITREND_FACTOR);
        uri_glu = (dataMeasure.getTrendValue(KParamType.URINERT_GLU) ==
                -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_GLU) / GlobalConstant.URITREND_FACTOR);
        uri_asc = (dataMeasure.getTrendValue(KParamType.URINERT_ASC) ==
                -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_ASC) / GlobalConstant.URITREND_FACTOR);
        uri_ket = (dataMeasure.getTrendValue(KParamType.URINERT_KET)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_KET) / GlobalConstant.URITREND_FACTOR);
        uri_bil = (dataMeasure.getTrendValue(KParamType.URINERT_BIL)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_BIL) / GlobalConstant.URITREND_FACTOR);
        uri_alb = (dataMeasure.getTrendValue(KParamType.URINERT_ALB)
                == -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_ALB) / GlobalConstant.URITREND_FACTOR);
//        String uri_cre = (dataMeasure.getTrendValue(KParamType.URINERT_CRE)
//                == -1000) ? "-?-" : String.format("%.1f", (float) dataMeasure.
//                getTrendValue(KParamType.URINERT_CRE) / 100.0f);
        uri_cre = (dataMeasure.getTrendValue(KParamType.URINERT_CRE)
                == -1000) ? "-?-" : valueToString(dataMeasure
                .getTrendValue(KParamType.URINERT_CRE) / GlobalConstant.URITREND_FACTOR);
        uri_ca = (dataMeasure.getTrendValue(KParamType.URINERT_CA) ==
                -1000) ? "-?-" : valueToString(dataMeasure.
                getTrendValue(KParamType.URINERT_CA) / GlobalConstant.URITREND_FACTOR);
        uri_pro = (dataMeasure.getTrendValue(KParamType.URINERT_PRO) ==
                -1000) ? "-?-" : valueToString(dataMeasure.getTrendValue
                (KParamType.URINERT_PRO) / GlobalConstant.URITREND_FACTOR);
        blood_glu = (dataMeasure.getTrendValue(
                KParamType.BLOODGLU_BEFORE_MEAL) == -1000) ? "-?-" :
                String.valueOf((float) dataMeasure.getTrendValue(KParamType.
                        BLOODGLU_BEFORE_MEAL) / GlobalConstant.TREND_FACTOR);
        //尿酸
        nsValue = dataMeasure.getTrendValue(KParamType
                .URICACID_TREND) == -1000 ? UiUitls.getString(R
                .string._1_02) :
                String.valueOf(dataMeasure.getTrendValue(KParamType.URICACID_TREND) / 100.0f);
        //总胆固醇
        cholValue = dataMeasure.getTrendValue(KParamType
                .CHOLESTEROL_TREND) == -1000 ? UiUitls.getString
                (R.string._1_02) :
                String.valueOf(dataMeasure.getTrendValue(KParamType.CHOLESTEROL_TREND) / 100.0f);

        //获取BMi值
        bmi = TextUtils.isEmpty(dataMeasure.getBmi()) ? getRecString(R.string.invalid_data)
                : dataMeasure.getBmi();
        height = TextUtils.isEmpty(dataMeasure.getHeight()) ?
                getRecString(R.string.invalid_data) : dataMeasure.getHeight();
        weight = TextUtils.isEmpty(dataMeasure.getWeight()) ?
                getRecString(R.string.invalid_data) : dataMeasure.getWeight();
        //获取糖化血红蛋白三个测量值
        ngsp = getFormatterStr(KParamType.HBA1C_NGSP
                , dataMeasure.getTrendValue(KParamType.HBA1C_NGSP), false);
        ifcc = getFormatterStr(KParamType.HBA1C_IFCC
                , dataMeasure.getTrendValue(KParamType.HBA1C_IFCC), false);
        eag = getFormatterStr(KParamType.HBA1C_EAG
                , dataMeasure.getTrendValue(KParamType.HBA1C_EAG), false);
        //获取白细胞值
        wbcValue = dataMeasure.getTrendValue(KParamType.BLOOD_WBC) ==
                GlobalConstant.INVALID_DATA ? "-?-" : dataMeasure
                .getTrendValue(KParamType.BLOOD_WBC) / GlobalConstant.WBC_FACTOR + "";

        //血红蛋白
        //容积比
        bloodHctTrend = (dataMeasure.getTrendValue(
                KParamType.BLOOD_HCT) == -1000) ? "-?-" :
                dataMeasure.getTrendValue(KParamType.BLOOD_HCT)
                        / GlobalConstant.TREND_FACTOR + "";
        //血红蛋白
        bloodHgbTrend = (dataMeasure.getTrendValue(
                KParamType.BLOOD_HGB) == -1000) ? "-?-" :
                String.valueOf(NumUtil.trans2Decimal((float) dataMeasure.getTrendValue(KParamType.
                        BLOOD_HGB) / GlobalConstant.TREND_FACTOR, 1));
        tvBloodHCTTrend.setText(bloodHctTrend);
        tvBloodHGBTrend.setText(bloodHgbTrend);

        lipoidemiatc = UiUitls.getValueAfterFactor(KParamType.LIPIDS_CHOL
                , dataMeasure.getTrendValue(KParamType.LIPIDS_CHOL));
        lipoidemiatg = UiUitls.getValueAfterFactor(KParamType.LIPIDS_TRIG
                , dataMeasure.getTrendValue(KParamType.LIPIDS_TRIG));
        lipoidemialdl = UiUitls.getValueAfterFactor(KParamType.LIPIDS_LDL
                , dataMeasure.getTrendValue(KParamType.LIPIDS_LDL));
        lipoidemiahdl = UiUitls.getValueAfterFactor(KParamType.LIPIDS_HDL
                , dataMeasure.getTrendValue(KParamType.LIPIDS_HDL));

        tvChValue.setText(lipoidemiatc);
        tvTrigValuee.setText(lipoidemiatg);
        tvLdlValue.setText(lipoidemialdl);
        tvHdlValue.setText(lipoidemiahdl);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        measureTime = dateFormat.format(dataMeasure.getMeasureTime());
        //暂时通过此方式判断是
        if (dataMeasure.getTrendValue(KParamType.TEMP_T1) == -1000) {
            temp = (dataMeasure.getTrendValue(KParamType.IRTEMP_TREND) ==
                    -1000) ? "-?-" : String.valueOf(dataMeasure
                    .getTrendValue(KParamType.IRTEMP_TREND) / 100f);
        } else {
            temp = (dataMeasure.getTrendValue(KParamType.TEMP_T1) == -1000) ?
                    "-?-" : String.valueOf(dataMeasure.getTrendValue(KParamType.TEMP_T1) / 100f);
        }
        tvHr.setText(ecghr);
        tvBo.setText(spo2Trend);
        tvBopr.setText(spo2Pr);

        tvBs.setText(blood_glu);
        tvNs.setText(nsValue);
        tvChol.setText(cholValue);

        tvTemperature.setText(temp);

        tvShrinkPressure.setText(nibpSys);
        tvSpreadPressure.setText(nibpDia);
        tvAveragePressure.setText(nibpMap);
        tvPr.setText(nibpPr);

        tvUrSg.setText(uri_sg);
        tvUrPro.setText(uri_pro);
        tvUrGlu.setText(uri_glu);
        tvUrLeu.setText(uri_leu);
        tvUrBld.setText(uri_bld);
        tvUrNit.setText(uri_nit);
        tvUrKet.setText(uri_ket);
        tvUrUbg.setText(uri_ubg);
        tvUrBil.setText(uri_bil);
        tvUrPh.setText(uri_ph);
        tvUrAsc.setText(uri_asc);
        tvUrAlb.setText(uri_alb);
        tvUrCre.setText(uri_cre);
        tvUrCa.setText(uri_ca);

        //bmi赋值
        tvBmi.setText(bmi);
        tvHeight.setText(height);
        tvWeight.setText(weight);
        //糖化测量项赋值
        tvNgsp.setText(ngsp);
        tvIfcc.setText(ifcc);
        tvEag.setText(eag);
        //白细胞赋值
        tvWbcValue.setText(wbcValue);

        compareUrineValue(tvUrCa, uri_ca);
        compareUrineValue(tvUrPro, uri_pro);
        compareUrineValue(tvUrGlu, uri_glu);
        compareUrineValue(tvUrLeu, uri_leu);
        compareUrineValue(tvUrBld, uri_bld);
        compareUrineValue(tvUrNit, uri_nit);
        compareUrineValue(tvUrKet, uri_ket);
        compareUrineValue(tvUrUbg, uri_ubg);
        compareUrineValue(tvUrBil, uri_bil);
        compareUrineValue(tvUrAsc, uri_asc);
        compareUrineValue(tvUrAlb, uri_alb);
        compareUrineValue(tvUrCre, uri_cre);

        // 比较参考值，如不在范围内，提示箭头图片
        compareReference(tvHr, GlobalConstant.HR_ALARM_LOW, GlobalConstant.HR_ALARM_HIGH
                , ivHrHint);
//        compareReference(tvUrAlb, Urine.MA_LOW, Urine.MA_HIGH, ivAlbHint);
//        compareReference(tvUrCre, 0.9f, 26.5f, ivCreHint);
        compareReference(tvUrSg, Urine.SG_LOW, Urine.SG_HIGH, ivSgHint);
        compareReference(tvUrPh, Urine.PH_LOW, Urine.PH_HIGH, ivPhHint);
//        compareReference(tvUrCa, Urine.CA_LOW, Urine.CA_HIGH, ivCaHint);
        compareReference(tvBo, Spo.SPO2_LOW, Spo.SPO2_HIGH, ivBoHint);
        compareReference(tvBopr, Spo.PR_LOW, Spo.PR_HIGH, ivBoprHint);
        compareReference(tvShrinkPressure, Nibp.SYS_LOW, Nibp.SYS_HIGH, ivShrinkHint);
        compareReference(tvSpreadPressure, Nibp.DIA_LOW, Nibp.DIA_HIGH, ivSpreadHint);
        compareReference(tvAveragePressure, Nibp.MAP_LOW, Nibp.MAP_HIGH, ivAverageHint);
        compareReference(tvPr, Nibp.PR_LOW, Nibp.PR_HIGH, ivPrHint);
        compareReference(tvTemperature, IrTemp.LOW, IrTemp.HIGH, ivTemperatureHint);

        compareReference(tvChValue, GlobalConstant.LIPIDS_CHOL_ALARM_LOW
                , GlobalConstant.LIPIDS_CHOL_ALARM_HIGH, ivChValue);
        compareReference(tvTrigValuee, GlobalConstant.LIPIDS_TRIG_ALARM_LOW
                , GlobalConstant.LIPIDS_TRIG_ALARM_HIGH, ivTrigValuee);
        compareReference(tvHdlValue, GlobalConstant.LIPIDS_HDL_ALARM_LOW
                , GlobalConstant.LIPIDS_HDL_ALARM_HIGH, ivHdlValue);
        compareReference(tvLdlValue, GlobalConstant.LIPIDS_LDL_ALARM_LOW
                , GlobalConstant.LIPIDS_LDL_ALARM_HIGH, ivLdlValue);
        //糖化血红蛋白异常比较
        compareReference(tvNgsp, SugarBloodParam.NGSP_MIN, SugarBloodParam.NGSP_MAX, ivNgsp);
        compareReference(tvIfcc, SugarBloodParam.IFCC_MIN, SugarBloodParam.IFCC_MAX, ivIfcc);
        compareReference(tvEag, SugarBloodParam.EAG_MIN, SugarBloodParam.EAG_MAX, ivEag);
        //BMI值比较
        compareReference(tvBmi, BmiParam.MIN_VALUE, BmiParam.MAX_VALUE, ivBmi);
        //白细胞值比较
        compareReference(tvWbcValue, WbcParamValue.MIN_VALUE, WbcParamValue.MAX_VALUE, ivWbc);

        if (sex == Sex.FEMALE) {
            hgbMax = BloodMem.WOMAN_BLOOD_MAX;
            hgbMin = BloodMem.WOMAN_BLOOD_MIN;
            htcMax = BloodMem.WOMAN_HOGIN_MAX;
            htcMin = BloodMem.WOMAN_HOGIN_MIN;
            nsMax = BeneParamValue.NS_VALUE_MAXG;
            nsMin = BeneParamValue.NS_VALUE_MING;
        } else {
            hgbMax = BloodMem.MAN_BLOOD_MAX;
            hgbMin = BloodMem.MAN_BLOOD_MIN;
            htcMax = BloodMem.MAN_HOGIN_MAX;
            htcMin = BloodMem.MAN_HOGIN_MIN;
            nsMax = BeneParamValue.NS_VALUE_MAX;
            nsMin = BeneParamValue.NS_VALUE_MIN;
        }
        compareReference(tvBs, bloodGluMin, bloodGluMax, ivBsHint);
        //尿酸
        compareReference(tvNs, nsMin, nsMax, ivNs);
        //总胆固醇
        compareReference(tvChol, BeneParamValue.CHOL_VALUE_MIN, BeneParamValue.CHOL_VALUE_MAX
                , ivChol);
        compareReference(tvBloodHGBTrend, hgbMin, hgbMax, ivHgbHint);
        compareReference(tvBloodHCTTrend, htcMin, htcMax, ivHtcHint);
        compareReference(uri_leu, ivLeuHint);
        compareReference(uri_ubg, ivUbgHint);
        compareReference(uri_pro, ivProHint);
        compareReference(uri_bil, ivBilHint);
        compareReference(uri_glu, ivGluHint);
        compareReference(uri_asc, ivAscHint);
        compareReference(uri_ket, ivKetHint);
        compareReference(uri_nit, ivNitHint);
        compareReference(uri_bld, ivBldHint);
        compareReference(uri_alb, ivAlbHint);
        compareReference(uri_ca, ivCaHint);
        compareReference(uri_cre, ivCreHint);

        //  基本资料信息
        String orgName = UiUitls.getContent().getString(R.string.x_district_temp);
        if (!TextUtils.isEmpty(orgName)) {
            tvDistrict.setText("" + orgName);
        }
        tvDoctor.setText(TextUtils.isEmpty(dataMeasure.getDoctor()) ?
                getRecString(R.string.sex_unknown) : dataMeasure.getDoctor());
        tvMeasureTime.setText(measureTime);
        switch (gluStyle) {
            case lift:
                glu = UiUitls.getContent().getString(R.string.glustyle_0);
                break;
            case middle:
                glu = UiUitls.getContent().getString(R.string.glustyle_2);
                break;
            default:
                glu = UiUitls.getContent().getString(R.string.glustyle_0);
                break;
        }
        tvGluStyle.setText(glu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnectUsb();
        //页面退出，释放对象所占用的内存
        bitmapEcg = null;
        bitmapPort = null;
        bitmapPort2 = null;
        printBmp = null;

        GlobalConstant.isInHealthReportFragment = false;
    }

    /**
     * 判断打印机是否连接
     */
    private int printByPantum1() {
        PantumPrint pantumPrint = new PantumPrint();
        pantumPrint.initPrint(UiUitls.getContent());
        int connectUsb = pantumPrint.connectUsb(UiUitls.getContent());
        if (connectUsb == 0) {
            GlobalConstant.PRINT_ACTION = true;
        }
        return connectUsb;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 生成报告空位图
     * @param bitmap
     * @param o
     */
    private void drawReportOnBitmaptest(Bitmap bitmap, Object o) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawLine(0, 0, bitmap.getHeight(), bitmap.getWidth(), paint);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        mMeasureDataBeanList.get(0);
        Bundle bundle = new Bundle();
        bundle.putString("idcard",
                ((MeasureDataBean) parent.getAdapter()
                        .getItem(position)).getIdcard());
        Fragment fragment;
        switch (GlobalConstant.ECG_NUM) {
            case 5:
                //有誤
                fragment = new GetEcgWaveRecordFor5();
                break;
            default:
                fragment = new GetEcgWaveRecordFor5();
                break;
        }
        fragment.setArguments(bundle);
        switchToFragment(R.id.fragment, fragment, "_fragment", true);
    }

    /**
     * 获取url
     * @param which
     */
    private void getUrl(final String which, final Object obj) {
        final boolean res = true;
        RequestParams params = new RequestParams();
        Map<String, Object> map = new HashMap<>();
        map.put("ver", GlobalConstant.VER);
        map.put("command", "1005");
        map.put("errcode", "000");
        map.put("timestamp", System.currentTimeMillis());
        map.put("apptype", "1");
        map.put("language", "1");
        if (which.equals("person")) {
            map.put("keyname", "c112");
        } else if (which.equals("health")) {
            map.put("keyname", "c113");
        }
        map.put("idcard", idcard);

        params.put("measureDetail", UnitConvertUtil.mapToJSON(map));
    }

    public class MyPrintDocumentAdapter
            extends PrintDocumentAdapter {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 1;

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                PrintAttributes newAttributes,
                CancellationSignal cancellationSignal,
                LayoutResultCallback callback,
                Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            //实际测得pageHeight为864，pageWidth为648
            pageHeight = newAttributes.getMediaSize()
                    .getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize()
                    .getWidthMils() / 1000 * 72;

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("report.pdf").setContentType(
                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(
                                totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                final ParcelFileDescriptor destination,
                final CancellationSignal cancellationSignal,
                final WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i)) {
                    PageInfo newPage = new PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page = myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(destination
                        .getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                CrashReport.postCatchedException(e);
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page) {
            for (int i = 0; i < pageRanges.length; i++) {
                if ((page >= pageRanges[i].getStart()) && (page <=
                        pageRanges[i].getEnd())) {
                    return true;
                }
            }
            return false;
        }

        private void drawPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            int x; //x方向坐标
            int y; //y方向坐标
            //行间隔
            final int row_interval = 25;
            //列间隔
            final int column_interval = 150;
            //打印标题的起始位置
            final int title_x = 240;
            final int title_y = 35;
            //打印个人信息的起始位置
            final int persion_info_x = 54;
            final int persion_info_y = 60;
            //打印测量结果的起始位置
            final int measure_result_x = 54;
            final int measure_result_y = persion_info_y + row_interval * 2;

            //设置画笔
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(24);
            //打印标题
            x = title_x;
            y = title_y;
            canvas.drawText(UiUitls.getString(R.string.measure_report), x, y,
                    paint);

            //设置画笔
            paint.setTextSize(14);
            //获取待打印的数据
            String ecghr = (dataBean.getTrendValue(KParamType.ECG_HR) ==
                    GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .ECG_HR) / GlobalConstant.TREND_FACTOR);
            String ecgDiagnoseResult = (dataBean.getEcgDiagnoseResult()
                    .length() == 0)
                    ? UiUitls.getString(R.string.default_value)
                    : dataBean.getEcgDiagnoseResult();
            String spo2Trend = (dataBean.getTrendValue(KParamType
                    .SPO2_TREND) == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .SPO2_TREND) / GlobalConstant.TREND_FACTOR);
            String spo2Pr = (dataBean.getTrendValue(KParamType.SPO2_PR) ==
                    GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .SPO2_PR) / GlobalConstant.TREND_FACTOR);
            String nibpSys = (dataBean.getTrendValue(KParamType.NIBP_SYS) ==
                    GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .NIBP_SYS) / GlobalConstant.TREND_FACTOR);
            String nibpDia = (dataBean.getTrendValue(KParamType.NIBP_DIA) ==
                    GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .NIBP_DIA) / GlobalConstant.TREND_FACTOR);
            String nibpMap = (dataBean.getTrendValue(KParamType.NIBP_MAP) ==
                    GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .NIBP_MAP) / GlobalConstant.TREND_FACTOR);
            String uri_sg = (dataBean.getTrendValue(KParamType.URINERT_SG)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf((float) dataBean.getTrendValue
                    (KParamType.URINERT_SG) / 1000.0);
            String uri_leu = (dataBean.getTrendValue(KParamType.URINERT_LEU)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_LEU) / GlobalConstant.URITREND_FACTOR);
            String uri_nit = (dataBean.getTrendValue(KParamType.URINERT_NIT)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_NIT) / GlobalConstant.URITREND_FACTOR);
            String uri_ubg = (dataBean.getTrendValue(KParamType.URINERT_UBG)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_UBG) / GlobalConstant.URITREND_FACTOR);
            String uri_ph = (dataBean.getTrendValue(KParamType.URINERT_PH)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf((float) dataBean.getTrendValue
                    (KParamType.URINERT_PH) / 100.0);
            String uri_bld = (dataBean.getTrendValue(KParamType.URINERT_BLD)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_BLD) / GlobalConstant.URITREND_FACTOR);
            String uri_glu = (dataBean.getTrendValue(KParamType.URINERT_GLU)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_GLU) / GlobalConstant.URITREND_FACTOR);
            String uri_asc = (dataBean.getTrendValue(KParamType.URINERT_ASC)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_ASC) / GlobalConstant.URITREND_FACTOR);
            String uri_ket = (dataBean.getTrendValue(KParamType.URINERT_KET)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_KET) / GlobalConstant.URITREND_FACTOR);
            String uri_bil = (dataBean.getTrendValue(KParamType.URINERT_BIL)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_BIL) / GlobalConstant.URITREND_FACTOR);
            String uri_pro = (dataBean.getTrendValue(KParamType.URINERT_PRO)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_PRO) / GlobalConstant.URITREND_FACTOR);
            String uri_alb = (dataBean.getTrendValue(KParamType.URINERT_ALB)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_ALB) / GlobalConstant.URITREND_FACTOR);
            String uri_cre = (dataBean.getTrendValue(KParamType.URINERT_CRE)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_CRE) / GlobalConstant.URITREND_FACTOR);
            String uri_ca = (dataBean.getTrendValue(KParamType.URINERT_CA)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : valueToString(dataBean.getTrendValue(KParamType
                    .URINERT_CA) / GlobalConstant.URITREND_FACTOR);
            String blood_glu = (dataBean.getTrendValue(KParamType
                    .BLOODGLU_BEFORE_MEAL) == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf((float) dataBean.getTrendValue
                    (KParamType.BLOODGLU_BEFORE_MEAL) / GlobalConstant
                    .TREND_FACTOR);
            String blood_wbc = (dataBean.getTrendValue(KParamType.BLOOD_WBC)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf((float) dataBean.getTrendValue
                    (KParamType.BLOOD_WBC) / GlobalConstant.TREND_FACTOR);
            measureTime = UiUitls.getDateFormat(UiUitls.DateState.LONG).format(dataMeasure
                    .getMeasureTime());
            String temp = (dataBean.getTrendValue(KParamType.IRTEMP_TREND)
                    == GlobalConstant.INVALID_DATA)
                    ? UiUitls.getString(R.string.default_value)
                    : String.valueOf(dataBean.getTrendValue(KParamType
                    .IRTEMP_TREND) / 100f);

            //打印个人信息
            x = persion_info_x;
            y = persion_info_y;
            canvas.drawText(UiUitls.getContent().getString(R.string.p_name),
                    x, y, paint);
            x += 50;
            canvas.drawText(tvName.getText()
                    .toString(), x, y, paint);
            x += 100;
            canvas.drawText(UiUitls.getContent().getString(R.string.p_idcard),
                    x, y, paint);
            x += 80;
            canvas.drawText(idcard, x, y, paint);
            x = persion_info_x;
            y = persion_info_y + row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_measuretime), x, y, paint);
            x += 80;
            canvas.drawText(measureTime, x, y, paint);

            //打印测量结果
            x = measure_result_x;
            y = measure_result_y;
            //打印心电测量结果
            canvas.drawLine(measure_result_x,
                    measure_result_y - 20,
                    measure_result_x + 500,
                    measure_result_y - 20,
                    paint);
            canvas.drawLine(measure_result_x,
                    measure_result_y - 20,
                    measure_result_x + 500,
                    measure_result_y - 20,
                    paint);
            canvas.drawLine(measure_result_x,
                    measure_result_y - 20,
                    measure_result_x + 500,
                    measure_result_y - 20,
                    paint);
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_xinnv), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_QT), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_pr), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_qrs_t), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_qrs), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .rv5_sv1), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .rv5__sv1), x, y, paint);
            x += 80;
            y += row_interval;
            canvas.drawText(UiUitls.getContent().getString(R.string
                    .p_diagnose), x, y, paint);

            //打印血氧测量结果
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_xueyang), x, y, paint);
//            y += row_interval;
//            canvas.drawText(context.getString(R.string.p_mainv), x, y, paint);

            y += row_interval;
            canvas.drawText(context.getString(R.string.p_shousuoya), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_shuzhangya), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_pingjunya), x, y, paint);

            y += row_interval;
            canvas.drawText(context.getString(R.string.p_temprature), x, y, paint);

            y += row_interval;
            canvas.drawText(context.getString(R.string.p_blood_glu), x, y, paint);

            y += row_interval;
            canvas.drawText(context.getString(R.string.p_baixibao), x, y, paint);

            y += row_interval;
            canvas.drawText(context.getString(R.string.p_leu), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_bld), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_nit), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_ket), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_ubg), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_bil), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_pro), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_glu), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_ph), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_vc), x, y, paint);
            y += row_interval;
            canvas.drawText(context.getString(R.string.p_sg), x, y, paint);

            x = measure_result_x + column_interval;
            y = measure_result_y;
            //打印心电测量结果
            canvas.drawText(ecghr, x, y, paint);

            if (!"-?-".equals(ecgDiagnoseResult)) {
                y += row_interval;
                canvas.drawText("3/4", x, y, paint);
                y += row_interval;
                canvas.drawText("1", x, y, paint);
                y += row_interval;
                canvas.drawText("5/6/7", x, y, paint);
                y += row_interval;
                canvas.drawText("2", x, y, paint);
                y += row_interval;
                canvas.drawText("0.08/0.09", x, y, paint);
                y += row_interval;
                canvas.drawText("0.17", x, y, paint);
            } else {
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
                y += row_interval;
                canvas.drawText("-?-", x, y, paint);
            }
            y += row_interval;
            canvas.drawText(ecgDiagnoseResult, x, y, paint);

            //打印血氧测量结果
            y += row_interval;
            canvas.drawText(spo2Trend, x, y, paint);
//            y += row_interval;
//            canvas.drawText(spo2Pr, x, y, paint);

            y += row_interval;
            canvas.drawText(nibpSys, x, y, paint);
            y += row_interval;
            canvas.drawText(nibpDia, x, y, paint);
            y += row_interval;
            canvas.drawText(nibpMap, x, y, paint);

            y += row_interval;
            canvas.drawText(temp, x, y, paint);

            y += row_interval;
            canvas.drawText(blood_glu, x, y, paint);

            y += row_interval;
            canvas.drawText(blood_wbc, x, y, paint);

            y += row_interval;
            canvas.drawText(uri_leu, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_bld, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_nit, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_ket, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_ubg, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_bil, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_pro, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_glu, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_ph, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_asc, x, y, paint);
            y += row_interval;
            canvas.drawText(uri_sg, x, y, paint);

            //打印单位
            x = measure_result_x + column_interval + column_interval;
            y = measure_result_y;
            canvas.drawText("bpm", x, y, paint);
            y += row_interval;
            canvas.drawText("ms", x, y, paint);
            y += row_interval;
            canvas.drawText("ms", x, y, paint);
            y += row_interval;
            canvas.drawText("°", x, y, paint);
            y += row_interval;
            canvas.drawText("ms", x, y, paint);
            y += row_interval;
            canvas.drawText("mV", x, y, paint);
            y += row_interval;
            canvas.drawText("mV", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);

            //打印血氧测量结果
            y += row_interval;
            canvas.drawText("%", x, y, paint);
            y += row_interval;
            canvas.drawText("bmp", x, y, paint);

            y += row_interval;
            canvas.drawText("mmHg", x, y, paint);
            y += row_interval;
            canvas.drawText("mmHg", x, y, paint);
            y += row_interval;
            canvas.drawText("mmHg", x, y, paint);

            y += row_interval;
            canvas.drawText("℃", x, y, paint);

            y += row_interval;
            canvas.drawText("mmol/L", x, y, paint);

            y += row_interval;
            canvas.drawText("10^9/L", x, y, paint);

            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
            y += row_interval;
            canvas.drawText("", x, y, paint);
        }
    }

    //通过奔图打印机打印
    public void drawOnBitmap() {
        pageOneY = 520;
        pageTwoY = 520;
        printList.clear();
        //画图
        if (checkPage()) {
            overOnePage = true;
            if (null == bitmapPort) {
                InputStream resourceAsStream1 = UiUitls.getContent().getClass()
                        .getClassLoader().getResourceAsStream("assets/report.bmp");
                bitmapPort = BitmapFactory.decodeStream(resourceAsStream1)
                        .copy(Bitmap.Config.RGB_565, true);
                drawReportOnBitmap(bitmapPort, null);
            }
            if (null == bitmapPort2) {
                InputStream resourceAsStream2 = UiUitls.getContent().getClass()
                        .getClassLoader().getResourceAsStream("assets/report2.bmp");
                bitmapPort2 = BitmapFactory.decodeStream(resourceAsStream2)
                        .copy(Bitmap.Config.RGB_565, true);
                drawReportOnBitmap2(bitmapPort2, null);
            }
        } else {
            overOnePage = false;
            if (null == bitmapPort) {
                InputStream resourceAsStream1 = UiUitls.getContent().getClass()
                        .getClassLoader().getResourceAsStream("assets/report.bmp");
                bitmapPort = BitmapFactory.decodeStream(resourceAsStream1)
                        .copy(Bitmap.Config.RGB_565, true);
                drawReportOnBitmap(bitmapPort, null);
            }
        }
    }

    /**
     * 判断打印内容是否超过一页
     * @return true代表超过一页
     */
    private boolean checkPage() {
        int paramValue = dataMeasure.getParamValue();
        int height = 520;
        if ((paramValue & (0x01 << 0)) != 0) {
            if (height + HR_HEIGHT < END_Y) {
                printList.put(0, true);
            } else {
                printList.put(0, false);
            }
            height += HR_HEIGHT;
        }
        if ((paramValue & (0x01 << 1)) != 0) {
            if (height + SPO2_HEIGHT < END_Y) {
                printList.put(1, true);
            } else {
                printList.put(1, false);
            }
            height += SPO2_HEIGHT;
        }
        if ((paramValue & (0x01 << 2)) != 0) {
            if (height + NIBP_HEIGHT < END_Y) {
                printList.put(2, true);
            } else {
                printList.put(2, false);
            }
            height += NIBP_HEIGHT;
        }
        if ((paramValue & (0x01 << 3)) != 0) {
            if (height + TEMP_HEIGHT < END_Y) {
                printList.put(3, true);
            } else {
                printList.put(3, false);
            }
            height += TEMP_HEIGHT;
        }
        if ((paramValue & (0x01 << 4)) != 0) {
            if (height + GLU_HEIGHT < END_Y) {
                printList.put(4, true);
            } else {
                printList.put(4, false);
            }
            height += GLU_HEIGHT;
        }
        if ((paramValue & (0x01 << 5)) != 0) {
            if (height + URT_11_HEIGHT < END_Y) {
                printList.put(5, true);
            } else {
                printList.put(5, false);
            }
            height += URT_11_HEIGHT;
        }
        if ((paramValue & (0x01 << 6)) != 0) {
            if (height + URT_14_HEIGHT < END_Y) {
                printList.put(6, true);
            } else {
                printList.put(6, false);
            }
            height += URT_14_HEIGHT;
        }
        if ((paramValue & (0x01 << 7)) != 0) {
            if (height + HGB_HEIGHT < END_Y) {
                printList.put(7, true);
            } else {
                printList.put(7, false);
            }
            height += HGB_HEIGHT;
        }
        if ((paramValue & (0x01 << 8)) != 0) {
            if (height + LIPIDS_HEIGHT < END_Y) {
                printList.put(8, true);
            } else {
                printList.put(8, false);
            }
            height += LIPIDS_HEIGHT;
        }
        if ((paramValue & (0x01 << 9)) != 0) {
            if (height + SUGAR_HGB_HEIGHT < END_Y) {
                printList.put(9, true);
            } else {
                printList.put(9, false);
            }
            height += SUGAR_HGB_HEIGHT;
        }
        if ((paramValue & (0x01 << 10)) != 0) {
            if (height + BMI_HEIGHT < END_Y) {
                printList.put(10, true);
            } else {
                printList.put(10, false);
            }
            height += BMI_HEIGHT;
        }
        if ((paramValue & (0x01 << 11)) != 0) {
            if (height + WBC_HEIGHT < END_Y) {
                printList.put(11, true);
            } else {
                printList.put(11, false);
            }
            height += WBC_HEIGHT;
        }
        if (height + RESULT_ADVICE_HEIGHT < END_Y) {
            printList.put(12, true);
        } else {
            printList.put(12, false);
        }
        height += RESULT_ADVICE_HEIGHT;
        return height > END_Y;
    }

    /**
     * 获取待打印的数据
     */
    private void initReportData() {
        //获取待打印的数据
        dataBean = dataMeasure;
        ecghr = (dataBean.getTrendValue(KParamType.ECG_HR) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.ECG_HR) /
                GlobalConstant.TREND_FACTOR);
        ecgbr = (dataBean.getTrendValue(KParamType.RESP_RR) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.RESP_RR)
                / GlobalConstant.TREND_FACTOR);
        ecgDiagnoseResult = (dataBean.getEcgDiagnoseResult()
                .length() == 0)
                ? UiUitls.getString(R.string.empty_value)
                : dataBean.getEcgDiagnoseResult();
        spo2Trend = (dataBean.getTrendValue(KParamType.SPO2_TREND) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType
                .SPO2_TREND) / GlobalConstant.TREND_FACTOR);
        spo2Pr = (dataBean.getTrendValue(KParamType.SPO2_PR) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.SPO2_PR)
                / GlobalConstant.TREND_FACTOR);
        nibpSys = (dataBean.getTrendValue(KParamType.NIBP_SYS) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.NIBP_SYS)
                / GlobalConstant.TREND_FACTOR);
        nibpDia = (dataBean.getTrendValue(KParamType.NIBP_DIA) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.NIBP_DIA)
                / GlobalConstant.TREND_FACTOR);
        nibpMap = (dataBean.getTrendValue(KParamType.NIBP_MAP) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.NIBP_MAP)
                / GlobalConstant.TREND_FACTOR);
        nibpPr = (dataBean.getTrendValue(KParamType.NIBP_MAP) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType.NIBP_PR)
                / GlobalConstant.TREND_FACTOR);
        uri_sg = (dataBean.getTrendValue(KParamType.URINERT_SG) ==
                -1000) ? UiUitls.getString(R.string.invalid_value)
                : String.format("%.3f", (double) dataBean.getTrendValue
                (KParamType.URINERT_SG) / 1000.0);
        uri_leu = (dataBean.getTrendValue(KParamType.URINERT_LEU) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_LEU) / GlobalConstant.URITREND_FACTOR);
        uri_nit = (dataBean.getTrendValue(KParamType.URINERT_NIT) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_NIT) / GlobalConstant.URITREND_FACTOR);
        uri_ubg = (dataBean.getTrendValue(KParamType.URINERT_UBG) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_UBG) / GlobalConstant.URITREND_FACTOR);
        uri_ph = (dataBean.getTrendValue(KParamType.URINERT_PH) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .URINERT_PH) / 100.0);
        uri_bld = (dataBean.getTrendValue(KParamType.URINERT_BLD) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_BLD) / GlobalConstant.URITREND_FACTOR);
        uri_glu = (dataBean.getTrendValue(KParamType.URINERT_GLU) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_GLU) / GlobalConstant.URITREND_FACTOR);
        uri_asc = (dataBean.getTrendValue(KParamType.URINERT_ASC) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_ASC) / GlobalConstant.URITREND_FACTOR);
        uri_ket = (dataBean.getTrendValue(KParamType.URINERT_KET) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_KET) / GlobalConstant.URITREND_FACTOR);
        uri_bil = (dataBean.getTrendValue(KParamType.URINERT_BIL) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_BIL) / GlobalConstant.URITREND_FACTOR);
        uri_pro = (dataBean.getTrendValue(KParamType.URINERT_PRO) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : valueToString(dataBean.getTrendValue(KParamType
                .URINERT_PRO) / GlobalConstant.URITREND_FACTOR);
        uri_alb = (dataBean.getTrendValue(KParamType.URINERT_ALB) == GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value) : valueToString(dataBean.getTrendValue
                (KParamType.URINERT_ALB) / GlobalConstant.URITREND_FACTOR);
        uri_cre = (dataBean.getTrendValue(KParamType.URINERT_CRE) == GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value) : valueToString(dataBean
                .getTrendValue(KParamType.URINERT_CRE) / GlobalConstant.URITREND_FACTOR);
        uri_ca = (dataBean.getTrendValue(KParamType.URINERT_CA) == GlobalConstant.INVALID_DATA) ?
                UiUitls.getString(R.string.invalid_value) : valueToString(dataBean.getTrendValue
                (KParamType.URINERT_CA) / GlobalConstant.URITREND_FACTOR);
        blood_glu = (dataBean.getTrendValue(KParamType
                .BLOODGLU_BEFORE_MEAL) == GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .BLOODGLU_BEFORE_MEAL) / GlobalConstant.TREND_FACTOR);
        //尿酸
        nsValue = dataMeasure.getTrendValue(KParamType
                .URICACID_TREND) == -1000 ? UiUitls.getString(R.string.invalid_value) :
                String.valueOf(dataMeasure.getTrendValue(KParamType.URICACID_TREND) / 100.0f);
        //总胆固醇
        cholValue = dataMeasure.getTrendValue(KParamType
                .CHOLESTEROL_TREND) == -1000 ? UiUitls.getString(R.string.invalid_value) :
                String.valueOf(dataMeasure.getTrendValue(KParamType.CHOLESTEROL_TREND) / 100.0f);

        blood_wbc = (dataBean.getTrendValue(KParamType.BLOOD_WBC) ==
                GlobalConstant.INVALID_DATA) ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .BLOOD_WBC) / GlobalConstant.WBC_FACTOR);
        blood_hgb = (dataBean.getTrendValue(KParamType.BLOOD_HGB) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .BLOOD_HGB) / GlobalConstant.TREND_FACTOR);
        blood_hct = (dataBean.getTrendValue(KParamType.BLOOD_HCT) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType
                .BLOOD_HCT) / GlobalConstant.TREND_FACTOR);
        uric_acid = (dataBean.getTrendValue(KParamType
                .URICACID_TREND) == GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .URICACID_TREND) / GlobalConstant.TREND_FACTOR);
        cholesterol = (dataBean.getTrendValue(KParamType
                .CHOLESTEROL_TREND) == GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf((float) dataBean.getTrendValue(KParamType
                .CHOLESTEROL_TREND) / GlobalConstant.TREND_FACTOR);
        measureTime = UiUitls.getDateFormat(UiUitls.DateState.LONG).format(dataBean
                .getMeasureTime());
        temp = (dataBean.getTrendValue(KParamType.IRTEMP_TREND) ==
                GlobalConstant.INVALID_DATA)
                ? UiUitls.getString(R.string.invalid_value)
                : String.valueOf(dataBean.getTrendValue(KParamType
                .IRTEMP_TREND) / 100f);
        //获取糖化血红蛋白三个测量值
        ngsp = getFormatterStr(KParamType.HBA1C_NGSP, dataBean.getTrendValue(KParamType
                .HBA1C_NGSP), true);
        ifcc = getFormatterStr(KParamType.HBA1C_IFCC, dataBean.getTrendValue(KParamType
                .HBA1C_IFCC), true);
        eag = getFormatterStr(KParamType.HBA1C_EAG, dataBean.getTrendValue(KParamType.HBA1C_EAG),
                true);
        height = TextUtils.isEmpty(dataBean.getHeight()) ? UiUitls.getString(R.string
                .invalid_value) : dataBean.getHeight();
        weight = TextUtils.isEmpty(dataBean.getWeight()) ? UiUitls.getString(R.string
                .invalid_value) : dataBean.getWeight();
        bmi = TextUtils.isEmpty(dataBean.getBmi()) ? UiUitls.getString(R.string.invalid_value) :
                dataBean.getBmi();

        lipoidemiatc = getFormatterStr(KParamType.LIPIDS_CHOL
                , dataMeasure.getLipoidemiatc(), true);
        lipoidemiatg = getFormatterStr(KParamType.LIPIDS_TRIG
                , dataMeasure.getLipoidemiatg(), true);
        lipoidemialdl = getFormatterStr(KParamType.LIPIDS_LDL
                , dataMeasure.getLipoidemialdl(), true);
        lipoidemiahdl = getFormatterStr(KParamType.LIPIDS_HDL
                , dataMeasure.getLipoidemiahdl(), true);
    }

    /*
    * 清除上次测试报告的值
    * */
    public void cleanBitmapPort() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmapPort == null) {
                    return;
                }
                try {
                    //清除上次绘制的内容
                    for (int i = 0; i < bitmapPort.getWidth(); i++) {
                        for (int j = 0; j < bitmapPort.getHeight(); j++) {
                            bitmapPort.setPixel(i, j, Color.WHITE);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        }).start();
    }

    /*
    * 清除上次测试报告的值
    * */
    public void cleanBitmapPort2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmapPort2 == null) {
                    return;
                }
                try {
                    //清除上次绘制的内容
                    for (int i = 0; i < bitmapPort2.getWidth(); i++) {
                        for (int j = 0; j < bitmapPort2.getHeight(); j++) {
                            bitmapPort2.setPixel(i, j, Color.WHITE);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
        }).start();
    }

    /*
     * 清除上次心电图的值
     * */
    public void cleanBitmapEcg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmapEcg == null) {
                    return;
                }
                try {
                    //清除上次绘制的内容
                    for (int i = 0; i < bitmapEcg.getWidth(); i++) {
                        for (int j = 0; j < bitmapEcg.getHeight(); j++) {
                            bitmapEcg.setPixel(i, j, Color.WHITE);
                        }
                    }
                } catch (NullPointerException e) {
                    CrashReport.postCatchedException(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public byte[] getBmpToByte(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        byte[] rgb = addBMP_RGB_888(pixels, w, h);
        byte[] header = addBMPImageHeader(rgb.length);
        byte[] infos = addBMPImageInfosHeader(rgb.length, w, h);

        byte[] buffer = new byte[54 + rgb.length];
        System.arraycopy(header, 0, buffer, 0, header.length);
        System.arraycopy(infos, 0, buffer, 14, infos.length);
        System.arraycopy(rgb, 0, buffer, 54, rgb.length);

        return buffer;
    }

    private byte[] addBMP_RGB_888(int[] b, int w, int h) {
        int len = b.length;
        System.out.println(b.length);
        byte[] buffer = new byte[w * h * 3];
        int offset = 0;

        for (int i = len - 1; i >= w - 1; i -= w) {
            int end = i, start = i - w + 1;
            for (int j = start; j <= end; j++) {
                buffer[offset] = (byte) (b[j]);
                buffer[offset + 1] = (byte) (b[j] >> 8);
                buffer[offset + 2] = (byte) (b[j] >> 16);
                offset += 3;
            }
        }

        return buffer;
    }

    private byte[] addBMPImageHeader(int size) {
        byte[] buffer = new byte[14];
        buffer[0] = 0x42;
        buffer[1] = 0x4D;
        buffer[2] = (byte) ((size + 54) >> 0);
        buffer[3] = (byte) ((size + 54) >> 8);
        buffer[4] = (byte) ((size + 54) >> 16);
        buffer[5] = (byte) ((size + 54) >> 24);
        buffer[6] = 0x00;
        buffer[7] = 0x00;
        buffer[8] = 0x00;
        buffer[9] = 0x00;
        buffer[10] = 0x36;
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        return buffer;
    }

    private byte[] addBMPImageInfosHeader(int size, int w, int h) {
        byte[] buffer = new byte[40];

        // 这个是固定的 BMP 信息头要40个字节
        buffer[0] = 0x28;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = 0x00;

        // 宽度 地位放在序号前的位置 高位放在序号后的位置
        buffer[4] = (byte) (w >> 0);
        buffer[5] = (byte) (w >> 8);
        buffer[6] = (byte) (w >> 16);
        buffer[7] = (byte) (w >> 24);

        // 长度 同上
        buffer[8] = (byte) (h >> 0);
        buffer[9] = (byte) (h >> 8);
        buffer[10] = (byte) (h >> 16);
        buffer[11] = (byte) (h >> 24);

        // 总是被设置为1
        buffer[12] = 0x01;
        buffer[13] = 0x00;

        // 比特数 像素 32位保存一个比特 这个不同的方式(ARGB 32位 RGB24位不同的!!!!
        buffer[14] = 0x18;
        buffer[15] = 0x00;

        // 0-不压缩 1-8bit位图
        // 2-4bit位图 3-16/32位图
        // 4 jpeg 5 png
        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = 0x00;
        buffer[19] = 0x00;

        // 说明图像大小
        buffer[20] = (byte) size;
        buffer[21] = (byte) (size >> 8);
        buffer[22] = (byte) (size >> 16);
        buffer[23] = (byte) (size >> 24);

        // 水平分辨率
        buffer[24] = 0x00;
        buffer[25] = 0x00;
        buffer[26] = 0x00;
        buffer[27] = 0x00;

        // 垂直分辨率
        buffer[28] = 0x00;
        buffer[29] = 0x00;
        buffer[30] = 0x00;
        buffer[31] = 0x00;

        // 0 使用所有的调色板项
        buffer[32] = 0x00;
        buffer[33] = 0x00;
        buffer[34] = 0x00;
        buffer[35] = 0x00;

        // 不开颜色索引
        buffer[36] = 0x00;
        buffer[37] = 0x00;
        buffer[38] = 0x00;
        buffer[39] = 0x00;
        return buffer;
    }

    private int unPack(String printDataIn) {
        int PackageResult = 0;

        if (printDataIn.length() <= 10240) {
            char[] printDataArray = printDataIn.toCharArray();

            int printDataSize = printDataArray.length;
            Runtime.getRuntime()
                    .gc();
            PackageResult = loadjni.combinationPackage(printDataArray,
                    printDataSize,
                    printDataSize,
                    printDataSize);
        } else {
            int countsend = 0;
            for (countsend = 0; printDataIn.length() / 10240 > countsend;
                    countsend++) {

                char[] printDataArray = printDataIn.substring(10240 *
                                (countsend),
                        10240 * (countsend + 1))
                        .toCharArray();
                int printDataSize = printDataArray.length;
                PackageResult += loadjni.combinationPackage(printDataArray,
                        printDataSize,
                        printDataSize,
                        printDataIn.length());
            }
            if (printDataIn.length() % 10240 != 0) {
                char[] printDataArray = printDataIn.substring((printDataIn
                                .length() / 10240) * 10240,
                        printDataIn.length())
                        .toCharArray();
                int printDataSize = printDataArray.length;
                PackageResult += loadjni.combinationPackage(printDataArray,
                        printDataSize,
                        printDataSize,
                        printDataIn.length());
            }
        }
        return PackageResult;
    }

    public final String dir = "Text2Img";
    public final String ACCESS_ERROR = "access_error";
    public final String FILE_ERROR = "file_error";
    public final String SAVE_ERROR = "save_error";

    public String saveFileHighQuality(Bitmap bm, String path) {
        File file = new File(path);
        //        File file = new File("/sdcard/data/report.bmp");
        if (!file.getParentFile()
                .exists()) {
            file.getParentFile()
                    .mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return FILE_ERROR;
        }

        if (bm == null) {
            return FILE_ERROR;
        }
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);

        byte[] rgb = addBMP_RGB_888(pixels, w, h);
        byte[] header = addBMPImageHeader(rgb.length);
        byte[] infos = addBMPImageInfosHeader(rgb.length, w, h);

        byte[] buffer = new byte[54 + rgb.length];
        System.arraycopy(header, 0, buffer, 0, header.length);
        System.arraycopy(infos, 0, buffer, 14, infos.length);
        System.arraycopy(rgb, 0, buffer, 54, rgb.length);
        try {
            out.write(buffer);
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return SAVE_ERROR;
        }
        return file.getPath();
    }

    /**
     * 画体检报告到一个bitmap中
     * @param bitmap 装载体检报告的bitmap容器
     * @param bean 体检报告中的数据
     */
    private void drawReportOnBitmap(Bitmap bitmap, EcgDiagnoseBean bean) {

        initReportData();

        Canvas canvas = new Canvas(bitmap);

        int x;//x方向坐标
        int y;//y方向坐标
        //打印标题的起始位置
        final int title_x = 700;
        final int title_y = 120;
        //打印个人图标的起始位置
        final int persion_pic_x = 140;
        final int persion_pic_y = 270;
        //打印测量结果的起始位置
        measure_result_x = 104;
        measure_result_y = persion_pic_y + row_interval * 2;

        // 画布宽度
        bitmapWidthwidth = bitmap.getWidth();

        //设置画笔
        Paint paint = new Paint();

        //打印报告图标
        Bitmap hosPic = BitmapFactory.decodeResource(getResources(), R
                .drawable.pic_hos);
        canvas.drawBitmap(hosPic, title_x, title_y - 120, null);

        //打印第一行标题
        paint.setColor(Color.BLACK);
        paint.setTextSize(75);
        x = title_x + 200;
        y = title_y;

//        canvas.drawText(UiUitls.getString(R.string.x_district_temp), x, y,
//                paint);
        //打印第二行标题
        x = 1000;
        y = title_y + 10;
        canvas.drawText(UiUitls.getString(R.string.measure_report_title), x, y,
                paint);

        paint.setStrokeWidth((float) 5);
        canvas.drawLine(measure_result_x, y + 60,
                bitmapWidthwidth - measure_result_x, y + 60,
                paint);

        //照片终点位置
        int picEndX = 344;
        int picEndY = 522;
        //打印照片
        if (personPic != null) {
            canvas.drawBitmap(personPic, null, new Rect(persion_pic_x,
                    persion_pic_y - 25, picEndX, picEndY - 25), null);
        }

        //第一行个人信息
        //打印个人信息的起始位置
        paint.setTextSize(48);
        final int persion_info_x = 250;
        final int persion_info_y = 350;
        x = persion_info_x;
        y = persion_info_y;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_name), x, y,
                paint);
        x += 150;
        canvas.drawText(tvName.getText()
                .toString(), x, y, paint);
        x += 550;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_sex), x, y,
                paint);
        x += 150;
        canvas.drawText(UiUitls.getSexString(patientBean
                .getSex()), x, y, paint);

        x += 350;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_measure_time),
                x, y, paint);
        x += 240;
        canvas.drawText(measureTime, x, y, paint);

        //第二行个人信息
        x = persion_info_x;
        y = persion_info_y + 100;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_age),
                x, y, paint);
        x += 150;
        canvas.drawText(patientAge, x, y, paint);
        x += 100;
        canvas.drawText(UiUitls.getString(R.string.age), x, y, paint);

        x += 450;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_doc),
                x, y, paint);
        x += 150;
        canvas.drawText("" + dataBean.getDoctor(), x, y, paint);

        printMeasureData(canvas, paint, true);

        x = measure_result_x;
        y = 3348;
        reportTime = UiUitls.getDateFormat(UiUitls.DateState.LONG).format(new Date());
        canvas.drawText(UiUitls.getContent().getString(R.string.p_report_print), x, y, paint);
        x += 250;
        canvas.drawText("" + reportTime, x, y, paint);

        x += 1700;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_page1),
                x, y, paint);

        x = measure_result_x;
        paint.setStrokeWidth((float) 1);
        canvas.drawLine(measure_result_x, y - 60,
                bitmapWidthwidth - measure_result_x, y - 60,
                paint);
        // ### 生成位图文件 ###
        saveBitmapFile(bitmap, GlobalConstant.HEALTH_REPORT_NAME, 100);
        bitmapPort = null; //释放资源
    }

    /**
     * 打印测量项
     * @param canvas 画布
     * @param paint 画笔
     * @param pageOne 是否是第一页（true代表打印在第一页，false代表打印在第二页）
     */
    private void printMeasureData(Canvas canvas, Paint paint, boolean pageOne) {
        if (printList.containsKey(0)) {
            //打印心率测量项
            int x = 0;
            int y;
            if (printList.get(0) && pageOne) {
                //打印在第一页，每次打印记录高度的Y坐标对应增加
                y = pageOneY;
                pageOneY += HR_HEIGHT;
                printHr(canvas, paint, x, y);
            } else if (!printList.get(0) && !pageOne) {
                //打印在第二页
                y = pageTwoY;
                pageTwoY += HR_HEIGHT;
                printHr(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(1)) {
            //打印血氧测量项
            int x = measure_result_x;
            int y;
            if (printList.get(1) && pageOne) {
                y = pageOneY;
                pageOneY += SPO2_HEIGHT;
                printSpo2(canvas, paint, x, y);
            } else if (!printList.get(1) && !pageOne) {
                y = pageTwoY;
                pageTwoY += SPO2_HEIGHT;
                printSpo2(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(2)) {
            //打印血压测量结果
            int x = measure_result_x;
            int y;
            if (printList.get(2) && pageOne) {
                y = pageOneY;
                pageOneY += NIBP_HEIGHT;
                printNibp(canvas, paint, x, y);
            } else if (!printList.get(2) && !pageOne) {
                y = pageTwoY;
                pageTwoY += NIBP_HEIGHT;
                printNibp(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(3)) {
            //打印体温测量结果
            int x = 0;
            int y;
            if (printList.get(3) && pageOne) {
                y = pageOneY;
                pageOneY += TEMP_HEIGHT;
                printTemp(canvas, paint, x, y);
            } else if (!printList.get(3) && !pageOne) {
                y = pageTwoY;
                pageTwoY += TEMP_HEIGHT;
                printTemp(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(4)) {
            int x = 0;
            int y;
            if (printList.get(4) && pageOne) {
                y = pageOneY;
                pageOneY += GLU_HEIGHT;
                printGlu(canvas, paint, x, y);
            } else if (!printList.get(4) && !pageOne) {
                y = pageTwoY;
                pageTwoY += GLU_HEIGHT;
                printGlu(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(5)) {
            int x = 0;
            int y;
            if (printList.get(5) && pageOne) {
                y = pageOneY;
                pageOneY += URT_11_HEIGHT;
                printUrt11(canvas, paint, x, y);
            } else if (!printList.get(5) && !pageOne) {
                y = pageTwoY;
                pageTwoY += URT_11_HEIGHT;
                printUrt11(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(6)) {
            int x = 0;
            int y;
            if (printList.get(6) && pageOne) {
                y = pageOneY;
                pageOneY += URT_14_HEIGHT;
                printUrt14(canvas, paint, x, y);
            } else if (!printList.get(6) && !pageOne) {
                y = pageTwoY;
                pageTwoY += URT_14_HEIGHT;
                printUrt14(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(7)) {
            //打印血红蛋白
            int x = 0;
            int y;
            if (printList.get(7) && pageOne) {
                y = pageOneY;
                pageOneY += HGB_HEIGHT;
                printHgb(canvas, paint, x, y);
            } else if (!printList.get(7) && !pageOne) {
                y = pageTwoY;
                pageTwoY += HGB_HEIGHT;
                printHgb(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(8)) {
            //打印血脂四项测量结果
            int x = 0;
            int y;
            if (printList.get(8) && pageOne) {
                y = pageOneY;
                pageOneY += LIPIDS_HEIGHT;
                printLipids(canvas, paint, x, y);
            } else if (!printList.get(8) && !pageOne) {
                y = pageTwoY;
                pageTwoY += LIPIDS_HEIGHT;
                printLipids(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(9)) {
            int x = measure_result_x;
            int y;
            if (printList.get(9) && pageOne) {
                y = pageOneY;
                pageOneY += SUGAR_HGB_HEIGHT;
                printSugarHgb(canvas, paint, x, y);
            } else if (!printList.get(9) && !pageOne) {
                y = pageTwoY;
                pageTwoY += SUGAR_HGB_HEIGHT;
                printSugarHgb(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(10)) {
            int x = measure_result_x;
            int y;
            if (printList.get(10) && pageOne) {
                y = pageOneY;
                pageOneY += BMI_HEIGHT;
                printBmi(canvas, paint, x, y);
            } else if (!printList.get(10) && !pageOne) {
                y = pageTwoY;
                pageTwoY += BMI_HEIGHT;
                printBmi(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(11)) {
            int x = measure_result_x;
            int y;
            if (printList.get(11) && pageOne) {
                y = pageOneY;
                pageOneY += WBC_HEIGHT;
                printWbc(canvas, paint, x, y);
            } else if (!printList.get(11) && !pageOne) {
                y = pageTwoY;
                pageTwoY += WBC_HEIGHT;
                printWbc(canvas, paint, x, y);
            }
        }
        if (printList.containsKey(12)) {
            //打印结论与建议
            int x = 0;
            int y;
            if (printList.get(12) && pageOne) {
                y = pageOneY;
                pageOneY += RESULT_ADVICE_HEIGHT;
                printResultAdvice(canvas, paint, x, y);
            } else if (!printList.get(12) && !pageOne) {
                y = pageTwoY;
                pageTwoY += RESULT_ADVICE_HEIGHT;
                printResultAdvice(canvas, paint, x, y);
            }
        }
    }

    /**
     * 为了居中显示，计算需要打印的字符串起点X坐标
     * @param mid 中线
     * @param text 字符串
     * @param paint 画笔
     * @return text起点x坐标
     */
    private int getXStart(int mid, String text, Paint paint) {
        float textLength = paint.measureText(text);
        return (int) (mid - textLength / 2);
    }

    /**
     * 打印心率
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printHr(Canvas canvas, Paint paint, int x, int y) {
        //打印心率测量结果
        drawReportTitle(UiUitls.getContent().getString(R.string.health_heart_pr), x, y, canvas,
                paint, true);
        x = measure_result_x + 80;
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.health_heart_pr), x, y, paint);
        x = getXStart(MID_OF_RESULT, ecghr, paint);
        canvas.drawText(ecghr, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_bpm), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bpm), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string
                .hr_reference_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.hr_reference_range), x, y, paint);
        canvas.drawText(getReferenceStr(ecghr, GlobalConstant.HR_ALARM_LOW, GlobalConstant
                .HR_ALARM_HIGH), ALARM_X, y, paint);
    }

    /**
     * 打印血氧
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printSpo2(Canvas canvas, Paint paint, int x, int y) {
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_xueyang), x, y, canvas, paint,
                true);
        x += 80;
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_xueyang), x, y, paint);
        x = getXStart(MID_OF_RESULT, spo2Trend, paint);
        canvas.drawText(spo2Trend, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_percentage),
                paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_percentage), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_94100), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_94100), x, y, paint);
        canvas.drawText(getReferenceStr(spo2Trend, 94, 100), ALARM_X, y, paint);
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_mainv), x, y, paint);
        x = getXStart(MID_OF_RESULT, spo2Pr, paint);
        canvas.drawText(spo2Pr, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_bpm), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bpm), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string
                .hr_reference_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.hr_reference_range), x, y, paint);
        canvas.drawText(getReferenceStr(spo2Pr, GlobalConstant.HR_ALARM_LOW, GlobalConstant
                .HR_ALARM_HIGH), ALARM_X, y, paint);
    }

    /**
     * 打印血压
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printNibp(Canvas canvas, Paint paint, int x, int y) {
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_xueya), x, y, canvas, paint,
                true);
        x = measure_result_x + 80;
        y += 220;
        paint.setTextSize(48);

        canvas.drawText(UiUitls.getContent().getString(R.string.p_shousuoya), x, y, paint);
        x = getXStart(MID_OF_RESULT, nibpSys, paint);
        canvas.drawText(nibpSys, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmhg), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmhg), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_90140), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_90140), x, y, paint);
        canvas.drawText(getReferenceStr(nibpSys, 90, 140), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_shuzhangya), x, y, paint);
        x = getXStart(MID_OF_RESULT, nibpDia, paint);
        canvas.drawText(nibpDia, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmhg), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmhg), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_5090), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_5090), x, y, paint);
        canvas.drawText(getReferenceStr(nibpDia, 60, 90), ALARM_X, y, paint);
//      TODO 测试要求不显示脉率，保留代码逻辑
//        x = measure_result_x + 80;
//        y += row_interval;
//        canvas.drawText(UiUitls.getContent().getString(R.string.p_mainv), x, y, paint);
//        x = getXStart(MID_OF_RESULT, nibpPr, paint);
//        canvas.drawText(nibpPr, x, y, paint);
//        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_bpm), paint);
//        canvas.drawText(UiUitls.getContent().getString(R.string.p_bpm), x, y, paint);
//        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string
//                .hr_reference_range), paint);
//        canvas.drawText(UiUitls.getContent().getString(R.string.hr_reference_range), x, y, paint);
//        canvas.drawText(getReferenceStr(nibpPr, GlobalConstant.HR_ALARM_LOW, GlobalConstant
//                .HR_ALARM_HIGH), ALARM_X, y, paint);
    }

    /**
     * 打印体温
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printTemp(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_temperature), x, y, canvas,
                paint, true);
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_temperature), x, y, paint);
        x = getXStart(MID_OF_RESULT, temp, paint);
        canvas.drawText(temp, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_c), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_c), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_temp_range),
                paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_temp_range), x, y, paint);
        canvas.drawText(getReferenceStr(temp, 36.2f, 37.2f), ALARM_X, y, paint);
    }

    /**
     * 打印血液三项
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printGlu(Canvas canvas, Paint paint, int x, int y) {
        //打印血糖测量结果
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.blood_three),
                x, y, canvas, paint, true);
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(glu, x, y, paint);
        x = getXStart(MID_OF_RESULT, blood_glu, paint);
        canvas.drawText(blood_glu, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol),
                x, y, paint);
        x = getXStart(MID_OF_REFERENCE, getGluReferenceStr(dataMeasure.getGluStyle()), paint);
        canvas.drawText(getGluReferenceStr(dataMeasure.getGluStyle()), x, y, paint);
        // 血糖2.8-7.0是显示范围  4-7才是正常范围
        canvas.drawText(getReferenceStr(blood_glu,
                getGluReferenceValue(dataMeasure.getGluStyle(), false),
                getGluReferenceValue(dataMeasure.getGluStyle(), true)),
                ALARM_X, y, paint);
        //尿酸
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.qc_ns), x, y,
                paint);
        x = getXStart(MID_OF_RESULT, uric_acid, paint);
        canvas.drawText(uric_acid, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, nsReference, paint);
        canvas.drawText(nsReference, x, y, paint);
        canvas.drawText(getReferenceStr(uric_acid, nsMin, nsMax), ALARM_X, y, paint);

        //总胆固醇
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.qc_dgc), x, y,
                paint);
        x = getXStart(MID_OF_RESULT, cholesterol, paint);
        canvas.drawText(cholesterol, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.x_measure_td_reference), paint);
        canvas.drawText(UiUitls.getString(R.string.x_measure_td_reference), x, y, paint);
        canvas.drawText(getReferenceStr(cholesterol, BeneParamValue.CHOL_VALUE_MIN,
                BeneParamValue.CHOL_VALUE_MAX), ALARM_X, y, paint);
    }

    /**
     * 打印尿常规11项
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printUrt11(Canvas canvas, Paint paint, int x, int y) {
        //打印尿常规测量结果
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_ur),
                x, y, canvas, paint, true);
        y += 220;
        paint.setTextSize(48);

        canvas.drawText(UiUitls.getContent().getString(R.string.p_baixibao1), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_leu, paint);
        canvas.drawText(uri_leu, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_leu), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_yaxiaosuanyan), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_nit, paint);
        canvas.drawText(uri_nit, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_nit), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_niaodanyuan), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ubg, paint);
        canvas.drawText(uri_ubg, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ubg), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_danbaizhi), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_pro, paint);
        canvas.drawText(uri_pro, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_pro), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_suanjianzhi), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ph, paint);
        canvas.drawText(uri_ph, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_ph_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_ph_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ph, 5.0f, 8.5f), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bizhong), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_sg, paint);
        canvas.drawText(uri_sg, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_bizhong_range),
                paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bizhong_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_sg, 1.015f, 1.025f), ALARM_X, y, paint);

        //隐血
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.urinert_bld_no), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_bld, paint);
        canvas.drawText(uri_bld, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_bld), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_dongti), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ket, paint);
        canvas.drawText(uri_ket, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ket), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_danhongsu), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_bil, paint);
        canvas.drawText(uri_bil, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_bil), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_putaotang), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_glu, paint);
        canvas.drawText(uri_glu, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_glu), ALARM_X, y, paint);

        //抗坏血酸
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_vc1), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_asc, paint);
        canvas.drawText(uri_asc, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_asc), ALARM_X, y, paint);
    }

    /**
     * 打印尿常规14项
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printUrt14(Canvas canvas, Paint paint, int x, int y) {
        //打印尿常规测量结果
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_ur), x, y, canvas, paint, true);
        y += 220;
        paint.setTextSize(48);

        canvas.drawText(UiUitls.getContent().getString(R.string.p_baixibao1), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_leu, paint);
        canvas.drawText(uri_leu, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_leu), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_yaxiaosuanyan), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_nit, paint);
        canvas.drawText(uri_nit, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_nit), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_niaodanyuan), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ubg, paint);
        canvas.drawText(uri_ubg, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ubg), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_danbaizhi), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_pro, paint);
        canvas.drawText(uri_pro, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_pro), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_suanjianzhi), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ph, paint);
        canvas.drawText(uri_ph, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_ph_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_ph_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ph, 5.0f, 8.5f), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bizhong), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_sg, paint);
        canvas.drawText(uri_sg, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.p_bizhong_range),
                paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_bizhong_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_sg, 1.015f, 1.025f), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText("尿钙（Ca）", x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ca, paint);
        canvas.drawText(uri_ca, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ca), ALARM_X, y, paint);

        //隐血
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.urinert_bld_no), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_bld, paint);
        canvas.drawText(uri_bld, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_bld), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_dongti), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_ket, paint);
        canvas.drawText(uri_ket, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_ket), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_danhongsu), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_bil, paint);
        canvas.drawText(uri_bil, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_bil), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_putaotang), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_glu, paint);
        canvas.drawText(uri_glu, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_glu), ALARM_X, y, paint);

        //维生素C
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_vc1), x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_asc, paint);
        canvas.drawText(uri_asc, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_asc), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText("微量白蛋白（MA）", x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_alb, paint);
        canvas.drawText(uri_alb, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_alb), ALARM_X, y, paint);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText("肌酐（CRE）", x, y, paint);
        x = getXStart(MID_OF_RESULT, uri_cre, paint);
        canvas.drawText(uri_cre, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.urt_default_range), paint);
        canvas.drawText(UiUitls.getString(R.string.urt_default_range), x, y, paint);
        canvas.drawText(getReferenceStr(uri_cre), ALARM_X, y, paint);
    }

    /**
     * 打印血红蛋白
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printHgb(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.hemoglobin), x, y, canvas, paint,
                true);
        x += 80;
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.hemoglobin), x, y, paint);
        x = getXStart(MID_OF_RESULT, blood_hgb, paint);
        canvas.drawText(blood_hgb, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, combine2Str(hgbMin, hgbMax), paint);
        canvas.drawText(combine2Str(hgbMin, hgbMax), x, y, paint);
        canvas.drawText(getReferenceStr(blood_hgb, hgbMin, hgbMax), ALARM_X, y, paint);
        //红细胞积压值
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.red_blood), x, y, paint);
        x = getXStart(MID_OF_RESULT, blood_hct, paint);
        canvas.drawText(blood_hct, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string
                .x_measure_unit_percent), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.x_measure_unit_percent), x, y,
                paint);
        x = getXStart(MID_OF_REFERENCE, combine2Str(htcMin, htcMax), paint);
        canvas.drawText(combine2Str(htcMin, htcMax), x, y, paint);
        canvas.drawText(getReferenceStr(blood_hct, htcMin, htcMax), ALARM_X, y, paint);
    }

    /**
     * 打印血脂四项
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printLipids(Canvas canvas, Paint paint, int x, int y) {
        String lipoidemiatc = getFormatterStr(KParamType.LIPIDS_CHOL, dataMeasure
                .getLipoidemiatc(), true);
        x = measure_result_x;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.lipids_name), x, y, canvas,
                paint, true);
        x = measure_result_x + 80;
        y += 220;
        paint.setTextSize(48);
        //总胆固醇
        canvas.drawText(UiUitls.getContent().getString(R.string.health_ct), x, y, paint);
        x = getXStart(MID_OF_RESULT, lipoidemiatc, paint);
        canvas.drawText(lipoidemiatc, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, GlobalConstant.LIPIDS_CHOL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_CHOL_ALARM_HIGH, paint);
        canvas.drawText(GlobalConstant.LIPIDS_CHOL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_CHOL_ALARM_HIGH, x, y, paint);
        canvas.drawText(getReferenceStr(lipoidemiatc, GlobalConstant.LIPIDS_CHOL_ALARM_LOW,
                GlobalConstant.LIPIDS_CHOL_ALARM_HIGH), ALARM_X, y, paint);

        //甘油三酯
        String lipoidemiatg = getFormatterStr(KParamType.LIPIDS_TRIG, dataMeasure
                .getLipoidemiatg(), true);
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.health_gt), x, y, paint);
        x = getXStart(MID_OF_RESULT, lipoidemiatg, paint);
        canvas.drawText(lipoidemiatg, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, GlobalConstant.LIPIDS_TRIG_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_TRIG_ALARM_HIGH, paint);
        canvas.drawText(GlobalConstant.LIPIDS_TRIG_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_TRIG_ALARM_HIGH, x, y, paint);
        canvas.drawText(getReferenceStr(lipoidemiatg, GlobalConstant.LIPIDS_TRIG_ALARM_LOW,
                GlobalConstant.LIPIDS_TRIG_ALARM_HIGH), ALARM_X, y, paint);

        //高密度脂蛋白
        String lipoidemiahdl = getFormatterStr(KParamType.LIPIDS_HDL, dataMeasure
                .getLipoidemiahdl(), true);
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.lipids_hdl_1), x, y, paint);
        x = getXStart(MID_OF_RESULT, lipoidemiahdl, paint);
        canvas.drawText(lipoidemiahdl, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, GlobalConstant.LIPIDS_HDL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_HDL_ALARM_HIGH, paint);
        canvas.drawText(GlobalConstant.LIPIDS_HDL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_HDL_ALARM_HIGH, x, y, paint);
        canvas.drawText(getReferenceStr(lipoidemiahdl, GlobalConstant.LIPIDS_HDL_ALARM_LOW,
                GlobalConstant.LIPIDS_HDL_ALARM_HIGH), ALARM_X, y, paint);

        //低密度脂蛋白
        String lipoidemialdl = getFormatterStr(KParamType.LIPIDS_LDL, dataMeasure
                .getLipoidemialdl(), true);

        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.lipids_ldl_1), x, y, paint);
        x = getXStart(MID_OF_RESULT, lipoidemialdl, paint);
        canvas.drawText(lipoidemialdl, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.p_unit_mmol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.p_unit_mmol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, GlobalConstant.LIPIDS_LDL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_LDL_ALARM_HIGH, paint);
        canvas.drawText(GlobalConstant.LIPIDS_LDL_ALARM_LOW + "-" + GlobalConstant
                .LIPIDS_LDL_ALARM_HIGH, x, y, paint);
        canvas.drawText(getReferenceStr(lipoidemialdl, GlobalConstant.LIPIDS_LDL_ALARM_LOW,
                GlobalConstant.LIPIDS_LDL_ALARM_HIGH), ALARM_X, y, paint);
    }

    /**
     * 打印糖化血红蛋白
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printSugarHgb(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x + 80;
        y += 20;
        //HbA1c(NGSP)
        drawReportTitle(UiUitls.getContent().getString(R.string.suagr_bhd), x, y, canvas, paint,
                true);
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.report_ngsp), x, y, paint);
        x = getXStart(MID_OF_RESULT, ngsp, paint);
        canvas.drawText(ngsp, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.per_cent), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.per_cent), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.ngsp_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.ngsp_range), x, y, paint);
        canvas.drawText(getReferenceStr(ngsp, SugarBloodParam.NGSP_MIN, SugarBloodParam.NGSP_MAX)
                , ALARM_X, y, paint);
        //HbA1c(IFCC)
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.report_ifcc), x, y, paint);
        x = getXStart(MID_OF_RESULT, ifcc, paint);
        canvas.drawText(ifcc, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.unit_mmol_mol), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.unit_mmol_mol), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.ifcc_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.ifcc_range), x, y, paint);
        canvas.drawText(getReferenceStr(ifcc, SugarBloodParam.IFCC_MIN, SugarBloodParam.IFCC_MAX)
                , ALARM_X, y, paint);

        //HbA1c(eAG)
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.report_eag), x, y, paint);
        x = getXStart(MID_OF_RESULT, eag, paint);
        canvas.drawText(eag, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.health_unit_dl), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.health_unit_dl), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.eag_range), paint);
        canvas.drawText(UiUitls.getString(R.string.eag_range), x, y, paint);
        canvas.drawText(getReferenceStr(eag, SugarBloodParam.EAG_MIN, SugarBloodParam.EAG_MAX),
                ALARM_X, y, paint);
    }

    /**
     * 打印体质BMI
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printBmi(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.param_bmi), x, y, canvas, paint,
                true);
        y += 220;
        paint.setTextSize(48);
        //身高
        canvas.drawText(UiUitls.getContent().getString(R.string.bmi_height), x, y, paint);
        x = getXStart(MID_OF_RESULT, height, paint);
        canvas.drawText(height, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.bmi_height_unit), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.bmi_height_unit), x, y, paint);

        //体重
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.bmi_weight), x, y, paint);
        x = getXStart(MID_OF_RESULT, weight, paint);
        canvas.drawText(weight, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.bmi_weight_unit), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.bmi_weight_unit), x, y, paint);

        //BMI
        x = measure_result_x + 80;
        y += row_interval;
        canvas.drawText(UiUitls.getContent().getString(R.string.param_bmi), x, y, paint);
        x = getXStart(MID_OF_RESULT, bmi, paint);
        canvas.drawText(bmi, x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getString(R.string.bmi_report_range), paint);
        canvas.drawText(UiUitls.getString(R.string.bmi_report_range), x, y, paint);
        canvas.drawText(getReferenceStr(bmi, BmiParam.MIN_VALUE, BmiParam.MAX_VALUE), ALARM_X, y,
                paint);
    }

    /**
     * 打印白细胞
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printWbc(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.param_white), x, y, canvas,
                paint, true);
        y += 220;
        paint.setTextSize(48);
        canvas.drawText(UiUitls.getContent().getString(R.string.param_white), x, y, paint);
        x = getXStart(MID_OF_RESULT, blood_wbc, paint);
        canvas.drawText(blood_wbc, x, y, paint);
        x = getXStart(MID_OF_UNIT, UiUitls.getContent().getString(R.string.unit_wbc), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.unit_wbc), x, y, paint);
        x = getXStart(MID_OF_REFERENCE, UiUitls.getContent().getString(R.string.wbc_range), paint);
        canvas.drawText(UiUitls.getContent().getString(R.string.wbc_range), x, y, paint);
        canvas.drawText(getReferenceStr(blood_wbc, WbcParamValue.MIN_VALUE, WbcParamValue
                .MAX_VALUE), ALARM_X, y, paint);
    }

    /**
     * 打印结论与建议
     * @param canvas 画布
     * @param paint 画笔
     * @param x 打印起点X坐标
     * @param y 打印起点Y坐标
     */
    private void printResultAdvice(Canvas canvas, Paint paint, int x, int y) {
        x = measure_result_x + 80;
        y += 20;
        drawReportTitle(UiUitls.getContent().getString(R.string.p_advice), x, y, canvas, paint,
                false);
        x = measure_result_x;
        y += 100;
        paint.setStyle(Paint.Style.STROKE);
        int adviceEndY = y + 250;
        canvas.drawRect(x, y, bitmapWidthwidth - measure_result_x, adviceEndY, paint);
        x += 20;
        y += 50;
        paint.setTextSize(48);
        paint.setStyle(Paint.Style.FILL);
        if (!TextUtils.isEmpty(advice)) {
            if (advice.length() > 45) {
                String substring = advice.substring(0, 45);
                canvas.drawText("" + substring, x, y, paint);
            }
            if (advice.length() > 90) {
                y += row_interval;
                String substring = advice.substring(45, 90);
                canvas.drawText("" + substring, x, y, paint);
            }
            if (advice.length() > 135) {
                y += row_interval;
                String substring = advice.substring(90, 135);
                canvas.drawText("" + substring, x, y, paint);
            }
            if (advice.length() > 180) {
                y += row_interval;
                String substring1 = advice.substring(135, 180);
                canvas.drawText("" + substring1, x, y, paint);

                y += row_interval;
                String substring2 = advice.substring(180);
                canvas.drawText("" + substring2, x, y, paint);
            }
        }
    }

    /**
     * 获取血糖参考值字符串
     * @param style 血糖模式
     * @return
     */
    private String getGluReferenceStr(String style) {
        String gluReferenceValue = "";
        switch (style) {
            case GLU_BEFORE_EAT:
                gluReferenceValue = UiUitls.getContent().getString(R.string.p_2_7);
                break;
            case GLU_AFTER_EAT:
                gluReferenceValue = UiUitls.getContent().getString(R.string.p_2_11);
                break;
            default:
                gluReferenceValue = UiUitls.getContent().getString(R.string.p_2_7);
                break;
        }
        return gluReferenceValue;
    }

    /**
     * 获取血糖参考值
     * 空腹血糖 3.9-7.2
     * 餐后血糖 0-10.0
     * @param style 血糖模式
     * @param high 是高还是低
     * @return
     */
    private float getGluReferenceValue(String style, boolean high) {
        float value;
        switch (style) {
            case GLU_BEFORE_EAT:
                if (high) {
                    value = 7.2f;
                } else {
                    value = 3.9f;
                }
                break;
            case GLU_AFTER_EAT:
                if (high) {
                    value = 10.0f;
                } else {
                    value = 0.0f;
                }
                break;
            default:
                if (high) {
                    value = 7.2f;
                } else {
                    value = 3.9f;
                }
                break;
        }

        return value;
    }

    /**
     * 画体检报告到一个bitmap中
     * @param bitmap 装载体检报告的bitmap容器
     * @param bean 体检报告中的数据
     */
    private void drawReportOnBitmap2(Bitmap bitmap, EcgDiagnoseBean bean) {

        initReportData();

        Canvas canvas = new Canvas(bitmap);

        int x;//x方向坐标
        int y;//y方向坐标
        //打印标题的起始位置
        final int title_x = 700;
        final int title_y = 120;
        //打印个人图标的起始位置
        final int persion_pic_x = 140;
        final int persion_pic_y = 270;
        //打印测量结果的起始位置
        measure_result_x = 104;
        measure_result_y = persion_pic_y + row_interval * 2;

        // 画布宽度
        bitmapWidthwidth = bitmap.getWidth();

        //设置画笔
        Paint paint = new Paint();

        //打印报告图标
        Bitmap hosPic = BitmapFactory.decodeResource(getResources(), R
                .drawable.pic_hos);
        canvas.drawBitmap(hosPic, title_x, title_y - 120, null);

        //打印第一行标题
        paint.setColor(Color.BLACK);
        paint.setTextSize(75);

//        canvas.drawText(UiUitls.getString(R.string.x_district_temp), x, y,
//                paint);
        //打印第二行标题
        x = 1000;
        y = title_y + 10;
        canvas.drawText(UiUitls.getString(R.string.measure_report_title), x, y,
                paint);

        paint.setStrokeWidth((float) 5);
        canvas.drawLine(measure_result_x, y + 60,
                bitmapWidthwidth - measure_result_x, y + 60,
                paint);

        //照片终点位置
        int picEndX = 344;
        int picEndY = 522;
        //打印照片 TODO
        if (personPic != null) {
            canvas.drawBitmap(personPic, null, new Rect(persion_pic_x,
                    persion_pic_y - 25, picEndX, picEndY - 25), null);
        }

        //第一行个人信息
        //打印个人信息的起始位置
        paint.setTextSize(48);
        final int persion_info_x = 250;
        final int persion_info_y = 350;
        x = persion_info_x;
        y = persion_info_y;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_name), x, y,
                paint);
        x += 150;
        canvas.drawText(tvName.getText()
                .toString(), x, y, paint);
        x += 550;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_sex), x, y,
                paint);
        x += 150;
        canvas.drawText(UiUitls.getSexString(patientBean
                .getSex()), x, y, paint);

        x += 350;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_measure_time),
                x, y, paint);
        x += 240;
        canvas.drawText(measureTime, x, y, paint);

        //第二行个人信息
        x = persion_info_x;
        y = persion_info_y + 100;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_age),
                x, y, paint);
        x += 150;
        canvas.drawText(patientAge, x, y, paint);
        x += 100;
        canvas.drawText(UiUitls.getString(R.string.age), x, y, paint);

        x += 450;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_doc),
                x, y, paint);
        x += 150;
        canvas.drawText("" + dataBean.getDoctor(), x, y, paint);

        printMeasureData(canvas, paint, false);

        x = measure_result_x;
        y = 3348;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_report_print), x, y, paint);
        x += 250;
        canvas.drawText("" + reportTime, x, y, paint);

        x += 1700;
        canvas.drawText(UiUitls.getContent().getString(R.string.p_page2),
                x, y, paint);

        x = measure_result_x;
        paint.setStrokeWidth((float) 1);
        canvas.drawLine(measure_result_x, y - 60,
                bitmapWidthwidth - measure_result_x, y - 60,
                paint);
        // ### 生成位图文件 ###
        saveBitmapFile(bitmap, GlobalConstant.HEALTH_REPORT_TWO, 100);
        bitmapPort2 = null; //释放资源
    }

    /**
     * 画心电图
     */
    private void drawEcgImage() {
        initOldEcgData();
        Canvas canvas = new Canvas(bitmapEcg);
        //设置画笔
        Paint paint = new Paint();
        measure_result_x = 104;
        int x; //x方向坐标
        int y; //y方向坐标

        paint.setStyle(Paint.Style.STROKE);
        x = measure_result_x;
        y = measure_result_x;
        //画打印的矩形边框
        canvas.drawRect(x, y, 1653, 600, paint);
        canvas.drawRect(1653, y, 3202, 600, paint);
        canvas.drawRect(x, 600, 3202, 2264, paint);

        String pr = dataBean.getPr() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R.string
                .default_value) : String.valueOf(dataBean.getPr());
        String qrs = dataBean.getQrs() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R
                .string.default_value) : String.valueOf(dataBean.getQrs());
        String qt = dataBean.getQt() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R.string
                .default_value) : String.valueOf(dataBean.getQt());
        String qtc = dataBean.getQtc() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R
                .string.default_value) : String.valueOf(dataBean.getQtc());
        String pAxis = dataBean.getpAxis() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R
                .string.default_value) : String.valueOf(dataBean.getpAxis());
        String qrsAxis = dataBean.getQrsAxis() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R
                .string.default_value) : String.valueOf(dataBean.getQrsAxis());
        String tAxis = dataBean.gettAxis() == GlobalConstant.INVALID_DATA ? UiUitls.getString(R
                .string.default_value) : String.valueOf(dataBean.gettAxis());
        String rv5 = dataBean.getRv5().equals("") ? UiUitls.getString(R.string.default_value) :
                dataBean.getRv5();
        String sv1 = dataBean.getSv1().equals("") ? UiUitls.getString(R.string.default_value) :
                dataBean.getSv1();
        String rv5PlusSv1 = dataBean.getRv5PlusSv1().equals("") ? UiUitls.getString(R.string
                .default_value) : dataBean.getRv5PlusSv1();
        String ecgHr = UiUitls.getString(R.string.HR) + UiUitls.getString(R.string.width_colon) +
                String.valueOf(dataBean.getTrendValue(KParamType.ECG_HR) / GlobalConstant
                        .TREND_FACTOR) + UiUitls.getString(R.string.blank) + UiUitls.getString(R
                .string
                .health_unit_bpm); //心率测量值
        String ecgPr = UiUitls.getString(R.string.pr_en) + UiUitls.getString(R.string
                .width_colon) + pr + UiUitls.getString(R.string.blank) + UiUitls.getString(R
                .string.unit_ms); //PR间隔
        String ecgQrs = UiUitls.getString(R.string.qrs) + UiUitls.getString(R.string.width_colon)
                + qrs + UiUitls.getString(R.string.blank) + UiUitls.getString(R.string.unit_ms);
        //p/QRS间隔
        String ecgQtQtc = UiUitls.getString(R.string.qtQtc) + UiUitls.getString(R.string
                .width_colon) + qt + "/" + qtc + UiUitls.getString(R.string.blank) + UiUitls
                .getString(R.string.unit_ms); //QT/QTC间隔
        String ecgPQrsT = UiUitls.getString(R.string.P_QRS_T) + UiUitls.getString(R.string
                .width_colon) + pAxis + "/" + qrsAxis + "/" + tAxis + UiUitls.getString(R.string
                .blank) + UiUitls.getString(R.string.unit_limit); //P/QRS/T轴
        String ecgRvSv = UiUitls.getString(R.string.rv5Sv1) + UiUitls.getString(R.string
                .width_colon) + rv5 + "/" + sv1 + UiUitls.getString(R.string.blank) + UiUitls
                .getString(R.string.unit_mv); //RV5/SV1
        String ecgRvAddSv = UiUitls.getString(R.string.rv5_plus_sv1) + UiUitls.getString(R.string
                .width_colon) + rv5PlusSv1 + UiUitls.getString(R.string.blank) + UiUitls
                .getString(R.string.unit_mv); //RV5+SV1
        String ecgResult = dataBean.getEcgDiagnoseResult().equals("0.00") ? "" : dataBean
                .getEcgDiagnoseResult(); //心电诊断结果
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(48);
        //顶部病人基本信息
        String patientInfo = "";
        if (!StringUtil.isEmpty(patientBean.getCard())) {
            patientInfo = patientBean.getName() + "   " + UiUitls.getSexString(patientBean
                    .getSex()) + "   " + patientAge + UiUitls.getString(R.string.age) + "  " +
                    " " + patientBean.getCard() + "   " + UiUitls.getString(R.string.measure_time)
                    + UiUitls.getDateFormat(UiUitls.DateState.LONG).format(dataBean
                    .getMeasureTime());
        } else if (!StringUtil.isEmpty(patientBean.getMemberShipCard())
                && !patientBean.getMemberShipCard().contains(GlobalConstant.preStr)) {
            //会员卡不为空且没有自动生成的“ks”标记才打印出来。
            patientInfo = patientBean.getName() + "   " + UiUitls.getSexString(patientBean
                    .getSex()) + "   " + patientAge + UiUitls.getString(R.string.age) + "  " +
                    " " + patientBean.getMemberShipCard() + "   "
                    + UiUitls.getString(R.string.measure_time)
                    + UiUitls.getDateFormat(UiUitls.DateState.LONG).format(dataBean
                    .getMeasureTime());
        } else {
            patientInfo = patientBean.getName() + "   " + UiUitls.getSexString(patientBean
                    .getSex()) + "   " + patientAge + UiUitls.getString(R.string.age) + "  " +
                    " " + patientBean.getCard() + "   " + UiUitls.getString(R.string.measure_time)
                    + UiUitls.getDateFormat(UiUitls.DateState.LONG).format(dataBean
                    .getMeasureTime());
        }

        canvas.drawText(patientInfo, 130, 90, paint);
        x += 20;
        y += 60;
        canvas.drawText(ecgHr, x, y, paint);
        y += 65;
        canvas.drawText(ecgPr, x, y, paint);
        y += 65;
        canvas.drawText(ecgQrs, x, y, paint);
        y += 65;
        canvas.drawText(ecgQtQtc, x, y, paint);
        y += 65;
        canvas.drawText(ecgPQrsT, x, y, paint);
        y += 65;
        canvas.drawText(ecgRvSv, x, y, paint);
        y += 65;
        canvas.drawText(ecgRvAddSv, x, y, paint);
        x = 880;
        y = measure_result_x + 60;
        canvas.drawText(UiUitls.getString(R.string.ecg_diagnose_result), x, y, paint);
        //心电诊断结果每行显示15个字，超过则换行
        if (ecgResult.length() > 0) {
            if (ecgResult.length() > 15) {
                y += 65;
                String substring = ecgResult.substring(0, 15);
                canvas.drawText(substring, x, y, paint);
            }
            if (ecgResult.length() > 30) {
                y += 65;
                String substring = ecgResult.substring(15, 30);
                canvas.drawText(substring, x, y, paint);
            }
            if (ecgResult.length() > 45) {
                y += 65;
                String substring = ecgResult.substring(30, 45);
                canvas.drawText(substring, x, y, paint);
            }
            if (ecgResult.length() > 60) {
                y += 65;
                String substring = ecgResult.substring(45, 60);
                canvas.drawText(substring, x, y, paint);
            }
            if (ecgResult.length() > 75) {
                y += 65;
                String substring = ecgResult.substring(60, 75);
                canvas.drawText(substring, x, y, paint);
            }
            int end = ecgResult.length() % 15;
            String substring;
            if (end == 0) {
                substring = ecgResult.substring(ecgResult.length() - 15, ecgResult.length());
            } else {
                substring = ecgResult.substring(ecgResult.length() - end, ecgResult.length());
            }
            y += 65;
            canvas.drawText(substring, x, y, paint);
        }

        x = 1673;
        y = measure_result_x + 60;
        if (remoteEcgResolved) {
            printRemoteEcg(canvas, x, y, paint);
        } else {
            canvas.drawText(UiUitls.getString(R.string.ecg_expert_diagnose), x, y, paint);
        }

        //心电图宽3098px，高1664px
        HeartImage.drawEcgImage(dataBean, canvas, 3098, 1664);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#70AD47"));
        paint.setStrokeWidth((float) 1);

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap bmp = Bitmap.createBitmap(bitmapEcg, 0, 0, bitmapEcg.getWidth(), bitmapEcg
                .getHeight(), matrix, true);
        Canvas newCanvas = new Canvas(bmp);
        //打印标题的起始位置
        final int titleX = 700;
        final int titleY = 120;
        //打印报告图标
        Bitmap hosPic = BitmapFactory.decodeResource(getResources(), R.drawable.pic_hos);
        newCanvas.drawBitmap(hosPic, titleX, titleY - 120, null);

        //打印第一行标题
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(75);

        //打印第二行标题
        x = 1000;
        y = titleY + 10;
        newCanvas.drawText(UiUitls.getString(R.string.measure_report_title), x, y, paint);

        paint.setStrokeWidth((float) 5);
        newCanvas.drawLine(measure_result_x, y + 60, bmp.getWidth() - measure_result_x, y + 60,
                paint);

        paint.setTextSize(48);
        x = measure_result_x;
        y = 3348;
        newCanvas.drawText(UiUitls.getContent().getString(R.string.p_report_print), x, y, paint);
        x += 250;
        newCanvas.drawText(reportTime, x, y, paint);

        x += 1700;
        paint.setStrokeWidth((float) 1);
        newCanvas.drawLine(measure_result_x, y - 60, bmp.getWidth() - measure_result_x, y - 60,
                paint);
        if (overOnePage) {
            newCanvas.drawText(UiUitls.getContent().getString(R.string.p_page3), x, y, paint);
        } else {
            newCanvas.drawText(UiUitls.getContent().getString(R.string.p_page2), x, y, paint);
        }

        saveBitmapFile(bmp, GlobalConstant.HEALTH_ECG_NAME, 90);
        bitmapEcg = null; //释放资源
    }

    /**
     * 查询远程心电处理结果
     */
    private void requestRemoteEcgStatus() {
        QueryEcgDiagnosesRequest request = new QueryEcgDiagnosesRequest();
        request.dataId = dataMeasure.getUuid();
        request.beginPage = 0;
        request.pageRecord = 1;
        new UploadData(UiUitls.getContent()).queryEcgRemote(request, new UploadData
                .ResponseCallBack() {
            @Override
            public void onSuccess(String s) {
                UiUitls.hideProgress();
                try {
                    EcgQueryResponse response = JsonUtils.toEntity(s, EcgQueryResponse.class);
                    int total = response.total;
                    if (total > 0) {
                        EcgRemoteInfoSaveModule.getInstance().rowData = response.rows.get(0);
                        String statue = EcgRemoteInfoSaveModule.getInstance().rowData.conState;
                        if (statue.equals(REMOTE_ECG_RESOLVED)) {
                            remoteEcgResolved = true;
                        }
                    } else {
                        remoteEcgResolved = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                remoteEcgResolved = false;
            }

            @Override
            public void onException() {
                remoteEcgResolved = false;
            }
        });
    }

    /**
     * 画远程心电信息
     * @param canvas 画布
     * @param x x坐标
     * @param y y坐标
     * @param paint 画笔
     */
    private void printRemoteEcg(Canvas canvas, int x, int y, Paint paint) {
        String expertHr = UiUitls.getString(R.string.HR) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.hr + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.health_unit_bpm); //心率测量值
        String expertPr = UiUitls.getString(R.string.pr_en) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.pr + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms); //PR间隔
        String expertQrs = UiUitls.getString(R.string.qrs) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.qrs + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms); //p/QRS间隔
        String expertQtQtc = UiUitls.getString(R.string.qtQtc) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.qtQtc + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms); //QT/QTC间隔
        String expertPQrsT = UiUitls.getString(R.string.P_QRS_T) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.pQrsT + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_limit); //P/QRS/T轴
        String expertRvSv = UiUitls.getString(R.string.rv5Sv1) + UiUitls.getString(R.string
                .width_colon) + EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1 + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_mv); //RV5/SV1
        String expertRvAddSv = "";
        if (!TextUtils.isEmpty(EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1)) {
            String[] value = EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1.split(UiUitls
                    .getString(R.string.unit_divide));
            if (value.length > 1) {
                String rv5 = value[0].replaceAll(" ", "");
                String sv1 = value[1].replaceAll(" ", "");
                if (!TextUtils.isEmpty(rv5) && !TextUtils.isEmpty(sv1)) {
                    try {
                        BigDecimal bigDecimal1 = new BigDecimal(rv5);
                        BigDecimal bigDecimal2 = new BigDecimal(sv1);
                        String rv5PlusSv1 = bigDecimal1.add(bigDecimal2).toString();
                        expertRvAddSv = UiUitls.getString(R.string.rv5_plus_sv1) + UiUitls
                                .getString(R.string.width_colon) + rv5PlusSv1 + UiUitls.getString(R
                                .string.blank) + UiUitls.getString(R.string.unit_mv); //RV5+SV1
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String expertResult = EcgRemoteInfoSaveModule.getInstance().rowData.conResult; //专家诊断结果
        String resolveTime = UiUitls.getString(R.string.resolve_time) + EcgRemoteInfoSaveModule
                .getInstance().rowData.conResultDate; //处理时间
        String expertName = UiUitls.getString(R.string.resolve_doctor) + EcgRemoteInfoSaveModule
                .getInstance().rowData.conDoctorName; //专家姓名
        canvas.drawText(expertHr, x, y, paint);
        y += 65;
        canvas.drawText(expertPr, x, y, paint);
        y += 65;
        canvas.drawText(expertQrs, x, y, paint);
        y += 65;
        canvas.drawText(expertQtQtc, x, y, paint);
        y += 65;
        canvas.drawText(expertPQrsT, x, y, paint);
        y += 65;
        canvas.drawText(expertRvSv, x, y, paint);
        y += 65;
        canvas.drawText(expertRvAddSv, x, y, paint);
        x += 680;
        canvas.drawText(resolveTime, x, y, paint);
        y -= 65;
        canvas.drawText(expertName, x, y, paint);
        y = measure_result_x + 60;
        canvas.drawText(UiUitls.getString(R.string.ecg_expert_diagnose), x, y, paint);
        //远程心电诊断结果每行显示17个字，超过则换行
        if (null != expertResult && expertResult.length() > 0) {
            if (expertResult.length() > 17) {
                y += 65;
                String substring = expertResult.substring(0, 17);
                canvas.drawText(substring, x, y, paint);
            }
            if (expertResult.length() > 34) {
                y += 65;
                String substring = expertResult.substring(17, 34);
                canvas.drawText(substring, x, y, paint);
            }
            if (expertResult.length() > 51) {
                y += 65;
                String substring = expertResult.substring(34, 51);
                canvas.drawText(substring, x, y, paint);
            }
            int end = expertResult.length() % 17;
            String substring;
            if (end == 0) {
                substring = expertResult.substring(expertResult.length() - 17, expertResult
                        .length());
            } else {
                substring = expertResult.substring(expertResult.length() - end, expertResult
                        .length());
            }
            y += 65;
            canvas.drawText(substring, x, y, paint);
        }
    }

    /**
     * 兼容老版本心电数据
     */
    private void initOldEcgData() {
        if (dataBean.getEcgDiagnoseResult().contains(",")
                && dataBean.getEcgDiagnoseResult().split(",").length >= 12) {
            String[] splitArray = dataBean.getEcgDiagnoseResult().split(",");
            dataBean.setPr(Integer.parseInt(splitArray[1]));
            dataBean.setQrs(Integer.parseInt(splitArray[2]));
            dataBean.setQt(Integer.parseInt(splitArray[3]));
            dataBean.setQtc(Integer.parseInt(splitArray[4]));
            dataBean.setpAxis(Integer.parseInt(splitArray[5]));
            dataBean.setQrsAxis(Integer.parseInt(splitArray[6]));
            dataBean.settAxis(Integer.parseInt(splitArray[7]));
            dataBean.setRv5(splitArray[8]);
            dataBean.setSv1(splitArray[9]);
            dataBean.setRv5PlusSv1(splitArray[10]);
            dataBean.setEcgDiagnoseResult(splitArray[11]);

            DBDataUtil.getMeasureDao().update(dataBean);
        }
    }

    /**
     * 比较容器中的参考值，如不在范围内，提示箭头
     * @param result
     * @param min
     * @param max
     * @return 返回应显示的上下箭头或空
     */
    private String getReferenceStr(String result, float min, float max) {
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        if (result.contains("+") || result.contains(">")) {
            return UiUitls.getContent().getString(R.string.p_point_up);
        }
        if (result.contains("<")) {
            return UiUitls.getContent().getString(R.string.p_point_down);
        }

        if (getString(R.string.negative).equals(result) || getString(R.string.unknown_value)
                .equals(result) || UiUitls.getString(R.string.invalid_value).equals(result)) {
            return "";
        }

        float value = Float.valueOf(result);

        if (value < min) {
            return UiUitls.getContent().getString(R.string.p_point_down);
        } else if (value > max) {
            return UiUitls.getContent().getString(R.string.p_point_up);
        } else {
            return "";
        }
    }

    /**
     * 比较容器中的参考值，如不在范围内，提示箭头
     * @param result
     * @return 返回应显示的上下箭头或空
     */
    private String getReferenceStr(String result) {
        //不太明确下面的判断逻辑。自己新增一个逻辑判断是否要显示向上箭头
        if (result != null && result.contains("+")) {
            return UiUitls.getContent().getString(R.string.p_point_up);
        } else {
            return "";
        }

//        if (TextUtils.isEmpty(result) || getString(R.string.unknown_value)
//                .equals(result)) {
//            return "";
//        }
//
//        if (!getString(R.string.v_measure_normal).equals(result)) {
//            return UiUitls.getContent().getString(R.string.p_point_up);
//        } else {
//            return "";
//        }

    }

    /**
     * 生成体检报告的位图文件
     * @param bitmapPort
     * @param sampleSize 压缩比例（1-100）
     */
    private void saveBitmapFile(Bitmap bitmapPort, String fileName, int sampleSize) {
        File file = new File(GlobalConstant.HEALTH_REPORT_PATH, fileName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmapPort.compress(Bitmap.CompressFormat.JPEG, sampleSize, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    /**
     * 画打印报告中的标题栏
     * @param title 标题名
     * @param x 标题起始x
     * @param y 标题起始y
     * @param canvas
     * @param paint
     * @param b 是否包含了副标题栏
     */
    private void drawReportTitle(String title, int
            x, int y, Canvas canvas, Paint paint, boolean b) {
        x = measure_result_x;
        paint.setStrokeWidth((float) 2);
        canvas.drawLine(x, y,
                bitmapWidthwidth - measure_result_x, y,
                paint);
        y += 10;
        paint.setColor(Color.parseColor(TITLE_COLOR));
        canvas.drawRect(x, y,
                bitmapWidthwidth - measure_result_x, y + 80,
                paint);

        x += 80;
        y += 60;
        paint.setTextSize(58);
        paint.setColor(Color.BLACK);
        canvas.drawText(title, x, y, paint);

        paint.setColor(Color.BLACK);

        if (b) {
            // subtitle 副标题栏
            paint.setTextSize(48);
            y += 80;
            canvas.drawText("项目名称", x, y, paint);

            x += column_interval;
            canvas.drawText("检查结果", x, y, paint);

            x += column_interval;
            canvas.drawText("单位", x, y, paint);

            x += column_interval;
            canvas.drawText("参考值", x, y, paint);

            x += column_interval;
            canvas.drawText("提示", x, y, paint);

            paint.setStrokeWidth((float) 1);
            y += 20;
            canvas.drawLine(measure_result_x, y,
                    bitmapWidthwidth - measure_result_x, y,
                    paint);
        }
    }

    /**
     * 比较容器中的参考值，如不在范围内，提示箭头图片
     * @param tv 显示的容器
     * @param min 范围下标
     * @param max 范围上标
     * @param iv 箭头显示的位置
     */
    private void compareReference(TextView tv, float min, float max, ImageView
            iv) {
        String valueStr = tv.getText().toString().trim();
        if (valueStr.equals("-?-") || valueStr.equals("/") || valueStr.length() == 0) {
            return;
        } else if (valueStr.contains(">")) {
            iv.setImageResource(R.drawable.ic_top);
        } else if (valueStr.contains("<")) {
            iv.setImageResource(R.drawable.ic_low);
        } else {
            float value = Float.valueOf(valueStr);

            if (value < min) {
                iv.setImageResource(R.drawable.ic_low);
            } else if (value > max) {
                iv.setImageResource(R.drawable.ic_top);
            }
        }
    }

    /**
     * 参考值为“-”的字段，如不为“-”或者默认的“-?-”，提示 ↑
     * @param result 测量结果值
     * @param iv 视图
     */
    private void compareReference(String result, ImageView iv) {
        if (!getString(R.string.negative).equals(result) &&
                !getString(R.string.unknown_value).equals(result)) {
            iv.setImageResource(R.drawable.ic_top);
        }
    }

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    private String valueToString(int value) {

        switch (value) {
            case -1:
                return "-";
            case 0:
                return "+-";
            case 1:
                return "+1";
            case 2:
                return "+2";
            case 3:
                return "+3";
            case 4:
                return "+4";
            case 5:
                return "+";
            case 6:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }

    /**
     * 对血脂四项超过范围值得处理
     * @param type 测量项标识
     * @param value 测量值
     * @param print 是否是打印
     * @return 显示结果
     */
    private String getFormatterStr(int type, int value, boolean print) {
        //超低指标
        int supLow = -10;
        //超高指标
        int supHigh = -100;
        //无效值
        if (value == -1000 || value == 0) {
            if (print) {
                return UiUitls.getString(R.string.invalid_value);
            } else {
                return UiUitls.getString(R.string.default_value);
            }
        }
        return UiUitls.getValueAfterFactor(type, value);
    }

    /**
     * 比较尿常规测量值的异常显示
     * @param tv 显示的TextView
     * @param value 测量值
     */
    private void compareUrineValue(TextView tv, String value) {
        if (!tv.getText().equals("-?-")) {
            if (!"-".equals(value)) {
                tv.setTextColor(getActivity().getResources().getColor(R.color.high_color));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        disconnectUsb();
        //页面退出，释放对象所占用的内存
        bitmapEcg = null;
        printBmp = null;
        bitmapPort = null;
        bitmapPort2 = null;
    }

    /**
     * 关闭打印机
     */
    public class TaskClose implements Runnable {
        USBPrinting usb = null;

        /**
         * 类
         * @param usb 接口
         */
        public TaskClose(USBPrinting usb) {
            this.usb = usb;
        }

        @Override
        public void run() {
            if (null != usb) {
                usb.Close();
            }
        }
    }

    /**
     * 关闭端口
     */
    public void disconnectUsb() {
        es.submit(new TaskClose(usb));
    }

    /**
     * Usb设备权限激活广播接受
     */
    BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (intent.getBooleanExtra(UsbManager
                    .EXTRA_PERMISSION_GRANTED, false)) {
                if (action != null) {
                    switch (action) {
                        case PantumPrint.ACTION_USB_PERMISSION:
                            //使用奔图打印机打印
                            mHandler.postDelayed(pantumPrinterRunnable, 1000);
                            break;
                        case BroadcastAction.ACTION_QUICK_PRINTER_PERMISSION:
                            printWithQuickPrinter();
                            break;
                        default:
                            break;
                    }
                }
            } else {
                Toast.makeText(context, R.string.can_not_access_printer, Toast.LENGTH_LONG).show();
            }
        }
    };

    /*
   * 获取打印机连接的广播接收者
   * */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (("android.hardware.usb.action.USB_DEVICE_DETACHED").equals(
                    intent.getAction())) {
                // 当有usb 拔出的时候检测一次，奔图打印机是否被拔出
                if (GlobalConstant.IS_PRINTING_INTO) {
                    if (!checkPrintUSB(context)) {
                        loadjni.cancelSendDataToServer();//停止打印
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.setTitle(UiUitls.getString(R.string
                                    .dispose_print_failed) + UiUitls.getString(
                                    R.string.dispose_print_data));
                        }
                        checkUSB(3);//传入打印机未连接的参数，提示用户
                    }
                }
            }
            if (("android.hardware.usb.action.USB_DEVICE_ATTACHED").equals(
                    intent.getAction())) {
                // 当有usb 插入的时候检测一次是否存在打印机
            }
        }
    };

    /**
     * 检测打印机状态
     * @param context 上下文
     */
    private boolean checkPrintUSB(Context context) {
        UsbDevice usbDevice = null;
        //进入方法后先将打印机状态置为false
        GlobalConstant.IS_PRINTING_INTO = false;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context
                .USB_SERVICE);

        if (usbManager == null) {
            //未得到USB管理者
            return false;
        }
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceList.size() == 0) {
            //不存在USB设备
            return false;
        }

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == 9003) {
                //找到了打印设备
                usbDevice = device;
            }
        }
        if (usbDevice == null) {
            //未找到打印机设备
            return false;
        }
        GlobalConstant.IS_PRINTING_INTO = true;
        return true;
    }

    /**
     * 当连接奔图打印机的时候开启USB的拔插监听
     * @param context 上下文引用
     */
    private void initReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);

        IntentFilter permissionFilter = new IntentFilter();
        permissionFilter.addAction(PantumPrint.ACTION_USB_PERMISSION);
        permissionFilter.addAction(BroadcastAction.ACTION_QUICK_PRINTER_PERMISSION);
        context.registerReceiver(usbPermissionReceiver, permissionFilter);
    }

    private Runnable pantumPrinterRunnable = new Runnable() {
        @Override
        public void run() {
            if (checkPanTumConnection()) {
                //奔图打印机
                printWithPantum();
            } else {

            }
        }
    };

    /**
     * 获取奔图打印机访问权限
     */
    private void requestPantumPermission() {
        //如果找到了打印设备，才申请打印权限
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new
                Intent(PantumPrint.ACTION_USB_PERMISSION), 0);
        if (!usbManager.hasPermission(usbDevice)) {
            usbManager.requestPermission(usbDevice, mPermissionIntent);
            Toast.makeText(getActivity(),
                    R.string.dispose_print_again, Toast.LENGTH_LONG).show();
        } else {
            mHandler.post(pantumPrinterRunnable);
        }
    }

    /**
     * 搜索奔图打印机
     * @return 是否搜索到设备
     */
    private boolean searchPantumPrinter() {
        usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceList.size() > 0) {
            // 初始化选择对话框布局，并添加按钮和事件
            while (deviceIterator.hasNext()) { // 这里是if不是while，说明我只想支持一种device
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 9003) {
                    usbDevice = device;
                    return true;
                }
            }
        }
        return false;
    }
}