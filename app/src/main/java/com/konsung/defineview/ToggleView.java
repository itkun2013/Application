package com.konsung.defineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.UiUitls;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/12/28 0028.
 * 定义的开关控件
 */
public class ToggleView extends LinearLayout
        implements View.OnClickListener {
    @InjectView(R.id.view_toggle_tv)
    protected TextView viewToggleTv;
    @InjectView(R.id.view_toggle_refresh)
    protected ImageView viewToggleRefresh;

    //用来记录当前的开关是否打开
    private boolean isOpen;
    //记录当前的监听状态
    private OnToggleListener toggleListener;

    public ToggleView(Context context) {
        this(context, null);
    }

    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //关联布局
        View view = View.inflate(context, R.layout.view_toggle, this);
        ButterKnife.inject(this, view);
        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable
                .ToggleView);
        //获取文字内容
        String text = ta.getString(R.styleable.ToggleView_toggle_text);
        //获取开关是否打开
        isOpen = ta.getBoolean(R.styleable.ToggleView_toggle_isOpen, true);

        viewToggleTv.setText(isOpen ? UiUitls.getContent().getResources().getString(R
                .string.open) : UiUitls.getContent().getResources().getString(R
                .string.close));
        viewToggleRefresh.setImageResource(isOpen ? R.drawable.switch_on : R
                .drawable
                .switch_off);

        //为开关设置监听事件
        viewToggleRefresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //点击开关按钮
        if (v == viewToggleRefresh) {
            clickToggle();
        }
    }

    /**
     * 点击开关图片的方法
     */
    private void clickToggle() {
        isOpen = !isOpen;
        viewToggleRefresh.setImageResource(isOpen
                ? R.drawable.switch_on
                : R.drawable.switch_off);
        viewToggleTv.setText(isOpen ? UiUitls.getContent().getResources().getString(R
                .string.open) : UiUitls.getContent().getResources().getString(R
                .string.close));
        if (toggleListener != null) {
            toggleListener.onclickToggle(isOpen);
        }
    }

    /**
     * 监听用户是否打开了开关
     */
    public interface OnToggleListener {
        void onclickToggle(boolean isOpen);
    }

    /**
     * 获取当前开关的状态，如果是ture表示打开
     *
     * @return
     */
    public boolean getIsOpen() {
        return isOpen;
    }

    /**
     * 设置监听事件
     *
     * @param listener
     */
    public void setOnToggleListener(OnToggleListener listener) {
        this.toggleListener = listener;
    }

    public void setToggle(boolean is) {
        isOpen = is;
        viewToggleRefresh.setImageResource(is
                ? R.drawable.switch_on
                : R.drawable.switch_off);
        viewToggleTv.setText(isOpen
                ? UiUitls.getContent().getResources().getString(R
                .string.open) : UiUitls.getContent().getResources().getString(R
                .string.close));
    }
}
