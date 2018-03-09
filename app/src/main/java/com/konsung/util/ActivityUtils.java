package com.konsung.util;

import android.app.Activity;
import android.os.Process;

import java.util.Stack;

/**
 * Created by xiangshicheng on 2017/10/10 0010.
 * 栈中Activity的管理
 */

public class ActivityUtils {
    public static Stack activityStack;
    private static ActivityUtils instance;

    /**
     * 获取实例
     * @return 类对象
     */
    public static ActivityUtils getActivityUtils() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    /**
     * 结束当前页面
     */
    public void popActivity() {
        Activity activity = (Activity) activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定页面
     * @param activity Activity对象
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获取当前页面
     * @return activity对象
     */
    public Activity currentActivity() {
        Activity activity = (Activity) activityStack.lastElement();
        return activity;
    }

    /**
     * 获取指定页面
     * @param cls 类
     * @return activity对象
     */
    public Activity getNeedActivity(Class cls) {
        if (activityStack != null) {
            for (int i = 0, size = activityStack.size(); i < size; i ++) {
                if (activityStack.get(i).getClass().equals(cls)) {
                    return  (Activity) activityStack.get(i);
                }
            }
        }
        return null;
    }

    /**
     * 指定页面入栈
     * @param activity 对象
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.add(activity);
    }

    /**
     *结束除指定页面之外的所有页面
     * @param cls 类对象
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 结束所有页面
     */
    public void popAllActivity() {
        while (activityStack.size() > 0) {
            int position = activityStack.size() - 1;
            if (activityStack.get(position) != null) {
                popActivity((Activity) activityStack.get(position));
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitApp() {
        popAllActivity();
        //杀死应用进程
        android.os.Process.killProcess(Process.myPid());
        System.exit(0);
    }
}