package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction summary object")
public class TransactionSummaryDTO {

    @Schema(description = "Total number of transactions", example = "1523")
    private Long totalTransactions;

    @Schema(description = "Total amount of transactions", example = "245670.50")
    private BigDecimal totalAmount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Count of transactions by status")
    private Map<String, Long> byStatus;
}