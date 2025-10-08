package com.anr.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anr.common.SBUtil;

import net.jodah.failsafe.CircuitBreaker;

/**
 * circuit breaker example for Jodah Failsafe library
 *
 * https://github.com/jhalterman/failsafe
 * http://jodah.net/failsafe/javadoc/index.html?net/jodah/failsafe/CircuitBreaker.html
 *
 * @author amitr
 *
 */
@Configuration
public class CircuitBreakerFailsafeConfig {
    @Autowired
    private ConfigProperties appProps;
    @Autowired
    private SBUtil sbutil;

    private final String msgForClosed = "circuit is closed";
    private final String msgForOpened = "circuit is opened";
    private final String msgForHalfClosed = "circuit is half-closed";

    private CircuitBreaker buildCktBrkr() {
        return new CircuitBreaker().onClose(() -> sbutil.logInfo(null, msgForClosed))
                .onOpen(() -> sbutil.logInfo(null, msgForOpened))
                .onHalfOpen(() -> sbutil.logInfo(null, msgForHalfClosed));
    }

    private CircuitBreaker buildCktBrkr_publishedConfig() {
        return new CircuitBreaker()
                // if 7 out of 10 requests fail, oepn the circuit
                .withFailureThreshold(3, 10)
                // when half-open, if 3 out of 5 requests
                .withSuccessThreshold(3, 5)
                // delay this long before half opening the circuit
                .withDelay(1, TimeUnit.SECONDS).onClose(() -> sbutil.logInfo(null, msgForClosed))
                .onOpen(() -> sbutil.logInfo(null, msgForOpened))
                .onHalfOpen(() -> sbutil.logInfo(null, msgForHalfClosed));
    }

    @Bean(name = "CBFSDefaultApi")
    public CircuitBreaker cktBrkrFSDefaultApi() {
        int timeoutMS = appProps.getWaitperiod().getApiDefaultService();
        return buildCktBrkr().withTimeout(timeoutMS, TimeUnit.MILLISECONDS);
    }

}
