package com.anr.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ErrorRootElement {
    private String errorCode;
    private String message;
    private String techMessage;

    @Override
    public String toString() {
        return "ErrorRootElement [errorCode=" + errorCode + ", message=" + message + "]";
    }

    public ErrorRootElement() {
        // TODO Auto-generated constructor stub
    }

    public ErrorRootElement(String errorCode, String message) {
        setErrorCode(errorCode);
        setMessage(message);
    }

    public ErrorRootElement(String errorCode, String message, String techMsg) {
        setErrorCode(errorCode);
        setMessage(message);
        setTechMessage(techMsg);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTechMessage() {
        return techMessage;
    }

    public void setTechMessage(String techMessage) {
        this.techMessage = techMessage;
    }
}
