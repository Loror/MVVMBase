package com.loror.mvvm.utls;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.loror.lororUtil.http.api.ApiClient;
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
    public static ViewDataBinding sign(AppCompatActivity obj, int layoutResID) {
        signApi(obj);
        ViewDataBinding binding = DataBindingUtil.setContentView(obj, layoutResID);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    field.set(obj, binding);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    break;
                }
            } else if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
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
        return binding;
    }

    /**
     * 为Sign注解字段赋值
     */
    public static ViewDataBinding sign(Fragment obj, LayoutInflater inflater, int layoutResID, ViewGroup container) {
        signApi(obj);
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutResID, container, false);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            if (ViewDataBinding.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    field.set(obj, binding);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    break;
                }
            } else if (MvvmViewModel.class.isAssignableFrom(field.getType())) {
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
