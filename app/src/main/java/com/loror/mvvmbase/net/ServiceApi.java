package com.loror.mvvmbase.net;

import com.loror.lororUtil.annotation.BaseUrl;
import com.loror.lororUtil.annotation.GET;
import com.loror.lororUtil.http.Responce;
import com.loror.lororUtil.http.api.Observable;

import org.jetbrains.annotations.NotNull;

@BaseUrl("https://www.baidu.com")
public interface ServiceApi {

    @GET("/")
    @NotNull
    Observable<Responce> baidu();
}
