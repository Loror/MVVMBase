package com.loror.mvvm.utls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
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
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean result = false;
        for (Field field : fields) {
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                Sign sign = field.getAnnotation(Sign.class);
                if (sign != null) {
                    field.setAccessible(true);
                    ViewDataBinding binding = DataBindingUtil.setContentView(obj, layoutResID);
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
}
