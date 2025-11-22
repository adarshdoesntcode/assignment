package com.payment.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Introspected
@Schema(description = "Request object for transaction filtering and pagination")
public class TransactionRequest {

    @Schema(description = "Page number", example = "0", defaultValue = "0")
    @Min(0)
    private Integer page;

    @Schema(description = "Page size", example = "20", defaultValue = "20")
    @Min(1)
    private Integer size;

    @Schema(description = "Filter start date in ISO format", example = "2025-11-01")
    private LocalDate startDate;

    @Schema(description = "Filter end date in ISO format", example = "2025-11-18")
    private LocalDate endDate;

    @Schema(description = "Filter by transaction status", example = "completed")
    private String status;
}