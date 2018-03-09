package com.konsung.bean.Request;

/**
 * Created by xiangshicheng on 2017/7/3 0003.
 * 测量数据字段
 */

public class VillageHealthPortRequest {
    /**不可空 Y**/
    public String id = "";
    //医生id Y
    public String empId = "";
    /**申请单号,该版本不需要**/
    public String reqno = "";
    /**健康档案号--可为空**/
    public String healthfileNo = "";
    /**基本信息id--可为空，该版本不需要**/
    public String fileNumber = "";
    /**医生编码--必填 Y**/
    public String doctorCode = "";
    /**机构编码--不能为空 Y**/
    public String orgCode = "";
    /**设备编码--不能为空 Y**/
    public String deviceCode = "";
    /**不能为空,数据上传时间,精确到秒,2017-06-12 15:00:01 Y**/
    public String uploadDate = "";
    /**检查日期--不能为空 Y**/
    public String checkDate = "";
    /**数据上传区域 Y**/
    public String area = "";
    /**设备模式：一期二期**/
    public String equipmentModel = "";
    /**key Y**/
    public String key = "";
    /**健康卡号**/
    public String jkno = "";
    /**姓名首字母**/
    public String pym = "";
    /**身高**/
    public String height = "";
    /**体重**/
    public String weight = "";
    /**体质**/
    public String bmi = "";
    /**腰围**/
    public String waist = "";
    /**臀围**/
    public String hipline = "";
    /**病人类型 Y**/
    public String patientType = "";
    /**接口版本，定义为对应的软件发布版本 Y**/
    public String version = "";
    /**设备软件版本，定义为对应的软件发布版本 Y**/
    public String deviceVersion = "";
    /**总胆固醇--单独**/
    public String chol = "";
    /**心电文件**/
    public String heartFile = "";
    /**心电上传方式**/
    public String heartType = "";
    /**数据上传--用户基本信息对象 Y**/
    public UploadPersonInfoDto personinfo = new UploadPersonInfoDto();
    /**数据上传--血生化数据**/
    public UploadBloodBiochemistry ubb = new UploadBloodBiochemistry();
    /**数据上传--血脂四项**/
    public UploadBloodLipidFourDto ubsd = new UploadBloodLipidFourDto();
    /**数据上传--血压数据**/
    public UploadBloodPressureDto ubpd = new UploadBloodPressureDto();
    /**数据上传--血糖数据**/
    public UploadBloodSugarDto ubsud = new UploadBloodSugarDto();
    /**数据上传-- 五分类**/
    public UploadFiveWayDto ufwd = new UploadFiveWayDto();
    /**数据上传--血红蛋白数据**/
    public UploadHemoglobinDto uhd = new UploadHemoglobinDto();
    /**数据上传--血氧数据**/
    public UploadOxygenDto uod = new UploadOxygenDto();
    /**数据上传--体温**/
    public UploadTemperature temp = new UploadTemperature();
    /**数据上传--三分类**/
    public UploadThreeWayDto utd = new UploadThreeWayDto();
    /**数据上传--尿常规数据**/
    public UploadUrineDto uud = new UploadUrineDto();
    /**数据上传--心电**/
    public UploadWaveDto uwd = new UploadWaveDto();
    /**数据上传--胎儿心跳**/
    public UploadFetalHeartDto ufhd = new UploadFetalHeartDto();
    /**数据上传--血常规**/
    public UploadXCGDto uxd = new UploadXCGDto();
    /**糖化血红蛋白**/
    public UploadGlyHemogloDto ughd = new UploadGlyHemogloDto();
    /**白细胞**/
    public HemamebaDto hemameba = new HemamebaDto();

    //数据上传--心电
    public class UploadWaveDto {
        /**心率--没有测试心电时，心率为空**/
        public String hr = "";
        /**心电图诊断结果**/
        public String anal = "";
        public String resp_rr = "";
        /**采样频率/秒**/
        public String sample = ""; //
        /**+0.5mv对应的数值**/
        public String p05 = "";
        /**-0.5mv对应的数值**/
        public String n05 = "";
        /**波形持续时间**/
        public String duration = "";
        /**PR间隔**/
        public String PR = "";
        /**QRS间隔**/
        public String QRS = "";
        /**QT间隔**/
        public String QT = "";
        /**QTC间隔**/
        public String QTC = "";
        /**P电轴**/
        public String P = "";
        /**QRS电轴**/
        public String QRSZ = "";
        /**T电轴**/
        public String T = "";
        /**V5(mV)**/
        public String RV5 = "";
        /**V1(mV)**/
        public String SV1 = "";
        public String ecg_i = "";
        public String ecg_ii = "";
        public String ecg_iii = "";
        public String ecg_avr = "";
        public String ecg_avf = "";
        public String ecg_avl = "";
        public String ecg_v1 = "";
        public String ecg_v2 = "";
        public String ecg_v3 = "";
        public String ecg_v4 = "";
        public String ecg_v5 = "";
        public String ecg_v6 = "";
    }

    //数据上传--血糖对象
    public class UploadBloodSugarDto {
        /**血糖**/
        public String glu = "";
        /**血液尿酸**/
        public String bsPh = "";
        /**血糖测量方式**/
        public String gluStyle = "";
        /*总胆固醇(单测项)*/
        public String totalCholesterol = "";
    }

    //血氧
    public class UploadOxygenDto {
        /**血氧饱和度**/
        public String spo2 = "";
        /**血氧脉率**/
        public String oxPr = "";
    }

    //血压
    public class UploadBloodPressureDto {
        /**收缩压--测量了血压后不能为空**/
        public String sbp = "";
        /**舒张压--测量了血压后不能为空**/
        public String dbp = "";
        /**平均压--测量了血压后不能为空**/
        public String mbp = "";
        /**左收缩压**/
        public String leftSbp = "";
        /**左舒张压**/
        public String leftDbp = "";
        /**左平均压**/
        public String leftMbp = "";
        /**右收缩压**/
        public String rightSbp = "";
        /**右舒张压**/
        public String rightDbp = "";
        /**右平均压**/
        public String rightMbp = "";
        /**血压脉率**/
        public String bpPr = "";
    }

    //体温
    public class UploadTemperature {
        /**体温--如果测量了体温则不能为空**/
        public String temp = "";
    }

    //尿常规
    public class UploadUrineDto {
        /**尿常规酸碱度-无单位**/
        public String urinePh = "";
        /**尿常规尿胆原-无单位**/
        public String urineUbg = "";
        /**尿常规隐血-无单位**/
        public String urineBld = "";
        /**尿常规尿蛋白-无单位**/
        public String urinePro = "";
        /** 尿常规酮体-无单位**/
        public String urineKet = "";
        /**尿常规亚硝酸盐-无单位**/
        public String urineNit = "";
        /**尿常规尿糖-无单位**/
        public String urineGlu = "";
        /**尿常规胆红素-无单位**/
        public String urineBil = "";
        /**尿常规白细胞-无单位**/
        public String urineLeu = "";
        /**尿常规尿比密-无单位**/
        public String urineSg = "";
        /**尿常规维生素c-无单位**/
        public String urineVc = "";
        /**尿常规肌酐-无单位**/
        public String urineCre = "";
        /**尿常规尿钙-无单位**/
        public String urineCa = "";
        /**微量白蛋白-无单位**/
        public String urineMa = "";
    }

    //血红蛋白
    public class UploadHemoglobinDto {
        /**血常规--血红蛋白--同血常规**/
        public String hgb = "";
        /**红细胞积压值**/
        public String htc = "";
    }

    //血脂四项
    public class UploadBloodLipidFourDto {
        /**甘油三酯**/
        public String flipidsTrig = "";
        /**高密度脂蛋白**/
        public String flipidsHdl = "";
        /**低密度脂蛋白**/
        public String flipidsLDL = "";
        /**总胆固醇**/
        public String cholesterol = "";
    }

    //胎儿心率
    public class UploadFetalHeartDto {
        /**胎儿心率**/
        public String  fetalHeartNum = "";
    }

    //三分类
    public class UploadThreeWayDto {
        /** 小细胞群 --109/L**/
        public String smallCellGroup = "";
        /** 中间细胞群 --109/L**/
        public String middleCellGroup = "";
        /** 大细胞群 --109/L**/
        public String bigCellGroup = "";

    }
    //血常规
    public class UploadXCGDto {
        /**白细胞数目**/
        public String WBC = "";
        /**淋巴细胞数目**/
        public String Lymph1 = "";
        /**中性粒细胞计数**/
        public String Mid1 = "";
        /**单核细胞计数**/
        public String Gran1 = "";
        /**淋巴细胞比值**/
        public String Lymph2 = "";
        /**中性粒细胞比例**/
        public String Mid2 = "";
        /**单核细胞比例**/
        public String Gran2 = "";
        /**红细胞计数**/
        private String RBC = "";
        /**血红蛋白浓度**/
        public String HGB = "";
        /**红细胞压积**/
        public String HCT = "";
        /**平均红细胞体积**/
        public String MCV = "";
        /**平均红细胞血红蛋白含量**/
        public String MCH = "";
        /**平均红细胞血红蛋白浓度**/
        private String MCHC = "";
        /**红细胞分布宽度-SD值**/
        public String RDW_CV = "";
        /**红细胞分布宽度-CV值**/
        public String RDW_SD = "";
        /**血小板计数**/
        public String PLT = "";
        /**平均血小板体积**/
        public String MPV = "";
        /**血小板分布宽度**/
        public String PDW = "";
        /**血小板压积**/
        public String PCT = "";
        /**嗜酸性细胞比例**/
        public String ROEC = "";
        /**嗜碱性细胞比例**/
        public String POBC = "";
        /**嗜酸性细胞数**/
        public String EL = "";
        /**嗜碱性细胞数**/
        public String BL = "";
        /**有核红细胞比例**/
        public String PLCR = "";
        /**有核红细胞比例**/
        public String RON = "";
        /**有核红细胞数10*9/L**/
        public String NRBC = "";
    }

    //生化数据
    public class UploadBloodBiochemistry {

        /**丙氨酸氨基转氨酶（ALT）--肝功能**/
        public String alanine = "";
        /**总胆红素（TBIL）--肝功能**/
        public String totalBilirubin = "";
        /**天门冬氨酸氨基转移酶（AST）--肝功能**/
        public String aspartate = "";
        /**直接胆红素--肝功能**/
        public String directBilirubin = "";

        /**总蛋白--肝功能**/
        public String totalProtein = "";

        /**白蛋白--肝功能**/
        public String albumin = "";

        /**尿酸--肾功能**/
        public String uricAcid = "";

        /**肌酐--肾功能**/
        public String creatinine = "";

        /**尿素--肾功能**/
        public String urea = "";

        /**葡萄糖--同血糖**/
        public String glu = "";

        /**甘油三酯--同血脂四项**/
        public String flipidsTrig = "";

        /**高密度脂蛋白--同血脂四项**/
        public String flipidsHdl = "";

        /**总胆固醇--同血脂四项**/
        public String cholesterol = "";
    }

    //糖化血红蛋白
    public class UploadGlyHemogloDto {
        /**糖化血红蛋白 --%**/
        public String hba1cNgsp = "";
        public String hba1cIfcc = "";
        public String hba1cEag = "";
    }

    //五分类
    public class UploadFiveWayDto {
        /**中性粒细胞--109/L**/
        public String neutrophil = "";
        /**淋巴细胞--109/L**/
        public String  lymphocyte = "";
        /**嗜酸性粒细胞--109/L**/
        public String eosnophils = "";
        /**嗜碱性粒细胞--109/L**/
        public String basophilic = "";
        /**单核细胞--109/L**/
        public String monocyte = "";
    }

    //个人基本信息
    public class UploadPersonInfoDto {
        /**身份证--不能为空**/
        public String cardNo = "";
        /**姓名--不能为空**/
        public String name = "";
        /**性别--不能为空 0 1 2 9**/
        public String sex = "";
        /**出生日期**/
        public String birthDay = "";
        //联系方式
        public String telePhone = "";
        //年龄
        public String age = "";
        //居住地址
        public String address = "";
        //会员卡
        public String membershipCard = "";
        //头像
        public byte[] headPortrait;
        //血型
        public String bloodType = "";
        //备注
        public String desc = "";
    }

    //白细胞
    public class HemamebaDto {
        public String hemameba = "";
    }
}
