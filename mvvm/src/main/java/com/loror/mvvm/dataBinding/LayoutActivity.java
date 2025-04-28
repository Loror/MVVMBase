package com.loror.mvvm.dataBinding;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.core.MvvmActivity;
import com.loror.mvvm.core.MvvmView;
import com.loror.mvvm.utls.SignUtil;

/**
 * databinding支持
 */
public abstract class LayoutActivity extends MvvmActivity implements MvvmView {

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
