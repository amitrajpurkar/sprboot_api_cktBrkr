package com.anr.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Basic Load Test Simulation
 * 
 * This simulation tests the basic functionality of the API under normal load conditions.
 * It simulates users accessing various endpoints with a gradual ramp-up.
 * 
 * Run with: ./gradlew gatlingRun-com.anr.performance.BasicLoadSimulation
 */
class BasicLoadSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Performance Test")
    .basicAuth("user", "password") // Default Spring Security credentials

  // Scenario: Health Check
  val healthCheckScenario = scenario("Health Check")
    .exec(
      http("Health Check Request")
        .get("/actuator/health")
        .check(status.is(200))
        .check(jsonPath("$.status").is("UP"))
    )

  // Scenario: API Endpoints
  val apiEndpointsScenario = scenario("API Endpoints")
    .exec(
      http("Get Default API")
        .get("/api/v1/default")
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("Get Second API")
        .get("/api/v1/second")
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("Get Probe")
        .get("/api/v1/probe")
        .check(status.is(200))
    )

  // Scenario: Circuit Breaker Test
  val circuitBreakerScenario = scenario("Circuit Breaker")
    .exec(
      http("Test Circuit Breaker - Success")
        .get("/api/v1/default")
        .check(status.is(200))
    )
    .pause(500.milliseconds)
    .repeat(5) {
      exec(
        http("Rapid Requests")
          .get("/api/v1/default")
          .check(status.in(200, 503))
      )
      .pause(100.milliseconds)
    }

  // Load Profile: Gradual ramp-up
  setUp(
    healthCheckScenario.inject(
      rampUsers(10) during (10.seconds)
    ),
    apiEndpointsScenario.inject(
      rampUsers(20) during (20.seconds)
    ),
    circuitBreakerScenario.inject(
      rampUsers(15) during (15.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(5000),
      global.successfulRequests.percent.gt(95)
    )
}
