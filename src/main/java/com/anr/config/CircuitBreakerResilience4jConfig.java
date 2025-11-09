package com.anr.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.anr.common.SBUtil;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * Resilience4j Circuit Breaker Configuration
 * 
 * Modern replacement for Netflix Hystrix, compatible with Spring Boot 3.x
 * 
 * Documentation:
 * - https://resilience4j.readme.io/docs/circuitbreaker
 * - https://resilience4j.readme.io/docs/getting-started-3
 * 
 * Key Features:
 * - Circuit Breaker: Prevents cascading failures
 * - Time Limiter: Timeout management
 * - Bulkhead: Limits concurrent executions
 * - Retry: Automatic retry logic
 * - Rate Limiter: Request throttling
 * 
 * Migration from Hystrix:
 * - Hystrix Thread Pool → Resilience4j Bulkhead
 * - Hystrix Timeout → Resilience4j TimeLimiter
 * - Hystrix Circuit Breaker → Resilience4j CircuitBreaker
 * 
 * @author amitr
 */
@Configuration
public class CircuitBreakerResilience4jConfig {

    @Autowired
    private ConfigProperties appProps;
    
    @Autowired
    private SBUtil sbutil;

    private final String msgForClosed = "Circuit breaker transitioned to CLOSED state";
    private final String msgForOpened = "Circuit breaker transitioned to OPEN state";
    private final String msgForHalfOpen = "Circuit breaker transitioned to HALF_OPEN state";

    /**
     * Default Circuit Breaker configuration based on Hystrix settings
     * 
     * Hystrix → Resilience4j mapping:
     * - circuitBreakerSleepWindowInMilliseconds → waitDurationInOpenState
     * - circuitBreakerRequestVolumeThreshold → minimumNumberOfCalls
     * - circuitBreakerErrorThresholdPercentage → failureRateThreshold
     */
    private CircuitBreakerConfig buildDefaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // Wait duration before transitioning from OPEN to HALF_OPEN
                // Hystrix: circuitBreakerSleepWindowInMilliseconds
                .waitDurationInOpenState(Duration.ofMillis(
                        appProps.getWaitperiod().getHyxCbSleepWindowMS()))
                
                // Minimum number of calls before calculating error rate
                // Hystrix: circuitBreakerRequestVolumeThreshold
                .minimumNumberOfCalls(
                        appProps.getWaitperiod().getHyxCbReqVolmThreshold())
                
                // Failure rate threshold percentage to open circuit
                // Hystrix: circuitBreakerErrorThresholdPercentage
                .failureRateThreshold(
                        appProps.getWaitperiod().getHyxCbErrThresholdPercentage())
                
                // Size of the sliding window for recording outcomes
                .slidingWindowSize(100)
                
                // Type of sliding window: COUNT_BASED or TIME_BASED
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                
                // Number of permitted calls when circuit is HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(10)
                
                // Automatically transition from OPEN to HALF_OPEN
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                
                // Record these exceptions as failures
                .recordExceptions(Exception.class)
                
                // Build the configuration
                .build();
    }

    /**
     * Circuit Breaker Registry with default configuration
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = buildDefaultCircuitBreakerConfig();
        return CircuitBreakerRegistry.of(config);
    }

    /**
     * Default API Circuit Breaker
     * 
     * Replaces Hystrix @HystrixCommand annotation
     * 
     * Usage:
     * <pre>
     * {@code
     * @Autowired
     * private CircuitBreaker defaultApiCircuitBreaker;
     * 
     * public String callExternalService() {
     *     return circuitBreaker.executeSupplier(() -> {
     *         // Your service call here
     *         return externalService.call();
     *     });
     * }
     * }
     * </pre>
     */
    @Bean(name = "defaultApiCircuitBreaker")
    public CircuitBreaker defaultApiCircuitBreaker(CircuitBreakerRegistry registry) {
        String circuitBreakerName = appProps.getWaitperiod().getApiGroupKey() + "-" + 
                                    appProps.getWaitperiod().getApiDefServiceKey();
        
        CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
        
        // Register event listeners for state transitions
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    switch (event.getStateTransition().getToState()) {
                        case CLOSED:
                            sbutil.logInfo(null, msgForClosed + ": " + circuitBreakerName);
                            break;
                        case OPEN:
                            sbutil.logInfo(null, msgForOpened + ": " + circuitBreakerName);
                            break;
                        case HALF_OPEN:
                            sbutil.logInfo(null, msgForHalfOpen + ": " + circuitBreakerName);
                            break;
                        default:
                            break;
                    }
                })
                .onError(event -> {
                    sbutil.logError(null, "Circuit breaker error: " + event.getThrowable().getMessage());
                })
                .onSuccess(event -> {
                    sbutil.logDebug(null, "Circuit breaker call succeeded");
                });
        
        return circuitBreaker;
    }

    /**
     * Time Limiter for timeout management
     * 
     * Replaces Hystrix execution timeout
     * 
     * Hystrix: executionTimeoutInMilliseconds
     * Resilience4j: timeoutDuration
     */
    @Bean(name = "defaultApiTimeLimiter")
    public TimeLimiter defaultApiTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(
                        appProps.getWaitperiod().getApiDefaultService()))
                .cancelRunningFuture(true)
                .build();
        
        return TimeLimiter.of("defaultApiTimeLimiter", config);
    }

    /**
     * Test Method Circuit Breaker (for testing purposes)
     * 
     * Shorter timeout for testing scenarios
     */
    @Bean(name = "testMethodCircuitBreaker")
    public CircuitBreaker testMethodCircuitBreaker(CircuitBreakerRegistry registry) {
        // Custom config for test methods with shorter timeout
        CircuitBreakerConfig testConfig = CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofMillis(500))
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .slidingWindowSize(10)
                .build();
        
        return registry.circuitBreaker("testMethod", testConfig);
    }

    /**
     * Test Method Time Limiter
     */
    @Bean(name = "testMethodTimeLimiter")
    public TimeLimiter testMethodTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(200))
                .cancelRunningFuture(true)
                .build();
        
        return TimeLimiter.of("testMethodTimeLimiter", config);
    }
}
