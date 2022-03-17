package com.loror.mvvm.net;

public class Message {

    private int code;
    private String message;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message == null ? "操作失败" : this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
