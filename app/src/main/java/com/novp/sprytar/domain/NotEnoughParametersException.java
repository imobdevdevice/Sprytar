package com.novp.sprytar.domain;

@SuppressWarnings("unused")
public class NotEnoughParametersException extends Throwable {

    public NotEnoughParametersException() {
        super("Not enough parameters.");
    }

    public NotEnoughParametersException(String message) {
        super(message);
    }
}
