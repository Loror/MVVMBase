package com.loror.mvvm.widget;

import android.os.SystemClock;
import android.view.View;

import androidx.annotation.CallSuper;

import com.loror.mvvm.utls.ConfigUtil;

public class OnSafeClickListener implements View.OnClickListener {

    private final int clickSpace;

    private long click;
    private final View.OnClickListener l;

    public OnSafeClickListener(View.OnClickListener l) {
        this(800, l);
    }

    public OnSafeClickListener(int space, View.OnClickListener l) {
        this.clickSpace = space;
        this.l = l;
    }

    @Override
    public void onClick(View v) {
        if (l != null) {
            long now = SystemClock.uptimeMillis();
            if (now - click > clickSpace) {
                click = now;
                try {
                    l.onClick(v);
                } catch (Throwable e) {
                    onError(e);
                }
            }
        }
    }

    /**
     * 当发生异常
     */
    @CallSuper
    protected void onError(Throwable e) {
        ConfigUtil.handlerException(e);
    }
}
