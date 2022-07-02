package com.illcc.libbase.model;

import java.io.Serializable;

public class BaseModel implements Serializable {
    public static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private Object data;

    public int getCode() {
        return code;
    }

    public BaseModel setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public BaseModel setData(Object data) {
        this.data = data;
        return this;
    }
}
