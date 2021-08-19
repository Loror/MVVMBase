package com.loror.mvvm.widget;

import android.os.SystemClock;
import android.view.View;

public class OnSafeClickListener implements View.OnClickListener {

    final int CLICK_SPACE = 800;

    private long click;
    private final View.OnClickListener l;

    public OnSafeClickListener(View.OnClickListener l) {
        this.l = l;
    }

    @Override
    public void onClick(View v) {
        if (l != null) {
            long now = SystemClock.uptimeMillis();
            if (now - click > CLICK_SPACE) {
                click = now;
                try {
                    l.onClick(v);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
