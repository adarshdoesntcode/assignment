package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for peak transaction times heatmap
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Peak transaction times analysis with hourly and day-of-week distribution")
public class PeakTimesHeatmapDTO {

    @Schema(description = "Hourly transaction distribution (0-23 hours)")
    private List<HourlyDistributionDTO> hourly;

    @Schema(description = "Day of week transaction distribution")
    private List<DayOfWeekDistributionDTO> dayOfWeek;
}
