package com.loror.mvvm.utls;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.loror.mvvm.core.ConfigApplication;
import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.core.MvvmSign;
import com.loror.mvvm.dialog.ProgressDialog;

/**
 * 配置工具
 */
public class ConfigUtil {

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForActivity(Activity activity) {
        return MvvmSign.progressDialogForActivity(activity);
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForFragment(Fragment fragment) {
        return MvvmSign.progressDialogForFragment(fragment);
    }

    /**
     * 处理配置
     */
    public static void handler(Object data) {
        MvvmSign.handler(data);
    }

    /**
     * 处理框架内部异常
     */
    public static void handlerException(Throwable t) {
        MvvmSign.handlerException(t);
    }

    /**
     * ConfigApplication配置
     */
    public static void config(ConfigApplication application) {
        MvvmSign.config(application);
    }

    /**
     * MvvmActivity配置
     */
    public static void config(MvvmActivity activity) {
        MvvmSign.config(activity);
    }

    /**
     * MvvmFragment配置
     */
    public static void config(MvvmFragment fragment) {
        MvvmSign.config(fragment);
    }

    /**
     * 查找静态配置
     */
    public static synchronized void config(Class<?> type) {
        MvvmSign.config(type);
    }
}
