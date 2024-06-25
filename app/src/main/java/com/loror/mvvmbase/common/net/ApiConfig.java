package com.loror.mvvmbase.common.net;

import android.util.Log;

import com.loror.lororUtil.http.HttpClient;
import com.loror.lororUtil.http.api.ApiRequest;
import com.loror.lororUtil.http.api.ApiResult;
import com.loror.lororUtil.http.api.MultiOnRequestListener;
import com.loror.lororUtil.http.api.OnRequestListener;
import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.bean.ApiInfo;
import com.loror.mvvm.net.Action;
import com.loror.mvvm.net.CookieRequestListener;
import com.loror.mvvm.net.LogRequestListener;
import com.loror.mvvm.net.Message;
import com.loror.mvvmbase.BuildConfig;

public class ApiConfig {

    @Config
    public static void handlerMessage(Message message) {
        if ((message.getCode() & Action.UNKNOWN) != 0) {
            Log.e("ApiConfig", "这里处理自定义异常");
        }
    }

    @Config
    public static void handlerApi(ApiInfo apiInfo) {
        apiInfo.getApiClient().setOnRequestListener(
                new MultiOnRequestListener()
                        .addOnRequestListener(new CookieRequestListener())
                        .addOnRequestListener(new LogRequestListener(BuildConfig.DEBUG))
                        .addOnRequestListener(new OnRequestListener() {
                            @Override
                            public void onRequestBegin(HttpClient client, ApiRequest request) {
                                client.setTimeOut(15000);
                                client.setReadTimeOut(50000);
                            }

                            @Override
                            public void onRequestEnd(HttpClient client, ApiResult result) {

                            }
                        })
        );
    }
}
