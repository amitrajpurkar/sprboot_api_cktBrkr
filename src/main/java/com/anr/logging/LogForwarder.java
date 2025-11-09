package com.anr.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.anr.config.ConfigProperties;
import com.anr.logging.model.SplunkEvent;
import com.anr.logging.model.SplunkLogRecord;
import com.google.gson.Gson;

@Component
public class LogForwarder {
    private HttpHeaders headers;
    private static final Logger logger = LoggerFactory.getLogger(LogForwarder.class);
    private static final String ERROR_TOKEN = "SplunkForwarder";
    private static final String INFO_MSG = "Failed to log this message to splunk";
    private static final String DATE_FORMAT_WITH_MS = "yyyy-MM-dd HH:mm:ss.SSS";

    @Autowired
    private SplunkLogRecord logRecord;

    // circular dependencies are not allowed
    //@Autowired
    //private SBUtil sbutil;
    @Autowired
    private ConfigProperties appProps;
    @Autowired
    private Gson gson;

    private void sendRecord(SplunkLogRecord logRecord) {
        // try to post log to Splunk or a log-capturing system /ELK-stack

        // else write to console

    }

    private void logToConsole(SplunkLogRecord logRecord) {
        String currTS = new SimpleDateFormat(DATE_FORMAT_WITH_MS).format(Calendar.getInstance().getTime());
        logRecord.setTimestamp(currTS);
        String logMsgString = gson.toJson(logRecord);
        if (StringUtils.isNotBlank(logMsgString)) {
            logger.info(logMsgString);
        }
    }

    private HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        return headers;
    }

    private void log(SplunkLogRecord logRecord) {
        sendRecord(logRecord);
    }

    @Async("SBThreadPool")
    public void logEvent(SplunkEvent event) {
        event.setPodName(null);
        logRecord.setData(event);
        log(logRecord);
    }
}
