package com.loror.mvvm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置 若为interface且包含value将尝试实例化value、若不包含value将生成api 若为object将尝试实例化
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {

    /**
     * 实现类
     */
    Class<?> value() default Object.class;

    /**
     * 是否进入常量池
     */
    boolean intoPool() default true;
}
