package com.loror.mvvm.core;

import android.app.Application;

import com.loror.lororUtil.http.api.ApiClient;
import com.loror.lororUtil.http.api.JsonParser;
import com.loror.lororUtil.http.api.MockData;
import com.loror.lororUtil.view.ViewUtil;
import com.loror.mvvm.utls.ActivityUtil;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.SharedPreferenceUtil;

/**
 * 可配置Application
 */
public abstract class ConfigApplication extends Application implements JsonParser {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigUtil.config(this);
        SharedPreferenceUtil.init(this);
        initLororUtil();
        initActivity();
    }

    /**
     * 获取id的class
     */
    protected Class<?> getIdClass() {
        return null;
    }

    /**
     * 获取半屏像素
     */
    protected int getHalfPixel() {
        return 0;
    }

    /**
     * 配置LororUtil
     */
    protected void initLororUtil() {
        ApiClient.setJsonParser(this);
        MockData.init(this);
        ViewUtil.setGlobalIdClass(getIdClass());
        ViewUtil.setSuiteHump(true);
        ViewUtil.setHumpPriority(1);
    }

    /**
     * 界面相关配置
     */
    protected void initActivity() {
        ActivityUtil.register(this);
        ActivityUtil.setDpHalf(getHalfPixel());
        ActivityUtil.setCustomDensity(this);
    }

}
