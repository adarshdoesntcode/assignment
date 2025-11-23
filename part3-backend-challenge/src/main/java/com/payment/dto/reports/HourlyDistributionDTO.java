package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for hourly transaction distribution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Hourly transaction distribution (0-23)")
public class HourlyDistributionDTO {

    @Schema(description = "Hour of day (0-23)", example = "14")
    private Integer hour;

    @Schema(description = "Number of transactions in this hour", example = "450")
    private Long count;
}
