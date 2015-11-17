package com.rda.core.exception;

import com.rda.core.exception.util.ExceptionUtils;
import org.springframework.core.NestedCheckedException;

import java.util.Date;

public class BaseCheckedException extends NestedCheckedException
        implements BaseException {
    private static final long serialVersionUID = 6094639580226398138L;
    private String code;
    private Date time;
    private String[] args;
    private String className;
    private String methodName;
    private String[] parameters;
    private boolean handled;
    private String i18nMessage;

    public BaseCheckedException(String code, String defaultMessage, Object[] args) {
        super(defaultMessage);
        this.code = code;
        this.args = ExceptionUtils.convertArgsToString(args);
    }

    public BaseCheckedException(String code, String defaultMessage, Throwable cause, Object[] args) {
        super(defaultMessage, cause);
        this.code = code;
        this.args = ExceptionUtils.convertArgsToString(args);
    }

    public BaseCheckedException(String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
    }

    public BaseCheckedException(String defaultMessage) {
        super(defaultMessage);
    }

    public String getCode() {
        return this.code;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getParameters() {
        return this.parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public boolean isHandled() {
        return this.handled;
    }

    public void setI18nMessage(String i18nMessage) {
        this.i18nMessage = i18nMessage;
    }

    public String getI18nMessage() {
        return this.i18nMessage;
    }

    public String[] getArgs() {
        return this.args;
    }
}

