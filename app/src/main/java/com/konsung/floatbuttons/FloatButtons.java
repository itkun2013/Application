package com.konsung.floatbuttons;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.konsung.defineview.ButtonFloat;


/**
 * 抽象底部按钮
 * 显示的为一个ButtonFloat,"+"号
 * 点击显示多个ButtonFloatSmall
 * ButtonFloatSmall的单个点击事件并未在这里实现,需要在具体的Fragment中进行实现,
 * 因为ButtonFloatSmall的单个点击事件涉及到具体的Item所持有的事件。
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-10 10:54
 */
public class FloatButtons {

    // buttons是否在显示
    public static boolean isButtonsShow;
    private static ButtonFloat buttonFloat;
    private static RelativeLayout buttonsLayout;

    /*
     * 初始化按钮, 并设置动画效果
     * @param layout Button所在的布局视图
     * @param bf 底部"+"号Button
     */
    public static void initButtons(RelativeLayout layout, ButtonFloat bf) {
        FloatButtonAnimation.initOffset(layout.getContext());

        buttonFloat = bf;
        buttonsLayout = layout;

        // 浮动按钮点击事件监听器
        bf.startAnimation(FloatButtonAnimation.getRotateAnimation(0, 360, 200));
        bf.setOnClickListener(buttonFloatOnClickListener);

        // 触摸消失Buttons
        buttonsLayout.setOnTouchListener(buttonFloatDismissOnTouchListener);
    }

    /*
     * 浮动按钮监听器
     */
    private static View.OnClickListener buttonFloatOnClickListener = new
            View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isButtonsShow) {
                // 图标的动画
                FloatButtonAnimation.startAnimationsIn(buttonsLayout, 300);
                // 浮动按钮加号的动画
                buttonFloat.startAnimation(FloatButtonAnimation
                        .getRotateAnimation(0, -225, 300));//角度
            } else {
                FloatButtonAnimation.startAnimationsOut(buttonsLayout, 300);
                buttonFloat.startAnimation(FloatButtonAnimation
                        .getRotateAnimation(-225, 0, 300));
            }
            isButtonsShow = !isButtonsShow;
        }
    };

    /*
     * ButtonFloat "加号"按钮,
     * 触摸其他位置,消失dismiss
     */
    private static View.OnTouchListener buttonFloatDismissOnTouchListener =
            new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getX() < (buttonFloat.getLeft() + 150)//移动距离
                    || event.getX() > buttonFloat.getRight()
                    || event.getY() > buttonFloat.getBottom()
                    || event.getY() < buttonFloat.getTop() + 200) {
                if (isButtonsShow) {
                    FloatButtonAnimation.startAnimationsOut(buttonsLayout,
                            300);
                    buttonFloat.startAnimation(FloatButtonAnimation
                            .getRotateAnimation(-225, 0, 300));
                    isButtonsShow = !isButtonsShow;
                }
            }
            return false;
        }
    };
}
