package com.loror.mvvmbase.service;

import com.loror.lororUtil.http.Responce;
import com.loror.lororUtil.http.api.Observable;
import com.loror.mvvmbase.net.ServiceApi;

public class ServiceTestImpl implements ServiceTest {

    private final ServiceApi serviceApi;

    public ServiceTestImpl(ServiceApi serviceApi) {
        this.serviceApi = serviceApi;
    }

    @Override
    public Observable<Responce> baidu() {
        return serviceApi.baidu();
    }
}
