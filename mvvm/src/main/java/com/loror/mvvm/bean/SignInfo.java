package com.loror.mvvm.bean;

import java.lang.reflect.Type;

public class SignInfo {

    private final Type type;
    private final String fieldName;

    public SignInfo(Type type, String fieldName) {
        this.type = type;
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }
}
