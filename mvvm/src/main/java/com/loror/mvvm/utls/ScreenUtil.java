package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 屏幕工具
 */
public class ScreenUtil {

    /**
     * 获取屏幕宽度像素
     */
    public static int getScreenWidth(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕宽度像素
     */
    public static int getScreenHeight(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度像素
     */
    public static int getScreenWidth(Application application) {
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        return appDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕宽度像素
     */
    public static int getScreenHeight(Application application) {
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        return appDisplayMetrics.heightPixels;
    }
}
