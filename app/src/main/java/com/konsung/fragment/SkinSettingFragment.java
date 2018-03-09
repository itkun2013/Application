package com.konsung.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.defineview.ImageTextButton;
import com.konsung.defineview.TipsDialog;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konusng.adapter.PictureListAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 皮肤设置
 * Created by DJH on 2017/9/1 0001.
 */
public class SkinSettingFragment extends BaseFragment implements View.OnClickListener {
    @InjectView(R.id.btn_change_background)
    ImageTextButton btnChangeBackground;
    @InjectView(R.id.gv_background)
    GridView gvBackground;
//    @InjectView(R.id.btn_change_logo)
//    ImageTextButton btnChangeLogo;
//    @InjectView(R.id.gv_logo)
//    GridView gvLogo;
    @InjectView(R.id.tv_no_background)
    TextView tvNoBackground;
//    @InjectView(R.id.tv_no_logo)
//    TextView tvNoLogo;
    @InjectView(R.id.btn_default_background)
    ImageTextButton btnDefaultBackground;
//    @InjectView(R.id.btn_default_logo)
//    ImageTextButton btnDefaultLogo;

    public static final String BACKGROUND_PATH = Environment.getExternalStorageDirectory() +
            "/konsungSkin/background"; //主页背景图片文件夹路径
    public static final String LOGO_PATH = Environment.getExternalStorageDirectory() +
            "/konsungSkin/logo"; //Logo图片文件夹路径
    public static final String CURRENT_PICTURE_PATH = UiUitls.getContent().getFilesDir()
            .getAbsolutePath(); //当前背景及logo保存的路径
    public static final String LOGO_NAME = "currentLogo.jpeg"; //当前logo文件名
    public static final String BACKGROUND_NAME = "currentBackground.jpeg"; //当前主页背景文件名

    private boolean backgroundListGone = true; //背景列表是否显示
    private boolean logoListGone = true; //logo列表是否显示
    private List<Bitmap> backgroundList; //背景图片集合
    private List<Bitmap> logoList; //logo图片集合
    private PictureListAdapter backgroundAdapter; //背景图片适配器
    private PictureListAdapter logoAdapter; //logo图片适配器
    private Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skin_setting, null);
        ButterKnife.inject(this, view);
        context = getActivity();
        initEvent();
        return view;
    }

    /**
     * 将目标路径文件夹下的图片转换成bitmap集合
     * @param path 目标路径
     * @return bitmap集合
     */
    private List<Bitmap> getBitmapList(String path) {
        File fileFolder = new File(path);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        File[] files = fileFolder.listFiles();
        List<Bitmap> bitmaps = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file
                    .getName().endsWith(".png")) {
                Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + file.getName(),
                        getBitmapOption(2));
                bitmaps.add(bitmap);
            }
        }
        return bitmaps;
    }

    /**
     * 压缩bitmap
     * @param inSampleSize 压缩倍数
     * @return Options
     */
    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 初始化换肤页面事件
     */
    private void initEvent() {
        btnChangeBackground.setOnClickListener(this);
//        btnChangeLogo.setOnClickListener(this);
        btnDefaultBackground.setOnClickListener(this);
//        btnDefaultLogo.setOnClickListener(this);
        gvBackground.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position
                    , long id) {
                UiUitls.showTitle(context, UiUitls.getString(R.string.change_background),
                        UiUitls.getString(R.string.confirm_change_background)
                        , new TipsDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                if (pressed) {
                                    final Bitmap bitmap = backgroundList.get(position);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveBitmapFile(bitmap, CURRENT_PICTURE_PATH + "/" +
                                                    BACKGROUND_NAME);
                                            UiUitls.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    UiUitls.toast(context, UiUitls
                                                            .getString(R.string.change_success));
                                                }
                                            });
                                        }
                                    }).start();
                                    backgroundAdapter.setPosition(position);
                                    backgroundAdapter.notifyDataSetChanged();
                                }
                                UiUitls.hideTitil();
                            }
                        });
            }
        });


        // TODO 主页面更改导致的问题
//        gvLogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position
// , long id) {
//                UiUitls.showTitle(getActivity(), UiUitls.getString(R.string.change_logo), UiUitls
//                        .getString(R.string.confirm_change_logo), new TipsDialog
//                        .UpdataButtonState() {
//                    @Override
//                    public void getButton(Boolean pressed) {
//                        if (pressed) {
//                            final Bitmap bitmap = logoList.get(position);
//                            LauncherActivity activity = (LauncherActivity)
//                                    getActivity();
//                            activity.setLogo(bitmap);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    saveBitmapFile(bitmap, CURRENT_PICTURE_PATH +
// "/" + LOGO_NAME);
//                                    UiUitls.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            CustomToast.showMessage(getActivity(), UiUitls
//                                                    .getString(R.string.change_success));
//                                        }
//                                    });
//                                }
//                            }).start();
//                            logoAdapter.setPosition(position);
//                            logoAdapter.notifyDataSetChanged();
//                        }
//                        UiUitls.hideTitil();
//                    }
//                });
//            }
//        });
    }

    /**
     * 保存图片
     * @param bitmap 图片
     * @param path 路径
     */
    private void saveBitmapFile(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, GlobalNumber.SIXTY_NUMBER, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_background:
                initBackgroundList();
                break;
//            case R.id.btn_change_logo:
//                initLogoList();
//                break;
            case R.id.btn_default_background:
                //恢复为默认主页
                deleteFile(CURRENT_PICTURE_PATH + "/" + BACKGROUND_NAME);
                if (null != backgroundAdapter) {
                    backgroundAdapter.setPosition(-1);
                    backgroundAdapter.notifyDataSetChanged();
                }
                UiUitls.toast(context
                        , UiUitls.getString(R.string.recover_default_background_success));
                break;
//            case R.id.btn_default_logo:
                //恢复为默认logo
                // TODO 主页面更改导致的问题
//                LauncherActivity activity = (LauncherActivity) getActivity();
//                activity.recoverDefaultLogo();
//                deleteFile(CURRENT_PICTURE_PATH + "/" + LOGO_NAME);
//                if (null != logoAdapter) {
//                    logoAdapter.setPosition(-1);
//                    logoAdapter.notifyDataSetChanged();
//                }
//                CustomToast.showMessage(getActivity(), UiUitls.getString(R.string
//                        .recover_default_logo_success));
//                break;
            default:
                break;
        }
    }

    /**
     * 删除文件
     * @param path 文件路径
     */
    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 初始化主页背景图片列表
     */
    private void initBackgroundList() {
        if (backgroundListGone) {
            UiUitls.showProgress(context, UiUitls.getString(R.string.loading_picture));
            backgroundListGone = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    backgroundList = getBitmapList(BACKGROUND_PATH);
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            UiUitls.hideProgress();
                            if (null != backgroundList && backgroundList.size() > 0) {
                                gvBackground.setVisibility(View.VISIBLE);
                                tvNoBackground.setVisibility(View.GONE);
                                backgroundAdapter = new PictureListAdapter(UiUitls.getContent(),
                                        backgroundList);
                                gvBackground.setAdapter(backgroundAdapter);
                            } else {
                                tvNoBackground.setVisibility(View.VISIBLE);
                                gvBackground.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
        } else {
            gvBackground.setVisibility(View.GONE);
            tvNoBackground.setVisibility(View.GONE);
            backgroundListGone = true;
        }
    }

//    /**
//     * 初始化Logo图片列表
//     */
//    private void initLogoList() {
//        if (logoListGone) {
//            UiUitls.showProgress(getActivity(), UiUitls.getString(R.string.loading_picture));
//            logoListGone = false;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    logoList = getBitmapList(LOGO_PATH);
//                    UiUitls.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            UiUitls.hideProgress();
//                            if (null != logoList && logoList.size() > 0) {
//                                gvLogo.setVisibility(View.VISIBLE);
//                                tvNoLogo.setVisibility(View.GONE);
//                                logoAdapter = new PictureListAdapter(UiUitls.getContent(),
//                                        logoList);
//                                gvLogo.setAdapter(logoAdapter);
//                            } else {
//                                gvLogo.setVisibility(View.GONE);
//                                tvNoLogo.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    });
//                }
//            }).start();
//        } else {
//            gvLogo.setVisibility(View.GONE);
//            tvNoLogo.setVisibility(View.GONE);
//            logoListGone = true;
//        }
//    }
}
