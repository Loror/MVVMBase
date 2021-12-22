package com.loror.mvvm.net;

import android.text.TextUtils;
import android.util.Log;

import com.loror.lororUtil.http.Responce;
import com.loror.lororUtil.http.api.Observer;
import com.loror.lororUtil.http.api.ResultException;
import com.loror.mvvm.utls.ConfigUtil;

import java.lang.Exception;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public abstract class Action<T> implements Observer<T> {

    public static final int UNKNOWN = 0B10000000000;
    public static final int DNS_ERROR = 1;
    public static final int CONNECT_ERROR = 2;
    public static final int CONNECT_TIME_OUT = 4;
    public static final int REQUEST_CANCELED = 8;
    public static final int UNKNOWN_EXCEPTION = -1;

    public final void failed(int code, Throwable e) {
        Message message = new Message();
        if (e instanceof ResultException) {
            Responce responce = ((ResultException) e).getResponce();
            if (responce.getThrowable() instanceof UnknownHostException) {
                message.setCode(DNS_ERROR);
                message.setMessage("DNS解析异常");
            } else if (responce.getThrowable() instanceof ConnectException) {
                message.setCode(CONNECT_ERROR);
                message.setMessage("网络连接异常");
            } else if (responce.getThrowable() instanceof SocketTimeoutException) {
                message.setCode(CONNECT_TIME_OUT);
                message.setMessage("网络连接超时");
            } else if (responce.getThrowable() instanceof SocketException) {
                message.setCode(REQUEST_CANCELED);
                message.setMessage("网络请求取消");
            } else {
                message.setCode(UNKNOWN | code);
                message.setMessage(responce.toString());
            }
        } else {
            message.setCode(UNKNOWN_EXCEPTION);
            message.setMessage(Log.getStackTraceString(e));
            if (TextUtils.isEmpty(message.getMessage())) {
                message.setMessage("操作异常");
            }
            if (e != null) {
                e.printStackTrace();
            }
        }
        ConfigUtil.handler(message);
        this.failed(message);
    }

    public final void success(T data) {
        try {
            this.result(data);
        } catch (Throwable e) {
            this.failed(0, e);
        }
    }

    public abstract void failed(Message message);

    public abstract void result(T data);

}
