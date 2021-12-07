package com.loror.mvvm.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.loror.mvvm.utls.SignUtil;

public abstract class MvvmSignActivity extends MvvmActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SignUtil.sign(this, getLayout()) == null) {
            setContentView(getLayout());
        }
        initView(savedInstanceState);
    }

    /**
     * 获取layout
     */
    public abstract int getLayout();

    /**
     * 初始化控件
     */
    public abstract void initView(@Nullable Bundle savedInstanceState);
}
