package com.konsung.floatbuttons;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.konsung.defineview.ButtonFloatSmall;


/**
 * 底部浮动按钮动画效果类
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-10 10:16
 */
public class FloatButtonAnimation {
    // 用来适配不同的分辨率
    private static int xOffset = 15;
    private static int yOffset = -13;

    public static void initOffset(Context context) {
        xOffset = (int) (10.667 * context.getResources().getDisplayMetrics()
                .density);
        yOffset = -(int) (8.667 * context.getResources().getDisplayMetrics()
                .density);
    }

    // 浮动按钮加号的动画
    public static Animation getRotateAnimation(float fromDegrees, float
            toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        return rotate;
    }

    // 图标的动画(入动画)
    public static void startAnimationsIn(ViewGroup viewgroup, int
            durationMillis) {
        viewgroup.setVisibility(View.VISIBLE);
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            ButtonFloatSmall inoutimagebutton = (ButtonFloatSmall) viewgroup
                    .getChildAt(i);
            inoutimagebutton.setVisibility(View.VISIBLE);
            inoutimagebutton.setClickable(true);
            inoutimagebutton.setFocusable(true);
            MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
                    .getLayoutParams();
            Animation animation = new TranslateAnimation(mlp.rightMargin -
                    xOffset, 0F, yOffset + mlp.bottomMargin, 0F);

            animation.setFillAfter(true);
            animation.setDuration(durationMillis);
            animation.setStartOffset((i * 100) / (-1 + viewgroup
                    .getChildCount()));// 下一个动画的偏移时间
            animation.setInterpolator(new OvershootInterpolator(2F));// 动画的效果
            // 弹出再回来的效果
            inoutimagebutton.startAnimation(animation);
        }
    }

    // 图标的动画(出动画)
    public static void startAnimationsOut(final ViewGroup viewgroup, int
            durationMillis) {
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            final ButtonFloatSmall inoutimagebutton = (ButtonFloatSmall)
                    viewgroup.getChildAt(i);
            MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
                    .getLayoutParams();
            Animation animation = new TranslateAnimation(0F, mlp.rightMargin
                    - xOffset, 0F, yOffset + mlp.bottomMargin);

            animation.setFillAfter(true);
            animation.setDuration(durationMillis);
            // 下一个动画的偏移时间
            animation.setStartOffset(((viewgroup.getChildCount() - i) * 100)
                    / (-1 + viewgroup.getChildCount()));
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    inoutimagebutton.setVisibility(View.GONE);
                    inoutimagebutton.setClickable(false);
                    inoutimagebutton.setFocusable(false);
                    viewgroup.setVisibility(View.GONE);
                }
            });
            inoutimagebutton.startAnimation(animation);
        }
    }

    // icon缩小消失的动画
    public static Animation getMiniAnimation(int durationMillis) {
        Animation miniAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        miniAnimation.setDuration(durationMillis);
        miniAnimation.setFillAfter(true);
        return miniAnimation;
    }

    // icon放大渐变消失的动画
    public static Animation getMaxAnimation(int durationMillis, final
    ViewGroup viewGroup) {
        AnimationSet animationset = new AnimationSet(true);

        Animation maxAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);

        animationset.addAnimation(maxAnimation);
        animationset.addAnimation(alphaAnimation);

        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);

        // 设置viewGroup消失,避免动画完再次点击时依旧出现图标
        animationset.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return animationset;
    }

}
