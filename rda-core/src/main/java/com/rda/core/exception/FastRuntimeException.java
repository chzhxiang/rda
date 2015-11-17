package com.rda.core.exception;

public class FastRuntimeException extends BaseRuntimeException {
    private static final long serialVersionUID = -4954118251735823026L;

    public FastRuntimeException(String msg) {
        super(msg);
    }

    public FastRuntimeException(String code, String msg) {
        super(code, msg, new Object[0]);
    }

    public FastRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FastRuntimeException(String code, String msg, Throwable cause) {
        super(code, msg, cause, new Object[0]);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}

