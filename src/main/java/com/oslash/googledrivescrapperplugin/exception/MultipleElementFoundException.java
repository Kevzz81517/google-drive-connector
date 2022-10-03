package com.oslash.googledrivescrapperplugin.exception;

public class MultipleElementFoundException extends ApplicationException {

    public MultipleElementFoundException(String message) {
        super(message);
    }

    public MultipleElementFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleElementFoundException(Throwable cause) {
        super(cause);
    }
}
