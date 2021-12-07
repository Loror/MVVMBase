package com.loror.mvvm.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignActivity extends MvvmActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SignUtil.sign(this, getLayout())) {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }

    public abstract int getLayout();

    public abstract void initView(@Nullable Bundle savedInstanceState);
}
