package com.loror.mvvm.utls;

import android.app.Dialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.loror.lororUtil.flyweight.ObjectPool;
import com.loror.lororUtil.http.api.ApiClient;
import com.loror.mvvm.annotation.Sign;
import com.loror.mvvm.core.MvvmViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 自动赋值工具
 */
public class SignUtil {

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
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.set(obj.getClass(), binding);
                    } else {
                        field.set(obj, binding);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    break;
                }
            } else if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
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
                        e.printStackTrace();
                        break;
                    }
                }
            } else if (field.getType() == Handler.class) {
                field.setAccessible(true);
                try {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.set(obj.getClass(), ObjectPool.getInstance().getHandler());
                    } else {
                        field.set(obj, ObjectPool.getInstance().getHandler());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
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
            if (ViewDataBinding.class.isAssignableFrom(type)) {
                method.setAccessible(true);
                try {
                    if (Modifier.isStatic(method.getModifiers())) {
                        method.invoke(method.getDeclaringClass(), binding);
                    } else {
                        method.invoke(obj, binding);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    break;
                }
            } else if (MvvmViewModel.class.isAssignableFrom(type)) {
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
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } else if (type == Handler.class) {
                method.setAccessible(true);
                try {
                    if (Modifier.isStatic(method.getModifiers())) {
                        method.invoke(method.getDeclaringClass(), ObjectPool.getInstance().getHandler());
                    } else {
                        method.invoke(obj, ObjectPool.getInstance().getHandler());
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * 为Sign注解字段赋值
     */
    public static ViewDataBinding sign(AppCompatActivity obj, int layoutResID) {
        signApi(obj);
        ViewDataBinding binding = DataBindingUtil.setContentView(obj, layoutResID);
        signFieldAndMethod(obj, binding);
        return binding;
    }

    /**
     * 为Sign注解字段赋值
     */
    public static ViewDataBinding sign(Fragment obj, LayoutInflater inflater, int layoutResID, ViewGroup container) {
        signApi(obj);
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutResID, container, false);
        signFieldAndMethod(obj, binding);
        return binding;
    }

    /**
     * 为Sign注解字段赋值
     */
    public static ViewDataBinding sign(Dialog obj, int layoutResID) {
        signApi(obj);
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(obj.getContext()), layoutResID, null, false);
        signFieldAndMethod(obj, binding);
        return binding;
    }

    /**
     * 为Sign注解api字段赋值
     */
    public static void signApi(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            if (field.getType().isInterface()) {
                Object api = ConfigUtil.getApi(field.getType());
                if (api == null) {
                    api = new ApiClient().create(field.getType());
                }
                field.setAccessible(true);
                try {
                    field.set(obj, api);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
