# Circuit Breaker Upgrade Plan: Hystrix → Resilience4j

**Current:** Netflix Hystrix (deprecated 2018)  
**Target:** Resilience4j 2.2.0  
**Effort:** 1-2 days  
**Risk:** Medium  
**Prerequisites:** Spring Boot 3.2.x completed

---

## Executive Summary

Migration from deprecated **Hystrix** to **Resilience4j** - the modern, actively maintained fault tolerance library recommended by Netflix.

**Why Resilience4j?**
- ✅ Actively maintained
- ✅ Spring Boot 3.x compatible
- ✅ Lightweight, no dependencies
- ✅ Functional programming API
- ✅ Better metrics (Micrometer)
- ✅ More patterns: Circuit Breaker, Retry, Rate Limiter, Bulkhead

**Files to Update:**
1. `build.gradle` - Dependencies
2. `Bootstrap.java` - Remove @EnableCircuitBreaker
3. `CircuitBreakerHystrixConfig.java` - Delete/replace
4. `ControllerLoggingAspect.java` - Rewrite with Resilience4j
5. `application.properties` - New configuration

---

## Phase 0: Preparation (1 hour)

### 0.1 Backup

```bash
git checkout -b backup/before-resilience4j
git commit -am "Backup before Resilience4j migration"
git checkout -b feature/migrate-to-resilience4j
```

### 0.2 Document Current Config

**Hystrix → Resilience4j Mapping:**
- `apiDefaultService` (200ms) → `timeoutDuration`
- `hyxCbSleepWindowMS` (1000ms) → `waitDurationInOpenState`
- `hyxCbReqVolmThreshold` (20) → `minimumNumberOfCalls`
- `hyxCbErrThresholdPercentage` (50%) → `failureRateThreshold`

---

## Phase 1: Dependencies (30 min)

### 1.1 Update build.gradle

```gradle
dependencies {
    // ❌ REMOVE
    // implementation 'org.springframework.cloud:spring-cloud-starter-netflix-hystrix'
    
    // ✅ ADD Resilience4j
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-circuitbreaker:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-timelimiter:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-micrometer:2.2.0'
}
```

```bash
./gradlew clean build --refresh-dependencies
```

---

## Phase 2: Configuration (1 hour)

### 2.1 Create Resilience4jConfig.java

**File:** `src/main/java/com/anr/config/Resilience4jConfig.java`

```java
package com.anr.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Autowired
    private ConfigProperties appProps;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(appProps.getWaitperiod().getHyxCbErrThresholdPercentage())
                .minimumNumberOfCalls(appProps.getWaitperiod().getHyxCbReqVolmThreshold())
                .waitDurationInOpenState(Duration.ofMillis(appProps.getWaitperiod().getHyxCbSleepWindowMS()))
                .permittedNumberOfCallsInHalfOpenState(10)
                .slidingWindowSize(100)
                .build();
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(appProps.getWaitperiod().getApiDefaultService()))
                .cancelRunningFuture(true)
                .build();
        return TimeLimiterRegistry.of(config);
    }
}
```

### 2.2 Add application.properties

```properties
# Resilience4j Circuit Breaker
resilience4j.circuitbreaker.configs.default.registerHealthIndicator=true
resilience4j.circuitbreaker.configs.default.slidingWindowSize=100
resilience4j.circuitbreaker.configs.default.minimumNumberOfCalls=20
resilience4j.circuitbreaker.configs.default.waitDurationInOpenState=1000ms
resilience4j.circuitbreaker.configs.default.failureRateThreshold=50

resilience4j.circuitbreaker.instances.defaultApi.baseConfig=default
resilience4j.circuitbreaker.instances.secondApi.baseConfig=default

# Time Limiter
resilience4j.timelimiter.instances.defaultApi.timeoutDuration=200ms
resilience4j.timelimiter.instances.secondApi.timeoutDuration=3000ms

# Bulkhead (replaces thread pools)
resilience4j.bulkhead.instances.defaultApi.maxConcurrentCalls=40

# Actuator
management.endpoints.web.exposure.include=health,circuitbreakers
management.health.circuitbreakers.enabled=true
```

---

## Phase 3: Update Bootstrap (15 min)

**File:** `src/main/java/com/anr/Bootstrap.java`

```java
@Configuration
@Slf4j
@EnableAsync
@EnableAspectJAutoProxy
// ❌ REMOVE: @EnableCircuitBreaker
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Bootstrap extends SpringBootServletInitializer {
    // ... rest unchanged
}
```

---

## Phase 4: Migrate Aspect (2 hours)

**File:** `src/main/java/com/anr/logging/ControllerLoggingAspect.java`

```java
package com.anr.logging;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.vavr.control.Try;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class ControllerLoggingAspect {
    
    @Autowired private ConfigProperties appProps;
    @Autowired private SBUtil sbutil;
    @Autowired private Gson gson;
    @Autowired private ControllerFailureResponses failures;
    @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;
    @Autowired private TimeLimiterRegistry timeLimiterRegistry;

    @Around("execution(* com.anr.controller.MainSBController.getSampleResponse(..))")
    public SBResponseModel logSampleResponse(ProceedingJoinPoint jointpoint, 
            String transactionID, String sourceChannel, String locale, 
            String field1, String field2) {
        
        long startTime = System.currentTimeMillis();
        SplunkEventBuilder bldr = new SplunkEventBuilder("Default-Api", "localhost", 
                sourceChannel, transactionID);
        bldr.transactionType(TransactionType.Request);

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("defaultApi");
        TimeLimiter tl = timeLimiterRegistry.timeLimiter("defaultApi");

        SBResponseModel response;
        try {
            Supplier<CompletableFuture<SBResponseModel>> futureSupplier = () -> 
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return (SBResponseModel) jointpoint.proceed();
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
                });

            Callable<SBResponseModel> decorated = TimeLimiter
                    .decorateFutureSupplier(tl, futureSupplier);
            decorated = CircuitBreaker.decorateCallable(cb, decorated);

            response = Try.ofCallable(decorated)
                    .recover(t -> handleFailure(t, jointpoint, transactionID, 
                            sourceChannel, locale, field1, field2))
                    .get();
        } catch (Exception e) {
            response = handleFailure(e, jointpoint, transactionID, 
                    sourceChannel, locale, field1, field2);
        }

        logResponse(response, bldr, transactionID, startTime);
        return response;
    }

    private SBResponseModel handleFailure(Throwable t, ProceedingJoinPoint jp,
            String txId, String channel, String locale, String f1, String f2) {
        sbutil.logError(txId, "Circuit breaker failure: " + t.getMessage());
        return failures.getSampleFailureResponse(txId, channel, locale, f1, f2, t);
    }

    private void logResponse(SBResponseModel response, SplunkEventBuilder bldr, 
            String txId, long startTime) {
        String msg = response == null ? "null" : gson.toJson(response);
        msg += "; time=" + (System.currentTimeMillis() - startTime) + "ms";
        sbutil.logInfo(txId, msg);
        sbutil.logToSplunkOrSimilar(bldr.build(), startTime);
    }
}
```

---

## Phase 5: Cleanup (30 min)

### 5.1 Remove Old Files

```bash
rm src/main/java/com/anr/config/CircuitBreakerHystrixConfig.java
```

### 5.2 Remove Hystrix Properties

Comment out in `application.properties`:

```properties
# ❌ Removed Hystrix config
# sbsvc.waitperiod.hyxCbSleepWindowMS=1000
# sbsvc.waitperiod.hyxThrdPoolCoreSize=30
```

---

## Phase 6: Testing (2 hours)

### 6.1 Create Tests

**File:** `src/test/java/com/anr/controller/ControllerCircuitBreakerTest.java`

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ControllerCircuitBreakerTest {

    @Autowired
    private CircuitBreakerRegistry registry;

    @Test
    void testCircuitBreakerExists() {
        CircuitBreaker cb = registry.circuitBreaker("defaultApi");
        assertNotNull(cb);
        assertEquals(CircuitBreaker.State.CLOSED, cb.getState());
    }

    @Test
    void testCircuitOpensOnFailures() {
        CircuitBreaker cb = registry.circuitBreaker("defaultApi");
        for (int i = 0; i < 25; i++) {
            try {
                cb.executeSupplier(() -> { throw new RuntimeException(); });
            } catch (Exception e) {}
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.getState());
    }
}
```

### 6.2 Run Tests

```bash
./gradlew test
./gradlew bootRun
```

---

## Phase 7: Verification (1 hour)

### 7.1 Test Endpoints

```bash
# Health check with circuit breaker status
curl http://localhost:8080/actuator/health

# Circuit breaker metrics
curl http://localhost:8080/actuator/circuitbreakers

# Test API
curl -u user:password http://localhost:8080/api/v1/default
```

### 7.2 Monitor Metrics

Access Actuator endpoints:
- `/actuator/circuitbreakers` - CB status
- `/actuator/circuitbreakerevents` - CB events
- `/actuator/metrics/resilience4j.circuitbreaker.calls` - Metrics

---

## Comparison: Hystrix vs Resilience4j

| Feature | Hystrix | Resilience4j |
|---------|---------|--------------|
| **Status** | Deprecated | Active |
| **Spring Boot 3** | ❌ No | ✅ Yes |
| **Dependencies** | Heavy | Lightweight |
| **API Style** | Imperative | Functional |
| **Metrics** | Custom | Micrometer |
| **Thread Pools** | Built-in | Use Bulkhead |
| **Patterns** | CB only | CB, Retry, Rate Limit, Bulkhead |

---

## Rollback Plan

```bash
git checkout backup/before-resilience4j
```

---

## Success Criteria

- ✅ Application builds
- ✅ All tests pass
- ✅ Circuit breaker opens on failures
- ✅ Timeouts work correctly
- ✅ Metrics available in Actuator
- ✅ No Hystrix dependencies

---

## Timeline

| Phase | Duration |
|-------|----------|
| Preparation | 1 hour |
| Dependencies | 30 min |
| Configuration | 1 hour |
| Bootstrap | 15 min |
| Aspect Migration | 2 hours |
| Cleanup | 30 min |
| Testing | 2 hours |
| Verification | 1 hour |
| **Total** | **8-10 hours (1-2 days)** |

---

**Next:** Proceed to `Upgrade_H2_Plan.md` for MongoDB → H2 migration.
