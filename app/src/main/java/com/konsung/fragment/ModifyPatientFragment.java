package com.konsung.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.activity.BaseActivity;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.SpinPopupWindow;
import com.konsung.defineview.TipsDialog;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;
import com.konsung.util.StringUtil;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.synjones.bluetooth.BmpUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 修改用户fragment
 */
public class ModifyPatientFragment extends BaseFragment implements View.OnClickListener
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
    TextView flagTv;
    // 用户数据
    // 修改用户的ID
    private Long mId;
    // 修改的用户
    private PatientBean mPatient;
    // 修改前用户名
    private String nameLast;
    private TipsDialog mDialog;
    boolean flag = false;
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
    //血型选中位置临时记录
    private int bloodSelectPosition;
    //类型标识 0:性别 1:病人类型 2:血型
    private int typeFlag;
    //头像位图
    private Bitmap bitmap = null;
    //增加的宽度
    private final int addWidth = 16;
    //x轴的偏移量
    private final int xOff = -8;
    private Context context = null;
    //标识身份证是否需要输入
    private boolean isCardInput = true;
    //会员卡
    private String memberShipCard = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 得到传递过来的ID数据
        mId = getArguments().getLong(GlobalConstant.idKey);
        mPatient = DBDataUtil.getPatientDao().load(mId);
        nameLast = mPatient.getName();
        isCardInput = SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , GlobalConstant.CARD_INPUT, true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_patient_new, container, false);
        ButterKnife.inject(this, view);
        context = getActivity();
        // 设置传递过来的值
        initData();
        initViewWithBundle();
        initListener();
        initEvent();
        return view;
    }

    /**
     * 初始化监听器
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
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //设置点击监听
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                mDialog = new TipsDialog(v.getContext(), UiUitls.getString(R.string.confirm)
                        , new TipsDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                if (pressed) {
                                    if (flag) {
                                        return;
                                    }
                                    flag = true;
                                    updatePatient();
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
                mDialog.setTips(UiUitls.getString(R.string.confirm_modify_user) + nameLast +
                        UiUitls.getString(R.string.data_question));
                mDialog.setHideIvClose(true);
                mDialog.setCancelable(false);
            }
        });
        etPatientName.setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent
                    event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int length = etPatientIdCard.getText().toString().length();
                    etPatientIdCard.setText(etPatientIdCard.getText());
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
                    etPatientAge.setText(etPatientAge.getText());
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
                    etMembershipCard.setText(etMembershipCard.getText());
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

        // 修改用户信息界面禁止修改姓名和身份证
        etPatientName.setFocusable(false);
        etPatientIdCard.setFocusable(false);
        etMembershipCard.setFocusable(false);
        if (!StringUtil.isEmpty(mPatient.getCard()) && UiUitls.getAge(mPatient.getCard()) >= 0) {
            etPatientAge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUitls.toast(context, getString(R.string.tips_can_not_modify_age));
                }
            });
            etPatientAge.setFocusable(false);
            etPatientAge.setBackgroundResource(R.drawable.user_modify_not_select_bg);
        } else {
            etPatientAge.setFocusable(true);
            etPatientAge.setBackgroundResource(R.drawable.user_add_et_bg);
        }
        etPatientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.toast(context, getRecString(R.string.tip_name_cant_change));
            }
        });
        etPatientIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.toast(context, getRecString(R.string.tip_idcard_cant_change));
            }
        });
        etMembershipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.toast(context, getRecString(R.string.tip_memshipcard_cant_change));
            }
        });
    }

    /**
     * 设置传递过来值给各个控件
     */
    private void initViewWithBundle() {
        if (null != mPatient) {
            //身份证不必须输入的时候隐藏*号
            if (!isCardInput) {
                flagTv.setVisibility(View.INVISIBLE);
            }
            etPatientName.setText(mPatient.getName());
            etPatientIdCard.setText(mPatient.getCard());
            etPatientAge.setText(mPatient.getAge() == -1 || mPatient.getAge() == GlobalNumber
                    .UN_TWO ? "" : mPatient.getAge() + "");
            if (null != mPatient.getSelfmobile()) {
                etPatientTelephone.setText(mPatient.getSelfmobile());
            }
            etPatientRemark.setText(mPatient.getRemark());
            sexSelectPosition = mPatient.getSex() > 1 ? 0 : mPatient.getSex();
            tvSex.setText(sexArr[sexSelectPosition]);
            bloodSelectPosition = mPatient.getBlood();
            if (bloodSelectPosition == -1) {
                bloodSelectPosition = GlobalNumber.FOUR_NUMBER;
            }
            tvBloodType.setText(bTypeArr[bloodSelectPosition]);
            patientSelectPosition = mPatient.getPatient_type();
            tvPatientType.setText(pTypeArr[patientSelectPosition]);
            memberShipCard = mPatient.getMemberShipCard();
            if (!TextUtils.isEmpty(memberShipCard)
                    && !memberShipCard.startsWith(GlobalConstant.preStr)) {
                etMembershipCard.setText(memberShipCard);
            }
            etAddress.setText(mPatient.getAddress());
            String picStr = mPatient.getBmpStr();
            if (!TextUtils.isEmpty(picStr)) {
                bitmap = BmpUtil.getBitmapByFileName(picStr);
                if (bitmap != null) {
                    ivHeadPic.setImageBitmap(bitmap);
                }
            }
        }
        etPatientName.setBackgroundResource(R.drawable.user_modify_not_select_bg);
        etPatientIdCard.setBackgroundResource(R.drawable.user_modify_not_select_bg);
        etMembershipCard.setBackgroundResource(R.drawable.user_modify_not_select_bg);
    }

    /**
     * 更新用户信息
     */
    private void updatePatient() {
        if (null == etPatientName) {
            return;
        }
        if ("".equals(etPatientName.getText().toString())
                || null == etPatientName.getText().toString()) {
            UiUitls.toast(context, getRecString(R.string.name_cannot_empty));
            return;
        }
        if ((!"".equals(etPatientName.getText().toString()))
                && (null != etPatientName.getText().toString())) {
            mPatient.setName(etPatientName.getText().toString());
        }
        if (("".equals(etPatientAge.getText().toString()))
                && (null != etPatientAge.getText().toString())) {
            //如果输入空串，设置-2，显示的在判断
            mPatient.setAge(GlobalNumber.UN_TWO);
        } else {
            mPatient.setAge(Integer.parseInt(etPatientAge.getText().toString()));
        }
        mPatient.setSex(sexSelectPosition);
        mPatient.setBlood(bloodSelectPosition);
        mPatient.setPatient_type(patientSelectPosition);
        mPatient.setSelfmobile(etPatientTelephone.getText().toString().trim());
        mPatient.setRemark(etPatientRemark.getText().toString().trim());
        mPatient.setAddress(etAddress.getText().toString().trim());
        // 更新数据库
//        DBDataUtil.getPatientDao().update(mPatient);
        String idcard = SpUtils.getSp(getActivity().getApplicationContext()
                , "app_config", "idcard", "");
        // 如果修改的是当前用户，则需要发送病人信息数据包
        if (mPatient.getIdCard().equals(idcard)) {
            EchoServerEncoder.setPatientConfig((short) mPatient
                    .getPatient_type(), (short) mPatient.getSex(), (short)
                    mPatient.getBlood(), mPatient.getWeight(), mPatient
                    .getHeight(), (short) 0);
            //保存病人信息
            SpUtils.saveToSp(getActivity().getApplicationContext(),
                    "sys_config", "type", mPatient.getPatient_type());
            SpUtils.saveToSp(getActivity().getApplicationContext(),
                    "sys_config", "sex", mPatient.getSex());
            SpUtils.saveToSp(getActivity().getApplicationContext(),
                    "sys_config", "blood", mPatient.getBlood());
            SpUtils.saveToSp(getActivity().getApplicationContext(),
                    "sys_config", "weight", mPatient.getWeight());
            SpUtils.saveToSp(getActivity().getApplicationContext(),
                    "sys_config", "height", mPatient.getHeight());
        }
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                DBDataUtil.getPatientDao().update(mPatient);
                GlobalConstant.isAddUser = true;
                ((BaseActivity) getActivity()).popActivity();
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                    }
                });
            }
        });
    }

    /**
     * 要判断是否包含特殊字符的目标字符串
     * @param str 源字符串
     */
    private void compileExChar(String str) {
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*" +
                "（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            UiUitls.toast(context, getRecString(R.string.name_no_special));
            etPatientName.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            //隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) getActivity()
                    .getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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
        spinPopupWindow = new SpinPopupWindow(getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT
                , v.getWidth() + addWidth, tempArr, position, typeFlag);
        spinPopupWindow.showAsDropDown(v, xOff, 0, Gravity.NO_GRAVITY);
        spinPopupWindow.setOnPopwindowSelectListener(this);
    }

    @Override
    public void onSelect(int position, int flag) {
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
}