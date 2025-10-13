# Resilience4j Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### 1. Inject Circuit Breaker

```java
@Service
public class MyService {
    
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    @Autowired
    private TimeLimiter defaultApiTimeLimiter;
}
```

### 2. Wrap Your Service Calls

**Simple Call:**
```java
public String callService() {
    return defaultApiCircuitBreaker.executeSupplier(() -> {
        return externalService.call();
    });
}
```

**With Fallback:**
```java
public String callServiceWithFallback() {
    return defaultApiCircuitBreaker.executeSupplier(
        () -> externalService.call(),
        throwable -> "Fallback response"
    );
}
```

**With Timeout:**
```java
public String callServiceWithTimeout() {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
        defaultApiCircuitBreaker.executeSupplier(() -> externalService.call())
    );
    
    try {
        return future.get(200, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
        return "Timeout fallback";
    }
}
```

## 📊 Circuit Breaker States

```
CLOSED  → Normal operation, all requests pass through
OPEN    → Circuit tripped, all requests fail immediately
HALF_OPEN → Testing if service recovered
```

## 🔧 Common Patterns

### Pattern 1: Simple Protection

```java
@Service
public class OrderService {
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    public Order createOrder(OrderRequest request) {
        return defaultApiCircuitBreaker.executeSupplier(() -> {
            return orderApi.create(request);
        });
    }
}
```

### Pattern 2: With Fallback

```java
@Service
public class ProductService {
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    public Product getProduct(String id) {
        return defaultApiCircuitBreaker.executeSupplier(
            () -> productApi.get(id),
            throwable -> getCachedProduct(id) // Fallback to cache
        );
    }
    
    private Product getCachedProduct(String id) {
        return cache.get(id);
    }
}
```

### Pattern 3: Async with Timeout

```java
@Service
public class NotificationService {
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    @Async
    public CompletableFuture<Void> sendNotification(String message) {
        return CompletableFuture.runAsync(() ->
            defaultApiCircuitBreaker.executeRunnable(() -> {
                notificationApi.send(message);
            })
        ).orTimeout(500, TimeUnit.MILLISECONDS);
    }
}
```

### Pattern 4: Multiple Resilience Patterns

```java
@Service
public class PaymentService {
    @Autowired
    private CircuitBreaker circuitBreaker;
    
    @Autowired
    private Bulkhead bulkhead;
    
    @Autowired
    private Retry retry;
    
    public Payment processPayment(PaymentRequest request) {
        // Combine circuit breaker + bulkhead + retry
        return Decorators.ofSupplier(() -> paymentApi.process(request))
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withRetry(retry)
            .decorate()
            .get();
    }
}
```

## 🎯 Best Practices

### ✅ DO

- **Use meaningful names** for circuit breakers
- **Implement fallbacks** for critical operations
- **Log state transitions** for monitoring
- **Test circuit breaker behavior** in integration tests
- **Configure appropriate timeouts** based on SLAs
- **Use bulkhead** to isolate thread pools

### ❌ DON'T

- **Don't wrap database calls** (use connection pooling instead)
- **Don't use for fast operations** (< 10ms)
- **Don't ignore circuit breaker state** in monitoring
- **Don't use same circuit breaker** for different services
- **Don't set thresholds too low** (causes flapping)

## 📈 Monitoring

### Check Circuit Breaker State

```bash
# Health endpoint
curl http://localhost:8080/actuator/health

# Metrics endpoint
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.calls
```

### Log Analysis

```java
// State transitions are automatically logged
INFO: Circuit breaker transitioned to OPEN state: sbapi-defapi
INFO: Circuit breaker transitioned to HALF_OPEN state: sbapi-defapi
INFO: Circuit breaker transitioned to CLOSED state: sbapi-defapi
```

## 🔍 Debugging

### Check Current State

```java
CircuitBreaker.State state = defaultApiCircuitBreaker.getState();
System.out.println("Circuit Breaker State: " + state);
```

### Get Metrics

```java
CircuitBreaker.Metrics metrics = defaultApiCircuitBreaker.getMetrics();
System.out.println("Failure Rate: " + metrics.getFailureRate());
System.out.println("Number of Calls: " + metrics.getNumberOfSuccessfulCalls());
```

### Force State Change (Testing Only)

```java
// For testing purposes
defaultApiCircuitBreaker.transitionToOpenState();
defaultApiCircuitBreaker.transitionToClosedState();
```

## 🧪 Testing

### Unit Test

```java
@Test
public void testServiceWithCircuitBreaker() {
    // Given
    when(externalService.call()).thenThrow(new RuntimeException());
    
    // When
    String result = myService.callServiceWithFallback();
    
    // Then
    assertEquals("Fallback response", result);
}
```

### Integration Test

```java
@SpringBootTest
public class CircuitBreakerIntegrationTest {
    
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    @Autowired
    private MyService myService;
    
    @Test
    public void testCircuitBreakerOpens() {
        // Simulate multiple failures
        for (int i = 0; i < 25; i++) {
            try {
                myService.callService();
            } catch (Exception e) {
                // Expected
            }
        }
        
        // Circuit should be OPEN
        assertEquals(CircuitBreaker.State.OPEN, 
                    defaultApiCircuitBreaker.getState());
    }
}
```

## ⚙️ Configuration Quick Reference

### Circuit Breaker

```properties
# Minimum calls before calculating failure rate
minimumNumberOfCalls=20

# Failure rate threshold (50%)
failureRateThreshold=50

# Wait time in OPEN state
waitDurationInOpenState=1000ms

# Test calls in HALF_OPEN state
permittedNumberOfCallsInHalfOpenState=10
```

### Time Limiter

```properties
# Timeout duration
timeoutDuration=200ms

# Cancel running future on timeout
cancelRunningFuture=true
```

### Bulkhead

```properties
# Max concurrent calls
maxConcurrentCalls=30

# Thread pool size
maxThreadPoolSize=40
coreThreadPoolSize=30
```

## 🆘 Troubleshooting

### Circuit Breaker Not Opening

**Problem:** Circuit stays CLOSED despite failures

**Solution:**
```properties
# Reduce minimum calls for testing
resilience4j.circuitbreaker.instances.test.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.test.failureRateThreshold=50
```

### Fallback Not Executing

**Problem:** Fallback method not called

**Solution:**
```java
// Ensure fallback method signature matches
@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
public String call() { ... }

// Fallback must accept Throwable
private String fallback(Throwable t) {
    return "Fallback";
}
```

### Timeout Not Working

**Problem:** Calls not timing out

**Solution:**
```java
// Use CompletableFuture with timeout
CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
    circuitBreaker.executeSupplier(() -> service.call())
);
return future.get(200, TimeUnit.MILLISECONDS);
```

## 📚 More Information

- **Full Documentation:** [RESILIENCE4J_MIGRATION.md](./RESILIENCE4J_MIGRATION.md)
- **Official Docs:** https://resilience4j.readme.io/
- **Examples:** https://github.com/resilience4j/resilience4j-spring-boot3-demo

## 🎉 You're Ready!

Start protecting your service calls with Resilience4j circuit breakers!

```java
@Service
public class MyService {
    @Autowired
    private CircuitBreaker defaultApiCircuitBreaker;
    
    public String protectedCall() {
        return defaultApiCircuitBreaker.executeSupplier(
            () -> externalService.call(),
            throwable -> "Fallback"
        );
    }
}
```

**Happy Coding!** 🚀
