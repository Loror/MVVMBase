package com.loror.mvvm.bean;

import com.loror.mvvm.annotation.AllowExact;
import com.loror.mvvm.annotation.ExactOf;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 框架使用
 */
public class MethodInfo {

    private final Method method;
    private final boolean allowExact;
    private final boolean byGetter;

    private Object exactBy;

    public MethodInfo(Method method) {
        this.method = method;
        AllowExact allowExact = method.getAnnotation(AllowExact.class);
        if (allowExact != null) {
            this.allowExact = true;
            this.byGetter = allowExact.byGetter();
        } else {
            this.allowExact = false;
            this.byGetter = false;
        }
    }

    public Method getMethod() {
        return method;
    }

    public boolean isAllowExact() {
        return allowExact;
    }

    public boolean isByGetter() {
        return byGetter;
    }

    public String getName() {
        return method.getName();
    }

    /**
     * 解包
     */
    public Object exact(Object data, Class<?> type) {
        if (allowExact && data != null) {

            if (ReflectionUtil.isPrimitive(data.getClass())) {
                return ReflectionUtil.convertValue(data, type, null);
            }

            if (data.getClass() == String.class) {
                return ReflectionUtil.convertValue(data, type, null);
            }

            boolean needCheckExact = true;
            if (exactBy != null) {
                if (exactBy.getClass() == Method.class) {
                    Method method = (Method) exactBy;
                    method.setAccessible(true);
                    try {
                        return method.invoke(data);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ConfigUtil.handlerException(e);
                    }
                } else if (exactBy.getClass() == Field.class) {
                    Field field = (Field) exactBy;
                    field.setAccessible(true);
                    try {
                        return field.get(data);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    needCheckExact = false;
                }
            }
            if (!type.isAssignableFrom(data.getClass())) {
                if (byGetter) {
                    Method[] methods = ReflectionUtil.findAllGetterMethod(data.getClass());
                    if (needCheckExact) {
                        for (Method method : methods) {
                            if (method.getAnnotation(ExactOf.class) != null) {
                                exactBy = method;
                                return exact(data, type);
                            }
                        }
                        exactBy = Void.class;
                    }
                    for (Method method : methods) {
                        if (type.isAssignableFrom(method.getReturnType())) {
                            method.setAccessible(true);
                            try {
                                return method.invoke(data);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                ConfigUtil.handlerException(e);
                            }
                        }
                    }
                } else {
                    Field[] fields = data.getClass().getDeclaredFields();
                    if (needCheckExact) {
                        for (Field field : fields) {
                            if (field.getAnnotation(ExactOf.class) != null) {
                                exactBy = field;
                                return exact(data, type);
                            }
                        }
                        exactBy = Void.class;
                    }
                    for (Field field : fields) {
                        if (type.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            try {
                                return field.get(data);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return data;
    }
}
