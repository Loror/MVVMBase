package com.loror.mvvm.utls;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log统一管理类
 * 解决打印过长打印不完整问题
 */
public class Logger {

    public static final String TAG = "DEFAULT_TAG";

    private Logger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //可以全局控制是否打印log日志
    private static  boolean isPrintLog = true;
    //日志分页长度
    private static final int LOG_MAX_LENGTH = 2000;
    private static final String DEFAULT_TAG = "LogUtil";

    public static void setIsPrintLog(boolean isPrintLog) {
        Logger.isPrintLog = isPrintLog;
    }

    public static void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public static void v(String tagName, String msg) {
        logSplit(tagName, "V", msg);
    }

    public static void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public static void d(String tagName, String msg) {
        logSplit(tagName, "D", msg);
    }

    public static void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public static void i(String tagName, String msg) {
        logSplit(tagName, "I", msg);
    }

    public static void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public static void w(String tagName, String msg) {
        logSplit(tagName, "W", msg);
    }

    public static void e(String msg) {
        e(DEFAULT_TAG, msg);
    }

    public static void e(String tagName, String msg) {
        logSplit(tagName, "E", msg);
    }

    /**
     * 根据level分段打印日志
     */
    private static void logSplit(String tagName, String level, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (isPrintLog) {
            int strLength = msg.length();
            if (strLength < LOG_MAX_LENGTH) {
                log(tagName, level, msg);
            } else {
                int start = 0;
                int end = LOG_MAX_LENGTH;
                for (int i = 0; i < 500; i++) {
                    if (strLength > end) {
                        log(tagName + i, level, msg.substring(start, end));
                        start = end;
                        end = end + LOG_MAX_LENGTH;
                    } else {
                        log(tagName + i, level, msg.substring(start, strLength));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 根据level打印日志
     */
    private static void log(String tagName, String level, String msg) {
        msg = "[" + tagName + "]: " + msg;
        switch (level) {
            case "V":
                Log.v(TAG, msg);
                break;
            case "D":
                Log.d(TAG, msg);
                break;
            case "I":
                Log.i(TAG, msg);
                break;
            case "W":
                Log.w(TAG, msg);
                break;
            case "E":
                Log.e(TAG, msg);
                break;
        }
    }


}
