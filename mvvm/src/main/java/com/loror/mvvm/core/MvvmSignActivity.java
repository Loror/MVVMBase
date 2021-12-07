package com.loror.mvvm.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignActivity extends MvvmActivity implements MvvmView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SignUtil.sign(this, getLayout()) == null) {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }

}
