package com.konsung.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.konsung.R;
import com.konsung.bean.AppPersonDto;
import com.konsung.bean.PatientBean;
import com.konsung.bean.PersonDownloadResponse;
import com.konsung.bean.QueryCommand;
import com.konsung.defineview.UploadAllProgressDialog;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.ReflectUtil;
import com.konsung.util.RequestUtils;
import com.konsung.util.URLManage;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konusng.adapter.PatientDownloadItemAdapter;
import com.konusng.adapter.PopupWindowAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.synjones.bluetooth.BmpUtil;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

/**
 * @author DLX
 */

public class PatientDownloadActivity extends BaseActivity {

    public static final int PAGE_LIMIT = 5; //每页数量限制
    public static final int ALL_PAGE_LIMIT = 100; //下载所有，每一次下载100条
    private static final int UPDATE_HEAD = 1001;
    @InjectView(R.id.iv_search)
    ImageView ivSearch;
    @InjectView(R.id.et_search)
    EditText etSearch;
    @InjectView(R.id.btn_query)
    Button btnQuery;
    @InjectView(R.id.btn_download)
    Button btnDownload;
    @InjectView(R.id.lv_patient)
    ListView lvPatient;
    @InjectView(R.id.btn_page_first)
    Button btnPageFirst;
    @InjectView(R.id.btn_page_pre)
    Button btnPagePre;
    @InjectView(R.id.btn_page_number)
    Button btnPageNumber;
    @InjectView(R.id.btn_page_next)
    Button btnPageNext;
    @InjectView(R.id.btn_page_last)
    Button btnPageLast;
    @InjectView(R.id.ll_root)
    LinearLayout llRoot;
    @InjectView(R.id.cb_all)
    CheckBox cbAll;
    @InjectView(R.id.blank_page_show_tv)
    TextView blankShowTv;
    private PatientDownloadItemAdapter adapter;
    private int currentPage = 1; //当前页数，默认查询第一页
    private static final String SUCC = "10000"; //请求成功状态码
    private int total;
    private Handler handler; //处理listview更新 保持同步（避免嵌套子线程网络同步问题）
    private boolean isQueryType = false; //是否为搜索模式
    private String lastQueryCondition = ""; //最新一次的搜索内容
    private Context context = null;
    private int allPage; //服务器所有数据
    private int requestCount; //下载全部所需要请求次数
    private UploadAllProgressDialog uploadAllProgressDialog;
    private int totalPersent; //下载总数
    private int percentValue; //进度条
    private final int color = 0xb0000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                , WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_patient_download);
        //防止弹出软键盘遮挡编辑框
        getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING | SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        context = PatientDownloadActivity.this;
        init();
    }

    /**
     * 初始化, 默认进入后加载第一页数据
     */
    private void init() {
        setLeftButtonText(getString(R.string.patient_download_title));
        adapter = new PatientDownloadItemAdapter(this, null);
        lvPatient.setAdapter(adapter);
        //首次进来显示默认当前1页
        queryPatient(currentPage, true);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setCheckAll(isChecked);
                if (isChecked) {
                    //全选，赋值false.
                    GlobalConstant.isStopDownload = false;
                }
            }
        });
        btnPageFirst.setEnabled(false);
        btnPagePre.setEnabled(false);
        btnPageNext.setEnabled(false);
        btnPageLast.setEnabled(false);
        btnPageNumber.setEnabled(false);
        btnPageNumber.setText("0");
        btnPageNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> datas = new ArrayList<>();
                for (int i = 1; i <= total; i++) {
                    if (i < GlobalNumber.TEN_NUMBER) {
                        datas.add("0" + i);
                    } else {
                        datas.add(i + "");
                    }
                }
                initPopWindow(datas, v);
            }
        });

        btnPagePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage - 1 < 1) {
                    return;
                }
                currentPage--;
                if (currentPage <= 1) {
                    btnPageFirst.setEnabled(false);
                    btnPagePre.setEnabled(false);
                } else {
                    btnPageFirst.setEnabled(true);
                    btnPagePre.setEnabled(true);
                }
                btnPageLast.setEnabled(true);
                btnPageNext.setEnabled(true);
                if (isQueryType) {
                    queryPatient(lastQueryCondition, currentPage, false);
                } else {
                    queryPatient(currentPage, false);
                }
            }
        });

        btnPageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage + 1 > total) {
                    return;
                }
                currentPage++;
                if (currentPage >= total) {
                    btnPageLast.setEnabled(false);
                    btnPageNext.setEnabled(false);
                } else {
                    btnPageLast.setEnabled(true);
                    btnPageNext.setEnabled(true);
                }
                btnPageFirst.setEnabled(true);
                btnPagePre.setEnabled(true);
                if (isQueryType) {
                    queryPatient(lastQueryCondition, currentPage, false);
                } else {
                    queryPatient(currentPage, false);
                }
            }
        });

        btnPageFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                btnPageFirst.setEnabled(false);
                btnPagePre.setEnabled(false);
                btnPageLast.setEnabled(true);
                btnPageNext.setEnabled(true);
                if (isQueryType) {
                    queryPatient(lastQueryCondition, currentPage, false);
                } else {
                    queryPatient(currentPage, false);
                }
            }
        });

        btnPageLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPageFirst.setEnabled(true);
                btnPagePre.setEnabled(true);
                btnPageNext.setEnabled(false);
                btnPageLast.setEnabled(false);
                //如果网络没链接或者没有数据
                if (total == 0) {
                    btnPageNumber.setText("1");
                    btnPageFirst.setEnabled(false);
                } else {
                    currentPage = total;
                    if (currentPage <= 1) {
                        btnPageFirst.setEnabled(false);
                        btnPagePre.setEnabled(false);
                    } else {
                        btnPageFirst.setEnabled(true);
                        btnPagePre.setEnabled(true);
                    }
                    if (isQueryType) {
                        queryPatient(lastQueryCondition, currentPage, false);
                    } else {
                        queryPatient(currentPage, false);
                    }
                }
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheckAll = adapter.isCheckAll();
                if (isCheckAll) {
                    if (allPage % PAGE_LIMIT > 0) {
                        requestCount = allPage / ALL_PAGE_LIMIT + 1;
                    } else {
                        requestCount = allPage / ALL_PAGE_LIMIT;
                    }
                    //如果是搜索结果<5个
                    if (requestCount == 0) {
                        downSelectData();
                        return;
                    }
                    //如果是全选，数据超过100
                    uploadAllProgressDialog = new UploadAllProgressDialog(context
                            , new UploadAllProgressDialog.UpdataButtonState() {

                                @Override
                                public void cancelUpload() {
                                    GlobalConstant.isStopDownload = true;
                                }
                            });
                    uploadAllProgressDialog.show();
                    uploadAllProgressDialog.setText(UiUitls.getString(R.string.cancel_download),
                            UiUitls.getString(R.string.data_downloading));
                    for (int i = 0; i < requestCount; i++) {
                        //全部选择的用户,每次请求100条数据
                        queryAllPatient(lastQueryCondition, i);
                    }
                } else {
                    //单项选择的用户
                    downSelectData();
                }
                //下载完成操作
                GlobalConstant.isAddUser = true;
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                lastQueryCondition = etSearch.getText().toString();
                isQueryType = true;
                queryPatient(lastQueryCondition, currentPage, true);
            }
        });
        //修改键盘的enter键，点击消费
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL) {
                    //点击enter键搜索结果
                    currentPage = 1;
                    lastQueryCondition = etSearch.getText().toString();
                    isQueryType = true;
                    queryPatient(lastQueryCondition, currentPage, true);
                    return true;
                }
                return false;
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_HEAD:
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 下载选择的数据
     */
    private void downSelectData() {
        List<PatientBean> checkedPatients = adapter.getCheckedPatients();
        if (checkedPatients.size() == 0) {
            UiUitls.hideProgress();
            //没有选择数据
            UiUitls.toast(context, R.string.download_patient_empty);
            return;
        }
        UiUitls.showProgress(context, getRecString(R.string.wait_for_load));
        //比较服务器选择的数据和本地居民列表的数据，有重复的不进行下载
        List<PatientBean> localList = DBDataUtil.queryAll();
        List<PatientBean> diffList = getFiltrtDownData(checkedPatients, localList);
        if (diffList.size() > 0) {
            transformCardInsert(diffList);
        }
        UiUitls.hideProgress();
        UiUitls.toast(context, R.string.download_patient_ok);
        //点击全部下载或者单点下载，下载完成后，设置还原
        adapter.setCheckAll(false);
        cbAll.setChecked(false);
    }

    /**
     * 将card重新赋值
     * @param diffList 所选数据
     */
    private void transformCardInsert(List<PatientBean> diffList) {
        for (PatientBean bean : diffList) {
            //设置显示电话
            bean.setSelfmobile(bean.getPhone());
            if (bean.getSex() == 2) {
                //服务器返回的性别女为2，男为1 ，0为未知
                //但是本地 0 为女 1为男
                bean.setSex(0);
            }
            //插入数剧
            DBDataUtil.getPatientDao().insertOrReplace(bean);
        }
    }

    /**
     * 获取比较后的下载的数据
     * @param checkedPatients 选择服务器的数据
     * @param localList 本地已有的数据
     * @return 比较后的数据
     */
    private List<PatientBean> getFiltrtDownData(List<PatientBean> checkedPatients
            , List<PatientBean> localList) {
        if (localList.size() == 0) {
            //如果本地没有数据
            return checkedPatients;
        }
        for (PatientBean local : localList) {
            //遍历本地数据，服务器有，移除
            for (Iterator it = checkedPatients.iterator(); it.hasNext(); ) {
                PatientBean check = (PatientBean) it.next();
                if (!TextUtils.isEmpty(local.getCard())) {
                    if (local.getCard().equals(check.getCard())) {
                        //有相同的移除
                        it.remove();
                    }
                } else if (!TextUtils.isEmpty(local.getMemberShipCard())) {
                    if (local.getMemberShipCard().equals(check.getMemberShipCard())) {
                        //有相同的移除
                        it.remove();
                    }
                } else {
                    if (local.getName().equals(check.getName())) {
                        //有相同的移除
                        it.remove();
                    }
                }
            }
        }
        return checkedPatients;
    }

    /**
     * 网络查询给定页数的用户并显示
     * @param page 页数
     * @param isOnce 首次进来标志
     */
    private void queryPatient(int page, final boolean isOnce) {
        queryPatient("", page, isOnce);
    }

    /**
     * 网络查询所有用户并显示
     * @param condition 查询条件
     * @param page 页数
     */
    private void queryAllPatient(String condition, final int page) {
        final String url = URLManage.getInstance().getDowloadPatientURL();
        QueryCommand command = new QueryCommand();
        command.setOrgId(GlobalConstant.ORG_ID);
        command.setPageStart(page * ALL_PAGE_LIMIT);
        command.setPageRecords(ALL_PAGE_LIMIT);
        if (!TextUtils.isEmpty(condition)) {
            command.setCondition(condition);
        }
        final AsyncHttpResponseHandler callback = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String json = new String(bytes);
                PersonDownloadResponse<AppPersonDto> datas = JsonUtils.toEntity(
                        json, new TypeToken<PersonDownloadResponse<AppPersonDto>>() {
                        }.getType());
                if (GlobalConstant.isStopDownload) {
                    //停止全部下载
                    return;
                }
                if (!datas.resultCode.equals(SUCC)) {
                    return;
                }
                //请求成功
                final List<PatientBean> list = ReflectUtil.toPatients(datas.list);
                if (list.size() == 0) {
                    return;
                }
                //下载头像文件
                queryHeadPic(list);
                totalPersent += list.size();
                percentValue = totalPersent * GlobalNumber.HUNDRED_NUMBER / allPage;
                //比较服务器选择的数据和本地居民列表的数据，有重复的不进行下载
                List<PatientBean> localList = DBDataUtil.queryAll();
                List<PatientBean> diffList = getFiltrtDownData(list, localList);
                if (diffList.size() > 0) {
                    transformCardInsert(diffList);
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadAllProgressDialog.setProgress(percentValue);
                        //最後一次請求后，彈窗消失
                        if (totalPersent >= allPage) {
                            if (null != uploadAllProgressDialog) {
                                uploadAllProgressDialog.dismiss();
                            }
                            //点击全部下载或者单点下载，下载完成后，设置还原
                            adapter.setCheckAll(false);
                            cbAll.setChecked(false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, R.string.request_timeout);
                        if (null != uploadAllProgressDialog) {
                            uploadAllProgressDialog.dismiss();
                        }
                    }
                });
            }
        };
        try {
            final StringEntity entity = new StringEntity(JsonUtils.toJsonString(command), "UTF-8");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestUtils.clientPost(PatientDownloadActivity.this, url, entity, callback);
                }
            }).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络查询给定页数的用户并显示
     * @param page 页数
     * @param isOnce 首次进来标志
     * @param condition 查询条件
     */
    private void queryPatient(String condition, int page, final boolean isOnce) {
        UiUitls.showProgress(context, getRecString(R.string.wait_for_load));
        final String url = URLManage.getInstance().getDowloadPatientURL();
        QueryCommand command = new QueryCommand();
        command.setOrgId(GlobalConstant.ORG_ID);
        command.setPageStart((page - 1) * PAGE_LIMIT);
        command.setPageRecords(PAGE_LIMIT);
        if (!TextUtils.isEmpty(condition)) {
            command.setCondition(condition);
        }
        final AsyncHttpResponseHandler callback = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String json = new String(bytes);
                try {
                    final PersonDownloadResponse<AppPersonDto> datas = JsonUtils.toEntity(json
                            , new TypeToken<PersonDownloadResponse<AppPersonDto>>() {
                            }.getType());
                    if (!datas.resultCode.equals(SUCC)) {
                        UiUitls.hideProgress();
                        if (!isOnce) {
                            btnPageNumber.setText(currentPage + "/" + total);
                            btnPageNumber.setEnabled(true);
                        }
                        lvPatient.setVisibility(View.GONE);
                        blankShowTv.setVisibility(View.VISIBLE);
                        return;
                    }
                    //请求成功
                    final List<PatientBean> patientBeen = ReflectUtil.toPatients(datas.list);
                    if (patientBeen.size() == 0) {
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                UiUitls.toast(context, R.string.query_noempty);
                                UiUitls.hideProgress();
                                if (!isOnce) {
                                    btnPageNumber.setText(currentPage + "/" + total);
                                    btnPageNumber.setEnabled(true);
                                }
                                lvPatient.setVisibility(View.GONE);
                                blankShowTv.setVisibility(View.VISIBLE);
                            }
                        });
                        return;
                    }
                    //下载头像文件
                    queryHeadPic(patientBeen);
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            if (patientBeen.size() > 0) {
                                lvPatient.setVisibility(View.VISIBLE);
                                blankShowTv.setVisibility(View.GONE);
                                btnPageNumber.setEnabled(true);
                                adapter.setDatas(patientBeen);
                            }
                            try {
                                allPage = Integer.valueOf(datas.entity);
                                if (allPage % PAGE_LIMIT > 0) {
                                    total = allPage / PAGE_LIMIT + 1;
                                } else {
                                    total = allPage / PAGE_LIMIT;
                                }
                                btnPageNumber.setText(currentPage + "/" + total);
                                if (isOnce) {
                                    btnPageNext.setEnabled(true);
                                    btnPageLast.setEnabled(true);
                                }
                                UiUitls.hideProgress();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                throwable.printStackTrace();
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        UiUitls.toast(context, R.string.request_timeout);
                        UiUitls.hideProgress();
                        lvPatient.setVisibility(View.GONE);
                        blankShowTv.setVisibility(View.VISIBLE);
                        if (!isOnce) {
                            btnPageNumber.setText(currentPage + "/" + total);
                            btnPageNumber.setEnabled(true);
                        }
                    }
                });
            }
        };
        try {
            final StringEntity entity = new StringEntity(JsonUtils.toJsonString(command), "UTF-8");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestUtils.clientPost(PatientDownloadActivity.this, url, entity, callback);
                }
            }).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据居民列表下载保存头像
     * @param patientBeen patientBeen
     */
    private void queryHeadPic(List<PatientBean> patientBeen) {
        for (final PatientBean patientBean : patientBeen) {
            String bmpStr = patientBean.getBmpStr();
            if (TextUtils.isEmpty(bmpStr)) {
                continue;
            }
            downloadHeadPic(patientBean.getCard(), patientBean.getBmpStr());
        }
    }

    /**
     * 根据头像名下载保存头像
     * @param headPicName headPicName
     * @param fileName fileName
     */
    private void downloadHeadPic(final String fileName, String headPicName) {
        final String url = URLManage.getInstance().getDownloadHeadURL();
        final RequestParams params = new RequestParams();
        //固定参数 文件名和文件范围
        params.put("fileName", headPicName);
        params.put("rRange", "0-");
        final FileAsyncHttpResponseHandler callback = new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File response) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int i, Header[] headers, File response) {
                try {
                    File picFile = new File(BmpUtil.getHeadPicRootFile(), fileName + ".bmp");
                    if (picFile.exists() && picFile.length() == response.length()) {
                        handler.sendEmptyMessage(UPDATE_HEAD);
                        return;
                    } else {
                        if (!picFile.exists()) {
                            try {
                                if (!picFile.createNewFile()) {
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //复制文件
                    BmpUtil.fileChannelCopy(response, picFile);
                    handler.sendEmptyMessage(UPDATE_HEAD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestUtils.clientPost(PatientDownloadActivity.this, url, params, callback);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从云南1.0.1合并过来的选择页面
     * @param name 姓名集合
     * @param view 视图
     */
    private void initPopWindow(ArrayList<String> name, View view) {

        View contentView = LayoutInflater.from(this.getApplicationContext())
                .inflate(R.layout.popupwindow_page, null);
        contentView.setBackgroundColor(Color.BLUE);

        final PopupWindow popupWindow = new PopupWindow(this.findViewById(R.id
                .iv_search), 1300, 350);
        contentView.setBackgroundColor(this.getResources().getColor(R.color.white));
        popupWindow.setContentView(contentView);

        TextView textView = (TextView) contentView.findViewById(R.id.pop_title);
        textView.setText(UiUitls.getString(R.string.select_page_number));
        ListView listView = (ListView) contentView.findViewById(R.id.pop_list);
        PopupWindowAdapter adapter = new PopupWindowAdapter(this, name);
        listView.setAdapter(adapter);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(color));
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strPageNumber = (i + 1) + "";
                if (strPageNumber == null || strPageNumber.equals("0")) {
                    //当前页默认1
                    currentPage = 1;
                    btnPageNumber.setText(currentPage + "");
                } else {
                    //选择当前页
                    currentPage = Integer.valueOf(strPageNumber);
                    btnPageNumber.setText(strPageNumber + "/" + total);
                }
                if (currentPage <= 1) {
                    //首页
                    btnPageFirst.setEnabled(false);
                    btnPagePre.setEnabled(false);
                    btnPageNext.setEnabled(true);
                    btnPageLast.setEnabled(true);
                } else if (currentPage >= total) {
                    //末页
                    btnPageFirst.setEnabled(true);
                    btnPagePre.setEnabled(true);
                    btnPageNext.setEnabled(false);
                    btnPageLast.setEnabled(false);
                } else {
                    btnPageFirst.setEnabled(true);
                    btnPagePre.setEnabled(true);
                    btnPageNext.setEnabled(true);
                    btnPageLast.setEnabled(true);
                }
                queryPatient(currentPage, false);
                popupWindow.dismiss();
            }
        });
    }
}
