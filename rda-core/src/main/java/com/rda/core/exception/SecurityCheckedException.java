package com.rda.core.exception;

public class SecurityCheckedException extends BaseCheckedException {
    private static final long serialVersionUID = 7589423398813215488L;

    public SecurityCheckedException(String code, String defaultMessage, Throwable cause, Object[] args) {
        super(code, defaultMessage, cause, args);
    }

    public SecurityCheckedException(String code, String defaultMessage, Object[] args) {
        super(code, defaultMessage, args);
    }

    public SecurityCheckedException(String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
    }

    public SecurityCheckedException(String defaultMessage) {
        super(defaultMessage);
    }
}

