package com.loror.mvvm.dataBinding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loror.mvvm.core.MvvmDialog;
import com.loror.mvvm.core.MvvmView;
import com.loror.mvvm.utls.SignUtil;

/**
 * databinding支持
 */
public abstract class LayoutDialog extends MvvmDialog implements MvvmView {

    public LayoutDialog(@NonNull Context context) {
        super(context);
    }

    public LayoutDialog(@NonNull Context context, int themeResId) {
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
