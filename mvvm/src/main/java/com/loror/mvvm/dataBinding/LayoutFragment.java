package com.loror.mvvm.dataBinding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.core.MvvmFragment;
import com.loror.mvvm.core.MvvmView;
import com.loror.mvvm.utls.SignUtil;

/**
 * databinding支持
 */
public abstract class LayoutFragment extends MvvmFragment implements MvvmView {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, getLayout(), container, false);
        SignUtil.sign(this, binding);
        if (binding == null) {
            view = inflater.inflate(getLayout(), container, false);
        } else {
            view = binding.getRoot();
        }
        initView(savedInstanceState);
        return view;
    }
}
