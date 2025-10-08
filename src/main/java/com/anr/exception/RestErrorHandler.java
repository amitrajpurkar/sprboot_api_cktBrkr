package com.anr.exception;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RestErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        logger.info("from rest err handler.. status-text: {}, status-code: {}, response-body: {}",
                response.getStatusText(), response.getStatusCode(), getResponseBody(response));
    }

}
