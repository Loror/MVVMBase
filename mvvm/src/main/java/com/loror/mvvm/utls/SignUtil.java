package com.loror.mvvm.utls;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.view.LayoutInflater;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewbinding.ViewBinding;

import com.loror.mvvm.annotation.Sign;
import com.loror.mvvm.core.MvvmSign;
import com.loror.mvvm.core.MvvmViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 自动赋值工具
 */
public class SignUtil {

    private static boolean viewBindingSupport;

    static {
        try {
            viewBindingSupport = ViewBinding.class != null;
        } catch (Throwable e) {
            viewBindingSupport = false;
        }
    }

    /**
     * 手动注册@Sign支持的类型
     */
    public static <S, T extends S> void registerSign(Class<S> type, T data, boolean allowCover) {
        MvvmSign.registerSign(type, data, allowCover);
    }

    /**
     * 为Sign注解字段赋值
     */
    public static void sign(Object obj) {
        signConfig(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            signViewModel(obj, field, null);
            if (viewBindingSupport) {
                signViewBinding(obj, field, null);
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
            signViewModel(obj, null, method);
            if (viewBindingSupport) {
                signViewBinding(obj, null, method);
            }
        }
    }

    /**
     * 为Sign注解字段赋值
     */
    public static void sign(Object obj, ViewDataBinding binding) {
        signConfig(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            signViewModel(obj, field, null);
            if (binding != null) {
                signDataBinding(obj, field, null, binding);
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
            signViewModel(obj, null, method);
            if (binding != null) {
                signDataBinding(obj, null, method, binding);
            }
        }
    }

    /**
     * 为Sign注解字段赋值，特殊类型
     */
    private static void signViewModel(Object obj, Field field, Method method) {
        if (field != null) {
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
            }
        } else if (method != null) {
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
            }
        }
    }

    /**
     * 为Sign注解字段赋值，特殊类型
     */
    private static void signDataBinding(Object obj, Field field, Method method, ViewDataBinding binding) {
        if (field != null) {
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                if (binding == null) {
                    return;
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
        } else if (method != null) {
            Class<?> type = method.getParameterTypes()[0];
            if (ViewDataBinding.class.isAssignableFrom(type)) {
                if (binding == null) {
                    return;
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
     * 为Sign注解字段赋值，特殊类型
     */
    private static void signViewBinding(Object obj, Field field, Method method) {
        LayoutInflater inflater = null;
        if (obj instanceof Activity) {
            inflater = ((Activity) obj).getLayoutInflater();
        } else if (obj instanceof Fragment) {
            inflater = LayoutInflater.from(((Fragment) obj).getActivity());
        } else if (obj instanceof androidx.fragment.app.Fragment) {
            inflater = LayoutInflater.from(((androidx.fragment.app.Fragment) obj).getActivity());
        } else if (obj instanceof Dialog) {
            inflater = LayoutInflater.from(((Dialog) obj).getContext());
        }
        if (inflater == null) {
            return;
        }
        if (field != null) {
            if (ViewBinding.class.isAssignableFrom(field.getType())) {
                ViewBinding binding = create(inflater, field.getType());
                if (binding == null) {
                    return;
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
        } else if (method != null) {
            Class<?> type = method.getParameterTypes()[0];
            if (ViewBinding.class.isAssignableFrom(type)) {
                ViewBinding binding = create(inflater, type);
                if (binding == null) {
                    return;
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

    private static ViewBinding create(LayoutInflater inflater, Class<?> type) {
        try {
            Method method = type.getMethod("inflate", LayoutInflater.class);
            Object binding = method.invoke(type, inflater);
            return (ViewBinding) binding;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 为Sign注解字段赋值
     */
    public static void signConfig(Object obj) {
        MvvmSign.signConfig(obj);
    }
}
