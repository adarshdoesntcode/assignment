package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for volume metrics aggregated by different time periods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction volume metrics by day, week, and month")
public class VolumeMetricsDTO {

    @Schema(description = "Daily volume breakdown")
    private List<DailyVolumeDTO> daily;

    @Schema(description = "Weekly volume breakdown")
    private List<WeeklyVolumeDTO> weekly;

    @Schema(description = "Monthly volume breakdown")
    private List<MonthlyVolumeDTO> monthly;
}
