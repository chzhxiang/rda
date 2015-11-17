package com.rda.core.exception;

public class SecurityRuntimeException extends BaseRuntimeException {
    private static final long serialVersionUID = -1001968495110168387L;

    public SecurityRuntimeException(String code, String defaultMessage, Throwable cause, Object[] args) {
        super(code, defaultMessage, cause, args);
    }

    public SecurityRuntimeException(String code, String defaultMessage, Object[] args) {
        super(code, defaultMessage, args);
    }

    public SecurityRuntimeException(String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
    }

    public SecurityRuntimeException(String defaultMessage) {
        super(defaultMessage);
    }
}

