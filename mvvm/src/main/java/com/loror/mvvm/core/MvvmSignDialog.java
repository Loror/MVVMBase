package com.loror.mvvm.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.loror.mvvm.dataBinding.LayoutDialog;

/**
 * 修改使用LayoutFragment
 */
@Deprecated
public abstract class MvvmSignDialog extends LayoutDialog {

    public MvvmSignDialog(@NonNull Context context) {
        super(context);
    }

    public MvvmSignDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
}
