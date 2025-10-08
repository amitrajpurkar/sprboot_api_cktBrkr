package com.anr.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
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
import com.mongodb.MongoException;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoSocketReadTimeoutException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;

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

    public ErrorRootElement parseHystrixException(String transactionID, Throwable e, String callingMethodName) {
        String errMsgString = null;
        ErrorRootElement err = new ErrorRootElement("ERR-000",
                getRootCauseMessage(e) + String.format(ERR_MSG_SUFFIX, callingMethodName));
        SBNestedException ne = new SBNestedException("caught in hystrix fallback", e);
        if (ne.contains(MongoException.class) || ne.contains(MongoSecurityException.class)
                || ne.contains(MongoSocketReadTimeoutException.class)) {
            err.setErrorCode("ERR-MONGO");
        } else if (ne.contains(RestClientException.class)) {
            err.setErrorCode("ERR-RESTBACKEND");
        }

        if (e instanceof HystrixRuntimeException) {
            switch (((HystrixRuntimeException) e).getFailureType()) {
            case SHORTCIRCUIT:
            case COMMAND_EXCEPTION:
            case BAD_REQUEST_EXCEPTION:
                errMsgString = "Hystrix runtime failure " + String.format(ERR_MSG_SUFFIX, callingMethodName);
                logStackTrace(transactionID, errMsgString, e);
                break;
            case TIMEOUT:
                errMsgString = "Hystrix timeout " + String.format(ERR_MSG_SUFFIX, callingMethodName);
                logStackTrace(transactionID, errMsgString, e);
                break;
            case REJECTED_THREAD_EXECUTION:
                errMsgString = "Hystrix rejects " + String.format(ERR_MSG_SUFFIX, callingMethodName);
                logStackTrace(transactionID, errMsgString, e);
                break;
            default:
                errMsgString = "Hystrix something else " + String.format(ERR_MSG_SUFFIX, callingMethodName);
                logStackTrace(transactionID, errMsgString, e);
                break;
            }
        } else if (e instanceof HystrixTimeoutException) {
            errMsgString = "Hystrix timeout " + String.format(ERR_MSG_SUFFIX, callingMethodName);
            logStackTrace(transactionID, errMsgString, e);
        } else {
            errMsgString = "Not a hystrix failure " + String.format(ERR_MSG_SUFFIX, callingMethodName);
            logStackTrace(transactionID, errMsgString, e);
        }

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
