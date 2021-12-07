package com.loror.mvvm.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignFragment extends MvvmFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        ViewDataBinding binding = SignUtil.sign(this, inflater, getLayout(), container);
        if (binding == null) {
            view = inflater.inflate(getLayout(), container, false);
        } else {
            view = binding.getRoot();
        }
        initView(savedInstanceState);
        return view;
    }

    public abstract int getLayout();

    public abstract void initView(@Nullable Bundle savedInstanceState);
}
