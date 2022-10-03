package com.oslash.googledrivescrapperplugin.exception;

public class InvalidScopeAcceptedException extends ApplicationException {

    public InvalidScopeAcceptedException(String message) {
        super(message);
    }

    public InvalidScopeAcceptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidScopeAcceptedException(Throwable cause) {
        super(cause);
    }
}
