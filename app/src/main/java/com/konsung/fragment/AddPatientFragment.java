package com.konsung.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mtreader.Mi32Api;
import com.greendao.dao.PatientBeanDao;
import com.konsung.R;
import com.konsung.activity.AddPatientActivity;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.SpinPopupWindow;
import com.konsung.defineview.TipsDialog;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.IdCardUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.synjones.bluetooth.BmpUtil;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * AddPatientFragment类
 * 增加用户类
 */
public class AddPatientFragment extends BaseFragment implements View.OnClickListener
        , SpinPopupWindow.OnPopwindowSelectListener {
    @InjectView(R.id.patient_name_et)
    EditText etPatientName;
    @InjectView(R.id.patient_idCard_et)
    EditText etPatientIdCard;
    @InjectView(R.id.save_btn)
    ImageTextButton btnSave;
    @InjectView(R.id.patient_age_et)
    EditText etPatientAge;
    @InjectView(R.id.membership_card_et)
    EditText etMembershipCard;
    @InjectView(R.id.patient_sex_et)
    TextView tvSex;
    @InjectView(R.id.patient_type_et)
    TextView tvPatientType;
    @InjectView(R.id.patient_tele_et)
    EditText etPatientTelephone;
    @InjectView(R.id.patient_blood_type_et)
    TextView tvBloodType;
    @InjectView(R.id.patient_address_et)
    EditText etAddress;
    @InjectView(R.id.patient_note_et)
    EditText etPatientRemark;
    @InjectView(R.id.patient_head_pic)
    CircleImageView ivHeadPic;
    @InjectView(R.id.id_hint)
    TextView tvIdHint;
    private String mSubstring;
    //下拉框
    private SpinPopupWindow spinPopupWindow;
    //性别数组
    private String[] sexArr;
    //血型数组
    private String[] bTypeArr;
    //病人类型数组
    private String[] pTypeArr;
    //临时存储数组
    private String[] tempArr;
    //下拉框选中位置
    private int position = 0;
    //性别选中位置临时变量记录
    private int sexSelectPosition;
    //病人类型选中位置临时变量记录
    private int patientSelectPosition;
    private final int defaultPosition = 4;
    //血型选中位置临时记录,默认位置为4 不详
    private int bloodSelectPosition = defaultPosition;
    //类型标识 0:性别 1:病人类型 2:血型
    private int typeFlag;
    boolean flag = false;
    //头像名
    private String bmpStr = "";
    //身份证长度
    private final int idCardLenth = 18;
    //身份证截取开始位置
    private final int subStart = 6;
    //身份证截取结束位置
    private final int subEnd = 14;
    //身份证性别判断位置
    private final int sexPosition = 16;
    //增加的宽度
    private final int widthAdd = 16;
    //x轴的偏移量
    private final int xOff = -8;
    //头像位图
    private Bitmap bitmap = null;
    //允许线程内任务循环执行条件
    private boolean isCanRunning = true;
    //用来读取心一康卡的类
    private Mi32Api mi32Api = GlobalConstant.MI32API;
    private Context context = null;
    //身份证是否必填
    private boolean isCardInput;
    //身份证号
    private String idCard = "";
    //会员卡号
    private String memberShipCard = "";
    //导入头像字符串
    private String headBmpStr = "";
    //健康卡号
    private String snStr = "";
    private final int flagNumber = -2;
    private final int idCardLength = 15; //身份证长度
    private final int idCardLengthEighty = 18; //身份证长度
    private final int ageMax = 150; //最大年龄值
    private final int ageThree = 3; //年龄值
    private final int ageTwelve = 12; //年龄值
    private final int subPositionSix = 6; //字符截取位置
    private final int subPositionEeight = 8; //字符截取位置
    private final int subPositionTen = 10; //字符截取位置
    private final int subPositionTwelve = 12; //字符截取位置
    private final int subPositionFourteen = 14; //字符截取位置
    private final int subPositionSixteen = 16; //字符截取位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_patient_new, container, false);
        ButterKnife.inject(this, view);
        context = getActivity();
        // 监听身份证输入框
        etPatientIdCard.addTextChangedListener(mIdCardWatcher);
        // 浮动按钮监听器
        btnSave.setOnClickListener(mAddPatientFloatButtonOnClickListener);
        btnSave.setAnimationCacheEnabled(true);
        //服务监听
        initMeasureListener();
        isCardInput = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.CARD_INPUT, true);
        if (!isCardInput) {
            tvIdHint.setVisibility(View.INVISIBLE);
        }
        initEven();
        initListener();
        initData();
        initThread();
        return view;
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        tvSex.setOnClickListener(this);
        tvBloodType.setOnClickListener(this);
        tvPatientType.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //性别数组
        sexArr = getResources().getStringArray(R.array.detail_sex);
        //血型数组
        bTypeArr = getResources().getStringArray(R.array.detail_blood_type_array);
        //病人类型数组
        pTypeArr = getResources().getStringArray(R.array.patient_type_array);
        etPatientName.setText("");
        etPatientIdCard.setText("");
        tvPatientType.setText(pTypeArr[patientSelectPosition]);
        tvSex.setText(sexArr[sexSelectPosition]);
        tvBloodType.setText(bTypeArr[bloodSelectPosition]);
        etPatientAge.setText("");
        etPatientTelephone.setText("");
        etPatientRemark.setText("");
        etMembershipCard.setText("");
        etAddress.setText("");
        isCanRunning = true;
    }

    /**
     * 初始化事件
     */
    private void initEven() {
        etPatientName.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etPatientIdCard.getText().toString().length();
                    etPatientIdCard.requestFocus(length);
                }
                return false;
            }
        });

        etPatientIdCard.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etPatientAge.getText().toString().length();
                    etPatientAge.requestFocus(length);
                }
                return false;
            }
        });

        etPatientAge.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etMembershipCard.getText().toString().length();
                    etMembershipCard.requestFocus(length);
                }
                return false;
            }
        });

        //添加禁止输入特殊字符串的监听
        etPatientName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                compileExChar(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 初始化线程，用于监听mt3读卡器读卡信息
     */
    private void initThread() {
        UiUitls.postShortThread(runnable);
    }

    /**
     *
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isCanRunning) {
                if (GlobalConstant.IS_LINK_MI32API_SUCCESS) {
                    // 获取心一康卡信息
                    brushCardiovascularCard();
                }
            }
        }
    };

    /**
     * 刷心康卡，获取心康卡卡号
     */
    private void brushCardiovascularCard() {
        if (mi32Api == null) {
            mi32Api = new Mi32Api(context);
            GlobalConstant.MI32API = mi32Api;
            GlobalConstant.MI32API.openDevice(context);
        }
        snStr = mi32Api.getM1CardSn();
        if (!"".equals(snStr)) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    if (etMembershipCard != null) {
                        etMembershipCard.setText(snStr);
                        etMembershipCard.setClickable(false);
                        etMembershipCard.setFocusable(false);
                        etMembershipCard.setFocusableInTouchMode(false);
                        etMembershipCard
                                .setBackgroundResource(R.drawable.user_modify_not_select_bg);
                        etMembershipCard.setOnClickListener(onClickListener);
                    }
                }
            });
        }
    }

    /**
     * 要判断是否包含特殊字符的目标字符串
     * @param str 源字符串
     */
    private void compileExChar(String str) {
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]" +
                ".<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            UiUitls.toast(context, getRecString(R.string.name_no_special));
            etPatientName.setText("");
        }
    }

    /**
     * 初始化服务监听
     */
    private void initMeasureListener() {
        ServiceUtils.setOnMessageSendListener(new ServiceUtils.OnMessageSendListener() {
            @Override
            public void sendParaStatus(String name, String version) {

            }

            @Override
            public void sendWave(int param, byte[] bytes) {

            }

            @Override
            public void sendTrend(int param, int value) {

            }

            @Override
            public void sendConfig(int param, int value) {

            }

            @Override
            public void sendPersonalDetail(String name, String idcard, int sex, int type
                    , String pic, String address) {
                etPatientIdCard.setClickable(false);
                etPatientIdCard.setFocusable(false);
                etPatientIdCard.setFocusableInTouchMode(false);
                etPatientName.setClickable(false);
                etPatientName.setFocusable(false);
                etPatientName.setFocusableInTouchMode(false);
                try {
                    //头像用Base64保存
                    headBmpStr = Base64.encodeToString(pic.getBytes("ISO-8859-1"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                etPatientName.setBackgroundResource(R.drawable.user_modify_not_select_bg);
                etPatientIdCard.setBackgroundResource(R.drawable.user_modify_not_select_bg);

                etPatientIdCard.setText(idcard);
                etPatientName.setText(name);
                char s = idcard.charAt(sexPosition);
                sex = Integer.valueOf(s);
                if (sex % 2 == 0) {
                    //女
                    sexSelectPosition = 0;
                } else if (sex % 2 == 1) {
                    //男
                    sexSelectPosition = 1;
                }
                tvSex.setText(sexArr[sexSelectPosition]);
                patientSelectPosition = type;
                tvPatientType.setText(pTypeArr[patientSelectPosition]);
                if (!TextUtils.isEmpty(pic)) {
                    //头像名以时间戳唯一命名
                    bmpStr = idcard;
                    //保存头像至本地
                    UiUitls.savePhoto(pic, bmpStr);
                    //从本地获取头像
                    bitmap = BmpUtil.getBitmapByFileName(bmpStr);
                    if (bitmap != null) {
                        ivHeadPic.setImageBitmap(bitmap);
                    }
                }
                etAddress.setText(address);
                initBrushListener();
            }

            @Override
            public void send12LeadDiaResult(byte[] bytes) {

            }

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }

    /**
     * 刷身份证后对姓名和身份证进行不可点击提示
     */
    private void initBrushListener() {
        etPatientIdCard.setOnClickListener(onClickListener);
        etPatientName.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == etPatientName) {
                UiUitls.toast(context, getRecString(R.string.tip_name_cant_change));
            } else if (v == etPatientIdCard) {
                UiUitls.toast(context, getRecString(R.string.tip_idcard_cant_change));
            } else if (v == etMembershipCard) {
                UiUitls.toast(context, getRecString(R.string.tip_memshipcard_cant_change));
            }
        }
    };

    /**
     * 保存数据库的方法
     */
    public void addPatientToDB() {
        idCard = etPatientIdCard.getText().toString().trim();
        memberShipCard = etMembershipCard.getText().toString().trim();
        if (TextUtils.isEmpty(idCard) && TextUtils.isEmpty(memberShipCard)) {
            //如果身份证和会员卡同时为空的情况下即随机生成以ks+随机数的字符串作为会员卡号用于上传数据
            memberShipCard = GlobalConstant.preStr + System.currentTimeMillis();
        }
        // 每次保存都创建一个新用户
        final PatientBean patient = new PatientBean();
        patient.setName(etPatientName.getText().toString());
        patient.setHeadBmpStr(headBmpStr);
        patient.setSex(sexSelectPosition);
        patient.setBlood(bloodSelectPosition);
        patient.setPatient_type(patientSelectPosition);
        patient.setSelfmobile(etPatientTelephone.getText().toString().trim());
        patient.setRemark(etPatientRemark.getText().toString().trim());
        //给排序时间戳设置当前时间
        Date date = new Date();
        patient.setSortDate(date);
        String ageTemp = etPatientAge.getText().toString();
        if (!TextUtils.isEmpty(ageTemp)) {
            patient.setAge(Integer.parseInt(ageTemp));
        }
        if (!TextUtils.isEmpty(idCard)) {
            patient.setCard(idCard);
            String s = idCard.substring(subStart, subEnd);
            s += "0000";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
            try {
                patient.setBirthday(sdf.parse(s));
            } catch (ParseException e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
        } else {
            patient.setCard("");
        }
        patient.setBmpStr(bmpStr);
        patient.setAddress(etAddress.getText().toString().trim());
        patient.setMemberShipCard(memberShipCard);
        // 保存进数据库
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                DBDataUtil.getPatientDao().insert(patient);
                GlobalConstant.isAddUser = true;
                ((AddPatientActivity) context).exitActivitySafe(true);
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                        UiUitls.toast(context
                                , UiUitls.getString(R.string.patients_into_success));
                    }
                });
            }
        });
    }

    private View.OnClickListener mAddPatientFloatButtonOnClickListener = new
            View.OnClickListener() {
                TipsDialog mDialog;

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.save_btn:
                            mDialog = null;
                            flag = false;
                            if ("".equals(UiUitls.removeSpace(etPatientName.getText()
                                    .toString())) || null == etPatientName.getText().toString()) {
                                UiUitls.toast(context, getRecString(R.string.name_cannot_empty));
                                return;
                            }
                            if (etPatientName.getText().toString().contains("" + ".")) {
                                UiUitls.toast(context, getRecString(R.string.name_input_error));
                                return;
                            }
                            String idCard = etPatientIdCard.getText().toString().trim();
                            //身份证必填状态下
                            if (isCardInput) {
                                if (TextUtils.isEmpty(idCard)) {
                                    UiUitls.toast(context, getRecString(R.string.card_not_empty));
                                    return;
                                }
                                if (idCard.length() != idCardLenth) {
                                    UiUitls.toast(context
                                            , getRecString(R.string.input_18_idcard));
                                    return;
                                }
                                if (mSubstring.contains("x") || mSubstring.contains("X")) {
                                    UiUitls.toast(context
                                            , getRecString(R.string.idcard_error));
                                    return;
                                }
                                //如果数据库中身份证号码已存在，提示用户重新输入
                                List<PatientBean> list = getIdcard();
                                if (list != null && list.size() > 0) {
                                    UiUitls.toast(context
                                            , getRecString(R.string.id_error_infoexist));
                                    return;
                                }
                                if (UiUitls.getAge(idCard) < 0) {
                                    UiUitls.toast(context
                                            , getRecString(R.string.idcard_error));
                                    return;
                                }
                            } else {
                                if (!TextUtils.isEmpty(idCard)) {
                                    if (idCard.length() != idCardLenth) {
                                        UiUitls.toast(context
                                                , getRecString(R.string.input_18_idcard));
                                        return;
                                    }
                                    if (mSubstring.contains("x") || mSubstring.contains("X")) {
                                        UiUitls.toast(context
                                                , getRecString(R.string.idcard_error));
                                        return;
                                    }
                                    //如果数据库中身份证号码已存在，提示用户重新输入
                                    List<PatientBean> list = getIdcard();
                                    if (list != null && list.size() > 0) {
                                        UiUitls.toast(context
                                                , getRecString(R.string.id_error_infoexist));
                                        return;
                                    }
                                    if (UiUitls.getAge(idCard) < 0) {
                                        UiUitls.toast(context
                                                , getRecString(R.string.idcard_error));
                                        return;
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(etMembershipCard.getText().toString())) {
                                //查询数据库是否存在该健康卡号
                                List<PatientBean> listPatient = getMemberShipCard();
                                //存在
                                if (listPatient != null && listPatient.size() > 0) {
                                    UiUitls.toast(context, getRecString(R.string.HealthCard_exist));
                                    return;
                                }
                            }
                            if (mDialog != null) {
                                boolean showing = mDialog.isShowing();
                                if (showing) {
                                    return;
                                }
                            }
                            mDialog = new TipsDialog(context, UiUitls
                                    .getString(R.string.save), new
                                    TipsDialog.UpdataButtonState() {
                                        @Override
                                        public void getButton(Boolean pressed) {
                                            if (pressed) {
                                                if (UiUitls.checkFileIsFull(Environment
                                                        .getExternalStorageDirectory())) {
                                                    UiUitls.toast(context
                                                            , R.string.storage_limits);
                                                    return;
                                                }
                                                if (flag) {
                                                    return;
                                                }
                                                flag = true;
                                                List<PatientBean> list = getIdcard();
                                                if (list.size() == 0) {
                                                    addPatientToDB();
                                                }
                                                mDialog.dismiss();
                                            } else {
                                                if (flag) {
                                                    return;
                                                }
                                                flag = true;
                                                mDialog.dismiss();
                                            }
                                        }
                                    });

                            mDialog.setOnDismissListener(new DialogInterface
                                    .OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    flag = false;
                                }
                            });
                            mDialog.show();
                            mDialog.setTips(UiUitls.getString(R.string
                                    .verify_patients_into_success));
                            mDialog.setHideIvClose(true);
                            mDialog.setCancelable(false);
                            break;
                        default:
                            break;
                    }
                }
            };

    /**
     * 根据身份证号码查询数据
     * @return 用户列表
     */
    private List<PatientBean> getIdcard() {
        String idcard = etPatientIdCard.getText().toString();
        if (TextUtils.isEmpty(idcard)) {
            return new ArrayList<>();
        }
        List<PatientBean> list = DBDataUtil.getPatientDao().queryBuilder().where(PatientBeanDao
                .Properties.Card.eq(idcard)).list();
        return list;
    }

    /**
     * 根据会员卡号码查询数据
     * @return 用户列表
     */
    private List<PatientBean> getMemberShipCard() {
        String memberShipCard = etMembershipCard.getText().toString();
        if (TextUtils.isEmpty(memberShipCard)) {
            return new ArrayList<>();
        }
        List<PatientBean> list = DBDataUtil.getPatientDao().queryBuilder().where(PatientBeanDao
                .Properties.MemberShipCard.eq(memberShipCard)).list();
        return list;
    }

    // 监听身份证输入框
    private TextWatcher mIdCardWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                mSubstring = s.toString().substring(0, s.length() - 1);
                initIdcardInfo(s.toString());
            }
        }
    };

    /**
     * 初始化身份信息
     * @param idcard 身份证
     */
    private void initIdcardInfo(String idcard) {
        if (idcard == null) {
            return;
        }
        // 符合身份证号码位数
        int year;
        int month;
        int day;
        int sex;
        if (idCardLength == idcard.length()) {
            // 校验正确
            if ("".equals(IdCardUtil.IdCardValidate(idcard))) {
                year = Integer.parseInt("19" + idcard.subSequence(subPositionSix
                        , subPositionEeight).toString());
                month = Integer.parseInt(idcard.subSequence(subPositionEeight
                        , subPositionTen).toString());
                day = Integer.parseInt(idcard.subSequence(subPositionTen
                        , subPositionTwelve).toString());
                int age = UiUitls.getAge(idcard);
                etPatientAge.setText(age < 0 || age > ageMax ? "" : age + "");
                //统一需求，当身份证正确时，不允许修改年龄
                etPatientAge.setFocusable(false);
                etPatientAge.setBackgroundResource(R.drawable.user_modify_not_select_bg);
                etPatientAge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UiUitls.toast(context, getString(R.string.tips_can_not_modify_age));
                    }
                });
            } else {
                etPatientAge.setFocusable(true);
                etPatientAge.setBackgroundResource(R.drawable.user_add_et_bg);
                etPatientAge.setOnClickListener(null);
            }
            char sexStr = idcard.charAt(subPositionFourteen);
            sex = Integer.valueOf(sexStr);
            if (sex % 2 == 0) {
                //女
                sexSelectPosition = 0;
            } else if (sex % 2 == 1) {
                //男
                sexSelectPosition = 1;
            }
            tvSex.setText(sexArr[sexSelectPosition]);
        } else if (idCardLengthEighty == idcard.length()) {
            int age = UiUitls.getAge(idcard);
            String ageStr = age < 0 || age > ageMax ? "" : age + "";
            etPatientAge.setText(ageStr);
            if (age >= 0) {
                etPatientAge.setFocusable(false);
                etPatientAge.setBackgroundResource(R.drawable.user_modify_not_select_bg);
                etPatientAge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UiUitls.toast(context, getString(R.string.tips_can_not_modify_age));
                    }
                });
            } else {
                etPatientAge.setFocusable(true);
                etPatientAge.setBackgroundResource(R.drawable.user_add_et_bg);
                etPatientAge.setOnClickListener(null);
            }
            // 校验正确
            if ("".equals(IdCardUtil.IdCardValidate(idcard))) {
                year = Integer.parseInt(idcard.subSequence(subPositionSix
                        , subPositionTen).toString());
                month = Integer.parseInt(idcard.subSequence(subPositionTen
                        , subPositionTwelve).toString());
                day = Integer.parseInt(idcard.subSequence(subPositionTwelve
                        , subPositionFourteen).toString());
            }
            if (age >= 0 && age < ageThree) {
                patientSelectPosition = 2;
            } else if (age >= ageThree && age <= ageTwelve) {
                patientSelectPosition = 1;
            } else {
                patientSelectPosition = 0;
            }
            tvPatientType.setText(pTypeArr[patientSelectPosition]);
            char sexStr = idcard.charAt(subPositionSixteen);
            sex = Integer.valueOf(sexStr);
            if (sex % 2 == 0) {
                //女
                sexSelectPosition = 0;
            } else if (sex % 2 == 1) {
                //男
                sexSelectPosition = 1;
            }
            tvSex.setText(sexArr[sexSelectPosition]);
        } else {
            etPatientAge.setFocusable(true);
            etPatientAge.setBackgroundResource(R.drawable.user_add_et_bg);
            etPatientAge.setOnClickListener(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPatientName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etPatientIdCard.getWindowToken(), 0);
        ServiceUtils.setOnMessageSendListener(null);
        ButterKnife.reset(this);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvSex) {
            tempArr = sexArr;
            typeFlag = 0;
            position = sexSelectPosition;
        } else if (v == tvPatientType) {
            tempArr = pTypeArr;
            typeFlag = 1;
            position = patientSelectPosition;
        } else if (v == tvBloodType) {
            tempArr = bTypeArr;
            typeFlag = 2;
            position = bloodSelectPosition;
        }
        spinPopupWindow = new SpinPopupWindow(context, ViewGroup.LayoutParams.WRAP_CONTENT
                , v.getWidth() + widthAdd, tempArr, position, typeFlag);
        spinPopupWindow.showAsDropDown(v, xOff, 0, Gravity.NO_GRAVITY);
        spinPopupWindow.setOnPopwindowSelectListener(this);
    }

    @Override
    public void onSelect(int position, int typeFlag) {
        switch (typeFlag) {
            //性别
            case 0:
                tvSex.setText(sexArr[position]);
                sexSelectPosition = position;
                break;
            //病人类型
            case 1:
                tvPatientType.setText(pTypeArr[position]);
                patientSelectPosition = position;
                break;
            //血型
            case 2:
                tvBloodType.setText(bTypeArr[position]);
                bloodSelectPosition = position;
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isCanRunning = false;
        UiUitls.cancelThread(runnable);
    }
}
