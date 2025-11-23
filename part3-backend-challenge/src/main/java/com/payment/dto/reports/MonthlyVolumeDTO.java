package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for monthly transaction volume
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Monthly transaction volume metrics")
public class MonthlyVolumeDTO {

    @Schema(description = "Month (YYYY-MM)", example = "2024-01")
    private String month;

    @Schema(description = "Number of transactions", example = "4500")
    private Long count;

    @Schema(description = "Total amount", example = "1350000.00")
    private BigDecimal amount;
}
