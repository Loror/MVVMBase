package com.loror.mvvmbase.model;

import com.loror.mvvm.annotation.Config;
import com.loror.mvvm.core.MvvmModel;
import com.loror.mvvmbase.net.ApiServiceUtil;
import com.loror.mvvmbase.net.ServiceApi;

public class BaseModel extends MvvmModel {

    @Config
    public static ServiceApi configServiceApi() {
        return ApiServiceUtil.INSTANCE.getServiceApi();
    }
}
