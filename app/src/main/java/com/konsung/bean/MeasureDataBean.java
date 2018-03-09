package com.konsung.bean;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测量数据实体
 * 用来封装测量时的数据
 * 主要MeasureDataByPatientListAdapter传递数据
 * @author ouyangfan
 * @version 0.0.1
 */
@Entity
public class MeasureDataBean implements Cloneable{

    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;

    private Long patientId; //关联的居民

    private String uuid; //记录数据的唯一标示uuid

    private String gluStyle = "0"; //测量方式：0:为空腹血糖，1为随机血糖，2为餐后血糖

    private boolean uploadFlag = false; //记录是否上传的标准

    private String idcard = ""; //存储用户表里面的idCard字段，即UUID值

    private String memberShipCard = ""; //会员卡号

    private int ecgHr = GlobalConstant.INVALID_TREND_DATA; // 心电图心率

    private int ecgBr = GlobalConstant.INVALID_TREND_DATA; // 心电图呼吸

    private String ecgDiagnoseResult = "";

    private int spo2Tred = GlobalConstant.INVALID_TREND_DATA;

    private int spo2Pr = GlobalConstant.INVALID_TREND_DATA;

    //BMI值

    private String bmi = "";

    //身高值

    private String height = "";

    //体重值

    private String weight = "";

    //糖化-NGSP

    private int ngsp = GlobalConstant.INVALID_DATA;
    //糖化-IFCC

    private int ifcc = GlobalConstant.INVALID_DATA;
    //糖化-EAG

    private int eag = GlobalConstant.INVALID_DATA;

    private int nibpSys = GlobalConstant.INVALID_TREND_DATA;

    private int nibpDia = GlobalConstant.INVALID_TREND_DATA;

    private int nibpPr = GlobalConstant.INVALID_TREND_DATA;

    private int temp = GlobalConstant.INVALID_TREND_DATA;

    private int irtemp = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_leu = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_nit = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_ubg = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_pro = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_ph = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_bld = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_sg = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_bil = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_ket = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_glu = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_asc = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_alb = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_cre = GlobalConstant.INVALID_TREND_DATA;

    private int urinert_ca = GlobalConstant.INVALID_TREND_DATA;

    private int bloodglu_before_meal = GlobalConstant.INVALID_TREND_DATA;

    private int bloodglu_after_meal = GlobalConstant.INVALID_TREND_DATA;

    private int blood_wbc = GlobalConstant.INVALID_TREND_DATA;

    private int blood_hgb = GlobalConstant.INVALID_TREND_DATA;

    private int blood_hct = GlobalConstant.INVALID_TREND_DATA;

    private int uricacid_trend = GlobalConstant.INVALID_TREND_DATA;

    private int cholesterol_trend = GlobalConstant.INVALID_TREND_DATA;

    private boolean isUpdata = true;

    private String _ecgWave_i = "";

    private String _ecgWave_ii = "";

    private String _ecgWave_iii = "";

    private String _ecgWave_avr = "";

    private String _ecgWave_avl = "";

    private String _ecgWave_avf = "";

    private String _ecgWave_v1 = "";

    private String _ecgWave_v2 = "";

    private String _ecgWave_v3 = "";

    private String _ecgWave_v4 = "";

    private String _ecgWave_v5 = "";

    private String _ecgWave_v6 = "";

    private int _waveNum = 0;

    private int lipoidemiatc = -1000; //血脂总胆固醇

    private int lipoidemiatg = -1000; //血脂甘油三酯

    private int lipoidemialdl = -1000; //血清低密度脂蛋白胆固醇

    private int lipoidemiahdl = -1000; //血清高密度脂蛋白胆固醇

    private int nibpMap = GlobalConstant.INVALID_DATA;

    //健康测量时间

    private Date measureTime = new Date();

    //健康测量时间
    private String measureStrTime
            = UiUitls.getDateFormat(UiUitls.DateState.SHORT).format(measureTime);
    //体检建表时间
    private Date check_day = new Date(); //检测时间

    private String doctor = ""; //责任医师

    //用于测量时判断是否当天建的表
    private Date createtime = new Date();

    private boolean waveStatus1 = true; //是否需要上传

    private boolean waveStatus2 = true; //是否需要上传

    private boolean waveStatus3 = true; //是否需要上传

    private boolean waveStatus4 = true; //是否需要上传

    private boolean waveStatus5 = true; //是否需要上传

    private boolean waveStatus6 = true; //是否需要上传

    private boolean waveStatus7 = true; //是否需要上传

    private boolean waveStatus8 = true; //是否需要上传

    private boolean waveStatus9 = true; //是否需要上传

    private boolean waveStatus10 = true; //是否需要上传

    private boolean waveStatus11 = true; //是否需要上传

    private boolean waveStatus12 = true; //是否需要上传

    //世轩上传

    private boolean updataToSX = true;//是否需要上传

    private int ECGSIZE = 10;
    //字節緩存
    @Transient
    private List<String> _wave1;
    @Transient
    private List<String> _wave2;
    @Transient
    private List<String> _wave3;
    @Transient
    private List<String> _wave4;
    @Transient
    private List<String> _wave5;
    @Transient
    private List<String> _wave6;
    @Transient
    private List<String> _wave7;
    @Transient
    private List<String> _wave8;
    @Transient
    private List<String> _wave9;
    @Transient
    private List<String> _wave10;
    @Transient
    private List<String> _wave11;
    @Transient
    private List<String> _wave12;
    private int tAxis = GlobalConstant.INVALID_DATA; // 心电t波心电轴 单位°
    private int pAxis = GlobalConstant.INVALID_DATA; // 心电P波心电轴 单位°
    private int qrsAxis = GlobalConstant.INVALID_DATA; // 心电qrs波心电轴 单位°
    private int qrs = GlobalConstant.INVALID_DATA; // 心电qrs间期
    private int pr = GlobalConstant.INVALID_DATA; // 心电pr间期
    private int qt = GlobalConstant.INVALID_DATA; // 心电qt间期
    private int qtc = GlobalConstant.INVALID_DATA; // 心电qtc间期
    private String sv1 = ""; // 心电sv1 单位mV
    private String rv5 = ""; // 心电rv5 单位mV
    private String rv5PlusSv1 = ""; //RV5+SV1
    private int paramValue; //测量配置项参数值

    // 构造器
    public MeasureDataBean() {
//	    bytes = new byte[512];
        // for ormLite
        //初始化时间

        _wave1 = new ArrayList<>();
        _wave2 = new ArrayList<>();
        _wave3 = new ArrayList<>();
        _wave4 = new ArrayList<>();
        _wave5 = new ArrayList<>();
        _wave6 = new ArrayList<>();
        _wave7 = new ArrayList<>();
        _wave8 = new ArrayList<>();
        _wave9 = new ArrayList<>();
        _wave10 = new ArrayList<>();
        _wave11 = new ArrayList<>();
        _wave12 = new ArrayList<>();
        //为uuid赋值
        uuid = UUIDGenerator.getUUID();
        //主干代码未有服务器获取测量医生因此默认为未知
        doctor = UiUitls.getString(R.string.unknown);
    }


    @Generated(hash = 884720922)
    public MeasureDataBean(Long id, Long patientId, String uuid, String gluStyle,
            boolean uploadFlag, String idcard, String memberShipCard, int ecgHr,
            int ecgBr, String ecgDiagnoseResult, int spo2Tred, int spo2Pr,
            String bmi, String height, String weight, int ngsp, int ifcc, int eag,
            int nibpSys, int nibpDia, int nibpPr, int temp, int irtemp,
            int urinert_leu, int urinert_nit, int urinert_ubg, int urinert_pro,
            int urinert_ph, int urinert_bld, int urinert_sg, int urinert_bil,
            int urinert_ket, int urinert_glu, int urinert_asc, int urinert_alb,
            int urinert_cre, int urinert_ca, int bloodglu_before_meal,
            int bloodglu_after_meal, int blood_wbc, int blood_hgb, int blood_hct,
            int uricacid_trend, int cholesterol_trend, boolean isUpdata,
            String _ecgWave_i, String _ecgWave_ii, String _ecgWave_iii,
            String _ecgWave_avr, String _ecgWave_avl, String _ecgWave_avf,
            String _ecgWave_v1, String _ecgWave_v2, String _ecgWave_v3,
            String _ecgWave_v4, String _ecgWave_v5, String _ecgWave_v6,
            int _waveNum, int lipoidemiatc, int lipoidemiatg, int lipoidemialdl,
            int lipoidemiahdl, int nibpMap, Date measureTime, String measureStrTime,
            Date check_day, String doctor, Date createtime, boolean waveStatus1,
            boolean waveStatus2, boolean waveStatus3, boolean waveStatus4,
            boolean waveStatus5, boolean waveStatus6, boolean waveStatus7,
            boolean waveStatus8, boolean waveStatus9, boolean waveStatus10,
            boolean waveStatus11, boolean waveStatus12, boolean updataToSX,
            int ECGSIZE, int tAxis, int pAxis, int qrsAxis, int qrs, int pr, int qt,
            int qtc, String sv1, String rv5, String rv5PlusSv1, int paramValue) {
        this.id = id;
        this.patientId = patientId;
        this.uuid = uuid;
        this.gluStyle = gluStyle;
        this.uploadFlag = uploadFlag;
        this.idcard = idcard;
        this.memberShipCard = memberShipCard;
        this.ecgHr = ecgHr;
        this.ecgBr = ecgBr;
        this.ecgDiagnoseResult = ecgDiagnoseResult;
        this.spo2Tred = spo2Tred;
        this.spo2Pr = spo2Pr;
        this.bmi = bmi;
        this.height = height;
        this.weight = weight;
        this.ngsp = ngsp;
        this.ifcc = ifcc;
        this.eag = eag;
        this.nibpSys = nibpSys;
        this.nibpDia = nibpDia;
        this.nibpPr = nibpPr;
        this.temp = temp;
        this.irtemp = irtemp;
        this.urinert_leu = urinert_leu;
        this.urinert_nit = urinert_nit;
        this.urinert_ubg = urinert_ubg;
        this.urinert_pro = urinert_pro;
        this.urinert_ph = urinert_ph;
        this.urinert_bld = urinert_bld;
        this.urinert_sg = urinert_sg;
        this.urinert_bil = urinert_bil;
        this.urinert_ket = urinert_ket;
        this.urinert_glu = urinert_glu;
        this.urinert_asc = urinert_asc;
        this.urinert_alb = urinert_alb;
        this.urinert_cre = urinert_cre;
        this.urinert_ca = urinert_ca;
        this.bloodglu_before_meal = bloodglu_before_meal;
        this.bloodglu_after_meal = bloodglu_after_meal;
        this.blood_wbc = blood_wbc;
        this.blood_hgb = blood_hgb;
        this.blood_hct = blood_hct;
        this.uricacid_trend = uricacid_trend;
        this.cholesterol_trend = cholesterol_trend;
        this.isUpdata = isUpdata;
        this._ecgWave_i = _ecgWave_i;
        this._ecgWave_ii = _ecgWave_ii;
        this._ecgWave_iii = _ecgWave_iii;
        this._ecgWave_avr = _ecgWave_avr;
        this._ecgWave_avl = _ecgWave_avl;
        this._ecgWave_avf = _ecgWave_avf;
        this._ecgWave_v1 = _ecgWave_v1;
        this._ecgWave_v2 = _ecgWave_v2;
        this._ecgWave_v3 = _ecgWave_v3;
        this._ecgWave_v4 = _ecgWave_v4;
        this._ecgWave_v5 = _ecgWave_v5;
        this._ecgWave_v6 = _ecgWave_v6;
        this._waveNum = _waveNum;
        this.lipoidemiatc = lipoidemiatc;
        this.lipoidemiatg = lipoidemiatg;
        this.lipoidemialdl = lipoidemialdl;
        this.lipoidemiahdl = lipoidemiahdl;
        this.nibpMap = nibpMap;
        this.measureTime = measureTime;
        this.measureStrTime = measureStrTime;
        this.check_day = check_day;
        this.doctor = doctor;
        this.createtime = createtime;
        this.waveStatus1 = waveStatus1;
        this.waveStatus2 = waveStatus2;
        this.waveStatus3 = waveStatus3;
        this.waveStatus4 = waveStatus4;
        this.waveStatus5 = waveStatus5;
        this.waveStatus6 = waveStatus6;
        this.waveStatus7 = waveStatus7;
        this.waveStatus8 = waveStatus8;
        this.waveStatus9 = waveStatus9;
        this.waveStatus10 = waveStatus10;
        this.waveStatus11 = waveStatus11;
        this.waveStatus12 = waveStatus12;
        this.updataToSX = updataToSX;
        this.ECGSIZE = ECGSIZE;
        this.tAxis = tAxis;
        this.pAxis = pAxis;
        this.qrsAxis = qrsAxis;
        this.qrs = qrs;
        this.pr = pr;
        this.qt = qt;
        this.qtc = qtc;
        this.sv1 = sv1;
        this.rv5 = rv5;
        this.rv5PlusSv1 = rv5PlusSv1;
        this.paramValue = paramValue;
    }
   

    public void setWaveStatues(int i, boolean value) {
        switch (i) {
            case 1:
                waveStatus1 = value;
                break;
            case 2:
                waveStatus2 = value;
                break;
            case 3:
                waveStatus3 = value;
                break;
            case 4:
                waveStatus4 = value;
                break;
            case 5:
                waveStatus5 = value;
                break;
            case 6:
                waveStatus6 = value;
                break;
            case 7:
                waveStatus7 = value;
                break;
            case 8:
                waveStatus8 = value;
                break;
            case 9:
                waveStatus9 = value;
                break;
            case 10:
                waveStatus10 = value;
                break;
            case 11:
                waveStatus11 = value;
                break;
            case 12:
                waveStatus12 = value;
                break;
        }
    }

    public boolean getWaveStatus(int i) {
        switch (i) {
            case 1:
                return waveStatus1;
            case 2:
                return waveStatus2;
            case 3:
                return waveStatus3;
            case 4:
                return waveStatus4;
            case 5:
                return waveStatus5;
            case 6:
                return waveStatus6;
            case 7:
                return waveStatus7;
            case 8:
                return waveStatus8;
            case 9:
                return waveStatus9;
            case 10:
                return waveStatus10;
            case 11:
                return waveStatus11;
            case 12:
                return waveStatus12;
            default:
                return true;
        }
    }

    public String get_ecgWave(int param) {
        String str = "";
        switch (param) {
            case 1:
                str = _ecgWave_i;

                break;
            case 2:
                str = _ecgWave_ii;
                break;
            case 3:
                str = _ecgWave_iii;
                break;
            case 4:
                str = _ecgWave_avr;
                break;
            case 5:
                str = _ecgWave_avl;
                break;
            case 6:
                str = _ecgWave_avf;
                break;
            case 7:
                str = _ecgWave_v1;
                break;
            case 8:
                str = _ecgWave_v2;
                break;
            case 9:
                str = _ecgWave_v3;
                break;
            case 10:
                str = _ecgWave_v4;
                break;
            case 11:
                str = _ecgWave_v5;
                break;
            case 12:
                str = _ecgWave_v6;
                break;
        }
        return str;
    }

    /**
     * 这里需要修改，ecg数据会一直发送，只需保存最后10分钟的数据
     * @param param
     * @param _wave
     */
    public void set_ecgWave(int param, String _wave) {
        int size;
        switch (param) {
            case 1:
                if (_wave1 == null) {
                    _wave1 = new ArrayList<>();
                }
                _wave1.add(_wave);
                if (_wave1.size() > ECGSIZE) {
                    _wave1.remove(0);
                }
                _ecgWave_i = "";
                size = _wave1.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_i += _wave1.get(i);
                }
                break;
            case 2:
                if (_wave2 == null) {
                    _wave2 = new ArrayList<>();
                }
                _wave2.add(_wave);
                if (_wave2.size() > ECGSIZE) {
                    _wave2.remove(0);
                }
                _ecgWave_ii = "";
                size = _wave2.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_ii += _wave2.get(i);
                }
                break;
            case 3:
                if (_wave3 == null) {
                    _wave3 = new ArrayList<>();
                }
                _wave3.add(_wave);
                if (_wave3.size() > ECGSIZE) {
                    _wave3.remove(0);
                }
                _ecgWave_iii = "";
                size = _wave3.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_iii += _wave3.get(i);
                }
                break;
            case 4:
                if (_wave4 == null) {
                    _wave4 = new ArrayList<>();
                }
                _wave4.add(_wave);
                if (_wave4.size() > ECGSIZE) {
                    _wave4.remove(0);
                }
                _ecgWave_avr = "";
                size = _wave4.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_avr += _wave4.get(i);
                }
                break;
            case 5:
                if (_wave5 == null) {
                    _wave5 = new ArrayList<>();
                }
                _wave5.add(_wave);
                if (_wave5.size() > ECGSIZE) {
                    _wave5.remove(0);
                }
                _ecgWave_avl = "";
                size = _wave5.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_avl += _wave5.get(i);
                }
                break;
            case 6:
                if (_wave6 == null) {
                    _wave6 = new ArrayList<>();
                }
                _wave6.add(_wave);
                if (_wave6.size() > ECGSIZE) {
                    _wave6.remove(0);
                }
                _ecgWave_avf = "";
                size = _wave6.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_avf += _wave6.get(i);
                }
                break;
            case 7:
                if (_wave7 == null) {
                    _wave7 = new ArrayList<>();
                }
                _wave7.add(_wave);
                if (_wave7.size() > ECGSIZE) {
                    _wave7.remove(0);
                }
                _ecgWave_v1 = "";
                size = _wave7.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v1 += _wave7.get(i);
                }
                break;
            case 8:
                if (_wave8 == null) {
                    _wave8 = new ArrayList<>();
                }
                _wave8.add(_wave);
                if (_wave8.size() > ECGSIZE) {
                    _wave8.remove(0);
                }
                _ecgWave_v2 = "";
                size = _wave8.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v2 += _wave8.get(i);
                }
                break;
            case 9:
                if (_wave9 == null) {
                    _wave9 = new ArrayList<>();
                }
                _wave9.add(_wave);
                if (_wave9.size() > ECGSIZE) {
                    _wave9.remove(0);
                }
                _ecgWave_v3 = "";
                size = _wave9.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v3 += _wave9.get(i);
                }
                break;
            case 10:
                if (_wave10 == null) {
                    _wave10 = new ArrayList<>();
                }
                _wave10.add(_wave);
                if (_wave10.size() > ECGSIZE) {
                    _wave10.remove(0);
                }
                _ecgWave_v4 = "";
                size = _wave10.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v4 += _wave10.get(i);
                }
                break;
            case 11:
                if (_wave11 == null) {
                    _wave11 = new ArrayList<>();
                }
                _wave11.add(_wave);
                if (_wave11.size() > ECGSIZE) {
                    _wave11.remove(0);
                }
                _ecgWave_v5 = "";
                size = _wave11.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v5 += _wave11.get(i);
                }
                break;
            case 12:
                if (_wave12 == null) {
                    _wave12 = new ArrayList<>();
                }
                _wave12.add(_wave);
                if (_wave12.size() > ECGSIZE) {
                    _wave12.remove(0);
                }
                _ecgWave_v6 = "";
                size = _wave12.size();
                for (int i = 0; i < size; i++) {
                    _ecgWave_v6 += _wave12.get(i);
                }
                break;
            default:
                break;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGluStyle() {
        return gluStyle;
    }

    public void setGluStyle(String gluStyle) {
        this.gluStyle = gluStyle;
    }

    public void setUploadFlag(boolean uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public int getEcgHr() {
        return ecgHr;
    }

    public void setEcgHr(int ecgHr) {
        this.ecgHr = ecgHr;
    }

    public int getEcgBr() {
        return ecgBr;
    }

    public void setEcgBr(int ecgBr) {
        this.ecgBr = ecgBr;
    }

    public String getEcgDiagnoseResult() {
        return ecgDiagnoseResult;
    }

    public void setEcgDiagnoseResult(String ecgDiagnoseResult) {
        this.ecgDiagnoseResult = ecgDiagnoseResult;
    }

    public int getSpo2Tred() {
        return spo2Tred;
    }

    public void setSpo2Tred(int spo2Tred) {
        this.spo2Tred = spo2Tred;
    }

    public int getSpo2Pr() {
        return spo2Pr;
    }

    public void setSpo2Pr(int spo2Pr) {
        this.spo2Pr = spo2Pr;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getNgsp() {
        return ngsp;
    }

    public void setNgsp(int ngsp) {
        this.ngsp = ngsp;
    }

    public int getIfcc() {
        return ifcc;
    }

    public void setIfcc(int ifcc) {
        this.ifcc = ifcc;
    }

    public int getEag() {
        return eag;
    }

    public void setEag(int eag) {
        this.eag = eag;
    }

    public int getNibpSys() {
        return nibpSys;
    }

    public void setNibpSys(int nibpSys) {
        this.nibpSys = nibpSys;
    }

    public int getNibpDia() {
        return nibpDia;
    }

    public void setNibpDia(int nibpDia) {
        this.nibpDia = nibpDia;
    }

    public int getNibpPr() {
        return nibpPr;
    }

    public void setNibpPr(int nibpPr) {
        this.nibpPr = nibpPr;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getIrtemp() {
        return irtemp;
    }

    public void setIrtemp(int irtemp) {
        this.irtemp = irtemp;
    }

    public int getUrinert_leu() {
        return urinert_leu;
    }

    public void setUrinert_leu(int urinert_leu) {
        this.urinert_leu = urinert_leu;
    }

    public int getUrinert_nit() {
        return urinert_nit;
    }

    public void setUrinert_nit(int urinert_nit) {
        this.urinert_nit = urinert_nit;
    }

    public int getUrinert_ubg() {
        return urinert_ubg;
    }

    public void setUrinert_ubg(int urinert_ubg) {
        this.urinert_ubg = urinert_ubg;
    }

    public int getUrinert_pro() {
        return urinert_pro;
    }

    public void setUrinert_pro(int urinert_pro) {
        this.urinert_pro = urinert_pro;
    }

    public int getUrinert_ph() {
        return urinert_ph;
    }

    public void setUrinert_ph(int urinert_ph) {
        this.urinert_ph = urinert_ph;
    }

    public int getUrinert_bld() {
        return urinert_bld;
    }

    public void setUrinert_bld(int urinert_bld) {
        this.urinert_bld = urinert_bld;
    }

    public int getUrinert_sg() {
        return urinert_sg;
    }

    public void setUrinert_sg(int urinert_sg) {
        this.urinert_sg = urinert_sg;
    }

    public int getUrinert_bil() {
        return urinert_bil;
    }

    public void setUrinert_bil(int urinert_bil) {
        this.urinert_bil = urinert_bil;
    }

    public int getUrinert_ket() {
        return urinert_ket;
    }

    public void setUrinert_ket(int urinert_ket) {
        this.urinert_ket = urinert_ket;
    }

    public int getUrinert_glu() {
        return urinert_glu;
    }

    public void setUrinert_glu(int urinert_glu) {
        this.urinert_glu = urinert_glu;
    }

    public int getUrinert_asc() {
        return urinert_asc;
    }

    public void setUrinert_asc(int urinert_asc) {
        this.urinert_asc = urinert_asc;
    }

    public int getUrinert_alb() {
        return urinert_alb;
    }

    public void setUrinert_alb(int urinert_alb) {
        this.urinert_alb = urinert_alb;
    }

    public int getUrinert_cre() {
        return urinert_cre;
    }

    public void setUrinert_cre(int urinert_cre) {
        this.urinert_cre = urinert_cre;
    }

    public int getUrinert_ca() {
        return urinert_ca;
    }

    public void setUrinert_ca(int urinert_ca) {
        this.urinert_ca = urinert_ca;
    }

    public int getBloodglu_before_meal() {
        return bloodglu_before_meal;
    }

    public void setBloodglu_before_meal(int bloodglu_before_meal) {
        this.bloodglu_before_meal = bloodglu_before_meal;
    }

    public int getBloodglu_after_meal() {
        return bloodglu_after_meal;
    }

    public void setBloodglu_after_meal(int bloodglu_after_meal) {
        this.bloodglu_after_meal = bloodglu_after_meal;
    }

    public int getBlood_wbc() {
        return blood_wbc;
    }

    public void setBlood_wbc(int blood_wbc) {
        this.blood_wbc = blood_wbc;
    }

    public int getBlood_hgb() {
        return blood_hgb;
    }

    public void setBlood_hgb(int blood_hgb) {
        this.blood_hgb = blood_hgb;
    }

    public int getBlood_hct() {
        return blood_hct;
    }

    public void setBlood_hct(int blood_hct) {
        this.blood_hct = blood_hct;
    }

    public int getUricacid_trend() {
        return uricacid_trend;
    }

    public void setUricacid_trend(int uricacid_trend) {
        this.uricacid_trend = uricacid_trend;
    }

    public int getCholesterol_trend() {
        return cholesterol_trend;
    }

    public void setCholesterol_trend(int cholesterol_trend) {
        this.cholesterol_trend = cholesterol_trend;
    }

    public boolean isUpdata() {
        return isUpdata;
    }

    public void setUpdata(boolean updata) {
        isUpdata = updata;
    }

    public String get_ecgWave_i() {
        return _ecgWave_i;
    }

    public void set_ecgWave_i(String _ecgWave_i) {
        this._ecgWave_i = _ecgWave_i;
    }

    public String get_ecgWave_ii() {
        return _ecgWave_ii;
    }

    public void set_ecgWave_ii(String _ecgWave_ii) {
        this._ecgWave_ii = _ecgWave_ii;
    }

    public String get_ecgWave_iii() {
        return _ecgWave_iii;
    }

    public void set_ecgWave_iii(String _ecgWave_iii) {
        this._ecgWave_iii = _ecgWave_iii;
    }

    public String get_ecgWave_avr() {
        return _ecgWave_avr;
    }

    public void set_ecgWave_avr(String _ecgWave_avr) {
        this._ecgWave_avr = _ecgWave_avr;
    }

    public String get_ecgWave_avl() {
        return _ecgWave_avl;
    }

    public void set_ecgWave_avl(String _ecgWave_avl) {
        this._ecgWave_avl = _ecgWave_avl;
    }

    public String get_ecgWave_avf() {
        return _ecgWave_avf;
    }

    public void set_ecgWave_avf(String _ecgWave_avf) {
        this._ecgWave_avf = _ecgWave_avf;
    }

    public String get_ecgWave_v1() {
        return _ecgWave_v1;
    }

    public void set_ecgWave_v1(String _ecgWave_v1) {
        this._ecgWave_v1 = _ecgWave_v1;
    }

    public String get_ecgWave_v2() {
        return _ecgWave_v2;
    }

    public void set_ecgWave_v2(String _ecgWave_v2) {
        this._ecgWave_v2 = _ecgWave_v2;
    }

    public String get_ecgWave_v3() {
        return _ecgWave_v3;
    }

    public void set_ecgWave_v3(String _ecgWave_v3) {
        this._ecgWave_v3 = _ecgWave_v3;
    }

    public String get_ecgWave_v4() {
        return _ecgWave_v4;
    }

    public void set_ecgWave_v4(String _ecgWave_v4) {
        this._ecgWave_v4 = _ecgWave_v4;
    }

    public String get_ecgWave_v5() {
        return _ecgWave_v5;
    }

    public void set_ecgWave_v5(String _ecgWave_v5) {
        this._ecgWave_v5 = _ecgWave_v5;
    }

    public String get_ecgWave_v6() {
        return _ecgWave_v6;
    }

    public void set_ecgWave_v6(String _ecgWave_v6) {
        this._ecgWave_v6 = _ecgWave_v6;
    }

    public int get_waveNum() {
        return _waveNum;
    }

    public void set_waveNum(int _waveNum) {
        this._waveNum = _waveNum;
    }

    public int getLipoidemiatc() {
        return lipoidemiatc;
    }

    public void setLipoidemiatc(int lipoidemiatc) {
        this.lipoidemiatc = lipoidemiatc;
    }

    public int getLipoidemiatg() {
        return lipoidemiatg;
    }

    public void setLipoidemiatg(int lipoidemiatg) {
        this.lipoidemiatg = lipoidemiatg;
    }

    public int getLipoidemialdl() {
        return lipoidemialdl;
    }

    public void setLipoidemialdl(int lipoidemialdl) {
        this.lipoidemialdl = lipoidemialdl;
    }

    public int getLipoidemiahdl() {
        return lipoidemiahdl;
    }

    public void setLipoidemiahdl(int lipoidemiahdl) {
        this.lipoidemiahdl = lipoidemiahdl;
    }

    public int getNibpMap() {
        return nibpMap;
    }

    public void setNibpMap(int nibpMap) {
        this.nibpMap = nibpMap;
    }

    public Date getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
    }

    public Date getCheck_day() {
        return check_day;
    }

    public void setCheck_day(Date check_day) {
        this.check_day = check_day;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public boolean isWaveStatus1() {
        return waveStatus1;
    }

    public void setWaveStatus1(boolean waveStatus1) {
        this.waveStatus1 = waveStatus1;
    }

    public boolean isWaveStatus2() {
        return waveStatus2;
    }

    public void setWaveStatus2(boolean waveStatus2) {
        this.waveStatus2 = waveStatus2;
    }

    public boolean isWaveStatus3() {
        return waveStatus3;
    }

    public void setWaveStatus3(boolean waveStatus3) {
        this.waveStatus3 = waveStatus3;
    }

    public boolean isWaveStatus4() {
        return waveStatus4;
    }

    public void setWaveStatus4(boolean waveStatus4) {
        this.waveStatus4 = waveStatus4;
    }

    public boolean isWaveStatus5() {
        return waveStatus5;
    }

    public void setWaveStatus5(boolean waveStatus5) {
        this.waveStatus5 = waveStatus5;
    }

    public boolean isWaveStatus6() {
        return waveStatus6;
    }

    public void setWaveStatus6(boolean waveStatus6) {
        this.waveStatus6 = waveStatus6;
    }

    public boolean isWaveStatus7() {
        return waveStatus7;
    }

    public void setWaveStatus7(boolean waveStatus7) {
        this.waveStatus7 = waveStatus7;
    }

    public boolean isWaveStatus8() {
        return waveStatus8;
    }

    public void setWaveStatus8(boolean waveStatus8) {
        this.waveStatus8 = waveStatus8;
    }

    public boolean isWaveStatus9() {
        return waveStatus9;
    }

    public void setWaveStatus9(boolean waveStatus9) {
        this.waveStatus9 = waveStatus9;
    }

    public boolean isWaveStatus10() {
        return waveStatus10;
    }

    public void setWaveStatus10(boolean waveStatus10) {
        this.waveStatus10 = waveStatus10;
    }

    public boolean isWaveStatus11() {
        return waveStatus11;
    }

    public void setWaveStatus11(boolean waveStatus11) {
        this.waveStatus11 = waveStatus11;
    }

    public boolean isWaveStatus12() {
        return waveStatus12;
    }

    public void setWaveStatus12(boolean waveStatus12) {
        this.waveStatus12 = waveStatus12;
    }

    public boolean isUpdataToSX() {
        return updataToSX;
    }

    public void setUpdataToSX(boolean updataToSX) {
        this.updataToSX = updataToSX;
    }

    public int getECGSIZE() {
        return ECGSIZE;
    }

    public void setECGSIZE(int ECGSIZE) {
        this.ECGSIZE = ECGSIZE;
    }

    public List<String> get_wave1() {
        return _wave1;
    }

    public void set_wave1(List<String> _wave1) {
        this._wave1 = _wave1;
    }

    public List<String> get_wave2() {
        return _wave2;
    }

    public void set_wave2(List<String> _wave2) {
        this._wave2 = _wave2;
    }

    public List<String> get_wave3() {
        return _wave3;
    }

    public void set_wave3(List<String> _wave3) {
        this._wave3 = _wave3;
    }

    public List<String> get_wave4() {
        return _wave4;
    }

    public void set_wave4(List<String> _wave4) {
        this._wave4 = _wave4;
    }

    public List<String> get_wave5() {
        return _wave5;
    }

    public void set_wave5(List<String> _wave5) {
        this._wave5 = _wave5;
    }

    public List<String> get_wave6() {
        return _wave6;
    }

    public void set_wave6(List<String> _wave6) {
        this._wave6 = _wave6;
    }

    public List<String> get_wave7() {
        return _wave7;
    }

    public void set_wave7(List<String> _wave7) {
        this._wave7 = _wave7;
    }

    public List<String> get_wave8() {
        return _wave8;
    }

    public void set_wave8(List<String> _wave8) {
        this._wave8 = _wave8;
    }

    public List<String> get_wave9() {
        return _wave9;
    }

    public void set_wave9(List<String> _wave9) {
        this._wave9 = _wave9;
    }

    public List<String> get_wave10() {
        return _wave10;
    }

    public void set_wave10(List<String> _wave10) {
        this._wave10 = _wave10;
    }

    public List<String> get_wave11() {
        return _wave11;
    }

    public void set_wave11(List<String> _wave11) {
        this._wave11 = _wave11;
    }

    public List<String> get_wave12() {
        return _wave12;
    }

    public void set_wave12(List<String> _wave12) {
        this._wave12 = _wave12;
    }

    public int gettAxis() {
        return tAxis;
    }

    public void settAxis(int tAxis) {
        this.tAxis = tAxis;
    }

    public int getpAxis() {
        return pAxis;
    }

    public void setpAxis(int pAxis) {
        this.pAxis = pAxis;
    }

    public int getQrsAxis() {
        return qrsAxis;
    }

    public void setQrsAxis(int qrsAxis) {
        this.qrsAxis = qrsAxis;
    }

    public int getQrs() {
        return qrs;
    }

    public void setQrs(int qrs) {
        this.qrs = qrs;
    }

    public int getPr() {
        return pr;
    }

    public void setPr(int pr) {
        this.pr = pr;
    }

    public int getQt() {
        return qt;
    }

    public void setQt(int qt) {
        this.qt = qt;
    }

    public int getQtc() {
        return qtc;
    }

    public void setQtc(int qtc) {
        this.qtc = qtc;
    }

    public String getSv1() {
        return sv1;
    }

    public void setSv1(String sv1) {
        this.sv1 = sv1;
    }

    public String getRv5() {
        return rv5;
    }

    public void setRv5(String rv5) {
        this.rv5 = rv5;
    }

    public String getRv5PlusSv1() {
        return rv5PlusSv1;
    }

    public void setRv5PlusSv1(String rv5PlusSv1) {
        this.rv5PlusSv1 = rv5PlusSv1;
    }

    public int getParamValue() {
        return paramValue;
    }

    public void setParamValue(int paramValue) {
        this.paramValue = paramValue;
    }

    public void setTrendValue(int param, int value) {
        switch (param) {
            case KParamType.ECG_HR:
                ecgHr = value;
                break;
            case KParamType.SPO2_TREND:
                spo2Tred = value;
                break;
            case KParamType.SPO2_PR:
                spo2Pr = value;
                break;
            case KParamType.NIBP_SYS:
                nibpSys = value;
                break;
            case KParamType.NIBP_DIA:
                nibpDia = value;
                break;
            case KParamType.NIBP_PR:
                nibpPr = value;
                break;
            case KParamType.TEMP_T1:
                temp = value;
                break;
            case KParamType.IRTEMP_TREND:
                irtemp = value;
                break;
            case KParamType.NIBP_MAP:
                nibpMap = value;
                break;
            case KParamType.BLOODGLU_AFTER_MEAL:
                bloodglu_after_meal = value;
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                bloodglu_before_meal = value;
                break;
            case KParamType.URINERT_BIL:
                urinert_bil = value;
                break;
            case KParamType.URINERT_BLD:
                urinert_bld = value;
                break;
            case KParamType.URINERT_GLU:
                urinert_glu = value;
                break;
            case KParamType.URINERT_KET:
                urinert_ket = value;
                break;
            case KParamType.URINERT_LEU:
                urinert_leu = value;
                break;
            case KParamType.URINERT_NIT:
                urinert_nit = value;
                break;
            case KParamType.URINERT_PH:
                urinert_ph = value;
                break;
            case KParamType.URINERT_PRO:
                urinert_pro = value;
                break;
            case KParamType.URINERT_SG:
                urinert_sg = value;
                break;
            case KParamType.URINERT_UBG:
                urinert_ubg = value;
                break;
            case KParamType.URINERT_ASC:
                urinert_asc = value;
                break;
            case KParamType.URINERT_ALB:
                urinert_alb = value;
                break;
            case KParamType.URINERT_CRE:
                urinert_cre = value;
                break;
            case KParamType.URINERT_CA:
                urinert_ca = value;
                break;
            case KParamType.BLOOD_WBC:
                blood_wbc = value;
                break;
            case KParamType.BLOOD_HGB:
                blood_hgb = value;
                break;
            case KParamType.BLOOD_HCT:
                blood_hct = value;
                break;
            case KParamType.URICACID_TREND:
                uricacid_trend = value;
                break;
            case KParamType.CHOLESTEROL_TREND:
                cholesterol_trend = value;
                break;
/*			case KParamType.IRTEMP_TREND:
                break;*/
            case KParamType.RESP_RR:
                ecgBr = value;
                break;
            case KParamType.LIPIDS_CHOL://血脂四项
                lipoidemiatc = value;
                break;
            case KParamType.LIPIDS_TRIG:
                lipoidemiatg = value;
                break;
            case KParamType.LIPIDS_HDL:
                lipoidemiahdl = value;
                break;
            case KParamType.LIPIDS_LDL:
                lipoidemialdl = value;
                break;
            case KParamType.HBA1C_NGSP:
                ngsp = value;
                break;
            case KParamType.HBA1C_IFCC:
                ifcc = value;
                break;
            case KParamType.HBA1C_EAG:
                eag = value;
                break;
            default:
                break;
        }
    }

    public int getTrendValue(int param) {
        if (param == KParamType.ECG_HR) {
            return ecgHr;
        } else if (param == KParamType.SPO2_PR) {
            return spo2Pr;
        } else if (param == KParamType.SPO2_TREND) {
            return spo2Tred;
        } else if (param == KParamType.NIBP_SYS) {
            return nibpSys;
        } else if (param == KParamType.NIBP_DIA) {
            return nibpDia;
        } else if (param == KParamType.NIBP_PR) {
            return nibpPr;
        } else if (param == KParamType.TEMP_T1) {
            return temp;
        } else if (param == KParamType.IRTEMP_TREND) {
            return irtemp;
        } else if (param == KParamType.NIBP_MAP) {
            return nibpMap;
        } else if (param == KParamType.BLOODGLU_AFTER_MEAL) {
            return bloodglu_after_meal;
        } else if (param == KParamType.BLOODGLU_BEFORE_MEAL) {
            return bloodglu_before_meal;
        } else if (param == KParamType.URINERT_BIL) {
            return urinert_bil;
        } else if (param == KParamType.URINERT_BLD) {
            return urinert_bld;
        } else if (param == KParamType.URINERT_GLU) {
            return urinert_glu;
        } else if (param == KParamType.URINERT_KET) {
            return urinert_ket;
        } else if (param == KParamType.URINERT_LEU) {
            return urinert_leu;
        } else if (param == KParamType.URINERT_NIT) {
            return urinert_nit;
        } else if (param == KParamType.URINERT_PH) {
            return urinert_ph;
        } else if (param == KParamType.URINERT_PRO) {
            return urinert_pro;
        } else if (param == KParamType.URINERT_SG) {
            return urinert_sg;
        } else if (param == KParamType.URINERT_UBG) {
            return urinert_ubg;
        } else if (param == KParamType.URINERT_ASC) {
            return urinert_asc;
        } else if (param == KParamType.URINERT_ALB) {
            return urinert_alb;
        } else if (param == KParamType.URINERT_CRE) {
            return urinert_cre;
        } else if (param == KParamType.URINERT_CA) {
            return urinert_ca;
        } else if (param == KParamType.BLOOD_WBC) {
            return blood_wbc;
        } else if (param == KParamType.BLOOD_HGB) {
            return blood_hgb;
        } else if (param == KParamType.BLOOD_HCT) {
            return blood_hct;
        } else if (param == KParamType.RESP_RR) {
            return ecgBr;
        } else if (param == KParamType.URICACID_TREND) {
            return uricacid_trend;
        } else if (param == KParamType.CHOLESTEROL_TREND) {
            return cholesterol_trend;
        } else if (param == KParamType.LIPIDS_CHOL) {
            return lipoidemiatc;
        } else if (param == KParamType.LIPIDS_TRIG) {
            return lipoidemiatg;
        } else if (param == KParamType.LIPIDS_LDL) {
            return lipoidemialdl;
        } else if (param == KParamType.LIPIDS_HDL) {
            return lipoidemiahdl;
        } else if (param == KParamType.HBA1C_NGSP) {
            return ngsp;
        } else if (param == KParamType.HBA1C_IFCC) {
            return ifcc;
        } else if (param == KParamType.HBA1C_EAG) {
            return eag;
        } else {
            return -1000;
        }
    }

    /**
     * 数据清空
     */
    public void reset() {
        if (_wave1 != null) {
            _wave1.clear();
        }
        if (_wave2 != null) {
            _wave2.clear();
        }
        if (_wave3 != null) {
            _wave3.clear();
        }
        if (_wave4 != null) {
            _wave4.clear();
        }
        if (_wave5 != null) {
            _wave5.clear();
        }
        if (_wave6 != null) {
            _wave6.clear();
        }
        if (_wave7 != null) {
            _wave7.clear();
        }
        if (_wave8 != null) {
            _wave8.clear();
        }
        if (_wave9 != null) {
            _wave9.clear();
        }
        if (_wave10 != null) {
            _wave10.clear();
        }
        if (_wave11 != null) {
            _wave11.clear();
        }
        if (_wave12 != null) {
            _wave12.clear();
        }
        _ecgWave_i = "";
        _ecgWave_ii = "";
        _ecgWave_iii = "";
        _ecgWave_avr = "";
        _ecgWave_avl = "";
        _ecgWave_avf = "";
        _ecgWave_v1 = "";
        _ecgWave_v2 = "";
        _ecgWave_v3 = "";
        _ecgWave_v4 = "";
        _ecgWave_v5 = "";
        _ecgWave_v6 = "";
    }

    public String getEcgWave(int param) {
        String str = "";
        switch (param) {
            case 1:
                str = _ecgWave_i;
                break;
            case 2:
                str = _ecgWave_ii;
                break;
            case 3:
                str = _ecgWave_iii;
                break;
            case 4:
                str = _ecgWave_avr;
                break;
            case 5:
                str = _ecgWave_avl;
                break;
            case 6:
                str = _ecgWave_avf;
                break;
            case 7:
                str = _ecgWave_v1;
                break;
            case 8:
                str = _ecgWave_v2;
                break;
            case 9:
                str = _ecgWave_v3;
                break;
            case 10:
                str = _ecgWave_v4;
                break;
            case 11:
                str = _ecgWave_v5;
                break;
            case 12:
                str = _ecgWave_v6;
                break;
        }
        return str;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public boolean getIsUpdata() {
        return this.isUpdata;
    }

    public void setIsUpdata(boolean isUpdata) {
        this.isUpdata = isUpdata;
    }

    public boolean getWaveStatus1() {
        return this.waveStatus1;
    }

    public boolean getWaveStatus2() {
        return this.waveStatus2;
    }

    public boolean getWaveStatus3() {
        return this.waveStatus3;
    }

    public boolean getWaveStatus4() {
        return this.waveStatus4;
    }

    public boolean getWaveStatus5() {
        return this.waveStatus5;
    }

    public boolean getWaveStatus6() {
        return this.waveStatus6;
    }

    public boolean getWaveStatus7() {
        return this.waveStatus7;
    }

    public boolean getWaveStatus8() {
        return this.waveStatus8;
    }

    public boolean getWaveStatus9() {
        return this.waveStatus9;
    }

    public boolean getWaveStatus10() {
        return this.waveStatus10;
    }

    public boolean getWaveStatus11() {
        return this.waveStatus11;
    }

    public boolean getWaveStatus12() {
        return this.waveStatus12;
    }

    public boolean getUpdataToSX() {
        return this.updataToSX;
    }

    public int getTAxis() {
        return this.tAxis;
    }

    public void setTAxis(int tAxis) {
        this.tAxis = tAxis;
    }

    public int getPAxis() {
        return this.pAxis;
    }

    public void setPAxis(int pAxis) {
        this.pAxis = pAxis;
    }

    public String getMemberShipCard() {
        return this.memberShipCard;
    }

    public void setMemberShipCard(String memberShipCard) {
        this.memberShipCard = memberShipCard;
    }

    public boolean getUploadFlag() {
        return this.uploadFlag;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MeasureDataBean measureDataBean = null;
        try {
            measureDataBean = (MeasureDataBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return measureDataBean;
    }

    public String getMeasureStrTime() {
        return this.measureStrTime;
    }

    public void setMeasureStrTime(String measureStrTime) {
        this.measureStrTime = measureStrTime;
    }
}
