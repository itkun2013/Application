package com.konsung.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greendao.dao.MeasureDataBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.fileselect.MyFileManager;
import com.konsung.fileselect.MyImportFileManager;
import com.konsung.util.DBDataUtil;
import com.konsung.util.ExcelUtils;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.ReflectUtil;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.konsung.util.GlobalConstant.MEASURE_FILE_NAME;
import static com.konsung.util.GlobalConstant.PATIENT_FILE_NAME;

/**
 * SettingFragment
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    //设置
    @InjectView(R.id.ll_sys_setting)
    LinearLayout llSetting;
    @InjectView(R.id.sys_setting_iv)
    ImageView ivSetting;
    @InjectView(R.id.sys_setting_tv)
    TextView tvSetting;
    @InjectView(R.id.system_right_iv_1)
    ImageView ivArrow1;
    //导入
    @InjectView(R.id.ll_sys_pull)
    LinearLayout llPull;
    @InjectView(R.id.sys_pull_iv)
    ImageView ivPull;
    @InjectView(R.id.sys_pull_tv)
    TextView tvPull;
    @InjectView(R.id.system_right_iv_2)
    ImageView ivArrow2;
    //导出
    @InjectView(R.id.ll_sys_push)
    LinearLayout llPush;
    @InjectView(R.id.sys_push_iv)
    ImageView ivPush;
    @InjectView(R.id.sys_push_tv)
    TextView tvPush;
    @InjectView(R.id.system_right_iv_3)
    ImageView ivArrow3;
    //关于
    @InjectView(R.id.ll_system_about)
    LinearLayout llAbout;
    @InjectView(R.id.sys_about_iv)
    ImageView ivAbout;
    @InjectView(R.id.sys_about_tv)
    TextView tvAbout;
    @InjectView(R.id.system_right_iv_4)
    ImageView ivArrow4;
    //厂家维护
    @InjectView(R.id.ll_system_factory_protect)
    LinearLayout llFactory;
    @InjectView(R.id.sys_fp_iv)
    ImageView ivFactory;
    @InjectView(R.id.sys_fp_tv)
    TextView tvFactory;
    @InjectView(R.id.system_right_iv_5)
    ImageView ivArrow5;
    @InjectView(R.id.line_factory)
    View lineFactory;
    //视频培训
    @InjectView(R.id.ll_system_video_practise)
    LinearLayout llVideo;
    @InjectView(R.id.sys_vp_iv)
    ImageView ivVideo;
    @InjectView(R.id.sys_vp_tv)
    TextView tvVideo;
    @InjectView(R.id.system_right_iv_6)
    ImageView ivArrow6;
    @InjectView(R.id.line_video)
    View lineVideo;
    //皮肤设置
    @InjectView(R.id.ll_system_skin_set)
    LinearLayout llSkin;
    @InjectView(R.id.sys_skin_iv)
    ImageView ivSkin;
    @InjectView(R.id.sys_skin_tv)
    TextView tvSkin;
    @InjectView(R.id.system_right_iv_7)
    ImageView ivArrow7;
    @InjectView(R.id.skin_line)
    View lineSkin;
    //网络检测
    @InjectView(R.id.ll_system_net_check)
    LinearLayout llNet;
    @InjectView(R.id.sys_net_iv)
    ImageView ivNet;
    @InjectView(R.id.sys_net_tv)
    TextView tvNet;
    @InjectView(R.id.system_right_iv_8)
    ImageView ivArrow8;
    @InjectView(R.id.net_line)
    View lineNet;
    public static final String ID_CARD = "idCard";
    public static final String UUID = "uuid";
    public static final int FILE_RESULT_CODE = 1;
    public static final int FILE_RESULT_IMPORT = 2;
    public static final int FILE_RESULT_LOG = 3;
    public static final String CSV_TYPE = ".csv";
    public static final String PERSONINFO_CSV_FILE = "konsung_personinfo.csv";
    public static final String MEASURE_CSV_FILE = "konsung_checkdata.csv";
    public static final String PERSONINFO_XLS_FILE = "konsung_personinfo.xls";
    public static final String MEASURE_XLS_FILE = "konsung_checkdata.xls";
    //旧有的导出数据中版本号字段最大长度
    public static final int MAX_DATA_VERION_LENGTH = 8;
    //用来记录加载的fragment
    private ConfigureFragment fragment1;
    private BaseFragment fragment2;
    private BaseFragment fragment3;
    private BaseFragment fragment4;
    private BaseFragment fragment5;
    private BaseFragment fragment6;
    private BaseFragment fragment7;
    private BaseFragment fragment8;
    private View mCurrentView;
    // 是否为第一次进入系统设置页面
    private boolean isFirstIn = true;
    // 根布局view
    private View view;
    //当前选中的ImageTextButton的id
    private int currentBtnSelected = R.id.ll_sys_setting;
    private BaseFragment mCurrentFragment;
    //数量大小
    private final int num = 50;
    //字节长度
    private final int byteLenth = 1024;
    private Context context = null;

    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, null);
        ButterKnife.inject(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getActivity();
        initView();
        return view;
    }

    /**
     * 初始化view
     */
    private void initView() {
        llSetting.setOnClickListener(this);
        llPull.setOnClickListener(this);
        llPush.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llFactory.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llSkin.setOnClickListener(this);
        llNet.setOnClickListener(this);

        if (GlobalConstant.TEST_PASSWORD.equals(getString(R.string.test_password))) {
            llFactory.setVisibility(View.GONE);
            llVideo.setVisibility(View.GONE);
            llSkin.setVisibility(View.VISIBLE);
            llNet.setVisibility(View.VISIBLE);
            lineNet.setVisibility(View.VISIBLE);
            lineSkin.setVisibility(View.VISIBLE);
            lineFactory.setVisibility(View.GONE);
            lineVideo.setVisibility(View.GONE);
        } else {
            llFactory.setVisibility(View.VISIBLE);
            llVideo.setVisibility(View.VISIBLE);
            llSkin.setVisibility(View.GONE);
            llNet.setVisibility(View.GONE);
            lineSkin.setVisibility(View.GONE);
            lineNet.setVisibility(View.GONE);
            lineFactory.setVisibility(View.VISIBLE);
            lineVideo.setVisibility(View.VISIBLE);
        }
        //默认进入setting界面时，看到的是关于页面
        setSelected(R.id.ll_sys_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sys_setting:
                setSelected(R.id.ll_sys_setting);
                if (fragment1 == null) {
                    fragment1 = new ConfigureFragment();
                }
                switchToFragment(R.id.right_fragment, fragment1, "mConfigurament");
                mCurrentView = v;
                break;
            //导出
            case R.id.ll_sys_push:
                GlobalConstant.isImportAndPush = true;
                setSelected(R.id.ll_sys_push);
                Intent intent = new Intent(context, MyFileManager.class);
                startActivityForResult(intent, FILE_RESULT_CODE);
                break;
            //导入
            case R.id.ll_sys_pull:
                GlobalConstant.isImportAndPush = true;
                setSelected(R.id.ll_sys_pull);
                Intent imoptIntent = new Intent(context, MyImportFileManager.class);
                startActivityForResult(imoptIntent, FILE_RESULT_IMPORT);
                break;
            case R.id.ll_system_about:
                setSelected(R.id.ll_system_about);
                if (fragment4 == null) {
                    fragment4 = new AboutFragment();
                }
                switchToFragment(R.id.right_fragment, fragment4, "aboutFragment");
                mCurrentView = v;
                break;
            case R.id.ll_system_factory_protect:
                setSelected(R.id.ll_system_factory_protect);
                if (fragment5 == null) {
                    fragment5 = new FactoryProtectFragment();
                }
                switchToFragment(R.id.right_fragment, fragment5, "factoryProtectFragment");
                mCurrentView = v;
                break;
            case R.id.ll_system_video_practise:
                setSelected(R.id.ll_system_video_practise);
                if (fragment6 == null) {
                    fragment6 = new VideoHelpFragment();
                }
                switchToFragment(R.id.right_fragment, fragment6, "videoHelpFragment");
                mCurrentView = v;
                break;
            case R.id.ll_system_skin_set:
                setSelected(R.id.ll_system_skin_set);
                if (fragment7 == null) {
                    fragment7 = new SkinSettingFragment();
                }
                switchToFragment(R.id.right_fragment, fragment7, "skinSettingFragment");
                mCurrentView = v;
                break;
            case R.id.ll_system_net_check:
                setSelected(R.id.ll_system_net_check);
                if (fragment8 == null) {
                    fragment8 = new NetCheckFragment();
                }
                switchToFragment(R.id.right_fragment, fragment8, "netCheckFragment");
                mCurrentView = v;
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FILE_RESULT_CODE == requestCode) {
            Bundle bundle;
            UiUitls.showProgress(context, UiUitls.getString(R.string.waiting_import_out));
            if (data != null && (bundle = data.getExtras()) != null) {
                final String strPath = bundle.getString("file");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        export2CSV(strPath);
                        export2Xls(strPath);
                        exportInfoToCSV(strPath);
                    }
                }).start();
            } else {
                UiUitls.hideProgress();
            }
        }
        if (requestCode == FILE_RESULT_IMPORT) {
            Bundle bundle;
            if (data != null && (bundle = data.getExtras()) != null) {
                UiUitls.showProgress(context, UiUitls.getString(R.string.waiting_import_in));
                final String strFilename = bundle.getString("file");
                final File file = new File(strFilename);
                if (file.isDirectory()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            importFile(file);
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            importData(strFilename);
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.hideProgress();
                                }
                            });
                        }
                    }).start();
                }
            } else {
                UiUitls.hideProgress();
            }
        }
        if (requestCode == FILE_RESULT_LOG) {
            UiUitls.setProgressText(UiUitls.getString(R.string.waiting_import_out));
            Bundle bundle;
            if (data != null && (bundle = data.getExtras()) != null) {
                String strPath = bundle.getString("file");
                //导出LOG到介质中
                exportLog(strPath);
            } else {
                UiUitls.hideProgress();
            }
        }
    }

    /**
     * 导出log文件到其他介质中
     * @param strPath 外部介质路径
     */
    private void exportLog(String strPath) {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + GlobalConstant.CRASHLOGPATH);
        if (!file.exists()) {
            return;
        }
        //获取所有的log文件
        File[] files = file.listFiles();
        if (files.length != 0) {
            for (final File logFile : files) {
                //如果存在需要导出的日志文件
                if (logFile.getName().contains("konsung")) {
                    boolean copyResult = copy(Environment
                            .getExternalStorageDirectory() + File
                            .separator + GlobalConstant.CRASHLOGPATH + File
                            .separator + logFile.getName(), strPath + File
                            .separator + logFile.getName());
                    if (copyResult) {
                        //文件导入成功
                        UiUitls.toast(context, logFile.getName() + UiUitls.getString(
                                R.string.export_success));
                    } else {
                        UiUitls.toast(context, logFile.getName() + UiUitls.getString(
                                R.string.export_fail));
                    }
                }
            }
        }
    }

    /**
     * 拷贝文件
     * @param oldPath 文件源
     * @param newPath 文件目的地
     * @return Boolean值
     */
    private boolean copy(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[byteLenth];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导出xls文件
     * @param strPath 路径
     */
    private void export2Xls(final String strPath) {
        if (isPathValid(strPath)) {
            return;
        }
        List<PatientBean> patientBeens = DBDataUtil.getPatientDao().loadAll();
        final File patientFile = new File(strPath, PERSONINFO_XLS_FILE);
        if (patientBeens.size() > 0) {
            try {
                ExcelUtils.exportToExcel(patientFile, patientBeens,
                        ReflectUtil.getExportAnnotation(PatientBean.class));
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, patientFile.getName() + UiUitls
                                .getString(R.string.file_exported) + strPath + UiUitls
                                .getString(R.string.complete));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出json格式的csv文件数据
     * @param strPath 文件路径
     */
    public void exportInfoToCSV(final String strPath) {
        if ("/storage".equals(strPath) || "/stroage/usbotg".equals(strPath)) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    UiUitls.hideProgress();
                    UiUitls.toast(context, UiUitls.getString(R.string.cannot_export) + strPath
                            + UiUitls.getString(R.string.target_file));
                }
            });
            return;
        }
        List<File> fileList = new ArrayList<>();
        final File info = new File(strPath, "konsung_personinfo.csv");
        final File checkdata = new File(strPath, "konsung_checkdata.csv");
        fileList.add(info);
        fileList.add(checkdata);
        for (final File file : fileList) {
            file.setReadable(true, false);
            file.setExecutable(true, false);
            file.setWritable(true);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (file.getName().equals("konsung_personinfo.csv")) {
                    List<PatientBean> patientBeens = DBDataUtil.getPatientDao().loadAll();
                    if (patientBeens.size() > 0) {
                        BufferedWriter bwInfo = new BufferedWriter(new FileWriter(info));
                        bwInfo.write(GlobalConstant.DATABASE_VERSION + "");
                        bwInfo.newLine();
                        for (PatientBean patientBean : patientBeens) {
                            String strBean = JsonUtils.toJsonString(patientBean);
                            bwInfo.write(strBean);
                            bwInfo.newLine();
                        }
                        bwInfo.flush();
                        bwInfo.close();
                    } else {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                UiUitls.hideProgress();
                                UiUitls.toast(context
                                        , UiUitls.getString(R.string.no_record_export));
                                info.delete();
                            }
                        });
                        return;
                    }
                } else {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(checkdata));
                    bw.write(GlobalConstant.DATABASE_VERSION + "");
                    bw.newLine();
                    //首先删除测量数据
                    long measuresCount = DBDataUtil.getMeasureDao().count();
                    if (measuresCount == 0) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                UiUitls.toast(context
                                        , UiUitls.getString(R.string.no_record_export));
                                checkdata.delete();
                            }
                        });
                        continue;
                    }
                    //测量数据大于50则需要进行批量删除
                    if (measuresCount > num) {
                        int max = num;
                        int count = 0;
                        long totalCout = measuresCount;
                        List<MeasureDataBean> datas = DBDataUtil.getMeasureDao().queryBuilder()
                                .limit(max).offset(count).list();
                        while (true) {
                            if (datas.size() > 0) {
                                for (MeasureDataBean measureBean : datas) {
                                    String strBean = JsonUtils.toJsonString(measureBean);
                                    bw.write(strBean);
                                    bw.newLine();
                                }
                            }
                            if ((totalCout - max) <= 0) {
                                break;
                            } else {
                                totalCout = totalCout - max;
                                count = count + max;
                            }
                            if (datas.size() > 0) {
                                datas.clear();
                            }
                            datas.addAll(DBDataUtil.getMeasureDao().queryBuilder()
                                    .limit(max).offset(count).list());
                        }
                    } else {
                        List<MeasureDataBean> measureDataBeens = DBDataUtil.getMeasureDao()
                                .loadAll();
                        if (measureDataBeens.size() > 0) {
                            for (MeasureDataBean measureBean : measureDataBeens) {
                                String strBean = JsonUtils.toJsonString(measureBean);
                                bw.write(strBean);
                                bw.newLine();
                            }
                        }
                    }
                    bw.flush();
                    bw.close();
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, file.getName()
                                + UiUitls.getString(R.string.file_exported)
                                + strPath + UiUitls.getString(R.string.complete));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.hideProgress();
            }
        });
    }

    /**
     * 导出一个csv文件
     * @param strPath 导出文件的路径
     */
    private void export2CSV(final String strPath) {
        if (isPathValid(strPath)) {
            return;
        }
        List<PatientBean> patientBeens = DBDataUtil.getPatientDao().loadAll();
        if (patientBeens.size() > 0) {
            File patientInfo = new File(strPath, PERSONINFO_CSV_FILE);
            try {
                ReflectUtil.writeBeanToCsvFile(patientInfo.getAbsolutePath(), patientBeens);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UiUitls.toast(context, getString(R.string.no_patient_data));
        }

        List<MeasureDataBean> measureDataBeens = DBDataUtil.getMeasureDao().loadAll();
        if (measureDataBeens.size() > 0) {
            File patientInfo = new File(strPath, MEASURE_CSV_FILE);
            try {
                ReflectUtil.writeBeanToCsvFile(patientInfo.getAbsolutePath(), measureDataBeens);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UiUitls.toast(context, getString(R.string.no_measure_data));
        }
        UiUitls.toast(context, UiUitls.getString(R.string.file_exported) + strPath
                + UiUitls.getString(R.string.complete));
    }

    /**
     * 路径是否合法
     * @param strPath 路径字符串
     * @return 是否合法
     */
    private boolean isPathValid(final String strPath) {
        if ("/storage".equals(strPath) || "/stroage/usbotg".equals(strPath)) {
            UiUitls.post(new Runnable() {
                @Override
                public void run() {
                    UiUitls.toast(context, UiUitls.getString(R.string
                            .cannot_export) + strPath + UiUitls.getString(R.string.target_file));
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 选择需要导入的文件夹，导入全部文件
     * @param file 要导入的文件对象（文件根目录对象）
     */
    private void importFile(final File file) {
        Map<String, String> csvList = new HashMap<>();
        findCSV(file, csvList);
        importData(csvList.get(PATIENT_FILE_NAME));
        importData(csvList.get(MEASURE_FILE_NAME));
        UiUitls.post(new Runnable() {
            @Override
            public void run() {
                UiUitls.hideProgress();
            }
        });
    }

    /**
     * 获取该文件对象及其子目录下的所有csv文件
     * @param file 选择的文件的对象
     * @param csvList 获取到的所有csv文件路径集合
     */
    private void findCSV(final File file, Map<String, String> csvList) {
        for (File f : file.listFiles()) {
            if (!f.isDirectory()) {
                String absolutePath = f.getAbsolutePath();
                if (absolutePath.endsWith(CSV_TYPE)) {
                    if (absolutePath.contains(PATIENT_FILE_NAME)) {
                        csvList.put(PATIENT_FILE_NAME, f.getAbsolutePath());
                    }
                    if (absolutePath.contains(MEASURE_FILE_NAME)) {
                        csvList.put(MEASURE_FILE_NAME, f.getAbsolutePath());
                    }
                }
            } else {
                File[] files = f.listFiles();
                if (files.length > 0) {
                    findCSV(f, csvList);
                }
            }
        }
    }

    /**
     * 导入某一个CSV文件
     * @param filename csv文件名
     * @return 是否成功
     */
    private boolean importData(final String filename) {
        boolean bRet = false;
        FileReader fr;
        if (TextUtils.isEmpty(filename)) {
            return false;
        }
        File saveFile = new File(filename);
        try {
            if (!saveFile.exists()) {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.hideProgress();
                    }
                });
                return false;
            }
            if (saveFile.length() > 0) {
                fr = new FileReader(saveFile);
                BufferedReader in = new BufferedReader(fr);
                String dataBaseVersion = in.readLine();
                if (saveFile.getPath().contains("konsung_personinfo.csv")) {
                    //数据库版本如果小于7需要做相应处理
                    if (Integer.valueOf(dataBaseVersion) < GlobalNumber.SEVEN_NUMBER) {
                        String strData = "";
                        while ((strData = in.readLine()) != null) {
                            PatientBean patientBean = JsonUtils.toEntity(strData, PatientBean
                                    .class);
                            List<PatientBean> list = DBDataUtil.getPatientByCard(patientBean
                                    .getIdCard());
                            if (TextUtils.isEmpty(patientBean.getCard())) {
                                String tempCard = patientBean.getIdCard();
                                //身份证
                                patientBean.setCard(tempCard);
                                //UUID
                                patientBean.setIdCard(UUIDGenerator.getUUID());
                            }
                            if (patientBean.getBlood() > GlobalNumber.FOUR_NUMBER) {
                                patientBean.setBlood(GlobalNumber.FOUR_NUMBER);
                            }
                            if (list.size() <= 0) {
                                //在进行数据导入，将json解析的数据ID设置为null自增长状态
                                //否则可能会导致id重复。报异常
                                patientBean.setId(null);
                                DBDataUtil.getPatientDao().insert(patientBean);
                            }
                        }
                    } else {
                        String strData = "";
                        while ((strData = in.readLine()) != null) {
                            PatientBean patientBean = JsonUtils.toEntity(strData
                                    , PatientBean.class);
                            //1.3.0的数据库导入的时候，查询本地数据库是否有该条数据
                            List<PatientBean> list = DBDataUtil.getPatientByCard(patientBean
                                    .getCard());

                            if (list.size() > 0) {
                                //说明本地有该条数据，idcard赋值为导入的idcard，方便居民列表查看报告
                                list.get(0).setIdCard(patientBean.getIdCard());
                                DBDataUtil.getPatientDao().updateInTx(list.get(0));
                                continue;
                            }

                            if (patientBean.getBlood() > GlobalNumber.FOUR_NUMBER) {
                                patientBean.setBlood(GlobalNumber.FOUR_NUMBER);
                            }
                            if (list.size() <= 0) {
                                patientBean.setId(null);
                                DBDataUtil.getPatientDao().insert(patientBean);
                            }
                        }
                    }
                } else if (saveFile.getPath().contains("konsung_checkdata.csv")) {
                    //数据库版本如果小于7需要做相应处理
                    if (Integer.valueOf(dataBaseVersion) < GlobalNumber.SEVEN_NUMBER) {
                        String strData = "";
                        while ((strData = in.readLine()) != null) {
                            MeasureDataBean measureBean = JsonUtils.toEntity(strData
                                    , MeasureDataBean.class);
                            List<MeasureDataBean> listMeasure = DBDataUtil
                                    .getMeasuresByMeasureUuid(measureBean.getUuid());
                            List<PatientBean> list = DBDataUtil.getPatientByCard(measureBean
                                    .getIdcard());
                            //判断居民是否存在
                            if (list == null || list.size() <= 0) {
                                continue;
                            }
                            measureBean.setIdcard(list.get(0).getIdCard());
                            measureBean.setPatientId(list.get(0).getId());
                            measureBean.setMeasureStrTime(UiUitls
                                    .getDateFormat(UiUitls.DateState.SHORT)
                                    .format(measureBean.getMeasureTime()));
                            if (0 == measureBean.getParamValue()) {
                                //4063表示勾选了所有测量配置项
                                measureBean.setParamValue(GlobalNumber
                                        .FOUR_THOUSAND_SIXTY_THREE_NUMBER);
                            }
                            if (listMeasure.size() <= 0) { //测量数据不进行数据覆盖
                                measureBean.setId(null);
                                DBDataUtil.getMeasureDao().insert(measureBean);
                            }
                        }
                    } else {
                        String strData = "";
                        while ((strData = in.readLine()) != null) {
                            MeasureDataBean measureBean = JsonUtils.toEntity(strData
                                    , MeasureDataBean.class);
                            List<PatientBean> list = DBDataUtil.getPatientByIdCard(
                                    measureBean.getIdcard());
                            //1.3.0数据 idcard 已经与1.3.0的病人关联上了
                            if (list.size() > 0) {
                                measureBean.setPatientId(list.get(0).getId());
                                //设置测量时间
                                measureBean.setMeasureStrTime(UiUitls
                                        .getDateFormat(UiUitls.DateState.SHORT)
                                        .format(measureBean.getMeasureTime()));
//                                measureBean.setId(null);
                                DBDataUtil.getMeasureDao().insertOrReplaceInTx(measureBean);
                            }
                        }
                    }
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, UiUitls.getString(R.string.inport) + filename
                                + UiUitls.getString(R.string.success));
                    }
                });
            } else {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context
                                , filename + UiUitls.getString(R.string.file_no_data));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return bRet;
    }

    /**
     * 按测量时间倒序查询返回测量集合
     * @param idCard 身份证号
     * @return 测量bean集合
     */
    public static List<MeasureDataBean> getMeasureData(String idCard) {
        MeasureDataBeanDao dao = DBDataUtil.getMeasureDao();
        List<MeasureDataBean> list = dao.queryBuilder().where(MeasureDataBeanDao.Properties
                .Idcard.eq(idCard)).orderDesc(MeasureDataBeanDao.Properties.Createtime).list();
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalConstant.isCancel) {
            GlobalConstant.isCancel = false;
            mCurrentView = null;
        }
        if (mCurrentView == null) {
            onClick(llSetting);
        } else {
            if (!isFirstIn) {
                onClick(mCurrentView);
            } else {
                onClick(llSetting);
            }
        }
    }

    /**
     * fragment切换
     * @param containerViewId 容器id
     * @param dfragment 切换到的fragment
     * @param tag 标识
     */
    public void switchToFragment(int containerViewId, BaseFragment dfragment, String tag) {
        dfragment.initDatas();
        Fragment fragmentByTag = getFragmentManager().findFragmentByTag(tag);
        if (fragmentByTag == null) {
            if (mCurrentFragment == null) {
                getFragmentManager().beginTransaction()
                        .add(containerViewId, dfragment, tag)
                        .show(dfragment)
                        .commit();
            } else {
                getFragmentManager().beginTransaction()
                        .add(containerViewId, dfragment, tag)
                        .show(dfragment)
                        .hide(mCurrentFragment)
                        .commit();
            }
        } else {
            if (mCurrentFragment == fragmentByTag) {
                return;
            } else {
                getFragmentManager().beginTransaction()
                        .show(dfragment)
                        .hide(mCurrentFragment)
                        .commit();
            }
        }
        mCurrentFragment = dfragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        // 如果只是看不见说明之前已经进入过系统设置页面
        isFirstIn = false;
        clearFragments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出系统设置页面时，清除标志位为初始
        isFirstIn = true;
        clearFragments();
    }

    /**
     * 清空fragment栈
     */
    private void clearFragments() {
        super.onDestroy();
        remove(fragment1);
        remove(fragment2);
        remove(fragment3);
        remove(fragment4);
        remove(fragment5);
        remove(fragment6);
        remove(fragment7);
        remove(fragment8);
        mCurrentFragment = null;
    }

    /**
     * 移除fragment
     * @param fragment fragment对象
     */
    public void remove(Fragment fragment) {
        if (fragment != null) {
            try {
                getFragmentManager().beginTransaction().remove(fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * 点击改变背景色
     * @param id view id
     */
    private void setSelected(int id) {
        currentBtnSelected = id;
        setSelectBg(currentBtnSelected);
    }

    /**
     * 设置选中项背景改变颜色
     * @param id viewId
     */
    private void setSelectBg(int id) {
        if (id == R.id.ll_sys_setting) {
            ivSetting.setImageResource(R.drawable.ic_settings_app_setting_sel);
            tvSetting.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow1.setVisibility(View.VISIBLE);
        } else {
            ivSetting.setImageResource(R.drawable.ic_settings_app_setting);
            tvSetting.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow1.setVisibility(View.GONE);
        }
        if (id == R.id.ll_sys_pull) {
            ivPull.setImageResource(R.drawable.ic_settings_import_sel);
            tvPull.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow2.setVisibility(View.VISIBLE);
        } else {
            ivPull.setImageResource(R.drawable.ic_settings_import);
            tvPull.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow2.setVisibility(View.GONE);
        }
        if (id == R.id.ll_sys_push) {
            ivPush.setImageResource(R.drawable.ic_settings_export_sel);
            tvPush.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow3.setVisibility(View.VISIBLE);
        } else {
            ivPush.setImageResource(R.drawable.ic_settings_export);
            tvPush.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow3.setVisibility(View.GONE);
        }
        if (id == R.id.ll_system_about) {
            ivAbout.setImageResource(R.drawable.ic_settings_about_sel);
            tvAbout.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow4.setVisibility(View.VISIBLE);
        } else {
            ivAbout.setImageResource(R.drawable.ic_settings_about);
            tvAbout.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow4.setVisibility(View.GONE);
        }
        if (id == R.id.ll_system_factory_protect) {
            ivFactory.setImageResource(R.drawable.ic_settings_maintenance_sel);
            tvFactory.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow5.setVisibility(View.VISIBLE);
        } else {
            ivFactory.setImageResource(R.drawable.ic_settings_maintenance);
            tvFactory.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow5.setVisibility(View.GONE);
        }
        if (id == R.id.ll_system_video_practise) {
            ivVideo.setImageResource(R.drawable.ic_settings_video_sel);
            tvVideo.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow6.setVisibility(View.VISIBLE);
        } else {
            ivVideo.setImageResource(R.drawable.ic_settings_video);
            tvVideo.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow6.setVisibility(View.GONE);
        }
        if (id == R.id.ll_system_skin_set) {
            ivSkin.setImageResource(R.drawable.ic_settings_skin_setting_sel);
            tvSkin.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow7.setVisibility(View.VISIBLE);
        } else {
            ivSkin.setImageResource(R.drawable.ic_settings_skin_setting);
            tvSkin.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow7.setVisibility(View.GONE);
        }
        if (id == R.id.ll_system_net_check) {
            ivNet.setImageResource(R.drawable.ic_settings_network_test_sel);
            tvNet.setTextColor(getResources().getColor(R.color.actionbar_bg_color));
            ivArrow8.setVisibility(View.VISIBLE);
        } else {
            ivNet.setImageResource(R.drawable.ic_settings_network_test);
            tvNet.setTextColor(getResources().getColor(R.color.system_left_text_color));
            ivArrow8.setVisibility(View.GONE);
        }
    }
}
