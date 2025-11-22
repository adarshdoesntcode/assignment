package com.payment.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.payment.dto.ApiResponse;

import java.time.Instant;
import java.util.Map;

/**
 * Status controller - provides API health and status information
 */
@Controller("/api/v1/status")
@Tag(name = "Status")
public class HealthController {

    @Get
    @Operation(summary = "API status check", description = "Returns the status of the API with version and timestamp information")
    public HttpResponse<ApiResponse<Map<String, Object>>> status() {
        Map<String, Object> statusData = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "service", "payment-api",
                "version", "1.0.0");

        return HttpResponse.ok(ApiResponse.success("API is healthy", statusData));
    }
}
