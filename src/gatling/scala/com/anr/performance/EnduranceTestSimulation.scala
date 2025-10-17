package com.anr.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Endurance Test Simulation (Soak Test)
 * 
 * This simulation tests the API's stability and performance over an extended period.
 * It helps identify memory leaks, resource exhaustion, and degradation over time.
 * 
 * Duration: 10 minutes of sustained load
 * 
 * Run with: ./gradlew gatlingRun-com.anr.performance.EnduranceTestSimulation
 */
class EnduranceTestSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Endurance Test")
    .basicAuth("user", "password")
    .shareConnections

  // Scenario: Continuous API Calls
  val continuousApiScenario = scenario("Continuous API Calls")
    .forever {
      exec(
        http("API Request")
          .get("/api/v1/default")
          .check(status.is(200))
      )
      .pause(1.second, 3.seconds)
    }

  // Scenario: Health Monitoring
  val healthMonitoringScenario = scenario("Health Monitoring")
    .forever {
      exec(
        http("Health Check")
          .get("/actuator/health")
          .check(status.is(200))
          .check(jsonPath("$.status").is("UP"))
      )
      .pause(10.seconds)
    }

  // Scenario: Mixed Endpoint Usage
  val mixedEndpointScenario = scenario("Mixed Endpoints")
    .forever {
      randomSwitch(
        40.0 -> exec(http("Default").get("/api/v1/default").check(status.is(200))),
        30.0 -> exec(http("Second").get("/api/v1/second").check(status.is(200))),
        20.0 -> exec(http("Probe").get("/api/v1/probe").check(status.is(200))),
        10.0 -> exec(http("Metrics").get("/actuator/metrics").check(status.is(200)))
      )
      .pause(2.seconds, 5.seconds)
    }

  // Scenario: Periodic Bursts
  val periodicBurstScenario = scenario("Periodic Bursts")
    .asLongAs(session => true) {
      // Normal load
      repeat(10) {
        exec(
          http("Normal Request")
            .get("/api/v1/default")
            .check(status.is(200))
        )
        .pause(1.second)
      }
      .pause(5.seconds)
      // Burst
      .repeat(20) {
        exec(
          http("Burst Request")
            .get("/api/v1/default")
            .check(status.in(200, 503))
        )
        .pause(100.milliseconds)
      }
      .pause(10.seconds)
    }

  // Endurance Test Load Profile
  setUp(
    // Sustained moderate load
    continuousApiScenario.inject(
      rampUsers(15) during (30.seconds)
    ).protocols(httpProtocol),
    
    // Health monitoring
    healthMonitoringScenario.inject(
      rampUsers(2) during (10.seconds)
    ).protocols(httpProtocol),
    
    // Mixed endpoint usage
    mixedEndpointScenario.inject(
      rampUsers(10) during (20.seconds)
    ).protocols(httpProtocol),
    
    // Periodic bursts
    periodicBurstScenario.inject(
      rampUsers(5) during (15.seconds)
    ).protocols(httpProtocol)
  ).maxDuration(10.minutes)
    .assertions(
      global.responseTime.max.lt(5000),
      global.responseTime.mean.lt(1000),
      global.responseTime.percentile3.lt(2000),
      global.successfulRequests.percent.gt(99),
      global.failedRequests.count.lt(50)
    )
}
