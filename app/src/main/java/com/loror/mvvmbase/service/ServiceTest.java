package com.loror.mvvmbase.service;

import com.loror.lororUtil.http.Responce;
import com.loror.lororUtil.http.api.Observable;
import com.loror.mvvm.annotation.Service;

@Service(ServiceTestImpl.class)
public interface ServiceTest {

    Observable<Responce> baidu();
}
