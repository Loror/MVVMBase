package com.loror.mvvm.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.loror.mvvm.dataBinding.LayoutDialog;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.ScreenUtil;
import com.loror.mvvm.utls.SignUtil;

public class MvvmDialog extends Dialog {
    private final Context context;

    public MvvmDialog(@NonNull Context context) {
        this(context, 0);
    }

    public MvvmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        ConfigUtil.config(getClass());
        if (!(this instanceof LayoutDialog)) {
            SignUtil.signConfig(this);
        } else if (MvvmSign.SIGN_VIEW) {
            SignUtil.sign(this);
        }
    }

    /**
     * 获取屏幕宽度像素
     */
    protected int getScreenWidth() {
        if (context instanceof Activity) {
            return ScreenUtil.getScreenWidth((Activity) context);
        }
        return 1080;
    }

    /**
     * 获取屏幕宽度像素
     */
    protected int getScreenHeight() {
        if (context instanceof Activity) {
            return ScreenUtil.getScreenHeight((Activity) context);
        }
        return 1920;
    }

    protected void showAtCenter(double bl) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (getScreenWidth() * bl);
        dialogWindow.setAttributes(lp);
    }

    protected void showAtCenter() {
        showAtCenter(0.9);
    }

    protected void showAtBottom() {
        showAtBottom(0);
    }

    protected void showAtBottom(int height) {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (height > 0) {
            lp.height = height;
        }
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    protected void hideSoft(View v) {
        if (v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
