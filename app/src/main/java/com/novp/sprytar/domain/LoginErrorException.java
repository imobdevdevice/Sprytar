package com.novp.sprytar.domain;

@SuppressWarnings("unused")
public class LoginErrorException extends Throwable {

    public LoginErrorException() {
        super("Login error");
    }

    public LoginErrorException(String message) {
        super(message);
    }
}
