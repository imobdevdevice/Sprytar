package com.novp.sprytar.network;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SpLoginResult {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    @SerializedName("status")
    private String status;

    @SerializedName("result")
    private String token;

    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @StringDef({SUCCESS, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

}
