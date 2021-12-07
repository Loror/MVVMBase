package com.loror.mvvm.core;

import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.SignUtil;

public class MvvmModel {

    {
        ConfigUtil.collect(this);
        SignUtil.signApi(this);
    }
}
