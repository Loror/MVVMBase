package com.loror.mvvmbase.bean;

import com.loror.mvvm.annotation.ExactOf;

public class DispatchTest {

    private String code;
    @ExactOf
    private String message;

    public DispatchTest(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
