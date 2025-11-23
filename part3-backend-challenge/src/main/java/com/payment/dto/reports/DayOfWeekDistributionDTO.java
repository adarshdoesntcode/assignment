package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for day of week transaction distribution
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Day of week transaction distribution")
public class DayOfWeekDistributionDTO {

    @Schema(description = "Day of week", example = "MONDAY", allowableValues = { "SUNDAY", "MONDAY", "TUESDAY",
            "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" })
    private String dayOfWeek;

    @Schema(description = "Number of transactions on this day of week", example = "1500")
    private Long count;
}
