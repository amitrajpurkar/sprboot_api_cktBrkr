package com.anr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/probe")
public class ProbeController {

    @RequestMapping(method = RequestMethod.GET, path = "/readiness")
    @Operation(hidden = true, summary = "POD Readiness Probe")
    public String readiness() {
        return "Readiness Probe";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/liveness")
    @Operation(hidden = true, summary = "POD Liveness Probe")
    public String liveness() {
        return "Liveness Probe";
    }
}
