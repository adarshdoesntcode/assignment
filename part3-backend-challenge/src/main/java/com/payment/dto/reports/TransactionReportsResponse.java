package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main response DTO for comprehensive transaction reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Comprehensive transaction metrics and trends report")
public class TransactionReportsResponse {

    @Schema(description = "Report period (date range)")
    private ReportPeriodDTO reportPeriod;

    @Schema(description = "Transaction volume metrics by day/week/month")
    private VolumeMetricsDTO volumeMetrics;

    @Schema(description = "Success vs failure rate analysis")
    private SuccessRateMetricsDTO successRateMetrics;

    @Schema(description = "Average transaction amount trends")
    private AmountTrendsDTO amountTrends;

    @Schema(description = "Peak transaction times heatmap")
    private PeakTimesHeatmapDTO peakTimesHeatmap;

    @Schema(description = "Card type distribution")
    private CardTypeDistributionDTO cardTypeDistribution;
}
