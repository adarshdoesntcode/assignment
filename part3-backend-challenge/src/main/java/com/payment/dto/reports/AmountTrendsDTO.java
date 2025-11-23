package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for amount trends analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction amount trends and statistics")
public class AmountTrendsDTO {

    @Schema(description = "Overall amount statistics")
    private AmountStatsDTO overall;

    @Schema(description = "Daily average amount trends")
    private List<DailyAmountTrendDTO> daily;
}
