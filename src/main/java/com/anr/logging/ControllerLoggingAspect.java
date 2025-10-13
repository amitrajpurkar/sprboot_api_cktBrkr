package com.anr.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anr.common.SBUtil;
import com.anr.common.SBUtil.TransactionType;
import com.anr.config.ConfigProperties;
import com.anr.controller.ControllerFailureResponses;
import com.anr.logging.model.SplunkEvent.SplunkEventBuilder;
import com.anr.model.SBResponseModel;
import com.google.gson.Gson;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

@Aspect
@Component
public class ControllerLoggingAspect {
    @Autowired
    private ConfigProperties appProps;

    @Autowired
    private SBUtil sbutil;

    @Autowired
    private Gson gson;

    @Autowired
    private ControllerFailureResponses failures;

    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;

    private static final String SPACE = " ";

    @Around("execution(* com.anr.controller.MainSBController.getSampleResponse(..)) "
            + "&&args(transactionID,sourceChannel,locale,field1,field2,..)")
    public SBResponseModel logSampleResponse(ProceedingJoinPoint jointpoint, String transactionID, String sourceChannel,
            String locale, String field1, String field2) {
        long startTime = System.currentTimeMillis();
        // construct a logBuilder event
        SplunkEventBuilder bldr = new SplunkEventBuilder("Default-Api", "localhost", sourceChannel, transactionID);
        bldr.transactionType(TransactionType.Request);
        sbutil.logInfo(transactionID, "start time:" + startTime);

        // Execute with Resilience4j circuit breaker protection
        SBResponseModel response;
        try {
            response = defaultApiCircuitBreaker.executeSupplier(() -> {
                try {
                    return (SBResponseModel) jointpoint.proceed();
                } catch (Throwable t) {
                    // Convert checked exception to unchecked for circuit breaker
                    throw new RuntimeException("Controller execution failed", t);
                }
            });
        } catch (Exception e) {
            // Fallback logic (circuit breaker fallback or execution failure)
            Signature signature = jointpoint.getSignature();
            String methodName = signature.getName();
            
            // Unwrap the original exception if it was wrapped
            Throwable originalException = e.getCause() != null ? e.getCause() : e;
            
            sbutil.logError(transactionID, String.format("Circuit breaker fallback: (method: %s) %s", 
                    methodName, sbutil.getRootCauseMessage(originalException)));
            sbutil.logStackTrace(transactionID, methodName, originalException);

            response = failures.getSampleFailureResponse(transactionID, sourceChannel, locale, field1, field2, originalException);
        }

        String messageString = null;
        if (response == null) {
            messageString = "null response for default-service";
        } else if (response.getErr() != null) {
            StringBuilder errMsg = new StringBuilder();
            errMsg.append(response.getErr().getMessage());
            errMsg.append(SPACE);
            errMsg.append(response.getErr().getTechMessage());
            messageString = "Failure: " + errMsg.toString();
            bldr.transactionType(TransactionType.Failure);
            bldr.addErrorMsg(messageString);
            bldr.errorCode("ERR-002");
        } else {
            messageString = "Success: " + gson.toJson(response);
            bldr.transactionType(TransactionType.Response);
        }

        messageString += "; timetaken = " + (System.currentTimeMillis() - startTime) + " ms";
        sbutil.logInfo(transactionID, messageString);
        sbutil.logToSplunkOrSimilar(bldr.build(), startTime);

        return response;
    }

}
