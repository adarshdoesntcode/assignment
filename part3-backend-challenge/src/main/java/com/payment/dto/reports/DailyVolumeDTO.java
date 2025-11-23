package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for daily transaction volume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Daily transaction volume metrics")
public class DailyVolumeDTO {

    @Schema(description = "Date (YYYY-MM-DD)", example = "2024-01-15")
    private String date;

    @Schema(description = "Number of transactions", example = "150")
    private Long count;

    @Schema(description = "Total amount", example = "45000.00")
    private BigDecimal amount;
}
