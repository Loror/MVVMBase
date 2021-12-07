package com.loror.mvvm.utls;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.core.ConfigApplication;
import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.dialog.ProgressDialog;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 配置工具
 */
public class ConfigUtil {

    /**
     * app
     */
    private static ConfigApplication application;

    /**
     * 全局生成loading配置
     */
    private static Method globalProgressDialogForActivity;
    private static Method globalProgressDialogForFragment;
    /**
     * 单独生成loading配置
     */
    private static Method progressDialogForActivity;
    private static Method progressDialogForFragment;

    /**
     * ConfigApplication配置
     */
    public static void collect(ConfigApplication application) {
        ConfigUtil.application = application;
        Method[] methods = application.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (ProgressDialog.class.isAssignableFrom(method.getReturnType())) {
                    if (method.getParameterTypes().length != 1) {
                        continue;
                    }
                    if (Activity.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        globalProgressDialogForActivity = method;
                    } else if (Fragment.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        globalProgressDialogForFragment = method;
                    }
                }
            }
        }
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForActivity(Activity activity) {
        if (progressDialogForActivity != null) {
            progressDialogForActivity.setAccessible(true);
            try {
                return (ProgressDialog) progressDialogForActivity.invoke(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (globalProgressDialogForActivity != null) {
            globalProgressDialogForActivity.setAccessible(true);
            try {
                if (Modifier.isStatic(globalProgressDialogForActivity.getModifiers())) {
                    return (ProgressDialog) globalProgressDialogForActivity.invoke(ConfigApplication.class, activity);
                } else {
                    return (ProgressDialog) globalProgressDialogForActivity.invoke(application, activity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForFragment(Fragment fragment) {
        if (progressDialogForFragment != null) {
            progressDialogForFragment.setAccessible(true);
            try {
                return (ProgressDialog) progressDialogForFragment.invoke(fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (globalProgressDialogForFragment != null) {
            globalProgressDialogForFragment.setAccessible(true);
            try {
                if (Modifier.isStatic(globalProgressDialogForFragment.getModifiers())) {
                    return (ProgressDialog) globalProgressDialogForFragment.invoke(ConfigApplication.class, fragment);
                } else {
                    return (ProgressDialog) globalProgressDialogForFragment.invoke(application, fragment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * MvvmActivity配置
     */
    public static void collect(MvvmActivity activity) {
        Method[] methods = activity.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (ProgressDialog.class.isAssignableFrom(method.getReturnType())) {
                    if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0) {
                        continue;
                    }
                    progressDialogForActivity = method;
                }
            }
        }
    }

    /**
     * MvvmFragment配置
     */
    public static void collect(MvvmFragment fragment) {
        Method[] methods = fragment.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (ProgressDialog.class.isAssignableFrom(method.getReturnType())) {
                    if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0) {
                        continue;
                    }
                    progressDialogForFragment = method;
                }
            }
        }
    }
}
