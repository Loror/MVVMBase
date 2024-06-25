package com.loror.mvvmbase.common.util;

import android.util.Log;

/**
 * 统一log，用于替换普通Log
 * <p>
 * example 吉利规范
 * Log.e()  --- 优先级最高；Error 等级日志，记录进程关键且需要分析解决的信息
 * Log.w() --- 优先级高；   Warning，不会报错，但需要重点关注的信息
 * Log.i()  --- 优先级一般；Info，有效信息，进程关键流程，关键状态等的信息
 * Log.d() --- 优先级低；   Debug，打印进程运行的过程信息，用于调试跟踪的信息
 * Log.v() --- 优先级低；   Verbose，开发过程中用于调试，可随时关闭的信息
 */
public class Logger {

    public static final boolean PRINT = true;//是否打印日志，默认打印

    public static final String TAG = "Loror";

    public static void v(String tag, String message) {
        log(Log.VERBOSE, tag, message);
    }

    public static void d(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public static void i(String tag, String message) {
        log(Log.INFO, tag, message);
    }

    public static void w(String tag, String message) {
        log(Log.WARN, tag, message);
    }

    public static void w(String tag, String message, Throwable e) {
        w(tag, message + " " + Log.getStackTraceString(e));
    }

    public static void e(String tag, String message) {
        log(Log.ERROR, tag, message);
    }

    public static void e(String tag, String message, Throwable e) {
        e(tag, message + " " + Log.getStackTraceString(e));
    }

    private static void log(int level, String tag, String message) {
        if (!PRINT) {
            return;
        }
        message = "[" + tag + "]: " + message;
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, message);
                break;
            case Log.DEBUG:
                Log.d(TAG, message);
                break;
            case Log.INFO:
                Log.i(TAG, message);
                break;
            case Log.WARN:
                Log.w(TAG, message);
                break;
            case Log.ERROR:
                Log.e(TAG, message);
                break;
        }
    }
}
