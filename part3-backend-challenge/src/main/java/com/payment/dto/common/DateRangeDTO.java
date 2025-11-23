package com.payment.dto.common;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Date range object")
public class DateRangeDTO {

    @Schema(description = "Start date and time", example = "2025-11-01T00:00:00Z")
    private Instant start;

    @Schema(description = "End date and time", example = "2025-11-18T23:59:59Z")
    private Instant end;
}