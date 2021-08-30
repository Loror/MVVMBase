package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

import com.loror.lororUtil.convert.DpPxUtil;

public class DesignUtil {

    /**
     * 设计图上的宽度
     */
    private static int DESIGN = 385;

    private static float compactDensity;
    private static float compactScaleDensity;
    private static float scaleFix = 1.0f;
    private static boolean logged = false;

    /**
     * 设置设计图宽度，单位dp
     */
    public static void setDesignWidth(int DESIGN) {
        DesignUtil.DESIGN = DESIGN;
    }

    /**
     * 屏幕适配
     */
    public static void setCustomDensity(Activity activity, Application application) {
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (compactScaleDensity == 0) {
            compactDensity = appDisplayMetrics.density;
            compactScaleDensity = appDisplayMetrics.scaledDensity;
            int width = DpPxUtil.Dp2Px(activity, DESIGN);
            scaleFix = appDisplayMetrics.widthPixels * 1.0f / width;
            if (!logged) {
                Log.e("Design", "width:" + width + ",screen:" + appDisplayMetrics.widthPixels);
            }
        }
        float scale = appDisplayMetrics.widthPixels / 1080f;
        float standardDensity = 3.0f * scale;
        float standardDpi = 480 * scale;
        float targetDensity = standardDensity / (standardDpi / appDisplayMetrics.densityDpi) * scaleFix;
        float targetScaleDensity = (compactScaleDensity / compactDensity) * targetDensity;
        if (!logged) {
            Log.e("Design", "compactDensity:" + compactDensity + ",compactScaleDensity:" + compactScaleDensity
                    + ",targetDensity:" + targetDensity + ",targetScaleDensity:" + targetScaleDensity + ",densityDpi:" + appDisplayMetrics.densityDpi);
        }
        if (targetScaleDensity < 0.5) {
            targetScaleDensity = targetDensity;
        }
        appDisplayMetrics.density = targetDensity;
//        appDisplayMetrics.densityDpi = targetDensityDpi;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
//        activityDisplayMetrics.densityDpi = targetDensityDpi;
        activityDisplayMetrics.scaledDensity = targetScaleDensity;
        logged = true;
    }
}
