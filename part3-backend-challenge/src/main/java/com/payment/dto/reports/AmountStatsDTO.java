package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for amount statistics (overall metrics)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Overall amount statistics")
public class AmountStatsDTO {

    @Schema(description = "Average transaction amount", example = "150.50")
    private BigDecimal average;

    @Schema(description = "Median transaction amount", example = "125.00")
    private BigDecimal median;

    @Schema(description = "Minimum transaction amount", example = "10.00")
    private BigDecimal min;

    @Schema(description = "Maximum transaction amount", example = "5000.00")
    private BigDecimal max;
}
