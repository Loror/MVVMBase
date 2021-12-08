package com.loror.mvvm.core;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignActivity extends MvvmActivity implements MvvmView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, getLayout());
        SignUtil.sign(this, binding);
        if (binding == null) {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }

}
