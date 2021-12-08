package com.loror.mvvm.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignDialog extends MvvmDialog implements MvvmView {

    public MvvmSignDialog(@NonNull Context context) {
        super(context);
    }

    public MvvmSignDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), getLayout(), null, false);
        SignUtil.sign(this, binding);
        if (binding != null) {
            setContentView(binding.getRoot());
        } else {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }
}
