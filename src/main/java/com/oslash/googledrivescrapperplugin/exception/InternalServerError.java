package com.oslash.googledrivescrapperplugin.exception;

public class InternalServerError extends ApplicationException {

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerError(Throwable cause) {
        super(cause);
    }
}
