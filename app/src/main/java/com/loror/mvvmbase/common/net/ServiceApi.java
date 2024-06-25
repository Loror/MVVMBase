package com.loror.mvvmbase.common.net;

import com.loror.lororUtil.annotation.BaseUrl;
import com.loror.lororUtil.annotation.GET;
import com.loror.lororUtil.http.Responce;
import com.loror.lororUtil.http.api.Observable;
import com.loror.mvvm.annotation.Service;

import org.jetbrains.annotations.NotNull;

@Service
@BaseUrl("https://www.baidu.com")
public interface ServiceApi {

    @GET("/")
    @NotNull
    Observable<Responce> baidu();
}
