package com.anr.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.anr.config.ConfigProperties;
import com.anr.exception.ErrorRootElement;
import com.anr.exception.SBNestedException;
import com.anr.logging.LogForwarder;
import com.anr.logging.model.SplunkEvent;
import com.google.gson.Gson;
// MongoDB exceptions removed - migrated to H2 database
// import com.mongodb.MongoException;
// import com.mongodb.MongoSecurityException;
// import com.mongodb.MongoSocketReadTimeoutException;
// Removed: Hystrix not compatible with Spring Boot 3.x
// import com.netflix.hystrix.exception.HystrixRuntimeException;
// import com.netflix.hystrix.exception.HystrixTimeoutException;

@Component
public class SBUtil {
    @Autowired
    private ConfigProperties appProps;

    @Autowired
    private LogForwarder logforwarder;

    @Autowired
    private Gson gson;

    private static final Logger logger = LoggerFactory.getLogger(SBUtil.class);
    private static final String LOG_MSG_FORMAT = "[transmissionID = %s] %s (ts=%s)%n";
    private static final String DATE_FORMAT_WITH_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String SPACE = " ";
    private static final String ERR_MSG_SUFFIX = " at method: %s";

    public void logDebug(String transactionID, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(LOG_MSG_FORMAT,
                    StringUtils.isEmpty(transactionID) ? StringUtils.EMPTY : transactionID, message,
                    new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date())));
        }
    }

    public void logInfo(String transactionID, String message) {
        if (logger.isInfoEnabled()) {
            logger.debug(String.format(LOG_MSG_FORMAT,
                    StringUtils.isEmpty(transactionID) ? StringUtils.EMPTY : transactionID, message,
                    new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date())));
        }
    }

    public void logError(String transactionID, String message) {
        if (logger.isErrorEnabled()) {
            logger.debug(String.format(LOG_MSG_FORMAT,
                    StringUtils.isEmpty(transactionID) ? StringUtils.EMPTY : transactionID, message,
                    new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date())));
        }
    }

    public void logTrace(String transactionID, String message) {
        if (logger.isTraceEnabled()) {
            logger.debug(String.format(LOG_MSG_FORMAT,
                    StringUtils.isEmpty(transactionID) ? StringUtils.EMPTY : transactionID, message,
                    new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date())));
        }
    }

    public void logStackTrace(String transactionID, String message, Throwable e) {
        String txnID = StringUtils.isEmpty(transactionID) ? StringUtils.EMPTY : transactionID;
        String dtNow = new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date());
        Boolean isStackTraceLoggingEnabled = appProps.getLogStackTrace();

        if (logger.isErrorEnabled() && isStackTraceLoggingEnabled) {
            logger.error(LOG_MSG_FORMAT, txnID, message, dtNow);
            logger.error(LOG_MSG_FORMAT, txnID, ExceptionUtils.getRootCause(e), dtNow);
            ExceptionUtils.printRootCauseStackTrace(e);
        }
    }

    public String getRootCauseMessage(Throwable e) {
        String rootCauseMsg = null;
        Throwable rootExcept = NestedExceptionUtils.getMostSpecificCause(e);
        rootCauseMsg = ExceptionUtils.getRootCauseMessage(e);
        rootCauseMsg = StringUtils.isBlank(rootCauseMsg) ? rootExcept.getMessage() : rootCauseMsg;
        if (rootExcept.getStackTrace().length > 0) {
            rootCauseMsg += SPACE + rootExcept.getStackTrace()[0].toString();
        }

        return rootCauseMsg;
    }

    /**
     * Parse exception - Generic exception handling
     * MongoDB-specific handling removed (migrated to H2)
     */
    public ErrorRootElement parseException(String transactionID, Throwable e, String callingMethodName) {
        String errMsgString = null;
        ErrorRootElement err = new ErrorRootElement("ERR-000",
                getRootCauseMessage(e) + String.format(ERR_MSG_SUFFIX, callingMethodName));
        SBNestedException ne = new SBNestedException("caught in fallback", e);
        
        // MongoDB exception handling removed - now using H2 database
        if (ne.contains(RestClientException.class)) {
            err.setErrorCode("ERR-RESTBACKEND");
        }

        // Generic exception handling
        errMsgString = "Exception occurred " + String.format(ERR_MSG_SUFFIX, callingMethodName);
        logStackTrace(transactionID, errMsgString, e);

        return err;
    }

    public void logToSplunkOrSimilar(SplunkEvent event, long startTimeMS) {
        long responseTimeMS = 0;
        if (TransactionType.Request.equals(event.getTransactionType())) {
            event.setProcesStartTs(new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date(startTimeMS)));
        } else if (TransactionType.Response.equals(event.getTransactionType())
                || TransactionType.Failure.equals(event.getTransactionType())) {
            responseTimeMS = System.currentTimeMillis() - startTimeMS;
            event.setReponseTimeInMillis(responseTimeMS);
            event.setProcessEndTs(new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date()));
            event.setProcesStartTs(new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(new Date(startTimeMS)));
        }
        logforwarder.logEvent(event);
    }

    public enum TransactionType {
        Request, Response, Failure, InProcess;
    }
}
