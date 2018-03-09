package com.konsung.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.greendao.dao.UserBeanDao;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.UserBean;
import com.konsung.sqlite.DBHelper;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.SpUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 内容提供 查询: app更新地址, 系统更新地址, 云平台地址, 设备号, 尿常规测量项配置, 设备配置参数,
 * 测量项配置参数, 快速检测配置参数
 */
public class ServiceProvider extends ContentProvider {

    //创建两个常量来区分不同的URI请求
    private static final int CURRENT_PATIENT = 1; //当前居民身份证
    private static final int UPDATE_MEASURE = 2; //更新测量数据
    private static final int UPDATE_MEASURE_ID = 3; //更新测量数据
    private static final int ADD_MEASURE = 4; //新增测量数据
    private static final int DELETE_MEASURE = 5; //删除测量数据
    private static final int QUERY_MEASURE_DATA = 6; //查询测量数据
    private static final int QUERY_MEASURE_DATA_TEN = 7; //查询测量数据
    private static final int QUERY_MEASURE_PEOPLE_REPORT = 8; //查询测量记录人数
    private static final int QUERY_IDCARD_BEING = 9; //查询身份证号码是否已经存在
    private static final int QUERY_NEW_MEASURE = 10; //查询最新的测量记录
    private static final int ADD_ALL_MEASURE = 11; //批量添加测量数据
    private static final int ADD_ALL_PATIENT = 12; //批量添加居民档案
    private static final int QUERY_LOGIN_INFO = 13; //查询当前医生
    private static final int QUERY_DEVICE_CODE = 14; //查询设备号

    private static final String MEASUREDATABEAN = "MeasureDataBean"; //类名
    private static UriMatcher uriMatcher; //填充匹配URI的容器

    //填充UriMatcher对象，其中以第二个参数区分数据类型
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.CURRENT_PATIENT,
                CURRENT_PATIENT);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.UPDATE_MEASURE,
                UPDATE_MEASURE);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.UPDATE_MEASURE_ID,
                UPDATE_MEASURE_ID);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.ADD_MEASURE,
                ADD_MEASURE);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.DELETE_MEASURE,
                DELETE_MEASURE);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_MEASURE_DATA,
                QUERY_MEASURE_DATA);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_MEASURE_DATA_TEN,
                QUERY_MEASURE_DATA_TEN);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_MEASURE_PEOPLE_REPORT,
                QUERY_MEASURE_PEOPLE_REPORT);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_IDCARD_BEING,
                QUERY_IDCARD_BEING);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_NEW_MEASURE,
                QUERY_NEW_MEASURE);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.ADD_ALL_MEASURE,
                ADD_ALL_MEASURE);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_LOGIN_INFO,
                QUERY_LOGIN_INFO);
        uriMatcher.addURI(GlobalConstant.AUTHORITY, GlobalConstant.QUERY_DEVICE_CODE,
                QUERY_DEVICE_CODE);
    }

    //构建key值数组
    private String[] keyArr = new String[]{GlobalConstant.CONFIG};

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        MatrixCursor matrixCursor = new MatrixCursor(keyArr, 1);
        String idcard = "";
        switch (uriMatcher.match(uri)) {
            case CURRENT_PATIENT: //当前居民
                String currentPatient = "";
                idcard = SpUtils.getSp(getContext(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.ID_CARD, "");
                List<PatientBean> list = DBDataUtil.getPatientDao().queryBuilder()
                        .where(PatientBeanDao.Properties.IdCard.eq(idcard)).list();

                if (list != null && list.size() > 0) {
                    currentPatient = new Gson().toJson(list.get(0));
                }
                matrixCursor.addRow(new Object[]{currentPatient});
                break;
            case QUERY_MEASURE_DATA: //查询当前居民最新的测量数据
                Calendar c = Calendar.getInstance(); //得到当前日期和时间
                c.set(Calendar.HOUR, 0); //把当前时间小时变成0
                c.set(Calendar.MINUTE, 0); //把当前时间分钟变成0
                c.set(Calendar.SECOND, 0); //把当前时间秒数变成0
                c.set(Calendar.MILLISECOND, 0); //把当前时间毫秒变成0
                Date startDate = c.getTime();
                MeasureDataBeanDao measureDataDao = DBDataUtil.getMeasureDao();

                String idCard = SpUtils.getSp(getContext(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.ID_CARD, "");

                MeasureDataBean measureDataBean = null;
                if (!TextUtils.isEmpty(idCard)) {
                    List<MeasureDataBean> query = measureDataDao.queryBuilder()
                            .orderDesc(MeasureDataBeanDao.Properties.MeasureTime)
                            .where(MeasureDataBeanDao.Properties.Idcard.eq(idCard))
                            .where(MeasureDataBeanDao.Properties.MeasureTime.ge(startDate))
                            .list();
                    if (query != null && query.size() > 0) {
                        measureDataBean = query.get(0);
                    }
                }

                String data;
                if (measureDataBean == null) {
                    data = "";
                } else {
                    data = new Gson().toJson(measureDataBean);
                }
                matrixCursor.addRow(new Object[]{data});
                break;
            case QUERY_MEASURE_DATA_TEN: //查询最近的10条测量记录
                //由于10条心电数据过大，采用分次加载。
                Integer startIndex;
                Integer length;
                try {
                    startIndex = Integer.valueOf(projection[0]);
                    length = Integer.valueOf(projection[1]);
                } catch (Exception e) {
                    startIndex = 0;
                    length = 5;
                }

                String json = "";
                idcard = SpUtils.getSp(getContext(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.ID_CARD, "");
                if (!TextUtils.isEmpty(idcard)) {
                    List<MeasureDataBean> query = DBDataUtil.getMeasureDao()
                            .queryBuilder()
                            .offset(startIndex)
                            .limit(length)
                            .orderDesc(MeasureDataBeanDao.Properties.MeasureTime)
                            .where(MeasureDataBeanDao.Properties.Idcard.eq(idcard))
                            .list();

                    if (query != null && query.size() > 0) {
                        Gson gson = new Gson();
                        json = gson.toJson(query);
                    }
                }
                matrixCursor.addRow(new Object[]{json});
                break;
            case QUERY_MEASURE_PEOPLE_REPORT: // 查询每个月测量人数的总数

                break;
            case QUERY_IDCARD_BEING: //查询身份证号码是否已存在

                break;
            case QUERY_NEW_MEASURE: //查询最新的测量记录
                break;

            case QUERY_LOGIN_INFO:
                String userName = GlobalConstant.USERNAME;
                String pwd = GlobalConstant.PASSWORD;
                QueryBuilder<UserBean> builder = DBHelper.getInstance().getUserDao()
                        .queryBuilder();
                builder.and(UserBeanDao.Properties.Username.eq(userName)
                        , UserBeanDao.Properties.Password.eq(pwd));
                List<UserBean> userBeen = builder.list();
                if (userBeen != null && userBeen.size() > 0) {
                    UserBean userBean = userBeen.get(0);
                    if (userBean.getPassword().equals(pwd)) {
                        String info = new Gson().toJson(userBean);
                        matrixCursor.addRow(new Object[]{info});
                    }
                }

                break;
            case QUERY_DEVICE_CODE:
                String deviceCode = SpUtils.getSp(getContext(), GlobalConstant.APP_CONFIG
                        , GlobalConstant.APP_CODING, "");
                matrixCursor.addRow(new Object[]{deviceCode});
                break;
            default:
                throw new IllegalArgumentException("Unkonw Uri:" + uri.toString());
        }
        //返回结果Cursor
        return matrixCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return -1;
    }
}