package com.loror.mvvm.utls;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.loror.mvvm.annotation.Sign;
import com.loror.mvvm.core.MvvmViewModel;

import java.lang.reflect.Field;

/**
 * 自动赋值工具
 */
public class SignUtil {

    /**
     * 为Sign注解字段赋值
     */
    public static boolean sign(AppCompatActivity obj, int layoutResID) {
        ViewDataBinding binding = DataBindingUtil.setContentView(obj, layoutResID);
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean result = false;
        for (Field field : fields) {
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                Sign sign = field.getAnnotation(Sign.class);
                if (sign != null) {
                    field.setAccessible(true);
                    try {
                        field.set(obj, binding);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        break;
                    }
                    result = true;
                }
            } else if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
                Sign sign = field.getAnnotation(Sign.class);
                if (sign != null) {
                    field.setAccessible(true);
                    try {
                        MvvmViewModel viewModel = (MvvmViewModel) new ViewModelProvider(obj).get((Class) field.getType());
                        viewModel.attachView(obj);
                        field.set(obj, viewModel);
                        result = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 为Sign注解字段赋值
     */
    public static ViewDataBinding sign(Fragment obj, LayoutInflater inflater, int layoutResID, ViewGroup container) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutResID, container, false);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                Sign sign = field.getAnnotation(Sign.class);
                if (sign != null) {
                    field.setAccessible(true);
                    try {
                        field.set(obj, binding);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } else if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
                Sign sign = field.getAnnotation(Sign.class);
                if (sign != null) {
                    field.setAccessible(true);
                    try {
                        MvvmViewModel viewModel = (MvvmViewModel) new ViewModelProvider(obj).get((Class) field.getType());
                        viewModel.attachView(obj);
                        field.set(obj, viewModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        return binding;
    }
}
