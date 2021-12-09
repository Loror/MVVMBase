package com.loror.mvvm.utls;

import android.app.Activity;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.loror.lororUtil.flyweight.ObjectPool;
import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.core.ConfigApplication;
import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.dialog.ProgressDialog;
import com.loror.mvvm.net.Message;

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
     * 查找过配置的
     */
    private static final List<Class<?>> found = new ArrayList<>();

    /**
     * 配置
     */
    private static final Map<Class<?>, Method> handlerConfigs = new HashMap<>();

    /**
     * 全局配置
     * key 赋值类型
     * value configs：可用值 globalAppConfigs、globalStaticConfigs可用方法，参数数量1（obj） 或 2（obj,String）
     */
    private static final Map<Class<?>, Object> configs = new HashMap<>();
    private static final Map<Class<?>, Method> globalAppConfigs = new HashMap<>();
    private static final Map<Class<?>, Method> globalStaticConfigs = new HashMap<>();

    /**
     * 局部配置
     * key 方法所属类类型
     * value 可用方法，参数数量0 或 1（String）
     */
    private static final Map<Class<?>, List<Method>> localConfigs = new HashMap<>();

    static {
        configs.put(Handler.class, ObjectPool.getInstance().getHandler());
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForActivity(Activity activity) {
        return (ProgressDialog) getConfined(ProgressDialog.class, activity);
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForFragment(Fragment fragment) {
        return (ProgressDialog) getConfined(ProgressDialog.class, fragment);
    }

    /**
     * 处理配置
     */
    public static void handler(Object data) {
        if (data == null) {
            return;
        }
        Method handler = handlerConfigs.get(data.getClass());
        if (handler != null) {
            handler.setAccessible(true);
            try {
                if (Modifier.isStatic(handler.getModifiers())) {
                    handler.invoke(handler.getDeclaringClass(), data);
                } else {
                    handler.invoke(application, data);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理框架内部net异常
     */
    public static void handlerMessage(Message message) {
        if (message == null) {
            return;
        }
        Method messageHandler = handlerConfigs.get(Message.class);
        if (messageHandler != null) {
            messageHandler.setAccessible(true);
            try {
                if (Modifier.isStatic(messageHandler.getModifiers())) {
                    messageHandler.invoke(messageHandler.getDeclaringClass(), message);
                } else {
                    messageHandler.invoke(application, message);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理框架内部异常
     */
    public static void handlerException(Throwable t) {
        if (t == null) {
            return;
        }
        boolean find = false;
        for (Map.Entry<Class<?>, Method> handler : handlerConfigs.entrySet()) {
            if (handler.getKey().isAssignableFrom(t.getClass())) {
                find = true;
                handler.getValue().setAccessible(true);
                try {
                    if (Modifier.isStatic(handler.getValue().getModifiers())) {
                        handler.getValue().invoke(handler.getValue().getDeclaringClass(), t);
                    } else {
                        handler.getValue().invoke(application, t);
                    }
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
            if (method.getParameterTypes().length > 1) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                Class<?>[] paramsType = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                if (paramsType.length == 1 && returnType == Void.TYPE) {
                    handlerConfigs.put(method.getParameterTypes()[0], method);
                } else if (returnType == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(application);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (paramsType.length == 0) {
                    try {
                        method.setAccessible(true);
                        configs.put(returnType, method.invoke(application));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (paramsType.length == 1 || (paramsType.length == 2 && paramsType[1] == String.class)) {
                    Method find = globalAppConfigs.get(method.getReturnType());
                    if (find != null) {
                        throw new IllegalStateException(method.getReturnType().getName() + "类型已配置config，请勿重复配置");
                    }
                    globalAppConfigs.put(method.getReturnType(), method);
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
            Class<?>[] paramsType = method.getParameterTypes();
            if (method.getParameterTypes().length != 0 && (paramsType.length == 1 && paramsType[0] != String.class)) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                Class<?> returnType = method.getReturnType();
                if (returnType == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(activity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    List<Method> methodList = localConfigs.get(activity.getClass());
                    if (methodList == null) {
                        methodList = new ArrayList<>();
                        localConfigs.put(activity.getClass(), methodList);
                    }
                    methodList.add(method);
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
            Class<?>[] paramsType = method.getParameterTypes();
            if (method.getParameterTypes().length != 0 && (paramsType.length == 1 && paramsType[0] != String.class)) {
                continue;
            }
            Config config = method.getAnnotation(Config.class);
            if (config != null) {
                Class<?> returnType = method.getReturnType();
                if (returnType == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(fragment);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    List<Method> methodList = localConfigs.get(fragment.getClass());
                    if (methodList == null) {
                        methodList = new ArrayList<>();
                        localConfigs.put(fragment.getClass(), methodList);
                    }
                    methodList.add(method);
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
                        Class<?>[] paramsType = method.getParameterTypes();
                        Class<?> returnType = method.getReturnType();
                        if (paramsType.length == 1 && returnType == Void.TYPE) {
                            handlerConfigs.put(method.getParameterTypes()[0], method);
                        } else if (returnType == Void.TYPE) {
                            try {
                                method.setAccessible(true);
                                method.invoke(method.getDeclaringClass());
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (paramsType.length == 0) {
                            try {
                                method.setAccessible(true);
                                configs.put(returnType, method.invoke(method.getDeclaringClass()));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else if (paramsType.length == 1 || (paramsType.length == 2 && paramsType[1] == String.class)) {
                            Method find = globalStaticConfigs.get(method.getReturnType());
                            if (find != null) {
                                throw new IllegalStateException(method.getReturnType().getName() + "类型已配置config，请勿重复配置（"
                                        + (method.getDeclaringClass().getName() + "." + method.getName()) + "<=>"
                                        + (find.getDeclaringClass().getName() + "." + find.getName()) + "）");
                            }
                            globalStaticConfigs.put(method.getReturnType(), method);
                        }
                    }
                }
                found.add(type);
            }
            type = type.getSuperclass();
        } while (type != null && type != Object.class && !type.getName().startsWith("com.loror.mvvm"));
    }

    /**
     * 获取Object
     */
    protected static Object getConfined(Class<?> type, Object obj) {
        return getConfined(type, obj, null);
    }

    /**
     * 获取Object
     */
    protected static Object getConfined(Class<?> type, Object obj, String fieldName) {
        Object data = configs.get(type);
        if (data == null && obj != null) {
            List<Method> methods = localConfigs.get(obj.getClass());
            if (methods != null) {
                for (Method method : methods) {
                    if (method.getReturnType().isAssignableFrom(obj.getClass())) {
                        try {
                            if (method.getParameterTypes().length == 0) {
                                data = method.invoke(obj);
                            } else {
                                data = method.invoke(obj, fieldName);
                            }
                            break;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (data == null && obj != null) {
            Method method = globalAppConfigs.get(type);
            if (method != null) {
                if (method.getReturnType().isAssignableFrom(obj.getClass())) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    try {
                        if (method.getParameterTypes().length == 1 && paramType == String.class) {
                            data = method.invoke(application, fieldName);
                        } else if (paramType.isAssignableFrom(obj.getClass())) {
                            if (method.getParameterTypes().length == 1) {
                                data = method.invoke(application, obj);
                            } else {
                                data = method.invoke(application, obj, fieldName);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (data == null && obj != null) {
            Method method = globalStaticConfigs.get(type);
            if (method != null) {
                if (method.getReturnType().isAssignableFrom(obj.getClass())) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    try {
                        if (method.getParameterTypes().length == 1 && paramType == String.class) {
                            data = method.invoke(method.getDeclaringClass(), fieldName);
                        } else if (paramType.isAssignableFrom(obj.getClass())) {
                            if (method.getParameterTypes().length == 1) {
                                data = method.invoke(method.getDeclaringClass(), obj);
                            } else {
                                data = method.invoke(method.getDeclaringClass(), obj, fieldName);
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return data;
    }
}
