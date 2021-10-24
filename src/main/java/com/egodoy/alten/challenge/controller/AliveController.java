package com.egodoy.alten.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Controller to apply the Health Check pattern in case the app is deployed in a Microservice Architecture environment
 * which might be useful for if Kubernetes is implemented
 */
@RestController
@RequestMapping("/alive")
public class AliveController {

    @Value( "${info.version:unknown}" )
    private String version;

    @Operation( description = "Health check endpoint",
            summary = "Validate if the application instance is up and running")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType ="text/plan",
                    schema = @Schema(implementation =  String.class)))})
    @GetMapping
    public ResponseEntity<String> isAlive(HttpServletRequest request) {
        return new ResponseEntity<>(LocalDateTime.now() + ": Application is live... "+version, HttpStatus.OK);
    }
}
