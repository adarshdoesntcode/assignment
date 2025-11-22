package com.payment.exception;

import com.payment.dto.ErrorResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application
 * Handles common exceptions and returns appropriate HTTP responses
 */
@Produces
@Singleton
@Requires(classes = { Exception.class, ExceptionHandler.class })
public class GlobalExceptionHandler implements ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Exception exception) {
        String path = request.getPath();

        // Handle NotFoundException (404)
        if (exception instanceof NotFoundException) {
            LOG.warn("Not Found: {} - {}", path, exception.getMessage());
            return HttpResponse.notFound(new ErrorResponse(
                    HttpStatus.NOT_FOUND.getCode(),
                    "Not Found",
                    exception.getMessage(),
                    path));
        }

        // Handle validation errors (400)
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String violations = cve.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));

            LOG.warn("Validation failed: {} - {}", path, violations);
            return HttpResponse.badRequest(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.getCode(),
                    "Validation Failed",
                    "Invalid input parameters",
                    path,
                    violations));
        } // Handle illegal arguments (400)
        if (exception instanceof IllegalArgumentException) {
            LOG.warn("Bad Request: {} - {}", path, exception.getMessage());
            return HttpResponse.badRequest(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.getCode(),
                    "Bad Request",
                    exception.getMessage(),
                    path));
        }

        // Handle number format exceptions (400)
        if (exception instanceof NumberFormatException) {
            LOG.warn("Invalid number format: {} - {}", path, exception.getMessage());
            return HttpResponse.badRequest(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.getCode(),
                    "Bad Request",
                    "Invalid number format in request parameters",
                    path,
                    exception.getMessage()));
        }

        // Handle date/time parsing exceptions (400)
        if (exception instanceof java.time.format.DateTimeParseException) {
            LOG.warn("Invalid date format: {} - {}", path, exception.getMessage());
            return HttpResponse.badRequest(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.getCode(),
                    "Bad Request",
                    "Invalid date format. Please use ISO format (YYYY-MM-DD)",
                    path,
                    exception.getMessage()));
        }

        // Catch-all for unexpected errors (500)
        LOG.error("Internal Server Error: {} - ", path, exception);
        return HttpResponse.serverError(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support if the issue persists.",
                path,
                exception.getClass().getSimpleName()));
    }
}
