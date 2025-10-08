package com.anr.logging.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.anr.common.SBUtil.TransactionType;

public class SplunkEvent {
    String errorMsg;
    String api;// which service call is this
    String sourceChannel;// which is the calling system
    String podName;
    String tid;// unique transaction id
    long responseTimeInMillis;
    TransactionType transactionType;// Success, Failure, InProcess

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    String environment;// localhost, unit, stage, prod
    String errorCode;
    List<String> warningCodes;
    String requestUrl;
    String requestedTimestamp;
    String processStartTimestamp;
    String processEndTimestamp;

    public void setReponseTimeInMillis(long respTimeMS) {
        responseTimeInMillis = respTimeMS;
    }

    public void setProcessEndTs(String processEndTs) {
        processEndTimestamp = processEndTs;
    }

    public void setProcesStartTs(String processStartTs) {
        processStartTimestamp = processStartTs;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    protected SplunkEvent(SplunkEventBuilder b) {
        errorMsg = b.errorMsg;
        errorCode = b.errorCode;
        api = b.api;
        environment = b.environment;
        sourceChannel = b.sourceChannel;
        podName = b.podName;
        tid = b.tid;
        responseTimeInMillis = b.responseTimeInMillis;
        transactionType = b.transactionType;
        warningCodes = b.warningCodes;
        requestUrl = b.requestUrl;
        requestedTimestamp = b.requestedTimestamp;
        processStartTimestamp = b.processStartTimestamp;
        processEndTimestamp = b.processEndTimestamp;
    }

    public static class SplunkEventBuilder {
        String errorMsg;
        String api;// which service call is this
        String sourceChannel;// which is the calling system
        String podName;
        String tid;// unique transaction id
        long responseTimeInMillis;
        TransactionType transactionType;// Success, Failure, InProcess
        String environment;// localhost, unit, stage, prod
        String errorCode;
        List<String> warningCodes;
        String requestUrl;
        String requestedTimestamp;
        String processStartTimestamp;
        String processEndTimestamp;

        public SplunkEventBuilder(String api, String env, String source, String tid) {
            this.api = api;
            environment = env;
            sourceChannel = source;
            this.tid = tid;
        }

        public SplunkEventBuilder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public SplunkEventBuilder addErrorMsg(String newError) {
            if (StringUtils.isNotBlank(errorMsg) && StringUtils.isNotBlank(newError)) {
                errorMsg = errorMsg.concat(" ").concat(newError);
            } else if (StringUtils.isBlank(errorMsg)) {
                errorMsg = newError;
            }
            return this;
        }

        public SplunkEventBuilder warningCodes(List<String> warningCodes) {
            this.warningCodes = warningCodes;
            return this;
        }

        public SplunkEventBuilder addOneWarningCode(String warningCode) {
            if (warningCodes != null && !warningCodes.isEmpty()) {
                warningCodes.add(warningCode);
            } else {
                warningCodes = new ArrayList<String>();
                warningCodes.add(warningCode);
            }
            return this;
        }

        public SplunkEventBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public SplunkEventBuilder responseTimeInMillis(long responseTimeInMillis) {
            this.responseTimeInMillis = responseTimeInMillis;
            return this;
        }

        public SplunkEventBuilder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public SplunkEventBuilder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public SplunkEventBuilder requestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
            return this;
        }

        public SplunkEventBuilder requestedTs(String requestedTimestamp) {
            this.requestedTimestamp = requestedTimestamp;
            return this;
        }

        public SplunkEventBuilder processStartTs(String processStartTimestamp) {
            this.processStartTimestamp = processStartTimestamp;
            return this;
        }

        public SplunkEventBuilder processEndTs(String processEndTimestamp) {
            this.processEndTimestamp = processEndTimestamp;
            return this;
        }

        public SplunkEvent build() {
            return new SplunkEvent(this);
        }

    }// end of builder class
}
