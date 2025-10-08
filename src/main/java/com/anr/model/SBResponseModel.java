package com.anr.model;

import com.anr.exception.ErrorRootElement;

public class SBResponseModel {

    private String field1;
    private String field2;
    private ErrorRootElement err;

    public SBResponseModel() {
        setErr(new ErrorRootElement("ERR-001", "Default Empty Response"));
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public ErrorRootElement getErr() {
        return err;
    }

    public void setErr(ErrorRootElement err) {
        this.err = err;
    }

}
