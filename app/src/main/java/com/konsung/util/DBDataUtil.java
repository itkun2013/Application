package com.konsung.util;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.greendao.dao.UserBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.QueryItem;
import com.konsung.sqlite.DBHelper;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.global.GlobalNumber;

import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by YYX on 2016/6/2 0002.
 * 数据处理的类
 */
public class DBDataUtil {
    /**
     * 根据输入的UUID查询测量数据
     *
     * @param uuid UUID
     * @return 用户测量列表记录
     */
    public static List<MeasureDataBean> getMeasures(String uuid) {
        List<MeasureDataBean> list = getMeasureDao().queryBuilder().where(MeasureDataBeanDao
                .Properties.Idcard.eq(uuid)).list();
        return list;
    }

    /**
     * 通过UUID或者身份证号查询
     *
     * @param uuid UUID号
     * @param card 身份证号
     * @return 用户测量记录
     */
    public static List<MeasureDataBean> getMeasuresByUuidOrIdCard(String uuid, String card) {
        List<MeasureDataBean> list = getMeasureDao().queryBuilder().whereOr(MeasureDataBeanDao
                .Properties.Idcard.eq(uuid), MeasureDataBeanDao.Properties.Idcard.eq(card)).list();
        return list;
    }

    /**
     * 根据分页查询数据
     *
     * @param start 开始的条数
     * @param count 查询多少条
     * @param value 查询的条件
     * @param sex   性别
     * @return 居民信息
     */
    public static List<PatientBean> queryPatient(int start, int count, String value, int sex) {
        QueryBuilder<PatientBean> patientBeanQueryBuilder = getPatientDao().queryBuilder();
        if (!TextUtils.isEmpty(value)) {
            patientBeanQueryBuilder.whereOr(PatientBeanDao.Properties.Name.like("%" + value + "%"),
                    PatientBeanDao.Properties.Card.like("%" + value + "%")).list();
        }
        if (sex >= 0) {
            patientBeanQueryBuilder.where(PatientBeanDao.Properties.Sex.eq(sex));
        }
        patientBeanQueryBuilder.offset(start);
        patientBeanQueryBuilder.limit(count);
        patientBeanQueryBuilder.orderDesc(PatientBeanDao.Properties.SortDate);
        return patientBeanQueryBuilder.list();
    }

    /**
     * 根据UUID查询测量数据
     *
     * @param uuid UUID
     * @return 用户测量列表记录
     */
    public static List<MeasureDataBean> getMeasuresByMeasureUuid(String uuid) {
        List<MeasureDataBean> listMeasure = getMeasureDao().queryBuilder()
                .where(MeasureDataBeanDao.Properties.Uuid.eq(uuid)).list();
        return listMeasure;
    }

    /**
     * 通过会员卡号查询测量数据
     *
     * @param membershipCard 会员卡号
     * @return 用户测量列表记录
     */
    public static List<MeasureDataBean> getMeasuresByMembership(String membershipCard) {
        List<MeasureDataBean> list = getMeasureDao().queryBuilder().where(MeasureDataBeanDao
                .Properties.MemberShipCard.eq(membershipCard)).list();
        return list;
    }

    /**
     * 根据会员卡号查询用户记录
     *
     * @param memberShipCard 会员卡号
     * @return 用户记录
     */
    public static List<PatientBean> getPatientByMemberShipCard(String memberShipCard) {
        List<PatientBean> list = getPatientDao().queryBuilder()
                .where(PatientBeanDao.Properties.MemberShipCard.eq(memberShipCard)).list();
        return list;
    }

    /**
     * 根据用户创建日期倒序查询用户列表
     *
     * @return 病人集合
     */
    public static List<PatientBean> getAllPatientByDate() {
        List<PatientBean> list = getPatientDao().queryBuilder()
                .orderDesc(PatientBeanDao.Properties.SortDate).list();
        return list;
    }

    /**
     * Dao层对象获取
     *
     * @return 获取用户表数据dao类
     */
    public static PatientBeanDao getPatientDao() {
        return DBHelper.getInstance().getPatientDao();
    }

    /**
     * 获取测量表数据dao类
     *
     * @return 量表数据dao类
     */
    public static MeasureDataBeanDao getMeasureDao() {
        return DBHelper.getInstance().getMeasureDao();
    }

    /**
     * 获取基本信息表数据dao类
     *
     * @return 基本信息表数据dao类
     */
    public static UserBeanDao getUserDao() {
        return DBHelper.getInstance().getUserDao();
    }

    /**
     * @return 模板的xml文件
     */
    public static String getUpLoadXml() {
        String str = "<TAIO_HEACHECK>" +
                "<id>%s</id>" +
                "<uploadDate>%s</uploadDate>" +
                "<area>%s</area>" +
                "<equipmentModel>%s</equipmentModel>" +
                "<key>%s</key>" +
                "<IcpName>%s</IcpName>" +
                "<DeviceID>%s</DeviceID>" +
                "<CardNo>%s</CardNo>" +
                "<Name>%s</Name>" +
                "<Sex>%s</Sex>" +
                "<Age>%s</Age>" +
                "<Birthday>%s</Birthday>" +
                "<Doctor>%s</Doctor>" +
                "<CheckDate>%s</CheckDate>" +
                "<patientType>%s</patientType>" +
                "<HR>%s</HR>" +
                "<SBP>%s</SBP>" +
                "<DBP>%s</DBP>" +
                "<MBP>%s</MBP>" +
                "<Left_SBP>%s</Left_SBP>" +
                "<Left_DBP>%s</Left_DBP>" +
                "<Left_MBP>%s</Left_MBP>" +
                "<Right_SBP>%s</Right_SBP>" +
                "<Right_DBP>%s</Right_DBP>" +
                "<Right_MBP>%s</Right_MBP>" +
                "<SPO2>%s</SPO2>" +
                "<PR>%s</PR>" +
                "<Glu>%s</Glu>" +
                "<Glu_style>%s</Glu_style>" +
                "<Height>%s</Height>" +
                "<Weight>%s</Weight>" +
                "<WAIST>%s</WAIST>" +
                "<Hipline>%s</Hipline>" +
                "<Temp>%s</Temp>" +
                "<Urine_ph>%s</Urine_ph>" +
                "<Urine_ubg>%s</Urine_ubg>" +
                "<Urine_bld>%s</Urine_bld>" +
                "<Urine_pro>%s</Urine_pro>" +
                "<Urine_ket>%s</Urine_ket>" +
                "<Urine_nit>%s</Urine_nit>" +
                "<Urine_glu>%s</Urine_glu>" +
                "<Urine_bil>%s</Urine_bil>" +
                "<Urine_leu>%s</Urine_leu>" +
                "<Urine_sg>%s</Urine_sg>" +
                "<Urine_vc>%s</Urine_vc>" +
                "<Urine_cre>%s</Urine_cre>" +
                "<Urine_ca>%s</Urine_ca>" +
                "<Anal>%s</Anal>" +
                "<lipids_chol>%s</lipids_chol>" +
                "<lipids_trig>%s</lipids_trig>" +
                "<lipids_hdl>%s</lipids_hdl>" +
                "<lipids_ldl>%s</lipids_ldl>" +
                "<BMI>%s</BMI>" +
                "<ASSKFXTL/>" +
                "<ASSXHDB>%s</ASSXHDB>" +
                "<ASSHXBYJZ>%s</ASSHXBYJZ>" +
                "<ASSBXB>%s</ASSBXB>" +
                "<ASSXXB/>" +
                "<ASSKFXTDL/>" +
                "<ASSWLDB>%s</ASSWLDB>" +
                "<ASSTHXHDB/>" +
                "<GGNXQGB/>" +
                "<GGNXQGC/>" +
                "<GGNBDB/>" +
                "<GGNZDHS/>" +
                "<SGNXQJ/>" +
                "<SGNXNSD/>" +
                "<SGNXJND/>" +
                "<SGNXNND/>" +
                "<URICACID>%S</URICACID>" +
                "<XZZDGC>%s</XZZDGC>" +
                "<HGB>%s</HGB>" +
                "<CHOL>%s</CHOL>" +
                "<XZGYSZ/>" +
                "<XZGQD/>" +
                "<WaveForm>" +
                "<Sample>%s</Sample>" +
                "<P05>%s</P05>" +
                "<N05>%s</N05>" +
                "<Duration>%s</Duration>" +
                "<ECG_I>%s</ECG_I>" +
                "<ECG_II>%s</ECG_II>" +
                "<ECG_III>%s</ECG_III>" +
                "<ECG_aVR>%s</ECG_aVR>" +
                "<ECG_aVF>%s</ECG_aVF>" +
                "<ECG_aVL>%s</ECG_aVL>" +
                "<ECG_V1>%s</ECG_V1>" +
                "<ECG_V2>%s</ECG_V2>" +
                "<ECG_V3>%s</ECG_V3>" +
                "<ECG_V4>%s</ECG_V4>" +
                "<ECG_V5>%s</ECG_V5>" +
                "<ECG_V6>%s</ECG_V6>" +
                "</WaveForm>" +
                "<RESP_RR>%s</RESP_RR>" +
                "<province>%s</province>" +
                "<city>%s</city>" +
                "<county>%s</county>" +
                "<address>%s</address>" +
                "</TAIO_HEACHECK>";
        return str;
    }

    /**
     * 尿常规值转换
     *
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    public static String valueToString(int value) {

        switch (value) {
            case GlobalNumber.UN_ONE:
                return "-";
            case GlobalNumber.ZERO_NUMBER:
                return "+-";
            case GlobalNumber.ONE_NUMBER:
                return "+1";
            case GlobalNumber.TWO_NUMBER:
                return "+2";
            case GlobalNumber.THREE_NUMBER:
                return "+3";
            case GlobalNumber.FOUR_NUMBER:
                return "+4";
            case GlobalNumber.FIVE_NUMBER:
                return "+";
            case GlobalNumber.SIX_NUMBER:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }

    /**
     * 拼接上传字段的方法
     *
     * @param bean    病人bean
     * @param measure 测量数据bean
     * @return 拼接好的xml文件
     */
    public static String getXmlData(PatientBean bean, MeasureDataBean
            measure) {
        //检查机构名称
        String IcpName = GlobalConstant.ORG_ID;
        //一体机编号
        String DeviceID = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.APP_CODING, "M0000");
        if (TextUtils.isEmpty(DeviceID)) {
            DeviceID = "M0000";
        }
        //姓名
        String Name = UiUitls.stringFilter(bean.getName());
        //身份证
        String idcard = "konsungid";
        if (!TextUtils.isEmpty(bean.getCard())) {
            idcard = bean.getCard();
        }
        //性别
        //为了配合后台的兼容，这边对性别传值做该改变
        String Sex = "";
        if (bean.getSex() == 0) {
            Sex = "2";
        } else {
            Sex = String.valueOf(bean.getSex());
        }
        //年龄
        String age = bean.getAge() == 0 ? "" : bean.getAge() + "";
        //生日 yyyy-MM-dd
        if (bean.getBirthday() == null) {
            bean.setBirthday(IdCardUtil.getBirthday(bean.getIdCard()));
        }
        String birthday = "";
        if (bean.getBirthday() != null) {
            birthday = UiUitls.getDateFormat(UiUitls.DateState.SHORT).format(bean.getBirthday());
        }
        //检查时间yyyy-MM-dd hh:mm:ss
        String CheckDate = UiUitls.getDateFormat(UiUitls.DateState.LONG)
                .format(measure.getMeasureTime());
        String patientType = bean.getPatient_type() + "";
        //心率 次/分钟
        String HR = (measure.getTrendValue(KParamType.ECG_HR) == GlobalNumber.UN_THOUSAND_NUMBER)
                ? "" : String.valueOf(measure.getTrendValue(KParamType.ECG_HR)
                / GlobalConstant.TREND_FACTOR);
        //收缩压 mmHg
        String SBP = (measure.getTrendValue(KParamType.NIBP_SYS) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure.getTrendValue(KParamType
                .NIBP_SYS) / GlobalConstant.TREND_FACTOR);
        //舒张压 mmHg
        String DBP = (measure.getTrendValue(KParamType.NIBP_DIA) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure.getTrendValue(KParamType
                .NIBP_DIA) / GlobalConstant.TREND_FACTOR);
        //平均压 mmHg
        String MBP = (measure.getTrendValue(KParamType.NIBP_MAP) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure.getTrendValue(KParamType
                .NIBP_MAP) / GlobalConstant.TREND_FACTOR);
        //血氧饱和度 %
        String SPO2 = (measure.getTrendValue(KParamType.SPO2_TREND) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure.getTrendValue
                (KParamType.SPO2_TREND) / GlobalConstant.TREND_FACTOR);
        //脉率(血氧) 次/分钟
        String PR = (measure.getTrendValue(KParamType.SPO2_PR) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure.getTrendValue(KParamType
                .SPO2_PR) / GlobalConstant.TREND_FACTOR);
        //脉率(血压)
//        String BP_PR = (measure.getTrendValue(KParamType.NIBP_PR) == -1000) ? "" : String
//                .valueOf(measure.getTrendValue(KParamType.NIBP_PR) / GlobalConstant.TREND_FACTOR);
        //血糖 mmol/L
        String Glu = (measure.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL) / GlobalConstant.TREND_FACTOR);
        //血糖测量方式
        String Glu_style = measure.getGluStyle();
        //身高 cm
        String Height = measure.getHeight();
        //体重 kg
        String Weight = measure.getWeight();
        //左收缩压
        String Left_SBP = "";
        //左舒张压
        String Left_DBP = "";
        //左平均压
        String Left_MBP = "";
        //右收缩压
        String Right_SBP = "";
        //右舒张压
        String Right_DBP = "";
        //右平均压
        String Right_MBP = "";
        //腰围 cm
        String Waist = bean.getWaist() == null ? "" : bean.getWaist();
        //臀围 cm
        String Hipline = bean.getHipline() == null ? "" : bean.getHipline();
        //体温 摄氏度
        String Temp = "";
        int type = SpUtils.getSpInt(UiUitls.getContent(), "sys_config",
                "temp_type",
                TempDefine.TEMP_INFRARED);
        switch (type) {
            case TempDefine.TEMP_CONTACT:
                Temp = (measure.getTrendValue(KParamType.TEMP_T1) == GlobalNumber
                        .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure
                        .getTrendValue(KParamType.TEMP_T1) / GlobalNumber.HUNDRED_NUMBER_FLOAT);
                break;
            case TempDefine.TEMP_INFRARED:
                Temp = (measure.getTrendValue(KParamType.IRTEMP_TREND) == GlobalNumber
                        .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure
                        .getTrendValue(KParamType.IRTEMP_TREND) / GlobalNumber
                        .HUNDRED_NUMBER_FLOAT);
                break;
            default:
                Temp = "";
                break;
        }
        //体质指数
        double bmi = TextUtils.isEmpty(measure.getBmi()) ? 0 : Double.valueOf(measure.getBmi());
        //血常规--血红蛋白 (g/L)
        String ASSXHDB = (measure.getTrendValue(KParamType.BLOOD_HGB) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.BLOOD_HGB) / GlobalConstant.TREND_FACTOR);
        //血氧饱和度%
        String ASSHXBYJZ = (measure.getTrendValue(KParamType.BLOOD_HCT) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure
                .getTrendValue(KParamType.BLOOD_HCT) / GlobalConstant.TREND_FACTOR);
        //血常规--白细胞
        String ASSBXB = (measure.getTrendValue(KParamType.BLOOD_WBC) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.BLOOD_WBC) / GlobalNumber.HUNDRED_NUMBER);
        //尿常规酸碱度
        String Urine_ph = (measure.getTrendValue(KParamType.URINERT_PH) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.URINERT_PH) / GlobalNumber.HUNDRED_NUMBER_FLOAT);
        //尿常规尿胆原
        String Urine_ubg = (measure.getTrendValue(KParamType.URINERT_UBG) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_UBG) / GlobalConstant.TREND_FACTOR);
        //尿常规隐血
        String Urine_bld = (measure.getTrendValue(KParamType.URINERT_BLD) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_BLD) / GlobalConstant.TREND_FACTOR);
        //尿常规尿蛋白
        String Urine_pro = (measure.getTrendValue(KParamType.URINERT_PRO) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_PRO) / GlobalConstant.TREND_FACTOR);
        //尿常规酮体
        String Urine_ket = (measure.getTrendValue(KParamType.URINERT_KET) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_KET) / GlobalConstant.TREND_FACTOR);
        //尿常规亚硝酸盐
        String Urine_nit = (measure.getTrendValue(KParamType.URINERT_NIT) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_NIT) / GlobalConstant.TREND_FACTOR);
        //尿常规尿糖
        String Urine_glu = (measure.getTrendValue(KParamType.URINERT_GLU) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_GLU) / GlobalConstant.TREND_FACTOR);
        //尿常规胆红素
        String Urine_bil = (measure.getTrendValue(KParamType.URINERT_BIL) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_BIL) / GlobalConstant.TREND_FACTOR);
        //尿常规白细胞
        String Urine_leu = (measure.getTrendValue(KParamType.URINERT_LEU) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_LEU) / GlobalConstant.TREND_FACTOR);
        //尿常规尿比密
        String Urine_sg = (measure.getTrendValue(KParamType.URINERT_SG) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.format("%.3f", (double) measure
                .getTrendValue(KParamType.URINERT_SG) / GlobalNumber.THOUSAND_NUMBER_FLOAT);
        //尿常规维生素c
        String Urine_asc = (measure.getTrendValue(KParamType.URINERT_ASC) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : DBDataUtil.valueToString(measure
                .getTrendValue(KParamType.URINERT_ASC) / GlobalConstant.TREND_FACTOR);
        //肌酐（mmol/L）
        String Urine_cre = (measure.getTrendValue(KParamType.URINERT_CRE) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.format("%.1f", (float) measure
                .getTrendValue(KParamType.URINERT_CRE) / GlobalConstant.TREND_FACTOR);
        //尿钙（mmol/L）
        String Urine_ca = (measure.getTrendValue(KParamType.URINERT_CA) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.URINERT_CA) / GlobalConstant.TREND_FACTOR);
        //尿微量白蛋白  （mg/dL）
        String ASSWLDB = (measure.getTrendValue(KParamType.URINERT_ALB) ==
                GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure
                .getTrendValue(KParamType.URINERT_ALB) / GlobalConstant.TREND_FACTOR);
        //尿酸 (mmol/L)
        String URICACID = (measure.getTrendValue(KParamType.URICACID_TREND)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.URICACID_TREND) / GlobalNumber.HUNDRED_NUMBER);
        //血液三项--总胆固醇  (mmol/L)
        String XZZDGC = (measure.getTrendValue(KParamType.CHOLESTEROL_TREND)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.CHOLESTEROL_TREND) / GlobalNumber.HUNDRED_NUMBER);

        String CHOL = (measure.getTrendValue(KParamType.CHOLESTEROL_TREND)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.CHOLESTEROL_TREND) / GlobalNumber.HUNDRED_NUMBER);
        //血脂四项
        String chol = (measure.getTrendValue(KParamType.LIPIDS_CHOL)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.LIPIDS_CHOL) / GlobalNumber.HUNDRED_NUMBER);
        String trig = (measure.getTrendValue(KParamType.LIPIDS_TRIG)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.LIPIDS_TRIG) / GlobalNumber.HUNDRED_NUMBER);
        String hdl = (measure.getTrendValue(KParamType.LIPIDS_HDL)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.LIPIDS_HDL) / GlobalNumber.HUNDRED_NUMBER);
        String ldl = (measure.getTrendValue(KParamType.LIPIDS_LDL)
                == GlobalNumber.UN_THOUSAND_NUMBER) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.LIPIDS_LDL) / GlobalNumber.HUNDRED_NUMBER);
//        String chol = getOtherValue(measure, KParamType.LIPIDS_CHOL);
//        String trig = getOtherValue(measure, KParamType.LIPIDS_TRIG);
//        String hdl = getOtherValue(measure, KParamType.LIPIDS_HDL);
//        String ldl = getOtherValue(measure, KParamType.LIPIDS_LDL);

        String HGB = (measure.getTrendValue(KParamType.BLOOD_HGB) == GlobalNumber
                .UN_THOUSAND_NUMBER) ? "" : String.valueOf(measure
                .getTrendValue(KParamType.BLOOD_HGB) / GlobalNumber.HUNDRED_NUMBER_FLOAT);
        //心电图自动诊断结果
        //心电图自动诊断结果
        String Anal = "";
        //心电诊断
        String[] rut = measure.getEcgDiagnoseResult().split(",");
        //拼接结果字符串
        String AnalNew = "";
        if (rut.length >= GlobalNumber.TWELVE_NUMBER) {
            AnalNew = rut[0] + "," + rut[1] + "," + rut[2] + "," +
                    rut[3] + "," + rut[4] + "," + rut[5] + "," + rut
                    [6] + "," + rut[7] + "," + rut[8] + "," + rut[9] +
                    "," + "" + rut[10] + ",,,,,," + rut[11];
            Anal = AnalNew;
        } else {
            Anal = measure.getEcgDiagnoseResult();
        }
        //波形采样率
        String Sample = "500";
        //+5
        String P05 = "2150";
        //-5
        String N05 = "1946";
        //现在为5秒
        String Duration = "5";
        //心电图波形图数据
        String ECG_I = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_I)), Base64.NO_WRAP);
        String ECG_II = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_II)), Base64.NO_WRAP);
        String ECG_III = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_III)), Base64.NO_WRAP);
        String ECG_aVR = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_AVR)), Base64.NO_WRAP);
        String ECG_aVF = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_AVF)), Base64.NO_WRAP);
        String ECG_aVL = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_AVL)), Base64.NO_WRAP);
        String ECG_V1 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V1)), Base64.NO_WRAP);
        String ECG_V2 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V2)), Base64.NO_WRAP);
        String ECG_V3 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V3)), Base64.NO_WRAP);
        String ECG_V4 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V4)), Base64.NO_WRAP);
        String ECG_V5 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V5)), Base64.NO_WRAP);
        String ECG_V6 = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measure.get_ecgWave(KParamType
                        .ECG_V6)), Base64.NO_WRAP);
        String RESP_RR = (measure.getTrendValue(KParamType.RESP_RR) ==
                -1000) ? "" : String.valueOf((float) measure
                .getTrendValue(KParamType.RESP_RR) / GlobalNumber.TEN_THOUSAND_NUMBER);
        //事先固定好的一个私钥
        String single = "konsung";
        //设备类型
        String equipmentModel = UiUitls.getAreaName() + "-1";
        //设备地区
        String area = UiUitls.getAreaName();
        //上传时间
        String uploadDate = UiUitls.getDateFormat(UiUitls.DateState.LONG)
                .format(new Date());
        //加密字段，key=md5(uploadDate+area+single),其中single是在事先固定好的一个私钥。
        String key = UiUitls.stringMD5(uploadDate + area + single);
        String id = measure.getUuid();
//        String doctor = measure.getDoctor() == null ? "" : measure
//                .getDoctor();
        String doctor = GlobalConstant.USERNAME;
        //省编码
        String province = SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, "provinceId", "");
        //城市编码
        String city = SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, "cityId", "");
        //地区编码
        String county = SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, "districtId", "");
        //详细地址
        String address = SpUtils.getSp(UiUitls.getContent(), GlobalConstant
                .APP_CONFIG, GlobalConstant.DETAIL_ADDRESS, "");
        //获取模板xml
        String str = DBDataUtil.getUpLoadXml();
        String strArg4 = String.format(str, id, uploadDate, area,
                equipmentModel, key, IcpName, DeviceID, idcard,
                Name, Sex, age, birthday, doctor, CheckDate, patientType,
                HR, SBP, DBP, MBP, Left_SBP, Left_DBP, Left_MBP,
                Right_SBP, Right_DBP, Right_MBP, SPO2, PR, Glu, Glu_style, Height,
                Weight, Waist, Hipline, Temp,
                Urine_ph, Urine_ubg, Urine_bld,
                Urine_pro, Urine_ket, Urine_nit, Urine_glu,
                Urine_bil, Urine_leu,
                Urine_sg, Urine_asc, Urine_cre, Urine_ca, Anal,
                chol, trig, hdl, ldl, bmi,
                ASSXHDB, ASSHXBYJZ, ASSBXB,
                ASSWLDB, URICACID, XZZDGC, HGB, CHOL, Sample, P05, N05, Duration,
                ECG_I, ECG_II, ECG_III, ECG_aVR, ECG_aVF, ECG_aVL,
                ECG_V1, ECG_V2, ECG_V3, ECG_V4, ECG_V5, ECG_V6,
                RESP_RR, province, city, county, address);
        return strArg4;
    }

    /**
     * 类型转化
     *
     * @param type 类型
     * @return 病人类型
     */
    public static String getPatientType(int type) {
        Resources res = UiUitls.getContent().getResources();
        String[] patientType = res.getStringArray(R.array.patient_type_array);
        switch (type) {
            case 0:
                return patientType[0];
            case 1:
                return patientType[1];
            case 2:
                return patientType[2];
            default:
                return patientType[0];
        }
    }

    /**
     * 删除病人记录
     *
     * @param bean 测量bean类
     */
    public static void deleteMeasure(MeasureDataBean bean) {
        getMeasureDao().delete(bean);
    }

    /**
     * 根据idcard(之前的身份证字段，该版本作为居民唯一标识，为自动生成的uuid)查询病人
     *
     * @param idCard 身份证号
     * @return 用户集合
     */
    public static List<PatientBean> getPatientByIdCard(String idCard) {
        return getPatientDao().queryBuilder()
                .where(PatientBeanDao.Properties.IdCard.eq(idCard)).list();
    }

    /**
     * 根据唯一标识查找当前居民（唯一标识可能为身份证或者UUID）
     *
     * @param unique 身份证号
     * @return 唯一标识
     */
    public static PatientBean getPatientByUnique(String unique) {
        if (TextUtils.isEmpty(unique)) {
            return null;
        }
        List<PatientBean> patientBeen = getPatientDao().queryBuilder().whereOr(PatientBeanDao
                .Properties
                .IdCard.eq(unique), PatientBeanDao.Properties.Card.eq(unique)).list();
        if (!patientBeen.isEmpty()) {
            return patientBeen.get(0);
        } else {
            return null;
        }

    }

    /**
     * 根据身份证查询病人
     *
     * @param card 身份证号
     * @return 用户集合
     */
    public static List<PatientBean> getPatientByCard(String card) {
        return getPatientDao().queryBuilder()
                .where(PatientBeanDao.Properties.Card.eq(card)).list();
    }

    /**
     * @param card 身份证/UUID
     */
    public static List<PatientBean> getPatientByCardDouble(String card) {
        return getPatientDao().queryBuilder()
                .whereOr(PatientBeanDao.Properties.Card.eq(card)
                        , PatientBeanDao.Properties.IdCard.eq(card)).list();
    }

    /**
     * 根据会员卡查询病人
     *
     * @param memberShipCard 会员卡
     * @return 用户集合
     */
    public static List<PatientBean> getPatientByShip(String memberShipCard) {
        return getPatientDao().queryBuilder()
                .where(PatientBeanDao.Properties.MemberShipCard.eq(memberShipCard)).list();
    }

    /**
     * 根据测量信息保存病人
     *
     * @param bean 测量bena
     */
    public static void saveMeasure(MeasureDataBean bean) {
        getMeasureDao().update(bean);
    }

    /**
     * 返回尿常规的值
     *
     * @param value 数值
     * @param key   项目
     * @return string值
     */
    public static String getUrineCa(int key, int value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return UiUitls.getString(R.string.default_value);
        }
        String str = "";
        value = value / GlobalConstant.TREND_FACTOR;
        Resources resources = UiUitls.getContent().getResources();
        switch (key) {
            case KParamType.URINERT_CA:
                final String[] cas = resources.getStringArray(R.array.uri_ca);
                //根据协议定义参数
                switch (value) {
                    case -1:
                        str = cas[0];
                        break;
                    case 1:
                        str = cas[1];
                        break;
                    case 2:
                        str = cas[2];
                        break;
                    case 3:
                        str = cas[3];
                        break;
                    case 4:
                        str = cas[4];
                        break;
                    default:
                        break;
                }
                break;
            case KParamType.URINERT_CRE:
                final String[] cre = resources.getStringArray(R.array.uri_cre);
                //根据协议定义参数
                switch (value) {
                    case -1:
                        str = cre[0];
                        break;
                    case 1:
                        str = cre[1];
                        break;
                    case 2:
                        str = cre[2];
                        break;
                    case 3:
                        str = cre[3];
                        break;
                    case 4:
                        str = cre[4];
                        break;
                    default:
                        break;
                }
                break;
            case KParamType.URINERT_ALB:
                final String[] alb = resources.getStringArray(R.array.uri_alb);
                //根据协议定义参数
                switch (value) {
                    case -1:
                        str = alb[0];
                        break;
                    case 1:
                        str = alb[1];
                        break;
                    case 2:
                        str = alb[2];
                        break;
                    case 3:
                        str = alb[3];
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return str;
    }

    /**
     * 临时方法，转换传值给后台处理
     *
     * @param measureDataBean 测量bean类
     * @param paramValue      类型
     * @return 转换后的值
     */
    private static String getOtherValue(MeasureDataBean measureDataBean, int paramValue) {
        String result = "";
        int tempValue = measureDataBean.getTrendValue(paramValue);
        if (tempValue == GlobalNumber.UN_THOUSAND_NUMBER) {
            result = "";
        } else if (tempValue == GlobalNumber.UN_TEN) {
            //超低标准
            result = "-10";
        } else if (tempValue == GlobalNumber.UN_HUNDRED) {
            //超高标准
            result = "100";
        } else {
            result = (float) tempValue / GlobalNumber.HUNDRED_NUMBER + "";
        }
        return result;
    }

    /**
     * 创建数据的方法
     *
     * @param bean  泛型
     * @param clazz 类对象
     * @param <T>   泛型
     */
    public static <T extends Object> void delete(Class<T> clazz, T bean) {
        DBHelper.getDao(clazz).delete(bean);
    }

    /**
     * 创建数据的方法
     *
     * @param bean  泛型
     * @param clazz 类对象
     * @param <T>   泛型
     * @return 返回插入数据在表中的位置
     */
    public static <T extends Object> int create(Class<T> clazz, T bean) {
        return (int) DBHelper.getDao(clazz).insert(bean);
    }

    /**
     * 更新数据的方法
     *
     * @param clazz 类对象
     * @param bean  泛型类
     * @param <T>   泛型
     */
    public static <T extends Object> void update(Class<T> clazz, T bean) {
        DBHelper.getDao(clazz).update(bean);
    }

    /**
     * 创建或者更新数据的方法
     *
     * @param clazz 类对象
     * @param bean  泛型类
     * @param <T>   泛型
     */
    public static <T extends Object> void createOrUpdate(Class<T> clazz, T bean) {
        DBHelper.getDao(clazz).insertOrReplace(bean);
    }

    /**
     * 查询该表全部数据,倒序
     *
     * @return 集合
     */
    public static List<PatientBean> queryAll() {
        return DBHelper.getDao(PatientBean.class).queryBuilder().orderDesc(PatientBeanDao
                .Properties.SortDate).list();
    }

    /**
     * 查询所有居民的条目
     *
     * @return 所有居民的条目
     */
    public static long queryAllPatientSize() {
        return getPatientDao().queryBuilder().count();
    }

    /**
     * 根据bean取出GreenDao数据库中的表名
     *
     * @param str bean类名
     * @return 表名
     */
    public static String getDBName(String str) {
        StringBuffer buffer = new StringBuffer();
        // 转为char数组
        char[] ch = str.toCharArray();
        // 得到大写字母
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] >= 'A' && ch[i] <= 'Z') {
                if (i > 0) {
                    if (!(ch[i - 1] >= 'A' && ch[i - 1] <= 'Z')) {
                        buffer.append("_");
                    }
                }
            }
            buffer.append(ch[i]);
        }
        return buffer.toString().toUpperCase();
    }

    /**
     * 获取数据
     *
     * @param db        数据库
     * @param tableName 表名
     * @param clz       类
     * @param <T>       泛型
     * @return 集合
     */
    public static <T> List<T> loadAll(SQLiteDatabase db, String tableName, Class clz) {
        List<T> entities = new ArrayList<T>();
        if (checkTable(db, tableName)) {
            String sql = "select * from " + tableName;
            Cursor cursor = db.rawQuery(sql, null);
            try {
                while (cursor.moveToNext()) {
                    T obj = (T) clz.newInstance();
                    getEntity(cursor, obj);
                    entities.add(obj);
                }
                return entities;
            } catch (Exception e) {
                e.printStackTrace();
                return entities;
            } finally {
                cursor.close();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据条件获取数据
     *
     * @param db        数据库
     * @param tableName 表名
     * @param clz       类
     * @param <T>       泛型
     * @param limit     限制条数
     * @param offSet    起始位置
     * @return 集合
     */
    public static <T> List<T> loadAllBySelect(SQLiteDatabase db, String tableName, Class clz
            , int offSet, int limit) {
        List<T> entities = new ArrayList<T>();
        if (checkTable(db, tableName)) {
            String sql = "select * from " + tableName + " limit " + limit + " offset " + offSet;
            Cursor cursor = db.rawQuery(sql, null);
            try {
                while (cursor.moveToNext()) {
                    T obj = (T) clz.newInstance();
                    getEntity(cursor, obj);
                    entities.add(obj);
                }
                return entities;
            } catch (Exception e) {
                e.printStackTrace();
                return entities;
            } finally {
                cursor.close();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 获取表单数据总条数
     *
     * @param db        数据库
     * @param tableName 表名
     * @return 表单记录总条数
     */
    public static long getAllRecord(SQLiteDatabase db, String tableName) {
        long count = 0;
        String sql = "select * from " + tableName;
        Cursor cursor = db.rawQuery(sql, null);
        count = cursor.getCount();
        return count;
    }

    /**
     * 检测table是否存在
     *
     * @param db        数据库
     * @param tableName 表名
     * @return 是否已经check表
     */
    public static Boolean checkTable(SQLiteDatabase db, String tableName) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='")
                .append(tableName).append("'");
        Cursor c = db.rawQuery(query.toString(), null);
        if (c.moveToNext()) {
            int count = c.getInt(0);
            if (count > 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 将数据库记录转换为对象
     *
     * @param cursor 指针
     * @param entity 实例对象
     * @param <T>    泛型
     * @return 泛型对象
     */
    public static <T> T getEntity(Cursor cursor, T entity) {
        try {
            Class<?> entityClass = entity.getClass();

            Field[] fs = entityClass.getDeclaredFields();
            for (Field f : fs) {
                String name = f.getName();
                if (name.equalsIgnoreCase("id")) {
                    continue;
                }
                int index = cursor.getColumnIndex(name);
                if (index >= 0) {
                    Method set = getSetMethod(entityClass, f);
                    if (set != null) {
                        String value = cursor.getString(index);

                        if (cursor.isNull(index)) {
                            value = null;
                        }
                        Class<?> type = f.getType();
                        if (type == String.class) {
                            set.invoke(entity, value);
                        } else if (type == int.class || type == Integer.class) {
                            set.invoke(entity, value == null ? (Integer) null
                                    : Integer.parseInt(value));
                        } else if (type == float.class || type == Float.class) {
                            set.invoke(entity, value == null ? (Float) null
                                    : Float.parseFloat(value));
                        } else if (type == Date.class) {
                            try {
                                Date date = stringToDateTime(value);
                                set.invoke(entity, date == null ? (Date) null : date);
                            } catch (Exception e) {
                                Log.e("tdlx", "date error: " + name);
                            }
                        } else if (type == Boolean.TYPE) {
                            set.invoke(entity, value == null ? null : value.equals("1"));
                        } else {
                            Log.e("tdlx", "unKnown type: " + type.getName());
                            set.invoke(entity, value);
                        }
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 字符串日期转换为Data类型
     *
     * @param s 日期
     * @return 日期
     */
    private static Date stringToDateTime(String s) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (s.length() >= pattern.length()) {
            String date = s.substring(0, pattern.length());
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                Date parse = sdf.parse(date);
                return parse;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取set方法
     *
     * @param entity_class 类对象
     * @param f            属性
     * @return 方法
     */
    public static Method getSetMethod(Class<?> entity_class, Field f) {
        String fn = f.getName();
        String mn = "set" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
        try {
            return entity_class.getDeclaredMethod(mn, f.getType());
        } catch (NoSuchMethodException e) {
//            Log.e("tdlx", "Method: " + mn + " not found.");
            return null;
        }
    }

    /**
     * 数据插入
     *
     * @param db        数据库
     * @param entity    实例对象
     * @param selective 是否选择
     * @param tableName 表名
     * @param <T>       泛型
     * @return 泛型对象
     */
    public static <T> long insert(T entity, SQLiteDatabase db, String tableName
            , boolean selective) {
        ContentValues values = getContentValues(entity, selective);
        return db.insert(tableName, null, values);
    }

    /**
     * 将对象转换为ContentValues
     *
     * @param entity    对象
     * @param selective 是否存在值
     * @return ContentValues键值对
     */
    private static ContentValues getContentValues(Object entity, boolean selective) {
        ContentValues values = new ContentValues();
        try {
            Class<?> entityClass = entity.getClass();
            Field[] fs = entityClass.getDeclaredFields();
            for (Field f : fs) {
                if (f.getName().equalsIgnoreCase("daoSession") || f.getType().equals(List.class)) {
                    continue;
                }
                Method get = getGetMethod(entityClass, f);
                if (get != null) {
                    Object o = get.invoke(entity);
                    if (!selective || (selective && o != null)) {
                        String name = DBDataUtil.getDBName(f.getName());
                        Class<?> type = f.getType();
                        if (type == String.class) {
                            values.put(name, (String) o);
                        } else if (type == int.class || type == Integer.class) {
                            values.put(name, (Integer) o);
                        } else if (type == float.class || type == Float.class) {
                            values.put(name, (Float) o);
                        } else if (type == long.class || type == Long.class) {
                            values.put(name, (Long) o);
                        } else if (type == boolean.class || type == Boolean.class) {
                            values.put(name, (Boolean) o);
                        } else if (type == Date.class) {
                            Date date = (Date) o;
                            values.put(name, date.getTime());
                        } else {
                            values.put(name, o.toString());
                        }
                    }
                }
            }
            return values;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 通过反射获得类里面的get方法
     *
     * @param entityClass 实体类
     * @param f           属性
     * @return 方法
     */
    private static Method getGetMethod(Class<?> entityClass, Field f) {
        String fn = f.getName();
        String mn = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
        try {
            return entityClass.getDeclaredMethod(mn);
        } catch (NoSuchMethodException e) {
//            Log.e("tdlx", "Method: " + mn + " not found.");
            return null;
        }
    }

    /**
     * 根据条件查询数据
     *
     * @param start 开始的条数
     * @param count 查询多少条
     * @param item  查询条件
     * @return 查询的数据
     */
    public static List<PatientBean> getPatientBeen(int start, int count, QueryItem item) {
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        QueryBuilder<PatientBean> patientQb = patientDao.queryBuilder();
        SimpleDateFormat dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        if (item.getSex() >= 0) {
            patientQb.where(PatientBeanDao.Properties.Sex.eq(item.getSex()));
        }
        if (!TextUtils.isEmpty(item.getName())) {
            patientQb.whereOr(PatientBeanDao.Properties.Name.like("%" + item.getName() + "%"),
                    PatientBeanDao.Properties.Card.like("%" + item.getName() + "%")).list();
        }
        Date measureStart = item.getMeasureStart();
        if (measureStart != null) {
            String measureStartStr = dateFormat.format(measureStart);
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.MeasureStrTime.ge(measureStartStr));
        }

        Date measureEnd = item.getMeasureEnd();
        if (measureEnd != null) {
            String measureEndStr = dateFormat.format(measureEnd);
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.MeasureStrTime.le(measureEndStr));
        }
        patientQb.offset(start);
        patientQb.limit(count);
        patientQb.orderDesc(PatientBeanDao.Properties.SortDate);
        //查询数据去重
        patientQb.distinct();
        List<PatientBean> list = patientQb.list();
        return list;
    }

    /**
     * 根据条件查询数据
     *
     * @param item 查询条件
     * @return 查询的数据
     */
    public static void deletePatientBeen(QueryItem item) {
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        QueryBuilder<PatientBean> patientQb = patientDao.queryBuilder();

        if (item.getSex() >= 0) {
            patientQb.where(PatientBeanDao.Properties.Sex.eq(item.getSex()));
        }
        if (!TextUtils.isEmpty(item.getName())) {
            patientQb.whereOr(PatientBeanDao.Properties.Name.like("%" + item.getName() + "%"),
                    PatientBeanDao.Properties.Card.like("%" + item.getName() + "%")).list();
        }
        Date measureStart = item.getMeasureStart();
        if (measureStart != null) {
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.Check_day.ge(UiUitls.addDay(
                            measureStart, -1)));
        }

        Date measureEnd = item.getMeasureEnd();
        if (measureEnd != null) {
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.Check_day.le(UiUitls.addDay(
                            measureEnd, 1)));
        }
//        patientQb.buildDelete().executeDeleteWithoutDetachingEntities();
        patientDao.deleteInTx(patientQb.list());
    }

    /**
     * 删除测量数据和用户数据
     * @param item 查询条件
     */
    public static void deleteMeasureDataBeen(QueryItem item) {
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        QueryBuilder<PatientBean> patientQb = patientDao.queryBuilder();

        if (item.getSex() >= 0) {
            patientQb.where(PatientBeanDao.Properties.Sex.eq(item.getSex()));
        }
        if (!TextUtils.isEmpty(item.getName())) {
            patientQb.whereOr(PatientBeanDao.Properties.Name.like("%" + item.getName() + "%"),
                    PatientBeanDao.Properties.Card.like("%" + item.getName() + "%")).list();
        }
        //测量记录限制开始时间
        Date measureStart = item.getMeasureStart();
        if (measureStart != null) {
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.Check_day.ge(UiUitls.addDay(
                            measureStart, -1)));
        }
        //测量记录限制结束时间
        Date measureEnd = item.getMeasureEnd();
        if (measureEnd != null) {
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.Check_day.le(UiUitls.addDay(
                            measureEnd, 1)));
        }
        //分页删除，防止删除数据量过大时候的内存溢出
        //用户数量
        long countPatient = patientQb.distinct().count();
        int offsetPatient = 0;
        int limit = 100;
        List<PatientBean> listPatient = null;
        int cPtient = 0;
        if (countPatient > 0) {
            //整数部分
            long pZ = countPatient / 100;
            //余数部分
            long pX = countPatient % 100;
            //分批删除
            while (cPtient < pZ) {
                cPtient ++;
                listPatient = patientQb.offset(offsetPatient).limit(limit).list();
                if (listPatient != null && listPatient.size() > 0) {
                    for (PatientBean patientBean : listPatient) {
                        getMeasureDao().deleteInTx(getMeasures(patientBean.getIdCard()));
                    }
                    patientDao.deleteInTx(listPatient);
                    listPatient.clear();
                    listPatient = null;
                }
            }
            if (pX > 0) {
                listPatient = patientQb.offset(offsetPatient).limit(limit).list();
                if (listPatient != null && listPatient.size() > 0) {
                    for (PatientBean patientBean : listPatient) {
                        getMeasureDao().deleteInTx(getMeasures(patientBean.getIdCard()));
                    }
                    patientDao.deleteInTx(listPatient);
                    listPatient.clear();
                    listPatient = null;
                }
            }
        }
    }

    /**
     * 根据条件查询一共多少条数据
     *
     * @param item 查询条件
     * @return 查询的数据条数
     */
    public static long getPatients(QueryItem item) {
        PatientBeanDao patientDao = DBDataUtil.getPatientDao();
        QueryBuilder<PatientBean> patientQb = patientDao.queryBuilder();
        SimpleDateFormat dateFormat = UiUitls.getDateFormat(UiUitls.DateState.SHORT);
        if (item.getSex() >= 0) {
            patientQb.where(PatientBeanDao.Properties.Sex.eq(item.getSex()));
        }
        if (!TextUtils.isEmpty(item.getName())) {
            patientQb.whereOr(PatientBeanDao.Properties.Name.like("%" + item.getName() + "%"),
                    PatientBeanDao.Properties.Card.like("%" + item.getName() + "%")).list();
        }
        Date measureStart = item.getMeasureStart();
        if (measureStart != null) {
            String measureStartStr = dateFormat.format(measureStart);
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.MeasureStrTime.ge(measureStartStr));
        }

        Date measureEnd = item.getMeasureEnd();
        if (measureEnd != null) {
            String measureEndStr = dateFormat.format(measureEnd);
            patientQb.join(MeasureDataBean.class, MeasureDataBeanDao.Properties.PatientId)
                    .where(MeasureDataBeanDao.Properties.MeasureStrTime.le(measureEndStr));
        }
        //数据去重
        patientQb.distinct();
        return patientQb.list().size();
    }

    /**
     * 获取居民信息所有数量的方法
     *
     * @return 数量
     */
    public static long getPatientCount() {
        return getPatientDao().queryBuilder().count();
    }

    /**
     * 根据分页查询数据
     *
     * @param start 开始的条数
     * @param count 查询多少条
     * @param value 查询的条件
     * @return 数量
     */
    public static long getPatientCount(int start, int count, String value) {
        QueryBuilder<PatientBean> patientBeanQueryBuilder = getPatientDao().queryBuilder();
        if (!TextUtils.isEmpty(value)) {
            patientBeanQueryBuilder.whereOr(PatientBeanDao.Properties.Name.like("%" + value + "%"),
                    PatientBeanDao.Properties.Card.like("%" + value + "%")).list();
        }
        return patientBeanQueryBuilder.count();
    }
}
