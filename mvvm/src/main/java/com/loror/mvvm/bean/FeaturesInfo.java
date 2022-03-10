package com.loror.mvvm.bean;

public interface FeaturesInfo {

    /**
     * 是否支持创建对象
     */
    boolean canCreate(Class<?> type);

    /**
     * 创建对象
     */
    Object create(Class<?> type);
}
