package com.anr.exception;

import org.springframework.core.NestedRuntimeException;

public class SBNestedException extends NestedRuntimeException {

    private static final long serialVersionUID = 1L;

    public SBNestedException(String msg) {
        super(msg);
    }

    public SBNestedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
