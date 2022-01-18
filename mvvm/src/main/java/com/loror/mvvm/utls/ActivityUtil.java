package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loror.lororUtil.flyweight.ObjectPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ActivityUtil {

    private static final List<Activity> activities = new ArrayList<>();
    private static Application context;

    /**
     * 注册监听
     */
    public static void register(Application application) {
        context = application;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activities.remove(activity);
            }
        });
    }

    /**
     * 屏幕适配信息
     */
    private static class CustomInfo {

        //原始数据
        private float compactDensity;
        private float compactScaleDensity;

        //计算数据
        private float targetDensity;
        private float targetScaleDensity;

        /**
         * 计算适配信息
         */
        private static CustomInfo calculateCustomInfo(DisplayMetrics displayMetrics) {
            CustomInfo customInfo = new CustomInfo();
            customInfo.compactDensity = displayMetrics.density;
            customInfo.compactScaleDensity = displayMetrics.scaledDensity;
            float scaleFix = displayMetrics.widthPixels / 2.0f / dpHalf;
            float scale = displayMetrics.widthPixels / 1080f;
            float standardDensity = 3.0f * scale;
            float standardDpi = 480 * scale;
            customInfo.targetDensity = standardDensity / (standardDpi / displayMetrics.densityDpi) * scaleFix;
            customInfo.targetScaleDensity = (customInfo.compactScaleDensity / customInfo.compactDensity) * customInfo.targetDensity;
            if (customInfo.targetScaleDensity < 0.5) {
                customInfo.targetScaleDensity = customInfo.targetDensity;
            }
            return customInfo;
        }
    }

    private static CustomInfo customInfo;
    private static int dpHalf;//suggest 190dp


    /**
     * 设置半屏像素
     */
    public static void setDpHalf(int dpHalf) {
        ActivityUtil.dpHalf = dpHalf;
    }

    /**
     * 全局屏幕适配
     */
    public static void setGlobalCustomDensity(Activity activity) {
        if (dpHalf <= 0) {
            return;
        }
        Application application = activity.getApplication();
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (customInfo == null) {
            customInfo = CustomInfo.calculateCustomInfo(appDisplayMetrics);
        }
        appDisplayMetrics.density = customInfo.targetDensity;
//        appDisplayMetrics.densityDpi = customInfo.targetDensityDpi;
        appDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = customInfo.targetDensity;
//        activityDisplayMetrics.densityDpi = targetDensityDpi;
        activityDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        DisplayMetrics defaultDisplayMetrics = Resources.getSystem().getDisplayMetrics();
        if (defaultDisplayMetrics != null) {
            defaultDisplayMetrics.density = customInfo.targetDensity;
//        defaultDisplayMetrics.densityDpi = targetDensityDpi;
            defaultDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        }
    }

    /**
     * app级屏幕适配
     */
    public static void setCustomDensity(Application application) {
        if (dpHalf <= 0) {
            return;
        }
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (customInfo == null) {
            customInfo = CustomInfo.calculateCustomInfo(appDisplayMetrics);
        }
        appDisplayMetrics.density = customInfo.targetDensity;
//        appDisplayMetrics.densityDpi = customInfo.targetDensityDpi;
        appDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        DisplayMetrics defaultDisplayMetrics = Resources.getSystem().getDisplayMetrics();
        if (defaultDisplayMetrics != null) {
            defaultDisplayMetrics.density = customInfo.targetDensity;
//        defaultDisplayMetrics.densityDpi = customInfo.targetDensityDpi;
            defaultDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        }
    }

    /**
     * activity屏幕适配
     */
    public static void setCustomDensity(Activity activity) {
        if (dpHalf <= 0) {
            return;
        }
        DisplayMetrics appDisplayMetrics = activity.getApplication().getResources().getDisplayMetrics();
        if (customInfo == null) {
            customInfo = CustomInfo.calculateCustomInfo(appDisplayMetrics);
        }
        appDisplayMetrics.density = customInfo.targetDensity;
//        appDisplayMetrics.densityDpi = customInfo.targetDensityDpi;
        appDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = customInfo.targetDensity;
//        activityDisplayMetrics.densityDpi = customInfo.targetDensityDpi;
        activityDisplayMetrics.scaledDensity = customInfo.targetScaleDensity;
    }

    /**
     * displayMetrics屏幕适配
     */
    public static void setCustomDensity(DisplayMetrics displayMetrics) {
        if (dpHalf <= 0) {
            return;
        }
        if (customInfo == null) {
            customInfo = CustomInfo.calculateCustomInfo(displayMetrics);
        }
        displayMetrics.density = customInfo.targetDensity;
//        displayMetrics.densityDpi = customInfo.targetDensityDpi;
        displayMetrics.scaledDensity = customInfo.targetScaleDensity;
    }

    /**
     * 获取栈顶activity
     */
    public static Activity getLast() {
        return activities.size() == 0 ? null : activities.get(activities.size() - 1);
    }

    /**
     * 获取activity
     */
    public static Activity get(Class<? extends Activity> activity) {
        for (Activity a : activities) {
            if (a.getClass() == activity) {
                return a;
            }
        }
        return null;
    }

    /**
     * 获取activity
     */
    public static List<Activity> getAll(Class<? extends Activity> activity) {
        List<Activity> result = new ArrayList<>();
        for (Activity a : activities) {
            if (a.getClass() == activity) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * 关闭界面
     */
    public static void finish(Class<? extends Activity> activity) {
        for (Activity a : activities) {
            if (a.getClass() == activity) {
                a.finish();
            }
        }
    }

    /**
     * 关闭所有界面
     */
    public static void finishAll(Class<? extends Activity>... exclude) {
        List<Class<? extends Activity>> excludeList = exclude != null ? Arrays.asList(exclude) : null;
        for (Activity a : activities) {
            if (excludeList != null && excludeList.contains(a.getClass())) {
                continue;
            }
            a.finish();
        }
    }

    /**
     * 关闭界面到activity，activity不关闭
     */
    public static void finishUntil(Class<? extends Activity> activity) {
        List<Activity> task = new ArrayList<>(activities);
        Collections.reverse(task);
        for (Activity a : task) {
            if (a.getClass() == activity) {
                break;
            }
            a.finish();
        }
    }

    /**
     * 关闭界面到activity，包含activity关闭
     */
    public static void finishUntilInclude(Class<? extends Activity> activity) {
        List<Activity> task = new ArrayList<>(activities);
        Collections.reverse(task);
        for (Activity a : task) {
            a.finish();
            if (a.getClass() == activity) {
                break;
            }
        }
    }

    /**
     * 关闭所有界面
     */
    public static void finishAll() {
        for (Activity a : activities) {
            a.finish();
        }
    }

    /**
     * 关闭所有界面
     */
    public static void finishAllLater() {
        List<Activity> activities = new ArrayList<>(ActivityUtil.activities);
        ObjectPool.getInstance().getHandler().postDelayed(() -> {
            for (Activity a : activities) {
                a.finish();
            }
        }, 100);
    }

    /**
     * 开启界面
     */
    public static <T extends Activity> void startNewActivity(Class<T> aClass) {
        Intent intent = new Intent(context, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 界面是否存在
     */
    public static boolean exits(Class<? extends Activity> activity) {
        for (Activity a : activities) {
            if (a.getClass() == activity) {
                return true;
            }
        }
        return false;
    }
}