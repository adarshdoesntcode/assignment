package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Serdeable
@Schema(description = "Transaction search request with filters")
public class TransactionSearchRequest {
    
    @Schema(description = "List of search filters to apply")
    private List<SearchFilter> searchFilters;
}
