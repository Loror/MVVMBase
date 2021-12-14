package com.loror.mvvm.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class SignInfo {

    private final Type type;
    private final String fieldName;
    private final Annotation[] annotations;

    public SignInfo(Type type, Annotation[] annotations, String fieldName) {
        this.type = type;
        this.annotations = annotations;
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Annotation getAnnotation(Class<? extends Annotation> type) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.getClass() == type) {
                    return annotation;
                }
            }
        }
        return null;
    }
}
