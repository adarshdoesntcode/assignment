package com.payment.exception;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler for the application
 * Handles common exceptions and returns appropriate HTTP responses
 */
@Produces
@Singleton
@Requires(classes = {NotFoundException.class, ValidationException.class, ExceptionHandler.class})
public class GlobalExceptionHandler implements ExceptionHandler<Exception, HttpResponse<Map<String, Object>>> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<Map<String, Object>> handle(HttpRequest request, Exception exception) {
        LOG.error("Exception occurred: ", exception);

        if (exception instanceof NotFoundException) {
            return HttpResponse.notFound(buildErrorResponse(
                HttpStatus.NOT_FOUND.getCode(),
                "Not Found",
                exception.getMessage(),
                request.getPath()
            ));
        }

        if (exception instanceof ValidationException) {
            return HttpResponse.badRequest(buildErrorResponse(
                HttpStatus.BAD_REQUEST.getCode(),
                "Validation Error",
                exception.getMessage(),
                request.getPath()
            ));
        }

        if (exception instanceof IllegalArgumentException) {
            return HttpResponse.badRequest(buildErrorResponse(
                HttpStatus.BAD_REQUEST.getCode(),
                "Bad Request",
                exception.getMessage(),
                request.getPath()
            ));
        }

        // Default to 500 Internal Server Error
        return HttpResponse.serverError(buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getPath()
        ));
    }

    private Map<String, Object> buildErrorResponse(int status, String error, String message, String path) {
        return Map.of(
            "timestamp", Instant.now().toString(),
            "status", status,
            "error", error,
            "message", message,
            "path", path
        );
    }
}
