package com.anr.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Stress Test Simulation
 * 
 * This simulation tests the API under high load to identify breaking points
 * and verify circuit breaker behavior under stress conditions.
 * 
 * Run with: ./gradlew gatlingRun-com.anr.performance.StressTestSimulation
 */
class StressTestSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Stress Test")
    .basicAuth("user", "password")
    .shareConnections // Share connections between virtual users

  // Scenario: Sustained High Load
  val sustainedLoadScenario = scenario("Sustained High Load")
    .during(60.seconds) {
      exec(
        http("High Load Request")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(100.milliseconds, 500.milliseconds)
    }

  // Scenario: Spike Test
  val spikeScenario = scenario("Spike Test")
    .exec(
      http("Spike Request")
        .get("/api/v1/second")
        .check(status.in(200, 503))
    )

  // Scenario: Circuit Breaker Stress
  val circuitBreakerStressScenario = scenario("Circuit Breaker Stress")
    .repeat(50) {
      exec(
        http("Rapid Fire Request")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(50.milliseconds)
    }

  // Scenario: Mixed Workload
  val mixedWorkloadScenario = scenario("Mixed Workload")
    .randomSwitch(
      40.0 -> exec(http("Default API").get("/api/v1/default").check(status.in(200, 503))),
      30.0 -> exec(http("Second API").get("/api/v1/second").check(status.in(200, 503))),
      20.0 -> exec(http("Probe API").get("/api/v1/probe").check(status.in(200, 503))),
      10.0 -> exec(http("Health Check").get("/actuator/health").check(status.in(200, 503)))
    )
    .pause(200.milliseconds, 1.second)

  // Stress Test Load Profile
  setUp(
    // Gradual ramp-up to high load
    sustainedLoadScenario.inject(
      rampUsers(50) during (30.seconds),
      constantUsersPerSec(10) during (60.seconds)
    ),
    // Sudden spike
    spikeScenario.inject(
      nothingFor(20.seconds),
      atOnceUsers(100)
    ),
    // Circuit breaker stress
    circuitBreakerStressScenario.inject(
      rampUsers(20) during (20.seconds)
    ),
    // Mixed workload
    mixedWorkloadScenario.inject(
      rampUsers(30) during (30.seconds),
      constantUsersPerSec(5) during (60.seconds)
    )
  ).protocols(httpProtocol)
    .maxDuration(2.minutes)
    .assertions(
      global.responseTime.max.lt(10000),
      global.successfulRequests.percent.gt(80), // Lower threshold for stress test
      forAll.failedRequests.percent.lt(20)
    )
}
