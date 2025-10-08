package com.anr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anr.common.SBUtil;
import com.anr.exception.ErrorRootElement;
import com.anr.model.SBResponseModel;

@Component
public class ControllerFailureResponses {
    @Autowired
    private SBUtil sbutil;

    public SBResponseModel getSampleFailureResponse(String transactionID, String sourceChannel, String locale,
            String field1, String field2, Throwable e) {
        ErrorRootElement err = new ErrorRootElement("ERR-002", "Failure Response");
        err.setTechMessage("Either exception happened or timeout happened... " + sbutil.getRootCauseMessage(e));

        SBResponseModel resp = new SBResponseModel();
        resp.setErr(err);

        return resp;
    }

}
