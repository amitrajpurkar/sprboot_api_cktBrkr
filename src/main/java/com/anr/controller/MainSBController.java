package com.anr.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anr.model.SBResponseModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1")
public class MainSBController {

    @GetMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Default Service", operationId = "DEFAULT_SVC",

            description = "This API method provides a sample service to see how a controller responds back. "
                    + "This service is not connected to any backend" + "<br/>following parameters are anticipated:"
                    + "<br/>&nbsp;&nbsp;&bull;&nbsp; transaction ID: so that we can record end to end trace"
                    + "<br/>&nbsp;&nbsp;&bull;&nbsp; sourceChannel: to know requests are coming from which systems")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = SBResponseModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failure", content = @Content) })
    public SBResponseModel getSampleResponse(@RequestHeader(required = false) String transactionID,
            @RequestHeader(required = false, defaultValue = "definedChannel01") String sourceChannel,
            @RequestHeader(required = false) String locale, @RequestParam(required = false) String field1,
            @RequestParam(required = false) String field2) {
        SBResponseModel response = new SBResponseModel();
        // validate input
        // perform processing
        // give back response
        return response;
    }

}
