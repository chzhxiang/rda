package com.rda.core.exception;

public class FastCheckedException extends BaseCheckedException {
    private static final long serialVersionUID = 599320371775054155L;

    public FastCheckedException(String msg) {
        super(msg);
    }

    public FastCheckedException(String code, String msg) {
        super(code, msg, new Object[0]);
    }

    public FastCheckedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FastCheckedException(String code, String msg, Throwable cause) {
        super(code, msg, cause, new Object[0]);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}

