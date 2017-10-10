package com.sprytar.android.network;


import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
public class SpResult<T> {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private String status;
    @SerializedName("result")
    private T data;

    private String message;

    private int code;

    public void setError() {
        status = ERROR;
    }

    public boolean isSuccess() {
        return status.equalsIgnoreCase("success");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @StringDef({SUCCESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }
}
