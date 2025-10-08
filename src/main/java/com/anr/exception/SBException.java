/*
 * Copyright (c) DB Schenker Australia Pty Ltd
 *
 * All rights reserved.
 */
package com.anr.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author amitr
 *
 */
public class SBException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private List<String> errMsgs = new ArrayList<String>();

    public SBException() {
        super();
    }

    public SBException(String message) {
        super(message);
    }

    public SBException(Throwable ex) {
        super(ex);
    }

    public SBException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public void addErrorMsgs(List<String> errorMessages) {
        for (String errMsg : errorMessages) {
            addErrMsg(errMsg);
        }
    }

    public boolean hasErrors() {
        return !errMsgs.isEmpty();
    }

    public void addErrMsg(String errMsg) {
        errMsgs.add(errMsg);
    }
}
