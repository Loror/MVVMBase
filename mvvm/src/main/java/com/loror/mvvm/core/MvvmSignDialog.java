package com.loror.mvvm.core;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
        ViewDataBinding binding = SignUtil.sign(this, getLayout());
        if (binding != null) {
            setContentView(binding.getRoot());
        } else {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }
}
