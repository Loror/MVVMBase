package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.loror.lororUtil.flyweight.ObjectPool;
import com.loror.lororUtil.http.api.ApiClient;
import com.loror.lororUtil.http.api.OnRequestListener;
import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.annotation.Service;
import com.loror.mvvm.bean.ApiInfo;
import com.loror.mvvm.bean.SignInfo;
import com.loror.mvvm.core.ConfigApplication;
import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.dialog.ProgressDialog;

import java.lang.reflect.Constructor;
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
     * 查找过service的
     */
    private static final List<Class<?>> foundService = new ArrayList<>();

    /**
     * 配置
     */
    private static final Map<Class<?>, Method> handlerConfigs = new HashMap<>();

    /**
     * 全局配置
     * key 赋值类型
     * value configs：可用值 globalAppConfigs、globalStaticConfigs可用方法，参数数量1（obj） 或 2（obj,SignInfo）
     */
    private static final Map<Class<?>, Object> configs = new HashMap<>();
    private static final Map<Class<?>, List<Method>> globalAppConfigs = new HashMap<>();
    private static final Map<Class<?>, List<Method>> globalStaticConfigs = new HashMap<>();

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
        return (ProgressDialog) getConfined(ProgressDialog.class, activity, null);
    }

    /**
     * 获取ProgressDialog
     */
    public static ProgressDialog progressDialogForFragment(Fragment fragment) {
        return (ProgressDialog) getConfined(ProgressDialog.class, fragment, null);
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
                handlerException(e);
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
                    throw new RuntimeException(e);
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
        configs.put(Application.class, application);
        configs.put(application.getClass(), application);
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
                        ConfigUtil.handlerException(e);
                    }
                } else if (paramsType.length == 0) {
                    try {
                        method.setAccessible(true);
                        configs.put(returnType, method.invoke(application));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ConfigUtil.handlerException(e);
                    }
                } else if (paramsType.length == 1 || (paramsType.length == 2 && paramsType[1] == SignInfo.class)) {
                    List<Method> find = globalAppConfigs.get(method.getReturnType());
                    if (find != null) {
                        for (Method item : find) {
                            if (ArrayUtils.same(item.getParameterTypes(), method.getParameterTypes())) {
                                throw new IllegalStateException(method.getReturnType().getName() + "类型已配置config，请勿重复配置");
                            }
                        }
                    } else {
                        find = new ArrayList<>();
                        globalAppConfigs.put(method.getReturnType(), find);
                    }
                    find.add(method);
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
            if (method.getParameterTypes().length > 1 || (paramsType.length == 1 && paramsType[0] != SignInfo.class)) {
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
                        ConfigUtil.handlerException(e);
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
            if (method.getParameterTypes().length > 1 || (paramsType.length == 1 && paramsType[0] != SignInfo.class)) {
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
                        ConfigUtil.handlerException(e);
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
    public static synchronized void config(Class<?> type) {
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
                                ConfigUtil.handlerException(e);
                            }
                        } else if (paramsType.length == 0) {
                            try {
                                method.setAccessible(true);
                                configs.put(returnType, method.invoke(method.getDeclaringClass()));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                ConfigUtil.handlerException(e);
                            }
                        } else if (paramsType.length == 1 || (paramsType.length == 2 && paramsType[1] == SignInfo.class)) {
                            List<Method> find = globalStaticConfigs.get(method.getReturnType());
                            if (find != null) {
                                for (Method item : find) {
                                    if (ArrayUtils.same(item.getParameterTypes(), method.getParameterTypes())) {
                                        throw new IllegalStateException(method.getReturnType().getName() + "类型已配置config，请勿重复配置（"
                                                + (method.getDeclaringClass().getName() + "." + method.getName()) + "<=>"
                                                + (item.getDeclaringClass().getName() + "." + item.getName()) + "）");
                                    }
                                }
                            } else {
                                find = new ArrayList<>();
                                globalStaticConfigs.put(method.getReturnType(), find);
                            }
                            find.add(method);
                        }
                    }
                }
                found.add(type);
            }
            type = type.getSuperclass();
        } while (type != null && type != Object.class && !type.getName().startsWith("com.loror.mvvm"));
    }

    /**
     * 获取单例Object
     */
    private static synchronized Object getConstConfined(Class<?> type, Object obj) {
        Object data = configs.get(type);
        if (data == null && !foundService.contains(type)) {
            Service service = type.getAnnotation(Service.class);
            if (service != null) {
                boolean intoPool = service.intoPool();
                Class<?> generate = type;
                if (type.isInterface()) {
                    if (service.value() == Object.class) {
                        ApiClient apiClient = new ApiClient();
                        ApiInfo apiInfo = new ApiInfo(apiClient, type);
                        handler(apiInfo);
                        OnRequestListener onRequestListener = apiInfo.getMultiOnRequestListener();
                        if (!apiClient.hasOnRequestListener()) {
                            apiClient.setOnRequestListener(onRequestListener);
                        }
                        data = apiClient.create(type);
                        if (intoPool) {
                            configs.put(type, data);
                            foundService.add(type);
                        }
                        return data;
                    } else {
                        if (!type.isAssignableFrom(service.value())) {
                            throw new IllegalStateException(service.value().getName() + "未实现" + type.getName());
                        }
                        generate = service.value();
                    }
                }
                if (Modifier.isAbstract(generate.getModifiers())) {
                    throw new IllegalStateException("无法构造抽象类" + generate.getName());
                }
                Constructor<?>[] constructors = generate.getConstructors();
                if (constructors.length == 1) {
                    Constructor<?> constructor = constructors[0];
                    try {
                        constructor.setAccessible(true);
                        Class<?>[] paramTypes = constructor.getParameterTypes();
                        if (paramTypes.length == 0) {
                            data = constructor.newInstance();
                            SignUtil.signConfig(data);
                        } else {
                            Object[] args = new Object[paramTypes.length];
                            for (int i = 0; i < paramTypes.length; i++) {
                                Class<?> paramType = paramTypes[i];
                                args[i] = getConstConfined(paramType, obj);
                                if (args[i] == null) {
                                    if (obj != null && paramType.isAssignableFrom(obj.getClass())) {
                                        intoPool = false;
                                        args[i] = obj;
                                    } else {
                                        throw new IllegalArgumentException(generate.getName() + "构造所需参数必须是已配置过类型");
                                    }
                                }
                            }
                            data = constructor.newInstance(args);
                        }
                        if (intoPool) {
                            configs.put(type, data);
                            foundService.add(type);
                            if (type != generate) {
                                configs.put(generate, data);
                            } else if (type.getSuperclass() != null && type.getSuperclass().isInterface()) {
                                configs.put(type.getSuperclass(), data);
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        ConfigUtil.handlerException(e);
                    }
                }
            }
        }
        return data;
    }

    /**
     * 获取Object
     */
    protected static Object getConfined(Class<?> type, Object obj, SignInfo signInfo) {
        Object data = getConstConfined(type, obj);
        if (data == null && obj != null) {
            List<Method> methods = localConfigs.get(obj.getClass());
            if (methods != null) {
                for (Method method : methods) {
                    if (type == method.getReturnType()) {
                        try {
                            if (method.getParameterTypes().length == 0) {
                                data = method.invoke(obj);
                            } else {
                                data = method.invoke(obj, signInfo);
                            }
                            break;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            ConfigUtil.handlerException(e);
                        }
                    }
                }
            }
        }
        if (data == null && obj != null) {
            List<Method> methods = globalAppConfigs.get(type);
            if (methods != null) {
                for (Method method : methods) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    try {
                        if (method.getParameterTypes().length == 1 && paramType == SignInfo.class) {
                            data = method.invoke(application, signInfo);
                            break;
                        } else if (paramType.isAssignableFrom(obj.getClass())) {
                            if (method.getParameterTypes().length == 1) {
                                data = method.invoke(application, obj);
                            } else {
                                data = method.invoke(application, obj, signInfo);
                            }
                            break;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ConfigUtil.handlerException(e);
                    }
                }
            }
        }
        if (data == null && obj != null) {
            List<Method> methods = globalStaticConfigs.get(type);
            if (methods != null) {
                for (Method method : methods) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    try {
                        if (method.getParameterTypes().length == 1 && paramType == SignInfo.class) {
                            data = method.invoke(method.getDeclaringClass(), signInfo);
                            break;
                        } else if (paramType.isAssignableFrom(obj.getClass())) {
                            if (method.getParameterTypes().length == 1) {
                                data = method.invoke(method.getDeclaringClass(), obj);
                            } else {
                                data = method.invoke(method.getDeclaringClass(), obj, signInfo);
                            }
                            break;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ConfigUtil.handlerException(e);
                    }
                }
            }
        }
        return data;
    }

    /**
     * 获取Object
     */
    protected static Object getSingletonConfig(Class<?> type) {
        return configs.get(type);
    }

    /**
     * 添加Object
     */
    protected static void addSingletonConfig(Class<?> type, Object data) {
        configs.put(type, data);
    }
}
