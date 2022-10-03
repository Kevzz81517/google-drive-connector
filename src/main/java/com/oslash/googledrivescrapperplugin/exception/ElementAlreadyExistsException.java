package com.oslash.googledrivescrapperplugin.exception;

public class ElementAlreadyExistsException extends ApplicationException {

    public ElementAlreadyExistsException(String message) {
        super(message);
    }

    public ElementAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
