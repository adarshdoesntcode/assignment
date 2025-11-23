package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for weekly transaction volume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Weekly transaction volume metrics")
public class WeeklyVolumeDTO {

    @Schema(description = "Week start date (YYYY-MM-DD)", example = "2024-01-08")
    private String weekStart;

    @Schema(description = "Week number", example = "2")
    private Integer weekNumber;

    @Schema(description = "Number of transactions", example = "1050")
    private Long count;

    @Schema(description = "Total amount", example = "315000.00")
    private BigDecimal amount;
}
