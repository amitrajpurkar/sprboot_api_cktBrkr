package com.anr.logging.model;

public class SplunkLogRecord {
    private String reportType;
    private String hostname;
    private String component;
    private SplunkEvent data;
    private String forward_msg_to_splunk;
    private String reporter;
    private String env;
    private String timestamp;
    private String host;
    private String source;
    private String sourceType;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getForward_msg_to_splunk() {
        return forward_msg_to_splunk;
    }

    public void setForward_msg_to_splunk(String forward_msg_to_splunk) {
        this.forward_msg_to_splunk = forward_msg_to_splunk;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public SplunkEvent getData() {
        return data;
    }

    public void setData(SplunkEvent data) {
        this.data = data;
    }
}
