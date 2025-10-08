package com.anr.config;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;

public class TraceInterceptor extends CustomizableTraceInterceptor {
    private static final long serialVersionUID = 1212234L;
    protected static Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);

    @Override
    protected void writeToLog(Log logger, String message, Throwable ex) {

    }

    @Override
    protected void writeToLog(Log logger, String message) {
        writeToLog(logger, message, null);
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return true;
    }

}
