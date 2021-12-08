package com.loror.mvvm.utls;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.loror.mvvm.annotation.Sign;
import com.loror.mvvm.core.MvvmViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 自动赋值工具
 */
public class SignUtil {

    /**
     * 为Sign注解字段赋值
     */
    public static void sign(Object obj, ViewDataBinding binding) {
        signFieldAndMethod(obj, binding);
        signConfig(obj);
    }

    /**
     * 为Sign注解字段赋值
     */
    private static void signFieldAndMethod(Object obj, ViewDataBinding binding) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
                if (obj instanceof LifecycleOwner && obj instanceof ViewModelStoreOwner) {
                    field.setAccessible(true);
                    try {
                        MvvmViewModel viewModel = (MvvmViewModel) new ViewModelProvider((ViewModelStoreOwner) obj).get((Class) field.getType());
                        viewModel.attachView((LifecycleOwner) obj);
                        if (Modifier.isStatic(field.getModifiers())) {
                            field.set(obj.getClass(), viewModel);
                        } else {
                            field.set(obj, viewModel);
                        }
                    } catch (Exception e) {
                        ConfigUtil.handlerException(e);
                    }
                }
            } else if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                if (binding == null) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.set(obj.getClass(), binding);
                    } else {
                        field.set(obj, binding);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (!method.getName().startsWith("set") || method.getParameterTypes().length != 1) {
                continue;
            }
            Sign sign = method.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            Class<?> type = method.getParameterTypes()[0];
            if (MvvmViewModel.class.isAssignableFrom(type)) {
                if (obj instanceof LifecycleOwner && obj instanceof ViewModelStoreOwner) {
                    method.setAccessible(true);
                    try {
                        MvvmViewModel viewModel = (MvvmViewModel) new ViewModelProvider((ViewModelStoreOwner) obj).get((Class) type);
                        viewModel.attachView((LifecycleOwner) obj);
                        if (Modifier.isStatic(method.getModifiers())) {
                            method.invoke(method.getDeclaringClass(), viewModel);
                        } else {
                            method.invoke(obj, viewModel);
                        }
                    } catch (Exception e) {
                        ConfigUtil.handlerException(e);
                    }
                }
            } else if (ViewDataBinding.class.isAssignableFrom(type)) {
                if (binding == null) {
                    continue;
                }
                method.setAccessible(true);
                try {
                    if (Modifier.isStatic(method.getModifiers())) {
                        method.invoke(method.getDeclaringClass(), binding);
                    } else {
                        method.invoke(obj, binding);
                    }
                } catch (Exception e) {
                    ConfigUtil.handlerException(e);
                }
            }
        }
    }

    /**
     * 为Sign注解api字段赋值
     */
    public static void signConfig(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (field.get(obj) == null) {
                    Object conf = ConfigUtil.getConfined(field.getType());
                    if (conf != null) {
                        field.set(obj, conf);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (!method.getName().startsWith("set") || method.getParameterTypes().length != 1) {
                continue;
            }
            Sign sign = method.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            Class<?> type = method.getParameterTypes()[0];
            Object conf = ConfigUtil.getConfined(type);
            if (conf != null) {
                method.setAccessible(true);
                try {
                    method.invoke(obj, conf);
                } catch (Exception e) {
                    ConfigUtil.handlerException(e);
                }
            }
        }
    }
}
