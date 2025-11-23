package com.payment.dto.reports;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for card type distribution analysis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Card type distribution with counts and percentages")
public class CardTypeDistributionDTO {

    @Schema(description = "Transaction counts by card type")
    private Map<String, Long> byType;

    @Schema(description = "Percentage distribution by card type")
    private Map<String, Double> percentages;
}
