package com.anr.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

/**
 * Rate Limiter Aspect
 * 
 * Applies rate limiting to all controller methods to prevent:
 * - DoS attacks
 * - Resource exhaustion
 * - Abusive client behavior
 * 
 * Different rate limits are applied based on HTTP method:
 * - GET (read): 200 requests/second
 * - POST/PUT/DELETE (write): 20 requests/second
 * 
 * When rate limit is exceeded, returns HTTP 429 (Too Many Requests)
 * 
 * @author amitr
 */
@Aspect
@Component
@ConditionalOnProperty(name = "rate.limiting.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final RateLimiter readApiRateLimiter;
    private final RateLimiter writeApiRateLimiter;

    public RateLimiterAspect(RateLimiterRegistry rateLimiterRegistry) {
        this.readApiRateLimiter = rateLimiterRegistry.rateLimiter("readApi");
        this.writeApiRateLimiter = rateLimiterRegistry.rateLimiter("writeApi");
    }

    /**
     * Apply rate limiting to all GET methods (read operations)
     * Higher limit: 200 requests/second
     */
    @Around("(execution(* com.anr.controller.*Controller.get*(..)) || " +
            "execution(* com.anr.controller.*Controller.find*(..))) && " +
            "!within(com.anr..*Test)")
    public Object applyReadRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        
        try {
            return readApiRateLimiter.executeCheckedSupplier(() -> {
                return joinPoint.proceed();
            });
        } catch (RequestNotPermitted e) {
            logger.warn("Read rate limit exceeded for method: {}", methodName);
            return createRateLimitExceededResponse();
        }
    }

    /**
     * Apply rate limiting to all write methods (POST, PUT, DELETE)
     * Stricter limit: 20 requests/second
     */
    @Around("(execution(* com.anr.controller.*Controller.create*(..)) || " +
            "execution(* com.anr.controller.*Controller.update*(..)) || " +
            "execution(* com.anr.controller.*Controller.delete*(..)) || " +
            "execution(* com.anr.controller.*Controller.save*(..))) && " +
            "!within(com.anr..*Test)")
    public Object applyWriteRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        
        try {
            return writeApiRateLimiter.executeCheckedSupplier(() -> {
                return joinPoint.proceed();
            });
        } catch (RequestNotPermitted e) {
            logger.warn("Write rate limit exceeded for method: {}", methodName);
            return createRateLimitExceededResponse();
        }
    }

    /**
     * Creates a standardized response for rate limit exceeded scenarios
     * Returns HTTP 429 (Too Many Requests) with retry information
     */
    private ResponseEntity<?> createRateLimitExceededResponse() {
        String message = "Rate limit exceeded. Please try again in a few seconds.";
        
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-RateLimit-Retry-After", "1")
                .header("Retry-After", "1")
                .body(new RateLimitError(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests",
                    message
                ));
    }

    /**
     * Error response class for rate limit exceeded
     */
    public static class RateLimitError {
        private final int status;
        private final String error;
        private final String message;

        public RateLimitError(int status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
