# Gatling Performance Testing - Quick Reference

## 🚀 Quick Commands

### Run Tests
```bash
# All tests
./gradlew gatlingRun

# Specific test
./gradlew gatlingRun-com.anr.performance.BasicLoadSimulation
./gradlew gatlingRun-com.anr.performance.StressTestSimulation
./gradlew gatlingRun-com.anr.performance.CircuitBreakerSimulation
./gradlew gatlingRun-com.anr.performance.EnduranceTestSimulation
```

### View Reports
```bash
# Open latest report
open build/reports/gatling/*/index.html

# List all reports
ls -lt build/reports/gatling/
```

---

## 📊 Test Suite Overview

| Test | Users | Duration | Purpose |
|------|-------|----------|---------|
| **Basic Load** | 45 | 1 min | Normal load, smoke test |
| **Stress Test** | 200+ | 2 min | Breaking points, limits |
| **Circuit Breaker** | 60 | 2 min | Resilience, recovery |
| **Endurance** | 32 | 10 min | Stability, memory leaks |

---

## 🎯 Success Criteria

### BasicLoadSimulation
- ✅ Max response time: <5s
- ✅ Success rate: >95%
- ✅ No circuit breaker trips

### StressTestSimulation
- ✅ Max response time: <10s
- ✅ Success rate: >80%
- ⚠️ Circuit breaker may open

### CircuitBreakerSimulation
- ✅ Success rate: >70%
- ✅ Circuit breaker opens/closes
- ✅ System recovers

### EnduranceTestSimulation
- ✅ Success rate: >99%
- ✅ Stable response times
- ✅ No memory leaks

---

## 📈 Key Metrics

### Response Time
- **Mean:** Average time
- **50th %ile:** Median
- **95th %ile:** SLA metric (most important)
- **99th %ile:** Worst case

### Throughput
- **Req/sec:** Request rate
- **Resp/sec:** Response rate

### Success Rate
- **OK:** HTTP 2xx
- **KO:** HTTP 4xx/5xx, timeouts

---

## 🔧 Common Issues

### Application Not Running
```bash
# Start application
docker-compose up -d

# Check health
curl http://localhost:8080/actuator/health
```

### High Failure Rate
```bash
# Check logs
docker-compose logs app

# Check circuit breaker
curl http://localhost:8080/actuator/health

# Increase memory
JAVA_OPTS=-Xmx2g docker-compose up -d
```

### Slow Tests
```bash
# Reduce users
# Shorten duration
# Check system resources
docker stats
```

---

## 📝 Test Scenarios

### Basic Load
- Health checks
- API endpoints
- Circuit breaker basics

### Stress Test
- Sustained high load
- Spike tests (100 users at once)
- Mixed workload
- Breaking point identification

### Circuit Breaker
- Normal operation
- Trigger circuit breaker
- Recovery testing
- Resilience verification

### Endurance
- 10-minute sustained load
- Memory leak detection
- Stability verification
- Periodic bursts

---

## 🎓 For Java Developers

### Test Structure
```
src/gatling/
├── scala/com/anr/performance/
│   ├── BasicLoadSimulation.scala
│   ├── StressTestSimulation.scala
│   ├── CircuitBreakerSimulation.scala
│   └── EnduranceTestSimulation.scala
├── resources/
│   ├── gatling.conf
│   └── data/
│       ├── products.csv
│       └── members.csv
```

### Key Concepts
- **Scenario:** User journey
- **Injection:** Load pattern
- **Check:** Validation
- **Feeder:** Test data source
- **Assertion:** Pass/fail criteria

### Example Scenario
```scala
val scn = scenario("My Test")
  .exec(
    http("Request")
      .get("/api/endpoint")
      .check(status.is(200))
  )
  .pause(1.second)

setUp(
  scn.inject(rampUsers(10) during (10.seconds))
).assertions(
  global.successfulRequests.percent.gt(95)
)
```

---

## 📚 Resources

- Full Guide: [PERFORMANCE_TESTING.md](PERFORMANCE_TESTING.md)
- Gatling Docs: https://gatling.io/docs/
- Resilience4j: https://resilience4j.readme.io/

---

**Need Help?** See [PERFORMANCE_TESTING.md](PERFORMANCE_TESTING.md) for detailed documentation.
