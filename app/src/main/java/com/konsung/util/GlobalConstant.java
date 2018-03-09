package com.konsung.util;

import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.decard.healthcard.WSRead;
import com.example.mtreader.Mi32Api;
import com.greendao.dao.DaoMaster;
import com.huada.healthcard.HuaDaDeviceLib;
import com.konsung.bean.MeasureDataBean;
import com.konsung.defineview.EcgViewFor12;

/**
 * 全局常量类
 * 定义程序用到的全局常量
 * @author ouyangfan
 * @version 0.0.1
 */
public class GlobalConstant {

    //记录是否添加了本地用户和密码
    public static final String USER_ADD = "user_add";
    //应用信息
    public static final String VER = "1.0.0";
    //修正次数
    public static final int NUMS = 1;
    //device统一的升级区域
    public static final String DEVICE_NAME = "COMMON";
    public static final String REFRESH_IP = "refresh_ip";
    public static final String REFRESH_ADRESS = "yun.konsung.net";
    //首先项的参数
    public static final String APP_CONFIG = "app_config";
    //sp身份证key值
    public static final String ID_CARD = "idcard";
    //参数板key值
    public static final String PARA_KEY = "paraBoardVersion";
    //具名姓名key值
    public static final String PATIENT_NAME = "name";
    //测量bean的id key值
    public static final String MEASURE_ID = "measureId";
    public static final String APP_CODING = "app_coding";
    //数据库版本号
    public static final int DATABASE_VERSION = DaoMaster.SCHEMA_VERSION;
    public static final String APP_ISCODING = "app_iscoding";
    public static final String SERVICE_IP = "ip";
    //云平台ip标记
    public static final String CLOUD_IP = "cloudip";
    //云平台端口标记
    public static final String CLOUD_IP_PORT = "cloudipport";
    public static final String CIP_PORT = "8080";
    //服务器地址标记
    public static final String SERVER_ADDRESS = "ServerAddress";
    //云平台地址标记
    public static final String CLOUD_ADDRESS = "CloudAddress";
    //上传地址的详细地址
    public static final String DETAIL_ADDRESS = "detail_address";
    //机构名称
    public static final String ORGANIZATION_NAME = "organization_name";
    public static final String AUTOUPLOAD = "auto_upload";
    public static final String URINETYPE = "urine_type";
    //服务器默认地址
    public static final String SERVER_FIX_ADDRESS =
            "/imms-web/upLoadData/villageHealthPort";
    //云平台默认地址
    public static final String CLOUD_FIX_ADDRESS =
            "/cloud/services/VillageHealthPort";
    public static final int TREND_FACTOR_SG = 1000;
    //开机动画的文件名后缀
    public static final CharSequence BOOTANIMATION = ".zip";
    //开机音乐的文件名后缀
    public static final CharSequence BOOTAUDIO = ".mp3";
    //开机动画的目标文件
    public static final String BOOTANIMATIONFILE = "/data/local/bootanimation.zip";
    //开机音乐目标文件
    public static final String BOOTAUDIOFILE = "/data/local/bootaudio.mp3";
    // 参数版本升级
    public static boolean UPLAOD_CSBU = true;
    public static String HTTP = "http://";
    public static String KUNSONGYITIJI = "konsungyitiji";
    //血糖默认选中按钮
    public static BtnFlag CURRENT_SELETE_BTN = BtnFlag.lift;
    // 帮助文档名称
    public static final String KONSUNG_HELP_PDF_NAME = "konsung_help.pdf";

    //判断当前acivity是否正常运行
    public static boolean IS_RUNING_ACTIVITY = false;
    //软件更新的存放端口的标记
    public static final String REFRESH_IP_PORT = "refresh_ip_port";
    //默认的软件更新端口
    public static final String REFRESH_ADRESS_PORT = "8080";
    //服务器地址存放的标记
    public static final String IP_PROT = "ip_port";
    //内存储过低的标准3GB大小值
    public static final long MAX_DATA = 3221225472L;
    //测量数据上限标准
    public static final long MEASURE_MAX = 3000;
    //上传成功的标记
    public static final String UPLOADFLAG = "1";
    //上传失败的标准
    public static final String UNUPLOADFLAG = "0";
    //上传成功的标记
    public static final int SUCCESS = 0;
    //上传失败的标记
    public static final int FAIL = 1;
    //上传超时的标志
    public static final int TIMEOUT = 2;
    //上传是我器失败，抛出异常
    public static final int SERVER_FAIL = 3;
    //没有测量数据的标记
    public static final int IS_NULL = 4;
    //记录后台是否正在自动上传数据
    public static boolean IS_UPLOADING = false;
    public static String ECGDIAGNOSERESULT = "";
    public static boolean PENSON_RECORD = false;
    public static int REFRESH_ISDOAD = 0;

    // 设备配置信息
    // 根据协议对应关系如下：bit0-KSM5;bit1-EmpUi;bit2-GA7;bit3-IDA007;
    // bit4-HTD8819;bit5-TH809;bit6-BeneCheckGlu;bit7-BeneCheck;bit8-OGM111;
    // bit9-URIT-31;bit10-Mission U120;bit11-HemoCue WBC;bit12-HemoCue Hb;
    // bit13-URIT-12;bit14-Mission Hb;
//    public static final int DEVICE_CONFIG = 0x4519;
    //演示版本默认配置信息全部选中，方便演示来回切换勾选配置信息(仅供演示版本)
//    public static final int DEVICE_CONFIG = 0x7fff;
    public static final int DEVICE_CONFIG = 0xc09f;
    //默认参数配置项
    public static final int PARAM_CONFIG = 0x3f;
    //无效小数值
    public static final float INVALID_DECIMAL_VALUE = -0.1f;
    //测试账号
    public static String TEST_USER = "";
    public static String TEST_PASSWORD = "";

    //探头脱落状态
    public static int LEFF_OFF = -1000;

    // 网络数据的放大倍数，趋势数据统一为10000
    public static final int TREND_FACTOR = 100;
    // 尿常规网络数据的放大倍数，趋势数据统一为100
    public static final int URITREND_FACTOR = 100;
    //测量数据转换倍数
    public static final float SWITCH_VALUE = 100f;
    public static final float THOSOUND_VALUE = 1000.0f;

    // 网络端口号
    public static final int PORT = 6613;
    // 无效趋势值
    public static final int INVALID_TREND_DATA = -1000;
    // 网络数据包最大长度
    public static final int MAX_PACKET_LEN = 1024;
    // 网络命令字
    public static final byte PARA_STATUS = 0x12;
    public static final byte NET_TREND = 0x51;
    public static final byte NET_WAVE = 0x52;
    public static final byte NET_ECG_CONFIG = 0x21;
    public static final byte NET_RESP_CONFIG = 0x22;
    public static final byte NET_TEMP_CONFIG = 0x23;
    public static final byte NET_SPO2_CONFIG = 0x24;
    public static final byte NET_NIBP_CONFIG = 0x25;

    // 12导心电结果包
    public static final byte NET_12LEAD_DIAG_RESULT = 0x60;

    public static final byte NET_DEVICE_CONFIG = 0x70;

    public static final byte NET_ECG_PO5 = 0x0b;
    public static final byte NET_ECG_NO5 = 0x0c;

    public static final byte NET_PATIENT_CONFIG = 0x11;
    public static final int STRING_MAX_LEN = 60;

    //默认为25mm/s
    public static float ECG_MM = 1.0f;
    //默认为x1
    public static float ECG_XX = 1.0f;

    // 模拟一次ECG测量时间10000(10s)
    public static final int MEASURE_TIME = 10000;
    // 无效趋势值
    public static final int INVALID_DATA = -1000;
    //ECG导联数，默认为三，在设置中可以设置
    public static final int ECG_NUM = 12;
    public static final int HR_ALARM_HIGH = 100;
    public static final int HR_ALARM_LOW = 60;
    //记录是否更新的数据
    public static final String APP_ISREFRESH = "app_isrefresh";

    public static int TEMP_STATUS = -1000;

    public static final String CRASHLOGPATH = "KonsungLog";

    public static final String HELP_PDF_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() +
            "/KonsungDocuments/";

    //选项按钮颜色
    public static String[] APPLIST_COLOR = new String[]{"#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8",
            "#4ca4a8"};

    public static final String IP_DEFAULT = "139.196.214.201";
    //默认的服务器端口
    public final static String PORT_DEFAULT = "9028";
    //云平台的ip
    public static final String CIP = "yun.konsung.net";
    public static final String PRINTER_IP = "172.27.35.24";
    public static String LANGUAGE = "简体中文";
    //    public static int LANGUAGE_POSITION = 0;

    public static String ORG_NAME;
    public static String ORG_ID;
    public static String USERNAME;
    public static String EPMID;
    public static String EMP_NAME = "";
    public static String PASSWORD = "";
    //当天的测量记录
    public static MeasureDataBean todayMeasureRecord;
    //临时保存单次记录的测量值 是否创建新纪录
    public static boolean IS_NEW_RECORD = true;
    public static int SPO2_VALUE = INVALID_DATA;
    public static int SPO2_PR_VALUE = INVALID_DATA;
    public static int ECG_PR_VALUE = INVALID_DATA;
    public static int ECG_BR_VALUE = INVALID_DATA;
    public static int NIBP_SYS_VALUE = INVALID_DATA;
    public static int NIBP_DIA_VALUE = INVALID_DATA;
    public static int NIBP_MAP_VALUE = INVALID_DATA;
    public static int NIBP_PR_VALUE = INVALID_DATA;
    public static float TEMP_VALUE = INVALID_DATA;
    public static float IR_TEMP_VALUE = INVALID_DATA;
    public static float BLOOD_GLU_VALUE = INVALID_DATA;
    public static float URICACID_TREND = INVALID_DATA;
    public static float CHOLESTEROL_TREND = INVALID_DATA;
    public static int URINE_LEU_VALUE = INVALID_DATA;
    public static int URINE_NIT_VALUE = INVALID_DATA;
    public static int URINE_UBG_VALUE = INVALID_DATA;
    public static int URINE_PRO_VALUE = INVALID_DATA;
    public static float URINE_PH_VALUE = INVALID_DATA;
    public static double URINE_SG_VALUE = INVALID_DATA;
    public static int URINE_BLD_VALUE = INVALID_DATA;
    public static int URINE_KET_VALUE = INVALID_DATA;
    public static int URINE_BIL_VALUE = INVALID_DATA;
    public static int URINE_GLU_VALUE = INVALID_DATA;
    public static int URINE_VC_VALUE = INVALID_DATA;
    public static float URINE_ALB_VALUE = INVALID_DATA;
    public static int URINE_ASC_VALUE = INVALID_DATA;
    public static float URINE_CRE_VALUE = INVALID_DATA;
    public static float URINE_CA_VALUE = INVALID_DATA;
    public static float BLOOD_WBC_VALUE = INVALID_DATA;
    public static float BLOOD_HGB_VALUE = INVALID_DATA;
    public static int BLOOD_HCT_VALUE = INVALID_DATA;
    public static float URIC_ACID_VALUE = INVALID_DATA;
    public static float CHOLESTEROL_VALUE = INVALID_DATA;

    public static float HBA1C_NGSP = INVALID_DATA;
    public static float HBA1C_IFCC = INVALID_DATA;
    public static float HBA1C_EAG = INVALID_DATA;
    public static float URINE_MA_CR = INVALID_DATA;

    //BMI临时存储变量,默认为空
    public static String BMI = "";
    //身高
    public static String HEIGHT = "";
    //体重
    public static String WEIGHT = "";

    //0 餐前 1 餐后
    public static String BLOODGLUSTATE = "0";

    public static ActionBar bar;

    public static int GET_ALL_INDEX = 0;
    public static boolean GET_ALL_SELECTOR = false;

    public static int GET_ALL_SELETING = 0;
    //记录用户是否有测量数据
    public static int CREATE_MEASURE = 0;
    public static boolean CLICK_MEUES = false;
    //用于控制血氧在页面外的信息是否提示
    public static boolean IS_MEUSE = false;

    //记录是否申请过usb
    public static boolean PRINT_ACTION = false;
    //记录是否正在打印
    public static boolean IS_PRINTING = false;

    public static WSRead HEALTH_CARD_WSREAD = null;
    public static float LIPIDS_CHOL_VALUE = INVALID_DATA;
    public static float LIPIDS_TRIG_VALUE = INVALID_DATA;
    public static float LIPIDS_HDL_VALUE = INVALID_DATA;
    public static float LIPIDS_LDL_VALUE = INVALID_DATA;

    // 血脂四项报警限(数值来源于艾康血脂仪说明书)
    public static final float LIPIDS_CHOL_ALARM_HIGH = 5.69f;
    public static final float LIPIDS_CHOL_ALARM_LOW = 2.59f;
    public static final float LIPIDS_TRIG_ALARM_HIGH = 1.70f;
    public static final float LIPIDS_TRIG_ALARM_LOW = 0.56f;
    public static final float LIPIDS_HDL_ALARM_HIGH = 2.07f;
    public static final float LIPIDS_HDL_ALARM_LOW = 0.91f;
    public static final float LIPIDS_LDL_ALARM_HIGH = 3.16f;
    public static final float LIPIDS_LDL_ALARM_LOW = 1.29f;

    //血脂显示值
    public static final String LIPIDS_CHOL_ALARM_ABOVE = ">12.93";
    public static final String LIPIDS_CHOL_ALARM_BELOW = "<2.59";
    public static final String LIPIDS_TRIG_ALARM_ABOVE = ">7.34";
    public static final String LIPIDS_TRIG_ALARM_BELOW = "<0.51";
    public static final String LIPIDS_HDL_ALARM_ABOVE = ">2.59";
    public static final String LIPIDS_HDL_ALARM_BELOW = "<0.39";
    public static final String LIPIDS_LDL_ALARM_ABOVE = ">3.16";
    public static final String LIPIDS_LDL_ALARM_BELOW = "<1.29";

    public static final float LIPIDS_CHOL_ALARM_ABOVE_VALUE = 12.93f;
    public static final float LIPIDS_CHOL_ALARM_BELOW_VALUE = 2.59f;
    public static final float LIPIDS_TRIG_ALARM_ABOVE_VALUE = 7.34f;
    public static final float LIPIDS_TRIG_ALARM_BELOW_VALUE = 0.51f;
    public static final float LIPIDS_HDL_ALARM_ABOVE_VALUE = 2.59f;
    public static final float LIPIDS_HDL_ALARM_BELOW_VALUE = 0.39f;
    public static final float LIPIDS_LDL_ALARM_ABOVE_VALUE = 3.16f;
    public static final float LIPIDS_LDL_ALARM_BELOW_VALUE = 1.29f;

    //糖化血红蛋白测量项显示值
    public static final String NGSP_ABOVE = ">15.0";
    public static final String NGSP_BELOW = "<3.0";
    public static final String IFCC_ABOVE = ">140.4";
    public static final String IFCC_BELOW = "<9.3";
    public static final String EAG_ABOVE = ">383.8";
    public static final String EAG_BELOW = "<39.4";

    //华大一体机读卡器
    public static HuaDaDeviceLib ID_HEALTH_CARD = null;
    //记录是否插入打印机
    public static boolean IS_PRINTING_INTO = false;
    //记录是非有插入扫描枪
    public static boolean IS_INSERT_SCANNER = false;
    //换行的常量
    public static String LINE = "\n";
    //记录是否测量数据
    public static boolean ISMEASURE = false;
    public static String LOG_TAG = "MYAPPTAG";

    /**
     * 标记btn选中的方法
     */
    public enum BtnFlag {
        lift,
        middle,
        right
    }

    public static final String HEALTH_REPORT_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static final String HEALTH_REPORT_NAME = "health_report.jpg";
    public static final String HEALTH_REPORT_TWO = "health_report2.jpg";
    public static final String HEALTH_ECG_NAME = "ecg_report.jpg";
    //判断主界面是否存活
    public static boolean ACTIVITY_IS_RUNNING = true;

    //血糖上下限值
    public static final float BLOODGLUMAX = 7.0f;
    public static final float BLOODGLUMIN = 2.8f;

    //尿酸上下限值
    public static final float NSMAX = 0.42f;
    public static final float NSMIN = 0.2f;

    //总胆固醇上下限值
    public static final float CHOLMAX = 5.2f;
    public static final float CHOLMIN = 0f;

    //血红蛋白上下限值
    public static final float XHDBMAX = 10.5f;
    public static final float XHDBMIN = 8.1f;

    //红细胞积压上下限值
    public static final float HXBMAX = 50f;
    public static final float HXBMIN = 40f;

    //血脂 总胆固醇上下限值
    public static final float XZCHOLMAX = 5.69f;
    public static final float XZCHOLMIN = 0f;

    //血脂 甘油三脂上下限值
    public static final float TGMAX = 1.7f;
    public static final float TGMIN = 0.56f;

    //血脂 高密度脂蛋白上下限值
    public static final float HDLMAX = 2.07f;
    public static final float HDLMIN = 0.91f;

    //血脂 低密度脂蛋白上下限值
    public static final float LDLMAX = 3.16f;
    public static final float LDLMIN = 0f;

    //判断是否从CheckIdCardFragnent进入新增用户
    public static boolean IS_CHECK_IDCARD = false;
    //测量页面操作的数据bean
    public static MeasureDataBean CURRENT_MEASURE_DATA = null;
    //记录线程是否停止
    public static boolean isUploadStop = false;
    //记录判断是否是管理员账号下的应用设置页面
    public static boolean isAdministraor = false;
    //导出文件的名称
    public static final String PATIENT_FILE_NAME = "konsung_personinfo";
    public static final String MEASURE_FILE_NAME = "konsung_checkdata";
    //判断是否在健康体检报告页面
    public static boolean isInHealthReportFragment = false;
    //全部下载判断(是否取消下载)
    public static boolean isStopDownload = false;
    //页面业务逻辑的刷新标识，用于用户新增后用户列表页刷新至首页展示（用于
    // 区分和测量列表页返回后的刷新）
    public static boolean isFirstPageRefresh = true;
    //血红蛋白转换倍数值
    public static final float TREND_HGB = 0.621f;
    //导入和导出标识，用于升级判断
    public static boolean isImportAndPush = false;
    //参数配置项的参数
    public static final String PARAM_CONFIGS = "param_config";
    public static final String PARAM_KEY = "param";

    //接口控制版本
    public static final String INTEFACE_VERSION = "1.2.0";
    public static final String AUTHORITY_DEVICE_CONFIG = "content://com.konsung" +
            ".factorymaintain/deviceConfig";
    public static final String AUTHORITY_MEASURE_CONFIG = "content://com.konsung" +
            ".factorymaintain/measureConfig";
    public static final String AUTHORITY_URT_CONFIG = "content://com.konsung" +
            ".factorymaintain/urtSetting";
    public static final String QUICK_CURRENT_PATIENT
            = "content://com.konsung.healthfile/currentPatient";
    public static final String CONFIG = "config"; //数据类型 配置参数
    public static final float FACTOR = 100f;
    public static final float SG_FACTOR = 1000f;
    //根据APPDevice协议，上传值是10^6，显示值时10^9,需要先除以1000，再除以比例
    public static final float WBC_FACTOR = 1000 * FACTOR;
    public static final String SYS_CONFIG = "sys_config"; //系统首先项的参数
    public static final String DEVICE_CONFIG_TAG = "device_config"; //记录系统信息的tag
    public static final int DEFAULT_CONFIG = 0xFFFFFFFF;

    //appdevice部分设备传值
    public static int valueMin = -10;
    public static int valueMax = -100;

    public static final int BACKGROUND_COLOR = 0xb0000000;

    //查询测量记录条数
    public static final int MEASURE_NUM = 10;

    //保存EditText视图，用来隐藏键盘
    public static View currentView = null;
    //判断是否在快检页面
    public static boolean isInQuickPage = false;
    public static final String UPDATE_DATA = "update_data_all"; //升级兼容数据
    //病人id公用字段key
    public static final String idKey = "id";
    //当前用户名key值
    public static final String CURRENT_NAME_KEY = "name";
    //当前用户头像key值
    public static final String CURRENT_HEAD_PIC_KEY = "head_pic";
    //当前用户类型key值
    public static final String CURRENT_TYPE = "patient_type";
    //当前用户性别key值
    public static final String CURRENT_SEX = "patient_sex";
    //标识bmi的标识符
    public static final int BMI_FLAG = 10000;
    public static long CLICK_TIMES = 1000;
    //在线视频url后缀
    public static final String ONLINE_VIDEO_URL = "/imms-web/ksVideo/findVideo";
    //明泰读取心一康读卡器
    public static Mi32Api MI32API = null;
    //连接明泰读取心一康读卡器状态
    public static boolean IS_LINK_MI32API_SUCCESS = false;
    //分隔符
    public static final String CHAR_SEMICOLON = ";";
    //判断是否进行了参数配置操作
    public static boolean isParamOperate = false;
    //转换数量级
    public static final float TEN_VALUE = 10f;
    public static final float TEN_VALUE_MIN = -10f;
    public static final float HUNDRED_VALUE = -100f;
    public static final int HUNDRED_VALUE_INT = -100;
    //快检页面跳转传值标识
    public static final String QUICK_FLAG = "quickFlag";
    //快检页面跳转值
    public static String quickFlag = "";

    public static final String ADDR_JSON = "addr.json"; //本地地区文件名
    //会员卡号的保存标识
    public static final String MEMBER_CARD = "memshipCard";
    //标识
    public static final String NAME = "name";
    public static final String CARD = "card"; //身份证
    public static final String SHIP = "ship"; //会员卡
    public static final String PATIENT_TYPE = "patientType";
    public static final String SEX_TYPE = "sexType";
    public static final String UUID = "uuid";
    //appDevice包名
    public static final String DEVICE_PACKAGE_NAME = "org.qtproject.qt5.android.bindings";
    //测量页面用户切换数据刷新标识
    public static final String MEASURE_USER_SWITCH = "refresh_data";
    //内存里面app路径存取flag
    public static final String APP_PATH = "app_path";
    //内存里面
    public static final String DEVICE_PATH = "device_path";
    //内容提供者
    public static final String AUTHORITY = "com.konsung.dev"; //对应包名
    public static final String UPDATE_MEASURE = "updateMeasure"; //更新测量数据
    public static final String UPDATE_MEASURE_ID = "updateMeasureId"; //以id更新数据库
    public static final String ADD_MEASURE = "addMeasure"; //添加测量数据
    public static final String DELETE_MEASURE = "deleteMeasure"; //删除测量数据
    public static final String QUERY_MEASURE_DATA = "queryMeasureData"; //查询测量数据
    public static final String QUERY_MEASURE_DATA_TEN = "queryMeasureData10";
    public static final String QUERY_MEASURE_PEOPLE_REPORT = "queryMeasurePeopleReport";
    public static final String QUERY_IDCARD_BEING = "queryIdcardBeing";
    public static final String QUERY_NEW_MEASURE = "QueryNewMeasure";
    public static final String ADD_ALL_MEASURE = "AddAllMeasure";
    public static final String ADD_ALL_PATIENT = "AddAllPatient";
    public static final String QUERY_LOGIN_INFO = "currentDoctor";
    public static final String QUERY_DEVICE_CODE = "deviceCode";
    public static final String AIDL_SERVICE_NAME = "com.konsung.aidl";
    public static final String URT_ITEM = "urtItem"; //尿常规设备配置
    public static final String URT_SETTING = "urtSetting"; //尿常规项配置 11-13-14项
    public static final String MEASURE_CONFIG = "measureConfig"; //测量项配置（身高体重）
    public static final String QUICK_CONFIG = "quickConfig"; //快速检测配置（身高体重）
    public static final String CURRENT_PATIENT = "currentPatient"; //当前居民key值
    public static final String CARD_INPUT = "card_input"; //身份证必填
    //标识更新是否是出现在首页
    public static boolean mainPageUpdate = false;
    //当前用户性别值 默认女
    public static int currentSex = 0;
    //记录血氧仪器是否插入等状态,默认状态为0
    public static int spoState = 0;
    public static final String BYTESPARA1 = "bytespara1";
    public static final String INTPARA1 = "intpara1";
    public static final String INTPARA2 = "intpara2";
    public static final String PARCE1 = "parce1";
    //波形View的临时存储值
    public static EcgViewFor12 ecgViewFor12 = null;
    //记录版本升级时候的旧数据版本号
    public static int oldDbVersion = 0;
    //标识体检报告页面是否处于选择删除状态
    public static boolean isDeleteState = false;
    //当身份证和会员卡号均不填写的情况下生成的假会员卡号前缀
    public static final String preStr = "ks";
    //没有输入年龄
    public static int NONE_AGE = -2;
    //标识数据库升级完成
    public static boolean dataBaseUpgradeFinish = false;
    //省份保存标识
    public static String province = "province";
    //城市保存标识
    public static String city = "city";
    //地址保存标识
    public static String district = "district";
    //标识导入导出取消标记
    public static boolean isCancel = false;
    //ksboot-image在存储器中的文件位置
    public static String csbPath = Environment.getExternalStorageDirectory()
            + "/Konsung/AppUpdate/KS-boot.image";
    //标识本地数据库是否已经存在
    public static boolean isExitsDataBaseInUsbMem = false;
    //标识新增用户页面是否已经销毁
    public static boolean isAddPatientActivityDestyoy = true;
    //标识是否有新增用户
    public static boolean isAddUser = true;
    //标识是否从体检报告详情页返回
    public static boolean isBackFromReportDetail = false;
    //标识全部上传完毕后的数据清空刷新
    public static boolean isAllUploadDataRefresh = false;
}