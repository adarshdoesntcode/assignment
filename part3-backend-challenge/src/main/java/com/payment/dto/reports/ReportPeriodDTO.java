package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for report period (date range)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Report period with start and end dates")
public class ReportPeriodDTO {

    @Schema(description = "Start date of the report period", example = "2024-01-01T00:00:00Z")
    private Instant start;

    @Schema(description = "End date of the report period", example = "2024-12-31T23:59:59Z")
    private Instant end;
}
