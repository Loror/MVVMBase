package com.loror.mvvm.utls;

import android.app.Activity;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.loror.lororUtil.flyweight.ObjectPool;
import com.loror.lororUtil.http.api.ApiClient;
import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.core.ConfigApplication;
import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.dialog.ProgressDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 查找过配置的
     */
    private static final List<Class<?>> found = new ArrayList<>();
    private static final Map<Class<?>, Object> configs = new HashMap<>();
    private static final Map<Class<?>, Method> exceptionHandler = new HashMap<>();

    static {
        configs.put(Handler.class, ObjectPool.getInstance().getHandler());
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
                    return (ProgressDialog) globalProgressDialogForActivity.invoke(globalProgressDialogForActivity.getDeclaringClass(), activity);
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
                    return (ProgressDialog) globalProgressDialogForFragment.invoke(globalProgressDialogForFragment.getDeclaringClass(), fragment);
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
     * 处理框架内部异常
     */
    public static void handlerException(Throwable t) {
        if (t == null) {
            return;
        }
        boolean find = false;
        for (Map.Entry<Class<?>, Method> handler : exceptionHandler.entrySet()) {
            if (handler.getKey().isAssignableFrom(t.getClass())) {
                find = true;
                handler.getValue().setAccessible(true);
                try {
                    handler.getValue().invoke(application, t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (!find) {
            t.printStackTrace();
        }
    }

    /**
     * ConfigApplication配置
     */
    public static void config(ConfigApplication application) {
        ConfigUtil.application = application;
        config(application.getClass());
        Method[] methods = application.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (method.getParameterTypes().length != 1) {
                    continue;
                }
                if (Throwable.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    exceptionHandler.put(method.getParameterTypes()[0], method);
                }
            }
        }
    }

    /**
     * MvvmActivity配置
     */
    public static void config(MvvmActivity activity) {
        config(activity.getClass());
        Method[] methods = activity.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (ProgressDialog.class.isAssignableFrom(method.getReturnType())) {
                    if (method.getParameterTypes().length != 0) {
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
    public static void config(MvvmFragment fragment) {
        config(fragment.getClass());
        Method[] methods = fragment.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                if (ProgressDialog.class.isAssignableFrom(method.getReturnType())) {
                    if (method.getParameterTypes().length != 0) {
                        continue;
                    }
                    progressDialogForFragment = method;
                }
            }
        }
    }

    /**
     * 查找静态配置
     */
    public static void config(Class<?> type) {
        do {
            if (!found.contains(type)) {
                Method[] methods = type.getDeclaredMethods();
                for (Method method : methods) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    Config config = method.getAnnotation(Config.class);
                    if (config != null) {
                        if (Activity.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            if (globalProgressDialogForActivity != null) {
                                throw new IllegalStateException("Activity的ProgressDialog已配置，请保证唯一性");
                            }
                            globalProgressDialogForActivity = method;
                        } else if (Fragment.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            if (globalProgressDialogForFragment != null) {
                                throw new IllegalStateException("Activity的ProgressDialog已配置，请保证唯一性");
                            }
                            globalProgressDialogForFragment = method;
                        } else if (method.getReturnType().isInterface()) {
                            Class<?>[] params = method.getParameterTypes();
                            try {
                                method.setAccessible(true);
                                if (params.length == 0) {
                                    configs.put(method.getReturnType(), method.invoke(method.getDeclaringClass()));
                                } else if (params.length == 1 && params[0] == ApiClient.class) {
                                    configs.put(method.getReturnType(), method.invoke(method.getDeclaringClass(), new ApiClient()));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                found.add(type);
            }
            type = type.getSuperclass();
        } while (type != null && type != Object.class && !type.getName().startsWith("com.loror.mvvm"));
    }

    /**
     * 获取api
     */
    protected static Object getConfined(Class<?> type) {
        return configs.get(type);
    }
}
