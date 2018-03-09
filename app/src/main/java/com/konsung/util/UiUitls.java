package com.konsung.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.EcgReportPopupWindow;
import com.konsung.defineview.ProgressDialog;
import com.konsung.defineview.RefreshDialog;
import com.konsung.defineview.TipsDialog;
import com.konsung.defineview.WaitingDialog;
import com.konsung.netty.EchoServerEncoder;
import com.konsung.util.NetworkDefine.PatientDefine;
import com.konsung.util.NetworkDefine.ProtocolDefine;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.ParamDefine.RespDefine;
import com.konsung.util.ParamDefine.Spo2Define;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.GlobalNumber;
import com.squareup.okhttp.HttpUrl;
import com.synjones.bluetooth.BmpUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 常用工具类
 */
public class UiUitls {
    private static TipsDialog mTips;
    private static Context mContext;
    private static Handler mHandler;
    //通知对话框
    private static RefreshDialog mDialog;
    //等待的对话框
    private static WaitingDialog mWaitingDialog;
    private static long lastClickTime;
    private static Toast toast;

    /**
     * 初始化
     * @param context 上下文引用
     */
    public static void initData(Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    /**
     * 获取文件md5码值的方法
     * @param file 文件
     * @return MD5值
     * @throws IOException io异常
     */
    public static String getMd5(File file) throws IOException {
        return MD5.getFileMD5String(file);
    }

    /**
     * 获取进程号对应的进程名
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取加密后的字符串
     * @param pw 要转换md5的字符串
     * @return 返回已经转换的md5字符串
     */
    public static String stringMD5(String pw) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = pw.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * @param byteArray 要转换的字符串
     * @return 返回的字符
     */
    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2
        // 次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 获取应用程序的名字
     * @return app名称
     */
    public static String getAppName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = mContext.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 获取包名
     * @return 包名
     */
    public static String getPackName() {
        return mContext.getPackageName();
    }

    /**
     * 获取本应用应用程序的软件版本号
     * @return 软件版本号
     */
    public static int getAppVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取本应用应用程序的软件版本名
     * @return 软件版本名
     */
    public static String getAppVersionName() {
        String version = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            return version;
        }
    }

    /**
     * 安装应用程序的方法
     * @param file 文件
     */
    public static void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android"
                + ".package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(intent);
    }

    /**
     * 关闭数据流的方法
     * @param is 接口对象
     */
    public static void close(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        }
    }

    /**
     * 增加一天时间
     * @param date 时间
     * @param add 增加
     * @return  时间
     */
    public static Date addDay(Date date, int add) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, add);
        date = calendar.getTime();
        return date;
    }
    /**
     * 根据包名获取应用程序的版本号
     * @param packageName 应用包名
     * @return app版本号
     */
    public static int getAppViersion(String packageName) {
        PackageInfo packageInfo = null;
        int code;
        try {
            packageInfo = mContext.getPackageManager()
                    .getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return 0;
        }
        code = packageInfo.versionCode;
        return code;
    }

    /**
     * 获取当前应用的版本名字
     * @return app版本名称
     */
    public static String getappVersionName() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager()
                    .getPackageInfo(getPackName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return "未知版本";
        }
        String name = packageInfo.versionName;
        return name;
    }

    /**
     * 获取上下文
     * @return 上下文引用
     */
    public static Context getContent() {
        return mContext;
    }

    /**
     * 判断是否含特殊字符
     * @param string 字符串
     * @return 是否特殊字符串
     */
    public static boolean isConSpeCharacters(String string) {
        if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*[\\u4e00-\\u9fa5" +
                "]*", "").length() == 0) {
            //不包含特殊字符
            return false;
        }
        return true;
    }

    /**
     * 判断是否含特殊字符
     * @param string 字符串
     * @return 特殊字符
     */
    public static String conSpeCharacters(String string) {
        String s = string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*[\\u4e00-\\u9fa5]*", "");
        if (s.length() == 0) {
            //不包含特殊字符
            return null;
        }
        if (!(s.replace(".", "").length() > 0)) {
            //不包含特殊字符
            return null;
        }
        return s.replace(".", "");
    }

    /**
     * 判断是否连接了wifi
     * @return 是否连接wifi
     */
    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo
                = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 获取测量值
     * @param i 测量值
     * @return 转换后的测量值
     */
    public static float getMesue(float i) {
        return i == GlobalConstant.INVALID_DATA ? GlobalConstant.INVALID_DATA
                : i / GlobalConstant.URITREND_FACTOR;
    }

    /**
     * 获取string资源
     * @param id 资源id
     * @return 资源
     */
    public static String getString(int id) {
        return mContext.getString(id);
    }

    /**
     * 根据用户输入的信息，算出年龄
     * @param dateOfBirth 出生日期
     * @return 年龄
     */
    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    /**
     * 兼容15位身份证号码和18位身份证号码还有yyyyMMdd格式的日期
     * @param birth yyyyMMdd
     * @return 年龄大小
     * @throws Exception
     */
    public static int getAge(String birth) {
        if (birth.length() == GlobalNumber.FIVETEEN_NUMBER) {
            birth = birth.substring(GlobalNumber.SIX_NUMBER, GlobalNumber.TWELVE_NUMBER);
            birth = "19" + birth;
        } else if (birth.length() == GlobalNumber.EIGHTTEEN_NUMBER) {
            birth = birth.substring(GlobalNumber.SIX_NUMBER, GlobalNumber.FOURTEEN_NUMBER);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date birthDay = null;
        try {
            birthDay = sdf.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (birthDay == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now." +
                    "It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * 获取年龄信息
     * @param patientBean 居民数据
     * @return 年龄大小
     * @throws Exception
     */
    public static int getAge(PatientBean patientBean) {
        String card = patientBean.getCard();
        if (card.length() == 18 || card.length() == 15) {
            return getAge(card);
        }

        String idCard = patientBean.getIdCard();
        if (idCard.length() == 18 || idCard.length() == 15) {
            return getAge(idCard);
        }

        return -1;
    }

    /**
     * 根据提供的身份证号查询最新几条测量数据
     * @param idcard 身份证号码
     * @param count 查询多少条数据
     * @return 查询到的list
     */
    public static List<MeasureDataBean> getDataMesure(int count, String idcard) {
        DBDataUtil.getMeasureDao().queryBuilder().where(MeasureDataBeanDao.Properties.Idcard
                .eq(idcard)).limit(count).list();
        return new ArrayList<>();
    }

    /**
     * 过滤特殊字符
     * @param str 字符串
     * @return 字符串
     * @throws PatternSyntaxException 异常
     */
    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]" +
                ".<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断内部存储器剩余空间是否低于200M
     * @return true代表剩余空间不足200M
     */
    public static boolean checkMemorySize() {
        File root = Environment.getDataDirectory();
        StatFs sf = new StatFs(root.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return availCount * blockSize < GlobalConstant.MAX_DATA;
    }

    /**
     * 判断某个路径下文件夹大小是否低于200M
     * @param file 文件夹
     * @return 是否低于200M的标识判断
     */
    public static boolean checkFileIsFull(File file) {
        StatFs sf = new StatFs(file.getPath());
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();
        return availCount * blockSize < GlobalConstant.MAX_DATA;
    }

    /**
     * 显示加载的进度条
     * @param context 上下文
     * @param content 显示的内容
     */
    public static void showProgress(final Context context, final String content) {
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                if (null == mWaitingDialog) {
                    mWaitingDialog = new WaitingDialog(context, content);
                }
                mWaitingDialog.setCancelable(false);
                if (!mWaitingDialog.isShowing()) {
                    mWaitingDialog.show();
                    mWaitingDialog.setText(content);
                }
            }
        });

    }

    /**
     * 隐藏进度条
     */
    public static void hideProgress() {
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                if (null != mWaitingDialog && mWaitingDialog.isShowing()) {
                    mWaitingDialog.dismiss();
                    mWaitingDialog = null;
                }
            }
        });
    }

    /**
     * 根据传入的枚举数据返回不同的时间值
     * @param state 枚举类
     * @return 转换后的日期格式
     */
    public static SimpleDateFormat getDateFormat(DateState state) {
        SimpleDateFormat dateFormat = null;
        switch (state) {
            case LONG:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case SHORT:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case STATISTICAL:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
                break;
            case NOSECOND:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
        }
        return dateFormat;
    }

    /**
     * 显示选择对话框
     * @param context 上下文
     * @param title 对话框的标题
     * @param desc 对话框中间显示的内容
     * @param state 回调的方法
     */
    public static void showTitle(Context context, String title, String desc
            , TipsDialog.UpdataButtonState state) {
        if (null != mTips) {
            mTips = null;
        }
        if (null == mTips) {
            mTips = new TipsDialog(context, title, state);
            mTips.show();
            mTips.setTips(desc);
        }
    }

    /**
     * 隐藏选择对话框
     */
    public static void hideTitil() {
        if (null != mTips && mTips.isShowing()) {
            mTips.dismiss();
            mTips = null;
        }
    }

    /**
     * 显示提示框
     * @param context 上下文引用
     * @param title 显示提示的类容
     * @param desc 显示描述的内容
     */
    public static void showPrompt(Context context, String title, String desc) {
        if (mDialog == null) {
            mDialog = null;
            mDialog = new RefreshDialog(context, title,
                    new RefreshDialog.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
        }
        mDialog.setCancelable(false);
        mDialog.show();
        mDialog.setTips(desc);
        mDialog.setTitle(title);
    }

    /**
     * 检验端口是否合法
     * @param ipPort 端口号
     * @return 检查是否是端口
     */
    public static boolean checkIsPort(String ipPort) {
        try {
            int port = Integer.valueOf(ipPort);
            if (port >= 0 && port <= GlobalNumber
                    .SIXTY_THOUSAND_FIVE_THOUSAND_FIVE_HUNDRED_THIRTY_FIVE_NUMBER) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于区分时间格式的枚举
     * LONG 表示长时间
     * SHORT 表示短时间
     */
    public enum DateState {
        LONG,
        SHORT,
        STATISTICAL,
        NOSECOND //显示年月日时分
    }

    /**
     * 获得可用的内存
     * @return 当前可用内存
     */
    public static long getmemCurrent() {
        long unUsedMem;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        unUsedMem = mi.availMem / GlobalNumber.ONE_THOUSAND_TWENTY_FOUR_NUMBER;
        return unUsedMem;
    }

    /**
     * 获得总内存
     * @return 总内存
     */
    public static long getmemTOTAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), GlobalNumber.EIGHT_NUMBER);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息
        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }

    /**
     * 获取CPU占用率
     * @return CPU占用率
     */
    public static float getProcessCpuRate() {
        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(GlobalNumber.THOUSAND_NUMBER);
        } catch (Exception e) {
        }
        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();
        float cpuRate = GlobalNumber.HUNDRED_NUMBER * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
        return cpuRate;
    }

    /**
     * 获取系统总CPU使用时间
     * @return 时间
     */
    public static long getTotalCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), GlobalNumber.THOUSAND_NUMBER);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[GlobalNumber.THREE_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.FOUR_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.SIX_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.FIVE_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.SEVEN_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.EIGHT_NUMBER]);
        return totalCpu;
    }

    /**
     * 获取应用占用的CPU时间
     * @return 应用占用的CPU时间
     */
    public static long getAppCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat"))
                    , GlobalNumber.THOUSAND_NUMBER);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[GlobalNumber.THREETEEN_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.FOURTEEN_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.FIVETEEN_NUMBER])
                + Long.parseLong(cpuInfos[GlobalNumber.SIXTEEN_NUMBER]);
        return appCpuTime;
    }

    /**
     * 根据路径获取内存状态
     * @param path 文件
     * @return 内存状态
     */
    public static String getSizeInfo(File path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小
        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数
        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量
        // 总空间
        String totalMemory = Formatter.formatFileSize(mContext, totalBlocks * blockSize);
        // 可用空间
        String availableMemory = Formatter.formatFileSize(mContext, availableBlocks * blockSize);
        return "可用空间: " + availableMemory + "  总空间: " + totalMemory;
    }

    /**
     * dp转换成像素
     * @param dp dp值
     * @param resources 资源管理器对象
     * @return px值
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp
                , resources.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 压缩文件-由于out要在递归调用外,所以封装一个方法用来
     * 调用zipFiles(ZipOutputStream out,String path,File... srcFiles)
     * @param zip 要输出的zip文件
     * @param path 源文件目录
     * @param srcFiles 文件集合
     * @throws IOException 异常
     */
    public static void zipFiles(File zip, String path, List<File> srcFiles) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        zipFiles(out, path, srcFiles);
        out.close();
    }

    /**
     * 压缩文件-File
     * @param out 压缩流
     * @param path 输出目录
     * @param srcFiles 被压缩源文件
     */
    public static void zipFiles(ZipOutputStream out, String path, List<File> srcFiles) {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        byte[] buf = new byte[GlobalNumber.ONE_THOUSAND_TWENTY_FOUR_NUMBER];
        try {
            for (int i = 0; i < srcFiles.size(); i++) {
                if (!srcFiles.get(i).isDirectory()) {
                    FileInputStream in = new FileInputStream(srcFiles.get(i));
                    out.putNextEntry(new ZipEntry(path + srcFiles.get(i).getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                } else {
                    File[] files = srcFiles.get(i).listFiles();
                    ArrayList<File> fileList = new ArrayList<File>();
                    for (File file : files) {
                        fileList.add(file);
                    }
                    String srcPath = srcFiles.get(i).getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    out.putNextEntry(new ZipEntry(path + srcPath));
                    zipFiles(out, path + srcPath, fileList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示选择对话框
     * @param context 上下文
     * @param title 对话框的标题
     * @param desc 对话框中间显示的内容
     * @param state 回调的方法
     * @param count 接口对象
     */
    public static void showTitle(final ClickClose count, Context context
            , String title, String desc, TipsDialog.UpdataButtonState state) {
        if (null != mTips) {
            mTips = null;
        }
        if (null == mTips) {
            mTips = new TipsDialog(context, title, state) {
                @Override
                public void btnClose() {
                    if (null != count) {
                        count.close();
                    }
                }
            };
            mTips.show();
            mTips.setTips(desc);
        }
    }

    /**
     * 判断是否当前出现连续快速点击（小于0.5秒每次的点击频率即为快速点击）
     * @return 是否快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < GlobalNumber.FIVE_HUNDRED_NUMBER) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 根据用户传人的值返回字符串
     * @param flag 标识类型
     * @return 按钮标识
     */
    public static String getBtnFlag(GlobalConstant.BtnFlag flag) {
        String str = "1";
        switch (flag) {
            case lift:
                str = "0";
                break;
            case middle:
                str = "1";
                break;
            case right:
                str = "2";
                break;
            default:
                break;
        }
        return str;
    }

    /**
     * 根据sex的int类型返回它的string类型
     * @param sex 性别标识
     * @return 性别
     */
    public static String getSexString(int sex) {
        switch (sex) {
            case 0:
                return getString(R.string.sex_woman);
            case 1:
                return getString(R.string.sex_man);
            default:
                return getString(R.string.sex_woman);
        }
    }

    /**
     * 获取软件名称的方法
     * @return 名称
     */
    public static String getAreaName() {
        String s = UiUitls.getappVersionName();
        return s.substring(s.indexOf("-") + 1).replace("-dev", "");
    }

    /**
     * 用户点击叉的回调方法
     */
    public interface ClickClose {
        /**
         * 回调方法
         */
        public void close();
    }

    /**
     * 匹配ip的规则
     * @param ipAddress ip地址
     * @return 是否为ip地址
     */
    public static boolean isIpv4(String ipAddress) {

        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * 弹出toast （主线程）
     * @param msg 消息
     * @param context 上下文引用
     */
    public static void toast(final Context context, final String msg) {
        if (null == context) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 弹出toast （主线程）
     * @param resId 资源id
     * @param context 上下文引用
     */
    public static void toast(final Context context, final int resId) {
        if (null == context) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 多次点击，弹出很多toast工具
     * @param context 上下文
     * @param message 消息
     */
    public static void toastContent(Context context, String message) {
        if (null == context) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 根据身份证判断性别
     * @param idCard 身份证号码
     * @return 性别
     */
    public static String judgeSexByIdCard(String idCard) {
        String sexFlag = "";
        if (TextUtils.isEmpty(idCard)) {
            sexFlag = "男";
            return sexFlag;
        }
        if (idCard.length() != GlobalNumber.EIGHTTEEN_NUMBER) {
            return "";
        }
        float flag = Float.parseFloat(idCard.substring(GlobalNumber.SIXTEEN_NUMBER
                , GlobalNumber.SEVENTEEN_NUMBER));
        if ((flag % 2) == 1) {
            //奇数为男性 偶数为女性
            sexFlag = "男";
        } else {
            sexFlag = "女";
        }
        return sexFlag;
    }

    /**
     * 判断一个已经赋值了的textview是否超出范围，如超出范围设置红色
     * @param min 最小值
     * @param max 最大值
     * @param textView 显示控件
     */
    public static void compareRange(float min, float max, TextView textView) {
        try {
            String text = textView.getText().toString();
            if (text.equals("-?-") || text.length() == 0) {
                textView.setTextColor(UiUitls.getContent().
                        getResources().getColor(R.color.mesu_text));
                return;
            } else if (text.contains(">")) {
                textView.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else if (text.contains("<")) {
                textView.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                Float value = Float.valueOf(text);
                if (value > max || value < min) {
                    textView.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    textView.setTextColor(UiUitls.getContent().
                            getResources().getColor(R.color.mesu_text));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            textView.setTextColor(UiUitls.getContent().
                    getResources().getColor(R.color.mesu_text));
        }
    }

    /**
     * 判断一个已经赋值了的textview是否超出范围，如超出范围设置红色（血糖特殊性）
     * @param min 最小值
     * @param max 最大值
     * @param textView 显示控件
     */
    public static void compareRangeForSugar(float min, float max, TextView textView) {
        try {
            String text = textView.getText().toString();
            if (text.equals("-?-") || text.length() == 0) {
                textView.setTextColor(UiUitls.getContent().
                        getResources().getColor(R.color.title_text_normal));
                return;
            } else if (text.contains(">")) {
                textView.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else if (text.contains("<")) {
                textView.setTextColor(UiUitls.getContent().getResources()
                        .getColor(R.color.high_color));
            } else {
                Float value = Float.valueOf(text);
                if (value > max || value < min) {
                    textView.setTextColor(UiUitls.getContent().getResources()
                            .getColor(R.color.high_color));
                } else {
                    textView.setTextColor(UiUitls.getContent().
                            getResources().getColor(R.color.title_text_normal));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            textView.setTextColor(UiUitls.getContent().
                    getResources().getColor(R.color.title_text_normal));
        }
    }

    /**
     * 获取性别
     * @param sex 性别标识
     * @return 性别
     */
    public static String getSex(int sex) {
        switch (sex) {
            case 0:
                return mContext.getString(R.string.sex_woman);
            case 1:
                return mContext.getString(R.string.sex_man);
            default:
                return mContext.getString(R.string.sex_unknown);
        }
    }

    /**
     * 切换fragment
     * @param containerViewId Fragment被添加到的容器id
     * @param dfragment 被添加的Fragment
     * @param tag fragment的Tag
     * @param isAddedStack 是否被加入到BackStack 的标识符，在这里一定要被添加的
     * @param fragmentManager fragment管理类
     */
    public static void switchToFragment(int containerViewId, Fragment dfragment
            , String tag, boolean isAddedStack, FragmentManager fragmentManager) {
        // 如果当前的Fragment正在显示就不需要去切换
        if (dfragment.isVisible()) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(containerViewId, dfragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (isAddedStack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    /**
     * 截取视屏名
     * @param strName 视频全名
     * @return 截取后视频名
     */
    public static String separatorVideoName(String strName) {
        String[] videoNameArray = strName.split("\\.");
        return videoNameArray[0];
    }

    /**
     * 修改进度条的提示内容
     * @param text 内容
     */
    public static void setProgressText(String text) {
        if (mWaitingDialog != null) {
            mWaitingDialog.setText(text);
        }
    }

    /**
     * 获取放大100倍前的值
     * @param measure 测量记录
     * @param param 各测量项类型
     * @return 转换后的数据
     */
    public static float getTrendFloat(MeasureDataBean measure, int param) {
        float value = measure.getTrendValue(param);
        if (value != GlobalConstant.INVALID_DATA) {
            if (param == KParamType.IRTEMP_TREND) {
                return NumUtil.trans2FloatValue(value / GlobalConstant.TREND_FACTOR, 1);
            }
            if (param == KParamType.BLOODGLU_BEFORE_MEAL) {
                return NumUtil.trans2FloatValue(value / GlobalConstant.TREND_FACTOR, 1);
            }
            if (param == KParamType.CHOLESTEROL_TREND || param == KParamType.URICACID_TREND) {
                return NumUtil.trans2FloatValue(value / GlobalConstant.TREND_FACTOR, 2);
            }
            if (param == KParamType.BLOOD_WBC) {
                return NumUtil.trans2FloatValue(value / GlobalConstant.WBC_FACTOR, 2);
            }
            if (param == KParamType.LIPIDS_CHOL || param == KParamType.LIPIDS_HDL
                    || param == KParamType.LIPIDS_LDL || param == KParamType.LIPIDS_TRIG
                    || param == KParamType.HBA1C_NGSP || param == KParamType.HBA1C_IFCC
                    || param == KParamType.HBA1C_EAG) {
                float temp;
                if (value == GlobalNumber.UN_TEN) {
                    temp = GlobalNumber.UN_TEN_FLOAT;
                } else if (value == GlobalNumber.UN_HUNDRED) {
                    temp = GlobalNumber.UN_HUNDRED_FLOAT;
                } else {
                    temp = value / GlobalNumber.HUNDRED_NUMBER_FLOAT;
                }
                return temp;
            }
            return NumUtil.trans2FloatValue(value / GlobalConstant.TREND_FACTOR, 1);
        } else {
            return GlobalConstant.INVALID_DATA;
        }
    }

    /**
     * 动态隐藏软键盘
     * @param context 上下文
     * @param view 视图
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 判断是否有网络连接
     * @param context 上下文
     * @return true 为可用，false 为不可用
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取多参数板版本号,已经跟嵌入式那边确定，参数板的版本号只会是 X.X
     * 与后台交互的版本号规则 ( 版本号 = 参数板版本号 * 1000 )
     * @param mContext 上下文
     * @return 版本号
     */
    public static int getAppCSBVersion(Context mContext) {
        String versionName = SpUtils.getSp(mContext, "app_config", "paraBoardVersion", "");
        int version = 0;
        if (versionName.length() > 0) {
            float versionF = Float.valueOf(versionName);
            version = (int) (versionF * GlobalNumber.THOUSAND_NUMBER);
        }
        return version;
    }

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     * 在Android 5.0中已经废弃
     * @param context 上下文
     * @param pageName 包名
     * @return app运行状态
     */
    public static int getAppStatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(GlobalNumber.FIVTY_NUMBER);
        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            //栈里找不到，返回3
            return GlobalNumber.THREE_NUMBER;
        }
    }

    /**
     * 根据异常切换背景图
     * @param value 值
     * @param min 最小值
     * @param max 最大值
     * @param message 提示信息
     * @param tv 控件
     * @param view 背景
     */
    public static void changePicByValue(float min, float max, float value, View view
            , TextView tv, String message) {
        if (value > max || value < min) {
            view.setBackgroundResource(R.drawable.bg_above_high);
            tv.setText(message);
        } else {
            view.setBackgroundResource(R.drawable.bg_above);
            String type = message.substring(0, message.length() - 2);
            tv.setText(type + mContext.getString(R.string.item_normal));
        }
    }

    /**
     * 移除所有子view
     * @param child 子view
     */
    public static void removeAllView(View child) {
        if (child != null) {
            ViewGroup parent = (ViewGroup) child.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }

    /**
     * 初始化系统配置
     */
    public static void initSysConfig() {
        int value = 0;
        //注意：需要优先进行设备配置初始化
        //设备配置初始化
        value = SpUtils.getSpInt(mContext, "sys_config"
                , "device_config", GlobalConstant.DEVICE_CONFIG);
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
        EchoServerEncoder.setDeviceConfig(ProtocolDefine.NET_DEVICE_CONFIG, value);
        //病人信息初始化
        int type = SpUtils.getSpInt(mContext, "sys_config", "type", PatientDefine.ADULT);
        int sex = SpUtils.getSpInt(mContext, "sys_config", "sex", PatientDefine.UNKNOWN);
        int blood = SpUtils.getSpInt(mContext, "sys_config", "blood", PatientDefine.A);
        float weight = SpUtils.getSpFloat(mContext, "sys_config", "weight", (float) 0.0);
        float height = SpUtils.getSpFloat(mContext, "sys_config", "height", (float) 0.0);
        EchoServerEncoder.setPatientConfig((short) type
                , (short) sex, (short) blood, weight, height, (short) 0);
        //心电参数配置初始化
        // 波形速度
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "ecg_wave_speed", value);
        // 增益
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "ecg_gain", value);
        // 导联系统
        value = SpUtils.getSpInt(mContext
                , "sys_config", "ecg_lead_system", EcgDefine.ECG_12_LEAD);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM, value);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_LEAD_SYSTEM, value);
        // 计算导联
        value = SpUtils.getSpInt(mContext
                , "sys_config", "ecg_calc_lead", EcgDefine.ECG_LEAD_II);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_CALC_LEAD, value);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_CALC_LEAD, value);
        // 工频干扰抑制开关
        value = SpUtils.getSpInt(mContext
                , "sys_config", "ecg_hum_filter_mode", EcgDefine.ECG_HUM_ON);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_HUMFILTER_MODE, value);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_HUMFILTER_MODE, value);
        // 滤波模式
        value = SpUtils.getSpInt(mContext
                , "sys_config", "ecg_filter_mode", EcgDefine.ECG_FILTER_DIAGNOSIS);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_FILTER_MODE, value);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_FILTER_MODE, value);
        // PACE开关
        value = SpUtils.getSpInt(mContext
                , "sys_config", "ecg_pace_mode", EcgDefine.ECG_PACE_UNKNOW);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_PACE_SWITCH, value);
        EchoServerEncoder.setEcgConfig(ProtocolDefine.NET_ECG_PACE_SWITCH, value);
        // ST分析开关
        //        value = SpUtils.getSpInt(getApplicationContext(),
        // "sys_config", "ecg_st_analysis",
        //                EcgDefine.ECG_ST_OFF);
        //血氧参数配置初始化
        // 调制音
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "spo2_pitch_tone", value);
        // 灵敏度
        value = SpUtils.getSpInt(mContext
                , "sys_config", "spo2_sensitivity", Spo2Define.SPO2_SENSITIVITY_MIDDLE);
        EchoServerEncoder.setSpo2Config(ProtocolDefine.NET_SPO2_SENSITIVE, value);
        EchoServerEncoder.setSpo2Config(ProtocolDefine.NET_SPO2_SENSITIVE, value);
        // 波形速度
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "spo2_wave_speed", value);
        //血压参数配置初始化
        // 测量模式
        //        value = SpUtils.getSpInt(getApplicationContext(),
        // "sys_config", "nibp_measure_mode",
        //                NibpDefine.MANUAL_MODE);
        // 测量间隔
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "nibp_interval", value);
        //呼吸参数配置初始化
        // 呼吸导联
        value = SpUtils.getSpInt(mContext
                , "sys_config", "resp_lead_type", RespDefine.RESP_LEAD_II);
        EchoServerEncoder.setRespConfig(ProtocolDefine.NET_RESP_LEAD_TYPE, value);
        EchoServerEncoder.setRespConfig(ProtocolDefine.NET_RESP_LEAD_TYPE, value);
        // 窒息报警延迟时间
        value = SpUtils.getSpInt(mContext
                , "sys_config", "resp_apnea_time", RespDefine.RESP_APNEA_DELAY_20S);
        EchoServerEncoder.setRespConfig(ProtocolDefine.NET_RESP_APNEA_TIME, value);
        EchoServerEncoder.setRespConfig(ProtocolDefine.NET_RESP_APNEA_TIME, value);
        // 波形速度
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "resp_wave_speed", value);
        // 波形增益
        //            SpUtils.getSpInt(getApplicationContext(), "sys_config",
        // "resp_wave_gain", value);
        //参数配置初始化
        // 体温类型
        value = SpUtils.getSpInt(mContext
                , "sys_config", "temp_type", TempDefine.TEMP_INFRARED);
        EchoServerEncoder.setTempConfig(ProtocolDefine.NET_TEMP_TYPE, value);
        EchoServerEncoder.setTempConfig(ProtocolDefine.NET_TEMP_TYPE, value);
    }

    /**
     * 根据体重和身高计算BMI
     * @param height 身高
     * @param weight 体重
     * @return BMI值
     */
    public static String countBmi(String height, String weight) {
        String result = "";
        if (TextUtils.isEmpty(height) || TextUtils.isEmpty(weight)) {
            return result;
        }
        double a = Double.parseDouble(height) * GlobalNumber.ZERO_POINT_ZERO_ONE;
        double b = Double.parseDouble(weight);
        result = String.format("%.2f", (b / (a * a)));
        return result;
    }

    /**
     * 通过未销毁的上下文获取 Drawable
     * @param id id
     * @return Drawable
     */
    public static Drawable getDrawable(int id) {
        return mContext.getResources().getDrawable(id);
    }

    /**
     * 获取颜色值
     * @param colorId 颜色id
     * @return 对应的颜色
     */
    public static int getColor(int colorId) {
        return mContext.getResources().getColor(colorId);
    }

    /**
     * 根据参数值获取测量项名称
     * @param param 参数
     * @return 名称
     */
    public static String getParamString(int param) {
        switch (param) {
            case KParamType.ECG_HR:
                return UiUitls.getString(R.string.ecg_hr_other);
            case KParamType.SPO2_TREND:
                return UiUitls.getString(R.string.spo2);
            case KParamType.SPO2_PR:
                return UiUitls.getString(R.string.qc_pr);
            case KParamType.NIBP_SYS:
                return UiUitls.getString(R.string.nibp_sys);
            case KParamType.NIBP_DIA:
                return UiUitls.getString(R.string.nibp_dia);
            case KParamType.IRTEMP_TREND:
                return UiUitls.getString(R.string.qc_tp);
            case KParamType.BLOOD_HCT:
                return UiUitls.getString(R.string.red_blood);
            case KParamType.BLOOD_HGB:
                return UiUitls.getString(R.string.hemoglobin);
            case KParamType.BLOODGLU_AFTER_MEAL:
                return UiUitls.getString(R.string.after_dinner);
            case KParamType.BLOODGLU_BEFORE_MEAL:
                return UiUitls.getString(R.string.before_dinner);
            case KParamType.URICACID_TREND:
                return UiUitls.getString(R.string.qc_ns);
            case KParamType.CHOLESTEROL_TREND:
                return UiUitls.getString(R.string.qc_dgc);
            case KParamType.BLOOD_FAT_CHO:
                return UiUitls.getString(R.string.qc_dgc);
            case KParamType.BLOOD_FAT_HDL:
                return UiUitls.getString(R.string.lipids_hdl_1);
            case KParamType.BLOOD_FAT_LDL:
                return UiUitls.getString(R.string.lipids_ldl_1);
            case KParamType.BLOOD_FAT_TRIG:
                return UiUitls.getString(R.string.lipids_trig_1);
            case KParamType.GHB_HBA1C_NGSP:
                return UiUitls.getString(R.string.ghb_ngsp);
            case KParamType.GHB_HBA1C_IFCC:
                return UiUitls.getString(R.string.ghb_ifcc);
            case KParamType.GHB_EAG:
                return UiUitls.getString(R.string.ghb_eag);
            case KParamType.BLOOD_WBC:
                return UiUitls.getString(R.string.hemameba);
            case KParamType.HEIGHT:
                return UiUitls.getString(R.string.health_height);
            case KParamType.WEIGHT:
                return UiUitls.getString(R.string.health_weight);
            default:
                return "";
        }
    }

    /**
     * 根据参数值获取单位字符串
     * @param param 参数
     * @return 单位
     */
    public static String getValueUnit(int param) {
        switch (param) {
            case KParamType.ECG_HR:
            case KParamType.SPO2_PR:
                return UiUitls.getString(R.string.health_unit_bpm);
            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
                return UiUitls.getString(R.string.health_unit_mmhg);
            case KParamType.TEMP_T1:
            case KParamType.IRTEMP_TREND:
                return UiUitls.getString(R.string.health_unit_degree);
            case KParamType.BLOOD_HCT:
            case KParamType.SPO2_TREND:
            case KParamType.GHB_HBA1C_NGSP:
                return UiUitls.getString(R.string.x_measure_unit_percent);
            case KParamType.BLOODGLU_AFTER_MEAL:
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.URICACID_TREND:
            case KParamType.CHOLESTEROL_TREND:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
                return UiUitls.getString(R.string.health_unit_mol);
            case KParamType.GHB_HBA1C_IFCC:
                return UiUitls.getString(R.string.unit_mmol_mol);
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_BLD:
                return UiUitls.getString(R.string.unit_cells);
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_BIL:
                return UiUitls.getString(R.string.health_unit_umol);
            case KParamType.URINERT_PRO:
            case KParamType.BLOOD_HGB:
                return UiUitls.getString(R.string.unit_mmol_mol);
            case KParamType.BLOOD_WBC:
                return UiUitls.getString(R.string.health_unit_x9);
            case KParamType.GHB_EAG:
                return UiUitls.getString(R.string.health_unit_dl);
            default:
                return "";
        }
    }

    /**
     * 返回参数显示的值的字符串
     * @param param 参数
     * @param value 原始值
     * @return 比例
     * @see KParamType
     */
    public static String getValueAfterFactor(int param, float value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return UiUitls.getString(R.string.invalid_data);
        }
        switch (param) {
            case KParamType.GHB_EAG:
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
                if (value == OverCheckUtil.FLAG_OVER_MAX) {
                    return OverCheckUtil.getOverMaxString(param, value);
                } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                    return OverCheckUtil.getOverMinString(param, value);
                } else {
                    return String.valueOf(value / GlobalConstant.FACTOR);
                }
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                if (value == OverCheckUtil.FLAG_OVER_MAX) {
                    return OverCheckUtil.getOverMaxString(param, value);
                } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
                    return OverCheckUtil.getOverMinString(param, value);
                } else {
                    return String.format(getString(R.string.rule_limit_2_after_point),
                            value / GlobalConstant.FACTOR);
                }
                //下面这几项，都是整数
            case KParamType.ECG_HR:
            case KParamType.SPO2_PR:
            case KParamType.SPO2_TREND:
            case KParamType.NIBP_DIA:
            case KParamType.NIBP_SYS:
            case KParamType.BLOOD_HCT:
                return String.valueOf(((int) value) / GlobalConstant.TREND_FACTOR);
            default:
                return String.valueOf(value / getFactor(param));
        }
    }

    /**
     * 获取参数比例
     * @param param 参数
     * @return 比例
     */
    public static float getFactor(int param) {
        switch (param) {
            case KParamType.URINERT_SG:
                return GlobalConstant.SG_FACTOR;
            case KParamType.BLOOD_WBC:
                return GlobalConstant.WBC_FACTOR;
            default:
                return GlobalConstant.FACTOR;
        }
    }

    /**
     * 对应参数结果是否是整型
     * @param param 参数
     * @return 是否是整型
     */
    public static boolean whetherResultIsInt(int param) {
        if (!getValueAfterFactor(param, GlobalNumber.TEN_THOUSAND_NUMBER).contains(".")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置测量结果,尿常规不能用，该方法不支持没有小数点的显示
     * 如果需要兼容血压，脉率，心率 {@link #setMeasureResult(int, int, TextView, TextView, boolean)}
     * @param param 参数
     * @param bean 结果集
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     */
    public static void setMeasureResult(int param, MeasureDataBean bean, TextView tvName,
            TextView tvValue) {
        float value = bean.getTrendValue(param);
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            tvName.setTextColor(getColor(R.color.measure_value_text_color));
            tvValue.setTextColor(getColor(R.color.measure_value_text_color));
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            tvValue.setText(getValueAfterFactor(param, (int) value));
        }
        UiUitls.setMeasureValueColor(tvValue, tvName, ReferenceUtils.getMinReference(param),
                ReferenceUtils.getMaxReference(param));
    }

    /**
     * 设置测量结果,尿常规不能用，该方法不支持没有小数点的显示
     * 如果需要兼容血压，脉率，心率 {@link #setMeasureResult(int, int, TextView, TextView, boolean)}
     * @param param 参数
     * @param value 结果
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     */
    public static void setMeasureResult(int param, float value, TextView tvName, TextView tvValue) {
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            tvValue.setTextColor(getColor(R.color.measure_value_text_color));
            tvName.setTextColor(getColor(R.color.measure_value_text_color));
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            tvValue.setText(getValueAfterFactor(param, (int) value));
        }
        UiUitls.setMeasureValueColor(tvValue, tvName, ReferenceUtils.getMinReference(param),
                ReferenceUtils.getMaxReference(param));
    }

    /**
     * 设置测量结果,尿常规不能用
     * @param param 参数
     * @param value 结果
     * @param tvName 参数名TextView
     * @param tvValue 结果TextView
     * @param hasPoint 值是否有小数点
     */
    public static void setMeasureResult(int param, int value, TextView tvName, TextView tvValue
            , boolean hasPoint) {
        if (value == GlobalConstant.INVALID_DATA) {
            tvValue.setText(R.string.invalid_data);
            tvValue.setTextColor(getColor(R.color.measure_value_text_color));
            tvName.setTextColor(getColor(R.color.measure_value_text_color));
            return;
        }
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            tvValue.setText(OverCheckUtil.getOverMaxString(param, value));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            tvValue.setText(OverCheckUtil.getOverMinString(param, value));
        } else {
            if (hasPoint) {
                tvValue.setText(String.valueOf(value / getFactor(param)));
            } else {
                tvValue.setText(String.valueOf((int) (value / getFactor(param))));
            }
        }
        UiUitls.setMeasureValueColor(tvValue, tvName
                , ReferenceUtils.getMinReference(param), ReferenceUtils.getMaxReference(param));
    }

    /**
     * 测量值根据参考值范围设置颜色
     * @param tvValue 测量值控件
     * @param tvName 测量值名字的控件
     * @param max 最大值
     * @param min 最小值
     */
    public static void setMeasureValueColor(TextView tvValue, TextView tvName, float min
            , float max) {
        String valueStr = tvValue.getText().toString();
        if (!valueStr.equals(getString(R.string.invalid_data))) {
            try {
                if (valueStr.contains(">") || valueStr.contains("<")) {
                    tvValue.setTextColor(getColor(R.color.error_value_color));
                    tvName.setTextColor(getColor(R.color.error_value_color));
                    return;
                }
                Float value = Float.valueOf(valueStr);
                if (value > max || value < min) {
                    tvValue.setTextColor(getColor(R.color.error_value_color));
                    tvName.setTextColor(getColor(R.color.error_value_color));
                    return;
                }
            } catch (Exception e) {
                tvValue.setTextColor(getColor(R.color.measure_value_text_color));
                tvName.setTextColor(getColor(R.color.measure_value_text_color));
            }
        }
        tvValue.setTextColor(getColor(R.color.measure_value_text_color));
        tvName.setTextColor(getColor(R.color.measure_value_text_color));
    }

    /**
     * 获取艾康U120使用情况
     * @return 是否使用U120
     */
    public static boolean getU120UseStatus() {
        int config = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.SYS_CONFIG
                , GlobalConstant.DEVICE_CONFIG_TAG, GlobalConstant.DEFAULT_CONFIG);
        if ((config & (0x01 << GlobalNumber.TEN_NUMBER)) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开心电报告
     * @param activity activity组件
     */
    public static void openEcgReport(Activity activity) {
        String measureId = EcgRemoteInfoSaveModule.getInstance().rowData.equipmentDataId;
        List<MeasureDataBean> measureList = DBDataUtil.getMeasureDao().queryBuilder()
                .where(MeasureDataBeanDao.Properties.Uuid.eq(measureId)).list();
        boolean isDeleted;
        MeasureDataBean measureDataBean;
        if (null != measureList && measureList.size() > 0) {
            isDeleted = false;
            measureDataBean = measureList.get(0);
        } else {
            isDeleted = true;
            measureDataBean = null;
        }
        View view = activity.getWindow().getDecorView();
        int width = view.getWidth();
        Rect outRect = new Rect();
        view.getWindowVisibleDisplayFrame(outRect);
        int height = outRect.height();
        EcgReportPopupWindow ecgReport = new EcgReportPopupWindow(activity, null
                , measureDataBean, width, height, false, isDeleted);
        ecgReport.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 压缩 文件
     * @param sourceDir 压缩文件路径
     * @param zipFilePath 压缩后的文件路径
     * @return 压缩文件
     * @throws IOException io异常
     */
    public static File doZip(File sourceDir, File zipFilePath) throws IOException {
        File file = sourceDir;
        File zipFile = zipFilePath;
        ZipOutputStream zos = null;
        try {
            // 创建写出流操作
            OutputStream os = new FileOutputStream(zipFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            zos = new ZipOutputStream(bos);
            String basePath = null;
            // 获取目录
            if (file.isDirectory()) {
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos);
        } finally {
            if (zos != null) {
                zos.closeEntry();
                zos.close();
            }
        }
        return zipFile;
    }

    /**
     * @param source 源文件,压缩文件
     * @param basePath 文件路径
     * @param zos 输出流
     * @throws IOException io异常
     */
    private static void zipFile(File source, String basePath
            , ZipOutputStream zos) throws IOException {
        File[] files = null;
        if (source.isDirectory()) {
            files = source.listFiles();
        } else {
            files = new File[1];
            files[0] = source;
        }
        InputStream is = null;
        String pathName;
        byte[] buf = new byte[GlobalNumber.ONE_THOUSAND_TWENTY_FOUR_NUMBER];
        int length = 0;
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    pathName = file.getPath().substring(basePath.length() + 1) + "/";
                    zos.putNextEntry(new ZipEntry(pathName));
                    zipFile(file, basePath, zos);
                } else {
                    pathName = file.getPath().substring(basePath.length() + 1);
                    is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    zos.putNextEntry(new ZipEntry(pathName));
                    while ((length = bis.read(buf)) > 0) {
                        zos.write(buf, 0, length);
                    }
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 将一个输入流转化为字节数组
     * @param in 输入流
     * @return 字节数组
     * @throws IOException io异常
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[GlobalNumber.ONE_THOUSAND_TWENTY_FOUR_NUMBER
                * GlobalNumber.FOUR_NUMBER];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * 判断当前应用是否是debug状态
     * @param context 上下文
     * @return 是否是debug版本
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @param dialog dialog 显示的进度条
     */
    public static void copyFile(String oldPath, String newPath, ProgressDialog dialog) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            long max = oldfile.length();
            long progress = 0;
            if (null != dialog) {
                dialog.setProgressMax((int) max);
            }
            //文件存在时
            if (oldfile.exists()) {
                //读入原文件
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[GlobalNumber.ONE_THOUSAND_TWENTY_FOUR_NUMBER];
                while ((byteread = inStream.read(buffer)) != -1) {
                    //字节数 文件大小
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                    if (null != dialog) {
                        dialog.setProgress((int) progress);
                    }
                }
                inStream.close();
            }
            if (null != dialog) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != dialog) {
                Toast.makeText(UiUitls.getContent(), getString(R.string.copy_fail)
                        , Toast.LENGTH_SHORT).show();
                dialog.setText(getString(R.string.copy_fail));
                dialog.dismiss();
            }
        }
    }

    /**
     * 字符形式转换为Bitmap位图
     * @param pic 图片文件的字符串形式
     * @return Bitmap位图
     */
    public static Bitmap stringToBmp(String pic) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(pic)) {
            try {
                byte[] bytes = pic.getBytes("ISO-8859-1");
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 去空格函数
     * @param str 源字符串
     * @return 去掉空格后的字符串
     */
    public static String removeSpace(String str) {
        str = str.replace(" ", "");
        return str;
    }

    /**
     * 通过未销毁的上下文获取 Dimens
     * @param id id
     * @return Dimens
     */
    public static float getDimens(int id) {
        return mContext.getResources().getDimension(id);
    }

    /**
     * 将身份证图片转化为BitMap
     * @param pic 图片字符串形式
     * @param fileName 文件名
     */
    public static void savePhoto(String pic, String fileName) {
        if (!TextUtils.isEmpty(pic)) {
            try {
                byte[] pictureBytes = pic.getBytes("ISO-8859-1");
                BmpUtil.saveHeadPic(pictureBytes, fileName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将身份证图片转化为BitMap
     * @param pic 图片字符串形式
     * @param fileName 文件名
     */
    public static void savePhotoDefaultCharset(String pic, String fileName) {
        if (!TextUtils.isEmpty(pic)) {
            byte[] pictureBytes = pic.getBytes();
            BmpUtil.saveHeadPic(pictureBytes, fileName);
        }
    }

    /**
     * 将图片转utf-8字符串保存
     * @param bitmap 图片
     * @return 图片字符串
     */
    public static String getStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, GlobalNumber.HUNDRED_NUMBER, baos);
        byte[] datas = baos.toByteArray();
        return new String(datas);
    }

    /**
     * 将图片转换为二进制字节数组
     * @param pic 图片字符串形式
     * @return 字节数组
     */
    public static byte[] getByteArrByPicStr(String pic) {
        byte[] headBytes = null;
        if (!TextUtils.isEmpty(pic)) {
            try {
                headBytes = pic.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return headBytes;
    }

    /**
     * 判断网络是否有效
     * @param host 外网地址
     * @param pingCount ping次数
     * @return 网络是否有效
     */
    public static boolean isNetAvilable(String host, int pingCount) {
        boolean isAvilable = true;
        Process process = null;
        String command = "ping -c " + pingCount + " " + host;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                return false;
            }
            int status = process.waitFor();
            if (status == 0) {
                isAvilable = true;
            } else {
                isAvilable = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isAvilable;
    }

    /**
     * 抛出runnable任务
     * @param task 任务类对象
     */
    public static void post(Runnable task) {
        mHandler.post(task);
    }

    /**
     * 抛出runnable
     * @param task 任务
     * @param time 延时时长
     */
    public static void postDelayed(Runnable task, long time) {
        mHandler.postDelayed(task, time);
    }

    /**
     * 移除任务
     * @param task 任务对象
     */
    public static void removeCallbacks(Runnable task) {
        mHandler.removeCallbacks(task);
    }

    /**
     * 通过线程池执行任务
     * @param task 任务对象
     */
    public static void postShortThread(Runnable task) {
        ThreadManager.getInstance().createShortPool().execute(task);
    }

    /**
     * 通过线程池执行任务
     * @param task 任务对象
     */
    public static void postLongThread(Runnable task) {
        ThreadManager.getInstance().createLongPool().execute(task);
    }

    /**
     * 取消该任务的执行
     * @param task 任务对象
     */
    public static void cancelThread(Runnable task) {
        ThreadManager.getInstance().createShortPool().cancel(task);
    }

    /**
     * 将多个字节数组按顺序合并
     * @param data 二维字节数组
     * @return 字节数组
     */
    public static byte[] byteArraysToBytes(byte[][] data) {

        int length = 0;
        for (int i = 0; i < data.length; i++) {
            length += data[i].length;
        }
        byte[] send = new byte[length];
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                send[k++] = data[i][j];
            }
        }
        return send;
    }

    /**
     * 设置窗体透明度
     * @param activity 页面对象
     * @param alpha 透明度
     * 1 完全不透明
     * 0 完全透明
     */
    public static void setWindowAlpha(Activity activity, float alpha) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    /**
     * 结合Ip和port来判断域名的有效性
     * @param ip ip地址
     * @param port 端口号
     * @return 域名地址是否有效
     */
    public static boolean isValidAddress(String ip, String port) {
        String preHttp = "http://";
        String signFlag = ":";
        HttpUrl parsed = HttpUrl.parse(preHttp + ip + signFlag + port);
        if (parsed == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取有效的身份证信息 （1.2.2以后的版本兼容）
     * @param patientBean 居民信息
     * @return 转换后的数据
     */
    public static String getValidCard(PatientBean patientBean) {
        String idCard = patientBean.getIdCard();
        String card = patientBean.getCard();
        if (TextUtils.isEmpty(idCard)) {
            return card;
        } else {
            if (idCard.length() == 15 || idCard.length() == 18) {
                return idCard;
            } else {
                return card;
            }
        }
    }
}
