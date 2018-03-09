package com.konsung.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;
import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.util.ActivityUtils;
import com.konsung.util.GlobalConstant;

/**
 * activity 基类
 * 使用FlatUI实现统一的UI界面，只需要新activity继承本BaseActivity即可
 */
public abstract class BaseActivity extends ActionBarActivity {

    //导航栏背景色
    private final int appTheme = R.array.konsung;
    //ActionBar对象
    private ActionBar bar;
    //返回键
    private ImageView ivBack;
    //导航栏左控件
    private TextView leftBtn;
    //导航栏右控件
    private TextView rightBtn;
    //导航栏标题控件
    private TextView titleView;
    //导航栏右边可扩展容器
    private LinearLayout rightContainer;
    //记录上次点击时间
    private long lastClickTime = 0;
    //点击时间间隔有效值
    private final int limitTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(appTheme);
        getSupportActionBar().setBackgroundDrawable(FlatUI
                .getActionBarDrawable(this, appTheme, false, 2));
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setActionBarLayout(R.layout.actionbar_pub);
        //Activity入栈管理
        ActivityUtils.getActivityUtils().pushActivity(this);
        lastClickTime = 0;
    }

    /**
     * 获取本地String字符
     * @param id 字符id值
     * @return 字符串
     */
    public String getRecString(int id) {
        return getResources().getString(id);
    }

    /**
     * 修改标题信息的方法，由子类去继承实现
     * @param bean 病人的信息bean
     */
    public void changActionBar(PatientBean bean) {
    }

    /**
     * 设置自定义导航栏
     * @param layoutId 布局id
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setActionBarLayout(int layoutId) {
        bar = getSupportActionBar();
        if (null != bar) {
            //使自定义的view能在title栏显示
            bar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflator = LayoutInflater.from(this);
            View v = inflator.inflate(layoutId, null);
            initView(v);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bar.setCustomView(v, layout);
        }
    }

    /**
     * 初始化视图
     * @param v 视图
     */
    private void initView(View v) {
        ivBack = (ImageView) v.findViewById(R.id.ic_nav_back);
        leftBtn = (TextView) v.findViewById(R.id.left_btn);
        rightBtn = (TextView) v.findViewById(R.id.right_btn);
        titleView = (TextView) v.findViewById(R.id.tv_title);
        rightContainer = (LinearLayout) v.findViewById(R.id.right_conteiner);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //防止快速点击，以1s钟为点击间隔有效值
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > limitTime) {
                    if (back()) {
                        popActivity();
                        if (GlobalConstant.isAdministraor) {
                            GlobalConstant.isAdministraor = false;
                            GlobalConstant.TEST_PASSWORD = "";
                        }
                    }
                }
                lastClickTime = currentTime;
            }
        });
    }

    /**
     * 设置是否显示左图标
     * @param isShow 是否显示
     */
    public void setShowLeftHomeButton(boolean isShow) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShow);
    }

    /**
     * 设置左按钮值
     * @param leftButtonText 文字
     */
    public void setLeftButtonText(String leftButtonText) {
        if (leftBtn != null) {
            leftBtn.setText(leftButtonText);
        }
    }

    /**
     * 设置右边按钮值
     * @param rightButtonText 文字
     */
    public void setRightButtonText(String rightButtonText) {
        if (rightBtn != null) {
            rightBtn.setText(rightButtonText);
        }
    }

    /**
     * 设置标题栏
     * @param title 文字
     * @param drawable 图标
     */
    public void setTitleText(String title, Drawable drawable) {
        if (titleView != null) {
            titleView.setText(title);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                titleView.setCompoundDrawables(null, null, drawable, null);
            }
        }
    }

    /**
     * 设置标题是否显示
     * @param visible 是否显示
     */
    public void setTitleVisible(boolean visible) {
        if (titleView != null) {
            if (visible) {
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置右按钮是否存在
     * @param visible 是否显示
     */
    public void setRightBtnVisible(boolean visible) {
        if (rightBtn != null) {
            if (visible) {
                rightBtn.setVisibility(View.VISIBLE);
            } else {
                rightBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置右边自定义区域是否存在
     * @param visible 是否显示
     */
    public void setRightContainerVisible(boolean visible) {
        if (rightContainer != null) {
            if (visible) {
                rightContainer.setVisibility(View.VISIBLE);
            } else {
                rightContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 获取右边自定义区域布局对象
     * @return 右容器
     */
    public LinearLayout getRightContainer() {
        return rightContainer;
    }

    /**
     * 获取右边按钮
     * @return 右控件
     */
    public TextView getRightBtn() {
        return rightBtn;
    }

    /**
     * 获取标题视图
     * @return 标题控件
     */
    public TextView getTitleView() {
        return titleView;
    }

    /**
     * 获取返回按钮
     * @return 返回按钮对象
     */
    public ImageView getIvBack() {
        return ivBack;
    }

    /**
     * 页面跳转
     * @param context 上下文
     * @param sourceTarget 跳转页面
     */
    public void pushActivity(Context context, Class sourceTarget) {
        Intent intent = new Intent(context, sourceTarget);
        startActivity(intent);
    }

    /**
     * 页面跳转，并传递参数
     * @param context 上下文
     * @param sourceTarget 跳转页面
     * @param key key值
     * @param value value值
     */
    public void pushActivityWithMessage(Context context, Class sourceTarget, String key
            , String value) {
        Intent intent = new Intent(context, sourceTarget);
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 页面跳转，并传递参数
     * @param context 上下文
     * @param sourceTarget 跳转页面
     * @param bundle 数据存储类
     */
    public void pushActivityWithMessage(Context context, Class sourceTarget, Bundle bundle) {
        Intent intent = new Intent(context, sourceTarget);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 页面跳转并判断是否关闭当前页面
     * @param sourceTarget 跳转页面
     * @param context 上下文
     * @param isfinish 是否关闭当前页面
     */
    public void pushActivityAndFinish(Context context, Class sourceTarget, boolean isfinish) {
        Intent intent = new Intent(context, sourceTarget);
        startActivity(intent);
        if (isfinish) {
            popActivity();
        }
    }

    /**
     * 退出当前页面
     */
    public void popActivity() {
        ActivityUtils.getActivityUtils().popActivity();
    }

//    /**
//     * 点击导航栏返回图标触发事件
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                if (back()) {
//                    popActivity();
//                    if (GlobalConstant.isAdministraor) {
//                        GlobalConstant.isAdministraor = false;
//                        GlobalConstant.TEST_PASSWORD = "";
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 点击返回时，执行的方法
     * @return 返回ture则运行基类中代码，反正则不
     */
    public boolean back() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
