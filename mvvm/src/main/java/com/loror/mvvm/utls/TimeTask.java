package com.loror.mvvm.utls;


import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;

public class TimeTask {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Callback callback;
    private long offset;

    public interface Callback {
        void onCallBack(long time);
    }

    private long time;
    private final long total;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            callback.onCallBack(time);
            if (time > 0) {
                handler.postAtTime(this, offset = (offset + 1000));
            }
        }
    };

    public TimeTask(long time, @NonNull Callback callback) {
        this.total = this.time = time;
        this.callback = callback;
    }

    /**
     * 启动
     */
    public void start() {
        offset = SystemClock.uptimeMillis();
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    /**
     * 暂停
     */
    public void pause() {
        handler.removeCallbacks(runnable);
    }

    /**
     * 停止
     */
    public void stop() {
        stop(false);
    }

    /**
     * 停止
     */
    public void stop(boolean resume) {
        pause();
        time = total;
        if (resume) {
            callback.onCallBack(0);
        }
    }

}
