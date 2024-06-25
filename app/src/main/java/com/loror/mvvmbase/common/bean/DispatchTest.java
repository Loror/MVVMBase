package com.loror.mvvmbase.common.bean;

import com.loror.mvvm.annotation.ExactOf;

public class DispatchTest<T> {

    private String code;
    @ExactOf
    private T message;

    public DispatchTest(String code, T message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
