package com.loror.mvvm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许解包
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AllowExact {

    /**
     * 是否使用getter方法解包
     */
    boolean byGetter() default false;

    /**
     * 脚本
     * */
    String script() default "";
}
