package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loror.lororUtil.flyweight.ObjectPool;

import java.util.ArrayList;
import java.util.Arrays;
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
     * 获取栈顶activity
     */
    public static Activity getLast() {
        return activities.size() == 0 ? null : activities.get(activities.size() - 1);
    }

    /**
     * 关闭所有界面
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
        for (Activity a : activities) {
            if (exclude != null && Arrays.asList(exclude).contains(a.getClass())) {
                continue;
            }
            a.finish();
        }
    }

    /**
     * 关闭界面到activity，activity不关闭
     */
    public static void finishUtil(Class<? extends Activity> activity) {
        for (Activity a : activities) {
            if (a.getClass() == activity) {
                break;
            }
            a.finish();
        }
    }

    /**
     * 关闭界面到activity，包含activity关闭
     */
    public static void finishUtilInclude(Class<? extends Activity> activity) {
        for (Activity a : activities) {
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
