package com.sprytar.android.domain;

@SuppressWarnings("unused")
public class LoginErrorException extends Throwable {

    public LoginErrorException() {
        super("Login error");
    }

    public LoginErrorException(String message) {
        super(message);
    }
}
