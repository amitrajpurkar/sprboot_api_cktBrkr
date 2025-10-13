package com.anr.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.anr.common.SBUtil;
import com.anr.common.SBUtil.TransactionType;
import com.anr.config.ConfigProperties;
import com.anr.controller.ControllerFailureResponses;
import com.anr.logging.model.SplunkEvent.SplunkEventBuilder;
import com.anr.model.SBResponseModel;
import com.google.gson.Gson;
// Removed: Hystrix not compatible with Spring Boot 3.x
// import com.netflix.hystrix.HystrixCommand;

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

    // Removed: Hystrix configuration - replaced with direct execution
    // @Autowired
    // @Qualifier("CmdConfigDefService")
    // private HystrixCommand.Setter cmdConfigDefaultSvc;

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

        // Direct execution (Hystrix removed for Spring Boot 3.x compatibility)
        // TODO: Replace with Resilience4j for circuit breaker functionality
        SBResponseModel response;
        try {
            response = (SBResponseModel) jointpoint.proceed();
        } catch (Throwable t) {
            // Fallback logic (previously in Hystrix getFallback())
            Signature signature = jointpoint.getSignature();
            String methodName = signature.getName();
            sbutil.logError(transactionID, String.format("Execution exception: (method: %s) %s", methodName,
                    sbutil.getRootCauseMessage(t)));
            sbutil.logStackTrace(transactionID, methodName, t);

            response = failures.getSampleFailureResponse(transactionID, sourceChannel, locale, field1, field2, t);
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
