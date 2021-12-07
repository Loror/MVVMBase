package com.loror.mvvm.dialog;

public interface ProgressDialog {

    /**
     * 设置文本
     */
    ProgressDialog setText(String message);

    /**
     * 显示
     */
    void show();

    /**
     * 隐藏
     */
    void dismiss();
}
