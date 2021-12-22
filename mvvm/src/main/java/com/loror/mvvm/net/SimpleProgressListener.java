package com.loror.mvvm.net;

import com.loror.lororUtil.http.ProgressListener;

public abstract class SimpleProgressListener implements ProgressListener {

    public void finish(boolean success) {
        if (success) {
            this.transing(-1.0F, 0, 0L);
        } else {
            this.transing(-2.0F, 0, 0L);
        }
    }
}
