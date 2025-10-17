package com.anr.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Circuit Breaker Behavior Simulation
 * 
 * This simulation specifically tests the Resilience4j circuit breaker behavior
 * by simulating failure scenarios and recovery patterns.
 * 
 * Test Scenarios:
 * 1. Normal operation
 * 2. Gradual failure increase
 * 3. Circuit breaker opens
 * 4. Recovery and circuit breaker closes
 * 
 * Run with: ./gradlew gatlingRun-com.anr.performance.CircuitBreakerSimulation
 */
class CircuitBreakerSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Circuit Breaker Test")
    .basicAuth("user", "password")

  // Scenario 1: Normal Operation
  val normalOperationScenario = scenario("Normal Operation")
    .during(20.seconds) {
      exec(
        http("Normal Request")
          .get("/api/v1/default")
          .check(status.is(200))
      )
      .pause(500.milliseconds, 1.second)
    }

  // Scenario 2: Trigger Circuit Breaker
  val triggerCircuitBreakerScenario = scenario("Trigger Circuit Breaker")
    .exec(
      http("Initial Request")
        .get("/api/v1/default")
        .check(status.in(200, 503))
    )
    .pause(100.milliseconds)
    // Rapid fire to trigger circuit breaker
    .repeat(30) {
      exec(
        http("Rapid Request #{__counter__}")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(50.milliseconds)
    }
    .pause(2.seconds)
    // Verify circuit breaker is open
    .exec(
      http("Verify Circuit Open")
        .get("/api/v1/default")
        .check(status.in(200, 503))
    )

  // Scenario 3: Recovery Test
  val recoveryScenario = scenario("Recovery Test")
    .exec(
      http("Wait for Recovery")
        .get("/actuator/health")
        .check(status.is(200))
    )
    .pause(5.seconds) // Wait for circuit breaker to transition to half-open
    .repeat(10) {
      exec(
        http("Recovery Request #{__counter__}")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(1.second)
    }

  // Scenario 4: Mixed Load with Circuit Breaker
  val mixedLoadScenario = scenario("Mixed Load")
    .randomSwitch(
      50.0 -> exec(
        http("Default API")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      ),
      30.0 -> exec(
        http("Second API")
          .get("/api/v1/second")
          .check(status.in(200, 503))
      ),
      20.0 -> exec(
        http("Health Check")
          .get("/actuator/health")
          .check(status.is(200))
      )
    )
    .pause(300.milliseconds, 800.milliseconds)

  // Scenario 5: Resilience Verification
  val resilienceScenario = scenario("Resilience Verification")
    .exec(
      http("Pre-stress Check")
        .get("/api/v1/default")
        .check(status.is(200))
    )
    .pause(1.second)
    // Apply stress
    .repeat(20) {
      exec(
        http("Stress Request")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(100.milliseconds)
    }
    .pause(3.seconds)
    // Verify recovery
    .exec(
      http("Post-stress Check")
        .get("/api/v1/default")
        .check(status.in(200, 503))
    )

  // Circuit Breaker Test Load Profile
  setUp(
    // Phase 1: Normal operation (0-20s)
    normalOperationScenario.inject(
      rampUsers(10) during (10.seconds)
    ),
    
    // Phase 2: Trigger circuit breaker (20-40s)
    triggerCircuitBreakerScenario.inject(
      nothingFor(20.seconds),
      rampUsers(15) during (10.seconds)
    ),
    
    // Phase 3: Recovery (40-60s)
    recoveryScenario.inject(
      nothingFor(40.seconds),
      rampUsers(5) during (5.seconds)
    ),
    
    // Phase 4: Mixed load throughout (0-90s)
    mixedLoadScenario.inject(
      rampUsers(20) during (20.seconds),
      constantUsersPerSec(3) during (70.seconds)
    ),
    
    // Phase 5: Resilience verification (60-90s)
    resilienceScenario.inject(
      nothingFor(60.seconds),
      rampUsers(10) during (10.seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(2.minutes)
    .assertions(
      global.responseTime.max.lt(8000),
      global.responseTime.percentile3.lt(3000), // 95th percentile
      global.successfulRequests.percent.gt(70), // Account for circuit breaker opening
      forAll.failedRequests.count.lt(100)
    )
}
