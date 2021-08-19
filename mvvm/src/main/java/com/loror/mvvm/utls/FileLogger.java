package com.loror.mvvm.utls;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.loror.lororUtil.asynctask.RemoveableThreadPool;
import com.loror.lororUtil.asynctask.ThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger {

    private static boolean isSaveLog = true;
    private static String dir;
    private static String date;
    private static final RemoveableThreadPool threadPool = new ThreadPool(1);
    private static final DateFormat formatter;

    static {
        formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss:SSS", Locale.CHINA);
    }

    private static String getDate() {
        if (date == null) {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        }
        return date;
    }

    private FileLogger() {
    }

    public static void setIsSaveLog(boolean isSaveLog) {
        FileLogger.isSaveLog = isSaveLog;
    }

    public static void setSaveDir(String dir) {
        FileLogger.dir = dir;
    }

    public static boolean d(String message) {
        if (TextUtils.isEmpty(dir)) {
            return false;
        }
        if (!isSaveLog) {
            return true;
        }
        if (message == null) {
            message = "";
        }
        if (!message.endsWith("\n")) {
            message += "\n";
        }
        String date = formatter.format(new Date());
        String finalMessage = message;
        threadPool.excute(() -> {
            try {
                String fileName = "log-" + getDate() + ".log";
                if (Environment.getExternalStorageState().equals("mounted")) {
                    File dirFile = new File(dir);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }

                    File file = new File(dirFile, fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(file, true);
                    fos.write((date + ":" + finalMessage).getBytes());
                    fos.close();
                }
            } catch (Exception var8) {
                Log.e("BLog", "an error occured while writing file...", var8);
            }
        });
        return true;
    }

}
