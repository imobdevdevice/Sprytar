package com.sprytar.android.domain;

@SuppressWarnings("unused")
public class NotEnoughParametersException extends Throwable {

    public NotEnoughParametersException() {
        super("Not enough parameters.");
    }

    public NotEnoughParametersException(String message) {
        super(message);
    }
}
