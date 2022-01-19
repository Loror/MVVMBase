package com.loror.mvvm.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class SignInfo {

    private final Object source;
    private final Type type;
    private final String fieldName;
    private final Field field;
    private final Method method;

    public SignInfo(Object source, Field field) {
        this.source = source;
        this.type = field.getGenericType();
        this.fieldName = field.getName();
        this.field = field;
        this.method = null;
    }

    public SignInfo(Object source, Method method) {
        //setter
        String name = method.getName();
        name = name.substring(3);
        if (name.length() > 0) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        this.source = source;
        this.type = method.getGenericParameterTypes()[0];
        this.fieldName = name;
        this.field = null;
        this.method = method;
    }

    public Object getSource() {
        return source;
    }

    public Type getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Field getField() {
        return field;
    }

    public Method getMethod() {
        return method;
    }

    public Annotation getAnnotation(Class<? extends Annotation> type) {
        if (field != null) {
            return field.getAnnotation(type);
        }
        if (method != null) {
            return method.getAnnotation(type);
        }
        return null;
    }
}
