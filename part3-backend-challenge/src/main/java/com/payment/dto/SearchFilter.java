package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
@Schema(description = "Search filter criteria")
public class SearchFilter {

    @Schema(description = "Field name to filter on", example = "txnDate")
    private String field;

    @Schema(description = "Filter condition", example = "equals", allowableValues = {"equals", "notEquals", "contains", "greaterThan", "lessThan", "greaterThanOrEqual", "lessThanOrEqual", "between"})
    private String condition;

    @Schema(description = "Filter value", example = "2025-11-16")
    private String value;
}