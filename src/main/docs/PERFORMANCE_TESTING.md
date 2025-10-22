# Performance Testing Guide with Gatling

This guide explains how to run performance tests for the Spring Boot Circuit Breaker API using Gatling.

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Test Scenarios](#test-scenarios)
- [Running Tests](#running-tests)
- [Understanding Results](#understanding-results)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Overview

This application includes comprehensive performance testing using **Gatling**, a powerful open-source load testing framework. The tests are designed to showcase various performance testing patterns for Java developers.

### What is Gatling?

Gatling is a highly capable load testing tool written in Scala. It provides:
- **High Performance:** Can simulate thousands of users with minimal resources
- **Expressive DSL:** Easy-to-read test scenarios
- **Rich Reports:** Beautiful HTML reports with detailed metrics
- **CI/CD Integration:** Easy integration with build tools

### Test Suite Overview

| Test | Purpose | Duration | Users | Focus |
|------|---------|----------|-------|-------|
| **BasicLoadSimulation** | Normal load testing | ~1 min | 45 | Basic functionality |
| **StressTestSimulation** | Breaking point identification | ~2 min | 200+ | High load |
| **CircuitBreakerSimulation** | Resilience testing | ~2 min | 60 | Circuit breaker behavior |
| **EnduranceTestSimulation** | Stability over time | 10 min | 32 | Memory leaks, degradation |

---

## Prerequisites

### Required
- Java 21 or higher
- Gradle 9.1.0 or higher
- Running Spring Boot application on `http://localhost:8080`

### Optional
- Docker (for containerized testing)
- 4GB+ RAM for stress tests
- MongoDB (if testing database operations)

---

## Quick Start

### 1. Start the Application

```bash
# Option 1: Run locally
./gradlew bootRun

# Option 2: Run with Docker
docker-compose up -d

# Verify application is running
curl http://localhost:8080/actuator/health
```

### 2. Run Your First Performance Test

```bash
# Run basic load test
./gradlew gatlingRun-com.anr.performance.BasicLoadSimulation

# View results
open build/reports/gatling/basicloadsimulation-*/index.html
```

---

## Test Scenarios

### 1. BasicLoadSimulation

**Purpose:** Validate basic functionality under normal load

**Characteristics:**
- 45 concurrent users
- Gradual ramp-up over 20 seconds
- Tests health checks, API endpoints, and circuit breaker
- Expected success rate: >95%

**When to use:**
- Smoke testing after deployments
- Baseline performance metrics
- Regression testing

**Run:**
```bash
./gradlew gatlingRun-com.anr.performance.BasicLoadSimulation
```

**Expected Results:**
- Max response time: <5 seconds
- Success rate: >95%
- No circuit breaker trips

---

### 2. StressTestSimulation

**Purpose:** Identify system breaking points and limits

**Characteristics:**
- 200+ concurrent users
- Includes spike tests (100 users at once)
- Sustained high load for 60 seconds
- Mixed workload patterns
- Expected success rate: >80%

**When to use:**
- Capacity planning
- Infrastructure sizing
- Identifying bottlenecks

**Run:**
```bash
./gradlew gatlingRun-com.anr.performance.StressTestSimulation
```

**Expected Results:**
- Max response time: <10 seconds
- Success rate: >80%
- Circuit breaker may open under extreme load

**What to watch:**
- Response time degradation
- Error rate increase
- Circuit breaker activation
- Resource utilization (CPU, memory)

---

### 3. CircuitBreakerSimulation

**Purpose:** Verify Resilience4j circuit breaker behavior

**Characteristics:**
- 60 concurrent users
- Specific scenarios to trigger circuit breaker
- Tests recovery patterns
- Validates fail-fast behavior

**Test Phases:**
1. **Normal Operation** (0-20s): Baseline behavior
2. **Trigger Circuit Breaker** (20-40s): Rapid requests to open circuit
3. **Recovery** (40-60s): Verify half-open and closed states
4. **Mixed Load** (throughout): Realistic traffic patterns
5. **Resilience Verification** (60-90s): Post-stress validation

**When to use:**
- Validating circuit breaker configuration
- Testing failure scenarios
- Verifying recovery mechanisms

**Run:**
```bash
./gradlew gatlingRun-com.anr.performance.CircuitBreakerSimulation
```

**Expected Results:**
- Success rate: >70% (accounts for circuit breaker opening)
- Circuit breaker opens under stress
- System recovers after load reduction
- No cascading failures

**Circuit Breaker States:**
- **CLOSED:** Normal operation, requests pass through
- **OPEN:** Circuit breaker activated, requests fail fast
- **HALF_OPEN:** Testing if service recovered

---

### 4. EnduranceTestSimulation

**Purpose:** Test stability and performance over extended periods

**Characteristics:**
- 32 concurrent users
- 10-minute duration
- Sustained moderate load
- Periodic burst patterns
- Continuous health monitoring

**When to use:**
- Identifying memory leaks
- Testing resource cleanup
- Validating long-running stability
- Production readiness verification

**Run:**
```bash
./gradlew gatlingRun-com.anr.performance.EnduranceTestSimulation
```

**Expected Results:**
- Success rate: >99%
- Stable response times throughout
- No memory leaks
- No resource exhaustion

**What to monitor:**
- Memory usage trends
- Response time stability
- Error rate consistency
- JVM garbage collection

---

## Running Tests

### Run All Tests

```bash
# Run all Gatling simulations
./gradlew gatlingRun
```

### Run Specific Test

```bash
# Basic load test
./gradlew gatlingRun-com.anr.performance.BasicLoadSimulation

# Stress test
./gradlew gatlingRun-com.anr.performance.StressTestSimulation

# Circuit breaker test
./gradlew gatlingRun-com.anr.performance.CircuitBreakerSimulation

# Endurance test
./gradlew gatlingRun-com.anr.performance.EnduranceTestSimulation
```

### Run with Custom Configuration

```bash
# Set custom base URL
./gradlew gatlingRun -Dgatling.baseUrl=http://production-server:8080

# Increase log level
./gradlew gatlingRun -Dgatling.logLevel=DEBUG

# Custom simulation
./gradlew gatlingRun -Dgatling.simulationClass=com.anr.performance.BasicLoadSimulation
```

### Run in CI/CD

```bash
# Non-interactive mode
./gradlew gatlingRun --no-daemon

# With specific assertions
./gradlew gatlingRun -Dgatling.assertions.global.successfulRequests.percent.gt=95
```

---

## Understanding Results

### Report Location

After running a test, reports are generated at:
```
build/reports/gatling/<simulation-name>-<timestamp>/index.html
```

### Key Metrics

#### 1. **Response Time**
- **Mean:** Average response time
- **Min/Max:** Fastest and slowest requests
- **Percentiles:**
  - 50th (Median): Half of requests faster
  - 75th: 75% of requests faster
  - 95th: 95% of requests faster (important SLA metric)
  - 99th: 99% of requests faster

#### 2. **Throughput**
- **Requests per second:** Total request rate
- **Responses per second:** Completed requests rate

#### 3. **Success Rate**
- **OK:** Successful requests (HTTP 2xx)
- **KO:** Failed requests (HTTP 4xx, 5xx, timeouts)

#### 4. **Active Users**
- Concurrent virtual users over time

### Reading the Report

**Global Statistics:**
```
================================================================================
---- Global Information --------------------------------------------------------
> request count                                    1000 (OK=950    KO=50    )
> min response time                                  12 (OK=12     KO=45    )
> max response time                                2345 (OK=2345   KO=1234  )
> mean response time                                234 (OK=220    KO=567   )
> std deviation                                     123 (OK=100    KO=234   )
> response time 50th percentile                     200 (OK=195    KO=450   )
> response time 75th percentile                     350 (OK=340    KO=670   )
> response time 95th percentile                     890 (OK=850    KO=1100  )
> response time 99th percentile                    1234 (OK=1200   KO=1234  )
> mean requests/sec                               16.67 (OK=15.83  KO=0.83  )
================================================================================
```

**What to look for:**
- ✅ Success rate >95%
- ✅ 95th percentile <2 seconds
- ✅ Mean response time <500ms
- ⚠️ High standard deviation (inconsistent performance)
- ❌ Increasing response times over time (degradation)

---

## Best Practices

### 1. Test Environment

**Do:**
- Use dedicated test environment
- Match production configuration
- Isolate from other tests
- Monitor system resources

**Don't:**
- Test against production
- Run multiple tests simultaneously
- Test on developer machines
- Ignore system resource limits

### 2. Test Data

**Do:**
- Use realistic data volumes
- Vary input data
- Clean up test data
- Use CSV feeders for variety

**Don't:**
- Use production data
- Hardcode test data
- Ignore data cleanup
- Use same data repeatedly

### 3. Interpreting Results

**Do:**
- Establish baselines
- Compare trends over time
- Consider percentiles, not just averages
- Analyze failures

**Don't:**
- Focus only on mean values
- Ignore outliers
- Compare different environments
- Skip failure analysis

### 4. Performance Tuning

**Do:**
- Test one change at a time
- Document configuration changes
- Measure before and after
- Consider JVM tuning

**Don't:**
- Make multiple changes at once
- Tune without measuring
- Ignore application logs
- Over-optimize prematurely

---

## Troubleshooting

### Application Not Responding

**Symptoms:**
- Connection refused errors
- Timeout errors

**Solutions:**
```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# Check application logs
docker-compose logs app

# Restart application
docker-compose restart app
```

### High Failure Rate

**Symptoms:**
- Success rate <80%
- Many HTTP 503 errors

**Possible Causes:**
1. **Circuit breaker opened** (expected in stress tests)
2. **Application overloaded**
3. **Database connection issues**
4. **Memory exhaustion**

**Solutions:**
```bash
# Check circuit breaker status
curl http://localhost:8080/actuator/health

# Check memory usage
docker stats

# Increase JVM heap
JAVA_OPTS=-Xmx2g -Xms1g docker-compose up -d
```

### Slow Response Times

**Symptoms:**
- High 95th/99th percentiles
- Increasing response times

**Possible Causes:**
1. **Insufficient resources**
2. **Database slow queries**
3. **Network latency**
4. **GC pauses**

**Solutions:**
```bash
# Enable GC logging
JAVA_OPTS="-Xlog:gc*:file=gc.log" ./gradlew bootRun

# Check database performance
# Monitor connection pool

# Increase resources
# Tune JVM parameters
```

### Out of Memory

**Symptoms:**
- Test crashes
- OutOfMemoryError

**Solutions:**
```bash
# Increase Gradle heap
export GRADLE_OPTS="-Xmx2g"

# Reduce concurrent users
# Shorten test duration
# Increase application memory
```

### Connection Timeouts

**Symptoms:**
- Request timeout errors
- Connection pool exhausted

**Solutions:**
```bash
# Increase timeout in gatling.conf
# Increase connection pool size
# Reduce concurrent users
# Add connection keep-alive
```

---

## Advanced Topics

### Custom Scenarios

Create your own simulation:

```scala
package com.anr.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CustomSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .basicAuth("user", "password")

  val scn = scenario("Custom Test")
    .exec(http("My Request").get("/api/v1/default"))

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
```

### Using CSV Feeders

```scala
val productFeeder = csv("data/products.csv").random

val scn = scenario("Product Test")
  .feed(productFeeder)
  .exec(http("Get Product")
    .get("/api/v1/products/${name}")
  )
```

### Assertions

```scala
setUp(scn.inject(rampUsers(100) during (60.seconds)))
  .assertions(
    global.responseTime.max.lt(5000),
    global.successfulRequests.percent.gt(95),
    details("My Request").responseTime.percentile3.lt(2000)
  )
```

---

## Integration with CI/CD

### GitHub Actions Example

```yaml
name: Performance Tests

on:
  schedule:
    - cron: '0 2 * * *'  # Daily at 2 AM

jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
      
      - name: Start Application
        run: docker-compose up -d
      
      - name: Wait for Application
        run: sleep 30
      
      - name: Run Performance Tests
        run: ./gradlew gatlingRun
      
      - name: Upload Reports
        uses: actions/upload-artifact@v3
        with:
          name: gatling-reports
          path: build/reports/gatling/
```

---

## Resources

- [Gatling Documentation](https://gatling.io/docs/gatling/)
- [Gatling Gradle Plugin](https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

**Last Updated:** October 17, 2025  
**Gatling Version:** 3.10.5  
**Status:** ✅ Production Ready
