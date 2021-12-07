package com.loror.mvvm.core;

import android.os.Bundle;

import androidx.annotation.Nullable;

public interface MvvmView {

    /**
     * 获取layout
     */
    int getLayout();

    /**
     * 初始化控件
     */
    void initView(@Nullable Bundle savedInstanceState);
}
