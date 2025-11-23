package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for daily average amount trend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Daily average amount trend")
public class DailyAmountTrendDTO {

    @Schema(description = "Date (YYYY-MM-DD)", example = "2024-01-15")
    private String date;

    @Schema(description = "Average transaction amount for the day", example = "300.00")
    private BigDecimal average;
}
