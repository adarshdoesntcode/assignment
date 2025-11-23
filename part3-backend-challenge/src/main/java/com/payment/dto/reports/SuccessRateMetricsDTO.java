package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for success vs failure rate analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Success and failure rate metrics")
public class SuccessRateMetricsDTO {

    @Schema(description = "Total number of transactions", example = "10000")
    private Long totalTransactions;

    @Schema(description = "Number of completed transactions", example = "9200")
    private Long completed;

    @Schema(description = "Number of failed transactions", example = "800")
    private Long failed;

    @Schema(description = "Success rate percentage", example = "92.0")
    private Double successRate;

    @Schema(description = "Failure rate percentage", example = "8.0")
    private Double failureRate;

    @Schema(description = "Transaction counts by status")
    private Map<String, Long> byStatus;
}
